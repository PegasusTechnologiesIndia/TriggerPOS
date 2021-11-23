package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.google.gson.Gson;
import com.hoin.btsdk.BluetoothService;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PrintTestActivity extends AppCompatActivity {

    Spinner sp_print;
    EditText edt_wifip;
    Button btn_connectprint,btn_testprint;
    private static final String TAG = "PrinterTestDemo";
    private boolean iswifi = false;
    private IWoyouService woyouService;
    private ICallback callback1 = null;
    private TextView info;
    private ICallback callback2 = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static BluetoothService mService;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    private static final int STATE_CONNECTED = 2;
    private int pri_pos;
    private ProgressDialog dialog;
    Settings settings;
    private TableRow wifi_set;
    Database db;
    SQLiteDatabase database;
    private TimerCountTools timeTools;
    JSONObject printJson;
    private PrintTestActivity.PrinterListener printer_callback = new PrintTestActivity.PrinterListener();
    private MyAdapter adp;
    public static PrinterBinder printer;
    private ArrayList<String> mylist = new ArrayList<String>();
    public static BluetoothDevice con_dev;
    MenuItem menuItem;
    String wifi_ip="";
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

            }
        }
    };


    private ISendCallback callback = new ISendCallback() {

        @Override
        public void onSendFail(int arg0, String arg1) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_SHORT).show();

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

                    }
                });
            }
        }

        @Override
        public void onReceiveCMD(DSData arg0) {
            // TODO
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
                    Toast.makeText(PrintTestActivity.this, "Printer Is Working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(PrintTestActivity.this, "Out Of Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(PrintTestActivity.this, "Exists Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(PrintTestActivity.this, "Printer High Temp Alarm", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_print_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.PrintTest);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

       /* try {
            ServiceManager.getInstence().init(getApplicationContext());
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         /*       pDialog = new ProgressDialog(SetttingsActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {*/
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(PrintTestActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //  pDialog.dismiss();
                            finish();

                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(PrintTestActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(PrintTestActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //  pDialog.dismiss();
                            finish();

                        } finally {
                        }
                    }
                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(PrintTestActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(Globals.objLPR.getIndustry_Type().equals("3")){
                    Intent intent = new Intent(PrintTestActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(PrintTestActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                  /*  }
                };
                timerThread.start();*/
            }
        });
        sp_print=(Spinner)findViewById(R.id.sp_printtest);
        edt_wifip=(EditText)findViewById(R.id.ip);
        btn_connectprint=(Button)findViewById(R.id.btn_searchdevice);
        btn_testprint=(Button)findViewById(R.id.btn_testprint);
        wifi_set = (TableRow) findViewById(R.id.wifi_set_row);

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        setPrinterType(settings);
        sp_print.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {

                if (Globals.objLPR.getIndustry_Type().equals("4")) {
                    if(pos==1){
                        btn_connectprint.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        mDSKernel = DSKernel.newInstance();
                        mDSKernel.init(getApplicationContext(), mConnCallback);
                        mDSKernel.addReceiveCallback(mReceiveCallback);

                        callback2 = new ICallback.Stub() {

                            @Override
                            public void onRunResult(final boolean success) throws RemoteException {
                            }

                            @Override
                            public void onReturnString(final String value) throws RemoteException {
//                Log.i(TAG, "printlength:" + value + "\n");
                            }

                            @Override
                            public void onRaiseException(int code, final String msg) throws RemoteException {
                                //  Log.i(TAG, "onRaiseException: " + msg);
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


                    }
                    if (pos==2||pos == 3 || pos == 4) {

                        btn_connectprint.setVisibility(View.VISIBLE);
                        btn_testprint.setVisibility(View.VISIBLE);
                            mService = PrintTestActivity.mService;

                    }


                    if (pos == 6) {
                        btn_connectprint.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
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
                    }

                }
                else {
                    if (pos == 3 || pos == 4) {

                        btn_connectprint.setVisibility(View.VISIBLE);
                        btn_testprint.setVisibility(View.VISIBLE);

                        mService = PrintTestActivity.mService;

                    }


                    if (pos == 1 || pos == 6 || pos == 9) {
                        btn_connectprint.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        mDSKernel = DSKernel.newInstance();
                        mDSKernel.init(getApplicationContext(), mConnCallback);
                        mDSKernel.addReceiveCallback(mReceiveCallback);

                        callback2 = new ICallback.Stub() {

                            @Override
                            public void onRunResult(final boolean success) throws RemoteException {
                            }

                            @Override
                            public void onReturnString(final String value) throws RemoteException {
//                Log.i(TAG, "printlength:" + value + "\n");
                            }

                            @Override
                            public void onRaiseException(int code, final String msg) throws RemoteException {
                                //  Log.i(TAG, "onRaiseException: " + msg);
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


                    }
                    if (pos == 8) {
                        btn_connectprint.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
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
                    }

                    if (pos == 2) {
                        wifi_set.setVisibility(View.VISIBLE);
                        if (settings != null) {
                            edt_wifip.setText(settings.getPrinterIp());
                        }
                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_connectprint.setVisibility(View.VISIBLE);

                    } else {
                        wifi_set.setVisibility(View.GONE);
                    }


                    if (pos == 6) {
                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_connectprint.setVisibility(View.GONE);

                    } else {

                    }
                    if (pos == 9) {
                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_connectprint.setVisibility(View.GONE);

                    } else {

                    }

                    if (pos == 7) {

                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_connectprint.setVisibility(View.GONE);

                    } else {

                    }
                    if (pos == 0) {

                        btn_connectprint.setVisibility(View.GONE);

                        btn_testprint.setVisibility(View.GONE);
                    } else {

                        // btn_testprint.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        btn_connectprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pri_pos = sp_print.getSelectedItemPosition();

                if(Globals.objLPR.getIndustry_Type().equals("4")){
                    try {
                        if (sp_print.getSelectedItemPosition() == 2 || sp_print.getSelectedItemPosition() == 3 || sp_print.getSelectedItemPosition() == 4) {
                            if (Globals.strIsBlueService.equals("utc")) {

                                //  mService = MainActivity.mService;
                                mService = new BluetoothService(getApplicationContext(), mHandler);

                                //   Globals.AppLogWrite(mService.toString());
                                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                                serverIntent.putExtra("flag", "printtest");
                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            } else if (con_dev == null) {
                                //   mService = MainActivity.mService;
                                mService = new BluetoothService(getApplicationContext(), mHandler);

                                // Globals.AppLogWrite(mService.toString());

                                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                                serverIntent.putExtra("flag", "printtest");

                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            }
                        }
                    } catch (Exception e) {

                    }
                }

                else {
                    try {
                        if (pri_pos == 3 || pri_pos == 4 || pri_pos == 5) {
                            if (Globals.strIsBlueService.equals("utc")) {

                                //  mService = MainActivity.mService;
                                mService = new BluetoothService(getApplicationContext(), mHandler);

                                //   Globals.AppLogWrite(mService.toString());
                                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                                serverIntent.putExtra("flag", "printtest");
                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            } else if (con_dev == null) {
                                //   mService = MainActivity.mService;
                                mService = new BluetoothService(getApplicationContext(), mHandler);

                                // Globals.AppLogWrite(mService.toString());

                                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                                serverIntent.putExtra("flag", "printtest");

                                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                            }
                        }
                    } catch (Exception e) {

                    }

                    try {
                        if (pri_pos == 2) {
                            String ipwifi = edt_wifip.getText().toString();

                            if (ipwifi.length() > 0) {
                                final PrintTestActivity.LongOperation tsk = new PrintTestActivity.LongOperation();
                                tsk.execute();
                                // setting timeout thread for async task
                                Thread thread1 = new Thread() {
                                    public void run() {
                                        try {
                                            tsk.get(6000, TimeUnit.MILLISECONDS);
                                            iswifi = true;// set time in
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
                                if (iswifi == true) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Printer connected Successfully", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else if (iswifi == false) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Printer connection Failed. Please check IP", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }
                                //wifi_set.setVisibility(View.VISIBLE);
                                //if (settings != null) {
                                String ipget = edt_wifip.getText().toString();
                                settings.setPrinterIp(ipget);
                                if (settings.getPrinterIp() != null) {
                                    edt_wifip.setText(settings.getPrinterIp());
                                }
                                //}
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        btn_testprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Globals.objLPR.getIndustry_Type().equals("4")) {
                    if (sp_print.getSelectedItemPosition() == 1) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                // text = edt_text.getText().toString().trim();
                                try {
                                    woyouService.setAlignment(1, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    //   woyouService.printBarCode("Test Print", 8, 80, 150, 0, callback2);
                                    woyouService.printTextWithFont("Test Print", "", 24, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);

                                    // woyouService.cutPaper(callback2);

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (sp_print.getSelectedItemPosition() == 2 || sp_print.getSelectedItemPosition() == 3||sp_print.getSelectedItemPosition()==4) {
                        final byte[] cmd = new byte[3];
                        cmd[0] = 0x1b;
                        cmd[1] = 0x21;
                        byte[] ab;
                        mService = PrintTestActivity.mService;

                        if (mService.isAvailable() == false) {
                        } else {
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage("Test Print", "GBK");
                            mService.sendMessage("\n\n", "GBK");
//                            mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");

                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            byte[] print = {0x1b, 0x0c};
                            mService.write(print);
                        }

                    }
                    if (sp_print.getSelectedItemPosition() == 5) {
                        JSONArray printTest = new JSONArray();
                        printJson = new JSONObject();
                        timeTools = new TimerCountTools();
                        timeTools.start();
                        try {
                            ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);


                            String Print_type = "0";
                            printTest.put(getPrintObject("Test Print", "3", "center"));
                            printJson.put("spos", printTest);
                            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                            ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (sp_print.getSelectedItemPosition() == 6) {

                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("TEST PRINT", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                                } catch (Exception e) {

                                }

                            }
                        });
                    }
                } else {
                    if (sp_print.getSelectedItemPosition() == 1) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                // text = edt_text.getText().toString().trim();
                                try {
                                    woyouService.setAlignment(1, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    //   woyouService.printBarCode("Test Print", 8, 80, 150, 0, callback2);
                                    woyouService.printTextWithFont("Test Print", "", 24, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);

                                    // woyouService.cutPaper(callback2);

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (sp_print.getSelectedItemPosition() == 3 || sp_print.getSelectedItemPosition() == 4) {
                        final byte[] cmd = new byte[3];
                        cmd[0] = 0x1b;
                        cmd[1] = 0x21;
                        byte[] ab;
                        mService = SetttingsActivity.mService;

                        if (mService.isAvailable() == false) {
                        } else {
                            ab = BytesUtil.setAlignCenter(1);
                            mService.write(ab);
                            mService.sendMessage("Test Print", "GBK");
                            mService.sendMessage("\n\n", "GBK");
//                            mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");

                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            byte[] print = {0x1b, 0x0c};
                            mService.write(print);
                        }

                    }
                    if (sp_print.getSelectedItemPosition() == 6) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                // text = edt_text.getText().toString().trim();
                                try {
                                    woyouService.setAlignment(1, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    //   woyouService.printBarCode("Test Print", 8, 80, 150, 0, callback2);
                                    woyouService.printTextWithFont("Test Print", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);

                                    woyouService.cutPaper(callback2);

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (sp_print.getSelectedItemPosition() == 7) {
                        JSONArray printTest = new JSONArray();
                        printJson = new JSONObject();
                        timeTools = new TimerCountTools();
                        timeTools.start();
                        try {
                            ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);


                            String Print_type = "0";
                            printTest.put(getPrintObject("Test Print", "3", "center"));
                            printJson.put("spos", printTest);
                            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                            ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (sp_print.getSelectedItemPosition() == 9) {
                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                // text = edt_text.getText().toString().trim();
                                try {
                                    woyouService.setAlignment(1, callback2);

                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    // woyouService.printBarCode("Test Print", 8, 80, 150, 0, callback2);
                                    woyouService.printTextWithFont("Test Print", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);
                                    woyouService.printTextWithFont(" \n", "", 24, callback2);

                                    woyouService.cutPaper(callback2);

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (pri_pos == 2) {

                        mylist = getlist();
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
                        }
                    }
                    if (sp_print.getSelectedItemPosition() == 8) {

                        ThreadPoolManager.getInstance().executeTask(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("TEST PRINT", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                                } catch (Exception e) {

                                }

                            }
                        });
                    }
                }

            }
        });
    }

    private void setPrinterType(Settings settings) {
        String[] stringArray;
        if(Globals.objLPR.getIndustry_Type().equals("4")){
            stringArray= getResources().getStringArray(R.array.Printer_Type_Parking);
        }
        else {
            stringArray = getResources().getStringArray(R.array.Printer_Type);
        }
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_print.setAdapter(adapterPtype);
        if (settings != null) {
            sp_print.setSelection(Integer.parseInt(settings.getPrinterId()));
        }
        adapterPtype.notifyDataSetChanged();
    }
    private ArrayList<String> getlist() {
        String strString = "";
        int strLength = 18;
        ArrayList<String> mylist = new ArrayList<String>();


        mylist.add("               " + "Test Print\n");
        mylist.add("\n");
        mylist.add("\n");
        mylist.add("\n");
        return mylist;

    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Globals.strIsBlueService = "cnt"; //������
                            Toast.makeText(PrintTestActivity.this, "Connect successful",
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
                    Toast.makeText(PrintTestActivity.this, "Device connection was lost",
                            Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Globals.strIsBlueService = "utc";
                    Toast.makeText(PrintTestActivity.this, "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };
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
                Toast.makeText(PrintTestActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(PrintTestActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(PrintTestActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public Boolean CheckprinterConnection() {
        //if (settings != null) {

        if (pri_pos==2) {
            String tmpStr = edt_wifip.getText().toString().trim();
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
        /*} else {
            return false;
        }*/
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
            else{
                dialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(PrintTestActivity.this);
            dialog.setCancelable(false);
            dialog.show();
        }

    }
    private void performOperationEn() {
       // noofPrint = Integer.parseInt(settings.get_No_Of_Print());

        if (mylist.size() > 0) {
            try {
                String bill = "";
                for (String data : mylist) {
                    bill = bill + data;
                }

             //   for (int k = 1; k <= noofPrint; k++) {
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

               // }
            } catch (Exception e) {
            }
        }
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(PrintTestActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.split_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

public void save(){
   // long e = Settings.delete_settings(PrintTestActivity.this, "Settings", database, "", new String[]{});
    pri_pos = (int) sp_print.getSelectedItemId();
    if (pri_pos == 2) {
        wifi_ip = edt_wifip.getText().toString().trim();
        if (wifi_ip.equals("")) {
            Toast.makeText(PrintTestActivity.this, R.string.plsinsrtIp, Toast.LENGTH_SHORT).show();
        }
    }


        Settings settings = Settings.getSettings(getApplicationContext(), database, "");
         settings.setPrinterId(String.valueOf(pri_pos));
        settings.setPrinterIp(edt_wifip.getText().toString().trim());
           settings.set_Is_Print_Invoice("true");
        settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);


        if(Globals.objLPR.getIndustry_Type().equals("1")){
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();

        }
     else if(Globals.objLPR.getIndustry_Type().equals("2")){
        Intent i=new Intent(getApplicationContext(),Retail_IndustryActivity.class);
        startActivity(i);
        finish();

      }
        else if(Globals.objLPR.getIndustry_Type().equals("3")){
            Intent i=new Intent(getApplicationContext(),PaymentCollection_MainScreen.class);
            startActivity(i);
            finish();
        }
        else if(Globals.objLPR.getIndustry_Type().equals("4")){
            Intent i=new Intent(getApplicationContext(),ParkingIndustryActivity.class);
            startActivity(i);
            finish();
        }

}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Globals.objLPR.getIndustry_Type().equals("1")) {
            if (settings.get_Home_Layout().equals("0")) {
                try {
                    Intent intent = new Intent(PrintTestActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //  pDialog.dismiss();
                    finish();

                } finally {
                }
            } else if (settings.get_Home_Layout().equals("2")) {
                try {
                    Intent intent = new Intent(PrintTestActivity.this, RetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else {
                try {
                    Intent intent = new Intent(PrintTestActivity.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //  pDialog.dismiss();
                    finish();

                } finally {
                }
            }
        }
        else if(Globals.objLPR.getIndustry_Type().equals("2")){
            Intent intent = new Intent(PrintTestActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(Globals.objLPR.getIndustry_Type().equals("3")){
            Intent intent = new Intent(PrintTestActivity.this, PaymentCollection_MainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(Globals.objLPR.getIndustry_Type().equals("4")){
            Intent intent = new Intent(PrintTestActivity.this, ParkingIndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
}