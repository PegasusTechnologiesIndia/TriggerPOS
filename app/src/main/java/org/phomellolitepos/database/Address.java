package org.phomellolitepos.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class Address {
private static String tableName = "address";
    private String address_id;
    private String device_code;
    private String address_code;
    private String address_category_code;
    private String area_id;
    private String address;
    private String landmark;
    private String latitude;
    private String longitude;
    private String contact_person;
    private String contact;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;

    public Address(Context context, String address_id, String device_code,
                   String address_code, String address_category_code, String area_id, String address, String landmark, String latitude, String longitude, String contact_person, String contact, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_address_id(address_id);
        this.set_device_code(device_code);
        this.set_address_code(address_code);
        this.set_address_category_code(address_category_code);
        this.set_area_id(area_id);
        this.set_address(address);
        this.set_landmark(landmark);
        this.set_latitude(latitude);
        this.set_longitude(longitude);
        this.set_contact_person(contact_person);
        this.set_contact(contact);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
    }


    public String get_address_id() {
        return address_id;
    }

    public void set_address_id(String address_id) {
        this.address_id = address_id;
        value.put("address_id", address_id);
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

    public String get_address_category_code() {
        return address_category_code;
    }

    public void set_address_category_code(String address_category_code) {
        this.address_category_code = address_category_code;
        value.put("address_category_code", address_category_code);
    }

    public String get_area_id() {
        return area_id;
    }

    public void set_area_id(String area_id) {
        this.area_id = area_id;
        value.put("area_id", area_id);
    }

    public String get_address() {
        return address;
    }

    public void set_address(String address) {
        this.address = address;
        value.put("address", address);
    }

    public String get_landmark() {
        return landmark;
    }

    public void set_landmark(String landmark) {
        this.landmark = landmark;
        value.put("landmark", landmark);
    }

    public String get_latitude() {
        return latitude;
    }

    public void set_latitude(String latitude) {
        this.latitude = latitude;
        value.put("latitude", latitude);
    }

    public String get_longitude() {
        return longitude;
    }

    public void set_longitude(String longitude) {
        this.longitude = longitude;
        value.put("longitude", longitude);
    }

    public String get_contact_person() {
        return contact_person;
    }

    public void set_contact_person(String contact_person) {
        this.contact_person = contact_person;
        value.put("contact_person", contact_person);
    }

    public String get_contact() {
        return contact;
    }

    public void set_contact(String contact) {
        this.contact = contact;
        value.put("contact", contact);
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




    public long insertAddress(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "address_category_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Address(Context context, String whereClause, String[] whereArgs) {
        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        sdb.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }


    public long updateAddress(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Address getAddress(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Address master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Address> getAllAddress(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Address> list = new ArrayList<Address>();
        Address master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14));
                 list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
