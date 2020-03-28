package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 3/17/2017.
 */

public class Contact_Bussiness_Group {

    private static String tableName = "contact_business_group";
    private String contact_code;
    private String business_group_code;


    private Database db;
    private ContentValues value;


    public Contact_Bussiness_Group(Context context, String contact_code, String business_group_code) {

        db = new Database(context);
        value = new ContentValues();

        this.set_contact_code(contact_code);
        this.set_business_group_code(business_group_code);

    }

    public String get_contact_code() {
        return contact_code;
    }

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }


    public String get_business_group_code() {
        return business_group_code;
    }

    public void set_business_group_code(String business_group_code) {
        this.business_group_code = business_group_code;
        value.put("business_group_code", business_group_code);
    }


    public long insertContact_Bussiness_Group(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "", value);
        //database.close();
        return insert;
    }

        public static long delete_Contact_Bussiness_Group(Context context, SQLiteDatabase database, Database db, String whereClause, String[] whereArgs) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
            database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateContact_Bussiness_Group(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Contact_Bussiness_Group getContact_Bussiness_Group(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Contact_Bussiness_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Contact_Bussiness_Group(context, cursor.getString(0),
                        cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Contact_Bussiness_Group> getAllContact_Bussiness_Group(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Contact_Bussiness_Group> list = new ArrayList<Contact_Bussiness_Group>();
        Contact_Bussiness_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Contact_Bussiness_Group(context, cursor.getString(0),
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
