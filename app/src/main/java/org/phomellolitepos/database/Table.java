package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 6/15/2017.
 */

public class Table {

    private static String tableName = "tables";
    private String table_id;
    private String table_code;
    private String table_name;

    private Database db;
    private ContentValues value;

    public Table(Context context, String table_id, String table_code,
                 String table_name) {

        db = new Database(context);
        value = new ContentValues();

        this.set_table_id(table_id);
        this.set_table_code(table_code);
        this.set_table_name(table_name);
    }

    public String get_table_id() {
        return table_id;
    }

    public void set_table_id(String table_id) {
        this.table_id = table_id;
        value.put("table_id", table_id);
    }

    public String get_table_code() {
        return table_code;
    }

    public void set_table_code(String table_code) {
        this.table_code = table_code;
        value.put("table_code", table_code);
    }


    public String get_table_name() {
        return table_name;
    }

    public void set_table_name(String table_name) {
        this.table_name = table_name;
        value.put("table_name", table_name);
    }


    public long insertTable(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "table_id", value);
        return insert;
    }

    public static long deleteTable(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateTable(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Table getTable(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Table master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Table(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    public static ArrayList<Manufacture> getAllManufactureSpinner_All(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Manufacture> list_spinner = new ArrayList<Manufacture>();
        Manufacture master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Manufacture(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8));
                list_spinner.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Table> getAllTable(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Table> list = new ArrayList<Table>();
        Table master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Table(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public void add_table(ArrayList<Table> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Table table : list) {


                values.put("table_code", table.get_table_code());
                values.put("table_name", table.get_table_name());

                db.insert(tableName, null, values);

            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }
    public static ArrayList<String> getAllItemforautocomplete(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();

        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                list.add(cursor.getString(5));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

}
