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
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Util.Globals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by LENOVO on 10/10/2017.
 */

public class Pay_Collection {

    private static String tableName = "Pay_Collection";
    private String id;
    private String contact_code;
    private String collection_code;
    private String collection_date;
    private String amount;
    private String payment_mode;
    private String ref_no;
    private String ref_type;
    private String on_account;
    private String remarks;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;


    public Pay_Collection(Context context, String id, String contact_code,String collection_code, String collection_date, String amount, String payment_mode,
                          String ref_no, String ref_type,String on_account, String remarks, String is_active, String modified_by, String modified_date,String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_contact_code(contact_code);
        this.set_collection_code(collection_code);
        this.set_collection_date(collection_date);
        this.set_amount(amount);
        this.set_payment_mode(payment_mode);
        this.set_ref_no(ref_no);
        this.set_ref_type(ref_type);
        this.set_on_account(on_account);
        this.set_remarks(remarks);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
        value.put("id", id);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_collection_code() {
        return collection_code;
    }

    public void set_collection_code(String collection_code) {
        this.collection_code = collection_code;
        value.put("collection_code", collection_code);
    }

    public String get_collection_date() {
        return collection_date;
    }

    public void set_collection_date(String collection_date) {
        this.collection_date = collection_date;
        value.put("collection_date", collection_date);
    }

    public String get_amount() {
        return amount;
    }

    public void set_amount(String amount) {
        this.amount = amount;
        value.put("amount", amount);
    }

    public String get_payment_mode() {
        return payment_mode;
    }

    public void set_payment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
        value.put("payment_mode", payment_mode);
    }

    public String get_ref_no() {
        return ref_no;
    }

    public void set_ref_no(String ref_no) {
        this.ref_no = ref_no;
        value.put("ref_no", ref_no);
    }

    public String get_ref_type() {
        return ref_type;
    }

    public void set_ref_type(String ref_type) {
        this.ref_type = ref_type;
        value.put("ref_type", ref_type);
    }

    public String get_on_account() {
        return on_account;
    }

    public void set_on_account(String on_account) {
        this.on_account = on_account;
        value.put("on_account", on_account);
    }

    public String get_remarks() {
        return remarks;
    }

    public void set_remarks(String remarks) {
        this.remarks = remarks;
        value.put("remarks", remarks);
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



    public long insertPay_Collection(SQLiteDatabase database) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updatePay_Collection(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static long delete_Pay_Collection(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public static Pay_Collection getPay_Collection(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Pay_Collection master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //database.close();
        // db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Pay_Collection> getAllPay_Collection(Context context,String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Pay_Collection> list = new ArrayList<Pay_Collection>();
        Pay_Collection master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Pay_Collection(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }


    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {

        String strItemCode = "", resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_payCollectionDetail = new JSONArray();
                JSONObject jsonObject_payCollectionDetail = new JSONObject();
                strItemCode = cursor.getString(2);
                row.put("device_code",Globals.Device_Code);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }

                try {

                    String pay_detail_qry = "Select item_code, contact_code from item_supplier where item_code  = '" + strItemCode + "'";
                    Cursor cursor_pay_detail = database.rawQuery(pay_detail_qry, null);
                    try {
                        int columnCount_pay_detail = cursor_pay_detail.getColumnCount();
                        while (cursor_pay_detail.moveToNext()) {
                            jsonObject_payCollectionDetail = new JSONObject();
                            for (int index = 0; index < columnCount_pay_detail; index++) {
                                jsonObject_payCollectionDetail.put(cursor_pay_detail.getColumnName(index).toLowerCase(), cursor_pay_detail.getString(index));
                            }
                            array_payCollectionDetail.put(jsonObject_payCollectionDetail);
                        }

                    } catch (Exception ex) {
                    }

                    row.put("pay_collection_detail", array_payCollectionDetail);
                    result.put(row);
                    sender.put("payment".toLowerCase(), result);
                    String serverData = "";
                            //send_item_json_on_server(sender.toString());
                    final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                    final String strStatus = collection_jsonObject1.getString("status");
                    if (strStatus.equals("true")) {
                        // Update This Item Group Push True
                        database.beginTransaction();
                        String Query = "Update Pay_Collection Set is_push = 'Y' Where collection_code = '" + strItemCode + "'";
                        long check = db.executeDML(Query, database);
                        if (check > 0) {
                            resultStr = "1";
                            database.setTransactionSuccessful();
                            database.endTransaction();
                        } else {
                            database.endTransaction();
                        }
                    } else {
                        resultStr = "2";
                        database.endTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                cursor.close();
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
        return resultStr;
    }

    // database.endTransaction();

    /*private static String send_item_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "payment_collection/data");

        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", cmpnyId));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
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
    }*/

}