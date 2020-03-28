package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 10/10/2017.
 */

public class Bank {

    private static String tableName = "Bank";
    private String bank_id;
    private String device_code;
    private String bank_code;
    private String bank_name;
    private String email;
    private String mobile;
    private String address;
    private String bank_ref_code;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Bank(Context context, String bank_id, String device_code,
                          String bank_code, String bank_name, String email, String mobile, String address,
                          String bank_ref_code, String is_active, String modified_by, String modified_date,String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_bank_id(bank_id);
        this.set_device_code(device_code);
        this.set_bank_code(bank_code);
        this.set_bank_name(bank_name);
        this.set_email(email);
        this.set_mobile(mobile);
        this.set_address(address);
        this.set_bank_ref_code(bank_ref_code);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);



    }


    public String get_bank_id() {
        return bank_id;
    }

    public void set_bank_id(String bank_id) {
        this.bank_id = bank_id;
        value.put("bank_id", bank_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_bank_code() {
        return bank_code;
    }

    public void set_bank_code(String bank_code) {
        this.bank_code = bank_code;
        value.put("bank_code", bank_code);
    }

    public String get_bank_name() {
        return bank_name;
    }

    public void set_bank_name(String bank_name) {
        this.bank_name = bank_name;
        value.put("bank_name", bank_name);
    }

    public String get_email() {
        return email;
    }

    public void set_email(String email) {
        this.email = email;
        value.put("email", email);
    }

    public String get_mobile() {
        return mobile;
    }

    public void set_mobile(String mobile) {
        this.mobile = mobile;
        value.put("mobile", mobile);
    }

    public String get_address() {
        return address;
    }

    public void set_address(String address) {
        this.address = address;
        value.put("address", address);
    }

    public String get_bank_ref_code() {
        return bank_ref_code;
    }

    public void set_bank_ref_code(String bank_ref_code) {
        this.bank_ref_code = bank_ref_code;
        value.put("bank_ref_code", bank_ref_code);
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

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }



    public long insertBank(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "bank_id", value);
        //database.close();
        return insert;
    }

    public long updateBank(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Bank(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Bank getBank(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Bank master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Bank(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10)
                        ,cursor.getString(11));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Bank> getAllBank(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Bank> list = new ArrayList<Bank>();
        Bank master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Bank(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10)
                        ,cursor.getString(11));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }
}
