package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.InputPBOCInitData;
import com.basewin.define.PBOCOption;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.google.gson.Gson;
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
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.phomellolitepos.Adapter.BankListAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Paytm.Api;
import org.phomellolitepos.Paytm.Checksum;
import org.phomellolitepos.Paytm.Constants;
import org.phomellolitepos.Paytm.Paytm;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.Util.Watermark;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Acc_Customer_Debit;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Bank;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Customer_PriceBook;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Loyalty_Earn;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Tax_Master;

import org.phomellolitepos.pboc.onlinePBOCListener;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.utils.GlobalData;
import org.phomellolitepos.utils.TimerCountTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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


public class PaymentActivity extends AppCompatActivity {
    EditText edt_mobile, edt_net_amount, edt_tender, edt_change, edt_tax, edt_total, edt_discount, edt_discount_value, edt_cheque, edt_description, edt_date;
    Button btn_paytm, btn_charge, btn_split, btn_get_cus;
    Spinner spn_pay_method, spn_bank;
    Button switch_discount;
    TextView txt_show_info;
    TableRow row_discount;
    String decimal_check, qty_decimal_check;
    Database db;
    SQLiteDatabase database;
    String date, modified_by;
    Double iPrice = 0d;
    Double iTax = 0d;
    Double total;
    String total_tax, discount_type = "P";
    Double showDiscount, final_netamount = 0d, priceAfDis;
    ArrayList<Order_Type_Tax> order_type_taxArrayList;
    String temp, opr = "", strOrderCode = "";
    Context context;
    Orders objOrder;
    ArrayList<String> list1a, list2a, list3a, list4a;
    String strOrderNo = "";
    Settings settings;
    int scale;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog progressDialog;
    String part2;
    String orderId = null;
    Calendar myCalendar;
    //Customer display variables
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String displayTilte;
    ProgressDialog pDialog;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    ArrayList<Payment> paymentArrayList;
    ArrayList<Bank> bankArrayList;
    LinearLayout tb_row_bank;
    String strBankCode, strPayMethod, PayId;
    Lite_POS_Device lite_pos_device;
    Bank bank;
    Payment payment;
    String payment_mode = "";
    ImageView img_date;
    String PrinterType;
    Contact contact;
    String ck_project_type;
    CheckBox chk_cus_debit;
    Customer_PriceBook customer_priceBook;
    IWoyouService woyouService;
    private ICallback callback1 = null;
    public static PrinterBinder printer;
    ArrayList<Order_Payment> order_payment_array;
    MenuItem menuItem;

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
                long mm = MemInfo.getmem_UNUSED(PaymentActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Payment);
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
//        String amt = "", scale = "5", place = "3";
        String amt = "";
        //Denomination(amt, scale, place);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        if (settings.getPrinterId().equals("7")) {
            try {
                ServiceManager.getInstence().init(PaymentActivity.this);
            } catch (Exception ex) {
            }
        }

        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        myCalendar = Calendar.getInstance();

        ArrayList<ShoppingCart> myCart = Globals.cart;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
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
            Intent intent = new Intent();
            intent.setPackage("woyou.aidlservice.jiuiv5");
            intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
            startService(intent);
            bindService(intent, connService, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
        }

        if (settings != null) {
            if (settings.get_Is_Customer_Display().equals("true")) {
                try {
                    mDSKernel = DSKernel.newInstance();
                    mDSKernel.checkConnection();
                    mDSKernel.init(getApplicationContext(), mConnCallback);
                    mDSKernel.addReceiveCallback(mReceiveCallback);
                } catch (Exception ex) {
                }
            }
        }

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        ck_project_type = lite_pos_registration.getproject_id();

        if (ck_project_type.equals("cloud")) {
            if (isNetworkStatusAvialable(getApplicationContext())) {
                new Thread() {
                    @Override
                    public void run() {
                        GetCustomerCreditDetail();
                    }
                }.start();
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PaymentActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });


        Intent intent1 = getIntent();
        opr = intent1.getStringExtra("opr");
        strOrderCode = intent1.getStringExtra("order_code");

        if (opr == null) {
            opr = "Add";
            strOrderCode = "";
        }


        modified_by = Globals.user;

        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();

        try {
            scale = Integer.parseInt(settings.get_Scale());
        } catch (Exception ex) {
            scale = 0;
        }
        img_date = (ImageView) findViewById(R.id.img_date);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_net_amount = (EditText) findViewById(R.id.edt_net_amount);
        edt_tender = (EditText) findViewById(R.id.edt_tender);
        edt_change = (EditText) findViewById(R.id.edt_change);
        edt_tax = (EditText) findViewById(R.id.edt_tax);
        edt_discount = (EditText) findViewById(R.id.edt_discount);
        edt_total = (EditText) findViewById(R.id.edt_total);
        edt_discount_value = (EditText) findViewById(R.id.edt_discount_value);
        edt_cheque = (EditText) findViewById(R.id.edt_cheque);
        edt_description = (EditText) findViewById(R.id.edt_description);
        row_discount = (TableRow) findViewById(R.id.row_discount);
        btn_charge = (Button) findViewById(R.id.btn_charge);
        btn_split = (Button) findViewById(R.id.btn_split);
        switch_discount = (Button) findViewById(R.id.switch_discount);
        btn_paytm = (Button) findViewById(R.id.btn_paytm);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        spn_bank = (Spinner) findViewById(R.id.spn_bank);
        tb_row_bank = (LinearLayout) findViewById(R.id.tb_row_bank);
        btn_paytm.setVisibility(View.GONE);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile);
        btn_get_cus = (Button) findViewById(R.id.btn_get_cus);
        txt_show_info = (TextView) findViewById(R.id.txt_show_info);
        chk_cus_debit = (CheckBox) findViewById(R.id.chk_cus_debit);

        String strChangeParam = settings.get_Change_Parameter();
        if (strChangeParam.equals("AD")) {
            chk_cus_debit.setChecked(true);
            chk_cus_debit.setEnabled(false);
        } else if (strChangeParam.equals("AC")) {
            chk_cus_debit.setChecked(false);
            chk_cus_debit.setEnabled(false);
        } else {
            chk_cus_debit.setChecked(false);
            chk_cus_debit.setEnabled(true);
        }


        btn_get_cus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_customer_dialog(Globals.strContact_Code);
            }
        });

        switch_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discount_type = switch_discount.getText().toString();
                //here discount_type  F means percentage

                if (discount_type.equals("P")) {
                    edt_discount_value.setText("");
                    row_discount.setVisibility(View.VISIBLE);
                    switch_discount.setText("F");
                    discount_type = "F";

                } else {
                    row_discount.setVisibility(View.VISIBLE);
                    edt_discount_value.setText("");
                    switch_discount.setText("P");
                    discount_type = "P";
                }
            }
        });

        try {
            decimal_check = lite_pos_device.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PaymentActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_charge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (edt_discount.getText().toString().length() > 0) {

                    edt_discount.requestFocus();

                }

            }
        });


        edt_discount_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (edt_discount.getText().toString().length() > 0) {

                    edt_discount.requestFocus();

                } else {
                    btn_charge.requestFocus();
                }
            }
        });

        edt_tender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (edt_discount.getText().toString().length() > 0) {

                    edt_discount.requestFocus();

                } else {
                    btn_charge.requestFocus();
                }
            }
        });


        edt_change.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (edt_change.getText().toString().equals("")) {

                    edt_tender.requestFocus();

                } else {
                    btn_charge.requestFocus();
                }
            }
        });


        edt_discount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    if (edt_discount.getText().toString().equals("\n") || edt_discount.getText().toString().equals("")) {
                        String net_amount;
                        Double netAmt = 0d;
                        if (Globals.strIsDenominationPrint.equals("true")) {
                            netAmt = Globals.Denomination(Double.parseDouble(total + ""), scale, decimal_check);
                        } else {
                            netAmt = Double.parseDouble(temp + "");
                        }
                        //Double netAmt = Globals.Denomination(Double.parseDouble(total + ""), scale, decimal_check);
                        net_amount = Globals.myNumberFormat2Price(netAmt, decimal_check);
                        edt_net_amount.setText(net_amount);
                        edt_discount_value.setText("");
                        edt_tender.requestFocus();
                    } else {
                        User user = User.getUser(getApplicationContext(), "WHERE is_active='1' And user_code='" + Globals.user + "'", database);
                        String max_discount;
                        max_discount = user.get_max_discount();
                        String strDiscount = edt_discount.getText().toString();
                        String strDisValue = strDiscount;
                        boolean bFlag = false;
                        Globals.DiscountPer = edt_discount.getText().toString();
                        if (switch_discount.getText().toString().equals("F")) {
                            // strDiscount = (Double.parseDouble(edt_total.getText().toString()) - (Double.parseDouble(strDiscount) * 100)/Double.parseDouble(edt_total.getText().toString()) )+"";

                            ArrayList<ShoppingCart> myCart = Globals.cart;
                            Double mySalesTotal = 0d;

                            for (int count = 0; count < myCart.size(); count++) {
                                mySalesTotal = mySalesTotal + (Double.parseDouble(myCart.get(count).get_Sales_Price()) * Double.parseDouble(myCart.get(count).get_Quantity()));
                            }

                            strDiscount = ((Double.parseDouble(strDiscount) * 100) / mySalesTotal) + "";

                            strDisValue = strDiscount;
                            bFlag = true;
                            discount_type = "P";
                        }

                        if (discount_type.equals("P")) {

                            if (Double.parseDouble(max_discount) >= Double.parseDouble(strDiscount)) {
                                Globals.order_tax_array.clear();
                                Globals.order_item_tax.clear();
                                final_netamount = 0d;
                                Double totalDisnt = 0d;
//                                showDiscount = (Double.parseDouble(edt_total.getText().toString()) * Double.parseDouble(strDiscount) / 100);
//                                final_netamount = Double.parseDouble(edt_total.getText().toString()) - showDiscount;

//                                String strShowDiscount;
//                                strShowDiscount = Globals.myNumberFormat2Price(Double.parseDouble(showDiscount + ""), decimal_check);
//                                edt_discount.setText("");
//                                edt_discount_value.setText(strShowDiscount);

                                ArrayList<ShoppingCart> myCart = Globals.cart;

                                for (int count = 0; count < myCart.size(); count++) {
                                    myCart.get(count);
                                    Double lineTotal = Double.parseDouble(myCart.get(count).get_Sales_Price());
                                    showDiscount = ((lineTotal) * Double.parseDouble(strDisValue) / 100);
                                    priceAfDis = Double.parseDouble(myCart.get(count).get_Sales_Price()) - showDiscount;
                                    myCart.get(count).set_Discount(priceAfDis + "");
                                    totalDisnt = totalDisnt + ((Double.parseDouble(myCart.get(count).get_Sales_Price()) - priceAfDis) * Double.parseDouble(myCart.get(count).get_Quantity()));
                                    ArrayList<Item_Group_Tax> item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + myCart.get(count).get_Item_Code() + "'", database, db);
                                    Double iTax = 0d;
                                    Double iTaxTotal = 0d;
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        iTax = 0d;
                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                        String tax_id = item_group_tax.get_tax_id();
                                        Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + tax_id + "'", database, db);
                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (priceAfDis * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            //Double.parseDouble(Globals.myNumberFormat2Price(iTax,decimal_check));
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }


                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        ;

                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", myCart.get(count).get_Item_Code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                    }
                                    myCart.get(count).set_Tax_Price(iTaxTotal + "");
                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (priceAfDis + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                    final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
                                }

                                String strShowDiscount;
                                strShowDiscount = Globals.myNumberFormat2Price(Double.parseDouble(totalDisnt + ""), decimal_check);
                                edt_discount.setText("");
                                edt_discount_value.setText(strShowDiscount);

                                Globals.cart = myCart;

                                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);
                                iTax = 0.0;
                                if (order_type_taxArrayList.size() > 0) {
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

                                        OrderTaxArray orderTaxArray = new OrderTaxArray(context, "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                                        Globals.order_tax_array.add(orderTaxArray);
                                    }

                                    total = iPrice + iTax;
                                    temp = total + "";

                                } else {

                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                    iPrice = Double.parseDouble(final_netamount + "");
                                    total = iPrice;
                                    temp = total + "";

                                }

                                Double netAmt = 0d;

                                if (Globals.strIsDenominationPrint.equals("true")) {
                                    netAmt = Globals.Denomination(Double.parseDouble(temp + ""), scale, decimal_check);
                                } else {
                                    netAmt = Double.parseDouble(temp + "");
                                }

//                                net_amount = Globals.myNumberFormat2Price(Double.parseDouble(final_netamount + ""), decimal_check);
                                String net_amount;
                                net_amount = Globals.myNumberFormat2Price(netAmt, decimal_check);
                                edt_tax.setText(Globals.myNumberFormat2Price(iTax, decimal_check));
                                edt_net_amount.setText(net_amount);
                                edt_tender.setText(net_amount);
                                edt_tender.requestFocus();
                            } else {
                                edt_discount.setError("Maximum Discount : " + max_discount);
                                edt_discount.requestFocus();
                            }

                            if (settings.get_Is_Customer_Display().equals("true")) {
                                try {
                                    if (settings.get_CustomerDisplay().equals("1")) {
                                        call_cmdTotalA();
                                        call_MNTotal();
                                    } else {
                                        call_cmdTotal();
                                        call_MNTotal();
                                    }
                                } catch (Exception ex) {
                                }
                            }

                        } else {

                            String ab = edt_discount.getText().toString();
                            max_discount = (Double.parseDouble(edt_total.getText().toString()) * Double.parseDouble(max_discount) / 100) + "";
                            String strMaxDis = Globals.myNumberFormat2Price(Double.parseDouble(max_discount + ""), decimal_check);
                            if (Double.parseDouble(max_discount) >= Double.parseDouble(strDiscount)) {


                                Globals.order_tax_array.clear();
                                Globals.order_item_tax.clear();
                                final_netamount = (Double.parseDouble(edt_total.getText().toString()) - Double.parseDouble(edt_discount.getText().toString()));
                                String net_amount;


                                String strShowDiscount;
                                strShowDiscount = Globals.myNumberFormat2Price(Double.parseDouble(ab + ""), decimal_check);
                                edt_discount.setText("");

                                edt_discount_value.setText(strShowDiscount);

                                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);
                                iTax = 0.0;
                                if (order_type_taxArrayList.size() > 0) {
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

                                        OrderTaxArray orderTaxArray = new OrderTaxArray(context, "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                                        Globals.order_tax_array.add(orderTaxArray);
                                    }

                                    total = iPrice + iTax;
                                    temp = total + "";

                                } else {
                                    iPrice = Double.parseDouble(final_netamount + "");
                                    total = iPrice;
                                    temp = total + "";

                                }
                                Double netAmt = 0d;
                                if (Globals.strIsDenominationPrint.equals("true")) {
                                    netAmt = Globals.Denomination(Double.parseDouble(temp + ""), scale, decimal_check);
                                } else {
                                    netAmt = Double.parseDouble(temp + "");
                                }
                                //  Double netAmt = Globals.Denomination(Double.parseDouble(temp + ""), scale, decimal_check);

                                net_amount = Globals.myNumberFormat2Price(netAmt, decimal_check);
                                edt_tax.setText(Globals.myNumberFormat2Price(iTax, decimal_check));
                                edt_net_amount.setText(net_amount);


                                edt_tender.requestFocus();


                            } else {

                                edt_discount.setError("Maximum Discount : " + strMaxDis);
                                edt_discount.requestFocus();

                            }
                            if (settings.get_Is_Customer_Display().equals("true")) {
                                try {
                                    if (settings.get_CustomerDisplay().equals("1")) {
                                        call_cmdTotalA();
                                        call_MNTotal();
                                    } else {
                                        call_cmdTotal();
                                        call_MNTotal();
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        }

                    }

                    return true;
                }

                return false;
            }
        });


        if (opr.equals("Edit")) {

            Orders orders = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderCode + "'");
            Order_Payment order_payment = Order_Payment.getOrder_Payment(getApplicationContext(), "WHERE order_code='" + strOrderCode + "'", database);
            try {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + orders.get_contact_code() + "'");
                if (contact == null) {
                } else {
                    edt_mobile.setText(contact.get_contact_1());
                    txt_show_info.setText(contact.get_name());
                }

            } catch (Exception ex) {

            }
            try {
                bank = Bank.getBank(getApplicationContext(), " WHERE bank_code='" + order_payment.get_field1() + "'", database);
                payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id='" + order_payment.get_payment_id() + "'");
            } catch (Exception ex) {

            }
            try {

                edt_date.setText(orders.get_delivery_date());

                Globals.order_tax_array.clear();
                String subtotal;

                subtotal = Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check);
