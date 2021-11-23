package org.phomellolitepos.database;

public class SalesByItemGroup
{
    private String itemcategory;
    private String saleprice;
    private String quantity;
    private String grossamount;
    private String discamnt;
    private String amountafterdis;
    private String netamount;

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
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

    public String getDiscamnt() {
        return discamnt;
    }

    public void setDiscamnt(String discamnt) {
        this.discamnt = discamnt;
    }

    public String getAmountafterdis() {
        return amountafterdis;
    }

    public void setAmountafterdis(String amountafterdis) {
        this.amountafterdis = amountafterdis;
    }

    public String getNetamount() {
        return netamount;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }



    public SalesByItemGroup(String itemcategory, String saleprice, String quantity, String grossamount, String discamnt, String amountafterdis, String netamount) {
        this.itemcategory = itemcategory;
        this.saleprice = saleprice;
        this.quantity = quantity;
        this.grossamount = grossamount;
        this.discamnt = discamnt;
        this.amountafterdis = amountafterdis;
        this.netamount = netamount;
    }


}
