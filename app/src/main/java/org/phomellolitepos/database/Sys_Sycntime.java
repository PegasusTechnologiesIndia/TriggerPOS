package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 1/2/2018.
 */

public class Sys_Sycntime {

    private static String tableName = "Sys_Sycntime";
    private String id;
    private String table_name;
    private String datetime;

    private Database db;
    private ContentValues value;

    public Sys_Sycntime(Context context, String id, String table_name,
                        String datetime) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_table_name(table_name);
        this.set_datetime(datetime);


    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_table_name() {
        return table_name;
    }

    public void set_table_name(String table_name) {
        this.table_name = table_name;
        value.put("table_name", table_name);
    }

    public String get_datetime() {
        return datetime;
    }

    public void set_datetime(String datetime) {
        this.datetime = datetime;
        value.put("datetime", datetime);
    }


    public long insertSys_Sycntime(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteSys_Sycntime(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateSys_Sycntime(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Sys_Sycntime getSys_Sycntime(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Sys_Sycntime master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Sycntime(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Sys_Sycntime> getAllSys_Sycntime(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Sys_Sycntime> list = new ArrayList<Sys_Sycntime>();
        Sys_Sycntime master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Sycntime(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


}
