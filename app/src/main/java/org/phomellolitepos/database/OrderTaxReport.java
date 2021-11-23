package org.phomellolitepos.database;

public class OrderTaxReport
{
    private String taxId,taxName,taxType,taxRate,name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderTaxReport(String taxId, String taxName, String taxType, String taxRate, String name)
    {
        this.taxId = taxId;
        this.taxName = taxName;
        this.taxType = taxType;
        this.taxRate = taxRate;
        this.name = name;
    }
}
