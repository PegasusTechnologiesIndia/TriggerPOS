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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    Button pbutton;
    Lite_POS_Registration lite_pos_registration;
    String status;
    String serial_no, android_id, myKey, device_id,imei_no;
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

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        Globals.Industry_Type = lite_pos_registration.getIndustry_Type();
        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        editor = pref.edit();
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

        showExpiry();

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
        user = User.getUser(getApplicationContext(), " WHERE user_code = '" + pref.getString("user_code", null) + "' or name = '" + pref.getString("user_code", null) + "' and is_active ='1'", database);
        if (user == null) {
            try {
                edt_username.setText(user.get_user_code());
                edt_userpass.setText(user.get_password());
            } catch (Exception ex) {
            }
        } else {
            try {
                Globals.user = user.get_user_code();
                Globals.UserCode = Globals.user;
                edt_username.setText(pref.getString("user_code", null));
                edt_userpass.setText(pref.getString("pass", null));
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

                            } else if (lite_pos_device.getLocation_Code().equals("0")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Location not selected for this device", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                if (result < 0) {
                                    user = User.getUser(getApplicationContext(), "WHERE  password = '" + user_pass + "' And (user_code = '" + user_id + "' or  name = '" + user_id + "')", database);
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
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("pass", edt_userpass.getText().toString());
                                        editor.putString("user_code", edt_username.getText().toString());
                                        editor.apply();
                                        Globals.user = user.get_user_code();
                                        Globals.UserCode = Globals.user;


                                         lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);


                                        try {
                                            if (lite_pos_device != null) {
                                                status = lite_pos_device.getStatus();
                                            }
                                        } catch (Exception e) {

                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                if(status.equals("IN")) {
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
                                                else if(status.equals("Out")) {
                                                    lite_pos_device.setStatus("IN");
                                                    long ct = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
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

                                                }

                                            }
                                        });
                                    }
                                } else if (result == 0) {
                                    user = User.getUser(getApplicationContext(), "WHERE  password = '" + user_pass + "' And (user_code = '" + user_id + "' or  name = '" + user_id + "')", database);
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
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("pass", edt_userpass.getText().toString());
                                        editor.putString("user_code", edt_username.getText().toString());// Storing string
                                        editor.apply();

                                        runOnUiThread(new Runnable() {
                                            public void run() {
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
                send_email(strEmail);
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
                    send_email_server(strEmail, pDialog);
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
            nameValuePairs.add(new BasicNameValuePair("lic_code", lite_pos_registration.getRegistration_Code()));
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
                final JSONObject jsonObject_bg = new JSONObject(serverData);
                final String strStatus = jsonObject_bg.getString("status");
                if (strStatus.equals("true")) {

                    JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                    for (int i = 0; i < jsonArray_bg.length(); i++) {
                        JSONObject jsonobject = jsonArray_bg.getJSONObject(i);
                        ltversion = jsonobject.getString("version");
                    }

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
                    Double playVersionCode = Double.parseDouble(ltversion);
                    if (playVersionCode > Globals.Version_Code) {
                        showInDayOnce(appPackageName);
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                    }
                }
            } catch (JSONException e) {
            }
            return;
        }

    }

    private String check_version_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/version");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("app_type", "ANDROID"));

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

}
