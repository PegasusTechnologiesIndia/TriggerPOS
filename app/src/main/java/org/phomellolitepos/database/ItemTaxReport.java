package org.phomellolitepos.database;

public class ItemTaxReport
{
    private String itemcode;

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

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getIsInclusiveTax() {
        return isInclusiveTax;
    }

    public void setIsInclusiveTax(String isInclusiveTax) {
        this.isInclusiveTax = isInclusiveTax;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    private String itemname;
    private String hsnCode;
    private String barcode;
    private String itemType;
    private String isInclusiveTax;
    private String taxId;
    private String taxName;
    private String taxType;

    public ItemTaxReport(String itemcode, String itemname, String hsnCode, String barcode, String itemType, String isInclusiveTax, String taxId, String taxName, String taxType, String taxRate) {
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.hsnCode = hsnCode;
        this.barcode = barcode;
        this.itemType = itemType;
        this.isInclusiveTax = isInclusiveTax;
        this.taxId = taxId;
        this.taxName = taxName;
        this.taxType = taxType;
        this.taxRate = taxRate;
    }

    private String taxRate;
}
