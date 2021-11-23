package org.phomellolitepos.database;

public class TaxReport
{
  private String taxId,taxName,taxType,TaxRate;

    public TaxReport(String taxId, String taxName, String taxType, String taxRate)
    {
        this.taxId = taxId;
        this.taxName = taxName;
        this.taxType = taxType;
        TaxRate = taxRate;
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
        return TaxRate;
    }

    public void setTaxRate(String taxRate) {
        TaxRate = taxRate;
    }
}
