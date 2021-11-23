package org.phomellolitepos;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.phomellolitepos.Adapter.BankListAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
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
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Tax_Master;

import org.phomellolitepos.database.VoidShoppingCart;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.printer.WifiPrintDriver;

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

import static org.phomellolitepos.Util.Globals.StringSplit;


public class PaymentActivity extends AppCompatActivity {
    EditText edt_mobile,edt_mobilecode, edt_net_amount, edt_tender, edt_change, edt_tax, edt_total, edt_discount, edt_discount_value,
            edt_cheque, edt_description, edt_date;
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
    String path1;
    int scale;
    TableLayout tabdeldate;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog progressDialog;
    String part2;
    String orderId = null;
    String countryCode;
    Calendar myCalendar;
    //Customer display variables
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String displayTilte;
    ProgressDialog pDialog;
    Sys_Tax_Group sys_tax_group;
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
    String  contactnumber="",contactname,contact_name;
    String serial_no, android_id, myKey, device_id,imei_no;
    String liccustomerid;
    Lite_POS_Device liteposdevice;
    String strDiscountPer;
    ArrayList<String> ipAdd = new ArrayList<String>();
    ArrayList<Item_Group> itemgroup_catArrayList;
    ArrayList<ShoppingCart> shoppingCartArrayList;
    Item item;
    String ip = null;
Order_Type order_type;
    private ArrayList<String> mylist = new ArrayList<String>();
    private ArrayList<String> catId = new ArrayList<String>();
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
                Globals.AppLogWrite("Payment Second screen data"+ secondScreenData);

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
            Globals.AppLogWrite("Woyoservice disconnection");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            woyouService = IWoyouService.Stub.asInterface(service);
            Globals.AppLogWrite("Woyoservice connection");
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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
//        String amt = "", scale = "5", place = "3";
        String amt = "";
        //Denomination(amt, scale, place);
          getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        settings = Settings.getSettings(getApplicationContext(), database, "");
/*        if (settings.getPrinterId().equals("7")) {
            try {
                ServiceManager.getInstence().init(getApplicationContext());
            } catch (Exception ex) {
            }
        }*/
        if (settings == null) {
            PrinterType = "";
        } else {
            try {
                PrinterType = settings.getPrinterId();
            } catch (Exception ex) {
                PrinterType = "";
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
            Intent intent_1 = new Intent();
            intent_1.setPackage("woyou.aidlservice.jiuiv5");
            intent_1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
            startService(intent_1);
            bindService(intent_1, connService, Context.BIND_AUTO_CREATE);
        }catch(Exception e){}
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

                        GetCusCrDetailFromServer(serial_no,android_id,myKey);
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
                        if (Globals.objLPR.getIndustry_Type().equals("2")) {
                            Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } else {
                            if (settings.get_Home_Layout().equals("0")) {
                                try {
                                    // Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("opr", opr);
                                    intent.putExtra("order_code", strOrderCode);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (settings.get_Home_Layout().equals("2")) {
                                try {
                                    //   Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("opr", opr);
                                    intent.putExtra("order_code", strOrderCode);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    // Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("opr", opr);
                                    intent.putExtra("order_code", strOrderCode);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
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
       // edt_mobilecode = (EditText) findViewById(R.id.edt_mobile_code);
        btn_get_cus = (Button) findViewById(R.id.btn_get_cus);
        txt_show_info = (TextView) findViewById(R.id.txt_show_info);
        chk_cus_debit = (CheckBox) findViewById(R.id.chk_cus_debit);
        tabdeldate=(TableLayout)findViewById(R.id.tab_deldate);
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
       // device_id = telephonyManager.getDeviceId();
      //  imei_no=telephonyManager.getImei();
        try {
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
        }
        catch(Exception e){

        }
       // String countrycode= Globals.objLPR.getCountry_Id();
        if(Globals.objLPR.getCountry_Id().equals("99")) {
            countryCode="91";
           // edt_mobilecode.setText(countryCode);
        }
        if(Globals.objLPR.getCountry_Id().equals("114")) {
            countryCode="965";
           // edt_mobilecode.setText(countryCode);
        }
        if(Globals.objLPR.getCountry_Id().equals("221")) {
            countryCode="971";
           // edt_mobilecode.setText(countryCode);
        }



        edt_mobile.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {


                    try {
                        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_1 = '" + edt_mobile.getText().toString() + "'");
                        if(contact.get_contact_code().equals(Globals.strContact_Code)) {
                            if (contact.get_contact_1().equals(edt_mobile.getText().toString())) {
                                Globals.strContact_Code = contact.get_contact_code();
                                txt_show_info.setText(contact.get_name());
                            }
                        }
                        else if(!contact.get_contact_code().equals(Globals.strContact_Code)){
                            if(contact.getIs_taxable().equals("1")){
                                if(contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){
                                    Globals.strContact_Code = contact.get_contact_code();
                                    txt_show_info.setText(contact.get_name());
                                  //  return true;
                                }
                                else{
                                    Globals.localstrContact_Code = contact.get_contact_code();
                                    txt_show_info.setText(contact.get_name());
                                    Toast.makeText(getApplicationContext(),"To Create Order with this contact clear cart add contact from main screen to add Tax  ",Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Globals.strContact_Code = contact.get_contact_code();
                                txt_show_info.setText(contact.get_name());
                              //  return true;
                            }
                        }
                        else {
                            txt_show_info.setText("");
                        }
                    }
                    catch(Exception e){

                    }
                    return true;
                }

                return false;
            }
        });
        btn_get_cus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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
                    strDiscountPer= edt_discount.getText().toString();

                    edt_discount.requestFocus();

                } else {
                    btn_charge.requestFocus();
                }
            }
        });
        edt_discount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

try {
    strDiscountPer = edt_discount.getText().toString();

    //Globals.DiscountPer = Double.parseDouble(strDiscountPer);
}
catch(Exception e){

}
            }
        });

        edt_tender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (edt_discount.getText().toString().length() > 0) {
                    strDiscountPer= edt_discount.getText().toString();

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

                    strDiscountPer= edt_discount.getText().toString();

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
                        strDiscountPer= edt_discount.getText().toString();
                        edt_tender.requestFocus();
                    }
//                    if (edt_discount.getText().toString().equals("0")) {
//                        String net_amount;
//                        Double netAmt = 0d;
//                        if (Globals.strIsDenominationPrint.equals("true")) {
//                            netAmt = Globals.Denomination(Double.parseDouble(total + ""), scale, decimal_check);
//                        } else {
//                            netAmt = Double.parseDouble(temp + "");
//                        }
//                        //Double netAmt = Globals.Denomination(Double.parseDouble(total + ""), scale, decimal_check);
//                        net_amount = Globals.myNumberFormat2Price(netAmt, decimal_check);
//                        edt_net_amount.setText(net_amount);
//                        edt_discount_value.setText("0");
//                        strDiscountPer= edt_discount.getText().toString();
//                        edt_discount.setText("");
//                        edt_tender.requestFocus();
//                    }
                    else {
                        if(edt_discount.getText().toString().equals("")||edt_discount.getText().toString().equals("."))
                        {
                            edt_discount.requestFocus();
                            edt_discount.setError("");
                            return false ;
                        }
                        User user = User.getUser(getApplicationContext(), "WHERE is_active='1' And user_code='" + Globals.user + "'", database);
                        String max_discount;
                        max_discount = user.get_max_discount();
                        String strDiscount = edt_discount.getText().toString();
                        String strDisValue = strDiscount;
                        boolean bFlag = false;
                        Globals.DiscountPer = Double.parseDouble(edt_discount.getText().toString());
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
                            strDiscountPer= strDisValue;

                            Globals.DiscountPer = Double.parseDouble(strDiscountPer);
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
                                    Double lineTotal = 0d;
                                    item=Item.getItem(getApplicationContext(),"Where item_code='" + myCart.get(count).get_Item_Code() +"'",database,db);

                                     lineTotal = Double.parseDouble(myCart.get(count).get_Sales_Price())-Double.parseDouble(myCart.get(count).get_Tax_Price());

                                    showDiscount = ((lineTotal) * Double.parseDouble(strDisValue) / 100);
                                    priceAfDis = lineTotal - showDiscount;
                                    myCart.get(count).set_Discount(priceAfDis + "");
                                    totalDisnt = totalDisnt + (lineTotal - priceAfDis) * Double.parseDouble(myCart.get(count).get_Quantity());
                                    ArrayList<Item_Group_Tax> item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + myCart.get(count).get_Item_Code() + "'", database, db);
                                    Double iTax = 0d;
                                    Double iTaxTotal = 0d;

                                    if (Globals.Taxwith_state.equals("1")) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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


                                                iTaxTotal = iTaxTotal + iTax;


                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", myCart.get(count).get_Item_Code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), iTax + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                            }
                                        }

                                        myCart.get(count).set_Tax_Price(iTaxTotal + "");
                                    /*    if(item.get_is_inclusive_tax().equals("1")) {
                                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (priceAfDis)) + "");
                                            final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
                                        }
                                        else{*/
                                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * ((priceAfDis) +Double.parseDouble(myCart.get(count).get_Tax_Price())) +""));
                                            final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
                                       // }
                                    }

                                    else if (Globals.Taxdifferent_state.equals("2")) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {
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


                                            iTaxTotal = iTaxTotal + iTax;


                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", myCart.get(count).get_Item_Code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), iTax + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                        }
                                        }

                                        myCart.get(count).set_Tax_Price(iTaxTotal + "");
                                        myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (priceAfDis)) +  +Double.parseDouble(myCart.get(count).get_Tax_Price())+ "");
                                        final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
                                    }
                                    else{

                                        myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (priceAfDis )) +  +Double.parseDouble(myCart.get(count).get_Tax_Price())+"");
                                        final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
                                    }
                                }

                                String strShowDiscount;
                                strShowDiscount = Globals.myNumberFormat2Price(Double.parseDouble(totalDisnt + ""), decimal_check);
                                edt_discount.setText("");
                                edt_discount_value.setText(strShowDiscount);

                                Globals.cart = myCart;

                                    contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);
                                iTax = 0.0;
                                if (order_type_taxArrayList.size() > 0) {
                                    if (Globals.objLPR.getCountry_Id().equals("99")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                                                }
                                                if(iPrice!=0.0) {
                                                    total = iPrice + iTax;
                                                    temp = total + "";
                                                }
                                                else{
                                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                 //   iPrice = Double.parseDouble(final_netamount + "");
                                                    total = iPrice;
                                                    temp = total + "";
                                                }
                                            }

                                            else if(!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){
                                                for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("3") ) {
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
                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    }
                                                    else{
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        iPrice = Double.parseDouble(final_netamount + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }

                                                }



                                            }
                                        } else {
                                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                            iPrice = Double.parseDouble(final_netamount + "");
                                            total = iPrice;
                                            temp = total + "";
                                        }
                                    }
                                    else if(contact==null) {
                                        for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                                        }
                                        if (iPrice != 0.0) {
                                            total = iPrice + iTax;
                                            temp = total + "";
                                        } else {
                                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                                            total = iPrice;
                                            temp = total + "";
                                        }
                                    }
                                    }

                                    else if(Globals.objLPR.getCountry_Id().equals("114")){
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {

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
                                                    if(iPrice!=0.0) {
                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    }
                                                    else{
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        //   iPrice = Double.parseDouble(final_netamount + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }



                                            } else {
                                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        }
                                        else if(contact==null){


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
                                                    if(iPrice!=0.0) {
                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    }
                                                    else{
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        //   iPrice = Double.parseDouble(final_netamount + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }


                                            }
                                        }

                                    else if(Globals.objLPR.getCountry_Id().equals("221")){
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {

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
                                                if(iPrice!=0.0) {
                                                    total = iPrice + iTax;
                                                    temp = total + "";
                                                }
                                                else{
                                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                    //   iPrice = Double.parseDouble(final_netamount + "");
                                                    total = iPrice;
                                                    temp = total + "";
                                                }



                                            } else {
                                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        }
                                        else if(contact==null){


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
                                                if(iPrice!=0.0) {
                                                    total = iPrice + iTax;
                                                    temp = total + "";
                                                }
                                                else{
                                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                    //   iPrice = Double.parseDouble(final_netamount + "");
                                                    total = iPrice;
                                                    temp = total + "";
                                                }



                                        }
                                    }

                                    }

                                 else {

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

                                strDiscountPer= edt_discount.getText().toString();
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
                            strDiscountPer= edt_discount.getText().toString();
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

                                    contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);
                                iTax = 0.0;
                                if (order_type_taxArrayList.size() > 0) {

                                    if (Globals.objLPR.getCountry_Id().equals("99")) {
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                                                    }
                                                    if (iPrice != 0.0) {
                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    } else {
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }

                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {

                                                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("3")) {
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
                                                    }
                                                    if (iPrice != 0.0) {

                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    } else {
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }
                                                }
                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        } else if (contact == null) {
                                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                                            }
                                            if (iPrice != 0.0) {
                                                total = iPrice + iTax;
                                                temp = total + "";
                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        }
                                    }

                                    else if(Globals.objLPR.getCountry_Id().equals("114")){

                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {

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
                                                    if (iPrice != 0.0) {
                                                        total = iPrice + iTax;
                                                        temp = total + "";
                                                    } else {
                                                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                        total = iPrice;
                                                        temp = total + "";
                                                    }

                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        } else if (contact == null) {
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
                                            if (iPrice != 0.0) {
                                                total = iPrice + iTax;
                                                temp = total + "";
                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        }



                                    }

                                    else if(Globals.objLPR.getCountry_Id().equals("221")){

                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {

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
                                                if (iPrice != 0.0) {
                                                    total = iPrice + iTax;
                                                    temp = total + "";
                                                } else {
                                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                                    total = iPrice;
                                                    temp = total + "";
                                                }

                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        } else if (contact == null) {
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
                                            if (iPrice != 0.0) {
                                                total = iPrice + iTax;
                                                temp = total + "";
                                            } else {
                                                iPrice = Double.parseDouble(final_netamount + "");
                                                total = iPrice;
                                                temp = total + "";
                                            }
                                        }



                                    }
                                }
                               else {
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

                tabdeldate.setVisibility(View.VISIBLE);
                edt_date.setText(orders.get_delivery_date());

                Globals.order_tax_array.clear();
                String subtotal;

                subtotal = Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check);
//                subtotal = Globals.myNumberFormat2Price(Double.parseDouble(orders.get_sub_total()), decimal_check);
                edt_total.setText(subtotal);


