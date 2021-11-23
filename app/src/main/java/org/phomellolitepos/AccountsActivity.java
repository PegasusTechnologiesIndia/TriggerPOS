package org.phomellolitepos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintManager;
import android.provider.ContactsContract;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.hoin.btsdk.BluetoothService;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Acc_Customer_Credit;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.User;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class AccountsActivity extends AppCompatActivity {
    TextView txt_cus_code, txt_cus_name, txt_cr_amt, txt_cr_lc_amt, txt_total_amt, txt_type, txt_type_total;
    EditText edt_pd_amt;
    Button btn_lastprint;
    Button btn_paid;
    ProgressDialog pDialog;
    String operation, code;
    Database db;
    String paidamount="0.00";
    SQLiteDatabase database;
    String date;
    private ProgressDialog dialog;
    private ArrayList<String> mylist = new ArrayList<String>();
    private MyAdapter adp;
    private ICallback callback = null;
    private IWoyouService woyouService;
    private boolean iswifi = false;
    private int order, noofPrint = 0, lang = 0, pos = 0;
    // Settings settings;
    String decimal_check;
    String data;
    String currentTime;
    String strPayMethod, PayId="1";
    String modified_by;
    Contact contact;
    String ck_project_type;
    String strCRAmount;
    String bWhatsappFlag="";
    private static final String TAG = "PrinterTestDemo";
    //private String PrinterType = "";
    private String is_directPrint = "";
    private TimerCountTools timeTools;
    Spinner spn_paymentmode;
    String spnpaymentmode;
    EditText edt_narration;
    JSONObject printJson = new JSONObject();
    private PrinterListener printer_callback = new PrinterListener();
    public static PrinterBinder printer;
    BluetoothService mService = null;
    ArrayList<Payment> paymentArrayList;
    String strnarration;
    String serial_no, android_id, myKey, device_id, imei_no;
    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
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


    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;


    private IPosPrinterService mIPosPrinterService;
    private IPosPrinterCallback callbackPPT8555 = null;

    private Random random = new Random();
    private HandlerUtils.MyHandler handlerPPT8555;

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
        setContentView(R.layout.activity_accounts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

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
     /*   device_id = telephonyManager.getDeviceId();
        imei_no = telephonyManager.getImei();*/
        modified_by = Globals.user;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(AccountsActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
try {
    Thread timerThread = new Thread() {
        public void run() {
            try {
                // sleep(100);
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    Intent intent = new Intent(AccountsActivity.this, AccountsListActivity.class);
                    intent.putExtra("whatsappFlag",bWhatsappFlag);
                    intent.putExtra("contact_code", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")) {
                    Intent intent = new Intent(AccountsActivity.this, AccountsListActivity.class);
                    intent.putExtra("whatsappFlag",bWhatsappFlag);
                    intent.putExtra("contact_code", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
                else if(Globals.objLPR.getIndustry_Type().equals("3")){
                    Intent intent = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                    intent.putExtra("whatsappFlag",bWhatsappFlag);
                    intent.putExtra("contact_code", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    };
    timerThread.start();
}
catch(Exception e){

}
            }
        });


        try {
            //  Globals.PrinterType = Globals.objsettings.getPrinterId();
            is_directPrint = Globals.objsettings.get_Is_Print_Dialog_Show();
        } catch (Exception ex) {
            // PrinterType = "";
            is_directPrint = "";
        }


        if (Globals.PrinterType.equals("7")) {
            try {
                ServiceManager.getInstence().init(AccountsActivity.this);
            } catch (Exception ex) {
            }
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

        Intent intent_1 = new Intent();
        intent_1.setPackage("woyou.aidlservice.jiuiv5");
        intent_1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_1);
        bindService(intent_1, connService, Context.BIND_AUTO_CREATE);
        intent = getIntent();
        getSupportActionBar().setTitle(R.string.accounts);
        code = intent.getStringExtra("contact_code");
        contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + code + "'");
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        //date = format.format(d);
        date = format.format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        if(Globals.objLPR.getIndustry_Type().equals("3")){
            mService = PaymentCollection_MainScreen.mService;
        }
        else {
            mService = MainActivity.mService;
        }
        txt_cus_code = (TextView) findViewById(R.id.txt_cus_code);
        txt_cus_name = (TextView) findViewById(R.id.txt_cus_name);
        txt_cr_amt = (TextView) findViewById(R.id.txt_cr_amt);
        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_cr_lc_amt = (TextView) findViewById(R.id.txt_cr_lc_amt);
        txt_type_total = (TextView) findViewById(R.id.txt_type_total);
        txt_total_amt = (TextView) findViewById(R.id.txt_total_amt);
        edt_pd_amt = (EditText) findViewById(R.id.edt_pd_amt);
        btn_paid = (Button) findViewById(R.id.btn_paid);
        edt_narration=(EditText)findViewById(R.id.edt_pd_narration);
        spn_paymentmode=(Spinner)findViewById(R.id.spn_pd_mode);
        btn_lastprint=(Button)findViewById(R.id.btn_lastprint);
        edt_pd_amt.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        try {
            txt_cus_code.setText(code);
        } catch (Exception ex) {
            txt_cus_code.setText("");
        }

        try {
            txt_cus_name.setText(contact.get_name());
        } catch (Exception ex) {
            txt_cus_name.setText("");
        }

        try{
            fill_spinner_pay_method("");

        }
        catch(Exception e){

        }
        //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        ck_project_type = Globals.objLPR.getproject_id();

        if (ck_project_type.equals("standalone")) {
            try {
                String strCreditAmt = "", strDeditAmount = "";
                Double creditAmount = 0d, debitAmount = 0d;
                Cursor cursor = null;

                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + code + "'";
                cursor = database.rawQuery(strQry1, null);
                while (cursor.moveToNext()) {
                    strCreditAmt = cursor.getString(0);
                }
                try {
                    creditAmount = Double.parseDouble(strCreditAmt);
                } catch (Exception ex) {
                }
                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + code + "')";
                cursor = database.rawQuery(strQry2, null);
                while (cursor.moveToNext()) {
                    strDeditAmount = cursor.getString(0);
                }
                try {
                    debitAmount = Double.parseDouble(strDeditAmount);
                } catch (Exception ex) {
                }
                Double showAmount = debitAmount + creditAmount;

                double abs1 = 0d;
                Double strCheckAmmt;
                try {
                    strCheckAmmt = showAmount;
                } catch (Exception ex) {
                    strCheckAmmt = 0d;
                }
                abs1 = Math.abs(strCheckAmmt);
                if (strCheckAmmt > 0) {
                    txt_cr_amt.setText(Globals.myNumberFormat2Price(0, decimal_check));
                    edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                    txt_type_total.setText("DR");
                    txt_type_total.setTextColor(Color.parseColor("#228B22"));
                    txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                } else {
                    txt_cr_amt.setText(Globals.myNumberFormat2Price(0, decimal_check));
                    edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                    txt_type_total.setText("CR");
                    txt_type_total.setTextColor(Color.parseColor("#FF0000"));
                    txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                }
                txt_cr_lc_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                edt_pd_amt.requestFocus();
                edt_pd_amt.selectAll();
            } catch (Exception e) {
            }
        } else {
            pDialog = new ProgressDialog(AccountsActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.waiting));
            pDialog.show();
            new Thread() {
                @Override
                public void run() {
                    try {

                        // Get Customer account from api
                        GetCusCrDetailFromServer(serial_no,android_id,myKey,pDialog);

                    } catch (Exception e) {

                    }

//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            edt_pd_amt.setText("21.000");
//                            txt_cr_amt.setText("21.000");
//                        }
//                    });
                }
            }.start();
        }

        edt_pd_amt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_pd_amt.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_pd_amt.requestFocus();
                    edt_pd_amt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_pd_amt, InputMethodManager.SHOW_IMPLICIT);
                    edt_pd_amt.selectAll();
                    return true;
                }
            }
        });


        btn_lastprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                System.out.print(printer.getPrinterInfo());

                String sqlQuery="select ac.*, pay.payment_name from Acc_Customer_Credit ac left join payments pay on pay.payment_id = ac.payment_id where ac.contact_code='"+contact.get_contact_code()+"' order by ac.modified_date desc limit 1";
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery(sqlQuery, null);

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        String contactcode = cursor.getString(cursor.getColumnIndex("contact_code"));
                        String cramnt = cursor.getString(cursor.getColumnIndex("cr_amount"));
                        String paidamount = cursor.getString(cursor.getColumnIndex("paid_amount"));
                        String balanceamnt = cursor.getString(cursor.getColumnIndex("balance_amount"));
                        String modifieddate = cursor.getString(cursor.getColumnIndex("modified_date"));
                        String paymentname = cursor.getString(cursor.getColumnIndex("payment_name"));

                        String customername=contact.get_name();
                        String contactno=contact.get_contact_1();
                        String modifie_date=modifieddate.substring(0,10);
                        String modified_time=modifieddate.substring(11,19);
                        String paymentmethod= paymentname;
                        String oldbal=cramnt;
                        String receivedbl=paidamount;
                        String currentbal=paidamount;
                        print(contactcode,customername,contactno,modifie_date,modified_time,paymentmethod,oldbal,receivedbl,currentbal);


                        // arrayListOrder.add(new Orders(getApplicationContext(),orderid));
                        //zonelabels.add(zoneId);
                        //arrayListTable.add(new Table_Order( tablecode, tablename, zone_id, zone_name, NoOfOrder));

                        ///arrayListTable.add(new Table(this, null, tablecode, tablename, zone_id, zone_name, "", "", "",""));
                    } while (cursor.moveToNext());

                }
                else {
                    Toast.makeText(getApplicationContext(),"No Last Transaction found",Toast.LENGTH_LONG).show();
                }

                // closing connection
                cursor.close();
            }
        });
      spn_paymentmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                try {


                   // Object item = adapterView.getItemAtPosition(pos);
                    spnpaymentmode= (String) spn_paymentmode.getSelectedItem();
                    Payment resultp = paymentArrayList.get(pos);
                    strPayMethod = resultp.get_payment_name();
                    PayId = resultp.get_payment_id();

                    try {

                        fill_spinner_pay_method(strPayMethod);

                    } catch (Exception ex) {
                        PayId = "1";
                    }
                  //  fill_spinner_pay_method("");
                  /*  if (PayId.equals("5")) {
                        if (Globals.strContact_Code.equals("")) {

                        }
                    }*/
                } catch (Exception ecx) {
                    System.out.println(ecx.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        btn_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        AccountsActivity.this);
                alertDialog.setTitle(R.string.confirmationalert);
                alertDialog
                        .setMessage(R.string.areyousure);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                alertDialog.setPositiveButton(R.string.alert_posbtn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {

                                    pay_amount();


                                } catch (Exception e) {

                                }
                            }
                        });

                alertDialog.setNegativeButton(R.string.alert_nobtn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                AlertDialog alert = alertDialog.create();

                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");


                     /* else if (lite_pos_registration.getIndustry_Type().equals("3") || lite_pos_registration.getIndustry_Type().equals("6")) {
                            ((AlertDialog) dialog).getButton(
                                    AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                        }*/
                    }
                });
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
    }
    public void fill_spinner_pay_method(String s) {
        if (Globals.strContact_Code.equals("")) {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'  and payment_id NOT IN (3,5)");
        } else {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'");
        }

        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList);
        spn_paymentmode.setAdapter(paymentListAdapter);

        //  spn_paymentmode.setSelection(i);

        if (!s.equals("")) {
            for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                String iname = paymentArrayList.get(i).get_payment_name();
               if (s.equals(iname)) {
                    spn_paymentmode.setSelection(i);

                    break;
               }
            }
        }

    }
    private void pay_amount() {
        if (edt_pd_amt.getText().toString().trim().equals("")) {
            edt_pd_amt.setError("Enter paid amount");
            edt_pd_amt.requestFocus();
            return;
        }

        if (edt_pd_amt.getText().toString().trim().equals(".")) {
            edt_pd_amt.setError("Can't enter decimal only");
            edt_pd_amt.requestFocus();
            edt_pd_amt.selectAll();
            return;
        }

        try {
            strnarration = edt_narration.getText().toString();
        }
        catch(Exception e){

        }

        if (Double.parseDouble(edt_pd_amt.getText().toString()) > 0) {

            if (isNetworkStatusAvialable(getApplicationContext())) {
                if (txt_cr_amt.getText().toString().trim().equals("")) {
                    txt_cr_amt.setError("No Credit Found");
                    txt_cr_amt.requestFocus();
                    return;
                }
                if (edt_pd_amt.getText().toString().trim().equals("")) {
                    edt_pd_amt.setError("Paid Amount Required");
                    edt_pd_amt.requestFocus();
                    return;
                }

                if (ck_project_type.equals("standalone")) {

                    pDialog = new ProgressDialog(AccountsActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.waiting));
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            String result = "";
                            try {
                                result = SaveTransaction();
                            } catch (Exception e) {

                            }
                            if (result.equals("1")) {
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + code + "'", database);
                                        if (acc_customer == null) {
                                        } else {
                                            Double strOldBalance = 0d;
                                            Double strAmount = 0d;
                                            strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                            strAmount = strOldBalance + Double.parseDouble(edt_pd_amt.getText().toString());
                                            acc_customer.set_amount(strAmount + "");
                                            long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{Globals.strContact_Code}, database);
                                        }
//                                                if (PrinterType.equals("1")) {
                                        try {


                                          //  print();


                                        } catch (Exception ex) {
                                        }
//                                                }
                                        Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).show();
                                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                            Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                            startActivity(intent1);
                                            finish();
                                        }
                                        else if(Globals.objLPR.getIndustry_Type().equals("3")){
                                            Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                                            startActivity(intent1);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        Toast.makeText(AccountsActivity.this, getString(R.string.transactionerr), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }.start();
                } else {
                    pDialog = new ProgressDialog(AccountsActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.waiting));
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {


                            Double ab;
                            String oldbal= Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check);
                            String recbal= Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check);
                            ab = Double.parseDouble(txt_total_amt.getText().toString()) + Double.parseDouble(edt_pd_amt.getText().toString());
                            double abs1 = Math.abs(ab);
                            String currentbal= Globals.myNumberFormat2Price(abs1, decimal_check);

                            if(Globals.PrinterType.equals("11")){
                                pdfPerform_80mm();
                            }
                            else {
                                performPDFExport(oldbal, recbal, currentbal, txt_type_total.getText().toString());
                            }

                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(Globals.objsettings.get_Is_File_Share().equals("true")){
                                        share_dialog();
                                    }
                                }
                            });*/


                          //  if(Globals.objsettings.get_Is_File_Share().equals(false)) {
                                SendCusCrDetailFromServer(serial_no, android_id, myKey, strnarration);
                                String contactCode = contact.get_contact_code();
                                // Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + contact.get_contact_code() + "'");
                                if (contact == null) {
                                } else {
                                    if (contact.get_email_1().equals("")) {
                                    } else {
                                        if (Globals.objsettings.get_Is_email().equals("true")) {
                                            String strEmail = contact.get_email_1();
                                            send_email(strEmail);
                                            Globals.AppLogWrite("sender email" + strEmail);
                                        }
                                    }
                                }
                            //}



                          /*   else{
                                 SendCusCrDetailFromServer(serial_no, android_id, myKey, strnarration);

                             }*/

                        }
                    }.start();
                }



            } else {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Can't paid", Toast.LENGTH_SHORT).show();
        }
    }

    private String SaveTransaction() {

        String succ = "0";
        database.beginTransaction();

        try {

            Acc_Customer_Credit acc_customer_credit = new Acc_Customer_Credit(getApplicationContext(), null, date, txt_cus_code.getText().toString(), "0", edt_pd_amt.getText().toString().trim(), "0", "0", "1", modified_by, date, "",PayId);
            long l = acc_customer_credit.insertAcc_Customer_Credit(database);
            if (l > 0) {
                succ = "1";
            }

            if (succ.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (Exception e) {
            database.setTransactionSuccessful();
            database.endTransaction();
        }
        return succ;
    }

    private void print(String customercode,String customername,String contactno, String modifieddate,String modifiedtime,String paymentmethod,String oldbal,String recbal,String currBal) {
        if (Globals.PrinterType.equals("2")) {
            mylist = getlist_wifiprint(oldbal,recbal);
            //  Toast.makeText(getApplicationContext(),"My list"+ mylist,Toast.LENGTH_LONG).show();
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

        if (Globals.PrinterType.equals("7")) {
            try {
                String strString = "";
                int strLength = 15;

                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                    JSONArray printTest = new JSONArray();
                    timeTools = new TimerCountTools();
                    timeTools.start();
                    ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                    String Print_type = "0";
                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                        printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));

                    }
                    else{
                        printTest.put(getPrintObject(Globals.objLPR.getShort_companyname(), "3", "center"));

                    }
                    printTest.put(getPrintObject(Globals.objLPR.getAddress(), "2", "center"));
                    printTest.put(getPrintObject(Globals.objLPR.getMobile_No(), "2", "center"));
                    try {
                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                        } else {
                            printTest.put(getPrintObject(Globals.objLPR.getService_code_tariff(), "2", "center"));
                        }
                    } catch (Exception ex) {
                    }

                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                    } else {

                        printTest.put(getPrintObject(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "2", "center"));
                    }
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));

                    printTest.put(getPrintObject("Payment Receipt", "3", "center"));
                    printTest.put(getPrintObject("--------------------------------", "2", "left"));

                    strString = Globals.myRequiredString("Customer Code", strLength);
                    printTest.put(getPrintObject(strString + ":" + customercode, "2", "left"));
                    strString = Globals.myRequiredString("Customer Name", strLength);
                    printTest.put(getPrintObject(strString + ":" + customername, "2", "left"));
                    strString = Globals.myRequiredString("Contact No", strLength);
                    printTest.put(getPrintObject(strString + ":" + contactno, "2", "left"));
                    strString = Globals.myRequiredString("Date", strLength);
                    printTest.put(getPrintObject(strString + ":" +modifieddate, "2", "left"));
                    strString = Globals.myRequiredString("Time", strLength);
                    printTest.put(getPrintObject(strString + ":" + modifiedtime, "2", "left"));
                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                    printTest.put(getPrintObject(strString + ":" + Globals.objLPD.getDevice_Name(), "2", "left"));

                    strString = Globals.myRequiredString("Old Balance", strLength);
                    printTest.put(getPrintObject(strString +":"+Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check), "2" , "left"));
                    // printTest.put(getPrintObject(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "(" + Globals.objLPD.getCurreny_Symbol() + ")", "2", "right"));
                    strString = Globals.myRequiredString("Received", strLength);
                    printTest.put(getPrintObject(strString +":"+ Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check), "2" , "left"));

                    Double ab;
                    if (txt_type_total.getText().toString().equals("DR")) {
                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);

                        double abs1 = Math.abs(ab);

                        if (ab > 0) {
                            strString = Globals.myRequiredString("Current Balance", strLength);

                            printTest.put(getPrintObject(strString +":"+ Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "2", "left"));

                        }
                    } else {
                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                        double abs1 = Math.abs(ab);

                        if (ab > 0) {
                            strString = Globals.myRequiredString("Current Balance", strLength);
                            printTest.put(getPrintObject(strString + ":"+Globals.myNumberFormat2Price(abs1, decimal_check) + " CR", "2", "left"));

                        } else {
                            strString = Globals.myRequiredString("Current Balance", strLength);
                            printTest.put(getPrintObject(strString + ":"+ Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "2", "left"));
                        }
                    }


                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    printJson.put("spos", printTest);
                    // 设置底部空3行
                    printJson.put("spos", printTest);

                    strString = Globals.myRequiredString("Signature", strLength);

                    printTest.put(getPrintObject(strString + ":" + user.get_name(), "2", "left"));
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

        } else if (Globals.PrinterType.equals("3")) {


            if (Globals.objsettings.get_Print_Lang().equals("0")) {
                byte[] ab1;
                ab1 = BytesUtil.setAlignCenter(1);
                String strString = "";
                int strLength = 15;
                mService.write(ab1);
                if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                    mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                } else {
                    mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");
                }
                if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                } else {
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                }
                if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                } else {
                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                }

                try {
                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().length() == 0 || Globals.objLPR.getService_code_tariff().isEmpty()) {

                    } else {
                        mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                    }
                } catch (Exception ex) {
                }


                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().length() == 0 || Globals.objLPR.getLicense_No().isEmpty()) {
                } else {
                    mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");

                }
                ab1 = BytesUtil.setAlignCenter(1);
                mService.write(ab1);
                mService.sendMessage("--------------------------------", "GBK");
                mService.sendMessage("Payment Receipt", "GBK");
                mService.sendMessage("--------------------------------", "GBK");
                ab1 = BytesUtil.setAlignCenter(0);
                mService.write(ab1);
                /*strString = Globals.myRequiredString("Company Name", strLength);
                mService.sendMessage(strString + " : " + Globals.objLPR.getCompany_Name(), "GBK");*/
                strString = Globals.myRequiredString("Customer Code", strLength);
                mService.sendMessage(strString + " : " + customercode, "GBK");
                strString = Globals.myRequiredString("Customer Name", strLength);
                mService.sendMessage(strString + " : " + customername, "GBK");
                strString = Globals.myRequiredString("Contact No", strLength);
                mService.sendMessage(strString + " : " + contactno, "GBK");
                strString = Globals.myRequiredString("Date", strLength);
                mService.sendMessage(strString + " : " + modifieddate, "GBK");
                strString = Globals.myRequiredString("Time", strLength);
                mService.sendMessage(strString + " : " + modifiedtime, "GBK");
                strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                mService.sendMessage(strString + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                strString = Globals.myRequiredString("Payment Mode" , strLength);
                mService.sendMessage(strString+ " : " + paymentmethod,"GBK");
                mService.sendMessage("--------------------------------", "GBK");

                strString = Globals.myRequiredString("Old Balance", strLength);
                mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check), "GBK");

                // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check), "GBK");
                strString = Globals.myRequiredString("Received", strLength);
                mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check), "GBK");

                // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check), "GBK");

                Double ab;
                strString = Globals.myRequiredString("Current Balance", strLength);
                if (txt_type_total.getText().toString().equals("DR")) {
                    ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                    double abs1 = Math.abs(ab);
                    if (ab > 0) {

                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "GBK");

                        //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR" , "GBK");

                    }
                } else {
                    ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                    double abs1 = Math.abs(ab);
                    if (ab > 0) {
                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR", "GBK");

                        // mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "CR", "GBK");

                    } else {
                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "GBK");

                        //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "DR", "GBK");
                    }
                }

                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                mService.sendMessage("--------------------------------", "GBK");
                strString = Globals.myRequiredString("Signature", strLength);
                mService.sendMessage(strString + " : " + user.get_name(), "GBK");
                mService.sendMessage("--------------------------------", "GBK");
                ab1 = BytesUtil.setAlignCenter(1);
                mService.write(ab1);
                mService.sendMessage(Globals.objsettings.get_Copy_Right() + "", "GBK");

                mService.sendMessage("\n", "GBK");
                mService.sendMessage("\n", "GBK");


            }

        } else if (Globals.PrinterType.equals("4")) {


            if (Globals.objsettings.get_Print_Lang().equals("0")) {
                byte[] ab1;
                ab1 = BytesUtil.setAlignCenter(1);
                String strString = "";
                int strLength = 16;
                mService.write(ab1);
                if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                    mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                } else {
                    mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");
                }
                if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                } else {
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                }
                if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                } else {
                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                }

                try {
                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().length() == 0 || Globals.objLPR.getService_code_tariff().isEmpty()) {

                    } else {
                        mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                    }
                } catch (Exception ex) {
                }


                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().length() == 0 || Globals.objLPR.getLicense_No().isEmpty()) {
                } else {
                    mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");

                }

                mService.sendMessage("...............................................", "GBK");
                mService.sendMessage("Payment Receipt", "GBK");
                mService.sendMessage("...............................................", "GBK");
                ab1 = BytesUtil.setAlignCenter(0);
                mService.write(ab1);
                /*strString = Globals.myRequiredString("Company Name", strLength);
                mService.sendMessage(strString + " : " + Globals.objLPR.getCompany_Name(), "GBK");*/
                strString = Globals.myRequiredString("Customer Code", strLength);
                mService.sendMessage(strString + " : " + customercode, "GBK");
                strString = Globals.myRequiredString("Customer Name", strLength);
                mService.sendMessage(strString + " : " + customername, "GBK");
                strString = Globals.myRequiredString("Contact No", strLength);
                mService.sendMessage(strString + " : " + contactno, "GBK");
                strString = Globals.myRequiredString("Date", strLength);
                mService.sendMessage(strString + " : " + modifieddate, "GBK");
                strString = Globals.myRequiredString("Time", strLength);
                mService.sendMessage(strString + " : " + modifiedtime, "GBK");
                strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                mService.sendMessage(strString + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                strString = Globals.myRequiredString("Payment Mode" , strLength);
                mService.sendMessage(strString+ " : " + paymentmethod,"GBK");
                mService.sendMessage("...............................................", "GBK");
                strString = Globals.myRequiredString("Old Balance", strLength);
                mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check), "GBK");

                // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check), "GBK");
                strString = Globals.myRequiredString("Received", strLength);
                mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check), "GBK");

                // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check), "GBK");

                Double ab;
                strString = Globals.myRequiredString("Current Balance", strLength);
                if (txt_type_total.getText().toString().equals("DR")) {
                    ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                    double abs1 = Math.abs(ab);
                    if (ab > 0) {

                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "GBK");

                        //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR" , "GBK");

                    }
                } else {
                    ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                    double abs1 = Math.abs(ab);
                    if (ab > 0) {
                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR", "GBK");

                        // mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "CR", "GBK");

                    } else {
                        mService.sendMessage(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", "GBK");

                        //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "DR", "GBK");
                    }
                }

                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                   /* strString = Globals.myRequiredString("(Amounts in" , strLength);
                    mService.sendMessage(strString + Globals.objLPD.getCurreny_Symbol() + ")", "GBK");*/
                mService.sendMessage("...............................................", "GBK");
                strString = Globals.myRequiredString("Signature", strLength);
                mService.sendMessage(strString + " : " + user.get_name(), "GBK");

                mService.sendMessage("...............................................", "GBK");
                ab1 = BytesUtil.setAlignCenter(1);
                mService.write(ab1);
                mService.sendMessage("" + Globals.objsettings.get_Copy_Right(), "GBK");
                mService.sendMessage("\n", "GBK");
                mService.sendMessage("\n", "GBK");


            }
        } else if (Globals.PrinterType.equals("8")) {
            try {


                if (Globals.objsettings.get_Print_Lang().equals("0")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String strString = "";
                                    int strLength = 15;


                                    String Print_type = "0";

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    else{
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getShort_companyname() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().equals("0")) {

                                        } else {
                                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().equals("0")) {
                                    } else {
                                        //  mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getLicense_No() + "\n", "", 24, 1, callbackPPT8555);

                                        //  mIPosPrinterService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 14}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                                    }


                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("Payment Receipt\n", "", 24, 1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                  /*  if (contact.get_company_name().length() > 0) {
                                        strString = Globals.myRequiredString("Company Name", strLength);
                                        mIPosPrinterService.printSpecifiedTypeText(strString + " : " + contact.get_company_name() + "\n", "ST", 24, callbackPPT8555);

                                    }*/
                                    strString = Globals.myRequiredString("Customer Code", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + customercode + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Customer Name", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + customername + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Contact No", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + contactno + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Date", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + modifieddate + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Time", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + modifiedtime + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Payment Mode" , strLength);
                                    //strPayMethod
                                    //mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + spn_paymentmode.getSelectedItem().toString()+ "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + paymentmethod.toString()+ "\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    strString = Globals.myRequiredString("Old Balance", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Received", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);

                                    strString = Globals.myRequiredString("Current Balance", strLength);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {

                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "ST", 24, callbackPPT8555);


                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    }

                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                                    // mIPosPrinterService.printSpecifiedTypeText("(Amounts In " + Globals.objLPD.getCurreny_Symbol() + ")" + "\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("Signature  \n", "", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(user.get_name(), "", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.PrintSpecFormatText(Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);


                                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (Globals.objsettings.get_Print_Lang().equals("1")) {

                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 15;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    else{
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getShort_companyname() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                        } else {
                                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                    } else {
                                        //  mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getLicense_No() + "\n", "", 24, 1, callbackPPT8555);

                                        //  mIPosPrinterService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 14}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                                    }

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(2, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("إيصال الدفع\n", "", 24, 1, callbackPPT8555);

                                    //  mIPosPrinterService.printSpecifiedTypeText("Payment Receipt\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                /*    if (contact.get_company_name().length() > 0) {
                                        mIPosPrinterService.printSpecifiedTypeText("اسم الشركة", "", 24, callbackPPT8555);
                                        mIPosPrinterService.setPrinterPrintAlignment(2, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(contact.get_company_name(), "ST", 24, callbackPPT8555);

                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                    }*/
                                    strString = Globals.myRequiredString("كود العميل", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + customercode + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("اسم الزبون", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + customername + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("رقم الاتصال", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + contactno + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("تاريخ", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + modifieddate + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("زمن", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + modifiedtime + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("طريقة الدفع" , strLength);
                                    //mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + spn_paymentmode.getSelectedItem().toString()+ "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + paymentmethod+ "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("توازن قديم", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("تم الاستلام", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);

                                    strString = Globals.myRequiredString("الرصيد الحالي", strLength);
                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "ST", 24, callbackPPT8555);


                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    }
                                    //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    // mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                                    /*strString = Globals.myRequiredString("(المبالغ ب" , strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + Globals.objLPD.getCurreny_Symbol() + ")" + "\n", "ST", 18, callbackPPT8555);*/

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("التوقيع", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString, "", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(user.get_name(), "", 24, callbackPPT8555);
                                    // mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.PrintSpecFormatText(Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                                }
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
                                String strString = "";
                                int strLength = 24;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    else{
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getShort_companyname() + "\n", "", 24, 1, callbackPPT8555);

                                    }
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "", 24, 1, callbackPPT8555);
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "", 24, 1, callbackPPT8555);
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                                        } else {
                                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "", 24, 1, callbackPPT8555);

                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                    } else {
                                        //  mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getLicense_No() + "\n", "", 24, 1, callbackPPT8555);

                                        //  mIPosPrinterService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 14}, new int[]{0, 0, 0}, 0, callbackPPT8555);
                                    }

                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(2, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    strString = Globals.myRequiredString("Payment Receipt/إيصال الدفع\n", strLength);
                                    mIPosPrinterService.PrintSpecFormatText(strString, "", 24, 1, callbackPPT8555);

                                    //  mIPosPrinterService.printSpecifiedTypeText("Payment Receipt\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                   /* if (contact.get_company_name().length() > 0) {
                                        strString = Globals.myRequiredString("Company Name/سم الشركة \n", strLength);

                                        mIPosPrinterService.printSpecifiedTypeText(strString, "", 24, callbackPPT8555);
                                        mIPosPrinterService.setPrinterPrintAlignment(2, callbackPPT8555);
                                        mIPosPrinterService.printSpecifiedTypeText(contact.get_company_name(), "ST", 24, callbackPPT8555);

                                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                    }*/
                                    strString = Globals.myRequiredString("Customer Code/كود العميل ", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + customercode + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Customer Name/اسم الزبون", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + customername + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Contact No/رقم الاتصال", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + contactno + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Date/تاريخ", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" +modifieddate + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("زمن/Time", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n"  + modifiedtime + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.objLPD.getDevice_Name() + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("طريقة الدفع" , strLength);
                                    //mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + spn_paymentmode.getSelectedItem().toString()+ "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(strString+ " : " + paymentmethod.toString()+ "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Old Balance/توازن قديم", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Received/تم الاستلام", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "ST", 24, callbackPPT8555);

                                    strString = Globals.myRequiredString("Current Balance/الرصيد الحالي", strLength);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "ST", 24, callbackPPT8555);


                                        } else {
                                            mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "ST", 24, callbackPPT8555);


                                        }
                                    }

                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("(Amounts in/(المبالغ ب", strLength);

                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n" + Globals.objLPD.getCurreny_Symbol() + ")" + "\n", "ST", 18, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                    strString = Globals.myRequiredString("Signature/التوقيع", strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strString + "\n", "", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(user.get_name(), "", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.PrintSpecFormatText(Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printBlankLines(1, 8, callbackPPT8555);
                                    mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }


            } catch (Exception e) {


            }

        } else if (Globals.PrinterType.equals("2")) {


        } else if (Globals.PrinterType.equals("1")) {
            if (woyouService == null) {
            } else {
                if(Globals.objsettings.get_Print_Lang().equals("0")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 15;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);

                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Payment Receipt\n", "", 28, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);
                                  /*  strString = Globals.myRequiredString("Company Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString("Customer Code", strLength);
                                    woyouService.printTextWithFont(strString + ":" +customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Customer Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Contact No", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Date", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Time", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Payment Mode" , strLength);
                                    woyouService.printTextWithFont(strString + ":" + paymentmethod+ "\n", "", 28, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString("Old Balance", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString("Received", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("Signature", strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else if (Globals.objsettings.get_Print_Lang().equals("1")){
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 14;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);

                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont( "إيصال الدفع" +"\n", "", 28, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);
                                    /*strString = Globals.myRequiredString("اسم الشركة", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString("كود العميل", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("اسم الزبون", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("رقم الاتصال", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("تاريخ", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("زمن", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);

                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);

                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString("الميزان القديم", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString("تم الاستلام", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("الرصيد الحالي", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString("الرصيد الحالي", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("التوقيع", strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        }
        else if(Globals.PrinterType.equals("6")) {
            if (woyouService == null) {
            } else {
                if (Globals.objsettings.get_Print_Lang().equals("0")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 18;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Payment Receipt\n", "", 28, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);
                                    /*strString = Globals.myRequiredString("Company Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString("Customer Code", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Customer Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Contact No", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Date", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Time", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Payment Mode" , strLength);
                                    woyouService.printTextWithFont(strString + ":" + paymentmethod + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString("Old Balance", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString("Received", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("Signature", strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else if(Globals.objsettings.get_Print_Lang().equals("1")){
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 20;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont( " إيصال الدفع"+"\n", "", 28, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);

                                   /* strString = Globals.myRequiredString(" اسم الشركة", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString( "كود العميل", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("اسم الزبون", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString( "رقم الاتصال", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("تاريخ", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("زمن", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);


                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString( "الميزان القديم", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString( "تم الاستلام", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString( "الرصيد الحالي", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString( "الرصيد الحالي", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString( "الرصيد الحالي", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString( "التوقيع" , strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }


                else{

                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 18;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Payment Receipt/إيصال الدفع\n", "", 28, callback);
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);
                                   /* strString = Globals.myRequiredString("Company Name/اسم الشركة \n", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString("Customer Code/كود العميل \n", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Customer Name/اسم الزبون \n", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Contact No/رقم الاتصال \n", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Date/تاريخ \n", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Time/زمن \n", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);

                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);

                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString("Old Balance/ لميزان القديم \n", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString("Received/تم الاستلام  \n", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance/الرصيد الحالي  \n", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance/الرصيد الحالي  \n", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString("Current Balance/الرصيد الحالي  \n", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("Signature", strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


            }
        }
        else if(Globals.PrinterType.equals("9")) {
            if (woyouService == null) {
            } else {
                if (Globals.objsettings.get_Print_Lang().equals("0")) {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String strString = "";
                                int strLength = 15;
                                for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    String Print_type = "0";
                                    woyouService.setFontSize(28, callback);
                                    woyouService.setAlignment(1, callback);
                                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);

                                    }
                                    if (Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null") || Globals.objLPR.getAddress().length() == 0 || Globals.objLPR.getAddress().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getAddress() + "\n", "", 28, callback);
                                    }
                                    if (Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null") || Globals.objLPR.getMobile_No().length() == 0 || Globals.objLPR.getMobile_No().isEmpty()) {

                                    } else {
                                        woyouService.printTextWithFont("" + Globals.objLPR.getMobile_No() + "\n", "", 28, callback);
                                    }
                                    try {
                                        if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().isEmpty()) {

                                        } else {
                                            woyouService.printTextWithFont("" + Globals.objLPR.getService_code_tariff() + "\n", "", 28, callback);
                                        }
                                    } catch (Exception ex) {
                                    }

                                    if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                                    } else {
                                        woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                                    }

                                    //   woyouService.setAlignment(1, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.printTextWithFont("Payment Receipt\n", "", 28, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    woyouService.setAlignment(0, callback);
                                   /* strString = Globals.myRequiredString("Company Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);*/
                                    strString = Globals.myRequiredString("Customer Code", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customercode + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Customer Name", strLength);
                                    woyouService.printTextWithFont(strString + ":" + customername + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Contact No", strLength);
                                    woyouService.printTextWithFont(strString + ":" + contactno + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Date", strLength);
                                    woyouService.printTextWithFont(strString + ":" + modifieddate + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Time", strLength);

                                    woyouService.printTextWithFont(strString + ":" + modifiedtime + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                                    woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                                    strString = Globals.myRequiredString("Payment Mode", strLength);
                                    woyouService.printTextWithFont(strString + ":" + paymentmethod + "\n", "", 28, callback);
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);

                                    strString = Globals.myRequiredString("Old Balance", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(oldbal), decimal_check) + "\n", "", 28, callback);

                                    // woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) + "\n", "", 35, callback);
                                    strString = Globals.myRequiredString("Received", strLength);

                                    woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(recbal), decimal_check) + "\n", "", 28, callback);

                                    //woyouService.printTextWithFont(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) +  "\n", "", 35, callback);

                                    Double ab;
                                    if (txt_type_total.getText().toString().equals("DR")) {
                                        ab = Double.parseDouble(oldbal) + Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);


                                        }
                                    } else {
                                        ab = Double.parseDouble(oldbal) - Double.parseDouble(recbal);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" + "\n", "", 28, callback);


                                        } else {
                                            strString = Globals.myRequiredString("Current Balance", strLength);

                                            woyouService.printTextWithFont(strString + " : " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" + "\n", "", 28, callback);

                                        }
                                    }
                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);
                                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                    strString = Globals.myRequiredString("Signature", strLength);

                                    woyouService.printTextWithFont(strString + ":" + user.get_name() + "\n", "", 28, callback);

                                    woyouService.printTextWithFont("-------------------------------\n", "", 24, callback);

                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                    woyouService.cutPaper(callback);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        else if(Globals.PrinterType.equals("10")) {
                       /* final File file = new File(Globals.folder + Globals.pdffolder
                                + "/" + strOrderNo + ".pdf");*/
                      Intent intent = new Intent(AccountsActivity.this, PdfWebView.class);
                        intent.putExtra("contact_code", contact.get_contact_code());
                        intent.putExtra("from","Accounts");
                        startActivity(intent);
                       finish();

                       /* Intent intent = new Intent(PrintLayout.this, PdfWebView.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);*/

        }
        else if(Globals.PrinterType.equals("11")) {
                       /* final File file = new File(Globals.folder + Globals.pdffolder
                                + "/" + strOrderNo + ".pdf");*/
            Intent intent = new Intent(AccountsActivity.this, PdfWebView.class);
            intent.putExtra("contact_code", contact.get_contact_code());
            intent.putExtra("from","Accounts");
            startActivity(intent);
            finish();

                       /* Intent intent = new Intent(PrintLayout.this, PdfWebView.class);
                        intent.putExtra("order_code", strOrderNo);
                        startActivity(intent);*/

        }
    }

    private void GetCustomerCreditDetail(String serverData) {
        try {

            if (serverData.equals("") || serverData.equals("null")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "No credit found", Toast.LENGTH_SHORT).show();
                    }
                });
             } else {
                try {

                    final JSONObject jsonObject_manufacture = new JSONObject(serverData);
                    final String strStatus = jsonObject_manufacture.getString("status");
                    final String strmsg = jsonObject_manufacture.getString("message");
                    if (strStatus.equals("true")) {
                        JSONArray jsonArray = jsonObject_manufacture.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            strCRAmount = jsonObject.getString("amount");
                            try {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        double abs = 0d;
                                        Double strCheckAmmt;
                                        try {
                                            strCheckAmmt = Double.parseDouble(strCRAmount);
//                                        abs1 = Math.abs(Double.parseDouble(strCRAmount));
                                        } catch (Exception ex) {
                                            strCheckAmmt = 0d;
//                                        abs1 = Math.abs(0);
                                        }
                                        try {
                                            abs = Math.abs(strCheckAmmt);
                                            if (strCheckAmmt > 0) {
                                                txt_type.setText("CR");
                                                txt_type.setTextColor(Color.parseColor("#FF0000"));

                                            } else {
                                                txt_type.setText("DR");
                                                txt_type.setTextColor(Color.parseColor("#228B22"));

                                            }
                                            txt_cr_amt.setText(Globals.myNumberFormat2Price(abs, decimal_check));
                                        } catch (Exception e) {

                                        }
                                        String curAmount = "";
                                        try {
                                            String strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + code + "' and orders.z_code = '0' and order_payment.payment_id='5')";
                                            Cursor cursor = database.rawQuery(strTableQry, null);
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);

                                                } while (cursor.moveToNext());
                                            }
                                        } catch (Exception ex) {
                                            curAmount = "0";
                                        }
                                        String strCur = Globals.myNumberFormat2Price(Double.parseDouble(curAmount), decimal_check);
                                        txt_cr_lc_amt.setText(strCur);
                                        try {

                                            if (txt_type.getText().toString().equals("DR")) {
                                                try {
                                                    Double strCrAmt = Double.parseDouble(strCRAmount);
                                                    double abs2 = Math.abs(strCrAmt);
                                                    double ab = abs2 - Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                        txt_type_total.setText("DR");
                                                        txt_type_total.setTextColor(Color.parseColor("#228B22"));
                                                        // edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                    } else {
                                                        txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                        txt_type_total.setText("CR");
                                                        txt_type_total.setTextColor(Color.parseColor("#FF0000"));
                                                        // edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                    }
                                                } catch (Exception ex) {
                                                    txt_total_amt.setText(Globals.myNumberFormat2Price(0, decimal_check));
                                                }
                                            } else {
                                                try {
                                                    Double strCrAmt = Double.parseDouble(strCRAmount);
                                                    double abs1 = Math.abs(strCrAmt + Double.parseDouble(curAmount));
                                                    if (abs1 > 0) {
                                                        txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                        txt_type_total.setText("CR");
                                                        txt_type_total.setTextColor(Color.parseColor("#FF0000"));
                                                        edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                    } else {
                                                        txt_total_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                        txt_type_total.setText("CR");
                                                        txt_type_total.setTextColor(Color.parseColor("#FF0000"));
                                                        edt_pd_amt.setText(Globals.myNumberFormat2Price(abs1, decimal_check));
                                                    }
                                                } catch (Exception ex) {
                                                    txt_total_amt.setText(Globals.myNumberFormat2Price(0, decimal_check));
                                                }


                                                edt_pd_amt.requestFocus();
                                                edt_pd_amt.selectAll();
                                            }

                                        } catch (Exception ex) {
                                        }

                                    }


                                });

                            } catch (Exception ex) {
                            }
                        }
                    } else if (strStatus.equals("false")) {

                        try {
                            if (strmsg.equals("Device Not Found")) {

                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                lite_pos_device.setStatus("Out");
                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                if (ct > 0) {

                                    Intent intent_category = new Intent(AccountsActivity.this, LoginActivity.class);
                                    startActivity(intent_category);
                                    finish();
                                }


                            }

                        } catch (Exception e) {
                            System.out.println("Device not found Exception " + e.getMessage());
                        }
                    } else {
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No credit found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No credit found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception ex) {
        }
    }

    private String SendCustomerCreditDetail(String serverData) {

        String succ = "0";
        try {
            database.beginTransaction();


            try {
                final JSONObject jsonObject_manufacture = new JSONObject(serverData);
                final String strStatus = jsonObject_manufacture.getString("status");
                final String strmsg = jsonObject_manufacture.getString("message");
                JSONObject result = jsonObject_manufacture.getJSONObject("result");
                if (strStatus.equals("true")) {

                    String accountid = result.getString("account_id");


                    Double balance = Double.parseDouble(txt_cr_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString().trim());
                    String strBalance = Globals.myNumberFormat2Price(balance, decimal_check);
                    Acc_Customer_Credit acc_customer_credit = new Acc_Customer_Credit(getApplicationContext(), null, date, txt_cus_code.getText().toString(), txt_cr_amt.getText().toString(), edt_pd_amt.getText().toString().trim(), strBalance, "0", "1", modified_by, date, accountid,PayId);
                    long l = acc_customer_credit.insertAcc_Customer_Credit(database);
                    if (l > 0) {
                        succ = "1";
                    }
                } else if (strStatus.equals("false")) {

                    try {
                        if (strmsg.equals("Device Not Found")) {

                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                            lite_pos_device.setStatus("Out");
                            long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                            if (ct > 0) {

                                Intent intent_category = new Intent(AccountsActivity.this, LoginActivity.class);
                                startActivity(intent_category);
                                finish();
                            }


                        }

                    } catch (Exception e) {
                        System.out.println("Device not found Exception " + e.getMessage());
                    }
                } else {
                    database.endTransaction();
                }

                if (succ.equals("1")) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                }
            } catch (JSONException e) {
                database.endTransaction();
            }
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
        return succ;
    }

   /* private String GetCusCrDetailFromServer() {
        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(

                    Globals.App_IP_URL + "accounts");
            ArrayList nameValuePairs = new ArrayList(9);
            nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
            nameValuePairs.add(new BasicNameValuePair("contact_code", code));
            nameValuePairs.add(new BasicNameValuePair("type", "S"));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
            System.out.println("get accounts" + nameValuePairs);
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
                System.out.println("response get accounts" + serverData);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
        return serverData;
    }
*/


    public void GetCusCrDetailFromServer(String serial_no,String android_id,String myKey,final ProgressDialog pDialog) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "accounts";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                           GetCustomerCreditDetail(response);
                            pDialog.dismiss();
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
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("contact_code", code);
                params.put("type", "S");
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", Globals.license_id);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
 /*   private String SendCusCrDetailFromServer(String stringnarration) {
        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
                    Globals.App_IP_URL + "accounts/data");
            ArrayList nameValuePairs = new ArrayList(10);
            nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("contact_code", code));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
            nameValuePairs.add(new BasicNameValuePair("type", "S"));
            nameValuePairs.add(new BasicNameValuePair("amount", edt_pd_amt.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
            nameValuePairs.add(new BasicNameValuePair("payment_id", PayId));
            nameValuePairs.add(new BasicNameValuePair("narration", stringnarration + " " + Globals.devicename +"/"+Globals.username));
            System.out.println("send account " + nameValuePairs);
//        nameValuePairs.add(new BasicNameValuePair("modified_by", modified_by));
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
                System.out.println("response send account " + nameValuePairs);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
        return serverData;

    }*/

    public void SendCusCrDetailFromServer(String serial_no,String android_id,String myKey,String stringnarration) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "accounts/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = SendCustomerCreditDetail(response);

                            if (result.equals("1")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        print_dialog();
                                    }
                                });



                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + code + "'", database);
                                            Double strOldBalance = 0d;
                                            Double strAmount = 0d;
                                            if (acc_customer == null) {
                                                try {
//                                                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                                    strAmount = strOldBalance + Double.parseDouble(edt_pd_amt.getText().toString());
                                                    acc_customer = new Acc_Customer(getApplicationContext(), null, code, strAmount + "");
                                                    acc_customer.insertAcc_Customer(database);
                                                } catch (Exception e) {

                                                }
                                            } else {
                                                try {
                                                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                                    strAmount = strOldBalance + Double.parseDouble(edt_pd_amt.getText().toString());
                                                    acc_customer.set_amount(strAmount + "");
                                                    long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{code}, database);
                                                } catch (Exception e) {

                                                }
                                            }

                                        } catch (Exception e) {

                                        }



