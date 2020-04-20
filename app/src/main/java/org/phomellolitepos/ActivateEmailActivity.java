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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ActivateEmailActivity extends AppCompatActivity {
    EditText edt_email,edt_regis_code;
    Button btn_save;
    ProgressDialog progressDialog;
    Lite_POS_Registration lite_pos_registration;
    Lite_POS_Device lpd;
    User user;
    Database db;
    SQLiteDatabase database;
    AESHelper aesHelper;
    ArrayList<String> List = new ArrayList<String>();
    String strResult = "";
    JavaEncryption javaEncryption;
    Last_Code last_code;
    String licensecustomer;
    String registration_code;
    String serial_no, android_id, myKey, device_id,imei_no;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fill_user_permission();
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_regis_code = (EditText) findViewById(R.id.edt_regis_code);
        btn_save = (Button) findViewById(R.id.btn_save);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

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
        Globals.serialno=serial_no;
        Globals.androidid=android_id;
        Globals.mykey=myKey;
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
//        int id = pref.getInt("id", 0);
//        if (id == 0) {
//            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
//        } else {
//            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
//        }
        aesHelper = new AESHelper();
        javaEncryption = new JavaEncryption();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivateEmailActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        edt_email.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (edt_email.getText().toString().trim().equals("")) {
//                    return false;
//                } else {
//                    edt_email.requestFocus();
//                    edt_email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm4.showSoftInput(edt_email, InputMethodManager.SHOW_IMPLICIT);
//                    edt_email.selectAll();
//                    return true;
//                }
//            }
//        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_email.getText().toString().trim().equals("")){
                    edt_email.setError("Please enter email");
                    return;
                }

                if (edt_regis_code.getText().toString().trim().equals("")){
                    edt_regis_code.setError("Please enter registration code");
                    return;
                }

                if (isNetworkStatusAvialable(getApplicationContext())) {
                    progressDialog = new ProgressDialog(ActivateEmailActivity.this);
                    progressDialog.setTitle(getString(R.string.Device_registration));
                    progressDialog.setMessage(getString(R.string.Wait_msg));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            String rslt = device_process();

                            progressDialog.dismiss();
                            if (rslt.equals("1")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                                            if (last_code == null) {
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        Lite_POS_Device liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                        Lite_POS_Registration  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                        getLastCode(Globals.license_id, Globals.reg_code);
                                                    }
                                                }.start();
                                            }
                                        }
                                        catch(Exception e){

                                        }
                                        Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ActivateEmailActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else if (rslt.equals("2")) {

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), Globals.responsemessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

