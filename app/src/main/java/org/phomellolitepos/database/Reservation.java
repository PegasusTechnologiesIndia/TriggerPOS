package org.phomellolitepos.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Reservation {

    private static String tableName = "Reservation";
    private String _id;
    private String date_time;
    private String customer_code;
    private String user_code;
    private String table_code;


    private Database db;
    private ContentValues value;

    public Reservation(Context context, String _id, String date_time,
                String customer_code, String user_code, String table_code) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(_id);
        this.set_date_time(date_time);
        this.set_customer_code(customer_code);
        this.set_user_code(user_code);
        this.set_table_code(table_code);
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
        value.put("_id", _id);
    }

    public String get_date_time() {
        return date_time;
    }

    public void set_date_time(String date_time) {
        this.date_time = date_time;
        value.put("date_time", date_time);
    }

    public String get_customer_code() {
        return customer_code;
    }

    public void set_customer_code(String customer_code) {
        this.customer_code = customer_code;
        value.put("customer_code", customer_code);
    }

    public String get_user_code() {
        return user_code;
    }

    public void set_user_code(String user_code) {
        this.user_code = user_code;
        value.put("user_code", user_code);
    }

    public String get_table_code() {
        return table_code;
    }

    public void set_table_code(String table_code) {
        this.table_code = table_code;
        value.put("table_code", table_code);
    }




    public long insertReservation(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "_id", value);
        return insert;
    }

    public static long deleteReservation(Context context,String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateReservation(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Reservation getReservation(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Reservation master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Reservation(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    public static ArrayList<Reservation> getAllReservation(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Reservation> list_spinner = new ArrayList<Reservation>();
        Reservation master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Reservation(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
                list_spinner.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Reservation> getAllReservation(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Reservation> list = new ArrayList<Reservation>();
        Reservation master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Reservation(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


}
