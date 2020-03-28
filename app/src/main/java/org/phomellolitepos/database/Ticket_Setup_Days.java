package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/4/2018.
 */

public class Ticket_Setup_Days {

    private static String tableName = "ticket_setup_days";
    private String id;
    private String ref_id;
    private String days;

    private Database db;
    private ContentValues value;

    public Ticket_Setup_Days(Context context, String _id, String ref_id,
                            String days) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(_id);
        this.set_ref_id(ref_id);
        this.set_days(days);

    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_ref_id() {
        return ref_id;
    }

    public void set_ref_id(String ref_id) {
        this.ref_id = ref_id;
        value.put("ref_id", ref_id);
    }

    public String get_days() {
        return days;
    }

    public void set_days(String days) {
        this.days = days;
        value.put("days", days);
    }


    public long insertTicket_Setup_Days(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteTicket_Setup_Days(Context context,String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateTicket_Setup_Days(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Ticket_Setup_Days getTicket_Setup_Days(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Ticket_Setup_Days master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup_Days(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    public static ArrayList<Ticket_Setup_Days> getAllTicket_Setup_Days(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Ticket_Setup_Days> list_spinner = new ArrayList<Ticket_Setup_Days>();
        Ticket_Setup_Days master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup_Days(context, cursor.getString(0),
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
    public static ArrayList<Ticket_Setup_Days> getAllTicket_Setup_Days(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Ticket_Setup_Days> list = new ArrayList<Ticket_Setup_Days>();
        Ticket_Setup_Days master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup_Days(context, cursor.getString(0),
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
