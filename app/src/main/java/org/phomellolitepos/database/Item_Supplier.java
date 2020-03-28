package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/24/2017.
 */

public class Item_Supplier {

    private static String tableName = "item_supplier";
    private String item_supplier_id;
    private String item_code;
    private String contact_code;


    private Database db;
    private ContentValues value;


    public Item_Supplier(Context context, String item_supplier_id, String item_code,String contact_code) {

        db = new Database(context);
        value = new ContentValues();

        this.set_item_supplier_id(item_supplier_id);
        this.set_item_code(item_code);
        this.set_contact_code(contact_code);

    }


    public String get_item_supplier_id() {
        return item_supplier_id;
    }

    public void set_item_supplier_id(String item_supplier_id) {
        this.item_supplier_id = item_supplier_id;
        value.put("item_supplier_id", item_supplier_id);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }



    public long insertItem_Supplier(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "item_supplier_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Item_Supplier(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
        Database db = new Database(context);
       /// SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateItem_Supplier(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Item_Supplier getItem_Supplier(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Item_Supplier master = null;
     //   Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Supplier(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Item_Supplier> getAllItem_Supplier(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Supplier> list = new ArrayList<Item_Supplier>();
        Item_Supplier master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Supplier(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
