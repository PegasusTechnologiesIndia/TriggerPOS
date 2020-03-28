package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class Purchase_Detail {

    private static String tableName = "purchase_detail";
    private String id;
    private String ref_voucher_no;
    private String s_no;
    private String item_code;
    private String qty;
    private String price;
    private String line_total;


    private Database db;
    private ContentValues value;

    public Purchase_Detail(Context context, String id, String ref_voucher_no, String s_no,
                         String item_code, String qty, String price, String line_total) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_ref_voucher_no(ref_voucher_no);
        this.set_s_no(s_no);
        this.set_item_code(item_code);
        this.set_qty(qty);
        this.set_price(price);
        this.set_line_total(line_total);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_ref_voucher_no() {
        return ref_voucher_no;
    }

    public void set_ref_voucher_no(String ref_voucher_no) {
        this.ref_voucher_no = ref_voucher_no;
        value.put("ref_voucher_no", ref_voucher_no);
    }

    public String get_s_no() {
        return s_no;
    }

    public void set_s_no(String s_no) {
        this.s_no = s_no;
        value.put("s_no", s_no);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_qty() {
        return qty;
    }

    public void set_qty(String qty) {
        this.qty = qty;
        value.put("qty", qty);
    }

    public String get_price() {
        return price;
    }

    public void set_price(String price) {
        this.price = price;
        value.put("price", price);
    }

    public String get_line_total() {
        return line_total;
    }

    public void set_line_total(String line_total) {
        this.line_total = line_total;
        value.put("line_total", line_total);
    }

    public long insertPurchase_Detail(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updatePurchase_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        sdb.close();
        return insert;
    }

    public static long delete_Purchase_Detail(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Purchase_Detail getPurchase_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Purchase_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Purchase_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
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
    public static ArrayList<Purchase_Detail> getAllPurchase_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Purchase_Detail> list = new ArrayList<Purchase_Detail>();
        Purchase_Detail master = null;
        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Purchase_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
