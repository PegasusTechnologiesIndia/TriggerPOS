package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/18/2018.
 */

public class Acc_Customer {
    private static String tableName = "acc_customer";
    private String id;
    private String contact_code;
    private String amount;
    private Database db;
    private ContentValues value;

    public Acc_Customer(Context context, String id,
                               String contact_code, String amount) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_contact_code(contact_code);
        this.set_amount(amount);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void  set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_amount() {
        return amount;
    }

    public void  set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }


    public long insertAcc_Customer(SQLiteDatabase database) {
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public long updateAcc_Customer(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Acc_Customer(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
        database.delete(tableName, whereClause, whereArgs);
        return 1;
    }

    public static Acc_Customer getAcc_Customer(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Acc_Customer master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Acc_Customer> getAllAcc_Customer(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Acc_Customer> list = new ArrayList<Acc_Customer>();
        Acc_Customer master = null;
        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }

    public static String getTotalCredit(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select SUM(paid_amount) FROM " + tableName + " " + WhereClasue;
        String master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                master = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return master;
    }
}
