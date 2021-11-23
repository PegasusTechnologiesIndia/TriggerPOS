package org.phomellolitepos.Adapter;

public class Table_Order {
    public String table_code;
    private String table_name;
    private String zone_name;
    private String zone_id;
    private String noOfOrder;
    public Table_Order(String tablecode,String tablename, String zoneid,
                    String zonename,String NoOfOrder)
    {
        this.table_code = tablecode;
        this.table_name = tablename;
        this.zone_id = zoneid;
        this.zone_name = zonename;
        this.noOfOrder = NoOfOrder;
    }


    public String getTable_code() {
        return table_code;
    }

    public void setTable_code(String table_code) {
        this.table_code = table_code;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getNoOfOrder() {
        return noOfOrder;
    }

    public void setNoOfOrder(String noOfOrder) {
        this.noOfOrder = noOfOrder;
    }
}
