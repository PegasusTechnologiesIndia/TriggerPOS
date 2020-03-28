package org.phomellolitepos.printer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.hoin.btsdk.BluetoothService;
import com.hoin.btsdk.PrintPic;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.xmp.impl.Utils;
import com.pos.sdk.printer.IPosPrinterService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import a1088sdk.PrnDspA1088Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.AboutActivity;
import org.phomellolitepos.AccountsListActivity;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.ItemListActivity;
import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.MainActivity;

import org.phomellolitepos.PayCollectionListActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.ReceptDetailActivity;
import org.phomellolitepos.RetailActivity;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bank;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Pay_Collection;
import org.phomellolitepos.database.Pay_Collection_Detail;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

@SuppressLint({"ShowToast", "DefaultLocale"})
public class PrintLayout extends PrnDspA1088Activity {
    private ArrayList<String> mylist = new ArrayList<String>();
    private ListView lv;
    private int reprint;
    private MyAdapter adp;
    private ProgressDialog dialog;
    private String strOrderNo = "";
    private String PrinterType = "";
    private Settings settings;
    private boolean iswifi = false;
    private int order, noofPrint = 0, lang = 0, pos = 0;
    private ArrayList<String> orderTypeArr;
    private int flag = 1;
    String decimal_check, qty_decimal_check;
    private IWoyouService woyouService;
    private TextView info;
    private byte[] inputCommand;
    private final int RUNNABLE_LENGHT = 11;
    private Random random = new Random();
    private ICallback callback = null;
    Database db;
    SQLiteDatabase database;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog progressDialog;
    LongOperation tsk;
    String strflag;
    User user;
    Item item;
    String id, opr, strOldCrAmt = "0";
    String ck_project_type;
    private static final String TAG = "PrinterTestDemo";
    private TimerCountTools timeTools;
    JSONObject printJson;
    private PrintLayout.PrinterListener printer_callback = new PrintLayout.PrinterListener();
    public static PrinterBinder printer;
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
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                //testAll();
                long mm = MemInfo.getmem_UNUSED(PrintLayout.this);
                if (mm < 100) {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 800);
                }
                Log.i(TAG, "testAll: " + printCount + " Memory: " + mm);
            }
        }
    };

    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
    private final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;
    /*打印机当前状态*/
    private int printerStatus = 0;

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
//    private final int MSG_TEST                               = 1;
    private final int MSG_IS_NORMAL = 2;
    private final int MSG_IS_BUSY = 3;
    private final int MSG_PAPER_LESS = 4;
    private final int MSG_PAPER_EXISTS = 5;
    private final int MSG_THP_HIGH_TEMP = 6;
    private final int MSG_THP_TEMP_NORMAL = 7;
    private final int MSG_MOTOR_HIGH_TEMP = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE = 10;

    /*循环打印类型*/
    private final int MULTI_THREAD_LOOP_PRINT = 1;
    private final int INPUT_CONTENT_LOOP_PRINT = 2;
    private final int DEMO_LOOP_PRINT = 3;
    private final int PRINT_DRIVER_ERROR_TEST = 4;
    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;
    private byte loopContent = 0x00;
    private int printDriverTestCount = 0;


    private com.iposprinter.iposprinterservice.IPosPrinterService mIPosPrinterService;
    private IPosPrinterCallback callbackPPT8555 = null;

    private HandlerUtils.MyHandler handlerPPT8555;

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
                    Toast.makeText(PrintLayout.this, "Printer Is Working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(PrintLayout.this, "Out Of Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(PrintLayout.this, "Exists Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(PrintLayout.this, "Printer High Temp Alarm", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
//                    Toast.makeText(PrintLayout.this, R.string.motor_high_temp_alarm, Toast.LENGTH_SHORT).show();
                    handlerPPT8555.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    printerInit();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
//                    Toast.makeText(PrintLayout.this, R.string.printer_current_task_print_complete, Toast.LENGTH_SHORT).show();
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
            mIPosPrinterService = com.iposprinter.iposprinterservice.IPosPrinterService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIPosPrinterService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_print_layout);

        handlerPPT8555 = new HandlerUtils.MyHandler(iHandlerIntent);
        callbackPPT8555 = new IPosPrinterCallback.Stub() {

            @Override
            public void onRunResult(final boolean isSuccess) throws RemoteException {
                Log.i(TAG,"result:" + isSuccess + "\n");
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG,"result:" + value + "\n");
            }
        };

        //绑定服务
        Intent intent=new Intent();
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
        registerReceiver(IPosPrinterStatusListener,printerStatusFilter);

        final Button b = (Button) findViewById(R.id.button1);


        intent = getIntent();
        id = intent.getStringExtra("id");
        opr = intent.getStringExtra("opr");
        if (opr == null || opr.equals("")) {
            opr = "other";
        }
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        lv = (ListView) findViewById(R.id.listView1);

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
        if (PrinterType.equals("7")) {
            try {
                ServiceManager.getInstence().init(PrintLayout.this);
            } catch (Exception ex) {
            }
        }


        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        if (settings.get_Home_Layout().equals("0")) {
            mService = MainActivity.mService;
        } else {
            mService = Main2Activity.mService;
        }


        strOrderNo = getIntent().getStringExtra("strOrderNo");
        strflag = getIntent().getStringExtra("flag");
        if (strflag == null) {
            strflag = "Add";
        }

        callback = new ICallback.Stub() {

            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "printlength:" + value + "\n");
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "onRaiseException: " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        Intent intent_1 = new Intent();
        intent_1.setPackage("woyou.aidlservice.jiuiv5");
        intent_1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_1);
        bindService(intent_1, connService, Context.BIND_AUTO_CREATE);

        final Orders orders = Orders.getOrders(PrintLayout.this, database, "WHERE order_code = '" + strOrderNo + "'");
        final ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(PrintLayout.this,
                "WHERE order_code = '" + strOrderNo + "'", database);

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "";
        }

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        ck_project_type = lite_pos_registration.getproject_id();

        mylist = getlist(orders, order_detail);
        if (mylist != null) {
            adp = new MyAdapter(getApplicationContext(), mylist);
        }
        lv.setAdapter(adp);

        try {
            tsk = new LongOperation();
            tsk.execute();
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
        } catch (Exception ex) {
        }

        if (settings.get_Is_Print_Dialog_Show().equals("true")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    b.performClick();
                }
            }, 500);

        }

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Print Wifi
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


                    if (strflag.equals("RDA")) {
                        Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);
                        finish();
                    } else if (opr.equals("PayCollection")) {
                        Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else if (PrinterType.equals("1")) {

                    mobile_pos(orders, order_detail);

                    if (strflag.equals("RDA")) {
                        Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);
                        finish();
                    } else if (opr.equals("PayCollection")) {
                        Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
//                    PrintLayout.this.finish();
                } else if (PrinterType.equals("7")) {
                    ppt8527(orders, order_detail);


                    if (strflag.equals("RDA")) {

                        Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);
                        finish();
                    } else if (opr.equals("PayCollection")) {
                        Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else if (PrinterType.equals("6")) {

                    PHAPOS(orders, order_detail);

                    if (strflag.equals("RDA")) {
                        Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);
                        finish();
                    } else if (opr.equals("PayCollection")) {
                        Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else if (PrinterType.equals("3")) {
                    String result = "";

                    result = bluetooth_56(orders, order_detail);

                    if (result.equals("1")) {
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                } else if (PrinterType.equals("4")) {
                    String result = "";
                    result = bluetooth_80(orders, order_detail);
                    if (result.equals("1")) {
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                } else if (PrinterType.equals("5")) {
                    String result = "";
                    result = bluetooth_100(orders, order_detail);
                    if (result.equals("1")) {
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth printer is not selected", Toast.LENGTH_SHORT).show();
                        if (strflag.equals("RDA")) {
                            Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                            intent.putExtra("order_code", strOrderNo);
                            startActivity(intent);
                            finish();
                        } else if (opr.equals("PayCollection")) {
                            Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (settings.get_Home_Layout().equals("2")) {
                                Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                } else if (PrinterType.equals("8")) {

                    ppt8555(orders, order_detail);

                    if (strflag.equals("RDA")) {
                        Intent intent = new Intent(PrintLayout.this, ReceptDetailActivity.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);
                        finish();
                    } else if (opr.equals("PayCollection")) {
                        Intent intent = new Intent(PrintLayout.this, PayCollectionListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PrintLayout.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
//                    PrintLayout.this.finish();
                } else {
                    Toast.makeText(PrintLayout.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ppt8555(final Orders orders, final ArrayList<Order_Detail> order_detail) {

        if (opr.equals("PayCollection")) {
            final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            Bitmap bitmap = StringToBitMap(settings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                            }

                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Receipt Voucher", "ST", 30, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintFontSize(26, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);


                            mIPosPrinterService.printColumnsText(new String[]{"Receipt No.", ":", pay_collection.get_collection_code()}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"Date", ":", pay_collection.get_collection_date()}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            String amount;
                            amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                            mIPosPrinterService.printColumnsText(new String[]{"Amount", ":", amount}, new int[]{11, 1, 40}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            String[] str = amount.split("\\.");
                            if (str.length == 1) {
                                mIPosPrinterService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            } else if (str.length == 2) {
                                if (Integer.parseInt(str[1].toString()) == 0) {
                                    mIPosPrinterService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, 0, callbackPPT8555);

                                } else {
                                    mIPosPrinterService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                                }

                            }

                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                            mIPosPrinterService.printColumnsText(new String[]{"Received From", ":", contact.get_name()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"Cash/Cheque", ":", pay_collection.get_payment_mode()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"On Account", ":", pay_collection.get_on_account()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"Remarks", ":", pay_collection.get_remarks()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);

                            if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                                Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                                mIPosPrinterService.printColumnsText(new String[]{"Bank Name", ":", bank.get_bank_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                                mIPosPrinterService.printColumnsText(new String[]{"Cheque No", ":", pay_collection.get_ref_no()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);

                            }

                            ArrayList<Pay_Collection_Detail> pay_collection_detail = Pay_Collection_Detail.getAllPay_Collection_Detail(getApplicationContext(), " WHERE collection_code='" + pay_collection.get_collection_code() + "'");
                            if (pay_collection_detail.size() > 0) {
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printColumnsText(new String[]{"Order No", "Amount"}, new int[]{13, 13}, new int[]{0, 0}, 0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                for (int i = 0; i < pay_collection_detail.size(); i++) {
                                    mIPosPrinterService.printColumnsText(new String[]{pay_collection_detail.get(i).get_invoice_no(), Globals.myNumberFormat2QtyDecimal(Double.parseDouble(pay_collection_detail.get(i).get_amount()), qty_decimal_check)}, new int[]{13, 13}, new int[]{0, 0}, 0, callbackPPT8555);
                                }
                            }
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            mIPosPrinterService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0},0, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Receiver Signature", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {

            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            mIPosPrinterService.setPrinterPrintFontSize(35, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("RECEIPT \n", "ST", 35, callbackPPT8555);
                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 35, callbackPPT8555);
                                }
                            } catch (Exception ex) {

                            }
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 35, 1,callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 35,1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 35,1, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);

                            mIPosPrinterService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
//                            woyouService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            mIPosPrinterService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"RECEIPT NO.", ":", strOrderNo}, new int[]{12, 1, 16}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBarCode(strOrderNo, 8, 80, 150, 0, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            }
                            mIPosPrinterService.printColumnsText(new String[]{"IN DT", ":", DateUtill.PaternDatePrintDate(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.printColumnsText(new String[]{"IN TM", ":", DateUtill.PaternDatePrintTime(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();

                                String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_Code");
                                int len = 12;
                                if (strItemName.length() > len) {
                                    strItemName = strItemName.substring(0, len);
                                } else {
                                    for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                        strItemName = strItemName + " ";
                                    }
                                }

                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);
                                //sale_price = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check);
                                String line_total;
                                mIPosPrinterService.printSpecifiedTypeText("VEHICLE TYPE: \n", "ST", 30, callbackPPT8555);
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                mIPosPrinterService.printColumnsText(new String[]{strItemName, " Rs." + sale_price}, new int[]{11, 9}, new int[]{0, 0}, 0, callbackPPT8555);

                                count++;
                            }

                            mIPosPrinterService.printColumnsText(new String[]{"V.NO : ", orders.get_remarks()}, new int[]{8, 20}, new int[]{0, 0}, 0, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);

                            if (!settings.get_Footer_Text().equals("")) {
                                mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text() + "\n", "ST", 35, callbackPPT8555);
                            }
                            mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 35, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            if (decimal_check.equals("2")) {
                if (settings.get_Print_Lang().equals("0")){
                    if (settings.get_Print_Memo().equals("1")) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                                    String Print_type = "0";

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("Order : " + orders.get_order_code() + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    if (orders.get_table_code().equals("")) {
                                    } else {
                                        mIPosPrinterService.printSpecifiedTypeText("Table : "+orders.get_table_code(),"ST",24, callbackPPT8555);
                                    }

                                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                                    if (order_detail.size() > 0) {
                                        Double total = 0d;

                                        mIPosPrinterService.printSpecifiedTypeText("Name         Qty   Price  Total","ST",24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        for (int i = 0; i < order_detail.size(); i++) {
                                            total = total + Double.parseDouble(order_detail.get(i).get_line_total());
                                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                                            mIPosPrinterService.printSpecifiedTypeText(item.get_item_name().substring(0, 8)+"X"+order_detail.get(i).get_quantity()+"     "+Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_sale_price()), decimal_check),"ST",24, callbackPPT8555);

                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total Amount : "+Globals.myNumberFormat2Price(total, decimal_check),"ST",24, callbackPPT8555);
                                    }

                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                        String Print_type = "0";
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                        if (bitmap != null) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                            bitmap = getResizedBitmap(bitmap, 80, 120);
                                            mIPosPrinterService.printBitmap(1, 12, bitmap, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1,callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24,1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24,1, callbackPPT8555);
                                        try {
                                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } catch (Exception ex) {}
                                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo+":"+ Globals.objLPR.getLicense_No(),"ST",24,callbackPPT8555);
                                        }
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintOrder + "\n", "ST", 24, callbackPPT8555);
                                        ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (order_payment_array.size() > 0) {

                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                arrayList.add(order_payment_array.get(i).get_payment_id());
                                            }

                                            if (arrayList.contains("1") && arrayList.contains("5")) {
                                                mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit/Cash\n", "ST", 24, callbackPPT8555);
                                            } else {
                                                if (arrayList.contains("5")) {
                                                    mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit\n", "ST", 24, callbackPPT8555);
                                                } else if (arrayList.contains("1")) {
                                                    mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Cash\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (Globals.strIsBarcodePrint.equals("true")) {
                                            mIPosPrinterService.printBarCode(strOrderNo, 8, 60, 120, 0, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintInvNo+" : " + strOrderNo + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintInvDate+" : " + DateUtill.PaternDate1(orders.get_order_date()) + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID+" : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintCashier+" : " + user.get_name() + "\n", "ST", 24, callbackPPT8555);
                                        if (Globals.ModeResrv.equals("Resv")) {
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                            mIPosPrinterService.printSpecifiedTypeText("Customer : " + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                            if (contact.get_gstin().length() > 0) {
                                                mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } else {
                                            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                            } else {
                                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                                mIPosPrinterService.printSpecifiedTypeText("Customer : " + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                                if (contact.get_gstin().length() > 0) {
                                                    mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);

                                                }
                                            }
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "", 24, callbackPPT8555);
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        int count = 0;
                                        Double itemFinalTax = 0d;
                                        while (count < order_detail.size()) {

                                            String strItemCode = order_detail.get(count).get_item_code();

                                            String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                                    + strItemCode + "'  GROUP By order_detail.item_Code");

                                            String sale_price;
                                            Double dDisAfterSalePrice = 0d;
                                            Double dDisAfter = 0d;
                                            dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                            dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                            String line_total;
                                            line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                            mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);

                                            Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                            String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                            mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check)+ "         "+sale_price+"      "+ line_total + "\n", "ST", 24, callbackPPT8555);
                                            String discnt = "";
                                            String disLbl = "";
                                            if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                            } else {
                                                if (Globals.strIsDiscountPrint.equals("true")) {
                                                    Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                                    discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                                    disLbl = "Dis :";
                                                }
                                                mIPosPrinterService.printSpecifiedTypeText(disLbl+" : " + discnt + "\n", "ST", 24, callbackPPT8555);
                                            }

                                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                            if (settings.get_HSN_print().equals("true")) {
                                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText("HSN Code :" + item.get_hsn_sac_code() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                                Tax_Master tax_master = null;
                                                ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                                for (int i = 0; i < order_detail_tax.size(); i++) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                                    itemFinalTax += valueFinal;
                                                    mIPosPrinterService.printSpecifiedTypeText(tax_master.get_tax_name()+" :" + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            count++;
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total Item    :" +  orders.get_total_item() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Item Quantity :" + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                        String subtotal = "";
                                        String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                        Cursor cursor1 = database.rawQuery(strTableQry, null);
                                        Tax_Master tax_master;
                                        while (cursor1.moveToNext()) {
                                            subtotal = cursor1.getString(0);
                                        }

                                        subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                        mIPosPrinterService.printSpecifiedTypeText("Subtotal      :" + subtotal + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                            strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                                    "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                                    "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText( tax_master.get_tax_name()+" : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        String discount = "0";
                                        if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                        } else {
                                            discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                            if (Globals.strIsDiscountPrint.equals("true")) {
                                                mIPosPrinterService.printSpecifiedTypeText( "Discount : " + Globals.DiscountPer + "%" + "\n", "ST", 24, callbackPPT8555);
                                                mIPosPrinterService.printSpecifiedTypeText( "Discount : " + discount + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        }
                                        String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                        String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Total : " + tatalAftrDis + "\n", "ST", 24, callbackPPT8555);
                                        String total_tax;
                                        total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                        if (Double.parseDouble(total_tax) > 0d) {
                                            mIPosPrinterService.printSpecifiedTypeText( "Total Tax : " + total_tax + "\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                            Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                            String name = "", value = "";
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    name = cursor.getString(0);
                                                    value = cursor.getString(1);
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + "\n", "ST", 32, callbackPPT8555);
                                                } while (cursor.moveToNext());
                                            }
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        String net_amount;
                                        net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                        String strCurrency;
                                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                            strCurrency = "";
                                        } else {
                                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText( "Net Amt : " + net_amount + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String tender;
                                        tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Tender  : " + tender + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String change_amount;
                                        change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Change  : " + change_amount+ "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (order_payment_array.size() > 0) {
                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                                String name = "";
                                                if (payment != null) {
                                                    name = payment.get_payment_name();
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)+ "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        if (settings.get_Is_Accounts().equals("true")) {
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    Double showAmount = 0d;
//
                                                    try {
                                                        String strCreditAmt = "", strDeditAmount = "";
                                                        Double creditAmount = 0d,
                                                                debitAmount = 0d;
                                                        Cursor cursor = null;

                                                        String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                        cursor = database.rawQuery(strQry1, null);
                                                        while (cursor.moveToNext()) {
                                                            strCreditAmt = cursor.getString(0);

                                                        }
                                                        creditAmount = Double.parseDouble(strCreditAmt);

                                                        String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                        cursor = database.rawQuery(strQry2, null);
                                                        while (cursor.moveToNext()) {
                                                            strDeditAmount = cursor.getString(0);

                                                        }
                                                        debitAmount = Double.parseDouble(strDeditAmount);
                                                        showAmount = debitAmount + creditAmount;
                                                    } catch (Exception ex) {
                                                    }
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"DR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = cursor1.getString(0);

                                                        }
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    Double strBalance = abs1 - Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();

                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    String curAmount = "";
                                                    try {
                                                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);
                                                        if (cursor1.moveToFirst()) {
                                                            do {
                                                                curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                            } while (cursor1.moveToNext());
                                                        }
                                                    } catch (Exception ex) {
                                                        curAmount = "0";
                                                    }
                                                    double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR", "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {}
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        }

                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }

                                                    Double strBalance = ab + Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        if (!settings.get_Footer_Text().equals("")) {
                                            mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        bitmapRecycle(bitmap);
                                        mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                    }
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Globals.DiscountPer = "";
                                    Globals.strOldCrAmt = "0";
                                    Globals.setEmpty();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }else if (settings.get_Print_Lang().equals("1")){
                    if (settings.get_Print_Memo().equals("1")) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                                    String Print_type = "0";

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("Order : " + orders.get_order_code() + "\n", "ST", 24,1, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    if (orders.get_table_code().equals("")) {
                                    } else {
                                        mIPosPrinterService.printSpecifiedTypeText("Table : "+orders.get_table_code(),"ST",24, callbackPPT8555);
                                    }

                                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                                    if (order_detail.size() > 0) {
                                        Double total = 0d;

                                        mIPosPrinterService.printSpecifiedTypeText("Name         Qty   Price  Total","ST",24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        for (int i = 0; i < order_detail.size(); i++) {
                                            total = total + Double.parseDouble(order_detail.get(i).get_line_total());
                                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                                            mIPosPrinterService.printSpecifiedTypeText(item.get_item_name().substring(0, 8)+"X"+order_detail.get(i).get_quantity()+"     "+Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_sale_price()), decimal_check),"ST",24, callbackPPT8555);

                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total Amount : "+Globals.myNumberFormat2Price(total, decimal_check),"ST",24, callbackPPT8555);
                                    }

                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                        String strPOSNo = "Device ID/لحم." + " ";
                                        String strGSTNo = Globals.GSTLbl + " ";
                                        String strOrderNum = "Invoice Number/رقم الطلب" + "  ";
                                        String strOrderDate = "Invoice Date/تاريخ الطلب" + "  ";
                                        String strCashier = "Salesperson/أمين الصندوق" + " ";
                                        String Print_type = "0";
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                        if (bitmap != null) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                            bitmap = getResizedBitmap(bitmap, 80, 120);
                                            mIPosPrinterService.printBitmap(1, 12, bitmap, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1,callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24,1, callbackPPT8555);
                                        try {
                                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } catch (Exception ex) {}
                                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(strGSTNo,"ST",24,callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText(Globals.objLPR.getLicense_No(),"ST",24,callbackPPT8555);
                                        }
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintOrder + "\n", "ST", 24, callbackPPT8555);
                                        ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (order_payment_array.size() > 0) {

                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                arrayList.add(order_payment_array.get(i).get_payment_id());
                                            }

                                            if (arrayList.contains("1") && arrayList.contains("5")) {
                                                mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: الائتمان / النقدية\n", "ST", 24, callbackPPT8555);
                                            } else {
                                                if (arrayList.contains("5")) {

                                                    mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: الائتمان / النقدية\n", "ST", 24, callbackPPT8555);
                                                } else if (arrayList.contains("1")) {

                                                    mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: النقدية\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (Globals.strIsBarcodePrint.equals("true")) {
                                            mIPosPrinterService.printBarCode(strOrderNo, 8, 60, 120, 0, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText(strOrderNum + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(strOrderNo + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(strOrderDate + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(DateUtill.PaternDate1(orders.get_order_date()) + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID+" : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                        mIPosPrinterService.printSpecifiedTypeText(strCashier + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(user.get_name() + "\n", "ST", 24, callbackPPT8555);
                                        if (Globals.ModeResrv.equals("Resv")) {
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                            mIPosPrinterService.printSpecifiedTypeText(contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                            if (contact.get_gstin().length() > 0) {
                                                //mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } else {
                                            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                            } else {
                                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                                mIPosPrinterService.printSpecifiedTypeText(contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                                if (contact.get_gstin().length() > 0) {
                                                    //mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("اسم العنصر\n", "", 24, callbackPPT8555);
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        int count = 0;
                                        Double itemFinalTax = 0d;
                                        while (count < order_detail.size()) {

                                            String strItemCode = order_detail.get(count).get_item_code();

                                            String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                                    + strItemCode + "'  GROUP By order_detail.item_Code");

                                            String sale_price;
                                            Double dDisAfterSalePrice = 0d;
                                            Double dDisAfter = 0d;
                                            dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                            dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                            String line_total;
                                            line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                            mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);

                                            Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                            String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                            mIPosPrinterService.printSpecifiedTypeText(line_total+ "         "+sale_price+"      "+ Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                            String discnt = "";
                                            String disLbl = "";
                                            if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                            } else {
                                                if (Globals.strIsDiscountPrint.equals("true")) {
                                                    Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                                    discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                                    disLbl = "Dis :";
                                                }
                                                mIPosPrinterService.printSpecifiedTypeText(disLbl+" : " + discnt + "\n", "ST", 24, callbackPPT8555);
                                            }

                                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                            if (settings.get_HSN_print().equals("true")) {
                                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText("HSN Code :" + item.get_hsn_sac_code() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                                Tax_Master tax_master = null;
                                                ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                                for (int i = 0; i < order_detail_tax.size(); i++) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                                    itemFinalTax += valueFinal;
                                                    mIPosPrinterService.printSpecifiedTypeText(tax_master.get_tax_name()+" :" + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            count++;
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("مجموع البند:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(orders.get_total_item() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("البند الكمية:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                        String subtotal = "";
                                        String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                        Cursor cursor1 = database.rawQuery(strTableQry, null);
                                        Tax_Master tax_master;
                                        while (cursor1.moveToNext()) {
                                            subtotal = cursor1.getString(0);
                                        }

                                        subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                        mIPosPrinterService.printSpecifiedTypeText("المجموع الفرعي:", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(subtotal + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                            strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                                    "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                                    "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText( tax_master.get_tax_name()+" : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        String discount = "0";
                                        if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                        } else {
                                            discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                            if (Globals.strIsDiscountPrint.equals("true")) {
                                                mIPosPrinterService.printSpecifiedTypeText( "خصم" + Globals.DiscountPer + "%" + "\n", "ST", 24, callbackPPT8555);
                                                mIPosPrinterService.printSpecifiedTypeText( discount + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        }
                                        String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                        String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "مجموع:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( tatalAftrDis + "\n", "ST", 24, callbackPPT8555);
                                        String total_tax;
                                        total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                        if (Double.parseDouble(total_tax) > 0d) {
                                            mIPosPrinterService.printSpecifiedTypeText( "مجموع الضريبة:\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText( total_tax + "\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                            Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                            String name = "", value = "";
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    name = cursor.getString(0);
                                                    value = cursor.getString(1);
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + "\n", "ST", 32, callbackPPT8555);
                                                } while (cursor.moveToNext());
                                            }
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        String net_amount;
                                        net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                        String strCurrency;
                                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                            strCurrency = "";
                                        } else {
                                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText( "ليس مكتب:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( net_amount + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String tender;
                                        tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "مناقصة:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( tender + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String change_amount;
                                        change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "يتغيرون:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( change_amount+ "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (order_payment_array.size() > 0) {
                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                                String name = "";
                                                if (payment != null) {
                                                    name = payment.get_payment_name();
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)+ "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        if (settings.get_Is_Accounts().equals("true")) {
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    Double showAmount = 0d;
//
                                                    try {
                                                        String strCreditAmt = "", strDeditAmount = "";
                                                        Double creditAmount = 0d,
                                                                debitAmount = 0d;
                                                        Cursor cursor = null;

                                                        String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                        cursor = database.rawQuery(strQry1, null);
                                                        while (cursor.moveToNext()) {
                                                            strCreditAmt = cursor.getString(0);

                                                        }
                                                        creditAmount = Double.parseDouble(strCreditAmt);

                                                        String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                        cursor = database.rawQuery(strQry2, null);
                                                        while (cursor.moveToNext()) {
                                                            strDeditAmount = cursor.getString(0);

                                                        }
                                                        debitAmount = Double.parseDouble(strDeditAmount);
                                                        showAmount = debitAmount + creditAmount;
                                                    } catch (Exception ex) {
                                                    }
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"DR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = cursor1.getString(0);

                                                        }
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    Double strBalance = abs1 - Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();

                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    String curAmount = "";
                                                    try {
                                                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);
                                                        if (cursor1.moveToFirst()) {
                                                            do {
                                                                curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                            } while (cursor1.moveToNext());
                                                        }
                                                    } catch (Exception ex) {
                                                        curAmount = "0";
                                                    }
                                                    double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR", "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {}
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        }

                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }

                                                    Double strBalance = ab + Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        if (!settings.get_Footer_Text().equals("")) {
                                            mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        bitmapRecycle(bitmap);
                                        mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                    }
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Globals.DiscountPer = "";
                                    Globals.strOldCrAmt = "0";
                                    Globals.setEmpty();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else {
                    if (settings.get_Print_Memo().equals("1")) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                                    String Print_type = "0";

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("Order : " + orders.get_order_code() + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    if (orders.get_table_code().equals("")) {
                                    } else {
                                        mIPosPrinterService.printSpecifiedTypeText("Table : "+orders.get_table_code(),"ST",24, callbackPPT8555);
                                    }

                                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                                    if (order_detail.size() > 0) {
                                        Double total = 0d;

                                        mIPosPrinterService.printSpecifiedTypeText("Name         Qty   Price  Total","ST",24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        for (int i = 0; i < order_detail.size(); i++) {
                                            total = total + Double.parseDouble(order_detail.get(i).get_line_total());
                                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                                            mIPosPrinterService.printSpecifiedTypeText(item.get_item_name().substring(0, 8)+"X"+order_detail.get(i).get_quantity()+"     "+Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_sale_price()), decimal_check),"ST",24, callbackPPT8555);

                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total Amount : "+Globals.myNumberFormat2Price(total, decimal_check),"ST",24, callbackPPT8555);
                                    }

                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                        String strPOSNo = "Device ID/لحم." + " ";
                                        String strGSTNo = Globals.GSTLbl + " ";
                                        String strOrderNum = "Invoice Number/رقم الطلب" + "  ";
                                        String strOrderDate = "Invoice Date/تاريخ الطلب" + "  ";
                                        String strCashier = "Salesperson/أمين الصندوق" + " ";
                                        String Print_type = "0";
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                        if (bitmap != null) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                            bitmap = getResizedBitmap(bitmap, 80, 120);
                                            mIPosPrinterService.printBitmap(1, 12, bitmap, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24,1, callbackPPT8555);
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1,callbackPPT8555);
                                        try {
                                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } catch (Exception ex) {}
                                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(strGSTNo,"ST",24,callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText(Globals.objLPR.getLicense_No(),"ST",24,callbackPPT8555);
                                        }
                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintOrder + "\n", "ST", 24, callbackPPT8555);
                                        ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (order_payment_array.size() > 0) {

                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                arrayList.add(order_payment_array.get(i).get_payment_id());
                                            }

                                            if (arrayList.contains("1") && arrayList.contains("5")) {
                                                mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit/Cash\n", "ST", 24, callbackPPT8555);
                                                mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: الائتمان / النقدية\n", "ST", 24, callbackPPT8555);
                                            } else {
                                                if (arrayList.contains("5")) {
                                                    mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit/Cash\n", "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: الائتمان / النقدية\n", "ST", 24, callbackPPT8555);
                                                } else if (arrayList.contains("1")) {
                                                    mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Cash\n", "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("نوع الفاتورة: النقدية\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        if (Globals.strIsBarcodePrint.equals("true")) {
                                            mIPosPrinterService.printBarCode(strOrderNo, 8, 60, 120, 0, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText(strOrderNum + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(strOrderNo + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(strOrderDate + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(DateUtill.PaternDate1(orders.get_order_date()) + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID+" : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                        mIPosPrinterService.printSpecifiedTypeText(strCashier + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(user.get_name() + "\n", "ST", 24, callbackPPT8555);
                                        if (Globals.ModeResrv.equals("Resv")) {
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                            mIPosPrinterService.printSpecifiedTypeText("Customer/زبون\n" + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText(contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                            if (contact.get_gstin().length() > 0) {
                                                //mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        } else {
                                            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                            } else {
                                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                                mIPosPrinterService.printSpecifiedTypeText("Customer/زبون\n" + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                                mIPosPrinterService.printSpecifiedTypeText(contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                                if (contact.get_gstin().length() > 0) {
                                                    //mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("اسم العنصر\n", "", 24, callbackPPT8555);
                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total       Price        Qty\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        int count = 0;
                                        Double itemFinalTax = 0d;
                                        while (count < order_detail.size()) {

                                            String strItemCode = order_detail.get(count).get_item_code();

                                            String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                                    + strItemCode + "'  GROUP By order_detail.item_Code");

                                            String sale_price;
                                            Double dDisAfterSalePrice = 0d;
                                            Double dDisAfter = 0d;
                                            dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                            dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                            String line_total;
                                            line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                            mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);

                                            Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                            String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                            mIPosPrinterService.printSpecifiedTypeText(line_total+ "         "+sale_price+"      "+ Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                            String discnt = "";
                                            String disLbl = "";
                                            if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                            } else {
                                                if (Globals.strIsDiscountPrint.equals("true")) {
                                                    Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                                    discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                                    disLbl = "Dis :";
                                                }
                                                mIPosPrinterService.printSpecifiedTypeText(disLbl+" : " + discnt + "\n", "ST", 24, callbackPPT8555);
                                            }

                                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                            if (settings.get_HSN_print().equals("true")) {
                                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText("HSN Code :" + item.get_hsn_sac_code() + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                                Tax_Master tax_master = null;
                                                ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                                for (int i = 0; i < order_detail_tax.size(); i++) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                                    itemFinalTax += valueFinal;
                                                    mIPosPrinterService.printSpecifiedTypeText(tax_master.get_tax_name()+" :" + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            count++;
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Total Item/مجموع البند:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(orders.get_total_item() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("Item Quantity/البند الكمية:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                        String subtotal = "";
                                        String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                        Cursor cursor1 = database.rawQuery(strTableQry, null);
                                        Tax_Master tax_master;
                                        while (cursor1.moveToNext()) {
                                            subtotal = cursor1.getString(0);
                                        }

                                        subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                        mIPosPrinterService.printSpecifiedTypeText("Subtotal/المجموع الفرعي:", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(subtotal + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                            strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                                    "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                                    "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                mIPosPrinterService.printSpecifiedTypeText( tax_master.get_tax_name()+" : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        String discount = "0";
                                        if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                        } else {
                                            discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                            if (Globals.strIsDiscountPrint.equals("true")) {
                                                mIPosPrinterService.printSpecifiedTypeText( "Discount/خصم" + Globals.DiscountPer + "%" + "\n", "ST", 24, callbackPPT8555);
                                                mIPosPrinterService.printSpecifiedTypeText( discount + "\n", "ST", 24, callbackPPT8555);
                                            }
                                        }
                                        String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                        String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Total/مجموع:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( tatalAftrDis + "\n", "ST", 24, callbackPPT8555);
                                        String total_tax;
                                        total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                        if (Double.parseDouble(total_tax) > 0d) {
                                            mIPosPrinterService.printSpecifiedTypeText( "Total Tax/مجموع الضريبة:\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText( total_tax + "\n", "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                            Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                            String name = "", value = "";
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    name = cursor.getString(0);
                                                    value = cursor.getString(1);
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + "\n", "ST", 32, callbackPPT8555);
                                                } while (cursor.moveToNext());
                                            }
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        String net_amount;
                                        net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                        String strCurrency;
                                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                            strCurrency = "";
                                        } else {
                                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText( "Net Amt/ليس مكتب:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( net_amount + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String tender;
                                        tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Tender/مناقصة:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( tender + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                        String change_amount;
                                        change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                        mIPosPrinterService.printSpecifiedTypeText( "Change/يتغيرون:\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( change_amount+ "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (order_payment_array.size() > 0) {
                                            for (int i = 0; i < order_payment_array.size(); i++) {
                                                Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                                String name = "";
                                                if (payment != null) {
                                                    name = payment.get_payment_name();
                                                    mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)+ "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }

                                        if (settings.get_Is_Accounts().equals("true")) {
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    Double showAmount = 0d;
//
                                                    try {
                                                        String strCreditAmt = "", strDeditAmount = "";
                                                        Double creditAmount = 0d,
                                                                debitAmount = 0d;
                                                        Cursor cursor = null;

                                                        String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                        cursor = database.rawQuery(strQry1, null);
                                                        while (cursor.moveToNext()) {
                                                            strCreditAmt = cursor.getString(0);

                                                        }
                                                        creditAmount = Double.parseDouble(strCreditAmt);

                                                        String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                        cursor = database.rawQuery(strQry2, null);
                                                        while (cursor.moveToNext()) {
                                                            strDeditAmount = cursor.getString(0);

                                                        }
                                                        debitAmount = Double.parseDouble(strDeditAmount);
                                                        showAmount = debitAmount + creditAmount;
                                                    } catch (Exception ex) {
                                                    }
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"DR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = cursor1.getString(0);

                                                        }
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    Double strBalance = abs1 - Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();

                                                if (Globals.strContact_Code.equals("")) {
                                                    mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                                } else {
                                                    String curAmount = "";
                                                    try {
                                                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);
                                                        if (cursor1.moveToFirst()) {
                                                            do {
                                                                curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                            } while (cursor1.moveToNext());
                                                        }
                                                    } catch (Exception ex) {
                                                        curAmount = "0";
                                                    }
                                                    double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR", "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {}
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        }

                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    } else {
                                                        mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }

                                                    Double strBalance = ab + Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        if (!settings.get_Footer_Text().equals("")) {
                                            mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        }

                                        mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                        bitmapRecycle(bitmap);
                                        mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                                    }
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Globals.DiscountPer = "";
                                    Globals.strOldCrAmt = "0";
                                    Globals.setEmpty();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }



            } else {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                String Print_type = "0";
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 12, bitmap, callbackPPT8555);
                                }

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24,1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24,1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1,callbackPPT8555);
                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {}
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo+":"+ Globals.objLPR.getLicense_No(),"ST",24,callbackPPT8555);
                                }
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintOrder + "\n", "ST", 24, callbackPPT8555);
                                ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                ArrayList<String> arrayList = new ArrayList<String>();
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                if (order_payment_array.size() > 0) {

                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        arrayList.add(order_payment_array.get(i).get_payment_id());
                                    }

                                    if (arrayList.contains("1") && arrayList.contains("5")) {
                                        mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit/Cash\n", "ST", 24, callbackPPT8555);
                                    } else {
                                        if (arrayList.contains("5")) {
                                            mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Credit\n", "ST", 24, callbackPPT8555);
                                        } else if (arrayList.contains("1")) {
                                            mIPosPrinterService.printSpecifiedTypeText("Invoice Type: Cash\n", "ST", 24, callbackPPT8555);
                                        }
                                    }
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(strOrderNo, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintInvNo+" : " + strOrderNo + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintInvDate+" : " + DateUtill.PaternDate1(orders.get_order_date()) + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID+" : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintCashier+" : " + user.get_name() + "\n", "ST", 24, callbackPPT8555);
                                if (Globals.ModeResrv.equals("Resv")) {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                    mIPosPrinterService.printSpecifiedTypeText("Customer : " + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                    if (contact.get_gstin().length() > 0) {
                                        mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } else {
                                    if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                    } else {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                        mIPosPrinterService.printSpecifiedTypeText("Customer : " + contact.get_name() + "\n", "ST", 24, callbackPPT8555);
                                        if (contact.get_gstin().length() > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText("Customer GST No. : " + contact.get_gstin() + "\n", "ST", 24, callbackPPT8555);

                                        }
                                    }
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                Double itemFinalTax = 0d;
                                while (count < order_detail.size()) {

                                    String strItemCode = order_detail.get(count).get_item_code();

                                    String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By order_detail.item_Code");

                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;
                                    Double dDisAfter = 0d;
                                    dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                    dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);

                                    Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                    String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check)+ "         "+sale_price+"    "+ line_total + "\n", "ST", 24, callbackPPT8555);
                                    String discnt = "";
                                    String disLbl = "";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                    } else {
                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                            discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                            disLbl = "Dis :";
                                        }
                                        mIPosPrinterService.printSpecifiedTypeText(disLbl+" : " + discnt + "\n", "ST", 24, callbackPPT8555);
                                    }

                                    mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                    if (settings.get_HSN_print().equals("true")) {
                                        item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                        mIPosPrinterService.printSpecifiedTypeText("HSN Code :" + item.get_hsn_sac_code() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                    if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                        Tax_Master tax_master = null;
                                        ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                        for (int i = 0; i < order_detail_tax.size(); i++) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                            double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                            itemFinalTax += valueFinal;
                                            mIPosPrinterService.printSpecifiedTypeText(tax_master.get_tax_name()+" :" + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                        }
                                    }
                                    count++;
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Total Item :" +  orders.get_total_item() + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Quantity :" + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                String subtotal = "";
                                String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                Cursor cursor1 = database.rawQuery(strTableQry, null);
                                Tax_Master tax_master;
                                while (cursor1.moveToNext()) {
                                    subtotal = cursor1.getString(0);
                                }

                                subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                mIPosPrinterService.printSpecifiedTypeText("Subtotal :" + subtotal + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                    strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                            "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                            "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                        mIPosPrinterService.printSpecifiedTypeText( tax_master.get_tax_name()+" : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                    }
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                }

                                String discount = "0";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                } else {
                                    discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        mIPosPrinterService.printSpecifiedTypeText( "Discount : " + Globals.DiscountPer + "%" + "\n", "ST", 24, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText( "Discount : " + discount + "\n", "ST", 24, callbackPPT8555);
                                    }
                                }
                                String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText( "Total : " + tatalAftrDis + "\n", "ST", 24, callbackPPT8555);
                                String total_tax;
                                total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                if (Double.parseDouble(total_tax) > 0d) {
                                    mIPosPrinterService.printSpecifiedTypeText( "Total Tax : " + total_tax + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                    String name = "", value = "";
                                    if (cursor.moveToFirst()) {
                                        do {
                                            name = cursor.getString(0);
                                            value = cursor.getString(1);
                                            mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + "\n", "ST", 32, callbackPPT8555);
                                        } while (cursor.moveToNext());
                                    }
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText( "Net Amt : " + net_amount + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                String tender;
                                tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText( "Tender : " + tender + "" + strCurrency + "\n", "ST", 24, callbackPPT8555);
                                String change_amount;
                                change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText( "Change : " + change_amount+ "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (order_payment_array.size() > 0) {
                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                        String name = "";
                                        if (payment != null) {
                                            name = payment.get_payment_name();
                                            mIPosPrinterService.printSpecifiedTypeText( name+" : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)+ "\n", "ST", 24, callbackPPT8555);
                                        }
                                    }
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                }

                                if (settings.get_Is_Accounts().equals("true")) {
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                        } else {
                                            Double showAmount = 0d;
//
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
                                            }
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"DR", "ST", 24, callbackPPT8555);
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = cursor1.getString(0);

                                                }
                                                mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "ST", 24, callbackPPT8555);
                                            }

                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();

                                        if (Globals.strContact_Code.equals("")) {
                                            mIPosPrinterService.printSpecifiedTypeText( "**", "ST", 24, callbackPPT8555);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }
                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+"CR", "ST", 24, callbackPPT8555);
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText( "Old Amt :"+Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR", "ST", 24, callbackPPT8555);
                                            }

                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {}
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }

                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                            } else {
                                                mIPosPrinterService.printSpecifiedTypeText( "Current Amt :"+strCur, "ST", 24, callbackPPT8555);
                                            }

                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = ab + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            mIPosPrinterService.printSpecifiedTypeText( "Balance Amt :"+Globals.myNumberFormat2Price(strBalance, decimal_check), "ST", 24, callbackPPT8555);
                                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                        }
                                    }
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                if (!settings.get_Footer_Text().equals("")) {
                                    mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                bitmapRecycle(bitmap);
                                mIPosPrinterService.printerPerformPrint(160,  callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private String bluetooth_100(Orders orders, ArrayList<Order_Detail> order_detail) {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);

        final byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if (opr.equals("PayCollection")) {

            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);

                    //printImage();
                    mService.sendMessage("Receipt Voucher", "GBK");
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage("Receipt No. :" + pay_collection.get_collection_code(), "GBK");
                    mService.sendMessage("Date  :" + pay_collection.get_collection_date(), "GBK");
                    String amount;
                    amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                    mService.sendMessage("Amount :" + amount, "GBK");

                    String[] str = amount.split("\\.");
                    if (str.length == 1) {
                        mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " Only ", "GBK");
                    } else if (str.length == 2) {
                        if (Integer.parseInt(str[1].toString()) == 0) {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only ", "GBK");

                        } else {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only ", "GBK");
                        }

                    }

                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                    mService.sendMessage("Received From :" + contact.get_name(), "GBK");
                    mService.sendMessage("Cash/Cheque   :" + pay_collection.get_payment_mode(), "GBK");

                    if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                        Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                        mService.sendMessage("Bank Name  :" + bank.get_bank_name(), "GBK");
                        mService.sendMessage("Cheque No  :" + pay_collection.get_ref_no(), "GBK");

                    }

                    mService.sendMessage("On Account  :" + pay_collection.get_on_account(), "GBK");


                    mService.sendMessage("Remarks  :" + pay_collection.get_remarks(), "GBK");

                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    mService.sendMessage("" + user.get_name(), "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("Receiver Signature", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                }
                flag = "1";
                Globals.setEmpty();
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
            }


        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    mService.sendMessage("RECEIPT", "GBK");


                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {
                            mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");
                        }
                    } catch (Exception ex) {

                    }
                    mService.sendMessage("" + Globals.objLPR.getCompany_Name(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage(Globals.PrintDeviceID + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                    mService.sendMessage(Globals.PrintCashier + " : " + user.get_name(), "GBK");
                    mService.sendMessage("RECEIPT NO. : " + strOrderNo, "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    if (Globals.strIsBarcodePrint.equals("true")) {
                        byte[] sendData;
                        sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                        mService.write(sendData);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                    }
                    mService.sendMessage("IN DT : " + DateUtill.PaternDatePrintDate(orders.get_order_date()), "GBK");
                    mService.sendMessage("IN TM : " + DateUtill.PaternDatePrintTime(orders.get_order_date()), "GBK");

                    int count = 0;
                    Double itemFinalTax = 0d;
                    while (count < order_detail.size()) {

                        String strItemCode = order_detail.get(count).get_item_code();

                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_Code");
                        int len = 12;
                        if (strItemName.length() > len) {
                            strItemName = strItemName.substring(0, len);
                        } else {
                            for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                strItemName = strItemName + " ";
                            }
                        }

                        String sale_price;
                        Double dDisAfterSalePrice = 0d;

                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                        sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);

                        mService.sendMessage("VEHICLE TYPE:  \n", "GBK");
                        mService.sendMessage(strItemName + " Rs. " + sale_price, "GBK");
                        count++;
                    }
                    mService.sendMessage("V.NO : " + orders.get_remarks(), "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);

                    if (!settings.get_Footer_Text().equals("")) {
                        mService.sendMessage(settings.get_Footer_Text() + "\n", "GBK");
                    }
                    mService.sendMessage(settings.get_Copy_Right() + "\n", "GBK");
                    mService.sendMessage("\n", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                }
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
                flag = "1";
                Globals.setEmpty();
            }

        } else {
            if ((lang.compareTo("en")) == 0) {
                try {
                    byte[] ab;
                    if (mService.isAvailable() == false) {
                    } else {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            printImage();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");

                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                            } else {
                                mService.sendMessage(Globals.GSTLbl + " : " + Globals.objLPR.getLicense_No(), "GBK");
                            }
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage(Globals.PrintOrder, "GBK");
                            ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            ArrayList<String> arrayList = new ArrayList<String>();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            if (order_payment_array.size() > 0) {

                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    arrayList.add(order_payment_array.get(i).get_payment_id());
                                }

                                if (arrayList.contains("1") && arrayList.contains("5")) {
                                    mService.sendMessage("Invoice Type: Credit/Cash", "GBK");
                                } else {
                                    if (arrayList.contains("5")) {
                                        mService.sendMessage("Invoice Type: Credit", "GBK");

                                    } else if (arrayList.contains("1")) {
                                        mService.sendMessage("Invoice Type: Cash", "GBK");
                                    }
                                }
                            }
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                byte[] sendData;
                                sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                                mService.write(sendData);
                            }
                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);

                            mService.sendMessage(Globals.PrintInvNo + " : " + strOrderNo, "GBK");
                            mService.sendMessage(Globals.PrintInvDate + "   : " + DateUtill.PaternDate1(orders.get_order_date()), "GBK");
                            mService.sendMessage(Globals.PrintDeviceID + "      : " + Globals.objLPD.getDevice_Name(), "GBK");
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            mService.sendMessage(Globals.PrintCashier + "    : " + user.get_name(), "GBK");

                            if (Globals.ModeResrv.equals("Resv")) {
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                mService.sendMessage("Customer     : " + contact.get_name(), "GBK");
                                if (contact.get_gstin().length() > 0) {
                                    mService.sendMessage("Customer GST No. : " + contact.get_gstin(), "GBK");
                                }
                            } else {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                    mService.sendMessage("Customer       : " + contact.get_name(), "GBK");
                                    if (contact.get_gstin().length() > 0) {
                                        mService.sendMessage("Customer GST No. : " + contact.get_gstin(), "GBK");
                                    }
                                }
                            }

                            mService.sendMessage("............................................................", "GBK");
                            mService.sendMessage("Item Name                    Qty         Price         Total", "GBK");
                            mService.sendMessage("............................................................", "GBK");

                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();
                                String strItemName = Order_Detail.getItemName(getApplicationContext(), "WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;
//                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                                Double dDisAfter = 0d;
                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                mService.sendMessage("" + strItemName, "GBK");
                                mService.sendMessage("                            " + order_detail.get(count).get_quantity() + "            " + sale_price + "         " + line_total, "GBK");

                                String discnt = "";
                                String disLbl = "";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                } else {
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                        discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                        disLbl = "Dis :";
                                    }
                                    mService.sendMessage("" + disLbl + " " + discnt, "GBK");
                                }

                                if (settings.get_HSN_print().equals("true")) {
                                    item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                    mService.sendMessage("HSN Code : " + item.get_hsn_sac_code() + "", "GBK");
                                }

                                if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                    Tax_Master tax_master = null;
                                    ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                    for (int i = 0; i < order_detail_tax.size(); i++) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                        double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                        itemFinalTax += valueFinal;
                                        //woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        mService.sendMessage("" + tax_master.get_tax_name() + "            " + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "", "GBK");
                                    }
                                }
                                count++;
                            }

                            mService.sendMessage("............................................................", "GBK");
                            mService.sendMessage("Total Item    : " + orders.get_total_item(), "GBK");
                            mService.sendMessage("Item Quantity : " + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check), "GBK");
                            String subtotal = "";
                            String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);
                            Tax_Master tax_master = null;
                            while (cursor1.moveToNext()) {
                                subtotal = cursor1.getString(0);
                            }
                            subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                            mService.sendMessage("Subtotal      : " + subtotal, "GBK");
                            mService.sendMessage("............................................................", "GBK");
                            if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                        "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                        "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                cursor1 = database.rawQuery(strTableQry, null);

                                while (cursor1.moveToNext()) {
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    mService.sendMessage("" + tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "", "GBK");
                                }
                                mService.sendMessage("............................................................", "GBK");
                            }

                            String discount = "0";
                            if (Double.parseDouble(orders.get_total_discount()) == 0) {

                            } else {
                                discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                if (Globals.strIsDiscountPrint.equals("true")) {
                                    mService.sendMessage("Discount      : " + Globals.DiscountPer + "%", "GBK");
                                    mService.sendMessage("Discount      : " + discount, "GBK");
                                }
                            }
                            String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                            String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                            mService.sendMessage("Total         : " + tatalAftrDis, "GBK");

                            String total_tax;
                            total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                            if (Double.parseDouble(total_tax) > 0d) {
                                mService.sendMessage("Total Tax     : " + total_tax, "GBK");
                                mService.sendMessage("............................................................", "GBK");
                                Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                String name = "", value = "";
                                if (cursor.moveToFirst()) {
                                    do {
                                        name = cursor.getString(0);
                                        value = cursor.getString(1);
                                        mService.sendMessage(name + "         " + total_tax, "GBK");
                                    } while (cursor.moveToNext());
                                }
                            }
                            mService.sendMessage("............................................................", "GBK");
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            mService.sendMessage("Net Amt       : " + net_amount + "" + strCurrency, "GBK");
                            String tender;
                            tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                            mService.sendMessage("Tender        : " + tender, "GBK");
                            String change_amount;
                            change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                            mService.sendMessage("Change        : " + change_amount + "\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            mService.sendMessage("............................................................", "GBK");
                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            if (order_payment_array.size() > 0) {
                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                    String name = "";
                                    if (payment != null) {
                                        name = payment.get_payment_name();
                                        mService.sendMessage(name + " : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + "" + "\n", "GBK");
//                                    woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{9, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                mService.sendMessage("............................................................", "GBK");
                            }
                            if (settings.get_Is_Accounts().equals("true")) {
                                if (ck_project_type.equals("standalone")) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        Double showAmount = 0d;
//                                    String curAmount = "";
                                        try {
                                            String strCreditAmt = "", strDeditAmount = "";
                                            Double creditAmount = 0d,
                                                    debitAmount = 0d;
                                            Cursor cursor = null;

                                            String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                            cursor = database.rawQuery(strQry1, null);
                                            while (cursor.moveToNext()) {
                                                strCreditAmt = cursor.getString(0);

                                            }
                                            creditAmount = Double.parseDouble(strCreditAmt);

                                            String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                            cursor = database.rawQuery(strQry2, null);
                                            while (cursor.moveToNext()) {
                                                strDeditAmount = cursor.getString(0);
                                            }
                                            debitAmount = Double.parseDouble(strDeditAmount);
                                            showAmount = debitAmount + creditAmount;
                                        } catch (Exception ex) {
                                        }

                                        double abs1 = Math.abs(showAmount);
                                        if (showAmount > 0) {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", abs1 + "");
                                        } catch (Exception ex) {
                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {
                                        }
                                        Double strBalance = abs1 - Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {
                                        }

                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount : " + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");

                                        mService.sendMessage("............................................................", "GBK");
                                    }
                                } else {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        String curAmount = "";
                                        try {
                                            strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);
                                            if (cursor1.moveToFirst()) {
                                                do {
                                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                } while (cursor1.moveToNext());
                                            }
                                        } catch (Exception ex) {
                                            curAmount = "0";
                                        }
                                        double ab1 = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                        double abs1 = Math.abs(ab1);
                                        if (ab1 > 0) {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", curAmount + "");
                                        } catch (Exception ex) {

                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {

                                        }
                                        Double strBalance = ab1 + Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {

                                        }
                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount : " + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");
                                        mService.sendMessage("............................................................", "GBK");
                                    }
                                }
                            }

                            if (!settings.get_Footer_Text().equals("")) {
                                mService.sendMessage(settings.get_Footer_Text(), "GBK");
                            }
                            mService.sendMessage("                       " + settings.get_Copy_Right() + "\n\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            byte[] print = {0x1b, 0x0c};
                            mService.write(print);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";

                        flag = "1";
                        Globals.setEmpty();
                    }
                } catch (Exception ex) {
                    String ab = ex.getMessage();
                }
            }
        }
        return flag;
    }

    private String bluetooth_80(Orders orders, ArrayList<Order_Detail> order_detail) {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);

        final byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if (opr.equals("PayCollection")) {

            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);

                    //printImage();
                    mService.sendMessage("Receipt Voucher", "GBK");
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage("Receipt No. :" + pay_collection.get_collection_code(), "GBK");
                    mService.sendMessage("Date  :" + pay_collection.get_collection_date(), "GBK");
                    String amount;
                    amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                    mService.sendMessage("Amount :" + amount, "GBK");

                    String[] str = amount.split("\\.");
                    if (str.length == 1) {
                        mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " Only ", "GBK");
                    } else if (str.length == 2) {
                        if (Integer.parseInt(str[1].toString()) == 0) {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only ", "GBK");

                        } else {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only ", "GBK");
                        }

                    }

                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                    mService.sendMessage("Received From :" + contact.get_name(), "GBK");
                    mService.sendMessage("Cash/Cheque   :" + pay_collection.get_payment_mode(), "GBK");

                    if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                        Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                        mService.sendMessage("Bank Name  :" + bank.get_bank_name(), "GBK");
                        mService.sendMessage("Cheque No  :" + pay_collection.get_ref_no(), "GBK");

                    }

                    mService.sendMessage("On Account  :" + pay_collection.get_on_account(), "GBK");


                    mService.sendMessage("Remarks  :" + pay_collection.get_remarks(), "GBK");

                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    mService.sendMessage("" + user.get_name(), "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("Receiver Signature", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                }
                flag = "1";
                Globals.setEmpty();
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
            }


        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    mService.sendMessage("RECEIPT", "GBK");


                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {
                            mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");
                        }
                    } catch (Exception ex) {

                    }
                    mService.sendMessage("" + Globals.objLPR.getCompany_Name(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage(Globals.PrintDeviceID + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                    mService.sendMessage(Globals.PrintCashier + " : " + user.get_name(), "GBK");
                    mService.sendMessage("RECEIPT NO. : " + strOrderNo, "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    if (Globals.strIsBarcodePrint.equals("true")) {
                        byte[] sendData;
                        sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                        mService.write(sendData);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                    }
                    mService.sendMessage("IN DT : " + DateUtill.PaternDatePrintDate(orders.get_order_date()), "GBK");
                    mService.sendMessage("IN TM : " + DateUtill.PaternDatePrintTime(orders.get_order_date()), "GBK");

                    int count = 0;
                    Double itemFinalTax = 0d;
                    while (count < order_detail.size()) {

                        String strItemCode = order_detail.get(count).get_item_code();

                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_Code");
                        int len = 12;
                        if (strItemName.length() > len) {
                            strItemName = strItemName.substring(0, len);
                        } else {
                            for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                strItemName = strItemName + " ";
                            }
                        }

                        String sale_price;
                        Double dDisAfterSalePrice = 0d;

                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                        sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);

                        mService.sendMessage("VEHICLE TYPE:  \n", "GBK");
                        mService.sendMessage(strItemName + " Rs. " + sale_price, "GBK");
                        count++;
                    }
                    mService.sendMessage("V.NO : " + orders.get_remarks(), "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);

                    if (!settings.get_Footer_Text().equals("")) {
                        mService.sendMessage(settings.get_Footer_Text() + "\n", "GBK");
                    }
                    mService.sendMessage(settings.get_Copy_Right() + "\n", "GBK");
                    mService.sendMessage("\n", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                }
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.strOldCrAmt = "0";
                Globals.DiscountPer = "";
                flag = "1";
                Globals.setEmpty();
            }

        } else {
            if ((lang.compareTo("en")) == 0) {
                try {
                    byte[] ab;
                    if (mService.isAvailable() == false) {
                    } else {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            //printImage();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");

                            try {
                                if (Globals.objLPR.getService_code_tariff() == null) {
                                } else {
                                    mService.sendMessage("" + Globals.objLPR.getLicense_No(), "GBK");
                                }
                            } catch (Exception ex) {
                            }

                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                            } else {
                                mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");
                            }
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage(Globals.PrintOrder, "GBK");
                            ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            ArrayList<String> arrayList = new ArrayList<String>();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            if (order_payment_array.size() > 0) {

                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    arrayList.add(order_payment_array.get(i).get_payment_id());
                                }

                                if (arrayList.contains("1") && arrayList.contains("5")) {
                                    mService.sendMessage("Invoice Type: Credit/Cash", "GBK");
                                } else {
                                    if (arrayList.contains("5")) {
                                        mService.sendMessage("Invoice Type: Credit", "GBK");

                                    } else if (arrayList.contains("1")) {
                                        mService.sendMessage("Invoice Type: Cash", "GBK");
                                    }
                                }
                            }
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                byte[] sendData;
                                sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                                mService.write(sendData);
                            }
                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);

                            mService.sendMessage(Globals.PrintInvNo + " : " + strOrderNo, "GBK");
                            mService.sendMessage(Globals.PrintInvDate + "   : " + DateUtill.PaternDate1(orders.get_order_date()), "GBK");
                            mService.sendMessage(Globals.PrintDeviceID + "      : " + Globals.objLPD.getDevice_Name(), "GBK");
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            mService.sendMessage(Globals.PrintCashier + "    : " + user.get_name(), "GBK");

                            if (Globals.ModeResrv.equals("Resv")) {
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                mService.sendMessage("Customer     : " + contact.get_name(), "GBK");
                                if (contact.get_gstin().length() > 0) {
                                    mService.sendMessage("Customer GST No. : " + contact.get_gstin(), "GBK");
                                }
                            } else {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                    mService.sendMessage("Customer       : " + contact.get_name(), "GBK");
                                    if (contact.get_gstin().length() > 0) {
                                        mService.sendMessage("Customer GST No. : " + contact.get_gstin(), "GBK");
                                    }
                                }
                            }

                            mService.sendMessage("................................................", "GBK");
                            mService.sendMessage("Item Name           Qty     Price      Total", "GBK");
                            mService.sendMessage("................................................", "GBK");

                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();
                                String strItemName = Order_Detail.getItemName(getApplicationContext(), "WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;
//                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                                Double dDisAfter = 0d;
                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                mService.sendMessage("" + strItemName, "GBK");
                                mService.sendMessage("                    " + order_detail.get(count).get_quantity() + "      " + sale_price + "      " + line_total, "GBK");

                                String discnt = "";
                                String disLbl = "";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                } else {
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                        discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                        disLbl = "Dis :";
                                    }
                                    mService.sendMessage("" + disLbl + " " + discnt, "GBK");
                                }

                                if (settings.get_HSN_print().equals("true")) {
                                    item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                    mService.sendMessage("HSN Code : " + item.get_hsn_sac_code() + "", "GBK");
                                }

                                if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                    Tax_Master tax_master = null;
                                    ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                    for (int i = 0; i < order_detail_tax.size(); i++) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                        double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                        itemFinalTax += valueFinal;
                                        //woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        mService.sendMessage("" + tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "", "GBK");
                                    }
                                }
                                count++;
                            }

                            mService.sendMessage("................................................", "GBK");
                            mService.sendMessage("Total Item    : " + orders.get_total_item(), "GBK");
                            mService.sendMessage("Item Quantity : " + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check), "GBK");
                            String subtotal = "";
                            String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);
                            Tax_Master tax_master = null;
                            while (cursor1.moveToNext()) {
                                subtotal = cursor1.getString(0);
                            }
                            subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                            mService.sendMessage("Subtotal      : " + subtotal, "GBK");
                            mService.sendMessage("................................................", "GBK");
                            if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                        "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                        "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                cursor1 = database.rawQuery(strTableQry, null);

                                while (cursor1.moveToNext()) {
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    mService.sendMessage("" + tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "", "GBK");
                                }
                                mService.sendMessage("................................................", "GBK");
                            }

                            String discount = "0";
                            if (Double.parseDouble(orders.get_total_discount()) == 0) {

                            } else {
                                discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                if (Globals.strIsDiscountPrint.equals("true")) {
                                    mService.sendMessage("Discount      : " + Globals.DiscountPer + "%", "GBK");
                                    mService.sendMessage("Discount      : " + discount, "GBK");
                                }
                            }
                            String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                            String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                            mService.sendMessage("Total         : " + tatalAftrDis, "GBK");

                            String total_tax;
                            total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                            if (Double.parseDouble(total_tax) > 0d) {
                                mService.sendMessage("Total Tax     : " + total_tax, "GBK");
                                mService.sendMessage("................................................", "GBK");
                                Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                String name = "", value = "";
                                if (cursor.moveToFirst()) {
                                    do {
                                        name = cursor.getString(0);
                                        value = cursor.getString(1);
                                        mService.sendMessage(name + "         " + total_tax, "GBK");
                                    } while (cursor.moveToNext());
                                }
                            }
                            mService.sendMessage("................................................", "GBK");
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            mService.sendMessage("Net Amt       : " + net_amount + "" + strCurrency, "GBK");
                            String tender;
                            tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                            mService.sendMessage("Tender        : " + tender, "GBK");
                            String change_amount;
                            change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                            mService.sendMessage("Change        : " + change_amount + "\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            mService.sendMessage("................................................", "GBK");
                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            if (order_payment_array.size() > 0) {
                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                    String name = "";
                                    if (payment != null) {
                                        name = payment.get_payment_name();
                                        mService.sendMessage(name + " : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + "" + "\n", "GBK");
//                                    woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{9, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                mService.sendMessage("................................................", "GBK");
                            }
                            if (settings.get_Is_Accounts().equals("true")) {
                                if (ck_project_type.equals("standalone")) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        Double showAmount = 0d;
//                                    String curAmount = "";
                                        try {
                                            String strCreditAmt = "", strDeditAmount = "";
                                            Double creditAmount = 0d,
                                                    debitAmount = 0d;
                                            Cursor cursor = null;

                                            String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                            cursor = database.rawQuery(strQry1, null);
                                            while (cursor.moveToNext()) {
                                                strCreditAmt = cursor.getString(0);

                                            }
                                            creditAmount = Double.parseDouble(strCreditAmt);

                                            String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                            cursor = database.rawQuery(strQry2, null);
                                            while (cursor.moveToNext()) {
                                                strDeditAmount = cursor.getString(0);
                                            }
                                            debitAmount = Double.parseDouble(strDeditAmount);
                                            showAmount = debitAmount + creditAmount;
                                        } catch (Exception ex) {
                                        }

                                        double abs1 = Math.abs(showAmount);
                                        if (showAmount > 0) {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", abs1 + "");
                                        } catch (Exception ex) {
                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {
                                        }
                                        Double strBalance = abs1 - Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {
                                        }

                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount : " + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");

                                        mService.sendMessage("................................................", "GBK");
                                    }
                                } else {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        String curAmount = "";
                                        try {
                                            strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);
                                            if (cursor1.moveToFirst()) {
                                                do {
                                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                } while (cursor1.moveToNext());
                                            }
                                        } catch (Exception ex) {
                                            curAmount = "0";
                                        }
                                        double ab1 = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                        double abs1 = Math.abs(ab1);
                                        if (ab1 > 0) {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt        : " + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", curAmount + "");
                                        } catch (Exception ex) {

                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt    : " + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {

                                        }
                                        Double strBalance = ab1 + Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {

                                        }
                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount : " + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");
                                        mService.sendMessage("................................................", "GBK");
                                    }
                                }
                            }

                            if (!settings.get_Footer_Text().equals("")) {
                                mService.sendMessage(settings.get_Footer_Text(), "GBK");
                            }
                            mService.sendMessage("             " + settings.get_Copy_Right() + "\n\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            byte[] print = {0x1b, 0x0c};
                            mService.write(print);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";

                        flag = "1";
                        Globals.setEmpty();
                    }
                } catch (Exception ex) {
                    String ab = ex.getMessage();
                }
            }
        }
        return flag;
    }

    private void ppt8527(Orders orders, ArrayList<Order_Detail> order_detail) {

        if (opr.equals("PayCollection")) {
            final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);

            Bitmap[] bitmap = null;
            Bitmap bm;
            try {
//                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                String Print_type = "0";
                JSONArray printTest = new JSONArray();
                printJson = new JSONObject();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);

                bm = StringToBitMap(settings.get_Logo());
                if (bm != null) {
                    JSONObject json11 = new JSONObject();
                    json11.put("content-type", "jpg");
                    json11.put("position", "center");
                    printTest.put(json11);
                    printJson.put("spos", printTest);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    bitmap = new Bitmap[]{bm.createScaledBitmap(bm, 240, bm.getHeight(), true)};
                }

                printTest.put(getPrintObject("Receipt Voucher", "3", "center"));
                printTest.put(getPrintObject("Receipt No." + ":" + pay_collection.get_collection_code(), "3", "center"));
                printTest.put(getPrintObject("Date" + ":" + pay_collection.get_collection_date(), "3", "center"));
                String amount;
                amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                printTest.put(getPrintObject("Amount" + ":" + amount, "3", "center"));
                String[] str = amount.split("\\.");
                if (str.length == 1) {
                    printTest.put(getPrintObject("In Words" + ":" + Globals.convert(Integer.parseInt(str[0].toString())) + " Only ", "3", "center"));
                } else if (str.length == 2) {
                    if (Integer.parseInt(str[1].toString()) == 0) {
                        printTest.put(getPrintObject("In Words" + ":" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only", "3", "center"));
                    } else {
                        printTest.put(getPrintObject("In Words" + ":" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only ", "3", "center"));
                    }

                }

                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                printTest.put(getPrintObject("Received From" + ":" + contact.get_name(), "3", "center"));
                printTest.put(getPrintObject("Cash/Cheque" + ":" + pay_collection.get_payment_mode(), "3", "center"));
                printTest.put(getPrintObject("On Account" + ":" + pay_collection.get_on_account(), "3", "center"));
                printTest.put(getPrintObject("Remarks" + ":" + pay_collection.get_remarks(), "3", "center"));

                if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                    Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                    printTest.put(getPrintObject("Bank Name" + ":" + bank.get_bank_name(), "3", "center"));
                    printTest.put(getPrintObject("Cheque No" + ":" + pay_collection.get_ref_no(), "3", "center"));
                }

                ArrayList<Pay_Collection_Detail> pay_collection_detail = Pay_Collection_Detail.getAllPay_Collection_Detail(getApplicationContext(), " WHERE collection_code='" + pay_collection.get_collection_code() + "'");
                if (pay_collection_detail.size() > 0) {
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    printTest.put(getPrintObject("Order No                Amount", "2", "left"));
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    for (int i = 0; i < pay_collection_detail.size(); i++) {
                        printTest.put(getPrintObject(pay_collection_detail.get(i).get_invoice_no() + "                  " + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(pay_collection_detail.get(i).get_amount()), qty_decimal_check), "2", "left"));
                    }
                }
                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                printTest.put(getPrintObject("Cashier" + ":" + user.get_name(), "2", "left"));
                ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);

                printTest.put(getPrintObject("Receiver Signature", "2", "left"));
                printJson.put("spos", printTest);
                // 设置底部空3行
//                    printJson.put("spos", printTest);
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    try {
                        ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                        ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }


//                }
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
                Globals.setEmpty();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
            try {
//                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                String Print_type = "0";
                JSONArray printTest = new JSONArray();
                printJson = new JSONObject();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));

                try {
                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                    } else {
                        printTest.put(getPrintObject(Globals.objLPR.getService_code_tariff(), "3", "center"));
                    }
                } catch (Exception ex) {
                }
                printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));
                printTest.put(getPrintObject(Globals.objLPR.getAddress(), "3", "center"));
                printTest.put(getPrintObject(Globals.objLPR.getMobile_No(), "3", "center"));
                printTest.put(getPrintObject(Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name(), "3", "left"));
                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                printTest.put(getPrintObject(Globals.PrintCashier + ":" + user.get_name(), "3", "left"));
                printTest.put(getPrintObject("RECEIPT NO." + ":" + strOrderNo, "3", "left"));
                if (Globals.strIsBarcodePrint.equals("true")) {
//                        woyouService.setAlignment(1, callback);
//                        woyouService.printBarCode(strOrderNo, 8, 80, 150, 0, callback);
//                        woyouService.printTextWithFont(" \n", "", 24, callback);
//                        woyouService.setAlignment(0, callback);
                }
                printTest.put(getPrintObject("IN DT" + ":" + DateUtill.PaternDatePrintDate(orders.get_order_date()), "3", "left"));
                printTest.put(getPrintObject("IN TM" + ":" + DateUtill.PaternDatePrintTime(orders.get_order_date()), "3", "left"));
                int count = 0;
                Double itemFinalTax = 0d;
                while (count < order_detail.size()) {

                    String strItemCode = order_detail.get(count).get_item_code();

                    String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By order_detail.item_Code");
                    int len = 12;
                    if (strItemName.length() > len) {
                        strItemName = strItemName.substring(0, len);
                    } else {
                        for (int sLen = strItemName.length(); sLen < len; sLen++) {
                            strItemName = strItemName + " ";
                        }
                    }

                    String sale_price;
                    Double dDisAfterSalePrice = 0d;

                    dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                    sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);
                    //sale_price = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check);
                    String line_total;
                    printTest.put(getPrintObject("VEHICLE TYPE:", "3", "left"));
                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                    printTest.put(getPrintObject(strItemName + " Rs." + sale_price, "3", "left"));
                    count++;
                }

                printTest.put(getPrintObject("V.NO : " + orders.get_remarks(), "3", "left"));

                if (!settings.get_Footer_Text().equals("")) {
                    printTest.put(getPrintObject(settings.get_Footer_Text(), "3", "center"));
                }
                printTest.put(getPrintObject(settings.get_Copy_Right(), "3", "center"));
                printJson.put("spos", printTest);
//                    // 设置底部空3行
//                    printJson.put("spos", printTest);
                // 设置底部空3行
                // Set at the bottom of the empty 3 rows
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    try {
                        ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                        ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }
//                }
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
                Globals.setEmpty();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

        } else {

            if (settings.get_Print_Memo().equals("1")) {
                try {
                    JSONArray printTest = new JSONArray();
                    printJson = new JSONObject();
                    timeTools = new TimerCountTools();
                    timeTools.start();
                    ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);

                    orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                    String Print_type = "0";

                    printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));
                    printTest.put(getPrintObject("Order : " + orders.get_order_code(), "3", "center"));

                    if (orders.get_table_code().equals("")) {
                    } else {
                        printTest.put(getPrintObject("Table :" + orders.get_table_code(), "3", "left"));

                    }

                    order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                    if (order_detail.size() > 0) {
                        Double total = 0d;

                        printTest.put(getPrintObject("Name       Qty   Price   Total", "2", "left"));
                        printTest.put(getPrintObject("--------------------------------", "2", "left"));
                        for (int i = 0; i < order_detail.size(); i++) {
                            total = total + Double.parseDouble(order_detail.get(i).get_line_total());
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            try {
                                String name = "";
                                String k = "";
                                if (item.get_item_name().length() <= 6) {
                                    int l = item.get_item_name().length();
                                    int space = 8 - l;
                                    for (int i2 = 0; i2 < space; i2++) {
                                        k = k + " ";
                                    }
                                    name = item.get_item_name() + k;
                                } else {
                                    try {
                                        name = item.get_item_name().substring(0, 8);
                                    } catch (Exception ex) {
                                        name = item.get_item_name();
                                    }


                                }
                                printTest.put(getPrintObject(name + "   " + "X " + order_detail.get(i).get_quantity() + "   " + Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_sale_price()), decimal_check) + "  " + Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_line_total()), decimal_check), "2", "left"));
                            } catch (Exception ex) {
                            }
                        }
                        printTest.put(getPrintObject("--------------------------------", "2", "left"));
                        printTest.put(getPrintObject("Total Amount :" + Globals.myNumberFormat2Price(total, decimal_check), "3", "left"));
                    }

                    printJson.put("spos", printTest);
                    ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                    ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);

                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Bitmap[] bitmap = null;
                    Bitmap bm;

                    JSONArray printTest = new JSONArray();
                    printJson = new JSONObject();
                    timeTools = new TimerCountTools();
                    timeTools.start();
                    ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);

                    String Print_type = "0";
                    bm = StringToBitMap(settings.get_Logo());
                    if (bm != null) {
                        JSONObject json11 = new JSONObject();
                        json11.put("content-type", "jpg");
                        json11.put("position", "center");
                        printTest.put(json11);
                        printJson.put("spos", printTest);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        bitmap = new Bitmap[]{bm.createScaledBitmap(bm, 240, bm.getHeight(), true)};
                    }

                    printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));

                    printTest.put(getPrintObject(Globals.objLPR.getAddress(), "3", "center"));

                    printTest.put(getPrintObject(Globals.objLPR.getMobile_No(), "3", "center"));
                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {

                            printTest.put(getPrintObject(Globals.objLPR.getService_code_tariff(), "3", "center"));
                        }
                    } catch (Exception ex) {

                    }

                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {

                    } else {
                        printTest.put(getPrintObject(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "3", "center"));

                    }

                    printTest.put(getPrintObject(Globals.PrintOrder, "3", "center"));
                    ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                    ArrayList<String> arrayList = new ArrayList<String>();

                    if (order_payment_array.size() > 0) {

                        for (int i = 0; i < order_payment_array.size(); i++) {
                            arrayList.add(order_payment_array.get(i).get_payment_id());
                        }

                        if (arrayList.contains("1") && arrayList.contains("5")) {
                            printTest.put(getPrintObject("Invoice Type: Credit/Cash", "2", "left"));

                        } else {
                            if (arrayList.contains("5")) {
                                printTest.put(getPrintObject("Invoice Type: Credit", "2", "left"));

                            } else if (arrayList.contains("1")) {
                                printTest.put(getPrintObject("Invoice Type: Cash", "2", "left"));
                            }
                        }
                    }

                    if (Globals.strIsBarcodePrint.equals("true")) {
//                            woyouService.printBarCode(strOrderNo, 8, 60, 120, 0, callback);
                    }
                    printTest.put(getPrintObject(Globals.PrintInvNo + " : " + strOrderNo, "2", "left"));
                    printTest.put(getPrintObject(Globals.PrintInvDate + " : " + DateUtill.PaternDate1(orders.get_order_date()), "2", "left"));
