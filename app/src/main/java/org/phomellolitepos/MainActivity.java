package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.DialogOrderTypeListAdapter;
import org.phomellolitepos.Adapter.DialogTableMainListAdapter;
import org.phomellolitepos.Adapter.ParentCategoryListAdapter;
import org.phomellolitepos.Adapter.RetailListAdapter;
import org.phomellolitepos.Fragment.ItemFragment;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.BottomNavigationViewHelper;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.Util.ZipManager;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Customer_Image;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ZBarScannerView.ResultHandler {
    TabLayout tabLayout;
    ViewPager viewPager;
    MenuItem items;
    Dialog listDialog;
    Dialog listDialog1;
    Dialog listDialog2;
    Dialog listDialog_table;
    Item item;
    String email,password;
    TextView list_title, my_company_name, my_company_email, txt_user_name, txt_version;
    Button btn_Item_Price, btn_Qty;
    Item_Group item_group;
    ArrayList<Item> arrayList;
    ArrayList<android.support.v4.app.Fragment> fragList;
    private SearchView searchView;
    String operation = "";
    String code_category = "";
    ArrayList<Item_Group> arrayListcategory;
    ArrayList<ShoppingCart> cart;
    SQLiteDatabase database;
    Database db;
    ArrayList<Order_Type> order_typeArrayList;
    ArrayList<Contact> contact_ArrayList;
    ArrayList<Table> table_ArrayList;
    Order_Type order_type;
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
    private Settings settings;
    private String PrinterType = "";
    String strRemarks = "";
    ArrayList<String> arrayListGetFile;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String displayTilte, line_total_af, line_total_bf;
    ProgressDialog pDialog;
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
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type, item_group_code, sale_priceStr, cost_priceStr;
    Double curQty = 0d;
    int OrintValue;
    ZBarScannerView mScannerView;
    String serial_no, android_id, myKey, device_id, imei_no;
    Menu menu2;
    String reg_code;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
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
        super.onCreate(savedInstanceState);
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

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        final Intent intent = getIntent();
        listDialog2 = new Dialog(this);

        mScannerView = new ZBarScannerView(MainActivity.this);
        mScannerView.setResultHandler(MainActivity.this);

        settings = Settings.getSettings(getApplicationContext(), database, "");
        if (settings == null) {
            PrinterType = "";
        } else {
            try {
                PrinterType = settings.getPrinterId();
            } catch (Exception ex) {
                PrinterType = "";
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

        Intent intent1 = new Intent();
        intent1.setPackage("woyou.aidlservice.jiuiv5");
        intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent1);
        bindService(intent1, connService, Context.BIND_AUTO_CREATE);

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        if (settings.get_Is_Customer_Display()==null){
        }else {
            if (settings.get_Is_Customer_Display().equals("true")) {
                ArrayList<Customer_Image> arrayList = Customer_Image.getAllCustomer_Image(getApplicationContext(), "", database);
                if (arrayList.size() > 0) {
                    for (int count = 0; count < arrayList.size(); count++) {
                        ArrayList<String> arrayListImages = new ArrayList<>();
                        arrayListImages.add(arrayList.get(count).get_image_Path());
                        Globals.CMD_Images = arrayListImages;
                    }
                }
            }

            if (settings.get_Is_Customer_Display().equals("true")) {
                mDSKernel = DSKernel.newInstance();
                mDSKernel.checkConnection();
                mDSKernel.init(getApplicationContext(), mConnCallback);
                mDSKernel.addReceiveCallback(mReceiveCallback);
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                            displayTilte = lite_pos_registration.getCompany_Name();
                            if (settings.get_CustomerDisplay().equals("1")) {
                                call_CMD();
                                call_MN();
                            } else {
                                call_customer_disply_title(displayTilte);
                                call_MN();
                            }
                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        }

        if (PrinterType.equals("3")||PrinterType.equals("4")||PrinterType.equals("5")) {
            if (Globals.strIsBlueService.equals("utc")) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            } else if (con_dev == null) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        }
        root = findViewById(R.id.anim_root);
        topNavigationView = (BottomNavigationView) findViewById(R.id.top_navigation);
        BottomNavigationViewHelper.disableShiftMode(topNavigationView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        modified_by = Globals.user;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei_no = telephonyManager.getImei();
        }
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
            qty_decimal_check = settings.get_Qty_Decimal();
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
                    cartItem = new ShoppingCart(getApplicationContext(), order_detail.get(i).get_sr_no(), order_detail.get(i).get_item_code(), item.get_item_name(), order_detail.get(i).get_quantity(), order_detail.get(i).get_cost_price(), order_detail.get(i).get_sale_price(), order_detail.get(i).get_tax(), "0", order_detail.get(i).get_line_total());
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
        list_title = (TextView) findViewById(R.id.list_title);
        fragList = new ArrayList<Fragment>();
        if (!Globals.Industry_Type.equals("3")) {
            setupViewPager(viewPager, "Main", "", "", "");
            btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_Item_Price.setText(itemPrice);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            btn_Qty.setVisibility(View.GONE);
            btn_Item_Price.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
        }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                String ItemCategoryCode = arrayListcategory.get(tab.getPosition()).get_item_group_code();
                call_parent_dialog(ItemCategoryCode);
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
        try {
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            if (lite_pos_registration.getproject_id().equals("cloud")) {
                nav_item = menu.findItem(R.id.nav_purchese);
                nav_item.setVisible(false);
                nav_item = menu.findItem(R.id.nav_stock_adjest);
                nav_item.setVisible(false);
            } else {
                nav_item = menu.findItem(R.id.nav_purchese);
                nav_item.setVisible(false);
            }
        } catch (Exception ex) {}

        // Industry_Type 3 for payment collection
        // Industry_Type 1 for restruant
        // Industry_Type 2 for saloon
        // Industry_Type 5 for retail
        // Industry_Type 4 for parking
        if (Globals.Industry_Type.equals("3")) {
            nav_item = menu.findItem(R.id.nav_item_category);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_item);
            nav_item.setVisible(false);

            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_recepts);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_man);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);

            nav_item = menu.findItem(R.id.nav_acc);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_stock_adjest);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_purchese);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_return);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_manufacture);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_class);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_destination);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_ticketing);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_setup);
            nav_item.setVisible(false);

            bottomNavigationView.setVisibility(View.GONE);
            topNavigationView.setVisibility(View.GONE);

        } else if (Globals.Industry_Type.equals("1")) {
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_manufacture);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_class);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_destination);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_ticketing);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_setup);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
        } else if (Globals.Industry_Type.equals("2")) {
//            nav_item = menu.findItem(R.id.nav_resv);
//            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_manufacture);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_class);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_destination);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_ticketing);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_setup);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
        } else if (Globals.Industry_Type.equals("5")) {
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_class);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_destination);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_ticketing);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_setup);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_manufacture);
            nav_item.setVisible(false);

        } else if (Globals.Industry_Type.equals("4")) {
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_manufacture);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_class);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_destination);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_ticketing);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_setup);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);
//            nav_item = menu.findItem(R.id.nav_item_tax);
//            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_acc);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_stock_adjest);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_purchese);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_return);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
        } else if (Globals.Industry_Type.equals("6")) {

            nav_item = menu.findItem(R.id.nav_item_category);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_item);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_search_order);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_recepts);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_man);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_resv);
            nav_item.setVisible(false);
