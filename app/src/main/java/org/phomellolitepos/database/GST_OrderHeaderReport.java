package org.phomellolitepos.database;

public class GST_OrderHeaderReport
{
    private String orderno, orderDateTime,amtwithouTax,disPer,disAmt,
            amtAfterdis,cgstPer,cgstAmt,sgstPer,sgstAmt,igstPer,igstAmt,totalGst,invoiceAmt;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getAmtwithouTax() {
        return amtwithouTax;
    }

    public void setAmtwithouTax(String amtwithouTax) {
        this.amtwithouTax = amtwithouTax;
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

    public String getTotalGst() {
        return totalGst;
    }

    public void setTotalGst(String totalGst) {
        this.totalGst = totalGst;
    }

    public String getInvoiceAmt() {
        return invoiceAmt;
    }

    public void setInvoiceAmt(String invoiceAmt) {
        this.invoiceAmt = invoiceAmt;
    }



    public GST_OrderHeaderReport(String orderno, String orderDateTime, String amtwithouTax, String disPer, String disAmt, String amtAfterdis, String cgstPer, String cgstAmt, String sgstPer, String sgstAmt, String igstPer, String igstAmt, String totalGst, String invoiceAmt) {
        this.orderno = orderno;
        this.orderDateTime = orderDateTime;

        this.amtwithouTax = amtwithouTax;
        this.disPer = disPer;
        this.disAmt = disAmt;
        this.amtAfterdis = amtAfterdis;
        this.cgstPer = cgstPer;
        this.cgstAmt = cgstAmt;
        this.sgstPer = sgstPer;
        this.sgstAmt = sgstAmt;
        this.igstPer = igstPer;
        this.igstAmt = igstAmt;
        this.totalGst = totalGst;
        this.invoiceAmt = invoiceAmt;
    }

}
