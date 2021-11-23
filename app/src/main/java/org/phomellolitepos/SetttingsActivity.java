package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
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
import com.itextpdf.text.pdf.codec.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.DefaultOrderTypeSettingAdapter;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

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

public class SetttingsActivity extends AppCompatActivity {
    CheckBox is_costp_show,is_show_brc, is_show_print_dialog, is_show_device_cus, is_show_zdetail_inreprt, is_change_price, is_zero_stck, is_cash_drawer, is_stock_manage, is_accounts, chk_ad, chk_ac, chk_bop, is_file_share, is_online, is_email, is_sms, is_cmd, chk_hsn, is_denomination, is_barcode_print, is_discount, is_print_kot, is_print_invoice;
    Button save, btn_cus_image,btn_test_email,btn_test_sms,btn_reset;
    EditText edt_list_limit,edt_gst_lbl, edt_no_of_prnt, edt_host, edt_port, edt_sequence_no, edt_footer, edt_gst_no, edt_print_order, edt_print_cashier, edt_print_inv_no, edt_print_inv_date, edt_print_device_id;
    Spinner sp_qr_type,sp_print_memo, sp_defualt_odrtype, sp_print, sp_scale, sp_print_lang, sp_item_tax, sp_qty_dicml, sp_cus_dis, sp_home_layout;
    private TableRow wifi_set;
    private EditText ip;
    private String chk_is_cost_show,chk_is_show_brc, chk_is_show_device_cus, chk_is_show_print_dialog, chk_is_zdetail_inreprt, chk_is_change_price, chk_is_zero_stock, chk_is_cash_drawer, chk, wifi_ip = "", chk_email, chk_sms, chk_cmd, chk_hsn_prnt, chk_is_denomination, chk_is_barcode, chk_is_discount, chk_is_print_kot, chk_is_print_invoice, chk_is_file_share;
    private int qr_type,pri_pos, scale, item_tax, qty, cus_dis, home_layout, default_ordertype, print_memo;
    LinearLayout ll_printmemo,ll_sel_logo,ll_homelayout,ll_paymentcollection,ll_qtydecimal,ll_homelayoout,ll_paymentcollection2,ll_paymentcollection3,ll_paymentcollection4,ll_hsnprint,ll_printlang;
    TableLayout ll_noprint;
    CheckBox chk_apisync;
    String isApiSync;
    Database db;
    LinearLayout ll_induDastrylayout;
    Spinner sp_industrylayout;
    private int order, noofPrint = 0;
    SQLiteDatabase database;
    Button btn_searchBT;
    private TimerCountTools timeTools;
    JSONObject printJson;
    private PrinterListener printer_callback = new PrinterListener();

    public static PrinterBinder printer;
    private MyAdapter adp;
  //  Lite_POS_Registration lite_pos_registration;
    ImageView img_logo, img_logo_add,img_delete;
    EditText edt_email, edt_pass, edt_mnger_email, edt_uri, edt_key, edt_sender_id;
    TableLayout tl_email, tl_sms;
    private static final int PICK_IMAGE_REQUEST = 3;
    Uri uri;
    String strQRType,strScale, strItemTax, strCusDis, strHomeLayout, strDefaultOrderType, strPrintMemo;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Bitmap bitmap;
    Intent intent;
    String strMgEmail = "";
    String strMailManager = "";
    String strEmail, strPass, strHost, strPort;
    String strPriLang, strQtyDecimal, strChangeParm;
    ProgressDialog pDialog;
    LinearLayout il_scale, ll_cash_drawer, ll_is_online,ll_CMD,ll_cus_img;
    Last_Code last_code;
    String strIsAccount;
    String strIsStockManage, strIsZeroStock, strIsChangePrice;
    String chk_is_account;
    String chk_is_stock_manage;
    CheckBox chk_is_kitchenprint;
    String str_kitchenprint;
    Settings settings;
    ArrayList<Order_Type> order_typeArrayList;
    MenuItem menuItem;
    Button btn_testprint;
    private static final String TAG = "PrinterTestDemo";
    private ProgressDialog dialog;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    private ArrayList<String> mylist = new ArrayList<String>();
    long taskId_sendImgsText;
    private boolean iswifi = false;
    private IWoyouService woyouService;
    private ICallback callback1 = null;
    private TextView info;
    private ICallback callback2 = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
     public static BluetoothService mService;
     View view7,viewdirect;
    CheckBox chk_is_saveprint;
    CheckBox chk_iscloudprint;
    String is_cloudprint;
    String is_show_saveprint;
    View view1;
    TextView lbl_eml;
    CheckBox chk_isdeliverydate,chk_is_selfposkot,chk_ispaymentmethod;
    String is_deliverydate,is_selfposkot,is_paymentmethod;
CheckBox is_showsinglewindow, is_showfilladvamnt,is_validatevehno,is_validatemobileno,is_nfc;
    private static final int STATE_CONNECTED = 2;
LinearLayout ll_ordertype,ll_qrscanning,ll_item_tax;

    // Parking Industry Strings
    private String  chk_is_singlewindow,chk_is_fillAdvAmnt,chk_is_valVehNo,chk_is_valmobNo,chk_isnfc;
    public static BluetoothDevice con_dev;
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


