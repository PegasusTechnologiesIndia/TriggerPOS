package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class ScaleSetup {

    private static String tableName = "Scale_Setup";
    public String id;
    public  String name;
    public String Plu_value;
    public String Plu_len;
    public String ITEM_CODE_LEN;
    public String Wp_LEN;
    public String Wp_CALC;
    public String Is_WEIGHT;
    public String PCS_UNIT_ID;
    public String PLU_BARCODE_LEN;
    private Database db;
    private ContentValues value;

    public ScaleSetup(Context context, String sid, String sname,
                 String plval, String pllen, String itcodelen, String wplen, String wpcalc, String isweight,String pcsunitid,
                      String plbarcodlen) {

        db = new Database(context);
        value = new ContentValues();

        this.setId(sid);
        this.setName(sname);
        this.setPlu_value(plval);
        this.setPlu_len(pllen);
        this.setITEM_CODE_LEN(itcodelen);
        this.setWp_LEN(wplen);
        this.setWp_CALC(wpcalc);
        this.setIs_WEIGHT(isweight);
        this.setPCS_UNIT_ID(pcsunitid);
        this.setPLU_BARCODE_LEN(plbarcodlen);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        value.put("name", name);
    }

    public String getPlu_value() {
        return Plu_value;
    }

    public void setPlu_value(String plu_value) {
        Plu_value = plu_value;
        value.put("Plu_value", plu_value);
    }

    public String getPlu_len() {
        return Plu_len;
    }

    public void setPlu_len(String plu_len) {
        Plu_len = plu_len;
        value.put("Plu_len", plu_len);
    }

    public String getITEM_CODE_LEN() {
        return ITEM_CODE_LEN;
    }

    public void setITEM_CODE_LEN(String ITEM_CODE_LEN) {
        this.ITEM_CODE_LEN = ITEM_CODE_LEN;
        value.put("ITEM_CODE_LEN", ITEM_CODE_LEN);
    }

    public String getWp_LEN() {
        return Wp_LEN;
    }

    public void setWp_LEN(String wp_LEN) {
        Wp_LEN = wp_LEN;
        value.put("Wp_LEN", wp_LEN);
    }

    public String getWp_CALC() {
        return Wp_CALC;
    }

    public void setWp_CALC(String wp_CALC) {
        Wp_CALC = wp_CALC;
        value.put("Wp_CALC", wp_CALC);
    }

    public String getIs_WEIGHT() {
        return Is_WEIGHT;
    }

    public void setIs_WEIGHT(String is_WEIGHT) {
        Is_WEIGHT = is_WEIGHT;
        value.put("Is_WEIGHT", is_WEIGHT);
    }

    public String getPCS_UNIT_ID() {
        return PCS_UNIT_ID;
    }

    public void setPCS_UNIT_ID(String PCS_UNIT_ID) {
        this.PCS_UNIT_ID = PCS_UNIT_ID;
        value.put("PCS_UNIT_ID", PCS_UNIT_ID);
    }

    public String getPLU_BARCODE_LEN() {
        return PLU_BARCODE_LEN;
    }

    public void setPLU_BARCODE_LEN(String PLU_BARCODE_LEN) {
        this.PLU_BARCODE_LEN = PLU_BARCODE_LEN;
        value.put("PLU_BARCODE_LEN", PLU_BARCODE_LEN);
    }

    public long insertScaleSetup(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        return insert;
    }

    public static long deleteScaleSetup(Context context,String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateScaleSetup(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }


    public static ScaleSetup getScaleSetup(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ScaleSetup master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new ScaleSetup(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)
                        , cursor.getString(3), cursor.getString(4)
                        , cursor.getString(5), cursor.getString(6), cursor.getString(7)
                , cursor.getString(8), cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        //db.close();
        return master;
    }


}
