package org.phomellolitepos.database;

public class OrderDetail_Report
{
    private String itemCategory;
    private String itemcode;
    private String itemname;
    private String ordertype;
    private String saleprice;
    private String quantity;


    private String grossamount;
    private String discountamount;
    private String amountafterdisc;
    private String netamount;

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
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

    public String getDiscountamount() {
        return discountamount;
    }

    public void setDiscountamount(String discountamount) {
        this.discountamount = discountamount;
    }

    public String getAmountafterdisc() {
        return amountafterdisc;
    }

    public void setAmountafterdisc(String amountafterdisc) {
        this.amountafterdisc = amountafterdisc;
    }

    public String getNetamount() {
        return netamount;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }


    public OrderDetail_Report(String itemCategory, String itemcode, String itemname, String ordertype, String saleprice, String quantity, String grossamount, String discountamount, String amountafterdisc, String netamount) {
        this.itemCategory = itemCategory;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.ordertype = ordertype;
        this.saleprice = saleprice;
        this.quantity = quantity;
        this.grossamount = grossamount;
        this.discountamount = discountamount;
        this.amountafterdisc = amountafterdisc;
        this.netamount = netamount;
    }

}