    /**
     * 发送消息的回调
     */
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

//    private void showImg() {
//        String json = UPacketFactory.createJson(DataModel.QRCODE,"");
//        mDSKernel.sendCMD(DSKernel.getDSDPackageName(),json,taskId_sendImgText,null);
//    }


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
                    public void run      () {

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
                  //  Toast.makeText(SetttingsActivity.this, "Printer Is Working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(SetttingsActivity.this, "Out Of Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(SetttingsActivity.this, "Exists Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(SetttingsActivity.this, "Printer High Temp Alarm", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_setttings);


        Date currentDate = new Date(System.currentTimeMillis()+25L*24L*60L*60L*1000L);
        Globals.AppLogWrite(currentDate.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Settings);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
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
                                    Intent intent = new Intent(SetttingsActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                  //  pDialog.dismiss();
                                    finish();

                                } finally {
                                }
                            } else if (settings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(SetttingsActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(SetttingsActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                  //  pDialog.dismiss();
                                    finish();

                                } finally {
                                }
                            }
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(SetttingsActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("3")){
                            Intent intent = new Intent(SetttingsActivity.this, PaymentCollection_MainScreen.class);
                            intent.putExtra("whatsappFlag","false");
                            intent.putExtra("contact_code", "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("4")){
                            Intent intent = new Intent(SetttingsActivity.this, ParkingIndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                  /*  }
                };
                timerThread.start();*/
            }
        });


try {
    initview();

}
catch(Exception e){
    Globals.AppLogWrite(e.getMessage());
}


       //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.set_Logo("");
                long h = settings.updateSettings("_Id = ?",new String[]{settings.get_Id()},database);
                if (h>0){
                    try {
                        Globals.logo1=null;
                        Bitmap bitmap = StringToBitMap("");
                        img_logo.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

btn_searchBT.setOnClickListener(new View.OnClickListener() {
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
                        serverIntent.putExtra("flag", "settings");
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    } else if (con_dev == null) {
                        //   mService = MainActivity.mService;
                        mService = new BluetoothService(getApplicationContext(), mHandler);

                        // Globals.AppLogWrite(mService.toString());

                        Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                        serverIntent.putExtra("flag", "settings");

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
                        serverIntent.putExtra("flag", "settings");
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    } else if (con_dev == null) {
                        //   mService = MainActivity.mService;
                        mService = new BluetoothService(getApplicationContext(), mHandler);

                        // Globals.AppLogWrite(mService.toString());

                        Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                        serverIntent.putExtra("flag", "settings");

                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    }
                }
            } catch (Exception e) {

            }

            try {
                if (pri_pos == 2) {
                    String ipwifi = ip.getText().toString();

                    if (ipwifi.length() > 0) {
                        final LongOperation tsk = new LongOperation();
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
                        String ipget = ip.getText().toString();
                        settings.setPrinterIp(ipget);
                        if (settings.getPrinterIp() != null) {
                            ip.setText(settings.getPrinterIp());
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
        chk_ad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_ad.isChecked()) {
                    chk_ac.setChecked(false);
                    chk_bop.setChecked(false);
                }
            }
        });

        chk_ac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_ac.isChecked()) {
                    chk_ad.setChecked(false);
                    chk_bop.setChecked(false);
                }
            }
        });

        chk_bop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_bop.isChecked()) {
                    chk_ad.setChecked(false);
                    chk_ac.setChecked(false);
                }
            }
        });

        is_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_email.isChecked()) {
                    tl_email.setVisibility(View.VISIBLE);
                } else {
                    tl_email.setVisibility(View.GONE);
                }
            }
        });

        is_sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_sms.isChecked()) {
                    tl_sms.setVisibility(View.VISIBLE);
                } else {
                    tl_sms.setVisibility(View.GONE);
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = Settings.getSettings(getApplicationContext(), database, "");
                settings = new Settings(getApplicationContext(), settings.get_Id(), "false", "0", "", "", "", "", "", "", "", "", "", "", "", "0", "false", "false", "0", "Powered By Phomello", "0", "", "", "false", "false", "false", "GST", "TAX INVOICE", "Salesperson", "Invoice Number", "Invoice Date", "Device ID", "false", "false", "false", "", "", "AC", "false", "false", "0", "false", "true", "false", "1", "GST", "false", "false", "false", "false", "1", "0","false","0","false","false","false","false","false","false","false","false","false","false","false","","false","","");
                long l = settings.updateSettings("_Id=?",new String[]{settings.get_Id()},database);
                if (l>0) {
                    Toast.makeText(getApplicationContext(), R.string.Reset_Succ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetttingsActivity.this,SetttingsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_test_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edt_email.getText().toString().trim();
                strPass = edt_pass.getText().toString().trim();
                strHost = edt_host.getText().toString().trim();
                strPort = edt_port.getText().toString().trim();
                test_email(strEmail, strPass, edt_mnger_email.getText().toString(),strHost,strPort);
            }
        });

        btn_test_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_sms.isChecked()) {
                    chk_sms = "true";

                    if (edt_uri.getText().toString().equals("")) {
                        edt_uri.setError(getString(R.string.Url_is_required));
                        edt_uri.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_key.getText().toString().equals("")) {
                        edt_key.setError(getString(R.string.Key_is_required));
                        edt_key.requestFocus();
                        return;
                    } else {}

                    if (edt_sender_id.getText().toString().equals("")) {
                        edt_sender_id.setError(getString(R.string.Sender_id_is_required));
                        edt_sender_id.requestFocus();
                        return;
                    } else {}

                } else {
                    chk_sms = "false";
                }
            }
        });

       // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        if (Globals.objLPR == null) {

        } else {

            String ck_project_type = Globals.objLPR.getproject_id();

            if (ck_project_type.equals("standalone")) {

                ll_is_online.setVisibility(View.GONE);

            } else {
                ll_is_online.setVisibility(View.VISIBLE);
            }
        }

        //settings = Settings.getSettings(SetttingsActivity.this, database, "");
        try {
            if (settings.get_Is_sms().equals("true")) {
                edt_uri.setText(settings.get_URL());
                edt_key.setText(settings.get_Auth_Key());
                edt_sender_id.setText(settings.get_Sender_Id());
            } else {
                edt_uri.setText("https://control.msg91.com/api/sendhttp.php");
                edt_key.setText("102234");
                edt_sender_id.setText("102234");
            }
        } catch (Exception Ex) {

            edt_uri.setText("https://control.msg91.com/api/sendhttp.php");
            edt_key.setText("102234");
            edt_sender_id.setText("102234");
            Globals.AppLogWrite(edt_uri.getText().toString());
            Globals.AppLogWrite("URI SMS"+Ex.getMessage());
        }

        edt_list_limit.setText(Globals.ListLimit);

        if (settings != null) {
            try {
                last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                edt_sequence_no.setText(last_code.getlast_order_code());
            } catch (Exception ex) {
                edt_sequence_no.setText("0");
            }


            String strCheckAD = settings.get_Change_Parameter();
            if (strCheckAD.equals("AD")) {
                chk_ad.setChecked(true);
                chk_ac.setChecked(false);
                chk_bop.setChecked(false);
            }

            String strCheckAC = settings.get_Change_Parameter();
            if (strCheckAC.equals("AC")) {
                chk_ad.setChecked(false);
                chk_ac.setChecked(true);
                chk_bop.setChecked(false);
            }

            String strCheckBOP = settings.get_Change_Parameter();
            if (strCheckBOP.equals("BOP")) {
                chk_ad.setChecked(false);
                chk_ac.setChecked(false);
                chk_bop.setChecked(true);
            }

            String strOnline = settings.get_IsOnline();
            if (strOnline.equals("true")) {
                is_online.setChecked(true);
            }

            if (settings.get_Is_email()==null) {
                tl_email.setVisibility(View.GONE);
            }else {
                String strIsEmail = settings.get_Is_email();
                if (strIsEmail.equals("true")) {
                    is_email.setChecked(true);
                    tl_email.setVisibility(View.VISIBLE);
                } else {
                    tl_email.setVisibility(View.GONE);
                }
            }

            if (settings.get_Is_sms()==null){
                tl_sms.setVisibility(View.GONE);
            }else {
                String strIsSMS = settings.get_Is_sms();
                if (strIsSMS.equals("true")) {
                    is_sms.setChecked(true);
                    tl_sms.setVisibility(View.VISIBLE);
                } else {
                    tl_sms.setVisibility(View.GONE);
                }
            }

            String strIsShowBRC = settings.get_Is_BR_Scanner_Show();
            if (strIsShowBRC.equals("true")) {
                is_show_brc.setChecked(true);
            }

            String strIsShowZDetail = settings.get_Is_ZDetail_InPrint();
            if (strIsShowZDetail.equals("true")) {
                is_show_zdetail_inreprt.setChecked(true);
            }

            String strIsShowDeviceCus = settings.get_Is_Device_Customer_Show();
            if (strIsShowDeviceCus.equals("true")) {
                is_show_device_cus.setChecked(true);
            }



            if (settings.get_Is_Customer_Display()==null){
            }else {
                String strIsCMD = settings.get_Is_Customer_Display();
                if (strIsCMD.equals("true")) {
                    is_cmd.setChecked(true);
                    btn_cus_image.setVisibility(View.VISIBLE);
                } else {
                    btn_cus_image.setVisibility(View.GONE);
                }
            }

            String strIsCashDrawer = settings.get_Is_Cash_Drawer();
            if (strIsCashDrawer.equals("true")) {
                is_cash_drawer.setChecked(true);
                ll_cash_drawer.setVisibility(View.VISIBLE);
            } else {
                ll_cash_drawer.setVisibility(View.GONE);
            }


            strIsAccount = settings.get_Is_Accounts();
            if (strIsAccount.equals("true")) {
                is_accounts.setChecked(true);
            }

            strIsStockManage = settings.get_Is_Stock_Manager();
            if (strIsStockManage.equals("true")) {
                is_stock_manage.setChecked(true);
            }

            strIsZeroStock = settings.get_Is_Zero_Stock();
            if (strIsZeroStock.equals("true")) {
                is_zero_stck.setChecked(true);
            }

            strIsChangePrice = settings.get_Is_Change_Price();
            if (strIsChangePrice.equals("true")) {
                is_change_price.setChecked(true);
            }

            String strIsHSN = settings.get_HSN_print();
            if (strIsHSN.equals("true")) {
                chk_hsn.setChecked(true);
            }

            String strDenomination = settings.get_Is_Denomination();
            if (strDenomination.equals("true")) {
                is_denomination.setChecked(true);
                il_scale.setVisibility(View.VISIBLE);
            } else {
                il_scale.setVisibility(View.GONE);
            }


            String strBarcodePrint = settings.get_Is_BarcodePrint();
            if (strBarcodePrint.equals("true")) {
                is_barcode_print.setChecked(true);
            }
            String strIsShowPrintDialog = settings.get_Is_Print_Dialog_Show();
            if (strIsShowPrintDialog.equals("true")) {
                is_show_print_dialog.setChecked(true);
            }


            String strIsDeliveryDate = settings.getIs_deliverydate();
            if (strIsDeliveryDate.equals("true")) {
                chk_isdeliverydate.setChecked(true);
            }


            String strIsPosKot = settings.getIs_selfpos_KOT();
            if (strIsPosKot.equals("true")) {
                chk_is_selfposkot.setChecked(true);
            }

            String strIspayment = settings.getIs_paymentmethod();
            if (strIspayment.equals("true")) {
                chk_ispaymentmethod.setChecked(true);
            }

            String strIsApisync = settings.getParam1();
            if (strIsApisync.equals("true")) {
                chk_apisync.setChecked(true);
            }
            /******************************************/
            // Single window, fill advance amount, validate vehicle no, validate moble no parameters for parking Industry

            if(Globals.objLPR.getIndustry_Type().equals("4")) {
                String strIsShowsinglewindow = settings.getIs_singleWindow();
                if (strIsShowsinglewindow.equals("true")) {
                    is_showsinglewindow.setChecked(true);
                }
                String strIsShowFillAmnt = settings.getIs_FillAdvanceAmnt();
                if (strIsShowFillAmnt.equals("true")) {
                    is_showfilladvamnt.setChecked(true);
                }
                String strIsShowvalidateVehNo = settings.getIs_ValidateVehNo();
                if (strIsShowvalidateVehNo.equals("true")) {
                    is_validatevehno.setChecked(true);
                }
                String strIsShowvalidateMobNo = settings.getIs_ValidateMobNo();
                if (strIsShowvalidateMobNo.equals("true")) {
                    is_validatemobileno.setChecked(true);
                }

                String strIsShownfc = settings.getIs_NFC();
                if (strIsShownfc.equals("true")) {
                    is_nfc.setChecked(true);

                }
                else if (strIsShownfc.equals("false")){
                    is_nfc.setChecked(false);
                    is_validatevehno.setEnabled(true);

                    //is_validatevehno.setChecked(false);
                }

                String strIsShowcloudprint = settings.getIs_cloudprint();
                if (strIsShowcloudprint.equals("true")) {
                    chk_iscloudprint.setChecked(true);
                }

                is_nfc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                                           if(is_nfc.isChecked()) {

                                                               is_validatevehno.setChecked(true);
                                                               is_validatevehno.setEnabled(false);
                                                           }
                                                           else if(!is_nfc.isChecked()) {
                                                               is_validatevehno.setEnabled(true);
                                                              // is_validatevehno.setChecked(false);
                                                           }
                                                       }
                                                   }
                );
            }
            /*************************/
            String strDiscount = settings.get_Is_Discount();
            if (strDiscount.equals("true")) {
                is_discount.setChecked(true);
            }
            String strkitchenprint = settings.getIs_KitchenPrint();
            if (strkitchenprint.equals("true")) {
                chk_is_kitchenprint.setChecked(true);
            }

            String strsaveprint = settings.getIs_saveprint();
            if (strsaveprint.equals("true")) {
                chk_is_saveprint.setChecked(true);
            }
            String strIsPrintKOT = settings.get_Is_KOT_Print();
            if (strIsPrintKOT.equals("true")) {
                is_print_kot.setChecked(true);
            }

            String strIsPrintInvoice = settings.get_Is_Print_Invoice();
            if (strIsPrintInvoice.equals("true")) {
                is_print_invoice.setChecked(true);
            }

            String strIsFileShare = settings.get_Is_File_Share();
            if (strIsFileShare.equals("true")) {
                is_file_share.setChecked(true);
            }

            String strIsCostShow = settings.get_Is_Cost_Show();
            if (strIsCostShow.equals("true")) {
                is_costp_show.setChecked(true);
            }


            edt_no_of_prnt.setText(settings.get_No_Of_Print());
            edt_gst_lbl.setText(settings.get_GST_Label());
            edt_gst_no.setText(settings.get_Gst_No());
            edt_print_order.setText(settings.get_Print_Order());
            edt_print_cashier.setText(settings.get_Print_Cashier());
            edt_print_inv_no.setText(settings.get_Print_InvNo());
            edt_print_inv_date.setText(settings.get_Print_InvDate());
            edt_print_device_id.setText(settings.get_Print_DeviceID());

            edt_footer.setText(settings.get_Footer_Text());
            edt_email.setText(settings.get_Email());
            edt_pass.setText(settings.get_Password());
            edt_host.setText(settings.get_Host());
            edt_port.setText(settings.get_Port());
            edt_mnger_email.setText(settings.get_Manager_Email());

            try {
                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                img_logo.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Toast.makeText(SetttingsActivity.this, R.string.noSet, Toast.LENGTH_SHORT).show();
        }

        setPrinterType(settings);
        setPrinterLang(settings);
        setScale(settings);
        setItemTax(settings);
        setQtyDecimal(settings);
        setCustomerImage(settings);
        setHomeLayout(settings);
        setDefaultOrderType(settings);
        setPrintMemo(settings);
        setQRType(settings);

        is_cmd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pri_pos = sp_print.getSelectedItemPosition();
               if( pri_pos==9){

                   if (isChecked) {

                       btn_cus_image.setVisibility(View.GONE);
                   }
               }
               else {
                   if (isChecked) {
                       btn_cus_image.setVisibility(View.VISIBLE);
                   } else {
                       btn_cus_image.setVisibility(View.GONE);
                   }
               }
            }
        });

        is_denomination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    il_scale.setVisibility(View.VISIBLE);
                } else {
                    il_scale.setVisibility(View.GONE);
                }
            }
        });


        sp_print.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {

                if (Globals.objLPR.getIndustry_Type().equals("4")) {
         if(pos==1){
     btn_searchBT.setVisibility(View.GONE);
     btn_testprint.setVisibility(View.VISIBLE);
             ll_cus_img.setVisibility(View.GONE);
             ll_CMD.setVisibility(View.GONE);
             ll_cash_drawer.setVisibility(View.GONE);
             chk_is_selfposkot.setChecked(false);
             chk_is_selfposkot.setVisibility(View.GONE);
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
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
                        btn_searchBT.setVisibility(View.VISIBLE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        if (home_layout == 0) {
                            mService = SetttingsActivity.mService;
                        } else {
                            mService = SetttingsActivity.mService;
                        }
                    }

                    if (pos==5) {

                        btn_searchBT.setVisibility(View.VISIBLE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
                        if (home_layout == 0) {
                            mService = SetttingsActivity.mService;
                        } else {
                            mService = SetttingsActivity.mService;
                        }
                    }

                    if (pos == 6) {
                        btn_searchBT.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
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
                    if(pos==7){
                        btn_searchBT.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);

                    }
                    if(pos==0){
                        btn_searchBT.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);

                    }

                    /*if (pos == 6) {

                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_searchBT.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.VISIBLE);
                        ll_cash_drawer.setVisibility(View.VISIBLE);
                    } else {
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
                    }*/
                }

                else {
                    if (pos == 3 || pos == 4) {

                        btn_searchBT.setVisibility(View.VISIBLE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        if (home_layout == 0) {
                            mService = SetttingsActivity.mService;
                        } else {
                            mService = SetttingsActivity.mService;
                        }
                    }


                    if (sp_print.getSelectedItemPosition() == 1 || sp_print.getSelectedItemPosition() == 9) {
                        btn_searchBT.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);

                        if(pos==1){
                        ll_CMD.setVisibility(View.GONE);
                        is_cash_drawer.setChecked(false);
                        ll_cash_drawer.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);
                            chk_is_selfposkot.setVisibility(View.GONE);
                            chk_is_selfposkot.setChecked(false);

                        }
                        else{
                            ll_CMD.setVisibility(View.VISIBLE);
                            ll_cash_drawer.setVisibility(View.VISIBLE);
                            chk_is_selfposkot.setVisibility(View.VISIBLE);
                        }
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

if(pos==6){
    ll_CMD.setVisibility(View.VISIBLE);
    ll_cash_drawer.setVisibility(View.VISIBLE);
    chk_is_selfposkot.setVisibility(View.VISIBLE);
}
                    if (pos == 8) {
                        btn_searchBT.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.VISIBLE);
                        ll_CMD.setVisibility(View.GONE);
                        is_cash_drawer.setChecked(false);
                        ll_cash_drawer.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
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
                            ip.setText(settings.getPrinterIp());
                        }
                        btn_testprint.setVisibility(View.VISIBLE);
                        btn_searchBT.setVisibility(View.VISIBLE);
                        ll_CMD.setVisibility(View.GONE);
                        is_cash_drawer.setChecked(false);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
                        ll_cus_img.setVisibility(View.GONE);

                    } else {
                        wifi_set.setVisibility(View.GONE);
                    }



                    if (pos == 0) {

                        btn_searchBT.setVisibility(View.GONE);
                        is_print_invoice.setChecked(false);
                        ll_cus_img.setVisibility(View.GONE);
                        ll_CMD.setVisibility(View.GONE);
                        is_cash_drawer.setChecked(false);
                        chk_is_selfposkot.setChecked(false);
                        chk_is_selfposkot.setVisibility(View.GONE);
                        ll_cash_drawer.setVisibility(View.GONE);
                        btn_testprint.setVisibility(View.GONE);
                    } else {
                        is_print_invoice.setChecked(true);
                        // btn_testprint.setVisibility(View.VISIBLE);

                    }
                }
            }
                @Override
                public void onNothingSelected (AdapterView < ? > arg0){
                }

        });

        btn_cus_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent item_intent = new Intent(SetttingsActivity.this, CustomerImageActivity.class);
                startActivity(item_intent);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.ListLimit = edt_list_limit.getText().toString();
                Globals.cart.clear();
                Globals.order_item_tax.clear();
                Globals.TotalItemPrice = 0;
                Globals.TotalQty = 0;
                Globals.setEmpty();
                long e = Settings.delete_settings(SetttingsActivity.this, "Settings", database, "", new String[]{});

                strEmail = edt_email.getText().toString().trim();
                strPass = edt_pass.getText().toString().trim();
                strHost = edt_host.getText().toString().trim();
                strPort = edt_port.getText().toString().trim();

                if (chk_ad.isChecked()) {
                    strChangeParm = "AD";
                }

                if (chk_ac.isChecked()) {
                    strChangeParm = "AC";
                }

                if (chk_bop.isChecked()) {
                    strChangeParm = "BOP";
                }

                if (is_show_brc.isChecked()) {
                    chk_is_show_brc = "true";
                } else {
                    chk_is_show_brc = "false";
                }

                if (is_show_zdetail_inreprt.isChecked()) {
                    chk_is_zdetail_inreprt = "true";
                } else {
                    chk_is_zdetail_inreprt = "false";
                }

                if (is_show_device_cus.isChecked()) {
                    chk_is_show_device_cus = "true";
                } else {
                    chk_is_show_device_cus = "false";
                }

                if (is_show_print_dialog.isChecked())
                {
                    chk_is_show_print_dialog = "true";
                } else {
                    chk_is_show_print_dialog = "false";
                }

                if (is_denomination.isChecked()) {
                    chk_is_denomination = "true";
                } else {
                    chk_is_denomination = "false";
                }

                if (is_barcode_print.isChecked()) {
                    chk_is_barcode = "true";
                } else {
                    chk_is_barcode = "false";
                }

                if (is_discount.isChecked()) {
                    chk_is_discount = "true";
                } else {
                    chk_is_discount = "false";
                }

                if (is_online.isChecked()) {
                    chk = "true";
                } else {
                    chk = "false";
                }

                if (chk_hsn.isChecked()) {
                    chk_hsn_prnt = "true";
                } else {
                    chk_hsn_prnt = "false";
                }


                if (is_print_kot.isChecked()) {
                    chk_is_print_kot = "true";
                } else {
                    chk_is_print_kot = "false";
                }


                if (is_print_invoice.isChecked()) {
                    chk_is_print_invoice = "true";
                } else {
                    chk_is_print_invoice = "false";
                }

                if (is_file_share.isChecked()) {
                    chk_is_file_share = "true";
                } else {
                    chk_is_file_share = "false";
                }

                if (is_accounts.isChecked()) {
                    chk_is_account = "true";
                } else {
                    chk_is_account = "false";
                }

                if (is_stock_manage.isChecked()) {
                    chk_is_stock_manage = "true";
                } else {
                    chk_is_stock_manage = "false";
                }

                if (is_cash_drawer.isChecked()) {
                    chk_is_cash_drawer = "true";
                } else {
                    chk_is_cash_drawer = "false";
                }

                if (is_zero_stck.isChecked()) {
                    chk_is_zero_stock = "true";
                } else {
                    chk_is_zero_stock = "false";
                }

                if (is_change_price.isChecked()) {
                    chk_is_change_price = "true";
                } else {
                    chk_is_change_price = "false";
                }

                if (is_costp_show.isChecked()) {
                    chk_is_cost_show = "true";
                } else {
                    chk_is_cost_show = "false";
                }

                if (is_email.isChecked()) {
                    chk_email = "true";

                    if (!isValidEmail(strEmail)) {
                        edt_email.setError(getString(R.string.Invalid_Email));
                        edt_email.requestFocus();
                        return;
                    } else {
                        strEmail = edt_email.getText().toString();
                    }

                    if (strPass.equals("")) {
                        edt_pass.setError(getString(R.string.Password_is_required));
                        edt_pass.requestFocus();
                        return;
                    } else {
                        strPass = edt_pass.getText().toString();
                    }

                    if (strHost.equals("")) {
                        edt_host.setError(getString(R.string.Password_is_required));
                        edt_host.requestFocus();
                        return;
                    } else {
                        strHost = edt_host.getText().toString();
                    }

                    if (strPort.equals("")) {
                        edt_port.setError(getString(R.string.Password_is_required));
                        edt_port.requestFocus();
                        return;
                    } else {
                        strPort = edt_port.getText().toString();
                    }


                    //test_email(strEmail, strPass, edt_mnger_email.getText().toString());

                } else {
                    chk_email = "false";
                }

                if (is_sms.isChecked()) {
                    chk_sms = "true";

                    if (edt_uri.getText().toString().equals("")) {
                        edt_uri.setError(getString(R.string.Url_is_required));
                        edt_uri.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_key.getText().toString().equals("")) {
                        edt_key.setError(getString(R.string.Key_is_required));
                        edt_key.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_sender_id.getText().toString().equals("")) {
                        edt_sender_id.setError(getString(R.string.Sender_id_is_required));
                        edt_sender_id.requestFocus();
                        return;
                    } else {
                    }

                } else {
                    chk_sms = "false";
                }
