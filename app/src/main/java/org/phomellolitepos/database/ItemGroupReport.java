package org.phomellolitepos.database;

public class ItemGroupReport
{


    private String srno;
    private String groupCode;
    private String groupName;


    public ItemGroupReport(String srno,String groupCode, String groupName)
    {
        this.srno=srno;
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    public String getSrno(){return srno;}

    public void setSrno(String srno){this.srno = srno;}

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}
