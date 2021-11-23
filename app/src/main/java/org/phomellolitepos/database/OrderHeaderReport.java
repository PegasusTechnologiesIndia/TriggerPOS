package org.phomellolitepos.database;

public class OrderHeaderReport
{
    private String invoicenumber;
    private String date;
    private String time;
    private String salesperson;
    private String devicename;
    private String totalitem;
    private String totalquantity;
    private String remarks;
    private String amount;
    private String discountper;
    private String discountamnt;
    private String amountafterdis;
    private String netamnt;
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

    public String getTotalitem() {
        return totalitem;
    }

    public void setTotalitem(String totalitem) {
        this.totalitem = totalitem;
    }

    public String getTotalquantity() {
        return totalquantity;
    }

    public void setTotalquantity(String totalquantity) {
        this.totalquantity = totalquantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscountper() {
        return discountper;
    }

    public void setDiscountper(String discountper) {
        this.discountper = discountper;
    }

    public String getDiscountamnt() {
        return discountamnt;
    }

    public void setDiscountamnt(String discountamnt) {
        this.discountamnt = discountamnt;
    }

    public String getAmountafterdis() {
        return amountafterdis;
    }

    public void setAmountafterdis(String amountafterdis) {
        this.amountafterdis = amountafterdis;
    }

    public String getNetamnt() {
        return netamnt;
    }

    public void setNetamnt(String netamnt) {
        this.netamnt = netamnt;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }


//    public OrderHeaderReport(String invoicenumber, String date, String time, String salesperson, String devicename, String totalitem, String totalquantity, String remarks, String amount, String discountper, String discountamnt, String amountafterdis, String netamnt, String ordertype) {
//    }
public OrderHeaderReport(String invoicenumber, String date, String time, String salesperson, String devicename, String totalitem, String totalquantity, String remarks, String amount, String discountper, String discountamnt, String amountafterdis, String netamnt, String ordertype) {
    this.invoicenumber = invoicenumber;
    this.date = date;
    this.time = time;
    this.salesperson = salesperson;
    this.devicename = devicename;
    this.totalitem = totalitem;
    this.totalquantity = totalquantity;
    this.remarks = remarks;
    this.amount = amount;
    this.discountper = discountper;
    this.discountamnt = discountamnt;
    this.amountafterdis = amountafterdis;
    this.netamnt = netamnt;
    this.ordertype = ordertype;
}
}
