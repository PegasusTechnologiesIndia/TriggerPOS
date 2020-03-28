package org.phomellolitepos.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

/**
 * Created by LENOVO on 10/6/2017.
 */

public class Customer_Image {

    private static String tableName = "Customer_Image";
    private String id;
    private String image_Path;

    private Database db;
    private ContentValues value;

    public Customer_Image(Context context, String id, String image_Path) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_image_Path(image_Path);

    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_image_Path() {
        return image_Path;
    }

    public void set_image_Path(String image_Path) {
        this.image_Path = image_Path;
        value.put("image_Path", image_Path);
    }

    public long insertCustomer_Image(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public long updateCustomer_Image(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Customer_Image(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {

        long delete = database.delete(tableName, whereClause, whereArgs);
        return delete;
    }

    public static Customer_Image getCustomer_Image(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Customer_Image master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Customer_Image(context, cursor.getString(0),
                        cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Customer_Image> getAllCustomer_Image(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Customer_Image> list = new ArrayList<Customer_Image>();
        Customer_Image master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Customer_Image(context, cursor.getString(0),
                        cursor.getString(1));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public static ArrayList<String> getAllCustomer_ImageString(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();
//        Customer_Image master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