//                    woyouService.printColumnsText(new String[]{Globals.PrintInvDate, ":", DateUtill.PaternDate1(orders.get_order_date())}, new int[]{12, 1, 20}, new int[]{0, 0, 0}, callback);
                    printTest.put(getPrintObject(Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name(), "2", "left"));

                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    printTest.put(getPrintObject(Globals.PrintCashier + ":" + user.get_name(), "2", "left"));


                    if (Globals.ModeResrv.equals("Resv")) {
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");

                        printTest.put(getPrintObject("Customer" + ":" + contact.get_name(), "2", "left"));
                        if (contact.get_gstin().length() > 0) {
                            printTest.put(getPrintObject("Customer GST No." + ":" + contact.get_gstin(), "2", "left"));

                        }
                    } else {
                        if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                        } else {
                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");

                            printTest.put(getPrintObject("Customer" + ":" + contact.get_name(), "2", "left"));
                            if (contact.get_gstin().length() > 0) {
                                printTest.put(getPrintObject("Customer GST No." + ":" + contact.get_gstin(), "2", "left"));


                            }
                        }
                    }


                    printTest.put(getPrintObject("--------------------------------", "2", "left"));

                    printTest.put(getPrintObject("Item Name", "2", "left"));

                    printTest.put(getPrintObject("Qty           Price    Total", "2", "left"));

                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    int count = 0;
                    Double itemFinalTax = 0d;
                    while (count < order_detail.size()) {

                        String strItemCode = order_detail.get(count).get_item_code();

                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_Code");


                        String sale_price;
                        Double dDisAfterSalePrice = 0d;

//                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                        Double dDisAfter = 0d;
                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                        dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                        String line_total;
                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);

                        try {
                            String name = "";
                            String k1 = "";
                            if (strItemName.length() <= 6) {
                                int l = strItemName.length();
                                int space = 8 - l;
                                for (int i2 = 0; i2 < space; i2++) {
                                    k1 = k1 + " ";
                                }
                                name = strItemName + k1;
                            } else {
                                try {
                                    name = strItemName.substring(0, 8);
                                } catch (Exception ex) {
                                    name = strItemName;
                                }
                            }
                            printTest.put(getPrintObject(name, "2", "left"));
                        } catch (Exception ex) {
                        }
                        printTest.put(getPrintObject(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check) + "            " + sale_price + "     " + line_total, "2", "left"));
                        String discnt = "";
                        String disLbl = "";
                        if (Double.parseDouble(orders.get_total_discount()) == 0) {
                        } else {
                            if (Globals.strIsDiscountPrint.equals("true")) {
                                Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                disLbl = "Dis :";
                            }
                            printTest.put(getPrintObject(disLbl + " " + discnt, "2", "left"));
                        }

                        if (settings.get_HSN_print().equals("true")) {
                            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                            printTest.put(getPrintObject("HSN Code :" + item.get_hsn_sac_code(), "2", "left"));
                        }
                        if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                            Tax_Master tax_master = null;
                            ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                            for (int i = 0; i < order_detail_tax.size(); i++) {
                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                itemFinalTax += valueFinal;

                                printTest.put(getPrintObject(tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(valueFinal, decimal_check), "2", "left"));
                            }
                        }
                        count++;
                    }

                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    printTest.put(getPrintObject("Total Item" + ":" + orders.get_total_item(), "2", "left"));
                    printTest.put(getPrintObject("Item Quantity" + ":" + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check), "2", "left"));
                    String subtotal = "";
                    String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                    Cursor cursor1 = database.rawQuery(strTableQry, null);
                    Tax_Master tax_master;
                    while (cursor1.moveToNext()) {
                        subtotal = cursor1.getString(0);
                    }
                    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);
                    printTest.put(getPrintObject("Subtotal" + ":" + subtotal, "2", "left"));
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));

                    if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {
                        strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                        cursor1 = database.rawQuery(strTableQry, null);
                        while (cursor1.moveToNext()) {
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);

                            printTest.put(getPrintObject(tax_master.get_tax_name() + " " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "2", "left"));
                        }
                        printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    }

                    String discount = "0";
                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                    } else {
                        discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                        if (Globals.strIsDiscountPrint.equals("true")) {
                            printTest.put(getPrintObject("Discount      : " + Globals.DiscountPer + "%", "2", "left"));
                            printTest.put(getPrintObject("Discount" + ":" + discount, "2", "left"));
                        }
                    }
                    String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                    String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                    printTest.put(getPrintObject("Total" + ":" + tatalAftrDis, "2", "left"));
                    String total_tax;
                    total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                    if (Double.parseDouble(total_tax) > 0d) {
                        printTest.put(getPrintObject("Total Tax" + ":" + total_tax, "2", "left"));
                        printTest.put(getPrintObject("--------------------------------", "2", "left"));
                        Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                        String name = "", value = "";
                        if (cursor.moveToFirst()) {
                            do {
                                name = cursor.getString(0);
                                value = cursor.getString(1);

                                printTest.put(getPrintObject(name + ":" + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check), "2", "left"));
                            } while (cursor.moveToNext());
                        }
                    }
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    String net_amount;
                    net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                    String strCurrency;
                    if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                        strCurrency = "";
                    } else {
                        strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                    }

                    printTest.put(getPrintObject("Net Amt" + ":" + net_amount + "" + strCurrency, "2", "left"));
                    String tender;
                    tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                    printTest.put(getPrintObject("Tender" + ":" + tender, "2", "left"));
                    String change_amount;
                    change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                    printTest.put(getPrintObject("Change" + ":" + change_amount, "2", "left"));
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                    if (order_payment_array.size() > 0) {
                        for (int i = 0; i < order_payment_array.size(); i++) {
                            Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                            String name = "";
                            if (payment != null) {
                                name = payment.get_payment_name();

                                printTest.put(getPrintObject(name + ":" + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check), "2", "left"));
                            }
                        }
                        printTest.put(getPrintObject("--------------------------------", "2", "left"));
                    }

                    if (settings.get_Is_Accounts().equals("true")) {
                        if (ck_project_type.equals("standalone")) {
                            JSONObject jsonObject = new JSONObject();
                            if (Globals.strContact_Code.equals("")) {
                                printTest.put(getPrintObject("**", "2", "left"));
                            } else {
                                Double showAmount = 0d;
                                try {
                                    String strCreditAmt = "", strDeditAmount = "";
                                    Double creditAmount = 0d,
                                            debitAmount = 0d;
                                    Cursor cursor = null;
                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                    cursor = database.rawQuery(strQry1, null);
                                    while (cursor.moveToNext()) {
                                        strCreditAmt = cursor.getString(0);
                                    }
                                    creditAmount = Double.parseDouble(strCreditAmt);
                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                    cursor = database.rawQuery(strQry2, null);
                                    while (cursor.moveToNext()) {
                                        strDeditAmount = cursor.getString(0);
                                    }
                                    debitAmount = Double.parseDouble(strDeditAmount);
                                    showAmount = debitAmount + creditAmount;
                                } catch (Exception ex) {
                                }
                                double abs1 = Math.abs(showAmount);
                                if (showAmount > 0) {
                                    printTest.put(getPrintObject("Old Amt" + ":" + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR", "2", "left"));
                                } else {
                                    printTest.put(getPrintObject("Old Amt" + ":" + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR", "2", "left"));
                                }
                                try {
                                    jsonObject.put("Old Amt", abs1 + "");
                                } catch (Exception ex) {
                                }
                                String strCur = "";
                                try {
                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);
                                    while (cursor1.moveToNext()) {
                                        strCur = cursor1.getString(0);
                                    }
                                    printTest.put(getPrintObject("Current Amt" + ":" + Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "2", "left"));
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    printTest.put(getPrintObject("Current Amt" + ":" + Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check), "2", "left"));
                                }
                                try {
                                    jsonObject.put("Current Amt", strCur + "");
                                } catch (Exception ex) {
                                }
                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                try {
                                    jsonObject.put("Balance Amt", strBalance + "");
                                } catch (Exception ex) {
                                }
                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                db.executeDML(strUpdatePayment, database);
                                printTest.put(getPrintObject("Balance Amt" + ":" + Globals.myNumberFormat2Price(strBalance, decimal_check), "2", "left"));
                                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                            }
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            if (Globals.strContact_Code.equals("")) {
                                printTest.put(getPrintObject("**", "2", "left"));
                            } else {
                                String curAmount = "";
                                try {
                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);
                                    if (cursor1.moveToFirst()) {
                                        do {
                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                        } while (cursor1.moveToNext());
                                    }
                                } catch (Exception ex) {
                                    curAmount = "0";
                                }
                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                double abs1 = Math.abs(ab);
                                if (ab > 0) {
                                    printTest.put(getPrintObject("Old Amt" + ":" + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR", "2", "left"));
                                } else {
                                    printTest.put(getPrintObject("Old Amt" + ":" + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR", "2", "left"));
                                }
                                try {
                                    jsonObject.put("Old Amt", curAmount + "");
                                } catch (Exception ex) {
                                }
                                String strCur = "";
                                try {
                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);
                                    while (cursor1.moveToNext()) {
                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                    }
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    printTest.put(getPrintObject("Current Amt" + ":" + strCur, "2", "left"));
                                }
                                if (strCur.equals("")) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    printTest.put(getPrintObject("Current Amt" + ":" + strCur, "2", "left"));
                                } else {
                                    printTest.put(getPrintObject("Current Amt" + ":" + strCur, "2", "left"));
                                }
                                try {
                                    jsonObject.put("Current Amt", strCur + "");
                                } catch (Exception ex) {
                                }
                                Double strBalance = ab + Double.parseDouble(strCur);
                                try {
                                    jsonObject.put("Balance Amt", strBalance + "");
                                } catch (Exception ex) {
                                }
                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                db.executeDML(strUpdatePayment, database);
                                printTest.put(getPrintObject("Balance Amt" + ":" + Globals.myNumberFormat2Price(strBalance, decimal_check), "2", "left"));
                                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                            }
                        }
                    }
                    if (!settings.get_Footer_Text().equals("")) {
                        printTest.put(getPrintObject(settings.get_Footer_Text(), "2", "left"));
                    }
                    printTest.put(getPrintObject(settings.get_Copy_Right(), "2", "center"));
                    printJson.put("spos", printTest);
                    final Bitmap[] finalBitmap = bitmap;
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                        try {
                            ServiceManager.getInstence().getPrinter().print(printJson.toString(), finalBitmap, printer_callback);
                            ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
                            Thread.sleep(3000);
                        } catch (Exception e) {
                        }
                    }
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    Globals.DiscountPer = "";
                    Globals.strOldCrAmt = "0";
                    Globals.setEmpty();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private String bluetooth_56(final Orders orders, final ArrayList<Order_Detail> order_detail) {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);

        final byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        if (opr.equals("PayCollection")) {

            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);

                    //printImage();
                    mService.sendMessage("Receipt Voucher", "GBK");
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage("Receipt No. :" + pay_collection.get_collection_code(), "GBK");
                    mService.sendMessage("Date  :" + pay_collection.get_collection_date(), "GBK");
                    String amount;
                    amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                    mService.sendMessage("Amount :" + amount, "GBK");

                    String[] str = amount.split("\\.");
                    if (str.length == 1) {
                        mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " Only ", "GBK");
                    } else if (str.length == 2) {
                        if (Integer.parseInt(str[1].toString()) == 0) {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only ", "GBK");

                        } else {
                            mService.sendMessage("In Words :" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only ", "GBK");
                        }

                    }

                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                    mService.sendMessage("Received From :" + contact.get_name(), "GBK");
                    mService.sendMessage("Cash/Cheque   :" + pay_collection.get_payment_mode(), "GBK");

                    if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                        Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                        mService.sendMessage("Bank Name  :" + bank.get_bank_name(), "GBK");
                        mService.sendMessage("Cheque No  :" + pay_collection.get_ref_no(), "GBK");

                    }

                    mService.sendMessage("On Account  :" + pay_collection.get_on_account(), "GBK");


                    mService.sendMessage("Remarks  :" + pay_collection.get_remarks(), "GBK");

                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    mService.sendMessage("" + user.get_name(), "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("Receiver Signature", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    mService.sendMessage("", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                }
                flag = "1";
                Globals.setEmpty();
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
            }


        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
            if (mService.isAvailable() == false) {

            } else {
                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                    byte[] ab;
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    mService.sendMessage("RECEIPT", "GBK");


                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                        } else {
                            mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");
                        }
                    } catch (Exception ex) {

                    }
                    mService.sendMessage("" + Globals.objLPR.getCompany_Name(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage(Globals.PrintDeviceID + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                    mService.sendMessage(Globals.PrintCashier + " : " + user.get_name(), "GBK");
                    mService.sendMessage("RECEIPT NO. : " + strOrderNo, "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    if (Globals.strIsBarcodePrint.equals("true")) {
                        byte[] sendData;
                        sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                        mService.write(sendData);
                        ab = BytesUtil.setAlignCenter(0);
                        mService.write(ab);
                    }
                    mService.sendMessage("IN DT : " + DateUtill.PaternDatePrintDate(orders.get_order_date()), "GBK");
                    mService.sendMessage("IN TM : " + DateUtill.PaternDatePrintTime(orders.get_order_date()), "GBK");

                    int count = 0;
                    Double itemFinalTax = 0d;
                    while (count < order_detail.size()) {

                        String strItemCode = order_detail.get(count).get_item_code();

                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_Code");
                        int len = 12;
                        if (strItemName.length() > len) {
                            strItemName = strItemName.substring(0, len);
                        } else {
                            for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                strItemName = strItemName + " ";
                            }
                        }

                        String sale_price;
                        Double dDisAfterSalePrice = 0d;

                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                        sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);

                        mService.sendMessage("VEHICLE TYPE:  \n", "GBK");
                        mService.sendMessage(strItemName + " Rs. " + sale_price, "GBK");
                        count++;
                    }
                    mService.sendMessage("V.NO : " + orders.get_remarks(), "GBK");
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);

                    if (!settings.get_Footer_Text().equals("")) {
                        mService.sendMessage(settings.get_Footer_Text() + "\n", "GBK");
                    }
                    mService.sendMessage(settings.get_Copy_Right() + "\n", "GBK");
                    mService.sendMessage("\n", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                    byte[] print = {0x1b, 0x0c};
                    mService.write(print);
                }
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.DiscountPer = "";
                Globals.strOldCrAmt = "0";
                flag = "1";
                Globals.setEmpty();
            }

        } else {
            if ((lang.compareTo("en")) == 0) {
                try {
                    byte[] ab;
                    if (mService.isAvailable() == false) {
                    } else {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            //printImage();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                            mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");

                            try {
                                if (Globals.objLPR.getService_code_tariff() == null) {
                                } else {
                                    mService.sendMessage("" + Globals.objLPR.getLicense_No(), "GBK");
                                }
                            } catch (Exception ex) {
                            }

                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                            } else {
                                mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");
                            }
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage(Globals.PrintOrder, "GBK");
                            ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            ArrayList<String> arrayList = new ArrayList<String>();
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            if (order_payment_array.size() > 0) {

                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    arrayList.add(order_payment_array.get(i).get_payment_id());
                                }

                                if (arrayList.contains("1") && arrayList.contains("5")) {
                                    mService.sendMessage("Invoice Type: Credit/Cash", "GBK");
                                } else {
                                    if (arrayList.contains("5")) {
                                        mService.sendMessage("Invoice Type: Credit", "GBK");

                                    } else if (arrayList.contains("1")) {
                                        mService.sendMessage("Invoice Type: Cash", "GBK");
                                    }
                                }
                            }
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                byte[] sendData;
                                sendData = BytesUtil.getPrintQRCode(strOrderNo, 1, 0);
                                mService.write(sendData);
                            }
                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);

                            mService.sendMessage(Globals.PrintInvNo + ":" + strOrderNo, "GBK");
                            mService.sendMessage(Globals.PrintInvDate + ":" + DateUtill.PaternDate1(orders.get_order_date()), "GBK");
                            mService.sendMessage(Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name(), "GBK");
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            mService.sendMessage(Globals.PrintCashier + ":" + user.get_name(), "GBK");

                            if (Globals.ModeResrv.equals("Resv")) {
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                mService.sendMessage("Customer     :" + contact.get_name(), "GBK");
                                if (contact.get_gstin().length() > 0) {
                                    mService.sendMessage("Customer GST No. :" + contact.get_gstin(), "GBK");
                                }
                            } else {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                    mService.sendMessage("Customer     :" + contact.get_name(), "GBK");
                                    if (contact.get_gstin().length() > 0) {
                                        mService.sendMessage("Customer GST No. :" + contact.get_gstin(), "GBK");
                                    }
                                }
                            }

                            mService.sendMessage("................................", "GBK");
                            mService.sendMessage("Item Name       Qty       Price", "GBK");
                            mService.sendMessage("                          Total", "GBK");
                            mService.sendMessage("................................\n", "GBK");

                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();
                                String strItemName = Order_Detail.getItemName(getApplicationContext(), "WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;
//                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                Double dDisAfter = 0d;
                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                mService.sendMessage("" + strItemName, "GBK");
                                mService.sendMessage("        " + order_detail.get(count).get_quantity() + "  " + sale_price + "  " + line_total, "GBK");

                                String discnt = "";
                                String disLbl = "";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                } else {
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                        discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                        disLbl = "Dis :";
                                    }
                                    mService.sendMessage("" + disLbl + " " + discnt, "GBK");
                                }

                                if (settings.get_HSN_print().equals("true")) {
                                    item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                    mService.sendMessage("HSN Code :" + item.get_hsn_sac_code() + "", "GBK");
                                }

                                if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                    Tax_Master tax_master = null;
                                    ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                    for (int i = 0; i < order_detail_tax.size(); i++) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                        double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                        itemFinalTax += valueFinal;
                                        //woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        mService.sendMessage("" + tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "", "GBK");
                                    }
                                }
                                count++;
                            }

                            mService.sendMessage("................................", "GBK");
                            mService.sendMessage("Total Item  :" + orders.get_total_item(), "GBK");
                            mService.sendMessage("Item Quantity  :" + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check), "GBK");
                            String subtotal = "";
                            String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);
                            Tax_Master tax_master = null;
                            while (cursor1.moveToNext()) {
                                subtotal = cursor1.getString(0);
                            }
                            subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                            mService.sendMessage("Subtotal  :" + subtotal, "GBK");
                            mService.sendMessage("................................", "GBK");
                            if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                        "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                        "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                cursor1 = database.rawQuery(strTableQry, null);

                                while (cursor1.moveToNext()) {
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    mService.sendMessage("" + tax_master.get_tax_name() + "    " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "", "GBK");
                                }
                                mService.sendMessage("................................", "GBK");
                            }

                            String discount = "0";
                            if (Double.parseDouble(orders.get_total_discount()) == 0) {

                            } else {
                                discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                if (Globals.strIsDiscountPrint.equals("true")) {
                                    mService.sendMessage("Discount      : " + Globals.DiscountPer + "%", "GBK");
                                    mService.sendMessage("Discount  :" + discount, "GBK");
                                }
                            }
                            String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                            String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                            mService.sendMessage("Total    :" + tatalAftrDis, "GBK");

                            String total_tax;
                            total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                            if (Double.parseDouble(total_tax) > 0d) {
                                mService.sendMessage("Total Tax  :" + total_tax, "GBK");
                                mService.sendMessage("................................", "GBK");
                                Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                String name = "", value = "";
                                if (cursor.moveToFirst()) {
                                    do {
                                        name = cursor.getString(0);
                                        value = cursor.getString(1);
                                        mService.sendMessage(name + "         " + total_tax, "GBK");
                                    } while (cursor.moveToNext());
                                }
                            }
                            mService.sendMessage("................................", "GBK");

                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            mService.sendMessage("Net Amt   :" + net_amount + "" + strCurrency, "GBK");
                            String tender;
                            tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                            mService.sendMessage("Tender    :" + tender, "GBK");
                            String change_amount;
                            change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                            mService.sendMessage("Change    :" + change_amount + "\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            mService.sendMessage("................................", "GBK");
                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                            if (order_payment_array.size() > 0) {
                                for (int i = 0; i < order_payment_array.size(); i++) {
                                    Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                    String name = "";
                                    if (payment != null) {
                                        name = payment.get_payment_name();
                                        mService.sendMessage(name + " : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + "" + "\n", "GBK");
//                                    woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{9, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                mService.sendMessage("................................", "GBK");
                            }
                            if (settings.get_Is_Accounts().equals("true")) {
                                if (ck_project_type.equals("standalone")) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        Double showAmount = 0d;
//                                    String curAmount = "";
                                        try {
                                            String strCreditAmt = "", strDeditAmount = "";
                                            Double creditAmount = 0d,
                                                    debitAmount = 0d;
                                            Cursor cursor = null;

                                            String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                            cursor = database.rawQuery(strQry1, null);
                                            while (cursor.moveToNext()) {
                                                strCreditAmt = cursor.getString(0);

                                            }
                                            creditAmount = Double.parseDouble(strCreditAmt);

                                            String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                            cursor = database.rawQuery(strQry2, null);
                                            while (cursor.moveToNext()) {
                                                strDeditAmount = cursor.getString(0);
                                            }
                                            debitAmount = Double.parseDouble(strDeditAmount);
                                            showAmount = debitAmount + creditAmount;
                                        } catch (Exception ex) {
                                        }

                                        double abs1 = Math.abs(showAmount);
                                        if (showAmount > 0) {
                                            mService.sendMessage("Old Amt  :" + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt  :" + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", abs1 + "");
                                        } catch (Exception ex) {
                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {
                                        }
                                        Double strBalance = abs1 - Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {
                                        }

                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount  :" + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");

                                        mService.sendMessage("................................", "GBK");
                                    }
                                } else {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        mService.sendMessage("**\n", "GBK");
                                    } else {
                                        String curAmount = "";
                                        try {
                                            strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);
                                            if (cursor1.moveToFirst()) {
                                                do {
                                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                } while (cursor1.moveToNext());
                                            }
                                        } catch (Exception ex) {
                                            curAmount = "0";
                                        }
                                        double ab1 = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                        double abs1 = Math.abs(ab1);
                                        if (ab1 > 0) {
                                            mService.sendMessage("Old Amt  :" + Globals.myNumberFormat2Price(abs1, decimal_check) + "CR" + "\n", "GBK");
                                        } else {
                                            mService.sendMessage("Old Amt  :" + Globals.myNumberFormat2Price(abs1, decimal_check) + "DR" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Old Amt", curAmount + "");
                                        } catch (Exception ex) {

                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                            }
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            mService.sendMessage("Current Amt  :" + strCur + "" + "\n", "GBK");
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {

                                        }
                                        Double strBalance = ab1 + Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {

                                        }
                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        mService.sendMessage("Balance Amount  :" + Globals.myNumberFormat2Price(strBalance, decimal_check) + "" + "\n", "GBK");
                                        mService.sendMessage("................................", "GBK");
                                    }
                                }
                            }

                            if (!settings.get_Footer_Text().equals("")) {
                                mService.sendMessage(settings.get_Footer_Text(), "GBK");
                            }
                            mService.sendMessage("      " + settings.get_Copy_Right() + "\n\n", "GBK");
                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            byte[] print = {0x1b, 0x0c};
                            mService.write(print);

                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";

                        flag = "1";
                        Globals.setEmpty();
                    }
                } catch (Exception ex) {
                    String ab = ex.getMessage();
                    ab = ab;
                }
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
                    PrintLayout.this.finish();
                }
            } catch (Exception e) {
            }
        }
    }

    protected void performOperation() {

        for (int k = 1; k <= noofPrint; k++) {
            Bitmap bitmap = StringToBitMap(settings.get_Logo());
            PRN_PrintBitmap(bitmap, 80, 120);
            for (int i = 0; i < mylist.size(); i++) {
                PRN_PrintText(mylist.get(i), ALIGNMENT_LEFT, FT_DEFAULT,
                        TS_0WIDTH | TS_0HEIGHT);
                PRN_PrintText(mylist.get(i), ALIGNMENT_LEFT, FT_DEFAULT,
                        TS_0WIDTH | TS_0HEIGHT);
            }
            PRN_LineFeed(4);
            PRN_CutPaper();
        }
    }

    private void mobile_pos(final Orders orders, final ArrayList<Order_Detail> order_detail) {

        if (opr.equals("PayCollection")) {
            final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(settings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                                bitmap = getResizedBitmap(bitmap, 80, 120);

                                woyouService.printBitmap(bitmap, callback);
                            }
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont("Receipt Voucher", "", 30, callback);
                            woyouService.setFontSize(26, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);

                            woyouService.printColumnsText(new String[]{"Receipt No.", ":", pay_collection.get_collection_code()}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Date", ":", pay_collection.get_collection_date()}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            String amount;
                            amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                            woyouService.printColumnsText(new String[]{"Amount", ":", amount}, new int[]{11, 1, 40}, new int[]{0, 0, 0}, callback);
                            String[] str = amount.split("\\.");
                            if (str.length == 1) {
                                woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, callback);
                            } else if (str.length == 2) {
                                if (Integer.parseInt(str[1].toString()) == 0) {
                                    woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, callback);

                                } else {
                                    woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only "}, new int[]{8, 1, 40}, new int[]{0, 0, 0}, callback);
                                }

                            }

                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                            woyouService.printColumnsText(new String[]{"Received From", ":", contact.get_name()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Cash/Cheque", ":", pay_collection.get_payment_mode()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"On Account", ":", pay_collection.get_on_account()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Remarks", ":", pay_collection.get_remarks()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);

                            if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                                Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                                woyouService.printColumnsText(new String[]{"Bank Name", ":", bank.get_bank_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                woyouService.printColumnsText(new String[]{"Cheque No", ":", pay_collection.get_ref_no()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);

                            }

                            ArrayList<Pay_Collection_Detail> pay_collection_detail = Pay_Collection_Detail.getAllPay_Collection_Detail(getApplicationContext(), " WHERE collection_code='" + pay_collection.get_collection_code() + "'");
                            if (pay_collection_detail.size() > 0) {
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                woyouService.printColumnsText(new String[]{"Order No", "Amount"}, new int[]{13, 13}, new int[]{0, 0}, callback);
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                for (int i = 0; i < pay_collection_detail.size(); i++) {
                                    woyouService.printColumnsText(new String[]{pay_collection_detail.get(i).get_invoice_no(), Globals.myNumberFormat2QtyDecimal(Double.parseDouble(pay_collection_detail.get(i).get_amount()), qty_decimal_check)}, new int[]{13, 13}, new int[]{0, 0}, callback);
                                }
                            }
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            woyouService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("Receiver Signature", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {

            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            woyouService.setFontSize(35, callback);
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont("RECEIPT \n", "", 35, callback);
                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                } else {
                                    woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 35, callback);
                                }
                            } catch (Exception ex) {

                            }
                            woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 35, callback);
                            woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 35, callback);
                            woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 35, callback);
                            woyouService.setAlignment(0, callback);

                            woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