//                subtotal = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_sub_total()), decimal_check);
                edt_total.setText(subtotal);


//
                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);

                if (order_type_taxArrayList.size() > 0) {
                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                        Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                        String tax_id = order_type_tax.get_tax_id();

                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                        Double itempTax = 0d;
                        if (tax_master.get_tax_type().equals("P")) {
                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                        } else {
                            iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                            itempTax = Double.parseDouble(tax_master.get_rate());
                        }

                        OrderTaxArray orderTaxArray = new OrderTaxArray(context, "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                        Globals.order_tax_array.add(orderTaxArray);

                    }
                } else {

                }


                String strTax;

                if (orders.get_total_tax().equals("0") || orders.get_total_tax().equals("")) {

                    strTax = Globals.myNumberFormat2Price(iTax, decimal_check);
                    edt_tax.setText(strTax);

                } else {
                    strTax = Globals.myNumberFormat2Price(iTax, decimal_check);
                    edt_tax.setText(strTax);
                }


                if (orders.get_total_discount().equals("0") || orders.get_total_discount().equals("")) {
                    String strDiscount;
                    strDiscount = Globals.myNumberFormat2Price(0, decimal_check);
                    edt_discount_value.setText(strDiscount);
                    row_discount.setVisibility(View.VISIBLE);
                } else {

                    edt_discount_value.setText(orders.get_total_discount());
                    row_discount.setVisibility(View.VISIBLE);
                }