//          nav_item = menu.findItem(R.id.nav_item_tax);
//          nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_acc);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_stock_adjest);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_purchese);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_return);
            nav_item.setVisible(false);
            nav_item = menu.findItem(R.id.nav_pay_collection);
            nav_item.setVisible(false);
        }

        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.right_nav_view);

//        View hView1 = navigationViewRight.getHeaderView(0);
//        my_company_name = (TextView) hView1.findViewById(R.id.my_company_name);
//        my_company_email = (TextView) hView1.findViewById(R.id.my_company_email);
//        txt_user_name = (TextView) hView1.findViewById(R.id.txt_user_name);

        Menu menu1 = navigationViewRight.getMenu();
        MenuItem nav_item1;
        // Industry_Type 3 for payment collection
        // Industry_Type 1 for restruant
        // Industry_Type 2 for saloon
        // Industry_Type 5 for retail
        if (lite_pos_registration.getproject_id().equals("standalone")) {
            nav_item1 = menu1.findItem(R.id.nav_coupon);
            nav_item1.setVisible(false);
            nav_item1 = menu1.findItem(R.id.nav_loyalty);
            nav_item1.setVisible(false);
        } else {
            nav_item1 = menu1.findItem(R.id.nav_coupon);
            nav_item1.setVisible(false);
            nav_item1 = menu1.findItem(R.id.nav_loyalty);
            nav_item1.setVisible(false);
        }
