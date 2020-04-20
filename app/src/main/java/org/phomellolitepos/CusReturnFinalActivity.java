package org.phomellolitepos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.itextpdf.text.pdf.codec.Base64;

import org.phomellolitepos.Adapter.CusReturnFinalListAdapter;
import org.phomellolitepos.Adapter.MyAdapter;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Return_detail;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CusReturnFinalActivity extends AppCompatActivity {
    EditText edt_name, edt_qty, edt_price;
    AutoCompleteTextView edt_toolbar_item_code;
    Button btn_add;
    ListView list;
    private ProgressDialog dialog;
    LongOperation tsk;
    String operation, str_voucher_no, str_date, str_remarks;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
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
    ArrayList<String> arrayList1;
    private String PrinterType = "";
    private IWoyouService woyouService;
    Settings settings;
    private ICallback callback = null;
    User user;
    private boolean iswifi = false;
    private ArrayList<String> mylist = new ArrayList<String>();
    private MyAdapter adp;
    private int order, noofPrint = 0, lang = 0, pos = 0;
    ArrayList<Return_detail> return_details;
    private TimerCountTools timeTools;
    private HandlerUtils.MyHandler handlerPPT8555;
    String serial_no, android_id, myKey, device_id,imei_no;
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
                            sleep(1000);
                            pDialog.dismiss();

                            Intent intent = new Intent(CusReturnFinalActivity.this, CusReturnHeaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
                            intent.putExtra("contact_code", "");
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

        Intent intent = getIntent();
        getSupportActionBar().setTitle("");
        operation = intent.getStringExtra("operation");
        str_voucher_no = intent.getStringExtra("voucher_no");
        str_date = intent.getStringExtra("date");
        str_remarks = intent.getStringExtra("remarks");
        cusCode = intent.getStringExtra("contact_code");
        PayId = intent.getStringExtra("payment_id");

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
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
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
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
                alertDialog.setMessage("Are you sure you want delete this?");
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        arraylist.remove(position);
                        returnFinalListAdapter.notifyDataSetChanged();
                        edt_name.setText("");
                        edt_qty.setText("");
                        edt_price.setText("");
                    }
                });


                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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

        final Settings settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
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


            if(returns.get_is_cancel().equals("true")){
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

            arrayListReturn_detail = Return_detail.getAllReturn_detail(getApplicationContext(), " where ref_voucher_no ='" + str_voucher_no + "' ", database);
            if (arrayListReturn_detail.size() > 0) {
                String inv;
                for (int i = 0; i < arrayListReturn_detail.size(); i++) {
                    Item item = Item.getItem(getApplicationContext(), " where item_code = '" + arrayListReturn_detail.get(i).get_item_code() + "'", database, db);

                    StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", arrayListReturn_detail.get(i).get_item_code(), arrayListReturn_detail.get(i).get_qty(), "", item.get_item_name(), arrayListReturn_detail.get(i).get_price(), arrayListReturn_detail.get(i).get_line_total());
                    arraylist.add(stockAdjectmentDetailList);
                }
                list_load(arraylist);
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
                        if (settings.get_Is_Stock_Manager().equals("true")) {
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
                                Toast.makeText(getApplicationContext(), "This item not in stock", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()

                {
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

                                            if (result.equals("1")) {
                                                if (lite_pos_registration.getproject_id().equals("standalone")) {
                                                    String rsultPost = stock_post();
                                                    if (rsultPost.equals("1")) {
                                                        if (settings.get_Is_Stock_Manager().equals("true")) {
                                                            String rsultUpdate = stock_update();
                                                        }

                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                try {

                                                                    if(PrinterType.equals("0")) {



                                                                            Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                            startActivity(intent1);
                                                                            finish();




                                                                    }

                                                                    else{


                                                                        //  Toast.makeText(getApplicationContext(),"print start 1",Toast.LENGTH_LONG).show();
                                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                                        alertDialog.setTitle("Customer Return");
                                                                        alertDialog.setMessage("Do you want to print the Customer return?");
                                                                        alertDialog.setIcon(R.drawable.delete);

                                                                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                startActivity(intent1);
                                                                                finish();
                                                                                try {
                                                                                    print_return();
                                                                                } catch (Exception e) {

                                                                                }
                                                                            }
                                                                        });


                                                                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();

                                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                startActivity(intent1);
                                                                                finish();
                                                                                dialog.cancel();
                                                                            }
                                                                        });

                                                                        // Showing Alert Message
                                                                        alertDialog.show();
                                                                    }

                                                                }catch (Exception ex) {
                                                                    Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Record not post", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }

                                                } else {
                                                    if (isNetworkStatusAvialable(getApplicationContext())) {
                                                        result = send_online_return();
                                                        pDialog.dismiss();
                                                        if (result.equals("1")) {
                                                            String rsultPost = stock_post();
                                                            if (rsultPost.equals("1")) {
                                                                if (settings.get_Is_Stock_Manager().equals("true")) {
                                                                    String rsultUpdate = stock_update();
                                                                }



                                                            }

                                                            //print_return();

                                                            switch (rsultPost) {
                                                                case "1":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            //  print_return();

                                                                            if(PrinterType.equals("0")) {

                                                                                    Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                    startActivity(intent1);
                                                                                    finish();

                                                                            }
                                                                            else{
                                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                                                alertDialog.setTitle("Customer Return");
                                                                                alertDialog.setMessage("Do you want to print the Customer return?");
                                                                                alertDialog.setIcon(R.drawable.delete);

                                                                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                                        Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                                                        Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                                        startActivity(intent1);
                                                                                        finish();
                                                                                        try {
                                                                                            print_return();
                                                                                        } catch (Exception e) {

                                                                                        }
                                                                                    }
                                                                                });


                                                                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();

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
                                                                            Toast.makeText(getApplicationContext(), "Record not post", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    break;
                                                            }
                                                        }
                                                        else if(result.equals("3")) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    if (Globals.responsemessage.equals("Device Not Found")) {

                                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                                        lite_pos_device.setStatus("Out");
                                                                        long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                                        if (ct > 0) {

                                                                            Intent intent_category = new Intent(CusReturnFinalActivity.this, LoginActivity.class);
                                                                            startActivity(intent_category);
                                                                            finish();
                                                                        }


                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else if(result.equals("4")){
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), Globals.responsemessage, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "Record not post on server", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } else {
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

                                        }
                                    };
                                    timerThread1.start();
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
                                                        if(PrinterType.equals("0")) {



                                                            Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                            startActivity(intent1);
                                                            finish();


                                                        }
                                                        else{
                                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                                            alertDialog.setTitle("Customer Return");
                                                            alertDialog.setMessage("Do you want to print the Customer return?");
                                                            alertDialog.setIcon(R.drawable.delete);

                                                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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


                                                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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

                                }

                                else{
                                    if(PrinterType.equals("0")){
                                        Toast.makeText(getApplicationContext(), "Please check printer settings", Toast.LENGTH_SHORT).show();



                                    }

                                    else{
                                        try{
                                            print_return();
                                        }
                                        catch (Exception e){

                                        }

                                    }
                                }
                                break;
                        }
                        return true;
                    }
                });

        edt_toolbar_item_code.setOnKeyListener(new View.OnKeyListener()

                                               {
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
                                                                   Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
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
        mylist = getlist();
        //  Toast.makeText(getApplicationContext(),"My list"+ mylist,Toast.LENGTH_LONG).show();
        if (mylist != null) {
            adp = new MyAdapter(getApplicationContext(), mylist);
        }

        if (iswifi) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        performOperationEn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } else if (PrinterType.equals("1")) {
            try {
                if (woyouService == null) {
                } else {
                    mobile_pos(returns, return_details);
                }
            } catch (Exception ex) {
            }
        } else if (PrinterType.equals("3")) {
            String result = bluetooth_56(returns, return_details);
        } else if (PrinterType.equals("4")) {
            String result = bluetooth_80(returns, return_details);
        }else  if (PrinterType.equals("8")) {
           // Toast.makeText(getApplicationContext(),"customer return"+ PrinterType + ":::"+ settings.get_Copy_Right(),Toast.LENGTH_LONG).show();

            ppt_8555(returns, return_details);
        }
    }

    private void ppt_8555(final Returns returns, final ArrayList<Return_detail> return_details) {



        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                //  mIPosPrinterService.printSpecifiedTypeText("Rajwaniya\n", "ST", 24, callbackPPT8555);
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {

                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                        Bitmap bitmap = StringToBitMap(settings.get_Logo());
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
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1,callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1,callbackPPT8555);
                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1,callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        try {
                            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")) {
                            } else {
                                mIPosPrinterService.printSpecifiedTypeText("" + Globals.objLPR.getService_code_tariff() + "\n", "ST", 24, callbackPPT8555);
                            }
                        } catch (Exception ex) {
                        }
                        mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("")) {
                        } else {
                            mIPosPrinterService.printSpecifiedTypeText(Globals.GSTNo+":"+ Globals.objLPR.getLicense_No(),"ST",24,callbackPPT8555);
                        }

                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        if (Globals.strIsBarcodePrint.equals("true")) {
                            mIPosPrinterService.printBarCode(str_voucher_no, 8, 60, 120, 0, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        }
                        mIPosPrinterService.setPrinterPrintAlignment(0, callbackPPT8555);
                        mIPosPrinterService.setPrinterPrintFontSize(24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("Voucher No"+":"+ str_voucher_no,"ST",24,callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("Return Date"+":"+returns.get_date(),"ST",24,callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintDeviceID+":"+ Globals.objLPD.getDevice_Name(),"ST",24,callbackPPT8555);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        mIPosPrinterService.printSpecifiedTypeText(Globals.PrintCashier+ ":"+user.get_name(),"ST",24,callbackPPT8555);

                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                        mIPosPrinterService.printSpecifiedTypeText("Customer"+":"+contact.get_name(),"ST",24,callbackPPT8555);
                        if (contact.get_gstin().length() > 0) {
                            mIPosPrinterService.printSpecifiedTypeText("Customer GST No."+ ":"+contact.get_gstin(),"ST",24,callbackPPT8555 );
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
                            mIPosPrinterService.printSpecifiedTypeText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check)+ "         "+sale_price+"      "+ line_total + "\n","ST", 24, callbackPPT8555);
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
                        mIPosPrinterService.printSpecifiedTypeText("Net Amt" +":"+ net_amount + "" + strCurrency, "ST", 24, callbackPPT8555);

                        mIPosPrinterService.setPrinterPrintAlignment(1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText(" \n", "ST", 24, callbackPPT8555);
                        if (!settings.get_Footer_Text().equals("")) {
                           // mIPosPrinterService.PrintSpecFormatText(settings.get_Footer_Text(), "ST", 24, 1, callbackPPT8555);

                            mIPosPrinterService.printSpecifiedTypeText(settings.get_Footer_Text(), "ST", 24, callbackPPT8555);
                            mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        }
                        mIPosPrinterService.printSpecifiedTypeText("" + settings.get_Copy_Right() + "\n", "ST", 24, callbackPPT8555);

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

    private ArrayList<String> getlist() {
        ArrayList<String> mylist = new ArrayList<String>();
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

//        lbl = LableCentre(Globals.PrintOrder);
//        mylist.add("\n" + lbl);
        mylist.add("\n");
        mylist.add("\n" + "Voucher No" + ":" + str_voucher_no);
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

        mylist.add("\n-----------------------------------------------");
        mylist.add("\nItem                    Qty     Price   Total");
        mylist.add("\n-----------------------------------------------\n");
        Double itemFinalTax = 0d;
        int count = 0;
        while (count < return_details.size()) {

            String strItemCode = return_details.get(count).get_item_code();

            String strItemName = Order_Detail.getItemName(getApplicationContext(), " WHERE order_detail.item_code  = '"
                    + strItemCode + "'  GROUP By order_detail.item_Code");

            String line_total;
            line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);

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

        mylist.add("\n-----------------------------------------------");

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
        mylist.add("\nNet Amount  :  " + tt + strCurrency);

        if (!settings.get_Footer_Text().equals("")) {
            mylist.add("\n    " + settings.get_Footer_Text());
        }
        mylist.add("\n            " + settings.get_Copy_Right());
        mylist.add("\n");
        mylist.add("\n");
        Globals.strContact_Code = "";
        Globals.strResvContact_Code = "";
        Globals.strOldCrAmt = "0";
        Globals.setEmpty();
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
                    if (Globals.strIsBarcodePrint.equals("true")) {
                        byte[] sendData;
                        sendData = BytesUtil.getPrintQRCode(str_voucher_no, 1, 0);
                        mService.write(sendData);
                    }
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage(Globals.PrintInvNo + " : " + str_voucher_no, "GBK");
                    mService.sendMessage(Globals.PrintInvDate + "   : " + this.returns.get_date(), "GBK");
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
                    while (count < return_details.size()) {
                        String strItemCode = return_details.get(count).get_item_code();
                        String strItemName = Order_Detail.getItemName(getApplicationContext(), "WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_code");
                        String sale_price;
                        Double dDisAfterSalePrice = 0d;
                        dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);

                        String line_total;
                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                        mService.sendMessage("" + strItemName, "GBK");
                        mService.sendMessage("                    " + return_details.get(count).get_qty() + "      " + sale_price + "      " + line_total, "GBK");
                        count++;
                    }

                    mService.sendMessage("................................................", "GBK");
                    String net_amount;
                    net_amount = Globals.myNumberFormat2Price(Double.parseDouble(returns.get_total()), decimal_check);
                    String strCurrency;
                    if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                        strCurrency = "";
                    } else {
                        strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                    }
                    mService.sendMessage("Net Amount      : " + net_amount + "" + strCurrency, "GBK");

                    if (!settings.get_Footer_Text().equals("")) {
                        mService.sendMessage(settings.get_Footer_Text(), "GBK");
                    }
                    mService.sendMessage("             " + settings.get_Copy_Right() + "\n\n", "GBK");
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
                    if (Globals.strIsBarcodePrint.equals("true")) {
                        byte[] sendData;
                        sendData = BytesUtil.getPrintQRCode(str_voucher_no, 1, 0);
                        mService.write(sendData);
                    }
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    mService.sendMessage(Globals.PrintInvNo + ":" + str_voucher_no, "GBK");
                    mService.sendMessage(Globals.PrintInvDate + ":" + returns.get_date(), "GBK");
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
                    while (count < return_details.size()) {

                        String strItemCode = return_details.get(count).get_item_code();
                        String strItemName = Order_Detail.getItemName(getApplicationContext(), "WHERE order_detail.item_Code  = '"
                                + strItemCode + "'  GROUP By order_detail.item_code");
                        String sale_price;
                        Double dDisAfterSalePrice = 0d;
                        dDisAfterSalePrice = (((Double.parseDouble(return_details.get(count).get_line_total()) / Double.parseDouble(return_details.get(count).get_qty()))));
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(dDisAfterSalePrice + ""), decimal_check);
                        String line_total;
                        line_total = Globals.myNumberFormat2Price(Double.parseDouble(return_details.get(count).get_line_total()), decimal_check);
                        mService.sendMessage("" + strItemName, "GBK");
                        mService.sendMessage("        " + return_details.get(count).get_qty() + "  " + sale_price + "  " + line_total, "GBK");
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
                    mService.sendMessage("Net Amt   :" + net_amount + "" + strCurrency, "GBK");
                    if (!settings.get_Footer_Text().equals("")) {
                        mService.sendMessage(settings.get_Footer_Text(), "GBK");
                    }
                    mService.sendMessage("      " + settings.get_Copy_Right() + "\n\n", "GBK");
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
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int k = 0; k < Integer.parseInt(settings.get_No_Of_Print()); k++) {
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
                        if (Globals.strIsBarcodePrint.equals("true")) {
                            woyouService.printBarCode(str_voucher_no, 8, 60, 120, 0, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                        }
                        woyouService.setAlignment(0, callback);
                        woyouService.setFontSize(30, callback);
                        woyouService.printColumnsText(new String[]{"Voucher No", ":", str_voucher_no}, new int[]{14, 1, 16}, new int[]{0, 0, 0}, callback);
                        woyouService.printColumnsText(new String[]{"Return Date", ":", returns.get_date()}, new int[]{12, 1, 20}, new int[]{0, 0, 0}, callback);
                        woyouService.printColumnsText(new String[]{Globals.PrintDeviceID, ":", Globals.objLPD.getDevice_Name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                        user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                        woyouService.printColumnsText(new String[]{Globals.PrintCashier, ":", user.get_name()}, new int[]{12, 1, 18}, new int[]{0, 0, 0}, callback);

                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + returns.get_contact_code() + "'");
                        woyouService.printColumnsText(new String[]{"Customer", ":", contact.get_name()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                        if (contact.get_gstin().length() > 0) {
                            woyouService.printColumnsText(new String[]{"Customer GST No.", ":", contact.get_gstin()}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);
                        }

                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.printTextWithFont("Item Name\n", "", 30, callback);
                        woyouService.printColumnsText(new String[]{"Qty", "Price", "Total"}, new int[]{7, 9, 15}, new int[]{0, 0, 0}, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
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
                            woyouService.printTextWithFont(strItemName + "\n", "", 30, callback);
                            woyouService.printColumnsText(new String[]{Globals.myNumberFormat2QtyDecimal(Double.parseDouble(return_details.get(count).get_qty()), qty_decimal_check), sale_price, line_total}, new int[]{8, 8, 8}, new int[]{0, 0, 0}, callback);
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
                        woyouService.printColumnsText(new String[]{"Net Amt", ":", net_amount + "" + strCurrency}, new int[]{10, 1, 20}, new int[]{0, 0, 0}, callback);

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
                    Globals.strOldCrAmt = "0";
                    Globals.setEmpty();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
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
        String result = Returns.sendOnServer(getApplicationContext(), database, db, "Select * FROM  returns WHERE is_push = 'N' and is_post='false'",liccustomerid,serial_no,android_id,myKey);
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

    public void onsavePost(){

try {

            if (PrinterType.equals("0")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                alertDialog.setTitle("Customer Return");
                alertDialog.setMessage("Do you want to Post on server?");
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      String  result = send_online_return();
                       // pDialog.dismiss();
                        if (result.equals("1")) {

                            String rsultPost = stock_post();
                            if (rsultPost.equals("1")) {
                                if (settings.get_Is_Stock_Manager().equals("true")) {
                                    String rsultUpdate = stock_update();
                                }

                                if (PrinterType.equals("0")) {

                                    Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                    startActivity(intent1);
                                    finish();

                                } else {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                                    alertDialog.setTitle("Customer Return");
                                    alertDialog.setMessage("Do you want to print the Customer return?");
                                    alertDialog.setIcon(R.drawable.delete);

                                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                            startActivity(intent1);
                                            finish();
                                            try {
                                                print_return();
                                            } catch (Exception e) {

                                            }
                                        }
                                    });


                                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();

                                            Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                            startActivity(intent1);
                                            finish();
                                            dialog.cancel();
                                        }
                                    });

                                    // Showing Alert Message
                                    alertDialog.show();

                                }

                                //print_return();

                                                        /*                switch (rsultPost) {
                                                                            case "1":
                                                                                runOnUiThread(new Runnable() {
                                                                                    public void run() {
                                                                                        //  print_return();

                                                                                        }



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
                                                                                        Toast.makeText(getApplicationContext(), "Record not post", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                                break;
                                                                        }
                                                                    }*/
                            }
                        } else if (result.equals("3")) {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    if (Globals.responsemessage.equals("Device Not Found")) {

                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                        lite_pos_device.setStatus("Out");
                                        long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                        if (ct > 0) {

                                            Intent intent_category = new Intent(CusReturnFinalActivity.this, LoginActivity.class);
                                            startActivity(intent_category);
                                            finish();
                                        }


                                    }
                                }
                            });
                        } else if (result.equals("4")) {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Toast.makeText(getApplicationContext(), Globals.responsemessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Record not post on server", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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


                                                               /* Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                                                                Intent intent1 = new Intent(CusReturnFinalActivity.this, CustomerReturnListActivity.class);
                                                                startActivity(intent1);
                                                                finish();
*/

            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CusReturnFinalActivity.this);

                alertDialog.setTitle("Customer Return");
                alertDialog.setMessage("Do you want to print the Customer return?");
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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


                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
        catch(Exception e){

        }

    }



    private String stock_save() {
        String suc = "0";
        try {
            database.beginTransaction();
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
            }

        } catch (Exception ex) {
            database.endTransaction();
        }
        return suc;
    }

    private void list_load(final ArrayList<StockAdjectmentDetailList> arraylist) {
        ListView list = (ListView) findViewById(R.id.list);
        if (arraylist.size() > 0) {
            returnFinalListAdapter = new CusReturnFinalListAdapter(CusReturnFinalActivity.this, arraylist);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(returnFinalListAdapter);
            returnFinalListAdapter.notifyDataSetChanged();


        } else {
            list.setVisibility(View.GONE);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
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
        } catch (Exception ex) {
        }


    }
    public void setTextView() {
        try {


            edt_price.setText("");
            edt_name.setText("");
            edt_qty.setText("");
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
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(CusReturnFinalActivity.this, CusReturnHeaderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
}
