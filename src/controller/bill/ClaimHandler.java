package controller.bill;

public interface ClaimHandler {
    void setNext(ClaimHandler nextHandler);
    void handle(InsuranceClaimRequest request);
}