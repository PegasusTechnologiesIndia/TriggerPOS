
package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.google.gson.Gson;
import com.hoin.btsdk.BluetoothService;
import com.hoin.btsdk.PrintPic;
import com.itextpdf.text.ExceptionConverter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.DialogOrderTypeListAdapter;
import org.phomellolitepos.Adapter.DialogTableMainListAdapter;
import org.phomellolitepos.Adapter.ParentCategoryListAdapter;
import org.phomellolitepos.Adapter.RetailListAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.Fragment.ItemFragment;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.Base64;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.Util.ZipManager;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Customer_Image;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.VoidShoppingCart;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import au.com.bytecode.opencsv.CSVWriter;
import in.gauriinfotech.commons.Commons;
import sunmi.bean.SecondScreenData;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.Data;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

import static org.phomellolitepos.Util.Globals.StringSplit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ZBarScannerView.ResultHandler,ItemModifierSelection.ModifierListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    Address_Lookup address_lookup;
    private long pressedTime;
    org.phomellolitepos.database.Address address;
    RetailListAdapter retailListAdapter;
    Contact_Bussiness_Group contact_bussiness_group;
    Contact contact;
    MenuItem items;
    Button btn_syncdemo;
    ArrayList<String> arrayListGetFileImage;
    ArrayList<String> arrayListcategoryStr;
    Dialog listDialog;
    Dialog listDialog1;
    Dialog listDialog2;
    Dialog listDialog_table;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    Item item;
    AlertDialog.Builder builder;
    AlertDialog.Builder builder1;
    JavaEncryption javaEncryption;
    Item_Location item_location;
    SyncDialogCaller obj_syncdialog;
    String email, password;
    TextView tv_subcategory;
    private long mLastClickTime = 0;
    String ip = null;
    Order_Type order_type;
    private ArrayList<String> mylist = new ArrayList<String>();
        ArrayList<String> ipAdd = new ArrayList<String>();
    ArrayList<Item_Group> itemgroup_catArrayList;
    private ArrayList<String> catId = new ArrayList<String>();
    ArrayList<String> orderprint=new ArrayList<>();
    String strKitchenFlag="";
  //  String imageURL ="http://www.pegasustek.com/trigger-pos/upload/demo_files/images/india/";
// String imageURL ="http://192.168.2.72/trigger-pos/upload/demo_files/images/india/";

    TextView list_title, my_company_name, my_company_email, txt_user_name, txt_version;
    Button btn_Item_Price, btn_Qty;

    ArrayList<Item> arrayList;
    ArrayList<Fragment> fragList;
    private SearchView searchView;
    MenuItem searchItem;
    String operation = "";
    String code_category = "";
    String versionname;
    ArrayList<Item_Group> arrayListcategory;
    ArrayList<ShoppingCart> cart;
    SQLiteDatabase database;
    Tax_Master tax_master;
    Database db;
    ArrayList<Order_Type> order_typeArrayList;
    ArrayList<Contact> contact_ArrayList;
    ArrayList<Table> table_ArrayList;

    MenuItem orertype;
    DialogContactMainListAdapter dialogContactMainListAdapter;
    DialogOrderTypeListAdapter dialogOrderTypeListAdapter;
    DialogTableMainListAdapter dialogTableMainListAdapter;
    BottomNavigationView topNavigationView;
    BottomNavigationView bottomNavigationView;
    String decimal_check, qty_decimal_check, opr = "", strOrderCode = "";
    ArrayList<Order_Detail> order_detail = new ArrayList<Order_Detail>();
    ArrayList<Order_Tax> order_tax = new ArrayList<Order_Tax>();
    ArrayList<Order_Detail_Tax> order_detail_tax = new ArrayList<Order_Detail_Tax>();
    String modified_by;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int STATE_CONNECTED = 2;
    public static BluetoothService mService;
    public static BluetoothDevice con_dev;
    SearchableSpinner spn_item_category;

    String strRemarks = "";
    ArrayList<String> arrayListGetFile;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String displayTilte, line_total_af, line_total_bf;
    ProgressDialog pDialog;
    String PathHolder;
    boolean bValidate;
    ProgressDialog progressDialog;
    Order_Type_Tax order_type_tax;
    Tax_Detail tax_detail;
    Handler mHandler1;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    String path2 = Environment.getExternalStorageDirectory().getPath() + "/big.png";
    String path3 = Environment.getExternalStorageDirectory().getPath()
            + "/qrcode.png";

    String strSelectedCategory = "";
    View root;
    Animation animSideDown;
    DrawerLayout drawer;
    UserPermission userPermission;
    IWoyouService woyouService;
    private ICallback callback1 = null;
    String date;
    EditText searchEditText;
    ListView retail_list;
    String ck_project_type, item_group_code, sale_priceStr, cost_priceStr;
    Double curQty = 0d;
    int OrintValue;
    ZBarScannerView mScannerView;
    String serial_no, android_id, myKey, device_id, imei_no;
    Menu menu2;
    String reg_code;
    ProgressDialog p;
    Item_Supplier item_supplier;
    String temp;
    Double total;
    ArrayList<Order_Type_Tax> order_type_taxArrayList;
    ArrayList<Item> itemArrayList;
    Double iPrice = 0d;
    Double final_netamount = 0d;
    Sys_Tax_Group sys_tax_group;
    Double iTax = 0d;
    //AppLocationService appLocationService;
    String company_email, company_password;
    /**
     * 发送消息的回调
     */
    private ISendCallback callback = new ISendCallback() {

        @Override
        public void onSendFail(int arg0, String arg1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onSendProcess(long arg0, long arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSendSuccess(long arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    private IConnectionCallback mConnCallback = new IConnectionCallback() {
        @Override
        public void onDisConnect() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    connState.setText("连接断开了，请尝试重连");
//                    disEnableBtn();
                }
            });
        }

        @Override
        public void onConnected(final ConnState state) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case AIDL_CONN:
                            //connState.setText("与本地service的连接畅通");
                            break;
                        case VICE_SERVICE_CONN:
//                            connState.setText("与副屏service连接畅通");
//                            enableBtn();
                            break;
                        case VICE_APP_CONN:
//                            connState.setText("与副屏app连接畅通");
//                            enableBtn();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    };


    // 接收副屏数据的回调
    private IReceiveCallback mReceiveCallback = new IReceiveCallback() {

        @Override
        public void onReceiveFile(DSFile arg0) {
            // TODO
        }

        @Override
        public void onReceiveFiles(DSFiles dsFiles) {
            // TODO
        }

        @Override
        public void onReceiveData(DSData data) {
            if (dsPacket == null) return;
            long taskId = dsPacket.getData().taskId;
            Gson gson = new Gson();
            Data data4Json = gson.fromJson(data.data, Data.class);
            if (taskId == data.taskId) {
                final SecondScreenData secondScreenData = gson.fromJson(data4Json.data, SecondScreenData.class);
                Globals.AppLogWrite("Second screen data" + secondScreenData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //connState.setText(secondScreenData.toString());
                    }
                });
            }
        }

        @Override
        public void onReceiveCMD(DSData arg0) {
            // TODO
        }

    };

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    private final int MSG_TEST = 1;
    private long printCount = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                //testAll();
                long mm = MemInfo.getmem_UNUSED(MainActivity.this);
                if (mm < 100) {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 800);
                }
            }
        }
    };

    private void test() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    woyouService.printerSelfChecking(null);
                    woyouService.printText(" printed: " + printCount + " bills.\n\n\n\n", null);
                    printCount++;
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       /* StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
        super.onCreate(savedInstanceState);

        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        try {
            OrintValue = getApplicationContext().getResources().getConfiguration().orientation;

            if (OrintValue == Configuration.ORIENTATION_PORTRAIT) {
                Globals.OrientValue = OrintValue;
                setContentView(R.layout.activity_main);
                if (Globals.OrintFlagP) {
                    Load_Activity();
                }
                Globals.OrintFlagL = true;
                Globals.OrintFlagP = false;
            } else {
                Globals.OrientValue = OrintValue;
                setContentView(R.layout.activity_main_land);
                if (Globals.OrintFlagL) {
                    Load_Activity();
                }
                Globals.OrintFlagP = true;
                Globals.OrintFlagL = false;
            }


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            try {
                getSupportActionBar().setTitle("");
            } catch (Exception ex) {
            }

            try {
                db = new Database(getApplicationContext());
                database = db.getWritableDatabase();
            } catch (Exception e) {

            }


            javaEncryption = new JavaEncryption();
      /*  try {
            appLocationService = new AppLocationService(
                    MainActivity.this);
        } catch (Exception e) {
        }*/
            Globals.objLPD = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
            try {
                if (Globals.objLPD != null) {
                    Globals.license_id = Globals.objLPD.getLic_customer_license_id();
                }
            } catch (Exception e) {

            }


            Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            company_email = Globals.objLPR.getEmail();
            company_password = Globals.objLPR.getPassword();
            btn_syncdemo=(Button)findViewById(R.id.btn_syncdemo);
            tv_subcategory=(TextView)findViewById(R.id.txt_subcategory);
          /*  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);*/
            if(Globals.objLPR.getIndustry_Type().equals("3")){
                btn_syncdemo.setVisibility(View.VISIBLE);
            }
            else {
                if (item == null) {
                    btn_syncdemo.setVisibility(View.VISIBLE);
                }
            }

            btn_syncdemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    getAlertforDemoDB();
                }
            });
            if (Globals.objLPR.getproject_id().equals("cloud")) {

                item = Item.getItem(getApplicationContext(), "", database, db);
                if (item == null) {

                   // btn_syncdemo.setVisibility(View.VISIBLE);
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);

                    builder.setTitle(getString(R.string.alerttitle));
                    builder.setMessage(getString(R.string.alert_syncdata));

                    builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            try {

                                if (isNetworkStatusAvialable(getApplicationContext())) {
                                    progressDialog = new ProgressDialog(MainActivity.this);
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
                                                    obj_syncdialog = new SyncDialogCaller(MainActivity.this, database, db);
                                                    obj_syncdialog.sync_all(progressDialog, MainActivity.this, database, serial_no, android_id, myKey, Globals.license_id);

                                                    // progressDialog.dismiss();

                                               /* runOnUiThread(new Runnable() {
                                                    public void run() {

                                                     setReloadctivity();
                                                   }
                                                });
*/
                                              // btn_syncdemo.setVisibility(View.GONE);

                                                } catch (final Exception e) {
                                                    // progressDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } catch (Exception ex) {
                                                // progressDialog.dismiss();
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

                                //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
                            } catch (Exception e) {
                                Globals.AppLogWrite("Alert exception" + e.getMessage());
                            }

                        }
                    });

                    builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // btn_syncdemo.setVisibility(View.VISIBLE);
                            getAlertforDemoDB();
                            // Do nothing

                        }
                    });

                    androidx.appcompat.app.AlertDialog alert = builder.create();
                    alert.show();


                }
                else{

                    btn_syncdemo.setVisibility(View.GONE);
                }
            }

            else if (Globals.objLPR.getproject_id().equals("standalone")){
                item = Item.getItem(getApplicationContext(), "", database, db);
                if (item != null) {
                    btn_syncdemo.setVisibility(View.GONE);

                }
            }

            try {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                Date d = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = df.format(d);


/*            if (appLocationService.canGetLocation()) {


                double longitude = appLocationService.getLongitude();
                double latitude = appLocationService.getLatitude();
                Globals.latitude = String.valueOf(latitude);
                Globals.longitude = String.valueOf(longitude);
                getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

            } else {
                if (Globals.gpsFlag == true) {
                    appLocationService.showSettingsAlert();
                }
            }*/
                try {
                    backgroundLocationJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }
            Thread t = new Thread() {
                public void run() {
                    try {
                        Intent serviceIntent = new Intent(MainActivity.this, BackgroundApiService.class);
                        startService(serviceIntent);
                    } catch (Exception e) {
                    }
                }
            };t.start();
            final Intent intent = getIntent();


            try {
                if(mScannerView!=null) {
                    mScannerView = new ZBarScannerView(MainActivity.this);
                    mScannerView.setResultHandler(MainActivity.this);
               }
            } catch (Exception e) {

            }
            Globals.objsettings = Settings.getSettings(getApplicationContext(), database, "");
            if (Globals.objsettings == null) {
                Globals.PrinterType = "";
            } else {
                try {
                    Globals.PrinterType = Globals.objsettings.getPrinterId();
                    Globals.strIsBarcodePrint = Globals.objsettings.get_Is_BarcodePrint();
                    Globals.strIsDenominationPrint = Globals.objsettings.get_Is_Denomination();
                    Globals.strIsDiscountPrint = Globals.objsettings.get_Is_Discount();
                    Globals.GSTNo = Globals.objsettings.get_Gst_No();
                    Globals.GSTLbl = Globals.objsettings.get_GST_Label();
                    Globals.PrintOrder = Globals.objsettings.get_Print_Order();
                    Globals.PrintCashier = Globals.objsettings.get_Print_Cashier();
                    Globals.PrintInvNo = Globals.objsettings.get_Print_InvNo();
                    Globals.PrintInvDate = Globals.objsettings.get_Print_InvDate();
                    Globals.PrintDeviceID = Globals.objsettings.get_Print_DeviceID();
                } catch (Exception ex) {
                    Globals.PrinterType = "";
                }
            }

            callback1 = new ICallback.Stub() {

                @Override
                public void onRunResult(final boolean success) throws RemoteException {
                }

                @Override
                public void onReturnString(final String value) throws RemoteException {
                }

                @Override
                public void onRaiseException(int code, final String msg) throws RemoteException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            };

            try {
                Intent intent1 = new Intent();
                intent1.setPackage("woyou.aidlservice.jiuiv5");
                intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
                startService(intent1);
                bindService(intent1, connService, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
            }
            Date d = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            date = format.format(d);

            if (Globals.objsettings.get_Is_Customer_Display() == null) {
            } else {
                if (Globals.objsettings.get_Is_Customer_Display().equals("true")) {
                    ArrayList<Customer_Image> arrayList = Customer_Image.getAllCustomer_Image(getApplicationContext(), "", database);
                    if (arrayList.size() > 0) {
                        for (int count = 0; count < arrayList.size(); count++) {
                            ArrayList<String> arrayListImages = new ArrayList<>();
                            arrayListImages.add(arrayList.get(count).get_image_Path());
                            Globals.CMD_Images = arrayListImages;
                        }
                        Globals.AppLogWrite("Customer Display " + arrayList.toString());
                    }

                }

                if (Globals.objsettings.get_Is_Customer_Display().equals("true")) {
                    mDSKernel = DSKernel.newInstance();
                    mDSKernel.checkConnection();
                    mDSKernel.init(getApplicationContext(), mConnCallback);
                    mDSKernel.addReceiveCallback(mReceiveCallback);
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                                if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                    displayTilte = Globals.objLPR.getCompany_Name();
                                } else {
                                    displayTilte = Globals.objLPR.getShort_companyname();
                                }
                                Globals.AppLogWrite("Display Title" + displayTilte);
                                if (Globals.objsettings.get_CustomerDisplay().equals("1")) {

                                    call_CMD();
                                    call_MN(displayTilte);
                                } else {
                                    call_customer_disply_title(displayTilte);
                                    call_MN(displayTilte);
                                }
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                }
            }


            if (Globals.objsettings.getPrinterId().equals("3") || Globals.objsettings.getPrinterId().equals("4") || Globals.objsettings.getPrinterId().equals("5")) {
                if (Globals.strIsBlueService.equals("utc")) {
                    mService = new BluetoothService(getApplicationContext(), mHandler);
                    Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                    serverIntent.putExtra("flag", "MainActivity");

                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } else if (con_dev == null) {
                    mService = new BluetoothService(getApplicationContext(), mHandler);
                    Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                    serverIntent.putExtra("flag", "MainActivity");
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }

            root = findViewById(R.id.anim_root);
            topNavigationView = (BottomNavigationView) findViewById(R.id.top_navigation);
            spn_item_category=(SearchableSpinner)findViewById(R.id.spinner_item_category);

//        BottomNavigationViewHelper.disableShiftMode(topNavigationView);
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            modified_by = Globals.user;
            serial_no = Build.SERIAL;
            Globals.serialno = serial_no;

            android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Globals.androidid = android_id;
            myKey = serial_no + android_id;
            Globals.mykey = myKey;

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
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei_no = mTelephony.getImei();
            }*/
            if (intent != null) {
                operation = intent.getStringExtra("operation");
                code_category = intent.getStringExtra("code");
                opr = intent.getStringExtra("opr");
                strOrderCode = intent.getStringExtra("order_code");
                Globals.Order_Code = strOrderCode;
                Globals.Operation = opr;
            }

            try {
                decimal_check = Globals.objLPD.getDecimal_Place();
                qty_decimal_check = Globals.objsettings.get_Qty_Decimal();
            } catch (Exception ex) {
                decimal_check = "1";
            }


            if (opr == null) {
                Globals.cart.clear();
                Globals.TotalItemPrice = 0;
                Globals.TotalQty = 0;

                opr = "Add";
                Globals.Operation = opr;

            } else if (opr.equals("") || opr.equals("Add")) {
                //Change for landscap
//            String item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                String item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                setTextView(item_price, Globals.TotalQty + "");
                Menu menu = bottomNavigationView.getMenu();
                orertype = menu.findItem(R.id.action_clear);
                orertype.setEnabled(true);
            } else if (opr.equals("Resv")) {
                String item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                setTextView(item_price, Globals.TotalQty + "");
                Menu menu = bottomNavigationView.getMenu();
                orertype = menu.findItem(R.id.action_clear);
                orertype.setEnabled(false);
            } else {
                Menu menu = bottomNavigationView.getMenu();
                orertype = menu.findItem(R.id.action_clear);
                orertype.setEnabled(false);
            }

            if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {
                retail_list_load();
            }

            change_customer_icon();

            if (opr.equals("Edit")) {
                if (Globals.load_form_cart.equals("0")) {
                    ShoppingCart cartItem = null;
                   Globals.cart.clear();
                    Globals.order_item_tax.clear();
                    Globals.TotalItemPrice = 0;
                    Globals.TotalQty = 0;

                    order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);


                    for (int i = 0; i < order_detail.size(); i++) {
                        String strItemCode = order_detail.get(i).get_item_code();

                        Item item = Item.getItem(getApplicationContext(), " WHERE item_code = '" + strItemCode + "'", database, db);
                        Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item.get_item_group_code() + "'");
                          String masterCode="0";
                        if(item.getIs_modifier().equals("1")){
                            ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + item.get_item_code() + "'");

                            masterCode= dtl_modifier.getItem_code();
                        }
                        cartItem = new ShoppingCart(getApplicationContext(), order_detail.get(i).get_sr_no(), order_detail.get(i).get_item_code(), item.get_item_name(), order_detail.get(i).get_quantity(), order_detail.get(i).get_cost_price(), order_detail.get(i).get_sale_price(), order_detail.get(i).get_tax(), "0", order_detail.get(i).get_line_total(),item.getIs_modifier(),masterCode,item_group.getCategoryIp(),order_detail.get(i).getIs_KitchenPrintFlag(),order_detail.get(i).getUnit_id(),order_detail.get(i).getBeforeTaxPrice());
                        Globals.cart.add(cartItem);

                        Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(order_detail.get(i).get_line_total());
                        Globals.TotalQty = Globals.TotalQty + Integer.parseInt(order_detail.get(i).get_quantity());
                    }

                    Globals.SRNO = Globals.cart.size() + 1;

                    order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);
                    for (int i = 0; i < order_detail_tax.size(); i++) {
                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", order_detail_tax.get(i).get_order_code(), order_detail_tax.get(i).get_sr_no(), order_detail_tax.get(i).get_item_code(), order_detail_tax.get(i).get_tax_id(), order_detail_tax.get(i).get_tax_type(), order_detail_tax.get(i).get_rate(), order_detail_tax.get(i).get_tax_value());
                        Globals.order_item_tax.add(order_item_tax);
                    }

                    String odr_type_id;
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderCode + "'");
                    odr_type_id = orders.get_order_type_id();
                    Globals.strOrder_type_id = odr_type_id;
                    Menu menu = topNavigationView.getMenu();
                    orertype = menu.findItem(R.id.action_order_type);
                    order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + odr_type_id + "'", database, db);
                    try {
                        String strOrder_name = order_type.get_name();
                        orertype.setTitle(strOrder_name);
                    } catch (Exception ex) {
                    }

                    switch (odr_type_id) {
                        case "1":
                            orertype.setIcon(R.drawable.deliver);
                            break;
                        case "2":
                            orertype.setIcon(R.drawable.drive_thrue);
                            break;
                        case "3":
                            orertype.setIcon(R.drawable.pick_up);
                            break;
                        case "4":
                            orertype.setIcon(R.drawable.take_out);
                            break;
                        case "5":
                            orertype.setIcon(R.drawable.dine_ine);
                            break;
                        default:
                            orertype.setIcon(R.drawable.deliver);
                            break;
                    }

                    Orders orders1 = Orders.getOrders(getApplicationContext(), database, "WHERE order_code='" + strOrderCode + "'");
                    try {
                        String strtableCode = orders1.get_table_code();
                        Globals.strTable_Code = strtableCode;
                    } catch (Exception ex) {
                    }
                    Globals.strOrder_type_id=orders1.get_order_type_id();
                    if(Globals.strOrder_type_id.equals("5")){
                        Table table = Table.getTable(getApplicationContext(), database, db, " WHERE table_code='" + orders1.get_table_code() + "'");

                        Globals.strorderType="Dine-In";
                        Globals.table_code=table.get_table_code();
                        Globals.table_name=table.get_table_name();
                        Globals.strZoneName=table.getZone_name();
                    }
                    try {
                        String strContactCode = orders1.get_contact_code();
                        Globals.strContact_Code = strContactCode;
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        line_total_af = intent.getStringExtra("line_total_af");
                        line_total_bf = intent.getStringExtra("line_total_bf");
                        Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                        Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                    } catch (Exception ex) {
                    }
                }

                if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                    retail_list_load();
                }

                change_customer_icon();
            } else {
                try {
                    line_total_af = intent.getStringExtra("line_total_af");
                    line_total_bf = intent.getStringExtra("line_total_bf");
                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                } catch (Exception ex) {
                }
                defult_ordertype();
        }

            arrayListcategory = new ArrayList<Item_Group>();
            cart = new ArrayList<ShoppingCart>();
            btn_Item_Price = (Button) findViewById(R.id.btn_Item_Price);
            btn_Qty = (Button) findViewById(R.id.btn_Qty);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
          //  viewPager.beginFakeDrag();
          //  viewPager.beginFakeDrag();
            list_title = (TextView) findViewById(R.id.list_title);

            fragList = new ArrayList<Fragment>();
            if (!Globals.Industry_Type.equals("3")) {

                  try{
                    setCategoryAdapter();
                }
                catch(ExceptionConverter e)
                { }
                //setupViewPager(viewPager, "Main", "", "", "");
                btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                String itemPrice;
                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_Item_Price.setText(itemPrice);
                //tabLayout.setupWithViewPager(viewPager);
            } else {
           btn_syncdemo.setVisibility(View.VISIBLE);
                btn_Qty.setVisibility(View.GONE);
                btn_Item_Price.setVisibility(View.GONE);
               // tabLayout.setVisibility(View.GONE);
            }


            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
               /* FragmentManager fragMan = getSupportFragmentManager();
                if (fragList.size() > 0) {
                    for (int i = 0; i < fragList.size(); i++) {
                        fragMan.beginTransaction().remove(fragList.get(i)).commit();
                        fragList.clear();
                        fragList = new ArrayList<android.support.v4.app.Fragment>();
                        adapter.notifyDataSetChanged();
                    }
                }*/


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    String ItemCategoryCode = arrayListcategory.get(tab.getPosition()).get_item_group_code();
                   // call_parent_dialog(ItemCategoryCode);
                }
            });

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);
            my_company_name = (TextView) hView.findViewById(R.id.my_company_name);
            my_company_email = (TextView) hView.findViewById(R.id.my_company_email);
            txt_user_name = (TextView) hView.findViewById(R.id.txt_user_name);
            txt_version = (TextView) hView.findViewById(R.id.txt_version);
            Menu menu = navigationView.getMenu();
            MenuItem nav_item;


            // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            /*        if (Globals.objLPR.getproject_id().equals("cloud")) {
             *//*     nav_item = menu.findItem(R.id.nav_purchese);
                nav_item.setVisible(false);
                nav_item = menu.findItem(R.id.nav_stock_adjest);
                nav_item.setVisible(false);*//*
            // menu.findItem(R.id.nav_get_file).setVisible(false);

        } else {
            menu.findItem(R.id.nav_return).setVisible(false);
            menu.findItem(R.id.nav_acc).setVisible(false);
            menu.findItem(R.id.nav_get_file).setVisible(false);

        }*/


            if (Globals.objLPR.getproject_id().equals("cloud")) {
                menu.findItem(R.id.nav_payment).setVisible(false);


            } else {
                menu.findItem(R.id.nav_payment).setVisible(true);
                menu.findItem(R.id.nav_return).setVisible(false);
                menu.findItem(R.id.nav_acc).setVisible(false);

                //  menu.findItem(R.id.nav_get_file).setVisible(false);

            }


      /*  menu.findItem(R.id.nav_purchese).setVisible(false);
        menu.findItem(R.id.nav_stock_adjest).setVisible(false);
*/
            // Industry_Type 3 for payment collection
            // Industry_Type 1 for restaurant
            // Industry_Type 2 for saloon
            // Industry_Type 5 for retail
            // Industry_Type 4 for parking
      /*  if (Globals.Industry_Type.equals("1")) {
            menu.findItem(R.id.nav_resv).setVisible(false);
            menu.findItem(R.id.nav_pay_collection).setVisible(false);
            menu.findItem(R.id.nav_manufacture).setVisible(false);
           menu.findItem(R.id.nav_class).setVisible(false);
            menu.findItem(R.id.nav_destination).setVisible(false);
             menu.findItem(R.id.nav_ticketing).setVisible(false);
            menu.findItem(R.id.nav_setup).setVisible(false);
             menu.findItem(R.id.nav_search_order).setVisible(false);

        }
*/


            NavigationView navigationViewRight = (NavigationView) findViewById(R.id.right_nav_view);

