package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Neeraj on 6/21/2017.
 */

public class Last_Code {

    private static String tableName = "last_code";
    private String id;
    private String last_order_code;
    private String last_pos_balance_code;
    private String last_z_close_code;
    private String last_order_return_code;

    private Database db;
    private ContentValues value;

    public Last_Code(Context context, String id, String last_order_code,
                     String last_pos_balance_code, String last_z_close_code,String last_order_return_code) {

        db = new Database(context);
        value = new ContentValues();

        this.setid(id);
        this.setlast_order_code(last_order_code);
        this.setlast_pos_balance_code(last_pos_balance_code);
        this.setlast_z_close_code(last_z_close_code);
        this.setLast_order_return_code(last_order_return_code);
    }

    public String getLast_order_return_code() {
        return last_order_return_code;
    }

    public void setLast_order_return_code(String last_order_return_code) {
        this.last_order_return_code = last_order_return_code;
        value.put("last_order_return_code", last_order_return_code);
    }

    public String getid() {
        return id;
    }

    public void setid(String Id) {
        id = Id;
        value.put("id", id);
    }

    public String getlast_order_code() {
        return last_order_code;
    }

    public void setlast_order_code(String Last_order_code) {
        last_order_code = Last_order_code;
        value.put("last_order_code", last_order_code);
    }

    public String getlast_pos_balance_code() {
        return last_pos_balance_code;
    }

    public void setlast_pos_balance_code(String Last_pos_balance_code) {
        last_pos_balance_code = Last_pos_balance_code;
        value.put("last_pos_balance_code", last_pos_balance_code);
    }

    public String getlast_z_close_code() {
        return last_z_close_code;
    }

    public void setlast_z_close_code(String Last_z_close_code) {
        last_z_close_code = Last_z_close_code;
        value.put("last_z_close_code", last_z_close_code);
    }


    public long insertLast_Code(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }


    public static long delete_Last_Code(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public long updateLast_Code(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Last_Code getLast_Code(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Last_Code master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Last_Code(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3),cursor.getString(4));
                } while (cursor.moveToNext());
            }
            cursor.close();
//            database.close();
//            db.close();

        } catch (Exception ex) {
        }

        return master;
    }

}
