package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 5/3/2017.
 */

public class Offer_Product {

    private static String tableName = "offer_product";
    private String offer_id;
    private String item_code;
    private String buy_quantity;
    private String get_quantity;
    private String is_push;

    private Database db;
    private ContentValues value;

    public Offer_Product(Context context, String offer_id, String item_code,
                         String buy_quantity, String get_quantity, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_offer_id(offer_id);
        this.set_item_code(item_code);
        this.set_buy_quantity(buy_quantity);
        this.set_get_quantity(get_quantity);
        this.set_is_push(is_push);
    }


    public String get_offer_id() {
        return offer_id;
    }

    public void set_offer_id(String offer_id) {
        this.offer_id = offer_id;
        value.put("offer_id", offer_id);
    }


    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }


    public String get_buy_quantity() {
        return buy_quantity;
    }

    public void set_buy_quantity(String buy_quantity) {
        this.buy_quantity = buy_quantity;
        value.put("buy_quantity", buy_quantity);
    }

    public String get_get_quantity() {
        return get_quantity;
    }

    public void set_get_quantity(String get_quantity) {
        this.get_quantity = get_quantity;
        value.put("get_quantity", get_quantity);
    }


    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }


    public long insertOffer_Product(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "offer_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Offer_Product(Context context, String order_detail, String whereClause, String[] whereArgs, SQLiteDatabase database) {
        Database db = new Database(context);
        // SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);
        database.close();
        return 1;
    }


    public long updateOffer_Product(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Offer_Product getOffer_Product(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Offer_Product master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Offer_Product(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Offer_Product> getAllOffer_Product(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Offer_Product> list = new ArrayList<Offer_Product>();
        Offer_Product master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Offer_Product(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


}