//                            woyouService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"RECEIPT NO.", ":", strOrderNo}, new int[]{12, 1, 16}, new int[]{0, 0, 0}, callback);
//                        woyouService.printTextWithFont(" \n", "", 30, callback);
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.setAlignment(1, callback);
                                woyouService.printBarCode(strOrderNo, 8, 80, 150, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.setAlignment(0, callback);
                            }
                            woyouService.printColumnsText(new String[]{"IN DT", ":", DateUtill.PaternDatePrintDate(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"IN TM", ":", DateUtill.PaternDatePrintTime(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, callback);
                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();

                                String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_Code");
                                int len = 12;
                                if (strItemName.length() > len) {
                                    strItemName = strItemName.substring(0, len);
                                } else {
                                    for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                        strItemName = strItemName + " ";
                                    }
                                }

                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);
                                //sale_price = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check);
                                String line_total;
                                woyouService.printTextWithFont("VEHICLE TYPE: \n", "", 30, callback);
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                woyouService.printColumnsText(new String[]{strItemName, " Rs." + sale_price}, new int[]{11, 9}, new int[]{0, 0}, callback);

                                count++;
                            }

                            woyouService.printColumnsText(new String[]{"V.NO : ", orders.get_remarks()}, new int[]{8, 20}, new int[]{0, 0}, callback);
                            woyouService.setAlignment(1, callback);

                            if (!settings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(settings.get_Footer_Text() + "\n", "", 35, callback);
                            }
                            woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 35, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            if (decimal_check.equals("2")) {
                if (settings.get_Print_Memo().equals("1")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                                String Print_type = "0";

                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 40, callback);
                                woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 35, callback);
                                woyouService.setFontSize(30, callback);
                                if (orders.get_table_code().equals("")) {
                                } else {
                                    woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                                }

                                ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                                if (order_detail.size() > 0) {
                                    Double total = 0d;

                                    woyouService.printColumnsText(new String[]{"Name", "Qty", "Price", "Total"}, new int[]{8, 5, 5, 10}, new int[]{0, 0, 0, 0}, callback);
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                    for (int i = 0; i < order_detail.size(); i++) {
                                        total = total + Double.parseDouble(order_detail.get(i).get_line_total());
                                        Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                                        woyouService.setFontSize(30, callback);
                                        woyouService.printColumnsText(new String[]{item.get_item_name().substring(0, 8), "X" + order_detail.get(i).get_quantity(), Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_sale_price()), decimal_check), Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_line_total()), decimal_check)}, new int[]{8, 4, 6, 10}, new int[]{0, 0, 0, 0}, callback);
                                    }
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    woyouService.printColumnsText(new String[]{"Total Amount", ":", Globals.myNumberFormat2Price(total, decimal_check)}, new int[]{12, 1, 20}, new int[]{0, 0, 0}, callback);
                                }

                                woyouService.printTextWithFont("\n", "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setAlignment(1, callback);
                                    Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                    if (bitmap != null) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                        bitmap = getResizedBitmap(bitmap, 80, 120);
                                        woyouService.printBitmap(bitmap, callback);
                                    }

                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 30, callback);
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 30, callback);
                                        }
                                    } catch (Exception ex) {

                                    }
                                    woyouService.setFontSize(30, callback);
                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {

                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont(Globals.PrintOrder + "\n", "", 30, callback);
                                    ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    ArrayList<String> arrayList = new ArrayList<String>();
                                    woyouService.setAlignment(0, callback);
                                    if (order_payment_array.size() > 0) {

                                        for (int i = 0; i < order_payment_array.size(); i++) {
                                            arrayList.add(order_payment_array.get(i).get_payment_id());
                                        }

                                        if (arrayList.contains("1") && arrayList.contains("5")) {
                                            woyouService.printTextWithFont("Invoice Type: Credit/Cash\n", "", 30, callback);
                                        } else {
                                            if (arrayList.contains("5")) {
                                                woyouService.printTextWithFont("Invoice Type: Credit\n", "", 30, callback);
                                            } else if (arrayList.contains("1")) {
                                                woyouService.printTextWithFont("Invoice Type: Cash\n", "", 30, callback);
                                            }
                                        }
                                    }

                                    woyouService.setAlignment(1, callback);
                                    if (Globals.strIsBarcodePrint.equals("true")) {
                                        woyouService.printBarCode(strOrderNo, 8, 60, 120, 0, callback);
                                        woyouService.printTextWithFont(" \n", "", 24, callback);
                                    }
                                    woyouService.setAlignment(0, callback);
                                    woyouService.setFontSize(30, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintInvNo, ":", strOrderNo}, new int[]{14, 1, 16}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintInvDate, ":", DateUtill.PaternDate1(orders.get_order_date())}, new int[]{12, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, callback);

                                    if (Globals.ModeResrv.equals("Resv")) {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                        woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        if (contact.get_gstin().length() > 0) {
                                            woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                    } else {
                                        if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                        } else {
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                            woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                            if (contact.get_gstin().length() > 0) {
                                                woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);

                                            }
                                        }
                                    }

                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Item Name\n", "", 30, callback);
                                    woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{7, 9, 15}, new int[]{0, 0, 0}, callback);
                                    woyouService.setAlignment(0, callback);
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    int count = 0;
                                    Double itemFinalTax = 0d;
                                    while (count < order_detail.size()) {

                                        String strItemCode = order_detail.get(count).get_item_code();

                                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                                + strItemCode + "'  GROUP By order_detail.item_Code");

                                        String sale_price;
                                        Double dDisAfterSalePrice = 0d;