//        View hView1 = navigationViewRight.getHeaderView(0);
//        my_company_name = (TextView) hView1.findViewById(R.id.my_company_name);
//        my_company_email = (TextView) hView1.findViewById(R.id.my_company_email);
//        txt_user_name = (TextView) hView1.findViewById(R.id.txt_user_name);

            Menu menu1 = navigationViewRight.getMenu();

            if(Globals.objLPR.getproject_id().equals("standalone")){
                menu1.findItem(R.id.nav_get_itemimages).setVisible(false);

            }
            // MenuItem nav_item1;

            // Industry_Type 3 for payment collection
            // Industry_Type 1 for restruant
            // Industry_Type 2 for saloon
            // Industry_Type 5 for retail
      /*  if (Globals.objLPR.getproject_id().equals("standalone")) {
             menu1.findItem(R.id.nav_coupon).setVisible(false);
             menu1.findItem(R.id.nav_loyalty).setVisible(false);
            menu1.findItem(R.id.nav_get_file).setVisible(false);
           menu1.findItem(R.id.nav_get_itemimages).setVisible(false);
        } else {
            menu1.findItem(R.id.nav_coupon).setVisible(false);
           menu1.findItem(R.id.nav_loyalty).setVisible(false);
             menu1.findItem(R.id.nav_get_file).setVisible(false);
        }*/

//        if (lite_pos_registration.getproject_id().equals("cloud")) {
////            nav_item1 = menu1.findItem(R.id.nav_coupon);
////            nav_item1.setVisible(false);
////            nav_item1 = menu1.findItem(R.id.nav_loyalty);
////            nav_item1.setVisible(false);
//        }

            navigationView.setNavigationItemSelectedListener(this);

            Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            try {
                PackageInfo pInfo = null;
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                my_company_name.setText(Globals.objLPR.getCompany_Name());
                my_company_email.setText(Globals.objLPR.getEmail());
                txt_user_name.setText(user.get_name());
                email = user.get_email();
                password = user.get_password();
                Globals.userId=user.get_user_id();
                txt_version.setText("Version : " + pInfo.versionName);
                versionname = pInfo.versionName;

            } catch (Exception ex) {
            }

            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_check_out:
                                    if (Globals.cart.size() == 0) {
                                        Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_LONG).show();
                                    } else {
                                     /*   pDialog = new ProgressDialog(MainActivity.this);
                                        pDialog.setCancelable(false);
                                        pDialog.setMessage(getString(R.string.Wait_msg));
                                        pDialog.show();
                                        Thread timerThread = new Thread() {
                                            public void run() {*/
                                                try {
                                                    //sleep(100);
                                                    //pDialog.dismiss();
                                                    Globals.Order_Code = "";
                                                    Globals.Operation = "";
                                                    Globals.CMDItemPrice = 0;
                                                    Globals.CMDItemName = "";
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                                                            intent.putExtra("opr", opr);
                                                            intent.putExtra("order_code", strOrderCode);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } finally {
                                                }
                                          /*  }
                                        };
                                        timerThread.start();*/
                                    }


                                    break;
                                case R.id.action_save:
                                    if (Globals.objsettings.get_Is_Customer_Display().equals("true")) {
                                        mDSKernel = DSKernel.newInstance();
                                        mDSKernel.checkConnection();
                                        mDSKernel.init(getApplicationContext(), mConnCallback);
                                        mDSKernel.addReceiveCallback(mReceiveCallback);
                                        Thread timerThread = new Thread() {
                                            public void run() {
                                                try {
                                                    //Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                                        displayTilte = Globals.objLPR.getCompany_Name();
                                                    } else {
                                                        displayTilte = Globals.objLPR.getShort_companyname();
                                                    }
                                                    if (Globals.objsettings.get_CustomerDisplay().equals("1")) {
                                                        call_CMD();
                                                        call_MN(displayTilte);
                                                    } else {
                                                        call_customer_disply_title(displayTilte);
                                                        call_MN(displayTilte);
                                                    }
                                                } finally {
                                                }
                                            }
                                        };
                                        timerThread.start();
                                    }
                                    if (Globals.cart.size() == 0) {
                                        Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Globals.CheckContact = "0";
                                        pDialog = new ProgressDialog(MainActivity.this);
                                        pDialog.setCancelable(false);
                                        pDialog.setMessage(getString(R.string.Wait_msg));
                                        pDialog.show();
                                        Thread timerThread = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(100);
                                                    pDialog.dismiss();
                                                    Globals.Order_Code = "";
                                                    Globals.CMDItemPrice = 0;
                                                    Globals.CMDItemName = "";
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            if (opr.equals("Edit")) {
                                                                save_order(strRemarks);
                                                                if(Globals.objsettings.getIs_KitchenPrint().equals("true")) {
                                                                    progressDialog = new ProgressDialog(MainActivity.this);
                                                                    progressDialog.setTitle("");
                                                                    progressDialog.setMessage(getString(R.string.waiting));
                                                                    progressDialog.setCancelable(false);
                                                                    progressDialog.show();
                                                                    Thread t = new Thread() {
                                                                        @Override
                                                                        public void run() {
                                                                            try {
                                                                                printKOT(strOrderCode, progressDialog);
                                                                                progressDialog.dismiss();

                                                                                if (Globals.objsettings.getIs_saveprint().equals("true")) {

                                                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                                                    alertDialogBuilder.setTitle("Are You Sure");
                                                                                    alertDialogBuilder.setMessage("Do you want to Print Save Order");
                                                                                    alertDialogBuilder.setPositiveButton("yes",
                                                                                            new DialogInterface.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                                                    // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                                                                                    // if (Globals.objsettings.get_Is_Print_Invoice().equals("true")) {
                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        public void run() {

if(Globals.PrinterType.equals("0")){
     Globals.setEmpty();
                                                                                            opr = "Add";
                                                                                            Globals.Operation = opr;
                                                                                            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                            String itemPrice;
                                                                                            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                            btn_Item_Price.setText(itemPrice);
                                                                                            defult_ordertype();
                                                                                            clear_btn_enable();
                                                                                            if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                                                                                retail_list_load();
                                                                                            }
                                                                                            change_customer_icon();
                                                                                            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
}

    else {
    Intent launchIntent = new Intent(MainActivity.this, PrintLayout.class);
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    launchIntent.putExtra("strOrderNo", strOrderCode);
    startActivity(launchIntent);
}

                                                                                                        }
                                                                                                    });
                                                                                                    // }

                                                                                                }
                                                                                            });

                                                                                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            Globals.setEmpty1();
                                                                                            opr = "Add";
                                                                                            Globals.Operation = opr;
                                                                                            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                            String itemPrice;
                                                                                            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                            btn_Item_Price.setText(itemPrice);
                                                                                            defult_ordertype();
                                                                                            clear_btn_enable();
                                                                                            if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                                                                                retail_list_load();
                                                                                            }
                                                                                            change_customer_icon();
                                                                                            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });

                                                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                                                    alertDialog.show();
                                                                                } else {
                                                                                    Globals.setEmpty();
                                                                                    try {
                                                                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                                                        startActivity(i);
                                                                              /*  btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);*/
                                                                                    } catch (Exception e) {
                                                                                        System.out.println(e.getMessage());
                                                                                    }
                                                                                }
                                                                            } catch (Exception e) {
                                                                                System.out.println(e.getMessage());
                                                                            }

                                                                        }
                                                                    };
                                                                    t.start();
                                                                }
                                                                else{
                                                                    if (Globals.objsettings.getIs_saveprint().equals("true")) {

                                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                                        alertDialogBuilder.setTitle("Are You Sure");
                                                                        alertDialogBuilder.setMessage("Do you want to Print Save Order");
                                                                        alertDialogBuilder.setPositiveButton("yes",
                                                                                new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                                                        // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                                                                        // if (Globals.objsettings.get_Is_Print_Invoice().equals("true")) {
                                                                                        runOnUiThread(new Runnable() {
                                                                                            public void run() {


                                                                                                Intent launchIntent = new Intent(MainActivity.this, PrintLayout.class);
                                                                                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                launchIntent.putExtra("strOrderNo", strOrderCode);
                                                                                                startActivity(launchIntent);

                                                                                            }
                                                                                        });
                                                                                        // }

                                                                                    }
                                                                                });

                                                                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                Globals.setEmpty1();

                                                                                opr = "Add";
                                                                                Globals.Operation = opr;
                                                                                btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);
                                                                                defult_ordertype();
                                                                                clear_btn_enable();
                                                                                if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                                                                    retail_list_load();
                                                                                }
                                                                                change_customer_icon();
                                                                                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                                        alertDialog.show();
                                                                    } else {
                                                                        Globals.setEmpty();
                                                                        try {
                                                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                                            startActivity(i);
                                                                              /*  btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);*/
                                                                        } catch (Exception e) {
                                                                            System.out.println(e.getMessage());
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                showdialogremarksdialog();
                                                            }
                                                        }
                                                    });
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                }
                                            }
                                        };
                                        timerThread.start();
                                    }
                                    break;
                                case R.id.action_clear:
                                    if (Globals.objsettings.get_Is_Customer_Display().equals("true"))
                                    {
                                        mDSKernel = DSKernel.newInstance();
                                        mDSKernel.checkConnection();
                                        mDSKernel.init(getApplicationContext(), mConnCallback);
                                        mDSKernel.addReceiveCallback(mReceiveCallback);
                                        Thread timerThread = new Thread() {
                                            public void run() {
                                                try {
                                                    //Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                                        displayTilte = Globals.objLPR.getCompany_Name();
                                                    } else {
                                                        displayTilte = Globals.objLPR.getShort_companyname();
                                                    }
                                                    if (Globals.objsettings.get_CustomerDisplay().equals("1")) {
                                                        call_CMD();
                                                        call_MN(displayTilte);
                                                    } else {
                                                        call_customer_disply_title(displayTilte);
                                                        call_MN(displayTilte);
                                                    }
                                                } finally {
                                                }
                                            }
                                        };
                                        timerThread.start();
                                    }
                                   // change_table_icon();
                                    defult_ordertype();
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Globals.CheckContact = "0";
                                    Globals.NoTax="";
                                    Globals.Taxwith_state="";
                                    Globals.Taxdifferent_state="";
                                    setContactAction();
                                    String cart_check2 = Globals.TotalItemPrice + "";
                                    if (Globals.cart.size() == 0) {
                                        change_customer_icon();
                                        Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                    } else {
                                        change_customer_icon();
                                        Globals.setEmpty();
                                        btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                        String itemPrice;
                                        itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                        btn_Item_Price.setText(itemPrice);
                                        if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                            retail_list_load();
                                        }
                                    }
                                    break;
                                case R.id.action_whatsapphelp:
                                    openWhatsApp();
                            }
                            return true;
                        }
                    });

            topNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        //order
                        case R.id.action_order_type:
                            Order_Type order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE is_active ='1'", database, db);
                            if (order_type == null) {
                                Toast.makeText(getApplicationContext(), R.string.No_Order_Type_Fnd, Toast.LENGTH_SHORT).show();
                            } else {
                                showdialog();
                            }
                            break;

                        //contact
                        case R.id.action_category:
                            if (Globals.objsettings.get_Is_BR_Scanner_Show().equals("true")) {
                                try {
                                    Globals.BarcodeReslt = "";
                                    mScannerView.startCamera(); // Programmatically initialize the scanner view
                                    setContentView(mScannerView);
                                } catch (Exception ex) {
                                }
                            } else {
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE is_active ='1'");
                                if (contact == null) {
                                    Toast.makeText(getApplicationContext(), R.string.No_Contact_Found, Toast.LENGTH_SHORT).show();
                                } else {
                                    showdialogContact();
                                }
                            }

                            break;

                        //order
                        case R.id.action_music:
                            Intent intent_retail = new Intent(MainActivity.this, RetailActivity.class);
                            intent_retail.putExtra("opr1", opr);
                            intent_retail.putExtra("order_code1", strOrderCode);
                            startActivity(intent_retail);
                            finish();
                            break;

                        //reset
                        case R.id.action_reset:

