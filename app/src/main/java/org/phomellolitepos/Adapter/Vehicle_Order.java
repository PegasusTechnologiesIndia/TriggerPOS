package org.phomellolitepos.Adapter;

public class Vehicle_Order {
    public String ordercode;
    public String vehicleno;
    public String mobileno;
    public String inTime;
    public String vehiclestatus;
    public String advanceamnt;
    public String amount;
    public String rfid;
    public String UnitId;

    public Vehicle_Order(String order_code,String vehno, String mobile_no,
                       String in_tym,String veh_status,String advamnt,String amnt,String rfidvalue,String unitid)
    {
        this.ordercode = order_code;
        this.vehicleno = vehno;
        this.mobileno = mobile_no;
        this.inTime = in_tym;
        this.vehiclestatus = veh_status;
        this.advanceamnt = advamnt;
        this.amount = amnt;
        this.rfid = rfidvalue;
        this.UnitId=unitid;
    }

    public String getUnitId() {
        return UnitId;
    }

    public void setUnitId(String unitId) {
        UnitId = unitId;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getVehiclestatus() {
        return vehiclestatus;
    }

    public void setVehiclestatus(String vehiclestatus) {
        this.vehiclestatus = vehiclestatus;
    }

    public String getAdvanceamnt() {
        return advanceamnt;
    }

    public void setAdvanceamnt(String advanceamnt) {
        this.advanceamnt = advanceamnt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
