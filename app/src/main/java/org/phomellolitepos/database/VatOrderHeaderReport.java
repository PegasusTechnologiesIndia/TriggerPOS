package org.phomellolitepos.database;

public class VatOrderHeaderReport
{
    String orderCode;
    String orderDate;
    String amtWithouttax;
    String disAmt;
    String totalVat;
    String invoiceAmt;


    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getAmtWithouttax() {
        return amtWithouttax;
    }

    public void setAmtWithouttax(String amtWithouttax) {
        this.amtWithouttax = amtWithouttax;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public String getTotalVat() {
        return totalVat;
    }

    public void setTotalVat(String totalVat) {
        this.totalVat = totalVat;
    }

    public String getInvoiceAmt() {
        return invoiceAmt;
    }

    public void setInvoiceAmt(String invoiceAmt) {
        this.invoiceAmt = invoiceAmt;
    }



    public VatOrderHeaderReport(String orderCode, String orderDate, String amtWithouttax, String disAmt, String totalVat, String invoiceAmt) {
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.amtWithouttax = amtWithouttax;
        this.disAmt = disAmt;
        this.totalVat = totalVat;
        this.invoiceAmt = invoiceAmt;
    }

}