//                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                        Double dDisAfter = 0d;
                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                        dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                        String line_total;
                                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                        woyouService.printTextWithFont(strItemName + "\n", "", 30, callback);

                                        Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                        String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                        woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);

//                                      if(true)
//                                      {
//                                        woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), discntTemp, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
//                                      }
//                                      else
//                                      {
//                                         woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
//
//                                        }

                                        String discnt = "";
                                        String disLbl = "";
                                        if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                        } else {
                                            if (Globals.strIsDiscountPrint.equals("true")) {
                                                Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                                discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                                disLbl = "Dis :";
                                            }
                                            woyouService.printColumnsText(new String[]{disLbl, discnt}, new int[]{5, 11}, new int[]{0, 0}, callback);
                                        }

//                                woyouService.printTextWithFont("" + line_total + "\n", "", 30, callback);
                                        woyouService.setAlignment(0, callback);
                                        if (settings.get_HSN_print().equals("true")) {
                                            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                            woyouService.printColumnsText(new String[]{"HSN Code :", item.get_hsn_sac_code()}, new int[]{10, 10}, new int[]{0, 0}, callback);
                                        }
                                        if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                            Tax_Master tax_master = null;
                                            ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                            for (int i = 0; i < order_detail_tax.size(); i++) {
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                                double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                                itemFinalTax += valueFinal;
                                                woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                            }
                                        }
                                        count++;
                                    }

                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    woyouService.printColumnsText(new String[]{"Total Item", ":", orders.get_total_item()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{"Item Quantity", ":", Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check)}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String subtotal = "";
                                    String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                    Cursor cursor1 = database.rawQuery(strTableQry, null);
                                    Tax_Master tax_master;
                                    while (cursor1.moveToNext()) {
                                        subtotal = cursor1.getString(0);
                                    }

                                    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                    String oDiscount = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)), decimal_check);

                                    woyouService.printColumnsText(new String[]{"Subtotal", ":", subtotal}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    if(true)
//                                    {
//                                        woyouService.printColumnsText(new String[]{"Subtotal", ":", oDiscount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    }
//                                    else {
//                                        woyouService.printColumnsText(new String[]{"Subtotal", ":", subtotal}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    }

                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                    if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                        strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                                "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                                "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                        cursor1 = database.rawQuery(strTableQry, null);

                                        while (cursor1.moveToNext()) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                            woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check)}, new int[]{12, 10}, new int[]{0, 0}, callback);
                                        }
                                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    }

                                    String discount = "0";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                    } else {
                                        discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            woyouService.printColumnsText(new String[]{"Discount", ":", Globals.DiscountPer + "%"}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                            woyouService.printColumnsText(new String[]{"Discount", ":", discount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                    }
                                    String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                    String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Total", ":", tatalAftrDis}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String total_tax;
                                    total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                    if (Double.parseDouble(total_tax) > 0d) {
                                        woyouService.printColumnsText(new String[]{"Total Tax", ":", total_tax}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                        String name = "", value = "";
                                        if (cursor.moveToFirst()) {
                                            do {
                                                name = cursor.getString(0);
                                                value = cursor.getString(1);
                                                woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check)}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } while (cursor.moveToNext());
                                        }
                                    }
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    String net_amount;
                                    net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                    String strCurrency;
                                    if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                        strCurrency = "";
                                    } else {
                                        strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                    }
                                    woyouService.printColumnsText(new String[]{"Net Amt", ":", net_amount + "" + strCurrency}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String tender;
                                    tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Tender", ":", tender}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String change_amount;
                                    change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Change", ":", change_amount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (order_payment_array.size() > 0) {
                                        for (int i = 0; i < order_payment_array.size(); i++) {
                                            Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                            String name = "";
                                            if (payment != null) {
                                                name = payment.get_payment_name();
                                                woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{9, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }

                                        }
                                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    }

                                    if (settings.get_Is_Accounts().equals("true")) {
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
//                                              String curAmount = "";
                                                try {
                                                    String strCreditAmt = "", strDeditAmount = "";
                                                    Double creditAmount = 0d,
                                                            debitAmount = 0d;
                                                    Cursor cursor = null;

                                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                    cursor = database.rawQuery(strQry1, null);
                                                    while (cursor.moveToNext()) {
                                                        strCreditAmt = cursor.getString(0);

                                                    }
                                                    creditAmount = Double.parseDouble(strCreditAmt);

                                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                    cursor = database.rawQuery(strQry2, null);
                                                    while (cursor.moveToNext()) {
                                                        strDeditAmount = cursor.getString(0);

                                                    }
                                                    debitAmount = Double.parseDouble(strDeditAmount);
                                                    showAmount = debitAmount + creditAmount;
                                                } catch (Exception ex) {
//                                        curAmount = "0";
                                                }
//                                    double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = cursor1.getString(0);

                                                    }
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                String curAmount = "";
                                                try {
                                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);
                                                    if (cursor1.moveToFirst()) {
                                                        do {
                                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        } while (cursor1.moveToNext());
                                                    }
                                                } catch (Exception ex) {
                                                    curAmount = "0";
                                                }
                                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                double abs1 = Math.abs(ab);
                                                if (ab > 0) {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    }

                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }

                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }

                                                Double strBalance = ab + Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }

                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    if (!settings.get_Footer_Text().equals("")) {
                                        woyouService.printTextWithFont(settings.get_Footer_Text(), "", 24, callback);
                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                    }

                                    woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);

                                }
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Globals.DiscountPer = "";
                                Globals.strOldCrAmt = "0";
                                Globals.setEmpty();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            } else {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                String Print_type = "0";
                                woyouService.setAlignment(1, callback);
                                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    woyouService.printBitmap(bitmap, callback);
                                }

                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 30, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 30, callback);
                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 30, callback);
                                    }
                                } catch (Exception ex) {

                                }
                                woyouService.setFontSize(30, callback);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {

                                } else {
                                    woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(Globals.PrintOrder + "\n", "", 30, callback);
                                ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                ArrayList<String> arrayList = new ArrayList<String>();
                                woyouService.setAlignment(0, callback);
                                if (order_payment_array.size() > 0) {

                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        arrayList.add(order_payment_array.get(i).get_payment_id());
                                    }

                                    if (arrayList.contains("1") && arrayList.contains("5")) {
                                        woyouService.printTextWithFont("Invoice Type: Credit/Cash\n", "", 30, callback);
                                    } else {
                                        if (arrayList.contains("5")) {
                                            woyouService.printTextWithFont("Invoice Type: Credit\n", "", 30, callback);
                                        } else if (arrayList.contains("1")) {
                                            woyouService.printTextWithFont("Invoice Type: Cash\n", "", 30, callback);
                                        }
                                    }
                                }


                                woyouService.setAlignment(1, callback);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    woyouService.printBarCode(strOrderNo, 8, 60, 120, 0, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                }
                                woyouService.setAlignment(0, callback);
                                woyouService.setFontSize(30, callback);
                                woyouService.printColumnsText(new String[]{Globals.PrintInvNo, ":", strOrderNo}, new int[]{14, 1, 16}, new int[]{0, 0, 0}, callback);
                                woyouService.printColumnsText(new String[]{Globals.PrintInvDate, ":", DateUtill.PaternDate1(orders.get_order_date())}, new int[]{12, 1, 20}, new int[]{0, 0, 0}, callback);
                                woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, callback);

                                if (Globals.ModeResrv.equals("Resv")) {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                    woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                } else {
                                    if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                    } else {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                        woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        if (contact.get_gstin().length() > 0) {
                                            woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);

                                        }
                                    }
                                }

                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                woyouService.printTextWithFont("Item Name\n", "", 30, callback);
                                woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{7, 9, 15}, new int[]{0, 0, 0}, callback);
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                int count = 0;
                                Double itemFinalTax = 0d;
                                while (count < order_detail.size()) {

                                    String strItemCode = order_detail.get(count).get_item_code();

                                    String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By order_detail.item_Code");


                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
//                                    String line_total;
//                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
//                                    woyouService.printTextWithFont(strItemName + "\n", "", 30, callback);
//                                    woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                    woyouService.printTextWithFont(strItemName + "\n", "", 30, callback);

                                    Double disTemp = Double.parseDouble(order_detail.get(count).get_sale_price());
                                    String discntTemp = Globals.myNumberFormat2Price(disTemp, decimal_check);

                                    woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);

