package org.phomellolitepos.database;

public class ItemProfitReport
{
    private String itemCode;
    private String itemName;
    private String costprice;
    private String salesprice;
    private String Qty;
    private String netProfit;
    private String netProfitPer;

    public ItemProfitReport(String itemCode, String itemName, String costprice, String salesprice, String qty, String netProfit, String netProfitPer) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.costprice = costprice;
        this.salesprice = salesprice;
        Qty = qty;
        this.netProfit = netProfit;
        this.netProfitPer = netProfitPer;
    }




    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }

    public String getSalesprice() {
        return salesprice;
    }

    public void setSalesprice(String salesprice) {
        this.salesprice = salesprice;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(String netProfit) {
        this.netProfit = netProfit;
    }

    public String getNetProfitPer() {
        return netProfitPer;
    }

    public void setNetProfitPer(String netProfitPer) {
        this.netProfitPer = netProfitPer;
    }




}
