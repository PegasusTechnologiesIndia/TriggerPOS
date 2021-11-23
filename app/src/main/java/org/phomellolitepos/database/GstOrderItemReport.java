package org.phomellolitepos.database;

public class GstOrderItemReport
{
    private String itemno;
    private String itemcategory;
    private String itemname;
    private String hsncode;
    private String invoicenum;
    private String dateTime;
    private String salePriceWithoutTax;
    private String qty;
    private String Uom;
    private String gAmtWithoutTax;
    private String disPer;
    private String disAmt;
    private String amtAfterdis;
    private String cgstPer;
    private String cgstAmt;
    private String sgstPer;
    private String sgstAmt;
    private String igstPer;
    private String igstAmt;
    private String totalTax;
    private String netAmt;

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getHsncode() {
        return hsncode;
    }

    public void setHsncode(String hsncode) {
        this.hsncode = hsncode;
    }

    public String getInvoicenum() {
        return invoicenum;
    }

    public void setInvoicenum(String invoicenum) {
        this.invoicenum = invoicenum;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSalePriceWithoutTax() {
        return salePriceWithoutTax;
    }

    public void setSalePriceWithoutTax(String salePriceWithoutTax) {
        this.salePriceWithoutTax = salePriceWithoutTax;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getgAmtWithoutTax() {
        return gAmtWithoutTax;
    }

    public void setgAmtWithoutTax(String gAmtWithoutTax) {
        this.gAmtWithoutTax = gAmtWithoutTax;
    }

    public String getDisPer() {
        return disPer;
    }

    public void setDisPer(String disPer) {
        this.disPer = disPer;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public String getAmtAfterdis() {
        return amtAfterdis;
    }

    public void setAmtAfterdis(String amtAfterdis) {
        this.amtAfterdis = amtAfterdis;
    }

    public String getCgstPer() {
        return cgstPer;
    }

    public void setCgstPer(String cgstPer) {
        this.cgstPer = cgstPer;
    }

    public String getCgstAmt() {
        return cgstAmt;
    }

    public void setCgstAmt(String cgstAmt) {
        this.cgstAmt = cgstAmt;
    }

    public String getSgstPer() {
        return sgstPer;
    }

    public void setSgstPer(String sgstPer) {
        this.sgstPer = sgstPer;
    }

    public String getSgstAmt() {
        return sgstAmt;
    }

    public void setSgstAmt(String sgstAmt) {
        this.sgstAmt = sgstAmt;
    }

    public String getIgstPer() {
        return igstPer;
    }

    public void setIgstPer(String igstPer) {
        this.igstPer = igstPer;
    }

    public String getIgstAmt() {
        return igstAmt;
    }

    public void setIgstAmt(String igstAmt) {
        this.igstAmt = igstAmt;
    }

    public String getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }


    public GstOrderItemReport(String itemno, String itemcategory, String itemname, String hsncode, String invoicenum, String date, String salePriceWithoutTax, String qty, String uom, String gAmtWithoutTax, String disPer, String disAmt, String amtAfterdis, String cgstPer, String cgstAmt, String sgstPer, String sgstAmt, String igstPer, String igstAmt, String totalTax, String netAmt) {
        this.itemno = itemno;
        this.itemcategory = itemcategory;
        this.itemname = itemname;
        this.hsncode = hsncode;
        this.invoicenum = invoicenum;
        this.dateTime = date;
        this.salePriceWithoutTax = salePriceWithoutTax;
        this.qty = qty;
        Uom = uom;
        this.gAmtWithoutTax = gAmtWithoutTax;
        this.disPer = disPer;
        this.disAmt = disAmt;
        this.amtAfterdis = amtAfterdis;
        this.cgstPer = cgstPer;
        this.cgstAmt = cgstAmt;
        this.sgstPer = sgstPer;
        this.sgstAmt = sgstAmt;
        this.igstPer = igstPer;
        this.igstAmt = igstAmt;
        this.totalTax = totalTax;
        this.netAmt = netAmt;
    }



}
