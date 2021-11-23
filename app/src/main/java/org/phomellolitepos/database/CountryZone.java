package org.phomellolitepos.database;

import android.content.Context;

public class CountryZone {
    private String Countryid;
    private String Countryname;
    private String Zoneid;
    private String ZoneName;
    private String countryZid;
Context context;


    public CountryZone(Context ctx, String Countryid, String Countryname, String zoneid, String zonename,String countryzid){

        this.context=ctx;
        this.Countryid=Countryid;
        this.Countryname=Countryname;
        this.Zoneid=zoneid;
        this.ZoneName=zonename;
        this.countryZid=countryzid;
    }
    public String getCountryid() {
        return Countryid;
    }

    public void setCountryid(String countryid) {

        this.Countryid = countryid;
    }

    public String getCountryname() {
        return Countryname;
    }

    public void setCountryname(String countryname) {

        this.Countryname = countryname;
    }

    public String getZoneid() {
        return Zoneid;
    }

    public void setZoneid(String zoneid) {

        this.Zoneid = zoneid;
    }

    public String getZoneName() {
        return ZoneName;
    }

    public void setZoneName(String zoneName) {

        this.ZoneName = zoneName;
    }

    public String getCountryZid() {
        return countryZid;
    }

    public void setCountryZid(String countryZid) {
        this.countryZid = countryZid;
    }
}