//                                                if (PrinterType.equals("1")) {
                                            try {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // If  Is Direct Print  Parameter true
                                                        // If not then give dialog  if press yes then print function call else no




                                                    }
                                                });
                                            } catch (Exception ex) {


                                            }

//                                                }

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        Toast.makeText(AccountsActivity.this, "Transaction error", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
               params.put("contact_code", code);
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("type", "S");
                params.put("amount", edt_pd_amt.getText().toString().trim());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", Globals.license_id);
                params.put("payment_id", PayId);
                params.put("date", date);
                params.put("narration", stringnarration + " " + Globals.devicename +"/"+Globals.username);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
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
        pDialog = new ProgressDialog(AccountsActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    if(Globals.objLPR.getIndustry_Type().equals("1")) {
                        Intent intent = new Intent(AccountsActivity.this, AccountsListActivity.class);
                        intent.putExtra("whatsappFlag",bWhatsappFlag);
                        intent.putExtra("contact_code", "");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    }
                    else if(Globals.objLPR.getIndustry_Type().equals("2")) {
                        Intent intent = new Intent(AccountsActivity.this, AccountsListActivity.class);
                        intent.putExtra("whatsappFlag",bWhatsappFlag);
                        intent.putExtra("contact_code", "");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    }
                    else if(Globals.objLPR.getIndustry_Type().equals("3")){
                        Intent intent = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                        intent.putExtra("whatsappFlag",bWhatsappFlag);
                        intent.putExtra("contact_code", "");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    }
                } finally {
                }
            }
        };
        timerThread.start();
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
                Toast.makeText(AccountsActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(AccountsActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(AccountsActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
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

    private void performOperationEn() {
        noofPrint = Integer.parseInt(Globals.objsettings.get_No_Of_Print());
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
                    AccountsActivity.this.finish();
                }
            } catch (Exception e) {
            }

        }
    }
    public Boolean CheckprinterConnection() {
        if (Globals.objsettings != null) {

            if (Globals.objsettings.getPrinterId().equals("2")) {
                String tmpStr = Globals.objsettings.getPrinterIp().trim();
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
            dialog = new ProgressDialog(AccountsActivity.this);
            dialog.setCancelable(false);
            dialog.show();
        }

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

    private ArrayList<String> getlist_wifiprint(String oldbal,String recbal) {
    paidamount=edt_pd_amt.getText().toString();
    if(paidamount.equals("")){
        paidamount=recbal;
    }
    else{
        paidamount=edt_pd_amt.getText().toString();
    }
        ArrayList<String> mylist = new ArrayList<String>();
        String strString = "";
        int strLength = 16;
        String lbl;
        String tt = "", tt1 = "";
        if(Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null")|| Globals.objLPR.getShort_companyname().length()==0 ||Globals.objLPR.getShort_companyname().isEmpty()) {
            lbl = LableCentre(Globals.objLPR.getCompany_Name());
            mylist.add("\n" + lbl);
        }
        else {
            lbl = LableCentre(Globals.objLPR.getShort_companyname());
            mylist.add("\n" + lbl);
        }
        if(Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null")|| Globals.objLPR.getAddress().length()==0 ||Globals.objLPR.getAddress().isEmpty()) {

        }
        else{
            lbl = LableCentre(Globals.objLPR.getAddress());
            mylist.add("\n" + lbl);
        }
        if(Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null")|| Globals.objLPR.getMobile_No().length()==0|| Globals.objLPR.getMobile_No().isEmpty()) {

        }
        else{
            lbl = LableCentre(Globals.objLPR.getMobile_No());
            mylist.add("\n"+lbl+"\n");
        }


        try {
            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

            } else {
                lbl = LableCentre(Globals.objLPR.getService_code_tariff());
                mylist.add(" "+lbl +"\n");
//                    mylist.add("\n" + "Tariff Code");
            }
        } catch (Exception ex) {

        }
        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
        } else {
            mylist.add(" "+Globals.GSTNo + ":" + Globals.objLPR.getLicense_No()+"\n");
        }
        mylist.add("..............................................\n");

        mylist.add("               " + "Payment Receipt");
        mylist.add("\n..............................................\n");

        strString = Globals.myRequiredString("Customer Code" , strLength);
        mylist.add("  "+strString+ " : " + txt_cus_code.getText().toString()+"\n");
        strString = Globals.myRequiredString("Customer Name" , strLength);
        mylist.add("  "+strString+ " : "+ txt_cus_name.getText().toString()+"\n");
        strString = Globals.myRequiredString("Contact No" , strLength);
        mylist.add("  "+strString+ " : "+ contact.get_contact_1()+"\n");
        strString = Globals.myRequiredString("Date" , strLength);
        mylist.add("  "+strString+ " : " + date.substring(0,10)+"\n");
        strString = Globals.myRequiredString("Time" , strLength);
        mylist.add("  "+strString+ " : " + date.substring(11,19)+"\n");
        strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
        mylist.add("  "+strString+ " : " +  Globals.objLPD.getDevice_Name() + "\n");
        strString = Globals.myRequiredString("Payment Mode" , strLength);
        mylist.add("  "+strString+ " : " + strPayMethod +"\n");
        mylist.add("..............................................\n");
        strString = Globals.myRequiredString("Old Balance" , strLength);
        mylist.add("  "+strString+ " : " + Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check)+"\n");

        // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check), "GBK");
        strString = Globals.myRequiredString("Received" , strLength);
        mylist.add("  "+strString+ " : " + Globals.myNumberFormat2Price(Double.parseDouble(paidamount), decimal_check)+ "\n");

        // mService.sendMessage(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check), "GBK");

        Double ab;
        strString = Globals.myRequiredString("Current Balance" , strLength);
        if (txt_type_total.getText().toString().equals("DR")) {
            ab = Double.parseDouble(txt_total_amt.getText().toString()) + Double.parseDouble(paidamount);
            double abs1 = Math.abs(ab);
            if (ab > 0) {

                mylist.add("  "+strString+ " : "  + Globals.myNumberFormat2Price(abs1, decimal_check) +" DR");

                //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check)+ "DR" , "GBK");

            }
        } else {
            ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(paidamount);
            double abs1 = Math.abs(ab);
            if (ab > 0) {
                mylist.add("  "+strString+ " : "  + Globals.myNumberFormat2Price(abs1, decimal_check) +" CR" );

                // mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "CR", "GBK");

            } else {
                mylist.add("  "+strString+ " : "  + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR");

                //  mService.sendMessage(Globals.myNumberFormat2Price(abs1, decimal_check) + "DR", "GBK");
            }
        }


        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        //strString = Globals.myRequiredString("(Amounts in" , strLength);
        //mylist.add("  "+"(Amounts in" +Globals.objLPD.getCurreny_Symbol() + ")"+"\n");
        mylist.add("\n..............................................\n");
        strString = Globals.myRequiredString("Signature" , strLength);
        mylist.add("  "+strString+ " : " + user.get_name());
        mylist.add("\n..............................................\n");
        mylist.add("               " + Globals.objsettings.get_Copy_Right() +"\n");
        //  mylist.add("\nTotal           : " + totalAmount);
        mylist.add("\n");
        mylist.add("\n");
        mylist.add("\n");
        return mylist;
    }
    private  void createList(Section subCatPart) {

        List list = new List(false, false, 0);

        list.add(new ListItem("Company Name"+ " : " + Globals.objLPR.getCompany_Name()));
        list.add(new ListItem("Customer Code: "+txt_cus_code.getText().toString()));
        list.add(new ListItem("Customer Name: "+txt_cus_name.getText().toString()));
        list.add(new ListItem("Contact No: "+contact.get_contact_1().toString()));
        list.add(new ListItem("Date : "+date.substring(0,10)));
        list.add(new ListItem("Time : "+date.substring(11,19)));
        list.add(new ListItem(Globals.PrintDeviceID + Globals.objLPD.getDevice_Name()));
        list.add(new ListItem("Payment Mode: "+strPayMethod));
        list.add(new ListItem("Old Balance: "+Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check)));
        list.add(new ListItem("Received: "+strPayMethod));
        Double ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString());
        double abs1 = Math.abs(ab);
        if (ab > 0) {
            list.add(new ListItem("Current Balance: " + Globals.myNumberFormat2Price(abs1, decimal_check) +" CR"));
        }
        else{
            list.add(new ListItem("Current Balance: " + Globals.myNumberFormat2Price(abs1, decimal_check) +" DR"));
        }

        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

        list.add(new ListItem("Signature: "+user.get_name()));
        subCatPart.add(list);
    }

    protected void performPDFExport(String oldbal, String receivedbal,String currentbal, String totaltype) {

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + contact.get_contact_code() + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
           // writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
//            image = createimage();


            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);



                PdfPTable table_company_name = new PdfPTable(1);
                Phrase prcommpanyname ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getCompany_Name(), B10);
                } else {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getShort_companyname(), B10);

                }
                PdfPCell cell_company_name;
                cell_company_name = new PdfPCell(prcommpanyname);
                cell_company_name.setColspan(1);
                cell_company_name.setBorder(Rectangle.NO_BORDER);
                cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell_company_name.setPadding(5.0f);
                table_company_name.addCell(cell_company_name);


                table_company_name.setSpacingBefore(10.0f);
             //  document.open();

              PdfPTable  table_company_address = new PdfPTable(1);
                Phrase prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10);
                PdfPCell cell_company_add;
                cell_company_add = new PdfPCell(prcommpanyadd);
                cell_company_add.setBorder(Rectangle.NO_BORDER);
                cell_company_add.setColspan(1);
                cell_company_add.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell_company_add.setPadding(5.0f);
                table_company_address.addCell(cell_company_add);

               // document.open();

                PdfPTable table_company_mobile = new PdfPTable(1);
                Phrase prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10);
                PdfPCell cell_company_mobile;
                cell_company_mobile = new PdfPCell(prcommpanymobile);
                cell_company_mobile.setColspan(1);
                cell_company_mobile.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell_company_mobile.setPadding(5.0f);
                cell_company_mobile.setBorder(Rectangle.NO_BORDER);
                table_company_address.addCell(cell_company_mobile);
               document.open();

                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.paymentreceipt), B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prcommpanyposno = new Phrase(getString(R.string.customer_code), B10);
                PdfPCell cell_posno = new PdfPCell(prcommpanyposno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prcommpanyposno = new Phrase("" + contact.get_contact_code(), B10);
                PdfPCell posno = new PdfPCell(prcommpanyposno);
                posno.setPadding(5);
                posno.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_posno.addCell(posno);

                document.open();

                PdfPTable table_cusname = new PdfPTable(2);
                Phrase prcommpanyponame = new Phrase(getString(R.string.customer_name), B10);
                PdfPCell cell_posname = new PdfPCell(prcommpanyponame);
                cell_posname.setColspan(1);
                cell_posname.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_posname.setPadding(5.0f);
                table_cusname.addCell(cell_posname);
                prcommpanyponame = new Phrase("" + txt_cus_name.getText().toString(), B10);
                PdfPCell posname2 = new PdfPCell(prcommpanyponame);
                posname2.setPadding(5);
                posname2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_cusname.addCell(posname2);

                document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prcontactno = new Phrase(getString(R.string.contactno), B10);
                PdfPCell cell_order_no = new PdfPCell(prcontactno);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prcontactno = new Phrase("" + contact.get_contact_1(), B10);
                PdfPCell contctno = new PdfPCell(prcontactno);
                contctno.setPadding(5);
                contctno.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_no.addCell(contctno);
                document.open();


                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prtdatetime = new Phrase(getString(R.string.datetime), B10);

                PdfPCell cell_order_date = new PdfPCell(prtdatetime);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);
                prtdatetime = new Phrase("" + date, B10);
                PdfPCell datetime = new PdfPCell(prtdatetime);
                datetime.setPadding(5);
                datetime.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_date.addCell(datetime);

                PdfPTable table_deviceid = new PdfPTable(2);
                Phrase prtdevice = new Phrase( Globals.PrintDeviceID, B10);

                PdfPCell cell_order_device = new PdfPCell(prtdevice);
                cell_order_device.setColspan(1);
                cell_order_device.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_device.setPadding(5.0f);
                table_deviceid.addCell(cell_order_device);
                prtdevice = new Phrase("" + Globals.objLPD.getDevice_Name() , B10);
                PdfPCell deviceid = new PdfPCell(prtdevice);
                deviceid.setPadding(5);
                deviceid.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_deviceid.addCell(deviceid);


                PdfPTable table_paymentmode = new PdfPTable(2);
                Phrase prtpaymode = new Phrase( "Payment Mode", B10);

                PdfPCell cell_order_Pmode = new PdfPCell(prtpaymode);
                cell_order_Pmode.setColspan(1);
                cell_order_Pmode.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_Pmode.setPadding(5.0f);
                table_paymentmode.addCell(cell_order_Pmode);
                prtpaymode = new Phrase("" + strPayMethod , B10);
                PdfPCell paymentmode = new PdfPCell(prtpaymode);
                paymentmode.setPadding(5);
                paymentmode.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_paymentmode.addCell(paymentmode);

                PdfPTable table_oldbalance = new PdfPTable(2);
                Phrase prtoldbal = new Phrase(getString(R.string.oldbal), B10);

                PdfPCell cell_oldbal = new PdfPCell(prtoldbal);
                cell_oldbal.setColspan(1);
                cell_oldbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_oldbal.setPadding(5.0f);
                table_oldbalance.addCell(cell_oldbal);
                prtoldbal = new Phrase("" + oldbal, B10);
                PdfPCell dateoldbal = new PdfPCell(prtoldbal);
                dateoldbal.setPadding(5);
                dateoldbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_oldbalance.addCell(dateoldbal);

                PdfPTable table_recbalance = new PdfPTable(2);
                Phrase prtrecbal = new Phrase(getString(R.string.recbal), B10);

                PdfPCell cell_recbal = new PdfPCell(prtrecbal);
                cell_recbal.setColspan(1);
                cell_recbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_recbal.setPadding(5.0f);
                table_recbalance.addCell(cell_recbal);
                prtrecbal = new Phrase("" + receivedbal, B10);
                PdfPCell daterecbal = new PdfPCell(prtrecbal);
                daterecbal.setPadding(5);
                daterecbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_recbalance.addCell(daterecbal);


                PdfPTable table_currentbalance = new PdfPTable(2);
                Phrase prtcurrentbal = new Phrase(getString(R.string.currbal), B10);
                PdfPCell cell_currentbal = new PdfPCell(prtcurrentbal);
                cell_currentbal.setColspan(1);
                cell_currentbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_currentbal.setPadding(5.0f);
                table_currentbalance.addCell(cell_currentbal);
                prtcurrentbal = new Phrase("" + currentbal + " " + totaltype, B10);
                PdfPCell datecurrentbal = new PdfPCell(prtcurrentbal);
                datecurrentbal.setPadding(5);
                datecurrentbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_currentbalance.addCell(datecurrentbal);

                document.open();
               // User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                PdfPTable table_signature = new PdfPTable(2);
                Phrase prtcurrentsign = new Phrase(getString(R.string.signature), B10);
                PdfPCell cell_currentsign = new PdfPCell(prtcurrentsign);
                cell_currentsign.setColspan(1);
                cell_currentsign.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_currentsign.setPadding(5.0f);
                table_signature.addCell(cell_currentsign);
                prtcurrentsign = new Phrase("" +user.get_name(), B10);
                PdfPCell cellsign = new PdfPCell(prtcurrentsign);
                cellsign.setPadding(5);
                cellsign.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_signature.addCell(cellsign);

                document.open();
                document.add(table_company_name);
                document.add(table_company_address);
                document.add(table_company_mobile);
                document.add(tableh);
                document.add(table_posno);
                document.add(table_cusname);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_deviceid);
                document.add(table_paymentmode);
                document.add(table_oldbalance);
                document.add(table_recbalance);
                document.add(table_currentbalance);
                document.add(table_signature);
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
                    } catch (ActivityNotFoundException e) {
                    }
                }
            }

            else if(Globals.objsettings.get_Print_Lang().equals("1")){

                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.BOLD);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph("طلب ", B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(2);
                Phrase prcommpanyname = new Phrase("اسم الشركة", B10);
                table_company_name.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_company_name;
                cell_company_name = new PdfPCell(prcommpanyname);
                cell_company_name.setBorder(Rectangle.NO_BORDER);
                cell_company_name.setColspan(1);
                cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_company_name.setPadding(5.0f);
                table_company_name.addCell(cell_company_name);
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getCompany_Name(), B10E);
                } else {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getShort_companyname(), B10E);

                }

                PdfPCell company2 = new PdfPCell(prcommpanyname);
                company2.setPadding(5);
                company2.setBorder(Rectangle.NO_BORDER);
                company2.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_company_name.addCell(company2);

                table_company_name.setSpacingBefore(10.0f);
                document.open();


                PdfPTable table_company_address = new PdfPTable(2);
                Phrase prcommpanyadd = new Phrase("اسم الشركة", B10);
                table_company_address.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_company_add;
                cell_company_add = new PdfPCell(prcommpanyadd);
                cell_company_add.setBorder(Rectangle.NO_BORDER);
                cell_company_add.setColspan(1);
                cell_company_add.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_company_add.setPadding(5.0f);
                table_company_address.addCell(cell_company_add);
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10E);
                } else {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10E);

                }

                PdfPCell companyadd2 = new PdfPCell(prcommpanyadd);
                companyadd2.setPadding(5);
                companyadd2.setBorder(Rectangle.NO_BORDER);
                companyadd2.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_company_address.addCell(companyadd2);

                table_company_address.setSpacingBefore(10.0f);
                document.open();

                PdfPTable table_company_mobile = new PdfPTable(2);
                Phrase prcommpanymobile = new Phrase("اسم الشركة", B10);
                table_company_mobile.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_company_mobile;
                cell_company_mobile = new PdfPCell(prcommpanymobile);
                cell_company_mobile.setColspan(1);
                cell_company_mobile.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_company_mobile.setPadding(5.0f);
                table_company_mobile.addCell(cell_company_mobile);
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10E);
                } else {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10E);

                }

                PdfPCell companymobile = new PdfPCell(prcommpanymobile);
                companymobile.setPadding(5);
                companymobile.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_company_mobile.addCell(companymobile);

                table_company_mobile.setSpacingBefore(10.0f);
                document.open();


                PdfPTable table_posno = new PdfPTable(2);
                Phrase prcommpanyposno = new Phrase("بطاقة العميل", B10);
                table_posno.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_posno = new PdfPCell(prcommpanyposno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prcommpanyposno = new Phrase("" + contact.get_contact_code(), B10E);
                PdfPCell posno = new PdfPCell(prcommpanyposno);
                posno.setPadding(5);
                posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(posno);



                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prcontactno = new Phrase("رقم الاتصال", B10);
                table_order_no.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_no = new PdfPCell(prcontactno);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prcontactno = new Phrase("" + contact.get_contact_1(), B10E);
                PdfPCell contctno = new PdfPCell(prcontactno);
                contctno.setPadding(5);
                contctno.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(contctno);



                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prtdatetime = new Phrase("التاريخ والوقت", B10);
                table_order_date.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_date = new PdfPCell(prtdatetime);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);
                prtdatetime = new Phrase("" + date, B10E);
                PdfPCell datetime = new PdfPCell(prtdatetime);
                datetime.setPadding(5);
                datetime.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_date.addCell(datetime);

                PdfPTable table_oldbalance = new PdfPTable(2);
                Phrase prtoldbal = new Phrase("موازنة القديم", B10);
                table_oldbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_oldbal = new PdfPCell(prtoldbal);
                cell_oldbal.setColspan(1);
                cell_oldbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_oldbal.setPadding(5.0f);
                table_oldbalance.addCell(cell_oldbal);
                prtoldbal = new Phrase("" + oldbal, B10E);
                PdfPCell dateoldbal = new PdfPCell(prtoldbal);
                dateoldbal.setPadding(5);
                dateoldbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_oldbalance.addCell(dateoldbal);

                PdfPTable table_recbalance = new PdfPTable(2);
                Phrase prtrecbal = new Phrase("حصل موازنة", B10);
                table_recbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_recbal = new PdfPCell(prtrecbal);
                cell_recbal.setColspan(1);
                cell_recbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_recbal.setPadding(5.0f);
                table_recbalance.addCell(cell_recbal);
                prtrecbal = new Phrase("" + receivedbal, B10E);
                PdfPCell daterecbal = new PdfPCell(prtrecbal);
                daterecbal.setPadding(5);
                daterecbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_recbalance.addCell(daterecbal);


                PdfPTable table_currentbalance = new PdfPTable(2);
                Phrase prtcurrentbal = new Phrase("الرصيد الحالي", B10);
                table_currentbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_currentbal = new PdfPCell(prtcurrentbal);
                cell_currentbal.setColspan(1);
                cell_currentbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_currentbal.setPadding(5.0f);
                table_currentbalance.addCell(cell_currentbal);
                prtcurrentbal = new Phrase("" + currentbal + " " + totaltype, B10E);
                PdfPCell datecurrentbal = new PdfPCell(prtcurrentbal);
                datecurrentbal.setPadding(5);
                datecurrentbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_currentbalance.addCell(datecurrentbal);

                document.open();


                document.add(table_company_name);
                document.add(table_company_address);
                document.add(table_company_mobile);
                document.add(tableh);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_oldbalance);
                document.add(table_recbalance);
                document.add(table_currentbalance);

                PdfPTable table5 = new PdfPTable(1);
                table5.setSpacingBefore(10.0f); // Space Before table starts, like
                table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                PdfPTable table4 = new PdfPTable(2);
                table4.setSpacingBefore(20.0f);
                float[] columnWidths1 = new float[]{20f, 5f};
                table4.setWidths(columnWidths1);
                table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                Phrase p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Cashier, N12);
                PdfPCell cell5 = new PdfPCell(p5);
                cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell5.setBorder(Rectangle.NO_BORDER);
                p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Manager, N12);
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
                    } catch (ActivityNotFoundException e) {
                    }
                }
            }

            else if(Globals.objsettings.get_Print_Lang().equals("2")){

                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.BOLD);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph("حسابات", B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(2);
                Phrase prcommpanyname = new Phrase("اسم الشركة", B10);
                prcommpanyname.add(new Chunk("  Company Name",B10E));
                table_company_name.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_company_name;
                cell_company_name = new PdfPCell(prcommpanyname);
                cell_company_name.setColspan(1);
                cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_company_name.setPadding(5.0f);
                table_company_name.addCell(cell_company_name);
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getCompany_Name(), B10E);
                } else {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getShort_companyname(), B10E);

                }

                PdfPCell company2 = new PdfPCell(prcommpanyname);
                company2.setPadding(5);
                company2.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_company_name.addCell(company2);

                table_company_name.setSpacingBefore(10.0f);
                document.open();

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prcommpanyposno = new Phrase("بطاقة العميل", B10);
                prcommpanyposno.add(new Chunk("/Customer code",B10E));
                table_posno.setRunDirection(writer.RUN_DIRECTION_RTL);

                PdfPCell cell_posno = new PdfPCell(prcommpanyposno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prcommpanyposno = new Phrase("" + contact.get_contact_code(), B10E);
                PdfPCell posno = new PdfPCell(prcommpanyposno);
                posno.setPadding(5);
                posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(posno);



                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prcontactno = new Phrase("رقم الاتصال", B10);
                prcontactno.add(new Chunk("   Contact No",B10E));
                table_order_no.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_no = new PdfPCell(prcontactno);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prcontactno = new Phrase("" + contact.get_contact_1(), B10E);
                PdfPCell contctno = new PdfPCell(prcontactno);
                contctno.setPadding(5);
                contctno.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(contctno);



                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prtdatetime = new Phrase("التاريخ والوقت", B10);
                prtdatetime.add(new Chunk("   Date",B10E));
                table_order_date.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_date = new PdfPCell(prtdatetime);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);
                prtdatetime = new Phrase("" + date, B10E);
                PdfPCell datetime = new PdfPCell(prtdatetime);
                datetime.setPadding(5);
                datetime.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_date.addCell(datetime);

                PdfPTable table_oldbalance = new PdfPTable(2);
                Phrase prtoldbal = new Phrase("موازنة القديم", B10);
                prtoldbal.add(new Chunk("   Old Balance",B10E));
                table_oldbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_oldbal = new PdfPCell(prtoldbal);
                cell_oldbal.setColspan(1);
                cell_oldbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_oldbal.setPadding(5.0f);
                table_oldbalance.addCell(cell_oldbal);
                prtoldbal = new Phrase("" + oldbal, B10E);
                PdfPCell dateoldbal = new PdfPCell(prtoldbal);
                dateoldbal.setPadding(5);
                dateoldbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_oldbalance.addCell(dateoldbal);

                PdfPTable table_recbalance = new PdfPTable(2);
                Phrase prtrecbal = new Phrase("حصل موازنة", B10);
                prtrecbal.add(new Chunk("  Received Balance",B10E));
                table_recbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_recbal = new PdfPCell(prtrecbal);
                cell_recbal.setColspan(1);
                cell_recbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_recbal.setPadding(5.0f);
                table_recbalance.addCell(cell_recbal);
                prtrecbal = new Phrase("" + receivedbal, B10E);
                PdfPCell daterecbal = new PdfPCell(prtrecbal);
                daterecbal.setPadding(5);
                daterecbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_recbalance.addCell(daterecbal);


                PdfPTable table_currentbalance = new PdfPTable(2);
                Phrase prtcurrentbal = new Phrase("الرصيد الحالي", B10);
                prtcurrentbal.add(new Chunk("   Current Balance",B10E));
                table_currentbalance.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_currentbal = new PdfPCell(prtcurrentbal);
                cell_currentbal.setColspan(1);
                cell_currentbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_currentbal.setPadding(5.0f);
                table_currentbalance.addCell(cell_currentbal);
                prtcurrentbal = new Phrase("" + currentbal + " " + totaltype, B10E);
                PdfPCell datecurrentbal = new PdfPCell(prtcurrentbal);
                datecurrentbal.setPadding(5);
                datecurrentbal.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_currentbalance.addCell(datecurrentbal);

                document.open();

                document.add(tableh);
                document.add(table_company_name);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_oldbalance);
                document.add(table_recbalance);
                document.add(table_currentbalance);

                PdfPTable table5 = new PdfPTable(1);
                table5.setSpacingBefore(10.0f); // Space Before table starts, like
                table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                PdfPTable table4 = new PdfPTable(2);
                table4.setSpacingBefore(20.0f);
                float[] columnWidths1 = new float[]{20f, 5f};
                table4.setWidths(columnWidths1);
                table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                Phrase p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Cashier, N12);
                PdfPCell cell5 = new PdfPCell(p5);
                cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell5.setBorder(Rectangle.NO_BORDER);
                p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Manager, N12);
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
                    } catch (ActivityNotFoundException e) {
                    }
                }
            }
        } catch (Exception e) {
            f.delete();
        }
    }
    private void send_email(String strEmail) {
        try {
            String[] recipients = strEmail.split(",");
            final SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;

           // Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code = '" + Globals.strContact_Code + "'");
            Globals.AppLogWrite("settings email"+ Globals.objsettings.get_Email());
            Globals.AppLogWrite("settings Password"+ Globals.objsettings.get_Password());
            Globals.AppLogWrite("settings host"+ Globals.objsettings.get_Host());
            Globals.AppLogWrite("settings port"+ Globals.objsettings.get_Port());

            email.m = new GMailSender(Globals.objsettings.get_Email(), Globals.objsettings.get_Password(), Globals.objsettings.get_Host(), Globals.objsettings.get_Port());
            email.m.set_from(Globals.objsettings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Accounts Detail ");
            Globals.AppLogWrite("recipients"+ recipients);

            email.m.set_to(recipients);
            email.m.set_subject("Trigger POS Account Detail "+ contact.get_contact_code() );
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "PDF Report" + "/" + contact.get_contact_code() + ".pdf");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        AccountsActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(SetttingsActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
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



    private void startWhatsApp() {
        String strContct = "";
        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + contact.get_contact_code() + "'");
        if (contact == null) {
        } else {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                strContct = "91"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("114")) {

                strContct = "965"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("221")) {
                strContct = "971"+contact.get_contact_1();
            }
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" + contact.get_contact_code() + ".pdf");
        // Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_SHORT).show();
        if (contactExists(getApplicationContext(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file,getApplicationContext(),contact.get_contact_1());
                   // print_dialog();
                } catch (Exception e) {
                    Globals.AppLogWrite("Contact Exception  "+e.getMessage());
                    /// Toast.makeText(getApplicationContext(),"Exception"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        else {

            if (SaveContact()) {
                Toast.makeText(getBaseContext(), "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file,getApplicationContext(),contact.get_contact_1());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file,Context context,String contactnumbr) {
        Uri path = FileProvider.getUriForFile(context, "com.org.phomellolitepos.myfileprovider", file);
        //Uri path = Uri.fromFile(file);
        String formattedNumber =  contactnumbr;
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);

        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        if(!number.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null && cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            //  Toast.makeText(getApplicationContext(),app_installed+"App Installed",Toast.LENGTH_SHORT).show();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean SaveContact() {
        //Get text
        String szFirstname = contact.get_name(),
                szPhone = "+91" + " " + contact.get_contact_1();

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //Create a new contact entry!

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFirstname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, szFirstname) // Name of the person
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szPhone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        //INSERT PITURE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        stream.toByteArray())
                .build());

        Uri newContactUri = null;

        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
            }
        } catch (RemoteException e) {
            // error
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            newContactUri = null;
        }
        return newContactUri != null;
    }


    public void print_dialog(){

String customercode=txt_cus_code.getText().toString();
String customername=txt_cus_name.getText().toString();
String contactno=contact.get_contact_1();
String modifiedate=date.substring(0,10);
String modified_time=date.substring(11,19);
String oldbal=txt_total_amt.getText().toString();
String receivedbl= edt_pd_amt.getText().toString();
String currentbal=edt_pd_amt.getText().toString();
String paymentmethod=strPayMethod;


        if(Globals.objsettings.get_Is_File_Share().equals("true")) {
        bWhatsappFlag = "true";
        }
        else{
           bWhatsappFlag = "false";
        }
        if (Globals.PrinterType.equals("") || Globals.PrinterType.equals("0")) {
            Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).show();

                if (Globals.objLPR.getIndustry_Type().equals("1")) {
                    Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                    intent1.putExtra("whatsappFlag",bWhatsappFlag);
                    intent1.putExtra("contact_code", contact.get_contact_code());
                    startActivity(intent1);
                    finish();
                }
                else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                    Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                    intent1.putExtra("whatsappFlag",bWhatsappFlag);
                    intent1.putExtra("contact_code", contact.get_contact_code());

                    startActivity(intent1);
                    finish();
                }


                else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                    Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                    intent1.putExtra("whatsappFlag",bWhatsappFlag);
                    intent1.putExtra("contact_code", contact.get_contact_code());
                    startActivity(intent1);
                    finish();
                }

        } else {
            try {

                if (is_directPrint.equals("true")) {

                    print(customercode,customername,contactno,modifiedate,modified_time,paymentmethod,oldbal,receivedbl,currentbal);

                    Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).

                            show();

                    if (Globals.objLPR.getIndustry_Type().equals("1")) {
                        Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                        intent1.putExtra("contact_code", contact.get_contact_code());
                        startActivity(intent1);
                        finish();
                    }
                    else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                        Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                        intent1.putExtra("contact_code", contact.get_contact_code());

                        startActivity(intent1);
                        finish();
                    }
                    else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                        Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                        intent1.putExtra("contact_code", contact.get_contact_code());
                        startActivity(intent1);
                        finish();
                    }

                } else {


                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(AccountsActivity.this);

                    alertDialog.setTitle(R.string.accounts);
                    alertDialog.setMessage(R.string.acct_alertmsg);
                    alertDialog.setIcon(R.drawable.delete);

                    alertDialog.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                print(customercode,customername,contactno,modifiedate,modified_time,paymentmethod,oldbal,receivedbl,currentbal);

                                    Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).

                                            show();

                                    if (Globals.objLPR.getIndustry_Type().equals("1")) {
                                        Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                        intent1.putExtra("contact_code", contact.get_contact_code());
                                        startActivity(intent1);
                                        finish();
                                    }
                                    else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                        Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                        intent1.putExtra("contact_code", contact.get_contact_code());

                                        startActivity(intent1);
                                        finish();
                                    }
                                    else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                                        Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                                        intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                        intent1.putExtra("contact_code", contact.get_contact_code());
                                        startActivity(intent1);
                                        finish();
                                    }

                              /*  Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).

                                        show();
                                Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                startActivity(intent1);
                                finish();*/

                  /*              if (Globals.objsettings.get_Is_File_Share().equals("true")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                    share_dialog();
                                                }
                                            });

                                        }
                                    });

                                }
                                else {
                                    if (Globals.objLPR.getIndustry_Type().equals("1")) {
                                        Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                        startActivity(intent1);
                                        finish();
                                    } else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                                        Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                }*/
                            } catch (Exception e) {

                            }
                        }
                    });


                    alertDialog.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            Toast.makeText(AccountsActivity.this, getString(R.string.transactionsucc), Toast.LENGTH_SHORT).

                                    show();
                            if (Globals.objLPR.getIndustry_Type().equals("1")) {
                                Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                intent1.putExtra("contact_code", contact.get_contact_code());

                                startActivity(intent1);
                                finish();
                            }
                           else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                intent1.putExtra("contact_code", contact.get_contact_code());

                                startActivity(intent1);
                                finish();
                            }
                            else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                                Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                                intent1.putExtra("whatsappFlag",bWhatsappFlag);
                                intent1.putExtra("contact_code", contact.get_contact_code());

                                startActivity(intent1);
                                finish();
                            }
