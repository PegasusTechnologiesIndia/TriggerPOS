package org.phomellolitepos;

import android.Manifest;
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
import android.graphics.Matrix;
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
import android.provider.ContactsContract;
import androidx.annotation.RequiresApi;

import com.basewin.define.FontsType;
import com.basewin.services.ServiceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.services.PrinterBinder;
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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.CusReturnFinalListAdapter;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Return_Item_Tax;
import org.phomellolitepos.database.Return_detail;
import org.phomellolitepos.database.Return_detail_tax;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Master;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CusReturnFinalActivity extends AppCompatActivity {
    EditText edt_name, edt_qty, edt_price;
    AutoCompleteTextView edt_toolbar_item_code;
    Button btn_add;
    ListView list;
    Contact  contact;
    private ProgressDialog dialog;
    LongOperation tsk;
    String strItemName="";
    Item item;
    private TimerCountTools timeTools;
    JSONObject printJson = new JSONObject();
    private CusReturnFinalActivity.PrinterListener printer_callback = new CusReturnFinalActivity.PrinterListener();
    public static PrinterBinder printer;
    String operation, str_voucher_no, str_date, str_remarks;
    Database db;
    SQLiteDatabase database;
    ArrayList<String> list1a, list2a, list3a, list4a;
    Button btn_qty,btn_price;
    ProgressDialog pDialog;
    BottomNavigationView bottomNavigationView;
    String[] invFlag = {};
    ArrayList<Item> arrayListItem;
    ArrayList<StockAdjectmentDetailList> arraylist = new ArrayList<StockAdjectmentDetailList>();
    ArrayList<Return_detail> arrayListReturn_detail;
    String sale_priceStr, decimal_check, qty_decimal_check, str_inv;
    Item resultp;
    String item_code, date;
    CusReturnFinalListAdapter returnFinalListAdapter;
    Returns returns;
    Return_detail return_detail;
    String strupdate = "", strItemCode, cusCode, PayId;
    int Position;
    Lite_POS_Registration lite_pos_registration;
    Sys_Tax_Group sys_tax_group;
    ArrayList<String> arrayList1;
    //private String PrinterType = "";
    private String is_directPrint = "";
    private IWoyouService woyouService;
    //Settings settings;
    private ICallback callback = null;
    User user;
    private boolean iswifi = false;
    private ArrayList<String> mylist = new ArrayList<String>();
    private MyAdapter adp;
    private int order, noofPrint = 0, lang = 0, pos = 0;
    ArrayList<Return_detail> return_details;

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
    private static final String TAG = "PrinterTestDemo";
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
    BluetoothService mService = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_final);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invFlag = getResources().getStringArray(R.array.Inv_flag);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        mService = MainActivity.mService;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        Intent intent = getIntent();
        getSupportActionBar().setTitle("");
        try {
            operation = intent.getStringExtra("operation");
            str_voucher_no = intent.getStringExtra("voucher_no");
            str_date = intent.getStringExtra("date");
            str_remarks = intent.getStringExtra("remarks");
            cusCode = intent.getStringExtra("contact_code");
            PayId = intent.getStringExtra("payment_id");

            Globals.strContact_Code=cusCode;
        } catch (Exception e) {

        }
        btn_qty=(Button)findViewById(R.id.btn_Qty);
        btn_price=(Button)findViewById(R.id.btn_Item_Price);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(CusReturnFinalActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(100);
                            pDialog.dismiss();
                            if (!operation.equals("Edit")) {
                                Globals.ReturnTotalPrice = 0.0;
                                Globals.ReturnTotalQty = 0.0;
                            }


                            Intent intent = new Intent(CusReturnFinalActivity.this, CusReturnHeaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
                            intent.putExtra("contact_code", cusCode);
                            startActivity(intent);
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


        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
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
        /*device_id = telephonyManager.getDeviceId();
        imei_no = telephonyManager.getImei();*/

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.retail_bottom_navigation);
        edt_toolbar_item_code = (AutoCompleteTextView) findViewById(R.id.edt_toolbar_item_code);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_qty = (EditText) findViewById(R.id.edt_qty);
        edt_price = (EditText) findViewById(R.id.edt_price);
        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                alertDialog.setTitle(edt_name.getText().toString());
                alertDialog.setMessage(getString(R.string.aresuredlete));
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        arraylist.remove(position);
                        returnFinalListAdapter.notifyDataSetChanged();
                        edt_name.setText("");
                        edt_qty.setText("");
                        edt_price.setText("");
                    }
                });


                alertDialog.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
                return true;
            }
        });

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
                        //info.append("onRaiseException = " + msg + "\n");
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
        intent = new Intent();
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
            Intent intent1 = new Intent();
            intent1.setPackage("woyou.aidlservice.jiuiv5");
            intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
            startService(intent1);
            bindService(intent1, connService, Context.BIND_AUTO_CREATE);

        } catch (Exception ex) {
        }

        try {

            is_directPrint = Globals.objsettings.get_Is_Print_Dialog_Show();
        } catch (Exception ex) {

            is_directPrint = "";
        }
        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();

        edt_toolbar_item_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_toolbar_item_code.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_item_code.requestFocus();
                    edt_toolbar_item_code.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_item_code, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_item_code.selectAll();
                    return true;
                }
            }
        });

        edt_toolbar_item_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strFilter = edt_toolbar_item_code.getText().toString();
                autocomplete(strFilter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_toolbar_item_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        edt_toolbar_item_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //  final Settings settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = Globals.objsettings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        if (returns == null) {
        } else {
            if (returns.get_is_post().equals("true") || returns.get_is_cancel().equals("true")) {
                Menu menu = bottomNavigationView.getMenu();
                MenuItem cancel = menu.findItem(R.id.action_cancel);
                MenuItem delete = menu.findItem(R.id.action_delete);
                MenuItem post = menu.findItem(R.id.action_post);
                MenuItem save = menu.findItem(R.id.action_save);
                //  MenuItem print = menu.findItem(R.id.action_print);
                cancel.setEnabled(false);
                delete.setEnabled(false);
                post.setEnabled(false);
                save.setEnabled(false);

                btn_add.setEnabled(false);
            }


            if (returns.get_is_cancel().equals("true")) {
                Menu menu = bottomNavigationView.getMenu();
                MenuItem print = menu.findItem(R.id.action_print);
                print.setEnabled(false);
            }

           /* if(returns.get_is_post().equals("true")){
                Menu menu = bottomNavigationView.getMenu();
                MenuItem print = menu.findItem(R.id.action_print);
                print.setEnabled(true);
            }*/
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

        if (operation.equals("Edit")) {
            try {
                arrayListReturn_detail = Return_detail.getAllReturn_detail(getApplicationContext(), " where ref_voucher_no ='" + str_voucher_no + "' ", database);
                if (arrayListReturn_detail.size() > 0) {
                    String inv;
                    for (int i = 0; i < arrayListReturn_detail.size(); i++) {
                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + arrayListReturn_detail.get(i).get_item_code() + "'", database, db);

                        StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", arrayListReturn_detail.get(i).get_item_code(), arrayListReturn_detail.get(i).get_qty(), "", item.get_item_name(), arrayListReturn_detail.get(i).get_price(), arrayListReturn_detail.get(i).get_line_total());
                        arraylist.add(stockAdjectmentDetailList);
                    }
                    try {
                        list_load(arraylist);
                        String strQuantity=Globals.myNumberFormat2Price(Globals.ReturnTotalQty, decimal_check);
                        String strPrice=Globals.myNumberFormat2Price(Globals.ReturnTotalPrice, decimal_check);

                        btn_qty.setText(strQuantity);
                        btn_price.setText(strPrice);
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }
        }

        edt_qty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_qty.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_qty.requestFocus();
                    edt_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_qty, InputMethodManager.SHOW_IMPLICIT);
                    edt_qty.selectAll();
                    return true;
                }
            }
        });

        edt_price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_price.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_price.requestFocus();
                    edt_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_price, InputMethodManager.SHOW_IMPLICIT);
                    edt_price.selectAll();
                    return true;
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "No item selected", Toast.LENGTH_SHORT).show();
                } else {
                    if (edt_qty.getText().toString().trim().equals("")) {
                        edt_qty.setError("Enter Quantity");
                        return;
                    }
                    if (edt_price.getText().toString().trim().equals("")) {
                        edt_price.setError("Enter Price");
                        return;
                    }
                    closeKeyboard();
                    if (strupdate.equals("update")) {
                        StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", strItemCode, edt_qty.getText().toString().trim(), str_inv, edt_name.getText().toString().trim(), edt_price.getText().toString().trim(), (Double.parseDouble(edt_qty.getText().toString().trim()) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                        arraylist.remove(Position);
                        arraylist.add(Position, stockAdjectmentDetailList);
                        list_load(arraylist);
                    } else {
                        if (Globals.objsettings.get_Is_Stock_Manager().equals("true")) {
                            Item_Location item_location = Item_Location.getItem_Location(getApplication(), "where item_code='" + item_code + "'", database);
                            if (item_location != null) {
                                int count = 0;
                                boolean bFound = false;

                                while (count < arraylist.size()) {
                                    if (resultp.get_item_code().equals(arraylist.get(count).getItem_code())) {
                                        bFound = true;
                                        arraylist.get(count).setQty(((Integer.parseInt(arraylist.get(count).getQty())) + Integer.parseInt(edt_qty.getText().toString().trim())) + "");
                                        arraylist.get(count).setLine_total(((Double.parseDouble(arraylist.get(count).getQty())) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                                    }
                                    count = count + 1;
                                }

                                if (!bFound) {
                                    StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", item_code, edt_qty.getText().toString().trim(), str_inv, resultp.get_item_name(), edt_price.getText().toString().trim(), (Double.parseDouble(edt_qty.getText().toString().trim()) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                                    arraylist.add(stockAdjectmentDetailList);

                                }
                                list_load(arraylist);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.itemstcck), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            int count = 0;
                            boolean bFound = false;

                            while (count < arraylist.size()) {
                                if (resultp.get_item_code().equals(arraylist.get(count).getItem_code())) {
                                    bFound = true;
                                    arraylist.get(count).setQty(((Integer.parseInt(arraylist.get(count).getQty())) + Integer.parseInt(edt_qty.getText().toString().trim())) + "");
                                    arraylist.get(count).setLine_total(((Double.parseDouble(arraylist.get(count).getQty())) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                                }
                                count = count + 1;
                            }

                            if (!bFound) {
                                StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", item_code, edt_qty.getText().toString().trim(), str_inv, resultp.get_item_name(), edt_price.getText().toString().trim(), (Double.parseDouble(edt_qty.getText().toString().trim()) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                                arraylist.add(stockAdjectmentDetailList);

                            }
                            list_load(arraylist);
                           // setQuantityPrice(arraylist.get(count).getQty(),arraylist.get(count).getPrice());
                        }
                    }
                    edt_name.setText("");
                    edt_price.setText("");
                    edt_qty.setText("");
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_post:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(CusReturnFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread1 = new Thread() {
                                        public void run() {
                                            String result = stock_save();


                 /* runOnUiThread(new Runnable() {
                              @Override
                    public void run() {
                         share_dialog();
                     }
                  });*/


                                            //  String contactCode = contact.get_contact_code();
                                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
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


  /*                                          runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    try {
                                                        sleep(3000);



                                                        if (Globals.objsettings.get_Is_File_Share().equals("true")) {



                                                            final Dialog listDialog2 = new Dialog(CusReturnFinalActivity.this);
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
                                                                    finish();
                                                                }
                                                            });
                                                            btnButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {



                                                                    //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                                                                    startWhatsApp();



                                                                    listDialog2.dismiss();

                                                                }
                                                            });


                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }


                                            });*/



                                            if (result.equals("1")) {


                                                if (lite_pos_registration.getproject_id().equals("standalone")) {
                                                    String rsultPost = stock_post();
                                                    if (rsultPost.equals("1")) {
                                                        if (Globals.objsettings.get_Is_Stock_Manager().equals("true")) {
                                                            String rsultUpdate = stock_update();
                                                        }

                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                try {

                                                                    if (Globals.PrinterType.equals("0")) {


                                                                        Globals.ReturnTotalQty = 0.0;
                                                                        Globals.ReturnTotalPrice = 0.0;
                                                                        Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                                                                        Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                        startActivity(intent1);
                                                                        finish();
                                                                       /* if (Globals.objsettings.get_Is_File_Share().equals("true")) {

                                                                            share_dialog();
                                                                        }
*/
                                                                    } else {
                                                                        if (is_directPrint.equals("true")) {
                                                                            try {
                                                                                print_return();

                                                                               /* if (Globals.objsettings.get_Is_File_Share().equals("true")) {

                                                                                    share_dialog();
                                                                                }*/
                                                                            } catch (Exception e) {

                                                                            }
                                                                        } else {
                                                                            //  Toast.makeText(getApplicationContext(),"print start 1",Toast.LENGTH_LONG).show();
                                                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                                            alertDialog.setTitle(getString(R.string.cusreturn));
                                                                            alertDialog.setMessage(getString(R.string.printcusreturn));
                                                                            alertDialog.setIcon(R.drawable.delete);

                                                                            alertDialog.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                    Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                                                                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                    startActivity(intent1);
                                                                                    finish();
                                                                                    try {
                                                                                        print_return();
                                                                                    } catch (Exception e) {

                                                                                    }

                                                                                    /*if (Globals.objsettings.get_Is_File_Share().equals("true")) {

                                                                                        share_dialog();
                                                                                    }*/
                                                                                }
                                                                            });


                                                                            alertDialog.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();

                                                                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                    startActivity(intent1);
                                                                                    finish();
                                                                                    dialog.cancel();
                                                                                }
                                                                            });

                                                                            // Showing Alert Message
                                                                            alertDialog.show();
                                                                        }
                                                                    }

                                                                } catch (Exception ex) {
                                                                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), getString(R.string.recrdnotpost), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }

                                                } else {

                                                    if (isNetworkStatusAvialable(getApplicationContext())) {

                                                        send_online_return_single(pDialog, str_voucher_no);

                                                        // if (result.equals("1")) {
                                                        String rsultPost = stock_post();

                                                        //else {
                                                        // pDialog.dismiss();
                                                        if (rsultPost.equals("1")) {
                                                            if (Globals.objsettings.get_Is_Stock_Manager().equals("true")) {
                                                                String rsultUpdate = stock_update();
                                                            }


                                                        }

                                                        //print_return();

                                                        switch (rsultPost) {
                                                            case "1":


                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        //  print_return();

                                                                          Globals.return_item_tax.clear();
                                                                        if (Globals.PrinterType.equals("0")) {
                                                                            if(Globals.objsettings.get_Is_File_Share().equals("true")){
                                                                                runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                       // share_dialog();
                                                                                        Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                        startActivity(intent1);
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                            }
                                                                            else {
                                                                                Globals.strContact_Code = "";
                                                                                Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                startActivity(intent1);
                                                                                finish();
                                                                            }
                                                                           /* if (Globals.objsettings.get_Is_File_Share().equals("true")) {

                                                                                share_dialog();
                                                                            }*/
                                                                        } else {

                                                                            if (is_directPrint.equals("true")) {
                                                                                try {
                                                                                    print_return();
                                                                                } catch (Exception e) {

                                                                                }

                                                                                Globals.strContact_Code = "";
                                                                                Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                startActivity(intent1);
                                                                                finish();

                                                                            } else {
                                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                                                alertDialog.setTitle(getString(R.string.cusreturn));
                                                                                alertDialog.setMessage(getString(R.string.printcusreturn));
                                                                                alertDialog.setIcon(R.drawable.delete);

                                                                                alertDialog.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        if(Globals.objsettings.get_Is_File_Share().equals("true")){
                                                                                            runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                   // share_dialog();

                                                                                                }
                                                                                            });

                                                                                            runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        print_return();
                                                                                                    } catch (Exception e) {

                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                        else {
                                                                                            try {
                                                                                                print_return();
                                                                                            } catch (Exception e) {

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });


                                                                                alertDialog.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                                                                                        Globals.strContact_Code = "";
                                                                                        Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                        startActivity(intent1);
                                                                                        finish();
                                                                                        dialog.cancel();

                                                                                    }
                                                                                });

                                                                                // Showing Alert Message
                                                                                alertDialog.show();
                                                                            }
                                                                        }

                                                                        pDialog.dismiss();
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
                                                                        Toast.makeText(getApplicationContext(), getString(R.string.recrdnotpost), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
                                                        }
                                                        // } else if (result.equals("3")) {

                                                        //}
                                                    } else {
                                                        //   } else {
                                                        pDialog.dismiss();
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                                    }
                                                }


                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }



              /*                              runOnUiThread(new Runnable() {
                                                public void run() {

                                                    if (Globals.objsettings.get_Is_File_Share().equals("true")) {
                                     *//*   Thread t2 = new Thread() {
                                            public void run() {*//*


                                                        final Dialog listDialog2 = new Dialog(CusReturnFinalActivity.this);
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
                                                                finish();
                                                            }
                                                        });
                                                        btnButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                              *//*  runOnUiThread(new Runnable() {
                                                                    public void run() {
*//*
                                                                        //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                                                                        startWhatsApp();

                                                                 *//*   }
                                                                });*//*

                                                                listDialog2.dismiss();

                                                            }
                                                        });


                                                    }

                                                }});*/

                                          /*  runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    try {
                                                        //sleep(3000);



                                                        if (Globals.objsettings.get_Is_File_Share().equals("true")) {



                                                            final Dialog listDialog2 = new Dialog(CusReturnFinalActivity.this);
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
                                                                    finish();
                                                                }
                                                            });
                                                            btnButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {



                                                                    //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                                                                    startWhatsApp();



                                                                    listDialog2.dismiss();

                                                                }
                                                            });


                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }


                                            });
*/

                                        }
                                    };
                                    timerThread1.start();




                                    //share_dialog();
                                }
                                break;

                            case R.id.action_save:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(CusReturnFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            String result = stock_save();
                                            pDialog.dismiss();
                                            if (result.equals("1")) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if (Globals.PrinterType.equals("0")) {


                                                            Toast.makeText(getApplicationContext(), getString(R.string.savesucc), Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                            startActivity(intent1);
                                                            finish();


                                                        } else {
                                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                            alertDialog.setTitle(getString(R.string.cusreturn));
                                                            alertDialog.setMessage(getString(R.string.printcusreturn));
                                                            alertDialog.setIcon(R.drawable.delete);

                                                            alertDialog.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                                                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                    startActivity(intent1);
                                                                    finish();
                                                                    try {
                                                                        print_return();
                                                                    } catch (Exception e) {

                                                                    }
                                                                }
                                                            });


                                                            alertDialog.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                                                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                    startActivity(intent1);
                                                                    finish();
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            // Showing Alert Message
                                                            alertDialog.show();
                                                        }

                                                    }
                                                });

                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        }
                                    };
                                    timerThread.start();
                                }
                                break;


                            case R.id.action_cancel:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(CusReturnFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread2 = new Thread() {
                                        public void run() {
                                            returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                            if (returns == null) {
                                                String result = stock_save();
                                                pDialog.dismiss();
                                                if (result.equals("1")) {
                                                    String rsultPost = stock_cancel();
                                                    if (rsultPost.equals("1")) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                startActivity(intent1);
                                                                finish();
                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Record not Cancel", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }

                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                            } else {
                                                String rsultPost = stock_cancel();
                                                pDialog.dismiss();
                                                if (rsultPost.equals("1")) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                            startActivity(intent1);
                                                            finish();
                                                        }
                                                    });

                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Record not Cancel", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    };
                                    timerThread2.start();
                                }
                                break;


                            case R.id.action_delete:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(CusReturnFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread3 = new Thread() {
                                        public void run() {
                                            returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                            if (returns == null) {
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Cannot delete without saving", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                String result = stock_delete();

                                                pDialog.dismiss();
                                                if (result.equals("1")) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                            startActivity(intent1);
                                                            finish();
                                                        }
                                                    });

                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
                                    timerThread3.start();
                                }
                                break;

                            case R.id.action_print:
                                returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                if (returns == null) {
                                    Toast.makeText(getApplicationContext(), "New mode this operation will not work", Toast.LENGTH_SHORT).show();

                                } else {
                                    if (Globals.PrinterType.equals("0")) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.printersettingmsg), Toast.LENGTH_SHORT).show();


                                    } else {
                                        try {
                                            print_return();
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());

                                        }

                                    }
                                }
                                break;
                        }
                        return true;
                    }
                });

        edt_toolbar_item_code.setOnKeyListener(new View.OnKeyListener() {
                                                   @Override
                                                   public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                       if (event.getAction() == KeyEvent.ACTION_DOWN
                                                               && keyCode == KeyEvent.KEYCODE_ENTER) {
                                                           String strValue = edt_toolbar_item_code.getText().toString();
                                                           if (edt_toolbar_item_code.getText().toString().equals("\n") || edt_toolbar_item_code.getText().toString().equals("")) {
                                                               Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                               edt_toolbar_item_code.requestFocus();
                                                           } else {
                                                               String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                               arrayListItem = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                               if (arrayListItem.size() >= 1) {
                                                                   strupdate = "";
                                                                   resultp = arrayListItem.get(0);
                                                                   item_code = resultp.get_item_code();
                                                                   Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);

                                                                   if (item_location == null) {
                                                                       sale_priceStr = "0";
                                                                   } else {
                                                                       sale_priceStr = item_location.get_selling_price();
                                                                   }

                                                                   String item_price;
                                                                   item_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                                                                   edt_name.setText(resultp.get_item_name());
                                                                   edt_price.setText(item_price);
                                                                   edt_qty.setText("1");
                                                                   edt_toolbar_item_code.setText("");

                                                               } else {
                                                                   edt_toolbar_item_code.selectAll();
                                                                   Toast.makeText(getApplicationContext(), getString(R.string.nodatafound), Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                           return true;
                                                       }
                                                       return false;
                                                   }
                                               }
        );
    }

    private void print_return() {
        returns = Returns.getReturns(CusReturnFinalActivity.this, "WHERE voucher_no = '" + str_voucher_no + "'", database);
        return_details = Return_detail.getAllReturn_detail(CusReturnFinalActivity.this,
                "WHERE ref_voucher_no = '" + str_voucher_no + "'", database);
        /*Toast.makeText(getApplicationContext(),"Return"+ returns,Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(),"Return details"+ return_details,Toast.LENGTH_LONG).show();*/
        if(Globals.PrinterType.equals("2")){
        mylist = getlist();
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
        } else if (Globals.PrinterType.equals("1")) {
            try {
                if (woyouService == null) {
                } else {
                    mobile_pos(returns, return_details);
                    Globals.strContact_Code = "";
                    Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                    startActivity(intent1);
                    finish();

                }
            } catch (Exception ex) {
            }
        } else if (Globals.PrinterType.equals("3")) {
            String result = bluetooth_56(returns, return_details);
            Globals.strContact_Code = "";
            Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
            startActivity(intent1);
            finish();

        } else if (Globals.PrinterType.equals("4")) {
            String result = bluetooth_80(returns, return_details);
            Globals.strContact_Code = "";
            Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
            startActivity(intent1);
            finish();

        }
        else if (Globals.PrinterType.equals("6")) {
            try {
                if (woyouService == null) {
                } else {
                    PHA_POS(returns, return_details);
                    Globals.strContact_Code = "";
                    Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                    startActivity(intent1);
                    finish();

                }
            } catch (Exception ex) {
            }
        }
        else if (Globals.PrinterType.equals("7")) {
            try {

                ppt8527(returns, return_details);
                Globals.strContact_Code = "";
                Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                startActivity(intent1);
                finish();

            } catch (Exception ex) {
            }
        }
        else if (Globals.PrinterType.equals("8")) {
            // Toast.makeText(getApplicationContext(),"customer return"+ PrinterType + ":::"+ settings.get_Copy_Right(),Toast.LENGTH_LONG).show();

            ppt_8555(returns, return_details);
            Globals.strContact_Code = "";
            Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
            startActivity(intent1);
            finish();

        }
        else if (Globals.PrinterType.equals("9")) {
            // Toast.makeText(getApplicationContext(),"customer return"+ PrinterType + ":::"+ settings.get_Copy_Right(),Toast.LENGTH_LONG).show();

            try {
                if (woyouService == null) {
                } else {
                    mobile_pos(returns, return_details);
                    Globals.strContact_Code = "";
                    Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                    startActivity(intent1);
                    finish();

                }
            } catch (Exception ex) {
            }
        }

        else if(Globals.PrinterType.equals("10")){
            Intent intent = new Intent(CusReturnFinalActivity.this, PdfWebView.class);
            intent.putExtra("contact_code", str_voucher_no);
            intent.putExtra("from","CustomerReturn");
            startActivity(intent);


            /*Globals.strContact_Code = "";
            Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
            startActivity(intent1);*/


        }
        else if(Globals.PrinterType.equals("11")){
            Intent intent = new Intent(CusReturnFinalActivity.this, PdfWebView.class);
            intent.putExtra("contact_code", str_voucher_no);
            intent.putExtra("from","CustomerReturn");

            startActivity(intent);


            /*Globals.strContact_Code = "";
            Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
            startActivity(intent1);*/


        }
    }

    private void ppt_8555(final Returns returns, final ArrayList<Return_detail> return_details) {


        if (Globals.objsettings.get_Print_Lang().equals("0")) {
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strString = "";
                        int strLength = 14;

                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {


                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("") || Globals.objLPR.getService_code_tariff().equals("0")) {
                                } else {
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);
                                }
                            } catch (Exception ex) {
                            }
                            mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().equals("0")) {
                            } else {
                                mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                            }


                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("Customer Return \n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                            strString = Globals.myRequiredString("Return No", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + str_voucher_no, "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Return Date", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + returns.get_date().substring(0,10), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Return Time", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + returns.get_date().substring(11,19), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + user.get_name(), "ST", 24, callbackPPT8555);

                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                            strString = Globals.myRequiredString("Customer", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + contact.get_name(), "ST", 24, callbackPPT8555);
                            if (contact.get_gstin().length() > 0) {
                                strString = Globals.myRequiredString("Customer GST No", strLength);
                                mIPosPrinterService.printSpecifiedTypeText(strString + " : " + contact.get_gstin(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            int count = 0;
                            Double itemFinalTax = 0d;
                            double finalitemTax=0d;

                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                Tax_Master tax_master = null;
                               /* ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no()+ "'", database);
                                if(order_returndetail_tax.size()>0) {
                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                        double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                        itemFinalTax += valueFinal;

                                        strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                        Globals.AppLogWrite("IteM tAX Name" + strString);
                                        mIPosPrinterService.printSpecifiedTypeText(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                    }
                                }*/

                                if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                                    if (item.get_is_inclusive_tax().equals("0")) {
                                        ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                                        if (order_returndetail_tax.size() > 0) {
                                            if (!returns.get_contact_code().equals("")) {
                                                if (contact.getIs_taxable().equals("1")) {
                                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                mIPosPrinterService.printSpecifiedTypeText(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);

                                                              //  mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                            }

                                                        }
                                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                mIPosPrinterService.printSpecifiedTypeText(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);


                                                            }
                                                        }
                                                    }
                                                }
                                                else{

                                                }

                                            }

                                            else if (returns.get_contact_code().equals("")){
                                                for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                        double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                        itemFinalTax += valueFinal;

                                                        strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                        Globals.AppLogWrite("IteM tAX Name" + strString);
                                                        mIPosPrinterService.printSpecifiedTypeText(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }

                                count++;
                            }


                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = " (" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            int iTemp=0;
                            Tax_Master tax_master=null;
                            strString = Globals.myRequiredString("Net Amt", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);
                            String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);

                         /*   while (cursor1.moveToNext()) {
                                iTemp += 1;
                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                if(tax_master!=null) {
                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                    mIPosPrinterService.printSpecifiedTypeText(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                }
                            }*/
                              if (!returns.get_contact_code().equals("")) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        while (cursor1.moveToNext()) {
                                            // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    mIPosPrinterService.printSpecifiedTypeText(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }
                                        }

                                        //   }
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                        while (cursor1.moveToNext()) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    mIPosPrinterService.printSpecifiedTypeText(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);
                                                }
                                            }

                                        }
                                        // }
                                    }
                                }
                                else{

                                }

                            }

                            else if (returns.get_contact_code().equals("")){
                                // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                while (cursor1.moveToNext()) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTemp += 1;
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                        if(tax_master!=null) {
                                            String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                            mIPosPrinterService.printSpecifiedTypeText(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "ST", 24, callbackPPT8555);

                                        }
                                    }
                                }

                                //   }

                            }
                            Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                            strString = Globals.myRequiredString("Payment Mode", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + " : " + payment.get_payment_name(), "ST", 24, callbackPPT8555);

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            }

                         //   mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.PrintSpecFormatText( Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
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
                        int strLength = 14;

                        //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                            //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                            }
                            //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                } else {
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                    // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                }
                            } catch (Exception ex) {
                            }
                            mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                            } else {
                                mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            }
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("عودة العملاء", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                              /*  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);*/
                            strString = Globals.myRequiredString("رقم العودة", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + str_voucher_no, "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("تاريخ العودة", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + returns.get_date().substring(0,10), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("وقت العودة", strLength);
                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + returns.get_date().substring(11,19), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("معرف الجهاز", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            strString = Globals.myRequiredString("مندوب مبيعات", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + user.get_name(), "ST", 24, callbackPPT8555);
                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                            strString = Globals.myRequiredString("الزبون", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + contact.get_name(), "ST", 24, callbackPPT8555);
                            if (contact.get_gstin().length() > 0) {
                                strString = Globals.myRequiredString("رقم ضريبة السلع والخدمات للعملاء", strLength);

                                mIPosPrinterService.printSpecifiedTypeText(strString + ":" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            int count = 0;
                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "       " + sale_price + "       " + line_total + "\n", "ST", 24, callbackPPT8555);
                                count++;
                            }


                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("صافي صافي", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString + ":" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);

                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                            }
                            //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.PrintSpecFormatText( Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
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
                        //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                            //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                            }
                            //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                            try {
                                if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                } else {
                                    mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                    // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                }
                            } catch (Exception ex) {
                            }
                            mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                            } else {
                                mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            }
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            mIPosPrinterService.PrintSpecFormatText("Customer Return/عودة العملاء\n", "ST", 24, 1, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Return No/رقم العودة", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString +"\n"+ str_voucher_no, "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Return Date/تاريخ العودة", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString +"\n"+ returns.get_date().substring(0,10), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Return Time/وقت العودة", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString +"\n"+ returns.get_date().substring(11,19), "ST", 24, callbackPPT8555);
                            strString = Globals.myRequiredString("Device ID/معرف الجهاز", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString +"\n"+ Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            strString = Globals.myRequiredString("Salesperson/مندوب مبيعات", strLength);


                            mIPosPrinterService.printSpecifiedTypeText(strString+"\n" + user.get_name(), "ST", 24, callbackPPT8555);

                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                            strString = Globals.myRequiredString("Customer/الزبون", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString+"\n" + contact.get_name(), "ST", 24, callbackPPT8555);
                            if (contact.get_gstin().length() > 0) {
                                strString = Globals.myRequiredString("Customer GST No./رقم ضريبة السلع والخدمات للعملاء", strLength);

                                mIPosPrinterService.printSpecifiedTypeText(strString+"\n" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            int count = 0;
                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");

                                String strItemName_l = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(strItemName_l + "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "       " + sale_price + "       " + line_total + "\n", "ST", 24, callbackPPT8555);
                                count++;
                            }


                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("Net Amt/صافي صافي", strLength);

                            mIPosPrinterService.printSpecifiedTypeText(strString +"\n" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);

                            mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                            }

                            mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.PrintSpecFormatText( Globals.objsettings.get_Copy_Right(), "ST", 24, 1, callbackPPT8555);                            //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                            mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            });
        }


    }


    private void ppt_8555_1(final Returns returns, final ArrayList<Return_detail> return_details) {

        if (decimal_check.equals("2")) {

            if (Globals.objsettings.get_Print_Lang().equals("0")) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("Return Voucher\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                             /*   mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);*/
                                mIPosPrinterService.printSpecifiedTypeText("Return No" + ":" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return Date" + ":" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintCashier + ":" + user.get_name(), "ST", 24, callbackPPT8555);

                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("Customer" + ":" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("Customer GST No." + ":" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("Net Amt" + ":" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
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

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("قسيمة الإرجاع", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                             /*   mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);*/
                                mIPosPrinterService.printSpecifiedTypeText("رقم العودة" + ":" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("تاريخ العودة" + ":" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("معرف الجهاز" + ":" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText("مندوب مبيعات" + ":" + user.get_name(), "ST", 24, callbackPPT8555);
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("الزبون" + ":" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("رقم ضريبة السلع والخدمات للعملاء" + ":" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                //   mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                // mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("صافي صافي" + ":" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
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

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("Return Voucher/قسيمة الإرجاع\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return No/رقم العودة\n" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return Date/تاريخ العودة\n" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Device ID/معرف الجهاز\n" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText("Salesperson/مندوب مبيعات\n" + user.get_name(), "ST", 24, callbackPPT8555);

                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("Customer/الزبون\n" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("Customer GST No./رقم ضريبة السلع والخدمات للعملاء\n" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");

                                    String strItemName_l = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName_l + "\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("Net Amt/صافي صافي\n" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        } else {
            if (Globals.objsettings.get_Print_Lang().equals("0")) {
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("Return Voucher\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                              /*  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);*/
                                mIPosPrinterService.printSpecifiedTypeText("Return No" + ":" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return Date" + ":" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText(Globals.PrintCashier + ":" + user.get_name(), "ST", 24, callbackPPT8555);

                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("Customer" + ":" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("Customer GST No." + ":" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("Net Amt" + ":" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
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

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("قسيمة الإرجاع", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                              /*  mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);*/
                                mIPosPrinterService.printSpecifiedTypeText("رقم العودة" + ":" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("تاريخ العودة" + ":" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("معرف الجهاز" + ":" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText("مندوب مبيعات" + ":" + user.get_name(), "ST", 24, callbackPPT8555);
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("الزبون" + ":" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("رقم ضريبة السلع والخدمات للعملاء" + ":" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("صافي صافي" + ":" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            Globals.strOldCrAmt = "0";
                            Globals.setEmpty();
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

                            //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {

                                //mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                                if (bitmap != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                    bitmap = getResizedBitmap(bitmap, 80, 120);
                                    mIPosPrinterService.printBitmap(1, 6, bitmap, callbackPPT8555);
                                }
                                //  Toast.makeText(getApplicationContext(),"printer ppt 8555"+mIPosPrinterService,Toast.LENGTH_LONG).show();
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);

                                try {
                                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                                    } else {
                                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, 1, callbackPPT8555);

                                        // mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                                    }
                                } catch (Exception ex) {
                                }
                                mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                                } else {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                if (Globals.strIsBarcodePrint.equals("true")) {
                                    mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                                    mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                }
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.PrintSpecFormatText("Return Voucher/قسيمة الإرجاع\n", "ST", 24, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return No/رقم العودة\n" + str_voucher_no, "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Return Date/تاريخ العودة\n" + returns.get_date(), "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Device ID/معرف الجهاز\n" + Globals.objLPD.getDevice_Name(), "ST", 24, callbackPPT8555);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                mIPosPrinterService.printSpecifiedTypeText("Salesperson/مندوب مبيعات\n" + user.get_name(), "ST", 24, callbackPPT8555);

                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mIPosPrinterService.printSpecifiedTypeText("Customer/الزبون\n" + contact.get_name(), "ST", 24, callbackPPT8555);
                                if (contact.get_gstin().length() > 0) {
                                    mIPosPrinterService.printSpecifiedTypeText("Customer GST No./رقم ضريبة السلع والخدمات للعملاء\n" + contact.get_gstin(), "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Item Name\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("اسم العنصر", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("Qty       Price       Total\n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText("الكمية       السعر       مجموع\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                int count = 0;
                                while (count < return_details.size()) {
                                    String strItemCode = return_details.get(count).get_item_code();
                                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");

                                    String strItemName_l = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                                    String sale_price;
                                    Double dDisAfterSalePrice = 0d;

                                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                    String line_total;
                                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName + "\n", "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText(strItemName_l + "\n", "ST", 24, callbackPPT8555);

                                    mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "         " + sale_price + "      " + line_total + "\n", "ST", 24, callbackPPT8555);
                                    count++;
                                }


                                mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                                String strCurrency;
                                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                    strCurrency = "";
                                } else {
                                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                                }
                                mIPosPrinterService.printSpecifiedTypeText("Net Amt/صافي صافي\n" + net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                                mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                    mIPosPrinterService.printSpecifiedTypeText(Globals.objsettings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 24, callbackPPT8555);
                                }

                                mIPosPrinterService.printSpecifiedTypeText("" + Globals.objsettings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);
                                //   mIPosPrinterService.printSpecifiedTypeText("" + "Jyoti"+ "\n", "ST", 24, callbackPPT8555);

                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                                mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                            }
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
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

    private ArrayList<String> getlist() {
        ArrayList<String> mylist = new ArrayList<String>();


            if (Globals.objsettings.get_Print_Lang().equals("0")) {
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
                    mylist.add("\n"+ lbl +"\n");
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
                    mylist.add("  "+Globals.GSTNo + ":" + Globals.objLPR.getLicense_No()+ "\n");
                }
//        lbl = LableCentre(Globals.PrintOrder);
//        mylist.add("\n" + lbl);
                mylist.add("-------------------------------------------\n");
                mylist.add("               "+"Customer Return");
                mylist.add("\n-------------------------------------------\n");
                strString = Globals.myRequiredString("Return No", strLength);
                mylist.add(strString + ":" + str_voucher_no);
                strString = Globals.myRequiredString("Return Date", strLength);
                mylist.add("\n" + strString + ":" + returns.get_date().substring(0,10));
                strString = Globals.myRequiredString("Return Time", strLength);
                mylist.add("\n" + strString + ":" + returns.get_date().substring(11,19));
                strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                mylist.add("\n" +strString + ":" + Globals.objLPD.getDevice_Name());

                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                mylist.add("\n" + strString + ":" + user.get_name()+"\n");

                if (returns.get_contact_code().equals("")) {
                    Globals.strContact_Name = "";
                } else {
                    try {
                        strString = Globals.myRequiredString("Customer", strLength);
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                        mylist.add( strString + ":" + contact.get_name()+"\n");
                        if (contact.get_gstin().length() > 0) {
                            strString = Globals.myRequiredString("Customer GST No.:", strLength);
                            mylist.add(strString + contact.get_gstin()+"\n");
                        }
                        Globals.strContact_Name = contact.get_name();
                    } catch (Exception ex) {
                    }
                }

                mylist.add("-------------------------------------------\n");
                mylist.add("Item Name\n");
                mylist.add("Qty             Price            Total");
                mylist.add("\n-------------------------------------------\n");
                Double itemFinalTax = 0d;
                int count = 0;
                Contact contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                while (count < return_details.size()) {

                    String strItemCode = return_details.get(count).get_item_code();
                    // Item item = Item.getItem(getApplicationContext(), " Where item_code = '" + item_code + "'", database, db);
                    item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By Return_detail.item_Code");

                    String sale_price;

                    Double dDisAfterSalePrice = 0d;

                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                    String line_total;
                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);

                    //item name
                    int l1 = String.valueOf(strItemName).length();

                    mylist.add(strItemName +"\n");

                    String strItemQty,strItemPrice,strItemTotal;
                    //strString = Globals.myRequiredString(Globals.PrintInvNo, strLength);
                    strLength = 15;
                    strItemQty = Globals.myRequiredString(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), strLength);

                    strItemPrice = Globals.myRequiredString(sale_price, 17);
                    strItemTotal = Globals.myRequiredString(line_total, 15);

                    mylist.add(strItemQty
                            + strItemPrice + strItemTotal+"\n");
                   // mylist.add(tt);

                    Tax_Master tax_master = null;
         /*           ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no()+ "'", database);
                    if(order_returndetail_tax.size()>0) {
                        for (int i = 0; i < order_returndetail_tax.size(); i++) {
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                            itemFinalTax += valueFinal;

                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                            Globals.AppLogWrite("IteM tAX Name" + strString);
                            mylist.add(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n");
                        }
                    }*/



                    if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                        if (item.get_is_inclusive_tax().equals("0")) {
                            ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                            if (order_returndetail_tax.size() > 0) {
                                if (!returns.get_contact_code().equals("")) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                            for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                    itemFinalTax += valueFinal;

                                                    strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    Globals.AppLogWrite("IteM tAX Name" + strString);
                                                    mylist.add(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n");

                                                    //  mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                }

                                            }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                    itemFinalTax += valueFinal;

                                                    strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    Globals.AppLogWrite("IteM tAX Name" + strString);
                                                    mylist.add(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n");


                                                }
                                            }
                                        }
                                    }
                                    else{

                                    }

                                }

                                else if (returns.get_contact_code().equals("")){
                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                            itemFinalTax += valueFinal;

                                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                            Globals.AppLogWrite("IteM tAX Name" + strString);
                                            mylist.add(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n");
                                        }

                                    }

                                }
                            }
                        }
                    }
                    count++;
                }

                mylist.add("-------------------------------------------\n");
                strLength = 16;
                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }
                strString = Globals.myRequiredString("Net Amount", strLength);
                mylist.add( strString + ":" + net_amount + strCurrency +"\n");

                int iTemp=0;
                Tax_Master tax_master;
                String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                Cursor cursor1 = database.rawQuery(strTableQry, null);

            /*    while (cursor1.moveToNext()) {
                    iTemp += 1;
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                    if(tax_master!=null) {
                        String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                        mylist.add(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n");
                    }
                }*/

                if (!returns.get_contact_code().equals("")) {
                    if (contact.getIs_taxable().equals("1")) {
                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                            while (cursor1.moveToNext()) {
                                // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                    iTemp += 1;
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    if(tax_master!=null) {
                                        String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                        mylist.add(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n");
                                    }
                                }
                            }

                            //   }
                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                            //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                            while (cursor1.moveToNext()) {

                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                    iTemp += 1;
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    if(tax_master!=null) {
                                        String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                        mylist.add(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n");
                                    }
                                }

                            }
                            // }
                        }
                    }
                    else{

                    }

                }

                else if (returns.get_contact_code().equals("")){
                    // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                    while (cursor1.moveToNext()) {
                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                            iTemp += 1;
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                            if(tax_master!=null) {
                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                mylist.add(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n");

                            }
                        }
                    }

                    //   }

                }
                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                strString = Globals.myRequiredString("Payment Mode", strLength);

                mylist.add( strString + ":" + payment.get_payment_name()+"\n");

                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                    mylist.add("    " + Globals.objsettings.get_Footer_Text()+"\n");
                }
                mylist.add("-------------------------------------------\n");

                mylist.add("               " + Globals.objsettings.get_Copy_Right());
                mylist.add("\n");
                mylist.add("\n");
               /* Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.strOldCrAmt = "0";*/
               mylist.size();
             //   Globals.setEmpty();
            } else if (Globals.objsettings.get_Print_Lang().equals("1")) {
                String strString = "";
                int strLength = 15;

                String lbl;
                String tt = "", tt1 = "";
              //  mylist.add("\n-----------------------------------------------");
                lbl = LableCentre(Globals.objLPR.getCompany_Name());
                mylist.add("\n" + lbl);
                lbl = LableCentre(Globals.objLPR.getAddress());
                mylist.add("\n" + lbl);
                lbl = LableCentre(Globals.objLPR.getMobile_No());
                mylist.add("\n" + lbl);
                try {
                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                    } else {
                        lbl = LableCentre(Globals.objLPR.getService_code_tariff());
                        mylist.add("\n" + lbl);
//                    mylist.add("\n" + "Tariff Code");
                    }
                } catch (Exception ex) {
                }
                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                } else {
                    mylist.add("\n" + Globals.GSTNo + ":" + Globals.objLPR.getLicense_No());
                }

//        lbl = LableCentre(Globals.PrintOrder);
//        mylist.add("\n" + lbl);
                mylist.add("----------------------------------------\n");
                mylist.add("عودة العملاء\n"+"         ");
                mylist.add("----------------------------------------\n");
                strString = Globals.myRequiredString("رقم العودة" , strLength);
                mylist.add("\n" + strString + "  :" + str_voucher_no);
                strString = Globals.myRequiredString("تاريخ العودة" , strLength);
                mylist.add("\n" + strString + ":" + returns.get_date());
                strString = Globals.myRequiredString("معرف الجهاز" , strLength);
                mylist.add("\n" + strString + ":" + Globals.objLPD.getDevice_Name());

                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                mylist.add("\n" + Globals.PrintCashier + ":" + user.get_name());

                if (Globals.strContact_Code.equals("")) {
                    Globals.strContact_Name = "";
                } else {
                    try {
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.strContact_Code + "'");
                        strString = Globals.myRequiredString("الزبون :" , strLength);
                        mylist.add("\n" + strString + ":" + contact.get_name());
                        if (contact.get_gstin().length() > 0) {
                            strString = Globals.myRequiredString("رقم ضريبة السلع والخدمات للعملاء :" , strLength);
                            mylist.add("\n" + strString + contact.get_gstin());
                        }
                        Globals.strContact_Name = contact.get_name();
                    } catch (Exception ex) {
                    }
                }

                mylist.add("----------------------------------------\n");
                mylist.add("اسم العنصر");
                mylist.add("الكمية       السعر      مجموع\n");
                mylist.add("----------------------------------------\n");
                Double itemFinalTax = 0d;
                int count = 0;
                while (count < return_details.size()) {

                    String strItemCode = return_details.get(count).get_item_code();
                    // Item item = Item.getItem(getApplicationContext(), " Where item_code = '" + item_code + "'", database, db);

                    String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By Return_detail.item_Code");


                    String sale_price;

                    Double dDisAfterSalePrice = 0d;

                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                    String line_total;
                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);

                    //item name
                    int l1 = String.valueOf(strItemName).length();

                    mylist.add(strItemName +"\n");

                    String strItemQty,strItemPrice,strItemTotal;
                    //strString = Globals.myRequiredString(Globals.PrintInvNo, strLength);
                    strLength = 9;
                    strItemQty = Globals.myRequiredString(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), strLength);

                    strItemPrice = Globals.myRequiredString(sale_price, 12);
                    strItemTotal = Globals.myRequiredString(line_total, 9);

                    mylist.add(strItemQty
                            + strItemPrice + strItemTotal+ "\n");

                   // mylist.add(tt);
                    count++;
                }

                mylist.add("\n----------------------------------------\n");

                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

               /* tt = "";
                int ln = 0;
                ln = net_amount.length();
                int space = 9 - ln;
                for (int v = 0; v < space; v++) {
                    tt = tt + " ";
                }
                tt = tt + net_amount;*/

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }
                strString = Globals.myRequiredString("صافي صافي:" , strLength);

                mylist.add(strString + net_amount + strCurrency);

                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                    mylist.add("\n    " + Globals.objsettings.get_Footer_Text());
                }
                mylist.add("\n----------------------------------------\n");

                mylist.add("\n            " + Globals.objsettings.get_Copy_Right());
                mylist.add("\n");
                mylist.add("\n");
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.strOldCrAmt = "0";
                Globals.setEmpty();
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
                    if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {

                    } else {
                        lbl = LableCentre(Globals.objLPR.getService_code_tariff());
                        mylist.add("\n" + lbl);
//                    mylist.add("\n" + "Tariff Code");
                    }
                } catch (Exception ex) {
                }
                if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                } else {
                    mylist.add("\n" + Globals.GSTNo + ":" + Globals.objLPR.getLicense_No());
                }

//        lbl = LableCentre(Globals.PrintOrder);
//        mylist.add("\n" + lbl);
                mylist.add("----------------------------------------\n");
                mylist.add("         "+"Customer Return/عودة العملاء\n");
                mylist.add("----------------------------------------\n");
                mylist.add("\n" + "Return No" + "  :" + str_voucher_no);
                mylist.add("\n" + "Return Date" + ":" + returns.get_date());
                mylist.add("\n" + Globals.PrintDeviceID + ":" + Globals.objLPD.getDevice_Name());

                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                mylist.add("\n" + Globals.PrintCashier + ":" + user.get_name());

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

                mylist.add("--------------------------------------------------------------------------");
                mylist.add("Item Name                 ");
                mylist.add("Qty       Price      Total\n");
                mylist.add("--------------------------------------------------------------------------");
                Double itemFinalTax = 0d;
                int count = 0;
                while (count < return_details.size()) {

                    String strItemCode = return_details.get(count).get_item_code();
                    // Item item = Item.getItem(getApplicationContext(), " Where item_code = '" + item_code + "'", database, db);

                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By Return_detail.item_Code");

                    String strItemName_l = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                    String line_total;
                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);

                    //item name
                    int l1 = String.valueOf(strItemName).length();
                    if (l1 > 24) {

                        char[] nm = strItemName.toUpperCase().toCharArray();
                        for (int k = 0; k < 24; k++) {

                            tt = tt + nm[k];
                        }
                        tt = "\n";
                    } else {
                        char[] nm = strItemName.toUpperCase()
                                .toCharArray();
                        for (int k = 0; k < l1; k++) {

                            tt = tt + nm[k];
                        }
                        int space = 24 - l1;
                        for (int v = 0; v < space; v++) {

                            tt = "\n";
                        }
                    }
                    // item name arabic
                    int l1_ar = strItemName_l.length();
                    if (l1_ar > 24) {

                        char[] nm_ar = strItemName_l.toUpperCase().toCharArray();
                        for (int k = 0; k < 24; k++) {

                            tt = tt + nm_ar[k];
                        }
                        // tt = tt + " ";
                        tt = "\n";
                    } else {
                        char[] nm_ar = strItemName_l.toUpperCase()
                                .toCharArray();
                        for (int k = 0; k < l1_ar; k++) {

                            tt = tt + nm_ar[k];
                        }
                        int space = 24 - l1_ar;
                        for (int v = 0; v < space; v++) {

                            // tt = tt + " ";
                            tt = "\n";
                        }
                    }
                    //quantity
                    int l2 = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check).length();
                    if (l2 > 8) {
                        char[] qt = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check).toCharArray();
                        for (int k = 0; k < 8; k++) {
                            tt = tt + qt[k];
                        }
                        tt = tt + " ";
                    } else {
                        char[] qt = Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check).toCharArray();
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

                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

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
                    count++;
                }

                mylist.add("\n---------------------------------------------------------------------------");

                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                tt = "";
                int ln = 0;
                ln = net_amount.length();
                int space = 9 - ln;
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
                mylist.add("Net Amount/صافي صافي     :  " + tt + strCurrency);

                if (!Globals.objsettings.get_Footer_Text().equals("")) {
                    mylist.add("\n    " + Globals.objsettings.get_Footer_Text());
                }
                mylist.add("\n            " + Globals.objsettings.get_Copy_Right());
                mylist.add("\n");
                mylist.add("\n");
                /*Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                Globals.strOldCrAmt = "0";
                Globals.setEmpty();*/
            }

        //}
        return mylist;
    }

    private String bluetooth_80(Returns returns, ArrayList<Return_detail> return_details) {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        byte[] ab;


            if ((lang.compareTo("en")) == 0) {
                try {
                    if (mService.isAvailable() == false) {
                    } else {
                        String strString = "";
                        int strLength = 14;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);

                        if(Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null")|| Globals.objLPR.getShort_companyname().length()==0 ||Globals.objLPR.getShort_companyname().isEmpty()) {
                            mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");
                        }
                        if(Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null")|| Globals.objLPR.getAddress().length()==0 ||Globals.objLPR.getAddress().isEmpty()) {

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        }
                        if(Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null")|| Globals.objLPR.getMobile_No().length()==0|| Globals.objLPR.getMobile_No().isEmpty()) {

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        }

                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")|| Globals.objLPR.getService_code_tariff().length()==0|| Globals.objLPR.getService_code_tariff().isEmpty()) {

                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                            }
                        } catch (Exception ex) {
                        }


                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")||Globals.objLPR.getLicense_No().length()==0|| Globals.objLPR.getLicense_No().isEmpty()) {
                        } else {
                            mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");

                        }

                        mService.sendMessage("...............................................", "GBK");
                        mService.sendMessage("Customer Return ", "GBK");
                        mService.sendMessage("...............................................", "GBK");
                        if (Globals.strIsBarcodePrint.equals("null") || Globals.strIsBarcodePrint.equals("")) {
                        } else {
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                byte[] sendData;
                                sendData = BytesUtil.getPrintQRCode(str_voucher_no, 1, 0);
                                mService.write(sendData);
                            }
                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);
                        }
                        strString = Globals.myRequiredString("Return No", strLength);
                        mService.sendMessage(strString + " : " + str_voucher_no, "GBK");
                        strString = Globals.myRequiredString("Return Date", strLength);
                        mService.sendMessage(strString + " : " + returns.get_date().substring(0,10), "GBK");
                        strString = Globals.myRequiredString("Return Time", strLength);
                        mService.sendMessage(strString + " : " + returns.get_date().substring(11,19), "GBK");
                        strString = Globals.myRequiredString(Globals.PrintDeviceID , strLength);
                        mService.sendMessage(strString + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                        try {
                            user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                            strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                            mService.sendMessage(strString + " : " + user.get_name(), "GBK");
                        }
                        catch(Exception e){

                        }

                        if (Globals.ModeResrv.equals("Resv")) {
                            strString = Globals.myRequiredString("Customer" , strLength);
                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                            mService.sendMessage(strString + " : " + contact.get_name(), "GBK");
                            if (contact.get_gstin().length() > 0) {
                                strString = Globals.myRequiredString("Customer GST No." , strLength);
                                mService.sendMessage(strString + " : " + contact.get_gstin(), "GBK");
                            }
                        } else {
                            if (returns.get_contact_code().equals("")) {
                            } else {
                                strString = Globals.myRequiredString("Customer" , strLength);
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mService.sendMessage(strString + " : " + contact.get_name(), "GBK");
                                if (contact.get_gstin().length() > 0) {
                                    strString = Globals.myRequiredString("Customer GST No." , strLength);
                                    mService.sendMessage(strString + " : " + contact.get_gstin(), "GBK");
                                }
                            }
                        }

                        mService.sendMessage("...............................................", "GBK");
                        mService.sendMessage("Item Name", "GBK");
                        mService.sendMessage("Qty             Price            Total", "GBK");
                        mService.sendMessage("...............................................", "GBK");

                        int count = 0;
                        Double itemFinalTax = 0d;
                        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                        while (count < return_details.size()) {

                            String strItemCode = return_details.get(count).get_item_code();
                            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                            String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                    + strItemCode + "'  GROUP By Return_detail.item_Code");
                            String sale_price;
                            Double dDisAfterSalePrice = 0d;
                            dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                            String line_total;
                            line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                            mService.sendMessage("" + strItemName, "GBK");
                            String strItemQty, strItemPrice, strItemTotal;

                            strLength = 15;
                            strItemPrice = Globals.myRequiredString(sale_price, 17);
                            strItemTotal = Globals.myRequiredString(line_total, 15);
                            strItemQty = Globals.myRequiredString(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), strLength);

                            mService.sendMessage("" + strItemQty + strItemPrice + strItemTotal, "GBK");

                            Tax_Master tax_master = null;
                            if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                                if (item.get_is_inclusive_tax().equals("0")) {
                                    ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                                    if (order_returndetail_tax.size() > 0) {
                                        if (!returns.get_contact_code().equals("")) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                            itemFinalTax += valueFinal;

                                                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            Globals.AppLogWrite("IteM tAX Name" + strString);
                                                            mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                        }

                                                    }
                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                            itemFinalTax += valueFinal;

                                                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            Globals.AppLogWrite("IteM tAX Name" + strString);
                                                            mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");


                                                        }
                                                    }
                                                }
                                            }
                                            else{

                                            }

                                        }

                                        else if (returns.get_contact_code().equals("")){
                                            for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                    itemFinalTax += valueFinal;

                                                    strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    Globals.AppLogWrite("IteM tAX Name" + strString);
                                                    mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                }

                                            }

                                        }
                                    }
                                }
                            }

                            count++;
                        }
                        mService.sendMessage("...............................................", "GBK");
                        String net_amount;
                        net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                        String strCurrency;
                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                            strCurrency = "";
                        } else {
                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                        }

                        int iTemp=0;
                        Tax_Master tax_master;
                        strString = Globals.myRequiredString("Net Amt", strLength);
                        mService.sendMessage(strString + " : " + net_amount + "" + strCurrency, "GBK");
                        String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                        Cursor cursor1 = database.rawQuery(strTableQry, null);


                        //if (item.get_is_inclusive_tax().equals("0")) {
                           // ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                           // if (order_returndetail_tax.size() > 0) {
                                if (!returns.get_contact_code().equals("")) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            while (cursor1.moveToNext()) {
                                           // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                        iTemp += 1;
                                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                        if(tax_master!=null) {
                                                            String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                                        }
                                                    }
                                            }

                                         //   }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                         //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                            while (cursor1.moveToNext()) {

                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                        iTemp += 1;
                                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                        if(tax_master!=null) {
                                                            String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                                        }
                                                    }

                                                }
                                           // }
                                        }
                                    }
                                    else{

                                    }

                                }

                                else if (returns.get_contact_code().equals("")){
                                   // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                    while (cursor1.moveToNext()) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                            iTemp += 1;
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                            if(tax_master!=null) {
                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                            }
                        }
                                        }

                                 //   }

                                }
                          //  }
                        //}
                       /* while (cursor1.moveToNext()) {
                            iTemp += 1;
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                            if(tax_master!=null) {
                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                            }
                        }*/
                        Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                        strString = Globals.myRequiredString("Payment Mode", strLength);
                        mService.sendMessage(strString + " : "+ payment.get_payment_name(), "GBK");

                        if (!Globals.objsettings.get_Footer_Text().equals("")||!Globals.objsettings.get_Footer_Text().isEmpty()||Globals.objsettings.get_Footer_Text().length()!=0) {

                            strString = Globals.myRequiredString(Globals.objsettings.get_Footer_Text() , strLength);
                            mService.sendMessage(strString + " : ", "GBK");
                        }
                        mService.sendMessage("...............................................", "GBK");
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);
                        mService.sendMessage("" + Globals.objsettings.get_Copy_Right() + "\n\n\n\n", "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                    }
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    Globals.strOldCrAmt = "0";
                    flag = "1";
                    Globals.setEmpty();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

        }
        return flag;
    }

    private String bluetooth_56(Returns returns, ArrayList<Return_detail> return_details) {
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        byte[] ab;

            if ((lang.compareTo("en")) == 0) {
                try {
                    if (mService.isAvailable() == false) {
                    } else {
                        String strString = "";
                        int strLength = 14;
                        ab = BytesUtil.setAlignCenter(1);
                        mService.write(ab);


                        if(Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null")|| Globals.objLPR.getShort_companyname().length()==0 ||Globals.objLPR.getShort_companyname().isEmpty()) {
                            mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");
                        }
                        if(Globals.objLPR.getAddress().equals("") || Globals.objLPR.getAddress().equals("null")|| Globals.objLPR.getAddress().length()==0 ||Globals.objLPR.getAddress().isEmpty()) {

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");
                        }
                        if(Globals.objLPR.getMobile_No().equals("") || Globals.objLPR.getMobile_No().equals("null")|| Globals.objLPR.getMobile_No().length()==0|| Globals.objLPR.getMobile_No().isEmpty()) {

                        }
                        else {
                            mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                        }

                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")|| Globals.objLPR.getService_code_tariff().length()==0|| Globals.objLPR.getService_code_tariff().isEmpty()) {

                            } else {
                                mService.sendMessage("" + Globals.objLPR.getService_code_tariff(), "GBK");

                            }
                        } catch (Exception ex) {
                        }


                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")||Globals.objLPR.getLicense_No().length()==0|| Globals.objLPR.getLicense_No().isEmpty()) {
                        } else {
                            mService.sendMessage(Globals.GSTNo + " : " + Globals.objLPR.getLicense_No(), "GBK");

                        }

                        mService.sendMessage("--------------------------------", "GBK");
                        mService.sendMessage("Customer Return", "GBK");
                        mService.sendMessage("--------------------------------", "GBK");
                        if (Globals.strIsBarcodePrint.equals("null") || Globals.strIsBarcodePrint.equals("")||Globals.strIsBarcodePrint.isEmpty()) {
                        } else {
                            if (Globals.strIsBarcodePrint.equals("true")) {
                                byte[] sendData;
                                sendData = BytesUtil.getPrintQRCode(str_voucher_no, 1, 0);
                                mService.write(sendData);
                            }
                            ab = BytesUtil.setAlignCenter(0);
                            mService.write(ab);
                        }
                        strString = Globals.myRequiredString("Return No", strLength);
                        mService.sendMessage(strString + " : " + str_voucher_no, "GBK");
                        strString = Globals.myRequiredString("Return Date", strLength);
                        mService.sendMessage(strString + " : " + returns.get_date().substring(0,10), "GBK");
                        strString = Globals.myRequiredString("Return Time", strLength);
                        mService.sendMessage(strString + " : " + returns.get_date().substring(11,19), "GBK");
                        strString = Globals.myRequiredString(Globals.PrintDeviceID , strLength);
                        mService.sendMessage(strString + " : " + Globals.objLPD.getDevice_Name(), "GBK");
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        strString = Globals.myRequiredString(Globals.PrintCashier , strLength);
                        mService.sendMessage(strString + " : " + user.get_name(), "GBK");

                        if (Globals.ModeResrv.equals("Resv")) {
                            strString = Globals.myRequiredString("Customer" , strLength);
                            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + Globals.CustomerResrv + "'");
                            mService.sendMessage(strString + " : " + contact.get_name(), "GBK");
                            if (contact.get_gstin().length() > 0) {
                                strString = Globals.myRequiredString("Customer GST No." , strLength);
                                mService.sendMessage(strString + " : " + contact.get_gstin(), "GBK");
                            }
                        } else {
                            if (returns.get_contact_code().equals("")) {
                            } else {
                                strString = Globals.myRequiredString("Customer" , strLength);
                                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                mService.sendMessage(strString + " : " + contact.get_name(), "GBK");
                                if (contact.get_gstin().length() > 0) {
                                    strString = Globals.myRequiredString("Customer GST No." , strLength);
                                    mService.sendMessage(strString + " : " + contact.get_gstin(), "GBK");
                                }
                            }
                        }

                        mService.sendMessage("................................", "GBK");
                        mService.sendMessage("Item Name", "GBK");
                        mService.sendMessage("Qty       Price       Total", "GBK");
                        mService.sendMessage("................................", "GBK");

                        int count = 0;
                        Double itemFinalTax = 0d;
                        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                        while (count < return_details.size()) {

                            String strItemCode = return_details.get(count).get_item_code();
                            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                            String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                    + strItemCode + "'  GROUP By Return_detail.item_Code");
                            String sale_price;
                            Double dDisAfterSalePrice = 0d;
                            dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                            String line_total;
                            line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                            mService.sendMessage("" + strItemName, "GBK");
                            mService.sendMessage("" + return_details.get(count).get_qty() + "       " + sale_price + "       " + line_total, "GBK");

                            Tax_Master tax_master = null;
                            if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                                if (item.get_is_inclusive_tax().equals("0")) {
                                    ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                                    if (order_returndetail_tax.size() > 0) {
                                        if (!returns.get_contact_code().equals("")) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                            itemFinalTax += valueFinal;

                                                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            Globals.AppLogWrite("IteM tAX Name" + strString);
                                                            mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                        }

                                                    }
                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                            itemFinalTax += valueFinal;

                                                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                            Globals.AppLogWrite("IteM tAX Name" + strString);
                                                            mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");


                                                        }
                                                    }
                                                }
                                            }
                                            else{

                                            }

                                        }

                                        else if (returns.get_contact_code().equals("")){
                                            for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                    double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                    itemFinalTax += valueFinal;

                                                    strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    Globals.AppLogWrite("IteM tAX Name" + strString);
                                                    mService.sendMessage(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check), "GBK");
                                                }

                                            }

                                        }
                                    }
                                }
                            }

                            count++;


                        }
                        mService.sendMessage("................................", "GBK");
                        String net_amount;
                        net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                        String strCurrency;
                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                            strCurrency = "";
                        } else {
                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                        }
                        int iTemp=0;
                        Tax_Master tax_master;
                        strString = Globals.myRequiredString("Net Amt" , strLength);
                        mService.sendMessage(strString + " : "+ net_amount + "" + strCurrency, "GBK");
                        String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                        Cursor cursor1 = database.rawQuery(strTableQry, null);

                        if (!returns.get_contact_code().equals("")) {
                            if (contact.getIs_taxable().equals("1")) {
                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                    while (cursor1.moveToNext()) {
                                        // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                            iTemp += 1;
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                            if(tax_master!=null) {
                                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                            }
                                        }
                                    }

                                    //   }
                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                    //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                    while (cursor1.moveToNext()) {

                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("3")) {

                                            iTemp += 1;
                                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                            if(tax_master!=null) {
                                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                            }
                                        }

                                    }
                                    // }
                                }
                            }
                            else{

                            }

                        }

                        else if (returns.get_contact_code().equals("")){
                            // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                            while (cursor1.moveToNext()) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                    iTemp += 1;
                                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                    if(tax_master!=null) {
                                        String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                        mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                    }
                                }
                            }

                            //   }

                        }
                  /*      while (cursor1.moveToNext()) {
                            iTemp += 1;
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                            if(tax_master!=null) {
                                String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "GBK");
                            }
                        }*/

                        Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                        strString = Globals.myRequiredString("Payment Mode", strLength);
                        mService.sendMessage(strString + " : "+ payment.get_payment_name(), "GBK");

                        if (!Globals.objsettings.get_Footer_Text().equals("")||!Globals.objsettings.get_Footer_Text().isEmpty()||Globals.objsettings.get_Footer_Text().length()!=0) {
                            strString = Globals.myRequiredString(Globals.objsettings.get_Footer_Text() , strLength);
                            mService.sendMessage(strString + " : ", "GBK");
                        }
                        mService.sendMessage("................................\n", "GBK");
                        mService.sendMessage("      " + Globals.objsettings.get_Copy_Right() + "\n\n\n\n", "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                    }
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    Globals.strOldCrAmt = "0";
                    flag = "1";
                    Globals.setEmpty();
                } catch (Exception ex) {
                }

        }
        return flag;
    }

    private void mobile_pos(final Returns returns, final ArrayList<Return_detail> return_details) {
        if(Globals.objsettings.get_Print_Lang().equals("0")) {
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strString = "";
                        int strLength = 13;
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                woyouService.printBitmap(bitmap, callback);
                            }

                            woyouService.printTextWithFont(" \n", "", 24, callback);
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
                            woyouService.setFontSize(28, callback);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                            } else {
                                woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }


                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.printTextWithFont("Customer Return\n", "", 28, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(28, callback);
                            strString = Globals.myRequiredString("Return No", strLength);
                            woyouService.printTextWithFont(strString + ":" + str_voucher_no + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Date", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(0, 10) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Time", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(11, 19) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                            woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                            try {
                                //user = User.getUser(getApplicationContext(), " Where user_code='" + returns.get_modified_by() + " \n" + "'", database);
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                                woyouService.printTextWithFont(strString + ":" + user.get_name() + " \n", "", 28, callback);
                            } catch (Exception e) {
                                woyouService.printTextWithFont(e.getMessage()+" \n", "", 28, callback);
                            }
                            try {
                                if (returns.get_contact_code().equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                    strString = Globals.myRequiredString("Customer", strLength);

                                    woyouService.printTextWithFont(strString + ":" + contact.get_name() + "\n", "", 28, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        strString = Globals.myRequiredString("Customer GST No.", strLength);

                                        woyouService.printTextWithFont(strString + ":" + contact.get_gstin() + "\n", "", 28, callback);
                                    }
                                }
                            } catch (Exception e) {
                            }
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("Item Name\n", "", 28, callback);
                            woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{7, 9, 15}, new int[]{0, 0, 0}, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            int count = 0;
                            contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                woyouService.printTextWithFont(strItemName + "\n", "", 28, callback);
                                woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);

                                Double itemFinalTax = 0d;
                                Tax_Master tax_master = null;
                                if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                                    if (item.get_is_inclusive_tax().equals("0")) {
                                        ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                                        if (order_returndetail_tax.size() > 0) {
                                            if (!returns.get_contact_code().equals("")) {
                                                if (contact.getIs_taxable().equals("1")) {
                                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "",28, callback);
                                                            }

                                                        }
                                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "",28, callback);


                                                            }
                                                        }
                                                    }
                                                }
                                                else{

                                                }

                                            }

                                            else if (returns.get_contact_code().equals("")){
                                                for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                        double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                        itemFinalTax += valueFinal;

                                                        strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                        Globals.AppLogWrite("IteM tAX Name" + strString);
                                                        woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "",28, callback);
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }

                                count++;
                            }


                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("Net Amt", strLength);

                            woyouService.printTextWithFont(strString + ":" + net_amount + "" + strCurrency + "\n", "", 28, callback);
                            int iTemp=0;
                            Tax_Master tax_master;
                            String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);


                            if (!returns.get_contact_code().equals("")) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        while (cursor1.moveToNext()) {
                                            // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                                }
                                            }
                                        }

                                        //   }
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                        while (cursor1.moveToNext()) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                                }
                                            }

                                        }
                                        // }
                                    }
                                }
                                else{

                                }

                            }

                            else if (returns.get_contact_code().equals("")){
                                // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                while (cursor1.moveToNext()) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTemp += 1;
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                        if(tax_master!=null) {
                                            String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                            mService.sendMessage(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check), "GBK");
                                        }
                                    }
                                }

                                //   }

                            }


                            Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                            strString = Globals.myRequiredString("Payment Mode", strLength);
                            woyouService.printTextWithFont(strString + ":" + payment.get_payment_name() + "\n", "", 28, callback);

                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(Globals.objsettings.get_Footer_Text(), "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                            }

                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 28, callback);

                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (Globals.PrinterType.equals("9")) {

                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);

                            }
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
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
                        int strLength = 13;
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                woyouService.printBitmap(bitmap, callback);
                            }

                            woyouService.printTextWithFont(" \n", "", 24, callback);
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
                            woyouService.setFontSize(28, callback);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                            } else {
                                woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }


                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.printTextWithFont( "  عودة العملاء"+"\n" , "", 28, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(28, callback);
                            strString = Globals.myRequiredString("عودة لا", strLength);
                            woyouService.printTextWithFont(strString + ":" + str_voucher_no + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("تاريخ العودة", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(0, 10) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("وقت العودة", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(11, 19) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("معرف الجهاز", strLength);
                            woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                            try {
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + " \n" + "'", database);
                                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                                woyouService.printTextWithFont(strString + ":" + user.get_name() + " \n", "", 28, callback);
                            } catch (Exception e) {

                            }
                            try {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                    strString = Globals.myRequiredString("Customer", strLength);

                                    woyouService.printTextWithFont(strString + ":" + contact.get_name() + "\n", "", 28, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        strString = Globals.myRequiredString("Customer GST No.", strLength);

                                        woyouService.printTextWithFont(strString + ":" + contact.get_gstin() + "\n", "", 28, callback);
                                    }
                                }
                            } catch (Exception e) {
                            }
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("اسم العنصر" +"\n", "", 28, callback);
                            woyouService.printColumnsText(new String[]{"الكمية", "السعر", "مجموع"+"\n"}, new int[]{10, 15, 15}, new int[]{0, 0, 0}, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            int count = 0;
                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                woyouService.printTextWithFont(strItemName + "\n", "", 28, callback);
                                woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{8, 10, 10}, new int[]{0, 0, 0}, callback);
                                count++;
                            }


                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("صافي المبلغ", strLength);

                            woyouService.printTextWithFont(strString + ":" + net_amount + "" + strCurrency + "\n", "", 28, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(Globals.objsettings.get_Footer_Text(), "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                            }

                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 28, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (Globals.PrinterType.equals("9")) {

                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);

                            }

                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void ppt8527(final Returns returns,final ArrayList<Return_detail>return_details){
        try {
            String strString = "";
            int strLength = 15;

            for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                JSONArray printTest = new JSONArray();
                timeTools = new TimerCountTools();
                timeTools.start();
                ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                String Print_type = "0";
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));

                } else {
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

                printTest.put(getPrintObject("Customer Return", "3", "center"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));

                strString = Globals.myRequiredString("Return No", strLength);
                printTest.put(getPrintObject(strString + ":"+ str_voucher_no, "2", "left"));
                strString = Globals.myRequiredString("Return Date", strLength);

                printTest.put(getPrintObject(strString + ":" + returns.get_date().substring(0, 10), "2", "left"));
                strString = Globals.myRequiredString("Return Time", strLength);
                printTest.put(getPrintObject(strString + ":" + returns.get_date().substring(11, 19), "2", "left"));
                strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                printTest.put(getPrintObject(strString + ":" + Globals.objLPD.getDevice_Name(), "2", "left"));
                try {
                    user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                    printTest.put(getPrintObject(strString + ":" + user.get_name(), "2", "left"));
                }
                catch(Exception e){

                }

                try {
                    if (returns.get_contact_code().equals("")) {
                    } else {
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                        strString = Globals.myRequiredString("Customer", strLength);
                        printTest.put(getPrintObject(strString + ":" + contact.get_name(), "2", "left"));

                        if (contact.get_gstin().length() > 0) {
                            strString = Globals.myRequiredString("Customer GST No.", strLength);
                            printTest.put(getPrintObject(strString + ":" + contact.get_gstin(), "2", "left"));

                        }
                    }
                } catch (Exception e) {
                }

                printTest.put(getPrintObject("--------------------------------", "2", "left"));

                printTest.put(getPrintObject("Item Name \n", "2", "left"));

                printTest.put(getPrintObject("Qty       Price       Total\n", "2", "left"));

                printTest.put(getPrintObject("--------------------------------", "2", "left"));

                int count = 0;
                Double itemFinalTax = 0d;
                double finalitemTax=0d;
                Tax_Master tax_master=null;
                while (count < return_details.size()) {
                    String strItemCode = return_details.get(count).get_item_code();
                    String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                            + strItemCode + "'  GROUP By Return_detail.item_Code");
                    String sale_price;
                    Double dDisAfterSalePrice = 0d;

                    dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                    String line_total;
                    line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                    printTest.put(getPrintObject(strItemName, "2", "left"));
                    printTest.put(getPrintObject(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check) + "            " + sale_price + "      " + line_total, "2", "left"));
                    ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no()+ "'", database);
                    if(order_returndetail_tax.size()>0) {
                        for (int i = 0; i < order_returndetail_tax.size(); i++) {
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                            double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                            itemFinalTax += valueFinal;

                            strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                            Globals.AppLogWrite("IteM tAX Name" + strString);
                            printTest.put(getPrintObject(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "2", "left"));
                        }
                    }
                    count++;
                }

                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }
                strString = Globals.myRequiredString("Net Amt", strLength);
                printTest.put(getPrintObject(strString + ":" + net_amount + "" + strCurrency, "2", "left"));
                int iTemp=0;
                String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                Cursor cursor1 = database.rawQuery(strTableQry, null);

                while (cursor1.moveToNext()) {
                    iTemp += 1;
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                    if(tax_master!=null) {
                        String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                        printTest.put(getPrintObject(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "2", "left"));
                    }
                }


                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                strString = Globals.myRequiredString("Payment Mode", strLength);
                printTest.put(getPrintObject(strString + ":" + net_amount + "" +  payment.get_payment_name(), "2", "left"));
                printTest.put(getPrintObject("--------------------------------", "2", "left"));
                printTest.put(getPrintObject(Globals.objsettings.get_Copy_Right(), "2", "center"));
                printJson.put("spos", printTest);
                ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
                ServiceManager.getInstence().getPrinter().printBottomFeedLine(5);
            }
        }catch(Exception e){}


    }

    private void PHA_POS(final Returns returns, final ArrayList<Return_detail> return_details) {
        if(Globals.objsettings.get_Print_Lang().equals("0")) {
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strString = "";
                        int strLength = 18;
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                woyouService.printBitmap(bitmap, callback);
                            }

                            woyouService.printTextWithFont(" \n", "", 24, callback);
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
                            woyouService.setFontSize(28, callback);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                            } else {
                                woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }


                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.printTextWithFont("Customer Return\n", "", 28, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(28, callback);
                            strString = Globals.myRequiredString("Return No", strLength);
                            woyouService.printTextWithFont(strString + ":" + str_voucher_no + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Date", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(0, 10) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Time", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(11, 19) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString(Globals.PrintDeviceID, strLength);
                            woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                            try {
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + " \n" + "'", database);
                                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                                woyouService.printTextWithFont(strString + ":" + user.get_name() + " \n", "", 28, callback);
                            } catch (Exception e) {

                            }
                            try {
                                if (returns.get_contact_code().equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                    strString = Globals.myRequiredString("Customer", strLength);

                                    woyouService.printTextWithFont(strString + ":" + contact.get_name() + "\n", "", 28, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        strString = Globals.myRequiredString("Customer GST No.", strLength);

                                        woyouService.printTextWithFont(strString + ":" + contact.get_gstin() + "\n", "", 28, callback);
                                    }
                                }
                            } catch (Exception e) {
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("Item Name\n", "", 28, callback);
                            woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            int count = 0;
                            Tax_Master tax_master=null;
                            Double itemFinalTax = 0d;
                            double finalitemTax=0d;
                            contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + strItemCode + "'", database, db);

                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                woyouService.printTextWithFont(strItemName + "\n", "", 28, callback);
                                woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
   /*                             if(order_returndetail_tax.size()>0) {
                                    for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                        double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                        itemFinalTax += valueFinal;

                                        strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                        Globals.AppLogWrite("IteM tAX Name" + strString);
                                        woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "", 28, callback);
                                    }
                                }*/
                                if (Globals.objsettings.get_ItemTax().equals("1") || Globals.objsettings.get_ItemTax().equals("3")) {
                                    if (item.get_is_inclusive_tax().equals("0")) {
                                        ArrayList<Return_detail_tax> order_returndetail_tax = Return_detail_tax.getAllReturn_detail_tax(getApplicationContext(), "WHERE item_code='" + return_details.get(count).get_item_code() + "' And order_return_voucher_no = '" + return_details.get(count).get_ref_voucher_no() + "'", database);
                                        if (order_returndetail_tax.size() > 0) {
                                            if (!returns.get_contact_code().equals("")) {
                                                if (contact.getIs_taxable().equals("1")) {
                                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "", 28, callback);
                                                            }

                                                        }
                                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                        for (int i = 0; i < order_returndetail_tax.size(); i++) {


                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                            if (sys_tax_group.get_tax_master_id().equals("3")) {
                                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                                double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                                itemFinalTax += valueFinal;

                                                                strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                                Globals.AppLogWrite("IteM tAX Name" + strString);
                                                                woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "", 28, callback);


                                                            }
                                                        }
                                                    }
                                                }
                                                else{

                                                }

                                            }

                                            else if (returns.get_contact_code().equals("")){
                                                for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_returndetail_tax.get(i).getTax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + order_returndetail_tax.get(i).getTax_id() + "'", database, db);
                                                        double valueFinal = Double.parseDouble(order_returndetail_tax.get(i).getTax_value()) * (Double.parseDouble(return_details.get(count).get_qty()));
                                                        itemFinalTax += valueFinal;

                                                        strString = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                        Globals.AppLogWrite("IteM tAX Name" + strString);
                                                        woyouService.printTextWithFont(strString + Globals.myNumberFormat2Price(valueFinal, decimal_check) + "\n", "", 28, callback);
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }


                                count++;
                            }


                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("Net Amt", strLength);

                            woyouService.printTextWithFont(strString + ":" + net_amount + "" + strCurrency + "\n", "", 28, callback);

                            int iTemp=0;
                            String  strTableQry = "select return_detail_tax.tax_id,SUM(return_detail_tax.tax_value * return_detail.qty) As Amt from return_detail_tax left join return_detail on return_detail.ref_voucher_no = return_detail_tax.order_return_voucher_no and  return_detail.item_code = return_detail_tax.item_code where return_detail.ref_voucher_no ='" + str_voucher_no + "' group by  return_detail_tax.tax_id";
                            Cursor cursor1 = database.rawQuery(strTableQry, null);

                /*            while (cursor1.moveToNext()) {
                                iTemp += 1;
                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                if(tax_master!=null) {
                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                    woyouService.printTextWithFont(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "", 28, callback);
                                }
                            }*/

                            if (!returns.get_contact_code().equals("")) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        while (cursor1.moveToNext()) {
                                            // for (int i = 0; i < order_returndetail_tax.size(); i++) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    woyouService.printTextWithFont(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "", 28, callback);
                                                }
                                            }
                                        }

                                        //   }
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        //   for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                        while (cursor1.moveToNext()) {

                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTemp += 1;
                                                tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                                if(tax_master!=null) {
                                                    String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                                    woyouService.printTextWithFont(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "", 28, callback);
                                                }
                                            }

                                        }
                                        // }
                                    }
                                }
                                else{

                                }

                            }

                            else if (returns.get_contact_code().equals("")){
                                // for (int i = 0; i < order_returndetail_tax.size(); i++) {
                                while (cursor1.moveToNext()) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + cursor1.getString(0) + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTemp += 1;
                                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);
                                        if(tax_master!=null) {
                                            String strPaymentName = Globals.myRequiredString(tax_master.get_tax_name(), strLength);
                                            woyouService.printTextWithFont(strPaymentName + " : " + Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) + "\n", "", 28, callback);
                                        }
                                    }
                                }

                                //   }

                            }


                            Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                            strString = Globals.myRequiredString("Payment Mode", strLength);
                            woyouService.printTextWithFont(strString + ":" + payment.get_payment_name() + "\n", "", 28, callback);

                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(Globals.objsettings.get_Footer_Text(), "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                            }

                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 28, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.cutPaper(callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
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
                        int strLength = 18;
                        for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                woyouService.printBitmap(bitmap, callback);
                            }

                            woyouService.printTextWithFont(" \n", "", 24, callback);
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
                            woyouService.setFontSize(28, callback);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                            } else {
                                woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }


                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.printTextWithFont(  " عودة العملاء" +"\n", "", 28, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(28, callback);
                            strString = Globals.myRequiredString("عودة لا" +"\n", strLength);
                            woyouService.printTextWithFont(strString + ":" + str_voucher_no + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("تاريخ العودة" + "\n", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(0, 10) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString(" وقت العودة"+ "\n", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(11, 19) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("معرف الجهاز", strLength);
                            woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                            try {
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + " \n" + "'", database);
                                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                                woyouService.printTextWithFont(strString + ":" + user.get_name() + " \n", "", 28, callback);
                            } catch (Exception e) {

                            }
                            try {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                    strString = Globals.myRequiredString("الزبون", strLength);

                                    woyouService.printTextWithFont(strString + ":" + contact.get_name() + "\n", "", 28, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        strString = Globals.myRequiredString(  " رقم ضريبة السلع والخدمات للعميل" + "\n", strLength);

                                        woyouService.printTextWithFont(strString + ":" + contact.get_gstin() + "\n", "", 28, callback);
                                    }
                                }
                            } catch (Exception e) {
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("اسم العنصر"+"\n", "", 28, callback);
                            woyouService.printColumnsText(new String[]{"الكمية", "السعر",  "مجموع" +"\n"}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            int count = 0;
                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                woyouService.printTextWithFont(strItemName + "\n", "", 28, callback);
                                woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
                                count++;
                            }


                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("صافي المبلغ" + "\n", strLength);

                            woyouService.printTextWithFont(strString + ":" + net_amount + "" + strCurrency + "\n", "", 28, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(Globals.objsettings.get_Footer_Text(), "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                            }

                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 28, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.cutPaper(callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
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
                            woyouService.setAlignment(1, callback);
                            Bitmap bitmap = StringToBitMap(Globals.objsettings.get_Logo());
                            if (bitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                                bitmap = getResizedBitmap(bitmap, 80, 120);
                                woyouService.printBitmap(bitmap, callback);
                            }

                            woyouService.printTextWithFont(" \n", "", 24, callback);
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
                            woyouService.setFontSize(28, callback);
                            if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {
                            } else {
                                woyouService.printColumnsText(new String[]{Globals.GSTNo, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }


                            if (Globals.strIsBarcodePrint.equals("true")) {
                                woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                                woyouService.printTextWithFont(" \n", "", 24, callback);
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.printTextWithFont("Customer Return/عودة العملاء\n", "", 28, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(28, callback);
                            strString = Globals.myRequiredString("Return No/عودة لا", strLength);
                            woyouService.printTextWithFont(strString + ":" + str_voucher_no + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Date/تاريخ العودة", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(0, 10) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString("Return Time/وقت العودة", strLength);
                            woyouService.printTextWithFont(strString + ":" + returns.get_date().substring(11, 19) + " \n", "", 28, callback);
                            strString = Globals.myRequiredString(Globals.PrintDeviceID +"معرف الجهاز/", strLength);
                            woyouService.printTextWithFont(strString + ":" + Globals.objLPD.getDevice_Name() + "\n", "", 28, callback);
                            try {
                                user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + " \n" + "'", database);
                                strString = Globals.myRequiredString(Globals.PrintCashier, strLength);
                                woyouService.printTextWithFont(strString + ":" + user.get_name() + " \n", "", 28, callback);
                            } catch (Exception e) {

                            }
                            try {
                                if (Globals.strContact_Code.equals("")) {
                                } else {
                                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                                    strString = Globals.myRequiredString("Customer/الزبون", strLength);

                                    woyouService.printTextWithFont(strString + ":" + contact.get_name() + "\n", "", 28, callback);
                                    if (contact.get_gstin().length() > 0) {
                                        strString = Globals.myRequiredString("Customer GST No./رقم ضريبة السلع والخدمات للعميل", strLength);

                                        woyouService.printTextWithFont(strString + ":" + contact.get_gstin() + "\n", "", 28, callback);
                                    }
                                }
                            } catch (Exception e) {
                            }
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("Item Name/اسم العنصر\n", "", 28, callback);
                            woyouService.printColumnsText(new String[]{"Qty/الكمية", "Price/السعر", "Total/مجموع"}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            int count = 0;
                            while (count < return_details.size()) {
                                String strItemCode = return_details.get(count).get_item_code();
                                String strItemName = Return_detail.getItemNameReturn(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String strItemNameL = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                                        + strItemCode + "'  GROUP By Return_detail.item_Code");
                                String sale_price;
                                Double dDisAfterSalePrice = 0d;

                                dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                                String line_total;
                                line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                                woyouService.printTextWithFont(strItemName + "\n", "", 28, callback);
                                woyouService.printTextWithFont(strItemNameL + "\n", "", 28, callback);
                                woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{15, 17, 15}, new int[]{0, 0, 0}, callback);
                                count++;
                            }


                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                            String net_amount;
                            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                            String strCurrency;
                            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                                strCurrency = "";
                            } else {
                                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                            }
                            strString = Globals.myRequiredString("Net Amt/صافي المبلغ", strLength);

                            woyouService.printTextWithFont(strString + ":" + net_amount + "" + strCurrency + "\n", "", 28, callback);
                            woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            if (!Globals.objsettings.get_Footer_Text().equals("")) {
                                woyouService.printTextWithFont(Globals.objsettings.get_Footer_Text(), "", 24, callback);
                                woyouService.printTextWithFont("\n", "", 24, callback);
                            }

                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 28, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.cutPaper(callback);
                        }
                        Globals.strContact_Code = "";
                        Globals.strResvContact_Code = "";
                        Globals.strOldCrAmt = "0";
                        Globals.setEmpty();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void autocomplete(String strFilter) {
        strFilter = " and ( item_code Like '" + strFilter + "%'  Or item_name Like '" + strFilter + "%' )";
        arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), " WHERE is_active = '1' " + strFilter + " limit 10");
        if (arrayList1.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, R.layout.items_spinner, arrayList1);
            edt_toolbar_item_code.setThreshold(0);
            edt_toolbar_item_code.setAdapter(adapter);
        }
    }


    private String send_online_return() {
        String result = Returns.sendOnServer(pDialog,getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'", Globals.license_id, serial_no, android_id, myKey);
        return result;
    }

    private String send_online_return_single(ProgressDialog pDialog,String str_voucherno) {
        String result = Returns.sendOnServer(pDialog,getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false' and voucher_no = '" + str_voucherno + "'", Globals.license_id, serial_no, android_id, myKey);
        return result;
    }

    private String get_total() {
        Double total = 0d;
        try {
            if (arraylist.size() > 0) {
                for (int i = 0; i < arraylist.size(); i++) {
                    total = total + Double.parseDouble(arraylist.get(i).getLine_total());
                }
            }
        } catch (Exception e) {
        }
        return total + "";
    }

    private String stock_cancel() {
        String suc = "0";
        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        returns.set_is_cancel("true");
        long l = returns.updateReturns("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_post() {
        String suc = "0";
        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        returns.set_is_post("true");
        long l = returns.updateReturns("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }



        return suc;
    }

    private String stock_delete() {
        String suc = "0";
        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        returns.set_is_active("0");
        long l = returns.updateReturns("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }


    private String stock_save() {
        String suc = "0";
        database.beginTransaction();
        try {

            String total = get_total();
            returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
            if (returns == null) {
                returns = new Returns(getApplicationContext(), null, cusCode, str_voucher_no, str_date, str_remarks, total, "0", "false", "false", "1", "N", Globals.user, date, "", "CR", PayId);
                long l = returns.insertReturns(database);
                if (l > 0) {
                    suc = "1";
                    for (int i = 0; i < arraylist.size(); i++) {
                        return_detail = new Return_detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getPrice(), arraylist.get(i).getLine_total());
                        long l1 = return_detail.insertReturn_detail(database);
                        if (l1 > 0) {
                            suc = "1";
                            ArrayList<Return_Item_Tax> return_item_tax = Globals.return_item_tax;
                            Return_detail_tax objreturnDetailTax;
                            for (int cnt = 0; cnt < return_item_tax.size(); cnt++) {
                                Return_Item_Tax retItemTax = return_item_tax.get(cnt);

                                objreturnDetailTax = new Return_detail_tax(getApplicationContext(), null, str_voucher_no, retItemTax.getSr_no(), retItemTax.getItem_code(), retItemTax.getTax_id()
                                        , retItemTax.getTax_type(), retItemTax.getRate(), retItemTax.getTax_value());

                                long o = objreturnDetailTax.insertReturn_detail_tax(database);
                                if (o > 0) {
                                    suc = "1";
                                } else {
                                }
                            }

                        }
                    }
                }
            } else {
                returns = new Returns(getApplicationContext(), returns.get_id(), cusCode, str_voucher_no, str_date, str_remarks, total, returns.get_z_code(), returns.get_is_post(), returns.get_is_cancel(), "1", "N", Globals.user, date, "", "CR", PayId);
                long l = returns.updateReturns("voucher_no=?", new String[]{str_voucher_no}, database);
                if (l > 0) {
                    suc = "1";
                    long e6 = Return_detail.delete_Return_detail(getApplicationContext(), "return_detail", " ref_voucher_no =? ", new String[]{str_voucher_no}, database);
                    for (int i = 0; i < arraylist.size(); i++) {
                        return_detail = new Return_detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getPrice(), arraylist.get(i).getLine_total());
                        long l1 = return_detail.insertReturn_detail(database);
                        if (l1 > 0) {
                            suc = "1";
                            ArrayList<Return_Item_Tax> return_item_tax = Globals.return_item_tax;
                            Return_detail_tax objreturnDetailTax;
                            for (int cnt = 0; cnt < return_item_tax.size(); cnt++) {
                                Return_Item_Tax retItemTax = return_item_tax.get(cnt);

                                objreturnDetailTax = new Return_detail_tax(getApplicationContext(), null, str_voucher_no, retItemTax.getSr_no(), retItemTax.getItem_code(), retItemTax.getTax_id()
                                        , retItemTax.getTax_type(), retItemTax.getRate(), retItemTax.getTax_value());

                                long o = objreturnDetailTax.insertReturn_detail_tax(database);
                                if (o > 0) {
                                    suc = "1";
                                } else {
                                }
                            }
                        }
                    }
                }
            }

            if (suc.equals("1")) {
                // Accounts affect here
                Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + cusCode + "'", database);
                Double strOldBalance = 0d;
                Double strAmount = 0d;
                if (acc_customer == null) {
                    strAmount = strOldBalance + Double.parseDouble(returns.get_total());
                    acc_customer = new Acc_Customer(getApplicationContext(), null, cusCode, strAmount + "");
                    acc_customer.insertAcc_Customer(database);
                } else {
                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                    strAmount = strOldBalance + Double.parseDouble(returns.get_total());
                    acc_customer.set_amount(strAmount + "");
                    long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{cusCode}, database);
                }


                database.setTransactionSuccessful();
                database.endTransaction();

                if(Globals.PrinterType.equals("11")){
                    pdfPerform_80mm();

                }
                else {
                    performPDFExport();
                }

               /* runOnUiThread(new Runnable() {
                    public void run() {*/

             /*       }
                });*/

            }

        } catch (Exception ex) {

        }
        return suc;
    }

    private void list_load(final ArrayList<StockAdjectmentDetailList> arraylist) {
        try {
            ListView list = (ListView) findViewById(R.id.list);
            if (arraylist.size() > 0) {
                returnFinalListAdapter = new CusReturnFinalListAdapter(CusReturnFinalActivity.this, arraylist);
                list.setVisibility(View.VISIBLE);
                list.setAdapter(returnFinalListAdapter);
                returnFinalListAdapter.notifyDataSetChanged();
                ///setQuantityPrice(arraylist.get());
                setQuantityPrice();
            } else {
                list.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_qr);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retail, menu);
        menu.setGroupVisible(R.id.grp_retail, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Add button  click  on after search menu
        if (id == R.id.action_settings) {
            String strValue = edt_toolbar_item_code.getText().toString();
            if (edt_toolbar_item_code.getText().toString().equals("\n") || edt_toolbar_item_code.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                edt_toolbar_item_code.requestFocus();
            } else {
                String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                arrayListItem = Item.getAllItem(getApplicationContext(), strWhere, database);
                if (arrayListItem.size() >= 1) {
                    strupdate = "";
                    resultp = arrayListItem.get(0);
                    item_code = resultp.get_item_code();
                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                    if (item_location == null) {
                        sale_priceStr = "0";
                    } else {
                        sale_priceStr = item_location.get_selling_price();
                    }
                    String item_price;
                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                    edt_name.setText(resultp.get_item_name());
                    edt_price.setText(item_price);
                    edt_qty.setText("1");
                    edt_toolbar_item_code.setText("");
                    closeKeyboard();
                } else {
                    edt_toolbar_item_code.selectAll();
                    Toast.makeText(getApplicationContext(), getString(R.string.nodatafound), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTextView(String position, String item_code, String item_name, String qty, String price, String strUpdate) {
        try {
            Position = Integer.parseInt(position);
            strupdate = strUpdate;
            strItemCode = item_code;
            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check);
            edt_price.setText(item_price);
            edt_name.setText(item_name);
            edt_qty.setText(qty);
            Double dbqty= (Globals.ReturnTotalQty)-(Double.parseDouble(qty));
            Double dbprice= (Globals.ReturnTotalPrice)-(Double.parseDouble(item_price));

            Globals.ReturnTotalQty=dbqty;
            Globals.ReturnTotalPrice=dbprice;
            /*Globals.ReturnTotalQty =Double.parseDouble(edt_qty.getText().toString())+ Double.parseDouble(arraylist.get(Position).getQty());
            Globals.ReturnTotalPrice =  Double.parseDouble(String.valueOf(item_price));*/
           // setQuantityPrice();
        } catch (Exception ex) {
        }


    }

    public void setTextView(String position,String qty,String price, String strupdates) {
        try {

            Position = Integer.parseInt(position);
           // strupdate = strupdates;

            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check);
            edt_price.setText("");
            edt_name.setText("");
            edt_qty.setText("");
            Double dbqty= (Globals.ReturnTotalQty)-(Double.parseDouble(qty));
            Double dbprice= (Globals.ReturnTotalPrice)-(Double.parseDouble(item_price));

            Globals.ReturnTotalQty=dbqty;
            Globals.ReturnTotalPrice=dbprice;
            btn_qty.setText(String.valueOf(Globals.myNumberFormat2Price(Globals.ReturnTotalQty,decimal_check)));
            btn_price.setText(String.valueOf(Globals.myNumberFormat2Price(Globals.ReturnTotalPrice,decimal_check)));

        } catch (Exception ex) {
        }


    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(CusReturnFinalActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(100);
                    pDialog.dismiss();
                    if (!operation.equals("Edit")) {
                        Globals.ReturnTotalPrice = 0.0;
                        Globals.ReturnTotalQty = 0.0;
                    }
                    Intent intent = new Intent(CusReturnFinalActivity.this, CusReturnHeaderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
                    intent.putExtra("contact_code", cusCode);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        timerThread.start();
    }

    private String stock_update() {
        String suc = "0";
        try {
            returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
            if (returns == null) {
            } else {
                if (returns.get_is_post().equals("true")) {
                    ArrayList<Return_detail> return_detailArrayList = Return_detail.getAllReturn_detail(getApplicationContext(), " where ref_voucher_no='" + str_voucher_no + "'", database);
                    if (return_detailArrayList.size() > 0) {
                        for (int i = 0; i < return_detailArrayList.size(); i++) {
                            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "where item_code='" + return_detailArrayList.get(i).get_item_code() + "'", database);
                            Double updatedQty = 0d;
                            if (item_location != null) {
                                Double avlQty = Double.parseDouble(item_location.get_quantity());
                                Double effectiveQty = Double.parseDouble(return_detailArrayList.get(i).get_qty());

                                updatedQty = avlQty + effectiveQty;

                                item_location.set_quantity(updatedQty + "");
                                long l = item_location.updateItem_Location("item_code=?", new String[]{return_detailArrayList.get(i).get_item_code()}, database);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return suc;
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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
                    CusReturnFinalActivity.this.finish();
                }
            } catch (Exception e) {
            }

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
            dialog = new ProgressDialog(CusReturnFinalActivity.this);
            dialog.setCancelable(false);
            dialog.show();
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
                Toast.makeText(CusReturnFinalActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(CusReturnFinalActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(CusReturnFinalActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
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
    public void setQuantityPrice() {
        if (arraylist != null) {
            Globals.ReturnTotalQty = Globals.ReturnTotalQty + Double.parseDouble(edt_qty.getText().toString());
           String qty= edt_qty.getText().toString();
            float quantity= Float.parseFloat(qty);
            String price= edt_price.getText().toString();

            float flprice= Float.parseFloat(price);

            float totalprice=quantity*flprice;
           // resu.setText(String.valueOf(res));
//String totalprice= String.valueOf(edt_qty.getText().toString()) * price;
            Globals.ReturnTotalPrice = Globals.ReturnTotalPrice + Double.parseDouble(String.valueOf(totalprice));

            // setQuantityPrice();
            String returnqty = Globals.myNumberFormat2Price(Globals.ReturnTotalQty, decimal_check);
            String returntotal_price = Globals.myNumberFormat2Price(Globals.ReturnTotalPrice, decimal_check);

            btn_qty.setText(returnqty);
            btn_price.setText(returntotal_price);
        }
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


    protected void performPDFExport() {
        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        ArrayList<Return_detail> return_detailArrayList = Return_detail.getAllReturn_detail(getApplicationContext(), " where ref_voucher_no='" + str_voucher_no + "'", database);

        int count = 0;
        while (count < return_detailArrayList.size()) {

            String item_code = return_detailArrayList.get(count).get_item_code();
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
            String strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                    + strItemCode + "'  GROUP By Return_detail.item_Code");

            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                list1a.add(item.get_item_name());
            }
            else if(Globals.objsettings.get_Print_Lang().equals("1")){
                list1a.add(item.get_item_name());

            }
            else if(Globals.objsettings.get_Print_Lang().equals("2")){
                list1a.add(item.get_item_name());

            }

            list2a.add(return_detailArrayList.get(count).get_qty());
            list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(return_detailArrayList.get(count).get_price()), decimal_check));
            list4a.add(Globals.myNumberFormat2Price(Double.parseDouble(return_detailArrayList.get(count).get_line_total()), decimal_check));

            count++;
        }
        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + str_voucher_no + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
         //   writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
//            image = createimage();

            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.customerreturn), B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(1);
                PdfPCell cell_company_name;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    cell_company_name =new PdfPCell(new Paragraph(Globals.objLPR.getShort_companyname(), B10));
                } else {
                    cell_company_name =new PdfPCell(new Paragraph(Globals.objLPR.getShort_companyname(), B10));
                }
                cell_company_name.setColspan(1);

                cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell_company_name.setPadding(5.0f);
                table_company_name.addCell(cell_company_name);

document.open();
                PdfPTable table_address = new PdfPTable(1);
                PdfPCell cell_adress;

                    cell_adress =new PdfPCell(new Paragraph(Globals.objLPR.getAddress(), B10));


                // cell_adress.setColspan(1);

                cell_adress.setHorizontalAlignment(Element.ALIGN_CENTER);

                document.open();
                table_address.addCell(cell_adress);

                PdfPTable table_mobile = new PdfPTable(1);
                PdfPCell cell_mobile;
                cell_mobile =new PdfPCell(new Paragraph(Globals.objLPR.getMobile_No(), B10));

                //  cell_mobile.setColspan(1);

                cell_mobile.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_mobile.addCell(cell_mobile);

                document.open();
                PdfPTable table_posno = new PdfPTable(2);
                Phrase prreturnno = new Phrase(getString(R.string.returnno), B10);

                PdfPCell cell_posno = new PdfPCell(prreturnno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prreturnno = new Phrase("" + str_voucher_no, B12);
                PdfPCell returncell = new PdfPCell(prreturnno);
                returncell.setPadding(5);
                returncell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_posno.addCell(returncell);

                // document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prreturndatetime = new Phrase(getString(R.string.returndate), B12);

                PdfPCell cell_order_no = new PdfPCell(prreturndatetime);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prreturndatetime = new Phrase("" + returns.get_date(), B12);
                PdfPCell returndatecell = new PdfPCell(prreturndatetime);
                returndatecell.setPadding(5);
                returndatecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_no.addCell(returndatecell);
                //document.open();

                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");

                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prreturncustomer = new Phrase(getString(R.string.customer), B12);

                PdfPCell cell_order_date = new PdfPCell(prreturncustomer);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);

                prreturncustomer = new Phrase("" + contact.get_name(), B12);
                PdfPCell returndatecustomr = new PdfPCell(prreturncustomer);
                returndatecustomr.setPadding(5);
                returndatecustomr.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_date.addCell(returndatecustomr);


                PdfPTable table = new PdfPTable(4);

                Phrase pr = new Phrase(getString(R.string.ItemName), B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setPadding(5);
                table.addCell(c1);
                pr = new Phrase(getString(R.string.Quantity), B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);
                pr = new Phrase(getString(R.string.Price), B10);
                PdfPCell c2 = new PdfPCell(pr);
                c2.setPadding(5);
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c2);
                pr = new Phrase(getString(R.string.Total), B10);
                c3 = new PdfPCell(pr);
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

                    String total_amount;
                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    pr1 = new Phrase(total_amount, N9);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);
                }
                table.setSpacingBefore(10.0f);
                table.setHeaderRows(1);
                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }
                PdfPTable table_recbalance = new PdfPTable(1);
                PdfPCell cell_recbal = new PdfPCell(new Paragraph(getString(R.string.netamount) + " : " + net_amount + strCurrency, B12));
                cell_recbal.setColspan(1);
                cell_recbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_recbal.setPadding(5.0f);
                table_recbalance.addCell(cell_recbal);

                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");

                PdfPTable table_currentbalance = new PdfPTable(1);
                PdfPCell cell_currentbal = new PdfPCell(new Paragraph(getString(R.string.paymentmode) + " : " + payment.get_payment_name(), B12));
                cell_currentbal.setColspan(1);
                cell_currentbal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_currentbal.setPadding(5.0f);
                table_currentbalance.addCell(cell_currentbal);
                document.open();


                document.add(table_company_name);
                document.add(table_address);
                document.add(table_mobile);
                 document.add(tableh);

                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table);
                document.add(table_recbalance);
                document.add(table_currentbalance);
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
                PdfPCell cellh = new PdfPCell(new Paragraph("مبيعات المرتجع", B12));
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

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prreturnno = new Phrase("المرتجع الفاتورة", B10);
                table_posno.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_posno = new PdfPCell(prreturnno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prreturnno = new Phrase("" + str_voucher_no, B10E);
                PdfPCell returncell = new PdfPCell(prreturnno);
                returncell.setPadding(5);
                returncell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(returncell);

                // document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prreturndatetime = new Phrase("رجع التاريخ والوقت", B12);
                table_order_no.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_no = new PdfPCell(prreturndatetime);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prreturndatetime = new Phrase("" + returns.get_date(), B10E);
                PdfPCell returndatecell = new PdfPCell(prreturndatetime);
                returndatecell.setPadding(5);
                returndatecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(returndatecell);
                //document.open();

                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");

                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prreturncustomer = new Phrase("العميل", B12);
                table_order_date.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_date = new PdfPCell(prreturncustomer);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);

                prreturncustomer = new Phrase("" + contact.get_name(), B10E);
                PdfPCell returndatecustomr = new PdfPCell(prreturncustomer);
                returndatecustomr.setPadding(5);
                returndatecustomr.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_date.addCell(returndatecustomr);


                PdfPTable table = new PdfPTable(4);
                table.setRunDirection(writer.RUN_DIRECTION_RTL);
                Phrase pr = new Phrase("اسم المنتج", B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setPadding(5);
                table.addCell(c1);
                pr = new Phrase("كمية", B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);
                pr = new Phrase("سعر", B10);
                PdfPCell c2 = new PdfPCell(pr);
                c2.setPadding(5);
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c2);
                pr = new Phrase("مجموع", B10);
                c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);
                for (int i = 0; i < list1a.size(); i++) {
                    Phrase pr1 = new Phrase(list1a.get(i), B10E);
                    PdfPCell c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c7);

                    pr1 = new Phrase(list2a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);

                    pr1 = new Phrase(list3a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);

                    String total_amount;
                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    pr1 = new Phrase(total_amount, B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);
                }
                table.setSpacingBefore(10.0f);
                table.setHeaderRows(1);
                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }

                PdfPTable table_netamnt = new PdfPTable(2);
                Phrase prnetamnt= new Phrase("صافي المبلغ", B12);
                table_netamnt.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_netamnt = new PdfPCell(prnetamnt);
                cell_netamnt.setColspan(1);
                cell_netamnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_netamnt.setPadding(5.0f);
                table_netamnt.addCell(cell_netamnt);

                prnetamnt = new Phrase("" + net_amount + strCurrency, B10E);
                PdfPCell netamnt = new PdfPCell(prnetamnt);
                netamnt.setPadding(5);
                netamnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_netamnt.addCell(netamnt);
                table.setSpacingBefore(10.0f);

                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                PdfPTable table_payment = new PdfPTable(2);
                Phrase prtablepayment= new Phrase("الدفع نوع", B12);
                table_payment.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_payment = new PdfPCell(prtablepayment);
                cell_payment.setColspan(1);
                cell_payment.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_payment.setPadding(5.0f);
                table_payment.addCell(cell_payment);

                prtablepayment = new Phrase("" + payment.get_payment_name(), B10E);
                PdfPCell paymentcell = new PdfPCell(prtablepayment);
                paymentcell.setPadding(5);
                paymentcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_payment.addCell(paymentcell);
                document.open();

                document.add(tableh);
                document.add(table_company_name);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table);
                document.add(table_netamnt);
                document.add(table_payment);

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
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.BOLD);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph("عودة العملاء", B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(2);

                Phrase prcommpanyname = new Phrase("اسم الشركة", B10);
                prcommpanyname.add(new Chunk("/Company Name",B10E));
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

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prreturnno = new Phrase("المرتجع الفاتورة", B10);
                prreturnno.add(new Chunk("/Return NO",B10E));

                table_posno.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_posno = new PdfPCell(prreturnno);
                cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prreturnno = new Phrase("" + str_voucher_no, B10E);
                PdfPCell returncell = new PdfPCell(prreturnno);
                returncell.setPadding(5);
                returncell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(returncell);

                // document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prreturndatetime = new Phrase("تاريخ العودة", B12);
                prreturndatetime.add(new Chunk("/Return Date-Time",B10E));
                table_order_no.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_no = new PdfPCell(prreturndatetime);
                cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_no.setPadding(5.0f);
                table_order_no.addCell(cell_order_no);
                prreturndatetime = new Phrase("" + returns.get_date(), B10E);
                PdfPCell returndatecell = new PdfPCell(prreturndatetime);
                returndatecell.setPadding(5);
                returndatecell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(returndatecell);
                //document.open();

                Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");

                PdfPTable table_order_date = new PdfPTable(2);
                Phrase prreturncustomer = new Phrase("زبون", B12);
                prreturncustomer.add(new Chunk("/Customer",B10E));
                table_order_date.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_order_date = new PdfPCell(prreturncustomer);
                cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);

                prreturncustomer = new Phrase("" + contact.get_name(), B10E);
                PdfPCell returndatecustomr = new PdfPCell(prreturncustomer);
                returndatecustomr.setPadding(5);
                returndatecustomr.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_date.addCell(returndatecustomr);


                PdfPTable table = new PdfPTable(4);
                table.setRunDirection(writer.RUN_DIRECTION_RTL);
                Phrase pr = new Phrase("اسم العنصر", B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setPadding(5);
                table.addCell(c1);
                pr = new Phrase("كمية", B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);
                pr = new Phrase("السعر", B10);
                PdfPCell c2 = new PdfPCell(pr);
                c2.setPadding(5);
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c2);
                pr = new Phrase("مجموع", B10);
                c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);
                for (int i = 0; i < list1a.size(); i++) {
                    Phrase pr1 = new Phrase(list1a.get(i), B10E);
                    PdfPCell c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c7);

                    pr1 = new Phrase(list2a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);

                    pr1 = new Phrase(list3a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);

                    String total_amount;
                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    pr1 = new Phrase(total_amount, B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c7);
                }
                table.setSpacingBefore(10.0f);
                table.setHeaderRows(1);
                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }

                PdfPTable table_netamnt = new PdfPTable(2);
                Phrase prnetamnt= new Phrase("كمية الشبكة", B12);
                prnetamnt.add(new Chunk("/Net Amount",B10E));
                table_netamnt.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_netamnt = new PdfPCell(prnetamnt);
                cell_netamnt.setColspan(1);
                cell_netamnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_netamnt.setPadding(5.0f);
                table_netamnt.addCell(cell_netamnt);

                prnetamnt = new Phrase("" + net_amount + strCurrency, B10E);
                PdfPCell netamnt = new PdfPCell(prnetamnt);
                netamnt.setPadding(5);
                netamnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_netamnt.addCell(netamnt);
                table.setSpacingBefore(10.0f);

                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                PdfPTable table_payment = new PdfPTable(2);
                Phrase prtablepayment= new Phrase("طريقة الدفع", B12);
                prtablepayment.add(new Chunk("/Payment Mode",B10E));
                table_payment.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell cell_payment = new PdfPCell(prtablepayment);
                cell_payment.setColspan(1);
                cell_payment.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_payment.setPadding(5.0f);
                table_payment.addCell(cell_payment);

                prtablepayment = new Phrase("" + payment.get_payment_name(), B10E);
                PdfPCell paymentcell = new PdfPCell(prtablepayment);
                paymentcell.setPadding(5);
                paymentcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_payment.addCell(paymentcell);
                document.open();

                document.add(tableh);
                document.add(table_company_name);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table);
                document.add(table_netamnt);
                document.add(table_payment);
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
            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");

            // Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code = '" + Globals.strContact_Code + "'");
            Globals.AppLogWrite("settings email"+ Globals.objsettings.get_Email());
            Globals.AppLogWrite("settings Password"+ Globals.objsettings.get_Password());
            Globals.AppLogWrite("settings host"+ Globals.objsettings.get_Host());
            Globals.AppLogWrite("settings port"+ Globals.objsettings.get_Port());

            email.m = new GMailSender(Globals.objsettings.get_Email(), Globals.objsettings.get_Password(), Globals.objsettings.get_Host(), Globals.objsettings.get_Port());
            email.m.set_from(Globals.objsettings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Customer Return Detail ");
            Globals.AppLogWrite("recipients"+ recipients);

            email.m.set_to(recipients);
            email.m.set_subject("Trigger POS Customer Return Detail of "+ str_voucher_no);
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "PDF Report" + "/" + str_voucher_no + ".pdf");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        CusReturnFinalActivity activity;

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
    contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");
        if (contact == null) {
        } else {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                strContct = "91" + contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("114")) {

                    strContct = "965"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("221")) {
                strContct = "971"+contact.get_contact_1();
            }
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" + str_voucher_no + ".pdf");
        // Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_SHORT).show();
        if (contactExists(getApplicationContext(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {


                    openWhatsApp(file,getApplicationContext(),strContct);

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
        String toNumber = contactnumbr; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
        pdfOpenintent.putExtra("jid", toNumber + "@s.whatsapp.net");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.setType("application/pdf");

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


    public void share_dialog(){
        final Dialog listDialog2 = new Dialog(CusReturnFinalActivity.this);
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
                finish();
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* runOnUiThread(new Runnable() {
                    public void run() {*/

                        //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                        startWhatsApp();

                   /* }
                });*/

                listDialog2.dismiss();

            }
        });
    }



    private  void pdfPerform_80mm() {

        returns = Returns.getReturns(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        ArrayList<Return_detail> return_detailArrayList = Return_detail.getAllReturn_detail(getApplicationContext(), " where ref_voucher_no='" + str_voucher_no + "'", database);

        int count = 0;
        while (count < return_detailArrayList.size()) {

            String item_code = return_detailArrayList.get(count).get_item_code();
             item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
            strItemName = Return_detail.getItemNameReturn_l(getApplicationContext(), " WHERE Return_detail.item_Code  = '"
                    + strItemCode + "'  GROUP By Return_detail.item_Code");

            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                list1a.add(item.get_item_name());
            }
            else if(Globals.objsettings.get_Print_Lang().equals("1")){

                list1a.add(item.get_item_name());
            }

            else if(Globals.objsettings.get_Print_Lang().equals("2")){
                list1a.add(item.get_item_name());

            }

            list2a.add(return_detailArrayList.get(count).get_qty());
            list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(return_detailArrayList.get(count).get_price()), decimal_check));
            list4a.add(Globals.myNumberFormat2Price(Double.parseDouble(return_detailArrayList.get(count).get_line_total()), decimal_check));

            count++;
        }

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + str_voucher_no+"80mm" + ".pdf");
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
                String strString = "";
                int strLength = 18;
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);


                // Second parameter is the number of the chapter

                  document.open();
                Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10);
                subPara.setAlignment(Element.ALIGN_CENTER);
                Paragraph subParadress = new Paragraph( Globals.objLPR.getAddress(), B10);
                subParadress.setAlignment(Element.ALIGN_CENTER);
                Paragraph subParmobile = new Paragraph( Globals.objLPR.getMobile_No(), B10);
                subParmobile.setAlignment(Element.ALIGN_CENTER);
                DottedLineSeparator dottedline1 = new DottedLineSeparator();
                dottedline1.setOffset(-2);
                dottedline1.setGap(2f);
                subParmobile.add(dottedline1);
                Paragraph subParaacc = new Paragraph("Customer Return", B12);
                subParaacc.setAlignment(Element.ALIGN_CENTER);
                DottedLineSeparator dottedline = new DottedLineSeparator();
                dottedline.setOffset(-2);
                dottedline.setGap(2f);
                subParaacc.add(dottedline);

                Paragraph subPara1 = new Paragraph(getString(R.string.returnno) + ":                        "+str_voucher_no, B10);


                Paragraph subPara2 = new Paragraph(getString(R.string.returndate) + ":                      " + DateUtill.PaternDate1(returns.get_date()).substring(0,11), B10);
                Paragraph subPararet = new Paragraph(getString(R.string.returntime) + ":                     " + DateUtill.PaternDate1(returns.get_date()).substring(12,20), B10);
               Contact contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                Paragraph subPara3 = new Paragraph(getString(R.string.customer) + ":                          "+contact.get_name(), B10);

                document.add(subPara);
                document.add(subParadress);
                document.add(subParmobile);
                document.add(subParaacc);

                document.add(subPara1);
                document.add(subPara2);
                document.add(subPararet);
                document.add(subPara3);

                document.open();
                //PdfPTable table = new PdfPTable(4);
                //table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.ItemName), strLength);

                Phrase pr = new Phrase(strString, B10);
                //table.addCell(c1);
                pr.add(new Chunk(" "));
                document.add(pr);
                document.add(new Chunk(Chunk.NEWLINE));
                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Quantity), strLength);

                Phrase pr1q=new Phrase(strString,B10);

                pr1q.add(new Chunk(" "));
                pr1q.add(new Chunk(" "));

                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Price), strLength);
                pr1q.add(new Chunk(strString, B10));
                pr1q.add(new Chunk(" "));
                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Total), strLength);
                pr1q.add(new Chunk(strString, B10));
                document.add(pr1q);
                document.add(new Chunk(Chunk.NEWLINE));
                Phrase pr1 = null;
                Phrase prrq=null;
                for (int i = 0; i < list1a.size(); i++) {
                    strLength=19;
                    strString = Globals.myRequiredString(list1a.get(i), strLength);
                    pr1 = new Phrase(strString, N9);

                    pr1.add(new Chunk(" "));
                    pr1.add(new Chunk(Chunk.NEWLINE));
                    strLength=17;
                    strString = Globals.myRequiredString(list2a.get(i), strLength);
                    prrq = new Phrase(strString, N9);

                    prrq.add(new Chunk(" "));
                    prrq.add(new Chunk(" "));
                    strLength=13;
                    strString = Globals.myRequiredString(list3a.get(i), strLength);
                    prrq.add(new Chunk(strString, N9));
                    prrq.add(new Chunk(" "));
                    prrq.add(new Chunk(" "));
                    String total_amount;

                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                   strLength=14;
                    strString = Globals.myRequiredString(total_amount, strLength);

                    prrq.add(new Chunk(strString, N9));
                    prrq.add(new Chunk(" "));
                    prrq.add(new Chunk(Chunk.NEWLINE));
                    document.add(pr1);

                    document.add(prrq);
                }



                // table.setSpacingBefore(10.0f);


                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }


                Paragraph subPara4 = new Paragraph(getString(R.string.netamount) + ":                      "+net_amount + strCurrency, B10);
                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                Paragraph subPara5 = new Paragraph(getString(R.string.paymemtmode) + ":                  "+payment.get_payment_name(), B10);



                document.add(subPara4);
                document.add(subPara5);
                document.newPage();
                document.close();
                file.close();
            }

            else if(Globals.objsettings.get_Print_Lang().equals("1")) {
                String strString = "";
            int strLength = 18;
            Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
            Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
            Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
            Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
            Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
            Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);



            // Second parameter is the number of the chapter

            document.open();
            Paragraph subParaacc = new Paragraph("عودة العملاء", B12);
            subParaacc.setAlignment(Element.ALIGN_CENTER);
            document.add(subParaacc);



            strString = Globals.myRequiredString(Globals.objLPR.getCompany_Name(), strLength);

            Phrase p = new Phrase(strString + ": ",B10E);

            p.add(new Chunk("ااسم الشركة", B10));
            p.add(new Chunk(Chunk.NEWLINE));
            document.add(p);
            ColumnText canvas = new ColumnText(writer.getDirectContent());

            canvas.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            canvas.setSimpleColumn(36, 750, 559, 780);

            canvas.setAlignment(Element.ALIGN_RIGHT);
            canvas.addElement(p);
            canvas.addElement(new Chunk(Chunk.NEWLINE));
            canvas.go();

            strString = Globals.myRequiredString(str_voucher_no, strLength);

            Phrase p1 = new Phrase(strString + ": ",B10E);

            p1.add(new Chunk("رقم الإرجاع", B10));
            p1.add(new Chunk(Chunk.NEWLINE));
            document.add(p1);
            ColumnText canvas1 = new ColumnText(writer.getDirectContent());

            canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            canvas1.setSimpleColumn(36, 750, 559, 780);

            canvas1.setAlignment(Element.ALIGN_RIGHT);
            canvas1.addElement(p1);
            canvas1.addElement(new Chunk(Chunk.NEWLINE));
            canvas1.go();
            strString = Globals.myRequiredString(returns.get_date(), strLength);

            Phrase p2 = new Phrase(strString + ": ",B10E);

            p2.add(new Chunk("تاريخ العودة", B10));
            p2.add(new Chunk(Chunk.NEWLINE));
            document.add(p2);
            ColumnText canvas2 = new ColumnText(writer.getDirectContent());

            canvas2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            canvas2.setSimpleColumn(36, 750, 559, 780);

            canvas2.setAlignment(Element.ALIGN_RIGHT);
            canvas2.addElement(p2);
            canvas2.addElement(new Chunk(Chunk.NEWLINE));
            canvas2.go();

            contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");
            strString = Globals.myRequiredString(contact.get_name(), strLength);

            Phrase p3 = new Phrase(strString + ": ",B10E);

            p3.add(new Chunk("زبون", B10));
            p3.add(new Chunk(Chunk.NEWLINE));
            document.add(p3);
            ColumnText canvas3 = new ColumnText(writer.getDirectContent());

            canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            canvas3.setSimpleColumn(36, 750, 559, 780);

            canvas3.setAlignment(Element.ALIGN_RIGHT);
            canvas3.addElement(p3);
            canvas3.addElement(new Chunk(Chunk.NEWLINE));
            canvas3.go();


            //PdfPTable table = new PdfPTable(4);
            //table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            strLength=17;
            strString = Globals.myRequiredString("اسم العنصر", strLength);

            Phrase pr = new Phrase(strString, B10);
            //table.addCell(c1);
            pr.add(new Chunk(" "));

            document.add(pr);
            document.add(new Chunk(Chunk.NEWLINE));
            strLength =17;
            strString = Globals.myRequiredString("كمية", strLength);
            Phrase prq1 = new Phrase(strString, B10);

            prq1.add(new Chunk(" "));
            strLength=17;
            strString = Globals.myRequiredString("السعر", strLength);
            prq1.add(new Chunk(strString, B10));
            prq1.add(new Chunk(" "));
            strLength=17;
            strString = Globals.myRequiredString("مجموع", strLength);
            prq1.add(new Chunk(strString, B10));
            document.add(prq1);

               /* for (int i = 0; i < list1a.size(); i++) {
                    strString = Globals.myRequiredString(list1a.get(i), strLength);

                    pritem = new Phrase(strString, N9);
                }*/


            Phrase pr1 = null;
            Phrase pritem=null;
            for (int i = 0; i < list1a.size(); i++) {
                strLength=18;


                strString = Globals.myRequiredString(list1a.get(i), strLength);
                pritem = new Phrase(strString, B10E);
                pritem.add(new Chunk(" "));
               pritem.add(new Chunk(Chunk.NEWLINE));


                // pritem = new Phrase(strString, B10);


                strLength=17;
                strString = Globals.myRequiredString(list2a.get(i), strLength);
                pr1 = new Phrase(strString, B10E);

                pr1.add(new Chunk(" "));
                strLength=17;
                strString = Globals.myRequiredString(list3a.get(i), strLength);
                pr1.add(new Chunk(strString, B10E));
                pr1.add(new Chunk(" "));

                String total_amount;

                total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                strLength=15;
                strString = Globals.myRequiredString(total_amount, strLength);

                pr1.add(new Chunk(strString, B10E));
                pr1.add(new Chunk(""));
                pr1.add(new Chunk(Chunk.NEWLINE));
                document.add(pritem);

                document.add(pr1);
            }

            // table.setSpacingBefore(10.0f);


            String net_amount;
            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

            String strCurrency;
            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                strCurrency = "";
            } else {
                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
            }

                strString = Globals.myRequiredString(net_amount, strLength);

           // Paragraph subPara4 = new Paragraph(getString(R.string.netamount) + ":" + net_amount + strCurrency, B10);

                Phrase pnetamnt = new Phrase(strString + ": ",B10E);

                pnetamnt.add(new Chunk("كمية الشبكة", B10));
                pnetamnt.add(new Chunk(Chunk.NEWLINE));
                document.add(pnetamnt);
                ColumnText canvasnetamnt = new ColumnText(writer.getDirectContent());

                canvasnetamnt.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvasnetamnt.setSimpleColumn(36, 750, 559, 780);

                canvasnetamnt.setAlignment(Element.ALIGN_RIGHT);
                canvasnetamnt.addElement(pnetamnt);
                canvasnetamnt.addElement(new Chunk(Chunk.NEWLINE));
                canvasnetamnt.go();

                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
            Paragraph subPara5 = new Paragraph(getString(R.string.paymemtmode) + ":" + payment.get_payment_name(), B10);

                strString = Globals.myRequiredString(payment.get_payment_name(), strLength);

                // Paragraph subPara4 = new Paragraph(getString(R.string.netamount) + ":" + net_amount + strCurrency, B10);

                Phrase pnetpayment = new Phrase(strString + ": ",B10E);

                pnetpayment.add(new Chunk("طريقة الدفع", B10));
                pnetpayment.add(new Chunk(Chunk.NEWLINE));
                document.add(pnetpayment);
                ColumnText canvasnepayment = new ColumnText(writer.getDirectContent());

                canvasnepayment.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvasnepayment.setSimpleColumn(36, 750, 559, 780);

                canvasnepayment.setAlignment(Element.ALIGN_RIGHT);
                canvasnepayment.addElement(pnetpayment);
                canvasnepayment.addElement(new Chunk(Chunk.NEWLINE));
                canvasnepayment.go();

            document.newPage();
            document.close();
            file.close();
        }
           else if(Globals.objsettings.get_Print_Lang().equals("2")) {

                String strString = "";
                int strLength = 18;
                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 8, Font.NORMAL);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 9, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                document.open();
                Paragraph subParaacc = new Paragraph("عودة العملاء", B12);
                subParaacc.setAlignment(Element.ALIGN_CENTER);
                document.add(subParaacc);

                document.add(new Chunk(Chunk.NEWLINE));
                strString = Globals.myRequiredString(Globals.objLPR.getCompany_Name(), strLength);

                Phrase p = new Phrase("",B10E);

                p.add(new Chunk("اسم الشركة", B10));
                p.add(new Chunk("/Company Name :" , B10E));
                p.add(new Chunk(Chunk.NEWLINE));
                p.add(strString);
                document.add(p);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas = new ColumnText(writer.getDirectContent());

                canvas.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas.setSimpleColumn(36, 750, 559, 780);

                canvas.setAlignment(Element.ALIGN_RIGHT);
                canvas.addElement(p);
                canvas.addElement(new Chunk(Chunk.NEWLINE));
                canvas.go();


                strString = Globals.myRequiredString(str_voucher_no, strLength);

                Phrase p1 = new Phrase("",B10E);

                p1.add(new Chunk("عودة لا", B10));
                p1.add(new Chunk("/Return No :" , B10E));
                p1.add(new Chunk(Chunk.NEWLINE));
                p1.add(new Chunk(strString, B10E));
                document.add(p1);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas1 = new ColumnText(writer.getDirectContent());

                canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas1.setSimpleColumn(36, 750, 559, 780);

                canvas1.setAlignment(Element.ALIGN_RIGHT);
                canvas1.addElement(p1);
                canvas1.addElement(new Chunk(Chunk.NEWLINE));
                canvas1.go();

                strString = Globals.myRequiredString(returns.get_date(), strLength);

                Phrase p2 = new Phrase("" ,B10E);

                p2.add(new Chunk(" تاريخ العودة", B10));
                p2.add(new Chunk("/Return Date-Time :" , B10E));
                p2.add(new Chunk(Chunk.NEWLINE));
                p2.add(new Chunk(strString, B10E));
                document.add(p2);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas2 = new ColumnText(writer.getDirectContent());

                canvas2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas2.setSimpleColumn(36, 750, 559, 780);

                canvas2.setAlignment(Element.ALIGN_RIGHT);
                canvas2.addElement(p2);
                canvas2.addElement(new Chunk(Chunk.NEWLINE));
                canvas2.go();


                Contact contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + returns.get_contact_code() + "'");

                if(contact!=null) {
                    strString = Globals.myRequiredString(contact.get_name(), strLength);

                    Phrase p3 = new Phrase("" , B10E);

                    p3.add(new Chunk("الزبون", B10));
                    p3.add(new Chunk("/Customer :", B10E));
                    p3.add(new Chunk(Chunk.NEWLINE));
                    p3.add(new Chunk(strString, B10E));
                    document.add(p3);
                    document.add(new Chunk(Chunk.NEWLINE));
                    ColumnText canvas3 = new ColumnText(writer.getDirectContent());

                    canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    canvas3.setSimpleColumn(36, 750, 559, 780);

                    canvas3.setAlignment(Element.ALIGN_RIGHT);
                    canvas3.addElement(p3);
                    canvas3.addElement(new Chunk(Chunk.NEWLINE));
                    canvas3.go();
                }


                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.ItemName), strLength);

                Phrase pr = new Phrase(strString +"/", B10E);
                //table.addCell(c1);
                pr.add(new Chunk("اسم العنصر" , B10));
                document.add(pr);
                document.add(new Chunk(Chunk.NEWLINE));

                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Quantity), strLength);

                Phrase pr1q=new Phrase(strString,B10E);

                pr1q.add(new Chunk(" "));
                pr1q.add(new Chunk(" "));

                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Price), strLength);
                pr1q.add(new Chunk(strString, B10E));
                pr1q.add(new Chunk(" "));
                strLength=17;
                strString = Globals.myRequiredString(getString(R.string.Total), strLength);
                pr1q.add(new Chunk(strString, B10E));
                document.add(pr1q);
                document.add(new Chunk(Chunk.NEWLINE));


                strLength=17;
                strString = Globals.myRequiredString("كمية", strLength);

                Phrase pr1ar=new Phrase(strString,B10);

                pr1ar.add(new Chunk(" "));
                pr1ar.add(new Chunk(" "));

                strLength=17;
                strString = Globals.myRequiredString("السعر", strLength);
                pr1ar.add(new Chunk(strString, B10));
                pr1ar.add(new Chunk(" "));
                strLength=17;
                strString = Globals.myRequiredString("مجموع", strLength);
                pr1ar.add(new Chunk(strString, B10));
                document.add(pr1ar);
                document.add(new Chunk(Chunk.NEWLINE));

                Phrase pr1 = null;
                Phrase pritem=null;
                for (int i = 0; i < list1a.size(); i++) {
                    strLength=18;


                    strString = Globals.myRequiredString(list1a.get(i), strLength);
                    pritem = new Phrase(strString, B10E);
                    pritem.add(new Chunk(" "));
                   // pritem.add(new Chunk(Chunk.NEWLINE));
                    document.add(pritem);
                    document.add(new Chunk(Chunk.NEWLINE));

                    // pritem = new Phrase(strString, B10);


                    strLength=17;
                    strString = Globals.myRequiredString(list2a.get(i), strLength);
                    pr1 = new Phrase(strString, B10E);

                    pr1.add(new Chunk(" "));
                    strLength=17;
                    strString = Globals.myRequiredString(list3a.get(i), strLength);
                    pr1.add(new Chunk(strString, B10E));
                    pr1.add(new Chunk(" "));

                    String total_amount;

                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    strLength=15;
                    strString = Globals.myRequiredString(total_amount, strLength);

                    pr1.add(new Chunk(strString, B10E));
                    pr1.add(new Chunk(""));
                    pr1.add(new Chunk(Chunk.NEWLINE));

                    document.add(pr1);
                }

                String net_amount;
                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);

                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }


                strString = Globals.myRequiredString(net_amount, strLength);

                Phrase p4 = new Phrase("",B10E);

                p4.add(new Chunk("كمية الشبكة", B10));
                p4.add(new Chunk("/Net Amount :" , B10E));
                p4.add(new Chunk(Chunk.NEWLINE));
                p4.add(new Chunk(strString, B10E));
                document.add(p4);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas4 = new ColumnText(writer.getDirectContent());

                canvas4.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas4.setSimpleColumn(36, 750, 559, 780);

                canvas4.setAlignment(Element.ALIGN_RIGHT);
                canvas4.addElement(p2);
                canvas4.addElement(new Chunk(Chunk.NEWLINE));
                canvas4.go();

                Payment payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + PayId + "'");
                strString = Globals.myRequiredString(payment.get_payment_name(), strLength);

                Phrase p5 = new Phrase("" ,B10E);

                p5.add(new Chunk("طريقة الدفع", B10));
                p5.add(new Chunk("/Payment Mode :" , B10E));
                p5.add(new Chunk(Chunk.NEWLINE));
                p5.add(new Chunk(strString, B10E));
                document.add(p5);
                document.add(new Chunk(Chunk.NEWLINE));
                ColumnText canvas5 = new ColumnText(writer.getDirectContent());

                canvas5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                canvas5.setSimpleColumn(36, 750, 559, 780);

                canvas5.setAlignment(Element.ALIGN_RIGHT);
                canvas5.addElement(p5);
                canvas5.addElement(new Chunk(Chunk.NEWLINE));
                canvas5.go();


                document.newPage();
                document.close();
                file.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            System.out.println("Dom Exception"+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}
