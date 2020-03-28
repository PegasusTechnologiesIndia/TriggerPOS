package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 3/22/2018.
 */

public class Acc_Customer_Debit {

    private static String tableName = "acc_customer_dedit";
    private String id;
    private String order_code;
    private String amount;
    private String z_no;
    private String ref_type;


    private Database db;
    private ContentValues value;

    public Acc_Customer_Debit(Context context, String id, String order_code,
                               String amount, String z_no, String ref_type) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_order_code(order_code);
        this.set_amount(amount);
        this.set_z_no(z_no);
        this.set_ref_type(ref_type);

    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }


    public String get_z_no() {
        return z_no;
    }

    public void  set_z_no(String z_no) {
        this.z_no = z_no;
        value.put("z_no", z_no);
    }

    public String get_order_code() {
        return order_code;
    }

    public void  set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }

    public String get_amount() {
        return amount;
    }

    public void  set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }

    public String get_ref_type() {
        return ref_type;
    }

    public void  set_ref_type(String ref_type) {
        this.ref_type = ref_type;
        value.put("ref_type", ref_type);
    }

    public long insertAcc_Customer_Debit(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updateAcc_Customer_Debit(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Acc_Customer_Debit(Context applicationContext, String acc_customer_dedit, String s, String[] strings, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, s, strings);

//        sdb.close();
        return 1;
    }

    public static Acc_Customer_Debit getAcc_Customer_Debit(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Acc_Customer_Debit master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer_Debit(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4)
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Acc_Customer_Debit> getAllAcc_Customer_Debit(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Acc_Customer_Debit> list = new ArrayList<Acc_Customer_Debit>();
        Acc_Customer_Debit master = null;
        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Acc_Customer_Debit(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4)
                );

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