//                                    if(true)
//                                    {
//                                        woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), discntTemp, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
//                                    }
//                                    else
//                                    {
//                                        woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
//
//                                    }
                                    String discnt = "";
                                    String disLbl = "";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                    } else {

                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                            discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                            disLbl = "Dis :";
                                        }
                                        woyouService.printColumnsText(new String[]{disLbl, discnt}, new int[]{5, 11}, new int[]{0, 0}, callback);
                                    }

//                                woyouService.printTextWithFont("" + line_total + "\n", "", 30, callback);
                                    woyouService.setAlignment(0, callback);
                                    if (settings.get_HSN_print().equals("true")) {
                                        item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                        woyouService.printColumnsText(new String[]{"HSN Code :", item.get_hsn_sac_code()}, new int[]{10, 10}, new int[]{0, 0}, callback);
                                    }
                                    if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                        Tax_Master tax_master = null;
                                        ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                        for (int i = 0; i < order_detail_tax.size(); i++) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                            double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                            itemFinalTax += valueFinal;
                                            woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        }
                                    }
                                    count++;
                                }

                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                woyouService.printColumnsText(new String[]{"Total Item", ":", orders.get_total_item()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                                woyouService.printColumnsText(new String[]{"Item Quantity", ":", Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check)}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                                String subtotal = "";
                                String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                Cursor cursor1 = database.rawQuery(strTableQry, null);
                                Tax_Master tax_master;
                                while (cursor1.moveToNext()) {
                                    subtotal = cursor1.getString(0);
                                }

                                subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);

                                String oDiscount = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)), decimal_check);

                                woyouService.printColumnsText(new String[]{"Subtotal", ":", subtotal}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    if(true)
//                                    {
//                                        woyouService.printColumnsText(new String[]{"Subtotal", ":", oDiscount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    }
//                                    else {
//                                        woyouService.printColumnsText(new String[]{"Subtotal", ":", subtotal}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
//                                    }

                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                    strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                            "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                            "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                        woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check)}, new int[]{12, 10}, new int[]{0, 0}, callback);
                                    }
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                }

                                String discount = "0";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {

                                } else {
                                    discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        woyouService.printColumnsText(new String[]{"Discount", ":", Globals.DiscountPer + "%"}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        woyouService.printColumnsText(new String[]{"Discount", ":", discount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                woyouService.printColumnsText(new String[]{"Total", ":", tatalAftrDis}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                String total_tax;
                                total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                if (Double.parseDouble(total_tax) > 0d) {
                                    woyouService.printColumnsText(new String[]{"Total Tax", ":", total_tax}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                    String name = "", value = "";
                                    if (cursor.moveToFirst()) {
                                        do {
                                            name = cursor.getString(0);
                                            value = cursor.getString(1);
                                            woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check)}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                        } while (cursor.moveToNext());
                                    }
                                }
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                woyouService.printColumnsText(new String[]{"Net Amt", ":", net_amount + "" + strCurrency}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                String tender;
                                tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                woyouService.printColumnsText(new String[]{"Tender", ":", tender}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                String change_amount;
                                change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                woyouService.printColumnsText(new String[]{"Change", ":", change_amount}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (order_payment_array.size() > 0) {
                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                        String name = "";
                                        if (payment != null) {
                                            name = payment.get_payment_name();
                                            woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{9, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }

                                    }
                                    woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                }

                                if (settings.get_Is_Accounts().equals("true")) {
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
//                                    String curAmount = "";
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
                                            }
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }

                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } else {
                                                woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }

                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = ab + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                if (!settings.get_Footer_Text().equals("")) {
                                    woyouService.printTextWithFont(settings.get_Footer_Text(), "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                }

                                woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 30, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    }


    private void PHAPOS(final Orders orders, final ArrayList<Order_Detail> order_detail) {
        if (opr.equals("PayCollection")) {
            final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(settings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                                bitmap = getResizedBitmap(bitmap, 80, 120);

                                woyouService.printBitmap(bitmap, callback);
                            }
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont("Receipt Voucher", "", 30, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printColumnsText(new String[]{"Receipt No.", ":", pay_collection.get_collection_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Date", ":", pay_collection.get_collection_date()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                            String amount;
                            amount = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check);
                            woyouService.printColumnsText(new String[]{"Amount", ":", amount}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);

                            String[] str = amount.split("\\.");
                            if (str.length == 1) {
                                woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " Only "}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                            } else if (str.length == 2) {
                                if (Integer.parseInt(str[1].toString()) == 0) {
                                    woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only "}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);

                                } else {
                                    woyouService.printColumnsText(new String[]{"In Words", ":", Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only "}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                                }

                            }


                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
                            woyouService.printColumnsText(new String[]{"Received From", ":", contact.get_name()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Cash/Cheque", ":", pay_collection.get_payment_mode()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"On Account", ":", pay_collection.get_on_account()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"Remarks", ":", pay_collection.get_remarks()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);

                            if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                                Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                                woyouService.printColumnsText(new String[]{"Bank Name", ":", bank.get_bank_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                                woyouService.printColumnsText(new String[]{"Cheque No", ":", pay_collection.get_ref_no()}, new int[]{13, 1, 20}, new int[]{0, 0, 0}, callback);

                            }

                            ArrayList<Pay_Collection_Detail> pay_collection_detail = Pay_Collection_Detail.getAllPay_Collection_Detail(getApplicationContext(), " WHERE collection_code='" + pay_collection.get_collection_code() + "'");
                            if (pay_collection_detail.size() > 0) {
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                woyouService.printColumnsText(new String[]{"Order No", "Amount"}, new int[]{13, 13}, new int[]{0, 0}, callback);
                                woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                for (int i = 0; i < pay_collection_detail.size(); i++) {
                                    woyouService.printColumnsText(new String[]{pay_collection_detail.get(i).get_invoice_no(), Globals.myNumberFormat2QtyDecimal(Double.parseDouble(pay_collection_detail.get(i).get_amount()), qty_decimal_check)}, new int[]{13, 13}, new int[]{0, 0}, callback);
                                }
                            }
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            woyouService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("Receiver Signature", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                            woyouService.printTextWithFont("\n", "", 24, callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {

            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                            String Print_type = "0";
                            woyouService.setFontSize(35, callback);
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont("RECEIPT \n", "", 35, callback);
                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                } else {
                                    woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 35, callback);
                                }
                            } catch (Exception ex) {

                            }
                            woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 35, callback);
                            woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 35, callback);
                            woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 35, callback);
                            woyouService.setAlignment(0, callback);

                            woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
//                            woyouService.printColumnsText(new String[]{"Cashier", ":", user.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"RECEIPT NO.", ":", strOrderNo}, new int[]{12, 1, 16}, new int[]{0, 0, 0}, callback);
//                        woyouService.printTextWithFont(" \n", "", 30, callback);
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.setAlignment(1, callback);
                                woyouService.printBarCode(strOrderNo, 8, 80, 150, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.setAlignment(0, callback);
                            }
                            woyouService.printColumnsText(new String[]{"IN DT", ":", DateUtill.PaternDatePrintDate(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, callback);
                            woyouService.printColumnsText(new String[]{"IN TM", ":", DateUtill.PaternDatePrintTime(orders.get_order_date())}, new int[]{5, 1, 22}, new int[]{0, 0, 0}, callback);
                            int count = 0;
                            Double itemFinalTax = 0d;
                            while (count < order_detail.size()) {

                                String strItemCode = order_detail.get(count).get_item_code();

                                String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By order_detail.item_Code");
                                int len = 12;
                                if (strItemName.length() > len) {
                                    strItemName = strItemName.substring(0, len);
                                } else {
                                    for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                        strItemName = strItemName + " ";
                                    }
                                }

                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);
                                //sale_price = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check);
                                String line_total;
                                woyouService.printTextWithFont("VEHICLE TYPE: \n", "", 30, callback);
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                woyouService.printColumnsText(new String[]{strItemName, " Rs." + sale_price}, new int[]{11, 9}, new int[]{0, 0}, callback);
                                count++;
                            }

                            woyouService.printColumnsText(new String[]{"V.NO : ", orders.get_remarks()}, new int[]{8, 20}, new int[]{0, 0}, callback);

                            woyouService.setAlignment(1, callback);

                            if (!settings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(settings.get_Footer_Text() + "\n", "", 35, callback);
                            }

                            woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 35, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                        }
                        Globals.setEmpty();
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.DiscountPer = "";
                        Globals.strOldCrAmt = "0";
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            if (settings.get_Print_Lang().equals("0")) {
                if (settings.get_Print_Memo().equals("1")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                                String Print_type = "0";
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback);
                                woyouService.setFontSize(30, callback);
                                if (orders.get_table_code().equals("")) {
                                } else {
                                    woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                                }

                                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);

                                ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                                if (order_detail.size() > 0) {
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printColumnsText(new String[]{"Name", "Quantity", "Price", "Total"}, new int[]{12, 9, 8, 10}, new int[]{0, 0, 0, 0}, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                    for (int i = 0; i < order_detail.size(); i++) {
                                        Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                                        woyouService.setFontSize(35, callback);
                                        woyouService.printColumnsText(new String[]{item.get_item_name(), " X " + order_detail.get(i).get_quantity(), order_detail.get(i).get_sale_price(), Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(i).get_line_total()), decimal_check)}, new int[]{12, 5, 7, 10}, new int[]{0, 0, 0, 0}, callback);
                                    }
                                }

                                woyouService.printTextWithFont("\n", "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                                woyouService.cutPaper(callback);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setAlignment(1, callback);
                                    Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                    if (bitmap != null) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                        bitmap = getResizedBitmap(bitmap, 80, 120);
                                        woyouService.printBitmap(bitmap, callback);
                                    }

                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 30, callback);
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 30, callback);
                                        }
                                    } catch (Exception ex) {

                                    }
                                    woyouService.setFontSize(30, callback);
                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {

                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTLbl, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont(Globals.PrintOrder + "\n", "", 30, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.strIsBarcodePrint.equals("true")) {
                                        woyouService.printBarCode(strOrderNo, 8, 60, 120, 0, callback);
                                        woyouService.printTextWithFont(" \n", "", 24, callback);
                                    }
                                    woyouService.setAlignment(0, callback);
                                    woyouService.setFontSize(30, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintInvNo, ":", strOrderNo}, new int[]{14, 1, 16}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintInvDate, ":", DateUtill.PaternDate1(orders.get_order_date())}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{14, 1, 18}, new int[]{0, 0, 0}, callback);

                                    if (Globals.ModeResrv.equals("Resv")) {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                                        woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                        if (contact.get_gstin().length() > 0) {
                                            woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                    } else {
                                        if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                        } else {
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                            woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                            if (contact.get_gstin().length() > 0) {
                                                woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);

                                            }
                                        }
                                    }

                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Item Name\n", "", 30, callback);
                                    woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{12, 12, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.setAlignment(0, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    int count = 0;
                                    Double itemFinalTax = 0d;
                                    while (count < order_detail.size()) {

                                        String strItemCode = order_detail.get(count).get_item_code();

                                        String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                                + strItemCode + "'  GROUP By order_detail.item_Code");


                                        String sale_price;
                                        Double dDisAfterSalePrice = 0d;

//                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                                        Double dDisAfter = 0d;
                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                        dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                                        String line_total;
                                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                        woyouService.printTextWithFont(strItemName + "\n", "", 30, callback);
                                        woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{11, 11, 20}, new int[]{0, 0, 0}, callback);

                                        String discnt = "";
                                        String disLbl = "";
                                        if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                        } else {

                                            if (Globals.strIsDiscountPrint.equals("true")) {
                                                Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                                discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                                disLbl = "Dis :";
                                            }
                                            woyouService.printColumnsText(new String[]{disLbl, discnt}, new int[]{5, 11}, new int[]{0, 0}, callback);
                                        }

//                                woyouService.printTextWithFont("" + line_total + "\n", "", 30, callback);
                                        woyouService.setAlignment(0, callback);
                                        if (settings.get_HSN_print().equals("true")) {
                                            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                                            woyouService.printColumnsText(new String[]{"HSN Code :", item.get_hsn_sac_code()}, new int[]{10, 10}, new int[]{0, 0}, callback);
                                        }
                                        if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                            Tax_Master tax_master = null;
                                            ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                            for (int i = 0; i < order_detail_tax.size(); i++) {
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                                double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                                itemFinalTax += valueFinal;
                                                woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                            }
                                        }
                                        count++;
                                    }

                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printColumnsText(new String[]{"Total Item", ":", orders.get_total_item()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printColumnsText(new String[]{"Item Quantity", ":", Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check)}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String subtotal = "";
                                    String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                    Cursor cursor1 = database.rawQuery(strTableQry, null);
                                    Tax_Master tax_master;
                                    while (cursor1.moveToNext()) {
                                        subtotal = cursor1.getString(0);
                                    }

                                    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Subtotal", ":", subtotal}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                    if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                        strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                                "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                                "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                        cursor1 = database.rawQuery(strTableQry, null);

                                        while (cursor1.moveToNext()) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
//                                            woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check)}, new int[]{14, 10}, new int[]{0, 0}, callback);
                                            woyouService.printTextWithFont(tax_master.get_tax_name() + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + " \n", "", 24, callback);
                                        }
                                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    }

                                    String discount = "0";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                    } else {
                                        discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            woyouService.printColumnsText(new String[]{"Discount", ":", Globals.DiscountPer + "%"}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                            woyouService.printColumnsText(new String[]{"Discount", ":", discount}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                    }
                                    String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                    String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Total", ":", tatalAftrDis}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String total_tax;
                                    total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
                                    if (Double.parseDouble(total_tax) > 0d) {
                                        woyouService.printColumnsText(new String[]{"Total Tax", ":", total_tax}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                        Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                        String name = "", value = "";
                                        if (cursor.moveToFirst()) {
                                            do {
                                                name = cursor.getString(0);
                                                value = cursor.getString(1);
                                                woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check)}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } while (cursor.moveToNext());
                                        }
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    String net_amount;
                                    net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                    String strCurrency;
                                    if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                        strCurrency = "";
                                    } else {
                                        strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                    }
                                    woyouService.printColumnsText(new String[]{"Net Amt", ":", net_amount + "" + strCurrency}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String tender;
                                    tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Tender", ":", tender}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    String change_amount;
                                    change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                    woyouService.printColumnsText(new String[]{"Change", ":", change_amount}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (order_payment_array.size() > 0) {
                                        for (int i = 0; i < order_payment_array.size(); i++) {
                                            Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                            String name = "";
                                            if (payment != null) {
                                                name = payment.get_payment_name();
                                                woyouService.printTextWithFont(name + " \n", "", 24, callback);
                                                woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + " \n", "", 24, callback);
//                                                woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{16, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }

                                        }
                                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    }

                                    if (settings.get_Is_Accounts().equals("true")) {
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
//                                    String curAmount = "";
                                                try {
                                                    String strCreditAmt = "", strDeditAmount = "";
                                                    Double creditAmount = 0d,
                                                            debitAmount = 0d;
                                                    Cursor cursor = null;

                                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                    cursor = database.rawQuery(strQry1, null);
                                                    while (cursor.moveToNext()) {
                                                        strCreditAmt = cursor.getString(0);

                                                    }
                                                    creditAmount = Double.parseDouble(strCreditAmt);

                                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                    cursor = database.rawQuery(strQry2, null);
                                                    while (cursor.moveToNext()) {
                                                        strDeditAmount = cursor.getString(0);
                                                    }
                                                    debitAmount = Double.parseDouble(strDeditAmount);
                                                    showAmount = debitAmount + creditAmount;
                                                } catch (Exception ex) {
//                                        curAmount = "0";
                                                }
//                                    double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{14, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{14, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                                        woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }

                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                            }
                                        } else {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                String curAmount = "";
                                                try {
                                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);
                                                    if (cursor1.moveToFirst()) {
                                                        do {
                                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        } while (cursor1.moveToNext());
                                                    }
                                                } catch (Exception ex) {
                                                    curAmount = "0";
                                                }
                                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                double abs1 = Math.abs(ab);
//                                            double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                if (abs1 > 0) {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{14, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{14, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    }
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } else {
                                                    woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }

                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                Double strBalance = ab + Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }

                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    if (!settings.get_Footer_Text().equals("")) {
                                        woyouService.printTextWithFont(settings.get_Footer_Text(), "", 24, callback);
                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                    }

                                    woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 30, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Globals.DiscountPer = "";
                                Globals.strOldCrAmt = "0";
                                Globals.setEmpty();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                }
            } else if (settings.get_Print_Lang().equals("1")) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                String strPOSNo = "لحم." + " ";
                                String strGSTNo = "رقم ضريبة السلع والخدمات" + "  ";
                                String strOrderNum = "رقم الطلب" + "  ";
                                String strOrderDate = "تاريخ الطلب" + "  ";
                                String strCashier = "أمين الصندوق" + " ";

                                woyouService.setAlignment(1, callback);
                                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    woyouService.printBitmap(bitmap, callback);
                                }

                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 32, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 30, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 30, callback);
                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 30, callback);
                                    }
                                } catch (Exception ex) {

                                }
                                woyouService.setFontSize(30, callback);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                    woyouService.printTextWithFont(Globals.objLPR.getLicense_No() + " \n", "", 24, callback);
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("فاتورة ضريبية" + "\n", "", 30, callback);

                                woyouService.setAlignment(1, callback);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    woyouService.printBarCode(strOrderNo, 8, 60, 120, 1, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                }
                                woyouService.setAlignment(0, callback);
                                woyouService.setFontSize(30, callback);
