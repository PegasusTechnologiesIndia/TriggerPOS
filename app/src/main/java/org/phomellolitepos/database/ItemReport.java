package org.phomellolitepos.database;

public class ItemReport
{
    private String itemcode;
    private String itemname;
    private String costprice;
    private String saleprice;
    private String newsaleprice;

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

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }

    public String getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(String saleprice) {
        this.saleprice = saleprice;
    }

    public String getNewsaleprice() {
        return newsaleprice;
    }

    public void setNewsaleprice(String newsaleprice) {
        this.newsaleprice = newsaleprice;
    }



 public ItemReport(String itemcode, String itemname, String costprice, String saleprice, String newsaleprice)
 {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.costprice = costprice;
        this.saleprice = saleprice;
        this.newsaleprice = newsaleprice;
 }



}
