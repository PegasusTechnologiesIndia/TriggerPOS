package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Order_Loyalty_Earn {

    private static String tableName = "order_loyalty_earn";
    private String id;
    private String order_code;
    private String customer_code;
    private String earn_point;
    private String earn_value;
    private String redeem_point;

    private Database db;
    private ContentValues value;

    public Order_Loyalty_Earn(Context context, String id, String order_code, String customer_code,
                             String earn_point,String earn_value,String redeem_point) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_order_code(order_code);
        this.set_customer_code(customer_code);
        this.set_earn_point(earn_point);
        this.set_earn_value(earn_value);
        this.set_redeem_point(redeem_point);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }

    public String get_customer_code() {
        return customer_code;
    }

    public void set_customer_code(String customer_code) {
        this.customer_code = customer_code;
        value.put("customer_code", customer_code);
    }

    public String get_earn_point() {
        return earn_point;
    }

    public void set_earn_point(String earn_point) {
        this.earn_point = earn_point;
        value.put("earn_point", earn_point);
    }


    public String get_earn_value() {
        return earn_value;
    }

    public void set_earn_value(String earn_value) {
        this.earn_value = earn_value;
        value.put("earn_value", earn_value);
    }

    public String get_redeem_point() {
        return redeem_point;
    }

    public void set_redeem_point(String redeem_point) {
        this.redeem_point = redeem_point;
        value.put("redeem_point", redeem_point);
    }


    public long insertOrder_Loyalty_Earn(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteOrder_Loyalty_Earn(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateOrder_Loyalty_Earn(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Order_Loyalty_Earn getOrder_Loyalty_Earn(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Order_Loyalty_Earn master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Loyalty_Earn(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Order_Loyalty_Earn> getAllOrder_Loyalty_Earn(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Order_Loyalty_Earn> list = new ArrayList<Order_Loyalty_Earn>();
        Order_Loyalty_Earn master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Loyalty_Earn(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


    public static ArrayList<String> getAllItemforautocomplete(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();

        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(5));
                } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
