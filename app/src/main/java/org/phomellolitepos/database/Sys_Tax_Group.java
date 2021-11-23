package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/10/2018.
 */

public class Sys_Tax_Group {

    private static String tableName = "Sys_Tax_Group";
    private String id;
    private String tax_master_id;
    private String tax_id;

    private Database db;
    private ContentValues value;


    public Sys_Tax_Group(Context context, String id, String tax_master_id,
                          String tax_id) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_tax_master_id(tax_master_id);
        this.set_tax_id(tax_id);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }

    public String get_tax_master_id() {
        return tax_master_id;
    }

    public void set_tax_master_id(String tax_master_id) {
        this.tax_master_id = tax_master_id;
        value.put("tax_master_id", tax_master_id);
    }




    public long insertSys_Tax_Group(SQLiteDatabase database) {
        // SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public static long delete_Sys_Tax_Group(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        // sdb.close();
        return 1;
    }


    public long updateSys_Tax_Group(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Sys_Tax_Group getSys_Tax_Group(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Sys_Tax_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Tax_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
     //database.close();
       // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Sys_Tax_Group> getAllSys_Tax_Group(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Sys_Tax_Group> list = new ArrayList<Sys_Tax_Group>();
        Sys_Tax_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Sys_Tax_Group(context, cursor.getString(0),
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
