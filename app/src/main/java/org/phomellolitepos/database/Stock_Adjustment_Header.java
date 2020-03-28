package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/4/2018.
 */

public class Stock_Adjustment_Header {

    private static String tableName = "stock_adjustment_header";
    private String id;
    private String voucher_no;
    private String date;
    private String remarks;
    private String is_post;
    private String is_cancel;
    private String is_active;
    private String modified_by;
    private String modified_date;

    private Database db;
    private ContentValues value;

    public Stock_Adjustment_Header(Context context, String id, String voucher_no, String date,String remarks,
                                   String is_post, String is_cancel, String is_active,
                                   String modified_by, String modified_date) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_voucher_no(voucher_no);
        this.set_date(date);
        this.set_remarks(remarks);
        this.set_is_post(is_post);
        this.set_is_cancel(is_cancel);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);

    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_voucher_no() {
        return voucher_no;
    }

    public void set_voucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
        value.put("voucher_no", voucher_no);
    }

    public String get_date() {
        return date;
    }

    public void set_date(String date) {
        this.date = date;
        value.put("date", date);
    }

    public String get_remarks() {
        return remarks;
    }

    public void set_remarks(String remarks) {
        this.remarks = remarks;
        value.put("remarks", remarks);
    }

    public String get_is_post() {
        return is_post;
    }

    public void set_is_post(String is_post) {
        this.is_post = is_post;
        value.put("is_post", is_post);
    }

    public String get_is_cancel() {
        return is_cancel;
    }

    public void set_is_cancel(String is_cancel) {
        this.is_cancel = is_cancel;
        value.put("is_cancel", is_cancel);
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

    public long insertstock_adjustment_header(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();

        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updatestock_adjustment_header(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        sdb.close();
        return insert;
    }

    public static long delete_stock_adjustment_header(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Stock_Adjustment_Header getstock_adjustment_header(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Stock_Adjustment_Header master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Stock_Adjustment_Header(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7),cursor.getString(8));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }


    public static String getTotalCash(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select SUM(pay_amount) FROM " + tableName + " " + WhereClasue;
        String master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                master = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Stock_Adjustment_Header> getAllstock_adjustment_header(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Stock_Adjustment_Header> list = new ArrayList<Stock_Adjustment_Header>();
        Stock_Adjustment_Header master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Stock_Adjustment_Header(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7),cursor.getString(8));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
