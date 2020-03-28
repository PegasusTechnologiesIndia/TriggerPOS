package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 10/24/2017.
 */

public class Sys_Tax_Type {

    private static String tableName = "Sys_Tax_Type";
    private String id;
    private String type;
    private String is_active;

    private Database db;
    private ContentValues value;

    public Sys_Tax_Type(Context context, String id, String type,
                 String is_active) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_type(type);
        this.set_is_active(is_active);


    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_type() {
        return type;
    }

    public void set_type(String type) {
        this.type = type;
        value.put("type", type);
    }

    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }


    public long insertSys_Tax_Type(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteSys_Tax_Type(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateSys_Tax_Type(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Sys_Tax_Type getSys_Tax_Type(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Sys_Tax_Type master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Tax_Type(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }




    // Here Changed in function  need to update all classes
    public static ArrayList<Sys_Tax_Type> getAllSys_Tax_Type(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Sys_Tax_Type> list = new ArrayList<Sys_Tax_Type>();
        Sys_Tax_Type master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Tax_Type(context, cursor.getString(0),
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