//        if (lite_pos_registration.getproject_id().equals("cloud")) {
////            nav_item1 = menu1.findItem(R.id.nav_coupon);
////            nav_item1.setVisible(false);
////            nav_item1 = menu1.findItem(R.id.nav_loyalty);
////            nav_item1.setVisible(false);
//        }
        if (Globals.Industry_Type.equals("3")) {
            nav_item1 = menu1.findItem(R.id.nav_unit);
            nav_item1.setVisible(false);
            nav_item1 = menu1.findItem(R.id.nav_tax);
            nav_item1.setVisible(false);
            nav_item1 = menu1.findItem(R.id.nav_get_file);
            nav_item1.setVisible(false);
        } else if (Globals.Industry_Type.equals("6")) {
            nav_item1 = menu1.findItem(R.id.nav_unit);
            nav_item1.setVisible(false);
            nav_item1 = menu1.findItem(R.id.nav_get_file);
            nav_item1.setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(this);

        Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        try {
            PackageInfo pInfo = null;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            my_company_name.setText(Globals.objLPR.getCompany_Name());
            my_company_email.setText(Globals.objLPR.getEmail());
            txt_user_name.setText(user.get_name());
           email =user.get_email();
            password= user.get_password();
            txt_version.setText("Version : " + pInfo.versionName);
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
                                    pDialog = new ProgressDialog(MainActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);
                                                pDialog.dismiss();
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
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } finally {
                                            }
                                        }
                                    };
                                    timerThread.start();
                                }


                                break;
                            case R.id.action_save:
                                if (settings.get_Is_Customer_Display().equals("true")) {
                                    mDSKernel = DSKernel.newInstance();
                                    mDSKernel.checkConnection();
                                    mDSKernel.init(getApplicationContext(), mConnCallback);
                                    mDSKernel.addReceiveCallback(mReceiveCallback);
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                displayTilte = lite_pos_registration.getCompany_Name();
                                                if (settings.get_CustomerDisplay().equals("1")) {
                                                    call_CMD();
                                                    call_MN();
                                                } else {
                                                    call_customer_disply_title(displayTilte);
                                                    call_MN();
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
                                                sleep(1000);
                                                pDialog.dismiss();
                                                Globals.Order_Code = "";
                                                Globals.CMDItemPrice = 0;
                                                Globals.CMDItemName = "";
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if (opr.equals("Edit")) {
                                                            save_order(strRemarks);
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
                                if (settings.get_Is_Customer_Display().equals("true")) {
                                    mDSKernel = DSKernel.newInstance();
                                    mDSKernel.checkConnection();
                                    mDSKernel.init(getApplicationContext(), mConnCallback);
                                    mDSKernel.addReceiveCallback(mReceiveCallback);
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                displayTilte = lite_pos_registration.getCompany_Name();
                                                if (settings.get_CustomerDisplay().equals("1")) {
                                                    call_CMD();
                                                    call_MN();
                                                } else {
                                                    call_customer_disply_title(displayTilte);
                                                    call_MN();
                                                }
                                            } finally {
                                            }
                                        }
                                    };
                                    timerThread.start();
                                }
                                change_table_icon();
                                defult_ordertype();
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Globals.CheckContact = "0";
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
                        if (settings.get_Is_BR_Scanner_Show().equals("true")) {
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
                        setupViewPager(viewPager, "Main", "", "", "");

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

                if (id == R.id.nav_tax) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "Tax", TaxListActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_user) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "User", UserListActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }
                } else if (id == R.id.nav_unit) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "Unit", UnitListActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_database) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "Database", DataBaseActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_lic) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "Update License", ActivateActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_profile) {
                    userPermission = new UserPermission();
                    Boolean result = userPermission.Permission(MainActivity.this, "Profile", ProfileActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_ab) {
                    Intent intent_about = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent_about);
                    finish();
                } else if (id == R.id.nav_support) {
                    Intent intent_about = new Intent(MainActivity.this, YouTubeVideoListActivity.class);
                    startActivity(intent_about);
                    finish();
                } else if (id == R.id.nav_get_file) {
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
                                        DownloadFileFromURL("http://" + Globals.App_IP + "/lite-pos-lic/upload/demo_files/" + arrayListGetFile.get(i) + "", "" + arrayListGetFile.get(i) + "");
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
                } else if (id == R.id.nav_send_db) {
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
    }

    public void call_parent_dialog(String ItemCategoryCode) {
        listDialog = new Dialog(MainActivity.this);
        listDialog.setTitle("Select Category");
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.order_type_list, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
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

        Globals.TabPos = viewPager.getCurrentItem();
        setupViewPager(viewPager, "Main", "", ItemCategoryCode, "Parent");
    }


    public void change_customer_icon() {
        topNavigationView = (BottomNavigationView) findViewById(R.id.top_navigation);
        Menu menu2 = topNavigationView.getMenu();
        MenuItem cusIcon = menu2.findItem(R.id.action_category);
        if (Globals.strContact_Code.equals("") && Globals.strResvContact_Code.equals("")) {
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

    private void retail_list_load() {
        ArrayList<ShoppingCart> myCart = Globals.cart;
        ListView retail_list = (ListView) findViewById(R.id.retail_list);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        if (myCart.size() > 0) {
            RetailListAdapter retailListAdapter = new RetailListAdapter(MainActivity.this, myCart, opr, strOrderCode, "MainLand");
            retail_list.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
            retail_list.setAdapter(retailListAdapter);
            retailListAdapter.notifyDataSetChanged();
        } else {
            retail_list.setVisibility(View.GONE);
            txt_title.setVisibility(View.VISIBLE);
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
    }

    private void defult_ordertype() {
        Menu menu = topNavigationView.getMenu();
        orertype = menu.findItem(R.id.action_order_type);
        if (settings.get_Default_Ordertype() == null) {
            settings.set_Default_Ordertype("1");
        }
        switch (settings.get_Default_Ordertype()) {
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

        Globals.strOrder_type_id = settings.get_Default_Ordertype();
        String strOrder_type = Globals.strOrder_type_id;
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id ='" + strOrder_type + "'", database, db);
        try {
            String strOrder_name = order_type.get_name();
            orertype.setTitle(strOrder_name);
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
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
        }
    }


    private void call_MN() {
        try {
            woyouService.sendLCDDoubleString("Welcome to", Globals.objLPR.getCompany_Name(), null);
        } catch (Exception ex) {
        }
    }

    private void call_customer_disply_title(final String displayTilte) {

        Thread timerThread1 = new Thread() {
            public void run() {
                try {
                    try {
                        for (int i = 0; i < Globals.CMD_Images.size(); i++) {
                            try {
                                JSONObject json = new JSONObject();
                                json.put("title", "Welcome");
                                json.put("content", Globals.objLPR.getCompany_Name());
                                String titleContentJsonStr = json.toString();
                                mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images.get(i), new ISendCallback() {
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
                        }
                    } catch (Exception ex) {
                    }
                } finally {
                }
            }
        };
        timerThread1.start();
    }

    private void save_order(String strRemarks) {
        String strFlag1 = "0";
        String strOrdeeStatus = "OPEN";
        String strOrderNo = "";
        Order_Detail objOrderDetail;
        Orders objOrder;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        database.beginTransaction();

        if (opr.equals("Edit")) {
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            objOrder = Orders.getOrders(getApplicationContext(), database, "  WHERE order_code = '" + strOrderCode + "'");
            objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), liccustomerid, locCode, Globals.strOrder_type_id, strOrderCode, objOrder.get_order_date(), Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, objOrder.get_remarks(), Globals.strTable_Code, "");
            long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderCode, objOrder.get_order_id()}, database);
            if (l > 0) {
                strFlag1 = "1";
                long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderCode}, database);
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, liccustomerid, strOrderCode,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }

                long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderCode}, database);
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
            objOrder = new Orders(getApplicationContext(), null, liccustomerid, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, strRemarks, Globals.strTable_Code, "");
            long l = objOrder.insertOrders(database);

            if (l > 0) {
                strFlag1 = "1";
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, liccustomerid, strOrderNo,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                    long o = objOrderDetail.insertOrder_Detail(database);
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
            database.setTransactionSuccessful();
            database.endTransaction();
            Globals.setEmpty();
            Globals.strContact_Code = "";
            Globals.strResvContact_Code = "";
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
                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images.get(i), new ISendCallback() {
                                @Override
                                public void onSendSuccess(long fileId) {
                                    showQRCode(fileId);//sending the qr-code image
                                }

                                public void onSendFail(int i, String s) {
                                }

                                public void onSendProcess(long l, long l1) {
                                }
                            });
                        }
                    } catch (Exception ex) {
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
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (Globals.Industry_Type.equals("3")) {
            searchView.setVisibility(View.GONE);
            searchItem = menu.findItem(R.id.table);
            searchItem.setVisible(false);
            searchItem = menu.findItem(R.id.search);
            searchItem.setVisible(false);
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);
            ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
            searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.black));
            searchEditText.setBackgroundColor(getResources().getColor(R.color.white));
            searchEditText.setHintTextColor(getResources().getColor(R.color.search_hint_color));
            searchEditText.setHint(R.string.Search);
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
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
                    if (settings.get_Is_Zero_Stock().equals("true")) {
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
                                                            if (event.getAction() == KeyEvent.ACTION_DOWN
                                                                    && keyCode == KeyEvent.KEYCODE_ENTER) {
                                                                String strValue = searchEditText.getText().toString();

                                                                if (searchEditText.getText().toString().equals("\n") || searchEditText.getText().toString().equals("")) {
                                                                    Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                                    searchEditText.requestFocus();
                                                                } else {
                                                                    String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                                    arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                                    if (arrayList.size() >= 1) {
                                                                        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                                        ck_project_type = lite_pos_registration.getproject_id();

                                                                        if (settings.get_Is_Stock_Manager().equals("false")) {
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
                                                                                ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
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
                                                                                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
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
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.table) {
            Table table = Table.getTable(getApplicationContext(), database, db, "");
            if (table == null) {
                Toast.makeText(getApplicationContext(), R.string.No_Table_Fnd, Toast.LENGTH_SHORT).show();
            } else {
                showdialogTable();
            }
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

        if (id == R.id.nav_item) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_item_category) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_recepts) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_settings) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_lic) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_Report) {
            userPermission = new UserPermission();
            Boolean result = userPermission.Permission(MainActivity.this, "Report", ReportActivity.class);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ReportActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_logout) {
            Globals.user = " ";
            Globals.setEmpty();
            getAlertDialog();
        /*    Intent item_intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(item_intent);
            finish();*/
        } else if (id == R.id.nav_contact) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_search_order) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_man) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_resv) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_pay_collection) {
            userPermission = new UserPermission();
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

        } else if (id == R.id.nav_acc) {
            userPermission = new UserPermission();
            Boolean result = userPermission.Permission(MainActivity.this, "Accounts", AccountsListActivity.class);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
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

        } else if (id == R.id.nav_return) {
            userPermission = new UserPermission();
            Boolean result = userPermission.Permission(MainActivity.this, "Returns", ReturnOptionActivity.class);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
//            Intent in = new Intent(MainActivity.this,ReturnOptionActivity.class);
//            startActivity(in);

        } else if (id == R.id.nav_purchese) {
            userPermission = new UserPermission();
            Boolean result = userPermission.Permission(MainActivity.this, "Purchase", PurchaseListActivity.class);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_class) {
            userPermission = new UserPermission();
            Boolean result = userPermission.Permission(MainActivity.this, "Class/Category", ClassDestinationListActivity.class);
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
            Boolean result = userPermission.Permission(MainActivity.this, "Area/Destination", ClassDestinationListActivity.class);
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        arrayListcategory = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' Order By lower(item_group_name) asc", database, db);
        FragmentManager fragMan = getSupportFragmentManager();
        if (fragList.size() > 0) {
            for (int i = 0; i < fragList.size(); i++) {
                fragMan.beginTransaction().remove(fragList.get(i)).commit();
                fragList.clear();
                fragList = new ArrayList<android.support.v4.app.Fragment>();
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

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(Globals.TabPos);
        viewPager.setOffscreenPageLimit(0);
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
        public android.support.v4.app.Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
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
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

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

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogContactMainListAdapter = new DialogContactMainListAdapter(MainActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);

    }

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
                listDialog2.dismiss();
            }
        });
    }

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


