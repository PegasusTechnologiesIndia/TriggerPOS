package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 4/27/2017.
 */

public class Z_Detail {

    private static String tableName = "z_detail";
    private String z_no;
    private String z_code;
    private String device_code;
    private String sr_no;
    private String type;
    private String amount;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;


    private Database db;
    private ContentValues value;

    public Z_Detail(Context context, String z_no, String z_code, String device_code, String sr_no, String type, String amount, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_z_no(z_no);
        this.set_z_code(z_code);
        this.set_device_code(device_code);
        this.set_sr_no(sr_no);
        this.set_type(type);
        this.set_amount(amount);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }

    public String get_z_no() {
        return z_no;
    }

    public void set_z_no(String z_no) {
        this.z_no = z_no;
        value.put("z_no", z_no);
    }

    public String get_z_code() {
        return z_code;
    }

    public void set_z_code(String z_code) {
        this.z_code = z_code;
        value.put("z_code", z_code);
    }


    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }


    public String get_sr_no() {
        return sr_no;
    }

    public void set_sr_no(String sr_no) {
        this.sr_no = sr_no;
        value.put("sr_no", sr_no);
    }

    public String get_type() {
        return type;
    }

    public void set_type(String type) {
        this.type = type;
        value.put("type", type);
    }

    public String get_amount() {
        return amount;
    }

    public void set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }



    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }


    public String get_modified_by() {
        return modified_by;
    }

    public void set_modified_by(String modified_by) {
        this.modified_by = modified_by;
        value.put("modified_by", modified_by);
    }

    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }

    public long insert_Z_Detail(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "tax_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Z_Detail(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        sdb.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }


    public long update_Z_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Z_Detail getZ_Detail(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Z_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Z_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Z_Detail> getAllZ_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Z_Detail> list = new ArrayList<Z_Detail>();
        Z_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Z_Detail(context, cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


//    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {
////        Database db = new Database(context);
////        SQLiteDatabase database = db.getReadableDatabase();
//        String strTaxId = "", taxStr = "0";
//        Cursor cursor = database.rawQuery(strTableQry, null);
//        try {
//            // cursor = db.rawQuery(strTableQry, null);
//            int columnCount = cursor.getColumnCount();
//            while (cursor.moveToNext()) {
//                JSONObject sender = new JSONObject();
//                JSONArray result = new JSONArray();
//                JSONObject row = new JSONObject();
//                JSONArray item_group_tax_array = new JSONArray();
//                JSONObject item_group_tax_object = new JSONObject();
//
//                JSONArray order_type_tax_array = new JSONArray();
//                JSONObject order_type_tax_object = new JSONObject();
//
//                strTaxId = cursor.getString(0);
//                for (int index = 0; index < columnCount; index++) {
//                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
//                }
//
//                String item_group_tax_qry = "select location_id, tax_id ,item_group_code from item_group_tax where tax_id = '" + strTaxId + "'";
//                Cursor cursor_Igt = database.rawQuery(item_group_tax_qry, null);
//                try {
//                    int columnCount_Igt = cursor_Igt.getColumnCount();
//                    while (cursor_Igt.moveToNext()) {
//                        item_group_tax_object = new JSONObject();
//                        //item_location_row.put("jjj", cursor_location.getString(index));
//                        for (int index = 0; index < columnCount_Igt; index++) {
//                            item_group_tax_object.put(cursor_Igt.getColumnName(index).toLowerCase(), cursor_Igt.getString(index));
//
//                        }
//                        item_group_tax_array.put(item_group_tax_object);
//                    }
//
//                } catch (Exception ex) {
//                }
//
//                String order_type_tax_qry = "select location_id, tax_id ,order_type_id from order_type_tax where tax_id = '" + strTaxId + "'";
//                Cursor cursor_ott = database.rawQuery(order_type_tax_qry, null);
//                try {
//                    int columnCount_ott = cursor_ott.getColumnCount();
//                    while (cursor_ott.moveToNext()) {
//                        order_type_tax_object = new JSONObject();
//                        //item_location_row.put("jjj", cursor_location.getString(index));
//                        for (int index = 0; index < columnCount_ott; index++) {
//                            order_type_tax_object.put(cursor_ott.getColumnName(index).toLowerCase(), cursor_ott.getString(index));
//
//                        }
//                        order_type_tax_array.put(order_type_tax_object);
//                    }
//
//                } catch (Exception ex) {
//                }
//
//
//                row.put("item_group_tax".toLowerCase(), item_group_tax_array);
//                row.put("order_type_tax".toLowerCase(), order_type_tax_array);
//                result.put(row);
//                sender.put("tax".toLowerCase(), result);
//                String serverData = send_manufactrue_json_on_server(sender.toString());
//                final JSONObject jsonObject1 = new JSONObject(serverData);
//                final String strStatus = jsonObject1.getString("status");
//                if (strStatus.equals("true")) {
//                    database.beginTransaction();
//                    String Query = "Update  tax Set is_push = 'Y' Where tax_id = '" + strTaxId + "'";
//                    long check = db.executeDML(Query, database);
//                    if (check > 0) {
//                        taxStr = "1";
//                        database.setTransactionSuccessful();
//                        database.endTransaction();
//                    } else {
//                        database.endTransaction();
//                    }
//                }
//            }
//            cursor.close();
//        } catch (Exception ex) {
//        }
//
//        return taxStr;
//    }
//
//
//    private static String send_manufactrue_json_on_server(String JsonString) {
//        String cmpnyId = Globals.Company_Id;
//        String serverData = null;//
//        DefaultHttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(
//                "http://" + Globals.App_IP + "/lite-pos/index.php/api/tax/data");
//        ArrayList nameValuePairs = new ArrayList(5);
//        nameValuePairs.add(new BasicNameValuePair("company_id", cmpnyId));
//        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//        } catch (UnsupportedEncodingException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        try {
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            serverData = EntityUtils.toString(httpEntity);
//            Log.d("response", serverData);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return serverData;
//    }
}
