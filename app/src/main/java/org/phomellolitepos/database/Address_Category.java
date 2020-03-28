package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/15/2017.
 */

public class Address_Category {

    private static String tableName = "address_category";
    private String address_category_id;
    private String device_code;
    private String address_category_code;
    private String name;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Address_Category(Context context, String address_category_id, String device_code,
                String address_category_code, String name, String is_active,String modified_by,String modified_date,String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_address_category_id(address_category_id);
        this.set_device_code(device_code);
        this.set_address_category_code(address_category_code);
        this.set_name(name);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }


    public String get_address_category_id() {
        return address_category_id;
    }

    public void set_address_category_id(String address_category_id) {
        this.address_category_id = address_category_id;
        value.put("address_category_id", address_category_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_address_category_code() {
        return address_category_code;
    }

    public void set_address_category_code(String address_category_code) {
        this.address_category_code = address_category_code;
        value.put("address_category_code", address_category_code);
    }


    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
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



    public long insertAddress_Category(SQLiteDatabase database) {
//        SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "address_category_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Address_Category(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        ///sdb.close();
        return 1;
    }


    public long updateAddress_Category(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Address_Category getAddress_Category(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Address_Category master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Category(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Address_Category> getAllAddress_Category(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Address_Category> list = new ArrayList<Address_Category>();
        Address_Category master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Category(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

}