//                Double NetAmount = (Double.parseDouble(edt_total.getText().toString()) - Double.parseDouble(edt_discount_value.getText().toString())) + Double.parseDouble(edt_tax.getText().toString());
                String dis = "";
                if (edt_discount_value.getText().toString().trim().equals("")) {

                    dis = Globals.myNumberFormat2Price(0, decimal_check);
                } else {
                    dis = Globals.myNumberFormat2Price(Double.parseDouble(edt_discount_value.getText().toString().trim()), decimal_check);
                }
                Double NetAmount = (Double.parseDouble(edt_total.getText().toString()) - Double.parseDouble(dis) + Double.parseDouble(edt_tax.getText().toString()));

                String net_amount;
                net_amount = Globals.myNumberFormat2Price(NetAmount, decimal_check);


                if (orders.get_total().equals("0") || orders.get_total().equals("")) {

                    net_amount = Globals.myNumberFormat2Price(0, decimal_check);
                    edt_net_amount.setText(net_amount);
                    edt_tender.setText(net_amount);
                    edt_tender.requestFocus();
                    edt_tender.selectAll();

                } else {
                    Double netAmt = 0d;
                    if (Globals.strIsDenominationPrint.equals("true")) {
                        netAmt = Globals.Denomination(Double.parseDouble(net_amount + ""), scale, decimal_check);
                    } else {
                        netAmt = Double.parseDouble(net_amount + "");
                    }
                    //Double netAmt = Globals.Denomination(Double.parseDouble(net_amount), scale, decimal_check);
                    String net_amount1 = Globals.myNumberFormat2Price(netAmt, decimal_check);
                    edt_net_amount.setText(net_amount1);
                    edt_tender.setText(net_amount1);
                    edt_tender.requestFocus();
                    edt_tender.selectAll();
                }

                edt_description.setText(orders.get_remarks());


                payment_mode = payment.get_payment_name();
                if (payment_mode.equals("CHEQUE")) {
                    tb_row_bank.setVisibility(View.VISIBLE);
                    fill_spinner_bank(bank.get_bank_name());
                    edt_cheque.setText(order_payment.get_card_number());
                } else {
                    tb_row_bank.setVisibility(View.GONE);
                }


            } catch (Exception ex) {

            }
            fill_spinner_pay_method(payment_mode);
        } else

        {

            if (Globals.strContact_Code.equals("")) {

            } else {
                try {
                    Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strContact_Code + "'");
                    edt_mobile.setText(contact.get_contact_1());
                    txt_show_info.setText(contact.get_name());
                } catch (Exception ex) {
                }
            }

            Globals.order_tax_array.clear();

            String strDis;

            strDis = Globals.myNumberFormat2Price(0, decimal_check);

            edt_discount_value.setText(strDis);

            String subtotal;

            subtotal = Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check);
            edt_total.setText(subtotal);

            order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);

            if (order_type_taxArrayList.size() > 0) {
                for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                    Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                    String tax_id = order_type_tax.get_tax_id();

                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                    iPrice = Double.parseDouble(subtotal);
                    Double itempTax = 0d;
                    if (tax_master.get_tax_type().equals("P")) {
                        try {
                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                        } catch (Exception ex) {
                            String ab = ex.getMessage();
                            ab = ab;
                        }


                    } else {
                        iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                        itempTax = Double.parseDouble(tax_master.get_rate());
                    }

                    OrderTaxArray orderTaxArray = new OrderTaxArray(context, "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                    Globals.order_tax_array.add(orderTaxArray);
                }

                total = iPrice + iTax;
                temp = total + "";

            } else {

                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                total = iPrice;
                temp = total + "";

            }


            String strTax;

            strTax = Globals.myNumberFormat2Price(iTax, decimal_check);

            edt_tax.setText(strTax);

            String net_amount;
            String total1;

            Double netAmt = 0d;
            if (Globals.strIsDenominationPrint.equals("true")) {
                netAmt = Globals.Denomination(Double.parseDouble(total + ""), scale, decimal_check);
            } else {
                netAmt = Double.parseDouble(temp + "");
            }

            net_amount = Globals.myNumberFormat2Price(netAmt, decimal_check);
            edt_net_amount.setText(net_amount);
            edt_tender.setText(net_amount);
            edt_tender.requestFocus();
            edt_tender.selectAll();
            fill_spinner_bank("");
            fill_spinner_pay_method("");
        }


        edt_tax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!edt_tax.getText().toString().equals("")) {
                    edt_change.requestFocus();
                }
            }
        });

        edt_tender.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (edt_tender.getText().toString().equals("\n") || edt_tender.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                        edt_tender.requestFocus();
                    } else {
                        Double netAmountStr = Double.parseDouble(edt_net_amount.getText().toString());
                        Double tenderAmountStr = Double.parseDouble(edt_tender.getText().toString());
                        Double final_amount = tenderAmountStr - netAmountStr;
                        if (final_amount < 0) {
                            Double Change = 0.0;
                            String str_change;
                            str_change = Globals.myNumberFormat2Price(Change, decimal_check);
                            edt_change.setText(str_change);
                        } else {
                            String str_final_amount;
                            str_final_amount = Globals.myNumberFormat2Price(final_amount, decimal_check);
                            edt_change.setText(String.valueOf(str_final_amount));
                            btn_charge.requestFocus();
                        }

                        if (settings.get_Is_Customer_Display().equals("true")) {
                            Thread timerThread1 = new Thread() {
                                public void run() {
                                    try {
                                        sleep(2000);
                                        if (settings.get_CustomerDisplay().equals("1")) {
                                            call_cmdTanderA();
                                            call_MNTander();
                                        } else {
                                            call_cmdTander();
                                            call_MNTander();
                                        }
                                        Thread timerThread = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(2000);
                                                    if (settings.get_CustomerDisplay().equals("1")) {
                                                        call_cmdChangeA();
                                                        call_MNChnage();
                                                    } else {
                                                        call_cmdChange();
                                                        call_MNChnage();
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                }
                                            }
                                        };
                                        timerThread.start();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                    }
                                }
                            };
                            timerThread1.start();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        btn_charge.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (chk_cus_debit.isChecked()) {
                    if (Globals.strContact_Code.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please select customer for debit", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(edt_tender.getText().toString().trim()) > Double.parseDouble(edt_net_amount.getText().toString().trim()) && !PayId.equals("1")) {

                        Toast.makeText(getApplicationContext(), "Tender should not grater then net amount with payment mode", Toast.LENGTH_SHORT).show();

                    } else {

                        progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();
                        new Thread() {
                            public void run() {
                                call_MN();
                                if (settings.get_Is_Cash_Drawer().equals("true")) {
                                    open_cash_drawer();
                                }
                                CheckOutOrder();

                            }
                        }.start();
                    }
                } else {
                    if (Double.parseDouble(edt_tender.getText().toString().trim()) > Double.parseDouble(edt_net_amount.getText().toString().trim()) && !PayId.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Tender should not grater then net amount with payment mode", Toast.LENGTH_SHORT).show();
                    } else {

                        progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();
                        new Thread() {
                            public void run() {
                                call_MN();
                                if (settings.get_Is_Cash_Drawer().equals("true")) {
                                    open_cash_drawer();
                                }
                                CheckOutOrder();
                            }
                        }.start();
                    }
                }
            }
        });

        btn_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PaymentActivity.this, PaymentSplitActivity.class);
                intent.putExtra("Amount", edt_net_amount.getText().toString().trim());
                intent.putExtra("opr", opr);
                intent.putExtra("order_code", strOrderCode);

                Globals.Net_Amount = Double.parseDouble(edt_net_amount.getText().toString());
                Globals.Inv_Tax = iTax;
                Globals.Inv_Description = edt_description.getText().toString().trim();
                try {
                    Globals.Inv_Discount = Double.parseDouble(edt_discount_value.getText().toString());
                } catch (Exception e) {
                    Globals.Inv_Discount = 0d;
                }

                Globals.Inv_Opr = opr;
                Globals.Inv_Odr_Code = strOrderCode;
                Globals.Sub_Total = Double.parseDouble(edt_total.getText().toString());
                Globals.Inv_Delivery_Date = edt_date.getText().toString();
                startActivity(intent);
            }
        });

        if (settings.get_Is_Customer_Display().equals("true"))

        {
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        //sleep(1000);
                        if (settings.get_CustomerDisplay().equals("1")) {
                            call_cmdTotalA();
                            call_MNTotal();
                        } else {
                            call_cmdTotal();
                            call_MNTotal();
                        }

                    } finally {
                    }
                }
            };
            timerThread.start();
        }

        spn_pay_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Payment resultp = paymentArrayList.get(position);
                strPayMethod = resultp.get_payment_name();
                try {
                    PayId = resultp.get_payment_id();
                } catch (Exception ex) {
                    PayId = "";
                }

                if (PayId.equals("5")) {
                    if (Globals.strContact_Code.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please select customer for this mode", Toast.LENGTH_SHORT).show();
                        fill_spinner_pay_method("");
                    }
                } else if (PayId.equals("2")) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "Swipe Card!", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    toast.show();
