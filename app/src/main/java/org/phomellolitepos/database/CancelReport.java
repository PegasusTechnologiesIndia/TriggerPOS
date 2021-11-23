package org.phomellolitepos.database;

public class CancelReport
{
    private String invoiceNumber;
    private String date;
    private String time;
    private String salesperson;
    private String deviceName;
    private String amtWithouttax;
    private String discountPer;
    private String discountAmt;
    private String amtAfterDis;
    private String taxAmt;
    private String netAmt;
    private String orderType;
    private String contact;


    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAmtWithouttax() {
        return amtWithouttax;
    }

    public void setAmtWithouttax(String amtWithouttax) {
        this.amtWithouttax = amtWithouttax;
    }

    public String getDiscountPer() {
        return discountPer;
    }

    public void setDiscountPer(String discountPer) {
        this.discountPer = discountPer;
    }

    public String getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        this.discountAmt = discountAmt;
    }

    public String getAmtAfterDis() {
        return amtAfterDis;
    }

    public void setAmtAfterDis(String amtAfterDis) {
        this.amtAfterDis = amtAfterDis;
    }

    public String getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        this.taxAmt = taxAmt;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


    public CancelReport(String invoiceNumber, String date, String time, String salesperson, String deviceName, String amtWithouttax, String discountPer, String discountAmt, String amtAfterDis, String taxAmt, String netAmt, String orderType, String contact) {
        this.invoiceNumber = invoiceNumber;
        this.date = date;
        this.time = time;
        this.salesperson = salesperson;
        this.deviceName = deviceName;
        this.amtWithouttax = amtWithouttax;
        this.discountPer = discountPer;
        this.discountAmt = discountAmt;
        this.amtAfterDis = amtAfterDis;
        this.taxAmt = taxAmt;
        this.netAmt = netAmt;
        this.orderType = orderType;
        this.contact = contact;
    }

}