/*                            if (Globals.objsettings.get_Is_File_Share().equals("true")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                share_dialog();
                                            }
                                        });

                                    }
                                });

                            } else {
                                if (Globals.objLPR.getIndustry_Type().equals("1")) {
                                    Intent intent1 = new Intent(AccountsActivity.this, AccountsListActivity.class);
                                    startActivity(intent1);
                                    finish();
                                } else if (Globals.objLPR.getIndustry_Type().equals("3")) {
                                    Intent intent1 = new Intent(AccountsActivity.this, PaymentCollection_MainScreen.class);
                                    startActivity(intent1);
                                    finish();
                                }
                            }*/
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                    // give dialgo

                    // yes  call print
                }


            } catch (Exception e) {

            }




        }
    }


   public void share_dialog(){
       final Dialog listDialog2 = new Dialog(AccountsActivity.this);
       listDialog2.setTitle("File Share");
       LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
       listDialog2.setContentView(v1);
       listDialog2.setCancelable(false);
       final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
       edt_remark.setVisibility(View.GONE);
       Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
       btnButton.setText("Share");
       Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
       listDialog2.show();
       btnClear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              // print_dialog();

           }
       });
       btnButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               runOnUiThread(new Runnable() {
                   public void run() {

                       //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                       startWhatsApp();

                   }
               });

               listDialog2.dismiss();

           }
       });
   }

    private  void pdfPerform_80mm() {
        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + contact.get_contact_code()+"80mm" + ".pdf");
            OutputStream file = new FileOutputStream(f);
           // Rectangle pagesize = new Rectangle(370, 600);
           Document document = new Document(PageSize.B7);
          //  document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
          /*  writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));*/
            Image image = null;
