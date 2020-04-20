package org.phomellolitepos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ActivateActivity extends AppCompatActivity {
    TextView txt_expiry;
    EditText edt_expiry;
    Lite_POS_Device lite_pos_device;
    ProgressDialog progressDialog;
    String str;
    Database db;
    SQLiteDatabase database;
    AESHelper aesHelper;
    JavaEncryption javaEncryption;
    Settings settings;
    String serial_no, android_id, myKey, device_id,imei_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Update_License);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);

        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
//        aesHelper = new AESHelper();
        javaEncryption = new JavaEncryption();
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        device_id = telephonyManager.getDeviceId();
        imei_no=telephonyManager.getImei();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ActivateActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(ActivateActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ActivateActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });
        txt_expiry = (TextView) findViewById(R.id.txt_expiry);
        edt_expiry = (EditText) findViewById(R.id.edt_expiry);
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        // Getting date from device table
        if (lite_pos_device == null) {
        } else {
            String ex_date = null;
            try {
                ex_date = javaEncryption.decrypt(lite_pos_device.getExpiry_Date(),"12345678");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex_date = ex_date.substring(0, 10);
            edt_expiry.setText(ex_date);
        }

        final Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        String strCheck = lite_pos_registration.getproject_id();


            if (isNetworkStatusAvialable(getApplicationContext())) {
                progressDialog = new ProgressDialog(ActivateActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage(getString(R.string.Updating_License));
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        String existing_expiry = null;
                        try {
                            existing_expiry = javaEncryption.decrypt(lite_pos_device.getExpiry_Date(),"12345678");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        update_license(existing_expiry, progressDialog);
                    }
                }.start();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }

    }

    private void update_license(String existing_expiry, final ProgressDialog progressDialog) {
        // Call check license api here
        String serverData = Update_licence_from_server();
        try {
            final JSONObject jsonObject1 = new JSONObject(serverData);
            final String strStatus = jsonObject1.getString("status");
            final String msg = jsonObject1.getString("message");
            if (strStatus.equals("true")) {
                try {
                    JSONObject myResult = jsonObject1.getJSONObject("result");
                    JSONObject myResult1 = myResult.getJSONObject("device");
                    String device_code = myResult1.getString("device_code");
                    String expiry_date = myResult1.getString("expiry_date");
                    String duration = myResult1.getString("duration");
                    if (existing_expiry.equals(expiry_date)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.Not_Update_Found, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        lite_pos_device.setExpiry_Date(javaEncryption.encrypt(expiry_date,"12345678"));
                        long l = lite_pos_device.updateDevice("device_code=?", new String[]{device_code}, database);
                        if (l > 0) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent_category = new Intent(ActivateActivity.this, MainActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    } else {
                                        Intent intent_category = new Intent(ActivateActivity.this, Main2Activity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.License_Not_Updated, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException jex) {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (strStatus.equals("false")) {
                progressDialog.dismiss();
                try {
                    if(msg.equals("Device Not Found")){

                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                        lite_pos_device.setStatus("Out");
                        long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                        if (ct > 0) {

                            Intent intent_category = new Intent(ActivateActivity.this, LoginActivity.class);
                            startActivity(intent_category);
                            finish();
                        }


                    }


                    JSONObject myResult = jsonObject1.getJSONObject("result");
                    JSONObject myResult1 = myResult.getJSONObject("device");
                    String device_code = myResult1.getString("device_code");
                    String expiry_date = myResult1.getString("expiry_date");
                    String duration = myResult1.getString("duration");
                    if (existing_expiry.equals(expiry_date)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.Not_Update_Found, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        lite_pos_device.setExpiry_Date(javaEncryption.encrypt(expiry_date,"12345678"));
                        long l = lite_pos_device.updateDevice("device_code=?", new String[]{device_code}, database);
                        if (l > 0) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent_category = new Intent(ActivateActivity.this, MainActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent_category = new Intent(ActivateActivity.this, Main2Activity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.License_Not_Updated, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException jex) {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String Update_licence_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/device/check_license");
        ArrayList nameValuePairs = new ArrayList(5);
        try {
            try {
                str = Globals.objLPR.getEmail();
            } catch (Exception ex) {
                str = "null";
            }
            if (str.equals("null")) {
            } else {
                nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
                nameValuePairs.add(new BasicNameValuePair("email", Globals.objLPR.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
                nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
                nameValuePairs.add(new BasicNameValuePair("sys_code_2", "4"));
                nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
                nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            }
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

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        progressDialog = new ProgressDialog(ActivateActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(ActivateActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    } finally {
                    }
                }  else {
                    try {
                        Intent intent = new Intent(ActivateActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }
}
