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
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.AppController;
import org.phomellolitepos.CusReturnFinalActivity;
import org.phomellolitepos.LoginActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class Returns {
    private static String tableName = "returns";
    private String id;
    private String contact_code;
    private String voucher_no;
    private String date;
    private String remarks;
    private String total;
    private String z_code;
    private String is_post;
    private String is_cancel;
    private String is_active;
    private String is_push;
    private String modified_by;
    private String modified_date;
    private String order_code;
    private String return_type;
    private String payment_id;

    private Database db;
    private ContentValues value;

    public Returns(Context context, String id, String contact_code, String voucher_no, String date, String remarks, String total,
                   String z_code, String is_post, String is_cancel, String is_active, String is_push,
                   String modified_by, String modified_date,
                   String order_code, String return_type, String payment_id) {

        db = new Database(context);
        value = new ContentValues();

        this.set_id(id);
        this.set_contact_code(contact_code);
        this.set_voucher_no(voucher_no);
        this.set_date(date);
        this.set_remarks(remarks);
        this.set_total(total);
        this.set_z_code(z_code);
        this.set_is_post(is_post);
        this.set_is_cancel(is_cancel);
        this.set_is_active(is_active);
        this.set_is_push(is_push);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_order_code(order_code);
        this.set_return_type(return_type);
        this.set_payment_id(payment_id);
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

    public String get_voucher_no() {
        return voucher_no;
    }

    public void set_voucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
        value.put("voucher_no", voucher_no);
    }

    public String get_date() {
        return date;
    }

    public void set_date(String date) {
        this.date = date;
        value.put("date", date);
    }

    public String get_remarks() {
        return remarks;
    }

    public void set_remarks(String remarks) {
        this.remarks = remarks;
        value.put("remarks", remarks);
    }

    public String get_total() {
        return total;
    }

    public void set_total(String total) {
        this.total = total;
        value.put("total", total);
    }

    public String get_z_code() {
        return z_code;
    }

    public void set_z_code(String z_code) {
        this.z_code = z_code;
        value.put("z_code", z_code);
    }

    public String get_is_post() {
        return is_post;
    }

    public void set_is_post(String is_post) {
        this.is_post = is_post;
        value.put("is_post", is_post);
    }

    public String get_is_cancel() {
        return is_cancel;
    }

    public void set_is_cancel(String is_cancel) {
        this.is_cancel = is_cancel;
        value.put("is_cancel", is_cancel);
    }

    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
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

    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }

    public String get_return_type() {
        return return_type;
    }

    public void set_return_type(String return_type) {
        this.return_type = return_type;
        value.put("return_type", return_type);
    }

    public String get_payment_id() {
        return payment_id;
    }

    public void set_payment_id(String payment_id) {
        this.payment_id = payment_id;
        value.put("payment_id", payment_id);
    }

    public long insertReturns(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();

        long insert = database.insert(tableName, "id", value);
        //database.close();
        return insert;
    }

    public long updateReturns(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        sdb.close();
        return insert;
    }

    public static long delete_Returns(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Returns getReturns(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Returns master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Returns(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14), cursor.getString(15));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }


    public static String getTotalCash(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select SUM(pay_amount) FROM " + tableName + " " + WhereClasue;
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

    // Here Changed in function  need to update all classes
    public static ArrayList<Returns> getAllReturns(Context context, String WhereClasue, SQLiteDatabase database) {


            String Query = "Select * FROM " + tableName + " " + WhereClasue;
            ArrayList<Returns> list = new ArrayList<Returns>();
        try {
            Returns master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Returns(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10),
                            cursor.getString(11), cursor.getString(12),
                            cursor.getString(13), cursor.getString(14), cursor.getString(15));
                    list.add(master);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {}
        return list;

    }

    public static String sendOnServer(ProgressDialog pdialog,Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String syscode1, String syscode3, String syscode4) {

        String strItemCode = "", resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_return_detail = new JSONArray();
                JSONObject item_return_detail_row = null;
                JSONArray array_return_detail_tax = new JSONArray();
                JSONObject item_return_detail_row_tax = null;

                strItemCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }

                String item_location_qry = "Select * FROM return_detail WHERE ref_voucher_no  = '" + strItemCode + "'";
                Cursor cursor_location = database.rawQuery(item_location_qry, null);
                try {
                    int columnCount_location = cursor_location.getColumnCount();
                    while (cursor_location.moveToNext()) {
                        item_return_detail_row = new JSONObject();
                        for (int index = 0; index < columnCount_location; index++) {
                            item_return_detail_row.put(cursor_location.getColumnName(index).toLowerCase(), cursor_location.getString(index));
                        }
                        item_return_detail_row.put("device_code", Globals.objLPD.getDevice_Code());
                        array_return_detail.put(item_return_detail_row);
                    }
                    cursor_location.close();
                    String return_tax_qry = "Select * FROM return_detail_tax WHERE order_return_voucher_no  = '" + strItemCode + "'";
                    Cursor cursor_return_tax = database.rawQuery(return_tax_qry, null);

                        int columnCount_returntax = cursor_return_tax.getColumnCount();
                        while (cursor_return_tax.moveToNext()) {
                            item_return_detail_row_tax = new JSONObject();
                            for (int index = 0; index < columnCount_returntax; index++) {
                                item_return_detail_row_tax.put(cursor_return_tax.getColumnName(index).toLowerCase(), cursor_return_tax.getString(index));
                            }
                            array_return_detail_tax.put(item_return_detail_row_tax);
                        }

                    cursor_return_tax.close();
                    row.put("order_return_detail", array_return_detail);
                    row.put("order_return_detail_tax", array_return_detail_tax);
                    row.put("device_code", Globals.objLPD.getDevice_Code());
                    row.put("location_id", Globals.objLPD.getLocation_Code());
                    result.put(row);
                    sender.put("order_return".toLowerCase(), result);

                    send_itemreturn_json_on_server(pdialog,context,database,db,resultStr,sender.toString(),strItemCode,syscode1,Globals.syscode2,syscode3,syscode4,liccustomerid);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
           String msg = ex.getMessage();
        }
        return resultStr;
    }


    public static String sendOnServerXZ(ProgressDialog pdialog,Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String syscode1, String syscode3, String syscode4) {

        String strItemCode = "", resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_return_detail = new JSONArray();
                JSONObject item_return_detail_row = null;

                strItemCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }

                String item_location_qry = "Select * FROM return_detail WHERE ref_voucher_no  = '" + strItemCode + "'";
                Cursor cursor_location = database.rawQuery(item_location_qry, null);
                try {
                    int columnCount_location = cursor_location.getColumnCount();
                    while (cursor_location.moveToNext()) {
                        item_return_detail_row = new JSONObject();
                        for (int index = 0; index < columnCount_location; index++) {
                            item_return_detail_row.put(cursor_location.getColumnName(index).toLowerCase(), cursor_location.getString(index));
                        }
                        item_return_detail_row.put("device_code", Globals.objLPD.getDevice_Code());
                        array_return_detail.put(item_return_detail_row);
                    }

                    cursor_location.close();
                    row.put("order_return_detail", array_return_detail);
                    row.put("device_code", Globals.objLPD.getDevice_Code());
                    row.put("location_id", Globals.objLPD.getLocation_Code());
                    result.put(row);
                    sender.put("order_return".toLowerCase(), result);

                    send_itemreturn_json_on_serverXZ(pdialog,context,database,db,resultStr,sender.toString(),strItemCode,syscode1,Globals.syscode2,syscode3,syscode4,liccustomerid);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
        return resultStr;
    }

    /* private static String send_item_json_on_server(String JsonString,String regcode,String liccustomerid,String syscode1, String syscode3,String syscode4) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "order_return/data");

        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",regcode));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", syscode1));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", syscode3));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", syscode4));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id",liccustomerid ));
System.out.println("order return"+ nameValuePairs);
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
            System.out.println("order return response"+ serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/
    public static void send_itemreturn_json_on_server(final ProgressDialog pDialog,Context context, SQLiteDatabase database,Database db,final String ig,final String JsonString,String strItemCode,final String syscode1,final String syscode2,final String syscode3,final String syscode4, final String liccustomerid) {

    /*    pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "order_return/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject collection_jsonObject1 = new JSONObject(response);
                            final String strStatus = collection_jsonObject1.getString("status");
                            final String strmsg = collection_jsonObject1.getString("message");
                            if (strStatus.equals("true")) {
                                //Update This Item Group Push True

                                database.beginTransaction();
                                String Query = "Update returns Set is_post='true' Where voucher_no = '" + strItemCode + "'";
                                long check = db.executeDML(Query, database);
                                if (check > 0) {
                                    //resultStr = "1";
                                    Globals.strvoucherno=strItemCode;
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                } else {
                                    database.endTransaction();
                                }
                            }
                            else if(strStatus.equals("false")){


                                        if (Globals.responsemessage.equals("Device Not Found")) {

                                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(context, "", database);
                                            lite_pos_device.setStatus("Out");
                                            long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                            if (ct > 0) {

                                                Intent intent_category = new Intent(context, LoginActivity.class);
                                                context.startActivity(intent_category);
                                            }


                                        }

                              else if(!Globals.responsemessage.equals("Device Not Found")){
                                        Toast.makeText(context, Globals.responsemessage, Toast.LENGTH_SHORT).show();
                                    }




                            }

                            else {
                                Toast.makeText(context, context.getString(R.string.recordntpostserver), Toast.LENGTH_SHORT).show();

                                //resultStr = "2";
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                        }
                  // pDialog.dismiss();

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




    public static void send_itemreturn_json_on_serverXZ(final ProgressDialog pDialog,Context context, SQLiteDatabase database,Database db,final String ig,final String JsonString,String strItemCode,final String syscode1,final String syscode2,final String syscode3,final String syscode4, final String liccustomerid) {

    /*    pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "order_return/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject collection_jsonObject1 = new JSONObject(response);
                            final String strStatus = collection_jsonObject1.getString("status");
                            final String strmsg = collection_jsonObject1.getString("message");
                            if (strStatus.equals("true")) {
                                //Update This Item Group Push True

                                database.beginTransaction();
                                String Query = "Update returns Set is_push = 'Y' where  is_push = 'N'";
                                long check = db.executeDML(Query, database);
                                if (check > 0) {
                                    //resultStr = "1";
                                    Globals.strvoucherno=strItemCode;
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                } else {
                                    database.endTransaction();
                                }
                            }
                            else if(strStatus.equals("false")){


                                if (Globals.responsemessage.equals("Device Not Found")) {

                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(context, "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {

                                        Intent intent_category = new Intent(context, LoginActivity.class);
                                        context.startActivity(intent_category);
                                    }


                                }

                                else if(!Globals.responsemessage.equals("Device Not Found")){
                                    Toast.makeText(context, Globals.responsemessage, Toast.LENGTH_SHORT).show();
                                }




                            }

                            else {
                                Toast.makeText(context, context.getString(R.string.recordntpostserver), Toast.LENGTH_SHORT).show();

                                //resultStr = "2";
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                        }
                        // pDialog.dismiss();

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
