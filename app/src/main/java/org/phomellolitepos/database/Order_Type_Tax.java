package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/23/2017.
 */

public class Order_Type_Tax {

    private static String tableName = "order_type_tax";
    private String location_id;
    private String tax_id;
    private String order_type_id;

    private Database db;
    private ContentValues value;

    public Order_Type_Tax(Context context, String location_id, String tax_id,
                          String order_type_id) {

        db = new Database(context);
        value = new ContentValues();

        this.set_location_id(location_id);
        this.set_tax_id(tax_id);
        this.set_order_type_id(order_type_id);
    }


    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id = location_id;
        value.put("location_id", location_id);
    }

    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }

    public String get_order_type_id() {
        return order_type_id;
    }

    public void set_order_type_id(String order_type_id) {
        this.order_type_id = order_type_id;
        value.put("order_type_id", order_type_id);
    }

    public long insertOrder_Type_Tax(SQLiteDatabase database) {
        // SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "order_type_tax_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Order_Type_Tax(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);
        return 1;
    }


    public long updateOrder_Type_Tax(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        return insert;
    }

    public static Order_Type_Tax getOrder_Type_Tax(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Order_Type_Tax master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Type_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Order_Type_Tax> getAllOrder_Type_Tax(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Order_Type_Tax> list = new ArrayList<Order_Type_Tax>();
        Order_Type_Tax master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Type_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


}