//

                    contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);

                if (order_type_taxArrayList.size() > 0) {
                    if(Globals.objLPR.getCountry_Id().equals("99")) {
                        if (contact != null) {
                            if (contact.getIs_taxable().equals("1")) {
                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                                    }
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("3")) {
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
                                    }
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                                }
                            } else {
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                            }
                        } else if (contact == null) {

                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                            }
                        }
                    }
                  else  if(Globals.objLPR.getCountry_Id().equals("114")) {
                        if (contact != null) {
                            if (contact.getIs_taxable().equals("1")) {
                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
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
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
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
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                                }
                            } else {
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                            }
                        } else if (contact == null) {

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
                        }
                    }
                    else  if(Globals.objLPR.getCountry_Id().equals("221")) {
                        if (contact != null) {
                            if (contact.getIs_taxable().equals("1")) {
                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
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
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
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
                                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                                }
                            } else {
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");

                            }
                        } else if (contact == null) {

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
                        }
                    }
                } else {
                    iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
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

                contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

            order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);

            if (order_type_taxArrayList.size() > 0) {



                if (Globals.objLPR.getCountry_Id().equals("114")) {
                    if (contact != null) {

                        if (contact.getIs_taxable().equals("1")) {
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

                                    Globals.Taxwith_state="1";

                            }
                            if(iPrice!=0.0) {
                                total = iPrice + iTax;
                                temp = total + "";
                            }
                            else{
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                total = iPrice;
                                temp = total + "";
                            }
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            total = iPrice;
                            temp = total + "";
                            Globals.NoTax="0";

                        }
                    }
                    else if(contact==null){
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
                             Globals.Taxwith_state="1";

                        }
                        if(iPrice!=0.0) {
                            total = iPrice + iTax;
                            temp = total + "";
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            total = iPrice;
                            temp = total + "";
                        }
                    }
                }
                if (Globals.objLPR.getCountry_Id().equals("221")) {
                    if (contact != null) {

                        if (contact.getIs_taxable().equals("1")) {
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

                                Globals.Taxwith_state="1";

                            }
                            if(iPrice!=0.0) {
                                total = iPrice + iTax;
                                temp = total + "";
                            }
                            else{
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                total = iPrice;
                                temp = total + "";
                            }
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            total = iPrice;
                            temp = total + "";
                            Globals.NoTax="0";

                        }
                    }
                    else if(contact==null){
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
                            Globals.Taxwith_state="1";

                        }
                        if(iPrice!=0.0) {
                            total = iPrice + iTax;
                            temp = total + "";
                        }
                        else{
                            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                            total = iPrice;
                            temp = total + "";
                        }
                    }
                }
                else if(Globals.objLPR.getCountry_Id().equals("99")){
                if(contact!=null) {
                    if (contact.getIs_taxable().equals("1")) {
                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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

Globals.Taxwith_state="1";
                            }
                            if(iPrice!=0.0) {
                                total = iPrice + iTax;
                                temp = total + "";
                            }
                            else{
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                total = iPrice;
                                temp = total + "";
                            }
                        }else if(!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){

                            for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                                if (sys_tax_group.get_tax_master_id().equals("3")) {
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
                                Globals.Taxdifferent_state="2";

                            }
                            if(iPrice!=0.0)
                            {
                            total = iPrice + iTax;
                            temp = total + "";}
                            else{
                                iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                                total = iPrice;
                                temp = total + "";
                            }
                        }
                    }
                    else{
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        total = iPrice;
                        temp = total + "";
                        Globals.NoTax="0";
                    }
                }
                else if(contact==null) {
                    for (int i = 0; i < order_type_taxArrayList.size(); i++) {
                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + order_type_taxArrayList.get(i).get_tax_id() + "'");

                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
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
                            Globals.Taxwith_state="1";
                    }
                }
                    if(iPrice!=0.0) {
                        total = iPrice + iTax;
                        temp = total + "";
                    }
                    else{
                        iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
                        total = iPrice;
                        temp = total + "";
                    }
                }


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
            public boolean onKey(View view, int keyCode, KeyEvent event)
            {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                                                    sleep(3000);
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

                       /* progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();*/
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

                        /*progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();*/
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
                    if(edt_discount_value.getText().toString().length()>0) {
                        Globals.Inv_Discount = Double.parseDouble(edt_discount_value.getText().toString());
                    }
                    else{
                        Globals.Inv_Discount = 0d;
                    }


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
             /*   try {
                    JSONObject json = new JSONObject();


                    String title_value = Globals.myNumberFormat2Price(Double.parseDouble(edt_net_amount.getText().toString()), decimal_check);
                    json.put("title", title_value);
                    json.put("content", "Total : " + Globals.myNumberFormat2Price(Double.parseDouble(edt_tender.getText().toString()), decimal_check));
                    String titleContentJsonStr = json.toString();
                    dsPacket = UPacketFactory.buildShowText(
                            DSKernel.getDSDPackageName(), json.toString(), callback);
                    Globals.AppLogWrite("else json result 1" + json.toString());
                    mDSKernel.sendData(dsPacket);
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
                    Toast.makeText(getApplicationContext(), " Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();

                    //  progressBar.setVisibility(View.GONE);
                }
                try {
                    woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Double.parseDouble(edt_tender.getText().toString()), decimal_check), null);
                } catch (Exception ex) {
                }
*/
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



        if(settings.getIs_deliverydate().equals("true")){
            tabdeldate.setVisibility(View.VISIBLE);
            edt_date.setVisibility(View.VISIBLE);
        }
        else {
            tabdeldate.setVisibility(View.GONE);

            edt_date.setVisibility(View.GONE);
        }
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
                    woyouService.sendLCDDoubleString("Total", finalSubtotal2, null);
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
            Globals.AppLogWrite("inside open drawer"+connService);
            woyouService.openDrawer(callback1);
        } catch (RemoteException e) {
            e.printStackTrace();
            Globals.AppLogWrite("Exception "+ e.getMessage());
        }

        try {
            byte[] aa = new byte[4];
            aa[0] = 0x10;
            aa[1] = 0x14;
            aa[2] = 0x00;
            aa[3] = 0x00;

            try {
                Globals.AppLogWrite("Send Raw Data:"+ aa.toString());
                woyouService.sendRAWData(aa, callback1);
            } catch (RemoteException e1) {
                e1.printStackTrace();
                Globals.AppLogWrite("Exception 1"+ e1.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Globals.AppLogWrite("Exception 3"+ e.getMessage());
        }
    }

    private void create_customer_dialog(String strContact_Code) {
        final String str = Globals.str_userpassword;
        final Dialog listDialog2 = new Dialog(PaymentActivity.this);
        listDialog2.setTitle(R.string.Contact);
        LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.payment_customer_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_name = (EditText) listDialog2.findViewById(R.id.edt_name);
        edt_name.setFocusable(true);
        edt_name.requestFocus();
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_name, InputMethodManager.SHOW_IMPLICIT);*/

        final EditText edt_mobile_dialog = (EditText) listDialog2.findViewById(R.id.edt_mobile_dialog);
        final EditText edt_email = (EditText) listDialog2.findViewById(R.id.edt_email);
        Button btn_save = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) listDialog2.findViewById(R.id.btn_cancel);


        //listDialog2.show();
        Window window = listDialog2.getWindow();
        // listDialog2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        listDialog2.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        listDialog2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        listDialog2.show();
        edt_name.requestFocus();

        try {
            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_1 = '" + edt_mobile.getText().toString() + "'");
            // Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strContact_Code + "'");
            if (contact == null) {
                edt_name.setFocusable(true);
                edt_name.requestFocus();
                edt_name.setText("");
                edt_mobile_dialog.setText(edt_mobile.getText().toString().trim());
                edt_email.setText("");
            } else {
                if (!contact.get_contact_code().equals(Globals.strContact_Code)) {
                    Globals.localstrContact_Code=contact.get_contact_code();
                  listDialog2.dismiss();
                } else {
                    Globals.strContact_Code = contact.get_contact_code();
                    edt_name.setText(contact.get_name());
                    edt_mobile_dialog.setText(contact.get_contact_1());
                    edt_email.setText(contact.get_email_1());
                }
            }

        } catch (Exception ex) {

        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCTCode = "";
                String chk_mobile = edt_mobile_dialog.getText().toString().trim();
                contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_1 = '" + edt_mobile_dialog.getText().toString() + "'");
                if(contact!=null){
                   contactnumber= contact.get_contact_1();
                }
                Contact contact1=Contact.getContact(getApplicationContext(), database, db, "WHERE name = '" + edt_name.getText().toString() + "'");
                if(contact1!=null){
                    contactname=contact1.get_name();
                }
                if (edt_name.getText().toString().trim().equals("")) {
                    edt_name.setError(getString(R.string.Contact_name_is_required));
                    edt_name.requestFocus();
                    return;
                }
                else if(edt_name.getText().toString().equals(contactname)){
                    edt_name.setError(getString(R.string.Contact_name_duplicate));
                    edt_name.requestFocus();
                    return;
                }
                else {
                   contact_name = edt_name.getText().toString();
                }
                if (chk_mobile.equals("")) {
                    edt_mobile_dialog.setError(getString(R.string.Mobile_is_required));
                    return;
                }
                else if(chk_mobile.equals(contactnumber)){
                    edt_mobile_dialog.setError(getString(R.string.Mobile_already_exist));
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
                    Contact objCT1 = Contact.getContact(getApplicationContext(), database, db, " Where contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%'  order By contact_id Desc LIMIT 1");

                    if (objCT1 == null) {
                        strCTCode = Globals.objLPD.getDevice_Symbol() + "-"+"CT-" + 1;
                    } else {
                        strCTCode = Globals.objLPD.getDevice_Symbol() + "-"+"CT-" + (Integer.parseInt(objCT1.get_contact_code().toString().replace(Globals.objLPD.getDevice_Symbol() + "-CT-","")) + 1);
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
                                            edt_mobile.setText(countryCode+contact.get_contact_1());
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
                edt_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            listDialog2.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
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
               // contact.set_email_1(email);
                long l = contact.updateContact("contact_code=?", new String[]{Globals.strContact_Code}, database);
                if (l > 0) {

                }

            } else {
                contact = new Contact(getApplicationContext(), null, Globals.Device_Code, strCTCode, "",
                        name, "", "", "", "", mobile, "", email, "", "1", modified_by, "N", "", date, "0", "", "0", "0","1");
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
            String titleContentJsonStr=jsonObject.toString();
            Globals.AppLogWrite("Tender A"+ jsonObject.toString());
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);


            mDSKernel.sendData(dsPacket);
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
            String titleContentJsonStr=jsonObject.toString();
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            Globals.AppLogWrite("Tender amount Total A "+ jsonObject.toString());
            mDSKernel.sendData(dsPacket);
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
            String titleContentJsonStr = jsonObject.toString();
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            Globals.AppLogWrite("Net amount Total A "+ jsonObject.toString());
            mDSKernel.sendData(dsPacket);
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

            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), json.toString(), callback);
            Globals.AppLogWrite("Change Total "+ json.toString());
            mDSKernel.sendData(dsPacket);
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

            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), json.toString(), callback);

            Globals.AppLogWrite("Tender Total "+ json.toString());
            mDSKernel.sendData(dsPacket);
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
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), json.toString(), callback);
            Globals.AppLogWrite("Payment Screen Json Result"+ json.toString());
