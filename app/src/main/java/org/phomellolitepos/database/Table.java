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
    private String Zone_id;
    private String Zone_name;
    private String is_active;
    private String modified_by;
    private String modified_date;

    private Database db;
    private ContentValues value;

    public Table(Context context, String table_id, String table_code,
                 String table_name,String Zoneid,String Zone_name,String is_active,String modifiedby,String modifieddate) {

        db = new Database(context);
        value = new ContentValues();

        this.set_table_id(table_id);
        this.set_table_code(table_code);
        this.set_table_name(table_name);
        this.setZone_id(Zoneid);
        this.setZone_name(Zone_name);
        this.setIs_active(is_active);
        this.setModified_by(modifiedby);
        this.setModified_date(modifieddate);

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

    public String getZone_id() {
        return Zone_id;
    }

    public void setZone_id(String zone_id) {
        this.Zone_id = zone_id;
        value.put("Zone_id", zone_id);
    }

    public String getZone_name() {
        return Zone_name;
    }

    public void setZone_name(String zone_name) {
        this.Zone_name = zone_name;
        value.put("Zone_name", zone_name);
    }

    public String get_table_name() {
        return table_name;
    }

    public void set_table_name(String table_name) {
        this.table_name = table_name;
        value.put("table_name", table_name);
    }


    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
        value.put("modified_by", modified_by);
    }

    public String getModified_date() {
        return modified_date;

    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
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
                        cursor.getString(1), cursor.getString(2)
                        , cursor.getString(3), cursor.getString(4)
                        , cursor.getString(5), cursor.getString(6), cursor.getString(7));
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
                        cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4)
                        , cursor.getString(5), cursor.getString(6), cursor.getString(7));
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
