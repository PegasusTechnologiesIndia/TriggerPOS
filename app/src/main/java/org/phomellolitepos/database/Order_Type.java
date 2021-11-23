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

public class Order_Type {

    private static String tableName = "order_type";
    private String order_type_id;
    private String name;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Order_Type(Context context, String order_type_id, String name,
                      String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_order_type_id(order_type_id);
        this.set_name(name);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }

    public String get_order_type_id() {
        return order_type_id;
    }

    public void set_order_type_id(String order_type_id) {
        this.order_type_id = order_type_id;
        value.put("order_type_id", order_type_id);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
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

    public void add_ordertype(ArrayList<Order_Type> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Order_Type ordertype : list) {


                values.put("order_type_id", ordertype.get_order_type_id());
                values.put("name", ordertype.get_name());
                values.put("is_active", ordertype.get_is_active());
                values.put("modified_by", ordertype.get_modified_by());
                values.put("modified_date", ordertype.get_modified_date());
                values.put("is_push", ordertype.get_is_push());

                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public long insertOrder_Type(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "order_type_id", value);
        //database.close();
        return insert;
    }

    public long updateOrder_Type(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Order_Type(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }


    public static Order_Type getOrder_Type(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Order_Type master = null;
        //Database db = new Database(context);
       // SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Type(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Order_Type> getAllOrder_Type(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Order_Type> list = new ArrayList<Order_Type>();
        Order_Type master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Type(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

}
