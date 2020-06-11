package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.hoin.btsdk.BluetoothService;
import com.hoin.btsdk.PrintPic;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.Util.Watermark;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Acc_Customer_Credit;
import org.phomellolitepos.database.Acc_Customer_Debit;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Z_Close;
import org.phomellolitepos.database.Z_Detail;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

import static a1088sdk.PrnDspA1088.ZQ_CONTEXTNULL;
import static a1088sdk.PrnDspA1088.ZQ_ERRPARAM;
import static a1088sdk.PrnDspA1088.ZQ_NOCOMPORT;
import static a1088sdk.PrnDspA1088.ZQ_NODSPPORT;
import static a1088sdk.PrnDspA1088.ZQ_NOPERMISSION;
import static a1088sdk.PrnDspA1088.ZQ_NOPRNPORT;
import static a1088sdk.PrnDspA1088.ZQ_PORTISCLOSE;
import static a1088sdk.PrnDspA1088.ZQ_READERROR;
import static a1088sdk.PrnDspA1088.ZQ_SENDERROR;
import static a1088sdk.PrnDspA1088.ZQ_USBERROR;
import static a1088sdk.PrnDspA1088.ZQ_WRONGDATA;
import static a1088sdk.PrnDspA1088.ZQ_WRONGFILE;

public class X_ZActivity extends AppCompatActivity {
    EditText edt_ob, edt_expenses, edt_cash, edt_total, edt_cash_total, edt_cr_amt, edt_acccash, edt_salescash, edt_dr_amt, edt_totl_return, edt_ret_cash, edt_return_amount, edt_retntotal,edt_posbal_returncash;
    TextView total_sale;
    LinearLayout ll_return,ll_table_llview,ll_account,ll_returncasview, ll_returncash;

    Button btn_post, btn_exp_print, btn_xz_print,btn_returnpost;
    Database db;
    SQLiteDatabase database;
    Pos_Balance pos_balance;
    ArrayList<Pos_Balance> arraylist_PB = new ArrayList<Pos_Balance>();
    ArrayList<Orders> arraylist_orders = new ArrayList<Orders>();
    ArrayList<Orders> arrayList = new ArrayList<Orders>();
    ArrayList<Acc_Customer_Credit> arrayListAccCusCr = new ArrayList<Acc_Customer_Credit>();
    ArrayList<Acc_Customer_Debit> arrayListAccCusDr = new ArrayList<Acc_Customer_Debit>();
    ArrayList<String> strOrderCode = new ArrayList<String>();
    Order_Payment order_payment;
    Orders orders;
    Z_Close z_close;
    Z_Detail z_detail;
    ProgressDialog pDialog;
    Returns returns;
    ProgressDialog progressDialog;
    String decimal_check, strSelectedCategory = "", date, succ = "0";
    private Settings settings;
    private String PrinterType = "";
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    TableLayout linearLayout;
    //printer variables
    private IWoyouService woyouService;
    private boolean iswifi = false;
    private int order, noofPrint = 0, lang = 0, pos = 0;
    BluetoothService mService = null;
    private Random random = new Random();
    private ICallback callback = null;
    private TextView info;
    private ArrayList<String> mylist = new ArrayList<String>();
    private ListView lv;
    private int flag = 1;
    private ProgressDialog dialog;
    private static final String TAG = "PrinterTestDemo";
    Double amt = 0.0;
    String totalAmount;
    ArrayList<String> list1a, list2a, list3a, list4a;
    Lite_POS_Registration lite_pos_registration;
    String part2;
    String z_no_id = null;
    Orders orders1;
    User user;
    private String is_directPrint = "";
    private MyAdapter adp;
    ArrayList<String> arrPaymentName;
    ArrayList<String> arrPaymentAmount;
    String strCreditAmt, strDebitAmt, strReturnTotal;
    String strZ_Code = "";
    private TimerCountTools timeTools;
    JSONObject printJson = new JSONObject();
    private PrinterListener printer_callback = new PrinterListener();
    public static PrinterBinder printer;
    String strTotal, strname;
   String ck_project_type;
    private HandlerUtils.MyHandler handlerPPT8555;
    String serial_no, android_id, myKey, device_id, imei_no;
    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
    /*打印机当前状态*/

    /*定义状态广播*/
    private final String PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservice.NORMAL_ACTION";
    private final String PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";

    /*定义消息*/
    private final int MSG_TEST = 1;
    private final int MSG_IS_NORMAL = 2;
    private final int MSG_IS_BUSY = 3;
    private final int MSG_PAPER_LESS = 4;
    private final int MSG_PAPER_EXISTS = 5;
    private final int MSG_THP_HIGH_TEMP = 6;
    private final int MSG_THP_TEMP_NORMAL = 7;
    private final int MSG_MOTOR_HIGH_TEMP = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE = 10;
    private int printerStatus = 0;

    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;

    private IPosPrinterService mIPosPrinterService;
    private IPosPrinterCallback callbackPPT8555 = null;
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

    private long printCount = 0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                long mm = MemInfo.getmem_UNUSED(X_ZActivity.this);
                if (mm < 100) {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 800);
                }
            }
        }
    };

    /**
     * 消息处理
     */
    private HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent() {
        @Override
        public void handlerIntent(Message msg) {
            switch (msg.what) {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:
                    if (getPrinterStatus() == PRINTER_NORMAL) {
//                        loopPrint(loopPrintFlag);
                    }
                    break;
                case MSG_IS_BUSY:
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.printer_is_working, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.out_of_paper, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.exists_paper, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.printer_high_temp_alarm, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.motor_high_temp_alarm, Toast.LENGTH_SHORT).show();
                    handlerPPT8555.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    printerInit();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
//                    Toast.makeText(IPosPrinterTestDemo.this, R.string.printer_current_task_print_complete, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.d(TAG, "IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG, "IPosPrinterStatusListener action = " + action);
            if (action.equals(PRINTER_NORMAL_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0);
            } else if (action.equals(PRINTER_PAPERLESS_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0);
            } else if (action.equals(PRINTER_BUSY_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_IS_BUSY, 0);
            } else if (action.equals(PRINTER_PAPEREXISTS_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0);
            } else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0);
            } else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION)) {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0);
            } else {
                handlerPPT8555.sendEmptyMessageDelayed(MSG_TEST, 0);
            }
        }
    };

    /**
     * 绑定服务实例
     */
    private ServiceConnection connectService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIPosPrinterService = IPosPrinterService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIPosPrinterService = null;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x__z);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.X_Z);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        arrPaymentName = new ArrayList<>();
        arrPaymentAmount = new ArrayList<>();

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
                pDialog = new ProgressDialog(X_ZActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
       ck_project_type = lite_pos_registration.getproject_id();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        edt_ob = (EditText) findViewById(R.id.edt_ob);
        edt_expenses = (EditText) findViewById(R.id.edt_expenses);
        edt_cash = (EditText) findViewById(R.id.edt_cash);
        edt_total = (EditText) findViewById(R.id.edt_total);
        edt_cash_total = (EditText) findViewById(R.id.edt_cash_total);
        edt_cr_amt = (EditText) findViewById(R.id.edt_cr_amt);

        edt_ret_cash = (EditText) findViewById(R.id.edt_returncash);
        edt_return_amount = (EditText) findViewById(R.id.edt_ret_cr_amt);
        edt_retntotal = (EditText) findViewById(R.id.edt_totl_return1);
        edt_posbal_returncash = (EditText) findViewById(R.id.edt_return_cash_amnt);
        edt_dr_amt = (EditText) findViewById(R.id.edt_dr_amt);
        edt_totl_return = (EditText) findViewById(R.id.edt_totl_return);
        total_sale = (TextView) findViewById(R.id.total_sale);
        btn_post = (Button) findViewById(R.id.btn_post);
        btn_exp_print = (Button) findViewById(R.id.btn_exp_print);
        btn_xz_print = (Button) findViewById(R.id.btn_xz_print);
        btn_returnpost = (Button) findViewById(R.id.btn_returnpost);
        linearLayout = (TableLayout) findViewById(R.id.ll);
        ll_return = (LinearLayout) findViewById(R.id.return_layout);
        ll_table_llview = (LinearLayout) findViewById(R.id.table_layoutview);
        ll_account = (LinearLayout) findViewById(R.id.ll_account);
        ll_returncasview = (LinearLayout) findViewById(R.id.view_returncash);
        ll_returncash = (LinearLayout) findViewById(R.id.ll_returncash);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
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
        imei_no = telephonyManager.getImei();

        if (ck_project_type.equals("standalone")) {
            btn_returnpost.setVisibility(View.GONE);
            ll_return.setVisibility(View.GONE);
            ll_table_llview.setVisibility(View.GONE);
            ll_account.setVisibility(View.GONE);
            ll_returncasview.setVisibility(View.GONE);
            ll_returncash.setVisibility(View.GONE);
        }

        settings = Settings.getSettings(getApplicationContext(), database, "");
        if (settings == null) {
            PrinterType = "";
        } else {
            try {
                PrinterType = settings.getPrinterId();
                is_directPrint = settings.get_Is_Print_Dialog_Show();
            } catch (Exception ex) {
                PrinterType = "";
                is_directPrint ="";
            }
        }

        if (PrinterType.equals("7")) {
            try {
                ServiceManager.getInstence().init(X_ZActivity.this);
            } catch (Exception ex) {
            }
        }

        mService = MainActivity.mService;
        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        callback = new ICallback.Stub() {

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
                        info.append("onRaiseException = " + msg + "\n");
                    }
                });
            }
        };

        handlerPPT8555 = new HandlerUtils.MyHandler(iHandlerIntent);
        callbackPPT8555 = new IPosPrinterCallback.Stub() {

            @Override
            public void onRunResult(final boolean isSuccess) throws RemoteException {
                Log.i(TAG, "result:" + isSuccess + "\n");
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "result:" + value + "\n");
            }
        };

        //绑定服务
        Intent intent = new Intent();
        intent.setPackage("com.iposprinter.iposprinterservice");
        intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
