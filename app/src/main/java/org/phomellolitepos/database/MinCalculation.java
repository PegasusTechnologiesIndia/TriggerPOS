package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MinCalculation {
    private static String tableName = "Time_Calculation";
    public String tcalid;
    public String range1;
    public String range2;
    public String actualvalue;
    private Database db;
    private ContentValues value;
    public MinCalculation(Context context,String calid, String fromrange1, String torange2, String actual_value)
    {
        db = new Database(context);
        value = new ContentValues();
        this.setTcalid(calid);
        this.setRange1(fromrange1);
        this.setRange2(torange2);
        this.setActualvalue(actual_value);

    }


    public String getTcalid() {
        return tcalid;
    }

    public void setTcalid(String tcalid) {
        this.tcalid = tcalid;
        value.put("tcalid", tcalid);
    }

    public String getRange1() {
        return range1;
    }

    public void setRange1(String range1) {
        this.range1 = range1;
        value.put("range1", range1);
    }

    public String getRange2() {
        return range2;
    }

    public void setRange2(String range2) {
        this.range2 = range2;
        value.put("range2", range2);
    }

    public String getActualvalue() {
        return actualvalue;
    }

    public void setActualvalue(String actualvalue) {
        this.actualvalue = actualvalue;
        value.put("actualvalue", actualvalue);
    }
    public long insertMinCalculation(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteMinCalculation(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateMinCalculation(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static MinCalculation getMinCalculation(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        MinCalculation master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new MinCalculation(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),cursor.getString(3)
                       );
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }
    public static ArrayList<MinCalculation> getAllMin_Calculation(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<MinCalculation> list = new ArrayList<MinCalculation>();
        MinCalculation master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                try {


                    master = new MinCalculation(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),cursor.getString(3));
                    list.add(master);
                } catch (Exception ex) {

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

}
