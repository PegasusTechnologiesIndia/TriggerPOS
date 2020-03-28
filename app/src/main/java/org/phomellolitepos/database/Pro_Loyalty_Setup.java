package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Pro_Loyalty_Setup {
    private static String tableName = "pro_loyalty_setup";
    private String id;
    private String min_purchase_value;
    private String base_value;
    private String earn_point;
    private String earn_value;
    private String mis_redeem_value;
    private String loyalty_type;
    private String name;
    private String valid_from;
    private String valid_to;

    private Database db;
    private ContentValues value;

    public Pro_Loyalty_Setup(Context context, String id, String min_purchase_value, String base_value,
                        String earn_point,String earn_value, String mis_redeem_value,
                             String loyalty_type, String name, String valid_from, String valid_to) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_min_purchase_value(min_purchase_value);
        this.set_base_value(base_value);
        this.set_earn_point(earn_point);
        this.set_earn_value(earn_value);
        this.set_mis_redeem_value(mis_redeem_value);
        this.set_loyalty_type(loyalty_type);
        this.set_name(name);
        this.set_valid_from(valid_from);
        this.set_valid_to(valid_to);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_min_purchase_value() {
        return min_purchase_value;
    }

    public void set_min_purchase_value(String min_purchase_value) {
        this.min_purchase_value = min_purchase_value;
        value.put("min_purchase_value", min_purchase_value);
    }

    public String get_base_value() {
        return base_value;
    }

    public void set_base_value(String base_value) {
        this.base_value = base_value;
        value.put("base_value", base_value);
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

    public String get_mis_redeem_value() {
        return mis_redeem_value;
    }

    public void set_mis_redeem_value(String mis_redeem_value) {
        this.mis_redeem_value = mis_redeem_value;
        value.put("mis_redeem_value", mis_redeem_value);
    }

    public String get_loyalty_type() {
        return loyalty_type;
    }

    public void set_loyalty_type(String loyalty_type) {
        this.loyalty_type = loyalty_type;
        value.put("loyalty_type", loyalty_type);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }


    public String get_valid_from() {
        return valid_from;
    }

    public void set_valid_from(String valid_from) {
        this.valid_from = valid_from;
        value.put("valid_from", valid_from);
    }

    public String get_valid_to() {
        return valid_to;
    }

    public void set_valid_to(String valid_to) {
        this.valid_to = valid_to;
        value.put("valid_to", valid_to);
    }

    public long insertPro_Loyalty_Setup(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deletePro_Loyalty_Setup(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updatePro_Loyalty_Setup(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Pro_Loyalty_Setup getPro_Loyalty_Setup(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Pro_Loyalty_Setup master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pro_Loyalty_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5)
                        , cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Pro_Loyalty_Setup> getAllPro_Loyalty_Setup(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Pro_Loyalty_Setup> list = new ArrayList<Pro_Loyalty_Setup>();
        Pro_Loyalty_Setup master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pro_Loyalty_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4), cursor.getString(5)
                        ,cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
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
