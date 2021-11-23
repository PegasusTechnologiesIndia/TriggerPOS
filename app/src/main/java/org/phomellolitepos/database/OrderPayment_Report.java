package org.phomellolitepos.database;

public class OrderPayment_Report
{
    private String invoicenumber;
    private String date;
    private String time;
    private String salesperson;

    public OrderPayment_Report(String invoicenumber, String date, String time, String salesperson, String devicename, String remarks, String paymentmethod, String salesamountafterdisc, String netamount, String paymaount, String ordertype) {
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.time = time;
        this.salesperson = salesperson;
        this.devicename = devicename;
        this.remarks = remarks;
        this.paymentmethod = paymentmethod;
        this.salesamountafterdisc = salesamountafterdisc;
        this.netamount = netamount;
        this.paymaount = paymaount;
        this.ordertype = ordertype;
    }

    private String devicename;
    private String remarks;
    private String paymentmethod;
    private String salesamountafterdisc;
    private String netamount;
    private String paymaount;
    private String ordertype;

    public String getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
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

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getSalesamountafterdisc() {
        return salesamountafterdisc;
    }

    public void setSalesamountafterdisc(String salesamountafterdisc) {
        this.salesamountafterdisc = salesamountafterdisc;
    }

    public String getNetamount() {
        return netamount;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }

    public String getPaymaount() {
        return paymaount;
    }

    public void setPaymaount(String paymaount) {
        this.paymaount = paymaount;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }



}
