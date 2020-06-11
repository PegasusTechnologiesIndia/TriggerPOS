package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.phomellolitepos.Util.Globals;

/**
 * Created by Neeraj on 4/25/2017.
 */

public class Pos_Balance {
    private static String tableName = "pos_balance";
    private String pos_balance_id;
    private String pos_balance_code;
    private String device_code;
    private String type;
    private String date;
    private String amount;
    private String remarks;
    private String z_code;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private Database db;
    private ContentValues value;

    public Pos_Balance(Context context, String pos_balance_id, String pos_balance_code, String device_code, String type,
                       String date, String amount, String remarks, String z_code,
                       String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_pos_balance_id(pos_balance_id);
        this.set_pos_balance_code(pos_balance_code);
        this.set_device_code(device_code);
        this.set_type(type);
        this.set_date(date);
        this.set_amount(amount);
        this.set_remarks(remarks);
        this.set_z_code(z_code);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
    }

    public String get_pos_balance_id() {
        return pos_balance_id;
    }

    public void set_pos_balance_id(String pos_balance_id) {
        this.pos_balance_id = pos_balance_id;
        value.put("pos_balance_id", pos_balance_id);
    }

    public String get_pos_balance_code() {
        return pos_balance_code;
    }

    public void set_pos_balance_code(String pos_balance_code) {
        this.pos_balance_code = pos_balance_code;
        value.put("pos_balance_code", pos_balance_code);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }


    public String get_type() {
        return type;
    }

    public void set_type(String type) {
        this.type = type;
        value.put("type", type);
    }

    public String get_date() {
        return date;
    }

    public void set_date(String date) {
        this.date = date;
        value.put("date", date);
    }


    public String get_amount() {
        return amount;
    }

    public void set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }


    public String get_remarks() {
        return remarks;
    }

    public void set_remarks(String remarks) {
        this.remarks = remarks;
        value.put("remarks", remarks);
    }

    public String get_z_code() {
        return z_code;
    }

    public void set_z_code(String z_code) {
        this.z_code = z_code;
        value.put("z_code", z_code);
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

    public long insertPos_Balance(SQLiteDatabase database) {
        // SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "pos_balance_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Pos_Balance(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updatePos_Balance(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Pos_Balance getPos_Balance(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Pos_Balance master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                master = new Pos_Balance(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11));

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Pos_Balance> getAllPos_Balance(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Pos_Balance> list = new ArrayList<Pos_Balance>();
        Pos_Balance master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pos_Balance(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        //db.close();
        return list;
    }


    public static String getTotalExpenses(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select SUM(amount) FROM " + tableName + " " + WhereClasue;
        String master = null;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                master = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return master;
    }


    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String serialno,String androidid,String mykey) {
        //        Database db = new Database(context);
        //        SQLiteDatabase database = db.getReada,bleDatabase();
        String strZ_Code = "", resultStr = "0";
        // Cursor cursor = database.rawQuery(strTableQry, null);
        Cursor cursor_z_close = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor_z_close.getColumnCount();
            //            while (cursor.moveToNext()) {
            JSONObject sender = new JSONObject();
            //                JSONObject result = new JSONObject();
            //                JSONObject row = new JSONObject();
            JSONArray array_pos_balance = new JSONArray();
            JSONObject row_pos_balance = new JSONObject();
            JSONArray array_z_close = new JSONArray();
            JSONObject z_close_row = new JSONObject();
            JSONArray array_z_detail = new JSONArray();
            JSONObject z_detail_row = new JSONObject();
            JSONArray array_acc_detail = new JSONArray();
            JSONObject acc_detail_row = new JSONObject();

            int columnCount_z_close = cursor_z_close.getColumnCount();
            while (cursor_z_close.moveToNext()) {
                strZ_Code = cursor_z_close.getString(1);
                z_close_row = new JSONObject();
                for (int index = 0; index < columnCount_z_close; index++) {
                    z_close_row.put(cursor_z_close.getColumnName(index).toLowerCase(), cursor_z_close.getString(index));
                }
                array_z_close.put(z_close_row);

                String z_balance_qry = "Select pos_balance_id,pos_balance_code, device_code,type,date,amount,remarks,z_code,is_active,modified_by  FROM  pos_balance WHERE is_push = 'N' AND  z_code = '" + strZ_Code + "'";
                Cursor cursor_balance_qry = database.rawQuery(z_balance_qry, null);

                int columnCount_pos_balance = cursor_balance_qry.getColumnCount();
                while (cursor_balance_qry.moveToNext()) {

                    row_pos_balance = new JSONObject();
                    for (int index = 0; index < columnCount_pos_balance; index++) {
                        row_pos_balance.put(cursor_balance_qry.getColumnName(index).toLowerCase(), cursor_balance_qry.getString(index));
                    }
                    array_pos_balance.put(row_pos_balance);
                }
                cursor_balance_qry.close();


                String z_detail_qry = "SELECT device_code,z_code,sr_no,type,amount,is_active,modified_by FROM  z_detail Where z_code = '" + strZ_Code + "'";
                Cursor cursor_z_detail = database.rawQuery(z_detail_qry, null);

                int columnCount_z_detail = cursor_z_detail.getColumnCount();
                while (cursor_z_detail.moveToNext()) {
                    z_detail_row = new JSONObject();
                    for (int index = 0; index < columnCount_z_detail; index++) {
                        z_detail_row.put(cursor_z_detail.getColumnName(index).toLowerCase(), cursor_z_detail.getString(index));
                    }
                    array_z_detail.put(z_detail_row);
                }
                cursor_z_detail.close();

                String acc_detail_qry = "SELECT voucher_no FROM  Acc_Customer_Credit Where z_no = '" + strZ_Code + "'";
                Cursor cursor_acc_detail = database.rawQuery(acc_detail_qry, null);

                int columnCount_acc_detail = cursor_acc_detail.getColumnCount();
                while (cursor_acc_detail.moveToNext()) {
                    acc_detail_row = new JSONObject();
                    for (int index = 0; index < columnCount_acc_detail; index++) {
                        acc_detail_row.put(cursor_acc_detail.getColumnName(index).toLowerCase(), cursor_acc_detail.getString(index));
                    }
                    array_acc_detail.put(acc_detail_row);
                }
                cursor_acc_detail.close();

                sender.put("pos_balance", array_pos_balance);
                sender.put("z_close", array_z_close);
                sender.put("z_detail", array_z_detail);
               sender.put("account_detail", array_acc_detail);
//                    sender.put("pos_balance".toLowerCase(), sender);
                    String serverData = send_item_json_on_server(sender.toString(),liccustomerid,serialno,androidid,mykey);
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                if (strStatus.equals("true")) {
                    database.beginTransaction();
                    String Query = "Update  pos_balance Set is_push = 'Y' Where z_code = '" + strZ_Code + "'";
                    long or_result = db.executeDML(Query, database);
                    if (or_result > 0) {
                        resultStr = "1";
                    } else {
                        database.endTransaction();
                    }

                    String Query1 = "Update z_close Set is_push = 'Y' Where z_code = '" + strZ_Code + "'";
                    long or_result1 = db.executeDML(Query1, database);
                    if (or_result1 > 0) {
                        resultStr = "1";
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {
                        database.endTransaction();
                    }
                }
            }
            cursor_z_close.close();
        } catch (Exception ex) {}
        return resultStr;
    }

    private static String send_item_json_on_server(String JsonString,String liccustomerid,String serialno,String androidid,String mykey) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/pos_balance/data");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", mykey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }


}
