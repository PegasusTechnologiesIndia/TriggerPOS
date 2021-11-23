package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/15/2017.
 */

public class Payment {

    private static String tableName = "payments";
    private String payment_id;
    private String parent_id;
    private String payment_name;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Payment(Context context, String payment_id, String parent_id,
                   String payment_name, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_payment_id(payment_id);
        this.set_parent_id(parent_id);
        this.set_payment_name(payment_name);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }

    public String get_payment_id() {
        return payment_id;
    }

    public void set_payment_id(String payment_id) {
        this.payment_id = payment_id;
        value.put("payment_id", payment_id);
    }

    public String get_parent_id() {
        return parent_id;
    }

    public void set_parent_id(String parent_id) {
        this.parent_id = parent_id;
        value.put("parent_id", parent_id);
    }

    public String get_payment_name() {
        return payment_name;
    }

    public void set_payment_name(String payment_name) {
        this.payment_name = payment_name;
        value.put("payment_name", payment_name);
    }


    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }


    public String get_modified_by() {
        return modified_by;
    }

    public void set_modified_by(String modified_by) {
        this.modified_by = modified_by;
        value.put("modified_by", modified_by);
    }

    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }
    public void add_payment(ArrayList<Payment> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Payment payment : list) {


                values.put("payment_id", payment.get_payment_id());
                values.put("parent_id", payment.get_parent_id());
                values.put("payment_name", payment.get_payment_name());
                values.put("is_active", payment.get_is_active());
                values.put("modified_by", payment.get_modified_by());
                values.put("modified_date", payment.get_modified_date());
                values.put("is_push", payment.get_is_push());

                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public long insertPayment(SQLiteDatabase database) {
        long insert = 0;
        try {
            //SQLiteDatabase database = this.db.getWritableDatabase();
            insert = database.insert(tableName, "payment_id", value);
            //database.close();
        } catch (Exception ex) {
            String ab = ex.getMessage();
            ab = ab;
        }
        return insert;
    }

    public long updatePayment(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Payment(Context context, String tableName, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return insert;
    }

    public static Payment getPayment(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Payment master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Payment(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    public static Payment getPayment_maxid(Context context, String WhereClasue) {
        String Query = "Select (max(payment_id)+1) FROM " + tableName + " " + WhereClasue;
        Payment master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Payment(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Payment> getAllPayment(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Payment> list = new ArrayList<Payment>();
        Payment master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Payment(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }


}
