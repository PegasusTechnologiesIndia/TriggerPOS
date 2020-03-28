package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

/**
 * Created by Neeraj on 3/14/2017.
 */

public class Country {

    private static String tableName = "country";
    private String country_id;
    private String name;
    private String isd_code;
    private String iso_code_2;
    private String iso_code_3;
    private String currency_symbol;
    private String currency_place;
    private String decimal_place;
    private String postcode_required;
    private String status;

    private Database db;
    private ContentValues value;


    public Country(Context context, String country_id, String name,
                   String isd_code, String iso_code_2, String iso_code_3, String currency_symbol,
                   String currency_place, String decimal_place, String postcode_required, String status) {

        db = new Database(context);
        value = new ContentValues();

        this.set_country_id(country_id);
        this.set_name(name);
        this.set_isd_code(isd_code);
        this.set_iso_code_2(iso_code_2);
        this.set_iso_code_3(iso_code_3);
        this.set_currency_symbol(currency_symbol);
        this.set_currency_place(currency_place);
        this.set_decimal_place(decimal_place);
        this.set_postcode_required(postcode_required);
        this.set_status(status);

    }

    public String get_country_id() {
        return country_id;
    }

    public void set_country_id(String country_id) {
        this.country_id = country_id;
        value.put("country_id", country_id);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }

    public String get_isd_code() {
        return isd_code;
    }

    public void set_isd_code(String isd_code) {
        this.isd_code = isd_code;
        value.put("isd_code", isd_code);
    }


    public String get_iso_code_2() {
        return iso_code_2;
    }

    public void set_iso_code_2(String iso_code_2) {
        this.iso_code_2 = iso_code_2;
        value.put("iso_code_2", iso_code_2);
    }

    public String get_iso_code_3() {
        return iso_code_3;
    }

    public void set_iso_code_3(String iso_code_3) {
        this.iso_code_3 = iso_code_3;
        value.put("iso_code_3", iso_code_3);
    }

    public String get_currency_symbol() {
        return currency_symbol;
    }

    public void set_currency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
        value.put("currency_symbol", currency_symbol);
    }


    public String get_currency_place() {
        return currency_place;
    }

    public void set_currency_place(String currency_place) {
        this.currency_place = currency_place;
        value.put("currency_place", currency_place);
    }


    public String get_decimal_place() {
        return decimal_place;
    }

    public void set_decimal_place(String decimal_place) {
        this.decimal_place = decimal_place;
        value.put("decimal_place", decimal_place);
    }

    public String get_postcode_required() {
        return postcode_required;
    }

    public void set_postcode_required(String postcode_required) {
        this.postcode_required = postcode_required;
        value.put("postcode_required", postcode_required);
    }


    public String get_status() {
        return status;
    }

    public void set_status(String status) {
        this.status = status;
        value.put("status", status);
    }

    public long insertCountry(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "country_id", value);
        //database.close();
        return insert;
    }


    public static long delete_country(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateCountry(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Country getCountry(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Country master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Country(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Country> getAllCountry(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Country> list = new ArrayList<Country>();
        Country master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Country(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
}
