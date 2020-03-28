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

public class Pay_Collection_Detail {

    private static String tableName = "Pay_Collection_Detail";
    private String id;
    private String collection_code;
    private String invoice_no;
    private String amount;

    private Database db;
    private ContentValues value;


    public Pay_Collection_Detail(Context context, String id, String collection_code, String invoice_no, String amount) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_collection_code(collection_code);
        this.set_invoice_no(invoice_no);
       this.set_amount(amount);



    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }



    public String get_collection_code() {
        return collection_code;
    }

    public void set_collection_code(String collection_code) {
        this.collection_code = collection_code;
        value.put("collection_code", collection_code);
    }

    public String get_invoice_no() {
        return invoice_no;
    }

    public void set_invoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
        value.put("invoice_no", invoice_no);
    }


    public String get_amount() {
        return amount;
    }

    public void set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }





    public long insertPay_Collection_Detail(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updatePay_Collection_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Pay_Collection_Detail(Context context, String tablename,String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tablename, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Pay_Collection_Detail getPay_Collection_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Pay_Collection_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Pay_Collection_Detail> getAllPay_Collection_Detail(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Pay_Collection_Detail> list = new ArrayList<Pay_Collection_Detail>();
        Pay_Collection_Detail master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }

}
