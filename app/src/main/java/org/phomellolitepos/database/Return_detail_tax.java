package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Return_detail_tax {


    private static String tableName = "return_detail_tax";
    private String order_return_detail_tax_id;
    private String order_return_voucher_no;
    private String sr_no;
    private String item_code;
    private String tax_id;
    private String tax_type;
    private String rate;
    private String tax_value;


    private Database db;
    private ContentValues value;

    public Return_detail_tax(Context context, String order_returndetail_tax_id, String order_returnvoucherno, String sr_no, String item_code, String tax_id, String tax_type,
                            String rate, String tax_value) {

        db = new Database(context);
        value = new ContentValues();

        this.setOrder_return_detail_tax_id(order_returndetail_tax_id);
        this.setOrder_return_voucher_no(order_returnvoucherno);
        this.setSr_no(sr_no);
        this.setItem_code(item_code);
        this.setTax_id(tax_id);
        this.setTax_type(tax_type);
        this.setRate(rate);
        this.setTax_value(tax_value);



    }


    public String getOrder_return_detail_tax_id() {
        return order_return_detail_tax_id;
    }

    public void setOrder_return_detail_tax_id(String order_return_detail_tax_id) {
        this.order_return_detail_tax_id = order_return_detail_tax_id;
        value.put("order_return_detail_tax_id", order_return_detail_tax_id);
    }

    public String getOrder_return_voucher_no() {
        return order_return_voucher_no;
    }

    public void setOrder_return_voucher_no(String order_return_voucher_no) {
        this.order_return_voucher_no = order_return_voucher_no;
        value.put("order_return_voucher_no", order_return_voucher_no);
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
        value.put("sr_no", sr_no);
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
        value.put("tax_type", tax_type);
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
        value.put("rate", rate);
    }

    public String getTax_value() {
        return tax_value;
    }

    public void setTax_value(String tax_value) {
        this.tax_value = tax_value;
        value.put("tax_value", tax_value);
    }

    public long insertReturn_detail_tax(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "order_return_detail_tax_id", value);
        //database.close();
        return insert;
    }

    public long updateReturn_detail_tax(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        db.close();
        return insert;
    }

    public static long delete_Return_detail_tax(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Return_detail_tax getReturn_detail_tax(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Return_detail_tax master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Return_detail_tax(context, cursor.getString(0),
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
    public static ArrayList<Return_detail_tax> getAllReturn_detail_tax(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Return_detail_tax> list = new ArrayList<Return_detail_tax>();
        Return_detail_tax master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Return_detail_tax(context, cursor.getString(0),
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
