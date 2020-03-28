package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/30/2018.
 */

public class Ticket_Setup {
    private static String tableName = "ticket_setup";
    private String id;
    private String menufacture_id;
    private String tck_from;
    private String tck_to;
    private String price;
    private String departure;
    private String arrival;
    private String is_inclusive_tax;
    private String new_price;
    private String bus_number;
    private String is_active;

    private Database db;
    private ContentValues value;

    public Ticket_Setup(Context context, String id, String menufacture_id, String tck_from,
                        String tck_to, String price, String departure, String arrival,String is_inclusive_tax,String new_price,String bus_number,String is_active) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_menufacture_id(menufacture_id);
        this.set_tck_from(tck_from);
        this.set_tck_to(tck_to);
        this.set_price(price);
        this.set_departure(departure);
        this.set_arrival(arrival);
        this.set_is_inclusive_tax(is_inclusive_tax);
        this.set_new_price(new_price);
        this.set_bus_number(bus_number);
        this.set_is_active(is_active);
    }


    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_menufacture_id() {
        return menufacture_id;
    }

    public void set_menufacture_id(String menufacture_id) {
        this.menufacture_id = menufacture_id;
        value.put("menufacture_id", menufacture_id);
    }

    public String get_tck_from() {
        return tck_from;
    }

    public void set_tck_from(String tck_from) {
        this.tck_from = tck_from;
        value.put("tck_from", tck_from);
    }

    public String get_tck_to() {
        return tck_to;
    }

    public void set_tck_to(String tck_to) {
        this.tck_to = tck_to;
        value.put("tck_to", tck_to);
    }

    public String get_price() {
        return price;
    }

    public void set_price(String price) {
        this.price = price;
        value.put("price", price);
    }

    public String get_departure() {
        return departure;
    }

    public void set_departure(String departure) {
        this.departure = departure;
        value.put("departure", departure);
    }

    public String get_arrival() {
        return arrival;
    }

    public void set_arrival(String arrival) {
        this.arrival = arrival;
        value.put("arrival", arrival);
    }

    public String get_is_inclusive_tax() {
        return is_inclusive_tax;
    }

    public void set_is_inclusive_tax(String is_inclusive_tax) {
        this.is_inclusive_tax = is_inclusive_tax;
        value.put("is_inclusive_tax", is_inclusive_tax);
    }

    public String get_new_price() {
        return new_price;
    }

    public void set_new_price(String new_price) {
        this.new_price = new_price;
        value.put("new_price", new_price);
    }

    public String get_bus_number() {
        return bus_number;
    }

    public void set_bus_number(String bus_number) {
        this.bus_number = bus_number;
        value.put("bus_number", bus_number);
    }

    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }

    public long insertTicket_Setup(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteTicket_Setup(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateTicket_Setup(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static Ticket_Setup getTicket_Setup(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Ticket_Setup master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5)
                        , cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Ticket_Setup> getAllTicket_Setup(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Ticket_Setup> list = new ArrayList<Ticket_Setup>();
        Ticket_Setup master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Ticket_Setup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4), cursor.getString(5)
                        ,cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
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