//        startService(intent);
        bindService(intent, connectService, Context.BIND_AUTO_CREATE);

        //注册打印机状态接收器
        IntentFilter printerStatusFilter = new IntentFilter();
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
        registerReceiver(IPosPrinterStatusListener, printerStatusFilter);

        try {
            intent = new Intent();
            intent.setPackage("woyou.aidlservice.jiuiv5");
            intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
            startService(intent);
            bindService(intent, connService, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
        }


        final LongOperation tsk = new LongOperation();
        tsk.execute();
        // setting timeout thread for async task
        Thread thread1 = new Thread() {
            public void run() {
                try {
                    tsk.get(6000, TimeUnit.MILLISECONDS); // set time in
                } catch (Exception e) {
                    tsk.cancel(true);
                    iswifi = false;
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        };
        thread1.start();

        fill_x_z_detail();

        btn_xz_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orders orders = Orders.getOrders(getApplicationContext(), database, " Where order_status = 'OPEN' And z_code='0'");
                if (orders == null) {
                    orders = Orders.getOrders(getApplicationContext(), database, " Where  z_code='0'");
                    if (orders == null) {
                        Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                    } else {

                        if (PrinterType.equals("") || PrinterType.equals("0")) {
                            Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                if (is_directPrint.equals("true")) {


                                    print_x();
                                }
                                else{

                                    android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(X_ZActivity.this);

                                    alertDialog.setTitle(R.string.xtoz);
                                    alertDialog.setMessage(R.string.xz_alertmsg);


                                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            try {
                                                print_x();
                                            } catch (Exception e) {

                                            }
                                        }
                                    });


                                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.cancel();
                                            Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                    // Showing Alert Message
                                    alertDialog.show();
                                }

                            }catch (Exception e){
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.Orders_not_closed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " Where order_status = 'OPEN' And z_code='0'");


                    if (orders == null) {
                        //performPDFExport();
                        orders1 = Orders.getOrders(getApplicationContext(), database, " Where z_code='0'");

                        returns = Returns.getReturns(getApplicationContext(), " Where z_code='0' and is_post='false' and is_cancel='false' and is_active='1'", database);
                        if (returns != null) {
                      /*  AlertDialog.Builder builder = new AlertDialog.Builder(X_ZActivity.this);

                        builder.setTitle(getString(R.string.return_post_Alert));
                        builder.setMessage(getString(R.string.return_post_msg));

                        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                progressDialog = new ProgressDialog(X_ZActivity.this);
                                progressDialog.setTitle("");
                                progressDialog.setMessage(getString(R.string.Wait_msg));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                      //  String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'", liccustomerid, serial_no, android_id, myKey);
                                        String result = Returns.sendOnServer(getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'", liccustomerid, serial_no, android_id, myKey);

                                        progressDialog.dismiss();

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
                               // dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // Do nothing
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();*/
                            Toast.makeText(getApplicationContext(), R.string.returnposterror, Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (orders1 == null) {
                            Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                        } else {
                            database.beginTransaction();
                            String modified_by = Globals.user;
                            z_close = Z_Close.getZ_Close(getApplicationContext(), "  order By z_no Desc LIMIT 1", database, db);
                            Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                            if (last_code == null) {
                                if (z_close == null) {
                                    strZ_Code = "Z-" + 1;
                                } else {
                                    strZ_Code = "Z-" + (Integer.parseInt(z_close.get_z_no()) + 1);
                                }
                            } else {
                                if (last_code.getlast_z_close_code().equals("0")) {
                                    if (z_close == null) {
                                        strZ_Code = "Z-" + 1;
                                    } else {
                                        strZ_Code = "Z-" + (Integer.parseInt(z_close.get_z_no()) + 1);
                                    }
                                } else {
                                    if (z_close == null) {
                                        String code = last_code.getlast_order_code();
                                        String[] strCode = code.split("-");
                                        part2 = strCode[1];
                                        z_no_id = (Integer.parseInt(part2) + 1) + "";
                                        strZ_Code = "Z-" + (Integer.parseInt(part2) + 1);
                                    } else {
                                        strZ_Code = "Z-" + (Integer.parseInt(z_close.get_z_no()) + 1);
                                    }
                                }
                            }


                            try {
                                String ob = edt_ob.getText().toString();
                                String ex = edt_expenses.getText().toString();
                                String cash = edt_cash.getText().toString();

                                String strTotalAmount = Double.parseDouble(edt_cash.getText().toString()) + Double.parseDouble(edt_ob.getText().toString()) + Double.parseDouble(strCreditAmt) + Double.parseDouble(strDebitAmt) - Double.parseDouble(edt_expenses.getText().toString()) + "";
                                z_close = new Z_Close(getApplicationContext(), null, strZ_Code, liccustomerid, date, strTotalAmount, "1", modified_by, date, "N");

                                long I = z_close.insertZ_Close(database);
                                if (I > 0) {
                                    succ = "1";
                                    z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, "1", "OB", edt_ob.getText().toString(), "1", modified_by, date, "N");
                                    long d = z_detail.insert_Z_Detail(database);
                                    if (d > 0) {
                                        succ = "1";
                                    } else {
                                    }

                                    z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, "2", "EXP", edt_expenses.getText().toString(), "1", modified_by, date, "N");
                                    long d1 = z_detail.insert_Z_Detail(database);
                                    if (d1 > 0) {
                                        succ = "1";
                                    } else {
                                    }

                                    z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, "3", "CR AMT", edt_cr_amt.getText().toString(), "1", modified_by, date, "N");
                                    d1 = z_detail.insert_Z_Detail(database);
                                    if (d1 > 0) {
                                        succ = "1";
                                    } else {
                                    }

                                    z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, "4", "DR AMT", edt_dr_amt.getText().toString(), "1", modified_by, date, "N");
                                    d1 = z_detail.insert_Z_Detail(database);
                                    if (d1 > 0) {
                                        succ = "1";
                                    } else {
                                    }

                                    z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, "5", "RETURN", edt_totl_return.getText().toString(), "1", modified_by, date, "N");
                                    d1 = z_detail.insert_Z_Detail(database);
                                    if (d1 > 0) {
                                        succ = "1";
                                    } else {
                                    }

                                    String strQry;
                                    Cursor cursor;
                                    strQry = "Select Sum(order_payment.pay_amount),order_payment.payment_id ,  payments.payment_name   as TotalSales from orders   \n" +
                                            "INNER JOIN  order_payment on order_payment.order_code = orders.order_code   \n" +
                                            "INNER JOIN  payments on payments.payment_id =   order_payment.payment_id   \n" +
                                            "where orders.Z_Code = '0'  \n" +
                                            "Group by order_payment.payment_id";
                                    cursor = database.rawQuery(strQry, null);
                                    int count = 6;
                                    while (cursor.moveToNext()) {
                                        z_detail = new Z_Detail(getApplicationContext(), null, strZ_Code, liccustomerid, count + "", cursor.getString(2), cursor.getString(0), "1", modified_by, date, "N");
                                        long d5 = z_detail.insert_Z_Detail(database);
                                        if (d5 > 0) {
                                            succ = "1";
                                        } else {
                                        }
                                        count++;
                                    }

                                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                    String ck_project_type = lite_pos_registration.getproject_id();

                                    if (ck_project_type.equals("standalone")) {
                                        try {
                                            Double strCredit = 0d;
                                            strQry = "select order_payment.pay_amount,orders.contact_code from orders left join order_payment on orders.order_code = order_payment.order_code where order_payment.payment_id='5' and orders.z_code='0'";
                                            cursor = database.rawQuery(strQry, null);
                                            while (cursor.moveToNext()) {
                                                Acc_Customer_Credit acc_customer_credit = new Acc_Customer_Credit(getApplicationContext(), null, date, cursor.getString(1), cursor.getString(0), "", "", "0", "1", Globals.user, date, cursor.getString(9));
                                                long a = acc_customer_credit.insertAcc_Customer_Credit(database);
                                            }
                                        } catch (Exception e) {
                                        }
                                    }

                                    arrayListAccCusCr = Acc_Customer_Credit.getAllAcc_Customer_Credit(getApplicationContext(), "WHERE z_no ='0'", database);
                                    if (arrayListAccCusCr.size() > 0) {
                                        for (int i = 0; i < arrayListAccCusCr.size(); i++) {

                                            Acc_Customer_Credit acc_customer_credit = Acc_Customer_Credit.getAcc_Customer_Credit(getApplicationContext(), "where z_no ='0'", database);
                                            acc_customer_credit.set_z_no(strZ_Code);
                                            long o = acc_customer_credit.updateAcc_Customer_Credit("id=?", new String[]{acc_customer_credit.get_id()}, database);
                                            if (o > 0) {
                                                succ = "1";
                                            } else {
                                            }
                                        }
                                    }

                                    arrayListAccCusDr = Acc_Customer_Debit.getAllAcc_Customer_Debit(getApplicationContext(), "WHERE z_no ='0'", database);
                                    if (arrayListAccCusDr.size() > 0) {
                                        for (int i = 0; i < arrayListAccCusDr.size(); i++) {

                                            Acc_Customer_Debit acc_customer_debit = Acc_Customer_Debit.getAcc_Customer_Debit(getApplicationContext(), "where z_no ='0'", database);
                                            acc_customer_debit.set_z_no(strZ_Code);
                                            long o = acc_customer_debit.updateAcc_Customer_Debit("id=?", new String[]{acc_customer_debit.get_id()}, database);
                                            if (o > 0) {
                                                succ = "1";
                                            } else {
                                            }
                                        }
                                    }


                                    arrayList = Orders.getAllOrders(getApplicationContext(), "WHERE z_code ='0'", database);
                                    if (arrayList.size() > 0) {
                                        for (int i = 0; i < arrayList.size(); i++) {

                                            String strCode = arrayList.get(i).get_order_code();

                                            orders = Orders.getOrders(getApplicationContext(), database, " WHERE order_code ='" + strCode + "'");
                                            orders.set_z_code(strZ_Code);
                                            orders.set_is_push("N");
                                            long o = orders.updateOrders("order_code=?", new String[]{strCode}, database);
                                            if (o > 0) {
                                                succ = "1";
                                            } else {
                                            }
                                        }
                                    }

                                    ArrayList<Pos_Balance> arrayList_Pb = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE z_code ='0'", database);
                                    if (arrayList_Pb.size() > 0) {
                                        for (int i = 0; i < arrayList_Pb.size(); i++) {
                                            String strCode = arrayList_Pb.get(i).get_pos_balance_code();

                                            pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, " WHERE pos_balance_code ='" + strCode + "'");
                                            pos_balance.set_z_code(strZ_Code);
                                            long o = pos_balance.updatePos_Balance("pos_balance_code=?", new String[]{strCode}, database);
                                            if (o > 0) {
                                                succ = "1";
                                            } else {
                                            }
                                        }
                                    }

                                    ArrayList<Returns> arrayList_TotalReturn = Returns.getAllReturns(getApplicationContext(), "WHERE is_post='true' and z_code ='0' and is_cancel='false'", database);
                                    if (arrayList_TotalReturn.size() > 0) {
                                        for (int i = 0; i < arrayList_TotalReturn.size(); i++) {
                                            String strCode = arrayList_TotalReturn.get(i).get_voucher_no();
                                            Returns returns = Returns.getReturns(getApplicationContext(), " WHERE voucher_no ='" + strCode + "'", database);
                                            returns.set_z_code(strZ_Code);
                                            long o = returns.updateReturns("voucher_no=?", new String[]{strCode}, database);
                                            if (o > 0) {
                                                succ = "1";
                                            } else {
                                            }
                                        }
                                    }

                                } else {
                                }

                                if (succ.equals("1")) {

                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    try {
                                        if (settings.get_Is_email().equals("true")) {
                                            if (settings.get_Manager_Email().equals("")) {

                                            } else {
                                                String exp = "POST";
                                                if (settings.get_Is_ZDetail_InPrint().equals("true")) {
                                                    performPDFExportZDetail();
                                                } else {
                                                    performPDFExport();
                                                }

                                                String strEmail = settings.get_Manager_Email();
                                                if (isNetworkStatusAvialable(getApplicationContext())) {
                                                    send_email(strEmail, exp);
                                                }

                                            }
                                        } else {
                                            if (settings.get_Is_ZDetail_InPrint().equals("true")) {
                                                performPDFExportZDetail();
                                            } else {
                                                performPDFExport();
                                            }
                                        }
                                    } catch (Exception ex1) {
                                    }

                                    if (ck_project_type.equals("standalone")) {

                                    } else {
                                        if (isNetworkStatusAvialable(getApplicationContext())) {
                                            pDialog = new ProgressDialog(X_ZActivity.this);
                                            pDialog.setCancelable(false);
                                            pDialog.setMessage(getString(R.string.Posting_on_server));
                                            pDialog.show();
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    // send items on server


                                                    String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'", liccustomerid, serial_no, android_id, myKey);

                                                    if (result_order.equals("2")) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {

                                                                if (Globals.responsemessage.equals("Device Not Found")) {

                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                                    lite_pos_device.setStatus("Out");
                                                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                                    if (ct > 0) {

                                                                        Intent intent_category = new Intent(X_ZActivity.this, LoginActivity.class);
                                                                        startActivity(intent_category);
                                                                        finish();
                                                                    }


                                                                }

                                                            }
                                                        });

                                                    }
                                                    String result = send_online(pDialog);
                                                    if (result.equals("1")) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                    String result1 = Returns.sendOnServer(getApplicationContext(), database, db, "Select * FROM  returns WHERE is_post = 'true' and is_push  ='N' and  z_code !=  '0' and is_cancel ='false'", liccustomerid, serial_no, android_id, myKey);

                                                    if (result1.equals("1")) {

                                                        String Query = "Update returns Set is_push = 'Y' where  is_push = 'N'";
                                                        long check = db.executeDML(Query, database);
                                                        if (check > 0) {

                                                        }

                                                    }


                                                    // is_post = true   and is_push  ='N'   and  z_code !=  '0'  and is_cancel ='0'
                                                    // list you will get return then need to send pon  server and
                                                    // invidual order retrun  and eash return response then u need to set inr order
                                                    //Update    Returns  Set  is_push = 'Y'  where  is_push = 'N'


                                                }
                                            }.start();
                                        }
                                    }

