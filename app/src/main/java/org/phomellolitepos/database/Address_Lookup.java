package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/17/2017.
 */

public class Address_Lookup {

    private static String tableName = "address_lookup";
    private String address_lookup_id;
    private String device_code;
    private String address_code;
    private String refrence_type;
    private String refrence_code;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Address_Lookup(Context context, String address_lookup_id, String device_code,
                          String address_code, String refrence_type, String refrence_code, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_address_lookup_id(address_lookup_id);
        this.set_device_code(device_code);
        this.set_address_code(address_code);
        this.set_refrence_type(refrence_type);
        this.set_refrence_code(refrence_code);
        this.set_is_push(is_push);
    }


    public String get_address_lookup_id() {
        return address_lookup_id;
    }

    public void set_address_lookup_id(String address_lookup_id) {
        this.address_lookup_id = address_lookup_id;
        value.put("address_lookup_id", address_lookup_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_address_code() {
        return address_code;
    }

    public void set_address_code(String address_code) {
        this.address_code = address_code;
        value.put("address_code", address_code);
    }

    public String get_refrence_type() {
        return refrence_type;
    }

    public void set_refrence_type(String refrence_type) {
        this.refrence_type = refrence_type;
        value.put("refrence_type", refrence_type);
    }

    public String get_refrence_code() {
        return refrence_code;
    }

    public void set_refrence_code(String refrence_code) {
        this.refrence_code = refrence_code;
        value.put("refrence_code", refrence_code);
    }

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }


    public long insertAddress_Lookup(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "address_category_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Address_Lookup(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }


    public long updateAddress_Lookup(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Address_Lookup getAddress_Lookup(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Address_Lookup master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Lookup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Address_Lookup> getAllAddress_Lookup(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Address_Lookup> list = new ArrayList<Address_Lookup>();
        Address_Lookup master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Lookup(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
