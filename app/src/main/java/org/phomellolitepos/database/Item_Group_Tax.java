package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/3/2017.
 */

public class Item_Group_Tax {

    private static String tableName = "item_group_tax";
    private String location_id;
    private String tax_id;
    private String item_group_code;

    private Database db;
    private ContentValues value;


    public Item_Group_Tax(Context context, String location_id, String tax_id,
                          String item_group_code) {

        db = new Database(context);
        value = new ContentValues();

        this.set_location_id(location_id);
        this.set_tax_id(tax_id);
        this.set_item_group_code(item_group_code);

    }

    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id = location_id;
        value.put("location_id", location_id);
    }

    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }


    public String get_item_group_code() {
        return item_group_code;
    }

    public void set_item_group_code(String item_group_code) {
        this.item_group_code = item_group_code;
        value.put("item_group_code", item_group_code);
    }


    public long insertItem_Group_Tax(SQLiteDatabase database) {
        // SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "item_group_tax_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Item_Group_Tax(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

      //  sdb.close();
        return 1;
    }


    public long updateItem_Group_Tax(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Item_Group_Tax getItem_Group_Tax(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Item_Group_Tax master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Item_Group_Tax> getAllItem_Group_Tax(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Group_Tax> list = new ArrayList<Item_Group_Tax>();
        Item_Group_Tax master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group_Tax(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


}