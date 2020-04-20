package org.phomellolitepos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;

public class DataBaseActivity extends AppCompatActivity {
    Button btn_payment, btn_database, btn_database_clear, btn_import_bank, btn_initialize, btn_all_sync, btn_push_order, btn_import_table;
    Database db;
    SQLiteDatabase database;
    ProgressDialog progressDialog;
    Item item;
    Item_Location item_location;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    Lite_POS_Registration lite_pos_registration;
    String succ = "0";
    ProgressDialog pDialog;
    String ck_project_type;
    User user;
    Returns returns;
    Settings settings;
    LinearLayout linear_layout3, linear_layout7;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    String serial_no, android_id, myKey, device_id,imei_no;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Database);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

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

                pDialog = new ProgressDialog(DataBaseActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(DataBaseActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else if (settings.get_Home_Layout().equals("2")) {
                            try {
                                Intent intent = new Intent(DataBaseActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(DataBaseActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        btn_database = (Button) findViewById(R.id.btn_database);
        btn_database_clear = (Button) findViewById(R.id.btn_database_clear);
        btn_push_order = (Button) findViewById(R.id.btn_push_order);
        btn_import_bank = (Button) findViewById(R.id.btn_import_bank);
        btn_initialize = (Button) findViewById(R.id.btn_initialize);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        btn_import_table = (Button) findViewById(R.id.btn_import_table);
        btn_all_sync = (Button) findViewById(R.id.btn_all_sync);
        linear_layout3 = (LinearLayout) findViewById(R.id.linear_layout3);
        linear_layout7 = (LinearLayout) findViewById(R.id.linear_layout7);
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
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        ck_project_type = lite_pos_registration.getproject_id();
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        if (ck_project_type.equals("standalone")) {
            btn_database_clear.setEnabled(true);
            btn_all_sync.setEnabled(false);
        } else {
            btn_all_sync.setEnabled(true);
        }

        if (Globals.Industry_Type.equals("3")) {
            linear_layout3.setVisibility(View.GONE);
            linear_layout7.setVisibility(View.GONE);
            btn_import_bank.setVisibility(View.GONE);
        } else if (Globals.Industry_Type.equals("6")) {
            linear_layout3.setVisibility(View.GONE);
            linear_layout7.setVisibility(View.GONE);
            btn_import_bank.setVisibility(View.GONE);
        }
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    try {

                        pDialog = new ProgressDialog(DataBaseActivity.this);
                        pDialog.setCancelable(false);
                        pDialog.setMessage(getString(R.string.GettingData));
                        pDialog.show();
                        new Thread() {
                            @Override
                            public void run() {

                                String suss = getPaymentMethod();
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
                                    sleep(200);
                                    String result;
                                    String suss;

                                    try {
                                        suss = getPaymentMethod();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        suss = getBankDetail();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        result = send_online();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        result = send_online_bussiness();
                                        suss = getBussinessGroup();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        result = send_online_item_group();
                                        suss = getitemGroup();
                                    } catch (Exception ex) {
                                    }


                                    try {
                                        result = send_online_contact();
                                        suss = getContact();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        result = send_online_item();
                                        suss = getitem();
                                    } catch (Exception ex) {
                                    }


                                    try {
                                        result = send_online_tax();
                                        suss = getTax();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        send_online_user();
                                        suss = getUser();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        result = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);




                                    } catch (Exception ex) {
                                    }

                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.Data_sync_succ, Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    final String str = "13245";
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
                                                initialize();
                                                progressDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Intent intent = new Intent(DataBaseActivity.this, SplashScreen.class);
                                                        startActivity(intent);
                                                        finish();
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
                                Toast.makeText(getApplicationContext(), R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_import_bank.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
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

                                                                       String suss = getBankDetail();
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

                                                    progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                    progressDialog.setTitle("");
                                                    progressDialog.setMessage(getString(R.string.Importing_table_csv));
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();
                                                    final Thread t = new Thread() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                try {
                                                                    sleep(200);
                                                                    File myFile = new File("/sdcard/table.csv");
                                                                    FileInputStream fIn = new FileInputStream(myFile);
                                                                    BufferedReader myReader = new BufferedReader(
                                                                            new InputStreamReader(fIn));
                                                                    String aDataRow = "";
                                                                    String aBuffer = "";
                                                                    database.beginTransaction();
                                                                    Long tbl = Table.deleteTable(getApplicationContext(), "tables", "", new String[]{}, database);

                                                                    while ((aDataRow = myReader.readLine()) != null) {
                                                                        List<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));

                                                                        Table table = new Table(getApplicationContext(), null, myList.get(0),
                                                                                myList.get(1));

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

                                                }
                                            }

        );

        btn_database_clear.setOnClickListener(new View.OnClickListener()

                                              {
                                                  @Override
                                                  public void onClick(View view) {
                                                      if (ck_project_type.equals("cloud")) {
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

                                                                                  final String str = "13245";
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
                                                                                                          clear_transaction_server();
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

                                                                                                  final String str = "13245";
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
                                                                                                              clear_tranaction();
                                                                                                              new Thread() {
                                                                                                                  @Override
                                                                                                                  public void run() {
                                                                                                                      getLastCode();
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
                                                          final String str = "13245";
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
                                                                      clear_tranaction();
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
                                                  }
                                              }

        );

        btn_push_order.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View view) {
                                                  if (isNetworkStatusAvialable(getApplicationContext())) {

                                                      try {
                                                          ck_project_type = lite_pos_registration.getproject_id();
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
                                                                  String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);
                                                                  String result = Returns.sendOnServer(getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'",liccustomerid,serial_no,android_id,myKey);

                                                                 progressDialog.dismiss();

                                                                  if (result_order.equals("1")) {
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


                                                                  if (result.equals("1")) {
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

        btn_database.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                progressDialog = new ProgressDialog(DataBaseActivity.this);
                                                progressDialog.setTitle("");
                                                progressDialog.setMessage(getString(R.string.Wait_msg));
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        DatabaseBackUp();
                                                        progressDialog.dismiss();
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), R.string.Database_Backup_Successfully, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }.start();
                                            }
                                        }
        );
    }

    private String getUser() {
        String succ_manu = "0";
        // Call get bussiness group api here
        String serverData = get_user_from_server();
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
                    user = User.getUser(getApplicationContext(), "WHERE user_code ='" + bg_code + "'", database);

                    if (user == null) {
                        user = new User(getApplicationContext(), null, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.insertUser(database);

                        if (l > 0) {
                            succ_manu = "1";
                        } else {
                        }

                    } else {
                        user = new User(getApplicationContext(), bg_id, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.updateUser("user_code=? And user_id=? ", database, new String[]{bg_code, bg_id});
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
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

    private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/user");
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

    private void send_online_user() {
        User.sendOnServer(getApplicationContext(), database, db, "Select  * From  user  WHERE is_push = 'N'");
    }

    private void clear_transaction_server() {

        String serverData = clear_from_server();
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

    private String clear_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/Dbdetail");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
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

    private String getBankDetail() {
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();
        String serverData = get_bank_from_server();
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

    private String get_bank_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/bank");
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
        pDialog = new ProgressDialog(DataBaseActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(DataBaseActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else if (settings.get_Home_Layout().equals("2")) {
                    try {
                        Intent intent = new Intent(DataBaseActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(DataBaseActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }


    private String DatabaseBackUp() {
        File backupDB = null;
        String backupDBPath = null;
        try {
            File sd = new File(Environment.getExternalStorageDirectory(), "LitePOS/Backup");

            if (!sd.exists()) {
                sd.mkdirs();
            }
            // File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = Database.DATABASE_FILE_PATH + File.separator + Database.DBNAME;
            backupDBPath = "LitePOS" + DateUtill.currentDatebackup() + ".db";
            File currentDB = new File(currentDBPath);
            backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

                Toast.makeText(getApplicationContext(), R.string.BackupSuccCreated,
                        Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return backupDBPath;
    }


    private void getLastCode() {
        Last_Code last_code;
        String serverData = getLastCodeFromServer();
        if (serverData == null) {
            System.exit(0);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Server Not Found", Toast.LENGTH_SHORT).show();
                }

            });
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

    private String getLastCodeFromServer() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/last_code");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1",serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
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

    private String getBussinessGroup() {
        String succ_manu = "0";
        // Call get bussiness group api here
        Bussiness_Group bussiness_group;
        database.beginTransaction();
        String serverData = get_bussiness_gp_from_server();
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

    private String get_bussiness_gp_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/business_group");
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

    private String send_online_bussiness() {
        String l = Bussiness_Group.sendOnServer(getApplicationContext(), database, db, "Select  * From business_group  WHERE is_push = 'N'");
        return l;
    }

    private String send_online_item_group() {
        String l = Item_Group.sendOnServer(getApplicationContext(), database, db, "Select * From item_group  WHERE is_push = 'N'",Globals.serialno,Globals.syscode2,Globals.androidid,Globals.mykey,liccustomerid);
        return l;
    }

    private String getitemGroup() {
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");
        String succ_bg = "0";
        // Call get item group api here
        database.beginTransaction();
        String serverData = get_item_gp_from_server(sys_sycntime.get_datetime());
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");
                    sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = item_group.insertItem_Group(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            } else {

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

    private String get_item_gp_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/item_group");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("modified_data", datetime));
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

    private String send_online_contact() {

        String conList = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'",liccustomerid,serial_no,android_id,myKey);
        return conList;
    }

    private String getContact() {
        Contact contact;
        Address address;
        Contact_Bussiness_Group contact_bussiness_group = null;
        Address_Lookup address_lookup;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here
        String serverData = get_contact_from_server(sys_sycntime.get_datetime());
        try {
            final JSONObject jsonObject_contact = new JSONObject(serverData);
            final String strStatus = jsonObject_contact.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_id = jsonObject_contact1.getString("contact_id");
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");
                    sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    if (contact == null) {
                        // contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N","0",jsonObject_contact1.getString("modified_date"),"0",jsonObject_contact1.getString("gstin"),jsonObject_contact1.getString("country_id"),jsonObject_contact1.getString("zone_id"));

                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new Address(getApplicationContext(), null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "N");
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
//                                            database.endTransaction();
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
//                                                    database.endTransaction();
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    } else {

                        // Edit on 18-Oct-2017
                        contact = new Contact(getApplicationContext(), contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));


                        // contact = new Contact(getApplicationContext(), contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N","0",jsonObject_contact1.getString("modified_date"),"0",jsonObject_contact1.getString("gstin"),jsonObject_contact1.getString("country_id"),jsonObject_contact1.getString("zone_id"));
                        long l = contact.updateContact("contact_code=? And contact_id=?", new String[]{contact_code, contact.get_contact_id()}, database);

                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = Address.getAddress(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);
                                address = new Address(getApplicationContext(), address.get_address_id(), jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "N");
                                long itmsp = address.updateAddress("address_code=? And address_id=?", new String[]{contact_code, address.get_address_id()}, database);

                                if (itmsp > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }


                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);

                                address_lookup = Address_Lookup.getAddress_Lookup(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);

                                long g = address_lookup.delete_Address_Lookup(getApplicationContext(), "address_code=?", new String[]{contact_code}, database);
                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                    //database.endTransaction();
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
            } else {
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

    private String get_contact_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/contact");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("modified_data", datetime));
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

    private String send_online_item() {
        String result = Item.sendOnServer(getApplicationContext(), database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax FROM item  WHERE is_push = 'N'",liccustomerid);
        return result;
    }

    private String getitem() {
        Item_Supplier item_supplier;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();
        String serverData = get_item_from_server(sys_sycntime.get_datetime());
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);
                    sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    if (item == null) {
                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), "0", jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), "");
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


                    } else {
                        item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), "0", jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), "");
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
                            item_location = new Item_Location(getApplicationContext(), item_location.get_item_location_id(), jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.updateItem_Location("item_code=? And item_location_id=? ", new String[]{item_code, item_location.get_item_location_id()}, database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                            } else {
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = Item_Supplier.getItem_Supplier(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
                            item_supplier = new Item_Supplier(getApplicationContext(), item_supplier.get_item_supplier_id(), jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            long itmsp = item_supplier.updateItem_Supplier("item_code=? And item_supplier_id=?", new String[]{item_code, item_supplier.get_item_supplier_id()}, database);

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

    private String get_item_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/item");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("modified_data", datetime));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
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

    private String getTax() {
        Tax_Master tax_master;
        Order_Type_Tax order_type_tax;
        Tax_Detail tax_detail;
        String succ_manu = "0";
        database.beginTransaction();
        String serverData = get_tax_from_server();
        try {

            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id ='" + taxId + "'", database, db);
                    if (tax_master == null) {
                        tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
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
                            order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                            long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
                            if (odrtx > 0) {
                                succ_manu = "1";


                            } else {
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

                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

    private String get_tax_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/tax");
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

    private String send_online_tax() {

        String result = Tax_Master.sendOnServer(getApplicationContext(), database, db, "Select  * From tax  WHERE is_push = 'N'");
        return result;
    }

    private String send_online() {
        String result = Pos_Balance.sendOnServer(getApplicationContext(), database, db, "SELECT device_code,z_code,date,total_amount,is_active,modified_by  FROM  z_close Where is_push = 'N'",liccustomerid);

        return result;
    }


    private String getPaymentMethod() {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();
        String serverData = get_payment_from_server();
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
                database.endTransaction();
            }
        } catch (
                JSONException e)

        {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

    private String get_payment_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/payment");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
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
}
