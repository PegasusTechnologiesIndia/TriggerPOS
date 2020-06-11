package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 3/10/2018.
 */

public class Acc_Customer_Credit {

    private static String tableName = "Acc_Customer_Credit";
    private String id;
    private String trans_date;
    private String contact_code;
    private String cr_amount;
    private String paid_amount;
    private String balance_amount;
    private String z_no;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String voucher_no;
    private Database db;
    private ContentValues value;

    public Acc_Customer_Credit(Context context, String id, String trans_date,
                String contact_code, String cr_amount, String paid_amount, String balance_amount, String z_no,String is_active, String modified_by, String modified_date,String voucherno) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_trans_date(trans_date);
        this.set_contact_code(contact_code);
        this.set_cr_amount(cr_amount);
        this.set_paid_amount(paid_amount);
        this.set_balance_amount(balance_amount);
        this.set_z_no(z_no);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.setVoucher_no(voucherno);
    }

    public String getVoucher_no() {
        return voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
        value.put("voucher_no", voucher_no);
    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_trans_date() {
        return trans_date;
    }

    public void  set_trans_date(String trans_date) {
        this.trans_date = trans_date;
        value.put("trans_date", trans_date);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void  set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_cr_amount() {
        return cr_amount;
    }

    public void  set_cr_amount(String cr_amount) {
        this.cr_amount = cr_amount;
        value.put("cr_amount", cr_amount);
    }


    public String get_paid_amount() {
        return paid_amount;
    }

    public void  set_paid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
        value.put("paid_amount", paid_amount);
    }

    public String get_balance_amount() {
        return balance_amount;
    }

    public void  set_balance_amount(String balance_amount) {
        this.balance_amount = balance_amount;
        value.put("balance_amount", balance_amount);
    }

    public String get_z_no() {
        return z_no;
    }

    public void  set_z_no(String z_no) {
        this.z_no = z_no;
        value.put("z_no", z_no);
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


    public long insertAcc_Customer_Credit(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updateAcc_Customer_Credit(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Acc_Customer_Credit(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Acc_Customer_Credit getAcc_Customer_Credit(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Acc_Customer_Credit master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer_Credit(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9),cursor.getString(10)
                        );
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Acc_Customer_Credit> getAllAcc_Customer_Credit(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Acc_Customer_Credit> list = new ArrayList<Acc_Customer_Credit>();
        Acc_Customer_Credit master = null;
        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer_Credit(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9),cursor.getString(10)
                        );

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }

    public static String getTotalCredit(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select SUM(paid_amount) FROM " + tableName + " " + WhereClasue;
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
    
}