//                        mDSKernel.sendFiles(DSKernel.getDSDPackageName(), titleContentJsonStr, Globals.CMD_Images, callback);
            mDSKernel.sendData(dsPacket);
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
            Toast.makeText(context, " Payment Exception"+ ex.getMessage(), Toast.LENGTH_SHORT).show();

            //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            woyouService.sendLCDDoubleString("Total", Globals.objLPD.getCurreny_Symbol() + " " + Globals.myNumberFormat2Price(Double.parseDouble(edt_net_amount.getText().toString()), decimal_check), null);
        } catch (Exception ex) {
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
                    String strDis="";
                   // if(discount_type.equals("P")) {
                        strDis = edt_discount_value.getText().toString();
                        // String strDiscountPer= edt_discount.getText().toString();

                        if (strDis.equals("")) {
                            strDis = "0";
                        }
                  //  }

                    objOrder = new Orders(getApplicationContext(), orderId, liccustomerid, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                            "0", Globals.TotalItem + "", Globals.TotalQty + "",
                            Globals.TotalItemPrice + "", iTax + "", strDis, edt_net_amount.getText().toString(), tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, edt_description.getText().toString(), Globals.strTable_Code, edt_date.getText().toString(),null);

                    long l = objOrder.insertOrders(database);
                    if (l > 0) {
                        strFlag = "1";
                        double finalDis = 0;
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);

                            try {

                                Double cartsales_price=0d;

                                    cartsales_price = Double.parseDouble(mCart.get_Sales_Price()) - Double.parseDouble(mCart.get_Tax_Price());
                                    Double discountedvalue = Double.parseDouble(Globals.DiscountPer+"");
                                    finalDis = cartsales_price * (discountedvalue / 100.0);


                            } catch (Exception e) {
                            }

                            objOrderDetail = new Order_Detail(getApplicationContext(), null, liccustomerid, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", String.valueOf(finalDis), mCart.get_Line_Total(), "0","false",mCart.getUnitId(),mCart.getBeforeTaxPrice());
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
                            objOrderPayment = new Order_Payment(getApplicationContext(), null, liccustomerid, strOrderNo, "1", edt_net_amount.getText().toString(),
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
                        //progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                   //progressDialog.dismiss();
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
                    String strDis="";
                   // if(discount_type.equals("P")) {
                        strDis = edt_discount_value.getText().toString();
                        // String strDiscountPer= edt_discount.getText().toString();

                        if (strDis.equals("")) {
                            strDis = "0";
                        }
                   // }
                   /* else if(discount_type.equals("F")){
                        Double cartsales_price = Double.parseDouble(strDiscountPer);
                        Double discountedvalue = Double.parseDouble(Globals.DiscountPer);
                        strDis= String.valueOf(cartsales_price * (discountedvalue / 100.0));
                    }*/
                    objOrder = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderNo + "'");
                    int total=0;
                    Globals.TotalItem= myCart.size();
                    total+=Globals.TotalItem;
                    objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), liccustomerid ,locCode, Globals.strOrder_type_id, strOrderNo, objOrder.get_order_date(), Globals.strContact_Code,
                            "0", total + "", Globals.TotalQty + "",
                            edt_total.getText().toString().trim() + "", iTax + "", strDis, edt_net_amount.getText().toString(), tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, edt_description.getText().toString(), Globals.strTable_Code, edt_date.getText().toString(),null);
                    long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderNo, objOrder.get_order_id()}, database);
                    if (l > 0) {
                        strFlag = "1";
                        double finalDis=0;
                        long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderNo}, database);
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);

                            Double cartsales_price= Double.parseDouble(mCart.get_Sales_Price())- Double.parseDouble(mCart.get_Tax_Price());
                            Double discountedvalue= Globals.DiscountPer;
                             finalDis = cartsales_price * (discountedvalue / 100.0);


                            objOrderDetail = new Order_Detail(getApplicationContext(), null, liccustomerid, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", String.valueOf(finalDis), mCart.get_Line_Total(), "0",mCart.getKitchenprintflag(),mCart.getUnitId(),mCart.getBeforeTaxPrice());
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
                        objOrderPayment = new Order_Payment(getApplicationContext(), null, liccustomerid, strOrderNo, "1", edt_net_amount.getText().toString(),
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
                      //  progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                   // progressDialog.dismiss();
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

                if(PrinterType.equals("11")){
                    pdfPerform_80mm();
                }
                else {
                    performPDFExport();
                }
                try {
                    if (Globals.objLPR.getShort_companyname().equals("") || Globals.objLPR.getShort_companyname().equals("null") || Globals.objLPR.getShort_companyname().length() == 0 || Globals.objLPR.getShort_companyname().isEmpty()) {
                        displayTilte = Globals.objLPR.getCompany_Name();
                    }
                    else{
                        displayTilte=Globals.objLPR.getShort_companyname();
                    }
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


                               // startWhatsApp();

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
                                            Toast.makeText(getApplicationContext(),"call rem code",Toast.LENGTH_SHORT).show();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                  //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
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
              //  progressDialog.dismiss();
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

        runOnUiThread(new Runnable() {
            public void run() {
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
            }
        });
        String contactCode = Globals.strContact_Code;
        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + contactCode + "'");
        if (contact == null) {
        } else {
            if (contact.get_email_1().equals("")) {
            } else {
                if (Globals.objsettings.get_Is_email().equals("true")) {
                    String strEmail = contact.get_email_1();
                    send_email(strEmail);
                    Globals.AppLogWrite("sender email"+ strEmail);
                }
            }
        }

        if(settings.getIs_KitchenPrint().equals("true")){
            progressDialog.dismiss();
           printKOT();
           /* PrintKOT_BackgroundAsyncTask printorder= new PrintKOT_BackgroundAsyncTask();
            printorder.execute();*/

        }



        try {
            PrinterType = settings.getPrinterId();
            if (isNetworkStatusAvialable(getApplicationContext())) {
                String ck_projct_type = "";
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                try {
                    ck_projct_type = lite_pos_registration.getproject_id();
                } catch (Exception e) {
                    ck_projct_type = "";
                }
                if (ck_projct_type.equals("cloud") && settings.get_IsOnline().equals("true")) {
                   // progressDialog.dismiss();


                   // String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);

                 //   if (result_order.equals("1")) {
                    Globals.AppLogWrite("Printertype"+PrinterType);
                        if (settings.get_Is_KOT_Print().equals("true")) {
                            if (PrinterType.equals("1")) {
                                try {
                                    if (woyouService == null) {
                                    } else {
                                        print_kot(strOrderNo);
                                    }
                                } catch (Exception ex) {
                                }
                            }
                            else if (PrinterType.equals("2")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (Globals.responsemessage.equals("Device Not Found")) {

                                            Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                            lite_pos_device.setStatus("Out");
                                            long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                            if (ct > 0) {

                                                Intent intent_category = new Intent(PaymentActivity.this, LoginActivity.class);
                                                startActivity(intent_category);
                                                finish();
                                            }


                                        }

                                        else{


                                        }
                                    }
                                });
                            }

                            else if (PrinterType.equals("4")) {
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
                                        if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                            Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {

                                            if (settings.get_Home_Layout().equals("0")) {
                                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (settings.get_Home_Layout().equals("2")) {
                                                Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
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
                                    launchIntent.putExtra("posflag","0");
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
                                    Globals.setEmpty();
                                    if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                        Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (settings.get_Home_Layout().equals("2")) {
                                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        }

                   // }

                    //else
                        /*{
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

                    }*/


                    Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                    order.execute();
                } else {
                    //progressDialog.dismiss();
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
                                    Globals.setEmpty();
                                    Toast.makeText(PaymentActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                    if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                        Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (settings.get_Home_Layout().equals("2")) {
                                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
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
                                launchIntent.putExtra("posflag","0");
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
                                Globals.setEmpty();
                                if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (settings.get_Home_Layout().equals("0")) {

                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (settings.get_Home_Layout().equals("2")) {
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                }
            } else {
               // progressDialog.dismiss();
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
                                if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (settings.get_Home_Layout().equals("2")) {
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
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
                            launchIntent.putExtra("posflag","0");
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
                            Globals.setEmpty();
                            if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (settings.get_Home_Layout().equals("2")) {
                                    Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
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
                     Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                    order.execute();

                    //  String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);

                 //   if (result_order.equals("1")) {
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
                                if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (settings.get_Home_Layout().equals("2")) {
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
                            }
                        });

                    //}
                   if (PrinterType.equals("2")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (Globals.responsemessage.equals("Device Not Found")) {

                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {

                                        Intent intent_category = new Intent(PaymentActivity.this, LoginActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }


                                }
                            }
                        });
                    }


                    else {
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
                                if (Globals.objLPR.getIndustry_Type().equals("2")) {
                                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (settings.get_Home_Layout().equals("2")) {
                                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
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
                    if (Globals.objLPR.getIndustry_Type().equals("2")) {
                        Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
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
                if (Globals.objLPR.getIndustry_Type().equals("2")) {
                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (settings.get_Home_Layout().equals("0")) {
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (settings.get_Home_Layout().equals("2")) {
                        Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PaymentActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }
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
            Globals.AppLogWrite("settings email"+ Globals.objsettings.get_Email());
            Globals.AppLogWrite("settings Password"+ Globals.objsettings.get_Password());
            Globals.AppLogWrite("settings host"+ Globals.objsettings.get_Host());
            Globals.AppLogWrite("settings port"+ Globals.objsettings.get_Port());

            email.m = new GMailSender(Globals.objsettings.get_Email(), Globals.objsettings.get_Password(), Globals.objsettings.get_Host(), Globals.objsettings.get_Port());
            email.m.set_from(Globals.objsettings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Customer Copy of your Order no : " + strOrderNo + " ");
            Globals.AppLogWrite("recipients"+ recipients);

            email.m.set_to(recipients);
            email.m.set_subject("Confirmation of your Order " + strOrderNo + " Mail");
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "PDF Report" + "/" + strOrderNo + ".pdf");
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
            String strItemName = Order_Detail.getItemName_L(getApplicationContext(), " WHERE order_detail.item_Code  = '"
                    + item_code + "'  GROUP By order_detail.item_Code");
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
          if(Globals.objsettings.get_Print_Lang().equals("0")) {
              list1a.add(item.get_item_name());
          }
          else if(Globals.objsettings.get_Print_Lang().equals("1")) {
              list1a.add(item.get_item_name());

          }
          else if(Globals.objsettings.get_Print_Lang().equals("2")) {
              list1a.add(item.get_item_name());

          }
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
            if (Globals.objsettings.get_Print_Lang().equals("0")) {
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 30f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
           // writer.setPageEvent(new Watermark());
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


                PdfPTable table_address = new PdfPTable(1);
                PdfPCell cell_adress;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    cell_adress =new PdfPCell(new Paragraph(Globals.objLPR.getAddress(), B10));
                } else {
                    cell_adress =new PdfPCell(new Paragraph(Globals.objLPR.getAddress(), B10));
                }

               // cell_adress.setColspan(1);

                cell_adress.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_address.addCell(cell_adress);

                PdfPTable table_mobile = new PdfPTable(1);
                PdfPCell cell_mobile;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    cell_mobile =new PdfPCell(new Paragraph(Globals.objLPR.getMobile_No(), B10));
                } else {
                    cell_mobile =new PdfPCell(new Paragraph(Globals.objLPR.getMobile_No(), B10));
                }
              //  cell_mobile.setColspan(1);

                cell_mobile.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_mobile.addCell(cell_mobile);

                document.open();

                PdfPTable table_posno = new PdfPTable(4);
                Phrase prposno = new Phrase(getString(R.string.POS_No), B10);
                PdfPCell cell_posno = new PdfPCell(prposno);
                //  cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_posno.setPadding(5.0f);
                table_posno.addCell(cell_posno);
                prposno = new Phrase("" + Globals.objLPD.getDevice_Code(), B10);
                PdfPCell possecondcolumn = new PdfPCell(prposno);
                possecondcolumn.setPadding(5);
                possecondcolumn.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_posno.addCell(possecondcolumn);
               // document.open();

               // PdfPTable table_order_no = new PdfPTable(2);
                Phrase prorderno = new Phrase(getString(R.string.Order_No), B10);
                PdfPCell cell_order_no = new PdfPCell(prorderno);
                // cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_no.setPadding(5.0f);
                table_posno.addCell(cell_order_no);
                prorderno = new Phrase("" + objOrder.get_order_code(), B10);
                PdfPCell ordercol = new PdfPCell(prorderno);
                ordercol.setPadding(5);
                ordercol.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_posno.addCell(ordercol);
                document.open();


                PdfPTable table_order_date = new PdfPTable(4);
                Phrase prorderdate = new Phrase(getString(R.string.Order_Date), B10);
                PdfPCell cell_order_date = new PdfPCell(prorderdate);
                // cell_order_date.setColspan(1);
                cell_order_date.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_order_date.setPadding(5.0f);
                table_order_date.addCell(cell_order_date);
                prorderdate = new Phrase("" + objOrder.get_order_date(), B10);
                PdfPCell ordercoldate = new PdfPCell(prorderdate);
                ordercoldate.setPadding(5);
                ordercoldate.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_date.addCell(ordercoldate);

                document.open();
                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
               // PdfPTable table_cashier = new PdfPTable(2);
                Phrase prordercashier = new Phrase(getString(R.string.Cashier), B10);
                PdfPCell cell_cashier = new PdfPCell(prordercashier);
                // cell_cashier.setColspan(1);
                cell_cashier.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell_cashier.setPadding(5.0f);
                table_order_date.addCell(cell_cashier);
                prordercashier = new Phrase("" + user.get_name(), B10);
                PdfPCell ordercolcashier = new PdfPCell(prordercashier);
                ordercolcashier.setPadding(5);
                ordercolcashier.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_order_date.addCell(ordercolcashier);
                document.open();

                PdfPTable table_customer = new PdfPTable(2);

                if (contact != null) {
                    contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                    Phrase prordercustomer = new Phrase(getString(R.string.customer), B10);

                    PdfPCell cell_customer = new PdfPCell(prordercustomer);
                    //cell_customer.setColspan(1);
                    cell_customer.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell_customer.setPadding(5.0f);
                    table_customer.addCell(cell_customer);
                    prordercustomer = new Phrase("" + contact.get_name(), B10);
                    PdfPCell ordercolcustomer = new PdfPCell(prordercustomer);
                    ordercolcustomer.setPadding(5);
                    ordercolcustomer.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table_customer.addCell(ordercolcustomer);
                    document.open();
                }
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
                // table_discount.setSpacingBefore(10.0f);
                document.open();
                String strCurrency;
                if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                    strCurrency = "";
                } else {
                    strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                }
                PdfPTable table_net_amount = new PdfPTable(2);

                Phrase pr248 = new Phrase(getString(R.string.NetAmount), B12);
                PdfPCell c171 = new PdfPCell(pr248);
                c171.setPadding(5);
                c171.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_net_amount.addCell(c171);
                pr248 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check) + strCurrency, B12);

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



                int iTemp = 0;
                Tax_Master tax_master = null;

                PdfPTable table_ordertaxtitle = new PdfPTable(1);

                Paragraph paraordertax;
                PdfPTable  table_order_tax = new PdfPTable(2);;
                if (Globals.objsettings.get_ItemTax().equals("2") || Globals.objsettings.get_ItemTax().equals("3")) {

//            table_change.setSpacingBefore(10.0f);



                    String strTableQry = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                            "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                            "where order_detail.order_code ='" + strOrderNo + "' group by  order_detail_tax.tax_id";
                    Cursor cursor1 = database.rawQuery(strTableQry, null);


                    Phrase prodtitle= new Phrase("Item Tax", B10);
                    PdfPCell codtitle = new PdfPCell(prodtitle);

                    codtitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_ordertaxtitle.setSpacingBefore(10.0f);
                    table_ordertaxtitle.addCell(codtitle);
                     document.open();
                    while (cursor1.moveToNext()) {
                        iTemp += 1;
                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id='" + cursor1.getString(0) + "'", database, db);


                        Phrase prordertax = new Phrase(tax_master.get_tax_name(), B10);
                        PdfPCell C_ODTAX= new PdfPCell(prordertax);
                        C_ODTAX.setPadding(5);
                        C_ODTAX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_order_tax.addCell(C_ODTAX);
                        prordertax = new Phrase("" +  Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(1)), decimal_check) , B10);

                        PdfPCell cordrtax = new PdfPCell(prordertax);
                        cordrtax.setPadding(5);
                        cordrtax.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.setSpacingBefore(10.0f);
                        table_order_tax.addCell(cordrtax);
                       // document.open();

                    }

                }
                document.open();
                PdfPTable table_orderTtitle = new PdfPTable(1);


                Phrase prodtitletax= new Phrase("Order Tax", B10);
                PdfPCell codtitletax = new PdfPCell(prodtitletax);

                codtitletax.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_orderTtitle.setSpacingBefore(10.0f);
                table_orderTtitle.addCell(codtitletax);
                document.open();
             //   table_order_tax = new PdfPTable(2);
                PdfPTable table_ordertaxDetail = new PdfPTable(2);

                Cursor cursor = Order_Tax.getOrderTaxValue(getApplicationContext(), " Where order_tax.order_code = '" + strOrderNo + "'");
                String name = "", value = "";
                if (cursor.moveToFirst()) {
                    do {
                        name = cursor.getString(0);
                        value = cursor.getString(1);
                     //   strString = Globals.myRequiredString(name, strLength);

                        //woyouService.printTextWithFont(strString + ":" + Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) + "\n", "", 28, callback);
                        Phrase prordertaxDet = new Phrase(name, B10);
                        PdfPCell C_ODTAXDet= new PdfPCell(prordertaxDet);
                        C_ODTAXDet.setPadding(5);
                        C_ODTAXDet.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table_ordertaxDetail.addCell(C_ODTAXDet);
                        prordertaxDet = new Phrase("" +   Globals.myNumberFormat2Price(Double.parseDouble(value), decimal_check) , B10);

                        PdfPCell cordrtaxdet = new PdfPCell(prordertaxDet);
                        cordrtaxdet.setPadding(5);
                        cordrtaxdet.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.setSpacingBefore(10.0f);
                        table_ordertaxDetail.addCell(cordrtaxdet);

                    } while (cursor.moveToNext());
                }
                document.open();
/*
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                //   document.open();


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
                document.open();*/

                document.add(tableh);
                document.add(table_company_name);
                document.add(table_address);
                document.add(table_mobile);
                document.add(table_posno);
                //document.add(table_order_no);
                document.add(table_order_date);
                //document.add(table_cashier);
                document.add(table_customer);
                document.add(table);
                document.add(table_subtotal);
                document.add(table_total_tax);
                document.add(table_discount);
                document.add(table_net_amount);
                document.add(table_tender);
                document.add(table_change);
                document.add(table_ordertaxtitle);
                document.add(table_order_tax);
                document.add(table_orderTtitle);
                document.add(table_ordertaxDetail);

               /* PdfPTable table5 = new PdfPTable(1);
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
                document.add(table4);*/
                document.newPage();
                document.close();
                file.close();
          /*  Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
                    Toast.LENGTH_SHORT).show();*/
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
                OutputStream file = new FileOutputStream(f);
                Document document = new Document(PageSize.A4);
                document.setMargins(-50f, -50f, 10f, 20f);

                PdfWriter writer = PdfWriter.getInstance(document, file);



                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.BOLD);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph("طلب", B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);
                document.open();

                PdfPTable pdfTable=new PdfPTable(2);
                pdfTable.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell pdfPCell=new PdfPCell();

                Paragraph paragraph=new Paragraph("اسم الشركة",N12);
                paragraph.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell.addElement(paragraph);
                pdfTable.addCell(pdfPCell);

                PdfPCell pdfPCell1=new PdfPCell();

                Paragraph paragraph1=new Paragraph(Globals.objLPR.getCompany_Name(),B10E);
                paragraph1.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1.addElement(paragraph1);
                pdfTable.addCell(pdfPCell1);
                pdfTable.setSpacingBefore(10.0f);


                PdfPTable pdfTablepos=new PdfPTable(2);
                pdfTablepos.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellpos=new PdfPCell();

                Paragraph paragraphpos=new Paragraph("رقم POS",N12);
                paragraphpos.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellpos.addElement(paragraphpos);
                pdfTablepos.addCell(pdfPCellpos);


                PdfPCell pdfPCell1pos=new PdfPCell();

                Paragraph paragraph1pos=new Paragraph(Globals.objLPD.getDevice_Code(),B10E);
                paragraph1pos.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1pos.addElement(paragraph1pos);
                pdfTablepos.addCell(pdfPCell1pos);

                PdfPTable pdfTableorderno=new PdfPTable(2);
                pdfTableorderno.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderno=new PdfPCell();

                Paragraph paragraphorderno=new Paragraph("طلب رقم",N12);
                paragraphorderno.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellorderno.addElement(paragraphorderno);
                pdfTableorderno.addCell(pdfPCellorderno);

                PdfPCell pdfPCell1Orderno=new PdfPCell();

                Paragraph paragraph1ordeerno=new Paragraph(objOrder.get_order_code(),B10E);
                paragraph1ordeerno.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderno.addElement(paragraph1ordeerno);
                pdfTableorderno.addCell(pdfPCell1Orderno);

                PdfPTable pdfTableorderdate=new PdfPTable(2);
                pdfTableorderdate.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderdate=new PdfPCell();


                Paragraph paragraphorderdate=new Paragraph("تاريخ الطلب",N12);
                paragraphorderdate.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellorderdate.addElement(paragraphorderdate);
                pdfTableorderdate.addCell(pdfPCellorderdate);


                PdfPCell pdfPCell1Orderdatere=new PdfPCell();

                Paragraph paragraph1ordeerdater=new Paragraph(objOrder.get_order_date(),B10E);
                paragraph1ordeerdater.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderdatere.addElement(paragraph1ordeerdater);
                pdfTableorderdate.addCell(pdfPCell1Orderdatere);


                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                PdfPTable pdfTableordercashier=new PdfPTable(2);
                pdfTableordercashier.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordercashier=new PdfPCell();


                Paragraph paragraphordercashier=new Paragraph("أمين الصندوق",N12);
                paragraphordercashier.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordercashier.addElement(paragraphordercashier);
                pdfTableordercashier.addCell(pdfPCellordercashier);


                PdfPCell pdfPCell1Ordercashier=new PdfPCell();
                Paragraph paragraph1ordeercashier=new Paragraph(user.get_name(),B10E);
                paragraph1ordeercashier.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordercashier.addElement(paragraph1ordeercashier);
                pdfTableordercashier.addCell(pdfPCell1Ordercashier);

                contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                PdfPTable pdfTableordercustomer=new PdfPTable(2);
                if(contact!=null) {
                    pdfTableordercustomer.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    PdfPCell pdfPCellordercustomer = new PdfPCell();
                    Paragraph paragraphordercustomer = new Paragraph("العميل", N12);
                    paragraphordercustomer.setAlignment(PdfPCell.ALIGN_LEFT);
                    pdfPCellordercustomer.addElement(paragraphordercustomer);
                    pdfTableordercustomer.addCell(pdfPCellordercashier);


                    PdfPCell pdfPCell1Ordercustomer = new PdfPCell();
                    Paragraph paragraph1ordeercustomer = new Paragraph(contact.get_name(), B10E);
                    paragraph1ordeercustomer.setAlignment(PdfPCell.ALIGN_LEFT);
                    pdfPCell1Ordercustomer.addElement(paragraph1ordeercustomer);
                    pdfTableordercashier.addCell(pdfPCell1Ordercustomer);
                }


                PdfPTable tableitemms = new PdfPTable(4);
                tableitemms.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                Phrase pr = new Phrase("ااسم المنتج", B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setPadding(5);
                tableitemms.addCell(c1);
                pr = new Phrase("كمية", B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c3);
                pr = new Phrase("سعر", B10);
                PdfPCell c2 = new PdfPCell(pr);
                c2.setPadding(5);
                c2.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c2);
                pr = new Phrase("مجموع", B10);
                c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c3);
                document.open();
                for (int i = 0; i < list1a.size(); i++) {
                    Phrase pr1 = new Phrase(list1a.get(i), B10E);
                    tableitemms.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                    PdfPCell c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableitemms.addCell(c7);

                    pr1 = new Phrase(list2a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableitemms.addCell(c7);

                    pr1 = new Phrase(list3a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableitemms.addCell(c7);

                    String total_amount;
                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    pr1 = new Phrase(total_amount, B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableitemms.addCell(c7);
                }
                tableitemms.setSpacingBefore(10.0f);
                tableitemms.setHeaderRows(1);
document.open();


                PdfPTable pdfTableordersubtotal=new PdfPTable(2);
                pdfTableordersubtotal.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordersubtotal=new PdfPCell();
                Paragraph paragraphordercustomer=new Paragraph("مبلغ إجمالي",N12);
                paragraphordercustomer.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordersubtotal.addElement(paragraphordercustomer);
                pdfTableordersubtotal.addCell(pdfPCellordersubtotal);


                PdfPCell pdfPCell1Ordersubtotal=new PdfPCell();
             Paragraph paragraph1ordeersuubtotal=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check),B10E);
                paragraph1ordeersuubtotal.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordersubtotal.addElement(paragraph1ordeersuubtotal);
                pdfTableordersubtotal.addCell(pdfPCell1Ordersubtotal);
                pdfTableordersubtotal.setSpacingBefore(10.0f);

                PdfPTable pdfTableordertotaltax=new PdfPTable(2);
                pdfTableordertotaltax.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordertotaltax=new PdfPCell();
                Paragraph paragraphordertotaltax=new Paragraph("مجموع الضريبة",N12);
                paragraphordertotaltax.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordertotaltax.addElement(paragraphordertotaltax);
                pdfTableordertotaltax.addCell(pdfPCellordertotaltax);


                PdfPCell pdfPCell1Ordertottax=new PdfPCell();
                Paragraph paragraph1ordeertotaltax=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check),B10E);
                paragraph1ordeertotaltax.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordertottax.addElement(paragraph1ordeertotaltax);
                pdfTableordersubtotal.addCell(pdfPCell1Ordertottax);


                PdfPTable pdfTableorderdiscount=new PdfPTable(2);
                pdfTableorderdiscount.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderdiscount=new PdfPCell();
                Paragraph paragraphorderdiscount=new Paragraph("خصم",N12);
                paragraphorderdiscount.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellorderdiscount.addElement(paragraphordertotaltax);
                pdfTableorderdiscount.addCell(pdfPCellorderdiscount);


                PdfPCell pdfPCell1Orderdiscount=new PdfPCell();
                Paragraph paragraph1ordeerdiscount=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check)
                        ,B10E);
                paragraph1ordeerdiscount.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderdiscount.addElement(paragraph1ordeertotaltax);
                pdfTableorderdiscount.addCell(pdfPCell1Orderdiscount);


                PdfPTable pdfTableordernetamnt=new PdfPTable(2);
                pdfTableordernetamnt.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordernetamnt=new PdfPCell();
                Paragraph paragraphordernetamnt=new Paragraph("صافي المبلغ",N12);
                paragraphordernetamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordernetamnt.addElement(paragraphordernetamnt);
                pdfTableordernetamnt.addCell(pdfPCellordernetamnt);


                PdfPCell pdfPCell1Ordernetamnt=new PdfPCell();
                Paragraph paragraph1ordeernetamnt=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check),B10E);
                paragraph1ordeernetamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordernetamnt.addElement(paragraph1ordeernetamnt);
                pdfTableordernetamnt.addCell(pdfPCell1Ordernetamnt);

                PdfPTable pdfTableordertender=new PdfPTable(2);
                pdfTableordertender.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordertender=new PdfPCell();
                Paragraph paragraphordertender=new Paragraph("المبلاغ المدفوع",N12);
                paragraphordertender.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordertender.addElement(paragraphordertender);
                pdfTableordertender.addCell(pdfPCellordertender);


                PdfPCell pdfPCell1Ordernettender=new PdfPCell();
                Paragraph paragraph1ordeertender=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check),B10E);
                paragraph1ordeertender.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordernettender.addElement(paragraph1ordeertender);
                pdfTableordernetamnt.addCell(pdfPCell1Ordernettender);


                PdfPTable pdfTableorderchangeamnt=new PdfPTable(2);
                pdfTableorderchangeamnt.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderchangeamnt=new PdfPCell();
                Paragraph paragraphorderchangeamnt=new Paragraph("بدل",N12);
                paragraphorderchangeamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellorderchangeamnt.addElement(paragraphorderchangeamnt);
                pdfTableorderchangeamnt.addCell(pdfPCellorderchangeamnt);


                PdfPCell pdfPCell1Orderchangreamnt=new PdfPCell();
                Paragraph paragraph1ordeerchangeamnt=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check),B10E);
                paragraph1ordeerchangeamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderchangreamnt.addElement(paragraph1ordeerchangeamnt);
                pdfTableorderchangeamnt.addCell(pdfPCell1Orderchangreamnt);

                PdfPTable pdfTableorderaddress=new PdfPTable(2);
                pdfTableorderaddress.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderaddress=new PdfPCell();
                Paragraph paragraphorderadress=new Paragraph("عنوان",N12);
                paragraphorderadress.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellorderaddress.addElement(paragraphorderadress);
                pdfTableorderaddress.addCell(pdfPCellorderaddress);


                PdfPCell pdfPCell1Orderadress=new PdfPCell();
                Paragraph paragraph1ordeeradresss=new Paragraph(lite_pos_registration.getAddress(),B10E);
                paragraph1ordeeradresss.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderadress.addElement(paragraph1ordeeradresss);
                pdfTableorderaddress.addCell(pdfPCell1Orderadress);

                PdfPTable pdfTableordermobile=new PdfPTable(2);
                pdfTableordermobile.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordermobile=new PdfPCell();
                Paragraph paragraphordermobile=new Paragraph("جوال",N12);
                paragraphordermobile.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCellordermobile.addElement(paragraphordermobile);
                pdfTableordermobile.addCell(pdfPCellordermobile);


                PdfPCell pdfPCell1Ordermo=new PdfPCell();
                Paragraph paragraph1ordeermobile=new Paragraph(lite_pos_registration.getMobile_No(),B10E);
                paragraph1ordeermobile.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordermo.addElement(paragraph1ordeermobile);
                pdfTableordermobile.addCell(pdfPCell1Ordermo);


                document.add(tableh);
                document.add(pdfTable);

                document.add(pdfTablepos);

                document.add(pdfTableorderno);

                document.add(pdfTableorderdate);
                document.add(pdfTableordercashier);
                document.add(pdfTableordercustomer);
                document.add(tableitemms);
                document.add(pdfTableordersubtotal);
                document.add(pdfTableordertotaltax);
                document.add(pdfTableorderdiscount);
                document.add(pdfTableordernetamnt);
                document.add(pdfTableordertender);
                document.add(pdfTableorderchangeamnt);
                document.add(pdfTableorderaddress);
                document.add(pdfTableordermobile);
              /*  document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_cashier);
                document.add(table_customer);
                document.add(table);
                document.add(table_subtotal);
                document.add(table_total_tax);
                document.add(table_discount);
                document.add(table_net_amount);
                document.add(table_tender);
                document.add(table_change);
                document.add(table_address);
                document.add(table_mobile);
*/
                /*PdfPTable table5 = new PdfPTable(1);
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
                document.add(table4);*/
                document.newPage();
                document.close();
                file.close();
          /*  Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
                    Toast.LENGTH_SHORT).show();*/
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
                OutputStream file = new FileOutputStream(f);
                Document document = new Document(PageSize.A4);
                document.setMargins(-50f, -50f, 10f, 20f);

                PdfWriter writer = PdfWriter.getInstance(document, file);



                Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.BOLD);
                Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph("طلب", B12));
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellh.setPadding(5.0f);
                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);
                document.open();

                PdfPTable pdfTable=new PdfPTable(2);
                pdfTable.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell pdfPCell=new PdfPCell();

                Paragraph paragraph=new Paragraph("اسم الشركة",N12);
                paragraph.add(new Chunk(" Company Name",B10E));
                paragraph.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCell.addElement(paragraph);
                pdfTable.addCell(pdfPCell);

                PdfPCell pdfPCell1=new PdfPCell();

                Paragraph paragraph1=new Paragraph(Globals.objLPR.getCompany_Name(),B10E);
                paragraph1.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1.addElement(paragraph1);
                pdfTable.addCell(pdfPCell1);
                pdfTable.setSpacingBefore(10.0f);


                PdfPTable pdfTablepos=new PdfPTable(2);
                pdfTablepos.setRunDirection(writer.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellpos=new PdfPCell();

                Paragraph paragraphpos=new Paragraph("رقم POS",N12);
                paragraphpos.add(new Chunk("  POS No",B10E));
                paragraphpos.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellpos.addElement(paragraphpos);
                pdfTablepos.addCell(pdfPCellpos);


                PdfPCell pdfPCell1pos=new PdfPCell();

                Paragraph paragraph1pos=new Paragraph(Globals.objLPD.getDevice_Code(),B10E);
                paragraph1pos.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1pos.addElement(paragraph1pos);
                pdfTablepos.addCell(pdfPCell1pos);

                PdfPTable pdfTableorderno=new PdfPTable(2);
                pdfTableorderno.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderno=new PdfPCell();

                Paragraph paragraphorderno=new Paragraph("طلب رقم",N12);
                paragraphorderno.add(new Chunk("   Order No",B10E));
                paragraphorderno.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellorderno.addElement(paragraphorderno);
                pdfTableorderno.addCell(pdfPCellorderno);

                PdfPCell pdfPCell1Orderno=new PdfPCell();

                Paragraph paragraph1ordeerno=new Paragraph(objOrder.get_order_code(),B10E);
                paragraph1ordeerno.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderno.addElement(paragraph1ordeerno);
                pdfTableorderno.addCell(pdfPCell1Orderno);

                PdfPTable pdfTableorderdate=new PdfPTable(2);
                pdfTableorderdate.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderdate=new PdfPCell();


                Paragraph paragraphorderdate=new Paragraph("تاريخ الطلب",N12);
                paragraphorderdate.add(new Chunk("   Order Date",B10E));

                paragraphorderdate.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellorderdate.addElement(paragraphorderdate);
                pdfTableorderdate.addCell(pdfPCellorderdate);


                PdfPCell pdfPCell1Orderdatere=new PdfPCell();

                Paragraph paragraph1ordeerdater=new Paragraph(objOrder.get_order_date(),B10E);
                paragraph1ordeerdater.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderdatere.addElement(paragraph1ordeerdater);
                pdfTableorderdate.addCell(pdfPCell1Orderdatere);


                User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                PdfPTable pdfTableordercashier=new PdfPTable(2);
                pdfTableordercashier.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordercashier=new PdfPCell();


                Paragraph paragraphordercashier=new Paragraph("أمين الصندوق",N12);
                paragraphordercashier.add(new Chunk("   Cashier",B10E));

                paragraphordercashier.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordercashier.addElement(paragraphordercashier);
                pdfTableordercashier.addCell(pdfPCellordercashier);


                PdfPCell pdfPCell1Ordercashier=new PdfPCell();
                Paragraph paragraph1ordeercashier=new Paragraph(user.get_name(),B10E);
                paragraph1ordeercashier.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordercashier.addElement(paragraph1ordeercashier);
                pdfTableordercashier.addCell(pdfPCell1Ordercashier);

                contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                PdfPTable pdfTableordercustomer=new PdfPTable(2);
                if(contact!=null) {
                    pdfTableordercustomer.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    PdfPCell pdfPCellordercustomer = new PdfPCell();
                    Paragraph paragraphordercustomer = new Paragraph("العميل", N12);
                    paragraphordercustomer.add(new Chunk("   Customer",B10E));
                    paragraphordercustomer.setAlignment(PdfPCell.ALIGN_LEFT);
                    pdfPCellordercustomer.addElement(paragraphordercustomer);
                    pdfTableordercustomer.addCell(pdfPCellordercashier);


                    PdfPCell pdfPCell1Ordercustomer = new PdfPCell();
                    Paragraph paragraph1ordeercustomer = new Paragraph(contact.get_name(), B10E);
                    paragraph1ordeercustomer.setAlignment(PdfPCell.ALIGN_LEFT);
                    pdfPCell1Ordercustomer.addElement(paragraph1ordeercustomer);
                    pdfTableordercashier.addCell(pdfPCell1Ordercustomer);
                }


                PdfPTable tableitemms = new PdfPTable(4);
                tableitemms.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                Phrase pr = new Phrase("اسم المنتج", B10);
                PdfPCell c1 = new PdfPCell(pr);
                c1.setPadding(5);
                tableitemms.addCell(c1);
                pr = new Phrase("كمية", B10);
                PdfPCell c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c3);
                pr = new Phrase("سعر", B10);
                PdfPCell c2 = new PdfPCell(pr);
                c2.setPadding(5);
                c2.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c2);
                pr = new Phrase("مجموع", B10);
                c3 = new PdfPCell(pr);
                c3.setPadding(5);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableitemms.addCell(c3);
                document.open();
                for (int i = 0; i < list1a.size(); i++) {
                    Phrase pr1 = new Phrase(list1a.get(i), B10E);
                    tableitemms.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                    PdfPCell c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableitemms.addCell(c7);

                    pr1 = new Phrase(list2a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableitemms.addCell(c7);

                    pr1 = new Phrase(list3a.get(i), B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableitemms.addCell(c7);

                    String total_amount;
                    total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                    pr1 = new Phrase(total_amount, B10E);
                    c7 = new PdfPCell(pr1);
                    c7.setPadding(5);
                    c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableitemms.addCell(c7);
                }
                tableitemms.setSpacingBefore(10.0f);
                tableitemms.setHeaderRows(1);
                document.open();


                PdfPTable pdfTableordersubtotal=new PdfPTable(2);
                pdfTableordersubtotal.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordersubtotal=new PdfPCell();
                Paragraph paragraphordercustomer=new Paragraph("مبلغ إجمالي",N12);
                paragraphordercustomer.add(new Chunk("   Sub Total",B10E));

                paragraphordercustomer.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordersubtotal.addElement(paragraphordercustomer);
                pdfTableordersubtotal.addCell(pdfPCellordersubtotal);


                PdfPCell pdfPCell1Ordersubtotal=new PdfPCell();
                Paragraph paragraph1ordeersuubtotal=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check),B10E);
                paragraph1ordeersuubtotal.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordersubtotal.addElement(paragraph1ordeersuubtotal);
                pdfTableordersubtotal.addCell(pdfPCell1Ordersubtotal);
                pdfTableordersubtotal.setSpacingBefore(10.0f);

                PdfPTable pdfTableordertotaltax=new PdfPTable(2);
                pdfTableordertotaltax.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordertotaltax=new PdfPCell();
                Paragraph paragraphordertotaltax=new Paragraph("مجموع الضريبة",N12);
                paragraphordertotaltax.add(new Chunk("   Total Tax",B10E));
                paragraphordertotaltax.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordertotaltax.addElement(paragraphordertotaltax);
                pdfTableordertotaltax.addCell(pdfPCellordertotaltax);


                PdfPCell pdfPCell1Ordertottax=new PdfPCell();
                Paragraph paragraph1ordeertotaltax=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check),B10E);
                paragraph1ordeertotaltax.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordertottax.addElement(paragraph1ordeertotaltax);
                pdfTableordersubtotal.addCell(pdfPCell1Ordertottax);


                PdfPTable pdfTableorderdiscount=new PdfPTable(2);
                pdfTableorderdiscount.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderdiscount=new PdfPCell();
                Paragraph paragraphorderdiscount=new Paragraph("خصم",N12);
                paragraphorderdiscount.add(new Chunk("    Discount",B10E));

                paragraphorderdiscount.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellorderdiscount.addElement(paragraphordertotaltax);
                pdfTableorderdiscount.addCell(pdfPCellorderdiscount);


                PdfPCell pdfPCell1Orderdiscount=new PdfPCell();
                Paragraph paragraph1ordeerdiscount=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check)
                        ,B10E);
                paragraph1ordeerdiscount.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderdiscount.addElement(paragraph1ordeertotaltax);
                pdfTableorderdiscount.addCell(pdfPCell1Orderdiscount);


                PdfPTable pdfTableordernetamnt=new PdfPTable(2);
                pdfTableordernetamnt.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordernetamnt=new PdfPCell();
                Paragraph paragraphordernetamnt=new Paragraph("صافي المبلغ",N12);
                paragraphordernetamnt.add(new Chunk("/Net Amount",B10E));

                paragraphordernetamnt.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordernetamnt.addElement(paragraphordernetamnt);
                pdfTableordernetamnt.addCell(pdfPCellordernetamnt);


                PdfPCell pdfPCell1Ordernetamnt=new PdfPCell();
                Paragraph paragraph1ordeernetamnt=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check),B10E);
                paragraph1ordeernetamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordernetamnt.addElement(paragraph1ordeernetamnt);
                pdfTableordernetamnt.addCell(pdfPCell1Ordernetamnt);

                PdfPTable pdfTableordertender=new PdfPTable(2);
                pdfTableordertender.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordertender=new PdfPCell();
                Paragraph paragraphordertender=new Paragraph("المبلاغ المدفوع",N12);
                paragraphordertender.add(new Chunk("   Tender",B10E));

                paragraphordertender.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordertender.addElement(paragraphordertender);
                pdfTableordertender.addCell(pdfPCellordertender);


                PdfPCell pdfPCell1Ordernettender=new PdfPCell();
                Paragraph paragraph1ordeertender=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check),B10E);
                paragraph1ordeertender.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordernettender.addElement(paragraph1ordeertender);
                pdfTableordernetamnt.addCell(pdfPCell1Ordernettender);


                PdfPTable pdfTableorderchangeamnt=new PdfPTable(2);
                pdfTableorderchangeamnt.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderchangeamnt=new PdfPCell();
                Paragraph paragraphorderchangeamnt=new Paragraph("بدل",N12);
                paragraphorderchangeamnt.add(new Chunk("   Change",B10E));
                paragraphorderchangeamnt.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellorderchangeamnt.addElement(paragraphorderchangeamnt);
                pdfTableorderchangeamnt.addCell(pdfPCellorderchangeamnt);


                PdfPCell pdfPCell1Orderchangreamnt=new PdfPCell();
                Paragraph paragraph1ordeerchangeamnt=new Paragraph(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check),B10E);
                paragraph1ordeerchangeamnt.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderchangreamnt.addElement(paragraph1ordeerchangeamnt);
                pdfTableorderchangeamnt.addCell(pdfPCell1Orderchangreamnt);

                PdfPTable pdfTableorderaddress=new PdfPTable(2);
                pdfTableorderaddress.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellorderaddress=new PdfPCell();
                Paragraph paragraphorderadress=new Paragraph("جوال",N12);
                paragraphorderadress.add(new Chunk("   Address",B10E));
                paragraphorderadress.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellorderaddress.addElement(paragraphorderadress);
                pdfTableorderaddress.addCell(pdfPCellorderaddress);


                PdfPCell pdfPCell1Orderadress=new PdfPCell();
                Paragraph paragraph1ordeeradresss=new Paragraph(lite_pos_registration.getAddress(),B10E);
                paragraph1ordeeradresss.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Orderadress.addElement(paragraph1ordeeradresss);
                pdfTableorderaddress.addCell(pdfPCell1Orderadress);

                PdfPTable pdfTableordermobile=new PdfPTable(2);
                pdfTableordermobile.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                PdfPCell pdfPCellordermobile=new PdfPCell();
                Paragraph paragraphordermobile=new Paragraph("جوال",N12);
                paragraphordermobile.add(new Chunk("   Mobile",B10E));
                paragraphordermobile.setAlignment(PdfPCell.ALIGN_RIGHT);
                pdfPCellordermobile.addElement(paragraphordermobile);
                pdfTableordermobile.addCell(pdfPCellordermobile);


                PdfPCell pdfPCell1Ordermo=new PdfPCell();
                Paragraph paragraph1ordeermobile=new Paragraph(lite_pos_registration.getMobile_No(),B10E);
                paragraph1ordeermobile.setAlignment(PdfPCell.ALIGN_LEFT);
                pdfPCell1Ordermo.addElement(paragraph1ordeermobile);
                pdfTableordermobile.addCell(pdfPCell1Ordermo);


                document.add(tableh);
                document.add(pdfTable);

                document.add(pdfTablepos);

                document.add(pdfTableorderno);

                document.add(pdfTableorderdate);
                document.add(pdfTableordercashier);
                document.add(pdfTableordercustomer);
                document.add(tableitemms);
                document.add(pdfTableordersubtotal);
                document.add(pdfTableordertotaltax);
                document.add(pdfTableorderdiscount);
                document.add(pdfTableordernetamnt);
                document.add(pdfTableordertender);
                document.add(pdfTableorderchangeamnt);
                document.add(pdfTableorderaddress);
                document.add(pdfTableordermobile);
              /*  document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_cashier);
                document.add(table_customer);
                document.add(table);
                document.add(table_subtotal);
                document.add(table_total_tax);
                document.add(table_discount);
                document.add(table_net_amount);
                document.add(table_tender);
                document.add(table_change);
                document.add(table_address);
                document.add(table_mobile);
*/
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
          /*  Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
                    Toast.LENGTH_SHORT).show();*/
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
            Globals.AppLogWrite("Pdf Exception"+e.getMessage());
        }
    }
  /*  protected void performPDFExport() {
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
            PdfPCell cell_company_name;
            if(Globals.objLPR.getShort_companyname().isEmpty()){
                cell_company_name = new PdfPCell(new Paragraph(getString(R.string.CompanyName) + " : " + Globals.objLPR.getCompany_Name(), B12));

            }
            else{
                cell_company_name = new PdfPCell(new Paragraph(getString(R.string.CompanyName) + " : " + Globals.objLPR.getShort_companyname(), B12));

            }
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
    }*/
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
                if (Globals.objLPR.getIndustry_Type().equals("2")) {

                    Intent intent = new Intent(PaymentActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("opr", opr);
                    intent.putExtra("order_code", strOrderCode);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                } else {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            Globals.SetOrderDiscount(PaymentActivity.this, "0", "", db, database);
                            Intent intent = new Intent(PaymentActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
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
                + "/" + objOrder.get_order_code() + ".pdf");
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
             call_remaining_code();
           }

        }
         else {

            if (SaveContact()) {
                Toast.makeText(getBaseContext(), "Contact Saved in Ur PhoneContacts!", Toast.LENGTH_SHORT).show();
              //  finish();
                call_remaining_code();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file,getApplicationContext(),strContct);
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
        String toNumber = contactnumbr;
            // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.putExtra("jid", toNumber + "@s.whatsapp.net");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
       // context.startActivity(sendIntent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        call_remaining_code();

    }



