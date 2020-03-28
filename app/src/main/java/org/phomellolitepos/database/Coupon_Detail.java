package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class Coupon_Detail {
    private static String tableName = "coupon_detail";
    private String id;
    private String card_id;
    private String card_no;
    private String is_used;

    private Database db;
    private ContentValues value;

    public Coupon_Detail(Context context, String id, String card_id,
                     String card_no, String is_used) {

        db = new Database(context);
        value = new ContentValues();

        this.setid(id);
        this.set_card_id(card_id);
        this.set_card_no(card_no);
        this.set_is_used(is_used);

    }


    public String getid() {
        return id;
    }

    public void setid(String Id) {
        this.id = Id;
        value.put("id", id);
    }

    public String get_card_id() {
        return card_id;
    }

    public void set_card_id(String card_id) {
        this.card_id = card_id;
        value.put("card_id", card_id);
    }

    public String get_card_no() {
        return card_no;
    }

    public void set_card_no(String card_no) {
        this.card_no = card_no;
        value.put("card_no", card_no);
    }

    public String get_is_used() {
        return is_used;
    }

    public void set_is_used(String is_used) {
        this.is_used = is_used;
        value.put("is_used", is_used);
    }


    public long insertCoupon_Detail(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }


    public static long delete_Coupon_Detail(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public long updateCoupon_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Coupon_Detail getCoupon_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Coupon_Detail master = null;
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Coupon_Detail(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3));
                } while (cursor.moveToNext());
            }
            cursor.close();
            } catch (Exception ex) {
        }
        return master;
    }
}
