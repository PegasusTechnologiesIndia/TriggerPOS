package org.phomellolitepos.database;

public class SalesByItemReport
{
    private  String invoicenumber;
    private  String date;
    private  String time;
    private  String itemcategory;
    private  String itemcode;
    private  String itemname;
    private  String ordertype;
    private  String saleprice;
    private  String quantity;
    private  String grossamount;
    private  String netamount;
    private  String discamount;

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

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(String saleprice) {
        this.saleprice = saleprice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGrossamount() {
        return grossamount;
    }

    public void setGrossamount(String grossamount) {
        this.grossamount = grossamount;
    }

    public String getNetamount() {
        return netamount;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }

    public String getDiscamount() {
        return discamount;
    }

    public void setDiscamount(String discamount) {
        this.discamount = discamount;
    }



    public SalesByItemReport(String invoicenumber, String date, String time, String itemcategory, String itemcode, String itemname, String ordertype, String saleprice, String quantity, String grossamount, String netamount, String discamount) {
        this.invoicenumber = invoicenumber;
        this.date = date;
        this.time = time;
        this.itemcategory = itemcategory;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.ordertype = ordertype;
        this.saleprice = saleprice;
        this.quantity = quantity;
        this.grossamount = grossamount;
        this.netamount = netamount;
        this.discamount = discamount;
    }

}