/*
    private  void pdfPerform_80mm() {

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
                    + "/" + objOrder.get_order_code()+"80mm" + ".pdf");
            OutputStream file = new FileOutputStream(f);
             Rectangle pagesize = new Rectangle(80, 297);
            Document document = new Document(PageSize.B7);
            //  document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
          */
/*  writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));*//*

            Image image = null;
//            image = createimage();

            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);


            // Second parameter is the number of the chapter



            Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10);
            subPara.setAlignment(Element.ALIGN_CENTER);
            Paragraph subPara1 = new Paragraph("Order Invoice", B10);
            subPara1.setAlignment(Element.ALIGN_CENTER);
       */
/* Section subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Payment Receipt"));
       createList(subCatPart);*//*


*/
/*            List list = new List(false, false, 0);
            list.isAlignindent();
            //text2.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
            PdfPTable table = new PdfPTable(1);

            list.add(new ListItem(new Chunk(getString(R.string.POS_No)+ " : " + Globals.objLPD.getDevice_Code())));

            list.add(new ListItem(new Chunk(getString(R.string.Order_No)+ " : " +objOrder.get_order_code())));
            list.add(new ListItem(new Chunk(getString(R.string.Order_Date)+ " : " +objOrder.get_order_date())));
            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

            list.add(new ListItem(new Chunk(getString(R.string.Cashier)+ " : " +user.get_name())));
            contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");
if(contact!=null) {
    list.add(new ListItem(new Chunk(getString(R.string.customer)+ " : " + contact.get_name())));
}*//*


            PdfPTable table = new PdfPTable(4);

            Phrase pr = new Phrase(getString(R.string.ItemName), B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setPadding(3);
            table.addCell(c1);
            pr = new Phrase(getString(R.string.Quantity), B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setBorder(Rectangle.NO_BORDER);
            c3.setPadding(3);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            pr = new Phrase(getString(R.string.Price), B10);
            PdfPCell c2 = new PdfPCell(pr);
            c2.setBorder(Rectangle.NO_BORDER);
            c2.setPadding(3);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c2);
            pr = new Phrase(getString(R.string.Total), B10);
            c3 = new PdfPCell(pr);
            c3.setBorder(Rectangle.NO_BORDER);
            c3.setPadding(3);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            for (int i = 0; i < list1a.size(); i++) {
                Phrase pr1 = new Phrase(list1a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr1);
                c7.setBorder(Rectangle.NO_BORDER);
                c7.setPadding(3);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c7);

                pr1 = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setBorder(Rectangle.NO_BORDER);
                c7.setPadding(3);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr1 = new Phrase(list3a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setBorder(Rectangle.NO_BORDER);
                c7.setPadding(3);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                String total_amount;
                total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                pr1 = new Phrase(total_amount, N9);
                c7 = new PdfPCell(pr1);
                c7.setBorder(Rectangle.NO_BORDER);
                c7.setPadding(3);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);
            }
           // table.setSpacingBefore(10.0f);
            table.setHeaderRows(1);
            document.open();

            List list1 = new List(false, false, 0);
            list1.add(new ListItem(new Chunk(getString(R.string.SubTotal)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.Total_Tax)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.Discount)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.NetAmount)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.Tender)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.Change)+ " : " +Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check))));
            list1.add(new ListItem(new Chunk(getString(R.string.Address)+ " : " +lite_pos_registration.getAddress())));
            list1.add(new ListItem(new Chunk(getString(R.string.Mobile)+ " : " +lite_pos_registration.getMobile_No())));

           */