//                          setupViewPager(viewPager, "Main", "", "", "");
                            if (Globals.objsettings.get_Is_Customer_Display().equals("true"))
                            {
                                mDSKernel = DSKernel.newInstance();
                                mDSKernel.checkConnection();
                                mDSKernel.init(getApplicationContext(), mConnCallback);
                                mDSKernel.addReceiveCallback(mReceiveCallback);
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            //Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                            if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                                displayTilte = Globals.objLPR.getCompany_Name();
                                            } else {
                                                displayTilte = Globals.objLPR.getShort_companyname();
                                            }
                                            if (Globals.objsettings.get_CustomerDisplay().equals("1")) {
                                                call_CMD();
                                                call_MN(displayTilte);
                                            } else {
                                                call_customer_disply_title(displayTilte);
                                                call_MN(displayTilte);
                                            }
                                        } finally {
                                        }
                                    }
                                };
                                timerThread.start();
                            }
                            // change_table_icon();
                            defult_ordertype();
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.CheckContact = "0";
                            Globals.NoTax="";
                            Globals.Taxwith_state="";
                            Globals.Taxdifferent_state="";
                            setContactAction();
                            String cart_check2 = Globals.TotalItemPrice + "";
                            if (Globals.cart.size() == 0) {
                                change_customer_icon();
                            } else {
                                change_customer_icon();
                                Globals.setEmpty();
                                btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);
                                if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                    retail_list_load();
                                }
                            }

                            Intent intent=new Intent(MainActivity.this,MainActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            });

            setContactAction();


            NavigationView rightNavigationView = (NavigationView) findViewById(R.id.right_nav_view);
            rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    // Handle Right navigation view item clicks here.
                    int id = item.getItemId();
                    Globals.SRNO = 1;
// Tax Activity
                    if (id == R.id.nav_tax) {
                        userPermission = new UserPermission();

                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "Tax", TaxListActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }
// user Activity
                    } else if (id == R.id.nav_user) {

                        userPermission = new UserPermission();
                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "User", UserListActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }
                        }
                        else{

                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }
                    }
                    // Unit Activity
                    else if (id == R.id.nav_unit) {
                        userPermission = new UserPermission();
                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "Unit", UnitListActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }
                    }
                    // Database Activity
                    else if (id == R.id.nav_database) {
                        userPermission = new UserPermission();
                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "Database", DataBaseActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    }

                    // Updtae License (Activate Activity)
                    else if (id == R.id.nav_lic) {
                        userPermission = new UserPermission();
                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "Update License", ActivateActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;

                        }

                    }
                    // Profile Activity
                    else if (id == R.id.nav_profile) {
                        userPermission = new UserPermission();
                        if(Globals.cart.size()==0) {
                            Boolean result = userPermission.Permission(MainActivity.this, "Profile", ProfileActivity.class);

                            if (result == null) {
                                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.END);
                                return false;
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    }

                    // About Activity
                    else if (id == R.id.nav_ab) {
                        if(Globals.cart.size()==0) {
                            Intent intent_about = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(intent_about);
                            finish();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    }

                    // Support Activity
                    else if (id == R.id.nav_support) {
                        if(Globals.cart.size()==0) {
                            Intent intent_about = new Intent(MainActivity.this, YouTubeVideoListActivity.class);
                            startActivity(intent_about);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }
                    } else if (id == R.id.nav_privacy) {
                        try {
                        String url = "http://www.pegasustech.net/privacy";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));


          startActivity(i);
}
catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                            // Try with the default browser
    Toast.makeText(MainActivity.this,"There is no default browser Installed",Toast.LENGTH_LONG).show();
                            //i.setPackage(null);
                            //startActivity(i);
                        }
                        //finish();
                    }
                    // Get Files code
                    else if (id == R.id.nav_get_file) {
                        if(Globals.cart.size()==0) {
                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                arrayListGetFile = new ArrayList<String>();
                                arrayListGetFile.add("itemgroup.csv");
                                arrayListGetFile.add("item.csv");
                                arrayListGetFile.add("table.csv");

                                pDialog = new ProgressDialog(MainActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.DownloadServerData));
                                pDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        //downloading thread
                                        for (int i = 0; i < arrayListGetFile.size(); i++) {
                                            try {
                                                //   DownloadFileFromURL("http://" + Globals.App_IP + "/trigger-pos/upload/demo_files/" + arrayListGetFile.get(i) + "", "" + arrayListGetFile.get(i) + "");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(MainActivity.this, R.string.FilesDownloadSucc, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }.start();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    } else if (id == R.id.nav_get_itemimages) {

                        if (isNetworkStatusAvialable(getApplicationContext())) {
                            new DownlaodImageAsyncTask().execute();
                        }
                    }
// Send Database
                    else if (id == R.id.nav_send_db) {
                        if (isNetworkStatusAvialable(getApplicationContext())) {
                            DatabaseBackUp();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    } else if (id == R.id.nav_loyalty) {
                        userPermission = new UserPermission();

                        Boolean result = userPermission.Permission(MainActivity.this, getString(R.string.Profile), ProLoyaltySetupActivity.class);

                        if (result == null) {
                            Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.END);
                            return false;
                        }
                    } else if (id == R.id.nav_coupon) {
                        userPermission = new UserPermission();

                        Boolean result = userPermission.Permission(MainActivity.this, getString(R.string.Profile), CouponListActivity.class);

                        if (result == null) {
                            Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.END);
                            return false;
                        }
                    }

                    drawer.closeDrawer(GravityCompat.END);
                    return true;
                }
            });
        } catch (Exception e) {

            Globals.AppLogWrite(e.getMessage());
        }
    }

    // tab layout with item Group name
    public void call_parent_dialog(String ItemCategoryCode,String itemgroupname) {
        listDialog = new Dialog(MainActivity.this);
        listDialog.setTitle("Select Category");
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.order_type_list, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        tv_subcategory.setVisibility(View.VISIBLE);
        tv_subcategory.setText(itemgroupname);
        //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(),database,db," where item_group_code = '"+ItemCategoryCode+"'");
        final ArrayList<Item_Group> item_groupArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "where parent_code IN ('" + ItemCategoryCode + "') order by item_group_name ASC", database, db);
        ListView list1 = (ListView) listDialog.findViewById(R.id.lv_custom_ortype);
        if (item_groupArrayList.size() > 0) {
            ParentCategoryListAdapter parentCategoryListAdapter = new ParentCategoryListAdapter(MainActivity.this, item_groupArrayList, listDialog);
            list1.setVisibility(View.VISIBLE);
            list1.setAdapter(parentCategoryListAdapter);
            list1.setTextFilterEnabled(true);
            listDialog.show();

        }
        else{

        }

     Globals.TabPos = viewPager.getCurrentItem();
        setupViewPager(viewPager, "Main", "", ItemCategoryCode, "Parent");


    }


    // on Contact Selection icon changing color
    public void change_customer_icon() {
        topNavigationView = (BottomNavigationView) findViewById(R.id.top_navigation);
        Menu menu2 = topNavigationView.getMenu();
        MenuItem cusIcon = menu2.findItem(R.id.action_category);
        if (Globals.strContact_Code.equals("") && Globals.strResvContact_Code.equals("")) {
            //if (Globals.strContact_Code.equals("") ) {
            topNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.contact1);
        } else {
            topNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.green);
        }
    }


    private void Load_Activity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void retail_list_load() {
        ArrayList<ShoppingCart> myCart = Globals.cart;
        retail_list = (ListView) findViewById(R.id.retail_list);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        if (myCart.size() > 0) {
            retailListAdapter = new RetailListAdapter(MainActivity.this, myCart, opr, strOrderCode, "MainLand");
            retail_list.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
            //Collections.reverse(myCart);
            scrollMyListViewToBottom();
            retail_list.setAdapter(retailListAdapter);
            retailListAdapter.notifyDataSetChanged();
        } else {
            retail_list.setVisibility(View.GONE);
            txt_title.setVisibility(View.VISIBLE);
        }

    }

    // Code by Jyoti
    private void scrollMyListViewToBottom() {
        retail_list.post(new Runnable() {
            @Override
            public void run() {

                try {
                    retail_list.setSelection(retailListAdapter.getCount() - 1);
                }
                catch(Exception e){}
            }
        });
    }

    // Get files code
/*    public void DownloadFileFromURL(String strUrl, String fileName) {
        HttpEntity entity;
        String back = "false";
        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(strUrl);
            HttpResponse response = client.execute(post);
            //captures the response
            entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            if (code == 404 || code == 500) {
            } else {

                File root = new File(Environment.getExternalStorageDirectory(), "");
                if (!root.exists()) {
                    root.mkdirs();
                }
                //gives file name
                File outputFile = new File(root, fileName);
                FileOutputStream output = new FileOutputStream(outputFile);
//                    FileOutputStream output = openFileOutput("downloadedJson.json", 0);
                //creates new StreamWriter
                OutputStreamWriter writer = new OutputStreamWriter(output);

                InputStream entityStream = entity.getContent();

                StringBuilder entityStringBuilder = new StringBuilder();
                byte[] buffer = new byte[1024];
                int bytesReadCount;
                while ((bytesReadCount = entityStream.read(buffer)) > 0) {
                    entityStringBuilder.append(new String(buffer, 0, bytesReadCount));
                }
                String entityString = entityStringBuilder.toString();
                writer.write(entityString);

                writer.flush();
                //closes writer
                writer.close();

            }
        } catch (Exception e) {
            Log.e("log_tag", "Error saving string " + e.toString());
        }
        if (!back.equals("false")) {
            //ReadFile(back);
        }
    }*/

    // Order type selection
    private void defult_ordertype() {
        Menu menu = topNavigationView.getMenu();
        orertype = menu.findItem(R.id.action_order_type);
        if (Globals.objsettings.get_Default_Ordertype() == null) {
            Globals.objsettings.set_Default_Ordertype("1");
        }
        switch (Globals.objsettings.get_Default_Ordertype()) {
            case "1":
                orertype.setIcon(R.drawable.deliver);

                break;
            case "2":
                orertype.setIcon(R.drawable.drive_thrue);
                break;
            case "3":
                orertype.setIcon(R.drawable.pick_up);
                break;
            case "4":
                orertype.setIcon(R.drawable.take_out);
                break;
            case "5":
                orertype.setIcon(R.drawable.dine_ine);

                break;
            default:
                orertype.setIcon(R.drawable.deliver);
                break;
        }

        Globals.strOrder_type_id = Globals.objsettings.get_Default_Ordertype();


        String strOrder_type = Globals.strOrder_type_id;
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id ='" + strOrder_type + "'", database, db);
        try {
            String strOrder_name = order_type.get_name();
            orertype.setTitle(strOrder_name);

           // Globals.strorderType=strOrder_name;
        } catch (Exception ex) {
        }
    }

    private void call_CMD() {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("title", "Welcome");
            jsonObject.put("content", Globals.objLPR.getCompany_Name());
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            Globals.AppLogWrite("call cmd jsonobject" + jsonObject.toString());
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
        }
    }


    private void call_MN(String displayTilte) {
        try {
            woyouService.sendLCDDoubleString("Welcome to", displayTilte, null);
            //Globals.AppLogWrite("Call mn"+ Globals.objLPR.getCompany_Name());
        } catch (Exception ex) {
        }
    }

    private void call_customer_disply_title(final String displayTilte) {

        Thread timerThread1 = new Thread() {
            public void run() {
                try {
                    try {
                        // for (int i = 0; i < Globals.CMD_Images.size(); i++) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("title", "Welcome");
                            json.put("content", displayTilte);
                            String titleContentJsonStr = json.toString();
                            dsPacket = UPacketFactory.buildShowText(
                                    DSKernel.getDSDPackageName(), json.toString(), callback);
                            mDSKernel.sendData(dsPacket);
                            Globals.AppLogWrite("call_customer_disply_title" + titleContentJsonStr);
                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, path1, new ISendCallback() {
                                @Override
                                public void onSendSuccess(long fileId) {
                                    showQRCode(fileId);//sending the qr-code image
                                }

                                public void onSendFail(int i, String s) {
                                    //failure
                                }

                                public void onSendProcess(long l, long l1) {
                                    //sending status
                                }
                            });
                        } catch (Exception ex) {
                        }
                        //}
                    } catch (Exception ex) {
                    }
                } finally {
                }
            }
        };
        timerThread1.start();
    }

    //
    private void save_order(String strRemarks) {
        String strFlag1 = "0";
        String strOrdeeStatus = "OPEN";
        String strOrderNo = "";
        Order_Detail objOrderDetail;
        Orders objOrder;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

     //   database.beginTransaction();
        order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);

        iTax = 0.0;
        if (order_type_taxArrayList.size() > 0) {
            if (Globals.objLPR.getCountry_Id().equals("99")) {
                if (contact != null) {
                    if (contact.getIs_taxable().equals("1")) {
                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                    Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                                    String tax_id = order_type_tax.get_tax_id();

                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                                    iPrice = Globals.TotalItemPrice;

                                    Double itempTax = 0d;
                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                                    } else {
                                        iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                        itempTax = Double.parseDouble(tax_master.get_rate());
                                    }

                                    OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                                    Globals.order_tax_array.add(orderTaxArray);
                                }
                            }
                            if(iPrice!=0.0) {
                                total = iPrice + iTax;
                                temp = total + "";
                            }
                            else{
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                //   iPrice = Double.parseDouble(final_netamount + "");
                                total = iPrice;
                                temp = total + "";
                            }
                        }

                        else if(!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){
                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                if (sys_tax_group.get_tax_master_id().equals("3") ) {
                                    Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                                    String tax_id = order_type_tax.get_tax_id();

                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                                    iPrice = Globals.TotalItemPrice;

                                    Double itempTax = 0d;
                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                                    } else {
                                        iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                        itempTax = Double.parseDouble(tax_master.get_rate());
                                    }

                                    OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                                    Globals.order_tax_array.add(orderTaxArray);
                                    total = iPrice + iTax;
                                    temp = total + "";
                                }
                                else{
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                    iPrice = Double.parseDouble(final_netamount + "");
                                    total = iPrice;
                                    temp = total + "";
                                }

                            }



                        }
                    } else {
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        // iPrice = Double.parseDouble(final_netamount + "");
                        total = iPrice;
                        temp = total + "";
                    }
                }
                else if(contact==null) {
                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                            Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                            String tax_id = order_type_tax.get_tax_id();

                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                            iPrice = Globals.TotalItemPrice;

                            Double itempTax = 0d;
                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                            } else {
                                iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                itempTax = Double.parseDouble(tax_master.get_rate());
                            }

                            OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                            Globals.order_tax_array.add(orderTaxArray);
                        }
                    }
                    if (iPrice != 0.0) {
                        total = iPrice + iTax;
                        temp = total + "";
                    } else {
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                        total = iPrice;
                        temp = total + "";
                    }
                }
            }

            else if(Globals.objLPR.getCountry_Id().equals("114")){
                if (contact != null) {
                    if (contact.getIs_taxable().equals("1")) {

                        for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                            Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                            String tax_id = order_type_tax.get_tax_id();

                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                            iPrice = Globals.TotalItemPrice;

                            Double itempTax = 0d;
                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                            } else {
                                iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                itempTax = Double.parseDouble(tax_master.get_rate());
                            }

                            OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                            Globals.order_tax_array.add(orderTaxArray);

                        }
                        if(iPrice!=0.0) {
                            total = iPrice + iTax;
                            temp = total + "";
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            //   iPrice = Double.parseDouble(final_netamount + "");
                            total = iPrice;
                            temp = total + "";
                        }



                    } else {
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        //  iPrice = Double.parseDouble(final_netamount + "");
                        total = iPrice;
                        temp = total + "";
                    }
                }
                else if(contact==null){


                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {


                        Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                        String tax_id = order_type_tax.get_tax_id();

                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                        iPrice = final_netamount;

                        Double itempTax = 0d;
                        if (tax_master.get_tax_type().equals("P")) {
                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                        } else {
                            iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                            itempTax = Double.parseDouble(tax_master.get_rate());
                        }

                        OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                        Globals.order_tax_array.add(orderTaxArray);

                    }
                    if(iPrice!=0.0) {
                        total = iPrice + iTax;
                        temp = total + "";
                    }
                    else{
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        //   iPrice = Double.parseDouble(final_netamount + "");
                        total = iPrice;
                        temp = total + "";
                    }


                }
            }

            else if(Globals.objLPR.getCountry_Id().equals("221")){
                if (contact != null) {
                    if (contact.getIs_taxable().equals("1")) {

                        for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                            Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                            String tax_id = order_type_tax.get_tax_id();

                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                            iPrice = Globals.TotalItemPrice;

                            Double itempTax = 0d;
                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                            } else {
                                iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                itempTax = Double.parseDouble(tax_master.get_rate());
                            }

                            OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                            Globals.order_tax_array.add(orderTaxArray);

                        }
                        if(iPrice!=0.0) {
                            total = iPrice + iTax;
                            temp = total + "";
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            //   iPrice = Double.parseDouble(final_netamount + "");
                            total = iPrice;
                            temp = total + "";
                        }



                    } else {
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        //  iPrice = Double.parseDouble(final_netamount + "");
                        total = iPrice;
                        temp = total + "";
                    }
                }
                else if(contact==null){


                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {


                        Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                        String tax_id = order_type_tax.get_tax_id();

                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                        iPrice = Globals.TotalItemPrice;

                        Double itempTax = 0d;
                        if (tax_master.get_tax_type().equals("P")) {
                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                        } else {
                            iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                            itempTax = Double.parseDouble(tax_master.get_rate());
                        }

                        OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                        Globals.order_tax_array.add(orderTaxArray);

                    }
                    if(iPrice!=0.0) {
                        total = iPrice + iTax;
                        temp = total + "";
                    }
                    else{
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        //   iPrice = Double.parseDouble(final_netamount + "");
                        total = iPrice;
                        temp = total + "";
                    }



                }
            }

        }

        else {

            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
            //  iPrice = Double.parseDouble(final_netamount + "");
            total = iPrice;
            temp = total + "";

        }

        if (opr.equals("Edit")) {
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            objOrder = Orders.getOrders(getApplicationContext(), database, "  WHERE order_code = '" + strOrderCode + "'");
            Globals.TotalItem += Globals.cart.size();
            objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.license_id, locCode, Globals.strOrder_type_id, strOrderCode, objOrder.get_order_date(), Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", objOrder.get_total_tax(), "0", total + "", "0",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, objOrder.get_remarks(), Globals.strTable_Code, "",null);
            long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderCode, objOrder.get_order_id()}, database);


            if (l > 0) {
                strFlag1 = "1";

                order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);



                long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderCode}, database);
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);

                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.license_id, strOrderCode,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0",mCart.getKitchenprintflag(),mCart.getUnitId(),"");
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
                ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                Order_Tax objOrderTax;
               // long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderCode}, database);

                for (int count = 0; count < orderTaxArray.size(); count++) {
                    OrderTaxArray Otary = orderTaxArray.get(count);
                    objOrderTax = new Order_Tax(getApplicationContext(), null, "", Otary.get_tax_id()
                            , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

                    long o = objOrderTax.insertOrder_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
                long p = Order_Payment.delete_order_payment(getApplicationContext(), "order_payment", " order_code =? ", new String[]{strOrderCode}, database);

                ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                Order_Detail_Tax objOrderDetailTax;
                long l1 = Order_Detail_Tax.delete_Order_Detail_Tax(getApplicationContext(), "order_detail_tax", " order_code =? ", new String[]{strOrderCode}, database);
                for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                    Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                    objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderCode, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                            , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());
                    long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
            }

        } else {

            Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");
            if (objOrder1 == null) {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + 1;
            } else {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
            }
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            objOrder = new Orders(getApplicationContext(), null, Globals.license_id, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", iTax + "", "0", total + "", "0",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, strRemarks, Globals.strTable_Code, "",null);
            long l = objOrder.insertOrders(database);

            if (l > 0) {
                strFlag1 = "1";
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.license_id, strOrderNo,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0",mCart.getKitchenprintflag(),mCart.getUnitId(),"");
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }

                ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                Order_Tax objOrderTax;
                for (int count = 0; count < orderTaxArray.size(); count++) {
                    OrderTaxArray Otary = orderTaxArray.get(count);
                    objOrderTax = new Order_Tax(getApplicationContext(), null, "", Otary.get_tax_id()
                            , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

                            long o = objOrderTax.insertOrder_Tax(database);
                            if (o > 0) {
                                strFlag1 = "1";
                            } else {
                            }
                }
                ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                Order_Detail_Tax objOrderDetailTax;
                for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                    Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                    objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                            , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());
                    long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";



                    } else {
                    }
                }
            }


        }

        if (strFlag1.equals("1")) {
/*            database.setTransactionSuccessful();
            database.endTransaction();*/
            if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                order.execute();
            }


            if(!opr.equals("Edit")) {
                strOrderCode = strOrderNo;
            }
            opr = "Add";
            Globals.Operation = opr;
            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_Item_Price.setText(itemPrice);
            defult_ordertype();
            clear_btn_enable();
            if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                retail_list_load();
            }
            change_customer_icon();
            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
