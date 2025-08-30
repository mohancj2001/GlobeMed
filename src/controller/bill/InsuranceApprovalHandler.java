package controller.bill;

import model.MySQL;
import java.sql.ResultSet;

public class InsuranceApprovalHandler implements ClaimHandler {

    private ClaimHandler next;

    @Override
    public void setNext(ClaimHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public void handle(InsuranceClaimRequest request) {
        System.out.println("🏥 Verifying insurance provider ID " + request.getProviderId());

        try {
            // For direct payment (providerId = 0), skip insurance check
            if (request.getProviderId() == 0) {
                request.setInsuranceApproved(true); // Direct payment doesn't need insurance approval
            } else {
                String checkProviderQuery = "SELECT * FROM insurance_providers WHERE provider_id = " + request.getProviderId();
                ResultSet providerResult = MySQL.execute(checkProviderQuery);

                if (providerResult != null && providerResult.next()) {
                    // Basic validation - provider exists
                    boolean providerExists = true;

                    // Additional business rules (example)
                    String providerName = providerResult.getString("provider_name");
                    boolean isActiveProvider = !providerName.contains("(Inactive)"); // Example rule

                    request.setInsuranceApproved(providerExists && isActiveProvider);
                } else {
                    request.setInsuranceApproved(false);
                }
            }

            // REMOVED: request.setInsuranceApproved(true); // This was overriding the actual validation!
            if (request.isInsuranceApproved() && next != null) {
                next.handle(request);
            } else {
                System.out.println("❌ Insurance not approved. Claim rejected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setInsuranceApproved(false);
        }
    }
}
