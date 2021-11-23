package org.phomellolitepos.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import org.phomellolitepos.CategoryListActivity;
import org.phomellolitepos.LoginActivity;
import org.phomellolitepos.Util.Globals;

public class Item_Group {
    private static String tableName = "item_group";
    private String item_group_id;
    private String device_code;
    private String item_group_code;
    private String parent_code;
    private String item_group_name;
    private String image;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private String categoryIp;
    private Database db;
    private ContentValues value;

    public Item_Group(Context context, String item_group_id, String device_code,
                      String item_group_code, String parent_code, String item_group_name, String image, String is_active,
                      String modified_by, String modified_date, String is_push,String categoryIp) {

        db = new Database(context);
        value = new ContentValues();
        this.set_item_group_id(item_group_id);
        this.set_device_code(device_code);
        this.set_item_group_code(item_group_code);
        this.set_parent_code(parent_code);
        this.set_item_group_name(item_group_name);
        this.set_image(image);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
        this.setCategoryIp(categoryIp);

    }

    public String getCategoryIp() {
        return categoryIp;
    }

    public void setCategoryIp(String categoryIp) {
        this.categoryIp = categoryIp;
        value.put("categoryIp", categoryIp);
    }

    public Item_Group(String item_group_name, String item_group_code){

    }
    public String get_item_group_id() {
        return item_group_id;
    }

    public void set_item_group_id(String item_group_id) {
        this.item_group_id = item_group_id;
        value.put("item_group_id", item_group_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }


    public String get_item_group_code() {
        return item_group_code;
    }

    public void set_item_group_code(String item_group_code) {
        this.item_group_code = item_group_code;
        value.put("item_group_code", item_group_code);
    }


    public String get_parent_code() {
        return parent_code;
    }

    public void set_parent_code(String parent_code) {
        this.parent_code = parent_code;
        value.put("parent_code", parent_code);
    }


    public String get_item_group_name() {
        return item_group_name;
    }

    public void set_item_group_name(String item_group_name) {
        this.item_group_name = item_group_name;
        value.put("item_group_name", item_group_name);
    }

    public String get_image() {
        return image;
    }

    public void set_image(String image) {
        this.image = image;
        value.put("image", image);
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


    public long insertItem_Group(SQLiteDatabase db) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = db.insert(tableName, "item_group_id", value);
        //database.close();
        return insert;
    }


    public long updateItem_Group(String whereClause, String[] whereArgs, SQLiteDatabase db)
            throws SQLiteConstraintException {
        // SQLiteDatabase sdb = this.db.getWritableDatabase();
        long insert = db.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Item_Group getItem_Group(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue ;
        Item_Group master = null;
        //Database db = new Database(context);
        //SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Item_Group> getAllItem_Group(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Group> list = new ArrayList<Item_Group>();
        ArrayList<Item_Group> list_item_code = new ArrayList<Item_Group>();
        Item_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10));
                list.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static ArrayList<Item_Group> getAllItem_GroupSpinner_All(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Group> list_spinner = new ArrayList<Item_Group>();
        Item_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10));
                list_spinner.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }

    public static ArrayList<String> getAllItem_GroupCustomList(Context context, String FieldName, String WhereClasue) {
        String Query = "Select " + FieldName + " FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();
        Item_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }
    public static ArrayList<String> getAllItem_GroupSpinner(Context context, String WhereClasue) {
        String Query = "Select item_group_name FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();
        Item_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static JSONArray convertCursorToJSON(Context context, String strTableQry) {
        JSONArray result = new JSONArray();
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject row = new JSONObject();

                for (int index = 0; index < columnCount; index++) {

                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
            }
            cursor.close();
        } catch (Exception ex) {
        }
        return result;
    }

    public static String sendOnServer(ProgressDialog progressDialog,Context context, SQLiteDatabase database, Database db, String strTableQry, String syscode1, String syscode2, String sycode3, String syscode4, String liccustomerid) {
        String ig = "0";
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String strItemGroupCode = "";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                strItemGroupCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
                sender.put("Item_Group".toLowerCase(), result);
                 send_item_group_json_on_server(progressDialog,context,database,db,ig,sender.toString(),strItemGroupCode,syscode1,syscode2,sycode3,syscode4,liccustomerid);

            }
            cursor.close();
        } catch (Exception ex) {
        }

        return ig;
    }

   /* private static String send_item_group_json_on_server(String JsonString,String syscode1,String syscode2,String syscode3,String syscode4,String liccustomerid) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "item_group/data");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", syscode1));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", syscode3));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", syscode4));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
  System.out.println("namevalue send group"+ nameValuePairs);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);
            System.out.println("response send group"+ serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/
    public static void send_item_group_json_on_server(final ProgressDialog progressDialog,Context context, SQLiteDatabase database,Database db,final String ig,final String JsonString,String strItemGroupCode,final String syscode1,final String syscode2,final String syscode3,final String syscode4, final String liccustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "item_group/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject collection_jsonObject1 = new JSONObject(response);
                            final String strStatus = collection_jsonObject1.getString("status");
                            final String strmessage = collection_jsonObject1.getString("message");
                            if (strStatus.equals("true")) {
                                database.beginTransaction();
                                String Query = "Update  item_group Set is_push = 'Y' Where item_group_code = '" + strItemGroupCode + "'";
                                long check = db.executeDML(Query, database);
                                if (check > 0) {
                                    //ig = "1";
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                } else {
                                    database.endTransaction();
                                }
                                progressDialog.dismiss();
                            }
                            else if(strStatus.equals("false")){
                                database.endTransaction();
                                //ig="0";
                                Globals.responsemessage= strmessage;
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
                        progressDialog.dismiss();
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
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("data", JsonString);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", liccustomerid);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