/* list.add(new ListItem(new Chunk("Date : "+date.substring(0,10))));
            list.add(new ListItem(new Chunk("Time : "+date.substring(11,19))));
            list.add(new ListItem(new Chunk(Globals.PrintDeviceID + ":"+ Globals.objLPD.getDevice_Name())));
            list.add(new ListItem(new Chunk("Payment Mode: "+strPayMethod)));
            list.add(new ListItem(new Chunk("Old Balance: "+Globals.myNumberFormat2Price(Double.parseDouble(txt_total_amt.getText().toString()), decimal_check))));
            list.add(new ListItem(new Chunk("Received: "+Globals.myNumberFormat2Price(Double.parseDouble(edt_pd_amt.getText().toString()), decimal_check))));
            Double ab = Double.parseDouble(txt_total_amt.getText().toString()) - Double.parseDouble(edt_pd_amt.getText().toString());
            double abs1 = Math.abs(ab);
            if (ab > 0) {
                list.add(new ListItem(new Chunk("Current Balance: " + Globals.myNumberFormat2Price(abs1, decimal_check) +" CR")));
            }
            else{
                list.add(new ListItem(new Chunk("Current Balance: " + Globals.myNumberFormat2Price(abs1, decimal_check) +" DR")));
            }

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

            list.add(new ListItem(new Chunk("Signature: "+user.get_name())));*//*

            document.open();
           // document.add(subParaacc);
            document.add(subPara);
            document.add(subPara1);
            document.add( Chunk.NEWLINE );

            document.add(list);
            document.add(table);
            document.add(list1);
            document.newPage();
            document.close();
            file.close();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                } catch (ActivityNotFoundException e) {
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public void pdfPerform_80mm(){


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
                            + "/" + objOrder.get_order_code() + "80mm" + ".pdf");
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
                    Phrase prposno = new Phrase(getString(R.string.POS_No), B10);
                    PdfPCell cell_posno = new PdfPCell(prposno);
                    cell_posno.setBorder(Rectangle.NO_BORDER);

                    //  cell_posno.setColspan(1);
                    cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_posno.addCell(cell_posno);
                    prposno = new Phrase("" + Globals.objLPD.getDevice_Code(), B10);
                    PdfPCell possecondcolumn = new PdfPCell(prposno);
                    possecondcolumn.setBorder(Rectangle.NO_BORDER);

                    possecondcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_posno.addCell(possecondcolumn);
                    document.open();

                    PdfPTable table_order_no = new PdfPTable(2);
                    Phrase prorderno = new Phrase(getString(R.string.Order_No), B10);
                    PdfPCell cell_order_no = new PdfPCell(prorderno);
                    cell_order_no.setBorder(Rectangle.NO_BORDER);

                    // cell_order_no.setColspan(1);
                    cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_order_no.addCell(cell_order_no);
                    prorderno = new Phrase("" + objOrder.get_order_code(), B10);
                    PdfPCell ordercol = new PdfPCell(prorderno);

                    ordercol.setBorder(Rectangle.NO_BORDER);
                    ordercol.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_order_no.addCell(ordercol);
                    document.open();


                    PdfPTable table_order_date = new PdfPTable(2);
                    Phrase prorderdate = new Phrase(getString(R.string.Order_Date), B10);
                    PdfPCell cell_order_date = new PdfPCell(prorderdate);
                    cell_order_date.setBorder(Rectangle.NO_BORDER);

                    // cell_order_date.setColspan(1);
                    cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_order_date.addCell(cell_order_date);
                    prorderdate = new Phrase("" +  DateUtill.PaternDate1(objOrder.get_order_date()).substring(0,11), B10);
                    PdfPCell ordercoldate = new PdfPCell(prorderdate);
                    ordercoldate.setBorder(Rectangle.NO_BORDER);


                    ordercoldate.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_order_date.addCell(ordercoldate);

                    document.open();

                        PdfPTable table_order_time = new PdfPTable(2);
                        Phrase prordertime = new Phrase(getString(R.string.Order_time), B10);
                        PdfPCell cell_order_time = new PdfPCell(prordertime);
                        cell_order_time.setBorder(Rectangle.NO_BORDER);

                        // cell_order_date.setColspan(1);
                        cell_order_time.setHorizontalAlignment(Element.ALIGN_LEFT);

                        table_order_time.addCell(cell_order_time);
                        prordertime = new Phrase("" +  DateUtill.PaternDate1(objOrder.get_order_date()).substring(12,20), B10);
                        PdfPCell ordercoltime = new PdfPCell(prordertime);
                        ordercoltime.setBorder(Rectangle.NO_BORDER);
                        ordercoltime.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_order_time.addCell(ordercoltime);
                        document.open();

                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    PdfPTable table_cashier = new PdfPTable(2);
                    Phrase prordercashier = new Phrase(getString(R.string.Cashier), B10);
                    PdfPCell cell_cashier = new PdfPCell(prordercashier);
                    cell_cashier.setBorder(Rectangle.NO_BORDER);

                    // cell_cashier.setColspan(1);
                    cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_cashier.addCell(cell_cashier);
                    prordercashier = new Phrase("" + user.get_name(), B10);
                    PdfPCell ordercolcashier = new PdfPCell(prordercashier);

                    ordercolcashier.setBorder(Rectangle.NO_BORDER);

                    ordercolcashier.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_cashier.addCell(ordercolcashier);
                    document.open();
                    ;
                    PdfPTable table_customer = new PdfPTable(2);

                    if (contact != null) {
                        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                        Phrase prordercustomer = new Phrase(getString(R.string.customer), B10);

                        PdfPCell cell_customer = new PdfPCell(prordercustomer);
                        cell_customer.setBorder(Rectangle.NO_BORDER);
                        //cell_customer.setColspan(1);
                        cell_customer.setHorizontalAlignment(Element.ALIGN_LEFT);

                        table_customer.addCell(cell_customer);
                        prordercustomer = new Phrase("" + contact.get_name(), B10);
                        PdfPCell ordercolcustomer = new PdfPCell(prordercustomer);

                        ordercolcustomer.setBorder(Rectangle.NO_BORDER);
                        ordercolcustomer.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_customer.addCell(ordercolcustomer);
                        document.open();
                    }
                    PdfPTable table = new PdfPTable(4);

                    Phrase pr = new Phrase(getString(R.string.ItemName), B10);
                    PdfPCell c1 = new PdfPCell(pr);
                    c1.setBorder(Rectangle.NO_BORDER);

                   // c1.setPadding(5);
                    table.addCell(c1);
                    pr = new Phrase(getString(R.string.Quantity), B10);
                    PdfPCell c3 = new PdfPCell(pr);
                    c3.setBorder(Rectangle.NO_BORDER);

                   // c3.setPadding(5);
                    c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c3);
                    pr = new Phrase(getString(R.string.Price), B10);
                    PdfPCell c2 = new PdfPCell(pr);
                   // c2.setPadding(5);
                    c2.setBorder(Rectangle.NO_BORDER);

                    c2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c2);
                    pr = new Phrase(getString(R.string.Total), B10);
                    c3 = new PdfPCell(pr);
                    c3.setBorder(Rectangle.NO_BORDER);

                   // c3.setPadding(5);
                    c3.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c3);
                    for (int i = 0; i < list1a.size(); i++) {
                        Phrase pr1 = new Phrase(list1a.get(i), N9);
                        PdfPCell c7 = new PdfPCell(pr1);
                        //c7.setPadding(5);
                        c7.setBorder(Rectangle.NO_BORDER);

                        c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(c7);

                        pr1 = new Phrase(list2a.get(i), N9);
                        c7 = new PdfPCell(pr1);
                        //c7.setPadding(5);
                        c7.setBorder(Rectangle.NO_BORDER);

                        c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(c7);

                        pr1 = new Phrase(list3a.get(i), N9);
                        c7 = new PdfPCell(pr1);
                        //c7.setPadding(5);
                        c7.setBorder(Rectangle.NO_BORDER);

                        c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(c7);

                        String total_amount;
                        total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                        pr1 = new Phrase(total_amount, N9);
                        c7 = new PdfPCell(pr1);
                        //c7.setPadding(5);
                        c7.setBorder(Rectangle.NO_BORDER);

                        c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(c7);
                    }
                    table.setSpacingBefore(10.0f);
                    table.setHeaderRows(1);
                    document.open();


                    PdfPTable table_subtotal = new PdfPTable(2);

                    Phrase pr23 = new Phrase(getString(R.string.SubTotal), B10);
                    PdfPCell c16 = new PdfPCell(pr23);
                    //c16.setPadding(5);
                    c16.setBorder(Rectangle.NO_BORDER);

                    c16.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_subtotal.addCell(c16);
                    pr23 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check), B10);

                    PdfPCell c17 = new PdfPCell(pr23);
                  //  c17.setPadding(5);
                    c17.setBorder(Rectangle.NO_BORDER);

                    c17.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_subtotal.addCell(c17);
                    table_subtotal.setSpacingBefore(10.0f);
                    document.open();


                    PdfPTable table_total_tax = new PdfPTable(2);

                    Phrase pr234 = new Phrase(getString(R.string.Total_Tax), B10);
                    PdfPCell c161 = new PdfPCell(pr234);
                   // c161.setPadding(5);
                    c161.setBorder(Rectangle.NO_BORDER);

                    c161.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_total_tax.addCell(c161);
                    pr234 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check), B10);

                    PdfPCell c173 = new PdfPCell(pr234);
                  //  c173.setPadding(5);
                    c173.setBorder(Rectangle.NO_BORDER);

                    c173.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_total_tax.addCell(c173);
//            table_total_tax.setSpacingBefore(10.0f);
                    document.open();


                    PdfPTable table_discount = new PdfPTable(2);

                    Phrase pr24 = new Phrase(getString(R.string.Discount), B10);
                    PdfPCell c11 = new PdfPCell(pr24);
                 //   c11.setPadding(5);
                    c11.setBorder(Rectangle.NO_BORDER);

                    c11.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_discount.addCell(c11);
                    pr24 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check), B10);

                    PdfPCell c19 = new PdfPCell(pr24);
                   // c19.setPadding(5);
                    c19.setBorder(Rectangle.NO_BORDER);

                    c19.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_discount.addCell(c19);
                    // table_discount.setSpacingBefore(10.0f);
                    document.open();
                        String strCurrency;
                        if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                            strCurrency = "";
                        } else {
                            strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
                        }
                    PdfPTable table_net_amount = new PdfPTable(2);

                    Phrase pr248 = new Phrase(getString(R.string.NetAmount), B10);
                    PdfPCell c171 = new PdfPCell(pr248);
                  //  c171.setPadding(5);
                    c171.setBorder(Rectangle.NO_BORDER);

                    c171.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_net_amount.addCell(c171);
                    pr248 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check) + strCurrency, B10);

                    PdfPCell c169 = new PdfPCell(pr248);
                    //c169.setPadding(5);
                    c169.setBorder(Rectangle.NO_BORDER);

                    c169.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_net_amount.addCell(c169);
//            table_net_amount.setSpacingBefore(10.0f);
                    document.open();


                    PdfPTable table_tender = new PdfPTable(2);

                    Phrase pr249 = new Phrase(getString(R.string.Tender), B10);
                    PdfPCell c178 = new PdfPCell(pr249);
                    //c178.setPadding(5);
                    c178.setBorder(Rectangle.NO_BORDER);

                    c178.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_tender.addCell(c178);
                    pr249 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check), B10);

                    PdfPCell c167 = new PdfPCell(pr249);
                   // c167.setPadding(5);
                    c167.setBorder(Rectangle.NO_BORDER);

                    c167.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_tender.addCell(c167);
//            table_tender.setSpacingBefore(10.0f);
                    document.open();


                    PdfPTable table_change = new PdfPTable(2);

                    Phrase pr242 = new Phrase(getString(R.string.Change), B10);
                    PdfPCell c158 = new PdfPCell(pr242);
                   // c158.setPadding(5);
                    c158.setBorder(Rectangle.NO_BORDER);

                    c158.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_change.addCell(c158);
                    pr242 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check), B10);

                    PdfPCell c187 = new PdfPCell(pr242);
                   // c187.setPadding(5);
                    c187.setBorder(Rectangle.NO_BORDER);

                    c187.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_change.addCell(c187);
//            table_change.setSpacingBefore(10.0f);
                    document.open();

                        PdfPTable table_payment = new PdfPTable(2);


                        ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                        if (order_payment_array.size() > 0) {
                            for (int i = 0; i < order_payment_array.size(); i++) {
                                Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                                String name = "";
                                if (payment != null) {
                                    name = payment.get_payment_name();
                                    Phrase prpayment = new Phrase(name, B10);
                                    PdfPCell cpayment = new PdfPCell(prpayment);
                                    // c158.setPadding(5);
                                    cpayment.setBorder(Rectangle.NO_BORDER);

                                    cpayment.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    table_payment.addCell(cpayment);

                                 //   mIPosPrinterService.printSpecifiedTypeText("\n"+strString + " : " + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check), "ST", 24, callbackPPT8555);
                                    prpayment = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check), B10);
                                    PdfPCell c1payment= new PdfPCell(prpayment);
                                    // c187.setPadding(5);
                                    c1payment.setBorder(Rectangle.NO_BORDER);

                                    c1payment.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    table_payment.addCell(c1payment);
                                }
                            }
                        }



                        document.open();

                    document.add(tableh);
                    document.add(table_company_name);
                        document.add(table_company_adres);
                        document.add(table_company_mobile);
                    document.add(table_posno);
                    document.add(table_order_no);
                    document.add(table_order_date);
                    document.add(table_order_time);
                    document.add(table_cashier);
                    document.add(table_customer);
                    document.add(table);
                    document.add(table_subtotal);
                    document.add(table_total_tax);
                    document.add(table_discount);
                    document.add(table_net_amount);
                    document.add(table_tender);
                    document.add(table_change);
                    document.add(table_payment);

                    document.newPage();
                    document.close();
                    file.close();
                    }

                    else if(Globals.objsettings.get_Print_Lang().equals("1")){
                        String strString = "";
                        int strLength = 18;
                        Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                        Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                        Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                        Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                        Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                        Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                        document.open();
                        Paragraph subParaacc = new Paragraph("طلب", B12);
                        subParaacc.setAlignment(Element.ALIGN_CENTER);
                        Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10E);
                        subPara.setAlignment(Element.ALIGN_CENTER);
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


                        strString = Globals.myRequiredString(Globals.objLPD.getDevice_Code() , strLength);

                        Phrase p1 = new Phrase(strString + ": ",B10E);
                        p1.add(new Chunk("رقم POS", B10));
                        p1.add(new Chunk(Chunk.NEWLINE));
                        document.add(p1);

                        ColumnText canvas1 = new ColumnText(writer.getDirectContent());
                        canvas1.setSimpleColumn(36, 750, 559, 780);

                        canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas1 .setAlignment(Element.ALIGN_RIGHT);
                        canvas1.addElement(p1);
                        canvas1.addElement(new Chunk(Chunk.NEWLINE));
                        canvas1.go();


                        strString = Globals.myRequiredString(objOrder.get_order_code() , strLength);

                        Phrase p3 = new Phrase(strString + ": ",B10E);
                        p3.add(new Chunk("طلب رقم", B10));
                        p3.add(new Chunk(Chunk.NEWLINE));
                        document.add(p3);

                        ColumnText canvas3 = new ColumnText(writer.getDirectContent());
                        canvas3.setSimpleColumn(36, 750, 559, 780);

                        canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas3.setAlignment(Element.ALIGN_RIGHT);
                        canvas3.addElement(p3);
                        canvas3.addElement(new Chunk(Chunk.NEWLINE));
                        canvas3.go();


                        strString = Globals.myRequiredString(objOrder.get_order_date() , strLength);

                        Phrase p4 = new Phrase(strString + ": ",B10E);
                        p4.add(new Chunk("تاريخ الطلب", B10));
                        p4.add(new Chunk(Chunk.NEWLINE));
                        document.add(p4);

                        ColumnText canvas4 = new ColumnText(writer.getDirectContent());
                        canvas4.setSimpleColumn(36, 750, 559, 780);

                        canvas4.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas4.setAlignment(Element.ALIGN_RIGHT);
                        canvas4.addElement(p4);
                        canvas4.addElement(new Chunk(Chunk.NEWLINE));
                        canvas4.go();

                        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                        strString = Globals.myRequiredString(user.get_name() , strLength);

                        Phrase p5 = new Phrase(strString + ": ",B10E);
                        p5.add(new Chunk("العميل", B10));
                        p5.add(new Chunk(Chunk.NEWLINE));
                        document.add(p5);

                        ColumnText canvas5 = new ColumnText(writer.getDirectContent());
                        canvas5.setSimpleColumn(36, 750, 559, 780);

                        canvas5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas5.setAlignment(Element.ALIGN_RIGHT);
                        canvas5.addElement(p5);
                        canvas5.addElement(new Chunk(Chunk.NEWLINE));
                        canvas5.go();


                        strLength=17;
                        strString = Globals.myRequiredString("اسم المنتج", strLength);

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
                        strString = Globals.myRequiredString("سعر", strLength);
                        prq1.add(new Chunk(strString, B10));
                        prq1.add(new Chunk(" "));
                        strLength=17;
                        strString = Globals.myRequiredString("مجموع", strLength);
                        prq1.add(new Chunk(strString, B10));
                        document.add(prq1);

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

                         //  subtotal
                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check) , strLength);

                        Phrase p6 = new Phrase(strString + ": ",B10E);
                        p6.add(new Chunk("مبلغ إجمالي", B10));
                        p6.add(new Chunk(Chunk.NEWLINE));
                        document.add(p6);

                        ColumnText canvas6 = new ColumnText(writer.getDirectContent());
                        canvas6.setSimpleColumn(36, 750, 559, 780);

                        canvas6.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas6.setAlignment(Element.ALIGN_RIGHT);
                        canvas6.addElement(p6);
                        canvas6.addElement(new Chunk(Chunk.NEWLINE));
                        canvas6.go();


                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check) , strLength);

                        Phrase p7 = new Phrase(strString + ": ",B10E);
                        p7.add(new Chunk("مجموع الضريبة", B10));
                        p7.add(new Chunk(Chunk.NEWLINE));
                        document.add(p7);

                        ColumnText canvas7 = new ColumnText(writer.getDirectContent());
                        canvas7.setSimpleColumn(36, 750, 559, 780);

                        canvas7.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas7.setAlignment(Element.ALIGN_RIGHT);
                        canvas7.addElement(p7);
                        canvas7.addElement(new Chunk(Chunk.NEWLINE));
                        canvas7.go();

                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check) , strLength);

                        Phrase p8 = new Phrase(strString + ": ",B10E);
                        p8.add(new Chunk("خصم", B10));
                        p8.add(new Chunk(Chunk.NEWLINE));
                        document.add(p8);

                        ColumnText canvas8 = new ColumnText(writer.getDirectContent());
                        canvas8.setSimpleColumn(36, 750, 559, 780);

                        canvas8.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas8.setAlignment(Element.ALIGN_RIGHT);
                        canvas8.addElement(p8);
                        canvas8.addElement(new Chunk(Chunk.NEWLINE));
                        canvas8.go();



                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check) , strLength);

                        Phrase p10 = new Phrase(strString + ": ",B10E);
                        p10.add(new Chunk("صافي المبلغ", B10));
                        p10.add(new Chunk(Chunk.NEWLINE));
                        document.add(p10);

                        ColumnText canvas10 = new ColumnText(writer.getDirectContent());
                        canvas10.setSimpleColumn(36, 750, 559, 780);

                        canvas10.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas10.setAlignment(Element.ALIGN_RIGHT);
                        canvas10.addElement(p10);
                        canvas10.addElement(new Chunk(Chunk.NEWLINE));
                        canvas10.go();

                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check) , strLength);

                        Phrase p11 = new Phrase(strString + ": ",B10E);
                        p11.add(new Chunk("المبلاغ المدفوع", B10));
                        p11.add(new Chunk(Chunk.NEWLINE));
                        document.add(p11);

                        ColumnText canvas11 = new ColumnText(writer.getDirectContent());
                        canvas11.setSimpleColumn(36, 750, 559, 780);

                        canvas11.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas11.setAlignment(Element.ALIGN_RIGHT);
                        canvas11.addElement(p11);
                        canvas11.addElement(new Chunk(Chunk.NEWLINE));
                        canvas11.go();


                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check) , strLength);

                        Phrase p13 = new Phrase(strString + ": ",B10E);
                        p13.add(new Chunk("بدل", B10));
                        p13.add(new Chunk(Chunk.NEWLINE));
                        document.add(p13);

                        ColumnText canvas13 = new ColumnText(writer.getDirectContent());
                        canvas13.setSimpleColumn(36, 750, 559, 780);

                        canvas13.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas13.setAlignment(Element.ALIGN_RIGHT);
                        canvas13.addElement(p13);
                        canvas13.addElement(new Chunk(Chunk.NEWLINE));
                        canvas13.go();


                        strString = Globals.myRequiredString(lite_pos_registration.getAddress() , strLength);

                        Phrase p14 = new Phrase(strString + ": ",B10E);
                        p14.add(new Chunk("عنوان", B10));
                        p14.add(new Chunk(Chunk.NEWLINE));
                        document.add(p14);

                        ColumnText canvas14 = new ColumnText(writer.getDirectContent());
                        canvas14.setSimpleColumn(36, 750, 559, 780);

                        canvas14.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas14.setAlignment(Element.ALIGN_RIGHT);
                        canvas14.addElement(p14);
                        canvas14.addElement(new Chunk(Chunk.NEWLINE));
                        canvas14.go();


                        strString = Globals.myRequiredString(lite_pos_registration.getMobile_No() , strLength);

                        Phrase p15 = new Phrase(strString + ": ",B10E);
                        p15.add(new Chunk("جوال", B10));
                        p15.add(new Chunk(Chunk.NEWLINE));
                        document.add(p15);

                        ColumnText canvas15 = new ColumnText(writer.getDirectContent());
                        canvas15.setSimpleColumn(36, 750, 559, 780);

                        canvas15.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        canvas15.setAlignment(Element.ALIGN_RIGHT);
                        canvas15.addElement(p15);
                        canvas15.addElement(new Chunk(Chunk.NEWLINE));
                        canvas15.go();
                        document.newPage();
                        document.close();
                        file.close();
                    }

                    else if(Globals.objsettings.get_Print_Lang().equals("2")){
                        String strString = "";
                        int strLength = 18;
                        Font B12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.BOLD);
                        Font N10 = FontFactory.getFont("assets/fonts/Arabic Regular.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                        Font B10 = FontFactory.getFont("assets/fonts/DroidNaskh-Regular.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                        Font N9 =FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 10, Font.NORMAL);
                        Font N12 = FontFactory.getFont("assets/fonts/DroidNaskh-Bold.ttf", BaseFont.IDENTITY_H, 12, Font.NORMAL);
                        Font B10E = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);

                        document.open();
                        Paragraph subParaacc = new Paragraph("طلب", B12);
                        subParaacc.setAlignment(Element.ALIGN_CENTER);
                        Paragraph subPara = new Paragraph(Globals.objLPR.getCompany_Name(), B10E);
                        subPara.setAlignment(Element.ALIGN_CENTER);


                        document.add(subParaacc);
                        document.add(subPara);
                        document.add(new Chunk(Chunk.NEWLINE));

                        strString = Globals.myRequiredString(Globals.objLPR.getCompany_Name(), strLength);

                        Phrase p = new Phrase("",B10E);

                        p.add(new Chunk("اسم الشركة", B10));
                        p.add(new Chunk("  Company Name :", B10E));
                        p.add(new Chunk(Chunk.NEWLINE));
                        p.add(new Chunk(strString, B10E));
                        document.add(p);
                        document.add(new Chunk(Chunk.NEWLINE));
                        ColumnText canvas = new ColumnText(writer.getDirectContent());

                        canvas.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas.setSimpleColumn(36, 750, 559, 780);

                        canvas.setAlignment(Element.ALIGN_LEFT);
                        canvas.addElement(p);
                        canvas.addElement(new Chunk(Chunk.NEWLINE));
                        canvas.go();


                        strString = Globals.myRequiredString(Globals.objLPD.getDevice_Code() , strLength);

                        Phrase p1 = new Phrase("",B10E);
                        p1.add(new Chunk("الموضع لا", B10));
                        p1.add(new Chunk("   Pos No :", B10E));
                        p1.add(new Chunk(Chunk.NEWLINE));
                        p1.add(new Chunk(strString,B10E));
                        document.add(p1);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas1 = new ColumnText(writer.getDirectContent());
                        canvas1.setSimpleColumn(36, 750, 559, 780);

                        canvas1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas1 .setAlignment(Element.ALIGN_LEFT);
                        canvas1.addElement(p1);
                        canvas1.addElement(new Chunk(Chunk.NEWLINE));
                        canvas1.go();


                        strString = Globals.myRequiredString(objOrder.get_order_code() , strLength);

                        Phrase p3 = new Phrase("",B10E);
                        p3.add(new Chunk("طلب رقم", B10));
                        p3.add(new Chunk("   Order No :", B10E));
                        p3.add(new Chunk(Chunk.NEWLINE));
                        p3.add(new Chunk(strString,B10E));
                        document.add(p3);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas3 = new ColumnText(writer.getDirectContent());
                        canvas3.setSimpleColumn(36, 750, 559, 780);

                        canvas3.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas3.setAlignment(Element.ALIGN_LEFT);
                        canvas3.addElement(p3);
                        canvas3.addElement(new Chunk(Chunk.NEWLINE));
                        canvas3.go();


                        strString = Globals.myRequiredString(objOrder.get_order_date() , strLength);

                        Phrase p4 = new Phrase("",B10E);
                        p4.add(new Chunk("تاريخ الطلب", B10));
                        p4.add(new Chunk("    Order Date :", B10E));
                        p4.add(new Chunk(Chunk.NEWLINE));
                        p4.add(new Chunk(strString, B10E));
                        document.add(p4);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas4 = new ColumnText(writer.getDirectContent());
                        canvas4.setSimpleColumn(36, 750, 559, 780);

                        canvas4.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas4.setAlignment(Element.ALIGN_LEFT);
                        canvas4.addElement(p4);
                        canvas4.addElement(new Chunk(Chunk.NEWLINE));
                        canvas4.go();

                        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);

                        strString = Globals.myRequiredString(user.get_name() , strLength);

                        Phrase p5 = new Phrase("",B10E);
                        p5.add(new Chunk("أمين الصندوق", B10));
                        p5.add(new Chunk("   Cashier :", B10E));
                        p5.add(new Chunk(Chunk.NEWLINE));
                        p5.add(new Chunk(strString, B10E));
                        document.add(p5);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas5 = new ColumnText(writer.getDirectContent());
                        canvas5.setSimpleColumn(36, 750, 559, 780);

                        canvas5.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas5.setAlignment(Element.ALIGN_LEFT);
                        canvas5.addElement(p5);
                        canvas5.addElement(new Chunk(Chunk.NEWLINE));
                        canvas5.go();


                        strLength=17;
                        strString = Globals.myRequiredString("اسم المنتج", strLength);

                        Phrase pr = new Phrase(strString, B10);
                        //table.addCell(c1);
                        pr.add(new Chunk("/Item Name", B10E));
                        pr.add(new Chunk(" "));
                        document.add(pr);
                        document.add(new Chunk(Chunk.NEWLINE));

                        strLength =17;
                        strString = Globals.myRequiredString("Quantity", strLength);
                        Phrase prqen = new Phrase(strString, B10E);

                        prqen.add(new Chunk(" "));
                        strLength=17;
                        strString = Globals.myRequiredString("Price", strLength);
                        prqen.add(new Chunk(strString, B10E));
                        prqen.add(new Chunk(" "));
                        strLength=17;
                        strString = Globals.myRequiredString("Total", strLength);
                        prqen.add(new Chunk(strString, B10E));
                        document.add(prqen);


                        strLength =17;
                        strString = Globals.myRequiredString("كمية", strLength);
                        Phrase prq1 = new Phrase(strString, B10);

                        prq1.add(new Chunk(" "));
                        strLength=17;
                        strString = Globals.myRequiredString("سعر", strLength);
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

/////////////////////  subtotal
                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check) , strLength);

                        Phrase p6 = new Phrase("",B10E);
                        p6.add(new Chunk("مبلغ إجمالي", B10));
                        p6.add(new Chunk("  Sub Total :", B10E));
                        p6.add(new Chunk(Chunk.NEWLINE));
                        p6.add(new Chunk(strString, B10E));
                        document.add(p6);
                    document.add(new Chunk(Chunk.NEWLINE));
                        ColumnText canvas6 = new ColumnText(writer.getDirectContent());
                        canvas6.setSimpleColumn(36, 750, 559, 780);

                        canvas6.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas6.setAlignment(Element.ALIGN_LEFT);
                        canvas6.addElement(p6);
                        canvas6.addElement(new Chunk(Chunk.NEWLINE));
                        canvas6.go();


                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check) , strLength);

                        Phrase p7 = new Phrase("",B10E);
                        p7.add(new Chunk("مجموع الضريبة", B10));
                        p7.add(new Chunk("   Total Tax :", B10E));
                        p7.add(new Chunk(Chunk.NEWLINE));
                        p7.add(new Chunk(strString, B10E));
                        document.add(p7);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas7 = new ColumnText(writer.getDirectContent());
                        canvas7.setSimpleColumn(36, 750, 559, 780);

                        canvas7.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas7.setAlignment(Element.ALIGN_LEFT);
                        canvas7.addElement(p7);
                        canvas7.addElement(new Chunk(Chunk.NEWLINE));
                        canvas7.go();

                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check) , strLength);

                        Phrase p8 = new Phrase("",B10E);
                        p8.add(new Chunk("خصم", B10));
                        p8.add(new Chunk("   Discount :", B10E));
                        p8.add(new Chunk(Chunk.NEWLINE));
                        p8.add(new Chunk(strString, B10E));
                        document.add(p8);
                        document.add(new Chunk(Chunk.NEWLINE));

                        ColumnText canvas8 = new ColumnText(writer.getDirectContent());
                        canvas8.setSimpleColumn(36, 750, 559, 780);

                        canvas8.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas8.setAlignment(Element.ALIGN_LEFT);
                        canvas8.addElement(p8);
                        canvas8.addElement(new Chunk(Chunk.NEWLINE));
                        canvas8.go();



                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check) , strLength);

                        Phrase p10 = new Phrase(strString + ": ",B10E);
                        p10.add(new Chunk("صافي المبلغ", B10));
                        p10.add(new Chunk("   Net Amount :", B10E));
                        p10.add(new Chunk(Chunk.NEWLINE));
                        document.add(p10);

                        ColumnText canvas10 = new ColumnText(writer.getDirectContent());
                        canvas10.setSimpleColumn(36, 750, 559, 780);

                        canvas10.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas10.setAlignment(Element.ALIGN_LEFT);
                        canvas10.addElement(p10);
                        canvas10.addElement(new Chunk(Chunk.NEWLINE));
                        canvas10.go();

                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check) , strLength);

                        Phrase p11 = new Phrase(strString + ": ",B10E);
                        p11.add(new Chunk("المبلاغ المدفوع", B10));
                        p11.add(new Chunk("   Tender :", B10E));
                        p11.add(new Chunk(Chunk.NEWLINE));
                        document.add(p11);

                        ColumnText canvas11 = new ColumnText(writer.getDirectContent());
                        canvas11.setSimpleColumn(36, 750, 559, 780);

                        canvas11.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas11.setAlignment(Element.ALIGN_RIGHT);
                        canvas11.addElement(p11);
                        canvas11.addElement(new Chunk(Chunk.NEWLINE));
                        canvas11.go();

                        strString = Globals.myRequiredString(Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check) , strLength);

                        Phrase p13 = new Phrase(strString + ": ",B10E);
                        p13.add(new Chunk("يتغيرون", B10));
                        p13.add(new Chunk("   Change :", B10E));
                        p13.add(new Chunk(Chunk.NEWLINE));
                        document.add(p13);

                        ColumnText canvas13 = new ColumnText(writer.getDirectContent());
                        canvas13.setSimpleColumn(36, 750, 559, 780);

                        canvas13.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
                        canvas13.setAlignment(Element.ALIGN_LEFT);
                        canvas13.addElement(p13);
                        canvas13.addElement(new Chunk(Chunk.NEWLINE));
                        canvas13.go();

                        document.newPage();
                        document.close();
                        file.close();
                    }
                    Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
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
                Globals.AppLogWrite("customer debit check status"+chk_cus_debit.isChecked());

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
                                if(settings.get_Is_Cash_Drawer().equals("1")) {
                                    Globals.AppLogWrite("cash drawer status"+settings.get_Is_Cash_Drawer());
                                    open_cash_drawer();
                                }
                                Globals.AppLogWrite("cash drawer status inside checkbox code"+settings.get_Is_Cash_Drawer());

                                CheckOutOrder();
                            }
                        }.start();

                    }

                } else {
                    if (Double.parseDouble(edt_tender.getText().toString().trim()) > Double.parseDouble(edt_net_amount.getText().toString().trim()) && !PayId.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Tender should not grater then net amount with payment mode", Toast.LENGTH_SHORT).show();
                    } else {

                        Globals.AppLogWrite("cash drawer status before progressdialog"+settings.get_Is_Cash_Drawer());

                        progressDialog = new ProgressDialog(PaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.show();
                        new Thread() {
                            public void run() {
                                try {
                                    sleep(100);

                                   // call_MN();
                                    Globals.AppLogWrite("cash drawer status outside"+settings.get_Is_Cash_Drawer());
//if(settings.get_Is_Customer_Display().equals("true")) {
    if (settings.get_Is_Cash_Drawer().equals("true")) {

        Globals.AppLogWrite("cash drawer status" + settings.get_Is_Cash_Drawer());

        open_cash_drawer();

    }
//}



                                            CheckOutOrder();


                     //  progressDialog.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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

    private void GetCustomerCreditDetail( String serverData) {
        try {
            String succ_manu = "0";


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

   /* private String GetCusCrDetailFromServer() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "accounts");
        ArrayList nameValuePairs = new ArrayList(9);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("contact_code", Globals.strContact_Code));
        nameValuePairs.add(new BasicNameValuePair("type", "S"));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1",serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
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
    }*/

    public void GetCusCrDetailFromServer(String serial_no,String android_id,String myKey) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.waiting));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "accounts";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            GetCustomerCreditDetail(response);
                           // pDialog.dismiss();
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
                      //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("contact_code", Globals.strContact_Code);
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



    class Sendorder_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        PaymentActivity activity;

        public Sendorder_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Orders order=new Orders(getApplicationContext());

                 order.sendOn_Server(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                            //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


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


    private void printKOT() {
        boolean flag = false;
        getIP();
        try {


            for (int i = 0; i < ipAdd.size(); i++) {
                ip = ipAdd.get(i).toString();

                try {
                    final Orders orders = Orders.getOrders(PaymentActivity.this, database, "WHERE order_code = '" +strOrderCode + "'");
                    final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(PaymentActivity.this, "WHERE order_code = '" + strOrderNo + "'",database);

                    performOperationEn(ipAdd.get(i), 0, "Order",orders,order_detail1);
                    performOperationEn(ipAdd.get(i), 0, "Void",orders,order_detail1);

                } catch (Exception ex) {
                    //   GlobleVar.isSendOnline = true;
                    ex.getStackTrace();
                    //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
                //String last = ipAdd.get(ipAdd.size() - 1);


            }


/******************************** Master Print ***************************/

  /*          try {
                String result = getMasterPrint_from_server();
                JSONObject jsonObject = new JSONObject(result);

                String message = jsonObject.getString("Message");
                String printername = jsonObject.getString("PrinterName");


                if (message.equals("Success")) {
                    // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    performOperationEn(printername, 1, "Order");
                    performOperationEn(printername, 1, "Void");
                } else {
                    Toast.makeText(getApplicationContext(), "Master Print json" + result, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }*/
            // pDialog.dismiss();


        } catch (Exception e) {
            //  GlobleVar.isSendOnline = true;
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }


    private void performOperationEn(String ip, int iOperation, String strMode,Orders orders,Order_Detail order_detail) {
        try {
            mylist.clear();

            try {
                getItemCategoryForPrint(ip, iOperation, strMode);
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            if (catId.size() > 0) {
                try {
                    mylist = getlistTest(iOperation, strMode, ip,orders);
                } catch (Exception e) {

                    System.out.println(e.getMessage());
                }
                Globals.AppLogWrite(mylist);
                if (mylist.size() > 0) {
                    try {


                        ArrayList<String> lPrintLog1 = new ArrayList<>();
                        lPrintLog1.add("Operation :" + iOperation);
                        lPrintLog1.add("Mode :" + strMode);
                        lPrintLog1.add("Current Printer :" + ip);
                        lPrintLog1.add("Total Item   :" + Globals.cart.size() + "");
                       /* for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                            lPrintLog1.add("Count   :" + (lCount + 1) + "");
                            ShoppingCart cartitem = Globals.cart.get(lCount);
                            try {
                                lPrintLog1.add("Item IP :  " + cartitem.getCategoryIp().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item IP :  " + ex.getMessage().toString());
                            }

                 *//*           try {
                                lPrintLog1.add("Item Print Flag :  " + cartitem.getPrintFlag().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Print Falg :  " + ex.getMessage().toString());
                            }
                            try {
                                lPrintLog1.add("Item New :  " + cartitem.isNew() + "");
                            } catch (Exception ex) {
                                lPrintLog1.add("Item New Falg :  " + ex.getMessage().toString());
                            }*//*
                            try {
                                lPrintLog1.add("Item  :  " + cartitem.get_Item_Name().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Name :  " + ex.getMessage().toString());
                            }
                        }
*/
         /*               if (iOperation == 0 && strMode.equals("Order")) {
                            for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                ShoppingCart cartItem = Globals.cart.get(lCount);
                                try {
                                    if (cartItem.getCategoryIp().equals(ip)) {
                                        if (order_detail.getIs_KitchenPrintFlag().equals("false")) {
                                           // lPrintLog.add(cartItem.get_Item_Name());
                                            order_detail.setIs_KitchenPrintFlag("true");
                                        }
                                    }
                                } catch (Exception e) {

                                }


                            }
                        }*/
                        Globals.AppLogWrite(lPrintLog1);


                        if (CheckprinterConnection(ip.trim())) {
                            try {
                                String bill = "";
                                for (String data : mylist) {
                                    bill = bill + data;
                                }
                                for (int k = 1; k <= Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
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

                                    ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add(ip);
                                    lPrintLog.add(" Printing time ip ------------------");

                                    if (iOperation == 0 && strMode.equals("Order")) {
                                        for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                            ShoppingCart cartItem = Globals.cart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getKitchenprintflag().equals("false")) {
                                                        lPrintLog.add(cartItem.get_Item_Name());
                                                        String sql= "UPDATE order_detail SET is_KitchenPrintFlag = 'true' where item_code= '"+cartItem.get_Item_Code()+"' and order_code='"+strOrderCode+"'";
                                                        db.executeDML(sql,database);
                                                        //  order_detail.setIs_KitchenPrintFlag("true");
                                                    }
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                        // pDialog.dismiss();
                                      /*  Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    else  if (iOperation == 0 && strMode.equals("Void")) {
                                        for (int lCount = 0; lCount < Globals.voidcart.size(); lCount++) {
                                            VoidShoppingCart cartItem = Globals.voidcart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getVoidkitchenflag().equals("false")) {
                                                        String sql = "UPDATE Void SET print_flag = 'true' where item_code= '" + cartItem.get_Item_Code() + "' and order_no='" + strOrderCode + "'";
                                                        db.executeDML(sql, database);
                                                    }
                                                    //  order_detail.setIs_KitchenPrintFlag("true");
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                        // pDialog.dismiss();
                                   /*     Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    Globals.AppLogWrite(lPrintLog);


                                }


                                // Globals.isSendOnline = true;
                            } catch (Exception e) {
                                //GlobleVar.isSendOnline = true;

                            }

                        }


                    } catch (Exception ex) {
                        //  GlobleVar.isSendOnline = true;
                        Toast.makeText(getApplicationContext(), "Please Check Printer Connection", Toast.LENGTH_SHORT).show();
                    }

                    // pDialog.dismiss();
                    //  Globals.setEmpty();

                } else {
                    // GlobleVar.isSendOnline = true;
                }


            } else {

                //GlobleVar.isSendOnline = true;
            }
        } catch (Exception ex) {
            // GlobleVar.isSendOnline = true;
            ex.getStackTrace();
        }

    }
    public Boolean CheckprinterConnection(String ipp) {

        if (!ipp.equals("")) {
            String ipAddress = "";
            String tmpPort = "";
            int port = 9100;
            String[] strings = StringSplit(ipp, ":");
            ipAddress = strings[0];
            tmpPort = strings[1];
            port = Integer.parseInt(tmpPort);

            if (!WifiPrintDriver.WIFISocket(ipAddress, port)) {
                WifiPrintDriver.Close();
                return false;
            } else {
                if (WifiPrintDriver.IsNoConnection()) {
                  //  progressDialog.dismiss();
                    return false;
                }
                return true;
            }

        } else {
            return false;
        }
    }

    private void getIP() {
        try {
            ipAdd.clear();

            String sqlQuery=  "Select Distinct(categoryIp ) from item_group where item_group_code IN (Select item_group_code from item where item_code IN (Select item_code from order_detail Where order_code='"+strOrderCode+"'))";

            Cursor cursor = database.rawQuery(sqlQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    String categoryIps = cursor.getString(0);
                    ipAdd.add(categoryIps);

                    ///arrayListTable.add(new Table(this, null, tablecode, tablename, zone_id, zone_name, "", "", "",""));
                } while (cursor.moveToNext());

            }

            cursor.close();

            // Old Code

     /*       itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            if (itemgroup_catArrayList.size() > 0) {
                for (int i = 0; i < itemgroup_catArrayList.size(); i++) {

                   // if(itemgroup_catArrayList.get(i).getCategoryIp().equals(ip)) {
                        ipAdd.add(itemgroup_catArrayList.get(i).getCategoryIp());
                    //}
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getItemCategoryForPrint(String ip, int iOperation, String strMoString) {
        catId.clear();
        final ArrayList<ShoppingCart> myCart = Globals.cart;
        if (strMoString.equals("Order")) {
            if (iOperation == 0) {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0'", database, db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < myCart.size(); i++) {
                    ShoppingCart cartitem = myCart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");


                    if (cartitem.getIs_modifier().equals("1")) {
                        if (dtl_modifier != null) {
                            Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);

                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                        if (!catId.contains(item.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(item.get_item_group_code());
                                        }
                                    } /*else {
                                        if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                            if (!catId.contains(categoryitem.get_item_group_code())) {
                                                catId.add(categoryitem.get_item_group_code());
                                            }
                                        }
                                    }*/

                                }

                            }
                        }
                    } else {
                        Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + myCart.get(i).get_Item_Code() + "'",database);
                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code()+ "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                    if (!catId.contains(item.get_item_group_code())&& cartitem.getCategoryIp().equals(ipAdd.get(a))){
                                        catId.add(item.get_item_group_code());
                                    }
                                }
                            } /*else {
                                if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code())) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                            }*/
                        }

                    }
                }
            }


        } else {
            if (iOperation == 0) {


                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where categoryIp = '" + ip.trim() + "' and (is_active ='1' and parent_code = '0')", database,db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where (Active='1' or Active='1')", database,db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                        //    Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getItem_code() + "'",database);

                            Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }
                                } else {
                                    if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                      //  Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                // if (!item.getPrintFlag().equals("true")) {
                                if (!cartitem.getVoidkitchenflag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                                // }
                            } else {
                                if (!cartitem.getVoidkitchenflag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                            }

                        }

                    }
                }
            }



        }

    }


    private ArrayList<String> getlistTest(int iOperation, String strMode, String Ip,Orders orders) {
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + orders.get_order_type_id() + "'", database, db);

        if (strMode.equals("Order")) {


            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );

            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.cart.size(); i++) {


                    ShoppingCart cartitem = Globals.cart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            //Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + itemgroup_catArrayList.get(a).get_item_group_code()+ "'", database,db);
                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);

                            //     Item aetSet_pos_menu_item = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + catId.get(a) + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //item.setPrintFlag("true");

                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name + "(M)";
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name + " (M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'", database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);
                        if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                   /* ArrayList<String> lPrintLog1 = new ArrayList<>();
                                    lPrintLog1.add(ip);
                                    lPrintLog1.add("-------- get item operation 0");
                                    GlobleVar.AppLogWrite(lPrintLog1);*/
                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    //cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            } else {
                               /* ArrayList<String> lPrintLog = new ArrayList<>();
                                lPrintLog.add("-------- get item operation 1");
                                lPrintLog.add(Ip);
                                GlobleVar.AppLogWrite(lPrintLog);*/

                                // working code