//                            woyouService.setAlignment(2, callback);

                                woyouService.printTextWithFont(strOrderNum + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_order_code() + " \n", "", 24, callback);
                                woyouService.printTextWithFont(strOrderDate + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_order_date() + " \n", "", 24, callback);
                                woyouService.printTextWithFont(strPOSNo + " \n", "", 24, callback);
                                woyouService.printTextWithFont(Globals.objLPD.getDevice_Name() + " \n", "", 24, callback);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printTextWithFont(strCashier + " \n", "", 24, callback);
                                woyouService.printTextWithFont(user.get_name() + " \n", "", 24, callback);

                                if (Globals.ModeResrv.equals("Resv")) {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code ='" + Globals.strContact_Code + "'");
                                    String strCustomerName = "Customer/زبون" + "  ";
                                    woyouService.printTextWithFont(strCustomerName + "\n", "", 24, callback);
                                    woyouService.printTextWithFont(contact.get_name() + "\n", "", 24, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(contact.get_gstin() + " \n", "", 24, callback);
                                    }
                                } else {
                                    if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                    } else {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                        String strCustomerName = "زبون" + "  ";
                                        woyouService.printTextWithFont(strCustomerName + "\n", "", 24, callback);
                                        woyouService.printTextWithFont(contact.get_name() + "\n", "", 24, callback);
                                        if (contact.get_gstin().length() > 0) {
                                            woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(contact.get_gstin() + " \n", "", 24, callback);

                                        }
                                    }
                                }
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                woyouService.printTextWithFont("اسم العنصر\n", "", 30, callback);
                                woyouService.printTextWithFont("الكمية           السعر            مجموع\n", "", 26, callback);
