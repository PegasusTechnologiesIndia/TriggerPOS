package org.phomellolitepos.database;

public class ZeroReport
{

    private String devicename;
    private String date;
    private String openingbal;
    private String expense;
    private String cashales;
    private String accountcash;
    private String totalreturn;
    private String totalcash;
    private String zcode;

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOpeningbal() {
        return openingbal;
    }

    public void setOpeningbal(String openingbal) {
        this.openingbal = openingbal;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getCashales() {
        return cashales;
    }

    public void setCashales(String cashales) {
        this.cashales = cashales;
    }

    public String getAccountcash() {
        return accountcash;
    }

    public void setAccountcash(String accountcash) {
        this.accountcash = accountcash;
    }

    public String getTotalreturn() {
        return totalreturn;
    }

    public void setTotalreturn(String totalreturn) {
        this.totalreturn = totalreturn;
    }

    public String getTotalcash() {
        return totalcash;
    }

    public void setTotalcash(String totalcash) {
        this.totalcash = totalcash;
    }

    public String getZcode() {
        return zcode;
    }

    public void setZcode(String zcode) {
        this.zcode = zcode;
    }

    public ZeroReport(String devicename, String date, String openingbal, String expense, String cashales, String accountcash, String totalreturn, String totalcash, String zcode) {
        this.devicename = devicename;
        this.date = date;
        this.openingbal = openingbal;
        this.expense = expense;
        this.cashales = cashales;
        this.accountcash = accountcash;
        this.totalreturn = totalreturn;
        this.totalcash = totalcash;
        this.zcode = zcode;
    }

}
