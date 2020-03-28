package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 4/17/2017.
 */

public class Order_Detail_Tax {

    private static String tableName = "order_detail_tax";
    private String order_detail_tax_id;
    private String order_code;
    private String sr_no;
    private String item_code;
    private String tax_id;
    private String tax_type;
    private String rate;
    private String tax_value;


    private Database db;
    private ContentValues value;

    public Order_Detail_Tax(Context context, String order_detail_tax_id, String order_code, String sr_no, String item_code, String tax_id, String tax_type,
                            String rate, String tax_value) {

        db = new Database(context);
        value = new ContentValues();

        this.set_order_detail_tax_id(order_detail_tax_id);
        this.set_order_code(order_code);
        this.set_sr_no(sr_no);
        this.set_item_code(item_code);
        this.set_tax_id(tax_id);
        this.set_tax_type(tax_type);
        this.set_rate(rate);
        this.set_tax_value(tax_value);



    }

    public String get_order_detail_tax_id() {
        return order_detail_tax_id;
    }

    public void set_order_detail_tax_id(String order_detail_tax_id) {
        this.order_detail_tax_id = order_detail_tax_id;
        value.put("order_detail_tax_id", order_detail_tax_id);
    }


    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }

    public String get_sr_no() {
        return sr_no;
    }

    public void set_sr_no(String sr_no) {
        this.sr_no = sr_no;
        value.put("sr_no", sr_no);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }

    public String get_tax_type() {
        return tax_type;
    }

    public void set_tax_type(String tax_type) {
        this.tax_type = tax_type;
        value.put("tax_type", tax_type);
    }

    public String get_rate() {
        return rate;
    }

    public void set_rate(String rate) {
        this.rate = rate;
        value.put("rate", rate);
    }

    public String get_tax_value() {
        return tax_value;
    }

    public void set_tax_value(String tax_value) {
        this.tax_value = tax_value;
        value.put("tax_value", tax_value);
    }

    public long insertOrder_Detail_Tax(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "order_detail_tax_id", value);
        //database.close();
        return insert;
    }

    public long updateOrder_Detail_Tax(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        db.close();
        return insert;
    }

    public static long delete_Order_Detail_Tax(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Order_Detail_Tax getOrder_Detail_Tax(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Order_Detail_Tax master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Detail_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Order_Detail_Tax> getAllOrder_Detail_Tax(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Order_Detail_Tax> list = new ArrayList<Order_Detail_Tax>();
        Order_Detail_Tax master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Detail_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