//                          woyouService.printColumnsText(new String[]{"Qty/الكمية","Price/السعر","Total/مجموع"}, new int[]{10,10,10}, new int[]{0,0,0}, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                int count = 0;
                                Double itemFinalTax = 0d;
                                woyouService.setAlignment(0, callback);
                                while (count < order_detail.size()) {
                                    String strItemCode = order_detail.get(count).get_item_code();

                                    String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By order_detail.item_Code");

                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    int len = 15;
                                    if (strItemName.length() > len) {
                                        strItemName = strItemName.substring(0, len);
                                    } else {
                                        for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                            strItemName = strItemName + " ";
                                        }
                                    }

                                    try {
//                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                                        Double dDisAfter = 0d;
                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                        dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);
                                    } catch (Exception ex) {
                                        dDisAfterSalePrice = 0d;
                                        sale_price = "Error";
                                    }

                                    String line_total = "";
                                    try {
                                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                    } catch (Exception ex) {
                                        line_total = "error";
                                    }
                                    woyouService.printTextWithFont(strItemName + "\n", "", 24, callback);

                                    woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{11, 11, 20}, new int[]{0, 0, 0}, callback);
                                    //woyouService.printTextWithFont( " : Line No 4074 \n", "", 24, callback);
                                    String discnt = "";
                                    String disLbl = "";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                    } else {

                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            Double dis = 0d;
                                            try {
                                                dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                            } catch (Exception ex) {
                                                dis = 0d;
                                            }

                                            discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                            disLbl = "Dis :";
                                        }
                                        //woyouService.printTextWithFont( " : Line No 4096 \n", "", 24, callback);
                                        woyouService.printColumnsText(new String[]{disLbl, discnt}, new int[]{5, 11}, new int[]{0, 0}, callback);
                                    }

                                    if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                        Tax_Master tax_master = null;
                                        ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                        for (int i = 0; i < order_detail_tax.size(); i++) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                            double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                            itemFinalTax += valueFinal;
                                            //woyouService.printTextWithFont( " : Line No 4107 \n", "", 24, callback);
                                            woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        }
                                    }

                                    count++;
                                }


                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                woyouService.printTextWithFont("البند الكلي" + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_total_item() + " \n", "", 24, callback);
                                woyouService.printTextWithFont("البند الكمية" + " \n", "", 24, callback);
                                woyouService.printTextWithFont(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + " \n", "", 24, callback);


                                String subtotal = "";
                                String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                Cursor cursor1 = database.rawQuery(strTableQry, null);
                                Tax_Master tax_master;
                                while (cursor1.moveToNext()) {
                                    subtotal = cursor1.getString(0);
                                }
                                try {
                                    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);
                                } catch (Exception ex) {
                                    //subtotal = "0";
                                }

                                String strSbtotal = "حاصل الجمع" + " ";
                                woyouService.printTextWithFont(strSbtotal + " \n", "", 24, callback);
                                woyouService.printTextWithFont(subtotal + " \n", "", 24, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
//
                                if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                    strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                            "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                            "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
//                                  woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check)}, new int[]{20, 10}, new int[]{0, 0}, callback);
                                        woyouService.printTextWithFont(tax_master.get_tax_name() + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + " \n", "", 24, callback);
//                                    woyouService.printTextWithFont( + ":" +  + " \n", "", 24, callback);
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                }

                                String discount = "0";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                } else {
                                    discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        String strdiscnt = "خصم" + "  ";
                                        woyouService.printTextWithFont(strdiscnt + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(Globals.DiscountPer + "%" + " \n", "", 24, callback);

                                        strdiscnt = "خصم" + "  ";
                                        woyouService.printTextWithFont(strdiscnt + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(discount + " \n", "", 24, callback);
                                        //woyouService.printColumnsText(new String[]{"Discount", ":", discount}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);

                                String strAftrDis = "مجموع" + "  ";
                                woyouService.printTextWithFont(strAftrDis + " \n", "", 24, callback);
                                woyouService.printTextWithFont(tatalAftrDis + " \n", "", 24, callback);
//
//
                                String total_tax = "0";
////
                                total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
////
                                if (Double.parseDouble(total_tax) > 0d) {
                                    String strTotal_tax = "ضريبة" + "  ";
                                    woyouService.printTextWithFont(strTotal_tax + " \n", "", 24, callback);
                                    woyouService.printTextWithFont(total_tax + " \n", "", 24, callback);

                                    woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);

                                    Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                    String name = "", value = "";
                                    if (cursor.moveToFirst()) {
                                        do {
                                            name = cursor.getString(0);
                                            value = cursor.getString(1);
                                            //woyouService.printColumnsText(new String[]{name,":", Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check)}, new int[]{20, 2, 10}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont(name + "        " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(name + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + " \n", "", 24, callback);
                                        } while (cursor.moveToNext());
                                    }
                                }

                                woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);
                                String net_amount;

                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                String strNet_amount = "كمية الشبكة" + "  ";
                                woyouService.printTextWithFont(strNet_amount + " \n", "", 24, callback);
                                woyouService.printTextWithFont(net_amount + " \n", "", 24, callback);
                                String tender;
                                tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                String strTender = "مناقصة" + "  ";
                                woyouService.printTextWithFont(strTender + " \n", "", 24, callback);
                                woyouService.printTextWithFont(tender + " \n", "", 24, callback);
                                String change_amount;
                                change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                String strChange_amount = "يتغيرون" + "  ";
                                woyouService.printTextWithFont(strChange_amount + " \n", "", 24, callback);
                                woyouService.printTextWithFont(change_amount + " \n", "", 24, callback);

                                woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);
                                ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (order_payment_array.size() > 0) {
                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                        String name = "";
                                        if (payment != null) {
                                            name = payment.get_payment_name();
                                            woyouService.printTextWithFont(name + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + " \n", "", 24, callback);
//                                        woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{16, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }

                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                if (!settings.get_Footer_Text().equals("")) {
                                    woyouService.printTextWithFont(settings.get_Footer_Text(), "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                }

                                woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 30, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);

                                //woyouService.printColumnsText(new String[]{orders.get_total_item(),":","Tender/مناقصة"}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            woyouService.cutPaper(callback);
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                });
            } else if (settings.get_Print_Lang().equals("2")) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
                                String strPOSNo = "Device ID/لحم." + " ";
                                String strGSTNo = Globals.GSTLbl + " ";
                                String strOrderNum = "Invoice Number/رقم الطلب" + "  ";
                                String strOrderDate = "Invoice Date/تاريخ الطلب" + "  ";
                                String strCashier = "Salesperson/أمين الصندوق" + " ";

                                woyouService.setAlignment(1, callback);
                                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    woyouService.printBitmap(bitmap, callback);
                                }

                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 32, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 30, callback);
                                woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 30, callback);
                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 30, callback);
                                    }
                                } catch (Exception ex) {

                                }
                                woyouService.setFontSize(30, callback);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                    woyouService.printTextWithFont(Globals.objLPR.getLicense_No() + " \n", "", 24, callback);
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("TAX INVOICE/فاتورة ضريبية" + "\n", "", 30, callback);


                                woyouService.setAlignment(1, callback);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    woyouService.printBarCode(strOrderNo, 8, 60, 120, 1, callback);
                                    woyouService.printTextWithFont(" \n", "", 24, callback);
                                }
                                woyouService.setAlignment(0, callback);
                                woyouService.setFontSize(30, callback);
//                            woyouService.setAlignment(2, callback);

                                woyouService.printTextWithFont(strOrderNum + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_order_code() + " \n", "", 24, callback);
                                woyouService.printTextWithFont(strOrderDate + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_order_date() + " \n", "", 24, callback);
                                woyouService.printTextWithFont(strPOSNo + " \n", "", 24, callback);
                                woyouService.printTextWithFont(Globals.objLPD.getDevice_Name() + " \n", "", 24, callback);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                woyouService.printTextWithFont(strCashier + " \n", "", 24, callback);
                                woyouService.printTextWithFont(user.get_name() + " \n", "", 24, callback);

                                if (Globals.ModeResrv.equals("Resv")) {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code ='" + Globals.strContact_Code + "'");
                                    String strCustomerName = "Customer/زبون" + "  ";
                                    woyouService.printTextWithFont(strCustomerName + "\n", "", 24, callback);
                                    woyouService.printTextWithFont(contact.get_name() + "\n", "", 24, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(contact.get_gstin() + " \n", "", 24, callback);
                                    }
                                } else {
                                    if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {

                                    } else {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                                        String strCustomerName = "Customer/زبون" + "  ";
                                        woyouService.printTextWithFont(strCustomerName + "\n", "", 24, callback);
                                        woyouService.printTextWithFont(contact.get_name() + "\n", "", 24, callback);
                                        if (contact.get_gstin().length() > 0) {
                                            woyouService.printTextWithFont(strGSTNo + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(contact.get_gstin() + " \n", "", 24, callback);

                                        }
                                    }
                                }
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                woyouService.printTextWithFont("Item Name/اسم العنصر\n", "", 30, callback);
                                woyouService.printTextWithFont("Qty/الكمية     Price/السعر     Total/مجموع\n", "", 26, callback);
//                          woyouService.printColumnsText(new String[]{"Qty/الكمية","Price/السعر","Total/مجموع"}, new int[]{10,10,10}, new int[]{0,0,0}, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                int count = 0;
                                Double itemFinalTax = 0d;
                                woyouService.setAlignment(0, callback);
                                while (count < order_detail.size()) {
                                    String strItemCode = order_detail.get(count).get_item_code();

                                    String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By order_detail.item_Code");

                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    int len = 15;
                                    if (strItemName.length() > len) {
                                        strItemName = strItemName.substring(0, len);
                                    } else {
                                        for (int sLen = strItemName.length(); sLen < len; sLen++) {
                                            strItemName = strItemName + " ";
                                        }
                                    }

                                    try {
//                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
//                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                                        Double dDisAfter = 0d;
                                        dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                                        dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);
                                    } catch (Exception ex) {
                                        dDisAfterSalePrice = 0d;
                                        sale_price = "Error";
                                    }

                                    String line_total = "";
                                    try {
                                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);
                                    } catch (Exception ex) {
                                        line_total = "error";
                                    }
                                    woyouService.printTextWithFont(strItemName + "\n", "", 24, callback);

                                    woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check), sale_price, line_total}, new int[]{11, 11, 20}, new int[]{0, 0, 0}, callback);
                                    //woyouService.printTextWithFont( " : Line No 4074 \n", "", 24, callback);
                                    String discnt = "";
                                    String disLbl = "";
                                    if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                    } else {

                                        if (Globals.strIsDiscountPrint.equals("true")) {
                                            Double dis = 0d;
                                            try {
                                                dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                                            } catch (Exception ex) {
                                                dis = 0d;
                                            }

                                            discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                                            disLbl = "Dis :";
                                        }
                                        //woyouService.printTextWithFont( " : Line No 4096 \n", "", 24, callback);
                                        woyouService.printColumnsText(new String[]{disLbl, discnt}, new int[]{5, 11}, new int[]{0, 0}, callback);
                                    }

                                    if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                                        Tax_Master tax_master = null;
                                        ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                                        for (int i = 0; i < order_detail_tax.size(); i++) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);
                                            double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                                            itemFinalTax += valueFinal;
                                            //woyouService.printTextWithFont( " : Line No 4107 \n", "", 24, callback);
                                            woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(valueFinal, decimal_check)}, new int[]{15, 6}, new int[]{0, 0}, callback);
                                        }
                                    }

                                    count++;
                                }


                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                woyouService.printTextWithFont("Total Item/البند الكلي" + " \n", "", 24, callback);
                                woyouService.printTextWithFont(orders.get_total_item() + " \n", "", 24, callback);
                                woyouService.printTextWithFont("Item Quantity/البند الكمية" + " \n", "", 24, callback);
                                woyouService.printTextWithFont(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check) + " \n", "", 24, callback);


                                String subtotal = "";
                                String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
                                Cursor cursor1 = database.rawQuery(strTableQry, null);
                                Tax_Master tax_master;
                                while (cursor1.moveToNext()) {
                                    subtotal = cursor1.getString(0);
                                }
                                try {
                                    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);
                                } catch (Exception ex) {
                                    //subtotal = "0";
                                }

                                String strSbtotal = "Subtotal/حاصل الجمع" + " ";
                                woyouService.printTextWithFont(strSbtotal + " \n", "", 24, callback);
                                woyouService.printTextWithFont(subtotal + " \n", "", 24, callback);
                                woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
//
                                if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                                    strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                                            "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                                            "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
//                                  woyouService.printColumnsText(new String[]{tax_master.get_tax_name(), Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check)}, new int[]{20, 10}, new int[]{0, 0}, callback);
                                        woyouService.printTextWithFont(tax_master.get_tax_name() + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + " \n", "", 24, callback);
