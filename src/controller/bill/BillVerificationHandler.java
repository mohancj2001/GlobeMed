package controller.bill;

import model.MySQL;
import java.sql.ResultSet;

public class BillVerificationHandler implements ClaimHandler {

    private ClaimHandler next;

    @Override
    public void setNext(ClaimHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public void handle(InsuranceClaimRequest request) {
        System.out.println("🔎 Checking bill validity for Bill #" + request.getBillId());

        try {
            String checkBillQuery = "SELECT b.*, COUNT(bhtp.treatment_prices_id) as treatment_count "
                    + "FROM bills b "
                    + "LEFT JOIN bills_has_treatment_prices bhtp ON b.bill_id = bhtp.bills_bill_id "
                    + "WHERE b.bill_id = " + request.getBillId() + " "
                    + "GROUP BY b.bill_id";

            ResultSet billResult = MySQL.execute(checkBillQuery);
            if (billResult != null && billResult.next()) {
                boolean billExists = billResult.getInt("bill_id") > 0;
                boolean hasTreatments = billResult.getInt("treatment_count") > 0;
                request.setValidBill(billExists && hasTreatments);

                // Also set the total amount from database
                request.setTotalAmount(billResult.getDouble("total_amount"));
            } else {
                request.setValidBill(false);
            }

            // REMOVED: request.setValidBill(true); // This was overriding the actual validation!
            if (request.isValidBill() && next != null) {
                next.handle(request);
            } else {
                System.out.println("❌ Invalid bill. Claim stopped.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setValidBill(false); // Set to false on error
        }
    }
}