/*            if(!Globals.objsettings.getIs_KitchenPrint().equals("true")){
                 Globals.setEmpty();
                            try {
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                               *//* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*//*
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }
            }*/


/*            if(opr.equals("Edit")){
                            if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
               // pDialog.dismiss();
               // listDialog2.dismiss();

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage(getString(R.string.waiting));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        printKOT(strOrderCode,progressDialog);

                    }
                };t.start();
             *//*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*//*



            }
            }*/
        }
    }

    private void clear_btn_enable() {
        Menu menu = bottomNavigationView.getMenu();
        orertype = menu.findItem(R.id.action_clear);
        orertype.setEnabled(true);
    }

    public void setTextView(String price, String qty) {
//        Globals.TotalItemPrice = 15d;
        btn_Item_Price = (Button) findViewById(R.id.btn_Item_Price);
        btn_Qty = (Button) findViewById(R.id.btn_Qty);
        btn_Item_Price.setText(price);
        btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));

        if (Globals.OrientValue == Configuration.ORIENTATION_LANDSCAPE) {
            retail_list_load();
        }
    }

    @Override
    public void onItemModifier(String itemprice, String itemqty) {
         btn_Item_Price = (Button) findViewById(R.id.btn_Item_Price);
        btn_Qty = (Button) findViewById(R.id.btn_Qty);
        btn_Item_Price.setText(itemprice);
        Globals.TotalQty=Double.parseDouble(itemqty);
        btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));

        if (Globals.OrientValue == Configuration.ORIENTATION_LANDSCAPE) {
            retail_list_load();
        }
    }


    private class DownlaodImageAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MainActivity.this);
            p.setMessage("Please wait...It is downloading");
          /*  p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();*/
            Toast.makeText(getApplicationContext(), "Images downloading started", Toast.LENGTH_SHORT).show();


        }

        @Override
        protected String doInBackground(String... strings) {


            String result = "0";
            try {


                try {

                    itemArrayList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1'", database);
                    String item_code = "";
                    String item_id = "";
                    for (int i = 0; i < itemArrayList.size(); i++) {

                        item_code = itemArrayList.get(i).get_item_code();
                        item_id = itemArrayList.get(i).get_item_id();
                        GetImage_PosItem(item_code);

                    }

                    result = "1";

                } catch (Exception e) {
                    e.printStackTrace();

                }


            } catch (Exception e) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Images downloaded successfully", Toast.LENGTH_SHORT).show();

                    p.hide();
                }


            } else {
                p.show();
            }
        }
    }

    public void insertImagePosmenu_Item(String itemcode, String serverdata) throws IllegalStateException, IOException, JSONException {


        byte[] img = null;
        try {

            final JSONObject jsonObject_bg = new JSONObject(serverdata);
            final String strStatus = jsonObject_bg.getString("status");

            if (strStatus.equals("true")) {

                JSONArray jarray = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jarray.length(); i++) {

                    ContentValues cv = new ContentValues();
                    JSONObject json = jarray.getJSONObject(i);

                    String stringimage = json.getString("image");
                    String stringitemcode = json.getString("item_code");
                          /*  Bitmap bitmap=null;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Could be Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.WEBP
                    byte[] bai = baos.toByteArray();

                    String base64Image = Base64.encodeToString(bai, Base64.DEFAULT);*/
                    if (itemcode.equals(stringitemcode)) {
                        try {

                            if (stringimage != null) {
                                byte imgArr[] = Base64.decode(stringimage);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(imgArr, 0, imgArr.length);

                               /* decodedByte = Globals.getResizedBitmap(decodedByte, 100, 100);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 80, bos);
                                img = bos.toByteArray();*/
                                File path = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/ItemImages");
                                if (!path.exists()) {
                                    path.mkdirs();
                                }
                                File outFile = new File(path, itemcode + ".jpeg");
                                FileOutputStream outputStream = new FileOutputStream(outFile);
                                decodedByte.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();
                            }
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_LONG).show();

                        } catch (IOException e1) {

                        } catch (OutOfMemoryError e2) {
                            Toast.makeText(getApplicationContext(), "Out of Memory", Toast.LENGTH_LONG).show();
                        } catch (Exception e3) {
                            System.out.print(e3.getMessage());
                        }


                        /*} catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                   /* try {

                       // Bitmap bitmap = BitmapFactory.decodeByteArray(imgArr, 0, numberOfBytes);
                        File path = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/ItemImages" + File.separator + bitmap);
                        if(!path.exists()){
                            path.mkdirs();
                        }
                        File outFile = new File(path, itemcode + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(outFile);
                       // decodedByte.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                    } catch (FileNotFoundException e) {

                    } catch (IOException e) {

                    }*/
                    //if(itemCode.equals(stringitemcode)) {
                    // cv.put("item_image", stringimage);
                    //item.get_item_id();
                    // plist.add(stringstr);
                    // item.setItem_image(img);
                    //long l = item.updateItem("item_code=?", new String[]{itemCode}, database);
                     /*   long l = database.update("item", cv, "item_id=?", new String[]{itemid});

                        if (l > 0) {
                            // succ_bg = "1";
                        } else {
                        }*/
                    // }
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public  String GetImage_PosItem(String itemcode) throws IllegalStateException,
            IOException  {
        String result = null;
        InputStream ies = null;
        // Creating Http Object..
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 15000);
        HttpConnectionParams.setSoTimeout(myParams, 15000);
        HttpConnectionParams.setTcpNoDelay(myParams, true);
        DefaultHttpClient httpclient = new DefaultHttpClient(myParams);
       // "http://192.168.2.72/trigger-pos-ar/index.php/api/item/get_items_images"

        HttpPost httppost = new HttpPost(Globals.App_IP_URL + "item/get_items_images");
        ArrayList<NameValuePair> namevaluepair = new ArrayList<NameValuePair>();
        namevaluepair.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        namevaluepair.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        namevaluepair.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        namevaluepair.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        namevaluepair.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        namevaluepair.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        namevaluepair.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
        namevaluepair.add(new BasicNameValuePair("item_code", itemcode));
//        namevaluepair.add(new BasicNameValuePair("brandid", GlobleVar.brandID));
        // Details of server
        try {
            httppost.setEntity(new UrlEncodedFormEntity(namevaluepair));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity httpEntity = httpResponse.getEntity();

            ies = httpEntity.getContent();
            // converting in to string

            BufferedReader br = new BufferedReader(new InputStreamReader(ies,
                    "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            ies.close();
            result = sb.toString();
           // result = EntityUtils.toString(httpEntity);
            Log.d("response", result);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/


    public void GetImage_PosItem(String itemcode) {

      /*  pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "item/get_items_images";
        // HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            insertImagePosmenu_Item(itemcode, response);

                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();


                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();

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
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("sys_code_1", Globals.serialno);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", Globals.androidid);
                params.put("sys_code_4", Globals.mykey);
                params.put("lic_customer_license_id", Globals.license_id);
                params.put("item_code", itemcode);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void setContactAction() {
        if (Globals.CheckContact.equals("1")) {
            Menu menu = topNavigationView.getMenu();
            orertype = menu.findItem(R.id.action_category);
            orertype.setEnabled(false);
        } else {
            Menu menu = topNavigationView.getMenu();
            orertype = menu.findItem(R.id.action_category);
            orertype.setEnabled(true);
        }
    }

    public void callHandler() {
        Thread timerThread1 = new Thread() {
            public void run() {
                try {
                    try {
                        for (int i = 0; i < 0; i++) {
                            JSONObject json = new JSONObject();
                            if (Globals.CMDItemPrice == 0) {
                                json.put("title", Globals.CMDItemName + " ");
                            } else {
                                json.put("title", Globals.CMDItemName + " " + Globals.myNumberFormat2Price(Globals.CMDItemPrice, decimal_check));
                            }
                            json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                            String titleContentJsonStr = json.toString();
                            Globals.AppLogWrite("Call Handler ItemName" + Globals.CMDItemName);
                            Globals.AppLogWrite("ItemPrice" + Globals.CMDItemPrice);
                            dsPacket = UPacketFactory.buildShowText(
                                    DSKernel.getDSDPackageName(), json.toString(), callback);
                            Globals.AppLogWrite("Json string" + json.toString());
                            mDSKernel.sendData(dsPacket);
/*                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images.get(i), new ISendCallback() {
                                @Override
                                public void onSendSuccess(long fileId) {
                                    showQRCode(fileId);//sending the qr-code image
                                }

                                public void onSendFail(int i, String s) {
                                }

                                public void onSendProcess(long l, long l1) {
                                }
                            });*/
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Call Handler Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } finally {
                }
            }
        };
        timerThread1.start();
    }

    public void change_table_icon() {
        MenuItem cusIcon = menu2.findItem(R.id.table);
        if (Globals.strTable_Code.equals("")) {
            cusIcon.setIcon(R.drawable.contact1);
        } else {
            cusIcon.setIcon(R.drawable.green);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu2 = menu;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        InputMethodManager manager
                = (InputMethodManager)
                getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        manager
                .hideSoftInputFromWindow(
                        searchView.getWindowToken(), 0);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);
            ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
            searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.black));
            searchEditText.setBackgroundColor(getResources().getColor(R.color.whitecolor));
            searchEditText.setHintTextColor(getResources().getColor(R.color.search_hint_color));
            searchEditText.setHint(R.string.Search);
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    if (searchView.getQuery().length() > 0) {
                        searchView.setQuery("", false);
                        setupViewPager(viewPager, "Main", "", "", "");
                    } else {
                        //Collapse the action view
                        searchView.onActionViewCollapsed();
                    }
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Globals.TabPos = viewPager.getCurrentItem();
                    String operat = "search";
                    String strFilt = query;
                    if (Globals.objsettings.get_Is_Zero_Stock().equals("true")) {
                        strFilt = " and (item_code Like  '%" + strFilt + "%' or  item_name Like  '%" + strFilt + "%' or  barcode Like  '%" + strFilt + "%')";
                    } else {
                        strFilt = " and (item.item_code Like  '%" + strFilt + "%' or  item.item_name Like  '%" + strFilt + "%' or  barcode Like  '%" + strFilt + "%')";
                    }

                    setupViewPager(viewPager, operat, strFilt, "", "");
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });


            if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {
                btn_Item_Price = (Button) findViewById(R.id.btn_Item_Price);
                btn_Qty = (Button) findViewById(R.id.btn_Qty);
                try {
                    searchEditText.setOnKeyListener(new View.OnKeyListener() {
                                                        @Override
                                                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                                                            {
                                                                View view = getCurrentFocus();
                                                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                                                String strValue = searchEditText.getText().toString();

                                                                if (searchEditText.getText().toString().equals("\n") || searchEditText.getText().toString().equals("")) {
                                                                    Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                                    searchEditText.requestFocus();
                                                                } else {
                                                                    String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                                    arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                                    if (arrayList.size() >= 1) {
                                                                        // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                                        ck_project_type = Globals.objLPR.getproject_id();

                                                                        if (Globals.objsettings.get_Is_Stock_Manager().equals("false")) {
                                                                            Item resultp = arrayList.get(0);
                                                                            String item_code = resultp.get_item_code();

                                                                            item_group_code = resultp.get_item_code();
                                                                            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                            ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                            int count = 0;
                                                                            boolean bFound = false;

                                                                            while (count < myCart.size()) {
                                                                                if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                                    bFound = true;

                                                                                    myCart.get(count).set_Quantity(((Integer.parseInt(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                    Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                                    Globals.TotalQty = Globals.TotalQty + 1;


                                                                                }
                                                                                count = count + 1;
                                                                            }

                                                                            if (!bFound) {

                                                                                if (item_location == null) {
                                                                                    sale_priceStr = "0";
                                                                                    cost_priceStr = "0";
                                                                                } else {
                                                                                    sale_priceStr = item_location.get_selling_price();
                                                                                    cost_priceStr = item_location.get_cost_price();
                                                                                }
                                                                                ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                                Double iTax = 0d;
                                                                                Double iTaxTotal = 0d;
                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                    iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                                    String tax_id = item_group_taxArrayList.get(i);
                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                    if (tax_master.get_tax_type().equals("P")) {
                                                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                    } else {
                                                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                    }
                                                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                    Globals.order_item_tax.add(order_item_tax);
                                                                                }
                                                                                Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                Double beforetaxprice=sprice-iTaxTotal;
                                                                                Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");
                                                                                Order_Detail order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);
                                                                                if(order_detail!=null){
                                                                                    strKitchenFlag= order_detail.getIs_KitchenPrintFlag();
                                                                                }
                                                                                ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "","0","0",item_group.getCategoryIp(),strKitchenFlag,resultp.get_unit_id(),beforetaxprice+"");
                                                                                Globals.cart.add(cartItem);
                                                                                Globals.SRNO = Globals.SRNO + 1;
                                                                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                                Globals.TotalItem = Globals.TotalItem + 1;
                                                                                Globals.TotalQty = Globals.TotalQty + 1;

                                                                            }
                                                                            Globals.cart = myCart;
                                                                            retail_list_load();
                                                                            String item_price;
                                                                            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                            btn_Item_Price.setText(item_price);
                                                                            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                            searchEditText.setText("");
                                                                        } else {
                                                                            Item resultp = arrayList.get(0);
                                                                            String item_code = resultp.get_item_code();

                                                                            item_group_code = resultp.get_item_code();
                                                                            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                            ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                            int count = 0;
                                                                            boolean bFound = false;

                                                                            while (count < myCart.size()) {
                                                                                if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                                    bFound = true;
                                                                                    curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                                                                    boolean result = stock_check(item_code, curQty + 1);
                                                                                    if (result == true) {
                                                                                        myCart.get(count).set_Quantity(((Integer.parseInt(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                        myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                        Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                                        Globals.TotalQty = Globals.TotalQty + 1;
//                                                                            curQty = curQty+1;
                                                                                    }

                                                                                }
                                                                                count = count + 1;
                                                                            }

                                                                            if (!bFound) {
                                                                                curQty = 0d;
                                                                                boolean result = stock_check(item_code, Double.parseDouble("1"));
                                                                                if (result == true) {
                                                                                    if (item_location == null) {
                                                                                        sale_priceStr = "0";
                                                                                        cost_priceStr = "0";
                                                                                    } else {
                                                                                        sale_priceStr = item_location.get_selling_price();
                                                                                        cost_priceStr = item_location.get_cost_price();
                                                                                    }
                                                                                    ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                                    Double iTax = 0d;
                                                                                    Double iTaxTotal = 0d;
                                                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                        iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                                        String tax_id = item_group_taxArrayList.get(i);
                                                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                        if (tax_master.get_tax_type().equals("P")) {
                                                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                        } else {
                                                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                        }
                                                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                        Globals.order_item_tax.add(order_item_tax);
                                                                                    }
                                                                                    Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                    Double beforetaxprice=sprice-iTaxTotal;
                                                                                    Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");
                                                                                    Order_Detail order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);
                                                                                    if(order_detail!=null){
                                                                                        strKitchenFlag= order_detail.getIs_KitchenPrintFlag();
                                                                                    }
                                                                                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "","0","0",item_group.getCategoryIp(),strKitchenFlag,resultp.get_unit_id(),beforetaxprice+"");
                                                                                    Globals.cart.add(cartItem);
                                                                                    Globals.SRNO = Globals.SRNO + 1;
                                                                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                                    Globals.TotalItem = Globals.TotalItem + 1;
                                                                                    Globals.TotalQty = Globals.TotalQty + 1;
//                                                                        curQty = curQty+1;

                                                                                }
                                                                            }
                                                                            Globals.cart = myCart;
                                                                            retail_list_load();
                                                                            String item_price;
                                                                            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                            btn_Item_Price.setText(item_price);
                                                                            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                            searchEditText.setText("");
                                                                        }

                                                                    } else {
                                                                        searchEditText.selectAll();
                                                                        Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                return true;
                                                            }
                                                            return false;
                                                        }
                                                    }
                    );
                } catch (Exception e) {

                }
            }


            if (Globals.objLPR.getproject_id().equals("standalone")) {

                menu.setGroupVisible(R.id.overFlowtabbleToHide, false);
            } else {
                menu.setGroupVisible(R.id.overFlowtabbleToHide, true);

            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.table) {
            Table tableobj = Table.getTable(getApplicationContext(), database, db, "");
            if(tableobj==null){

                Toast.makeText(getApplicationContext(),"No Table Data Found",Toast.LENGTH_LONG).show();
            }
            else {
               // defult_ordertype();
                if (Globals.objsettings.get_Default_Ordertype().equals("5") ) {

                    Intent table = new Intent(getApplicationContext(), TableMangement.class);
                    startActivity(table);
                    // finish();

                }
                else if(Globals.strOrder_type_id.equals("5")){
                    Intent table = new Intent(getApplicationContext(), TableMangement.class);
                    startActivity(table);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Select Order Type Dine In to proceed",Toast.LENGTH_LONG).show();
                }

            }

//            Table table = Table.getTable(getApplicationContext(), database, db, "");
//            if (table == null) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                builder.setTitle(getString(R.string.alerttitle));
//                builder.setMessage(getString(R.string.alert_impexpmsg));
//
//                builder.setPositiveButton(getString(R.string.Import_CSV), new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do nothing but close the dialog
//                        try {
//
//                            String[] mimeTypes = {"text/*"};
//                            // File file= new File(Environment.getExternalStorageDirectory()+"");
//                            Intent intent;
//
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//
//                                // intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//                                if (mimeTypes.length > 0) {
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//                                    //   intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, mimeTypes);
//                                }
//                            } else {
//                                intent = new Intent(Intent.ACTION_GET_CONTENT);
//
//                                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                String mimeTypesStr = "";
//                                for (String mimeType : mimeTypes) {
//                                    mimeTypesStr += mimeType + "|";
//                                }
//                                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
//                            }
//                            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 7);
//
//                            //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
//                        } catch (Exception e) {
//
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//
//                builder.setNegativeButton(getString(R.string.Export_csv), new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        // Do nothing
//                        pDialog = new ProgressDialog(MainActivity.this);
//                        pDialog.setTitle("");
//                        pDialog.setMessage("Exporting data.....");
//                        pDialog.setCancelable(false);
//                        pDialog.show();
//                        final Thread t = new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    try {
//                                        // sleep(200);
//
//                                        // export csv code
//                                        String succ = export();
//
//                                        if (succ.equals("success")) {
//
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//
//                                                    try {
//                                                        File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "table_export" + ".csv");
//
//                                                        //  Toast.makeText(getApplicationContext(), "CSV Exported Successfully " + fileWithinMyDir, Toast.LENGTH_LONG).show();
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                                                        builder.setTitle(getString(R.string.alerttitle));
//                                                        builder.setMessage(getString(R.string.alert_sharemsg));
//
//                                                        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
//
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                // Do nothing but close the dialog
//
//                                                                try {
//                                                                    //  Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                                                                    File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "table_export.csv");
//
//                                                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//
//                                                                    File f = new File(fileWithinMyDir.getAbsolutePath());
//
//                                                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                                                                    shareIntent.setType("message/rfc822");
//                                                                    Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.org.phomellolitepos.myfileprovider", new File(fileWithinMyDir.getAbsolutePath()));
//                                                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Trigger POS Table CSV");
//                                                                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                                                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, Please find attached  Table.csv");
//                                                                    startActivity(Intent.createChooser(shareIntent, f.getName()));
//
//                                                                } catch (Exception e) {
//                                                                    System.out.println(e.getMessage());
//                                                                }
//                                                            }
//                                                        });
//
//                                                        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
//
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                // Do nothing
//
//                                                                dialog.dismiss();
//
//                                                            }
//                                                        });
//
//                                                        AlertDialog alert = builder.create();
//                                                        alert.show();
//
//                                                        pDialog.dismiss();
//                                                    } catch (Exception e) {
//
//                                                    }
//
//
//                                                }
//                                            });
//
//                                        }
//                                    } catch (final Exception e) {
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                pDialog.dismiss();
//                                                Toast.makeText(getApplicationContext(), e.getMessage(),
//                                                        Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                } catch (Exception ex) {
//
//
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            pDialog.dismiss();
//                                        }
//                                    });
//                                    // TODO Auto-generated catch block
//                                    ex.printStackTrace();
//                                }
//                            }
//                        };
//                        t.start();
//                    }
//                });
//                builder.setNeutralButton(getString(R.string.downloadfiles), new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do nothing but close the dialog
//
//                        pDialog = new ProgressDialog(MainActivity.this);
//                        pDialog.setTitle("");
//                        pDialog.setMessage("Exporting data.....");
//                        pDialog.setCancelable(false);
//                        pDialog.show();
//                        final Thread t = new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    try {
//                                        // sleep(200);
//
//                                        // Export sample file
//                                        String succ = Globals.export_sample();
//
//                                        if (succ.equals("success")) {
//
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//
//                                                    try {
//                                                        File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "table_export_sample" + ".csv");
//
//                                                        //  Toast.makeText(getApplicationContext(), "CSV Exported Successfully " + fileWithinMyDir, Toast.LENGTH_LONG).show();
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                                                        builder.setTitle(getString(R.string.alerttitle));
//                                                        builder.setMessage(getString(R.string.alert_sharemsg));
//
//                                                        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
//
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                // Do nothing but close the dialog
//
//                                                                try {
//                                                                    //  Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                                                                    File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "table_export_sample.csv");
//
//                                                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//
//                                                                    File f = new File(fileWithinMyDir.getAbsolutePath());
//
//                                                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                                                                    shareIntent.setType("message/rfc822");
//                                                                    Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.org.phomellolitepos.myfileprovider", new File(fileWithinMyDir.getAbsolutePath()));
//                                                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Trigger POS Item Sample CSV");
//                                                                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                                                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, Please find attached  item_export_sample.csv");
//                                                                    startActivity(Intent.createChooser(shareIntent, f.getName()));
//
//                                                                } catch (Exception e) {
//                                                                    System.out.println(e.getMessage());
//                                                                }
//                                                            }
//                                                        });
//
//                                                        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
//
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                // Do nothing
//
//                                                                dialog.dismiss();
//
//                                                            }
//                                                        });
//
//                                                        AlertDialog alert = builder.create();
//                                                        alert.show();
//
//                                                        pDialog.dismiss();
//                                                    } catch (Exception e) {
//
//                                                    }
//
//
//                                                }
//                                            });
//
//                                        }
//                                    } catch (final Exception e) {
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                pDialog.dismiss();
//                                                Toast.makeText(getApplicationContext(), e.getMessage(),
//                                                        Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                } catch (Exception ex) {
//
//
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            pDialog.dismiss();
//                                        }
//                                    });
//                                    // TODO Auto-generated catch block
//                                    ex.printStackTrace();
//                                }
//                            }
//                        };
//                        t.start();
//                    }
//                });
//                AlertDialog alert = builder.create();
//                alert.show();
//                //Toast.makeText(getApplicationContext(), R.string.No_Table_Fnd, Toast.LENGTH_SHORT).show();
//            } else {
//                showdialogTable();
//            }
            return true;

        } else if (id == R.id.action_openRight) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Globals.SRNO = 1;
        //Item Activity
     /*   Globals.cart.clear();
        Globals.order_item_tax.clear();
        Globals.TotalItemPrice = 0;
        Globals.TotalQty = 0;*/


        if (id == R.id.nav_item) {
            userPermission = new UserPermission();


            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Item", ItemListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ItemListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        }

        // Item Category
        else if (id == R.id.nav_item_category) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Item Category", CategoryListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, CategoryListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }
        // Order Screen (Recept Activity)

        else if (id == R.id.nav_recepts) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Order", ReceptActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent recept_intent = new Intent(MainActivity.this, ReceptActivity.class);
//                startActivity(recept_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;

            }

        }
        // Settings Activity

        else if (id == R.id.nav_settings) {

            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Settings", SetttingsActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;

            }


        }

        // Update License
        else if (id == R.id.nav_lic) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Update License", ActivateActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ActivateActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        }

        // Report Activity

        else if (id == R.id.nav_Report) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
            Boolean result = userPermission.Permission(MainActivity.this, "Report", ReportActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ReportActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }}
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }

        else if (id == R.id.nav_printtest) {
            if(Globals.cart.size()==0) {
                Intent it = new Intent(getApplicationContext(), PrintTestActivity.class);
                startActivity(it);

            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }
        // Logout

        else if (id == R.id.nav_logout) {
            if(Globals.cart.size()==0) {
                Globals.user = " ";
                Globals.setEmpty();
                getAlertDialog();
            }
            else{

                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        /*    Intent item_intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(item_intent);
            finish();*/
        }

        // Contact Activity

        else if (id == R.id.nav_contact) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Contact", ContactListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ContactListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;

            }

        } else if (id == R.id.nav_payment) {
            if(Globals.cart.size()==0) {
                Intent intent_payment = new Intent(MainActivity.this, PaymentListActivity.class);
                startActivity(intent_payment);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
  /*          userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Contact", PaymentListActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ContactListActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }*/

        } else if (id == R.id.nav_search_order) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Search Order", SearchOrderActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SearchOrderActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }
        // Manager

        else if (id == R.id.nav_man) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Manager", ManagerActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ManagerActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_resv) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Reservation", ReservationListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ReservationListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;

            }

        } else if (id == R.id.nav_pay_collection) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Payment Collection", PayCollectionListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }

            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        }
        // Accounts Activity

        else if (id == R.id.nav_acc) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Accounts", AccountsListActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                } else {
                    Intent acc = new Intent(MainActivity.this, AccountsListActivity.class);
                    acc.putExtra("whatsappFlag", "false");
                    acc.putExtra("contact_code", "");
                    startActivity(acc);
                }

            }
            else{
                Toast.makeText(getApplicationContext(), "Cart is not Empty.Please complete your transaction first.", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;

            }

        } else if (id == R.id.nav_stock_adjest) {
            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Stock Adjustment", StockAdjestmentListActivity.class);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }

        // Return
        else if (id == R.id.nav_return) {
            userPermission = new UserPermission();
            if(Globals.cart.size()==0) {
                Boolean result = userPermission.Permission(MainActivity.this, "Return", ReturnOptionActivity.class);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }
            else{


            }
//            Intent in = new Intent(MainActivity.this,ReturnOptionActivity.class);
//            startActivity(in);

        } else if (id == R.id.nav_purchese) {
            userPermission = new UserPermission();
            Boolean result = null;
            if (Locale.getDefault().equals("en")) {
                result = userPermission.Permission(MainActivity.this, "Purchase", PurchaseListActivity.class);
            } else if (Locale.getDefault().equals("ar")) {
                result = userPermission.Permission(MainActivity.this, "Purchase", PurchaseListActivity.class);

            }
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_class) {
            userPermission = new UserPermission();
            Boolean result = null;
            if (Locale.getDefault().equals("en")) {
                result = userPermission.Permission(MainActivity.this, "Class/Category", ClassDestinationListActivity.class);
            } else if (Locale.getDefault().equals("ar")) {
                result = userPermission.Permission(MainActivity.this, "Class/Category", ClassDestinationListActivity.class);

            }
            Globals.TicketCategory = "Class";
            if (result == null) {
                Globals.TicketCategory = "";
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_destination) {
            userPermission = new UserPermission();
            Boolean result = null;
            if (Locale.getDefault().equals("en")) {
                result = userPermission.Permission(MainActivity.this, "Area/Destination", ClassDestinationListActivity.class);
            } else if (Locale.getDefault().equals("ar")) {
                result = userPermission.Permission(MainActivity.this, "Area/Destination", ClassDestinationListActivity.class);

            }
            Globals.TicketCategory = "Distination";
            if (result == null) {
                Globals.TicketCategory = "";
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_setup) {
            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Setup", TicketSetupListActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_ticketing) {
            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Ticketing", TicketingActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_manufacture) {
            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Manufacture", ManufactureListActivity.class);


            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager, String oper, String str, String itemCategoryCode, String parent) {
        try {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            arrayListcategory = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' Order By lower(item_group_name) asc", database, db);
            FragmentManager fragMan = getSupportFragmentManager();


            if (fragList.size() > 0) {
                for (int i = 0; i < fragList.size(); i++) {
                    fragMan.beginTransaction().remove(fragList.get(i)).commit();
                    fragList.clear();
                    fragList = new ArrayList<Fragment>();
                    adapter.notifyDataSetChanged();
                }
            }

            if (parent.equals("Parent")) {
                for (int i = 0; i < arrayListcategory.size(); i++) {
                    Bundle bundle = new Bundle();
                    bundle.putString("itemGrpCode", itemCategoryCode);
                    bundle.putString("cat_name", arrayListcategory.get(i).get_item_group_name());
                    if (i == Globals.TabPos) {
                        bundle.putString("filter", str);
                        bundle.putString("operation", oper);
                    } else {
                        bundle.putString("filter", "");
                        bundle.putString("operation", oper);
                    }
                    ItemFragment fragment = new ItemFragment(MainActivity.this);
                    fragment.setArguments(bundle);
                    fragList.add(fragment);
                    adapter.notifyDataSetChanged();
                }
            } else {
                for (int i = 0; i < arrayListcategory.size(); i++) {
                    Bundle bundle = new Bundle();
                    bundle.putString("itemGrpCode", arrayListcategory.get(i).get_item_group_code());
                    bundle.putString("cat_name", arrayListcategory.get(i).get_item_group_name());
                    if (i == Globals.TabPos) {
                        bundle.putString("filter", str);
                        bundle.putString("operation", oper);
                    } else {
                        bundle.putString("filter", "");
                        bundle.putString("operation", oper);
                    }
                    ItemFragment fragment = new ItemFragment(MainActivity.this);
                    fragment.setArguments(bundle);
                    fragList.add(fragment);
                    adapter.notifyDataSetChanged();

                }


            }
            try {
                viewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();
               // viewPager.setCurrentItem(0);

                // viewPager.setOffscreenPageLimit(0);
            } catch (Exception e) {
                Globals.AppLogWrite("viewpager" + e.getMessage());
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            Globals.AppLogWrite("SetupViewPager" + e.getMessage());
            System.out.println(e.getMessage());
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragList.get(position).getArguments().getString("cat_name");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (fragList.get(arg0) instanceof ItemFragment) {
                ((ItemFragment) fragList.get(arg0)).init_View();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (fragList.get(arg0) instanceof ItemFragment) {
                ((ItemFragment) fragList.get(arg0)).init_View();
            }
        }

        @Override
        public void onPageSelected(int arg0) {
            if (fragList.get(arg0) instanceof ItemFragment) {
                ((ItemFragment) fragList.get(arg0)).init_View();
            }
        }
    }

    // Order Type Dialog
    private void showdialog() {
        listDialog = new Dialog(this);
        listDialog.setTitle(R.string.Select_Order_Type);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.order_type_list, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        order_typeArrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "", database);
        ListView list1 = (ListView) listDialog.findViewById(R.id.lv_custom_ortype);
        dialogOrderTypeListAdapter = new DialogOrderTypeListAdapter(MainActivity.this, order_typeArrayList, listDialog, orertype, topNavigationView);
        list1.setVisibility(View.VISIBLE);
        list1.setAdapter(dialogOrderTypeListAdapter);
        list1.setTextFilterEnabled(true);
        listDialog.show();
    }

    // Contact Display Dialog
    private void showdialogContact() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog1 = new Dialog(this);
        listDialog1.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);
        final ListView list11 = (ListView) listDialog1.findViewById(R.id.lv_custom_ortype);
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        edt_srch_contct.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_srch_contct.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_srch_contct.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String strFiltr = edt_srch_contct.getText().toString().trim();
                    strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                    edt_srch_contct.selectAll();
                    fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                    return true;
                }
                return false;
            }
        });
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        srch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);

            }
        });

        img_brs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listDialog1.dismiss();
                    Globals.BarcodeReslt = "";
                    mScannerView.startCamera();
                    // Programmatically initialize the scanner view
                    setContentView(mScannerView);
                } catch (Exception ex) {
                }

            }
        });

    }

    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "and business_group_code='" + str + "'";
        }

        if (Globals.objsettings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {

            if ((Globals.Taxwith_state.equals("")) && (Globals.NoTax.equals("")) && (Globals.Taxdifferent_state.equals(""))) {
                contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
            } else {
                if (Globals.cart.size() > 0) {
                    if (Globals.Taxwith_state.equals("1")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='1' And contact.zone_id = '"+Globals.objLPR.getZone_Id()+"' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    } else if (Globals.NoTax.equals("0")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='0' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    } else if (Globals.Taxdifferent_state.equals("2")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='1' And contact.zone_id != '"+Globals.objLPR.getZone_Id()+"' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    }
                }
                else{
                    contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                }
            }
        }
        dialogContactMainListAdapter = new DialogContactMainListAdapter(MainActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);

    }

    //  Table Dialog
    private void showdialogTable() {

        listDialog_table = new Dialog(this);
        listDialog_table.setTitle(R.string.Select_Table);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.order_type_list, null, false);
        listDialog_table.setContentView(v1);
        listDialog_table.setCancelable(true);

        table_ArrayList = Table.getAllTable(getApplicationContext(), "", database);

        ListView list11 = (ListView) listDialog_table.findViewById(R.id.lv_custom_ortype);
        dialogTableMainListAdapter = new DialogTableMainListAdapter(MainActivity.this, table_ArrayList, listDialog_table);
        list11.setVisibility(View.VISIBLE);
        list11.setAdapter(dialogTableMainListAdapter);
        list11.setTextFilterEnabled(true);
        listDialog_table.show();
    }


    private void showdialogremarksdialog() {
        listDialog2 = new Dialog(this);
        listDialog2.setTitle(R.string.Remarks);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(false);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
        listDialog2.show();
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog2.dismiss();
                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_remark.getText().toString().equals("")) {
                    strRemarks = "0";
                } else {
                    strRemarks = edt_remark.getText().toString();
                }
                save_order(strRemarks);
                if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
                    pDialog.dismiss();
                    listDialog2.dismiss();

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            printKOT(strOrderCode,progressDialog);
                            progressDialog.dismiss();
                            Globals.setEmpty();
                            try {
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                               /* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*/
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }

                        }
                    };t.start();
             /*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*/



                }
                else{

                    if (Globals.objsettings.getIs_saveprint().equals("true")) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Are You Sure");
                        alertDialogBuilder.setMessage("Do you want to Print Save Order");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                        // if (Globals.objsettings.get_Is_Print_Invoice().equals("true")) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                listDialog2.dismiss();
                                                Intent launchIntent = new Intent(MainActivity.this, PrintLayout.class);
                                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                launchIntent.putExtra("strOrderNo", strOrderCode);
                                                startActivity(launchIntent);

                                            }
                                        });
                                        // }

                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listDialog2.dismiss();
                                Globals.setEmpty1();

                                opr = "Add";
                                Globals.Operation = opr;
                                btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);
                                defult_ordertype();
                                clear_btn_enable();
                                if (OrintValue == Configuration.ORIENTATION_LANDSCAPE) {

                                    retail_list_load();
                                }
                                change_customer_icon();
                                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        listDialog2.dismiss();
                        Globals.setEmpty();
                        try {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                                                                              /*  btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);*/
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        });
    }


    //Bluetooth service handler
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Globals.strIsBlueService = "cnt"; //������
                            Toast.makeText(getApplicationContext(), "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:  //��������
                            Log.d("��������", "��������.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //�������ӵĵ���
                        case BluetoothService.STATE_NONE:
                            Log.d("��������", "�ȴ�����.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    Globals.strIsBlueService = "utc";//�����ѶϿ�����
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Globals.strIsBlueService = "utc";
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    // Onactivity result code for BT and Import table
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:      //���������
                if (resultCode == Activity.RESULT_OK) {   //�����Ѿ���
                    Toast.makeText(getApplicationContext(), "Bluetooth open successful", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //��������ĳһ�����豸
                if (resultCode == Activity.RESULT_OK) {   //�ѵ�������б��е�ĳ���豸��
                    String address = data.getExtras()
                            .getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);  //��ȡ�б������豸��mac��ַ
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }

                break;
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

                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setTitle("");
                    pDialog.setMessage("Importing data.....");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    final String finalFullPath = fullPath;
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    sleep(200);
                                       /* String[] path_separation = PathHolder.split(":");
                                        String filepath = path_separation[0];
                                        String filename = path_separation[1];*/

                                    File myFile = new File(finalFullPath);
                                    FileInputStream fIn = new FileInputStream(myFile);
                                    BufferedReader myReader = new BufferedReader(
                                            new InputStreamReader(fIn));
                                    String aDataRow = "";
                                    String aBuffer = "";

                                    db.executeDML("DROP table if Exists tables", database);

                                    long u = db.executeDML(" CREATE TABLE [tables]([table_id] INTEGER PRIMARY KEY AUTOINCREMENT,[table_code] NVARCHAR(50),[table_name] NVARCHAR(50),CONSTRAINT [table_code_unique] UNIQUE([table_code]))", database);

                                    //   long u = Product.delete_Product(getActivity(),"Products",database,"",new String []{});
                                    int count = 0;
                                    ContentValues contentValues = new ContentValues();
                                    ArrayList<Table> plist = new ArrayList<Table>();
                                    Table table = new Table(getApplicationContext(), null, "", "","","","","","");

                                    bValidate = true;
                                    while (((aDataRow = myReader.readLine()) != null) && bValidate) {
                                        if (count == 0) {
                                            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));
                                            String tablecode = myList.get(0).toString().replace("\"", "");
                                            String tablename = myList.get(1).toString().replace("\"", "");
                                            if (!tablecode.equals("table_code")) {
                                                bValidate = false;
                                            } else if (!tablename.equals("table_name")) {
                                                bValidate = false;
                                            } else {
                                                bValidate = true;
                                            }
                                            count = 1;
                                        } else {
                                            try {
                                                ArrayList<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));

                                                String tablecode = myList.get(0).toString().replace("\"", "");
                                                String tablename = myList.get(1).toString().replace("\"", "");
                                                plist.add(new Table(getApplicationContext(), null,
                                                        tablecode, tablename,"","",",",""," "));


                                            } catch (Exception ex) {
                                                ex.getStackTrace();
                                            }

                                        }


                                    }
                                    try {
                                        if (plist.size() > 0) {
                                            table.add_table(plist, database);
                                        }

                                    } catch (Exception e) {

                                    }
                                    myReader.close();

                                    //  if (succ_import.equals("1")) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();

                                            if (bValidate) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.csvimportsucc),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), getString(R.string.csvnotimportsucc),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } catch (final Exception e) {
                                    pDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } catch (Exception ex) {

                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                    }
                                });
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                        }
                    };
                    t.start();

                }


                break;

        }
    }

    //��ӡͼ��
    @SuppressLint("SdCardPath")
    private void printImagebluetooth() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(576);
        pg.initPaint();
        pg.drawImage(0, 0, "/mnt/sdcard/icon.jpg");
        //
        sendData = pg.printDraw();
        mService.write(sendData);   //��ӡbyte������
        Log.d("��������", "" + sendData.length);
    }


    //��ӡͼ��
    @SuppressLint("SdCardPath")
    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(576);
        pg.initPaint();
        pg.drawImage(0, 0, "/mnt/sdcard/icon.jpg");
        //
        sendData = pg.printDraw();
        mService.write(sendData);   //��ӡbyte������
        Log.d("��������", "" + sendData.length);
    }


    private void showQRCode(long fileId) {

        try {
            String json = UPacketFactory.createJson(sunmi.ds.data.DataModel.QRCODE, "");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
//        showing an image by sending this command
        } catch (Exception ex) {
            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
        Globals.strContact_Code = "";
        try {
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
        }catch(Exception e)
        { }
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    MainActivity.super.onBackPressed();
                    finish();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

                }
            });
            pressedTime = System.currentTimeMillis();
        }
     /*   Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("opr", "Add");
        intent.putExtra("order_code", "");
        startActivity(intent);*/
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

                String[] s = new String[1];

                // Type the path of the files in here
                s[0] = backupDB.getPath();

                // first parameter is d files second parameter is zip file name
                ZipManager zipManager = new ZipManager();

                // calling the zip function


                zipManager.zip(s, Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "Backup" + "/" + "DatabaseTriggerPOS.zip");

                send_email_manager();

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

    private void send_email_manager() {
        try {
            String[] recipients = {"developer.pegasus@gmail.com"};
            final MainActivity.SendEmailAsyncTask email = new MainActivity.SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender("developer.pegasus@gmail.com", "Passw0rd@", "smtp.gmail.com", "465");
            email.m.set_from("developer.pegasus@gmail.com");
            email.m.setBody("Phomello-TriggerPOSDB");
            email.m.set_to(recipients);
            email.m.set_subject("Phomello-TriggerPOSDB ( " + date + " )");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "Backup" + "/" + "DatabaseTriggerPOS.zip");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        MainActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(MainActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(MainActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }
    }

    private ArrayList<String> calculateTax() {
        ArrayList<Tax_Detail> taxIdAarry = new ArrayList<Tax_Detail>();
        ArrayList<String> taxIdFinalAarry = new ArrayList<String>();
        ArrayList<Item_Group_Tax> item_group_taxList = new ArrayList<Item_Group_Tax>();
        try {
            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {
                Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                if (taxIdAarry.size() > 0) {
                    for (int i = 0; i < taxIdAarry.size(); i++) {
                        Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                } else {
                    item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                    for (int i = 0; i < item_group_taxList.size(); i++) {
                        Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                }

            } else {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + Globals.strContact_Code + "'");
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                if (contact.get_zone_id().equals(lite_pos_registration.getZone_Id())) {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                } else {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Intrastate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return taxIdFinalAarry;
    }

    private Boolean stock_check(String item_code, Double curQty) {
        boolean flag = false;
        try {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
//        Order_Detail order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
//        Double total_qty = Double.parseDouble(order_detail.get_quantity()) + curQty;
            if (avl_stock >= curQty) {
                flag = true;
            } else {
                Toast.makeText(getApplicationContext(), "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Item not found in this location", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        // Prints the scan format (qrcode, pdf417 etc.)
Globals.BarcodeReslt= rawResult.getContents();

        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.BarcodeReslt + "'");
        if (contact == null) {
            Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_SHORT).show();
            Globals.strContact_Code = "";
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("opr", "Add");
            intent.putExtra("order_code", "");
            startActivity(intent);
        } else {
            Globals.strContact_Code = rawResult.getContents();
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("opr", "Add");
            intent.putExtra("order_code", "");
            startActivity(intent);
        }
    }

    public boolean Order_Total_Validation() {
        boolean resOrder = false;
        Double GTotal = 0d;
        Double DetailTotal = 0d;

        GTotal = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check));

        for (int count = 0; count < Globals.cart.size(); count++) {
            ShoppingCart mCart = Globals.cart.get(count);
            DetailTotal = DetailTotal + Double.parseDouble(mCart.get_Line_Total());
        }
        if (Globals.TotalItemPrice - DetailTotal == 0) {
            if (GTotal - DetailTotal == 0) {
                resOrder = true;
            } else {
                Globals.TotalItemPrice = DetailTotal;
            }
        }
        return resOrder;

    }

    public void getAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.alertlogtitle));
        builder.setMessage(getString(R.string.areyousure));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                // Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                // String licensecustomerid= lite_pos_device.getLic_customer_license_id();
                postDeviceInfo(company_email, company_password, Globals.isuse_logout, Globals.master_product_id, Globals.license_id, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.objLPR.getRegistration_Code());
            }
        });

        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing

              //  alert.dismiss();
                Load_Activity();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // Logout api calling using Volley mechanism
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String regcode) {

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging Out....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
        /*HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            String message = responseObject.getString("message");
                            if (status.equals("true")) {
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
                                    // database.beginTransaction();
                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {
                                       /* database.setTransactionSuccessful();
                                        database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                Intent intent_category = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent_category);
                                                finish();
                                            }
                                        });

                                    } else {
                                        /*database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "cannot logout", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (Exception e) {

                                }

                                //  }

                                pDialog.dismiss();

                            } else if (status.equals("false")) {
                                try {
                                    // database.beginTransaction();
                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
                                    if (ct > 0) {
                                       /* database.setTransactionSuccessful();
                                        database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                Intent intent_category = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent_category);
                                                finish();
                                            }
                                        });

                                    } else {
                                        /*database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "cannot logout", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (Exception e) {

                                }

                              /*  Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();*/
                            }

                        } catch (Exception e) {
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
                params.put("reg_code", regcode);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    // Getting address from Location service(used for sending value in Backgroud service )
    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    //Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        Globals.locationddress = result;
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    // Geo location handler used
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    Globals.locationddress = locationAddress;
                    break;
                default:
                    locationAddress = null;
            }

            // Toast.makeText(getApplicationContext(), locationAddress.toString(), Toast.LENGTH_LONG).show();
            //  tvAddress.setText(locationAddress);
        }
    }

    // Location json for background service api
    public void backgroundLocationJson() throws JSONException {
        JSONArray jsonArr = new JSONArray();

        final JSONObject jsonObj1 = new JSONObject();
        try {

            JSONObject jsonObj = new JSONObject();
            // Toast.makeText(getApplicationContext(), "size"+export.size(), Toast.LENGTH_SHORT).show();
            jsonObj.put("latitude", Globals.latitude);
            jsonObj.put("longitude", Globals.longitude);
            jsonObj.put("address", Globals.locationddress);
            jsonObj.put("datetime", date);

            jsonArr.put(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        jsonObj1.put("result", jsonArr);
        Globals.jsonArray_background = jsonObj1;


    }

    private String export() {

        String strResult = "";


        SQLiteDatabase db1 = db.getWritableDatabase();
        String selectQuery = "SELECT table_code,table_name from tables";

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "" + "table_export" + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            //SQLiteDatabase sqlite = db.getReadableDatabase();

            Cursor curCSV = db1.rawQuery(selectQuery, null);

            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {

                ArrayList<String> stringArrayList = new ArrayList<String>();
                int columncount = curCSV.getColumnCount();

                for (int i = 0; i < columncount; i++) {

                    stringArrayList.add(curCSV.getString(i));

                }
                //Which column you want to exprort
                String[] stringArray = stringArrayList.toArray(new String[stringArrayList.size()]);

                csvWrite.writeNext(stringArray);
            }
            csvWrite.close();
            curCSV.close();
            //csvWrite.close();
            curCSV.close();
            strResult = "success";

            //Toast.makeText(getApplicationContext(), getString(R.string.exportedcsv), Toast.LENGTH_SHORT).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

        }

        return strResult;

    }



    // Is Online Background thread code

    class Sendorder_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        MainActivity activity;

        public Sendorder_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Orders order=new Orders(getApplicationContext());

                order.sendOn_Server(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",Globals.objLPD.getLic_customer_license_id(),serial_no,android_id,myKey);

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }

    }
    public void openWhatsApp() {
        try {
            String text = "Hello Sir.\n I am Using Trigger POS Application Version " + versionname + "\n\n Company Name : " + Globals.objLPR.getCompany_Name() + "\n User name: " + Globals.objLPR.getContact_Person() + "\n Reg. Code :" + Globals.objLPR.getRegistration_Code() + "\n License Mode :" + Globals.objLPR.getproject_id() + "\n Expiry Date :" + javaEncryption.decrypt(Globals.objLPD.getExpiry_Date(), "12345678") + "\n";
            ;
            String toNumber = "";
            //India
            if (Globals.objLPR.getCountry_Id().equals("99")) {

                toNumber = "919530047775"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            }
            // Kuwait
            else if (Globals.objLPR.getCountry_Id().equals("114")) {
                toNumber = "96569029773";
            }
            // UAE
            else if (Globals.objLPR.getCountry_Id().equals("221")) {
                toNumber = "971558838749";
            } else {
                toNumber = "919530047775";
            }


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setPackage("com.whatsapp");
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReloadctivity(ProgressDialog pDialog) {
      /*  finish();
        Intent i= new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);*/
     /*   progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.Syncdataserver));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        Thread timerThread = new Thread() {
            public void run() {
                // if (settings.get_Home_Layout().equals("0")) {
                try {
                   // sleep(100);
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    // progressDialog.dismiss();
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pDialog.dismiss();
                            // progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.Data_sync_succ), Toast.LENGTH_SHORT).show();
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }


        };


        timerThread.start();
    }

    public void getDemoDatabse() {

      //  database.beginTransaction();

        // tax_master = new Tax_Master(getApplicationContext(), null, Globals.objLPD.getLocation_Code(), "Tax1"  , "P", "2.5", comment, "1", modified_by, date, "N");



        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.Syncdatademo));
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        sleep(2000);


                        if (Globals.objLPR.getIndustry_Type().equals("3")) {

                            try {
                                read_contact();
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            if (Globals.objLPR.getCountry_Id().equals("99")) {
                                try {
                                    read_tax();
                                } catch (Exception e) {
                                }
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            } else if (Globals.objLPR.getCountry_Id().equals("114")) {
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            } else if (Globals.objLPR.getCountry_Id().equals("221")) {

                                try {
                                    read_tax();
                                } catch (Exception e) {
                                }
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }


                             //   progressDialog.dismiss();

                            }
                        }

    ArrayList<String> arrayListGetFileImage = new ArrayList<>();
    arrayListGetFileImage.add("2001.png");
    arrayListGetFileImage.add("2002.png");
    arrayListGetFileImage.add("2003.png");
    arrayListGetFileImage.add("2004.png");
    arrayListGetFileImage.add("2005.png");
    arrayListGetFileImage.add("2006.png");
    arrayListGetFileImage.add("2007.png");
    arrayListGetFileImage.add("2008.png");
    arrayListGetFileImage.add("2009.png");
    arrayListGetFileImage.add("2010.png");
    arrayListGetFileImage.add("2011.png");
    arrayListGetFileImage.add("2012.png");
    arrayListGetFileImage.add("2013.png");
    arrayListGetFileImage.add("2014.png");
    arrayListGetFileImage.add("2015.jpg");
    arrayListGetFileImage.add("2016.jpg");
    arrayListGetFileImage.add("2017.jpg");
    arrayListGetFileImage.add("2018.jpg");
    arrayListGetFileImage.add("2019.jpg");
    arrayListGetFileImage.add("2020.jpg");
    arrayListGetFileImage.add("2021.jpg");
    arrayListGetFileImage.add("2022.jpg");
    arrayListGetFileImage.add("2023.jpg");
    arrayListGetFileImage.add("2024.jpg");
    arrayListGetFileImage.add("2025.jpg");
    arrayListGetFileImage.add("2027.jpg");
    arrayListGetFileImage.add("2028.jpg");
    arrayListGetFileImage.add("2029.jpg");
    arrayListGetFileImage.add("2030.jpg");
    arrayListGetFileImage.add("2031.jpg");
    for (int i = 0; i < arrayListGetFileImage.size(); i++) {
        //DownloadFileFromURL(imageURL, arrayListGetFileImage.get(i));
        downloadImage(Globals.imageURL + arrayListGetFileImage.get(i), arrayListGetFileImage.get(i));
        // DownloadImageFile(imageURL , arrayListGetFileImage.get(i));
        Globals.arrayListGetFile_Image = arrayListGetFileImage;
    }
    progressDialog.dismiss();


                        } catch(Exception e){

                           System.out.println(e.getMessage());
                        }

                    }

                catch (Exception e) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Globals.objLPR.getIndustry_Type().equals("3")) {
                            Toast.makeText(getApplicationContext(),"Demo Database Sync Sucessfully",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(MainActivity.this, PaymentCollection_MainScreen.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Demo Database Sync Sucessfully",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }


                    }
                });
            }

        };
        t.start();


    }


    private void read_tax() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.tax)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.tax_dubai)));

            }
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
           /*     try {
                    long l = Country.delete_country(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }*/

                JSONObject jsonObject_tax = new JSONObject(sb.toString());

                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");

                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);

                    tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "N");
                    long l = tax_master.insertTax_Master(database);
                    //  JSONObject jsonObject_country1 = jsonArray_country.getJSONObject(i);

                    JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                    for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                        JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                        order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                        long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                        if (odrtx > 0) {
                            // succ_manu = "1";
                        } else {
                        }
                    }

                    JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                    for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                        JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                        tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                        long odrtx1 = tax_detail.insertTax_Detail(database);

                        if (odrtx1 > 0) {
                            //  succ_manu = "1";
                        } else {
                        }
                    }

                    JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                    for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                        JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                        Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
                        long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);

                        if (odrtx1 > 0) {
                            //succ_manu = "1";
                        } else {
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void read_item() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_kuwait)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_dubai)));

            }
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
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_group)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.itemgroup_kuwait)));

            }
            else if(Globals.objLPR.getCountry_Id().equals("221")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.itemgroup_dubai)));

            }
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
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                           // succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
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



    private void read_contact() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact_kuwait)));

            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact_dubai)));

            }
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


                JSONObject jsonObject_contact = new JSONObject(sb.toString());

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    }


                    if (contact == null) {
                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            //succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new org.phomellolitepos.database.Address(getApplicationContext(), null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                long itmsp = address.insertAddress(database);

                                if (itmsp > 0) {
                                   // succ_bg = "1";

                                } else {
                                   // succ_bg = "0";
                                }


                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);
                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                   // succ_bg = "1";

                                } else {
                                  //  succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");

                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                   // succ_bg = "1";
                                } else {
                                  //  succ_bg = "0";
                                }
                            }
                        } else {
                            //succ_bg = "0";
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
        public void getAlertforDemoDB () {


            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.areyousure);

            //Setting message manually and performing action on button click
            builder.setMessage(R.string.demomessage)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(isNetworkStatusAvialable(getApplicationContext())) {
                                getDemoDatabse();
                            }
                            else{
                                builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setTitle(R.string.nointernet);

                                //Setting message manually and performing action on button click
                                builder1.setMessage(R.string.demomenetoff)
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                getDemoDatabse();


                                            }
                                        });
                                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button

                                        dialog.cancel();

                                    }
                                });
                                //Creating dialog box
                                AlertDialog alert = builder1.create();
                                //Setting the title manually
                                alert.setTitle(R.string.noonternet);
                                alert.show();
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();

                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle(R.string.areyousure);
            alert.show();

        }
    class DownloadImageUrlTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {


/*                String fileName;
                URL url = new URL(imageURL);
                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                //and connect!
                urlConnection.connect();
                fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1, imageURL.length());
                //set the path where we want to save the file in this case, going to save it on the root directory of the sd card.
                File SDCardRoot = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/ItemImages");
                //create a new file, specifying the path, and the filename which we want to save the file as.

                File file = new File(SDCardRoot,fileName);
                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);
                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();
                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);
                }*/
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Image Downloaded to sd card",Toast.LENGTH_SHORT).show();
        }
    }
    private void DownloadImageFile(String f_url, String filename) {

        String folder;
        int count1;
        try {
            URL url = new URL(f_url);

            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length

            int lengthOfFile = connection.getContentLength();


            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            //Extract file name from URL
          //  filename = f_url.substring(f_url.lastIndexOf('/') + 1, f_url.length());

            //Append timestamp to file name
            // fileName = timestamp + "_" + fileName;
            File SDCardRoot = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/ItemImages");

            //External directory path to save file
            folder = Environment.getExternalStorageDirectory()+ "TriggerPOS/ItemImages/";

            //Create androiddeft folder if it does not exist
            File directory = new File(folder+filename);

            if (!directory.exists()) {
                directory.mkdirs();
            }
           /* File file = new File(folder + fileName);
            if (file.exists())
            {
                progressDialog.setCancelable(true);
                Toast.makeText(getApplicationContext(),"File already exist",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                FileOutputStream fos = openFileOutput("", MODE_WORLD_WRITEABLE);

            }*/
            //  else {
            // Output stream to write file
            OutputStream output = new FileOutputStream(directory);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count1 = input.read(data)) != -1) {
                total += count1;
                // publishing the progress....
//                 After this onProgressUpdate will be called
//                publishProgress("" + (int) ((total * 100) / lengthOfFile));
//                Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));


                progressDialog.setProgress((int) ((total * 100) / lengthOfFile));
                //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                // writing data to file
                output.write(data, 0, count1);

            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            //flag = true;
            //  }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //flag = false;

        }
    }
        public void DownloadFileFromURL(String strUrl, String fileName) {
        HttpEntity entity;
        String back = "false";
        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(strUrl);
            HttpResponse response = client.execute(post);
            //captures the response
            entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            if (code == 404 || code == 500) {
            } else {

                File root = new File(Environment.getExternalStorageDirectory()+ "TriggerPOS/ItemImages/");
                if (!root.exists()) {
                    root.mkdirs();
                }
                //gives file name
                File outputFile = new File(root, fileName);
                FileOutputStream output = new FileOutputStream(outputFile);
//                    FileOutputStream output = openFileOutput("downloadedJson.json", 0);
                //creates new StreamWriter
                OutputStreamWriter writer = new OutputStreamWriter(output);

                InputStream entityStream = entity.getContent();

                StringBuilder entityStringBuilder = new StringBuilder();
                byte[] buffer = new byte[1024];
                int bytesReadCount;
                while ((bytesReadCount = entityStream.read(buffer)) > 0) {
                    entityStringBuilder.append(new String(buffer, 0, bytesReadCount));
                }
                String entityString = entityStringBuilder.toString();
                writer.write(entityString);

                writer.flush();
                //closes writer
                writer.close();

            }
        } catch (Exception e) {
            Log.e("log_tag", "Error saving string " + e.toString());
        }
        if (!back.equals("false")) {
            //ReadFile(back);
        }
    }
