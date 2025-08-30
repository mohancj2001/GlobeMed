package controller.bill;

import model.MySQL;

public class SettlementHandler implements ClaimHandler {

    private ClaimHandler next;

    @Override
    public void setNext(ClaimHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public void handle(InsuranceClaimRequest request) {
        try {
            // In SettlementHandler.handle() method
            if (request.isInsuranceApproved()) {
                // Insert insurance claim record
                String insertClaimQuery = "INSERT INTO insurance_claims "
                        + "(submitted_date, bills_bill_id, insurance_providers_provider_id, claim_status_claim_status_id) "
                        + "VALUES (NOW(), " + request.getBillId() + ", "
                        + request.getProviderId() + ", 1)"; // Assuming 1 = Submitted/Pending

                System.out.println("Executing SQL: " + insertClaimQuery);
                MySQL.execute(insertClaimQuery);

                // Update bill status to indicate insurance claim
                String updateBillQuery = "UPDATE bills SET "
                        + "bill_status_bill_status_id = 3, "
                        + // Assuming 3 = Insurance Claim
                        "paid = total_amount, balance = 0 "
                        + // Reset balance for insurance processing
                        "WHERE bill_id = " + request.getBillId();

                System.out.println("Executing SQL: " + updateBillQuery);
                MySQL.execute(updateBillQuery);

                request.setSettled(true);
            } else {
                System.out.println("❌ Claim settlement failed. Insurance not approved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
