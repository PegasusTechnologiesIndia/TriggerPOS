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

public class Ticket_Setup_Category {

    private static String tableName = "ticket_setup_category";
    private String id;
    private String ref_id;
    private String category_id;

    private Database db;
    private ContentValues value;

    public Ticket_Setup_Category(Context context, String id, String ref_id,
                              String category_id) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_ref_id(ref_id);
        this.set_category_id(category_id);

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

    public String get_category_id() {
        return category_id;
    }

    public void set_category_id(String category_id) {
        this.category_id = category_id;
        value.put("category_id", category_id);
    }


    public long insertTicket_Setup_Category(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteTicket_Setup_Category(Context context,String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateTicket_Setup_Category(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Ticket_Setup_Category getTicket_Setup_Category(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Ticket_Setup_Category master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup_Category(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Ticket_Setup_Category> getAllTicket_Setup_Category(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Ticket_Setup_Category> list = new ArrayList<Ticket_Setup_Category>();
        Ticket_Setup_Category master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup_Category(context, cursor.getString(0),
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
