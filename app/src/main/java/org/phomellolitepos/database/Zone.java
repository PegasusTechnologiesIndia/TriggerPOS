package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/14/2017.
 */

public class Zone {

    private static String tableName = "zone";
    private String zone_id;
    private String country_id;
    private String name;
    private String code;
    private String status;

    private Database db;
    private ContentValues value;


    public Zone(Context context, String zone_id, String country_id,
                String name, String code, String status) {

        db = new Database(context);
        value = new ContentValues();

        this.set_zone_id(zone_id);
        this.set_country_id(country_id);
        this.set_name(name);
        this.set_code(code);
        this.set_status(status);

    }

    public String get_zone_id() {
        return zone_id;
    }

    public void set_zone_id(String zone_id) {
        this.zone_id = zone_id;
        value.put("zone_id", zone_id);
    }

    public String get_country_id() {
        return country_id;
    }

    public void set_country_id(String country_id) {
        this.country_id = country_id;
        value.put("country_id", country_id);
    }


    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }

    public String get_code() {
        return code;
    }

    public void set_code(String code) {
        this.code = code;
        value.put("code", code);
    }

    public String get_status() {
        return status;
    }

    public void set_status(String status) {
        this.status = status;
        value.put("status", status);
    }


    public long insertZone(SQLiteDatabase database) {
       // SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "zone_id", value);
       // database.close();
        return insert;
    }


    public static long delete_Zone(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

       // sdb.close();
        return 1;
    }

    public long updateZone(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public void add_zone(ArrayList<Zone> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Zone zone : list) {


                values.put("zone_id", zone.get_zone_id());
                values.put("country_id", zone.get_country_id());
                values.put("name", zone.get_name());
                values.put("code", zone.get_code());
                values.put("status", zone.get_status());
                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public static Zone getZone(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Zone master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Zone(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Zone> getAllZone(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Zone> list = new ArrayList<Zone>();
        Zone master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Zone(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
       // db.close();
        return list;
    }

}