package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


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
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.User;

import sunmi.ds.DSKernel;
import sunmi.ds.data.DataPacket;


public class LoginActivity extends AppCompatActivity {
    EditText edt_username, edt_userpass;
    TextView txt_forgot_pswd, txt_version;
    Button btn_login;
    Contact contact;
    Address address;
    ToggleButton tgl_btn_lang;
    int resultdate;
    Database db;
    Address_Lookup address_lookup;
    Contact_Bussiness_Group contact_bussiness_group;
    ReceipeModifier item_modifier;
    SQLiteDatabase database;
    User user;
    Lite_POS_Device lite_pos_device;
    String date, strEmail;
    Dialog listDialog2;
    ProgressDialog pDialog;
    String displayTilte;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    Unit unit;
    Item item;
    Item_Group itemgroup;
    JSONObject jsonObject;
    String user_id = "", user_pass = "";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    boolean doubleBackToExitPressedOnce = false;
    //    AESHelper aesHelper;
    JavaEncryption javaEncryption;
    Settings settings;
    SimpleDateFormat format;
    AlertDialog.Builder alertDialog;
    LinearLayout.LayoutParams lp;
    AlertDialog alert;
    Button nbutton;
    Button pbutton, btn_clear;
    Lite_POS_Registration lite_pos_registration;
    String status;
    String serial_no, android_id, myKey, device_id, imei_no;
    String email, password, reg_code;
    SharedPreferences.Editor edtor_user;
    SharedPreferences prefrences;
    String user_name;
    Item_Location item_location;
    Item_Supplier item_supplier;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    String company_email, company_password;
       Animation animFadeIn;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setTitle(R.string.Login);
        FirebaseCrashlytics.getInstance().recordException(new Throwable());
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
//        aesHelper = new AESHelper();
        javaEncryption = new JavaEncryption();
        new Thread() {
            @Override
            public void run() {
                checkversion();
            }
        }.start();


        txt_forgot_pswd = (TextView) findViewById(R.id.txt_forgot_pswd);
        txt_version = (TextView) findViewById(R.id.txt_version);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_userpass = (EditText) findViewById(R.id.edt_userpass);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        tgl_btn_lang = (ToggleButton) findViewById(R.id.tgl_btn_lang);

        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
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
      //  device_id = mTelephony.getDeviceId();
        //imei_no = telephonyManager.getImei();
        edt_username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_username.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_username.requestFocus();
                    edt_username.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_username, InputMethodManager.SHOW_IMPLICIT);
                    edt_username.selectAll();
                    return true;
                }
            }
        });

                try {
            Date d = new Date();
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            date = format.format(d);


            settings = Settings.getSettings(getApplicationContext(), database, "");
        } catch (Exception e) {

        }


        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText("Version : " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {

             lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);


        } catch (Exception e) {

        }

        try {
            if (lite_pos_device != null) {
                status = lite_pos_device.getStatus();
            }
        } catch (Exception e) {

        }
        try {
            if(lite_pos_registration!=null) {
                 company_email = lite_pos_registration.getEmail();
                company_password = lite_pos_registration.getPassword();
                Globals.Industry_Type = lite_pos_registration.getIndustry_Type();
                reg_code = lite_pos_registration.getRegistration_Code();
            }
        }
        catch(Exception e){

        }

       /* try{

            Globals.locname=Globals.locname;
            Toast.makeText(getApplicationContext(),Globals.locname, Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){}*/
        // 0 - for private mode
