package org.phomellolitepos.database;

public class ModiefierData {

    public String itemcode;
    public String modifiercode;
    public String item_name;
    public String item_groupcode;
    public ModiefierData(String itemcode,String modifiercode, String itemname,String itemgroupcode)
    {
        this.itemcode = itemcode;
        this.modifiercode = modifiercode;
        this.item_name = itemname;
        this.item_groupcode = itemgroupcode;

    }

    public String getItem_groupcode() {
        return item_groupcode;
    }

    public void setItem_groupcode(String item_groupcode) {
        this.item_groupcode = item_groupcode;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getModifiercode() {
        return modifiercode;
    }

    public void setModifiercode(String modifiercode) {
        this.modifiercode = modifiercode;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}