//                                String strExpenses = Globals.myNumberFormat2Price(0, decimal_check);
//                                edt_expenses.setText(strExpenses);
//
//                                String opening_amount = Globals.myNumberFormat2Price(0, decimal_check);
//                                edt_ob.setText(opening_amount);
//
//                                String strCash = Globals.myNumberFormat2Price(0, decimal_check);
//                                edt_cash.setText(strCash);
//
//                                String strTotal = Globals.myNumberFormat2Price(0, decimal_check);
//                                edt_total.setText(strTotal);
//
//                                String strCashTotal = Globals.myNumberFormat2Price(0, decimal_check);
//                                edt_cash_total.setText(strCashTotal);

                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (PrinterType.equals("") || PrinterType.equals("0")) {
                                                    Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    try {
                                                        if (is_directPrint.equals("true")) {


                                                            print_x_z();
                                                            Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {

                                                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(X_ZActivity.this);

                                                            alertDialog.setTitle(R.string.post_alert);
                                                            alertDialog.setMessage(R.string.post_alertmsg);


                                                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    try {
                                                                        print_x_z();
                                                                        Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    } catch (Exception e) {

                                                                    }
                                                                }
                                                            });


                                                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    dialog.cancel();
                                                                    Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            });

                                                            // Showing Alert Message
                                                            alertDialog.show();
                                                        }

                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }
                                        });
                                    } catch (Exception e) {

                                    }

                                } else {
                                    database.endTransaction();
                                    Toast.makeText(getApplicationContext(), R.string.Post_Error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.Orders_not_closed, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_returnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    progressDialog = new ProgressDialog(X_ZActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.Wait_msg));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            //  String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'", liccustomerid, serial_no, android_id, myKey);
                            String result = Returns.sendOnServer(getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'", liccustomerid, serial_no, android_id, myKey);

                            progressDialog.dismiss();
                            if (result.equals("1")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String rsultPost = stock_post();
                                        finish();
                                        Intent i = new Intent(getApplicationContext(), X_ZActivity.class);
                                        startActivity(i);
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

            else

            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

            }
        }
        });
        btn_exp_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
                if (arraylist_PB.size() > 0) {
                   /* pDialog = new ProgressDialog(X_ZActivity.this);
                    pDialog.show();*/
                    if (PrinterType.equals("") || PrinterType.equals("0")) {
                        Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            if (is_directPrint.equals("true")) {


                                print_expenses();
                            }
                            else{

                                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(X_ZActivity.this);

                                alertDialog.setTitle(R.string.exp_alert);
                                alertDialog.setMessage(R.string.exp_alertmsg);


                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            print_expenses();
                                        } catch (Exception e) {

                                        }
                                    }
                                });


                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                        Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                // Showing Alert Message
                                alertDialog.show();
                            }

                        }catch (Exception e){
                        }
                    }



                   // pDialog.dismiss();
                    if (settings.get_Is_email().equals("true")) {
                        if (settings.get_Manager_Email().equals("")) {

                        } else {

                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                String exp = "EXP";
                                performPDFExport_Export();
                                String strEmail = settings.get_Manager_Email();
                                send_email(strEmail, exp);
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.No_Expenses_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void print_x() {

        mylist = getlistX();
        if (mylist != null) {
            adp = new MyAdapter(getApplicationContext(), mylist);
        }
        if (iswifi) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        performOperationEn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
            startActivity(intent);
            finish();
        } else if (PrinterType.equals("1")) {
            try {
                if (woyouService == null) {
                } else {
                    mobile_pos_x();
                }
            } catch (Exception ex) {
            }

        } else if (PrinterType.equals("3")) {

            String result = bluetooth_X_56();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }


        } else if (PrinterType.equals("4")) {

            String result = bluetooth_X_80();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }


        } else if (PrinterType.equals("5")) {
            String result = bluetooth_X_100();
            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (PrinterType.equals("6")) {
            try {
                if (woyouService == null) {
                } else {
                    PHA_POS_X();
                }
            } catch (Exception ex) {
            }
        } else if (PrinterType.equals("7")) {
            ppt_8527_X();
        } else if (PrinterType.equals("8")) {
            ppt_8555_X();
        } else {
            Toast.makeText(X_ZActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
        }


    }

    private void ppt_8555_X() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {

                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                        }
                    } catch (Exception ex) {
                    }
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("X Report\n", "ST", 24, 1, callbackPPT8555);

                    //mIPosPrinterService.printSpecifiedTypeText("X Report\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("Date     : " + date + "\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    mIPosPrinterService.printSpecifiedTypeText("Cashier  :" + user.get_name() + "\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                    String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);
                    mIPosPrinterService.printSpecifiedTypeText("Opening Balance : " + ob, "ST", 24, callbackPPT8555);
                    String expenses;

                    expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                    mIPosPrinterService.printSpecifiedTypeText("Expenses(-)  : " + expenses, "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintFontSize(30, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintFontSize(30, callbackPPT8555);
                    String cash;
                    cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                    mIPosPrinterService.printSpecifiedTypeText("Cash(+)  : " + cash, "ST", 24, callbackPPT8555);


                    // Here you need to Show Account Cash with project type Condition
                    //------------------------------------------
                    // Accounts (Cash)(+)   : 234.560
                    //------------------------------------------
                    if(Globals.objLPR.getproject_id().equals("cloud")){
                        //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("Accounts Cash(+)  :" + edt_cr_amt.getText().toString(), "ST", 24, callbackPPT8555);

                        mIPosPrinterService.printSpecifiedTypeText("Return (Cash)(-)  :" + edt_posbal_returncash.getText().toString(), "ST", 24, callbackPPT8555);

                    }
                    else{

                    }

                    // Here you need to Show Account Cash with project type Condition
                    //------------------------------------------
                    // Return (Cash)(-)   : 234.560
                    //------------------------------------------


                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("NetCash  : " + edt_total.getText().toString().trim().toString(), "ST", 32, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
//                    mIPosPrinterService.PrintSpecFormatText("Accounts\n", "ST", 32, 1, callbackPPT8555);
//
//                    mIPosPrinterService.printSpecifiedTypeText("Cash(+) : " + edt_cr_amt.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);
                    // Not Center Inside valus   Show Like pos balace
                    //------------
                    //              Sales Detail  -- Center Align
                    //Cash          :256.365
                    //Bank Card     :1258.255
                    //------------


                    mIPosPrinterService.PrintSpecFormatText("Sales Detail\n", "ST", 32, 1, callbackPPT8555);
                    String strTotal;
                    int iPayCount = 1;
                    while (iPayCount < arrPaymentAmount.size()) {
                        strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText(arrPaymentName.get(iPayCount) + " : " + strTotal, "ST", 24, callbackPPT8555);
                        iPayCount++;
                    }
                    //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    String strTotalSales;

                  //  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                    mIPosPrinterService.printSpecifiedTypeText("Total Sales    : " + strTotalSales , "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);


                    // Here check Project ype condition if cloud will print else not
                    // Here conect showing proper but need to alling values proper

                    mIPosPrinterService.PrintSpecFormatText("Sales Return\n", "ST", 32, 1, callbackPPT8555);
                    String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                    mIPosPrinterService.printSpecifiedTypeText("Cash(+)  : " + retruncash, "ST", 24, callbackPPT8555);

                    String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                    mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+)  : " + retruncredit, "ST", 24, callbackPPT8555);

                    String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                    mIPosPrinterService.printSpecifiedTypeText("Total Return(+)   : " + retruntotalsale , "ST", 24, callbackPPT8555);
//                    mIPosPrinterService.printSpecifiedTypeText("Total Return    : " + edt_totl_return.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void ppt_8555_XEXP() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                        }
                    } catch (Exception ex) {
                    }
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                    mIPosPrinterService.PrintSpecFormatText("Expense Report\n", "ST", 24, 1, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                    //mIPosPrinterService.printSpecifiedTypeText("Expense Report\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("Date     : " + date + "\n", "ST", 24, callbackPPT8555);
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    mIPosPrinterService.printSpecifiedTypeText("Cashier  :" + user.get_name() + "\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("Expenses      Amount ", "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printSpecifiedTypeText("................................", "ST", 24, callbackPPT8555);


                    String expenses = "";


                    int count = 0;
                    arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
                    while (count < arraylist_PB.size()) {

                        String strRemarks = arraylist_PB.get(count).get_remarks();
                        int len = 5;
                        if (strRemarks.length() > len) {
                            strRemarks = strRemarks.substring(0, len);
                        } else {
                            for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                                strRemarks = strRemarks + " ";
                            }
                        }

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText(" " + strRemarks + "       " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                        ;

                      /*  amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                        totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);*/


                        count++;
                    }
                    String strTotalSales;

                    mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                    strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                    mIPosPrinterService.printSpecifiedTypeText("Total Expenses    : " + expenses + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "ST", 24, callbackPPT8555);

                  /*  String strTotal;
                    int iPayCount = 1;
                    while (iPayCount < arrPaymentAmount.size()) {
                        strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText(arrPaymentName.get(iPayCount) + " : " + strTotal, "ST", 24, callbackPPT8555);
                        iPayCount++;
                    }
*/
//                    mIPosPrinterService.printSpecifiedTypeText("Total Return    : " + edt_totl_return.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String bluetooth_X_100() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {
            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage(" " + Globals.objLPR.getCompany_Name().toUpperCase() + "\n", "GBK");
                        mService.sendMessage("X Report\n", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("POS Name :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date     :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier  :" + user.get_name(), "GBK");
                        mService.sendMessage("............................................................", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance : " + ob, "GBK");

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)         : " + cash, "GBK");

                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)     : " + expenses, "GBK");

                        String cr_amt;

                        cr_amt = Globals.myNumberFormat2Price(Double.parseDouble(edt_cr_amt.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+): " + cr_amt, "GBK");

                        mService.sendMessage("............................................................", "GBK");
                        mService.sendMessage("Net Cash        : " + edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("............................................................", "GBK");
                        String strTotalSales;

                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        mService.sendMessage("Total Sales     : " + strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "GBK");

                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }
                        String totl_return;
                        totl_return = Globals.myNumberFormat2Price(Double.parseDouble(edt_totl_return.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return    :" + totl_return, "GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;

    }

    private String bluetooth_X_80() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {
            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage(" " + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                            } else {
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                            }
                        } catch (Exception ex) {
                        }
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        mService.sendMessage("X Report", "GBK");
                        mService.sendMessage("-----------------------------------------------", "GBK");

                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("POS Name :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date     :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier  :" + user.get_name(), "GBK");
                        mService.sendMessage("...............................................", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance       : " + ob, "GBK");
                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)           : " + expenses, "GBK");

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)               : " + cash, "GBK");
                        if(Globals.objLPR.getproject_id().equals("cloud")){
                            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                            mService.sendMessage("Accounts Cash(+)     :" + edt_cr_amt.getText().toString(), "GBK");

                            mService.sendMessage("Return (Cash)(-)      :" + edt_posbal_returncash.getText().toString(), "GBK");

                        }
                        else{

                        }

                       /* String cr_amt;

                        cr_amt = Globals.myNumberFormat2Price(Double.parseDouble(edt_cr_amt.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+): " + cr_amt, "GBK");*/

                        mService.sendMessage("...............................................", "GBK");
                        mService.sendMessage("Net Cash  : " + edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("...............................................", "GBK");
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Detail", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }
                        //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        String strTotalSales;

                        //  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Sales     : " + strTotalSales , "GBK");


                        // Here check Project ype condition if cloud will print else not
                        // Here conect showing proper but need to alling values proper
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Return", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Cash(+)          : " + retruncash, "GBK");

                        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+)     : " + retruncredit, "GBK");

                        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return(+)    : " + retruntotalsale , "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;

    }

    private void ppt_8527_X() {
        try {
            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                JSONArray printTest = new JSONArray();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                printTest.put(getPrintObject("Date     : " + date, "2", "left"));
                printTest.put(getPrintObject("POS NAME: " + Globals.objLPD.getDevice_Name() + "\n", "2", "left"));
                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                printTest.put(getPrintObject("Cashier  :" + user.get_name(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));
                printTest.put(getPrintObject("X Report", "3", "center"));
                printTest.put(getPrintObject("POS Name :" + Globals.objLPD.getDevice_Name(), "2", "left"));

                String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Opening Balance" + ":" + ob, "2", "left"));
                String expenses;

                expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Expenses(-)" + ":" + expenses, "2", "left"));
                String cash;
                cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Cash(+)" + ":" + cash, "2", "left"));


                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Net Cash" + ":" + edt_total.getText().toString().trim().toString(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Accounts", "3", "center"));
                printTest.put(getPrintObject("Cash(+)" + ":" + edt_cr_amt.getText().toString().trim().toString(), "2", "left"));

                printTest.put(getPrintObject("Sales Detail", "3", "center"));
                String strTotalSales;

                strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                printTest.put(getPrintObject("Total Sales" + ":" + strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "2", "left"));
                printTest.put(getPrintObject("Sales Return", "3", "center"));
                String cashreturn = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Cash(+)" + ":" + cashreturn, "2", "left"));
                String creditreturn = Globals.myNumberFormat2Price(Double.parseDouble(edt_return_amount.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Credit Amount(-)" + ":" + creditreturn, "2", "left"));
                String returntotal = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Toatal Return" + ":" + returntotal, "2", "left"));
                String strTotal;
                int iPayCount = 1;
                while (iPayCount < arrPaymentAmount.size()) {
                    strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                    printTest.put(getPrintObject(arrPaymentName.get(iPayCount) + ":" + strTotal, "2", "left"));
                    iPayCount++;
                }
                printTest.put(getPrintObject("Total Return" + ":" + edt_totl_return.getText().toString().trim().toString(), "2", "left"));
                printJson.put("spos", printTest);
                // 设置底部空3行
                printJson.put("spos", printTest);
                ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PHA_POS_X() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 30, callback);
                        woyouService.printTextWithFont("X Report\n", "", 30, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Date     : " + date + "\n", "", 30, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier  :" + user.get_name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Opening Balance :" + ob + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Opening Balance", ":", ob}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Cash            :" + cash + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Cash(+)", ":", cash}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);


                        String expenses;

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Expenses(-)", ":", expenses}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Credit Amount(+)", ":", edt_cr_amt.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Net Cash", ":", edt_total.getText().toString().trim().toString()}, new int[]{10, 1, 15}, new int[]{0, 0, 0}, callback);

                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("Sales Detail\n", "", 30, callback);

                        String strTotalSales;

                        woyouService.setAlignment(0, callback);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        //woyouService.printTextWithFont("Total Sales     : " +strTotalSales+ "\n", "", 24, callback);

                        woyouService.printColumnsText(new String[]{"Total Sales", ":", strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")"}, new int[]{6, 1, 15}, new int[]{0, 0, 0}, callback);


                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            // woyouService.printTextWithFont(arrPaymentName.get(iPayCount) + " : " +strTotal+ "\n", "", 24, callback);

                            woyouService.printColumnsText(new String[]{arrPaymentName.get(iPayCount), ":", strTotal}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                            iPayCount++;
                        }
                        woyouService.printColumnsText(new String[]{"Total Return", ":", edt_totl_return.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.cutPaper(callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String bluetooth_X_56() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {
            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage(" " + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                            } else {
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                            }
                        } catch (Exception ex) {
                        }
                        mService.sendMessage("--------------------------------", "GBK");
                        mService.sendMessage("X Report", "GBK");
                        mService.sendMessage("--------------------------------", "GBK");

                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("POS Name :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date     :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier  :" + user.get_name(), "GBK");
                        mService.sendMessage("................................", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance : " + ob, "GBK");
                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)   : " + expenses, "GBK");

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)  : " + cash, "GBK");
                        if(Globals.objLPR.getproject_id().equals("cloud")){
                            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                            mService.sendMessage("Accounts Cash(+)  :" + edt_cr_amt.getText().toString(), "GBK");

                            mService.sendMessage("Return (Cash)(-)  :" + edt_posbal_returncash.getText().toString(), "GBK");

                        }
                        else{

                        }

                       /* String cr_amt;

                        cr_amt = Globals.myNumberFormat2Price(Double.parseDouble(edt_cr_amt.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+): " + cr_amt, "GBK");*/

                        mService.sendMessage("................................", "GBK");
                        mService.sendMessage("Net Cash  : " + edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("................................", "GBK");
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Detail", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }
                        //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        String strTotalSales;

                        //  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Sales   : " + strTotalSales , "GBK");


                        // Here check Project ype condition if cloud will print else not
                        // Here conect showing proper but need to alling values proper
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Return", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Cash(+)  : " + retruncash, "GBK");

                        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+)  : " + retruncredit, "GBK");

                        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return(+)   : " + retruntotalsale , "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;
    }

    private void mobile_pos_x() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 30, callback);
                        woyouService.printTextWithFont("X Report\n", "", 30, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Date     : " + date + "\n", "", 30, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier :" + user.get_name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Opening Balance :" + ob + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Opening Balance", ":", ob}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Cash(+)", ":", cash}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);


                        String expenses;

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Expenses(-)", ":", expenses}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"CreditAmount(+)", ":", edt_cr_amt.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"NetCash", ":", edt_total.getText().toString().trim().toString()}, new int[]{7, 1, 15}, new int[]{0, 0, 0}, callback);

                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("Sales Detail\n", "", 30, callback);

                        String strTotalSales;

                        woyouService.setAlignment(0, callback);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        //woyouService.printTextWithFont("Total Sales     : " +strTotalSales+ "\n", "", 24, callback);

                        woyouService.printColumnsText(new String[]{"Total Sales", ":", strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")"}, new int[]{6, 1, 15}, new int[]{0, 0, 0}, callback);


                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            woyouService.printColumnsText(new String[]{arrPaymentName.get(iPayCount), ":", strTotal}, new int[]{16, 1, 10}, new int[]{0, 0, 0}, callback);
                            iPayCount++;
                        }
                        woyouService.printColumnsText(new String[]{"Total Return", ":", edt_totl_return.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ArrayList<String> getlistX() {
        ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("                " + Globals.objLPR.getCompany_Name() + "\n");
        mylist.add("                " + Globals.objLPR.getAddress() + "\n");
        mylist.add("                " + Globals.objLPR.getMobile_No() + "\n");
        try {
            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

            } else {
                mylist.add("                  " + Globals.objLPR.getService_code_tariff() + "\n");

            }
        } catch (Exception ex) {
        }
        mylist.add("-----------------------------------------------\n");
        mylist.add("                "+"X Report\n");
        mylist.add("-----------------------------------------------\n");
        mylist.add("Date            : " + date + "\n");
        mylist.add("POS Name        : " + Globals.objLPD.getDevice_Name() + "\n");
        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        mylist.add("Cashier         : " + user.get_name() + "\n");
        mylist.add("-----------------------------------------------\n");

        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);
        mylist.add("Opening Balance          : " + ob+"\n");
        String expenses;

        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

        mylist.add("Expenses(-)              : " + expenses+"\n");

        String cash;
        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

        mylist.add("Cash(+)                  : " + cash+"\n");


        // Here you need to Show Account Cash with project type Condition
        //------------------------------------------
        // Accounts (Cash)(+)   : 234.560
        //------------------------------------------
        if(Globals.objLPR.getproject_id().equals("cloud")){
            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
       mylist.add("Accounts Cash(+)         : " + edt_cr_amt.getText().toString()+"\n");

      mylist.add("Return (Cash)(-)         : " + edt_posbal_returncash.getText().toString()+"\n");

        }
        else{

        }

        mylist.add("-----------------------------------------------\n");
        mylist.add("Net Cash                 : " + edt_total.getText().toString().trim().toString()+"\n");
        mylist.add("-----------------------------------------------\n");


        mylist.add("                "+"Sales Detail\n");
        String strTotal;
        int iPayCount = 1;
        while (iPayCount < arrPaymentAmount.size()) {
          Double lp= Double.parseDouble(arrPaymentAmount.get(iPayCount).toString());
            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount).toString()), decimal_check);
            String strPaymentName  =  arrPaymentName.get(iPayCount);
            if(strPaymentName.length()<=15)
            {
                int lenPaymentName = strPaymentName.length();
                while(lenPaymentName <15)
                {
                    strPaymentName += " ";
                    lenPaymentName++;
                }
            }
            else
            {
                strPaymentName = strPaymentName.substring(0,15);
            }
            mylist.add(strPaymentName + " : " + strTotal+"\n");
            iPayCount++;
        }
        //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
        String strTotalSales;

        //  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

        mylist.add("Total Sales     : " + strTotalSales+"\n");



        // Here check Project ype condition if cloud will print else not
        // Here conect showing proper but need to alling values proper

        mylist.add("                  "+"Sales Return\n");
        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
        mylist.add("Cash(+)                : " + retruncash+"\n");

        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
        mylist.add("Credit Amount(+)       : " + retruncredit+"\n");

        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
        mylist.add("Total Return(+)        : " + retruntotalsale+"\n");

        mylist.add("\n");
        mylist.add("\n");
        mylist.add("\n");
        return mylist;
    }


    private void print_x_z() {

        mylist = getlist();
        if (mylist != null) {
            adp = new MyAdapter(getApplicationContext(), mylist);
        }
        if (iswifi) {
            performOperationEn();
            Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
            startActivity(intent);
            finish();
        } else if (PrinterType.equals("1")) {
            try {
                if (woyouService == null) {
                } else {
                    mobile_pos();
                }
            } catch (Exception ex) {
            }

        } else if (PrinterType.equals("3")) {

            String result = bluetooth_56();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (PrinterType.equals("4")) {

            String result = bluetooth_80();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (PrinterType.equals("5")) {

            String result = bluetooth_100();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (PrinterType.equals("6")) {
            try {
                if (woyouService == null) {
                } else {
                    PHA_POS();
                }
            } catch (Exception ex) {
            }
        } else if (PrinterType.equals("7")) {
            ppt_8527();
        } else if (PrinterType.equals("8")) {
            ppt_8555();
        } else {
            Toast.makeText(X_ZActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
        }
    }

    private void ppt_8555() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                            } else {
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                            }
                        } catch (Exception ex) {
                        }
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("Z Report\n", "ST", 24, 1, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("ZNo      :" + strZ_Code + "\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("POS Name : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("Date     : " + date + "\n", "ST", 24, callbackPPT8555);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mIPosPrinterService.printSpecifiedTypeText("Cashier  : " + user.get_name() + "\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mIPosPrinterService.printSpecifiedTypeText("Opening Balance : " + ob, "ST", 24, callbackPPT8555);

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mIPosPrinterService.printSpecifiedTypeText("Cash(+)  : " + cash, "ST", 24, callbackPPT8555);
                        String expenses;

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                        mIPosPrinterService.printSpecifiedTypeText("Expenses(-)  : " + expenses, "ST", 24, callbackPPT8555);
                        if(Globals.objLPR.getproject_id().equals("cloud")){
                            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Accounts Cash(+)  :" + edt_cr_amt.getText().toString(), "ST", 24, callbackPPT8555);

                            mIPosPrinterService.printSpecifiedTypeText("Return Cash(-)  :" + edt_posbal_returncash.getText().toString(), "ST", 24, callbackPPT8555);

                        }
                        else{

                        }

                        /*mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+): " + edt_cr_amt.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);*/
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("Net Cash : " +edt_total.getText().toString().trim().toString(), "ST", 32, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("Sales Detail\n", "ST", 32, 1, callbackPPT8555);

                        String strTotalSales;

                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mIPosPrinterService.printSpecifiedTypeText("Total Sales  : " + strTotalSales , "ST", 24, callbackPPT8555);

                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mIPosPrinterService.printSpecifiedTypeText(arrPaymentName.get(iPayCount) + " : " + strTotal, "ST", 24, callbackPPT8555);
                            iPayCount++;
                        }

//                        mIPosPrinterService.printSpecifiedTypeText("Total Return    : "+edt_totl_return.getText().toString().trim().toString(),"ST",24,callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("Sales Return\n", "ST", 32, 1, callbackPPT8555);
                        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText("Cash(+)  : " + retruncash, "ST", 24, callbackPPT8555);

                        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+)  : " + retruncredit, "ST", 24, callbackPPT8555);

                        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                        mIPosPrinterService.printSpecifiedTypeText("Total Return(+)  : " + retruntotalsale, "ST", 24, callbackPPT8555);
//                    mIPosPrinterService.printSpecifiedTypeText("Total Return    : " + edt_totl_return.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                        mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String bluetooth_100() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {
            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase() + "\n", "GBK");
                        mService.sendMessage("Z Report\n", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("ZNo      :" + strZ_Code, "GBK");
                        mService.sendMessage("POS Name :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date     :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier  :" + user.get_name(), "GBK");
                        mService.sendMessage("............................................................", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance : " + ob, "GBK");

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)         : " + cash, "GBK");

                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)     : " + expenses, "GBK");

                        String cr_amt;

                        cr_amt = Globals.myNumberFormat2Price(Double.parseDouble(edt_cr_amt.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+): " + cr_amt, "GBK");

                        mService.sendMessage("............................................................", "GBK");
                        mService.sendMessage("Net Cash        : " + edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("............................................................", "GBK");
                        String strTotalSales;

                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        mService.sendMessage("Total Sales     : " + strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "GBK");


                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }

                        String totl_return;
                        totl_return = Globals.myNumberFormat2Price(Double.parseDouble(edt_totl_return.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return  : " + totl_return, "GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;
    }

    private String bluetooth_80() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {

            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage(" " + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress() , "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                            }
                        } catch (Exception ex) {
                        }
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        mService.sendMessage("Z Report\n", "GBK");
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("ZNo          :" + strZ_Code, "GBK");
                        mService.sendMessage("POS Name     :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date         :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier      :" + user.get_name(), "GBK");
                        mService.sendMessage("-----------------------------------------------", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance      : " + ob, "GBK");
                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)          : " + expenses, "GBK");
                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)              : " + cash, "GBK");

                        if(Globals.objLPR.getproject_id().equals("cloud")){
                            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                            mService.sendMessage("Accounts Cash(+)       :" + edt_cr_amt.getText().toString(), "GBK");

                            mService.sendMessage("Return Cash(-)         :" + edt_posbal_returncash.getText().toString(), "GBK");

                        }
                        else{

                        }

                        /*mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+): " + edt_cr_amt.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);*/
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        mService.sendMessage("Net Cash      : " +edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Detail", "GBK");

                        String strTotalSales;
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Sales      : " + strTotalSales , "GBK");

                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }

//                        mIPosPrinterService.printSpecifiedTypeText("Total Return    : "+edt_totl_return.getText().toString().trim().toString(),"ST",24,callbackPPT8555);
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Return\n", "GBK");
                        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("Cash(+)      : " + retruncash, "GBK");

                        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+)      : " + retruncredit, "GBK");

                        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return(+)      : " + retruntotalsale, "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;

    }

    private void ppt_8527() {
        try {
            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                JSONArray printTest = new JSONArray();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);

                printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));
                printTest.put(getPrintObject("Z Report", "3", "center"));
                printTest.put(getPrintObject("ZNo      :" + strZ_Code, "2", "left"));
                printTest.put(getPrintObject("POS Name :" + Globals.objLPD.getDevice_Name(), "2", "left"));
                printTest.put(getPrintObject("Date     : " + date, "2", "left"));
                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                printTest.put(getPrintObject("Cashier  :" + user.get_name(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Opening Balance" + ":" + ob, "2", "left"));
                String cash;
                cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Cash" + ":" + cash, "2", "left"));

                String expenses;

                expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                printTest.put(getPrintObject("Expenses" + ":" + expenses, "2", "left"));
                printTest.put(getPrintObject("Credit Amount" + ":" + edt_cr_amt.getText().toString().trim().toString(), "2", "left"));
                printTest.put(getPrintObject("Debit Amount" + ":" + edt_dr_amt.getText().toString().trim().toString(), "2", "left"));
                printTest.put(getPrintObject("Total Return" + ":" + edt_totl_return.getText().toString().trim().toString(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Net Cash" + ":" + edt_total.getText().toString().trim().toString(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Sales Detail", "3", "center"));
                String strTotalSales;

                strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                printTest.put(getPrintObject("Total Sales" + ":" + strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "2", "left"));

                String strTotal;
                int iPayCount = 1;
                while (iPayCount < arrPaymentAmount.size()) {
                    strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                    printTest.put(getPrintObject(arrPaymentName.get(iPayCount) + ":" + strTotal, "2", "left"));
                    iPayCount++;
                }
                printJson.put("spos", printTest);
                // 设置底部空3行
                printJson.put("spos", printTest);
                ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PHA_POS() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 30, callback);
                        woyouService.printTextWithFont("Z Report\n", "", 30, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont("ZNo :" + strZ_Code + "\n", "", 30, callback);
                        woyouService.printTextWithFont("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Date     : " + date + "\n", "", 30, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier  :" + user.get_name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Opening Balance :" + ob + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Opening Balance", ":", ob}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Cash            :" + cash + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Cash(+)", ":", cash}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);


                        String expenses;

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Expenses(-)", ":", expenses}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Credit Amount(+)", ":", edt_cr_amt.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Net Cash", ":", edt_total.getText().toString().trim().toString()}, new int[]{10, 1, 15}, new int[]{0, 0, 0}, callback);

                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("Sales Detail\n", "", 30, callback);

                        String strTotalSales;

                        woyouService.setAlignment(0, callback);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        //woyouService.printTextWithFont("Total Sales     : " +strTotalSales+ "\n", "", 24, callback);

                        woyouService.printColumnsText(new String[]{"Total Sales", ":", strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")"}, new int[]{6, 1, 15}, new int[]{0, 0, 0}, callback);


                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            // woyouService.printTextWithFont(arrPaymentName.get(iPayCount) + " : " +strTotal+ "\n", "", 24, callback);

                            woyouService.printColumnsText(new String[]{arrPaymentName.get(iPayCount), ":", strTotal}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                            iPayCount++;
                        }
                        woyouService.printColumnsText(new String[]{"Total Return", ":", edt_totl_return.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.cutPaper(callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void print_expenses() {
        mylist = getlistEx();
        if (mylist != null) {
            adp = new MyAdapter(getApplicationContext(), mylist);
        }
        if (iswifi) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        performOperationEnEX();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
            startActivity(intent);
            finish();
        } else if (PrinterType.equals("1")) {
            try {
                if (woyouService == null) {
                } else {
                    mobile_pos_expenses();
                    Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception ex) {
            }

        } else if (PrinterType.equals("3")) {

            String result = bluetooth_exp_56();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (PrinterType.equals("4")) {

            String result = bluetooth_exp_80();

            if (result.equals("1")) {
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(X_ZActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }


        } else if (PrinterType.equals("5")) {
            try {
                if (woyouService == null) {
                } else {
                    PHA_POS_EXP_PRINT();
                }
            } catch (Exception ex) {
            }
        } else if (PrinterType.equals("6")) {
            ppt_8527_exp();

        } else if (PrinterType.equals("8")) {
            ppt_8555_XEXP();
        } else {
            Toast.makeText(X_ZActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
        }
    }

    private String bluetooth_exp_80() {

        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;


        if ((lang.compareTo("en")) == 0) {

            try {

                if (mService.isAvailable() == false) {

                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");
                                ab = BytesUtil.setAlignCenter(1);
                                mService.write(ab);
                            }
                        } catch (Exception ex) {
                        }
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("-----------------------------------------------", "GBK");
                        mService.sendMessage("Expense Report", "GBK");
                        mService.sendMessage("-----------------------------------------------", "GBK");

                        //mIPosPrinterService.printSpecifiedTypeText("X Report\n", "ST", 24, callbackPPT8555);
                        // mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("POS Name     : " + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date         : " + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier      : " + user.get_name(), "GBK");
                        mService.sendMessage("...............................................", "GBK");
                        mService.sendMessage("Expenses       Amount ", "GBK");
                        mService.sendMessage("...............................................", "GBK");

                        String expenses="";
                        int count = 0;
                        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
                        while (count < arraylist_PB.size()) {

                            String strRemarks = arraylist_PB.get(count).get_remarks();
                            int len = 12;
                            if (strRemarks.length() > len) {
                                strRemarks = strRemarks.substring(0, len);
                            } else {
                                for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                                    strRemarks = strRemarks + " ";
                                }
                            }
                            expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                            mService.sendMessage(" " + strRemarks + " " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check) , "GBK");


                            // mService.sendMessage(" " + strRemarks + "       " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check), "GBK");
                            ;
                           /* amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                            totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);*/


                            count++;
                        }
                       /* mService.sendMessage("................................", "GBK");
                        mService.sendMessage("Date  :" + totalAmount, "GBK");*/
                        String strTotalSales;
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Expenses      : " + expenses + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    Globals.strOldCrAmt = "0";
                    flag = "1";
                    Globals.setEmpty();

                }


            } catch (Exception ex) {

            }

        }

        return flag;
    }

    private void ppt_8527_exp() {
        try {
            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                JSONArray printTest = new JSONArray();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));
                printTest.put(getPrintObject("POS Name :" + Globals.objLPD.getDevice_Name(), "2", "left"));
                printTest.put(getPrintObject("Date     : " + date, "2", "left"));
                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                printTest.put(getPrintObject("Cashier  :" + user.get_name(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Expenses                Amount", "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));

                int count = 0;
                arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);

                while (count < arraylist_PB.size()) {
                    String strRemarks = arraylist_PB.get(count).get_remarks();
                    int len = 12;
                    if (strRemarks.length() > len) {
                        strRemarks = strRemarks.substring(0, len);
                    } else {
                        for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                            strRemarks = strRemarks + " ";
                        }
                    }
                    printTest.put(getPrintObject(" " + strRemarks + "         " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check), "2", "left"));
                    amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                    totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);
                    count++;
                }
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject("Total :" + totalAmount, "2", "left"));
                printJson.put("spos", printTest);
                // 设置底部空3行
                printJson.put("spos", printTest);
                ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PHA_POS_EXP_PRINT() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printTextWithFont("POS Name : " + Globals.objLPD.getDevice_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Date    : " + date + "\n", "", 30, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier : " + user.get_name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("--------------------------------------\n", "", 30, callback);
                        woyouService.printTextWithFont("Expenses                  Amount\n", "", 30, callback);
                        woyouService.printTextWithFont("--------------------------------------\n", "", 30, callback);

                        int count = 0;
                        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);

                        while (count < arraylist_PB.size()) {
                            String strRemarks = arraylist_PB.get(count).get_remarks();
                            int len = 12;
                            if (strRemarks.length() > len) {
                                strRemarks = strRemarks.substring(0, len);
                            } else {
                                for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                                    strRemarks = strRemarks + " ";
                                }
                            }

                            woyouService.printTextWithFont(" " + strRemarks + "             " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check) + "\n", "", 30, callback);

                            amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                            totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);
                            count++;
                        }
                        woyouService.printTextWithFont("--------------------------------------\n", "", 30, callback);
                        woyouService.printTextWithFont("Total :" + totalAmount + "\n", "", 30, callback);

                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.cutPaper(callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void mobile_pos_expenses() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.setAlignment(0, callback);

                        woyouService.printTextWithFont("POS Name : " + Globals.objLPD.getDevice_Name() + "\n", "", 24, callback);
                        woyouService.printTextWithFont("Date    : " + date + "\n", "", 24, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier : " + user.get_name() + "\n", "", 24, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.printTextWithFont("Expenses      Amount\n", "", 24, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                        int count = 0;
                        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);

                        while (count < arraylist_PB.size()) {
                            String strRemarks = arraylist_PB.get(count).get_remarks();
                            int len = 12;
                            if (strRemarks.length() > len) {
                                strRemarks = strRemarks.substring(0, len);
                            } else {
                                for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                                    strRemarks = strRemarks + " ";
                                }
                            }

                            woyouService.printTextWithFont(" " + strRemarks + " " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check) + "\n", "", 24, callback);

                            amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                            totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);
                            count++;
                        }
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.printTextWithFont("Total :" + totalAmount + "\n", "", 24, callback);

                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private String bluetooth_exp_56() {

        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;


        if ((lang.compareTo("en")) == 0) {

            try {

                if (mService.isAvailable() == false) {

                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");
                                ab = BytesUtil.setAlignCenter(1);
                                mService.write(ab);
                            }
                        } catch (Exception ex) {
                        }
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("--------------------------------", "GBK");
                        mService.sendMessage("Expense Report", "GBK");
                        mService.sendMessage("--------------------------------", "GBK");

                        //mIPosPrinterService.printSpecifiedTypeText("X Report\n", "ST", 24, callbackPPT8555);
                       // mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        //mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("POS Name : " + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date    : " + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier : " + user.get_name(), "GBK");
                        mService.sendMessage("................................", "GBK");
                        mService.sendMessage("Expenses      Amount ", "GBK");
                        mService.sendMessage("................................", "GBK");

String expenses="";
                        int count = 0;
                        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
                        while (count < arraylist_PB.size()) {

                            String strRemarks = arraylist_PB.get(count).get_remarks();
                            int len = 12;
                            if (strRemarks.length() > len) {
                                strRemarks = strRemarks.substring(0, len);
                            } else {
                                for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                                    strRemarks = strRemarks + " ";
                                }
                            }
                            expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                            mService.sendMessage(" " + strRemarks + " " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check) , "GBK");


                           // mService.sendMessage(" " + strRemarks + "       " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check), "GBK");
                            ;
                           /* amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
                            totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);*/


                            count++;
                        }
                       /* mService.sendMessage("................................", "GBK");
                        mService.sendMessage("Date  :" + totalAmount, "GBK");*/
                        String strTotalSales;
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Expenses    : " + expenses + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    Globals.strOldCrAmt = "0";
                    flag = "1";
                    Globals.setEmpty();

                }


            } catch (Exception ex) {

            }

        }

        return flag;
    }

    private void fill_x_z_detail() {
        try {
            String strDrQry = "Select sum(total) from returns where is_post = 'true' and z_code='0' and payment_id='1'";
            Cursor cursor = database.rawQuery(strDrQry, null);
            if (cursor.moveToFirst()) {
                do {
                    strReturnTotal = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                    edt_totl_return.setText("" + strReturnTotal);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            strReturnTotal = Globals.myNumberFormat2Price(0, decimal_check);
            edt_totl_return.setText("" + strReturnTotal);
        }

        String strTotalCredit = Acc_Customer_Credit.getTotalCredit(getApplicationContext(), database, "where z_no ='0'");
        if (strTotalCredit == null) {
            strCreditAmt = Globals.myNumberFormat2Price(0, decimal_check);
            edt_cr_amt.setText("" + strCreditAmt);
        } else {
            strCreditAmt = Globals.myNumberFormat2Price(Double.parseDouble(strTotalCredit), decimal_check);
            edt_cr_amt.setText("" + strCreditAmt);
        }

        try {
            String strDrQry = "Select sum(amount) from acc_customer_dedit where z_no ='0' and ref_type='S'";
            Cursor cursor = database.rawQuery(strDrQry, null);
            if (cursor.moveToFirst()) {
                do {
                    strDebitAmt = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                    // edt_dr_amt.setText("" + strDebitAmt);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            strDebitAmt = Globals.myNumberFormat2Price(0, decimal_check);
            //edt_dr_amt.setText("" + strDebitAmt);
        }

        String strTotalEXP = Pos_Balance.getTotalExpenses(getApplicationContext(), database, " where z_code ='0' And type ='EXP'");
        String strExpenses;
        if (strTotalEXP == null) {
            strExpenses = Globals.myNumberFormat2Price(0, decimal_check);
            edt_expenses.setText("" + strExpenses);
        } else {
            strExpenses = Globals.myNumberFormat2Price(Double.parseDouble(strTotalEXP), decimal_check);
            edt_expenses.setText("" + strExpenses);
        }

        pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, " WHERE z_code ='0' And type ='OB'");
        String opening_amount;
        if (pos_balance == null) {
            String op_balance = "0";
            opening_amount = Globals.myNumberFormat2Price(Double.parseDouble(op_balance), decimal_check);
            edt_ob.setText("      " + opening_amount);
        } else {
            opening_amount = Globals.myNumberFormat2Price(Double.parseDouble(pos_balance.get_amount()), decimal_check);
            edt_ob.setText("      " + opening_amount);
        }

        arrayList = Orders.getAllOrders(getApplicationContext(), "WHERE order_status ='CLOSE' And z_code ='0'", database);
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                String strCode = arrayList.get(i).get_order_code();
                strOrderCode.add(strCode);
            }
        }

        try {

            if (strOrderCode.size() > 0) {

                for (int j = 0; j < strOrderCode.size(); j++) {
                    strSelectedCategory = strSelectedCategory + "'" + strOrderCode.get(j).toString() + "',";
                }
                strSelectedCategory = strSelectedCategory.substring(0, strSelectedCategory.length() - 1).toString();
            } else {
            }
        } catch (Exception ex) {
            strSelectedCategory = "";
        }

        String strCash;
        String strTotalCash = Order_Payment.getTotalCash(getApplicationContext(), database, " where order_code IN (" + strSelectedCategory + ") And payment_id='1'");
        if (strTotalCash == null) {
            strCash = Globals.myNumberFormat2Price(0, decimal_check);
            edt_cash.setText("" + strCash + "");
        } else {
            strCash = Globals.myNumberFormat2Price(Double.parseDouble(strTotalCash), decimal_check);
            edt_cash.setText("" + strCash + "");
        }


        //This is for hamseer without return in total
        //Double total = Double.parseDouble(strCash) + Double.parseDouble(opening_amount) + Double.parseDouble(strCreditAmt) + Double.parseDouble(strDebitAmt) - Double.parseDouble(strExpenses);
        String symbol;
        try {
            symbol = Globals.objLPD.getCurreny_Symbol();
        } catch (Exception ex) {
            symbol = "";
        }

        try {
            if (symbol.equals("")) {
            } else {
                symbol = " (" + Globals.objLPD.getCurreny_Symbol() + ")";
            }
        } catch (Exception ex) {
            symbol = "";
        }
        try {
            String strDrQry = "Select sum(total) from returns where is_post = 'true' and z_code='0' and payment_id='1'";
            Cursor cursor = database.rawQuery(strDrQry, null);
            if (cursor.moveToFirst()) {
                do {
                    strReturnTotal = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                    edt_ret_cash.setText("" + strReturnTotal);
                    edt_posbal_returncash.setText("" + strReturnTotal);

                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            strReturnTotal = Globals.myNumberFormat2Price(0, decimal_check);
            edt_ret_cash.setText("" + strReturnTotal);
            edt_posbal_returncash.setText("" + strReturnTotal);
        }
        try {
            String strDrQry = "Select sum(total) from returns where is_post = 'true' and z_code='0' and payment_id='5'";
            Cursor cursor = database.rawQuery(strDrQry, null);
            if (cursor.moveToFirst()) {
                do {
                    String strReturncreditTotal = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                    edt_return_amount.setText("" + strReturncreditTotal);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            String strReturncreditTotal = Globals.myNumberFormat2Price(0, decimal_check);
            edt_return_amount.setText("" + strReturncreditTotal);
        }


        Double cash_total = Double.parseDouble(strCash) + Double.parseDouble(strCreditAmt) - Double.parseDouble(strReturnTotal);
        edt_cash_total.setText("      " + Globals.myNumberFormat2Price(cash_total, decimal_check) + "");


        Double sales_cash_total = Double.parseDouble(edt_ret_cash.getText().toString()) + Double.parseDouble(edt_return_amount.getText().toString());
        edt_retntotal.setText("  " + Globals.myNumberFormat2Price(sales_cash_total, decimal_check));

        Double total = Double.parseDouble(edt_cash_total.getText().toString()) + Double.parseDouble(opening_amount) - Double.parseDouble(strExpenses);
        edt_total.setText("      " + Globals.myNumberFormat2Price(total, decimal_check) + symbol);
        String strQry;
        Cursor cursor;
        strQry = "Select Sum(orders.total) as TotalSales from orders  where order_status ='CLOSE' and orders.Z_Code = '0'";
        cursor = database.rawQuery(strQry, null);
        String strTotalSales = "0";

        while (cursor.moveToNext()) {
            try {
                strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
            } catch (Exception ex) {
                strTotalSales = Globals.myNumberFormat2Price(0, decimal_check);
            }

            total_sale.setText(" Total Sales     : " + strTotalSales + symbol);
            arrPaymentName.add("Total Sales");
            arrPaymentAmount.add(strTotalSales);
        }

        strQry = "Select Sum(order_payment.pay_amount),order_payment.payment_id ,  payments.payment_name   as TotalSales from orders   \n" +
                "INNER JOIN  order_payment on order_payment.order_code = orders.order_code   \n" +
                "INNER JOIN  payments on payments.payment_id =   order_payment.payment_id   \n" +
                "where order_status ='CLOSE' and orders.Z_Code = '0'  \n" +
                "Group by order_payment.payment_id";

        cursor = database.rawQuery(strQry, null);


        while (cursor.moveToNext()) {
            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
            strname = cursor.getString(2);
            TableRow tr = new TableRow(this);

            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);
           // tr.setGravity(1);

            tv1.setTextColor(Color.parseColor("#333333"));
            tv1.setTextSize(16);
            tv1.setText(cursor.getString(2));
            tv1.setGravity(Gravity.LEFT);
            tr.addView(tv1);


            tv2.setTextColor(Color.parseColor("#333333"));
            tv2.setTextSize(16);
            tv2.setText(" : ");
            tv2.setGravity(Gravity.LEFT);
            tr.addView(tv2);

            tv3.setTextColor(Color.parseColor("#333333"));
            tv3.setTextSize(16);
            tv3.setText(strTotal);
            tv3.setGravity(Gravity.RIGHT);

            tr.addView(tv3);

            linearLayout.addView(tr);

            arrPaymentName.add(cursor.getString(2));
            arrPaymentAmount.add(strTotal);

        }
    }

    private String send_online(final ProgressDialog pDialog) {
        String result = Pos_Balance.sendOnServer(getApplicationContext(), database, db, "SELECT device_code,z_code,date,total_amount,is_active,modified_by FROM  z_close Where is_push = 'N'", liccustomerid,serial_no,android_id,myKey);
        return result;
    }

    private String bluetooth_56() {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if ((lang.compareTo("en")) == 0) {

            try {
                if (mService.isAvailable() == false) {
                } else {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        byte[] ab;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage(" " + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                        mService.sendMessage("" + Globals.objLPR.getAddress() , "GBK");
                        mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                            }
                        } catch (Exception ex) {
                        }
                        mService.sendMessage("------------------------------", "GBK");
                        mService.sendMessage("Z Report\n", "GBK");
                        mService.sendMessage("------------------------------", "GBK");
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("ZNo      :" + strZ_Code, "GBK");
                        mService.sendMessage("POS Name :" + Globals.objLPD.getDevice_Name(), "GBK");
                        mService.sendMessage("Date     :" + date, "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mService.sendMessage("Cashier  :" + user.get_name(), "GBK");
                        mService.sendMessage("------------------------------", "GBK");

                        String ob;

                        ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        mService.sendMessage("Opening Balance : " + ob, "GBK");
                        String expenses;
                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);
                        mService.sendMessage("Expenses(-)     : " + expenses, "GBK");
                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        mService.sendMessage("Cash(+)  : " + cash, "GBK");

                        if(Globals.objLPR.getproject_id().equals("cloud")){
                            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
                            mService.sendMessage("Accounts Cash(+)  :" + edt_cr_amt.getText().toString(), "GBK");

                            mService.sendMessage("Return Cash(-)  :" + edt_posbal_returncash.getText().toString(), "GBK");

                        }
                        else{

                        }

                        /*mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+): " + edt_cr_amt.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);*/
                        mService.sendMessage("--------------------------------", "GBK");
                        mService.sendMessage("Net Cash : " +edt_total.getText().toString().trim().toString(), "GBK");
                        mService.sendMessage("--------------------------------", "GBK");
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Detail", "GBK");

                        String strTotalSales;
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

                        mService.sendMessage("Total Sales  : " + strTotalSales , "GBK");

                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            mService.sendMessage(arrPaymentName.get(iPayCount) + " : " + strTotal, "GBK");
                            iPayCount++;
                        }

//                        mIPosPrinterService.printSpecifiedTypeText("Total Return    : "+edt_totl_return.getText().toString().trim().toString(),"ST",24,callbackPPT8555);
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("Sales Return\n", "GBK");
                        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                        mService.sendMessage("Cash(+)  : " + retruncash, "GBK");

                        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check);
                        mService.sendMessage("Credit Amount(+)  : " + retruncredit, "GBK");

                        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check);
                        mService.sendMessage("Total Return(+)  : " + retruntotalsale, "GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.sendMessage("\n","GBK");
                        mService.write(cmd);
                    }
                    flag = "1";
                }
            } catch (Exception ex) {
            }
        }
        return flag;
    }

    private void performOperationEn() {
        noofPrint = Integer.parseInt(settings.get_No_Of_Print());
        if (mylist.size() > 0) {
            try {
                String bill = "";
                for (String data : mylist) {
                    bill = bill + data;
                }

                for (int k = 0; k < noofPrint; k++) {
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
                    X_ZActivity.this.finish();
                }
            } catch (Exception e) {
            }

        }
    }

    private void performOperationEnEX() {

        if (mylist.size() > 0) {
            try {
                String bill = "";
                for (String data : mylist) {
                    bill = bill + data;
                }

                for (int k = 1; k <= noofPrint; k++) {
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
                    X_ZActivity.this.finish();
                }
            } catch (Exception e) {
            }
        }
    }


    private void mobile_pos() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont(" \n", "", 30, callback);
                        woyouService.printTextWithFont("Z Report\n", "", 30, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont("ZNo :" + strZ_Code + "\n", "", 30, callback);
                        woyouService.printTextWithFont("POS Name :" + Globals.objLPD.getDevice_Name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Date     : " + date + "\n", "", 30, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printTextWithFont("Cashier :" + user.get_name() + "\n", "", 30, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

                        // woyouService.printTextWithFont("Opening Balance :" + ob + "\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Opening Balance", ":", ob}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);

                        String cash;
                        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Cash(+)", ":", cash}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);


                        String expenses;

                        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check);

                        woyouService.printColumnsText(new String[]{"Expenses(-)", ":", expenses}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Credit Amount(+)", ":", edt_cr_amt.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Net Cash", ":", edt_total.getText().toString().trim().toString()}, new int[]{6, 1, 15}, new int[]{0, 0, 0}, callback);

                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.setAlignment(1, callback);
                        woyouService.printTextWithFont("Sales Detail\n", "", 30, callback);

                        String strTotalSales;

                        woyouService.setAlignment(0, callback);
                        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);
                        //woyouService.printTextWithFont("Total Sales     : " +strTotalSales+ "\n", "", 24, callback);

                        woyouService.printColumnsText(new String[]{"Total Sales", ":", strTotalSales + "(" + Globals.objLPD.getCurreny_Symbol() + ")"}, new int[]{6, 1, 15}, new int[]{0, 0, 0}, callback);


                        String strTotal;
                        int iPayCount = 1;
                        while (iPayCount < arrPaymentAmount.size()) {
                            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check);
                            woyouService.printColumnsText(new String[]{arrPaymentName.get(iPayCount), ":", strTotal}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                            iPayCount++;
                        }
                        woyouService.printColumnsText(new String[]{"Total Return", ":", edt_totl_return.getText().toString().trim().toString()}, new int[]{15, 1, 10}, new int[]{0, 0, 0}, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                        woyouService.printTextWithFont(" \n", "", 24, callback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void DisplayError(int nError) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Information");
        String strMsg = "Getting Request!";
        switch (nError) {
            case ZQ_USBERROR:
                // strMsg = "USB Error or printer is busy?";
                break;
            case ZQ_NOPRNPORT:
                // strMsg = "the port is not existed";
                break;
            case ZQ_NODSPPORT:
                // strMsg = "the port is not existed";
                break;
            case ZQ_NOCOMPORT:
                // strMsg = "the port is not existed";
                break;
            case ZQ_SENDERROR:
                // strMsg = "Send Data error";
                break;
            case ZQ_ERRPARAM:
                // strMsg = "Invalidate param";
                break;
            case ZQ_NOPERMISSION:
                strMsg = "Invalidate Permission";
                break;
            case ZQ_WRONGFILE:
                // strMsg = "Can not open the file";
                break;
            case ZQ_WRONGDATA:
                // strMsg = "Data format is wrong";
                break;
            case ZQ_READERROR:
                // strMsg = "Read data failed";
                break;
            case ZQ_CONTEXTNULL:
                // strMsg = "Contex is null";
                break;
            case ZQ_PORTISCLOSE:
                // strMsg = "The port is closed";
                break;
        }

        b.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //performOperation();
            }
        });

        b.setMessage(strMsg);
        b.show();
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(X_ZActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(X_ZActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (pDialog.isShowing()) {
//            pDialog.dismiss();
//        }

    }

    public Boolean CheckprinterConnection() {
        if (settings != null) {

            if (settings.getPrinterId().equals("2")) {
                String tmpStr = settings.getPrinterIp().trim();
                String ipAddress = "";
                String tmpPort = "";
                int port = 9100;
                String[] strings = Globals.StringSplit(tmpStr, ":");
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
        } else {
            return false;
        }
    }

    private class LongOperation extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean iswifi = CheckprinterConnection();
            return iswifi;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result != null) {
                iswifi = result;
            }

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(X_ZActivity.this);
            dialog.setCancelable(false);
            dialog.show();
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //������
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
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //�����ѶϿ�����
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //�޷������豸
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

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
        mService.write(sendData);
        Log.d("��������", "" + sendData.length);
    }

    private void send_email(String strEmail, String exp) {
        try {
            String[] recipients = strEmail.split(",");
            final SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender(settings.get_Email(), settings.get_Password(), settings.get_Host(), settings.get_Port());
            email.m.set_from(settings.get_Email());
            if (exp.equals("EXP")) {
                email.m.setBody("Expenses");
            } else {
                email.m.setBody("X-Z");
            }

            email.m.set_to(recipients);
            if (exp.equals("EXP")) {
                email.m.set_subject(Globals.objLPD.getDevice_Name() + ":" + " Expenses Report " + strZ_Code);
            } else {
                email.m.set_subject(Globals.objLPD.getDevice_Name() + ":" + " X-Z Report " + strZ_Code);
            }
            if (exp.equals("EXP")) {
                email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "PDF Report" + "/" + "Expense Report" + strZ_Code + ".pdf");
            } else {
                email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "PDF Report" + "/" + "X-Z Report" + strZ_Code + ".pdf");
            }

            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        X_ZActivity activity;

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
                Log.e(SetttingsActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(SetttingsActivity.SendEmailAsyncTask.class.getName(), "Email failed");
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

    protected void performPDFExport() {

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + "X-Z Report" + strZ_Code + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
            try {
                Bitmap bitmap;
                Uri uri = Uri.parse(settings.get_Logo());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] imageInByte = stream.toByteArray();
                image = Image.getInstance(imageInByte);
                image.scaleAbsolute(42f, 42f);//image width,height

            } catch (Exception ex) {
            }


            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph("X-Z Report", B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);

            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph("Company Name    : " + Globals.objLPR.getCompany_Name(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph("POS Name    : " + Globals.objLPD.getDevice_Name(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph("Date : " + date, B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);
            document.open();

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph("Cashier    : " + user.get_name(), B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);
            document.open();

            PdfPTable table_ob = new PdfPTable(2);

            Phrase pr = new Phrase("Opening Balance", B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_ob.addCell(c1);

            pr = new Phrase("" + edt_ob.getText().toString(), B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_ob.addCell(c3);
//            table_ob.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_cash = new PdfPTable(2);

            Phrase pr1 = new Phrase("Cash", B10);
            PdfPCell c11 = new PdfPCell(pr1);
            c11.setPadding(5);
//            c11.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash.addCell(c11);

            pr1 = new Phrase("" + edt_cash.getText().toString(), B10);
            PdfPCell c32 = new PdfPCell(pr1);
            c32.setPadding(5);
            c32.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash.addCell(c32);
//            table_cash.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_cash_total = new PdfPTable(2);

            Phrase pr2 = new Phrase(" ", B10);
            PdfPCell c12 = new PdfPCell(pr2);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash_total.addCell(c12);
            pr2 = new Phrase("" + edt_cash_total.getText().toString(), B10);
            PdfPCell c13 = new PdfPCell(pr2);
            c13.setPadding(5);
            c13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash_total.addCell(c13);
            table_cash_total.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_expenses = new PdfPTable(2);

            Phrase pr21 = new Phrase("Expenses", B10);
            PdfPCell c14 = new PdfPCell(pr21);
            c14.setPadding(5);
            c14.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_expenses.addCell(c14);
            pr21 = new Phrase("" + edt_expenses.getText().toString(), B10);

            PdfPCell c15 = new PdfPCell(pr21);
            c15.setPadding(5);
            c15.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_expenses.addCell(c15);
            document.open();

            PdfPTable table_total = new PdfPTable(2);

            Phrase pr23 = new Phrase("Total", B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total.addCell(c16);
            pr23 = new Phrase("" + edt_total.getText().toString(), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total.addCell(c17);
//            table_total.setSpacingBefore(10.0f);
            document.open();


//            document.add(image);
            document.add(tableh);
            document.add(table_company_name);
            document.add(table_posno);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table_ob);
            document.add(table_cash);
            document.add(table_cash_total);
            document.add(table_expenses);
            document.add(table_total);

            PdfPTable table5 = new PdfPTable(1);
            table5.setSpacingBefore(10.0f); // Space Before table starts, like
            table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPTable table4 = new PdfPTable(2);
            table4.setSpacingBefore(20.0f);
            float[] columnWidths1 = new float[]{20f, 5f};
            table4.setWidths(columnWidths1);
            table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            Phrase p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Cashier", N12);
            PdfPCell cell5 = new PdfPCell(p5);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell5.setBorder(Rectangle.NO_BORDER);
            p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Manager", N12);
            PdfPCell cell6 = new PdfPCell(p5);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell5);
            table4.addCell(cell6);
            document.add(table4);
            document.newPage();
            document.close();
            file.close();
//            Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
//                    Toast.LENGTH_SHORT).show();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    //startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "No Application available to view pdf",
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            f.delete();
        }
    }


    protected void performPDFExportZDetail() {
        Double strExpenseTotal = 0d, strInvoiceTotal = 0d;

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + "Z Detail Report" + strZ_Code + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
            try {
                Bitmap bitmap;
                Uri uri = Uri.parse(settings.get_Logo());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] imageInByte = stream.toByteArray();
                image = Image.getInstance(imageInByte);
                image.scaleAbsolute(42f, 42f);//image width,height

            } catch (Exception ex) {
            }


            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph("Z Detail Report", B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);

            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph("Company Name    : " + Globals.objLPR.getCompany_Name(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph("POS Name    : " + Globals.objLPD.getDevice_Name(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph("Date : " + date, B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);
            document.open();

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph("Cashier    : " + user.get_name(), B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);
            document.open();

            PdfPTable table_ob = new PdfPTable(2);

            Phrase pr = new Phrase("Opening Balance", B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob.addCell(c1);

            pr = new Phrase("" + edt_ob.getText().toString(), B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_ob.addCell(c3);
            table_ob.setSpacingAfter(10.0f);
            document.open();

            list1a.clear();
            list2a.clear();
            list3a.clear();

            arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
            if (arraylist_PB.size() > 0) {
                for (int i = 0; i < arraylist_PB.size(); i++) {
                    list1a.add(i + 1 + "");
                    list2a.add(arraylist_PB.get(i).get_remarks());
                    list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(i).get_amount()), decimal_check));

                    strExpenseTotal = strExpenseTotal + Double.parseDouble(arraylist_PB.get(i).get_amount());

//                    strExpenseTotal = strExpenseTotal + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(i).get_amount()), decimal_check));
                }

            }

            PdfPTable Expanses = new PdfPTable(3);
            Phrase pr1;
            PdfPCell c7;
            for (int i = 0; i < list1a.size(); i++) {
                pr1 = new Phrase(list1a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                Expanses.addCell(c7);

                pr1 = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                Expanses.addCell(c7);

                pr1 = new Phrase(list3a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                Expanses.addCell(c7);
            }

            PdfPTable Expanses_total = new PdfPTable(3);
            Phrase pr3;
            PdfPCell c9;

            pr3 = new Phrase("", B10);
            c9 = new PdfPCell(pr3);
            c9.setPadding(5);
            c9.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Expanses_total.addCell(c9);

            pr3 = new Phrase("Total Expanses", B10);
            c9 = new PdfPCell(pr3);
            c9.setPadding(5);
            c9.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Expanses_total.addCell(c9);

            pr3 = new Phrase("" + Globals.myNumberFormat2Price(strExpenseTotal, decimal_check), B10);
            c9 = new PdfPCell(pr3);
            c9.setPadding(5);
            c9.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Expanses_total.addCell(c9);
            document.open();


            ArrayList<String> list_ord_s_no = new ArrayList();
            ArrayList<String> list_ord_name = new ArrayList();
            ArrayList<String> list_ord_amt = new ArrayList();

            arraylist_orders = Orders.getAllOrders(getApplicationContext(), "WHERE  z_code ='0'", database);
            if (arraylist_orders.size() > 0) {
                for (int i = 0; i < arraylist_orders.size(); i++) {
                    list_ord_s_no.add(i + 1 + "");
                    list_ord_name.add(arraylist_orders.get(i).get_order_code());
                    list_ord_amt.add(Globals.myNumberFormat2Price(Double.parseDouble(arraylist_orders.get(i).get_total()), decimal_check));

                    strInvoiceTotal = strInvoiceTotal + Double.parseDouble(arraylist_orders.get(i).get_total());

                }

            }

            PdfPTable Orders = new PdfPTable(3);
            Phrase pr2;
            PdfPCell c8;
            for (int i = 0; i < list_ord_s_no.size(); i++) {
                pr2 = new Phrase(list_ord_s_no.get(i), N9);
                c8 = new PdfPCell(pr2);
                c8.setPadding(5);
                c8.setHorizontalAlignment(Element.ALIGN_LEFT);
                Orders.addCell(c8);

                pr2 = new Phrase(list_ord_name.get(i), N9);
                c8 = new PdfPCell(pr2);
                c8.setPadding(5);
                c8.setHorizontalAlignment(Element.ALIGN_RIGHT);
                Orders.addCell(c8);

                pr2 = new Phrase(list_ord_amt.get(i), N9);
                c8 = new PdfPCell(pr2);
                c8.setPadding(5);
                c8.setHorizontalAlignment(Element.ALIGN_RIGHT);
                Orders.addCell(c8);

            }
            PdfPTable Orders_total = new PdfPTable(3);
            Phrase pr4;
            PdfPCell c10;

            pr4 = new Phrase("", B10);
            c10 = new PdfPCell(pr4);
            c10.setPadding(5);
            c10.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Orders_total.addCell(c10);

            pr4 = new Phrase("Invoice Total", B10);
            c10 = new PdfPCell(pr4);
            c10.setPadding(5);
            c10.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Orders_total.addCell(c10);

            pr4 = new Phrase("" + Globals.myNumberFormat2Price(strInvoiceTotal, decimal_check), B10);
            c10 = new PdfPCell(pr4);
            c10.setPadding(5);
            c10.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Orders_total.addCell(c10);
            document.open();


            PdfPTable table_ob1 = new PdfPTable(2);
            pr = new Phrase("Opening Balance", B10);
            c1 = new PdfPCell(pr);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob1.addCell(c1);

            pr = new Phrase("" + edt_ob.getText().toString(), B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_ob1.addCell(c3);
            table_ob1.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_cash = new PdfPTable(2);

            pr1 = new Phrase("Total Expanses", B10);
            PdfPCell c11 = new PdfPCell(pr1);
            c11.setPadding(5);
            table_cash.addCell(c11);

            pr1 = new Phrase("" + Globals.myNumberFormat2Price(strExpenseTotal, decimal_check), B10);
            PdfPCell c32 = new PdfPCell(pr1);
            c32.setPadding(5);
            c32.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash.addCell(c32);
            document.open();


            PdfPTable table_cash_total = new PdfPTable(2);

            pr2 = new Phrase(" Total Invoice", B10);
            PdfPCell c12 = new PdfPCell(pr2);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cash_total.addCell(c12);
            pr2 = new Phrase("" + Globals.myNumberFormat2Price(strInvoiceTotal, decimal_check), B10);
            PdfPCell c13 = new PdfPCell(pr2);
            c13.setPadding(5);
            c13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cash_total.addCell(c13);
            document.open();


            PdfPTable table_total = new PdfPTable(2);

            Phrase pr23 = new Phrase("Total Cash On Hand", B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_total.addCell(c16);
            pr23 = new Phrase("" + Globals.myNumberFormat2Price(strInvoiceTotal - strExpenseTotal, decimal_check), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total.addCell(c17);
            document.open();

            document.add(tableh);
            document.add(table_company_name);
            document.add(table_posno);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table_ob);
            document.add(Expanses);
            document.add(Expanses_total);
            document.add(Orders);
            document.add(Orders_total);
            document.add(table_ob1);
            document.add(table_cash);
            document.add(table_cash_total);
            document.add(table_total);


            PdfPTable table5 = new PdfPTable(1);
            table5.setSpacingBefore(10.0f); // Space Before table starts, like
            table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPTable table4 = new PdfPTable(2);
            table4.setSpacingBefore(20.0f);
            float[] columnWidths1 = new float[]{20f, 5f};
            table4.setWidths(columnWidths1);
            table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            Phrase p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Cashier", N12);
            PdfPCell cell5 = new PdfPCell(p5);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell5.setBorder(Rectangle.NO_BORDER);
            p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Manager", N12);
            PdfPCell cell6 = new PdfPCell(p5);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell5);
            table4.addCell(cell6);
            document.add(table4);
            document.newPage();
            document.close();
            file.close();

            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    //startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "No Application available to view pdf",
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            f.delete();
        }
    }


    private void performPDFExport_Export() {
        Double strTotal = 0.0;
        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
        if (arraylist_PB.size() > 0) {
            for (int i = 0; i < arraylist_PB.size(); i++) {
                list1a.add(i + 1 + "");
                list2a.add(arraylist_PB.get(i).get_remarks());
                list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(i).get_amount()), decimal_check));

                strTotal = strTotal + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(i).get_amount()), decimal_check));

            }

        }


        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + "Expense Report" + strZ_Code + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
            try {
                Bitmap bitmap;
                Uri uri = Uri.parse(settings.get_Logo());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] imageInByte = stream.toByteArray();
                image = Image.getInstance(imageInByte);
                image.scaleAbsolute(42f, 42f);//image width,height

            } catch (Exception ex) {
            }


            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph("Expense Report", B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);

            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph("Company Name    : " + Globals.objLPR.getCompany_Name(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph("POS Name    : " + Globals.objLPD.getDevice_Name(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph("Date : " + date, B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);
            document.open();

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph("Cashier    : " + user.get_name(), B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);

            PdfPTable table = new PdfPTable(3);

            Phrase pr = new Phrase("SNo.", B10);
            PdfPCell c19 = new PdfPCell(pr);
            c19.setPadding(5);
            table.addCell(c19);

            pr = new Phrase("Expenses", B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setPadding(5);
            table.addCell(c1);
            pr = new Phrase("Amount", B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);

            for (int i = 0; i < list1a.size(); i++) {
                Phrase pr1 = new Phrase(list1a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c7);

                pr1 = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr1 = new Phrase(list3a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

            }
            table.setSpacingBefore(10.0f);
            table.setHeaderRows(1);
            document.open();

            PdfPTable table_total = new PdfPTable(2);

            Phrase pr23 = new Phrase("Total", B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total.addCell(c16);
            pr23 = new Phrase("" + strTotal, B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total.addCell(c17);
            table_total.setSpacingBefore(10.0f);
            document.open();


            document.add(tableh);
            document.add(table_company_name);
            document.add(table_posno);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table);
            document.add(table_total);

            PdfPTable table5 = new PdfPTable(1);
            table5.setSpacingBefore(10.0f); // Space Before table starts, like
            table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPTable table4 = new PdfPTable(2);
            table4.setSpacingBefore(20.0f);
            float[] columnWidths1 = new float[]{20f, 5f};
            table4.setWidths(columnWidths1);
            table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            Phrase p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Cashier", N12);
            PdfPCell cell5 = new PdfPCell(p5);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell5.setBorder(Rectangle.NO_BORDER);
            p5 = new Phrase("Signature" + "\n" + "\n" + "\n" + "\n" + "\n" + "Manager", N12);
            PdfPCell cell6 = new PdfPCell(p5);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell5);
            table4.addCell(cell6);
            document.add(table4);
            document.newPage();
            document.close();
            file.close();
//            Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
//                    Toast.LENGTH_SHORT).show();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    //startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "No Application available to view pdf",
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            f.delete();
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

    private ArrayList<String> getlist() {
        ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("                " + Globals.objLPR.getCompany_Name() + "\n");
        mylist.add("                " + Globals.objLPR.getAddress() + "\n");
        mylist.add("                " + Globals.objLPR.getMobile_No() + "\n");
        try {
            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

            } else {
                mylist.add("                  " + Globals.objLPR.getService_code_tariff() + "\n");

            }
        } catch (Exception ex) {
        }
        mylist.add("-----------------------------------------------\n");
        mylist.add("                "+"Z Report\n");
        mylist.add("-----------------------------------------------\n");

        mylist.add("ZNo         :" + strZ_Code + "\n");
        mylist.add("POS Name    : " + Globals.objLPD.getDevice_Name() + "\n");
        mylist.add("Date        : " + date + "\n");
        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        mylist.add("Cashier     : " + user.get_name() + "\n");
        mylist.add("------------------------------------------------------\n");
        String ob = Globals.myNumberFormat2Price(Double.parseDouble(edt_ob.getText().toString()), decimal_check);

        mylist.add("Opening Balance     : " + ob+"\n");

        String cash;
        cash = Globals.myNumberFormat2Price(Double.parseDouble(edt_cash.getText().toString()), decimal_check+"\n");

        mylist.add("Cash(+)             : " + cash+"\n");
        String expenses;

        expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check+"\n");

        mylist.add("Expenses(-)         : " + expenses+"\n");
        if(Globals.objLPR.getproject_id().equals("cloud")){
            //mIPosPrinterService.PrintSpecFormatText("Accounts (Cash)(+)   :", "ST", 32, 1, callbackPPT8555);
       mylist.add("Accounts Cash(+)     :" + edt_cr_amt.getText().toString()+"\n");

       mylist.add("Return Cash(-)       :" + edt_posbal_returncash.getText().toString()+"\n");

        }
        else{

        }

        /*mIPosPrinterService.printSpecifiedTypeText("Credit Amount(+): " + edt_cr_amt.getText().toString().trim().toString(), "ST", 24, callbackPPT8555);*/
        mylist.add("--------------------------------------------\n");
        mylist.add("Net Cash            : " +edt_total.getText().toString().trim().toString()+"\n");
        mylist.add("--------------------------------------------\n");

        mylist.add("                "+"Sales Detail\n");

        String strTotalSales;


        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check+"\n");

        mylist.add("Total Sales  : " + strTotalSales+"\n" );

        String strTotal;
        int iPayCount = 1;
        while (iPayCount < arrPaymentAmount.size()) {
            strTotal = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(iPayCount)), decimal_check+"\n");
            mylist.add(arrPaymentName.get(iPayCount) + "    : " + strTotal+"\n");
            iPayCount++;
        }

//                        mIPosPrinterService.printSpecifiedTypeText("Total Return    : "+edt_totl_return.getText().toString().trim().toString(),"ST",24,callbackPPT8555);
        mylist.add("                "+"Sales Return\n");
        String retruncash = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check+"\n");
        mylist.add("Cash(+)                : " + retruncash+"\n");

        String retruncredit = Globals.myNumberFormat2Price(Double.parseDouble(edt_ret_cash.getText().toString()), decimal_check+"\n");
        mylist.add("Credit Amount(+)       : " + retruncredit+"\n");

        String retruntotalsale = Globals.myNumberFormat2Price(Double.parseDouble(edt_retntotal.getText().toString()), decimal_check+"\n");
        mylist.add("Total Return(+)        : " + retruntotalsale+"\n");

        mylist.add("\n");
        mylist.add("\n");
        mylist.add("\n");
        return mylist;
    }


    private ArrayList<String> getlistEx() {
        ArrayList<String> mylist = new ArrayList<String>();

        mylist.add("             " + Globals.objLPR.getCompany_Name() + "\n");
        mylist.add("             " + Globals.objLPR.getAddress() + "\n");
        mylist.add("             " + Globals.objLPR.getMobile_No() + "\n");
        try {
            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

            } else {
                mylist.add("             " + Globals.objLPR.getService_code_tariff() + "\n");

            }
        } catch (Exception ex) {
        }
        mylist.add("-----------------------------------------------\n");

        mylist.add("             " +"Expense Report\n");
        mylist.add("--------------------------------------------------\n");
        mylist.add("\nPOS Name    : " + Globals.objLPD.getDevice_Name());
        mylist.add("\nDate        : " + date);
        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        mylist.add("\n" + "Cashier     : " + Globals.objLPR.getLicense_No());
        mylist.add("\n-----------------------------------------------");
        mylist.add("\n  Expenses          Amount");
        mylist.add("\n-----------------------------------------------");
        int count = 0;
        String expenses="";
        arraylist_PB = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1' Order By remarks asc", database);
        while (count < arraylist_PB.size()) {
            String strRemarks = arraylist_PB.get(count).get_remarks();
            int len = 12;
            if (strRemarks.length() > len) {
                strRemarks = strRemarks.substring(0, len);
            } else {
                for (int sLen = strRemarks.length(); sLen < len; sLen++) {
                    strRemarks = strRemarks + " ";
                }
            }

            expenses = Globals.myNumberFormat2Price(Double.parseDouble(edt_expenses.getText().toString()), decimal_check+"\n");

            mylist.add("\n" + "  " + strRemarks + "       " + Globals.myNumberFormat2Price(Double.parseDouble(arraylist_PB.get(count).get_amount()), decimal_check));
         /*   amt = amt + Double.parseDouble(arraylist_PB.get(count).get_amount());
            totalAmount = Globals.myNumberFormat2Price(amt, decimal_check);*/
            count++;
        }
        String strTotalSales;


        strTotalSales = Globals.myNumberFormat2Price(Double.parseDouble(arrPaymentAmount.get(0)), decimal_check);

        mylist.add("Total Expenses    : " + expenses + "(" + Globals.objLPD.getCurreny_Symbol() + ")");

        mylist.add("\n-----------------------------------------------");
      //  mylist.add("\nTotal           : " + totalAmount);
        mylist.add("\n");
        mylist.add("\n");
        mylist.add("\n");
        return mylist;
    }

    private JSONObject getPrintObject(String test) {
        JSONObject json = new JSONObject();
        try {
            json.put("content-type", "txt");
            json.put("content", test);
            json.put("size", "1");
            json.put("position", "left");
            json.put("offset", "0");
            json.put("bold", "0");
            json.put("italic", "0");
            json.put("height", "-1");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject getPrintObject(String test, String size, String aline) {
        JSONObject json = new JSONObject();
        try {
            json.put("content-type", "txt");
            json.put("content", test);
            json.put("size", size);
            json.put("position", aline);
            json.put("offset", "0");
            json.put("bold", "0");
            json.put("italic", "0");
            json.put("height", "-1");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }


    class PrinterListener implements OnPrinterListener {
        private final String TAG = "Print";

        @Override
        public void onStart() {
            // TODO 打印开始
            // Print start
//            LOGD("start print");
        }

        @Override
        public void onFinish() {
            // TODO 打印结束
            // End of the print
//            LOGD("pint success");
            timeTools.stop();
//            LOGD("time cost：" + timeTools.getProcessTime());
        }

        @Override
        public void onError(int errorCode, String detail) {
            // TODO 打印出错
            // print error
//            LOGD("print error" + " errorcode = " + errorCode + " detail = " + detail);
            if (errorCode == PrinterBinder.PRINTER_ERROR_NO_PAPER) {
                Toast.makeText(X_ZActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(X_ZActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(X_ZActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void printerInit() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    mIPosPrinterService.printerInit(callbackPPT8555);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getPrinterStatus() {

        Log.i(TAG, "***** printerStatus" + printerStatus);
        try {
            printerStatus = mIPosPrinterService.getPrinterStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "#### printerStatus" + printerStatus);
        return printerStatus;
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
