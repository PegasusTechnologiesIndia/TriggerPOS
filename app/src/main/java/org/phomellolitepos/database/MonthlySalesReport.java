package org.phomellolitepos.database;

public class MonthlySalesReport {
    private String UserId;
    private String DeviceName;
    private String TotalInvoice;
    private String NetAmount;
    private String Amount;

    private String month;
    private String year;
    private String locationname;
    private String OrderType;
    private String discountamount;

    public MonthlySalesReport(String userId, String deviceName, String totalInvoice, String netAmount, String amount, String month, String year, String locationname, String orderType,String discountamount) {
        this.setUserId(userId);
        this.setDeviceName(deviceName);
        this.setTotalInvoice(totalInvoice);
        this.setNetAmount(netAmount);
        this.setAmount(amount);
        this.setMonth(month);
        this.setYear(year);
        this.setLocationname(locationname);
        this.setOrderType(orderType);
        this.setDiscountamount(discountamount);
    }


    public String getDiscountamount() {
        return discountamount;
    }

    public void setDiscountamount(String discountamount) {
        this.discountamount = discountamount;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getTotalInvoice() {
        return TotalInvoice;
    }

    public void setTotalInvoice(String totalInvoice) {
        TotalInvoice = totalInvoice;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }
}