//    private final Runnable m_Runnable = new Runnable() {
//        public void run()
//
//        {
////            if (settings.get_Is_Customer_Display.equals("1")) {
//            CMD_Display();
////            }
//
//            //Toast.makeText(MainActivity.this, "in runnable", Toast.LENGTH_SHORT).show();
//
//            MainActivity.this.mHandler1.postDelayed(m_Runnable, 1000);
////            MapsActivity.this.mHandler.postDelayed(m_Runnable, 5000);
//        }
//    };


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
        mScannerView.stopCameraPreview(); //stopPreview
        mScannerView.stopCamera();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("opr", "Add");
        intent.putExtra("order_code", "");
        startActivity(intent);
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

                String[] s = new String[1];

                // Type the path of the files in here
                s[0] = backupDB.getPath();

                // first parameter is d files second parameter is zip file name
                ZipManager zipManager = new ZipManager();

                // calling the zip function


                zipManager.zip(s, Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "Backup" + "/" + "DatabaseLitePOS.zip");

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
            String[] recipients = {"pegasusq8@gmail.com", "paliwalneeraj.pegasus@gmail.com"};
            final MainActivity.SendEmailAsyncTask email = new MainActivity.SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender("pegasusq8@gmail.com", "@Neeraj@99534388", "smtp.gmail.com", "465");
            email.m.set_from("pegasusq8@gmail.com");
            email.m.setBody("Phomello-LitePOSDB");
            email.m.set_to(recipients);
            email.m.set_subject("Phomello-LitePOSDB ( " + date + " )");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "Backup" + "/" + "DatabaseLitePOS.zip");
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
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Email failed");
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

        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + rawResult.getContents() + "'");
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

        builder.setTitle("Do you want to logout from server");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
           reg_code=lite_pos_registration.getRegistration_Code();
                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                String licensecustomerid= lite_pos_device.getLic_customer_license_id();
                postDeviceInfo(email, password, Globals.isuse_logout, Globals.master_product_id, licensecustomerid, Globals.Device_Code, serial_no, "4", android_id, myKey,reg_code);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String regcode) {

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging Out....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL+ "/index.php?route=api/database_detail/device_login";
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
                                    Lite_POS_Device  lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
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
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
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
}
