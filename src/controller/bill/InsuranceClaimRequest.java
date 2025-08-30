package controller.bill;

public class InsuranceClaimRequest {
    private int billId;
    private int providerId;
    private double totalAmount;
    private boolean validBill;
    private boolean insuranceApproved;
    private boolean settled;

    public InsuranceClaimRequest(int billId, int providerId, double totalAmount) {
        this.billId = billId;
        this.providerId = providerId;
        this.totalAmount = totalAmount;
    }

    // Getters and setters
    public int getBillId() { return billId; }
    public int getProviderId() { return providerId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; } // Add this method

    public boolean isValidBill() { return validBill; }
    public void setValidBill(boolean validBill) { this.validBill = validBill; }

    public boolean isInsuranceApproved() { return insuranceApproved; }
    public void setInsuranceApproved(boolean insuranceApproved) { this.insuranceApproved = insuranceApproved; }

    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
}