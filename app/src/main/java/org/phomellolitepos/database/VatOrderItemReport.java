package org.phomellolitepos.database;

public class VatOrderItemReport
{


  private String itemCategory,itemcode,itemName,hsnCode,invoiceNum,dateTime,deviceName,
          salepricewithouttax,qty,uom,grossAmountWithoutTax,disPer,disAmt,amtAfterdis,totalVat,netAmount;

  public VatOrderItemReport(String itemCategory, String itemcode, String itemName, String hsnCode, String invoiceNum, String dateTime, String deviceName, String salepricewithouttax, String qty, String uom, String grossAmountWithoutTax, String disPer, String disAmt, String amtAfterdis, String totalVat, String netAmount) {
    this.itemCategory = itemCategory;
    this.itemcode = itemcode;
    this.itemName = itemName;
    this.hsnCode = hsnCode;
    this.invoiceNum = invoiceNum;
    this.dateTime = dateTime;
    this.deviceName = deviceName;
    this.salepricewithouttax = salepricewithouttax;
    this.qty = qty;
    this.uom = uom;
    this.grossAmountWithoutTax = grossAmountWithoutTax;
    this.disPer = disPer;
    this.disAmt = disAmt;
    this.amtAfterdis = amtAfterdis;
    this.totalVat = totalVat;
    this.netAmount = netAmount;
  }

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

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getHsnCode() {
    return hsnCode;
  }

  public void setHsnCode(String hsnCode) {
    this.hsnCode = hsnCode;
  }

  public String getInvoiceNum() {
    return invoiceNum;
  }

  public void setInvoiceNum(String invoiceNum) {
    this.invoiceNum = invoiceNum;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getSalepricewithouttax() {
    return salepricewithouttax;
  }

  public void setSalepricewithouttax(String salepricewithouttax) {
    this.salepricewithouttax = salepricewithouttax;
  }

  public String getQty() {
    return qty;
  }

  public void setQty(String qty) {
    this.qty = qty;
  }

  public String getUom() {
    return uom;
  }

  public void setUom(String uom) {
    this.uom = uom;
  }

  public String getGrossAmountWithoutTax() {
    return grossAmountWithoutTax;
  }

  public void setGrossAmountWithoutTax(String grossAmountWithoutTax) {
    this.grossAmountWithoutTax = grossAmountWithoutTax;
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

  public String getTotalVat() {
    return totalVat;
  }

  public void setTotalVat(String totalVat) {
    this.totalVat = totalVat;
  }

  public String getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(String netAmount) {
    this.netAmount = netAmount;
  }
}