//            image = createimage();

            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);


                // Second parameter is the number of the chapter


                Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10);
                subPara.setAlignment(Element.ALIGN_CENTER);
                Paragraph subParaacc = new Paragraph(Globals.objLPR.getAddress(), B12);
                subParaacc.setAlignment(Element.ALIGN_CENTER);
                Paragraph subParamobile= new Paragraph(Globals.objLPR.getMobile_No(), B12);
                subParamobile.setAlignment(Element.ALIGN_CENTER);
                DottedLineSeparator dottedline = new DottedLineSeparator();
                dottedline.setOffset(-2);
                dottedline.setGap(2f);
                subParamobile.add(dottedline);
                //("..........................................................", B10);
                Paragraph subPara1 = new Paragraph("Payment Receipt", B10);
                subPara1.setAlignment(Element.ALIGN_CENTER);
                DottedLineSeparator dottedline1 = new DottedLineSeparator();
                dottedline1.setOffset(-2);
                dottedline1.setGap(2f);
                subPara1.add(dottedline1);
             //   Paragraph subParal2 = new Paragraph("..........................................................", B10);
       /* Section subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Payment Receipt"));
       createList(subCatPart);*/

                List list = new List(false, false, 0);

                // List list = new List(List.UNORDERED);
                list.add(new Chunk(""));
                list.add(new Chunk(""));
                //text2.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
                //list.add(new ListItem(new Chunk("Company Name" + " : " + Globals.objLPR.getCompany_Name(), N9)));

                list.add(new ListItem(new Chunk("Customer Code " +  "      " + txt_cus_code.getText().toString(), N9)));
                list.add(new ListItem(new Chunk("Customer Name " +  "      " + txt_cus_name.getText().toString(), N9)));
                list.add(new ListItem(new Chunk("Contact No " +  "             " + contact.get_contact_1().toString(), N9)));

                list.add(new ListItem(new Chunk("Date" +  "                        " + date.substring(0, 10), N9)));
                list.add(new ListItem(new Chunk("Time" +  "                       " + date.substring(11, 19), N9)));
                list.add(new ListItem(new Chunk(Globals.PrintDeviceID +  "                "  + Globals.objLPD.getDevice_Name(), N9)));
                list.add(new ListItem(new Chunk("Payment Mode" +  "        " + strPayMethod, N9)));
                list.add(new ListItem(new Chunk("Old Balance " +  "            " + Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check), N9)));
                list.add(new ListItem(new Chunk("Received" + "                 " + Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check), N9)));
                Double ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString());
                double abs1 = Math.abs(ab);
                if (ab > 0) {
                    list.add(new ListItem(new Chunk("Current Balance" +  "       " + Globals.myNumberFormat2Price(abs1, decimal_check) + " CR", N9)));
                } else {
                    list.add(new ListItem(new Chunk("Current Balance" + "        " + Globals.myNumberFormat2Price(abs1, decimal_check) + " DR", N9)));
                }

                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                list.add(new ListItem(new Chunk("Signature" +  "                 " +user.get_name(), N9)));


                document.open();

                document.add(subPara);
                document.add(subParaacc);
                document.add(subParamobile);
             //   document.add(subParal1);
                document.add(subPara1);
            //    document.add(subParal2);
                document.add(list);
                document.newPage();
                document.close();
                file.close();
            }
            else if(Globals.objsettings.get_Print_Lang().equals("1")){
                String strString = "";
                int strLength = 25;
                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                document.open();
                Paragraph subParaacc = new Paragraph("حسابات", B12);
                subParaacc.setAlignment(Element.ALIGN_CENTER);
                Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10E);
                subPara.setAlignment(Element.ALIGN_CENTER);
                Paragraph subPara1 = new Paragraph("إيصال الدفع", B10);
                subPara1.setAlignment(Element.ALIGN_CENTER);

                document.add(subParaacc);
                document.add(subPara);
                document.add(subPara1);
                document.add(new Chunk(Chunk.NEWLINE));
                strString = Globals.myRequiredString(Globals.objLPR.getCompany_Name(), strLength);

                Phrase p = new Phrase(strString + ": ",B10E);

                p.add(new Chunk("اسم الشركة", B10));
               p.add(new Chunk(Chunk.NEWLINE));
                document.add(p);
                ColumnText canvas = new ColumnText(writer.getDirectContent());

                canvas.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas.setSimpleColumn(36, 750, 559, 780);

                canvas.setAlignment(Element.ALIGN_RIGHT);
                canvas.addElement(p);
                canvas.addElement(new Chunk(Chunk.NEWLINE));
                canvas.go();

                strString = Globals.myRequiredString(txt_cus_code.getText().toString() , strLength);

                Phrase p1 = new Phrase(strString + ": ",B10E);
                p1.add(new Chunk("بطاقة العميل", B10));
                p1.add(new Chunk(Chunk.NEWLINE));
                document.add(p1);

                ColumnText canvas1 = new ColumnText(writer.getDirectContent());
                canvas1.setSimpleColumn(36, 750, 559, 780);

                canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas1 .setAlignment(Element.ALIGN_RIGHT);
                canvas1.addElement(p1);
                canvas1.addElement(new Chunk(Chunk.NEWLINE));
                canvas1.go();
                strString = Globals.myRequiredString(txt_cus_name.getText().toString() , strLength);

                Phrase p2 = new Phrase(strString + ": ",B10E);


                p2.add(new Chunk("رقم الاتصال", B10));

                p2.add(new Chunk(Chunk.NEWLINE));
                document.add(p2);
                ColumnText canvas2 = new ColumnText(writer.getDirectContent());
                canvas2.setSimpleColumn(36, 750, 559, 780);
                canvas2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas2 .setAlignment(Element.ALIGN_RIGHT);
                canvas2.addElement(p2);
                canvas2.addElement(new Chunk(Chunk.NEWLINE));
                canvas2.go();
                strString = Globals.myRequiredString(contact.get_contact_1() , strLength);

                Phrase p3 = new Phrase(strString + ": ",B10E);

                p3.add(new Chunk("رقم الاتصال", B10));

                p3.add(new Chunk(Chunk.NEWLINE));
                document.add(p3);
                ColumnText canvas3 = new ColumnText(writer.getDirectContent());
                canvas3.setSimpleColumn(36, 750, 559, 780);
                canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas3 .setAlignment(Element.ALIGN_RIGHT);
                canvas3.addElement(p2);
                canvas3.addElement(new Chunk(Chunk.NEWLINE));
                canvas3.go();

               /* Phrase p4 = new Phrase(contact.get_contact_1() + ": ",B10E);
                strString = Globals.myRequiredString("رقم الاتصال" , strLength);

                p4.add(new Chunk(strString, B10));
                p4.add(new Chunk(Chunk.NEWLINE));
                document.add(p4);
                ColumnText canvas4 = new ColumnText(writer.getDirectContent());
                canvas4.setSimpleColumn(36, 750, 559, 780);
                canvas4.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas4 .setAlignment(Element.ALIGN_LEFT);
                canvas4.addElement(p4);
                canvas4.addElement(new Chunk(Chunk.NEWLINE));
                canvas4.go();
*/
                strString = Globals.myRequiredString(date.substring(0, 10) , strLength);

                Phrase p5 = new Phrase(strString + ": ",B10E);
                p5.add(new Chunk("تاريخ", B10));
                p5.add(new Chunk(Chunk.NEWLINE));
                document.add(p5);

                ColumnText canvas5 = new ColumnText(writer.getDirectContent());
                canvas5.setSimpleColumn(36, 750, 559, 780);
                canvas5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas5 .setAlignment(Element.ALIGN_RIGHT);
                canvas5.addElement(p5);
                canvas5.addElement(new Chunk(Chunk.NEWLINE));
                canvas5.go();

                strString = Globals.myRequiredString(date.substring(11, 19) , strLength);

                Phrase p6 = new Phrase(strString + ": ",B10E);
                p6.add(new Chunk("زمن", B10));
                p6.add(new Chunk(Chunk.NEWLINE));
                document.add(p6);

                ColumnText canvas6 = new ColumnText(writer.getDirectContent());
                canvas6.setSimpleColumn(36, 750, 559, 780);
                canvas6.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas6 .setAlignment(Element.ALIGN_RIGHT);
                canvas6.addElement(p6);
                canvas6.addElement(new Chunk(Chunk.NEWLINE));
                canvas6.go();
                strString = Globals.myRequiredString(Globals.objLPD.getDevice_Name() , strLength);

                Phrase p7 = new Phrase(strString + ": ",B10E);
                p7.add(new Chunk("معرف الجهاز", B10));
                p7.add(new Chunk(Chunk.NEWLINE));
                document.add(p7);

                ColumnText canvas7 = new ColumnText(writer.getDirectContent());
                canvas7.setSimpleColumn(36, 750, 559, 780);
                canvas7.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas7 .setAlignment(Element.ALIGN_RIGHT);
                canvas7.addElement(p7);
                canvas7.addElement(new Chunk(Chunk.NEWLINE));
                canvas7.go();
                strString = Globals.myRequiredString(strPayMethod , strLength);

                Phrase p8 = new Phrase(strString + ": ",B10E);
                p8.add(new Chunk("موازنة القديم", B10));
                p8.add(new Chunk(Chunk.NEWLINE));
                document.add(p8);

                ColumnText canvas8 = new ColumnText(writer.getDirectContent());
                canvas8.setSimpleColumn(36, 750, 559, 780);
                canvas8.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas8 .setAlignment(Element.ALIGN_RIGHT);
                canvas8.addElement(p8);
                canvas8.addElement(new Chunk(Chunk.NEWLINE));
                canvas8.go();
                strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) , strLength);

                Phrase p9 = new Phrase(strString + ": ",B10E);
                p9.add(new Chunk("موازنة القديم", B10));
                p9.add(new Chunk(Chunk.NEWLINE));
                document.add(p9);

                ColumnText canvas9 = new ColumnText(writer.getDirectContent());
                canvas9.setSimpleColumn(36, 750, 559, 780);
                canvas9.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas7 .setAlignment(Element.ALIGN_RIGHT);
                canvas9.addElement(p9);
                canvas9.addElement(new Chunk(Chunk.NEWLINE));
                canvas9.go();

                strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) , strLength);

                Phrase p10 = new Phrase(strString + ": ",B10E);
                p10.add(new Chunk("حصل موازنة", B10));
                p10.add(new Chunk(Chunk.NEWLINE));
                document.add(p10);

                ColumnText canvas10 = new ColumnText(writer.getDirectContent());
                canvas10.setSimpleColumn(36, 750, 559, 780);
                canvas10.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas10.addElement(p10);
                canvas10.addElement(new Chunk(Chunk.NEWLINE));
                canvas10.go();


                Double ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString());
                double abs1 = Math.abs(ab);
                Phrase p11;

                if (ab > 0) {
                    strString = Globals.myRequiredString(Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" , strLength);

                    p11 = new Phrase(strString + ": ", B10E);
                }
                else{
                    strString = Globals.myRequiredString(Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" , strLength);

                    p11 = new Phrase(strString + ": ", B10E);

                }
                p11.add(new Chunk("الرصيد الحالي", B10));
                p11.add(new Chunk(Chunk.NEWLINE));
                document.add(p11);

                ColumnText canvas11 = new ColumnText(writer.getDirectContent());
                canvas11.setSimpleColumn(36, 750, 559, 780);
                canvas11.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas11.addElement(p11);
                canvas11.addElement(new Chunk(Chunk.NEWLINE));
                canvas11.go();


                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                strString = Globals.myRequiredString(user.get_name() , strLength);

                Phrase p12 = new Phrase(strString + ": ",B10E);
                p12.add(new Chunk("التوقيع", B10));
                p12.add(new Chunk(Chunk.NEWLINE));
                document.add(p12);

                ColumnText canvas12 = new ColumnText(writer.getDirectContent());
                canvas12.setSimpleColumn(36, 750, 559, 780);
                canvas12.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas12.addElement(p12);
                canvas12.addElement(new Chunk(Chunk.NEWLINE));
                canvas12.go();
                document.close();
            }


            else if(Globals.objsettings.get_Print_Lang().equals("2")){
                String strString = "";
                int strLength = 14;
                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 8, Font.NORMAL);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 9, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                document.open();
                Paragraph subParaacc = new Paragraph("حسابات", B12);
                subParaacc.setAlignment(Element.ALIGN_CENTER);
                Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10E);
                subPara.setAlignment(Element.ALIGN_CENTER);
                Paragraph subPara1 = new Paragraph("إيصال الدفع", B10);
                subPara1.setAlignment(Element.ALIGN_CENTER);

                document.add(subParaacc);
                document.add(subPara);
                document.add(subPara1);
                document.add(new Chunk(Chunk.NEWLINE));
                strString = Globals.myRequiredString(Globals.objLPR.getCompany_Name(), strLength);

                Phrase p = new Phrase("" + ": ",B10E);

                p.add(new Chunk("اسم الشركة", B10));
                p.add(new Chunk("   Company Name" , B10E));
                p.add(new Chunk(Chunk.NEWLINE));
                p.add(new Chunk(strString,B10E));

                document.add(p);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas = new ColumnText(writer.getDirectContent());

                canvas.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas.setSimpleColumn(36, 750, 559, 780);

                canvas.setAlignment(Element.ALIGN_RIGHT);
                canvas.addElement(p);
                canvas.addElement(new Chunk(Chunk.NEWLINE));
                canvas.go();

                strString = Globals.myRequiredString(txt_cus_code.getText().toString() , strLength);

                Phrase p1 = new Phrase("" ,B10E);
                p1.add(new Chunk("بطاقة العميل", B10));
                p1.add(new Chunk("   Customer Code :", B10E));
                p1.add(new Chunk(Chunk.NEWLINE));
                p1.add(new Chunk(strString,B10E));

                //p1.add(new Chunk(strString, B10));
                document.add(p1);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas1 = new ColumnText(writer.getDirectContent());
                canvas1.setSimpleColumn(36, 750, 559, 780);

                canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas1 .setAlignment(Element.ALIGN_RIGHT);
                canvas1.addElement(p1);
                canvas1.addElement(new Chunk(Chunk.NEWLINE));
                canvas1.go();
                strString = Globals.myRequiredString(txt_cus_name.getText().toString() , strLength);

                Phrase p2 = new Phrase("" ,B10E);


                p2.add(new Chunk("اسم الزبون", B10));
                p2.add(new Chunk("   Customer Name :", B10E));
                p2.add(new Chunk(Chunk.NEWLINE));
                p2.add(new Chunk(strString,B10E));
                document.add(p2);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas2 = new ColumnText(writer.getDirectContent());
                canvas2.setSimpleColumn(36, 750, 559, 780);
                canvas2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas2 .setAlignment(Element.ALIGN_RIGHT);
                canvas2.addElement(p2);
                canvas2.addElement(new Chunk(Chunk.NEWLINE));
                canvas2.go();
                strString = Globals.myRequiredString(contact.get_contact_1() , strLength);

                Phrase p3 = new Phrase("" ,B10E);

                p3.add(new Chunk("رقم الاتصال", B10));
                p3.add(new Chunk("   Contact No :", B10E));
                p3.add(new Chunk(Chunk.NEWLINE));
                p3.add(new Chunk(strString,B10E));
                document.add(p3);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas3 = new ColumnText(writer.getDirectContent());
                canvas3.setSimpleColumn(36, 750, 559, 780);
                canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas3 .setAlignment(Element.ALIGN_RIGHT);
                canvas3.addElement(p2);
                canvas3.addElement(new Chunk(Chunk.NEWLINE));
                canvas3.go();

               /* Phrase p4 = new Phrase(contact.get_contact_1() + ": ",B10E);
                strString = Globals.myRequiredString("رقم الاتصال" , strLength);

                p4.add(new Chunk(strString, B10));
                p4.add(new Chunk(Chunk.NEWLINE));
                document.add(p4);
                ColumnText canvas4 = new ColumnText(writer.getDirectContent());
                canvas4.setSimpleColumn(36, 750, 559, 780);
                canvas4.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas4 .setAlignment(Element.ALIGN_LEFT);
                canvas4.addElement(p4);
                canvas4.addElement(new Chunk(Chunk.NEWLINE));
                canvas4.go();
*/
                strString = Globals.myRequiredString(date.substring(0, 10) , strLength);

                Phrase p5 = new Phrase("" ,B10E);
                p5.add(new Chunk("تاريخ", B10));
                p5.add(new Chunk("   Date :", B10E));
                p5.add(new Chunk(Chunk.NEWLINE));
                p5.add(new Chunk(strString,B10E));
                document.add(p5);
                document.add(new Chunk(Chunk.NEWLINE));


                ColumnText canvas5 = new ColumnText(writer.getDirectContent());
                canvas5.setSimpleColumn(36, 750, 559, 780);
                canvas5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas5 .setAlignment(Element.ALIGN_RIGHT);
                canvas5.addElement(p5);
                canvas5.addElement(new Chunk(Chunk.NEWLINE));
                canvas5.go();

                strString = Globals.myRequiredString(date.substring(11, 19) , strLength);

                Phrase p6 = new Phrase("",B10E);
                p6.add(new Chunk("زمن", B10));
                p6.add(new Chunk("   Time :", B10E));
                p6.add(new Chunk(Chunk.NEWLINE));
                p6.add(new Chunk(strString,B10E));
                document.add(p6);
                document.add(new Chunk(Chunk.NEWLINE));

                ColumnText canvas6 = new ColumnText(writer.getDirectContent());
                canvas6.setSimpleColumn(36, 750, 559, 780);
                canvas6.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas6 .setAlignment(Element.ALIGN_RIGHT);
                canvas6.addElement(p6);
                canvas6.addElement(new Chunk(Chunk.NEWLINE));
                canvas6.go();
                strString = Globals.myRequiredString(Globals.objLPD.getDevice_Name() , strLength);

                Phrase p7 = new Phrase("" ,B10E);
                p7.add(new Chunk("معرف الجهاز", B10));
                p7.add(new Chunk(   Globals.PrintDeviceID +":", B10E));
                p7.add(new Chunk(Chunk.NEWLINE));
                p7.add(new Chunk(strString,B10E));
                document.add(p7);
                document.add(new Chunk(Chunk.NEWLINE));

                ColumnText canvas7 = new ColumnText(writer.getDirectContent());
                canvas7.setSimpleColumn(36, 750, 559, 780);
                canvas7.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas7 .setAlignment(Element.ALIGN_RIGHT);
                canvas7.addElement(p7);
                canvas7.addElement(new Chunk(Chunk.NEWLINE));
                canvas7.go();
                strString = Globals.myRequiredString(strPayMethod , strLength);

                Phrase p8 = new Phrase("" ,B10E);
                p8.add(new Chunk("الدفع نوع", B10));
                p8.add(new Chunk("   Payment Mode :", B10E));
                p8.add(new Chunk(Chunk.NEWLINE));
                p8.add(new Chunk(strString,B10E));
                document.add(p8);
                document.add(new Chunk(Chunk.NEWLINE));

                ColumnText canvas8 = new ColumnText(writer.getDirectContent());
                canvas8.setSimpleColumn(36, 750, 559, 780);
                canvas8.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas8 .setAlignment(Element.ALIGN_RIGHT);
                canvas8.addElement(p8);
                canvas8.addElement(new Chunk(Chunk.NEWLINE));
                canvas8.go();
                strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check) , strLength);

                Phrase p9 = new Phrase("" ,B10E);
                p9.add(new Chunk("موازنة القديم", B10));
                p9.add(new Chunk("  Old Balance :", B10E));
                p9.add(new Chunk(Chunk.NEWLINE));
                p9.add(new Chunk(strString,B10E));
                document.add(p9);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas9 = new ColumnText(writer.getDirectContent());
                canvas9.setSimpleColumn(36, 750, 559, 780);
                canvas9.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas7 .setAlignment(Element.ALIGN_RIGHT);
                canvas9.addElement(p9);
                canvas9.addElement(new Chunk(Chunk.NEWLINE));
                canvas9.go();

                strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check) , strLength);

                Phrase p10 = new Phrase("" ,B10E);
                p10.add(new Chunk("حصل موازنة", B10));
                p10.add(new Chunk("   Received :", B10E));
                p10.add(new Chunk(Chunk.NEWLINE));
                p10.add(new Chunk(strString,B10E));
                document.add(p10);
                document.add(new Chunk(Chunk.NEWLINE));

                ColumnText canvas10 = new ColumnText(writer.getDirectContent());
                canvas10.setSimpleColumn(36, 750, 559, 780);
                canvas10.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas10.addElement(p10);
                canvas10.addElement(new Chunk(Chunk.NEWLINE));
                canvas10.go();


                Double ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString());
                double abs1 = Math.abs(ab);
                Phrase p11;

                if (ab > 0) {
                    strString = Globals.myRequiredString(Globals.myNumberFormat2Price(abs1, decimal_check) + " CR" , strLength);

                    p11 = new Phrase("" , B10E);
                }
                else{
                    strString = Globals.myRequiredString(Globals.myNumberFormat2Price(abs1, decimal_check) + " DR" , strLength);

                    p11 = new Phrase("", B10E);

                }
                p11.add(new Chunk("الرصيد الحالي", B10));
                p11.add(new Chunk("   Current Balance :", B10E));
                p11.add(new Chunk(Chunk.NEWLINE));
                p11.add(new Chunk(strString ,B10E));
                document.add(p11);
                document.add(new Chunk(Chunk.NEWLINE));

                ColumnText canvas11 = new ColumnText(writer.getDirectContent());
                canvas11.setSimpleColumn(36, 750, 559, 780);
                canvas11.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas11.addElement(p11);
                canvas11.addElement(new Chunk(Chunk.NEWLINE));
                canvas11.go();


                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                strString = Globals.myRequiredString(user.get_name() , strLength);

                Phrase p12 = new Phrase("" ,B10E);
                p12.add(new Chunk("التوقيع", B10));
                p12.add(new Chunk("/Signature :", B10E));
                p12.add(new Chunk(Chunk.NEWLINE));
                p12.add(new Chunk(strString,B10E));
                document.add(p12);
                p12.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas12 = new ColumnText(writer.getDirectContent());
                canvas12.setSimpleColumn(36, 750, 559, 780);
                canvas12.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                canvas12.addElement(p12);
                canvas12.addElement(new Chunk(Chunk.NEWLINE));
                canvas12.go();
                document.close();
            }
    } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class DottedCell implements PdfPCellEvent {

        @Override
        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.setLineDash(3f, 3f);
            canvas.rectangle(position.getLeft(), position.getBottom(),
                    position.getWidth(), position.getHeight());
            canvas.stroke();

        }
    }
}