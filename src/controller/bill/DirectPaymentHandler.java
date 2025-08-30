package controller.bill;

import model.MySQL;
import java.sql.ResultSet;

public class DirectPaymentHandler implements ClaimHandler {

    private ClaimHandler next;

    @Override
    public void setNext(ClaimHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public void handle(InsuranceClaimRequest request) {
        System.out.println("💵 Direct payment initiated for Bill #" + request.getBillId());

        try {
            String updateBillQuery = "UPDATE bills SET "
                    + "paid = total_amount, "
                    + "balance = 0, "
                    + "bill_status_bill_status_id = 2 "
                    + "WHERE bill_id = " + request.getBillId();

            System.out.println("Executing query: " + updateBillQuery); // Debug line
            MySQL.execute(updateBillQuery);

            // Verify the update worked
            String verifyQuery = "SELECT * FROM bills WHERE bill_id = " + request.getBillId();
            ResultSet result = MySQL.execute(verifyQuery);
            if (result != null && result.next()) {
                System.out.println("Updated bill status: " + result.getInt("bill_status_bill_status_id"));
                System.out.println("Updated balance: " + result.getDouble("balance"));
            }

            request.setSettled(true);

            if (next != null) {
                next.handle(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setSettled(false);
        }
    }
}