public void downloadImage(String fUrl,String file_name){
try{
    URL url = new URL(fUrl);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setRequestMethod("GET");
    urlConnection.setDoOutput(true);
    urlConnection.connect();
    File root = new File(Globals.folder+"ItemImages/");
    if (!root.exists()) {
        root.mkdirs();
    }
    String filename=file_name;
    Log.i("Local filename:",""+filename);
    File file = new File(root,filename);
  /*  if(file.createNewFile())
    {
        file.createNewFile();
    }*/
    FileOutputStream fileOutput = new FileOutputStream(file);
    InputStream inputStream = urlConnection.getInputStream();
    int totalSize = urlConnection.getContentLength();
    int downloadedSize = 0;
    byte[] buffer = new byte[1024];
    int bufferLength = 0;
    while ( (bufferLength = inputStream.read(buffer)) > 0 )
    {
        fileOutput.write(buffer, 0, bufferLength);
        downloadedSize += bufferLength;
        Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
    }
    fileOutput.close();
    if(downloadedSize==totalSize) fUrl=file.getPath();
}
catch (MalformedURLException e)
    {
        e.printStackTrace();
    }
catch (IOException e)
    {
        fUrl=null;
        e.printStackTrace();
    }
Log.i("filepath:"," "+fUrl) ;
}

    public void setCategoryAdapter(){
        arrayListcategoryStr = Item_Group.getAllItem_GroupSpinner(getApplicationContext(), " WHERE is_active ='1' and parent_code = '0' Order By item_group_name asc");
        ArrayAdapter<String> spinadapter;
        if(arrayListcategoryStr!=null) {
           // arrayListcategoryStr.add(0,"All Items");
          //  arrayListcategory.get(0).set_item_group_name("All Items");
          spinadapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayListcategoryStr);
            spn_item_category.setAdapter(spinadapter);


        }
        else{
           // arrayListcategoryStr.add(0,"Select");
            spinadapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayListcategoryStr);
            spn_item_category.setAdapter(spinadapter);
        }
        spn_item_category.setOnTouchListener((v, event) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();;
            event.setAction(MotionEvent.ACTION_UP);
            spn_item_category.onTouch(v,event);
            return false;
        });
        spn_item_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id)
            {
                // On selecting a spinner item
                if (searchView != null)
                    searchView.onActionViewCollapsed();
                try {
                    spn_item_category.setTitle("Select Category");
                    spn_item_category.setPositiveButton("CLOSE");
                    try {
                        ((TextView) adapter.getChildAt(0)).setTextColor(Color.BLACK);

                    } catch (Exception e) {
                    }
                    Bundle bundle = new Bundle();
                    arrayListcategory = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' Order By lower(item_group_name) asc", database, db);

                    String ItemCategoryCode = arrayListcategory.get(position).get_item_group_code();
                    String ItemCategoryName = arrayListcategory.get(position).get_item_group_name();

                    if (spn_item_category.getSelectedItem().equals("All Items")) {


                        call_parent_dialog("", "");
                    } else {

                        call_parent_dialog(ItemCategoryCode, "");
                    }


                }
                catch(Exception e){

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void printKOT(String strOrderNo,ProgressDialog pdialog) {
        boolean flag = false;
        getIP();
        try {


            for (int i = 0; i < ipAdd.size(); i++) {
                ip = ipAdd.get(i).toString();

                try {
                    final Orders orders = Orders.getOrders(MainActivity.this, database, "WHERE order_code = '" +strOrderNo + "'");
                    final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(MainActivity.this, "WHERE order_code = '" + strOrderNo + "'",database);

                    performOperationEn(ipAdd.get(i), 0, "Order",orders,order_detail1,pdialog);
                    performOperationEn(ipAdd.get(i), 0, "Void",orders,order_detail1,pdialog);

                } catch (Exception ex) {
                    //   GlobleVar.isSendOnline = true;
                    ex.getStackTrace();
                    //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
                //String last = ipAdd.get(ipAdd.size() - 1);


            }


/******************************** Master Print ***************************/

  /*          try {
                String result = getMasterPrint_from_server();
                JSONObject jsonObject = new JSONObject(result);

                String message = jsonObject.getString("Message");
                String printername = jsonObject.getString("PrinterName");


                if (message.equals("Success")) {
                    // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    performOperationEn(printername, 1, "Order");
                    performOperationEn(printername, 1, "Void");
                } else {
                    Toast.makeText(getApplicationContext(), "Master Print json" + result, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }*/
            // pDialog.dismiss();


        } catch (Exception e) {
            //  GlobleVar.isSendOnline = true;
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean CheckprinterConnection(String ipp) {


        if (!ipp.equals("")) {
            String ipAddress = "";
            String tmpPort = "";
            int port = 9100;
            String[] strings = StringSplit(ipp, ":");
            ipAddress = strings[0];
            tmpPort = strings[1];
            port = Integer.parseInt(tmpPort);

            if (!WifiPrintDriver.WIFISocket(ipAddress, port)) {
                WifiPrintDriver.Close();
                return false;
            } else {
                if (WifiPrintDriver.IsNoConnection()) {
                    return false;
                }
                return true;
            }

        } else {
            return false;
        }
    }

    private void getIP() {
        try {
            ipAdd.clear();

          String sqlQuery=  "Select Distinct(categoryIp ) from item_group where item_group_code IN (Select item_group_code from item where item_code IN (Select item_code from order_detail Where order_code='"+strOrderCode+"'))";

            Cursor cursor = database.rawQuery(sqlQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    String categoryIps = cursor.getString(0);
                    ipAdd.add(categoryIps);

                    ///arrayListTable.add(new Table(this, null, tablecode, tablename, zone_id, zone_name, "", "", "",""));
                } while (cursor.moveToNext());

            }

            cursor.close();

    // Old Code

     /*       itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            if (itemgroup_catArrayList.size() > 0) {
                for (int i = 0; i < itemgroup_catArrayList.size(); i++) {

                   // if(itemgroup_catArrayList.get(i).getCategoryIp().equals(ip)) {
                        ipAdd.add(itemgroup_catArrayList.get(i).getCategoryIp());
                    //}
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void performOperationEn(String ip, int iOperation, String strMode,Orders orders,Order_Detail order_detail,ProgressDialog pDialog) {
        try {
            mylist.clear();

            try {
                getItemCategoryForPrint(ip, iOperation, strMode);
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            if (catId.size() > 0) {
                try {
                    mylist = getlistTest(iOperation, strMode, ip,orders);
                } catch (Exception e) {

                    System.out.println(e.getMessage());
                }
                Globals.AppLogWrite(mylist);
                if (mylist.size() > 0) {
                    try {


                        ArrayList<String> lPrintLog1 = new ArrayList<>();
                        lPrintLog1.add("Operation :" + iOperation);
                        lPrintLog1.add("Mode :" + strMode);
                        lPrintLog1.add("Current Printer :" + ip);
                        lPrintLog1.add("Total Item   :" + Globals.cart.size() + "");
                       /* for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                            lPrintLog1.add("Count   :" + (lCount + 1) + "");
                            ShoppingCart cartitem = Globals.cart.get(lCount);
                            try {
                                lPrintLog1.add("Item IP :  " + cartitem.getCategoryIp().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item IP :  " + ex.getMessage().toString());
                            }

                 *//*           try {
                                lPrintLog1.add("Item Print Flag :  " + cartitem.getPrintFlag().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Print Falg :  " + ex.getMessage().toString());
                            }
                            try {
                                lPrintLog1.add("Item New :  " + cartitem.isNew() + "");
                            } catch (Exception ex) {
                                lPrintLog1.add("Item New Falg :  " + ex.getMessage().toString());
                            }*//*
                            try {
                                lPrintLog1.add("Item  :  " + cartitem.get_Item_Name().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Name :  " + ex.getMessage().toString());
                            }
                        }
*/
         /*               if (iOperation == 0 && strMode.equals("Order")) {
                            for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                ShoppingCart cartItem = Globals.cart.get(lCount);
                                try {
                                    if (cartItem.getCategoryIp().equals(ip)) {
                                        if (order_detail.getIs_KitchenPrintFlag().equals("false")) {
                                           // lPrintLog.add(cartItem.get_Item_Name());
                                            order_detail.setIs_KitchenPrintFlag("true");
                                        }
                                    }
                                } catch (Exception e) {

                                }


                            }
                        }*/
                        Globals.AppLogWrite(lPrintLog1);


                                if (CheckprinterConnection(ip.trim())) {
                            try {
                                String bill = "";
                                for (String data : mylist) {
                                    bill = bill + data;
                                }
                                for (int k = 1; k <= Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    WifiPrintDriver.Begin();
                                    WifiPrintDriver.ImportData(bill);
                                    WifiPrintDriver.ImportData("\r");
                                    WifiPrintDriver.excute();
                                    WifiPrintDriver.ClearData();
                                    String str = "\r\n\r\n\r\n\r\n";
                                    byte[] feed = str.getBytes();
                                    WifiPrintDriver.SPPWrite(feed);
                                    byte[] paramString1 = new byte[]{27, 109, 2};
                                    WifiPrintDriver.SPPWrite(paramString1);
                                    WifiPrintDriver.excute();
                                    WifiPrintDriver.ClearData();

                                    ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add(ip);
                                    lPrintLog.add(" Printing time ip ------------------");

                                    if (iOperation == 0 && strMode.equals("Order")) {
                                        for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                            ShoppingCart cartItem = Globals.cart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getKitchenprintflag().equals("false")) {
                                                        lPrintLog.add(cartItem.get_Item_Name());
                                                      String sql= "UPDATE order_detail SET is_KitchenPrintFlag = 'true' where item_code= '"+cartItem.get_Item_Code()+"' and order_code='"+strOrderCode+"'";
                                                       db.executeDML(sql,database);
                                                      //  order_detail.setIs_KitchenPrintFlag("true");
                                                    }
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                       // pDialog.dismiss();
                                      /*  Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    else  if (iOperation == 0 && strMode.equals("Void")) {
                                        for (int lCount = 0; lCount < Globals.voidcart.size(); lCount++) {
                                            VoidShoppingCart cartItem = Globals.voidcart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getVoidkitchenflag().equals("false")) {
                                                        String sql = "UPDATE Void SET print_flag = 'true' where item_code= '" + cartItem.get_Item_Code() + "' and order_no='" + strOrderCode + "'";
                                                        db.executeDML(sql, database);
                                                    }
                                                    //  order_detail.setIs_KitchenPrintFlag("true");
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                        // pDialog.dismiss();
                                   /*     Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    Globals.AppLogWrite(lPrintLog);


                                }


                                // Globals.isSendOnline = true;
                            } catch (Exception e) {
                                //GlobleVar.isSendOnline = true;

                            }

                        }


                    } catch (Exception ex) {
                        //  GlobleVar.isSendOnline = true;
                        Toast.makeText(getApplicationContext(), "Please Check Printer Connection", Toast.LENGTH_SHORT).show();
                    }

                   // pDialog.dismiss();
                  //  Globals.setEmpty();

                } else {
                    // GlobleVar.isSendOnline = true;
                }


            } else {

                //GlobleVar.isSendOnline = true;
            }
        } catch (Exception ex) {
            // GlobleVar.isSendOnline = true;
            ex.getStackTrace();
        }

    }

    private void getItemCategoryForPrint(String ip, int iOperation, String strMoString) {
        catId.clear();
        final ArrayList<ShoppingCart> myCart = Globals.cart;
        if (strMoString.equals("Order")) {
            if (iOperation == 0) {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0'", database, db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < myCart.size(); i++) {
                    ShoppingCart cartitem = myCart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");


                    if (cartitem.getIs_modifier().equals("1")) {
                        if (dtl_modifier != null) {
                            Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);

                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                        if (!catId.contains(item.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(item.get_item_group_code());
                                        }
                                    } /*else {
                                        if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                            if (!catId.contains(categoryitem.get_item_group_code())) {
                                                catId.add(categoryitem.get_item_group_code());
                                            }
                                        }
                                    }*/

                                }

                            }
                        }
                    } else {
                        Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + myCart.get(i).get_Item_Code() + "'",database);
                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code()+ "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                    if (!catId.contains(item.get_item_group_code())&& cartitem.getCategoryIp().equals(ipAdd.get(a))){
                                        catId.add(item.get_item_group_code());
                                    }
                                }
                            } /*else {
                                if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code())) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                            }*/
                        }

                    }
                }
            }


        } else {
            if (iOperation == 0) {


                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where categoryIp = '" + ip.trim() + "' and (is_active ='1' and parent_code = '0')", database,db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where (Active='1' or Active='1')", database,db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getItem_code() + "'",database);

                            Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }
                                } else {
                                   if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                      //  Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                // if (!item.getPrintFlag().equals("true")) {
                                     if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }
                                // }
                            } else {
                              if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }
                            }

                        }

                    }
                }
            }



        }

    }

   /* private ArrayList<String> getlistTest(int iOperation, String strMode, String Ip,Orders orders) {
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + orders.get_order_type_id() + "'", database, db);

        if (strMode.equals("Order")) {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "KOT";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );

            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.cart.size(); i++) {

                    ShoppingCart cartitem = Globals.cart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            //Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + itemgroup_catArrayList.get(a).get_item_group_code()+ "'", database,db);
                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);

                            //     Item aetSet_pos_menu_item = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + catId.get(a) + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //item.setPrintFlag("true");

                                        qty = cartitem.get_Quantity()+ "";
                                        // cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            if(ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    raw = qty + "   " + name + "(M)";
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                raw = qty + "   " + name + " (M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);
                        if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                   *//* ArrayList<String> lPrintLog1 = new ArrayList<>();
                                    lPrintLog1.add(ip);
                                    lPrintLog1.add("-------- get item operation 0");
                                    GlobleVar.AppLogWrite(lPrintLog1);*//*
                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);

                               *//*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*//*
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            if(ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    //cartitem.setCategoryIp(Ip);

                               *//*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*//*
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        if(ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            } else {
                               *//* ArrayList<String> lPrintLog = new ArrayList<>();
                                lPrintLog.add("-------- get item operation 1");
                                lPrintLog.add(Ip);
                                GlobleVar.AppLogWrite(lPrintLog);*//*

                                // working code
//                                if (cartitem.getIsmaster_PrintFlag().equals("true")) {
//                                } else {
//                                    qty = cartitem.get_Quantity() + "";
//                                    //item.setCatIp(Ip);
//                                    name = cartitem.get_Item_Name();
//                                    if (name == null) {
//                                    } else {
//                                        bFinalCheck = false;
//                                        bCheck = false;
//                                        raw = qty + "   " + name;
//                                        mylist.add("\n" + raw);
//                                    }
//
//                                }
                            }

                        }
                    }
                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("Order"+ mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;
        }
        else
        {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "Void KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "Void KOT";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );


            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);


                    if (cartitem.getIs_modifier().equals("1")) {
                        ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");
                        if (dtl_modifier != null) {

                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (strMode.equals("Order")) {
                                        if (aetSet_pos_menu_item.getIs_KitchenPrintFlag() .equals("true")) {
                                        } else {
//                                        item.setPrintFlag("true");
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name()+ "(M)";
                                            if (name == null) {
                                            } else {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name + " (M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    } else {
                                        qty = cartitem.get_Quantity()+ "";

                                        name = item.get_item_name() + "(M)";
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name + " (M)";
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                } else {
                                    // item.setPrintFlag("true");
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name()+ "(M)";
                                    ;
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        raw = qty + "   " + name + "(M)";
                                        mylist.add("\n" + raw);
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);                            if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //  item.setPrintFlag("true");
                                        qty = cartitem.get_Quantity() + "";
                                        cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name + " (M)";
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    cartitem.setCategoryIp(Ip);
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        raw = qty + "   " + name +" (M)";
                                        mylist.add("\n" + raw);
                                    }
                                }

                            } else {

                                //   item.setPrintFlag("true");
                                qty = cartitem.get_Quantity() + "";
                                cartitem.setCategoryIp(Ip);
                                name = cartitem.get_Item_Name();
                                if (name == null) {
                                } else {
                                    bFinalCheck = false;
                                    bCheck = false;
                                    raw = qty + "   " + name +"(M)";
                                    mylist.add("\n" + raw);
                                }

                            }

                        }
                    }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("VOID"+mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;

        }
//return  mylist;
    }*/

    private ArrayList<String> getlistTest(int iOperation, String strMode, String Ip,Orders orders) {
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + orders.get_order_type_id() + "'", database, db);

        if (strMode.equals("Order")) {


            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );

            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.cart.size(); i++) {


                            ShoppingCart cartitem = Globals.cart.get(i);
                            ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");

                            if (cartitem.getIs_modifier().equals("1")) {


                                if (dtl_modifier != null) {
                                    //Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + itemgroup_catArrayList.get(a).get_item_group_code()+ "'", database,db);
                                    Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                                    Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);

                                    //     Item aetSet_pos_menu_item = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + catId.get(a) + "'", database,db);
                                    if (aetSet_pos_menu_item != null) {
                                        if (iOperation == 0) {
                                            if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                            } else {
                                                //item.setPrintFlag("true");

                                                qty = cartitem.get_Quantity() + "";
                                                // cartitem.setCategoryIp(Ip);
                                                name = cartitem.get_Item_Name();
                                                if (name == null) {
                                                } else {

                                                    if (ip.equals(cartitem.getCategoryIp())) {
                                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                                            bFinalCheck = false;
                                                            bCheck = false;
                                                            raw = qty + "   " + name + "(M)";
                                                            mylist.add("\n" + raw);
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name();
                                            if (name == null) {
                                            } else {

                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name + " (M)";
                                                        mylist.add("\n" + raw);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            } else {
                                Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'", database);

                                Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);
                                if (aetSet_pos_menu_item != null) {
                                    if (iOperation == 0) {
                                        if (strMode.equals("Order")) {
                                            if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                            } else {
                                   /* ArrayList<String> lPrintLog1 = new ArrayList<>();
                                    lPrintLog1.add(ip);
                                    lPrintLog1.add("-------- get item operation 0");
                                    GlobleVar.AppLogWrite(lPrintLog1);*/
                                                qty = cartitem.get_Quantity() + "";
                                                // cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                                name = cartitem.get_Item_Name();
                                                if (name == null) {
                                                } else {

                                                    if (ip.equals(cartitem.getCategoryIp())) {
                                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                                            bFinalCheck = false;
                                                            bCheck = false;
                                                            raw = qty + "   " + name;
                                                            mylist.add("\n" + raw);
                                                        }
                                                    }
                                                }
                                            }

                                        } else {
                                            qty = cartitem.get_Quantity() + "";
                                            //cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                            name = cartitem.get_Item_Name();
                                            if (name == null) {
                                            } else {

                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name;
                                                        mylist.add("\n" + raw);
                                                    }
                                                }
                                            }
                                        }

                                    } else {
                               /* ArrayList<String> lPrintLog = new ArrayList<>();
                                lPrintLog.add("-------- get item operation 1");
                                lPrintLog.add(Ip);
                                GlobleVar.AppLogWrite(lPrintLog);*/

                                        // working code
//                                if (cartitem.getIsmaster_PrintFlag().equals("true")) {
//                                } else {
//                                    qty = cartitem.get_Quantity() + "";
//                                    //item.setCatIp(Ip);
//                                    name = cartitem.get_Item_Name();
//                                    if (name == null) {
//                                    } else {
//                                        bFinalCheck = false;
//                                        bCheck = false;
//                                        raw = qty + "   " + name;
//                                        mylist.add("\n" + raw);
//                                    }
//
//                                }
                                    }

                                }
                            }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("Order"+ mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;
        }
        else
        {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "Void KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "Void KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name());


            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);


                            if (cartitem.getIs_modifier().equals("1")) {
                                ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");
                                if (dtl_modifier != null) {

                                    Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                                    Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code()+ "'", database, db);
                                    if (cartitem != null) {
                                        if (iOperation == 0) {
                                            if (strMode.equals("Order")) {
                                                if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                                } else {
//                                        item.setPrintFlag("true");
                                                    qty = cartitem.get_Quantity() + "";

                                                    name = cartitem.get_Item_Name();
                                                    if (name == null) {
                                                    } else {
                                                        if (ip.equals(cartitem.getCategoryIp())) {
                                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                                bFinalCheck = false;
                                                                bCheck = false;
                                                                raw = qty + "   " + name + " (M)";
                                                                mylist.add("\n" + raw);
                                                            }}
                                                    }
                                                }
                                            } else {
                                                qty = cartitem.get_Quantity() + "";

                                                name = item.get_item_name();
                                                if (name == null) {
                                                } else {
                                                    if (ip.equals(cartitem.getCategoryIp())) {
                                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                                            bFinalCheck = false;
                                                            bCheck = false;
                                                            raw = qty + "   " + name + " (M)";
                                                            mylist.add("\n" + raw);
                                                        }}
                                                }
                                            }
                                        } else {
                                            // item.setPrintFlag("true");
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name();
                                            ;
                                            if (name == null) {
                                            } else {
                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name + "(M)";
                                                        mylist.add("\n" + raw);
                                                    }
                                                }}
                                        }

                                    }
                                }
                            } else {
                                //  Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                                Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database, db);
                                if (cartitem != null) {
                                    if (iOperation == 0) {
                                        if (strMode.equals("Order")) {
                                            if (cartitem.getVoidkitchenflag().equals("true")) {
                                            } else {
                                                //  item.setPrintFlag("true");
                                                qty = cartitem.get_Quantity() + "";
                                                // cartitem.setCategoryIp(Ip);
                                                name = cartitem.get_Item_Name();
                                                if (name == null) {
                                                } else {

                                                    if (ip.equals(cartitem.getCategoryIp())) {
                                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                                            bFinalCheck = false;
                                                            bCheck = false;
                                                            raw = qty + "   " + name;
                                                            mylist.add("\n" + raw);
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            qty = cartitem.get_Quantity() + "";
                                            //cartitem.setCategoryIp(Ip);
                                            name = cartitem.get_Item_Name();
                                            if (name == null) {
                                            } else {

                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name;
                                                        mylist.add("\n" + raw);
                                                    }
                                                }
                                            }
                                        }

                                    } else {

                                        //   item.setPrintFlag("true");
                                        qty = cartitem.get_Quantity() + "";
                                        //cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }

                                    }

                                }
                            }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("VOID"+mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;

        }
//return  mylist;
    }
   /* private ArrayList<String> getlistTest(int iOperation, String strMode, String Ip,Orders orders) {
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + orders.get_order_type_id() + "'", database, db);

        if (strMode.equals("Order")) {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "KOT";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );

            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.cart.size(); i++) {

                    ShoppingCart cartitem = Globals.cart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            //Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + itemgroup_catArrayList.get(a).get_item_group_code()+ "'", database,db);
                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);

                            //     Item aetSet_pos_menu_item = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + catId.get(a) + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //item.setPrintFlag("true");

                                        qty = cartitem.get_Quantity()+ "";
                                        cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            if(catId.get(a).equals(item.get_item_group_code())) {
                                                raw = qty + "   " + name + "(M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        if(catId.get(a).equals(item.get_item_group_code())) {
                                            raw = qty + "   " + name + " (M)";
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);
                        if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                   *//* ArrayList<String> lPrintLog1 = new ArrayList<>();
                                    lPrintLog1.add(ip);
                                    lPrintLog1.add("-------- get item operation 0");
                                    GlobleVar.AppLogWrite(lPrintLog1);*//*
                                        qty = cartitem.get_Quantity() + "";
                                        cartitem.setCategoryIp(Ip);

                               *//*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*//*
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            if(catId.get(a).equals(item.get_item_group_code())) {
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }

                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    cartitem.setCategoryIp(Ip);

                               *//*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*//*
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        if(catId.get(a).equals(item.get_item_group_code())) {
                                            raw = qty + "   " + name;
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                }

                            } else {
                               *//* ArrayList<String> lPrintLog = new ArrayList<>();
                                lPrintLog.add("-------- get item operation 1");
                                lPrintLog.add(Ip);
                                GlobleVar.AppLogWrite(lPrintLog);*//*

                                // working code
//                                if (cartitem.getIsmaster_PrintFlag().equals("true")) {
//                                } else {
//                                    qty = cartitem.get_Quantity() + "";
//                                    //item.setCatIp(Ip);
//                                    name = cartitem.get_Item_Name();
//                                    if (name == null) {
//                                    } else {
//                                        bFinalCheck = false;
//                                        bCheck = false;
//                                        raw = qty + "   " + name;
//                                        mylist.add("\n" + raw);
//                                    }
//
//                                }
                            }

                        }
                    }
                }
            }
            mylist.add("\n");

            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;
        }
        else
        {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "Void KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "Void KOT";
            for (int i = 0; i < part; i++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );


            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);


                    if (cartitem.getIs_modifier().equals("1")) {
                        ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");
                        if (dtl_modifier != null) {

                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (strMode.equals("Order")) {
                                        if (aetSet_pos_menu_item.getIs_KitchenPrintFlag() .equals("true")) {
                                        } else {
//                                        item.setPrintFlag("true");
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name()+ "(M)";
                                            if (name == null) {
                                            } else {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    } else {
                                        qty = cartitem.get_Quantity()+ "";

                                        name = item.get_item_name() + "(M)";
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name;
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                } else {
                                    // item.setPrintFlag("true");
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name()+ "(M)";
                                    ;
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        raw = qty + "   " + name;
                                        mylist.add("\n" + raw);
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database,db);                            if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //  item.setPrintFlag("true");
                                        qty = cartitem.get_Quantity() + "";
                                        cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name;
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    cartitem.setCategoryIp(Ip);
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {
                                        bFinalCheck = false;
                                        bCheck = false;
                                        raw = qty + "   " + name;
                                        mylist.add("\n" + raw);
                                    }
                                }

                            } else {

                                //   item.setPrintFlag("true");
                                qty = cartitem.get_Quantity() + "";
                                cartitem.setCategoryIp(Ip);
                                name = cartitem.get_Item_Name();
                                if (name == null) {
                                } else {
                                    bFinalCheck = false;
                                    bCheck = false;
                                    raw = qty + "   " + name;
                                    mylist.add("\n" + raw);
                                }

                            }

                        }
                    }

                }
            }
            mylist.add("\n");

            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;

        }
//return  mylist;
    }*/


    public  class PrintKOT_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        MainActivity activity;

        public PrintKOT_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

               // printKOT(strOrderCode);

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }

    }
}