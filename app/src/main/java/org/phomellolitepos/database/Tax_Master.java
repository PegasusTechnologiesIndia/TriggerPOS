package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.HashMap;
import java.util.Map;

import org.phomellolitepos.AppController;
import org.phomellolitepos.Util.Globals;

public class Tax_Master {

    private static String tableName = "tax";
    private String tax_id;
    private String location_id;
    private String tax_name;
    private String tax_type;
    private String rate;
    private String comment;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;

    private Database db;
    private ContentValues value;

    public Tax_Master(Context context, String tax_id, String location_id, String tax_name, String tax_type, String rate, String comment, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_tax_id(tax_id);
        this.set_location_id(location_id);
        this.set_tax_name(tax_name);
        this.set_tax_type(tax_type);
        this.set_rate(rate);
        this.set_comment(comment);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);

    }


    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;
        value.put("tax_id", tax_id);
    }

    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id = location_id;
        value.put("location_id", location_id);
    }

    public String get_tax_name() {
        return tax_name;
    }

    public void set_tax_name(String tax_name) {
        this.tax_name = tax_name;
        value.put("tax_name", tax_name);
    }

    public String get_tax_type() {
        return tax_type;
    }

    public void set_tax_type(String tax_type) {
        this.tax_type = tax_type;
        value.put("tax_type", tax_type);
    }

    public String get_rate() {
        return rate;
    }

    public void set_rate(String rate) {
        this.rate = rate;
        value.put("rate", rate);
    }

    public String get_comment() {
        return comment;
    }

    public void set_comment(String comment) {
        this.comment = comment;
        value.put("comment", comment);
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

    public long insertTax_Master(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "tax_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Tax_Master(Context context, String whereClause, String[] whereArgs) {
        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        sdb.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }


    public long updateTax_Master(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Tax_Master getTax_Master(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Tax_Master master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Tax_Master(context, cursor.getString(0),
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
    public static ArrayList<Tax_Master> getAllTax_Master(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Tax_Master> list = new ArrayList<Tax_Master>();
        Tax_Master master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Tax_Master(context, cursor.getString(0),
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
        //database.close();
        //db.close();
        return list;
    }


    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String strTaxId = "", taxStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray item_group_tax_array = new JSONArray();
                JSONObject item_group_tax_object = new JSONObject();

                JSONArray order_type_tax_array = new JSONArray();
                JSONObject order_type_tax_object = new JSONObject();

                JSONArray tax_detail_array = new JSONArray();
                JSONObject tax_detail_object = new JSONObject();

                strTaxId = cursor.getString(0);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }

                String item_group_tax_qry = "select location_id, tax_id ,item_group_code from item_group_tax where tax_id = '" + strTaxId + "'";
                Cursor cursor_Igt = database.rawQuery(item_group_tax_qry, null);
                try {
                    int columnCount_Igt = cursor_Igt.getColumnCount();
                    while (cursor_Igt.moveToNext()) {
                        item_group_tax_object = new JSONObject();
                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_Igt; index++) {
                            item_group_tax_object.put(cursor_Igt.getColumnName(index).toLowerCase(), cursor_Igt.getString(index));

                        }
                        item_group_tax_array.put(item_group_tax_object);
                    }

                } catch (Exception ex) {
                }

                String order_type_tax_qry = "select location_id, tax_id ,order_type_id from order_type_tax where tax_id = '" + strTaxId + "'";
                Cursor cursor_ott = database.rawQuery(order_type_tax_qry, null);
                try {
                    int columnCount_ott = cursor_ott.getColumnCount();
                    while (cursor_ott.moveToNext()) {
                        order_type_tax_object = new JSONObject();
                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_ott; index++) {
                            order_type_tax_object.put(cursor_ott.getColumnName(index).toLowerCase(), cursor_ott.getString(index));

                        }
                        order_type_tax_array.put(order_type_tax_object);
                    }

                } catch (Exception ex) {
                }

                String tax_detail_qry = "select id, tax_id ,tax_type_id from Tax_Detail where tax_id = '" + strTaxId + "'";
                Cursor cursor_tax_detail = database.rawQuery(tax_detail_qry, null);
                try {
                    int columnCount_ott = cursor_tax_detail.getColumnCount();
                    while (cursor_tax_detail.moveToNext()) {
                        order_type_tax_object = new JSONObject();
                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_ott; index++) {
                            tax_detail_object.put(cursor_tax_detail.getColumnName(index).toLowerCase(), cursor_tax_detail.getString(index));

                        }
                        tax_detail_array.put(tax_detail_object);
                    }

                } catch (Exception ex) {
                }

                JSONObject tax_group_object = new JSONObject();
                JSONArray tax_group_array = new JSONArray();

                String tax_group_qry = "select tax_id ,tax_master_id from Sys_Tax_Group where tax_id = '" + strTaxId + "'";
                Cursor cursor_tax_group = database.rawQuery(tax_group_qry, null);
                try {
                    int columnCount_ott = cursor_tax_group.getColumnCount();
                    while (cursor_tax_group.moveToNext()) {
                        order_type_tax_object = new JSONObject();
                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_ott; index++) {
                            tax_group_object.put(cursor_tax_group.getColumnName(index).toLowerCase(), cursor_tax_group.getString(index));

                        }
                        tax_group_array.put(tax_group_object);
                    }

                } catch (Exception ex) {
                }


                //row.put("item_group_tax".toLowerCase(), item_group_tax_array);
                row.put("order_type_tax".toLowerCase(), order_type_tax_array);
                row.put("tax_detail".toLowerCase(), tax_detail_array);
                row.put("tax_group".toLowerCase(), tax_group_array);
                result.put(row);
                sender.put("tax".toLowerCase(), result);
                //String serverData = send_manufactrue_json_on_server(sender.toString());
                sendtax_json_on_server(context,database,db,sender.toString(),strTaxId);

            }
            cursor.close();
        } catch (Exception ex) {
        }

        return taxStr;
    }


    /*private static String send_manufactrue_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "tax/data");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
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
    public static void sendtax_json_on_server(Context context, SQLiteDatabase database,Database db,final String JsonString,String strTaxId){
      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url =Globals.App_IP_URL + "tax/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject jsonObject1 = new JSONObject(response);
                            final String strStatus = jsonObject1.getString("status");
                            if (strStatus.equals("true")) {
                                database.beginTransaction();
                                String Query = "Update  tax Set is_push = 'Y' Where tax_id = '" + strTaxId + "'";
                                long check = db.executeDML(Query, database);
                                if (check > 0) {
                                    //taxStr = "1";
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                } else {
                                    database.endTransaction();
                                }
                            }
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("data", JsonString);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