//
//
                if (is_cmd.isChecked()) {
                    chk_cmd = "true";

                } else {
                    chk_cmd = "false";
                }
//
                strMgEmail = edt_mnger_email.getText().toString().trim();
                if (strMgEmail.equals("")) {
                } else {
                    {
                        strMgEmail = edt_mnger_email.getText().toString().trim();
                    }
                }

                pri_pos = sp_print.getSelectedItemPosition();
                if (pri_pos == 2) {
                    wifi_ip = ip.getText().toString().trim();
                    if (wifi_ip.equals("")) {
                        Toast.makeText(SetttingsActivity.this, R.string.plsinsrtIp, Toast.LENGTH_SHORT).show();
                    }
                }

                qr_type = sp_qr_type.getSelectedItemPosition();
                if (qr_type == 0) {
                    strQRType = "0";
                } else if (qr_type == 1) {
                    strQRType = "1";
                }

                scale = sp_scale.getSelectedItemPosition();
                if (scale == 0) {
                    strScale = "0";
                } else if (scale == 1) {
                    strScale = "1";
                } else if (scale == 2) {
                    strScale = "5";
                }

                qty = sp_qty_dicml.getSelectedItemPosition();
                if (qty == 0) {
                    strQtyDecimal = "0";
                } else if (qty == 1) {
                    strQtyDecimal = "1";
                } else if (qty == 2) {
                    strQtyDecimal = "2";
                } else if (qty == 3) {
                    strQtyDecimal = "3";
                }

                int pri_lang = sp_print_lang.getSelectedItemPosition();
                if (pri_lang == 0) {
                    strPriLang = "0";
                } else if (pri_lang == 1) {
                    strPriLang = "1";
                } else if (pri_lang == 2) {
                    strPriLang = "2";
                }

                item_tax = sp_item_tax.getSelectedItemPosition();
                if (item_tax == 0) {
                    strItemTax = "0";
                } else if (item_tax == 1) {
                    strItemTax = "1";
                } else if (item_tax == 2) {
                    strItemTax = "2";
                } else if (item_tax == 3) {
                    strItemTax = "3";
                }

                home_layout = sp_home_layout.getSelectedItemPosition();
                if (home_layout == 0) {
                    strHomeLayout = "0";
                } else if (home_layout == 1) {
                    strHomeLayout = "1";
                } else if (home_layout == 2) {
                    strHomeLayout = "2";
                }

                cus_dis = sp_cus_dis.getSelectedItemPosition();
                if (cus_dis == 0) {
                    strCusDis = "0";
                } else if (cus_dis == 1) {
                    strCusDis = "1";
                } else if (cus_dis == 2) {
                    strCusDis = "2";
                }

                String logo;
                if (Globals.logo1 == null) {
                    logo = settings.get_Logo();
                } else {
                    logo = BitmapToString(Globals.logo1);
                }

                default_ordertype = sp_defualt_odrtype.getSelectedItemPosition();
                if (default_ordertype == 0) {
                    strDefaultOrderType = "1";
                } else if (default_ordertype == 1) {
                    strDefaultOrderType = "2";
                } else if (default_ordertype == 2) {
                    strDefaultOrderType = "3";
                } else if (default_ordertype == 3) {
                    strDefaultOrderType = "4";
                } else if (default_ordertype == 4) {
                    strDefaultOrderType = "5";
                }

                print_memo = sp_print_memo.getSelectedItemPosition();
                if (print_memo == 0) {
                    strPrintMemo = "0";
                } else if (print_memo == 1) {
                    strPrintMemo = "1";
                }

                pDialog = new ProgressDialog(SetttingsActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                final String finalLogo = logo;
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(2000);
                            database.beginTransaction();
                            settings = new Settings(SetttingsActivity.this, null, chk, String.valueOf(pri_pos), wifi_ip, strScale, strEmail, strPass, finalLogo, strMgEmail, chk_sms, chk_email, edt_uri.getText().toString(), edt_key.getText().toString(), edt_sender_id.getText().toString(), strPriLang, chk_cmd, chk_hsn_prnt, strItemTax, settings.get_Copy_Right(), strQtyDecimal, edt_footer.getText().toString().trim(), strCusDis, chk_is_denomination, chk_is_barcode, chk_is_discount, edt_gst_no.getText().toString().trim(), edt_print_order.getText().toString().trim(), edt_print_cashier.getText().toString().trim(), edt_print_inv_no.getText().toString().trim(), edt_print_inv_date.getText().toString().trim(), edt_print_device_id.getText().toString().trim(), chk_is_print_kot, chk_is_print_invoice, chk_is_file_share, strHost, strPort, strChangeParm, chk_is_account, chk_is_stock_manage, strHomeLayout, chk_is_cash_drawer, chk_is_zero_stock, chk_is_change_price, edt_no_of_prnt.getText().toString().trim(), edt_gst_lbl.getText().toString().trim(), chk_is_zdetail_inreprt, chk_is_show_device_cus, chk_is_show_print_dialog, chk_is_show_brc, strDefaultOrderType, strPrintMemo,chk_is_cost_show,strQRType,"false","false","false","false","false","false","false","false","false","false","false","","false","","");
                            long l = settings.insertSettings(database);
                            if (l > 0) {
                                try {
                                    last_code.setlast_order_code(edt_sequence_no.getText().toString().trim());
                                    last_code.updateLast_Code("id=?", new String[]{last_code.getid()}, database);
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    pDialog.dismiss();
                                } catch (Exception e) {
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    pDialog.dismiss();
                                }
                                Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                                Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                                Globals.strIsDiscountPrint = settings.get_Is_Discount();
                                Globals.GSTNo = settings.get_Gst_No();
                                Globals.GSTLbl = settings.get_GST_Label();
                                Globals.PrintOrder = settings.get_Print_Order();
                                Globals.PrintCashier = settings.get_Print_Cashier();
                                Globals.PrintInvNo = settings.get_Print_InvNo();
                                Globals.PrintInvDate = settings.get_Print_InvDate();
                                Globals.PrintDeviceID = settings.get_Print_DeviceID();  Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                                Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                                Globals.strIsDiscountPrint = settings.get_Is_Discount();
                                Globals.GSTNo = settings.get_Gst_No();
                                Globals.GSTLbl = settings.get_GST_Label();
                                Globals.PrintOrder = settings.get_Print_Order();
                                Globals.PrintCashier = settings.get_Print_Cashier();
                                Globals.PrintInvNo = settings.get_Print_InvNo();
                                Globals.PrintInvDate = settings.get_Print_InvDate();
                                Globals.PrintDeviceID = settings.get_Print_DeviceID();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SetttingsActivity.this, R.string.savesucc, Toast.LENGTH_SHORT).show();
                                       if(Globals.objLPR.getproject_id().equals("cloud")){
                                           onBackPressed();

                                       }
                                       else if(Globals.objLPR.getproject_id().equals("standalone")){

                                       }
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });

        img_logo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void setPrintMemo(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.print_memo);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_print_memo.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_print_memo.setSelection(Integer.parseInt(settings.get_Print_Memo()));
            } catch (Exception ex) {}
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setDefaultOrderType(Settings settings) {
        try {
            order_typeArrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "", database);
            DefaultOrderTypeSettingAdapter defaultOrderTypeSettingAdapter = new DefaultOrderTypeSettingAdapter(getApplicationContext(), order_typeArrayList);
            sp_defualt_odrtype.setAdapter(defaultOrderTypeSettingAdapter);

            if (!settings.get_Default_Ordertype().equals("")) {
                for (int i = 0; i < defaultOrderTypeSettingAdapter.getCount(); i++) {
                    String iname = order_typeArrayList.get(i).get_order_type_id();
                    if (settings.get_Default_Ordertype().equals(iname)) {
                        sp_defualt_odrtype.setSelection(i);
                        break;
                    }
                }
            }

        }catch (Exception ex) {
        }
    }

    private void setHomeLayout(Settings settings) {

        String[] stringArray = new String[0];
        if(Globals.objLPR.getproject_id().equals("cloud")) {
            stringArray = getResources().getStringArray(R.array.home_layout);
        }
        else if(Globals.objLPR.getproject_id().equals("standalone")){
            stringArray = getResources().getStringArray(R.array.home_layout_standalone);

        }

        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_home_layout.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_home_layout.setSelection(Integer.parseInt(settings.get_Home_Layout()));


            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }




   private void setCustomerImage(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.customer_display);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_cus_dis.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_cus_dis.setSelection(Integer.parseInt(settings.get_CustomerDisplay()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setQtyDecimal(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.qty_decimal);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_qty_dicml.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_qty_dicml.setSelection(Integer.parseInt(settings.get_Qty_Decimal()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setItemTax(Settings settings) {

        String[] stringArray = getResources().getStringArray(R.array.item_tax);
        ArrayAdapter<String> adapterItemTax = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_item_tax.setAdapter(adapterItemTax);
        if (settings != null) {
            try {
                sp_item_tax.setSelection(Integer.parseInt(settings.get_ItemTax()));
            } catch (Exception ex) {

            }
        }
        adapterItemTax.notifyDataSetChanged();
    }

    private void test_email(final String strEmail, final String strPass, String s,final String strhost,final String strPort) {

//        String[] recipients = {strEmail};
//        final SendEmailAsyncTask email = new SendEmailAsyncTask();
//        email.activity = this;
//        email.m = new GMailSender("npaliwal24@gmail.com", strPass);
//        email.m.set_from("npaliwal24@gmail.com");
//        email.m.setBody("Test Email");
//        email.m.set_to(recipients);
//        email.m.set_subject("litePOS");
//        email.execute();

        String[] recipients = s.split(",");
        final SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new GMailSender(strEmail, strPass, strhost, strPort);

        email.m.set_from(strEmail);
        email.m.setBody("Dear Customer, \n" +
                " \n" +
                "This is a test email sent from TriggerPOS. If you received this email, it confirms that your TriggerPOS email notification service has been successfully set-up.\n" +
                "Thank you for choosing TriggerPOS.\n" +
                "\n" +
             "Kind Regards\n" +
             "TriggerPOS Team");
        email.m.set_to(recipients);

        email.m.set_subject("Test Email from TriggerPOS ");


        email.execute();

    }

    private void setScale(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.Scale);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        // this.sp_scale.setAdapter(adapterPtype1);
        String compareValue = settings.get_Scale();
        this.sp_scale.setAdapter(adapterPtype1);

        if (compareValue != null) {
            try {

                int spinnerPosition = adapterPtype1.getPosition(settings.get_Scale());
                // newSpinner.setSelection(spinnerPosition);
                sp_scale.setSelection(spinnerPosition);
            } catch (Exception ex) {

            }
            adapterPtype1.notifyDataSetChanged();
        }
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

    private void setPrinterLang(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.Printer_Lang);
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_print_lang.setAdapter(adapterPtype);
        if (settings != null) {
            sp_print_lang.setSelection(Integer.parseInt(settings.get_Print_Lang()));
        }
        adapterPtype.notifyDataSetChanged();
    }

    private void setQRType(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.qr_type);
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_qr_type.setAdapter(adapterPtype);
        if (settings != null) {
            sp_qr_type.setSelection(Integer.parseInt(settings.get_QR_Type()));
        }
        adapterPtype.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:      //���������
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {   //�����Ѿ���
                    Toast.makeText(getApplicationContext(), "Bluetooth open successful", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //��������ĳһ�����豸
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {   //�ѵ�������б��е�ĳ���豸��
                    String address = data.getExtras()
                            .getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);  //��ȡ�б������豸��mac��ַ
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                    Toast.makeText(getApplicationContext(), "Bluetooth Connect successfully", Toast.LENGTH_SHORT).show();

                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                    uri = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // Log.d(TAG, String.valueOf(bitmap));
                        img_logo.setImageBitmap(bitmap);
                        Globals.logo1 = bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
/*        pDialog = new ProgressDialog(SetttingsActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/


                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(SetttingsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                           // pDialog.dismiss();
                            finish();

                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            if(Globals.objLPR.getproject_id().equals("standalone")){
                                Globals.objLPR.setIndustry_Type("2");
                                long ct = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                if (ct > 0) {
                                    // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct1 > 0) {
                                        // database.endTransaction();
                                        Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }
                                    // succ_manu = "1";
                                    //}
                                }
                                /*Intent intent = new Intent(SetttingsActivity.this, Retail_IndustryActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);*/
                            }
                            else {
                                Intent intent = new Intent(SetttingsActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                           // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(SetttingsActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                          //  pDialog.dismiss();
                            finish();

                        } finally {
                        }
                    }
                }
                else if (Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(SetttingsActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else if (Globals.objLPR.getIndustry_Type().equals("3")){
                    Intent intent = new Intent(SetttingsActivity.this, PaymentCollection_MainScreen.class);
                    intent.putExtra("whatsappFlag","false");
                    intent.putExtra("contact_code", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(SetttingsActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
          /*  }
        };
        timerThread.start();*/
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        SetttingsActivity activity;

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


                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
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

                return false;
            }
        }

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void sendMySMS(String auth_key, String url, String sender_id) {

        //Your authentication key
        String authkey = auth_key;
//Multiple mobiles numbers separated by comma
        String mobiles = "9530470882";
//Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = sender_id;
//Your message to send, Add URL encoding here.
        String message = "Test message";
//define route
        String route = "default";

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

//encoding message
        String encoded_message = URLEncoder.encode(message);

//Send SMS API
        String mainUrl = url;

//Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + senderId);

//final string
        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
                Log.d("RESPONSE", "" + response);

            //finally close connection
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void initview(){
        is_costp_show = (CheckBox) findViewById(R.id.is_costp_show);
        is_show_brc = (CheckBox) findViewById(R.id.is_show_brc);
        is_show_device_cus = (CheckBox) findViewById(R.id.is_show_device_cus);
        is_show_print_dialog = (CheckBox) findViewById(R.id.is_show_print_dialog);
        is_show_print_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(sp_print.getItemAtPosition(sp_print.getSelectedItemPosition()).toString().equals("--Select Printer--"))
                {
                    is_show_print_dialog.setChecked(false);
                    Toast.makeText(SetttingsActivity.this,"Please select printer ",Toast.LENGTH_LONG).show();
                }


            }
        });
        is_show_zdetail_inreprt = (CheckBox) findViewById(R.id.is_show_zdetail_inreprt);
        is_cash_drawer = (CheckBox) findViewById(R.id.is_cash_drawer);
        is_online = (CheckBox) findViewById(R.id.is_online);
        sp_print_memo = (Spinner) findViewById(R.id.sp_print_memo);
        sp_defualt_odrtype = (Spinner) findViewById(R.id.sp_defualt_odrtype);
        sp_print = (Spinner) findViewById(R.id.sp_print);
        sp_scale = (Spinner) findViewById(R.id.sp_scale);
        sp_home_layout = (Spinner) findViewById(R.id.sp_home_layout);
        sp_qty_dicml = (Spinner) findViewById(R.id.sp_qty_dicml);
        sp_item_tax = (Spinner) findViewById(R.id.sp_item_tax);
        sp_print_lang = (Spinner) findViewById(R.id.sp_print_lang);
        sp_cus_dis = (Spinner) findViewById(R.id.sp_cus_dis);
        sp_qr_type = (Spinner) findViewById(R.id.sp_qr_type);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        edt_uri = (EditText) findViewById(R.id.edt_uri);
        edt_key = (EditText) findViewById(R.id.edt_key);
        edt_sender_id = (EditText) findViewById(R.id.edt_sender_id);
        edt_no_of_prnt = (EditText) findViewById(R.id.edt_no_of_prnt);
        edt_gst_lbl = (EditText) findViewById(R.id.edt_gst_lbl);
        edt_list_limit = (EditText) findViewById(R.id.edt_list_limit);
        tl_email = (TableLayout) findViewById(R.id.tl_email);
        tl_sms = (TableLayout) findViewById(R.id.tl_sms);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_logo_add = (ImageView) findViewById(R.id.img_logo_add);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        save = (Button) findViewById(R.id.save);
        btn_cus_image = (Button) findViewById(R.id.btn_cus_image);
        btn_test_email = (Button) findViewById(R.id.btn_test_email);
        btn_test_sms = (Button) findViewById(R.id.btn_test_sms);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        wifi_set = (TableRow) findViewById(R.id.wifi_set_row);
        ip = (EditText) findViewById(R.id.ip);
        edt_mnger_email = (EditText) findViewById(R.id.edt_mnger_email);
        is_email = (CheckBox) findViewById(R.id.is_email);
        is_sms = (CheckBox) findViewById(R.id.is_sms);
        is_cmd = (CheckBox) findViewById(R.id.is_cmd);
        chk_hsn = (CheckBox) findViewById(R.id.chk_hsn);
        edt_footer = (EditText) findViewById(R.id.edt_footer);
        is_denomination = (CheckBox) findViewById(R.id.is_denomination);
        is_barcode_print = (CheckBox) findViewById(R.id.is_barcode_print);
        is_discount = (CheckBox) findViewById(R.id.is_discount);
        is_print_kot = (CheckBox) findViewById(R.id.is_print_kot);
        is_print_invoice = (CheckBox) findViewById(R.id.is_print_invoice);
        is_file_share = (CheckBox) findViewById(R.id.is_file_share);
        chk_ad = (CheckBox) findViewById(R.id.chk_ad);
        chk_ac = (CheckBox) findViewById(R.id.chk_ac);
        chk_bop = (CheckBox) findViewById(R.id.chk_bop);
        is_accounts = (CheckBox) findViewById(R.id.is_accounts);
        is_stock_manage = (CheckBox) findViewById(R.id.is_stock_manage);
        is_zero_stck = (CheckBox) findViewById(R.id.is_zero_stck);
        is_change_price = (CheckBox) findViewById(R.id.is_change_price);
        edt_gst_no = (EditText) findViewById(R.id.edt_gst_no);
        edt_print_order = (EditText) findViewById(R.id.edt_print_order);
        edt_print_cashier = (EditText) findViewById(R.id.edt_print_cashier);
        edt_print_inv_no = (EditText) findViewById(R.id.edt_print_inv_no);
        edt_print_inv_date = (EditText) findViewById(R.id.edt_print_inv_date);
        edt_print_device_id = (EditText) findViewById(R.id.edt_print_device_id);
        edt_sequence_no = (EditText) findViewById(R.id.edt_sequence_no);
        edt_host = (EditText) findViewById(R.id.edt_host);
        edt_port = (EditText) findViewById(R.id.edt_port);
        il_scale = (LinearLayout) findViewById(R.id.ll_scale);
        ll_cash_drawer = (LinearLayout) findViewById(R.id.ll_cash_drawer);
        ll_is_online = (LinearLayout) findViewById(R.id.ll_is_online);
        ll_CMD = (LinearLayout) findViewById(R.id.ll_CMD);
        ll_cus_img = (LinearLayout) findViewById(R.id.ll_cus_img);
        is_print_invoice.setVisibility(View.GONE);
        btn_testprint=(Button)findViewById(R.id.btn_testprint);
        btn_searchBT=(Button)findViewById(R.id.btn_searchbtdevice);
      ll_paymentcollection=(LinearLayout)findViewById(R.id.ll_paymentcollection);
      ll_homelayout=(LinearLayout)findViewById(R.id.ll_home_layout);
        ll_paymentcollection2=(LinearLayout)findViewById(R.id.ll_paymentcollection2);
        ll_paymentcollection3=(LinearLayout)findViewById(R.id.ll_paymentcollection3);
        ll_paymentcollection4=(LinearLayout)findViewById(R.id.ll_paycollection4);
        ll_hsnprint=(LinearLayout)findViewById(R.id.ll_hsn_print);
        ll_ordertype=(LinearLayout)findViewById(R.id.ll_ordertype);
        ll_qrscanning=(LinearLayout)findViewById(R.id.ll_qrscanning);
        ll_item_tax=(LinearLayout)findViewById(R.id.ll_item_tax) ;
        is_showsinglewindow=(CheckBox)findViewById(R.id.is_show_singlewindow);
        view7=(View)findViewById(R.id.view7);
        viewdirect=(View)findViewById(R.id.viewdirect);
        is_showfilladvamnt=(CheckBox)findViewById(R.id.is_show_filladvamnt);
        is_validatevehno=(CheckBox)findViewById(R.id.is_show_vehicleno);
        is_validatemobileno=(CheckBox)findViewById(R.id.is_show_mobileno);
        is_nfc=(CheckBox)findViewById(R.id.is_show_nfc);
        chk_is_kitchenprint=(CheckBox)findViewById(R.id.is_kitchenprinting);
        chk_is_saveprint=(CheckBox)findViewById(R.id.is_showsave_print);
        chk_iscloudprint=(CheckBox)findViewById(R.id.is_showcloud_print);
         ll_printlang=(LinearLayout)findViewById(R.id.pri_lang);
        ll_printmemo=(LinearLayout)findViewById(R.id.ll_printmemo);
        ll_sel_logo=(LinearLayout)findViewById(R.id.ll_sel_logo);
        ll_qtydecimal=(LinearLayout)findViewById(R.id.ll_qty_dicml);
        ll_noprint=(TableLayout) findViewById(R.id.ll_noprint);
        lbl_eml=(TextView)findViewById(R.id.lbl_eml);
        view1=(View)findViewById(R.id.view1);
        chk_isdeliverydate=(CheckBox)findViewById(R.id.is_showdeliverydate);
        chk_is_selfposkot=(CheckBox)findViewById(R.id.is_selfposkitchenprint);
        chk_ispaymentmethod=(CheckBox)findViewById(R.id.is_showpaymentmethod);
        chk_apisync=(CheckBox)findViewById(R.id.is_syncatlogin) ;
        Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

chk_iscloudprint.setVisibility(View.GONE);
        if(Globals.objLPR.getIndustry_Type().equals("3")){


            ll_paymentcollection.setVisibility(View.GONE);
            ll_paymentcollection2.setVisibility(View.GONE);
            ll_paymentcollection3.setVisibility(View.GONE);
            ll_paymentcollection4.setVisibility(View.GONE);
            ll_homelayout.setVisibility(View.GONE);
            chk_is_kitchenprint.setVisibility(View.GONE);
             ll_hsnprint.setVisibility(View.GONE);
             chk_iscloudprint.setVisibility(View.GONE);
             chk_isdeliverydate.setVisibility(View.GONE);
           /* is_costp_show.setVisibility(View.GONE);
                    is_show_brc.setVisibility(View.GONE);
                    is_show_print_dialog.setVisibility(View.GONE);
                    is_show_device_cus.setVisibility(View.GONE);
                    is_show_zdetail_inreprt.setVisibility(View.GONE);
                    is_change_price.setVisibility(View.GONE);
                    is_zero_stck.setVisibility(View.GONE);

                    is_cash_drawer.setVisibility(View.GONE);
            is_stock_manage.setVisibility(View.GONE);
                    is_accounts.setVisibility(View.GONE);
                    chk_ad.setVisibility(View.GONE);
                    chk_ac.setVisibility(View.GONE);
                    chk_bop.setVisibility(View.GONE);
                    is_file_share.setVisibility(View.GONE);
                     is_online.setVisibility(View.GONE);
                    is_cmd.setVisibility(View.GONE);
                    chk_hsn.setVisibility(View.GONE);
                    is_denomination.setVisibility(View.GONE);
                    is_barcode_print.setVisibility(View.GONE);
                    is_discount.setVisibility(View.GONE);
                    is_print_kot.setVisibility(View.GONE);
                    is_print_invoice.setVisibility(View.GONE);;
                 sp_qr_type.setVisibility(View.GONE);
                     sp_print_memo.setVisibility(View.GONE);
                     sp_defualt_odrtype.setVisibility(View.GONE);
                   sp_scale.setVisibility(View.GONE);

                     sp_item_tax.setVisibility(View.GONE);
                     sp_qty_dicml.setVisibility(View.GONE);
                     sp_cus_dis.setVisibility(View.GONE);
                     sp_home_layout.setVisibility(View.GONE);;
            edt_list_limit.setVisibility(View.GONE);
                    edt_gst_lbl.setVisibility(View.GONE);
                    edt_no_of_prnt.setVisibility(View.GONE);

                    edt_sequence_no.setVisibility(View.GONE);
                    edt_footer.setVisibility(View.GONE);
                    edt_gst_no.setVisibility(View.GONE);
                    edt_print_order.setVisibility(View.GONE);
                    edt_print_cashier.setVisibility(View.GONE);
                    edt_print_inv_no.setVisibility(View.GONE);
                    edt_print_inv_date.setVisibility(View.GONE);
                    edt_print_device_id.setVisibility(View.GONE);
*/

        }
        else if(Globals.objLPR.getIndustry_Type().equals("4")){

            is_showsinglewindow.setVisibility(View.VISIBLE);
            is_showfilladvamnt.setVisibility(View.VISIBLE);
            is_validatevehno.setVisibility(View.VISIBLE);
            is_validatemobileno.setVisibility(View.VISIBLE);
            ll_ordertype.setVisibility(View.GONE);
            ll_qrscanning.setVisibility(View.GONE);
            is_discount.setVisibility(View.GONE);
            ll_item_tax.setVisibility(View.GONE);
            view7.setVisibility(View.GONE);
            viewdirect.setVisibility(View.GONE);
            is_file_share.setVisibility(View.VISIBLE);
            is_nfc.setVisibility(View.VISIBLE);
            ll_hsnprint.setVisibility(View.GONE);
            ll_paymentcollection.setVisibility(View.GONE);
            chk_is_kitchenprint.setVisibility(View.GONE);
            ll_homelayout.setVisibility(View.GONE);
            chk_iscloudprint.setVisibility(View.VISIBLE);
           ll_item_tax.setVisibility(View.GONE);
           ll_ordertype.setVisibility(View.GONE);
           ll_qrscanning.setVisibility(View.GONE);
           ll_hsnprint.setVisibility(View.GONE);
            ll_qtydecimal.setVisibility(View.GONE);
            is_email.setVisibility(View.GONE);
            lbl_eml.setVisibility(View.GONE);
            ll_sel_logo.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            chk_isdeliverydate.setVisibility(View.GONE);
            chk_apisync.setVisibility(View.GONE);

            chk_is_saveprint.setVisibility(View.GONE);
            if(Globals.objLPR.getproject_id().equals("cloud")){

                ll_is_online.setVisibility(View.VISIBLE);
                is_online.setChecked(true);
            }
            else{
                ll_is_online.setVisibility(View.GONE);
                is_online.setChecked(false);
            }
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(SetttingsActivity.this);
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


        Globals.ListLimit = edt_list_limit.getText().toString();
        Globals.cart.clear();
        Globals.order_item_tax.clear();
        Globals.TotalItemPrice = 0;
        Globals.TotalQty = 0;
        Globals.setEmpty();
        long e = Settings.delete_settings(SetttingsActivity.this, "Settings", database, "", new String[]{});

        strEmail = edt_email.getText().toString().trim();
        strPass = edt_pass.getText().toString().trim();
        strHost = edt_host.getText().toString().trim();
        strPort = edt_port.getText().toString().trim();

        if (chk_ad.isChecked()) {
            strChangeParm = "AD";
        }

        if (chk_ac.isChecked()) {
            strChangeParm = "AC";
        }

        if (chk_bop.isChecked()) {
            strChangeParm = "BOP";
        }

        if (is_show_brc.isChecked()) {
            chk_is_show_brc = "true";
        } else {
            chk_is_show_brc = "false";
        }

        if (is_show_zdetail_inreprt.isChecked()) {
            chk_is_zdetail_inreprt = "true";
        } else {
            chk_is_zdetail_inreprt = "false";
        }

        if (is_show_device_cus.isChecked()) {
            chk_is_show_device_cus = "true";
        } else {
            chk_is_show_device_cus = "false";
        }

        if (is_show_print_dialog.isChecked()) {
            chk_is_show_print_dialog = "true";
        } else {
            chk_is_show_print_dialog = "false";
        }

        if (is_denomination.isChecked()) {
            chk_is_denomination = "true";
        } else {
            chk_is_denomination = "false";
        }

        if (is_barcode_print.isChecked()) {
            chk_is_barcode = "true";
        } else {
            chk_is_barcode = "false";
        }

        if (is_discount.isChecked()) {
            chk_is_discount = "true";
        } else {
            chk_is_discount = "false";
        }

        if (is_online.isChecked()) {
            chk = "true";
        } else {
            chk = "false";
        }

        if (chk_hsn.isChecked()) {
            chk_hsn_prnt = "true";
        } else {
            chk_hsn_prnt = "false";
        }


        if (is_print_kot.isChecked()) {
            chk_is_print_kot = "true";
        } else {
            chk_is_print_kot = "false";
        }


        if (is_print_invoice.isChecked()) {
            chk_is_print_invoice = "true";
        } else {
            chk_is_print_invoice = "false";
        }

        if (is_file_share.isChecked()) {
            chk_is_file_share = "true";
        } else {
            chk_is_file_share = "false";
        }

        if (is_accounts.isChecked()) {
            chk_is_account = "true";
        } else {
            chk_is_account = "false";
        }

        if (is_stock_manage.isChecked()) {
            chk_is_stock_manage = "true";
        } else {
            chk_is_stock_manage = "false";
        }

        if (is_cash_drawer.isChecked()) {
            chk_is_cash_drawer = "true";
        } else {
            chk_is_cash_drawer = "false";
        }

        if (is_zero_stck.isChecked()) {
            chk_is_zero_stock = "true";
        } else {
            chk_is_zero_stock = "false";
        }

        if (is_change_price.isChecked()) {
            chk_is_change_price = "true";
        } else {
            chk_is_change_price = "false";
        }

        if (is_costp_show.isChecked()) {
            chk_is_cost_show = "true";
        } else {
            chk_is_cost_show = "false";
        }

        if (is_email.isChecked()) {
            chk_email = "true";

            if (!isValidEmail(strEmail)) {
                edt_email.setError(getString(R.string.Invalid_Email));
                edt_email.requestFocus();
                return;
            } else {
                strEmail = edt_email.getText().toString();
            }

            if (strPass.equals("")) {
                edt_pass.setError(getString(R.string.Password_is_required));
                edt_pass.requestFocus();
                return;
            } else {
                strPass = edt_pass.getText().toString();
            }

            if (strHost.equals("")) {
                edt_host.setError(getString(R.string.Password_is_required));
                edt_host.requestFocus();
                return;
            } else {
                strHost = edt_host.getText().toString();
            }

            if (strPort.equals("")) {
                edt_port.setError(getString(R.string.Password_is_required));
                edt_port.requestFocus();
                return;
            } else {
                strPort = edt_port.getText().toString();
            }


            //test_email(strEmail, strPass, edt_mnger_email.getText().toString());

        } else {
            chk_email = "false";
        }

        if (is_sms.isChecked()) {
            chk_sms = "true";

            if (edt_uri.getText().toString().equals("")) {
                edt_uri.setError(getString(R.string.Url_is_required));
                edt_uri.requestFocus();
                return;
            } else {
            }

            if (edt_key.getText().toString().equals("")) {
                edt_key.setError(getString(R.string.Key_is_required));
                edt_key.requestFocus();
                return;
            } else {
            }

            if (edt_sender_id.getText().toString().equals("")) {
                edt_sender_id.setError(getString(R.string.Sender_id_is_required));
                edt_sender_id.requestFocus();
                return;
            } else {
            }

        } else {
            chk_sms = "false";
        }
//
//
        if (is_cmd.isChecked()) {
            chk_cmd = "true";

        } else {
            chk_cmd = "false";
        }
//
        strMgEmail = edt_mnger_email.getText().toString().trim();
        if (strMgEmail.equals("")) {
        } else {
            {
                strMgEmail = edt_mnger_email.getText().toString().trim();
            }
        }

        pri_pos = sp_print.getSelectedItemPosition();
        if (!Globals.objLPR.getIndustry_Type().equals("4")) {
            if (pri_pos == 2) {
                wifi_ip = ip.getText().toString().trim();
                if (wifi_ip.equals("")) {
                    Toast.makeText(SetttingsActivity.this, R.string.plsinsrtIp, Toast.LENGTH_SHORT).show();
                }
            }
        }

        qr_type = sp_qr_type.getSelectedItemPosition();
        if (qr_type == 0) {
            strQRType = "0";
        } else if (qr_type == 1) {
            strQRType = "1";
        }

        scale = sp_scale.getSelectedItemPosition();
        if (scale == 0) {
            strScale = "0";
        } else if (scale == 1) {
            strScale = "1";
        } else if (scale == 2) {
            strScale = "5";
        }else if (scale == 3) {
            strScale = "25";
        }

        qty = sp_qty_dicml.getSelectedItemPosition();
        if (qty == 0) {
            strQtyDecimal = "0";
        } else if (qty == 1) {
            strQtyDecimal = "1";
        } else if (qty == 2) {
            strQtyDecimal = "2";
        } else if (qty == 3) {
            strQtyDecimal = "3";
        }

        int pri_lang = sp_print_lang.getSelectedItemPosition();
        if (pri_lang == 0) {
            strPriLang = "0";
        } else if (pri_lang == 1) {
            strPriLang = "1";
        } else if (pri_lang == 2) {
            strPriLang = "2";
        }

        item_tax = sp_item_tax.getSelectedItemPosition();
        if (item_tax == 0) {
            strItemTax = "0";
        } else if (item_tax == 1) {
            strItemTax = "1";
        } else if (item_tax == 2) {
            strItemTax = "2";
        } else if (item_tax == 3) {
            strItemTax = "3";
        }

        if(Globals.objLPR.getproject_id().equals("cloud")) {
            home_layout = sp_home_layout.getSelectedItemPosition();
            if (home_layout == 0) {
                strHomeLayout = "0";
            } else if (home_layout == 1) {
                strHomeLayout = "1";
            } else if (home_layout == 2) {
                strHomeLayout = "2";
            }

        }
        else if(Globals.objLPR.getproject_id().equals("standalone")){
            home_layout = sp_home_layout.getSelectedItemPosition();
            if (home_layout == 0) {
                strHomeLayout = "0";
            } else if (home_layout == 1) {
                strHomeLayout = "1";
            } else if (home_layout == 2) {
                strHomeLayout = "2";
            }
            else if (home_layout == 3) {
                strHomeLayout = "3";
            }
        }
        cus_dis = sp_cus_dis.getSelectedItemPosition();
        if (cus_dis == 0) {
            strCusDis = "0";
        } else if (cus_dis == 1) {
            strCusDis = "1";
        } else if (cus_dis == 2) {
            strCusDis = "2";
        }

        String logo;
        if (Globals.logo1 == null) {
            logo = settings.get_Logo();
        } else {
            logo = BitmapToString(Globals.logo1);
        }

        default_ordertype = sp_defualt_odrtype.getSelectedItemPosition();

        if (default_ordertype == 0) {
            strDefaultOrderType = "1";

        } else if (default_ordertype == 1) {
            strDefaultOrderType = "2";

        } else if (default_ordertype == 2) {
            strDefaultOrderType = "3";

        } else if (default_ordertype == 3) {
            strDefaultOrderType = "4";

        } else if (default_ordertype == 4) {
            strDefaultOrderType = "5";


        }

        print_memo = sp_print_memo.getSelectedItemPosition();
        if (print_memo == 0) {
            strPrintMemo = "0";
        } else if (print_memo == 1) {
            strPrintMemo = "1";
        }

        if (chk_is_kitchenprint.isChecked()) {
            str_kitchenprint = "true";

        } else {
            str_kitchenprint = "false";
        }

        if (chk_is_saveprint.isChecked()) {
            is_show_saveprint = "true";

        } else {
            is_show_saveprint = "false";
        }


        if (chk_isdeliverydate.isChecked()) {
            is_deliverydate = "true";

        } else {
            is_deliverydate = "false";
        }

        if (chk_is_selfposkot.isChecked()) {
            is_selfposkot = "true";

        } else {
            is_selfposkot = "false";
        }

        if (chk_ispaymentmethod.isChecked()) {
            is_paymentmethod = "true";

        } else {
            is_paymentmethod = "false";
        }

        if(chk_apisync.isChecked()){
            isApiSync="true";
        }
        else{
            isApiSync="false";
        }

/************************************************/
        // Parking Industry Parameters

        if(Globals.objLPR.getIndustry_Type().equals("4")) {
            if (is_showsinglewindow.isChecked()) {
                chk_is_singlewindow = "true";
            } else {
                chk_is_singlewindow = "false";
            }
            if (is_showfilladvamnt.isChecked()) {
                chk_is_fillAdvAmnt = "true";
            } else {
                chk_is_fillAdvAmnt = "false";
            }

            if (is_validatevehno.isChecked()) {
                chk_is_valVehNo = "true";
            } else {
                chk_is_valVehNo = "false";
            }
            if (is_validatemobileno.isChecked()) {
                chk_is_valmobNo = "true";
            } else {
                chk_is_valmobNo = "false";
            }

            if (chk_iscloudprint.isChecked()) {
                is_cloudprint = "true";
            } else {
                is_cloudprint = "false";
            }
            if(is_nfc.isChecked()){
                chk_isnfc="true";
                chk_is_valVehNo="true";
            }
            else{
                is_nfc.setChecked(false);
                chk_isnfc="false";

            }

            if (is_file_share.isChecked()) {
                chk_is_file_share = "true";
                chk_is_show_print_dialog = "true";

            } else {
                chk_is_file_share = "false";
            }
        }
        else{

            chk_is_singlewindow = "false";
            chk_is_fillAdvAmnt = "false";
            chk_is_valVehNo = "false";
            chk_is_valmobNo = "false";
            chk_isnfc="false";
            is_cloudprint= "false";

        }
        /*************************/
        pDialog = new ProgressDialog(SetttingsActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        final String finalLogo = logo;
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    //sleep(2000);
                    database.beginTransaction();
                    settings = new Settings(SetttingsActivity.this, null, chk, String.valueOf(pri_pos), wifi_ip, strScale, strEmail, strPass, finalLogo, strMgEmail, chk_sms, chk_email, edt_uri.getText().toString(), edt_key.getText().toString(), edt_sender_id.getText().toString(), strPriLang, chk_cmd, chk_hsn_prnt, strItemTax, settings.get_Copy_Right(), strQtyDecimal, edt_footer.getText().toString().trim(), strCusDis, chk_is_denomination, chk_is_barcode, chk_is_discount, edt_gst_no.getText().toString().trim(), edt_print_order.getText().toString().trim(), edt_print_cashier.getText().toString().trim(), edt_print_inv_no.getText().toString().trim(), edt_print_inv_date.getText().toString().trim(), edt_print_device_id.getText().toString().trim(), chk_is_print_kot, chk_is_print_invoice, chk_is_file_share, strHost, strPort, strChangeParm, chk_is_account, chk_is_stock_manage, strHomeLayout, chk_is_cash_drawer, chk_is_zero_stock, chk_is_change_price, edt_no_of_prnt.getText().toString().trim(), edt_gst_lbl.getText().toString().trim(), chk_is_zdetail_inreprt, chk_is_show_device_cus, chk_is_show_print_dialog, chk_is_show_brc, strDefaultOrderType, strPrintMemo,chk_is_cost_show,strQRType,chk_is_singlewindow,chk_is_fillAdvAmnt,chk_is_valVehNo,chk_is_valmobNo,chk_isnfc,str_kitchenprint,is_show_saveprint,is_cloudprint,is_deliverydate,is_selfposkot,is_paymentmethod,settings.getApi_Ip(),isApiSync,"","");

                    long l = settings.insertSettings(database);
                    if (l > 0) {
                        try {
                            last_code.setlast_order_code(edt_sequence_no.getText().toString().trim());
                            last_code.updateLast_Code("id=?", new String[]{last_code.getid()}, database);
                            database.setTransactionSuccessful();
                            database.endTransaction();
                            pDialog.dismiss();
                        } catch (Exception e) {
                            database.setTransactionSuccessful();
                            database.endTransaction();
                            pDialog.dismiss();
                        }
                        Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                        Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                        Globals.strIsDiscountPrint = settings.get_Is_Discount();
                        Globals.GSTNo = settings.get_Gst_No();
                        Globals.GSTLbl = settings.get_GST_Label();
                        Globals.PrintOrder = settings.get_Print_Order();
                        Globals.PrintCashier = settings.get_Print_Cashier();
                        Globals.PrintInvNo = settings.get_Print_InvNo();
                        Globals.PrintInvDate = settings.get_Print_InvDate();
                        Globals.PrintDeviceID = settings.get_Print_DeviceID();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetttingsActivity.this, R.string.savesucc, Toast.LENGTH_SHORT).show();

                            }
                        });
                        runOnUiThread(new Runnable() {
                            public void run() {
                              if(Globals.objLPR.getproject_id().equals("cloud")) {
                                  onBackPressed();
                              }
                              else if(Globals.objLPR.getproject_id().equals("standalone")){
                                  if(settings.get_Home_Layout().equals("3")){
                                      if(!Globals.objLPR.getIndustry_Type().equals("3")) {
                                          Globals.objLPR.setIndustry_Type("3");


                                          long ct = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                          if (ct > 0) {
                                              // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                              Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                              lite_pos_device.setStatus("Out");
                                              long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                              if (ct1 > 0) {
                                                  // database.endTransaction();
                                                  Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                  startActivity(intent_category);
                                                  finish();
                                              }
                                             // succ_manu = "1";
                                              //}
                                          }
                                      }

                         /*             else if(Globals.objLPR.getIndustry_Type().equals("3")){
                                          if(settings.get_Home_Layout().equals("0"))
                                          {

                                              if(!Globals.objLPR.getIndustry_Type().equals("1"))
                                              {
                                                  Globals.objLPR.setIndustry_Type("1");


                                                  long ct11 = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                                  if (ct11 > 0) {
                                                      // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                                      Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                      lite_pos_device.setStatus("Out");
                                                      long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                      if (ct1 > 0) {
                                                          // database.endTransaction();
                                                          finish();
                                                          Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                          startActivity(intent_category);

                                                      }
                                                      // succ_manu = "1";
                                                      //}
                                                  }
                                              }
                                          }
                                          else

                                          {

                                              Globals.objLPR.setIndustry_Type("1");


                                              long ct11 = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                              if (ct11 > 0) {
                                                  // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                                  Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                  lite_pos_device.setStatus("Out");
                                                  long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                  if (ct1 > 0) {
                                                      // database.endTransaction();
                                                      finish();
                                                      Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                      startActivity(intent_category);

                                                  }
                                                  // succ_manu = "1";
                                                  //}
                                              }

                                          }
                                      }*/

                                  }
                                  else if(settings.get_Home_Layout().equals("0"))
                                  {

                                      if(!Globals.objLPR.getIndustry_Type().equals("1"))
                                      {
                                          Globals.objLPR.setIndustry_Type("1");


                                          long ct11 = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                          if (ct11 > 0) {
                                              // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                              Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                              lite_pos_device.setStatus("Out");
                                              long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                              if (ct1 > 0) {
                                                  // database.endTransaction();

                                                  Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                  startActivity(intent_category);
                                                  finish();
                                              }
                                              // succ_manu = "1";
                                              //}
                                          }
                                      }
                                      else{
                                          onBackPressed();
                                      }
                                  }

                                  else if(settings.get_Home_Layout().equals("1"))
                                  {

                                      if(!Globals.objLPR.getIndustry_Type().equals("1"))
                                      {
                                          Globals.objLPR.setIndustry_Type("1");


                                          long ct11 = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                          if (ct11 > 0) {
                                              // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                              Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                              lite_pos_device.setStatus("Out");
                                              long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                              if (ct1 > 0) {
                                                  // database.endTransaction();

                                                  Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                  startActivity(intent_category);
                                                  finish();
                                              }
                                              // succ_manu = "1";
                                              //}
                                          }
                                      }
                                      else{
                                          onBackPressed();
                                      }
                                  }
                                  else if(settings.get_Home_Layout().equals("2"))
                                  {

                                      if(!Globals.objLPR.getIndustry_Type().equals("1"))
                                      {
                                          Globals.objLPR.setIndustry_Type("1");


                                          long ct11 = Globals.objLPR.updateRegistration("Id=?", new String[]{"1"}, database);
                                          if (ct11 > 0) {
                                              // if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                                              Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                              lite_pos_device.setStatus("Out");
                                              long ct1 = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                              if (ct1 > 0) {
                                                  // database.endTransaction();

                                                  Intent intent_category = new Intent(getApplicationContext(), LoginActivity.class);
                                                  startActivity(intent_category);
                                                  finish();
                                              }
                                              // succ_manu = "1";
                                              //}
                                          }
                                      }
                                      else{
                                          onBackPressed();
                                      }
                                  }

                              }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        timerThread.start();

        if(Globals.objLPR.getIndustry_Type().equals("3")){
            Intent intent = new Intent(SetttingsActivity.this, PaymentCollection_MainScreen.class);
            intent.putExtra("whatsappFlag","false");
            intent.putExtra("contact_code", "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    public Boolean CheckprinterConnection() {
        //if (settings != null) {

            if (pri_pos==2) {
                String tmpStr = ip.getText().toString().trim();
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
            dialog = new ProgressDialog(SetttingsActivity.this);
            dialog.setCancelable(false);
            dialog.show();
        }

    }
        private void performOperationEn() {
            noofPrint = Integer.parseInt(settings.get_No_Of_Print());

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

                    }
                } catch (Exception e) {
                }
            }
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
                Toast.makeText(SetttingsActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(SetttingsActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(SetttingsActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