// 0 - for private mode
        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS);
        editor = pref.edit();

        int id = pref.getInt("id", 0);
        if (id == 0) {
            tgl_btn_lang.setChecked(false);
            getLocal("en");
        } else {
            tgl_btn_lang.setChecked(true);
            getLocal("ar");
        }
        item = Item.getItem(getApplicationContext(), "", database, db);


        prefrences = getApplicationContext().getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        edtor_user = prefrences.edit();

        if(item==null){
            edtor_user.clear();
            edtor_user.apply();

        }
        if (isNetworkStatusAvialable(getApplicationContext())) {
            try {
                String device_symbol = lite_pos_device.getDevice_Symbol();
                if (device_symbol.equals("") || device_symbol.equals("null")) {

                    postDeviceCheckLicense(Globals.reg_code, Globals.Device_Code, lite_pos_registration.getEmail(), serial_no, Globals.syscode2, android_id, myKey);
                    // check_device();

                } else if (lite_pos_device.getLocation_Code().equals("0")) {
               /* new Thread() {
                    @Override
                    public void run() {*/
                    postDeviceCheckLicense(Globals.reg_code, Globals.Device_Code, lite_pos_registration.getEmail(), serial_no, Globals.syscode2, android_id, myKey);

                    // check_device();
                   /* }
                }.start();*/
                }

            }catch(Exception e){}
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        try {
            showExpiry();
        } catch (Exception e) {

        }
        tgl_btn_lang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final Animation myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                btn_clear.startAnimation(myAnim);
                String strLang = tgl_btn_lang.getText().toString();
                if (strLang.equals("Arabic")) {
                    getLocal("en");
                    editor.putInt("id", 0);
                    editor.commit();

                    finish();

                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    getLocal("ar");
                    editor.putInt("id", 1);
                    editor.commit();
                    finish();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);

                }



            }
        });
       // ShowHidePass();
        edt_userpass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //ShowHidePass();


                return false;
            }
        });
        edt_username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        listDialog2 = new Dialog(this);
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        Globals.objLPD = lite_pos_device;
        user = User.getUser(getApplicationContext(), " WHERE user_code = '" + prefrences.getString(getString(R.string.usercode_pref), null) + "' or name = '" + prefrences.getString(getString(R.string.usercode_pref), null) + "' or email = '" + prefrences.getString(getString(R.string.usercode_pref), null) + "'  and is_active ='1'", database);
        if (user == null) {
            try {
                edt_username.setText(user.get_user_code());
                edt_userpass.setText(user.get_password());
                Globals.str_userpassword = user.get_password();
                Globals.user = user.get_user_code();
               /* email=user.get_email();
                password=user.get_password();*/
            } catch (Exception ex) {
            }
        } else {
            try {
                Globals.user = user.get_user_code();
                Globals.UserCode = Globals.user;
                Globals.str_userpassword = user.get_password();
                user_name = user.get_name();
                edt_username.setText(prefrences.getString(getString(R.string.usercode_pref), null));
                edt_userpass.setText(prefrences.getString(getString(R.string.pass_pref), null));
                email = user.get_email();
                password = user.get_password();
            } catch (Exception ex) {

            }
        }


        txt_forgot_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showdialogforgotpswddialog();
                } catch (Exception e) {

                }
            }
        });


        final Lite_POS_Device finalLite_pos_device = lite_pos_device;
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* final Animation myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                btn_login.startAnimation(myAnim);*/
              //  Globals.localelang=pref.getString("id",null);

               user = User.getUser(getApplicationContext(), " WHERE (user_code ='" + edt_username.getText().toString().trim() + "'or email = '" + edt_username.getText().toString().trim() + "'or name = '" + edt_username.getText().toString().trim() + "') and password ='" + edt_userpass.getText().toString().trim() + "' and is_active= '1'", database);
                if (user == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.credentialcheck), Toast.LENGTH_SHORT).show();
                    //  Globals.showToast(getApplicationContext(), getString(R.string.credentialcheck), Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                }


                else {
                    if (edt_username.getText().toString().equals("")) {
                        edt_username.setError(getString(R.string.Usercode_is_required));
                        edt_username.requestFocus();
                        return;
                    } else {
                        if (edt_username.getText().toString().contains(" ")) {
                            edt_username.setError(getString(R.string.Cant_Enter_Space));
                            edt_username.requestFocus();
                            return;
                        } else {
                            user_id = edt_username.getText().toString();
                        }
                    }

                    if (edt_userpass.getText().toString().equals("")) {
                        edt_userpass.setError(getString(R.string.Password_is_required));
                        edt_userpass.requestFocus();
                        return;
                    } else {
                        if (edt_userpass.getText().toString().contains(" ")) {
                            edt_userpass.setError(getString(R.string.Cant_Enter_Space));
                            edt_userpass.requestFocus();
                            return;
                        } else {
                            user_pass = edt_userpass.getText().toString();
                        }

                    }

                    if (user.get_app_user_permission().equals("null")) {

                        Toast.makeText(getApplicationContext(), "Unauthorized User", Toast.LENGTH_LONG).show();
                    } else {
                        Globals.localelang = tgl_btn_lang.getText().toString();
                        pDialog = new ProgressDialog(LoginActivity.this);
                        pDialog.setCancelable(false);
                        pDialog.setMessage(getString(R.string.msg_dilog_login));
                        pDialog.show();

                        Thread timerThread = new Thread() {
                            public void run() {
                                try {
                                    sleep(2000);
                                    String strEx;
                                    String ex_date = null;
                                    try {
                                        ex_date = javaEncryption.decrypt(Globals.objLPD.getExpiry_Date(), "12345678");
                                    } catch (Exception e) {
                                        try {
                                            lite_pos_device.setExpiry_Date(javaEncryption.encrypt(Globals.objLPD.getExpiry_Date(), "12345678"));
                                            lite_pos_device.updateDevice("Device_Code=?", new String[]{Globals.objLPD.getDevice_Code()}, database);
                                            ex_date = javaEncryption.decrypt(Globals.objLPD.getExpiry_Date(), "12345678");

                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    strEx = ex_date.substring(0, 10);
                                    String strDate = "";
                                    strDate = date.substring(0, 10);

                                    int result = strDate.compareTo(strEx);
                                    resultdate=strDate.compareTo(strEx);
                                    String device_symbol = finalLite_pos_device.getDevice_Symbol();
                                    if (device_symbol.equals("") || device_symbol.equals("null")) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Device symbol not present", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } /*else if (lite_pos_device.getLocation_Code().equals("0")) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Location not selected for this device", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }*/ else {

                          /*          if(Globals.objLPR.getIndustry_Type().equals("3")){

                                        if (status.equals("IN")) {
                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                            pDialog.dismiss();
                                            Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                            startActivity(intent_category);
                                        }
                                        else if (status.equals("Out")) {
                                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                            String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                            postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                        }

                                    }*/


                                        if(settings.getParam1().equals("true")) {

                                            pDialog.dismiss();
                                            if (lite_pos_device.getStatus().equals("IN")) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setTitle("Syncing");
                                                    pDialog.setMessage(getString(R.string.pleasewait));
                                                    pDialog.show();


                                                        if ((lite_pos_registration.getIndustry_Type().equals("1")) || (lite_pos_registration.getIndustry_Type().equals("2"))) {
                                                            item = Item.getItem(getApplicationContext(), "", database, db);
                                                            if (item != null) {
                                                                try {
                                                                    // result = send_online_item();
                                                                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                                                                    if (sys_sycntime == null) {
                                                                        get_item_from_server(pDialog, result, lite_pos_registration.getRegistration_Code(), lite_pos_device.getLocation_Code(), lite_pos_device.getLic_customer_license_id(), "", serial_no, android_id, myKey);
                                                                    } else {
                                                                        get_item_from_server(pDialog, result, lite_pos_registration.getRegistration_Code(), lite_pos_device.getLocation_Code(), lite_pos_device.getLic_customer_license_id(), sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                                                    }
                                                                } catch (Exception ex) {
                                                                    System.out.println(ex.getMessage());
                                                                }

                                                            }
                                                        } else if (lite_pos_registration.getIndustry_Type().equals("3")) {
                                                            try {
                                                                // result = send_online_contact(serialno,android,mykey);
                                                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                                                //Call get contact api here
                                                                if (sys_sycntime == null) {
                                                                    getcontact_from_server(lite_pos_registration.getRegistration_Code(), lite_pos_device.getLic_customer_license_id(), "", serial_no, android_id, myKey);
                                                                } else {
                                                                    getcontact_from_server(lite_pos_registration.getRegistration_Code(), lite_pos_device.getLic_customer_license_id(), sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                                                }


                                                            } catch (Exception ex) {
                                                            }

                                                        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {

                                                            login_parkingfunc(result);

                                                        }

                                                }


                                            });

                                             }

                                                   else if(lite_pos_device.getStatus().equals("Out")){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");
                                                    }});

                                                    }
                                        }
                                        else {
                                            if (lite_pos_registration.getIndustry_Type().equals("1")) {
                                                if (result < 0) {
                                                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                    if (user == null) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                pDialog.dismiss();
                                                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                edt_username.requestFocus();
                                                            }
                                                        });

                                                        return;
                                                    } else {

                                                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                        edtor_user.apply();
                                                        Globals.user = user.get_user_code();
                                                        Globals.username = user.get_name();
                                                        Globals.devicename = lite_pos_device.getDevice_Name();
                                                        Globals.UserCode = Globals.user;


                                                        // pDialog.dismiss();
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {

                                                                if (status.equals("IN")) {
                                                                    if (settings.get_Home_Layout().equals("0")) {
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, MainActivity.class);
                                                                        startActivity(intent_category);
                                                                    } else if (settings.get_Home_Layout().equals("2")) {
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, RetailActivity.class);
                                                                        startActivity(intent_category);
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, Main2Activity.class);
                                                                        startActivity(intent_category);
                                                                    }
                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }

                                                            }
                                                        });
                                                    }
                                                } else if (result == 0) {
                                                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                    if (user == null) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                pDialog.dismiss();
                                                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                edt_username.requestFocus();
                                                            }
                                                        });

                                                        return;
                                                    } else {
                                                        prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                                        edtor_user = prefrences.edit();
                                                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                                        edtor_user.apply();

                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                if (status.equals("IN")) {
                                                                    if (settings.get_Home_Layout().equals("0")) {
                                                                        Globals.user = user.get_user_code();
                                                                        Globals.UserCode = Globals.user;
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, MainActivity.class);
                                                                        startActivity(intent_category);
                                                                    } else if (settings.get_Home_Layout().equals("2")) {
                                                                        Globals.user = user.get_user_code();
                                                                        Globals.UserCode = Globals.user;
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, RetailActivity.class);
                                                                        startActivity(intent_category);
                                                                    } else {
                                                                        Globals.user = user.get_user_code();
                                                                        Globals.UserCode = Globals.user;
                                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        Intent intent_category = new Intent(LoginActivity.this, Main2Activity.class);
                                                                        startActivity(intent_category);
                                                                    }
                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            } else if (lite_pos_registration.getIndustry_Type().equals("2")) {
                                                if (result < 0) {
                                                    runOnUiThread(new Runnable() {


                                                        public void run() {


                                                            user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                            if (user == null) {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        pDialog.dismiss();
                                                                        edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                        edt_username.requestFocus();
                                                                    }
                                                                });

                                                                return;
                                                            } else {

                                                                edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                                edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                                edtor_user.apply();
                                                                Globals.user = user.get_user_code();
                                                                Globals.username = user.get_name();
                                                                Globals.devicename = lite_pos_device.getDevice_Name();
                                                                Globals.UserCode = Globals.user;
                                                                if (status.equals("IN")) {
                                                                    //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, Retail_IndustryActivity.class);
                                                                    startActivity(intent_category);
                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        }
                                                    });

                                                } else if (result == 0) {
                                                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                    if (user == null) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                pDialog.dismiss();
                                                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                edt_username.requestFocus();
                                                            }
                                                        });

                                                        return;
                                                    } else {
                                                        prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                                        edtor_user = prefrences.edit();
                                                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                                        edtor_user.apply();

                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                if (status.equals("IN")) {

                                                                    Globals.user = user.get_user_code();
                                                                    Globals.UserCode = Globals.user;
                                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, Retail_IndustryActivity.class);
                                                                    startActivity(intent_category);


                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            } else if (lite_pos_registration.getIndustry_Type().equals("3")) {
                                                if (result < 0) {
                                                    runOnUiThread(new Runnable() {


                                                        public void run() {


                                                            user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                            if (user == null) {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        pDialog.dismiss();
                                                                        edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                        edt_username.requestFocus();
                                                                    }
                                                                });

                                                                return;
                                                            } else {

                                                                edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                                edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                                edtor_user.apply();
                                                                Globals.user = user.get_user_code();
                                                                Globals.username = user.get_name();
                                                                Globals.devicename = lite_pos_device.getDevice_Name();
                                                                Globals.UserCode = Globals.user;
                                                                if (status.equals("IN")) {
                                                                    //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                                                    intent_category.putExtra("whatsappFlag", "false");
                                                                    intent_category.putExtra("contact_code", "");
                                                                    startActivity(intent_category);
                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        }
                                                    });

                                                } else if (result == 0) {
                                                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                    if (user == null) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                pDialog.dismiss();
                                                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                edt_username.requestFocus();
                                                            }
                                                        });

                                                        return;
                                                    } else {
                                                        prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                                        edtor_user = prefrences.edit();
                                                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                                        edtor_user.apply();

                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                if (status.equals("IN")) {

                                                                    Globals.user = user.get_user_code();
                                                                    Globals.UserCode = Globals.user;
                                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                                                    intent_category.putExtra("whatsappFlag", "false");
                                                                    intent_category.putExtra("contact_code", "");
                                                                    startActivity(intent_category);


                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
                                                if (result < 0) {
                                                    runOnUiThread(new Runnable() {


                                                        public void run() {


                                                            user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                            if (user == null) {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        pDialog.dismiss();
                                                                        edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                        edt_username.requestFocus();
                                                                    }
                                                                });

                                                                return;
                                                            } else {
                                                                item = Item.getItem(getApplicationContext(), "", database, db);
                                                                if (item == null) {

                                                                    read_item();
                                                                    try {
                                                                        long l = Unit.deleteUnit(getApplicationContext(), null, null, database);
                                                                        if (l > 0) {
                                                                            read_Unit();
                                                                        } else {
                                                                        }
                                                                    } catch (Exception e) {

                                                                    }

                                                                    // read_itemgroup();

                                                                }
                                                                itemgroup = Item_Group.getItem_Group(getApplicationContext(), database, db, "");
                                                                if (item_group == null) {
                                                                    read_itemgroup();
                                                                }
                                                                edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                                edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                                edtor_user.apply();
                                                                Globals.user = user.get_user_code();
                                                                Globals.username = user.get_name();
                                                                Globals.devicename = lite_pos_device.getDevice_Name();
                                                                Globals.UserCode = Globals.user;
                                                                if (status.equals("IN")) {
                                                                    //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, ParkingIndustryActivity.class);
                                                                    startActivity(intent_category);
                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        }
                                                    });

                                                } else if (result == 0) {
                                                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                    if (user == null) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                pDialog.dismiss();
                                                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                                edt_username.requestFocus();
                                                            }
                                                        });

                                                        return;
                                                    } else {
                                                        prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                                        edtor_user = prefrences.edit();
                                                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                                        edtor_user.apply();
                                                        item = Item.getItem(getApplicationContext(), "", database, db);
                                                        if (item == null) {

                                                            read_item();
                                                            //read_itemgroup();

                                                        }
                                                        itemgroup = Item_Group.getItem_Group(getApplicationContext(), database, db, "");
                                                        if (item_group == null) {
                                                            read_itemgroup();
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                if (status.equals("IN")) {

                                                                    Globals.user = user.get_user_code();
                                                                    Globals.UserCode = Globals.user;
                                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                                    pDialog.dismiss();
                                                                    Intent intent_category = new Intent(LoginActivity.this, ParkingIndustryActivity.class);
                                                                    startActivity(intent_category);


                                                                } else if (status.equals("Out")) {
                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                                    String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    }

                                } catch (
                                        InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        timerThread.start();
                    }
                }
            }

        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Animation myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
                    btn_clear.startAnimation(myAnim);
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);

                    builder.setTitle(getString(R.string.alerttitle));
                    builder.setMessage(getString(R.string.alertmsg) + user_name);

                    builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            edtor_user.clear();
                            edtor_user.commit();
                            edt_username.setText(prefrences.getString(getString(R.string.pass_pref), ""));
                            edt_userpass.setText(prefrences.getString(getString(R.string.usercode_pref), ""));
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

                } catch (Exception e) {

                }

            }

        });
    }

    private void showExpiry() {

        String strEx = "";
        // = "2020-05-16 10:67:09";
        String ex_date = null;
        try {
            ex_date = javaEncryption.decrypt(lite_pos_device.getExpiry_Date(), "12345678");
        } catch (Exception e) {
            try {
                lite_pos_device.setExpiry_Date(javaEncryption.encrypt(Globals.objLPD.getExpiry_Date(), "12345678"));
                lite_pos_device.updateDevice("Device_Code=?", new String[]{Globals.objLPD.getDevice_Code()}, database);
                ex_date = javaEncryption.decrypt(lite_pos_device.getExpiry_Date(), "12345678");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        Date dateStart = null, dateEnd = null;
        try {
            dateStart = format.parse(ex_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            dateEnd = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = dateStart.getTime() - dateEnd.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days <= 10 && days > 0) {
            showdialog(days);
            // Toast.makeText(getApplicationContext(),"After"+days+" days you license will expire",Toast.LENGTH_LONG);
        }
    }

    private void showdialog(long days) {
        alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle("Alert!");
        alertDialog
                .setMessage(days + " days left your license will be expired.");

        lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        alertDialog.setNegativeButton(R.string.Ok,

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });

//        alertDialog.setPositiveButton(R.string.Ok,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//
//                    }
//                });

        alert = alertDialog.create();
        alert.show();

        nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showdialogforgotpswddialog() {
        listDialog2.setTitle(getString(R.string.Forgot_Password));
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        edt_remark.setHint(R.string.Enter_Email);
        edt_remark.setHintTextColor(Color.parseColor("#cccccc"));
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
        btnButton.setText(R.string.Send);
        btnButton.setTextColor(Color.parseColor("#ffffff"));
        listDialog2.show();
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog2.dismiss();
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_remark.getText().toString().equals("")) {
                    edt_remark.setError(getString(R.string.Enter_Email));
                    edt_remark.requestFocus();
                    return;
                } else {
                    strEmail = edt_remark.getText().toString().trim();
                    if (!isValidEmail(strEmail)) {
                        edt_remark.setError(getString(R.string.Invalid_Email));
                        edt_remark.requestFocus();
                        return;
                    }
                }
                try {

                    //send_email(strEmail);
                    if(lite_pos_registration.getproject_id().equals("standalone")) {
                        if (strEmail.equals(lite_pos_registration.getEmail())) {
                            postSendEmail(lite_pos_registration.getRegistration_Code(), strEmail);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "User Don't have access. Please contact Manager", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        postSendEmail(lite_pos_registration.getRegistration_Code(), strEmail);

                    }
                } catch (Exception e) {

                }
                listDialog2.dismiss();
            }
        });


    }

