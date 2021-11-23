package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 9/27/2017.
 */

public class Reservation_Detail {


    private static String tableName = "Reservation_detail";
    private String _id;
    private String ref_id;
    private String item_code;

    private Database db;
    private ContentValues value;

    public Reservation_Detail(Context context, String _id, String ref_id,
                       String item_code) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(_id);
        this.set_ref_id(ref_id);
        this.set_item_code(item_code);

    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
        value.put("_id", _id);
    }

    public String get_ref_id() {
        return ref_id;
    }

    public void set_ref_id(String ref_id) {
        this.ref_id = ref_id;
        value.put("ref_id", ref_id);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }


    public long insertReservation_Detail(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "_id", value);
        return insert;
    }

    public static long deleteReservation_Detail(Context context,String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateReservation_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Table getReservation_Detail(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Table master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Table(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)
                        , cursor.getString(3), cursor.getString(4), cursor.getString(5)
                        , cursor.getString(6), cursor.getString(7)
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    public static ArrayList<Reservation_Detail> getAllReservation_Detail(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Reservation_Detail> list_spinner = new ArrayList<Reservation_Detail>();
        Reservation_Detail master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Reservation_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                list_spinner.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Reservation_Detail> getAllReservation_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Reservation_Detail> list = new ArrayList<Reservation_Detail>();
        Reservation_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Reservation_Detail(context, cursor.getString(0),
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
