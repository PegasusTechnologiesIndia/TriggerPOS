package org.phomellolitepos.database;

public class DailySalesReport
{
    public String netAmt,amtWithoutTax,amtAfterDiscount,taxAmt, totalInvoice,disAmt,orderType;
    public DailySalesReport(String netAmt, String amtWithoutTax, String amtAfterDiscount, String taxAmt, String totalAmt, String disAmt, String orderType) {
        this.netAmt = netAmt;
        this.amtWithoutTax = amtWithoutTax;
        this.amtAfterDiscount = amtAfterDiscount;
        this.taxAmt = taxAmt;
        this.totalInvoice = totalAmt;
        this.disAmt = disAmt;
        this.orderType = orderType;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }

    public String getAmtWithoutTax() {
        return amtWithoutTax;
    }

    public void setAmtWithoutTax(String amtWithoutTax) {
        this.amtWithoutTax = amtWithoutTax;
    }

    public String getAmtAfterDiscount() {
        return amtAfterDiscount;
    }

    public void setAmtAfterDiscount(String amtAfterDiscount) {
        this.amtAfterDiscount = amtAfterDiscount;
    }

    public String getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        this.taxAmt = taxAmt;
    }

    public String getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(String totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
