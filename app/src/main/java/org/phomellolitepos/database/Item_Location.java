package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by pegasus-andorid-1 on 1/16/2017.
 */

public class Item_Location {
    private static String tableName = "item_location";
    private String item_location_id;
    private String location_id;
    private String item_code;
    private String cost_price;
    private String markup;
    private String selling_price;
    private String quantity;
    private String loyalty_point;
    private String reorder_point;
    private String reorder_amount;
    private String is_inventory_tracking;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String new_sell_price;

    private Database db;
    private ContentValues value;


    public Item_Location(Context context, String item_location_id, String location_id, String item_code,
                         String cost_price, String markup, String selling_price, String quantity,
                         String loyalty_point, String reorder_point, String reorder_amount, String is_inventory_tracking, String is_active,
                         String modified_by, String modified_date,String new_sell_price) {


        db = new Database(context);
        value = new ContentValues();


        this.set_item_location_id(item_location_id);
        this.set_location_id(location_id);
        this.set_item_code(item_code);
        this.set_cost_price(cost_price);
        this.set_markup(markup);
        this.set_selling_price(selling_price);
        this.set_quantity(quantity);
        this.set_loyalty_point(loyalty_point);
        this.set_reorder_point(reorder_point);
        this.set_reorder_amount(reorder_amount);
        this.set_is_inventory_tracking(is_inventory_tracking);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_new_sell_price(new_sell_price);

    }


    public String get_item_location_id() {
        return item_location_id;
    }

    public void set_item_location_id(String item_location_id) {
        this.item_location_id = item_location_id;
        value.put("item_location_id", item_location_id);
    }


    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }


    public String get_cost_price() {
        return cost_price;
    }

    public void set_cost_price(String cost_price) {
        this.cost_price = cost_price;
        value.put("cost_price", cost_price);
    }

    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id = location_id;
        value.put("location_id", location_id);
    }

    public String get_markup() {
        return markup;
    }

    public void set_markup(String markup) {
        this.markup = markup;
        value.put("markup", markup);
    }

    public String get_selling_price() {
        return selling_price;
    }

    public void set_selling_price(String selling_price) {
        this.selling_price = selling_price;
        value.put("selling_price", selling_price);
    }

    public String get_quantity() {
        return quantity;
    }

    public void set_quantity(String quantity) {
        this.quantity = quantity;
        value.put("quantity", quantity);
    }

    public String get_loyalty_point() {
        return loyalty_point;
    }

    public void set_loyalty_point(String loyalty_point) {
        this.loyalty_point = loyalty_point;
        value.put("loyalty_point", loyalty_point);
    }


    public String get_reorder_point() {
        return reorder_point;
    }

    public void set_reorder_point(String reorder_point) {
        this.reorder_point = reorder_point;
        value.put("reorder_point", reorder_point);
    }


    public String get_reorder_amount() {
        return reorder_amount;
    }

    public void set_reorder_amount(String reorder_amount) {
        this.reorder_amount = reorder_amount;
        value.put("reorder_amount", reorder_amount);
    }

    public String get_is_inventory_tracking() {
        return is_inventory_tracking;
    }

    public void set_is_inventory_tracking(String is_inventory_tracking) {
        this.is_inventory_tracking = is_inventory_tracking;
        value.put("is_inventory_tracking", is_inventory_tracking);
    }


    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }


    public String get_modified_by() {
        return modified_by;
    }

    public void set_modified_by(String modified_by) {
        this.modified_by = modified_by;
        value.put("modified_by", modified_by);
    }

    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }

    public String get_new_sell_price() {
        return new_sell_price;
    }

    public void set_new_sell_price(String new_sell_price) {
        this.new_sell_price = new_sell_price;
        value.put("new_sell_price", new_sell_price);
    }


    public long insertItem_Location(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "item_location_id", value);
        //database.close();
        return insert;
    }

    public long updateItem_Location(String s, String[] strings, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, s,
                strings, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public long deleteItem_Location(String whereClause, String[] whereArgs)
            throws SQLiteConstraintException {
        SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = sdb.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return insert;
    }


    public static Item_Location getItem_Location(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Item_Location master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            do {
                master = new Item_Location(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }


    public static Item_Location getItem_LocationForJson(Context context, String WhereClasue) {
        String Query = "SELECT * FROM " + tableName + "INNER JOIN  Item on  Item.Item_code = Item_Location.Item_code" + WhereClasue;
        Item_Location master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            do {
                master = new Item_Location(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes

    public static ArrayList<Item_Location> getAllItem_Location(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Location> list = new ArrayList<Item_Location>();
        Item_Location master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            do {
                master = new Item_Location(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


}
