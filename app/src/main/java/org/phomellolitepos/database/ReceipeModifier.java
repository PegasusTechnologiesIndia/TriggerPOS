package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ReceipeModifier
{
    private static String tableName = "Receipe_Modifier";
    private String id;
    private String item_code;

    private String modifier_code;
    private Database db;
    private ContentValues value;

    public ReceipeModifier(Context ctx,String id,String itemcode,String modifiercode){

        db = new Database(ctx);
        value = new ContentValues();

        this.setId(id);
        this.setItem_code(itemcode);
        this.setModifier_code(modifiercode);
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String getModifier_code() {
        return modifier_code;
    }

    public void setModifier_code(String modifier_code) {
        this.modifier_code = modifier_code;
        value.put("modifier_code", modifier_code);
    }


    public long insertReceipemodifier(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteReceipemodifier(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateReceipeModifier(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }
    public static long delete_Item_ReceipeModifier(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //  sdb.close();
        return 1;
    }

    public static ReceipeModifier getReceipemOdifier(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ReceipeModifier master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new ReceipeModifier(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)
                        );
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }

    public static ArrayList<ReceipeModifier> getAllReceipeModifier(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<ReceipeModifier> list = new ArrayList<ReceipeModifier>();
        ReceipeModifier master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new ReceipeModifier(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


    public static ReceipeModifier getAllModifier(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select modifier_code FROM " + tableName + " " + WhereClasue;
        ArrayList<ReceipeModifier> list = new ArrayList<ReceipeModifier>();
        ReceipeModifier master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new ReceipeModifier(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
               // list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
      return master;
    }
}
