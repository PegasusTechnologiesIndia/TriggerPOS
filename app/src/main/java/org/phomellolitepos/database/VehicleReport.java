package org.phomellolitepos.database;

public class VehicleReport {
    public String Vtype;
    public String Vdate;
    public String Vcount;
    public String Vprice;
    public String Vusername;


    public VehicleReport(String vehtype,String vehcount, String vehprice,String vehdate,String vehusername)
    {
        this.Vtype = vehtype;
        this.Vcount = vehcount;
        this.Vprice = vehprice;
        this.Vdate = vehdate;
        this.Vusername = vehusername;

    }

    public String getVdate() {
        return Vdate;
    }

    public void setVdate(String vdate) {
        Vdate = vdate;
    }

    public String getVusername() {
        return Vusername;
    }

    public void setVusername(String vusername) {
        Vusername = vusername;
    }

    public String getVtype() {
        return Vtype;
    }

    public void setVtype(String vtype) {
        Vtype = vtype;
    }

    public String getVcount() {
        return Vcount;
    }

    public void setVcount(String vcount) {
        Vcount = vcount;
    }

    public String getVprice() {
        return Vprice;
    }

    public void setVprice(String vprice) {
        Vprice = vprice;
    }
}
