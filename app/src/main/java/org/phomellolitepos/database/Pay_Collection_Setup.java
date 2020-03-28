package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 10/12/2017.
 */

public class Pay_Collection_Setup {
    private static String tableName = "Pay_Collection_Setup";
    private String id;
    private String contact_code;
    private String invoice_no;
    private String invoice_date;
    private String amount;

    private Database db;
    private ContentValues value;


    public Pay_Collection_Setup(Context context, String id, String contact_code,String invoice_no, String invoice_date, String amount) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_contact_code(contact_code);
        this.set_invoice_no(invoice_no);
        this.set_invoice_date(invoice_date);
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

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_invoice_no() {
        return invoice_no;
    }

    public void set_invoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
        value.put("invoice_no", invoice_no);
    }

    public String get_invoice_date() {
        return invoice_date;
    }

    public void set_invoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
        value.put("invoice_date", invoice_date);
    }


    public String get_amount() {
        return amount;
    }

    public void set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }





    public long insertPay_Collection_Setup(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updatePay_Collection_Setup(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Pay_Collection_Setup(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Pay_Collection_Setup getPay_Collection_Setup(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Pay_Collection_Setup master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Pay_Collection_Setup> getAllPay_Collection_Setup(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Pay_Collection_Setup> list = new ArrayList<Pay_Collection_Setup>();
        Pay_Collection_Setup master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }
}
