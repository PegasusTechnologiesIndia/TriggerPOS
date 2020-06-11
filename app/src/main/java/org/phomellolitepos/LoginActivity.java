package org.phomellolitepos;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;

import sunmi.ds.DSKernel;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;


public class LoginActivity extends AppCompatActivity {
    EditText edt_username, edt_userpass;
    TextView txt_forgot_pswd, txt_version;
    Button btn_login;
    ToggleButton tgl_btn_lang;
    Database db;
    SQLiteDatabase database;
    User user;
    Lite_POS_Device lite_pos_device;
    String date, strEmail;
    Dialog listDialog2;
    ProgressDialog pDialog;
    String displayTilte;
    DSKernel mDSKernel;
    DataPacket dsPacket;
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
    Button pbutton,btn_clear;
    Lite_POS_Registration lite_pos_registration;
    String status;
    String serial_no, android_id, myKey, device_id,imei_no;
    String email,password,reg_code;
    SharedPreferences.Editor edtor_user;
    SharedPreferences prefrences;
    String user_name;
    String company_email,company_password;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Login);
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

        Date d = new Date();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
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
}
catch(Exception e){

}
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);


        try {
            if (lite_pos_device != null) {
                status = lite_pos_device.getStatus();
            }
        } catch (Exception e) {

        }
company_email=lite_pos_registration.getEmail();
company_password=lite_pos_registration.getPassword();
        Globals.Industry_Type = lite_pos_registration.getIndustry_Type();
        reg_code=lite_pos_registration.getRegistration_Code();
        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS);
        prefrences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
// 0 - for private mode
        editor = pref.edit();
        edtor_user = prefrences.edit();
        int id = pref.getInt("id", 0);
        if (id == 0) {
            tgl_btn_lang.setChecked(false);
            getLocal("en");
        } else {
            tgl_btn_lang.setChecked(true);
            getLocal("ar");
        }
        if (isNetworkStatusAvialable(getApplicationContext())) {
            String device_symbol = lite_pos_device.getDevice_Symbol();
            if (device_symbol.equals("") || device_symbol.equals("null")) {
                new Thread() {
                    @Override
                    public void run() {
                        check_device();
                    }
                }.start();
            } else if (lite_pos_device.getLocation_Code().equals("0")) {
                new Thread() {
                    @Override
                    public void run() {
                        check_device();
                    }
                }.start();
            }

        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        try {
            showExpiry();
        }
        catch(Exception e){

        }
        tgl_btn_lang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String strLang = tgl_btn_lang.getText().toString();
                if (strLang.equals("Arabic")) {
                    getLocal("en");
                    editor.putInt("id", 0);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    getLocal("ar");
                    editor.putInt("id", 1);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        listDialog2 = new Dialog(this);
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        Globals.objLPD = lite_pos_device;
        user = User.getUser(getApplicationContext(), " WHERE user_code = '" + prefrences.getString(getString(R.string.usercode_pref), null) + "' or name = '" + prefrences.getString(getString(R.string.usercode_pref), null)+"' or email = '" + prefrences.getString(getString(R.string.usercode_pref), null) + "'  and is_active ='1'", database);
        if (user == null) {
            try {
                edt_username.setText(user.get_user_code());
                edt_userpass.setText(user.get_password());
               /* email=user.get_email();
                password=user.get_password();*/
            } catch (Exception ex) {
            }
        } else {
            try {
                Globals.user = user.get_user_code();
                Globals.UserCode = Globals.user;
                Globals.str_userpassword=user.get_password();
                user_name= user.get_name();
                edt_username.setText(prefrences.getString(getString(R.string.usercode_pref), null));
                edt_userpass.setText(prefrences.getString(getString(R.string.pass_pref), null));
                email=user.get_email();
                password=user.get_password();
            } catch (Exception ex) {

            }
        }


        txt_forgot_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialogforgotpswddialog();
            }
        });


        final Lite_POS_Device finalLite_pos_device = lite_pos_device;
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = User.getUser(getApplicationContext(), " WHERE (user_id ='" + edt_username.getText().toString().trim() + "'or email = '" + edt_username.getText().toString().trim() + "'or name = '" + edt_username.getText().toString().trim() + "') and password ='" + edt_userpass.getText().toString().trim() + "' and is_active= '1'", database);
                if (user == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.credentialcheck), Toast.LENGTH_SHORT).show();
                  //  Globals.showToast(getApplicationContext(), getString(R.string.credentialcheck), Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                } else {
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
                                            Globals.UserCode = Globals.user;


                                            pDialog.dismiss();
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
                                        user = User.getUser(getApplicationContext(), "WHERE  (user_code = '" + user_id + "' or  name = '" + user_id +  "' or email = '" + user_id + "' ) And password = '" + user_pass + "' And  is_active= '1'", database);
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
                                }

                            } catch (
                                    InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

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

                    android.support.v7.app.AlertDialog alert = builder.create();
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
                    send_email(strEmail);
                }
                catch (Exception e){

                }
                listDialog2.dismiss();
            }
        });


    }

    private void send_email(final String strEmail) {
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
                    }
                    catch(Exception e){

                    }
                }
            }.start();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void send_email_server(String strEmail, final ProgressDialog pDialog) {
        String msg = "";
        // Call get item group api here

            String serverData = send_email_on_server(strEmail);

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

    private String send_email_on_server(String strEmail) {

        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
                    "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/email");
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
        }
        catch(Exception e){

        }
        return serverData;

    }

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

    private void check_device() {
        // Call check license api here

        String serverData = Update_licence_from_server();
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
                            jsonObject_device.getString("currency_place"),jsonObject_device.getString("lic_customer_license_id"),jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"),jsonObject_device.getString("license_type"),"IN");

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
                            jsonObject_device.getString("currency_place"),jsonObject_device.getString("lic_customer_license_id"),jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"),jsonObject_device.getString("license_type"),"IN");

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

    private String Update_licence_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/device/check_license");
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
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "org.pegasusqburst" + "&hl=en")
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
            alertDialog.setTitle("Update");
            alertDialog
                    .setMessage("Version update is available.");

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
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String registrationcode,final String defaultlogout) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Logging In....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL+ "/index.php?route=api/license_product_1/device_login";
        HttpsTrustManager.allowAllSSL();
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
                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();

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
                                    }

                                     else {
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

                                pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                                if(message.toString().equalsIgnoreCase("Purchase more device")){

                                    try {
                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

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
                                     Toast.makeText(getApplicationContext(),message.toString(),Toast.LENGTH_SHORT).show();
                                }
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

                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();

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
                params.put("reg_code", registrationcode);
                params.put("default_logout", defaultlogout);
                System.out.println("params"+ params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getAlertclearTransactionDialog(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(getString(R.string.alerttitle));
        builder.setMessage(getString(R.string.alert_loginseconddialog));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {

                    postDeviceInfo(company_email, company_password, Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey,reg_code,"1");

                  //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
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

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();


    }
}