//                                Intent intent = new Intent(ActivateEmailActivity.this, RegistrationActivity.class);
//                                startActivity(intent);
//                                finish();
                            }
                            else if (rslt.equals("4")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.devicesymbolerror, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(ActivateEmailActivity.this, RegistrationActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                    }
                                });
                            }
                        }
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private String device_process() {
        String succ = "0";
        long u = 0;
        // Call device register api here
        String serverData = device_on_server();
        if (serverData==null){

        }else {
            database.beginTransaction();
            try {
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                final String strMassage = collection_jsonObject1.getString("message");

                if (strStatus.equals("true")) {
                    try {
                        JSONObject myResult = collection_jsonObject1.getJSONObject("result");
                        JSONObject jsonObject_device = myResult.optJSONObject("device");
                        if (jsonObject_device.getString("device_symbol").length() == 0) {
                            succ = "4";
                        }
                        else {
                            lpd = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                                    jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                                    jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                                    jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                                    jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                                    jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                                    jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN");

                           licensecustomer=jsonObject_device.getString("lic_customer_license_id");
                            long d = lpd.insertDevice(database);

                            if (d > 0) {
                                succ = "1";
                                JSONObject jsonObject_company = myResult.optJSONObject("company");
                                String company_id = jsonObject_company.getString("company_id");
                                Globals.Company_Id = company_id;
                                 registration_code = jsonObject_company.getString("registration_code");
                                Globals.reg_code=registration_code;

                                String company_name = jsonObject_company.getString("company_name");
                                String license_no;
                                try {
                                    license_no = jsonObject_company.getString("license_no");
                                } catch (Exception ex) {
                                    license_no = "";
                                }

                                String logo = jsonObject_company.getString("logo");
//                            String splash_screen = jsonObject_company.getString("splash_screen");
                                String contact_person = jsonObject_company.getString("contact_person");
                                String email = jsonObject_company.getString("email");
                                String country_id = jsonObject_company.getString("country_id");
                                String zone_id = jsonObject_company.getString("zone_id");
                                String mobile_no = jsonObject_company.getString("mobile_no");
                                String address = jsonObject_company.getString("address");
                                String project_id = jsonObject_company.getString("project_id");
                                String indus_type = jsonObject_company.getString("industry_type");
                                String srvc_trf = jsonObject_company.getString("service_code_tariff");

                                Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                                settings.set_Logo(logo);
                                settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);

                                lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name,
                                        contact_person, mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type);
                                long r = lite_pos_registration.insertRegistration(database);
                                if (r > 0) {
                                    for (int i = 0; i < List.size(); i++) {

                                        String name = List.get(i);
                                        if (strResult.equals("")) {
                                            strResult = name;
                                        }
                                        strResult = strResult + "," + name;

                                    }
                                    succ = "1";
                                    JSONArray jsonArray = myResult.getJSONArray("company_user");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject_user = jsonArray.getJSONObject(i);
                                        user = new User(getApplicationContext(), null,
                                                jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", strResult);
                                        u = user.insertUser(database);
                                    }
                                    if (u > 0) {
                                        succ = "1";
                                        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("pass", user.get_password()); // Storing string
                                        editor.apply();
                                    }
                                } else {
                                }
                            } else {
                            }
                        }
                    } catch (JSONException jex) {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else if (strStatus.equals("false")) {
                    succ = "2";
                   Globals.responsemessage=strMassage;
                }

                if (succ.equals("1")) {
                    Globals.license_id=licensecustomer;
                    Globals.reg_code=registration_code;
                    database.setTransactionSuccessful();
                    database.endTransaction();
                } else {
                    database.endTransaction();
                }


            } catch (JSONException e) {
                progressDialog.dismiss();
            }
        }

        return succ;
    }

    private String device_on_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/device/register");
        ArrayList nameValuePairs = new ArrayList(7);
        nameValuePairs.add(new BasicNameValuePair("email", edt_email.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("reg_code", edt_regis_code.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
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

    private void fill_user_permission() {
        List.clear();
        List.add("Item Category");
        List.add("Item");
        List.add("Contact");
        List.add("Order");
        List.add("Manager");
        List.add("Reservation");
        List.add("Settings");
        List.add("Report");
        List.add("Tax");
        List.add("Unit");
        List.add("Database");
        List.add("Update License");
        List.add("Profile");
        List.add("Returns");
        List.add("Accounts");
        List.add("User");
    }

    private void getLastCode(String liccustomerid,String registrationcode) {
        String serverData = getLastCodeFromServer(liccustomerid,registrationcode);
        try {
            if (serverData == null) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server Not Found", Toast.LENGTH_SHORT).show();
                    }

                });
            }

        }
        catch(Exception e){

        }
        try {
            JSONObject collection_jsonObject1 = new JSONObject(serverData);
            String strStatus = collection_jsonObject1.getString("status");
            String massage = collection_jsonObject1.getString("message");

            if (strStatus.equals("true")) {
                database.beginTransaction();
                JSONObject jsonObject = collection_jsonObject1.getJSONObject("result");

                String last_order_code = jsonObject.getString("last_order_code");
                String last_pos_balance_code = jsonObject.getString("last_pos_balance_code");
                String last_z_close_code = jsonObject.getString("last_z_close_code");
                String last_order_return_code=jsonObject.getString("last_order_return_code");

                long l = Last_Code.delete_Last_Code(getApplicationContext(), null, null, database);
                if (l > 0) {
                } else {
                }

                last_code = new Last_Code(getApplicationContext(), null, last_order_code, last_pos_balance_code, last_z_close_code,last_order_return_code);

                long d = last_code.insertLast_Code(database);

                if (d > 0) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                }
            } else {
            }
        } catch (JSONException e) {

        }
    }

    private String getLastCodeFromServer(String liccustomerid,String regcode) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/last_code");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",regcode));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1",serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
           System.out.println("get last code"+ nameValuePairs);
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
            System.out.println("get server last code"+ serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }

    @Override
    public void onBackPressed() {

    }
}
