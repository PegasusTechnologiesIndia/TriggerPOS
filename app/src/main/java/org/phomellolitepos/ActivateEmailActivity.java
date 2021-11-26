package org.phomellolitepos;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.MinCalculation;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;

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
    String registration_code,locationname;
    ProgressDialog pDialog;
    String project_id;
    TextView tx_newregister;
     String result = "";
    PackageInfo pInfo = null;
     TextView txt_version,txt_changeip;
    String serial_no, android_id, myKey, device_id,imei_no;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_activate_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.register));
        fill_user_permission();
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_regis_code = (EditText) findViewById(R.id.edt_regis_code);
        txt_version=(TextView)findViewById(R.id.txt_version) ;
        tx_newregister=(TextView)findViewById(R.id.txv_register) ;
        txt_changeip=(TextView)findViewById(R.id.txv_changeip) ;
        btn_save = (Button) findViewById(R.id.btn_save);
        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText("Version : " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;
        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

       // TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                   android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
       // imei_no=mTelephony.getImei();
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

        txt_changeip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Dialog listDialog1 = new Dialog(ActivateEmailActivity.this);
                listDialog1.setTitle(R.string.Select_Contact);
                LayoutInflater li1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.custom_layout_ipchange, null, false);
                listDialog1.setContentView(v1);
                listDialog1.setCancelable(true);


                final EditText edt_ip = (EditText) listDialog1.findViewById(R.id.edt_ipchange);
                final EditText edt_password = (EditText) listDialog1.findViewById(R.id.edt_ippassword);
                Button btn_save = (Button) listDialog1.findViewById(R.id.btn_save);

                listDialog1.show();
               // fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                Window window = listDialog1.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Settings settings = Settings.getSettings(getApplicationContext(), database, "");

                edt_ip.setText(settings.getApi_Ip());
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        edt_password.setVisibility(View.VISIBLE);

                        if(edt_ip.getText().toString().length()==0){
                            edt_ip.setError("Enter Ip Address");
                            return;
                        }
                       else if(edt_password.getText().toString().length()==0){
                            edt_password.setError("Enter Password");
                            return;
                        }
                       else if(!edt_password.getText().toString().equals(pInfo.versionName)){
                            Toast.makeText(getApplicationContext(),"Please Enter Password to proceed",Toast.LENGTH_LONG).show();

                            edt_password.setError("Wrong Password");
                            return;
                        }
                       else if(edt_password.getText().toString().equals(pInfo.versionName)){
                            Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                            settings.setApi_Ip(String.valueOf(edt_ip.getText().toString()));
                            settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);
                            listDialog1.dismiss();
                        }


                    }
                });

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


        tx_newregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(getApplicationContext(),NewRegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_email.getText().toString().trim().equals("")){
                    edt_email.setError("Please enter email");
                    return;
                }

                else if (edt_regis_code.getText().toString().trim().equals("")){
                    edt_regis_code.setError("Please enter registration code");
                    return;
                }
                else {
                    if (isNetworkStatusAvialable(getApplicationContext())) {


                                try {
                                    postDeviceRegister(edt_email.getText().toString(), edt_regis_code.getText().toString(), Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey);

                                }
                                catch(Exception e){
                                    System.out.println(e.getMessage());
                                }

                                // Globals.AppLogWrite("device register"+rslt);
                               // progressDialog.dismiss();
/*                                if (rslt.equals("1")) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                   *//*         try {
                                                Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                                                if (last_code == null) {
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            Lite_POS_Device liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                            Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                            getLastCode(Globals.license_id, Globals.reg_code);
                                                        }
                                                    }.start();
                                                }
                                            } catch (Exception e) {

                                            }*//*
                                            Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ActivateEmailActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else if (rslt.equals("5")) {

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    if (Globals.responsemessage.toString().equals("Purchase More Device")) {

                                                        try {
                                                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivateEmailActivity.this);

                                                            builder.setTitle(getString(R.string.alerttitle));
                                                            builder.setMessage(getString(R.string.alert_loginmsg));

                                                            builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Do nothing but close the dialog
                                                                    getAlertclearTransactionDialog();

                                                                }
                                                            });

                                                            builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    // Do nothing
                                                                    dialog.dismiss();
                                                                }
                                                            });

                                                            android.support.v7.app.AlertDialog alert = builder.create();
                                                            alert.show();

                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),Globals.responsemessage.toString(), Toast.LENGTH_SHORT).show();

                                                    }
                                                    // Globals.showToast(getApplicationContext(), Globals.json_responsemessage, Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);
                                                }
                                            });
                                        }
                                    });

//                                Intent intent = new Intent(ActivateEmailActivity.this, RegistrationActivity.class);
//                                startActivity(intent);
//                                finish();
                                } else if (rslt.equals("4")) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.devicesymbolerror, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(ActivateEmailActivity.this, RegistrationActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                        }
                                    });
                                }*/

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }
    public void getAlertclearTransactionDialog(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ActivateEmailActivity.this);

        builder.setTitle(getString(R.string.alerttitle));
        builder.setMessage(getString(R.string.alert_loginseconddialog));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {
                    postDeviceInfo(edt_email.getText().toString(), "", Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey,edt_regis_code.getText().toString(),"1");
                }
                catch(Exception e){

                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();


    }
    private String device_process(String serverData,final ProgressDialog progressDialog) {

        String succ = "0";
        long u = 0;
        // Call device register api here

           // String serverData = device_on_server();

            database.beginTransaction();
            try {
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);

                final String strStatus = collection_jsonObject1.getString("status");
                final String strMassage = collection_jsonObject1.getString("message");

                if (strStatus.equals("true")) {
                    try {
                        JSONObject myResult = collection_jsonObject1.getJSONObject("result");

                        JSONObject jsonObject_device = myResult.optJSONObject("device");
                        JSONObject jsonObject_company = myResult.optJSONObject("company");

                        if (jsonObject_company.getString("project_id").equals("cloud")){

                            if (jsonObject_device.getString("location_name") == null || jsonObject_device.getString("location_name").equals("null") || jsonObject_device.getString("location_name").length() == 0) {
                                    succ = "4";
                                }


                            }
                          if(!succ.equals("4"))  {
                                lpd = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                                        jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                                        jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                                        jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                                        jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                                        jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                                        jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN",jsonObject_device.getString("location_name"));


                                String Scale= jsonObject_device.getString("scale");
                                licensecustomer = jsonObject_device.getString("lic_customer_license_id");
                                long d = lpd.insertDevice(database);

                                if (d > 0) {
                                    succ = "1";
                                    String company_id = jsonObject_company.getString("company_id");
                                    Globals.Company_Id = company_id;
                                    registration_code = jsonObject_company.getString("registration_code");
                                    Globals.reg_code = registration_code;
                                    locationname=jsonObject_device.getString("location_name");
                                    Globals.locname=locationname;
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
                                    String mobile_no = jsonObject_company.getString("phone");
                                    String address = jsonObject_company.getString("address");
                                     project_id = jsonObject_company.getString("project_id");
                                    String indus_type = jsonObject_company.getString("industry_type");
                                    String srvc_trf = jsonObject_company.getString("service_code_tariff");
                                    String password = jsonObject_company.getString("password");
                                    String zonename = jsonObject_company.getString("zone_name");
                                    String countryname = jsonObject_company.getString("country_name");


                                    Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                                    settings.set_Logo(logo);
                                    settings.set_Scale(Scale);
                                    settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);

                                    lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name,
                                            contact_person, mobile_no, country_id, zone_id, password, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                                    long r = lite_pos_registration.insertRegistration(database);
                                    if (r > 0) {
                                        for (int i = 0; i < List.size(); i++) {

                                            String name = List.get(i);
                                            if (strResult.equals("")) {
                                                strResult = null;
                                            }
                                            strResult = strResult + "," + name;

                                        }
                                        succ = "1";
                                        JSONArray jsonArray = myResult.getJSONArray("company_user");

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject_user = jsonArray.getJSONObject(i);
                                            user = new User(getApplicationContext(), null,
                                                    jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", jsonObject_user.getString("app_user_permission"));
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
                        } catch(JSONException jex){
                            progressDialog.dismiss();
                        } catch(Exception e){
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                        if (succ.equals("1")) {
                            Globals.license_id = licensecustomer;
                            Globals.reg_code = registration_code;
                            Globals.projectid=project_id;
                       /* database.setTransactionSuccessful();
                        database.endTransaction();*/
                        } else {
                       /* database.setTransactionSuccessful();
                        database.endTransaction();*/
                        }
                    } else if (strStatus.equals("false")) {
                        succ = "5";
                        Globals.responsemessage = strMassage;
                    }


                } catch(JSONException e){
                Globals.AppLogWrite("Server Json exception"+ e.getMessage());

                    progressDialog.dismiss();
                }

        database.setTransactionSuccessful();
        database.endTransaction();
        return succ;
    }

   /* private String device_on_server() {
        String serverData = null;
        try {
          //
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
                    Globals.App_IP_URL + "device/register");
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
                Globals.AppLogWrite("Server namevaluepairs"+ nameValuePairs);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                serverData = EntityUtils.toString(httpEntity);
                Log.d("response", serverData);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            Globals.AppLogWrite("Server exception"+ e.getMessage());

            System.out.println(e.getMessage());
        }
        return serverData;
    }*/
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String registration_code,final String defaultlogout) {

        pDialog = new ProgressDialog(ActivateEmailActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
       /* HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                              JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                       // Globals.showToast(getApplicationContext(),message.toString(), Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                                        //   Globals.showToast(getApplicationContext(), message.toString(), Globals.txtSize, "#ffffff", "#4ec536", "short", Gravity.TOP, 0, 200);
                                    }
                                });


                                //  pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();


                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                           // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                          //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                          //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("is_use", isuse);
                params.put("master_product_id", masterproductid);
                params.put("lic_customer_license_id", liccustomerlicenseid);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("reg_code", registration_code);
                params.put("default_logout", defaultlogout);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
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
            //List.add("Reservation");
            List.add("Settings");
            List.add("Report");
            List.add("Tax");
            List.add("Unit");
            List.add("Database");
            List.add("Update License");
            List.add("Profile");
            List.add("Return");
            List.add("Accounts");
            List.add("User");




    }

    private String getLastCode(String serverData) {

String succ="0";


      try {
          database.beginTransaction();

          JSONObject collection_jsonObject1 = new JSONObject(serverData);
          String strStatus = collection_jsonObject1.getString("status");
          String massage = collection_jsonObject1.getString("message");

          if (strStatus.equals("true")) {

              JSONObject jsonObject = collection_jsonObject1.getJSONObject("result");

              String last_order_code = jsonObject.getString("last_order_code");
              String last_pos_balance_code = jsonObject.getString("last_pos_balance_code");
              String last_z_close_code = jsonObject.getString("last_z_close_code");
              String last_order_return_code = jsonObject.getString("last_order_return_code");

              long l = Last_Code.delete_Last_Code(getApplicationContext(), null, null, database);
              if (l > 0) {
              } else {
              }

              last_code = new Last_Code(getApplicationContext(), null, last_order_code, last_pos_balance_code, last_z_close_code, last_order_return_code);

              long d = last_code.insertLast_Code(database);

              if (d > 0) {

                  succ="1";
                  database.setTransactionSuccessful();
                  database.endTransaction();
              }
          } else {
              succ="2";
              database.endTransaction();
          }
      }
      catch (JSONException e){}
return succ;
    }

   /* private String getLastCodeFromServer(String liccustomerid,String regcode) {
        String serverData = null;//
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "last_code");
            ArrayList nameValuePairs = new ArrayList(8);
            nameValuePairs.add(new BasicNameValuePair("reg_code", regcode));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
            System.out.println("get last code" + nameValuePairs);
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
                System.out.println("get server last code" + serverData);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){

        }
        return serverData;
    }*/

    @Override
    public void onBackPressed() {

    }

    public void postDeviceRegister(final String email,final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4) {

        pDialog = new ProgressDialog(ActivateEmailActivity.this);
        pDialog.setMessage(getString(R.string.Activating));
        pDialog.setCancelable(false);
        pDialog.show();
        String server_url = Globals.App_IP_URL + "device/register";

        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                           result = device_process(response,pDialog);
                           Globals.AppLogWrite(result);
                           if(result.equals("1")){
                               try {
                                   if(Globals.projectid.equals("cloud")) {
                                       Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                                       if (last_code == null) {
                                           postLastCodeFromServer(pDialog, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);
                                           // getLastCode(Globals.license_id, Globals.reg_code);

                                       }
                                   }
                                   else{
                                       pDialog.dismiss();
                                       Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ActivateEmailActivity.this, LoginActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }

                               } catch (Exception e) {

                               }

                           }
                           else if(result.equals("4")){
                               pDialog.dismiss();
                               Toast.makeText(getApplicationContext(), R.string.locationserver, Toast.LENGTH_SHORT).show();

                           }
                           else if(result.equals("5")){
                               if (Globals.responsemessage.toString().equals("Purchase More Device")) {

                                   try {
                                       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ActivateEmailActivity.this);
                                       builder.setCancelable(false);
                                       builder.setTitle(getString(R.string.alerttitle));
                                       builder.setMessage(getString(R.string.alert_loginmsg));

                                       builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                                           public void onClick(DialogInterface dialog, int which) {
                                               // Do nothing but close the dialog
                                               pDialog.dismiss();
                                               getAlertclearTransactionDialog();

                                           }
                                       });

                                       builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                               // Do nothing
                                               pDialog.dismiss();
                                               dialog.dismiss();
                                           }
                                       });

                                       androidx.appcompat.app.AlertDialog alert = builder.create();
                                       alert.show();

                                   } catch (Exception e) {

                                   }
                               }
                               else{

                                   pDialog.dismiss();
                                   Toast.makeText(getApplicationContext(),Globals.responsemessage.toString(), Toast.LENGTH_SHORT).show();

                               }

                           }
                           else{
                               pDialog.dismiss();
                               Toast.makeText(getApplicationContext(),Globals.responsemessage.toString(), Toast.LENGTH_SHORT).show();

                           }

                        } catch (Exception e)
                        {
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(),e+"", Toast.LENGTH_SHORT).show();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                System.out.println("params" + params);
                //Globals.AppLogWrite("params"+params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
       // Globals.AppLogWrite("response"+result);
        //return result;
    }

    public void postLastCodeFromServer(final ProgressDialog pDialog,final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String licensecustomerid) {

      /*  pDialog = new ProgressDialog(ActivateEmailActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "last_code";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                           String result_lastcode= getLastCode(response);
                            getMinCalculation_fromserver(Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);


                        } catch (Exception e) {
                        }


                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("lic_customer_license_id", licensecustomerid);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getInvoiceParameter_fromserver(final ProgressDialog pDialog,String registration_code,String devicecode,String serial_no,String syscode2,String android_id,String myKey,String licensecustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "invoice_parameter";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getinvoiceparameter(response);

                            if(result.equals("1")) {


                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivateEmailActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();


                            }
                            else{
                                pDialog.dismiss();
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2",syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", licensecustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getMinCalculation_fromserver(String registration_code,String devicecode,String serial_no,String syscode2,String android_id,String myKey,String licensecustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
       // http://192.168.29.201/trigger-pos-ar/index.php/api/time_calculation
        String server_url = Globals.App_IP_URL + "time_calculation";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result =getMinCalculation(response);

                          //  if(result.equals("1")) {
                          //  }

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        getInvoiceParameter_fromserver(pDialog, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                          //  Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                         //   Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                          //  Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        getInvoiceParameter_fromserver(pDialog, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);

                    }


                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2",syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", licensecustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getinvoiceparameter(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        Lite_POS_Registration  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        Lite_POS_Device  lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(),"", database);

        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONObject jsonobj_bg = jsonObject_bp.getJSONObject("result");
                String gst_number=jsonobj_bg.getString("gst_number");
                String inv_address=jsonobj_bg.getString("invoice_address");
                String shortcompanyname=jsonobj_bg.getString("company_name");
                String industry_type=jsonobj_bg.getString("industry_type");
                String locationid=jsonobj_bg.getString("location_id");
                String locationname=jsonobj_bg.getString("location_name");

                lite_pos_device.setLocation_Code(locationid);
                lite_pos_device.setLocation_name(locationname);
                long dt = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
                if (dt > 0) {

                }
                lite_pos_registration.setAddress(inv_address);
                lite_pos_registration.setService_code_tariff(gst_number);
                lite_pos_registration.setShort_companyname(shortcompanyname);
                lite_pos_registration.setIndustry_Type(industry_type);
                long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                if(ct>0){
                    succ_manu="1";
                }


            } else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
            else{
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

    private String getMinCalculation(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here


        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_result = jsonObject_bp.optJSONArray("result");
                for (int j = 0; j < jsonArray_result.length(); j++) {
                    JSONObject jsonObject_result = jsonArray_result.getJSONObject(j);
                    String fromrange1 = jsonObject_result.getString("range_1");
                    String torange2 = jsonObject_result.getString("range_2");
                    String calculationvalue = jsonObject_result.getString("calculate_value");


                    MinCalculation min_calc = new MinCalculation(getApplicationContext(), null, fromrange1,
                            torange2, calculationvalue);


                    long k = min_calc.insertMinCalculation(database);
                    if (k > 0) {
                        succ_manu = "1";
                    }
                }
            }


             else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
            else{
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }
}
