package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Void {

    private static String tableName = "Void";
private String vId;
private String order_no;
private String device_code;
private String item_code;
private String is_modifier;
private String Qty;
private String vDateTime;
private String print_flag;
private String is_post;
private String user_id;
    private Database db;
    private ContentValues value;

    public Void(Context context, String voidid, String ordrno, String devicecode,
                String itemcode, String ismodifier, String quantity, String datetime, String printflag, String ispost, String userid
               ) {

        db = new Database(context);
        value = new ContentValues();

        this.setvId(voidid);
        this.setOrder_no(ordrno);
        this.setDevice_code(devicecode);
        this.setItem_code(itemcode);
        this.setIs_modifier(ismodifier);
        this.setQty(quantity);
        this.setvDateTime(datetime);
        this.setPrint_flag(printflag);
        this.setIs_post(ispost);
        this.setUser_id(userid);


    }
    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
        value.put("vId", vId);
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
        value.put("order_no", order_no);
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String getIs_modifier() {
        return is_modifier;
    }

    public void setIs_modifier(String is_modifier) {
        this.is_modifier = is_modifier;
        value.put("is_modifier", is_modifier);
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
        value.put("Qty", qty);
    }

    public String getvDateTime() {
        return vDateTime;
    }

    public void setvDateTime(String vDateTime) {
        this.vDateTime = vDateTime;
        value.put("vDateTime", vDateTime);
    }

    public String getPrint_flag() {
        return print_flag;
    }

    public void setPrint_flag(String print_flag) {
        this.print_flag = print_flag;
        value.put("print_flag", print_flag);
    }

    public String getIs_post() {
        return is_post;
    }

    public void setIs_post(String is_post) {
        this.is_post = is_post;
        value.put("is_post", is_post);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
        value.put("user_id", user_id);
    }

    public long insertVoid(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "vId", value);
        //database.close();
        return insert;
    }

    public static long delete_Void(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateVoid(String whereClause, SQLiteDatabase database, String[] whereArgs)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Void getVoid(Context context, String WhereClasue, SQLiteDatabase database) {
        Void master = null;
        try {
            String Query = "Select * FROM " + tableName + " " + WhereClasue;

            Database db = new Database(context);
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Void(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8), cursor.getString(9)
                            );
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
            db.close();
        }
        catch (Exception ex)
        {
            String  strError  = ex.getMessage();
            Log.d("User Error: ",strError);
        }
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Void> getAllVoid(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Void> list = new ArrayList<Void>();
        ArrayList<Void> list_item_code = new ArrayList<Void>();
        Void master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Void(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9));
                list.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }



}