//                    if (settings.getPrinterId().equals("5")) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                login();
//                            }
//                        }).start();
//
//                        onlineTrans();
//                    }


                } else if (strPayMethod.equals("CHEQUE")) {
                    tb_row_bank.setVisibility(View.VISIBLE);

                } else {
                    tb_row_bank.setVisibility(View.GONE);
                }
                fill_spinner_bank("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bank resultp = bankArrayList.get(position);
                strBankCode = resultp.get_bank_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void call_MNTotal() {
        String subtotal;
        try {
            subtotal = Globals.myNumberFormat2Price(Double.parseDouble(edt_net_amount.getText().toString()), decimal_check);
        } catch (Exception ex) {
            subtotal = Globals.myNumberFormat2Price(0, decimal_check);
        }

        final String finalSubtotal2 = subtotal;
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    woyouService.sendLCDDoubleString("Total", "Total:" + finalSubtotal2, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void call_MNChnage() {
        String change;
        try {
            change = Globals.myNumberFormat2Price(Double.parseDouble(edt_change.getText().toString()), decimal_check);
        } catch (Exception ex) {
            change = Globals.myNumberFormat2Price(0, decimal_check);
        }

        final String finalChange = change;
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    woyouService.sendLCDDoubleString("Change", finalChange, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void call_MNTander() {
        String tender;
        try {
            tender = Globals.myNumberFormat2Price(Double.parseDouble(edt_tender.getText().toString()), decimal_check);
        } catch (Exception ex) {
            tender = Globals.myNumberFormat2Price(0, decimal_check);
        }

        final String finalTender = tender;
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    woyouService.sendLCDDoubleString("Tender", finalTender, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void open_cash_drawer() {
        try {
            woyouService.openDrawer(callback1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            byte[] aa = new byte[4];
            aa[0] = 0x10;
            aa[1] = 0x14;
            aa[2] = 0x00;
            aa[3] = 0x00;

            try {
                woyouService.sendRAWData(aa, callback1);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create_customer_dialog(String strContact_Code) {
        final String str = "13245";
        final Dialog listDialog2 = new Dialog(PaymentActivity.this);
        listDialog2.setTitle(R.string.Contact);
        LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.payment_customer_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_name = (EditText) listDialog2.findViewById(R.id.edt_name);
        final EditText edt_mobile_dialog = (EditText) listDialog2.findViewById(R.id.edt_mobile_dialog);
        final EditText edt_email = (EditText) listDialog2.findViewById(R.id.edt_email);
        Button btn_save = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) listDialog2.findViewById(R.id.btn_cancel);
        listDialog2.show();
        Window window = listDialog2.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        try {
            Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strContact_Code + "'");
            if (contact == null) {
                edt_name.setText("");
                edt_mobile_dialog.setText(edt_mobile.getText().toString().trim());
                edt_email.setText("");
            } else {
                edt_name.setText(contact.get_name());
                edt_mobile_dialog.setText(contact.get_contact_1());
                edt_email.setText(contact.get_email_1());
            }

        } catch (Exception ex) {

        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCTCode = "";
                String chk_mobile = edt_mobile_dialog.getText().toString().trim();
                if (chk_mobile.equals("")) {
                    edt_mobile_dialog.setError(getString(R.string.Mobile_is_required));
                    return;
                }

                if (edt_email.getText().toString().trim().equals("")) {

                } else {
                    if (!isValidEmail(edt_email.getText().toString().trim())) {
                        edt_email.setError(getString(R.string.Enter_valid_Email_ID));
                        edt_email.requestFocus();
                        return;
                    }
                }

                if (Globals.strContact_Code.equals("")) {
                    Contact objCT1 = Contact.getContact(getApplicationContext(), database, db, "  order By contact_id Desc LIMIT 1");

                    if (objCT1 == null) {
                        strCTCode = "CT-" + 1;
                    } else {
                        strCTCode = "CT-" + (Integer.parseInt(objCT1.get_contact_id()) + 1);
                    }

                    pDialog = new ProgressDialog(PaymentActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    final String finalStrCTCode = strCTCode;
                    Thread timerThread = new Thread() {
                        public void run() {

                            try {
                                sleep(1000);

                                final String result = Fill_Contact(edt_name.getText().toString().trim(), edt_mobile_dialog.getText().toString().trim(), edt_email.getText().toString().trim(), finalStrCTCode, "");

                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + result + "'");
                                        if (contact == null) {
                                            edt_mobile.setText("");
                                            txt_show_info.setText("");
                                        } else {
                                            Globals.strContact_Code = result;
                                            edt_mobile.setText(contact.get_contact_1());
                                            txt_show_info.setText(contact.get_name());

                                        }
                                        listDialog2.dismiss();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                } else {

                    pDialog = new ProgressDialog(PaymentActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    final String finalStrCTCode = strCTCode;
                    Thread timerThread = new Thread() {
                        public void run() {

                            try {
                                sleep(1000);

                                String flagContact = "1";
                                final String result1 = Fill_Contact(edt_name.getText().toString().trim(), edt_mobile_dialog.getText().toString().trim(), edt_email.getText().toString().trim(), finalStrCTCode, flagContact);
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + result1 + "'");
                                        if (contact == null) {
                                            edt_mobile.setText("");
                                            txt_show_info.setText("");
                                        } else {
                                            Globals.strContact_Code = result1;
                                            edt_mobile.setText(contact.get_contact_1());
                                            txt_show_info.setText(contact.get_name());

                                        }
                                        listDialog2.dismiss();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();

//
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listDialog2.dismiss();
            }

        });

    }

    private String Fill_Contact(String name, String mobile, String email, String strCTCode, String s) {
        Contact contact = null;
        try {


            if (s.equals("1")) {
                contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strContact_Code + "'");
                contact.set_name(name);
                contact.set_contact_1(mobile);
                contact.set_email_1(email);

                long l = contact.updateContact("contact_code=?", new String[]{Globals.strContact_Code}, database);
                if (l > 0) {

                }

            } else {
                contact = new Contact(getApplicationContext(), null, Globals.Device_Code, strCTCode, "",
                        name, "", "", "", "", mobile, "", email, "", "1", modified_by, "N", "", date, "0", "", "", "");
                database.beginTransaction();
                long l1 = contact.insertContact(database);
                if (l1 > 0) {
                    Address address_class = new Address(getApplicationContext(), null, Globals.Device_Code, strCTCode, "AC-1",
                            "0", "", "0", "0", "0", "0", "0", "1", modified_by, date, "N");
                    long a = address_class.insertAddress(database);
                    if (a > 0) {
                        Address_Lookup address_lookup = new Address_Lookup(getApplicationContext(), null, Globals.Device_Code, strCTCode, "1",
                                strCTCode, "N");
                        long b = address_lookup.insertAddress_Lookup(database);

                    }

                    Contact_Bussiness_Group contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), strCTCode, "BGC-1");
                    long ab = contact_bussiness_group.insertContact_Bussiness_Group(database);
                    if (ab > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {
                        database.endTransaction();
                    }
                } else {
                    database.endTransaction();
                    //pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        } catch (Exception ex) {

        }


        return contact.get_contact_code();

    }

    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String from;
        try {
            from = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            from = "";
        }
        edt_date.setText(from);
    }


    private void call_cmdChangeA() {
        try {
            jsonObject = new JSONObject();
            String change;
            change = Globals.myNumberFormat2Price(Double.parseDouble(edt_change.getText().toString()), decimal_check);

            jsonObject.put("title", "Change");
            jsonObject.put("content", Globals.objLPD.getCurreny_Symbol() + " " + change);
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void call_cmdTanderA() {
        try {
            String tender;
            tender = Globals.myNumberFormat2Price(Double.parseDouble(edt_tender.getText().toString()), decimal_check);
            jsonObject = new JSONObject();
            jsonObject.put("title", "Tender");
            jsonObject.put("content", Globals.objLPD.getCurreny_Symbol() + " " + tender);
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void call_cmdTotalA() {

        try {
            String subtotal;
            subtotal = Globals.myNumberFormat2Price(Double.parseDouble(edt_net_amount.getText().toString()), decimal_check);
            jsonObject = new JSONObject();
            jsonObject.put("title", "Total");
            jsonObject.put("content", Globals.objLPD.getCurreny_Symbol() + " " + subtotal);
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            mDSKernel.sendData(dsPacket);
        } catch (final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void call_cmdChange() {

        try {
            String change;
            change = Globals.myNumberFormat2Price(Double.parseDouble(edt_change.getText().toString()), decimal_check);
            JSONObject json = new JSONObject();
            json.put("title", "Change");
            json.put("content", Globals.objLPD.getCurreny_Symbol() + " " + change);
            String titleContentJsonStr = json.toString();
//                        mDSKernel.sendFiles(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images, callback);

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
            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void call_cmdTander() {

        try {
            String tender;
            tender = Globals.myNumberFormat2Price(Double.parseDouble(edt_tender.getText().toString()), decimal_check);
            JSONObject json = new JSONObject();
            json.put("title", "Tender");
            json.put("content", Globals.objLPD.getCurreny_Symbol() + " " + tender);
            String titleContentJsonStr = json.toString();
//                        mDSKernel.sendFiles(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images, callback);

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
            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void call_cmdTotal() {

        try {
            JSONObject json = new JSONObject();
            json.put("title", "Total");
            json.put("content", Globals.objLPD.getCurreny_Symbol() + " " + Globals.myNumberFormat2Price(Double.parseDouble(edt_net_amount.getText().toString()), decimal_check));
            String titleContentJsonStr = json.toString();
//                        mDSKernel.sendFiles(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images, callback);

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
            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private void CheckOutOrder() {
        try {
            Globals.SRNO = 1;
            String strFlag = "0";
            String strOrdeeStatus = "CLOSE";
            Double netAmountStr = 0.0;
            database.beginTransaction();
            if (opr.equals("Add") || opr.equals("Resv")) {
                try {
                    netAmountStr = Double.parseDouble(edt_net_amount.getText().toString());
                } catch (Exception ex) {
                }

                Double taxStr = 0.0;
                try {
                    taxStr = Double.parseDouble(edt_tax.getText().toString());
                } catch (Exception ex) {
                }

                Double tenderAmountStr = 0.0;
                try {
                    tenderAmountStr = Double.parseDouble(edt_tender.getText().toString());
                } catch (Exception ex) {
                }

                if (tenderAmountStr == 0) {
                    tenderAmountStr = netAmountStr;
                }
                if (tenderAmountStr >= netAmountStr) {

                    Double changeStr = tenderAmountStr - netAmountStr;

                    // Order Number Generate
                    Order_Detail objOrderDetail;
                    Orders objOrder;
                    Order_Payment objOrderPayment = null;

                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                    date = format.format(d);

                    Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");
                    Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);

                    if (last_code == null) {
                        if (objOrder1 == null) {
                            strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
                        } else {
                            strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                        }
                    } else {
                        if (last_code.getlast_order_code().equals("0")) {

                            if (objOrder1 == null) {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
                            } else {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                            }
                        } else {
                            if (objOrder1 == null) {
                                String code = last_code.getlast_order_code();
                                String[] strCode = code.split("-");
                                part2 = strCode[1];
                                orderId = (Integer.parseInt(part2) + 1) + "";
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(part2) + 1);
                            } else {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                            }
                        }
                    }


                    ArrayList<ShoppingCart> myCart = Globals.cart;

                    String locCode = null;
                    try {
                        locCode = Globals.objLPD.getLocation_Code();
                    } catch (Exception ex) {
                        locCode = "";
                    }

                    String strDis = edt_discount_value.getText().toString();

                    if (strDis.equals("")) {
                        strDis = "0";
                    }

                    objOrder = new Orders(getApplicationContext(), orderId, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                            "0", Globals.TotalItem + "", Globals.TotalQty + "",
                            Globals.TotalItemPrice + "", iTax + "", strDis, edt_net_amount.getText().toString(), tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, edt_description.getText().toString(), Globals.strTable_Code, edt_date.getText().toString());

                    long l = objOrder.insertOrders(database);
                    if (l > 0) {
                        strFlag = "1";
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);
                            objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                            long o = objOrderDetail.insertOrder_Detail(database);
                            if (o > 0) {
                                if (settings.get_Is_Stock_Manager().equals("true")) {
                                    stock_deduct(mCart.get_Item_Code(), mCart.get_Quantity());
                                }
                                strFlag = "1";
                                try {
                                    if (!Globals.strContact_Code.equals("")) {
                                        customer_priceBook = Customer_PriceBook.getCustomer_PriceBook(getApplicationContext(), " where item_code='" + mCart.get_Item_Code() + "' and contact_code='" + Globals.strContact_Code + "'", database);
                                        if (customer_priceBook == null) {
                                            customer_priceBook = new Customer_PriceBook(getApplicationContext(), null, Globals.strContact_Code, mCart.get_Item_Code(), mCart.get_Sales_Price(), date);
                                            customer_priceBook.insertCustomer_PriceBook(database);
                                        } else {
                                            customer_priceBook.set_sale_price(mCart.get_Sales_Price());
                                            long itmlc = customer_priceBook.updateCustomer_PriceBook("item_code=? and contact_code=?", new String[]{mCart.get_Item_Code(), Globals.strContact_Code}, database);
                                        }
                                    }
                                } catch (Exception ex) {
                                }
                            } else {
                            }
                        }

                        ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                        Order_Tax objOrderTax;
                        for (int count = 0; count < orderTaxArray.size(); count++) {
                            OrderTaxArray Otary = orderTaxArray.get(count);
                            objOrderTax = new Order_Tax(getApplicationContext(), null, strOrderNo, Otary.get_tax_id()
                                    , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

                            long o = objOrderTax.insertOrder_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
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
                                strFlag = "1";
                            } else {
                            }
                        }

                        if (PayId.equals("2")) {
                            if (settings.getPrinterId().equals("5")) {
                                objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                        PayId, "", "", Globals.Param1, Globals.Param2, strBankCode, "");
                            } else {
                                objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                        PayId, "", "", Globals.Param1, Globals.Param2, strBankCode, "");
                            }
                        } else {
                            objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                    PayId, "", "", edt_cheque.getText().toString().trim(), "", strBankCode, "");
                        }
                        long op = objOrderPayment.insertOrder_Payment(database);
                        if (op > 0) {
                            strFlag = "1";
                        } else {
                        }
                        if (chk_cus_debit.isChecked()) {
                            if (changeStr > 0) {
                                Acc_Customer_Debit acc_customer_debit = new Acc_Customer_Debit(getApplicationContext(), null, strOrderNo, changeStr + "", "0", "S");
                                long ad = acc_customer_debit.insertAcc_Customer_Debit(database);
                                if (ad > 0) {
                                    strFlag = "1";
                                } else {}
                                Double strOldBalance = 0d;
                                Double strAmount = changeStr;
                                Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + Globals.strContact_Code + "'", database);
                                if (acc_customer == null) {
                                    strAmount = strOldBalance + strAmount;
                                    acc_customer = new Acc_Customer(getApplicationContext(), null, Globals.strContact_Code, strAmount + "");
                                    acc_customer.insertAcc_Customer(database);
                                } else {
                                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                    strAmount = strOldBalance + strAmount;
                                    acc_customer.set_amount(strAmount + "");
                                    long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{Globals.strContact_Code}, database);
                                }
                            }
                        }

                        if (!Globals.strContact_Code.equals("")) {
                            Double strOldBalance = 0d;
                            String Amount = "";
                            Double strAmount = 0d;
                            String strQry2 = "Select SUM(pay_amount) from order_payment Where order_code ='" + strOrderNo + "' and payment_id='5'";
                            try {
                                Cursor cursor = database.rawQuery(strQry2, null);
                                while (cursor.moveToNext()) {
                                    Amount = cursor.getString(0);
                                }
                            } catch (Exception ex) {
                                String ab = ex.getMessage();
                                ab = ab;
                            }
                            try {
                                strAmount = Double.parseDouble(Amount + "");
                            } catch (Exception ex) {
                            }
                            if (strAmount > 0) {
                                Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + Globals.strContact_Code + "'", database);
                                if (acc_customer == null) {
                                    strAmount = strOldBalance - strAmount;
                                    acc_customer = new Acc_Customer(getApplicationContext(), null, Globals.strContact_Code, strAmount + "");
                                    acc_customer.insertAcc_Customer(database);
                                } else {
                                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                    strAmount = strOldBalance - strAmount;
                                    acc_customer.set_amount(strAmount + "");
                                    long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{Globals.strContact_Code}, database);
                                }
                            }
                        }

//                        if (!Globals.strContact_Code.equals("")) {
//                            try {
//                                Pro_Loyalty_Setup pro_loyalty_setup = Pro_Loyalty_Setup.getPro_Loyalty_Setup(getApplicationContext(), database, db, "WHERE loyalty_type = 'POINTSYSTEM'");
//                                if (pro_loyalty_setup != null) {
//                                    if (netAmountStr >= Double.parseDouble(pro_loyalty_setup.get_min_purchase_value())) {
//                                        Double Net = Double.parseDouble(netAmountStr + "");
//                                        Double BaseValue = Double.parseDouble(pro_loyalty_setup.get_base_value());
//                                        Double earnPoint = Net / BaseValue;
//                                        int earnPointFinal = Integer.parseInt(Math.round(earnPoint) + "");
//                                        Double earnValue = earnPointFinal * Double.parseDouble(pro_loyalty_setup.get_earn_value());
//                                        Order_Loyalty_Earn order_loyalty_earn = new Order_Loyalty_Earn(getApplicationContext(), null, strOrderNo, Globals.strContact_Code, earnPointFinal + "", earnValue + "", "0");
//                                        long i = order_loyalty_earn.insertOrder_Loyalty_Earn(database);
//                                    }
//                                }
//                            } catch (Exception e) {
//                            }
//                        }

                    } else {
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Payment_Error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                try {
                    netAmountStr = Double.parseDouble(edt_net_amount.getText().toString());
                } catch (Exception ex) {
                }
                Double taxStr = 0.0;
                try {
                    taxStr = Double.parseDouble(edt_tax.getText().toString());
                } catch (Exception ex) {
                }
                Double tenderAmountStr = 0.0;
                try {
                    tenderAmountStr = Double.parseDouble(edt_tender.getText().toString());
                } catch (Exception ex) {
                }
                if (tenderAmountStr == 0) {
                    tenderAmountStr = netAmountStr;
                }
                if (tenderAmountStr >= netAmountStr) {
                    Double changeStr = tenderAmountStr - netAmountStr;
                    // Order Number Generate
                    Order_Detail objOrderDetail;
                    Orders objOrder;
                    Order_Payment objOrderPayment;
                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    date = format.format(d);
                    strOrderNo = strOrderCode;
                    ArrayList<ShoppingCart> myCart = Globals.cart;
                    String locCode = null;
                    try {
                        locCode = Globals.objLPD.getLocation_Code();
                    } catch (Exception ex) {
                        locCode = "";
                    }
                    String strDis = edt_discount_value.getText().toString();
                    if (strDis.equals("")) {
                        strDis = "0";
                    }
                    objOrder = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderNo + "'");
                    objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, objOrder.get_order_date(), Globals.strContact_Code,
                            "0", Globals.TotalItem + "", Globals.TotalQty + "",
                            edt_total.getText().toString().trim() + "", iTax + "", strDis, edt_net_amount.getText().toString(), tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, edt_description.getText().toString(), Globals.strTable_Code, edt_date.getText().toString());
                    long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderNo, objOrder.get_order_id()}, database);
                    if (l > 0) {
                        strFlag = "1";
                        long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderNo}, database);
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);
                            objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                            long o = objOrderDetail.insertOrder_Detail(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }
                        long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderNo}, database);
                        ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                        Order_Tax objOrderTax;
                        for (int count = 0; count < orderTaxArray.size(); count++) {
                            OrderTaxArray Otary = orderTaxArray.get(count);
                            objOrderTax = new Order_Tax(getApplicationContext(), null, strOrderNo, Otary.get_tax_id()
                                    , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());
                            long o = objOrderTax.insertOrder_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }
                        long e7 = Order_Detail_Tax.delete_Order_Detail_Tax(getApplicationContext(), "order_detail_tax", " order_code =? ", new String[]{strOrderNo}, database);
                        ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                        Order_Detail_Tax objOrderDetailTax;
                        for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                            Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                            objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                                    , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());

                            long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }
                        long e5 = Order_Payment.delete_order_payment(getApplicationContext(), "order_payment", " order_code =? ", new String[]{strOrderNo}, database);
                        objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                "1", "", "", "", "", "", "");
                        long op = objOrderPayment.insertOrder_Payment(database);
                        if (op > 0) {
                            strFlag = "1";
                        } else {
                        }
                        if (chk_cus_debit.isChecked()) {
                            if (changeStr > 0) {
                                long e6 = Acc_Customer_Debit.delete_Acc_Customer_Debit(getApplicationContext(), "acc_customer_dedit", " order_code =? ", new String[]{strOrderNo}, database);
                                Acc_Customer_Debit acc_customer_debit = new Acc_Customer_Debit(getApplicationContext(), null, strOrderNo, changeStr + "", "0", "S");
                                long ad = acc_customer_debit.insertAcc_Customer_Debit(database);
                                if (ad > 0) {
                                    strFlag = "1";
                                } else {
                                }
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Payment_Error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (settings == null) {
                PrinterType = "";
            } else {
                try {
                    PrinterType = settings.getPrinterId();
                } catch (Exception ex) {
                    PrinterType = "";
                }
            }
            if (strFlag.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                performPDFExport();
                try {
                    displayTilte = lite_pos_registration.getCompany_Name();
                    call_customer_disply_title(displayTilte);
                } catch (Exception ex) {
                }
//                progressDialog.dismiss();
                if (Globals.strContact_Code.equals("")) {
                    call_remaining_code();
                } else {
                    if (settings.get_Is_File_Share().equals("true")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Dialog listDialog2 = new Dialog(PaymentActivity.this);
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
                                        call_remaining_code();
                                    }
                                });
                                btnButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Globals.strContact_Code.equals("")) {
                                            call_remaining_code();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    startWhatsApp();
                                                }
                                            });
                                        }
                                        listDialog2.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        call_remaining_code();
                    }
                }
            } else {
                progressDialog.dismiss();
                database.endTransaction();
            }
        } finally {
        }
    }

    private void stock_deduct(String item_code, String curQty) {
        try {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
            if (item_location == null) {
            } else {
                Double reming_qty = Double.parseDouble(item_location.get_quantity()) - Double.parseDouble(curQty);
                item_location.set_quantity(reming_qty + "");
                long itmlc = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
            }
        } catch (Exception ex) {
        }
    }

    private void call_remaining_code() {
        opr = "";
        strOrderCode = "";
        Globals.CheckContact = "0";
        Globals.setEmpty();
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
            }
        });
        String contactCode = Globals.strContact_Code;
        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + contactCode + "'");
        if (contact == null) {
        } else {
            if (contact.get_email_1().equals("")) {
            } else {
                if (settings.get_Is_email().equals("true")) {
                    String strEmail = contact.get_email_1();
                    send_email(strEmail);
                }
            }
        }
        try {
            String printer_id = settings.getPrinterId();
            if (isNetworkStatusAvialable(getApplicationContext())) {
                String ck_projct_type = "";
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                try {
                    ck_projct_type = lite_pos_registration.getproject_id();
                } catch (Exception e) {
                    ck_projct_type = "";
                }
                if (ck_projct_type.equals("cloud") && settings.get_IsOnline().equals("true")) {
                    String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'");
                    progressDialog.dismiss();
                    if (result_order.equals("1")) {
                        if (settings.get_Is_KOT_Print().equals("true")) {
                            if (PrinterType.equals("1")) {
                                try {
                                    if (woyouService == null) {
                                    } else {
                                        print_kot(strOrderNo);
                                    }
                                } catch (Exception ex) {
                                }
                            } else if (PrinterType.equals("4")) {
                                print_kot_phapos(strOrderNo);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        String strTableQry = "";
                                        Cursor cursor1;
                                        if (settings.get_Is_Accounts().equals("true")) {
                                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                    //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                                } else {
                                                    Double showAmount = 0d;
                                                    //                                  String curAmount = "";
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
                                                    } catch (Exception ex) {}
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                    } else {}
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
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
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
                                                }
                                            } else {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
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
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    } else {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                }
                                            }
                                        }
                                        Globals.strOldCrAmt = "0";
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";
                                        Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if (settings.get_Home_Layout().equals("2")){
                                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                });

                            }
                        }
                        if (settings.get_Is_Print_Invoice().equals("true")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
//                                    if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                        printDirect.PrintWithoutDialog(PaymentActivity.this,strOrderNo,"","","");
//                                    } else {
                                    Intent launchIntent = new Intent(PaymentActivity.this, PrintLayout.class);
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.putExtra("strOrderNo", strOrderNo);
                                    PaymentActivity.this.startActivity(launchIntent);
//                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";

                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    } else {
                        if (settings.get_Is_KOT_Print().equals("true")) {
                            if (PrinterType.equals("1")) {
                                try {
                                    if (woyouService == null) {
                                    } else {
                                        print_kot(strOrderNo);
                                    }
                                } catch (Exception ex) {
                                }
                            } else if (PrinterType.equals("4")) {
                                print_kot_phapos(strOrderNo);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String strTableQry = "";
                                        Cursor cursor1;
                                        if (settings.get_Is_Accounts().equals("true")) {
                                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                } else {
                                                    Double showAmount = 0d;
                                                    //                                  String curAmount = "";
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
                                                    } else {
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

                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);

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

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();


                                                if (Globals.strContact_Code.equals("")) {
                                                    //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    } else {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                                }
                                            }
                                        }
                                        Globals.strOldCrAmt = "0";
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";
                                        Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if (settings.get_Home_Layout().equals("2")){
                                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                            }
                        }
                        if (settings.get_Is_Print_Invoice().equals("true")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                    if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                        printDirect.PrintWithoutDialog(PaymentActivity.this,strOrderNo,"","","");
//                                    } else {
                                    Intent launchIntent = new Intent(PaymentActivity.this, PrintLayout.class);
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.putExtra("strOrderNo", strOrderNo);
                                    PaymentActivity.this.startActivity(launchIntent);
//                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    }
                } else {
                    progressDialog.dismiss();
                    if (settings.get_Is_KOT_Print().equals("true")) {
                        if (PrinterType.equals("1")) {
                            try {
                                if (woyouService == null) {
                                } else {
                                    print_kot(strOrderNo);
                                }
                            } catch (Exception ex) {
                            }
                        } else if (PrinterType.equals("4")) {
                            print_kot_phapos(strOrderNo);
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        }
                    }
                    if (settings.get_Is_Print_Invoice().equals("true")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent launchIntent = new Intent(PaymentActivity.this, PrintLayout.class);
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchIntent.putExtra("strOrderNo", strOrderNo);
                                PaymentActivity.this.startActivity(launchIntent);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }

                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                if (settings.get_Home_Layout().equals("0")) {

                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            } else {
                progressDialog.dismiss();
                if (settings.get_Is_KOT_Print().equals("true")) {
                    if (PrinterType.equals("1")) {
                        try {
                            if (woyouService == null) {
                            } else {
                                print_kot(strOrderNo);
                            }
                        } catch (Exception ex) {
                        }
                    } else if (PrinterType.equals("4")) {
                        print_kot_phapos(strOrderNo);
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    }
                }
                if (settings.get_Is_Print_Invoice().equals("true")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                printDirect.PrintWithoutDialog(PaymentActivity.this,strOrderNo,"","","");
//                            } else {
                            Intent launchIntent = new Intent(PaymentActivity.this, PrintLayout.class);
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchIntent.putExtra("strOrderNo", strOrderNo);
                            PaymentActivity.this.startActivity(launchIntent);
//                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

//
                            String strTableQry = "";
                            Cursor cursor1;
                            if (settings.get_Is_Accounts().equals("true")) {
                                order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (ck_project_type.equals("standalone")) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                    } else {
                                        Double showAmount = 0d;
                                        //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                        }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                        double abs1 = Math.abs(showAmount);
                                        if (showAmount > 0) {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        } else {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                        //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                    }
                                } else {

                                    JSONObject jsonObject = new JSONObject();


                                    if (Globals.strContact_Code.equals("")) {
                                        //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        } else {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                        //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    }
                                }
                            }
                            Globals.strOldCrAmt = "0";
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (settings.get_Home_Layout().equals("2")){
                                Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }


        } catch (Exception e) {

            if (isNetworkStatusAvialable(getApplicationContext())) {
                String ck_projct_type = "";
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                try {
                    ck_projct_type = lite_pos_registration.getproject_id();
                } catch (Exception ex) {
                    ck_projct_type = "";
                }
                if (ck_projct_type.equals("cloud") && settings.get_IsOnline().equals("true")) {
                    String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'");

                    if (result_order.equals("1")) {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {


                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                } else {


                    String strTableQry = "";
                    Cursor cursor1;
                    if (settings.get_Is_Accounts().equals("true")) {
                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                        if (ck_project_type.equals("standalone")) {
                            JSONObject jsonObject = new JSONObject();
                            if (Globals.strContact_Code.equals("")) {
                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                            } else {
                                Double showAmount = 0d;
                                //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                double abs1 = Math.abs(showAmount);
                                if (showAmount > 0) {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                } else {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            }
                        } else {

                            JSONObject jsonObject = new JSONObject();


                            if (Globals.strContact_Code.equals("")) {
                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                } else {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
                                if (strCur.equals("")) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            }
                        }
                    }
                    Globals.strOldCrAmt = "0";
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    if (settings.get_Home_Layout().equals("0")) {
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (settings.get_Home_Layout().equals("2")){
                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {


                String strTableQry = "";
                Cursor cursor1;
                if (settings.get_Is_Accounts().equals("true")) {
                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                    if (ck_project_type.equals("standalone")) {
                        JSONObject jsonObject = new JSONObject();
                        if (Globals.strContact_Code.equals("")) {
                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                        } else {
                            Double showAmount = 0d;
                            //                                  String curAmount = "";
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
//                                        		curAmount = "0";
                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                            double abs1 = Math.abs(showAmount);
                            if (showAmount > 0) {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            } else {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            } catch (Exception ex) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                        }
                    } else {

                        JSONObject jsonObject = new JSONObject();


                        if (Globals.strContact_Code.equals("")) {
                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
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
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            } else {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
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
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            } catch (Exception ex) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            if (strCur.equals("")) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
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

                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        }
                    }
                }
                Globals.strOldCrAmt = "0";
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (settings.get_Home_Layout().equals("2")){
                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void print_kot(final String strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                    String Print_type = "0";
                    woyouService.setAlignment(1, callback1);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback1);
                    woyouService.setFontSize(30, callback1);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback1);
                    }

                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback1);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback1);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback1);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback1);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback1);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X" + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback1);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void print_kot_phapos(final String strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                    String Print_type = "0";
                    woyouService.setAlignment(1, callback1);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback1);
                    woyouService.setFontSize(30, callback1);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback1);
                    }
                    woyouService.setFontSize(35, callback1);
                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback1);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + orders.get_order_code() + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback1);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback1);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback1);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback1);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X " + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback1);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);

                    woyouService.cutPaper(callback1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void call_customer_disply_title(String displayTilte) {

        try {
            jsonObject = new JSONObject();
            jsonObject.put("title", "Welcome to " + displayTilte);
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void send_email(String strEmail) {
        try {
            String[] recipients = strEmail.split(",");
            final SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;

            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code = '" + Globals.strContact_Code + "'");
            email.m = new GMailSender(settings.get_Email(), settings.get_Password(), settings.get_Host(), settings.get_Port());
            email.m.set_from(settings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Customer Copy of your Order no : " + strOrderNo + " ");
            email.m.set_to(recipients);
            email.m.set_subject("Confirmation of your Order " + strOrderNo + " Mail");
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "PDF Report" + "/" + strOrderNo + ".pdf");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void performPDFExport() {
        objOrder = Orders.getOrders(getApplicationContext(), database, "WHERE order_code = '" + strOrderNo + "'");
        final ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), "WHERE order_code = '" + strOrderNo + "'", database);

        int count = 0;
        while (count < order_detail.size()) {

            String item_code = order_detail.get(count).get_item_code();
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
            list1a.add(item.get_item_name());
            list2a.add(order_detail.get(count).get_quantity());
            list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check));
            list4a.add(Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check));

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
                    + "/" + objOrder.get_order_code() + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
//            image = createimage();

            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.Order), B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);

            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph(getString(R.string.CompanyName) + " : " + Globals.objLPR.getCompany_Name(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph(getString(R.string.POS_No) + " : " + Globals.objLPD.getDevice_Code(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_no = new PdfPTable(1);
            PdfPCell cell_order_no = new PdfPCell(new Paragraph(getString(R.string.Order_No) + " : " + objOrder.get_order_code(), B12));
            cell_order_no.setColspan(1);
            cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_no.setPadding(5.0f);
            table_order_no.addCell(cell_order_no);
            table_order_no.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph(getString(R.string.Order_Date) + objOrder.get_order_date(), B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph(getString(R.string.Cashier) + " : " + user.get_name(), B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);


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
            document.open();


            PdfPTable table_subtotal = new PdfPTable(2);

            Phrase pr23 = new Phrase(getString(R.string.SubTotal), B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c16);
            pr23 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c17);
            table_subtotal.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_total_tax = new PdfPTable(2);

            Phrase pr234 = new Phrase(getString(R.string.Total_Tax), B10);
            PdfPCell c161 = new PdfPCell(pr234);
            c161.setPadding(5);
            c161.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total_tax.addCell(c161);
            pr234 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check), B10);

            PdfPCell c173 = new PdfPCell(pr234);
            c173.setPadding(5);
            c173.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total_tax.addCell(c173);
//            table_total_tax.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_discount = new PdfPTable(2);

            Phrase pr24 = new Phrase(getString(R.string.Discount), B10);
            PdfPCell c11 = new PdfPCell(pr24);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_discount.addCell(c11);
            pr24 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check), B10);

            PdfPCell c19 = new PdfPCell(pr24);
            c19.setPadding(5);
            c19.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_discount.addCell(c19);
            table_discount.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_net_amount = new PdfPTable(2);

            Phrase pr248 = new Phrase(getString(R.string.NetAmount), B10);
            PdfPCell c171 = new PdfPCell(pr248);
            c171.setPadding(5);
            c171.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_net_amount.addCell(c171);
            pr248 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check), B10);

            PdfPCell c169 = new PdfPCell(pr248);
            c169.setPadding(5);
            c169.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_net_amount.addCell(c169);
