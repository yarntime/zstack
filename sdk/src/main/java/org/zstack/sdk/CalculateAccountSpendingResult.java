package org.zstack.sdk;

public class CalculateAccountSpendingResult {
    public double total;
    public void setTotal(double total) {
        this.total = total;
    }
    public double getTotal() {
        return this.total;
    }

    public java.util.List<Spending> spending;
    public void setSpending(java.util.List<Spending> spending) {
        this.spending = spending;
    }
    public java.util.List<Spending> getSpending() {
        return this.spending;
    }

}
