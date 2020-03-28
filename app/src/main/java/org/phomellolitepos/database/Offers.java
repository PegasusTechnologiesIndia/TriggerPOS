package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 5/3/2017.
 */

public class Offers {

    private static String tableName = "offers";
    private String offer_id;
    private String location_id;
    private String name;
    private String description;
    private String offer_type;
    private String barcode;
    private String start_date;
    private String end_date;
    private String price;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Offers(Context context, String offer_id, String location_id,
                          String name, String description, String offer_type, String barcode,String start_date, String end_date, String price, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_offer_id(offer_id);
        this.set_location_id(location_id);
        this.set_name(name);
        this.set_description(description);
        this.set_offer_type(offer_type);
        this.set_barcode(barcode);

        this.set_start_date(start_date);
        this.set_end_date(end_date);
        this.set_price(price);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
    }


    public String get_offer_id() {
        return offer_id;
    }

    public void set_offer_id(String offer_id) {
        this.offer_id= offer_id;
        value.put("offer_id", offer_id);
    }


    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id= location_id;
        value.put("location_id", location_id);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name= name;
        value.put("name", name);
    }


    public String get_description() {
        return description;
    }

    public void set_description(String description) {
        this.description= description;
        value.put("description", description);
    }


    public String get_offer_type() {
        return offer_type;
    }

    public void set_offer_type(String offer_type) {
        this.offer_type= offer_type;
        value.put("offer_type", offer_type);
    }


    public String get_barcode() {
        return barcode;
    }

    public void set_barcode(String barcode) {
        this.barcode= barcode;
        value.put("barcode", barcode);
    }


    public String get_start_date() {
        return start_date;
    }

    public void set_start_date(String start_date) {
        this.start_date= start_date;
        value.put("start_date", start_date);
    }



    public String get_end_date() {
        return end_date;
    }

    public void set_end_date(String end_date) {
        this.end_date= end_date;
        value.put("end_date", end_date);
    }

    public String get_price() {
        return price;
    }

    public void set_price(String price) {
        this.price= price;
        value.put("price", price);
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


    public long insertOffers(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "offer_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Offers(Context context, String offers, String whereClause, String[] whereArgs, SQLiteDatabase database) {
        Database db = new Database(context);
        //SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }


    public long updateOffers(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Offers getOffers(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Offers master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Offers(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Offers> getAllOffers(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Offers> list = new ArrayList<Offers>();
        Offers master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Offers(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
