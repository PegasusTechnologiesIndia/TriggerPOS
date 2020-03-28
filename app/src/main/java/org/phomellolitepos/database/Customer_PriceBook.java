package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/3/2018.
 */

public class Customer_PriceBook {

    private static String tableName = "customer_price_book";
    private String id;
    private String contact_code;
    private String item_code;
    private String sale_price;
    private String modified_date;

    private Database db;
    private ContentValues value;


    public Customer_PriceBook(Context context, String id, String contact_code,
                String item_code, String sale_price,String modified_date) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_contact_code(contact_code);
        this.set_item_code(item_code);
        this.set_sale_price(sale_price);
        this.set_modified_date(modified_date);

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

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_sale_price() {
        return sale_price;
    }

    public void set_sale_price(String sale_price) {
        this.sale_price = sale_price;
        value.put("sale_price", sale_price);
    }

    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }



    public long insertCustomer_PriceBook(SQLiteDatabase database) {
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public long updateCustomer_PriceBook(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Customer_PriceBook(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Customer_PriceBook getCustomer_PriceBook(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Customer_PriceBook master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Customer_PriceBook(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Customer_PriceBook> getAllCustomer_PriceBook(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Customer_PriceBook> list = new ArrayList<Customer_PriceBook>();
        Customer_PriceBook master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Customer_PriceBook(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }
}