//            table_net_amount.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_tender = new PdfPTable(2);

            Phrase pr249 = new Phrase(getString(R.string.Tender), B10);
            PdfPCell c178 = new PdfPCell(pr249);
            c178.setPadding(5);
            c178.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_tender.addCell(c178);
            pr249 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check), B10);

            PdfPCell c167 = new PdfPCell(pr249);
            c167.setPadding(5);
            c167.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_tender.addCell(c167);
//            table_tender.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_change = new PdfPTable(2);

            Phrase pr242 = new Phrase(getString(R.string.Change), B10);
            PdfPCell c158 = new PdfPCell(pr242);
            c158.setPadding(5);
            c158.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_change.addCell(c158);
            pr242 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check), B10);

            PdfPCell c187 = new PdfPCell(pr242);
            c187.setPadding(5);
            c187.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_change.addCell(c187);
//            table_change.setSpacingBefore(10.0f);
            document.open();


            Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

            document.open();


            PdfPTable table_address = new PdfPTable(2);

            Phrase pr287 = new Phrase(getString(R.string.Address), B10);
            PdfPCell c188 = new PdfPCell(pr287);
            c188.setPadding(5);
            c188.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_address.addCell(c188);
            pr287 = new Phrase("" + lite_pos_registration.getAddress(), B10);

            PdfPCell c197 = new PdfPCell(pr287);
            c197.setPadding(5);
            c197.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_address.addCell(c197);
            table_address.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_mobile = new PdfPTable(2);
            Phrase pr217 = new Phrase(getString(R.string.Mobile), B10);
            PdfPCell c218 = new PdfPCell(pr217);
            c218.setPadding(5);
            c218.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_mobile.addCell(c218);
            pr217 = new Phrase("" + lite_pos_registration.getMobile_No(), B10);

            PdfPCell c210 = new PdfPCell(pr217);
            c210.setPadding(5);
            c210.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_mobile.addCell(c210);
            document.open();

            document.add(tableh);
            document.add(table_company_name);
            document.add(table_posno);
            document.add(table_order_no);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table);
            document.add(table_subtotal);
            document.add(table_total_tax);
            document.add(table_discount);
            document.add(table_net_amount);
            document.add(table_tender);
            document.add(table_change);
            document.add(table_address);
            document.add(table_mobile);

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

        } catch (Exception e) {
            f.delete();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        PaymentActivity activity;

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


//    private Image createimage() throws BadElementException {
//        Bitmap bit = null ;
//        Image image = null;
//        try
//        {
//            bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_shack_footer);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bit.compress(Bitmap.CompressFormat.PNG, 30, stream);
//            byte[] imageInByte = stream.toByteArray();
//            image = Image.getInstance(imageInByte);
//            image.scaleAbsolute(600f,50f);//image width,height
//        }
//        catch(Exception ex) { return null; }
//        return image;
//
//    }


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

    private void showQRCode(long fileId) {

        try {
            String json = UPacketFactory.createJson(sunmi.ds.data.DataModel.QRCODE, "");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
//        showing an image by sending this command
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        pDialog = new ProgressDialog(PaymentActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("opr", opr);
                        intent.putExtra("order_code", strOrderCode);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("opr", opr);
                        intent.putExtra("order_code", strOrderCode);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {}
                } else {
                    try {
                        Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("opr", opr);
                        intent.putExtra("order_code", strOrderCode);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {}
                }
            }
        };
        timerThread.start();
    }

    public void fill_spinner_pay_method(String s) {
        if (Globals.strContact_Code.equals("")) {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'  and payment_id!=3");
        } else {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'");
        }

        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList);
        spn_pay_method.setAdapter(paymentListAdapter);

        if (!s.equals("")) {
            for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                String iname = paymentArrayList.get(i).get_payment_name();
                if (s.equals(iname)) {
                    spn_pay_method.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner_bank(String s) {
        bankArrayList = Bank.getAllBank(getApplicationContext(), " WHERE is_active ='1' Order By bank_name asc");
        BankListAdapter bankListAdapter = new BankListAdapter(getApplicationContext(), bankArrayList);
        spn_bank.setAdapter(bankListAdapter);
        if (!s.equals("")) {
            for (int i = 0; i < bankListAdapter.getCount(); i++) {
                String iname = bankArrayList.get(i).get_bank_name();
                if (s.equals(iname)) {
                    spn_bank.setSelection(1);
                    break;
                }
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

    private void startWhatsApp() {
        String strContct = "";
        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");
        if (contact == null) {
        } else {
            strContct = contact.get_contact_1();
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" + objOrder.get_order_code() + ".pdf");
        if (contactExists(getApplicationContext(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
//                finish();
            }

        } else {

            if (SaveContact()) {
                Toast.makeText(getBaseContext(), "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file) {
        Uri path = Uri.fromFile(file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
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
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        call_remaining_code();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(PaymentActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.checkout1));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_tender.getText().toString().trim().equals("")) {
                    edt_tender.setText("0");
                }
                if (chk_cus_debit.isChecked()) {
                    if (Globals.strContact_Code.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please select customer for debit", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(edt_tender.getText().toString().trim()) > Double.parseDouble(edt_net_amount.getText().toString().trim()) && !PayId.equals("1")) {

                        Toast.makeText(getApplicationContext(), "Tender should not grater then net amount with payment mode", Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();
                        new Thread() {
                            public void run() {
                                call_MN();
                                if (settings.get_Is_Cash_Drawer().equals("true")) {
                                    open_cash_drawer();
                                }

                                CheckOutOrder();
                            }
                        }.start();
                    }

                } else {
                    if (Double.parseDouble(edt_tender.getText().toString().trim()) > Double.parseDouble(edt_net_amount.getText().toString().trim()) && !PayId.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Tender should not grater then net amount with payment mode", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();
                        new Thread() {
                            public void run() {
                                call_MN();
                                if (settings.get_Is_Cash_Drawer().equals("true")) {
                                    open_cash_drawer();
                                }
                                CheckOutOrder();
                            }
                        }.start();
                    }
                }
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

        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetCustomerCreditDetail() {
        try {
            String succ_manu = "0";

            String serverData = GetCusCrDetailFromServer();
            if (serverData.equals("") || serverData == null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
            } else {
                try {

                    final JSONObject jsonObject_manufacture = new JSONObject(serverData);
                    final String strStatus = jsonObject_manufacture.getString("status");
                    if (strStatus.equals("true")) {
                        JSONArray jsonArray = jsonObject_manufacture.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            final String strCRAmount = jsonObject.getString("amount");
                            try {
                                Globals.strOldCrAmt = Globals.myNumberFormat2Price(Double.parseDouble(strCRAmount), decimal_check);

                            } catch (Exception ex) {
                            }

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(getApplicationContext(), "No credit found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "No credit found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception ex) {
        }
    }

    private String GetCusCrDetailFromServer() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/accounts");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("contact_code", Globals.strContact_Code));
        nameValuePairs.add(new BasicNameValuePair("type", "S"));
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

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }

    private void call_MN() {
        try {
            woyouService.sendLCDDoubleString("Welcome to", Globals.objLPR.getCompany_Name(), null);
        } catch (Exception ex) {}
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
//            LOGD("time cost：" + timeTools.getProcessTime());
        }

        @Override
        public void onError(int errorCode, String detail) {
            // TODO 打印出错
            // print error
//            LOGD("print error" + " errorcode = " + errorCode + " detail = " + detail);
            if (errorCode == PrinterBinder.PRINTER_ERROR_NO_PAPER) {
                Toast.makeText(PaymentActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(PaymentActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(PaymentActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void parformpayment() {
        btn_charge.performClick();
    }
}
