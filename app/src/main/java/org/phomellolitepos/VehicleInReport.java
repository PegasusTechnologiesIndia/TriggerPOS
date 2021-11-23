package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.hoin.btsdk.BluetoothService;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
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
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.PdfDocumentAdapter;
import org.phomellolitepos.Adapter.VehicleHistoryAdapter;
import org.phomellolitepos.Adapter.VehicleIn_ReportAdapter;
import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.VehicleReport;
import org.phomellolitepos.printer.BytesUtil;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.utils.HandlerUtils;
import org.phomellolitepos.utils.TimerCountTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class VehicleInReport extends AppCompatActivity {
    Database db;
    SQLiteDatabase database;
    TextView txt_compname,txt_add,txt_date;
    TextView txt_getvtype,txt_getvtcont,txt_vfareamnt;
    String name, from, to, operation;
    Intent intent;
    TableRow tableRow;
    ProgressDialog progressDialog;
    Dialog listDialog;
    TableLayout tableLayout;
    int check = 0;
    String sql, sqlFooter, json, str_type;
    HorizontalScrollView hsv;
    LinearLayout ll;
    ScrollView sv;
    ArrayList<Integer> numCols;
    String decimal_check,qty_decimal_check;
    String flag = "0";
    int id;
    int limit = 200;

    int nop = 1;
    String strAddLimit = "",FirstCoulm="";
    int SnoCount = 1;
    TextView txtvehin,txtvehout;
    private ICallback callback = null;
    ArrayList<Integer> dateCols;
    private static final String TAG = "PrinterTestDemo";
    private final int MSG_TEST = 1;
    private long printCount = 0;
    private IWoyouService woyouService;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    ArrayList<VehicleReport> vehicleReportArrayList ;
    private TimerCountTools timeTools;
    JSONObject printJson = new JSONObject();;
    private VehicleInReport.PrinterListener printer_callback = new VehicleInReport.PrinterListener();
    public static PrinterBinder printer;
    TextView txt_totalamnt;
    double count_amnt=0;
    double count_amnt_summry=0;
    VehicleIn_ReportAdapter vehicleIn_reportAdapter;
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
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                //testAll();
                long mm = MemInfo.getmem_UNUSED(VehicleInReport.this);
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
                    Toast.makeText(VehicleInReport.this, "Printer Is Working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(VehicleInReport.this, "Out Of Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(VehicleInReport.this, "Exists Paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(VehicleInReport.this, "Printer High Temp Alarm", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_vehicle_in_report);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.vehreport));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));


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

        if(Globals.objLPR.getIndustry_Type().equals("4")){
            mService = ParkingIndustryActivity.mService;
        }
        else {
            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                mService = MainActivity.mService;
            } else {
                mService = Main2Activity.mService;
            }
        }
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
        if (Globals.PrinterType.equals("5")) {

            ServiceManager.getInstence().init(VehicleInReport.this);

        }
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        txt_compname=(TextView)findViewById(R.id.txt_companyname);
        txt_add=(TextView)findViewById(R.id.txt_adress);
        txt_date=(TextView)findViewById(R.id.txt_date);


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
       /* txt_getvtype=(TextView)findViewById(R.id.txt_getvehicletype);
        txt_getvtcont=(TextView)findViewById(R.id.txt_getttlcount);
        txt_vfareamnt=(TextView)findViewById(R.id.txt_getbasefareamnt);
*/
        intent=getIntent();
        str_type = intent.getStringExtra("type");
        sqlFooter = intent.getStringExtra("qry_footer");
        from=intent.getStringExtra("from");
        to=intent.getStringExtra("to");
        name = intent.getStringExtra("name");
        numCols = intent.getIntegerArrayListExtra("numCols");
        dateCols = intent.getIntegerArrayListExtra("dateCols");
        sql = intent.getStringExtra("qry");
        txt_date.setText(from + " to " + to);
        txt_compname.setText(Globals.objLPR.getCompany_Name());
         if(Globals.objLPR.getAddress().equals("")){
             txt_add.setVisibility(View.GONE);
         }
         else {
             txt_add.setVisibility(View.VISIBLE);
             txt_add.setText(Globals.objLPR.getAddress());
         }
        txtvehin=(TextView)findViewById(R.id.txt_vehiclein);
        txtvehout=(TextView)findViewById(R.id.txt_vehicleout);
        txt_totalamnt=(TextView)findViewById(R.id.totalamnt);
 /*       progressDialog = new ProgressDialog(VehicleInReport.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait...");
        progressDialog.show();*/
/*        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2 * 1000);
                    load_data();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = Globals.objsettings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "2";
        }
        getVehicleDetails();

        vehicleReportArrayList =new ArrayList<VehicleReport>();
        if(name.equals(getResources().getString(R.string.vehiclesummaryreport))){
            getqry1(sql);
            txt_totalamnt.setText(count_amnt_summry+"");
            getReportList();
        }
        else if(name.equals(getResources().getString(R.string.vehicleinreport))){
            getqry(sql);
            txt_totalamnt.setText(count_amnt+"");
            getReportList();
        }

    }

    public void getVehicleDetails(){

        String selectQuery = " Select  SUM(CASE WHEN order_status = 'OPEN' THEN 1 ELSE 0 END) AS 'OPEN', SUM(CASE WHEN order_status = 'CLOSE' THEN 1 ELSE 0 END) AS 'CLOSE' From orders" ;

        database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String vehiclein=cursor.getString(0);
                String vehicleout = cursor.getString(1);

                    txtvehin.setText(vehiclein);
                    txtvehout.setText(vehicleout);
            } while (cursor.moveToNext());

        }

        // closing connection
        cursor.close();
        // db.close();


    }


    private void getReportList() {

        RecyclerView order_list = (RecyclerView) findViewById(R.id.report_recyclerView);
        if (vehicleReportArrayList.size() > 0) {

            // order_arraylist = Orders.getAllOrders(getApplicationContext(), "WHERE is_active = '1' AND order_status ='OPEN' And z_code='0'", database);


            vehicleIn_reportAdapter = new VehicleIn_ReportAdapter(VehicleInReport.this, vehicleReportArrayList);
            //table_title.setVisibility(View.INVISIBLE);
            order_list.setVisibility(View.VISIBLE);
            order_list.setAdapter(vehicleIn_reportAdapter);

            int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            order_list.setLayoutManager(manager);


            vehicleIn_reportAdapter.notifyDataSetChanged();

        } else {

            order_list.setVisibility(View.GONE);
        }
    }

    private void load_data() {

        runOnUiThread(new Runnable() {
            public void run() {
                if (str_type.equals("online")) {
                    try {

                        sv = new ScrollView(VehicleInReport.this);
                        hsv = new HorizontalScrollView(VehicleInReport.this);
                        ll = (LinearLayout) findViewById(R.id.ll);

                        final TextView textView1 = new TextView(VehicleInReport.this);
                        textView1.setText(name);
                        textView1.setGravity(Gravity.CENTER);
                        ll.setGravity(Gravity.CENTER);
                        textView1.setPadding(0, 10, 0, 10);
                        textView1.setTextColor(Color.parseColor("#333333"));
                        textView1.setTextSize(20);

                        ll.addView(textView1);

                        try {
                            decimal_check = Globals.objLPD.getDecimal_Place();
                            qty_decimal_check = Globals.objsettings.get_Qty_Decimal();
                        } catch (Exception ex) {
                            decimal_check = "2";
                        }


                        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.FILL_PARENT);
                        tableLayout = new TableLayout(VehicleInReport.this);
                        tableLayout.setBackgroundColor(Color.BLACK);


                        // 2) create tableRow params
                        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.FILL_PARENT, 1f);
                        tableRowParams.setMargins(1, 1, 1, 1);
                        tableRowParams.weight = 1;

                        int i = 0;
                        Cursor mc = Globals.online_report_cursor;
                        Cursor colCursor = mc;
                        if (colCursor != null) {
                            if (colCursor != null)
                                colCursor.moveToFirst();

                            if (colCursor.moveToFirst()) {
                                do {
                                    {
                                        flag = "1";
                                        // 3) create tableRow
                                        tableRow = new TableRow(VehicleInReport.this);
                                        tableRow.setId(i);
                                        tableRow.setBackgroundColor(Color.BLACK);
                                        tableRow.setMinimumHeight(80);


                                        for (int j = 0; j < colCursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(VehicleInReport.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
                                            textView.setBackgroundColor(Color.parseColor("#fb7c3e"));
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setTextColor(Color.parseColor("#ffffff"));
                                            textView.setWidth(100);
                                            textView.setTextSize(16f);
                                            textView.setPadding(5, 5, 5, 5);
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(colCursor.getColumnName(j).toString());

                                            } else {

                                                if (numCols.contains(j)) {
                                                    textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                } else if (dateCols.contains(j)) {
                                                    textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                } else {
                                                    textView.setText(colCursor.getString(j).toString());
                                                }
                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }

                                } while (false);
                            }

                            i = 1;

                            if (mc != null)
                                mc.moveToFirst();
                            int rawIndex = 1;

                            if (mc.moveToFirst()) {
                                do {
                                    {
                                        // 3) create tableRow
                                        tableRow = new TableRow(VehicleInReport.this);

                                        tableRow.setBackgroundColor(Color.BLACK);

                                        for (int j = 0; j < mc.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(VehicleInReport.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
//                                            textView.setBackgroundColor(Color.WHITE);
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setPadding(0, 5, 0, 5);

                                            if(i%2==0) {
//                                                textView.setBackgroundColor(Color.WHITE);
                                                textView.setBackgroundColor(Color.WHITE);
                                            }
                                            //alternate background
                                            else {
                                                textView.setBackgroundColor(Color.parseColor("#FFFF66"));
                                            }
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(mc.getColumnName(j).toString());

                                            } else {

                                                if (j == 0) {
                                                    try {
                                                        tableRow.setId(Integer.parseInt(mc.getString(j).toString()));

                                                    } catch (Exception ex) {
                                                        tableRow.setId(Integer.parseInt("10000"));
                                                    }


                                                }
                                                try {

                                                    if (numCols.contains(j)) {
                                                        textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                    } else if (dateCols.contains(j)) {
                                                        textView.setPadding(10, 8, 8, 10);
                                                        textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                    } else {
                                                        if (j == 0) {
                                                            textView.setText(i + "");
                                                        } else {
                                                            textView.setText(colCursor.getString(j).toString());
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    textView.setText("");
                                                }


                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);

                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }


                                } while (mc.moveToNext());
                            }
                        }

                        hsv.addView(tableLayout);
                        sv.addView(hsv);
                        ll.addView(sv);
                    } catch (Exception ex) {

                    }
                } else {
                    try {
                        sv = new ScrollView(VehicleInReport.this);
                        hsv = new HorizontalScrollView(VehicleInReport.this);
                        ll = (LinearLayout) findViewById(R.id.ll);
                        ll.setGravity(Gravity.CENTER);
                        TextView textView1 = new TextView(VehicleInReport.this);
                        textView1.setText(name);
                        textView1.setGravity(Gravity.CENTER);
                        textView1.setPadding(0, 10, 0, 10);
                        textView1.setTextColor(Color.parseColor("#333333"));
                        textView1.setTextSize(20);
                        ll.addView(textView1);
                        String decimal_check;
                        try {
                            decimal_check = Globals.objLPD.getDecimal_Place();

                        } catch (Exception ex) {
                            decimal_check = "2";
                        }

                        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.FILL_PARENT);
                        tableLayout = new TableLayout(VehicleInReport.this);
                        tableLayout.setBackgroundColor(Color.BLACK);

                        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT, 1f);
                        tableRowParams.setMargins(1, 1, 1, 1);
                        tableRowParams.weight = 1;

                        if (sql.equals("")) {

                        } else {

                            int i = 0;
                            if (sql.contains("where") || sql.contains("Where") || sql.contains("WHERE")) {
//                                sql = sql + Globals.ReportCondition + "";
                                sql = sql + Globals.ReportCondition +" "+strAddLimit;
                            } else {
                                sql = sql + Globals.ReportCondition.replace("AND", " Where ") +" "+strAddLimit;
                            }
                            Cursor cursor = database.rawQuery(sql, null);
                            Cursor colCursor = cursor;

                            if (colCursor != null)
                                colCursor.moveToFirst();

                            if (colCursor.moveToFirst()) {

                                do {
                                    {
                                        flag = "1";
                                        // 3) create tableRow
                                        tableRow = new TableRow(VehicleInReport.this);
                                        tableRow.setId(i);
                                        tableRow.setBackgroundColor(Color.BLACK);
                                        tableRow.setMinimumHeight(80);


                                        for (int j = 0; j < colCursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(VehicleInReport.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
                                            textView.setBackgroundColor(Color.parseColor("#fb7c3e"));
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setTextColor(Color.parseColor("#ffffff"));
                                            textView.setTextSize(16f);
                                            textView.setPadding(5, 5, 5, 5);
                                            textView.setWidth(200);
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(colCursor.getColumnName(j).toString());

                                            } else {

                                                if (numCols.contains(j)) {
                                                    textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                } else if (dateCols.contains(j)) {
                                                    textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                } else {
                                                    textView.setText(colCursor.getString(j).toString());
                                                }
                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }

                                } while (false);
                            }

                            i = SnoCount;

                            if (cursor != null)
                                cursor.moveToFirst();
                            int rawIndex = 1;

                            if (cursor.moveToFirst()) {
                                do {
                                    {
                                        // 3) create tableRow
                                        tableRow = new TableRow(VehicleInReport.this);

                                        tableRow.setBackgroundColor(Color.BLACK);
                                        for (int j = 0; j < cursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(VehicleInReport.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
//                                            textView.setBackgroundColor(Color.WHITE);
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setPadding(0, 5, 0, 5);
                                            if(i%2==0) {
//                                                textView.setBackgroundColor(Color.WHITE);
                                                textView.setBackgroundColor(Color.parseColor("#FFFFE0"));
                                            }
                                            //alternate background
                                            else {
                                                textView.setBackgroundColor(Color.parseColor("#FFFF66"));
                                            }

                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(cursor.getColumnName(j).toString());

                                            } else {

                                                if (j == 0) {
                                                    try {
                                                        tableRow.setId(Integer.parseInt(cursor.getString(j).toString()));

                                                    } catch (Exception ex) {
                                                        tableRow.setId(Integer.parseInt("10000"));
                                                    }


                                                }
                                                try {

                                                    if (numCols.contains(j)) {
                                                        textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                    } else if (dateCols.contains(j)) {
                                                        textView.setPadding(10, 8, 8, 10);
                                                        textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                    } else {
                                                        if (j == 0) {
                                                            textView.setText(i + "");
                                                        } else {
                                                            textView.setText(colCursor.getString(j).toString());
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    textView.setText("");
                                                }
                                            }
                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }
                                } while (cursor.moveToNext());


                                if (sqlFooter.equals("")) {

                                } else {
/*                                    Cursor cursor1 = database.rawQuery(sqlFooter, null);
                                    int i1 = i;

                                    if (cursor1 != null)
                                        cursor1.moveToFirst();

                                    if (cursor1.moveToFirst()) {
                                        do {
                                            {
                                                // 3) create tableRow
                                                tableRow = new TableRow(VehicleInReport.this);

                                                tableRow.setBackgroundColor(Color.BLACK);

                                                for (int j = 0; j < cursor.getColumnCount(); j++) {
                                                    // 4) create textView
                                                    TextView textView = new TextView(VehicleInReport.this);
                                                    //  textView.setText(String.valueOf(j));dynamic table layout
                                                    textView.setBackgroundColor(Color.WHITE);
                                                    textView.setGravity(Gravity.CENTER);

                                                    String s1 = Integer.toString(i1);
                                                    String s2 = Integer.toString(j);
                                                    String s3 = s1 + s2;
                                                    id = Integer.parseInt(s3);
                                                    Log.d("TAG", "-___>" + id);
                                                    if (i1 == i) {
                                                        if (j == 0) {
                                                            try {
                                                                tableRow.setId(Integer.parseInt(cursor1.getString(j).toString()));

                                                            } catch (Exception ex) {
                                                                tableRow.setId(Integer.parseInt("10000"));
                                                            }

                                                        }
                                                        try {
                                                            if (numCols.contains(j)) {
                                                                textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(j).toString()), decimal_check));
                                                            } else if (dateCols.contains(j)) {
                                                                textView.setPadding(10, 8, 8, 10);
                                                                textView.setText(DateUtill.PaternDate2(cursor1.getString(j).toString()));
                                                            } else {
                                                                if (j == 0) {
                                                                    //textView.setText(i + "");
                                                                } else {
                                                                    textView.setText(cursor1.getString(j).toString());
                                                                }
                                                            }

                                                        } catch (Exception ex) {
                                                            textView.setText("");
                                                        }

                                                    } else {
                                                    }

                                                    // 5) add textView to tableRow
                                                    tableRow.addView(textView, tableRowParams);

                                                }

                                                // 6) add tableRow to tableLayout
                                                tableLayout.addView(tableRow, tableLayoutParams);
                                                i++;
                                            }


                                        } while (cursor.moveToNext());
                                    }*/
                                }
                            }


                            hsv.addView(tableLayout);
                            sv.addView(hsv);
                            ll.addView(sv);
                            if(vehicleReportArrayList.size()>0) {
                                pdfPerform_80mm();
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.receipt_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Print) {
            if (Globals.objsettings.getPrinterId().equals("") || Globals.objsettings.getPrinterId().equals("0")) {
                Toast.makeText(getApplicationContext(), R.string.chkpriset, Toast.LENGTH_SHORT).show();
            }
            else if(Globals.objsettings.getPrinterId().equals("1")){
                mobile_pos_parking();
            }
            else if(Globals.objsettings.getPrinterId().equals("5")) {

                ppt8527_parking();
            }
            else if(Globals.objsettings.getPrinterId().equals("6")) {

              ppt8555_parking();
            }
            else if(Globals.objsettings.getPrinterId().equals("2")) {

                bluetooth_56_parking();
            }
            else if(Globals.objsettings.getPrinterId().equals("3")) {

                bluetooth_80_parking();
            }
            else if(Globals.objsettings.getPrinterId().equals("7")) {

                String path= Globals.folder + Globals.pdffolder
                        + "/" + name+"80mm"+ ".pdf";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PrintManager printManager = (PrintManager) VehicleInReport.this.getSystemService(PRINT_SERVICE);
                    try {
                        PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(VehicleInReport.this, path);
                        String jobName = getApplicationContext().getString(R.string.app_name) + " Document";

                        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

                        PrintJobId printJobId = printJob.getId();

                    } catch (Exception e) {
                        Log.e("", e.getMessage());
                    }
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void ppt8555_parking() {


        if (Globals.objsettings.get_Print_Lang().equals("0")) {
            String Time = java.text.DateFormat.getTimeInstance().format(new Date());
            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strString = "";
                        int strLength = 14;

                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        if(Globals.objLPR.getShort_companyname().isEmpty()) {
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getCompany_Name() + "\n", "ST", 24, 1, callbackPPT8555);

                        }
                        else{
                            mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getShort_companyname() + "\n", "ST", 24, 1, callbackPPT8555);
                        }

                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getAddress() + "\n", "ST", 24, 1, callbackPPT8555);

                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objLPR.getMobile_No() + "\n", "ST", 24, 1, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                        strString = Globals.myRequiredString(" From Date" , strLength);
                        mIPosPrinterService.printSpecifiedTypeText(strString  + " : " + from + "\n", "ST", 24, callbackPPT8555);
                        strString = Globals.myRequiredString(" To Date" , strLength);
                        mIPosPrinterService.printSpecifiedTypeText(strString  + " : " + to + "\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);


                        mIPosPrinterService.printSpecifiedTypeText("WType      TVehicle     Amt\n", "ST", 24, callbackPPT8555);
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);
                        int count = 0;
                        while (count < vehicleReportArrayList.size()) {
                            mIPosPrinterService.printSpecifiedTypeText(vehicleReportArrayList.get(count).getVtype() + "           " + vehicleReportArrayList.get(count).getVcount()+ "          " + vehicleReportArrayList.get(count).getVprice() + "\n", "ST", 24, callbackPPT8555);
                            count++;
                        }
                        mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callbackPPT8555);

                        mIPosPrinterService.PrintSpecFormatText("" + Globals.objsettings.get_Copy_Right() + "\n", "ST", 24, 1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        mIPosPrinterService.printBlankLines(1, 1, callbackPPT8555);
                        //bitmapRecycle(bitmap);
                        mIPosPrinterService.printerPerformPrint(160, callbackPPT8555);
                    } catch (Exception e) {

                    }
                }
            });

        }
    }
    private void mobile_pos_parking() {

        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String strString = "";


                    for (int k = 0; k < Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                        String Print_type = "0";
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
                        if (Globals.objLPR.getShort_companyname().isEmpty()) {
                            woyouService.printTextWithFont("" + Globals.objLPR.getCompany_Name() + "\n", "", 28, callback);

                        } else {
                            woyouService.printTextWithFont("" + Globals.objLPR.getShort_companyname() + "\n", "", 28, callback);

                        }
                        if (Globals.objsettings.get_Print_Memo().equals("0")) {
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
                                woyouService.printColumnsText(new String[]{Globals.GSTLbl, ":", Globals.objLPR.getLicense_No()}, new int[]{6, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            // woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            woyouService.setAlignment(0, callback);
                            woyouService.setFontSize(30, callback);
                            int strLength = 14;
                            strString = Globals.myRequiredString(" From Date" , strLength);
                            woyouService.printTextWithFont(strString  + " : " + from + "\n", "", 24, callback);
                            strString = Globals.myRequiredString(" To Date" , strLength);
                            woyouService.printTextWithFont(strString  + " : " + to + "\n", "", 24, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("WType      TVehicle     Amt\n", "", 24, callback);
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            int count = 0;
                            while (count < vehicleReportArrayList.size()) {
                                woyouService.printTextWithFont(vehicleReportArrayList.get(count).getVtype() + "            " + vehicleReportArrayList.get(count).getVcount()+ "          " + vehicleReportArrayList.get(count).getVprice() + "\n", "", 24, callback);
                                count++;
                            }
                            woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            woyouService.printTextWithFont("" + Globals.objsettings.get_Copy_Right() + "\n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                            woyouService.printTextWithFont(" \n", "", 24, callback);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    private void ppt8527_parking() {
        try{
        String strString = "";
        int strLength = 14;
        JSONArray printTest = new JSONArray();


        ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);

        String Print_type = "0";
        if(Globals.objLPR.getShort_companyname().isEmpty()){
            printTest.put(getPrintObject(Globals.objLPR.getCompany_Name(), "3", "center"));

        }
        else{
            printTest.put(getPrintObject(Globals.objLPR.getShort_companyname(), "3", "center"));

        }

        printTest.put(getPrintObject(Globals.objLPR.getAddress(), "3", "center"));

        printTest.put(getPrintObject(Globals.objLPR.getMobile_No(), "3", "center"));
        try {
            if (Globals.objLPR.getService_code_tariff().equals("null") || Globals.objLPR.getService_code_tariff().equals("")|| Globals.objLPR.getService_code_tariff().isEmpty()  ) {

            } else {

                printTest.put(getPrintObject(Globals.objLPR.getService_code_tariff(), "3", "center"));
            }
        } catch (Exception ex) {

        }

        if (Globals.objLPR.getLicense_No().equals("null") || Globals.objLPR.getLicense_No().equals("") || Globals.objLPR.getLicense_No().isEmpty()) {

        } else {
            printTest.put(getPrintObject(Globals.GSTNo + ":" + Globals.objLPR.getLicense_No(), "3", "center"));

        }

        strString = Globals.myRequiredString(" From Date" , strLength);
        printTest.put(getPrintObject(strString  + " : " + from + "\n", "2", "left"));
        strString = Globals.myRequiredString(" To Date" , strLength);
        printTest.put(getPrintObject(strString  + " : " + to + "\n", "2", "left"));
        printTest.put(getPrintObject("--------------------------------\n", "2", "left"));
        printTest.put(getPrintObject("WType      TVehicle     Amt\n", "2", "left"));
        printTest.put(getPrintObject("--------------------------------\n", "2", "left"));
        int count = 0;
        while (count < vehicleReportArrayList.size()) {
            printTest.put(getPrintObject(vehicleReportArrayList.get(count).getVtype() + "            " + vehicleReportArrayList.get(count).getVcount()+ "          " + vehicleReportArrayList.get(count).getVprice() + "\n", "2", "left"));
            count++;
        }
        printTest.put(getPrintObject("--------------------------------\n", "2", "left"));
        printTest.put(getPrintObject(Globals.objsettings.get_Copy_Right(), "2", "center"));

        timeTools = new TimerCountTools();
        timeTools.start();
        printJson.put("spos", printTest);
        ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
        ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
        }catch(Exception e){

        }
    }
    private String bluetooth_56_parking(){
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);

        final byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        String Time = java.text.DateFormat.getTimeInstance().format(new Date());

        if (Globals.objsettings.get_Print_Lang().equals("0")) {

            try {
                byte[] ab;
                if (mService.isAvailable() == false) {
                } else {
                    String strString = "";
                    int strLength = 14;
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                        mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                    }
                    else{
                        mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");

                    }
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");


                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    strString = Globals.myRequiredString(" From Date" , strLength);
                    mService.sendMessage(strString  + " : " + from , "GBK");
                    strString = Globals.myRequiredString(" To Date" , strLength);
                    mService.sendMessage(strString  + " : " + to , "GBK");
                    mService.sendMessage("--------------------------------", "GBK");

                    mService.sendMessage("WType      TVehicle     Amt", "GBK");
                    mService.sendMessage("--------------------------------", "GBK");
                    int count = 0;
                    while (count < vehicleReportArrayList.size()) {
                        mService.sendMessage(vehicleReportArrayList.get(count).getVtype() + "            " + vehicleReportArrayList.get(count).getVcount()+ "          " + vehicleReportArrayList.get(count).getVprice(), "GBK");
                        count++;
                    }
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    mService.sendMessage(Globals.objsettings.get_Copy_Right() + "", "GBK");

                    mService.sendMessage("\n", "GBK");
                    mService.sendMessage("\n", "GBK");
                    mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");

                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                    byte[] print = {0x1b, 0x0c};
                    mService.write(print);
                    flag = "1";
                }
            }
            catch(Exception e){
            }
        }
        return flag;
    }
    private String bluetooth_80_parking(){
        String msg = "", flag = "0";
        String lang = getString(R.string.bluetooth_strLang);

        final byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        String Time = java.text.DateFormat.getTimeInstance().format(new Date());

        if (Globals.objsettings.get_Print_Lang().equals("0")) {

            try {
                byte[] ab;
                if (mService.isAvailable() == false) {
                } else {
                    String strString = "";
                    int strLength = 16;
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    if(Globals.objLPR.getShort_companyname().isEmpty()){
                        mService.sendMessage("" + Globals.objLPR.getCompany_Name().toUpperCase(), "GBK");

                    }
                    else{
                        mService.sendMessage("" + Globals.objLPR.getShort_companyname().toUpperCase(), "GBK");

                    }
                    mService.sendMessage("" + Globals.objLPR.getAddress(), "GBK");


                    mService.sendMessage("" + Globals.objLPR.getMobile_No(), "GBK");
                    ab = BytesUtil.setAlignCenter(0);
                    mService.write(ab);

                    strString = Globals.myRequiredString(" From Date" , strLength);
                    mService.sendMessage(strString  + " : " + from, "GBK");
                    strString = Globals.myRequiredString(" To Date" , strLength);
                    mService.sendMessage(strString  + " : " + to , "GBK");
                    mService.sendMessage("--------------------------------", "GBK");

                    mService.sendMessage("WType      TVehicle     Amt", "GBK");
                    mService.sendMessage("--------------------------------", "GBK");
                    int count = 0;
                    while (count < vehicleReportArrayList.size()) {
                        mService.sendMessage(vehicleReportArrayList.get(count).getVtype() + "            " + vehicleReportArrayList.get(count).getVcount()+ "          " + vehicleReportArrayList.get(count).getVprice() , "GBK");
                        count++;
                    }
                    ab = BytesUtil.setAlignCenter(1);
                    mService.write(ab);
                    mService.sendMessage("--------------------------------", "GBK");
                    mService.sendMessage(Globals.objsettings.get_Copy_Right() + "", "GBK");

                    mService.sendMessage("\n", "GBK");
                    mService.sendMessage("\n", "GBK");
                    mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");
//                            mService.sendMessage("\n", "GBK");

                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                    byte[] print = {0x1b, 0x0c};
                    mService.write(print);
                    flag = "1";
                }
            }
            catch(Exception e){
            }
        }
        return flag;
    }

public void getqry(String qry){

    Cursor cursor = database.rawQuery(qry, null);
    Cursor colCursor = cursor;

    if (colCursor != null)
        colCursor.moveToFirst();

    if (colCursor.moveToFirst()) {

        do {
           String vtype= cursor.getString(1);
            String vdate= cursor.getString(2);
           String vcount= cursor.getString(3);
           String vprice=cursor.getString(4);
            String vuername=cursor.getString(5);

            String price= Globals.myNumberFormat2Price(Double.parseDouble(vprice.toString()),decimal_check);
            count_amnt += Double.parseDouble(price);
            vehicleReportArrayList.add(new VehicleReport( vtype, vcount, vprice,vdate,vuername));

        } while (cursor.moveToNext());


    }

}

    public void getqry1(String qry){

        Cursor cursor = database.rawQuery(qry, null);
        Cursor colCursor = cursor;

        if (colCursor != null)
            colCursor.moveToFirst();

        if (colCursor.moveToFirst()) {

            do {
                String vtype= cursor.getString(1);
                String vcount= cursor.getString(2);
                String vprice=cursor.getString(3);

                String price= Globals.myNumberFormat2Price(Double.parseDouble(vprice.toString()),decimal_check);
                count_amnt_summry += Double.parseDouble(price);
                vehicleReportArrayList.add(new VehicleReport( vtype, vcount, vprice,"",""));

            } while (cursor.moveToNext());

        }

    }
    class PrinterListener implements OnPrinterListener {
        private final String TAG = "Print";

        @Override
        public void onStart() {
            // TODO 打印开始
            // Print start
            Log.d("start print","START");
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
                Toast.makeText(VehicleInReport.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(VehicleInReport.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(VehicleInReport.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }
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

    public void pdfPerform_80mm(){

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + name+ "80mm" + ".pdf");
            //if (Globals.objsettings.get_Print_Lang().equals("0")) {
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.B7);
            document.setMargins(-5f, -5f, 10f, 10f);

            PdfWriter writer = PdfWriter.getInstance(document, file);

            Image image = null;
//            image = createimage();
            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);


                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.Order), B12));

                cellh.setBorder(Rectangle.NO_BORDER);
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);

                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(1);

                Phrase prcommpanyname ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getCompany_Name(), B10);
                } else {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getShort_companyname(), B10);

                }
                PdfPCell cell_company_name;

                cell_company_name = new PdfPCell(prcommpanyname);
                cell_company_name.setBorder(Rectangle.NO_BORDER);

                cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_company_name.addCell(cell_company_name);

                table_company_name.setSpacingBefore(5.0f);

                PdfPTable table_company_adres = new PdfPTable(1);

                Phrase prcommpanyadd ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10);
                } else {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10);

                }
                PdfPCell cell_company_add;

                cell_company_add = new PdfPCell(prcommpanyadd);
                cell_company_add.setBorder(Rectangle.NO_BORDER);
                cell_company_add.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_company_adres.addCell(cell_company_add);

                PdfPTable table_company_mobile = new PdfPTable(1);

                Phrase prcommpanymobile ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10);
                } else {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10);

                }
                PdfPCell cell_company_mobile;

                cell_company_mobile = new PdfPCell(prcommpanymobile);
                cell_company_mobile.setBorder(Rectangle.NO_BORDER);
                // cell_company_add.setColspan(1);
                cell_company_mobile.setHorizontalAlignment(Element.ALIGN_CENTER);
                //cell_company_name.setPadding(5.0f);
                table_company_mobile.addCell(cell_company_mobile);

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prposno = new Phrase(getString(R.string.fromdate), B10);
                PdfPCell cell_posno = new PdfPCell(prposno);
                cell_posno.setBorder(Rectangle.NO_BORDER);

                //  cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);

                table_posno.addCell(cell_posno);
                prposno = new Phrase("" + from , B10);
                PdfPCell possecondcolumn = new PdfPCell(prposno);
                possecondcolumn.setBorder(Rectangle.NO_BORDER);

                possecondcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(possecondcolumn);
                document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prorderno = new Phrase(getString(R.string.todate), B10);
                PdfPCell cell_order_no = new PdfPCell(prorderno);
                cell_order_no.setBorder(Rectangle.NO_BORDER);

                // cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);

                table_order_no.addCell(cell_order_no);
                prorderno = new Phrase("" + to, B10);
                PdfPCell ordercol = new PdfPCell(prorderno);

                ordercol.setBorder(Rectangle.NO_BORDER);
                ordercol.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(ordercol);
                document.open();


                PdfPTable table = new PdfPTable(3);

                Phrase pr = new Phrase(getString(R.string.wtype), B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setBorder(Rectangle.NO_BORDER);

                // c1.setPadding(5);
                table.addCell(c1);
                pr = new Phrase(getString(R.string.tvehicle), B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setBorder(Rectangle.NO_BORDER);

                // c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c3);
                pr = new Phrase(getString(R.string.amt), B10);
                PdfPCell c2 = new PdfPCell(pr);
                // c2.setPadding(5);
                c2.setBorder(Rectangle.NO_BORDER);

                c2.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c2);

                for (int i = 0; i < vehicleReportArrayList.size(); i++) {
                    Phrase pr1 = new Phrase(vehicleReportArrayList.get(i).getVtype(), N9);
                    PdfPCell c7 = new PdfPCell(pr1);
                    //c7.setPadding(5);
                    c7.setBorder(Rectangle.NO_BORDER);

                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c7);

                    pr1 = new Phrase(vehicleReportArrayList.get(i).getVcount(), N9);
                    c7 = new PdfPCell(pr1);
                    //c7.setPadding(5);
                    c7.setBorder(Rectangle.NO_BORDER);

                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c7);

                    pr1 = new Phrase(vehicleReportArrayList.get(i).getVprice(), N9);
                    c7 = new PdfPCell(pr1);
                    //c7.setPadding(5);
                    c7.setBorder(Rectangle.NO_BORDER);

                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c7);


                }
                table.setSpacingBefore(10.0f);
                table.setHeaderRows(1);
                document.open();





                document.add(tableh);
                document.add(table_company_name);
                document.add(table_company_adres);
                document.add(table_company_mobile);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table);

                document.newPage();
                document.close();
                file.close();
            }


            Toast.makeText(getApplicationContext(), "Pdf Created Successfully at "+ f.getPath(),
                    Toast.LENGTH_SHORT).show();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                } catch (ActivityNotFoundException e) {
                }
            }
            // }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }



}