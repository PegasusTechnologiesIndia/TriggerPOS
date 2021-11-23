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

public class Bussiness_Group {

    private static String tableName = "business_group";
    private String business_group_id;
    private String device_code;
    private String business_group_code;
    private String parent_code;
    private String name;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private Database db;

    private ContentValues value;

    public Bussiness_Group(Context context, String business_group_id, String device_code,
                           String business_group_code,String parent_code, String name, String is_active, String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();

        this.set_business_group_id(business_group_id);
        this.set_device_code(device_code);
        this.set_business_group_code(business_group_code);
        this.set_parent_code(parent_code);
        this.set_name(name);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);


    }

    public String get_business_group_id() {
        return business_group_id;
    }

    public void set_business_group_id(String business_group_id) {
        this.business_group_id = business_group_id;
        value.put("business_group_id", business_group_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_business_group_code() {
        return business_group_code;
    }

    public String get_parent_code() {
        return parent_code;
    }

    public void set_parent_code(String parent_code) {
        this.parent_code = parent_code;
        value.put("parent_code", parent_code);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
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


    public void set_business_group_code(String business_group_code) {
        this.business_group_code = business_group_code;
        value.put("business_group_code", business_group_code);
    }

    public void add_businessgrp(ArrayList<Bussiness_Group> list,SQLiteDatabase db) {

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Bussiness_Group bussinessGroup : list) {


                values.put("business_group_id", bussinessGroup.get_business_group_id());
                values.put("device_code", bussinessGroup.get_device_code());
                values.put("business_group_code", bussinessGroup.get_business_group_code());
                values.put("parent_code", bussinessGroup.get_parent_code());
                values.put("name", bussinessGroup.get_name());
                values.put("is_active", bussinessGroup.get_is_active());
                values.put("modified_by", bussinessGroup.get_modified_by());
                values.put("modified_date", bussinessGroup.get_modified_date());
                values.put("is_push", bussinessGroup.get_is_push());

                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public long insertBussiness_Group(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "business_group_id", value);
        //database.close();
        return insert;
    }

    public static long delete_Bussiness_Group(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

//        sdb.close();
        return 1;
    }

    public long updateBussiness_Group(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Bussiness_Group getBussiness_Group(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Bussiness_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Bussiness_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Bussiness_Group> getAllBussiness_Group(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Bussiness_Group> list = new ArrayList<Bussiness_Group>();
        Bussiness_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Bussiness_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String ig = "0";
        String strBussinessGroupCode = "";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                strBussinessGroupCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
                sender.put("business_group".toLowerCase(), result);
               // String serverData = send_manufactrue_json_on_server(sender.toString());
                sendbusiness_json_on_server(context,database,db,sender.toString(),strBussinessGroupCode);

            }
            cursor.close();
        } catch (Exception ex) {
        }
        return ig;
    }

    /*private static String send_manufactrue_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "business_group/data");
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
    public static void sendbusiness_json_on_server(Context context, SQLiteDatabase database,Database db,final String JsonString,String strBussinessGroupCode){
      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String cmpnyId = Globals.Company_Id;
        String server_url =Globals.App_IP_URL + "business_group/data";
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
                                String Query = "Update  business_group Set is_push = 'Y' Where business_group_code = '" + strBussinessGroupCode + "'";
                                long check = db.executeDML(Query,database);
                                if (check>0){
                                  //  ig = "1";
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                }else {
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
                params.put("company_id", cmpnyId);
                params.put("data", JsonString);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