/*    private void send_email(final String strEmail) {
        if (isNetworkStatusAvialable(getApplicationContext())) {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            new Thread() {
                @Override
                public void run() {
                    // Get item group from server
                    try {
                        send_email_server(strEmail, pDialog);
                    } catch (Exception e) {

                    }
                }
            }.start();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }*/

    private void send_email_server(String serverData, final ProgressDialog pDialog) {
        String msg = "";
        // Call get item group api here



        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            msg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                final String finalMsg = msg;
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                final String finalMsg = msg;
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (JSONException e) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

   /* private String send_email_on_server(String strEmail) {

        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
                    Globals.App_IP_URL + "email");
            ArrayList nameValuePairs = new ArrayList(5);
            nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("email", strEmail));
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
        } catch (Exception e) {

        }
        return serverData;

    }*/

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String flag = "login";
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.putExtra("flag", flag);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_exit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.PressAgainexit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void getLocal(String lang) {
        Locale locale = null;
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getResources().updateConfiguration(config, null);
        onConfigurationChanged(config);
    }

    private void check_device(String serverData) {
        // Call check license api here


        try {
            final JSONObject jsonObject1 = new JSONObject(serverData);
            final String strStatus = jsonObject1.getString("status");
            final String msg = jsonObject1.getString("message");
            if (strStatus.equals("true")) {
                try {
                    database.beginTransaction();
                    JSONObject jsonObject_result = jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);

                    lite_pos_device = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN",jsonObject_device.getString("location_name"));

                    long d = lite_pos_device.insertDevice(database);
                    if (d > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    }

                } catch (JSONException jex) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (strStatus.equals("false")) {
                try {
                    database.beginTransaction();
                    JSONObject jsonObject_result = jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    long l = Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                    lite_pos_device = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN",jsonObject_device.getString("location_name"));

                    long d = lite_pos_device.insertDevice(database);

                    if (d > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    }

                } catch (JSONException jex) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
        }
    }

 /*   private String Update_licence_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "device/check_license");
        ArrayList nameValuePairs = new ArrayList(7);
        try {
            nameValuePairs.add(new BasicNameValuePair("email", lite_pos_registration.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", "4"));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));

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
*/
    private void checkversion() {
        String ltversion = "1";

        // Call get item api here

        String serverData = check_version_server();
        if (serverData == null) {
        } else {
            try {


                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    Globals.Version_Name = pInfo.versionName;
                    Globals.Version_Code = pInfo.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                //get the lastest application URI from the JSON string
                final String appPackageName = getPackageName();

                //check if we need to upgrade?
                Double playVersionCode = Double.parseDouble(serverData);
                if (playVersionCode > Double.parseDouble(Globals.Version_Name)) {
                    showInDayOnce(appPackageName);
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }

            } catch (Exception e) {

            }
            return;
        }

    }

    private String check_version_server() {
        String newVersion = null;

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "org.phomellolitepos" + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    void showInDayOnce(final String appPackageName) {
        boolean showInDayOnce;

        //return date in yyyy_MM_dd
        String currentDate = DateUtill.currentDateWithoutTime();
        SessionManeger sessionManeger = new SessionManeger(this);
        String sessionDate = sessionManeger.getDate();

        showInDayOnce = sessionDate != null && sessionDate.equals(currentDate);
        if (!showInDayOnce) {
            sessionManeger.setDate(currentDate);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoginActivity.this);
            alertDialog.setTitle(R.string.update);
            alertDialog
                    .setMessage(R.string.updateavailable);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            alertDialog.setPositiveButton(R.string.Ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                                        + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                        .parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });

            alertDialog.setNegativeButton(R.string.Cancel,

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });


            AlertDialog alert = alertDialog.create();
            alert.show();

            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


        } else {

        }
    }

    public void postcheck(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String registration_code,final String defaultlogout) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
        /*HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


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
    class SessionManeger {

        private final String APP_SHARED_PREFS = "MyPref";//SsManager.class.getSimpleName(); //  Name of the file -.xml
        private SharedPreferences _sharedPrefs;
        private SharedPreferences.Editor _prefsEditor;
        private String CURRENT_DATE = "currentDate";

        public SessionManeger(Context context) {
            this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
            this._prefsEditor = _sharedPrefs.edit();
            _prefsEditor.commit();
        }

        public String getDate() {
            return _sharedPrefs.getString(CURRENT_DATE, null);
        }

        public void setDate(String currentDate) {
            _prefsEditor.putString(CURRENT_DATE, currentDate);
            _prefsEditor.commit();
        }
    }

    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String registrationcode, final String defaultlogout) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Logging In....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
       // HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);
                     Globals.AppLogWrite("Login response "+response.toString());

                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();

                                JSONObject result2 = responseObject.optJSONObject("result");
                                //  for (int i = 0; i < result2.length(); i++) {
                                // JSONObject jobj= result2.getJSONObject(i);
                                String lic_custid = result2.getString("lic_customer_license_id");
                                String oc_custid = result2.getString("oc_customer_id");
                                String lic_product_id = result2.getString("lic_product_id");
                                String lic_code = result2.getString("lic_code");
                                String license_key = result2.getString("license_key");
                                String licenseType = result2.getString("license_type");
                                String deviceCode = result2.getString("device_code");
                                String locationId = result2.getString("location_id");
                                String deviceName = result2.getString("device_name");
                                String deviceSymbol = result2.getString("device_symbol");
                                String registrationDate = result2.getString("registration_date");
                                String expiryDate = result2.getString("expiry_date");
                                String isActive = result2.getString("is_active");
                                String isUse = result2.getString("is_use");
                                String isUpdate = result2.getString("is_update");
                                String modifiedBy = result2.getString("modified_by");
                                String modifiedDate = result2.getString("modified_date");

                               /* boolean recordExists = db.checkIfDeviceRecordExist("DeviceInformation");
                                if (recordExists == true) {

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                                 }*/

                                try {
                                    lite_pos_device.setStatus("IN");
                                    lite_pos_device.setDevice_Id(lic_custid);
                                    lite_pos_device.setLic_customer_license_id(lic_custid);
                                    lite_pos_device.setDevice_Code(deviceCode);
                                    lite_pos_device.setDevice_Symbol(deviceSymbol);
                                    lite_pos_device.setDevice_Name(deviceName);
                                    lite_pos_device.setLicense_type(licenseType);
                                    lite_pos_device.setLicense_key(license_key);
                                    lite_pos_device.setLic_code(lic_code);
                                    lite_pos_device.setExpiry_Date(expiryDate);
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"Out"}, database);
                                    // long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {
                                        if(settings.getParam1().equals("true")) {

                                            if (lite_pos_device.getStatus().equals("IN")) {
                                                if ((lite_pos_registration.getIndustry_Type().equals("1")) || (lite_pos_registration.getIndustry_Type().equals("2"))) {
                                                    item = Item.getItem(getApplicationContext(), "", database, db);
                                                    if (item != null) {
                                                        try {
                                                            // result = send_online_item();
                                                            Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                                                            if (sys_sycntime == null) {
                                                                get_item_from_server(pDialog, resultdate, lite_pos_registration.getRegistration_Code(), lite_pos_device.getLocation_Code(), lite_pos_device.getLic_customer_license_id(), "", serial_no, android_id, myKey);
                                                            } else {
                                                                get_item_from_server(pDialog, resultdate, lite_pos_registration.getRegistration_Code(), lite_pos_device.getLocation_Code(), lite_pos_device.getLic_customer_license_id(), sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                                            }
                                                        } catch (Exception ex) {
                                                            System.out.println(ex.getMessage());
                                                        }

                                                    }
                                                } else if (lite_pos_registration.getIndustry_Type().equals("3")) {
                                                    try {
                                                        // result = send_online_contact(serialno,android,mykey);
                                                        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                                        //Call get contact api here
                                                        if (sys_sycntime == null) {
                                                            getcontact_from_server(lite_pos_registration.getRegistration_Code(), lite_pos_device.getLic_customer_license_id(), "", serial_no, android_id, myKey);
                                                        } else {
                                                            getcontact_from_server(lite_pos_registration.getRegistration_Code(), lite_pos_device.getLic_customer_license_id(), sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                                        }


                                                    } catch (Exception ex) {
                                                    }

                                                } else if (lite_pos_registration.getIndustry_Type().equals("4")) {

                                                    login_parkingfunc(resultdate);

                                                }
                                            }
                                        }
                                        else {
                                            if (lite_pos_registration.getIndustry_Type().equals("1")) {
                                                if (settings.get_Home_Layout().equals("0")) {
                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                    pDialog.dismiss();
                                                    Intent intent_category = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent_category);
                                                } else if (settings.get_Home_Layout().equals("2")) {
                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                    pDialog.dismiss();
                                                    Intent intent_category = new Intent(LoginActivity.this, RetailActivity.class);
                                                    startActivity(intent_category);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                    pDialog.dismiss();
                                                    Intent intent_category = new Intent(LoginActivity.this, Main2Activity.class);
                                                    startActivity(intent_category);
                                                }
                                            } else if (lite_pos_registration.getIndustry_Type().equals("2")) {
                                                Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                                Intent intent_category = new Intent(LoginActivity.this, Retail_IndustryActivity.class);
                                                startActivity(intent_category);
                                            } else if (lite_pos_registration.getIndustry_Type().equals("3")) {
                                                Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                                Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                                intent_category.putExtra("whatsappFlag", "false");
                                                intent_category.putExtra("contact_code", "");
                                                startActivity(intent_category);
                                            } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
                                                Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                                Intent intent_category = new Intent(LoginActivity.this, ParkingIndustryActivity.class);
                                                startActivity(intent_category);
                                            }
                                        }
                                    } else {
                                        /*database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (Exception e) {

                                }

                                //  }

                               // pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                                if (message.toString().equalsIgnoreCase("Purchase more device")) {

                                    try {
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);

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

                                        androidx.appcompat.app.AlertDialog alert = builder.create();
                                        alert.show();

                                    } catch (Exception e) {
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                            Globals.AppLogWrite("Login response error "+response.toString());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {

                        }

                        Globals.AppLogWrite("Login response "+error.toString());
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
                params.put("reg_code", registrationcode);
                params.put("default_logout", defaultlogout);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getAlertclearTransactionDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getString(R.string.alerttitle));
        builder.setMessage(getString(R.string.alert_loginseconddialog));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {

                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "1");

                    //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
                } catch (Exception e) {

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

    public void postDeviceCheckLicense(final String registration_code,final String devicecode,final String email,final String syscode1,final String syscode2,final String syscode3,final String syscode4) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();
        String server_url= Globals.App_IP_URL + "device/check_license";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            check_device(response);
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
                params.put("email", email);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void postSendEmail(final String registration_code,final String email) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getString(R.string.sending));
        pDialog.show();
        String server_url = Globals.App_IP_URL + "email";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            send_email_server(response, pDialog);
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
                params.put("email", email);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void read_item() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.parkingitem)));

            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                JSONObject jsonObject_item = new JSONObject(sb.toString());

                JSONArray jsonArray_bg = jsonObject_item.getJSONArray("result");
                ArrayList<Item> itlist = new ArrayList<Item>();
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    }

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        //path = getPath(context, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {
                        // itlist.add(new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null));
                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            // succ_bg = "1";
                        } else {
                        }
                        /*try {
                            item.add_item(itlist, database);
                        } catch (Exception e) {

                        }*/
                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.insertItem_Location(database);

                            if (itmlc > 0) {
                                //succ_bg = "1";

                            } else {
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                // succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");

                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);
                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                //succ_bg = "1";
                            } else {
                            }
                        }

                    }

                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
    }


    private void read_itemgroup() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {

                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.parkingcategory)));

            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                JSONObject jsonObject_itemgroup = new JSONObject(sb.toString());

                JSONArray jsonArray_bg = jsonObject_itemgroup.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item_group'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    }

                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N","");
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            // succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y","");
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            //succ_bg = "1";
                        } else {
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void read_Unit() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.defaulltparking)));

            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                JSONObject jsonObject_default = new JSONObject(sb.toString());
            JSONArray jsonArray_unit = jsonObject_default.getJSONArray("unit");
            ArrayList<Unit> unitArrayList = new ArrayList<Unit>();
            Unit unitobj = new Unit(getApplicationContext(), "", "","","","","","","");
            for (int i = 0; i < jsonArray_unit.length(); i++) {

                JSONObject jsonObject_unit1 = jsonArray_unit.getJSONObject(i);
                unit = new Unit(getApplicationContext(),
                        jsonObject_unit1.getString("unit_id"),
                        jsonObject_unit1.getString("name"),
                        jsonObject_unit1.getString("code"),
                        jsonObject_unit1.getString("description"),
                        jsonObject_unit1.getString("is_active"),
                        jsonObject_unit1.getString("modified_by"),
                        date, "N");

                unitArrayList.add(unit);
                //unit.insertUnit(database);
            }

            try{
                unitobj.add_unit(unitArrayList,database);
            }
            catch(Exception e){

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        }
    }

    public void ShowHidePass() {


            if(edt_userpass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                edt_userpass.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.eye_hide, 0);
                //Show Password
                edt_userpass.setCompoundDrawablePadding(10);
                edt_userpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                edt_userpass.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.eye_visible, 0);
                //Hide Password
                edt_userpass.setCompoundDrawablePadding(10);
                edt_userpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

    }

    public void get_item_from_server(final ProgressDialog pDialog,int result,final String regcode,final String locationcode,final String licensecustomerid,final String datetime,final String serial_no,final String android_id,final String myKey) {


        String server_url = Globals.App_IP_URL + "item";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String resultitem = getitem(response);

                            if(resultitem.equals("1")){
                                try {
                                    // result = send_online_contact(serialno,android,mykey);
                                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                    //Call get contact api here
                                    if (sys_sycntime == null) {
                                        getcontact_from_server(regcode,licensecustomerid,"", serial_no, android_id, myKey);
                                    } else {
                                        getcontact_from_server(regcode,licensecustomerid,sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                    }


                                } catch (Exception ex) {
                                }

                                try {
                                    getInvoiceParameter_fromserver(pDialog,result,regcode,serial_no,android_id,myKey, licensecustomerid);
                                }
                                catch(Exception e){

                                }




                            }

                        } catch(Exception e){
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
                        // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",regcode);
                params.put("modified_date", datetime);
                params.put("location_id", locationcode);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id",  licensecustomerid);


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private String getitem(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();


        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmsg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                ArrayList<Item> itlist = new ArrayList<Item>();
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    }

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        path = getPath(getApplicationContext(), myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {
                        // itlist.add(new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null));
                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code").toString().trim(), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name").toString().trim(), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku").toString().trim(), jsonObject_bg1.getString("barcode").toString().trim(), jsonObject_bg1.getString("hsn_sac_code").toString().trim(), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                        /*try {
                            item.add_item(itlist, database);
                        } catch (Exception e) {

                        }*/
                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.insertItem_Location(database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                            } else {
                            }
                        }




                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");

                        for (int it = 0;it< json_item_tax.length(); it++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(it);
                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_modifier = jsonObject_bg1.getJSONArray("item_modifier");

                        for (int j = 0; j < json_item_modifier.length(); j++) {
                            JSONObject jsonObject_item_modifier = json_item_modifier.getJSONObject(j);
                            item_modifier = new ReceipeModifier(getApplicationContext(), null, jsonObject_item_modifier.getString("item_code"), jsonObject_item_modifier.getString("modifier_code"));
                            long itmlc = item_modifier.insertReceipemodifier(database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                            } else {
                            }
                        }


                    } else {
                        item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"),null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.updateItem("item_code=?", new String[]{item_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            String loc_id = jsonObject_item_location.getString("location_id");
                            String itm_lc_id = jsonObject_item_location.getString("item_location_id");
                            item_location = Item_Location.getItem_Location(getApplicationContext()  , "WHERE item_code ='" + item_code + "'", database);
                            if (item_location == null) {
                                item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.insertItem_Location(database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            } else {
                                item_location = new Item_Location(getApplicationContext(), item_location.get_item_location_id(), jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.updateItem_Location("item_code=? And item_location_id=? ", new String[]{item_code, item_location.get_item_location_id()}, database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");
                        // item_supplier = Item_Supplier.getItem_Supplier(getApplicationContext(), "WHERE item_code ='" + item_code + "' and  contact_code = '"+ jsonObject_item_supplier.getString("contact_code").toString() +"'", database);
                        long l3 = Item_Supplier.delete_Item_Supplier(getApplicationContext(), "item_code =?", new String[]{item_code}, database);

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            // Here wee will process loop according o json data For example : 2
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            //getting item supplier infor from item code two records
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        long ll = Item_Group_Tax.delete_Item_Group_Tax(getApplicationContext(), "item_group_code =?", new String[]{item_code}, database);
                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");
                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);

                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }
                    }
                }
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmsg;


            }
            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e
        ) {
            succ_bg = "2";
            database.endTransaction();
        }

        return succ_bg;
    }

    @SuppressLint("NewApi")
    public String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }



    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getContact(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here

        try {
            final JSONObject jsonObject_contact = new JSONObject(serverData);
            final String strStatus = jsonObject_contact.getString("status");
            final String strmsg = jsonObject_contact.getString("message");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");
                    if (sys_sycntime!=null){
                        sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    }


                    if (contact == null) {
                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new Address(getApplicationContext(), null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                long itmsp = address.insertAddress(database);

                                if (itmsp > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }


                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);
                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");

                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    } else {

                        // Edit on 18-Oct-2017
                        contact = new Contact(getApplicationContext(), contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));

                        long l = contact.updateContact("contact_code=? And contact_id=?", new String[]{contact_code, contact.get_contact_id()}, database);

                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = Address.getAddress(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);
                                if (address != null) {
                                    address = new Address(getApplicationContext(), address.get_address_id(), jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                    long itmsp = address.updateAddress("address_code=? And address_id=?", new String[]{contact_code, address.get_address_id()}, database);

                                    if (itmsp > 0) {
                                        succ_bg = "1";

                                    } else {
                                        succ_bg = "0";
                                    }
                                }
                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");
                            long g = address_lookup.delete_Address_Lookup(getApplicationContext(), "address_code=?", new String[]{contact_code}, database);
                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);

                                address_lookup = Address_Lookup.getAddress_Lookup(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);


                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");
                            long c = contact_bussiness_group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code=?", new String[]{contact_code});
                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    }
                }
            }
            else if (strStatus.equals("false")) {

                succ_bg="3";
                Globals.responsemessage=strmsg;
            }




            else {
                succ_bg = "0";
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }


    public void getcontact_from_server(final String regcode,final String licensecustomerid,final String datetime,final String serial_no,final String android_id,final String myKey) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "contact";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getContact(response);

                            if(result.equals("1")) {
                                if(lite_pos_registration.getIndustry_Type().equals("3")){
                                try {
                                    getInvoiceParameter_fromserver(pDialog, resultdate, regcode, serial_no, android_id, myKey, licensecustomerid);
                                } catch (Exception e) {

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
                params.put("reg_code",regcode);
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id",  licensecustomerid);


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getInvoiceParameter_fromserver(final ProgressDialog pDialog, int result,final String regcode,String serial_no,String android_id,String myKey,String liccustomerid) {

        String server_url = Globals.App_IP_URL + "invoice_parameter";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String resultinv = getinvoiceparameter(response);
                            if(resultinv.equals("1")){
                                Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                lite_pos_device.setStatus("Out");
                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                if (ct > 0) {
                                    // database.endTransaction();
                                    try {

                                        pDialog.dismiss();
                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "1");

                                        //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            else if(resultinv.equals("2")){
                                Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                if (lite_pos_registration.getIndustry_Type().equals("1")) {
                                    if (result < 0) {
                                        user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                        if (user == null) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pDialog.dismiss();
                                                    edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                    edt_username.requestFocus();
                                                }
                                            });

                                            return;
                                        } else {

                                            edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                            edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                            edtor_user.apply();
                                            Globals.user = user.get_user_code();
                                            Globals.username = user.get_name();
                                            Globals.devicename = lite_pos_device.getDevice_Name();
                                            Globals.UserCode = Globals.user;


                                            // pDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                    if (status.equals("IN")) {
                                                        if (settings.get_Home_Layout().equals("0")) {
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent_category);
                                                        } else if (settings.get_Home_Layout().equals("2")) {
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, RetailActivity.class);
                                                            startActivity(intent_category);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, Main2Activity.class);
                                                            startActivity(intent_category);
                                                        }
                                                    } else if (status.equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }

                                                }
                                            });
                                        }
                                    } else if (result == 0) {
                                        user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                        if (user == null) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pDialog.dismiss();
                                                    edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                    edt_username.requestFocus();
                                                }
                                            });

                                            return;
                                        } else {
                                            prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                            edtor_user = prefrences.edit();
                                            edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                            edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                            edtor_user.apply();

                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if (lite_pos_device.getStatus().equals("IN")) {
                                                        if (settings.get_Home_Layout().equals("0")) {
                                                            Globals.user = user.get_user_code();
                                                            Globals.UserCode = Globals.user;
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent_category);
                                                        } else if (settings.get_Home_Layout().equals("2")) {
                                                            Globals.user = user.get_user_code();
                                                            Globals.UserCode = Globals.user;
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, RetailActivity.class);
                                                            startActivity(intent_category);
                                                        } else {
                                                            Globals.user = user.get_user_code();
                                                            Globals.UserCode = Globals.user;
                                                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                            pDialog.dismiss();
                                                            Intent intent_category = new Intent(LoginActivity.this, Main2Activity.class);
                                                            startActivity(intent_category);
                                                        }
                                                    } else if (lite_pos_device.getStatus().equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } else if (lite_pos_registration.getIndustry_Type().equals("2")) {
                                    if (result < 0) {
                                        runOnUiThread(new Runnable() {


                                            public void run() {


                                                user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                if (user == null) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                            edt_username.requestFocus();
                                                        }
                                                    });

                                                    return;
                                                } else {

                                                    edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                    edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                    edtor_user.apply();
                                                    Globals.user = user.get_user_code();
                                                    Globals.username = user.get_name();
                                                    Globals.devicename = lite_pos_device.getDevice_Name();
                                                    Globals.UserCode = Globals.user;
                                                    if (lite_pos_device.getStatus().equals("IN")) {
                                                        //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                        pDialog.dismiss();
                                                        Intent intent_category = new Intent(LoginActivity.this, Retail_IndustryActivity.class);
                                                        startActivity(intent_category);
                                                    } else if (lite_pos_device.getStatus().equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }
                                                }
                                            }
                                        });

                                    } else if (result == 0) {
                                        user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                        if (user == null) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pDialog.dismiss();
                                                    edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                    edt_username.requestFocus();
                                                }
                                            });

                                            return;
                                        } else {
                                            prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                            edtor_user = prefrences.edit();
                                            edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                            edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                            edtor_user.apply();

                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if (status.equals("IN")) {

                                                        Globals.user = user.get_user_code();
                                                        Globals.UserCode = Globals.user;
                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                        pDialog.dismiss();
                                                        Intent intent_category = new Intent(LoginActivity.this, Retail_IndustryActivity.class);
                                                        startActivity(intent_category);


                                                    } else if (status.equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                                else if (lite_pos_registration.getIndustry_Type().equals("3")) {
                                    if (result < 0) {
                                        runOnUiThread(new Runnable() {


                                            public void run() {


                                                user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                                if (user == null) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            pDialog.dismiss();
                                                            edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                            edt_username.requestFocus();
                                                        }
                                                    });

                                                    return;
                                                } else {

                                                    edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                                    edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                                                    edtor_user.apply();
                                                    Globals.user = user.get_user_code();
                                                    Globals.username = user.get_name();
                                                    Globals.devicename = lite_pos_device.getDevice_Name();
                                                    Globals.UserCode = Globals.user;
                                                    if (status.equals("IN")) {
                                                        //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                        pDialog.dismiss();
                                                        Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                                        intent_category.putExtra("whatsappFlag", "false");
                                                        intent_category.putExtra("contact_code", "");
                                                        startActivity(intent_category);
                                                    } else if (status.equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }
                                                }
                                            }
                                        });

                                    } else if (result == 0) {
                                        user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                                        if (user == null) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pDialog.dismiss();
                                                    edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                                    edt_username.requestFocus();
                                                }
                                            });

                                            return;
                                        } else {
                                            prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                            edtor_user = prefrences.edit();
                                            edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                                            edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                                            edtor_user.apply();

                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if (status.equals("IN")) {

                                                        Globals.user = user.get_user_code();
                                                        Globals.UserCode = Globals.user;
                                                        Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                                                        pDialog.dismiss();
                                                        Intent intent_category = new Intent(LoginActivity.this, PaymentCollection_MainScreen.class);
                                                        intent_category.putExtra("whatsappFlag", "false");
                                                        intent_category.putExtra("contact_code", "");
                                                        startActivity(intent_category);


                                                    } else if (status.equals("Out")) {
                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                                                        String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                                                        postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
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
                params.put("reg_code",regcode);
                params.put("device_code", Globals.Device_Code);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", liccustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private String getinvoiceparameter(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

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
                // succ_manu="1";
                if(!lite_pos_registration.getIndustry_Type().equals(industry_type)) {
                    lite_pos_registration.setIndustry_Type(industry_type);


                    long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                    if (ct > 0) {
                        // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){

                        succ_manu = "1";
                        //}
                    }
                }
                else{
                    long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                    if (ct > 0) {
                        // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){

                        succ_manu = "2";
                        //}
                    }
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

    public void login_parkingfunc(int result){

        if (result < 0) {
            runOnUiThread(new Runnable() {


                public void run() {


                    user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
                    if (user == null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pDialog.dismiss();
                                edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                                edt_username.requestFocus();
                            }
                        });

                        return;
                    } else {
                        item = Item.getItem(getApplicationContext(), "", database, db);
                        if (item == null) {

                            read_item();
                            try {
                                long l = Unit.deleteUnit(getApplicationContext(), null, null, database);
                                if (l > 0) {
                                    read_Unit();
                                } else {
                                }
                            } catch (Exception e) {

                            }

                            // read_itemgroup();

                        }
                        itemgroup = Item_Group.getItem_Group(getApplicationContext(), database, db, "");
                        if (item_group == null) {
                            read_itemgroup();
                        }
                        edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                        edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());
                        edtor_user.apply();
                        Globals.user = user.get_user_code();
                        Globals.username = user.get_name();
                        Globals.devicename = lite_pos_device.getDevice_Name();
                        Globals.UserCode = Globals.user;
                        if (status.equals("IN")) {
                            //Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Intent intent_category = new Intent(LoginActivity.this, ParkingIndustryActivity.class);
                            startActivity(intent_category);
                        } else if (status.equals("Out")) {
                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                            postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                        }
                    }
                }
            });

        } else if (result == 0) {
            user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id + "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
            if (user == null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pDialog.dismiss();
                        edt_username.setError(getString(R.string.usercode_passwd_incorrect));
                        edt_username.requestFocus();
                    }
                });

                return;
            } else {
                prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                edtor_user = prefrences.edit();
                edtor_user.putString(getString(R.string.pass_pref), edt_userpass.getText().toString());
                edtor_user.putString(getString(R.string.usercode_pref), edt_username.getText().toString());// Storing string
                edtor_user.apply();
                item = Item.getItem(getApplicationContext(), "", database, db);
                if (item == null) {

                    read_item();
                    //read_itemgroup();

                }
                itemgroup = Item_Group.getItem_Group(getApplicationContext(), database, db, "");
                if (item_group == null) {
                    read_itemgroup();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (status.equals("IN")) {

                            Globals.user = user.get_user_code();
                            Globals.UserCode = Globals.user;
                            Toast.makeText(getApplicationContext(), R.string.Login_Successfully, Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Intent intent_category = new Intent(LoginActivity.this, ParkingIndustryActivity.class);
                            startActivity(intent_category);


                        } else if (status.equals("Out")) {
                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                            String licensecustomerid = lite_pos_device.getLic_customer_license_id();
                            postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, reg_code, "0");

                        }
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.licence_expired_msg, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
