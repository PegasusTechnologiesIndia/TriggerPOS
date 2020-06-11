package org.phomellolitepos;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

import org.json.JSONObject;

import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;

import java.util.HashMap;
import java.util.Map;

public class BackgroundApiService extends Service {
    //creating a mediaplayer object
    ProgressDialog pDialog;
    String serial_no, android_id, myKey, device_id, imei_no;
    private Handler mHandler;
    // default interval for syncing data
    public static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;
    SQLiteDatabase database;
    String reg_code, licensecode;
    Database db;
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {


            postBackgroundJson(Globals.objLPR.getRegistration_Code(), serial_no, Globals.syscode2, android_id, myKey, Globals.Device_Code, Globals.objLPD.getLic_customer_license_id(), Globals.jsonArray_background.toString());


            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            serial_no = Build.SERIAL;
            android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            myKey = serial_no + android_id;

            postBackgroundJson(Globals.objLPR.getRegistration_Code(), serial_no, Globals.syscode2, android_id, myKey, Globals.Device_Code, Globals.objLPD.getLic_customer_license_id(), Globals.jsonArray_background.toString());


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        super.onCreate();

        // postBackgroundJson(Globals.RegisCode,serial_no,"3",android_id,myKey,Globals.Device_Code ,Globals.lic_id,Globals.jsonArray_background.toString());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone


        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);


        //  postBackgroundJson(Globals.RegisCode,serial_no,"3",android_id,myKey,Globals.Device_Code ,Globals.lic_id,Globals.jsonArray_background.toString());
        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed

    }

    public void postBackgroundJson(final String registrationcode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String devicecode, final String licensecustomerid, final String backgroundjson) {

        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_3/last_background_service";
        HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            System.out.println("Background Service Response" + response);

                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            String message = responseObject.getString("message");
                            if (status.equals("true")) {

                            } else if (status.equals("false")) {
                                try {
                                    db = new Database(getApplicationContext());
                                    database = db.getWritableDatabase();
                                } catch (Exception e) {

                                }
                                if (message.equals("Device not found")) {


                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {


                                        ActivityManagerUtil.finishAllActivity();

                                        // Start the login form activity to let user login again.
                                        Intent loginFormIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        loginFormIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        getApplicationContext().startActivity(loginFormIntent);
                                    }
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
                           /* Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();*/
                            //Toast.makeText(getApplicationContext(), "Internet is not available, you cannot Logout", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            // Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof NetworkError) {
                            // Toast.makeText(getApplicationContext(), "Internet is not available, you cannot Logout", Toast.LENGTH_LONG).show();
                          /*  Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();*/
                        } else if (error instanceof ParseError) {

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registrationcode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("device_code", devicecode);
                params.put("lic_customer_license_id", licensecustomerid);
                params.put("last_background_json", backgroundjson);

                System.out.println("Background Service Params" + params);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}