//                                if (cartitem.getIsmaster_PrintFlag().equals("true")) {
//                                } else {
//                                    qty = cartitem.get_Quantity() + "";
//                                    //item.setCatIp(Ip);
//                                    name = cartitem.get_Item_Name();
//                                    if (name == null) {
//                                    } else {
//                                        bFinalCheck = false;
//                                        bCheck = false;
//                                        raw = qty + "   " + name;
//                                        mylist.add("\n" + raw);
//                                    }
//
//                                }
                            }

                        }
                    }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("Order"+ mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;
        }
        else
        {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "Void KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "Void KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name());


            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);


                    if (cartitem.getIs_modifier().equals("1")) {
                        ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");
                        if (dtl_modifier != null) {

                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code()+ "'", database, db);
                            if (cartitem != null) {
                                if (iOperation == 0) {
                                    if (strMode.equals("Order")) {
                                        if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                        } else {
//                                        item.setPrintFlag("true");
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name();
                                            if (name == null) {
                                            } else {
                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name + " (M)";
                                                        mylist.add("\n" + raw);
                                                    }}
                                            }
                                        }
                                    } else {
                                        qty = cartitem.get_Quantity() + "";

                                        name = item.get_item_name();
                                        if (name == null) {
                                        } else {
                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name + " (M)";
                                                    mylist.add("\n" + raw);
                                                }}
                                        }
                                    }
                                } else {
                                    // item.setPrintFlag("true");
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    ;
                                    if (name == null) {
                                    } else {
                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name + "(M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }}
                                }

                            }
                        }
                    } else {
                        //  Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database, db);
                        if (cartitem != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (cartitem.getVoidkitchenflag().equals("true")) {
                                    } else {
                                        //  item.setPrintFlag("true");
                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    //cartitem.setCategoryIp(Ip);
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            } else {

                                //   item.setPrintFlag("true");
                                qty = cartitem.get_Quantity() + "";
                                //cartitem.setCategoryIp(Ip);
                                name = cartitem.get_Item_Name();
                                if (name == null) {
                                } else {

                                    if (ip.equals(cartitem.getCategoryIp())) {
                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name;
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                }

                            }

                        }
                    }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("VOID"+mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;

        }
//return  mylist;
    }

    public  class PrintKOT_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        PaymentActivity activity;

        public PrintKOT_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

               // printKOT();

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


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
}

