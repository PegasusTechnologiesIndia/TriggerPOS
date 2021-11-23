package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Bank;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.MinCalculation;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.ScaleSetup;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.User;

import in.gauriinfotech.commons.Commons;

public class DataBaseActivity extends AppCompatActivity {
    Button btn_payment, btn_database, btn_database_clear, btn_import_bank, btn_initialize, btn_all_sync, btn_push_order, btn_push_orderparking, btn_import_table;
    Database db;
    SQLiteDatabase database;
    ProgressDialog progressDialog;
    Item item;
    ReceipeModifier item_modifier;
    Item_Location item_location;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    String PathHolder;
    String succ = "0";
    ProgressDialog pDialog;
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type;
    User user;
    Returns returns;
    // Settings settings;
    Item_Supplier item_supplier;
    LinearLayout linear_layout3, linear_layout7,linear_layout6;
    Animation animFadeIn;
    String serial_no, android_id, myKey, device_id, imei_no;
    Tax_Master tax_master;
    Order_Type_Tax order_type_tax;
    Tax_Detail tax_detail;
    Contact contact;
    Address address;
    Unit unit;
    Address_Lookup address_lookup;
Sys_Tax_Group systax_group;
    Contact_Bussiness_Group contact_bussiness_group;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Database);
        FirebaseCrashlytics.getInstance().recordException(new Exception());
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  pDialog = new ProgressDialog(DataBaseActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/

                if(Globals.objLPR.getIndustry_Type().equals("2")){

                    Intent intent = new Intent(DataBaseActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                else if(Globals.objLPR.getIndustry_Type().equals("3")){

                    Intent intent = new Intent(DataBaseActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //   pDialog.dismiss();
                    finish();
                }

                else if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(DataBaseActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //   pDialog.dismiss();
                    finish();
                }
                else {
                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(DataBaseActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(DataBaseActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(DataBaseActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }

                    /*}
                };
                timerThread.start();*/
            }
        });
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        // settings = Settings.getSettings(getApplicationContext(), database, "");
        btn_database = (Button) findViewById(R.id.btn_database);
        btn_database_clear = (Button) findViewById(R.id.btn_database_clear);
        btn_push_order = (Button) findViewById(R.id.btn_push_order);
        btn_import_bank = (Button) findViewById(R.id.btn_import_bank);
        btn_initialize = (Button) findViewById(R.id.btn_initialize);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        btn_import_table = (Button) findViewById(R.id.btn_import_table);
        btn_push_orderparking = (Button) findViewById(R.id.btn_push_orderparking);
        btn_all_sync = (Button) findViewById(R.id.btn_all_sync);
        linear_layout3 = (LinearLayout) findViewById(R.id.linear_layout3);
        linear_layout7 = (LinearLayout) findViewById(R.id.linear_layout7);
        linear_layout6 = (LinearLayout) findViewById(R.id.linear_layout6);
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
       /* device_id = telephonyManager.getDeviceId();
        imei_no = telephonyManager.getImei();*/
        //  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        ck_project_type = Globals.objLPR.getproject_id();
        //liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

        if (ck_project_type.equals("standalone")) {
            btn_database_clear.setEnabled(true);
            btn_all_sync.setEnabled(false);
            btn_all_sync.setVisibility(View.GONE);
            btn_push_order.setVisibility(View.GONE);
            btn_import_table.setVisibility(View.GONE);
        } else {
            btn_all_sync.setEnabled(true);
            btn_all_sync.setVisibility(View.VISIBLE);
            btn_push_order.setVisibility(View.VISIBLE);
            btn_import_table.setVisibility(View.VISIBLE);
        }


        if(Globals.objLPR.getIndustry_Type().equals("3")){
            linear_layout3.setVisibility(View.GONE);

        }
        else if(Globals.objLPR.getIndustry_Type().equals("4")){
            linear_layout3.setVisibility(View.GONE);
            btn_push_orderparking.setVisibility(View.VISIBLE);
            btn_all_sync.setEnabled(true);
            btn_all_sync.setVisibility(View.VISIBLE);
        }
        else{
            linear_layout3.setVisibility(View.VISIBLE);
        }
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                btn_payment.startAnimation(myAnim);
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    try {

                        pDialog = new ProgressDialog(DataBaseActivity.this);
                        pDialog.setCancelable(false);
                        pDialog.setMessage(getString(R.string.GettingData));
                        pDialog.show();
                        new Thread() {
                            @Override
                            public void run() {
                                String suss = "";
                                try {
                                    getPayment_from_Server(Globals.objLPR.getRegistration_Code());;
                                } catch (Exception e) {

                                }
                                pDialog.dismiss();
                                switch (suss) {
                                    case "1":
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), R.string.Paymddownld, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;

                                    case "3":
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    default:
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), R.string.Paymethodsnotfound, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                }
                            }
                        }.start();
                    } catch (Exception ex) {
                        pDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                }
            }
        });
        btn_all_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                btn_all_sync.startAnimation(myAnim);
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    progressDialog = new ProgressDialog(DataBaseActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.Syncdataserver));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {

                                    // 1. Synch Payment Method
                                    sleep(200);
                                    String result;
                                    String suss;

                                    if(Globals.objLPR.getIndustry_Type().equals("1") || Globals.objLPR.getIndustry_Type().equals("2")) {


                                        try {
                                            // result = send_online_item_group();
                                            Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");

                                            if (sys_sycntime == null) {
                                                getitemgrp_from_server(progressDialog,"",serial_no,android_id,myKey);
                                            } else {
                                                getitemgrp_from_server(progressDialog,sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                            }

                                        } catch (Exception ex) {
                                        }



                                    }
                                    else if(Globals.objLPR.getIndustry_Type().equals("3")){


                                        try {
                                            // result = send_online_contact(serialno,android,mykey);
                                            Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                            //Call get contact api here
                                            if (sys_sycntime==null){
                                                getcontact_from_server(progressDialog,"",serial_no,android_id,myKey);
                                            }else {
                                                getcontact_from_server(progressDialog,sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                            }


                                        } catch (Exception ex) {
                                        }

                                    }

                                    else if(Globals.objLPR.getIndustry_Type().equals("4")){

                                        try {
                                            // result = send_online_contact(serialno,android,mykey);
                                            Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                            //Call get contact api here
                                            if (sys_sycntime==null){
                                                getcontact_from_server(progressDialog,"",serial_no,android_id,myKey);
                                            }else {
                                                getcontact_from_server(progressDialog,sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                            }


                                        } catch (Exception ex) {
                                        }

                                    }





                                    // progressDialog.dismiss();

                                } catch (final Exception e) {
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    };
                    t.start();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_initialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseCrashlytics.getInstance().recordException(new Throwable());


                final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                btn_initialize.startAnimation(myAnim);
                //  if (isNetworkStatusAvialable(getApplicationContext())) {
               // final String str = Globals.str_userpassword;
                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                final String str= user.get_password();
                final Dialog listDialog2 = new Dialog(DataBaseActivity.this);
//                    listDialog2.setTitle("Enter Password");
                LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.password_dialog, null, false);
                listDialog2.setContentView(v1);
                listDialog2.setCancelable(true);
                final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                listDialog2.show();
                Window window = listDialog2.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                edt_pass.setText("");
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_pass.getText().toString().trim().equals("")) {
                            edt_pass.setError("Password is required");
                            return;
                        }

                        if (str.equals(edt_pass.getText().toString().trim())) {
                            listDialog2.dismiss();
                            progressDialog = new ProgressDialog(DataBaseActivity.this);
                            progressDialog.setTitle("");
                            progressDialog.setMessage("Initializing...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        try {
                                            sleep(200);
                                            try {
                                                initialize();
                                            } catch (Exception e) {

                                            }
                                            progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    finish();
                                               /*     SharedPreferences preferences =getSharedPreferences("MyPref",Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.clear();
                                                    editor.apply();
                                                    finish();*/
                                                    Intent intent = new Intent(DataBaseActivity.this, SplashScreen.class);
                                                    startActivity(intent);
                                                    Intent serviceIntent = new Intent(getApplicationContext(), SplashScreen.class);
                                                    stopService(serviceIntent);
                                                    Intent intent1 = new Intent();
                                                    intent1.setPackage("woyou.aidlservice.jiuiv5");
                                                    intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
                                                    stopService(intent1);
                                                    // bindService(intent1, connService, Context.BIND_AUTO_CREATE);
                                                }
                                            });
                                        } catch (final Exception e) {
                                            progressDialog.dismiss();
                                        }
                                    } catch (Exception ex) {
                                        progressDialog.dismiss();
                                        ex.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }
                            };
                            t.start();

                        } else {
                               Toast.makeText(DataBaseActivity.this, R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
               /* } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }*/


            }
        });

        btn_import_bank.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                                                   btn_import_bank.startAnimation(myAnim);
                                                   if (isNetworkStatusAvialable(getApplicationContext())) {
                                                       progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                       progressDialog.setTitle("");
                                                       progressDialog.setMessage(getString(R.string.BankDetailDnlding));
                                                       progressDialog.setCancelable(false);
                                                       progressDialog.show();
                                                       final Thread t = new Thread() {
                                                           @Override
                                                           public void run() {
                                                               try {
                                                                   try {
                                                                       sleep(200);
                                                                       String suss = "";
                                                                       try {
                                                                           suss = getBankDetail();
                                                                       } catch (Exception e) {

                                                                       }
                                                                       progressDialog.dismiss();

                                                                       switch (suss) {
                                                                           case "1":
                                                                               runOnUiThread(new Runnable() {
                                                                                   public void run() {

                                                                                       Toast.makeText(getApplicationContext(), R.string.bank_detail_succ, Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               });

                                                                               break;

                                                                           case "2":
                                                                               runOnUiThread(new Runnable() {
                                                                                   public void run() {

                                                                                       Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               });
                                                                               break;
                                                                           default:
                                                                               runOnUiThread(new Runnable() {
                                                                                   public void run() {

                                                                                       Toast.makeText(getApplicationContext(), R.string.Bank_detail_not_found, Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               });
                                                                               break;
                                                                       }

                                                                   } catch (final Exception e) {
                                                                   }
                                                               } catch (Exception ex) {
                                                                   // TODO Auto-generated catch block
                                                                   ex.printStackTrace();
                                                               }
                                                               progressDialog.dismiss();
                                                           }
                                                       };
                                                       t.start();
                                                   } else {
                                                       Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                   }

                                               }
                                           }

        );

        btn_import_table.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (isNetworkStatusAvialable(getApplicationContext())) {
                                                        progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                        progressDialog.setTitle("");
                                                        progressDialog.setMessage(getString(R.string.synctableDnlding));
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.show();
                                                        final Thread t = new Thread() {
                                                            @Override
                                                            public void run() {
                                                                try {

                                                                    // sleep(200);
                                                                    getZone_Table_fromserver(progressDialog,"2");

                                                                } catch (Exception ex) {
                                                                    // TODO Auto-generated catch block
                                                                    ex.printStackTrace();
                                                                }
                                                                // progressDialog.dismiss();
                                                            }
                                                        };
                                                        t.start();
                                                    }

//                                                        final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
//                                                        btn_import_table.startAnimation(myAnim);
//                                                        String[] mimeTypes = {"text/*"};
//                                                        // File file= new File(Environment.getExternalStorageDirectory()+"");
//                                                        Intent intent;
//
//                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//
//                                                            // intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                                            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//                                                            if (mimeTypes.length > 0) {
//                                                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//                                                                //   intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, mimeTypes);
//                                                            }
//                                                        } else {
//                                                            intent = new Intent(Intent.ACTION_GET_CONTENT);
//
//                                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                                            String mimeTypesStr = "";
//                                                            for (String mimeType : mimeTypes) {
//                                                                mimeTypesStr += mimeType + "|";
//                                                            }
//                                                            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
//                                                        }
//                                                        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 7);

                                                    //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");



                                                }
                                            }

        );

        btn_database_clear.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                                                      btn_database_clear.startAnimation(myAnim);
                                                      if (ck_project_type.equals("cloud")) {
                                                          if (isNetworkStatusAvialable(getApplicationContext())) {
                                                              runOnUiThread(new Runnable() {
                                                                  public void run() {

                                                                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                                                              DataBaseActivity.this);
                                                                      alertDialog.setTitle(R.string.ClearTransaction);
                                                                      alertDialog
                                                                              .setMessage(R.string.Dywntclearformserver);
                                                                      alertDialog.setCancelable(false);
                                                                      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                              LinearLayout.LayoutParams.MATCH_PARENT,
                                                                              LinearLayout.LayoutParams.MATCH_PARENT);

                                                                      alertDialog.setPositiveButton(R.string.yes,
                                                                              new DialogInterface.OnClickListener() {
                                                                                  public void onClick(DialogInterface dialog,
                                                                                                      int which) {

                                                                                      final String str = Globals.str_userpassword;
                                                                                      final Dialog listDialog2 = new Dialog(DataBaseActivity.this);
                                                                                      LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                                      View v1 = li1.inflate(R.layout.password_dialog, null, false);
                                                                                      listDialog2.setContentView(v1);
                                                                                      listDialog2.setCancelable(true);
                                                                                      final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                                                                                      Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                                                                                      listDialog2.show();
                                                                                      Window window = listDialog2.getWindow();
                                                                                      window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                                                                                      edt_pass.setText("");
                                                                                      btn_ok.setOnClickListener(new View.OnClickListener() {
                                                                                          @Override
                                                                                          public void onClick(View view) {

                                                                                              if (edt_pass.getText().toString().trim().equals("")) {
                                                                                                  edt_pass.setError(getString(R.string.PassIsRequired));
                                                                                                  return;
                                                                                              }

                                                                                              if (str.equals(edt_pass.getText().toString().trim())) {
                                                                                                  listDialog2.dismiss();
                                                                                                  progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                                                                  progressDialog.setTitle("");
                                                                                                  progressDialog.setMessage(getString(R.string.Wait_msg));
                                                                                                  progressDialog.setCancelable(false);
                                                                                                  progressDialog.show();
                                                                                                  new Thread() {
                                                                                                      @Override
                                                                                                      public void run() {
                                                                                                          if (isNetworkStatusAvialable(getApplicationContext())) {
                                                                                                              clear_from_server();

                                                                                                          } else {
                                                                                                              Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                                                                          }
                                                                                                      }
                                                                                                  }.start();
                                                                                              } else {
                                                                                                  Toast.makeText(getApplicationContext(), R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                                                                                              }
                                                                                          }

                                                                                      });

                                                                                  }
                                                                              });

                                                                      alertDialog.setNegativeButton(R.string.no,
                                                                              new DialogInterface.OnClickListener() {
                                                                                  public void onClick(DialogInterface dialog,
                                                                                                      int which) {

                                                                                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                                                                              DataBaseActivity.this);
                                                                                      alertDialog.setTitle("");
                                                                                      alertDialog
                                                                                              .setMessage(R.string.ClearTrasactionLocally);
                                                                                      alertDialog.setCancelable(false);
                                                                                      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                                              LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                              LinearLayout.LayoutParams.MATCH_PARENT);

                                                                                      alertDialog.setNegativeButton(R.string.Cancel,

                                                                                              new DialogInterface.OnClickListener() {
                                                                                                  public void onClick(DialogInterface dialog,
                                                                                                                      int which) {

                                                                                                  }
                                                                                              });

                                                                                      alertDialog.setPositiveButton(R.string.Ok,
                                                                                              new DialogInterface.OnClickListener() {
                                                                                                  public void onClick(DialogInterface dialog,
                                                                                                                      int which) {

                                                                                                      final String str = Globals.str_userpassword;
                                                                                                      final Dialog listDialog2 = new Dialog(DataBaseActivity.this);
                                                                                                      LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                                                      View v1 = li1.inflate(R.layout.password_dialog, null, false);
                                                                                                      listDialog2.setContentView(v1);
                                                                                                      listDialog2.setCancelable(true);
                                                                                                      final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                                                                                                      Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                                                                                                      listDialog2.show();
                                                                                                      Window window = listDialog2.getWindow();
                                                                                                      window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                                                                                                      edt_pass.setText("");
                                                                                                      btn_ok.setOnClickListener(new View.OnClickListener() {
                                                                                                          @Override
                                                                                                          public void onClick(View view) {

                                                                                                              if (edt_pass.getText().toString().trim().equals("")) {
                                                                                                                  edt_pass.setError(getString(R.string.PassIsRequired));
                                                                                                                  return;
                                                                                                              }

                                                                                                              if (str.equals(edt_pass.getText().toString().trim())) {
                                                                                                                  listDialog2.dismiss();
                                                                                                                  progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                                                                                  progressDialog.setTitle("");
                                                                                                                  progressDialog.setMessage(getString(R.string.Wait_msg));
                                                                                                                  progressDialog.setCancelable(false);
                                                                                                                  progressDialog.show();

                                                                                                                  new Thread() {
                                                                                                                      @Override
                                                                                                                      public void run() {
                                                                                                                          clear_tranaction();
                                                                                                                          //clear_tranaction();

                                                                                                                      }
                                                                                                                  }.start();

                                                                                                                  progressDialog.dismiss();
                                                                                                                  runOnUiThread(new Runnable() {
                                                                                                                      public void run() {
                                                                                                                          postLastCodeFromServer(Globals.reg_code,Globals.Device_Code,serial_no,Globals.syscode2,android_id,myKey,Globals.license_id);

                                                                                                                          // Toast.makeText(getApplicationContext(), R.string.Transaction_Clear_Successfully, Toast.LENGTH_SHORT).show();
                                                                                                                      }
                                                                                                                  });
                                                                                                                  runOnUiThread(new Runnable() {
                                                                                                                      public void run() {
                                                                                                                          postLastCodeFromServer(Globals.reg_code,Globals.Device_Code,serial_no,Globals.syscode2,android_id,myKey,Globals.license_id);
                                                                                                                          Toast.makeText(getApplicationContext(), R.string.Transaction_Clear_Successfully, Toast.LENGTH_SHORT).show();
                                                                                                                      }
                                                                                                                  });

                                                                                                              } else {
                                                                                                                  Toast.makeText(getApplicationContext(), R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                                                                                                              }
                                                                                                          }

                                                                                                      });
                                                                                                  }


                                                                                              });

                                                                                      AlertDialog alert = alertDialog.create();
                                                                                      alert.show();

                                                                                      Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                                                      nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                                                      Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                                                                      pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                                                  }
                                                                              });

                                                                      AlertDialog alert = alertDialog.create();
                                                                      alert.show();

                                                                      Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                                      nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                                      Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                                                      pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                  }
                                                              });
                                                          } else {
                                                              Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                                                          }
                                                      } else {


                                                          AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                                                  DataBaseActivity.this);
                                                          alertDialog.setTitle("");
                                                          alertDialog
                                                                  .setMessage(R.string.ClearTrasactionLocally);
                                                          alertDialog.setCancelable(false);
                                                          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                  LinearLayout.LayoutParams.MATCH_PARENT,
                                                                  LinearLayout.LayoutParams.MATCH_PARENT);

                                                          alertDialog.setNegativeButton(R.string.Cancel,

                                                                  new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog,
                                                                                          int which) {

                                                                      }
                                                                  });

                                                          alertDialog.setPositiveButton(R.string.Ok,
                                                                  new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog,
                                                                                          int which) {

                                                                          final String str = Globals.str_userpassword;
                                                                          final Dialog listDialog2 = new Dialog(DataBaseActivity.this);
                                                                          LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                          View v1 = li1.inflate(R.layout.password_dialog, null, false);
                                                                          listDialog2.setContentView(v1);
                                                                          listDialog2.setCancelable(true);
                                                                          final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                                                                          Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                                                                          listDialog2.show();
                                                                          Window window = listDialog2.getWindow();
                                                                          window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                                                                          edt_pass.setText("");
                                                                          btn_ok.setOnClickListener(new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(View view) {

                                                                                  if (edt_pass.getText().toString().trim().equals("")) {
                                                                                      edt_pass.setError(getString(R.string.PassIsRequired));
                                                                                      return;
                                                                                  }

                                                                                  if (str.equals(edt_pass.getText().toString().trim())) {
                                                                                      listDialog2.dismiss();
                                                                                      progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                                                      progressDialog.setTitle("");
                                                                                      progressDialog.setMessage(getString(R.string.Wait_msg));
                                                                                      progressDialog.setCancelable(false);
                                                                                      progressDialog.show();
                                                                                      new Thread() {
                                                                                          @Override
                                                                                          public void run() {
                                                                                              clear_tranaction();
                                                                                              //clear_tranaction();

                                                                                          }
                                                                                      }.start();

                                                                                      progressDialog.dismiss();

                                                                                      runOnUiThread(new Runnable() {
                                                                                          public void run() {
                                                                                              Toast.makeText(getApplicationContext(), R.string.Transaction_Clear_Successfully, Toast.LENGTH_SHORT).show();
                                                                                          }
                                                                                      });
                                                                                  } else {
                                                                                      Toast.makeText(getApplicationContext(), R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                                                                                  }
                                                                              }

                                                                          });
                                                                      }


                                                                  });

                                                          AlertDialog alert = alertDialog.create();
                                                          alert.show();

                                                          Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                          nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                          Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                                          pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                      }
                                                  }
                                              }

        );

        btn_push_order.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                                                  btn_push_order.startAnimation(myAnim);
                                                  if (isNetworkStatusAvialable(getApplicationContext())) {

                                                      try {
                                                          ck_project_type = Globals.objLPR.getproject_id();
                                                      } catch (Exception e) {
                                                          ck_project_type = "";
                                                      }
                                                      if (ck_project_type.equals("cloud"))
                                                      {
                                                          progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                          progressDialog.setTitle("");
                                                          progressDialog.setMessage(getString(R.string.Wait_msg));
                                                          progressDialog.setCancelable(false);
                                                          progressDialog.show();
                                                          new Thread() {
                                                              @Override
                                                              public void run() {
                                                                  Orders order=new Orders(getApplicationContext());

                                                                  String  result  =  order.sendOn_Server(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'", Globals.objLPD.getLic_customer_license_id(), serial_no, android_id, myKey);
                                                                 if(result.equals("1"))
                                                                 {
                                                                     String result1 = Returns.sendOnServer(progressDialog, getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'", Globals.objLPD.getLic_customer_license_id(), serial_no, android_id, myKey);
                                                                     progressDialog.dismiss();

                                                                 /* if (result_order.equals("1")) {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {

                                                                              Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  } else {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {
                                                                              Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  }
*/


                                                                     if(result1.equals("1"))
                                                                     {
                                                                         String rsultPost = stock_post();
                                                                         if(rsultPost.equals("1"))
                                                                         {
                                                                                        runOnUiThread(new Runnable()
                                                                                        {
                                                                                           public void run()
                                                                                           {
                                                                                               Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                                           }
                                                                                       });
                                                                         }
                                                                     }
                                                                     else {
                                                                         runOnUiThread(new Runnable() {
                                                                             public void run()
                                                                             {
                                                                                 Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                             }
                                                                         });
                                                                     }
                                                                 }
                                                                 else{
                                                                         runOnUiThread(new Runnable() {
                                                                             public void run()
                                                                             {
                                                                                 progressDialog.dismiss();
                                                                                 Toast.makeText(DataBaseActivity.this, R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                                                                             }
                                                                         });
                                                                     }
                                                                 }

                                                          }.start();
                                                      }
                                                  } else {
                                                      Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          }

        );

        btn_push_orderparking.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {
                                                         final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                                                         btn_push_orderparking.startAnimation(myAnim);
                                                         if (isNetworkStatusAvialable(getApplicationContext())) {

                                                             try {
                                                                 ck_project_type = Globals.objLPR.getproject_id();
                                                             } catch (Exception e) {
                                                                 ck_project_type = "";
                                                             }
                                                             if (ck_project_type.equals("cloud")) {
                                                                 progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                                 progressDialog.setTitle("");
                                                                 progressDialog.setMessage(getString(R.string.Wait_msg));
                                                                 progressDialog.setCancelable(false);
                                                                 progressDialog.show();
                                                                 new Thread() {
                                                                     @Override
                                                                     public void run() {
                                                                         Orders order=new Orders(getApplicationContext());

                                                                      String result=    order.sendOn_Server(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'", Globals.objLPD.getLic_customer_license_id(), serial_no, android_id, myKey);
                                                                         //String result = Returns.sendOnServer(progressDialog,getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'", Globals.objLPD.getLic_customer_license_id(), serial_no, android_id, myKey);
                                                                         if(result.equals("1"))
                                                                         {
                                                                             runOnUiThread(new Runnable() {
                                                                                 @Override
                                                                                 public void run()
                                                                                 {
                                                                                     progressDialog.dismiss();
                                                                                     Toast.makeText(DataBaseActivity.this,"Order Sucussfully Post ",Toast.LENGTH_SHORT).show();
                                                                                 }
                                                                             });
                                                                         }else
                                                                             if(result.equals("2"))
                                                                             {
                                                                                 runOnUiThread(new Runnable() {
                                                                                     @Override
                                                                                     public void run()
                                                                                     {
                                                                                         progressDialog.dismiss();
                                                                                         Toast.makeText(DataBaseActivity.this,"Response false ",Toast.LENGTH_SHORT).show();

                                                                                     }
                                                                                 });
                                                                             }
                                                                         else
                                                                         {
                                                                             runOnUiThread(new Runnable() {
                                                                                 @Override
                                                                                 public void run() {
                                                                                     progressDialog.dismiss();
                                                                                     Toast.makeText(DataBaseActivity.this,"No Data Found",Toast.LENGTH_SHORT).show();
                                                                                 }
                                                                             });
                                                                         }


                                                                 /* if (result_order.equals("1")) {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {

                                                                              Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  } else {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {
                                                                              Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  }
*/

                                                      /*            if (result.equals("1")) {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {
                                                                              String rsultPost = stock_post();
                                                                              Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  } else {
                                                                      runOnUiThread(new Runnable() {
                                                                          public void run() {
                                                                              Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      });
                                                                  }*/
                                                                     }
                                                                 }.start();
                                                             }
                                                         } else {
                                                             Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 }

        );
        btn_database.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                final Animation myAnim = AnimationUtils.loadAnimation(DataBaseActivity.this, R.anim.fade_in);
                                                btn_database.startAnimation(myAnim);
                                                progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                progressDialog.setTitle("");
                                                progressDialog.setMessage(getString(R.string.Wait_msg));
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            DatabaseBackUp();
                                                        }
                                                        catch(Exception e){

                                                        }

                                                    }
                                                }.start();

                                            }
                                        }
        );
    }

    private String getUser(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here

        database.beginTransaction();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("user_code");
                    String bg_id = jsonObject_bg1.getString("user_id");
                    user = User.getdbUser(getApplicationContext(), "WHERE  is_active = '1' and user_code ='" + bg_code + "'", database);

                    if (user == null) {
                        user = new User(getApplicationContext(), null, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.insertUser(database);

                        if (l > 0) {
                            succ_manu = "1";


                        } else {

                        }

                    } else {

                        user = new User(getApplicationContext(), bg_id, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.updateUser("user_code=? And user_id=?", database, new String[]{bg_code, bg_id});
                        if (l > 0) {
                            succ_manu = "2";


                        } else {

                        }
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

                Globals.objLPD.setLocation_Code(locationid);
                Globals.objLPD.setLocation_name(locationname);
                long dt = Globals.objLPD.updateDevice("Id=?", new String[]{"1"}, database);
                if (dt > 0) {

                }
                Globals.objLPR.setAddress(inv_address);
                Globals.objLPR.setService_code_tariff(gst_number);
                Globals.objLPR.setShort_companyname(shortcompanyname);
                lite_pos_registration.setIndustry_Type(industry_type);
               // succ_manu="1";
                if(!Globals.objLPR.getIndustry_Type().equals(industry_type)) {
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

    private String getScalesetup(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here

        database.beginTransaction();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray = jsonObject_bp.getJSONArray("result");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject_res= jsonArray.getJSONObject(i);
                    String scaleid= jsonObject_res.getString("scale_id");
                    String scalename= jsonObject_res.getString("scale_name");
                    String pluvalue= jsonObject_res.getString("plu_value");
                    String plulength= jsonObject_res.getString("plu_length");
                    String itemcodelength= jsonObject_res.getString("item_code_length");
                    String wplen= jsonObject_res.getString("wp_length");
                    String wpcal= jsonObject_res.getString("wp_cal");
                    String isweight= jsonObject_res.getString("is_weight");
                    String pcsunitid= jsonObject_res.getString("pcs_unit_id");
                    String plubarcodelen= jsonObject_res.getString("plu_barcode_length");
                    String locationid= jsonObject_res.getString("location_id");

                    ScaleSetup scaleSetup = new ScaleSetup(getApplicationContext(), null, scalename,
                            pluvalue, plulength, itemcodelength, wplen, wpcal, isweight,pcsunitid,plubarcodelen);


                    long k = scaleSetup.insertScaleSetup(database);
                    if (k > 0) {
                        succ_manu = "1";
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
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }


    /*private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "user");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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

    private void send_online_user() {
        User.sendOnServer(getApplicationContext(), database, db, "Select  * From  user  WHERE is_push = 'N'");
    }

    private void clear_transaction_server(String serverData) {


        try {

            JSONArray collection_jsonArray = new JSONArray(serverData);
            for (int i = 0; i < collection_jsonArray.length(); i++) {
                JSONObject jsonObject = collection_jsonArray.getJSONObject(i);
                String strStatus = jsonObject.getString("status");
                if (strStatus.equals("true")) {
                    clear_tranaction();
                    progressDialog.dismiss();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Transaction_Clear_Successfully, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                }
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
        }
    }

    /*private String clear_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "Dbdetail");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));

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
*/

    public void clear_from_server(){
      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url =Globals.App_IP_URL + "Dbdetail";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            clear_transaction_server(response);
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
                        // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.Device_Code);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private String getBankDetail() {
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();
        String serverData = "";
        //get_bank_from_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bank_id = jsonObject_bg1.getString("bank_id");
                    Bank bank = Bank.getBank(getApplicationContext(), "WHERE bank_id ='" + bank_id + "'", database);

                    if (bank == null) {
                        bank = new Bank(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("bank_code"), jsonObject_bg1.getString("bank_name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("mobile"), jsonObject_bg1.getString("address"), jsonObject_bg1.getString("bank_ref_code"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N");
                        long l = bank.insertBank(database);
                        if (l > 0) {
                            succ_bg = "1";

                        } else {
                        }
                    } else {
                        bank = new Bank(getApplicationContext(), jsonObject_bg1.getString("bank_id"), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("bank_code"), jsonObject_bg1.getString("bank_name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("mobile"), jsonObject_bg1.getString("address"), jsonObject_bg1.getString("bank_ref_code"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N");
                        long l = bank.updateBank("bank_id=?", new String[]{bank_id}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
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

    /*private String get_bank_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "bank");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
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
*/

    private void clear_tranaction() {
        try {
            Database db = new Database(getApplicationContext());
            SQLiteDatabase sdb = db.getWritableDatabase();
            InputStream ins = getResources().openRawResource(R.raw.drop_db);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            try {
                String line = null;
                String sql = "";
                while ((line = reader.readLine()) != null) {
                    sql += line;
                }
                ins.close();
                runInsert(sdb, sql);
            } catch (IOException e) {

            } catch (Exception e) {

            }
            Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
            last_code.setlast_z_close_code("0");
            last_code.setlast_pos_balance_code("0");
            last_code.setlast_order_code("0");
            last_code.setLast_order_return_code("0");
            last_code.updateLast_Code("id=?", new String[]{"1"}, database);

            String sql = "update item_location set quantity = '0'";
            long l = db.executeDML(sql, database);

        } catch (Exception ex) {
        }
    }


    private void initialize() {
        try {
            Database db = new Database(getApplicationContext());
            SQLiteDatabase sdb = db.getWritableDatabase();
            InputStream ins = getResources().openRawResource(R.raw.drop_db1);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            try {
                String line = null;
                String sql = "";
                while ((line = reader.readLine()) != null) {
                    sql += line;
                }
                ins.close();
                runInsert(sdb, sql);
            } catch (IOException e) {

            } catch (Exception e) {
            }
        } catch (Exception ex) {
        }
    }

    private void runInsert(SQLiteDatabase db, String sql) {
        String a[] = sql.split(";");
        for (String X : a) {
            db.execSQL(X);
        }
    }


    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static boolean SdIsPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
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
    public void onBackPressed() {
      /*  pDialog = new ProgressDialog(DataBaseActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/

        if(Globals.objLPR.getIndustry_Type().equals("2")){

            Intent intent = new Intent(DataBaseActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(Globals.objLPR.getIndustry_Type().equals("3")){

            Intent intent = new Intent(DataBaseActivity.this, PaymentCollection_MainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // pDialog.dismiss();
            finish();
        }
        else if(Globals.objLPR.getIndustry_Type().equals("4")){

            Intent intent = new Intent(DataBaseActivity.this, ParkingIndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // pDialog.dismiss();
            finish();
        }
        else {
            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                try {
                    Intent intent = new Intent(DataBaseActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //   pDialog.dismiss();
                    finish();
                } finally {
                }
            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                try {
                    Intent intent = new Intent(DataBaseActivity.this, RetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //   pDialog.dismiss();
                    finish();
                } finally {
                }
            } else {
                try {
                    Intent intent = new Intent(DataBaseActivity.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        }
          /*  }
        };
        timerThread.start();*/
    }


    private String DatabaseBackUp() {
        File backupDB = null;
        String backupDBPath = null;
        try {
            File sd = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/Backup");

            if (!sd.exists()) {
                sd.mkdirs();
            }
            // File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = Database.DATABASE_FILE_PATH + File.separator + Database.DBNAME;
            backupDBPath = "TriggerPOS" + DateUtill.currentDatebackup() + ".db";
            File currentDB = new File(currentDBPath);
            backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        //  File sd = new File(Environment.getExternalStorageDirectory(), "LitePOS/Backup");
                        Toast.makeText(getApplicationContext(),  "Database Backup Successfully at "+" sdCard/TriggerPOS/Backup", Toast.LENGTH_LONG).show();
                    }
                });
              /*  Toast.makeText(getApplicationContext(), R.string.BackupSuccCreated + sd.getPath(),
                        Toast.LENGTH_LONG).show();*/

            } catch (IOException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return backupDBPath;
    }


    private void getLastCode(String serverData) {
        Last_Code last_code;
        // getLastCodeFromServer();
        if (serverData == null) {
            // System.exit(0);
            runOnUiThread(new Runnable() {
                public void run() {
                    // Toast.makeText(getApplicationContext(), "Server Not Found", Toast.LENGTH_SHORT).show();
                }

            });
        }
        else {
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
                    String last_order_return_code = jsonObject.getString("last_order_return_code");
                    long l = Last_Code.delete_Last_Code(getApplicationContext(), null, null, database);


                    last_code = new Last_Code(getApplicationContext(), null, last_order_code, last_pos_balance_code, last_z_close_code, last_order_return_code);

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
    }

/*
    private String getLastCodeFromServer() {
        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost( Globals.App_IP_URL + "last_code");
            ArrayList nameValuePairs = new ArrayList(5);
            nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
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
        catch(Exception e){}
        return serverData;
    }
*/

    public void postLastCodeFromServer(final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String licensecustomerid) {

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
                            getLastCode(response);

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
 /*   private String getInvoiceParameter_fromserver() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "invoice_parameter");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
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

    private String getBussinessGroup(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        Bussiness_Group bussiness_group;
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("business_group_code");
                    String bg_id = jsonObject_bg1.getString("business_group_id");
                    bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE business_group_code ='" + bg_code + "'");
                    if (bussiness_group == null) {
                        bussiness_group = new Bussiness_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.insertBussiness_Group(database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    } else {
                        bussiness_group = new Bussiness_Group(getApplicationContext(), bg_id, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.updateBussiness_Group("business_group_code=? And business_group_id=? ", new String[]{bg_code, bg_id}, database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    }
                }
            } else {
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

    /* private String get_bussiness_gp_from_server() {
         String serverData = null;//
         DefaultHttpClient httpClient = new DefaultHttpClient();
         HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "business_group");
         ArrayList nameValuePairs = new ArrayList(5);
         nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
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
 */
    private String send_online_bussiness() {
        String l = Bussiness_Group.sendOnServer(getApplicationContext(), database, db, "Select  * From business_group  WHERE is_push = 'N'");
        return l;
    }

    private String send_online_item_group(final ProgressDialog pdialog) {
        //Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String l = Item_Group.sendOnServer(pdialog,getApplicationContext(), database, db, "Select * From item_group  WHERE is_push = 'N'", Globals.serialno, Globals.syscode2, Globals.androidid, Globals.mykey, Globals.license_id);
        return l;
    }

    private String getitemGroup(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");
        String succ_bg = "0";

        // Call get item group api here
        System.out.println("get sync date" + sys_sycntime.get_datetime());
        database.beginTransaction();


        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmessage = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    }

                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmessage;


            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";
            //database.setTransactionSuccessful();
            database.endTransaction();
        }
        return succ_bg;
    }

    /*private String get_item_gp_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "item_group");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
        System.out.println("namevalue get group" + nameValuePairs);
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
            System.out.println("response get group " + serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;


    }*/

    private JSONObject send_online_contact() {

        JSONObject conList = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'", Globals.license_id, serial_no, android_id, myKey);
        return conList;
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
                    if (sys_sycntime != null) {
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
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmsg;
            } else {
                succ_bg = "0";
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }

  /*  private String get_contact_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "contact");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
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

    private String send_online_item() {
        String result = Item.sendOnServer(progressDialog,getApplicationContext(), database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax FROM item  WHERE is_push = 'N'", Globals.license_id);
        return result;
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
                        path = getPath(DataBaseActivity.this, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {

                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code").toString().trim(), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name").toString().trim(), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku").toString().trim(), jsonObject_bg1.getString("barcode").toString().trim(), jsonObject_bg1.getString("hsn_sac_code").toString().trim(), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

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

                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);
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
                        item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
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
                            item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
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

    /* private String get_item_from_server(String datetime) {
         String serverData = null;//
         DefaultHttpClient httpClient = new DefaultHttpClient();
         HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "item");
         ArrayList nameValuePairs = new ArrayList(5);
         nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
         nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
         nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
         nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
         nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
         nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
         nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
         nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
         nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
         nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
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
 */
    private String getTax(String serverData) {
        //  Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='tax'");

        String succ_manu = "0";
        database.beginTransaction();

        try {
            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");

            if (strStatus.equals("true")) {
                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");

                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id ='" + taxId + "'", database, db);


                    if (tax_master == null) {
                        tax_master = new Tax_Master(getApplicationContext(), jsonObject_tax1.getString("tax_id"), jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.insertTax_Master(database);
                        if (l > 0) {
                            succ_manu = "1";
                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                            long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                            if (odrtx > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.insertTax_Detail(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null,jsonObject_tax_group.getString("tax_master_id"),jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    } else {
                        tax_master = new Tax_Master(getApplicationContext(), taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.updateTax_Master("tax_id=? And location_id=?", new String[]{taxId, taxlocation}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            Order_Type_Tax order_type_tax5 = Order_Type_Tax.getOrder_Type_Tax(getApplicationContext(),"WHERE tax_id = '"+taxId+"' and order_type_id='"+jsonObject_od_typ_tax.getString("order_type_id")+"'",database);
                            if (order_type_tax5==null){
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }else {
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }

                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), jsonObject_tax_detail.getString("id"), jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.updateTax_Detail("tax_id=? And tax_type_id=?", new String[]{taxId, jsonObject_tax_detail.getString("tax_type_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");
                     //   Sys_Tax_Group sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + taxId + "'");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            systax_group = new Sys_Tax_Group(getApplicationContext(), jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_master_id"), jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = systax_group.updateSys_Tax_Group("tax_id=? And tax_master_id=?", new String[]{taxId}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                                succ_manu = "0";
                            }
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }
    /*private String get_tax_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "tax");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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

    private String send_online_tax() {

        String result = Tax_Master.sendOnServer(getApplicationContext(), database, db, "Select  * From tax  WHERE is_push = 'N'");
        return result;
    }

    private String send_online_unit() {

        String result = Unit.sendOnServer(getApplicationContext(), database, db, "Select  * From unit  WHERE is_push = 'N'");
        return result;
    }


    private String send_online() {
        Pos_Balance pos_balance=new Pos_Balance(getApplicationContext());
        String result = pos_balance.sendOnServer(pDialog,getApplicationContext(), database, db, "SELECT device_code,z_code,date,total_amount,is_active,modified_by  FROM  z_close Where is_push = 'N'", Globals.license_id, serial_no, android_id, myKey);

        return result;
    }


    private String getPaymentMethod(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                if (jsonArray_bg.length() > 0) {
                    long e6 = Payment.delete_Payment(getApplicationContext(), "payments", "", new String[]{}, database);
                    for (int i = 0; i < jsonArray_bg.length(); i++) {

                        JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                        Payment payment = new Payment(getApplicationContext(), jsonObject_bg1.getString("payment_id")
                                , jsonObject_bg1.getString("parent_id"),
                                jsonObject_bg1.getString("payment_name"),
                                jsonObject_bg1.getString("is_active"),
                                jsonObject_bg1.getString("modified_by"),
                                jsonObject_bg1.getString("modified_date"), "N");


                        long k = payment.insertPayment(database);
                        if (k > 0) {
                            succ_manu = "1";
                        }
                    }
                }
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (
                JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

   /* private String get_payment_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "payment");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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
*/


    private String getTableZone(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONObject jsonobject_result = jsonObject_bp.getJSONObject("result");
                JSONArray jsonArray_zone = jsonobject_result.optJSONArray("zones");
                if (jsonArray_zone.length() > 0) {
                    Long tbl = Table.deleteTable(getApplicationContext(), "tables", "", new String[]{}, database);
                    for (int i = 0; i < jsonArray_zone.length(); i++) {

                        JSONObject jsonObject_zone = jsonArray_zone.getJSONObject(i);
                        String zoneid = jsonObject_zone.getString("diz_id");
                        String zonename = jsonObject_zone.getString("diz_name");

                        JSONArray jsonArray_tables = jsonObject_zone.getJSONArray("tables");
                        for (int j = 0; j < jsonArray_tables.length(); j++) {
                            JSONObject jsonObject_table = jsonArray_tables.getJSONObject(j);
                            String isactive = jsonObject_table.getString("is_active");
                            String modifiedBy = jsonObject_table.getString("modified_by");
                            String modifieddate = jsonObject_table.getString("modified_date");
                            String tableid = jsonObject_table.getString("dit_id");
                            String tablename = jsonObject_table.getString("dit_name");

                            Table table = new Table(getApplicationContext(), null, tableid,
                                    tablename, zoneid, zonename, isactive, modifiedBy, modifieddate);


                            long k = table.insertTable(database);
                            if (k > 0) {
                                succ_manu = "1";
                            }
                        }
                    }
                }
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (
                JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }




    private String stock_post() {
        String suc = "0";
        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + Globals.strvoucherno + "' ", database);
        returns.set_is_post("true");
        long l = returns.updateReturns("voucher_no=?", new String[]{Globals.strvoucherno}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    /*
     * Gets the file path of the given Uri.
     */
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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getLocationStock() {
        String succ_bg = "";
        database.beginTransaction();
        String serverData ="";
        //get_loc_stock_from_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_code = jsonObject_bg1.getString("item_code");

                    item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
                    if (item_location == null) {
                    } else {
                        item_location.set_quantity(jsonObject_bg1.getString("quantity"));
                        long itmlc = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                        if (itmlc > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
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
            Globals.ErrorMsg = e.getMessage();
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }

  /*  private String get_loc_stock_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "item/location");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            //TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private String getUnit(String serverData) {

        String succ_manu = "0";
        database.beginTransaction();

        try {

            final JSONObject jsonObject_unit = new JSONObject(serverData);
            final String strStatus = jsonObject_unit.getString("status");
            final String strResult = jsonObject_unit.getString("result");
            final JSONObject jsonObject = new JSONObject(strResult);
            if (strStatus.equals("true")) {

                JSONArray jsonArray_unit = jsonObject.getJSONArray("unit");
                for (int i = 0; i < jsonArray_unit.length(); i++) {
                    JSONObject jsonObjct_unit = jsonArray_unit.getJSONObject(i);
                    String unitId = jsonObjct_unit.getString("unit_id");

                    unit = Unit.getUnit(getApplicationContext(), database, db, "where unit_id='" + unitId + "'");
                    if (unit == null) {
                        unit = new Unit(getApplicationContext(), null, jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.insertUnit(database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                    } else {
                        unit = new Unit(getApplicationContext(), unitId, jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.updateUnit("unit_id=?", new String[]{unitId}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

    /*private String get_unit_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "unit");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_data", ""));
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
*/

    // Volley Call

    public void getPayment_from_Server(final String registration_code) {

        /*pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "payment";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getPaymentMethod(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("reg_code", registration_code);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
// Table and Zone

    public void getZone_Table_fromserver(final ProgressDialog pDialog, final String flag) {

        /*pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "dine_in";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                             String result=getTableZone(response);

                            if(result.equals("1")){
                                if(flag.equals("2")){

                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Sync Table Successfully",Toast.LENGTH_LONG).show();
                                }
                                else{}
                            }
                            else{
                                if(flag.equals("2")){
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"No Table Data Found",Toast.LENGTH_LONG).show();

                                }
                                else{}
                            }


                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("location_id", Globals.objLPD.getLocation_Code());
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());
                System.out.println("params zone" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }






    public void getbusiness_grpfrom_server() {

       /* pDialog = new ProgressDialog(context);
        pDialog.setMessage(getApplicationContext().getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL +"business_group";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getBussinessGroup(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("company_id", Globals.objLPR.getcompany_id());

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getcontact_from_server(final ProgressDialog pDialog,final String datetime,final String serial_no,final String android_id,final String myKey) {

      /*  pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncingh));
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

                            if(Globals.objLPR.getIndustry_Type().equals("3")){
                                try {
                                    getPayment_from_Server(Globals.objLPR.getRegistration_Code());
                                } catch (Exception ex) {
                                }

                                try {
                                    //  send_online_user();
                                    get_user_from_server();
                                } catch (Exception ex) {
                                }

                                try {
                                    getInvoiceParameter_fromserver(pDialog,serial_no,android_id,myKey, Globals.objLPR.getLicense_No());
                                }
                                catch(Exception e){

                                }


                            }

                            else if(Globals.objLPR.getIndustry_Type().equals("4")){


                                try{
                                    //   result = send_online_unit();
                                    get_unit_from_server();
                                }
                                catch(Exception e){

                                }

                                try {
                                    //  result = send_online_tax();
                                    get_tax_from_server();
                                } catch (Exception ex) {
                                }


                                try {
                                    getPayment_from_Server(Globals.objLPR.getRegistration_Code());
                                } catch (Exception ex) {
                                }

                                try {
                                    //  send_online_user();
                                    get_user_from_server();
                                } catch (Exception ex) {
                                }

                                try {
                                    getMinCalculation_fromserver(Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);

                                }
                                catch(Exception e){

                                }

                                try {
                                    getInvoiceParameter_fromserver(pDialog,serial_no,android_id,myKey, Globals.objLPR.getLicense_No());
                                }
                                catch(Exception e){

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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }






    public void getitemgrp_from_server(final ProgressDialog pDialog,final String datetime,final String serial_no,final String android_id,final String myKey) {

      /*  pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "item_group";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getitemGroup(response);


                            try{
                                //   result = send_online_unit();
                                get_unit_from_server();
                            }
                            catch(Exception e){

                            }

                            try {
                                //  result = send_online_tax();
                                get_tax_from_server();
                            } catch (Exception ex) {
                            }



                            try {
                                // result = send_online_item();
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                                if (sys_sycntime == null) {
                                    get_item_from_server("", serial_no, android_id, myKey);
                                } else {
                                    get_item_from_server( sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                }
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }

                            try {
                                // result = send_online_contact(serialno,android,mykey);
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                //Call get contact api here
                                if (sys_sycntime==null){
                                    getcontact_from_server(pDialog,"",serial_no,android_id,myKey);
                                }else {
                                    getcontact_from_server(pDialog,sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                }


                            } catch (Exception ex) {
                            }
                            try {
                                //  send_online_user();
                                get_user_from_server();
                            } catch (Exception ex) {
                            }


                            try {
                                getPayment_from_Server(Globals.objLPR.getRegistration_Code());
                            } catch (Exception ex) {
                            }



                            try {
                                //  result = send_online_bussiness();
                                 getbusiness_grpfrom_server();

                            } catch (Exception ex) {
                            }





                            // call Item Location Stock Same Logic Copy Here
                          /*  try {

                                result = getLocationStock();
                            } catch (Exception e) {

                            }*/




                            try{
                                getScale_fromserver(serial_no,android_id,myKey, Globals.objLPR.getLicense_No());
                            }
                            catch(Exception e){

                            }

                            try{
                                getZone_Table_fromserver(pDialog,"1");

                            }catch(Exception e){

                            }


                            try {
                                getInvoiceParameter_fromserver(pDialog,serial_no,android_id,myKey, Globals.objLPR.getLicense_No());
                            }
                            catch(Exception e){

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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    public void get_item_from_server(final String datetime,final String serial_no,final String android_id,final String myKey) {

        /*pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncdataserver));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "item";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getitem(response);



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
                        // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("location_id", Globals.objLPD.getLocation_Code());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_tax_from_server() {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "tax";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getTax(response);
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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void get_user_from_server() {

     /*   pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncdataserver));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "user";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUser(response);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        //pDialog.dismiss();
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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void get_unit_from_server() {

      /*  pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "unit";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUnit(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext()   ,"Network not available", Toast.LENGTH_SHORT).show();

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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_data","");
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getInvoiceParameter_fromserver(final ProgressDialog pDialog,String serial_no,String android_id,String myKey,String liccustomerid) {

      /*  pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncingh));
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
                            if(result.equals("1")){
                                Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                lite_pos_device.setStatus("Out");
                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                if (ct > 0) {
                                    // database.endTransaction();
                                    Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent_category);
                                    finish();
                                }
                            }
                            else if(result.equals("2")){
                                Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();
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
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
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


    //  getScale Api

    public void getScale_fromserver(String serial_no,String android_id,String myKey,String liccustomerid) {

      /*  pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getApplicationContext().getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "scale_setup";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getScalesetup(response);

                            if(result.equals("1")){


                                //  Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();


                            }
                            else if(result.equals("2")){
                                Toast.makeText(getApplicationContext(),R.string.Data_sync_succ,Toast.LENGTH_LONG).show();

                                // finish();
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
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





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 7:

                if (resultCode == RESULT_OK) {
                    //   String displayName = null;
                    PathHolder = data.getData().getPath();
                    String fullPath = "";
                    try {
                        fullPath = Commons.getPath(data.getData(), getApplicationContext());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    progressDialog = new ProgressDialog(DataBaseActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.Importing_table_csv));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final String finalFullPath = fullPath;
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    sleep(200);
                                    File myFile = new File(finalFullPath);
                                    FileInputStream fIn = new FileInputStream(myFile);
                                    BufferedReader myReader = new BufferedReader(
                                            new InputStreamReader(fIn));
                                    String aDataRow = "";
                                    String aBuffer = "";
                                    database.beginTransaction();
                                    Long tbl = Table.deleteTable(getApplicationContext(), "tables", "", new String[]{}, database);

                                    while ((aDataRow = myReader.readLine()) != null) {
                                        List<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));

                                        Table table = new Table(getApplicationContext(), null, myList.get(0).replace("\"",""),
                                                myList.get(1).replace("\"",""),"","","","","");

                                        long l = table.insertTable(database);
                                        if (l > 0) {

                                            succ = "1";

                                        }

                                    }

                                    if (succ.equals("1")) {
                                        database.setTransactionSuccessful();
                                        database.endTransaction();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), R.string.Tables_Import_Successfully,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        database.endTransaction();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), R.string.Tables_Not_Import,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    myReader.close();
                                } catch (final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    };
                    t.start();
                    break;
                }
        }
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