//                                    woyouService.printTextWithFont( + ":" +  + " \n", "", 24, callback);
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                }

                                String discount = "0";
                                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                                } else {
                                    discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                                    if (Globals.strIsDiscountPrint.equals("true")) {
                                        String strdiscnt = "Discount/خصم" + "  ";
                                        woyouService.printTextWithFont(strdiscnt + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(Globals.DiscountPer + "%" + " \n", "", 24, callback);

                                        strdiscnt = "Discount/خصم" + "  ";
                                        woyouService.printTextWithFont(strdiscnt + " \n", "", 24, callback);
                                        woyouService.printTextWithFont(discount + " \n", "", 24, callback);
                                        //woyouService.printColumnsText(new String[]{"Discount", ":", discount}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }
                                }
                                String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
                                String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
//
//                            String discount;
//                            discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
//                            String strdiscnt = "Discount/خصم" + "  ";
//                            woyouService.printTextWithFont(strdiscnt + " \n", "", 24, callback);
//                            woyouService.printTextWithFont(discount + " \n", "", 24, callback);
//
                                //woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);
//
//
//                            String ttl_aftr_dis = "";
//                            try {
//                                ttl_aftr_dis = (Double.parseDouble(subtotal) - Double.parseDouble(discount)) + "";
//                            } catch (Exception ex) {
//
//                            }
//                            String tatalAftrDis = "";
//                            try {
//                                tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
//                            } catch (Exception ex) {
//
//                            }


                                String strAftrDis = "Total/مجموع" + "  ";
                                woyouService.printTextWithFont(strAftrDis + " \n", "", 24, callback);
                                woyouService.printTextWithFont(tatalAftrDis + " \n", "", 24, callback);
//
//
                                String total_tax = "0";
////
                                total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
////
                                if (Double.parseDouble(total_tax) > 0d) {
                                    String strTotal_tax = "Tax/ضريبة" + "  ";
                                    woyouService.printTextWithFont(strTotal_tax + " \n", "", 24, callback);
                                    woyouService.printTextWithFont(total_tax + " \n", "", 24, callback);

                                    woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);

                                    Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                                    String name = "", value = "";
                                    if (cursor.moveToFirst()) {
                                        do {
                                            name = cursor.getString(0);
                                            value = cursor.getString(1);
                                            //woyouService.printColumnsText(new String[]{name,":", Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check)}, new int[]{20, 2, 10}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont(name + "        " + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(name + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + " \n", "", 24, callback);
                                        } while (cursor.moveToNext());
                                    }
                                }

                                woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);
                                String net_amount;

                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);
                                String strNet_amount = "Net Amount/كمية الشبكة" + "  ";
                                woyouService.printTextWithFont(strNet_amount + " \n", "", 24, callback);
                                woyouService.printTextWithFont(net_amount + " \n", "", 24, callback);
                                String tender;
                                tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
                                String strTender = "Tender/مناقصة" + "  ";
                                woyouService.printTextWithFont(strTender + " \n", "", 24, callback);
                                woyouService.printTextWithFont(tender + " \n", "", 24, callback);
                                String change_amount;
                                change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
                                String strChange_amount = "Change/يتغيرون" + "  ";
                                woyouService.printTextWithFont(strChange_amount + " \n", "", 24, callback);
                                woyouService.printTextWithFont(change_amount + " \n", "", 24, callback);

                                woyouService.printTextWithFont("--------------------------------------------\n", "", 26, callback);
                                ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (order_payment_array.size() > 0) {
                                    for (int i = 0; i < order_payment_array.size(); i++) {
                                        Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                        String name = "";
                                        if (payment != null) {
                                            name = payment.get_payment_name();
                                            woyouService.printTextWithFont(name + " \n", "", 24, callback);
                                            woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check) + " \n", "", 24, callback);
//                                        woyouService.printColumnsText(new String[]{name, ":", Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check)}, new int[]{16, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                }
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                if (!settings.get_Footer_Text().equals("")) {
                                    woyouService.printTextWithFont(settings.get_Footer_Text(), "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                }

                                woyouService.printTextWithFont("" + settings.get_Copy_Right() + "\n", "", 30, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);

                                //woyouService.printColumnsText(new String[]{orders.get_total_item(),":","Tender/مناقصة"}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            woyouService.cutPaper(callback);
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.DiscountPer = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private ArrayList<String> getlist(Orders orders, ArrayList<Order_Detail> order_detail) {
        ArrayList<String> mylist = new ArrayList<String>();
        if (opr.equals("PayCollection")) {
            final Pay_Collection pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), " WHERE id = '" + id + "'", database);

            String tt = "", tt1 = "";
            mylist.add("\n" + "        Receipt Voucher");
            mylist.add("\nReceipt No.     :    " + pay_collection.get_collection_code());
            mylist.add("\nDate    :    " + pay_collection.get_collection_date());
            mylist.add("\nAmount  :" + Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check));
            String[] str = Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check).split("\\.");
            if (str.length == 1) {
                mylist.add("\nIn Words:" + Globals.convert(Integer.parseInt(str[0].toString())) + " Only ");
            } else if (str.length == 2) {
                if (Integer.parseInt(str[1].toString()) == 0) {
                    mylist.add("\nIn Words:" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD  Only ");

                } else {
                    mylist.add("\nIn Words:" + Globals.convert(Integer.parseInt(str[0].toString())) + " KD " + Globals.convert(Integer.parseInt(str[1].toString())) + " Fills Only ");
                }
            }

            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
            mylist.add("\nReceived From :    " + contact.get_name());
            mylist.add("\nCash/Cheque   :    " + pay_collection.get_payment_mode());
            mylist.add("\nOn Account :    " + pay_collection.get_on_account());
            mylist.add("\nRemarks :    " + pay_collection.get_remarks());

            if (pay_collection.get_payment_mode().equals("CHEQUE")) {
                Bank bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + pay_collection.get_ref_type() + "'", database);
                mylist.add("\nBank Name :    " + bank.get_bank_name());
                mylist.add("\nCheque No :    " + pay_collection.get_ref_no());
            }

            ArrayList<Pay_Collection_Detail> pay_collection_detail = Pay_Collection_Detail.getAllPay_Collection_Detail(getApplicationContext(), " WHERE collection_code='" + pay_collection.get_collection_code() + "'");
            if (pay_collection_detail.size() > 0) {
                mylist.add("\n-----------------------------------------------");
                mylist.add("\n  Order No          Amount");
                mylist.add("\n-----------------------------------------------");
                for (int i = 0; i < pay_collection_detail.size(); i++) {
                    mylist.add("\n" + "     " + pay_collection_detail.get(i).get_invoice_no() + "               " + pay_collection_detail.get(i).get_amount());
                }

            }

            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            mylist.add("\n" + "Cashier     :" + user.get_name());
            mylist.add("\n" + "Receiver Signature" + "");
            mylist.add("\n");
            mylist.add("\n");
            mylist.add("\n");
            mylist.add("\n");
        } else if (lite_pos_registration.getIndustry_Type().equals("4")) {
            String tt = "", tt1 = "";
            mylist.add("\n" + "            RECEIPT");
            try {
                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                } else {
                    mylist.add("\n            " + Globals.objLPR.getService_code_tariff());
                }
            } catch (Exception ex) {

            }
            mylist.add("\n            " + Globals.objLPR.getCompany_Name());
            mylist.add("\n            " + Globals.objLPR.getAddress());
            mylist.add("\n            " + Globals.objLPR.getMobile_No());
            mylist.add("\n" + Globals.PrintDeviceID + "   :   " + Globals.objLPD.getDevice_Name());

            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            mylist.add("\n" + Globals.PrintCashier + "    :    " + user.get_name());
            mylist.add("\nRECEIPT NO.    :    " + Globals.objLPD.getDevice_Name());
            if (Globals.strIsBarcodePrint.equals("true")) {
                BytesUtil.getPrintBarCode(orders.get_order_code(), 1, 60, 120, 1);
            }
            mylist.add("\nIN DT  :    " + DateUtill.PaternDatePrintDate(orders.get_order_date()));
            mylist.add("\nIN TM  :    " + DateUtill.PaternDatePrintTime(orders.get_order_date()));

            int count = 0;
            while (count < order_detail.size()) {

                String strItemCode = order_detail.get(count).get_item_code();

                String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                        + strItemCode + "'  GROUP By order_detail.item_Code");
                int len = 12;
                if (strItemName.length() > len) {
                    strItemName = strItemName.substring(0, len);
                } else {
                    for (int sLen = strItemName.length(); sLen < len; sLen++) {
                        strItemName = strItemName + " ";
                    }
                }

                String sale_price;
                Double dDisAfterSalePrice = 0d;

                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                sale_price = Globals.myNumberFormat2Price((Double.parseDouble(dDisAfterSalePrice + "") * Double.parseDouble(order_detail.get(count).get_quantity() + "")), decimal_check);
                String line_total;
                mylist.add("\nVEHICLE TYPE :" + DateUtill.PaternDatePrintTime(orders.get_order_date()));
                mylist.add("\n" + "     " + strItemName + "  Rs. " + sale_price);
                count++;
            }
            mylist.add("\n" + "V.NO :" + orders.get_remarks());

            if (!settings.get_Footer_Text().equals("")) {
                mylist.add("\n    " + settings.get_Footer_Text());
            }
            mylist.add("\n            " + settings.get_Copy_Right());
            mylist.add("\n");
            mylist.add("\n");

        } else {
            String lbl;
            String tt = "", tt1 = "";
            mylist.add("\n-----------------------------------------------");
            lbl = LableCentre(Globals.objLPR.getCompany_Name());
            mylist.add("\n" + lbl);
            lbl = LableCentre(Globals.objLPR.getAddress());
            mylist.add("\n" + lbl);
            lbl = LableCentre(Globals.objLPR.getMobile_No());
            mylist.add("\n" + lbl);

            try {
                if (Globals.objLPR.getService_code_tariff().equals("null")) {

                } else {
                    lbl = LableCentre(Globals.objLPR.getService_code_tariff());
                    mylist.add("\n" + lbl);
//                    mylist.add("\n" + "Tariff Code");
                }
            } catch (Exception ex) {

            }
            if (Globals.objLPR.getLicense_No().equals("null")) {
            } else {
                mylist.add("\n" + Globals.GSTNo + ":" + Globals.objLPR.getLicense_No());
            }

            lbl = LableCentre(Globals.PrintOrder);
            mylist.add("\n" + lbl);
            mylist.add("\n");
            mylist.add("\n" + Globals.PrintInvNo + ":" + orders.get_order_code());
            mylist.add("\n" + Globals.PrintInvDate + ":" + orders.get_order_date());
            mylist.add("\n" + Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name());

            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            mylist.add("\n" + Globals.PrintCashier + ":" + user.get_name());

            if (Globals.ModeResrv.equals("Resv")) {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                mylist.add("\n" + "Customer   : " + contact.get_name());
                if (contact.get_gstin().length() > 0) {
                    mylist.add("\n" + "Customer GST No.: " + contact.get_gstin());
                }
                Globals.strContact_Name = contact.get_name();
            } else {
                if (Globals.strContact_Code.equals("")) {
                    Globals.strContact_Name = "";
                } else {
                    try {
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                        mylist.add("\n" + "Customer   : " + contact.get_name());
                        if (contact.get_gstin().length() > 0) {
                            mylist.add("\n" + "Customer GST No.: " + contact.get_gstin());
                        }
                        Globals.strContact_Name = contact.get_name();
                    } catch (Exception ex) {

                    }

                }
            }

            mylist.add("\n-----------------------------------------------");
            mylist.add("\nItem                    Qty     Price   Total");
            mylist.add("\n-----------------------------------------------\n");
            Double itemFinalTax = 0d;
            int count = 0;
            while (count < order_detail.size()) {

                String strItemCode = order_detail.get(count).get_item_code();

                String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_code  = '"
                        + strItemCode + "'  GROUP By order_detail.item_Code");

                String line_total;
                line_total = Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check);

                //item name
                int l1 = strItemName.length();
                if (l1 > 24) {

                    char[] nm = strItemName.toUpperCase().toCharArray();
                    for (int k = 0; k < 24; k++) {

                        tt = tt + nm[k];
                    }
                    tt = tt + " ";
                } else {
                    char[] nm = strItemName.toUpperCase()
                            .toCharArray();
                    for (int k = 0; k < l1; k++) {

                        tt = tt + nm[k];
                    }
                    int space = 24 - l1;
                    for (int v = 0; v < space; v++) {

                        tt = tt + " ";
                    }
                }

                //quantity
                int l2 = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check).length();
                if (l2 > 8) {
                    char[] qt = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check).toCharArray();
                    for (int k = 0; k < 8; k++) {
                        tt = tt + qt[k];
                    }
                    tt = tt + " ";
                } else {
                    char[] qt = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(order_detail.get(count).get_quantity()), qty_decimal_check).toCharArray();
                    for (int k = 0; k < l2; k++) {

                        tt = tt + qt[k];
                    }
                    int space = 8 - l2;
                    for (int v = 0; v < space; v++) {
                        tt = tt + " ";
                    }
                }

                String sale_price;

                Double dDisAfterSalePrice = 0d;
                Double dDisAfter = 0d;
                dDisAfterSalePrice = (((Double.parseDouble(order_detail.get(count).get_line_total()) / Double.parseDouble(order_detail.get(count).get_quantity()))) - Double.parseDouble(order_detail.get(count).get_tax()));
                dDisAfter = (Double.parseDouble(order_detail.get(count).get_sale_price()));
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfter + ""), decimal_check);

                //price
                int l12 = sale_price.length();
                if (l12 > 6) {
                    char[] qt = sale_price.toCharArray();
                    for (int k = 0; k < 6; k++) {
                        tt = tt + qt[k];
                    }
                    tt = tt + " ";

                } else {
                    char[] qt = sale_price.toCharArray();
                    for (int k = 0; k < l12; k++) {

                        tt = tt + qt[k];
                    }
                    int space = 6 - l12;
                    for (int v = 0; v < space; v++) {
                        tt = tt + " ";
                    }
                }

                //total
                int l3 = line_total.length();
                if (l3 > 7) {
                    char[] r = String.valueOf(line_total).toCharArray();
                    for (int k = 0; k < 3; k++) {

                        tt = tt + r[k];
                    }

                    mylist.add(tt);
                    tt = "\n";
                } else {
                    int space = 7 - l3;
                    for (int v = 0; v < space; v++) {
                        tt = tt + " ";
                    }
                    char[] r = String.valueOf(line_total).toCharArray();
                    for (int k = 0; k < l3; k++) {
                        tt = tt + r[k];
                    }
                    mylist.add(tt);
                    tt = "\n";

                }


                mylist.add(tt);

                String strHSNLbl = "", HSNValue = "", strLBlDis = "";
                if (settings.get_HSN_print().equals("true")) {
                    item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + order_detail.get(count).get_item_code() + "'", database, db);
                    HSNValue = item.get_hsn_sac_code();
                    strHSNLbl = "HSN Code :";

                }


                String discnt = "";

                if (Double.parseDouble(orders.get_total_discount()) == 0) {
                    strLBlDis = "";
                } else {
                    if (Globals.strIsDiscountPrint.equals("true")) {
                        Double dis = Double.parseDouble(order_detail.get(count).get_sale_price()) - dDisAfterSalePrice;
                        discnt = Globals.myNumberFormat2Price(dis, decimal_check);
                        strLBlDis = "Discount : ";
                    } else {
                        strLBlDis = "";
                    }

                }
                if (Globals.strIsDiscountPrint.equals("false") && settings.get_HSN_print().equals("false")) {

                } else {
                    mylist.add(strHSNLbl + HSNValue + "  " + strLBlDis + " " + discnt);
                }


                if (settings.get_ItemTax().equals("1") || settings.get_ItemTax().equals("3")) {
                    Tax_Master tax_master = null;
                    ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
                    for (int i = 0; i < order_detail_tax.size(); i++) {
                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_detail_tax.get(i).get_tax_id() + "'", database, db);

                        int a = tax_master.get_tax_name().length();
                        if (a > 10) {

                            char[] nm = tax_master.get_tax_name().toUpperCase().toCharArray();
                            for (int k = 0; k < 10; k++) {

                                tt = tt + nm[k];
                            }
                            tt = tt + " ";
                        } else {
                            char[] nm = tax_master.get_tax_name().toUpperCase()
                                    .toCharArray();
                            for (int k = 0; k < a; k++) {

                                tt = tt + nm[k];
                            }
                            int space1 = 10 - a;
                            for (int v = 0; v < space1; v++) {

                                tt = tt + " ";
                            }
                        }

                        String tax_rate;
//                        tax_rate = Globals.myNumberFormat2Price(Double.parseDouble(order_detail_tax.get(i).get_tax_value()), decimal_check);
                        double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                        tax_rate = valueFinal + "";
                        itemFinalTax += valueFinal;
                        int b = tax_rate.length();

                        if (b > 7) {
                            char[] r2 = String.valueOf(tax_rate).toCharArray();
                            for (int k = 0; k < 7; k++) {

                                tt = tt + r2[k];
                            }

                            mylist.add(tt);
                            tt = "\n";
                        } else {

                            int space1 = 7 - b;
                            for (int v = 0; v < space1; v++) {
                                tt = tt + " ";
                            }
                            char[] r2 = String.valueOf(tax_rate).toCharArray();
                            for (int k = 0; k < b; k++) {
                                tt = tt + r2[k];
                            }

                            mylist.add(tt);
                            tt = "\n";
                        }
                    }
                    mylist.add(tt);
                }

                count++;
            }

            mylist.add("\n-----------------------------------------------");

            mylist.add("\nTotal Item :  " + orders.get_total_item());
            mylist.add("\nItem Quantity:  " + Globals.myNumberFormat2QtyDecimal(Double.parseDouble(orders.get_total_quantity()), qty_decimal_check));
            String subtotal = "";
            String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + strOrderNo + "' ";
            Cursor cursor1 = database.rawQuery(strTableQry, null);
            Tax_Master tax_master = null;
            while (cursor1.moveToNext()) {
                subtotal = cursor1.getString(0);

            }
            subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)) - Double.parseDouble(orders.get_total_discount()), decimal_check);


            tt = "";
            int ln = 0;
            ln = subtotal.length();
            int space = 9 - ln;
            for (int v = 0; v < space; v++) {
                tt = tt + " ";
            }
            tt = tt + subtotal;
            mylist.add("\nSubtotal    :  " + tt);
            mylist.add("\n-----------------------------------------------");
            if (settings.get_ItemTax().equals("2") || settings.get_ItemTax().equals("3")) {

                strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax " +
                        " inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code " +
                        " where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";

                cursor1 = database.rawQuery(strTableQry, null);

                if (cursor1.moveToFirst()) {
                    do {
                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);

                        tt = "";
                        int c = tax_master.get_tax_name().length();
                        if (c > 10) {

                            char[] nm = tax_master.get_tax_name().toUpperCase().toCharArray();
                            for (int k = 0; k < 10; k++) {

                                tt = tt + nm[k];
                            }
                            tt = tt + " ";
                        } else {
                            char[] nm = tax_master.get_tax_name().toUpperCase()
                                    .toCharArray();
                            for (int k = 0; k < c; k++) {

                                tt = tt + nm[k];
                            }
                            int space1 = 10 - c;
                            for (int v = 0; v < space1; v++) {

                                tt = tt + " ";
                            }
                        }

                        String tax_value;
                        tax_value = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check);
                        int d = tax_value.length();
                        tt1 = "";
                        if (d > 7) {
                            char[] r1 = String.valueOf(tax_value).toCharArray();
                            for (int k = 0; k < 7; k++) {

                                tt1 = tt1 + r1[k];
                            }


                            tt1 = "\n";
                        } else {
                            int space1 = 7 - d;
                            for (int v = 0; v < space1; v++) {
                                tt1 = tt1 + " ";
                            }
                            char[] r1 = String.valueOf(tax_value).toCharArray();
                            for (int k = 0; k < d; k++) {
                                tt1 = tt1 + r1[k];
                            }


                        }
                        mylist.add("\n" + tt + "   :  " + tt1);
                    } while (cursor1.moveToNext());


                    tt = "\n";

                    cursor1.close();
                }
                mylist.add("\n-----------------------------------------------");
            }


            String discount = "0";
            if (Double.parseDouble(orders.get_total_discount()) == 0) {

            } else {
                discount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_discount()), decimal_check);
                if (Globals.strIsDiscountPrint.equals("true")) {
                    tt = "";
                    ln = 0;
                    ln = discount.length();
                    space = 9 - ln;
                    for (int v = 0; v < space; v++) {
                        tt = tt + " ";
                    }
                    tt = tt + discount;
                    mylist.add("\nDiscount    :  " + Globals.DiscountPer + "%");
                    mylist.add("\nDiscount    :  " + tt);
                }
            }

            String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax) + "";
            String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);


            tt = "";
            ln = 0;
            ln = tatalAftrDis.length();
            space = 9 - ln;
            for (int v = 0; v < space; v++) {
                tt = tt + " ";
            }
            tt = tt + tatalAftrDis;
            mylist.add("\nTotal       :  " + tt);

            String total_tax;

            total_tax = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total_tax()), decimal_check);
            if (Double.parseDouble(total_tax) > 0d) {

                tt = "";
                ln = 0;
                ln = total_tax.length();
                space = 9 - ln;
                for (int v = 0; v < space; v++) {
                    tt = tt + " ";
                }
                tt = tt + total_tax;
                mylist.add("\nTotal Tax   :  " + tt);


                mylist.add("\n-----------------------------------------------");


                Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                String name = "", value = "";
                if (cursor.moveToFirst()) {
                    do {
                        name = cursor.getString(0);
                        value = cursor.getString(1);

                        String tax_value;
                        tax_value = Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check);

                        tt1 = "";
                        ln = 0;
                        ln = name.length();
                        space = 4 - ln;
                        for (int v = 0; v < space; v++) {
                            tt1 = tt1 + " ";
                        }
                        tt1 = tt1 + name;

                        tt = "";
                        ln = 0;
                        ln = tax_value.length();
                        space = 7 - ln;
                        for (int v = 0; v < space; v++) {
                            tt = tt + " ";
                        }
                        tt = tt + tax_value;
                        mylist.add("\n" + tt1 + "   :  " + tt);
                    } while (cursor.moveToNext());
                }

            }

            mylist.add("\n-----------------------------------------------");

            String net_amount;

            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check);

            tt = "";
            ln = 0;
            ln = net_amount.length();
            space = 9 - ln;
            for (int v = 0; v < space; v++) {
                tt = tt + " ";
            }
            tt = tt + net_amount;

            String strCurrency;
            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                strCurrency = "";
            } else {
                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
            }
            mylist.add("\nNet Amount  :  " + tt + strCurrency);

            String tender;
//        if (decimal_check.equals("3")) {
//            tender = Globals.myNumberFormat3Price(Double.parseDouble(orders.get_tender()));
//        } else {
            tender = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_tender()), decimal_check);
//        }

            tt = "";
            ln = 0;
            ln = tender.length();
            space = 9 - ln;
            for (int v = 0; v < space; v++) {
                tt = tt + " ";
            }
            tt = tt + tender;
            mylist.add("\nTender      :  " + tt);


            String change_amount;
//        if (decimal_check.equals("3")) {
//            change_amount = Globals.myNumberFormat3Price(Double.parseDouble(orders.get_change_amount()));
//        } else {
            change_amount = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_change_amount()), decimal_check);
//        }

            tt = "";
            ln = 0;
            ln = change_amount.length();
            space = 9 - ln;
            for (int v = 0; v < space; v++) {
                tt = tt + " ";
            }
            tt = tt + change_amount;
            mylist.add("\nChange      :  " + tt);
            mylist.add("\n-----------------------------------------------");

            ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
            if (order_payment_array.size() > 0) {
                for (int i = 0; i < order_payment_array.size(); i++) {
                    Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                    String name = "";
                    if (payment != null) {
                        name = payment.get_payment_name();
                        mylist.add("\n" + name + " : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check));
                    }
                }
                mylist.add("\n-----------------------------------------------");
            }
            if (settings.get_Is_Accounts().equals("true")) {
                if (ck_project_type.equals("standalone")) {
                    JSONObject jsonObject = new JSONObject();
                    if (Globals.strContact_Code.equals("")) {
                        mylist.add("\n**");
                    } else {
                        Double showAmount = 0d;
                        try {
                            String strCreditAmt = "", strDeditAmount = "";
                            Double creditAmount = 0d,
                                    debitAmount = 0d;
                            Cursor cursor = null;

                            String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                            cursor = database.rawQuery(strQry1, null);
                            while (cursor.moveToNext()) {
                                strCreditAmt = cursor.getString(0);

                            }
                            creditAmount = Double.parseDouble(strCreditAmt);

                            String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                            cursor = database.rawQuery(strQry2, null);
                            while (cursor.moveToNext()) {
                                strDeditAmount = cursor.getString(0);
                            }
                            debitAmount = Double.parseDouble(strDeditAmount);
                            showAmount = debitAmount + creditAmount;
                        } catch (Exception ex) {
                        }

                        double abs1 = Math.abs(showAmount);
                        if (showAmount > 0) {
                            mylist.add("\nOld Amount      :  " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR");
                        } else {
                            mylist.add("\nOld Amount      :  " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR");
                        }
                        try {
                            jsonObject.put("Old Amt", abs1 + "");
                        } catch (Exception ex) {

                        }
                        String strCur = "";
                        tt = "";
                        ln = 0;
                        ln = change_amount.length();
                        space = 9 - ln;
                        for (int v = 0; v < space; v++) {
                            tt = tt + " ";
                        }
                        tt = tt + change_amount;

                        try {
                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                            cursor1 = database.rawQuery(strTableQry, null);
                            if (cursor1.moveToFirst()) {
                                do {
                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                    mylist.add("\nCurrent Amount      :  " + strCur);
                                } while (cursor1.moveToNext());
                            }
                        } catch (Exception ex) {
                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                            mylist.add("\nCurrent Amount      :  " + strCur);
                        }

                        if (strCur.equals("")) {
                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                            mylist.add("\nCurrent Amount      :  " + strCur);
                        }

                        try {
                            jsonObject.put("Current Amt", strCur + "");
                        } catch (Exception ex) {

                        }

                        Double strBalance = abs1 - Double.parseDouble(strCur);
                        try {
                            jsonObject.put("Balance Amt", strBalance + "");
                        } catch (Exception ex) {

                        }
                        tt = "";
                        ln = 0;
                        ln = change_amount.length();
                        space = 9 - ln;
                        for (int v = 0; v < space; v++) {
                            tt = tt + " ";
                        }
                        tt = tt + change_amount;

                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                        db.executeDML(strUpdatePayment, database);

                        mylist.add("\nBalance Amount      :  " + Globals.myNumberFormat2Price(strBalance, decimal_check));
                    }
                } else {
                    JSONObject jsonObject = new JSONObject();
                    if (Globals.strContact_Code.equals("")) {
                        mylist.add("\n**");
                    } else {
                        double abs2 = 0d;
                        String curAmount = "";
                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                        try {
                            cursor1 = database.rawQuery(strTableQry, null);
                            if (cursor1.moveToFirst()) {
                                do {
                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                } while (cursor1.moveToNext());
                            }
                        } catch (Exception ex) {
                            curAmount = "0";
                        }

                        double ab1 = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                        double abs1 = Math.abs(ab1);
                        if (ab1 > 0) {
                            mylist.add("\nOld Amount      :  " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR");
                        } else {
                            mylist.add("\nOld Amount      :  " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR");
                        }
                        try {
                            jsonObject.put("Old Amt", abs1 + "");
                        } catch (Exception ex) {

                        }

                        String strCur = "";
                        tt = "";
                        ln = 0;
                        ln = change_amount.length();
                        space = 9 - ln;
                        for (int v = 0; v < space; v++) {
                            tt = tt + " ";
                        }
                        tt = tt + change_amount;

                        try {
                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                            cursor1 = database.rawQuery(strTableQry, null);
                            if (cursor1.moveToFirst()) {
                                do {
                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);
                                    mylist.add("\nCurrent Amount      :  " + strCur);
                                } while (cursor1.moveToNext());
                            }
                        } catch (Exception ex) {
                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                            mylist.add("\nCurrent Amount      :  " + strCur);
                        }

                        if (strCur.equals("")) {
                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                            mylist.add("\nCurrent Amount      :  " + strCur);
                        }
                        try {
                            jsonObject.put("Current Amt", strCur + "");
                        } catch (Exception ex) {

                        }
                        Double strBalance = abs1 + abs2 + Double.parseDouble(strCur);
                        try {
                            jsonObject.put("Balance Amt", strBalance + "");
                        } catch (Exception ex) {

                        }
//                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
//                        db.executeDML(strUpdatePayment, database);
                        tt = "";
                        ln = 0;
                        ln = change_amount.length();
                        space = 9 - ln;
                        for (int v = 0; v < space; v++) {
                            tt = tt + " ";
                        }
                        tt = tt + change_amount;

                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                        db.executeDML(strUpdatePayment, database);

                        mylist.add("\nBalance Amount      :  " + Globals.myNumberFormat2Price(strBalance, decimal_check));
                    }
                }
            }

            if (!settings.get_Footer_Text().equals("")) {
                mylist.add("\n    " + settings.get_Footer_Text());
            }
            mylist.add("\n            " + settings.get_Copy_Right());
            mylist.add("\n");
            mylist.add("\n");

        }
        return mylist;
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
                performOperation();
            }
        });

        b.setMessage(strMsg);
        b.show();
    }

    @Override
    public void onBackPressed() {
        Globals.strContact_Code = "";
        Globals.strResvContact_Code = "";
        Globals.DiscountPer = "";
        Globals.strOldCrAmt = "0";
        Globals.setEmpty();
        if (settings.get_Home_Layout().equals("0")) {
            Intent intent = new Intent(PrintLayout.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(PrintLayout.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            dialog = new ProgressDialog(PrintLayout.this);
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_ENABLE_BT:      //���������
//                if (resultCode == Activity.RESULT_OK) {   //�����Ѿ���
//                    Toast.makeText(getApplicationContext(), "Bluetooth open successful", Toast.LENGTH_LONG).show();
//                }
//                break;
//            case REQUEST_CONNECT_DEVICE:     //��������ĳһ�����豸
//                if (resultCode == Activity.RESULT_OK) {   //�ѵ�������б��е�ĳ���豸��
//                    String address = data.getExtras()
//                            .getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);  //��ȡ�б������豸��mac��ַ
//                    con_dev = mService.getDevByMac(address);
//
//                    mService.connect(con_dev);
//                }
//                break;
//        }
//    }

    //��ӡͼ��
    @SuppressLint("SdCardPath")
    private void printImagebluetooth() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(576);
        pg.initPaint();
//        pg.drawImage(0, 0, "/mnt/sdcard/icon.jpg");
        //
        sendData = pg.printDraw();
        mService.write(sendData);   //��ӡbyte������
        Log.d("��������", "" + sendData.length);
    }

    //��ӡͼ��
    @SuppressLint("SdCardPath")
    private void printbluetoothBarcode(String strOrderNo) {
        byte[] sendData = null;
        sendData = BytesUtil.getPrintBarCode(strOrderNo, 8, 50, 80, 1);
        sendData = BytesUtil.getPrintBarCode(strOrderNo, 8, 50, 80, 1);
        mService.write(sendData);
    }


    //��ӡͼ��
    @SuppressLint("SdCardPath")
    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(576);
        pg.initPaint();

        Bitmap bitmap = StringToBitMap(settings.get_Logo());
        bitmap = getResizedBitmap(bitmap, 80, 120);
        Uri selectedImageURI = getImageUri(getApplicationContext(), bitmap);
        String path = getRealPathFromURI(selectedImageURI);
        if (bitmap != null) {
            bitmap = getResizedBitmap(bitmap, 120, 120);
            //            path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
            //            Uri.parse(path);
            pg.drawImage(130, 0, path);
            sendData = pg.printDraw();
            mService.write(sendData);

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeBytes(b, Base64.ENCODE);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DECODE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        // GET CURRENT SIZE
        int width = bm.getWidth();
        int height = bm.getHeight();
        // GET SCALE SIZE
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public String LableCentre(String InvoiceLabel) {

        int ln = InvoiceLabel.trim().length();
        int rem = 42 - ln;
        int part = rem / 2;
        String tt1 = "";
        for (int i = 0; i < part; i++) {
            tt1 = tt1 + " ";
        }
        tt1 = tt1 + InvoiceLabel;
        for (int i = 0; i < part; i++) {
            tt1 = tt1 + " ";
        }
        return tt1;
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
                Toast.makeText(PrintLayout.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(PrintLayout.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(PrintLayout.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
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

    /*加快bitmap回收，减少内存占用*/
    public static void bitmapRecycle(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
    }

}
