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

public class Address_Type {

    private static String tableName = "address_type";
    private String address_type;
    private String name;

    private Database db;
    private ContentValues value;


    public Address_Type(Context context, String address_type, String name) {

        db = new Database(context);
        value = new ContentValues();

        this.set_address_type(address_type);
        this.set_name(name);

    }


    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }



    public String get_address_type() {
        return address_type;
    }

    public void set_address_type(String address_type) {
        this.address_type = address_type;
        value.put("address_type", address_type);
    }
    public void add_Addressstype(ArrayList<Address_Type> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Address_Type address_type : list) {


                values.put("address_type", address_type.get_address_type());
                values.put("name", address_type.get_name());

                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }


    public long insertAddress_Type(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "address_type", value);
       // database.close();
        return insert;
    }

    public static long delete_Address_Type(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public long updateAddress_Type(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Address_Type getAddress_Type(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Address_Type master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Type(context, cursor.getString(0),
                        cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Address_Type> getAllAddress_Type(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Address_Type> list = new ArrayList<Address_Type>();
        Address_Type master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Address_Type(context, cursor.getString(0),
                        cursor.getString(1));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}