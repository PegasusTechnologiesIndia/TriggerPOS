package org.phomellolitepos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.RetailListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.Util.ZipManager;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.ScaleSetup;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.VoidShoppingCart;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import static org.phomellolitepos.Util.Globals.StringSplit;

public class Retail_IndustryActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler,NavigationView.OnNavigationItemSelectedListener
{
    Boolean doubleBackToExitPressedOnce = false;
    RetailListAdapter retailListAdapter;
    AutoCompleteTextView edt_toolbar_retail;
    EditText edt_toolbar_search;
    Button btn_retail_1, btn_retail_2;
    JavaEncryption javaEncryption;
    ArrayList<String> arrayList1;
    ArrayList<Item> arrayList;
    String strSelectedCategory = "";
    String versionname;
    ArrayList<ReceipeModifier> receipemodifierlist;
    String decimal_check, qty_decimal_check;
    Database db;
    String sale_priceStr;
    Contact_Bussiness_Group contact_bussiness_group;
    Contact contact;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    String cost_priceStr;
    ImageView img_add,img_qrcode;
    SQLiteDatabase database;
    String serial_no, android_id, myKey, device_id, imei_no;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    String line_total_af,line_total_bf,operation,opr,strOrderCode;
    TextView txt_title;
    String strRemarks = "",date,modified_by;
    Button btn_Item_Price, btn_Qty;
    Dialog listDialog2;
    MenuItem orertype;
    ProgressDialog pDialog;
    String item_group_code;
    Double curQty = 0d;
    private long pressedTime;

    private ArrayList<String> mylist = new ArrayList<String>();
    ArrayList<String> ipAdd = new ArrayList<String>();
    ArrayList<Item_Group> itemgroup_catArrayList;
    private ArrayList<String> catId = new ArrayList<String>();
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type;
    Settings settings;
    int OrintValue;
    ScaleSetup scalesetup;
    ZBarScannerView mScannerView;
    boolean flag_scan = false;
    String barcodelength="0",plvaluelen;
    String weightlength;
    DrawerLayout drawer;
    Dialog listDialog1;
    UserPermission userPermission;
    ProgressDialog progressDialog;
    SyncDialogCaller obj_syncdialog;
    String email,password;
    Item item;
    AlertDialog.Builder builder;
    AlertDialog.Builder builder1;ArrayList<Order_Detail> order_detail = new ArrayList<Order_Detail>();
    ArrayList<Order_Tax> order_tax = new ArrayList<Order_Tax>();
    ArrayList<Order_Detail_Tax> order_detail_tax = new ArrayList<Order_Detail_Tax>();
    String company_email, company_password;
    TextView list_title, my_company_name, my_company_email, txt_user_name, txt_version;
    double Dweightvalue=0;
    Tax_Master tax_master;
    Order_Type_Tax order_type_tax;
    Tax_Detail tax_detail;
    Item_Supplier item_supplier;
    Item_Location item_location;
    ArrayList<Contact> contact_ArrayList;
    DialogContactMainListAdapter dialogContactMainListAdapter;
    Address_Lookup address_lookup;
    org.phomellolitepos.database.Address address;
    Button btn_syncdemo;
    Sys_Tax_Group sys_tax_group;

    BottomNavigationView bottomNavigationView;
    String ip = null;
    Order_Type order_type;
  //  String strKitchenFlag="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_view();
    }

    public void main_view() {
       setContentView(R.layout.activity_retail__industry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
      /*  try {
            appLocationService = new AppLocationService(
                    RetailActivity.this);
        } catch (Exception e) {
        }*/
        settings = Settings.getSettings(getApplicationContext(), database, "");
        txt_title = (TextView) findViewById(R.id.txt_title);
        edt_toolbar_retail = (AutoCompleteTextView) findViewById(R.id.edt_toolbar_retail);
        edt_toolbar_search = (EditText) findViewById(R.id.edt_toolbar_search);
        btn_retail_1 = (Button) findViewById(R.id.btn_retail_1);
        btn_retail_2 = (Button) findViewById(R.id.btn_retail_2);
        btn_syncdemo=(Button)findViewById(R.id.btn_syncdemo);
        img_add=(ImageView)findViewById(R.id.imgadd);
        img_qrcode=(ImageView)findViewById(R.id.img_qrcode);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        edt_toolbar_search.requestFocus();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        try {
            if(mScannerView!=null) {
                mScannerView = new ZBarScannerView(Retail_IndustryActivity.this);
                mScannerView.setResultHandler(Retail_IndustryActivity.this);
            }
           else if(mScannerView == null)
            {
                mScannerView = new ZBarScannerView(this);
               // setContentView(mScannerView);
            }
        }
        catch(Exception e){

        }
  /*      OrintValue = getApplicationContext().getResources().getConfiguration().orientation;

        if (OrintValue == Configuration.ORIENTATION_PORTRAIT) {
            Globals.OrientValue = OrintValue;
            setContentView(R.layout.activity_main);
            if (Globals.OrintFlagP) {
                Load_Activity();
            }
            Globals.OrintFlagL = true;
            Globals.OrintFlagP = false;
        } else {
            Globals.OrientValue = OrintValue;
            setContentView(R.layout.activity_main_land);
            if (Globals.OrintFlagL) {
                Load_Activity();
            }
            Globals.OrintFlagP = true;
            Globals.OrintFlagL = false;
        }*/


        Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        company_email = Globals.objLPR.getEmail();
        company_password = Globals.objLPR.getPassword();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        my_company_name = (TextView) hView.findViewById(R.id.my_company_name);
        my_company_email = (TextView) hView.findViewById(R.id.my_company_email);
        txt_user_name = (TextView) hView.findViewById(R.id.txt_user_name);
        txt_version = (TextView) hView.findViewById(R.id.txt_version);
        serial_no = Build.SERIAL;
        Globals.serialno = serial_no;

        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Globals.androidid = android_id;
        myKey = serial_no + android_id;
        Globals.mykey = myKey;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei_no = telephonyManager.getImei();
        }*/
       // Menu menu = navigationView.getMenu();
        MenuItem nav_item;
        navigationView.setNavigationItemSelectedListener(this);
        item = Item.getItem(getApplicationContext(), "", database, db);
            if (item == null) {
                btn_syncdemo.setVisibility(View.VISIBLE);
            }
            else{
                btn_syncdemo.setVisibility(View.GONE);
            }



            img_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edt_toolbar_retail.getText().toString().equals("\n") || edt_toolbar_retail.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                        edt_toolbar_retail.requestFocus();
                    } else {
                        try {
                            AddToCart();
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }



            }
            });
            img_qrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globals.BarcodeReslt = "";

                    flag_scan = true;
                    try {
                        if(mScannerView!=null) {
                            mScannerView = new ZBarScannerView(Retail_IndustryActivity.this);
                            mScannerView.setResultHandler(Retail_IndustryActivity.this);
                            mScannerView.startCamera(); // Programmatically initialize the scanner view
                            setContentView(mScannerView);
                        }
                    }
                    catch(Exception e){

                    }
                }
            });
        btn_syncdemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                getAlertforDemoDB();
            }
        });
        if (Globals.objLPR.getproject_id().equals("cloud")) {

            item = Item.getItem(getApplicationContext(), "", database, db);
            if (item == null) {

                // btn_syncdemo.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(Retail_IndustryActivity.this);

                builder.setTitle(getString(R.string.alerttitle));
                builder.setMessage(getString(R.string.alert_syncdata));

                builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        try {

                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                progressDialog = new ProgressDialog(Retail_IndustryActivity.this);
                                progressDialog.setTitle("");
                                progressDialog.setMessage(getString(R.string.Syncdataserver));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                final Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            try {
                                                sleep(200);
                                                obj_syncdialog = new SyncDialogCaller(Retail_IndustryActivity.this, database, db);
                                                obj_syncdialog.sync_all(progressDialog, Retail_IndustryActivity.this, database, serial_no, android_id, myKey, Globals.license_id);

                                                // progressDialog.dismiss();

                                               /* runOnUiThread(new Runnable() {
                                                    public void run() {

                                                     setReloadctivity();
                                                   }
                                                });
*/
                                                // btn_syncdemo.setVisibility(View.GONE);

                                            } catch (final Exception e) {
                                                // progressDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception ex) {
                                            // progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                };
                                t.start();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_SHORT).show();
                            }

                            //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
                        } catch (Exception e) {
                            Globals.AppLogWrite("Alert exception" + e.getMessage());
                        }

                    }
                });

                builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // btn_syncdemo.setVisibility(View.VISIBLE);
                        getAlertforDemoDB();
                        // Do nothing

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
            else{

                btn_syncdemo.setVisibility(View.GONE);
            }
        }
        try {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.format(d);

        }
        catch(Exception e){}
        Globals.objsettings = Settings.getSettings(getApplicationContext(), database, "");

       try {
            scalesetup = ScaleSetup.getScaleSetup(getApplicationContext(), database, db, "");
            if (scalesetup == null) {
                barcodelength = "0";
                plvaluelen = "0";
            } else {
                barcodelength = scalesetup.getPLU_BARCODE_LEN();
                plvaluelen = scalesetup.getPlu_len();
            }
        }catch(Exception e){

        }
        edt_toolbar_search.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.retail_bottom_navigation);

        Intent intent = getIntent();
        // operation = intent.getStringExtra("opn");
      //  Globals.load_form_cart = "0";
        opr = intent.getStringExtra("opr");
        strOrderCode = intent.getStringExtra("order_code");
        modified_by = Globals.user;
      //  Globals.load_form_cart = "1";
        if (opr == null) {

            System.out.println("flagscAN"+ flag_scan);
            if(flag_scan==false) {

                    System.out.println("flagscAN" + flag_scan);
                    System.out.println("opr " + opr);
                    Globals.cart.clear();
                    Globals.TotalItemPrice = 0;
                    Globals.TotalQty = 0;

            }
            else{
                System.out.println("flagscAN else"+ flag_scan);
                System.out.println("opr else"+ opr);

            }

            opr = "Add";
            Globals.Operation = opr;


        }
        else if (opr.equals("") || opr.equals("Add")) {


        }
        if (strOrderCode == null) {
            strOrderCode = "";

        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        javaEncryption = new JavaEncryption();

        listDialog2 = new Dialog(this);

        if (opr.equals("Edit")) {
            try {
                if (flag_scan == true) {
                } else {
//                    line_total_af = intent.getStringExtra("line_total_af");
//                    line_total_bf = intent.getStringExtra("line_total_bf");
//
//                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
//                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                }
            } catch (Exception ex) {

                System.out.println(ex.getMessage());
            }

            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(item_price);

            Menu menu = bottomNavigationView.getMenu();
            orertype = menu.findItem(R.id.action_clear);
            orertype.setEnabled(false);

        } else {
            try {
                if (flag_scan == true) {
                } else {
//                    line_total_af = intent.getStringExtra("line_total_af");
//                    line_total_bf = intent.getStringExtra("line_total_bf");

//                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
//                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                }
            } catch (Exception ex) {

                System.out.println(ex.getMessage());
            }

            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String item_price;

            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(item_price);
        }

        arrayList1 = new ArrayList<String>();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        Globals.objsettings=settings;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode

        if(settings!=null){
            Globals.PrinterType = Globals.objsettings.getPrinterId();
            Globals.strIsBarcodePrint = Globals.objsettings.get_Is_BarcodePrint();
            Globals.strIsDenominationPrint = Globals.objsettings.get_Is_Denomination();
            Globals.strIsDiscountPrint = Globals.objsettings.get_Is_Discount();
            Globals.GSTNo = Globals.objsettings.get_Gst_No();
            Globals.GSTLbl = Globals.objsettings.get_GST_Label();
            Globals.PrintOrder = Globals.objsettings.get_Print_Order();
            Globals.PrintCashier = Globals.objsettings.get_Print_Cashier();
            Globals.PrintInvNo = Globals.objsettings.get_Print_InvNo();
            Globals.PrintInvDate = Globals.objsettings.get_Print_InvDate();
            Globals.PrintDeviceID = Globals.objsettings.get_Print_DeviceID();
        }

       /* final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE order_code = '" + strOrderCode + "'",database);
        if(order_detail1!=null){
            strKitchenFlag= order_detail1.getIs_KitchenPrintFlag();
        }*/

     //   Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
        try {
            PackageInfo pInfo = null;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            my_company_name.setText(Globals.objLPR.getCompany_Name());
            my_company_email.setText(Globals.objLPR.getEmail());
            txt_user_name.setText(user.get_name());
            email = user.get_email();
            password = user.get_password();
            txt_version.setText("Version : " + pInfo.versionName);
            versionname = pInfo.versionName;

        } catch (Exception ex) {
        }
        Menu menu = navigationView.getMenu();
        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.right_nav_view);

        Menu menu1 = navigationViewRight.getMenu();

        if(Globals.objLPR.getproject_id().equals("standalone")){

            menu1.findItem(R.id.nav_get_itemimages).setVisible(false);

        }
        if(Globals.objLPR.getIndustry_Type().equals("2")) {
            menu1.findItem(R.id.nav_get_itemimages).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);

        if (Globals.objLPR.getproject_id().equals("cloud")) {
            menu.findItem(R.id.nav_payment).setVisible(false);


        } else {
            menu.findItem(R.id.nav_payment).setVisible(true);
            menu.findItem(R.id.nav_return).setVisible(false);
            menu.findItem(R.id.nav_acc).setVisible(false);

            //  menu.findItem(R.id.nav_get_file).setVisible(false);

        }


        change_customer_icon(Globals.strContact_Code);
        if (opr.equals("Edit")) {
            if (Globals.load_form_cart.equals("0")) {
                ShoppingCart cartItem = null;
                Globals.cart.clear();
                Globals.order_item_tax.clear();
                Globals.TotalItemPrice = 0;
                Globals.TotalQty = 0;

                order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);

                for (int i = 0; i < order_detail.size(); i++) {
                    String strItemCode = order_detail.get(i).get_item_code();
                    Item item = Item.getItem(getApplicationContext(), " WHERE item_code = '" + strItemCode + "'", database, db);
                    Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item.get_item_group_code() + "'");

                    cartItem = new ShoppingCart(getApplicationContext(), order_detail.get(i).get_sr_no(), order_detail.get(i).get_item_code(), item.get_item_name(), order_detail.get(i).get_quantity(), order_detail.get(i).get_cost_price(), order_detail.get(i).get_sale_price(), order_detail.get(i).get_tax(), "0", order_detail.get(i).get_line_total(),"0","0",item_group.getCategoryIp(),"false",order_detail.get(i).getUnit_id(),order_detail.get(i).getBeforeTaxPrice());
                    Globals.cart.add(cartItem);

                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(order_detail.get(i).get_line_total());
                    Globals.TotalQty = Globals.TotalQty + Double.parseDouble(order_detail.get(i).get_quantity());

                }

                Globals.SRNO = Globals.cart.size() + 1;

                order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), " WHERE order_code='" + strOrderCode + "'", database);
                for (int i = 0; i < order_detail_tax.size(); i++) {
                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", order_detail_tax.get(i).get_order_code(), order_detail_tax.get(i).get_sr_no(), order_detail_tax.get(i).get_item_code(), order_detail_tax.get(i).get_tax_id(), order_detail_tax.get(i).get_tax_type(), order_detail_tax.get(i).get_rate(), order_detail_tax.get(i).get_tax_value());
                    Globals.order_item_tax.add(order_item_tax);
                }

                String odr_type_id;
                Orders orders = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderCode + "'");
                odr_type_id = orders.get_order_type_id();
                Globals.strOrder_type_id = odr_type_id;


                Orders orders1 = Orders.getOrders(getApplicationContext(), database, "WHERE order_code='" + strOrderCode + "'");
                try {
                    String strtableCode = orders1.get_table_code();
                    Globals.strTable_Code = strtableCode;
                } catch (Exception ex) {
                }
                Globals.strOrder_type_id=orders1.get_order_type_id();
                if(Globals.strOrder_type_id.equals("5")){
                    Table table = Table.getTable(getApplicationContext(), database, db, " WHERE table_code='" + orders1.get_table_code() + "'");

                    Globals.strorderType="Dine-In";
                    Globals.table_code=table.get_table_code();
                    Globals.table_name=table.get_table_name();
                    Globals.strZoneName=table.getZone_name();
                }
                try {
                    String strContactCode = orders1.get_contact_code();
                    Globals.strContact_Code = strContactCode;
                } catch (Exception ex) {
                }
            } else {
                try {
                    line_total_af = intent.getStringExtra("line_total_af");
                    line_total_bf = intent.getStringExtra("line_total_bf");
                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                } catch (Exception ex) {
                }
            }



            //change_customer_icon();
        } else {
            try {
                line_total_af = intent.getStringExtra("line_total_af");
                line_total_bf = intent.getStringExtra("line_total_bf");
                Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
            } catch (Exception ex) {
            }

        }

        retail_list_load();
        btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
        String itemPrice;
        itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
        btn_retail_2.setText(itemPrice);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_check_out:
//                                boolean OrdValdCheckOut = Order_Total_Validation();
//                                if (!OrdValdCheckOut) {
//                                    Toast.makeText(getApplicationContext(), "Order and Item total are not equal so updating total!", Toast.LENGTH_LONG).show();
//                                }
                                if (Globals.cart.size()  == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(Retail_IndustryActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(100);

                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Intent intent = new Intent(Retail_IndustryActivity.this, PaymentActivity.class);
                                                        intent.putExtra("opr", opr);
                                                        intent.putExtra("order_code", strOrderCode);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();

                                            } finally {
                                            }
                                        }
                                    };
                                    timerThread.start();
                                }
                                break;

                            case R.id.action_save:

                                if (Globals.cart.size() == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    Globals.CheckContact = "0";
                                    pDialog = new ProgressDialog(Retail_IndustryActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if (opr.equals("Edit")) {
                                                            save_order(strRemarks);
                                                            progressDialog = new ProgressDialog(Retail_IndustryActivity.this);
                                                            progressDialog.setTitle("");
                                                            progressDialog.setMessage(getString(R.string.waiting));
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.show();
                                                            Thread t = new Thread(){
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        printKOT(strOrderCode, progressDialog);
                                                                        progressDialog.dismiss();
                                                                        Globals.setEmpty();
                                                                        try {

                                                                            Intent i = new Intent(getApplicationContext(),Retail_IndustryActivity.class);
                                                                            startActivity(i);
                                                                              /*  btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);*/
                                                                        }
                                                                        catch(Exception e){
                                                                            System.out.println(e.getMessage());
                                                                        }

                                                                    }
                                                                    catch(Exception e){
                                                                        System.out.println(e.getMessage());
                                                                    }

                                                                }
                                                            };t.start();
                                                        } else {
                                                            showdialogremarksdialog();
                                                        }
                                                    }
                                                });
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } finally {
                                            }
                                        }
                                    };
                                    timerThread.start();
                                }

                                break;
                            case R.id.action_clear:
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Globals.CheckContact = "0";
                                Globals.NoTax="";
                                Globals.Taxwith_state="";
                                Globals.Taxdifferent_state="";
                                edt_toolbar_search.setText("");
                                edt_toolbar_search.requestFocus();
//                                String cart_check2 = Globals.TotalItemPrice + "";
                                if (Globals.cart.size() == 0) {
                                    change_customer_icon(Globals.strContact_Code);
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    Globals.setEmpty();
                                    change_customer_icon(Globals.strContact_Code);

                                    retail_list_load();
                                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                    String item_price;
                                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                    btn_retail_2.setText(item_price);
                                    break;


                                }
                                break;
                            case R.id.action_contact:
                                showdialogContact();
                                break;
                            case R.id.action_whatsapphelp:
                                openWhatsApp();
                                break;
                        }
                        return true;
                    }
                });



        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.activity_retail__industry, null);
        getSupportActionBar().setCustomView(v);



        NavigationView rightNavigationView = (NavigationView) findViewById(R.id.right_nav_view);
        rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle Right navigation view item clicks here.
                int id = item.getItemId();
                Globals.SRNO = 1;
// Tax Activity
                if (id == R.id.nav_tax) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Tax", TaxListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }
// user Activity
                } else if (id == R.id.nav_user) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "User", UserListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }
                }
                // Unit Activity
                else if (id == R.id.nav_unit) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Unit", UnitListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                }
                // Database Activity
                else if (id == R.id.nav_database) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Database", DataBaseActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                }

                // Updtae License (Activate Activity)
                else if (id == R.id.nav_lic) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Update License", ActivateActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                }
                // Profile Activity
                else if (id == R.id.nav_profile) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Profile", ProfileActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                }

                // About Activity
                else if (id == R.id.nav_ab) {

                    Intent intent_about = new Intent(Retail_IndustryActivity.this, AboutActivity.class);
                    startActivity(intent_about);
                    finish();


                }

                // Support Activity
                else if (id == R.id.nav_support) {
                    Intent intent_about = new Intent(Retail_IndustryActivity.this, YouTubeVideoListActivity.class);
                    startActivity(intent_about);
                    finish();
                } else if (id == R.id.nav_privacy) {
                    try {
                        String url = "http://www.pegasustech.net/privacy";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));


                        startActivity(i);
                    }
                    catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        Toast.makeText(Retail_IndustryActivity.this,"There is no default browser Installed",Toast.LENGTH_LONG).show();
                        //i.setPackage(null);
                        //startActivity(i);
                    }
                    //finish();
                }
                // Get Files code

                else if (id == R.id.nav_get_itemimages) {
                    if (isNetworkStatusAvialable(getApplicationContext())) {
                     //   new MainActivity.DownlaodImageAsyncTask().execute();
                    }
                }
// Send Database
                else if (id == R.id.nav_send_db) {
                    if (isNetworkStatusAvialable(getApplicationContext())) {
                        DatabaseBackUp();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.nav_loyalty) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, getString(R.string.Profile), ProLoyaltySetupActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }
                } else if (id == R.id.nav_coupon) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, getString(R.string.Profile), CouponListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }
                }

                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        edt_toolbar_search.requestFocus();
        edt_toolbar_retail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_retail.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_retail.requestFocus();
                    edt_toolbar_retail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_retail, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_retail.selectAll();
                    return true;
                }
            }
        });

        edt_toolbar_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_search.getText().toString().trim().equals("")) {

                    return false;
                } else {
                    edt_toolbar_search.requestFocus();
                    edt_toolbar_search.selectAll();
                    edt_toolbar_search.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_search, InputMethodManager.SHOW_IMPLICIT);

                    return true;
               }
            }
        });

        edt_toolbar_retail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strFilter = edt_toolbar_retail.getText().toString().trim();
                autocomplete(strFilter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_toolbar_search.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                                                            || keyCode == KeyEvent.KEYCODE_TAB || keyCode==KeyEvent.ACTION_DOWN) {
                                                        edt_toolbar_search.requestFocus();
                                                        edt_toolbar_search.selectAll();
                                                        String strValue = edt_toolbar_search.getText().toString().trim();
                                                        contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + Globals.strContact_Code + "'");

                                                        int barcodelen=Integer.parseInt(barcodelength);
                                                        if(strValue.length()==barcodelen){

                                                            String pllen=strValue.substring(0,Integer.parseInt(scalesetup.getPlu_len()));
                                                            String plengthfinal=strValue.substring(pllen.length());
                                                            if(pllen.equals(scalesetup.getPlu_value())){

                                                                String itemlen= plengthfinal.substring(0,Integer.parseInt(scalesetup.getITEM_CODE_LEN()));
                                                                String itemcodefinal= plengthfinal.substring(itemlen.length());

                                                                weightlength= itemcodefinal.substring(0,Integer.parseInt(scalesetup.getWp_LEN()));
                                                                String wightlenfinal= weightlength.substring(weightlength.length());

                                                                /************* Scale Integration Functon *********************/
                                                                try {
                                                                    getScaleBarcodefunc(strValue, weightlength);
                                                                }
                                                                catch(Exception e){}
                                                            }
                                                        }
                                                        else {
                                                            if (edt_toolbar_search.getText().toString().equals("\n") || edt_toolbar_search.getText().toString().equals("")) {
                                                                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                                edt_toolbar_search.requestFocus();
                                                                edt_toolbar_search.selectAll();
                                                            } else {
                                                                edt_toolbar_search.setFocusable(true);
                                                                edt_toolbar_search.requestFocus();
                                                                edt_toolbar_search.selectAll();

                                                                String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                                arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                                if (arrayList.size() >= 1) {
                                                                    Item resultp = arrayList.get(0);
                                                                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                                    ck_project_type = lite_pos_registration.getproject_id();

                                                                    if (settings.get_Is_Stock_Manager().equals("false")) {

                                                                        String item_code = resultp.get_item_code();

                                                                        item_group_code = resultp.get_item_code();
                                                                        Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                                                                        ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                        int count = 0;
                                                                        boolean bFound = false;

                                                                        while (count < myCart.size()) {
                                                                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                                bFound = true;
                                                                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                                Globals.TotalQty = Globals.TotalQty + 1;
                                                                            }
                                                                            count = count + 1;
                                                                        }

                                                                        if (!bFound) {
                                                                            if (item_location == null) {
                                                                                sale_priceStr = "0";
                                                                                cost_priceStr = "0";
                                                                            } else {
                                                                                sale_priceStr = item_location.get_selling_price();
                                                                                cost_priceStr = item_location.get_cost_price();
                                                                            }
                                                                           // ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                            Double iTax = 0d;
                                                                            Double iTaxTotal = 0d;
                                                                            Double sprice=0d;
                                                                            if(item_group_taxArrayList.size()>0) {
                                                                                if(Globals.objLPR.getCountry_Id().equals("99")) {
                                                                                    if (contact != null) {
                                                                                        if (contact.getIs_taxable().equals("1")) {
                                                                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                                                                        iTax = 0d;
                                                                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                        if (tax_master != null) {
                                                                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                            } else {
                                                                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                            }
                                                                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                            Globals.order_item_tax.add(order_item_tax);
                                                                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                        }
                                                                                                    }
                                                                                                    Globals.Taxwith_state="1";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                                                                    if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                                                                        iTax = 0d;
                                                                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                        if (tax_master != null) {
                                                                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                            } else {
                                                                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                            }
                                                                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                            Globals.order_item_tax.add(order_item_tax);
                                                                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                        }
                                                                                                    }
                                                                                                    Globals.Taxdifferent_state="2";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            }
                                                                                        } else {
                                                                                            Globals.NoTax="0";
                                                                                            sprice = Double.parseDouble(sale_priceStr);
                                                                                        }
                                                                                    } else if (contact == null) {
                                                                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                                                                iTax = 0d;
                                                                                                //String tax_id = item_group_taxArrayList.get(i);
                                                                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                if (tax_master != null) {
                                                                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                    if (tax_master.get_tax_type().equals("P")) {
                                                                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                    } else {
                                                                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                    }
                                                                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                    Globals.order_item_tax.add(order_item_tax);
                                                                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                }
                                                                                            }
                                                                                            Globals.Taxwith_state="1";
                                                                                        }
                                                                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                    }
                                                                                }
                                                                                else if(Globals.objLPR.getCountry_Id().equals("114")){
                                                                                    if (contact != null) {
                                                                                        if (contact.getIs_taxable().equals("1")) {
                                                                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                                    iTax = 0d;
                                                                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                    if(tax_master!=null) {
                                                                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                        if (tax_master.get_tax_type().equals("P")) {
                                                                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                        } else {
                                                                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                        }
                                                                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                        Globals.order_item_tax.add(order_item_tax);
                                                                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                    }
                                                                                                    Globals.Taxwith_state="1";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                                    iTax = 0d;
                                                                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                    if(tax_master!=null) {
                                                                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                        if (tax_master.get_tax_type().equals("P")) {
                                                                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                        } else {
                                                                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                        }
                                                                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                        Globals.order_item_tax.add(order_item_tax);
                                                                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                    }
                                                                                                    Globals.Taxdifferent_state="2";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            }
                                                                                        } else {
                                                                                            Globals.NoTax="0";
                                                                                            sprice = Double.parseDouble(sale_priceStr);
                                                                                        }
                                                                                    } else if (contact == null) {
                                                                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                            iTax = 0d;
                                                                                            //String tax_id = item_group_taxArrayList.get(i);
                                                                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                            if(tax_master!=null) {
                                                                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                if (tax_master.get_tax_type().equals("P")) {
                                                                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                } else {
                                                                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                }
                                                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                Globals.order_item_tax.add(order_item_tax);
                                                                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                            }
                                                                                            Globals.Taxwith_state="1";
                                                                                        }
                                                                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                    }
                                                                                }
                                                                                else if(Globals.objLPR.getCountry_Id().equals("221")){
                                                                                    if (contact != null) {
                                                                                        if (contact.getIs_taxable().equals("1")) {
                                                                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                                        iTax = 0d;
                                                                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                    if(tax_master!=null) {
                                                                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                        if (tax_master.get_tax_type().equals("P")) {
                                                                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                        } else {
                                                                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                        }
                                                                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                        Globals.order_item_tax.add(order_item_tax);
                                                                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                    }
                                                                                                    Globals.Taxwith_state="1";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                                    iTax = 0d;
                                                                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                                    if(tax_master!=null) {
                                                                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                        if (tax_master.get_tax_type().equals("P")) {
                                                                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                        } else {
                                                                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                        }
                                                                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                        Globals.order_item_tax.add(order_item_tax);
                                                                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                                    }
                                                                                                    Globals.Taxdifferent_state="2";
                                                                                                }
                                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                            }
                                                                                        } else {
                                                                                            Globals.NoTax="0";
                                                                                            sprice = Double.parseDouble(sale_priceStr);
                                                                                        }
                                                                                    } else if (contact == null) {
                                                                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                                                            iTax = 0d;
                                                                                            //String tax_id = item_group_taxArrayList.get(i);
                                                                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                                                            if(tax_master!=null) {
                                                                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                                if (tax_master.get_tax_type().equals("P")) {
                                                                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                                } else {
                                                                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                                }
                                                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                                Globals.order_item_tax.add(order_item_tax);
                                                                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                                                            }
                                                                                            Globals.Taxwith_state="1";
                                                                                        }
                                                                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                    }
                                                                                }
                                                                            }
                                                                            else{
                                                                                sprice = Double.parseDouble(sale_priceStr);
                                                                            }
/*                                                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                                String tax_id = item_group_taxArrayList.get(i);
                                                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                if (tax_master.get_tax_type().equals("P")) {
                                                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                } else {
                                                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                }
                                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                Globals.order_item_tax.add(order_item_tax);
                                                                            }
                                                                            Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;*/
                                                                            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                                                                            Double beforetaxprice =sprice-iTaxTotal;
                                                                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                                                                        Globals.cart.add(cartItem);
                                                                            Globals.cart.add(0, cartItem);
                                                                            receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                                                                            if (receipemodifierlist.size() > 0) {

                                                                                Globals.SRNO = Globals.SRNO;
                                                                            } else {
                                                                                Globals.SRNO = Globals.SRNO + 1;
                                                                            }
                                                                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                            Globals.TotalItem = Globals.TotalItem + 1;
                                                                            Globals.TotalQty = Globals.TotalQty + 1;
                                                                        }
                                                                        Globals.cart = myCart;

                                                                        retail_list_load();
                                                                        String item_price;
                                                                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                        btn_retail_2.setText(item_price);
                                                                        btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                       // edt_toolbar_search.setFocusable(true);

                                                                        edt_toolbar_search.requestFocus();

                                                                        edt_toolbar_search.setText("");



                                                                    }
                                                                    //else
                                                                        /*{
                                                                        // Item resultp = arrayList.get(0);
                                                                        String item_code = resultp.get_item_code();

                                                                        item_group_code = resultp.get_item_code();
                                                                        Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                        ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                        int count = 0;
                                                                        boolean bFound = false;

                                                                        while (count < myCart.size()) {
                                                                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                                bFound = true;
                                                                                curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                                                                boolean result = stock_check(item_code, curQty + 1);
                                                                                if (result == true) {
                                                                                    myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                    Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                                    Globals.TotalQty = Globals.TotalQty + 1;
//                                                                            curQty = curQty+1;
                                                                                }

                                                                            }
                                                                            count = count + 1;
                                                                        }

                                                                        if (!bFound) {
                                                                            curQty = 0d;
                                                                            boolean result = stock_check(item_code, Double.parseDouble("1"));
                                                                            if (result == true) {
                                                                                if (item_location == null) {
                                                                                    sale_priceStr = "0";
                                                                                    cost_priceStr = "0";
                                                                                } else {
                                                                                    sale_priceStr = item_location.get_selling_price();
                                                                                    cost_priceStr = item_location.get_cost_price();
                                                                                }
                                                                                ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                                Double iTax = 0d;
                                                                                Double iTaxTotal = 0d;
                                                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                    iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                                    String tax_id = item_group_taxArrayList.get(i);
                                                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                    if (tax_master.get_tax_type().equals("P")) {
                                                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                    } else {
                                                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                    }
                                                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                    Globals.order_item_tax.add(order_item_tax);
                                                                                }
                                                                                Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                                Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                                                                                ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id());
//                                                                            Globals.cart.add(cartItem);
                                                                                Globals.cart.add(0, cartItem);
                                                                                receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                                                                                if (receipemodifierlist.size() > 0) {

                                                                                    Globals.SRNO = Globals.SRNO;
                                                                                } else {
                                                                                    Globals.SRNO = Globals.SRNO + 1;
                                                                                }
                                                                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                                Globals.TotalItem = Globals.TotalItem + 1;
                                                                                Globals.TotalQty = Globals.TotalQty + 1;
//                                                                        curQty = curQty+1;
                                                                            }
                                                                        }
                                                                        Globals.cart = myCart;

                                                                        retail_list_load();
                                                                        String item_price;
                                                                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                        btn_retail_2.setText(item_price);
                                                                        btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));

                                                                        edt_toolbar_search.requestFocus();
                                                                        edt_toolbar_search.setText("");




                                                                    }*/
                                                                    String itemcode = resultp.get_item_code();

                                                                    receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
                                                                    if (receipemodifierlist.size() > 0) {
                                                                        Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                                                                        i.putExtra("itemcode", resultp.get_item_code());
                                                                        i.putExtra("opr", Globals.Operation);
                                                                        i.putExtra("srno", Globals.SRNO);
                                                                        i.putExtra("odr_code", Globals.Order_Code);
                                                                        startActivity(i);
                                                                        // return;
                                                                    }
                                                                } else {
                                                                    edt_toolbar_search.requestFocus();
                                                                    edt_toolbar_search.selectAll();

                                                                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (edt_toolbar_search != null) {
                                                                    edt_toolbar_search.requestFocus();
                                                                }

                                                            }
                                                        }, 10);
                                                        return true;
                                                    }
                                                    return false;
                                                }
                                            }
        );
    }

    private void autocomplete(String strFilter) {
        strFilter = " and ( item.item_code Like '" + strFilter + "%'  Or item.item_name Like '%" + strFilter + "%' OR barcode Like '%" + strFilter + "%')";
        if (settings.get_Is_Zero_Stock().equals("true")) {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), " WHERE is_active = '1' and  is_modifier != '1' " + strFilter + " limit 10");
        } else {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1' " + strFilter + "  and item_location.quantity!='0' and item.is_modifier != '1'  Order By lower(item_name) asc limit 10");
        }

        if (arrayList1.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, R.layout.items_spinner, arrayList1);
            edt_toolbar_retail.setThreshold(0);
            edt_toolbar_retail.setAdapter(adapter);
        }
    }
    public void getAlertforDemoDB () {


        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.areyousure);

        //Setting message manually and performing action on button click
        builder.setMessage(R.string.demomessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(isNetworkStatusAvialable(getApplicationContext())) {
                            getDemoDatabse();
                        }
                        else{
                            builder1 = new AlertDialog.Builder(Retail_IndustryActivity.this);
                            builder1.setTitle(R.string.nointernet);

                            //Setting message manually and performing action on button click
                            builder1.setMessage(R.string.demomenetoff)
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            getDemoDatabse();


                                        }
                                    });
                            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button

                                    dialog.cancel();

                                }
                            });
                            //Creating dialog box
                            AlertDialog alert = builder1.create();
                            //Setting the title manually
                            alert.setTitle(R.string.noonternet);
                            alert.show();
                        }

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();

            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.areyousure);
        alert.show();

    }
    private void retail_list_load() {
        ArrayList<ShoppingCart> myCart = Globals.cart;
//        Collections.reverse(myCart);
        ListView retail_list = (ListView) findViewById(R.id.retail_list);
        if (myCart.size() > 0) {
            retailListAdapter = new RetailListAdapter(Retail_IndustryActivity.this, myCart, opr, strOrderCode, "");
            retail_list.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
            retail_list.setAdapter(retailListAdapter);
            retailListAdapter.notifyDataSetChanged();
        } else {
            retail_list.setVisibility(View.GONE);
            txt_title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retail_industry, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
      /*  if (id == R.id.action_settings) {
            if (edt_toolbar_retail.getText().toString().equals("\n") || edt_toolbar_retail.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                edt_toolbar_retail.requestFocus();
            } else {
                try {
                    AddToCart();
                }
                catch(Exception e){
System.out.println(e.getMessage());
                }
            }
            return true;
        }

         if (id == R.id.action_qr) {
            Globals.BarcodeReslt = "";
            flag_scan = true;
             try {
                 if(mScannerView!=null) {
                     mScannerView = new ZBarScannerView(Retail_IndustryActivity.this);
                     mScannerView.setResultHandler(Retail_IndustryActivity.this);
                     mScannerView.startCamera(); // Programmatically initialize the scanner view
                     setContentView(mScannerView);
                 }
             }
             catch(Exception e){

             }

            return true;
        }*/
        if (id == R.id.action_openRight) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void AddToCart() {
         String strValue = edt_toolbar_retail.getText().toString().trim();
        contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + Globals.strContact_Code + "'");

        int barcodelen = Integer.parseInt(barcodelength);

        if(strValue.length()==barcodelen){

            String pllen=strValue.substring(0,Integer.parseInt(scalesetup.getPlu_len()));
            String plengthfinal=strValue.substring(pllen.length());
            if(pllen.equals(scalesetup.getPlu_value())){
                // String itemlen= strValue.substring(3,7);

                String itemlen= plengthfinal.substring(0,Integer.parseInt(scalesetup.getITEM_CODE_LEN()));
                String itemcodefinal= plengthfinal.substring(itemlen.length());

                weightlength= itemcodefinal.substring(0,Integer.parseInt(scalesetup.getWp_LEN()));
                String wightlenfinal= weightlength.substring(weightlength.length());
                // String weight=strValue.substring(itemlen.length(),itemlen.length()-1);

                /************* Scale Integration Functon *********************/

                try {
                    getScaleBarcodefunc(strValue, weightlength);
                }
                catch(Exception e)
                {
                  Toast.makeText(Retail_IndustryActivity.this, ""+ e,Toast.LENGTH_LONG).show();
                }
            }
            else {
                String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                if (arrayList.size() >= 1) {
                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    ck_project_type = lite_pos_registration.getproject_id();
                    Item resultp = arrayList.get(0);
                    if (settings.get_Is_Stock_Manager().equals("false")) {
                        // Item resultp = arrayList.get(0);
                        String item_code = resultp.get_item_code();
                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                        Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                        ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {
                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                Globals.TotalQty = Globals.TotalQty + 1;

                            }
                            count = count + 1;
                        }

                        if (!bFound) {

                            if (item_location == null) {
                                sale_priceStr = "0";
                                cost_priceStr = "0";
                            } else {
                                sale_priceStr = item_location.get_selling_price();
                                cost_priceStr = item_location.get_cost_price();
                            }
                            //ArrayList<String> item_group_taxArrayList = calculateTax();
                            Double iTax = 0d;
                            Double iTaxTotal = 0d;
                            Double sprice=0d;
                            if(item_group_taxArrayList.size()>0) {
                                if(Globals.objLPR.getCountry_Id().equals("99")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        if(tax_master!=null) {
                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                            } else {
                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                            }
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                        }
                                                    }
                                                    Globals.Taxwith_state="1";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        if (tax_master != null) {
                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                            } else {
                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                            }
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                        }
                                                    }
                                                    Globals.Taxdifferent_state="2";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            Globals.NoTax="0";
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                                else if(Globals.objLPR.getCountry_Id().equals("114")){
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if(tax_master!=null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                    Globals.Taxwith_state="1";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if(tax_master!=null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                    Globals.Taxdifferent_state="2";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            Globals.NoTax="0";
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                                else if(Globals.objLPR.getCountry_Id().equals("221")){
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if(tax_master!=null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                    Globals.Taxwith_state="1";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if(tax_master!=null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                    Globals.Taxdifferent_state="2";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            Globals.NoTax="0";
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                            else{
                                sprice = Double.parseDouble(sale_priceStr);
                            }
                            Double beforetaxprice=sprice-iTaxTotal;
                            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                    Globals.cart.add(cartItem);
                            Globals.cart.add(0, cartItem);
                            receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                            if (receipemodifierlist.size() > 0) {

                                Globals.SRNO = Globals.SRNO;
                            } else {
                                Globals.SRNO = Globals.SRNO + 1;
                            }
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                            Globals.TotalItem = Globals.TotalItem + 1;
                            Globals.TotalQty = Globals.TotalQty + 1;
                        }
                        Globals.cart = myCart;
                        retail_list_load();
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        btn_retail_2.setText(item_price);
                        btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                        edt_toolbar_retail.setText("");
                    } else {

                        String item_code = resultp.get_item_code();
                        item_group_code = resultp.get_item_code();
                        Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                        ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {
                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                boolean result = stock_check(item_code, curQty + 1);
                                if (result == true) {
                                    myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                    //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                    Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                    Globals.TotalQty = Globals.TotalQty + 1;
                                }
                            }
                            count = count + 1;
                        }

                        if (!bFound) {
                            curQty = 0d;
                            boolean result = stock_check(item_code, Double.parseDouble("1"));
                            if (result == true) {
                                if (item_location == null) {
                                    sale_priceStr = "0";
                                    cost_priceStr = "0";
                                } else {
                                    sale_priceStr = item_location.get_selling_price();
                                    cost_priceStr = item_location.get_cost_price();
                                }
                                //  ArrayList<String> item_group_taxArrayList = calculateTax();
                                Double iTax = 0d;
                                Double iTaxTotal = 0d;
                                Double sprice=0d;
                                if(item_group_taxArrayList.size()>0) {
                                    if(Globals.objLPR.getCountry_Id().equals("99")) {
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                            iTax = 0d;
                                                            //String tax_id = item_group_taxArrayList.get(i);
                                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                            } else {
                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                            }
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                        }
                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                        if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                            iTax = 0d;
                                                            //String tax_id = item_group_taxArrayList.get(i);
                                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                            } else {
                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                            }
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                        }
                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                }
                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr);
                                            }
                                        } else if (contact == null) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    }
                                    else if(Globals.objLPR.getCountry_Id().equals("114")){
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                }
                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr);
                                            }
                                        } else if (contact == null) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    }
                                    else if(Globals.objLPR.getCountry_Id().equals("221")){
                                        if (contact != null) {
                                            if (contact.getIs_taxable().equals("1")) {
                                                if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                    }
                                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                }
                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr);
                                            }
                                        } else if (contact == null) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    }
                                }
                                else{
                                    sprice = Double.parseDouble(sale_priceStr);
                                }

                                Double beforetaxprice=sprice-iTaxTotal;
                                Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                                ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "", "",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                        Globals.cart.add(cartItem);
                                Globals.cart.add(0, cartItem);
                                Globals.SRNO = Globals.SRNO + 1;
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                Globals.TotalItem = Globals.TotalItem + 1;
                                Globals.TotalQty = Globals.TotalQty + 1;
//                    curQty = curQty+1;
                            }
                        }
                        Globals.cart = myCart;
                        retail_list_load();
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        btn_retail_2.setText(item_price);
                        btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                        edt_toolbar_retail.setText("");
                    }
                    String itemcode = resultp.get_item_code();

                    receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
                    if (receipemodifierlist.size() > 0) {
                        Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                        i.putExtra("itemcode", resultp.get_item_code());
                        i.putExtra("opr", Globals.Operation);
                        i.putExtra("srno", Globals.SRNO);
                        i.putExtra("odr_code", Globals.Order_Code);
                        startActivity(i);
                        // return;
                    }
                } else {
                    edt_toolbar_retail.requestFocus();
                    edt_toolbar_retail.selectAll();
                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }

        }
        else {
            String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
            arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
            if (arrayList.size() >= 1) {
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                ck_project_type = lite_pos_registration.getproject_id();
                Item resultp = arrayList.get(0);
                if (settings.get_Is_Stock_Manager().equals("false")) {
                    // Item resultp = arrayList.get(0);
                    String item_code = resultp.get_item_code();
                    item_group_code = resultp.get_item_code();
                    item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                    ArrayList<ShoppingCart> myCart = Globals.cart;
                    int count = 0;
                    boolean bFound = false;
                    while (count < myCart.size()) {
                        if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                            bFound = true;
                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                            Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                            Globals.TotalQty = Globals.TotalQty + 1;

                        }
                        count = count + 1;
                    }

                    if (!bFound) {

                        if (item_location == null) {
                            sale_priceStr = "0";
                            cost_priceStr = "0";
                        } else {
                            sale_priceStr = item_location.get_selling_price();
                            cost_priceStr = item_location.get_cost_price();
                        }
                        //ArrayList<String> item_group_taxArrayList = calculateTax();
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        Double sprice=0d;
                        if(item_group_taxArrayList.size()>0) {
                            if(Globals.objLPR.getCountry_Id().equals("99")) {
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                   if(tax_master!=null) {
                                                       Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                       if (tax_master.get_tax_type().equals("P")) {
                                                           iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                       } else {
                                                           iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                       }
                                                       iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                       Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                       Globals.order_item_tax.add(order_item_tax);
                                                       Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                   }
                                                }
                                                Globals.Taxwith_state="1";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                }
                                                Globals.Taxdifferent_state="2";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    } else {
                                        Globals.NoTax="0";
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        Globals.Taxwith_state="1";
                                    }
                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                }
                            }
                            else if(Globals.objLPR.getCountry_Id().equals("114")){
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if(tax_master!=null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                                Globals.Taxwith_state="1";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if(tax_master!=null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                                Globals.Taxdifferent_state="2";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    } else {
                                        Globals.NoTax="0";
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if(tax_master!=null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                        Globals.Taxwith_state="1";
                                    }
                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                }
                            }
                            else if(Globals.objLPR.getCountry_Id().equals("221")){
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if(tax_master!=null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                                Globals.Taxwith_state="1";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if(tax_master!=null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                                Globals.Taxdifferent_state="2";
                                            }
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                    } else {
                                        Globals.NoTax="0";
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if(tax_master!=null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                        Globals.Taxwith_state="1";
                                    }
                                    sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                }
                            }
                        }
                        else{
                            sprice = Double.parseDouble(sale_priceStr);
                        }
                        Double beforetaxprice=sprice-iTaxTotal;
                        Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                    Globals.cart.add(cartItem);
                        Globals.cart.add(0, cartItem);
                        receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                        if (receipemodifierlist.size() > 0) {

                            Globals.SRNO = Globals.SRNO;
                        } else {
                            Globals.SRNO = Globals.SRNO + 1;
                        }
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                        Globals.TotalItem = Globals.TotalItem + 1;
                        Globals.TotalQty = Globals.TotalQty + 1;
                    }
                    Globals.cart = myCart;
                    retail_list_load();
                    String item_price;
                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                    btn_retail_2.setText(item_price);
                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                    edt_toolbar_retail.setText("");
                } else {

                    String item_code = resultp.get_item_code();
                    item_group_code = resultp.get_item_code();
                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                    ArrayList<ShoppingCart> myCart = Globals.cart;
                    int count = 0;
                    boolean bFound = false;
                    while (count < myCart.size()) {
                        if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                            bFound = true;
                            curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                            boolean result = stock_check(item_code, curQty + 1);
                            if (result == true) {
                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                Globals.TotalQty = Globals.TotalQty + 1;
                            }
                        }
                        count = count + 1;
                    }

                    if (!bFound) {
                        curQty = 0d;
                        boolean result = stock_check(item_code, Double.parseDouble("1"));
                        if (result == true) {
                            if (item_location == null) {
                                sale_priceStr = "0";
                                cost_priceStr = "0";
                            } else {
                                sale_priceStr = item_location.get_selling_price();
                                cost_priceStr = item_location.get_cost_price();
                            }
                            //  ArrayList<String> item_group_taxArrayList = calculateTax();
                            Double iTax = 0d;
                            Double iTaxTotal = 0d;
                            Double sprice=0d;
                            if(item_group_taxArrayList.size()>0) {
                                if(Globals.objLPR.getCountry_Id().equals("99")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                                else if(Globals.objLPR.getCountry_Id().equals("114")){
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                        iTax = 0d;
                                                        //String tax_id = item_group_taxArrayList.get(i);
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                                else if(Globals.objLPR.getCountry_Id().equals("221")){
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                     Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);

                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                            else{
                                sprice = Double.parseDouble(sale_priceStr);
                            }

                            Double beforetaxprice=sprice-iTaxTotal;
                            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "", "",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                        Globals.cart.add(cartItem);
                            Globals.cart.add(0, cartItem);
                            Globals.SRNO = Globals.SRNO + 1;
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                            Globals.TotalItem = Globals.TotalItem + 1;
                            Globals.TotalQty = Globals.TotalQty + 1;
//                    curQty = curQty+1;
                        }
                    }
                    Globals.cart = myCart;
                    retail_list_load();
                    String item_price;
                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                    btn_retail_2.setText(item_price);
                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                    edt_toolbar_retail.setText("");
                }
                String itemcode = resultp.get_item_code();

                receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
                if (receipemodifierlist.size() > 0) {
                    Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                    i.putExtra("itemcode", resultp.get_item_code());
                    i.putExtra("opr", Globals.Operation);
                    i.putExtra("srno", Globals.SRNO);
                    i.putExtra("odr_code", Globals.Order_Code);
                    startActivity(i);
                    // return;
                }
            } else {
                edt_toolbar_retail.requestFocus();
                edt_toolbar_retail.selectAll();
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void save_order(String strRemarks) {
        String strFlag1 = "0";
        String strOrdeeStatus = "OPEN";
        String strOrderNo = "";
        Order_Detail objOrderDetail;
        Orders objOrder;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        database.beginTransaction();

        if (opr.equals("Edit")) {
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            String strDis = Globals.Inv_Discount + "";

            if (strDis.equals("")) {
                strDis = "0";
            }
            objOrder = Orders.getOrders(getApplicationContext(), database, "  WHERE order_code = '" + strOrderCode + "'");
            objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderCode, objOrder.get_order_date(), Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, objOrder.get_remarks(), objOrder.get_table_code(), "",null);
            long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderCode, objOrder.get_order_id()}, database);
            if (l > 0) {
                strFlag1 = "1";
                long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderCode}, database);

                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    Double cartsales_price= Double.parseDouble(mCart.get_Sales_Price());
                    Double discountedvalue= Globals.DiscountPer;
                    double finalDis =  cartsales_price * (discountedvalue / 100.0);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderCode,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", String.valueOf(finalDis), mCart.get_Line_Total(), "0","false",mCart.getUnitId(),mCart.getBeforeTaxPrice());
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
                long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderCode}, database);
                long p = Order_Payment.delete_order_payment(getApplicationContext(), "order_payment", " order_code =? ", new String[]{strOrderCode}, database);
                ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                Order_Detail_Tax objOrderDetailTax;
                long l1 = Order_Detail_Tax.delete_Order_Detail_Tax(getApplicationContext(), "order_detail_tax", " order_code =? ", new String[]{strOrderCode}, database);

                for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                    Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                    objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderCode, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                            , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());
                    long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {

                    }
                }
            }
        } else {
            Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");

            if (objOrder1 == null) {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + 1;
            } else {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
            }
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            String strDis = Globals.Inv_Discount + "";

            if (strDis.equals("")) {
                strDis = "0";
            }
            objOrder = new Orders(getApplicationContext(), null, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, strRemarks, Globals.strTable_Code, "",null);
            long l = objOrder.insertOrders(database);
            if (l > 0) {
                strFlag1 = "1";
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    Double cartsales_price= Double.parseDouble(mCart.get_Sales_Price());
                    Double discountedvalue= Globals.DiscountPer;
                    double finalDis =  cartsales_price * (discountedvalue / 100.0);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0",  String.valueOf(finalDis), mCart.get_Line_Total(), "0",mCart.getKitchenprintflag(),mCart.getUnitId(),mCart.getBeforeTaxPrice());
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
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
                        strFlag1 = "1";
                    } else {
                    }
                }
            }
        }


        if (strFlag1.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                /*Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                order.execute();*/
            }


            if(!opr.equals("Edit")) {
                strOrderCode = strOrderNo;
            }
            opr = "Add";
            Globals.Operation = opr;
            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(itemPrice);



            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
            if(!Globals.objsettings.getIs_KitchenPrint().equals("true")){
                Globals.setEmpty();
                try {

                        Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
                        startActivity(intent);
                        finish();

                               /* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*/
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }


/*            if(opr.equals("Edit")){
                            if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
               // pDialog.dismiss();
               // listDialog2.dismiss();

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage(getString(R.string.waiting));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        printKOT(strOrderCode,progressDialog);

                    }
                };t.start();
             *//*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*//*



            }
            }*/
        }
       /* if (strFlag1.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                order.execute();
            }
            if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
                strOrderCode=strOrderNo;
               PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();


            }

                Globals.setEmpty1();
                opr = "Add";
                Globals.Operation = opr;
                strOrderCode = "";

            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(itemPrice);
            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();

            if(Globals.objLPR.getIndustry_Type().equals("1")) {
                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(Retail_IndustryActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Retail_IndustryActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else if(Globals.objLPR.getIndustry_Type().equals("2")){
                Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
                startActivity(intent);
                finish();
            }

        }*/
    }

    class Sendorder_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        MainActivity activity;

        public Sendorder_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Orders order=new Orders(getApplicationContext());

                order.sendOn_Server(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",Globals.objLPD.getLic_customer_license_id(),serial_no,android_id,myKey);

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
    private void printKOT(String strOrderNo,ProgressDialog pdialog) {
        boolean flag = false;
        getIP();
        try {


            for (int i = 0; i < ipAdd.size(); i++) {
                ip = ipAdd.get(i).toString();

                try {
                    final Orders orders = Orders.getOrders(Retail_IndustryActivity.this, database, "WHERE order_code = '" +strOrderNo + "'");
                    final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(Retail_IndustryActivity.this, "WHERE order_code = '" + strOrderNo + "'",database);

                    performOperationEn(ipAdd.get(i), 0, "Order",orders,order_detail1,pdialog);
                    performOperationEn(ipAdd.get(i), 0, "Void",orders,order_detail1,pdialog);

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
    private void performOperationEn(String ip, int iOperation, String strMode,Orders orders,Order_Detail order_detail,ProgressDialog pDialog) {
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
                            Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getItem_code() + "'",database);

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


    // Contact Display Dialog
    private void showdialogContact() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog1 = new Dialog(this);
        listDialog1.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);
        final ListView list11 = (ListView) listDialog1.findViewById(R.id.lv_custom_ortype);
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        edt_srch_contct.setMaxLines(1);
        edt_srch_contct.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_srch_contct.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edt_srch_contct.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String strFiltr = edt_srch_contct.getText().toString().trim();
                    strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                    edt_srch_contct.selectAll();
                    fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                    return true;

                }
                return false;
            }
        });
        srch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);

            }
        });

        img_brs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listDialog1.dismiss();
                    Globals.BarcodeReslt = "";
                    mScannerView.startCamera();
                    // Programmatically initialize the scanner view
                    setContentView(mScannerView);
                } catch (Exception ex) {
                }

            }
        });

    }
    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "and business_group_code='" + str + "'";
        }

        if (Globals.objsettings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {

            if ((Globals.Taxwith_state.equals("")) && (Globals.NoTax.equals("")) && (Globals.Taxdifferent_state.equals(""))) {
                contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
            } else {
                if (Globals.cart.size() > 0) {
                    if (Globals.Taxwith_state.equals("1")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='1' And contact.zone_id = '"+Globals.objLPR.getZone_Id()+"' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    } else if (Globals.NoTax.equals("0")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='0' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    } else if (Globals.Taxdifferent_state.equals("2")) {
                        contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1' AND contact.is_taxable='1' And contact.zone_id != '"+Globals.objLPR.getZone_Id()+"' AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                    }
                }
                else{
                    contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
                }
            }
        }

        dialogContactMainListAdapter = new DialogContactMainListAdapter(Retail_IndustryActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);

    }

    public void change_customer_icon(String contactcode) {
        bottomNavigationView  = (BottomNavigationView) findViewById(R.id.retail_bottom_navigation);
        Menu menu2 = bottomNavigationView.getMenu();
        MenuItem cusIcon = menu2.findItem(R.id.action_contact);
        Globals.strContact_Code=contactcode;
        if (Globals.strContact_Code.equals("") && Globals.strResvContact_Code.equals("")) {
            //if (Globals.strContact_Code.equals("") ) {
            bottomNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.contact1);
        } else {
            bottomNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.green);
        }
    }
    private void showdialogremarksdialog() {
        listDialog2.setTitle(R.string.Remarks);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
        listDialog2.show();
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog2.dismiss();
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_remark.getText().toString().equals("")) {
                } else {
                    strRemarks = edt_remark.getText().toString();
                }
                save_order(strRemarks);
                if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
                    pDialog.dismiss();
                    listDialog2.dismiss();

                    progressDialog = new ProgressDialog(Retail_IndustryActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            printKOT(strOrderCode,progressDialog);
                            progressDialog.dismiss();
                            Globals.setEmpty();
                            try {
                                Intent i = new Intent(getApplicationContext(),Retail_IndustryActivity.class);
                                startActivity(i);
                               /* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*/
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }

                        }
                    };t.start();
             /*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*/



                }
                else{
                    listDialog2.dismiss();
                }
            }
        });
    }
    private Boolean stock_check(String item_code, Double curQty) {
        boolean flag = false;
        try {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
//        Order_Detail order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
//        Double total_qty = Double.parseDouble(order_detail.get_quantity()) + curQty;
            if (avl_stock >= curQty) {
                flag = true;
            } else {
                Toast.makeText(getApplicationContext(), "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Item not found in this location", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    public boolean Order_Total_Validation() {
        boolean resOrder = false;
        Double GTotal = 0d;
        Double DetailTotal = 0d;

        GTotal = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check));

        for (int count = 0; count < Globals.cart.size(); count++) {
            ShoppingCart mCart = Globals.cart.get(count);
            DetailTotal = DetailTotal + Double.parseDouble(mCart.get_Line_Total());
        }
        if (Globals.TotalItemPrice - DetailTotal == 0) {
            if (GTotal - DetailTotal == 0) {
                resOrder = true;
            } else {
                Globals.TotalItemPrice = DetailTotal;
            }
        }
        return resOrder;

    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        //Prints the scan format (qrcode, pdf417 etc.)
        Globals.BarcodeReslt = rawResult.getContents();
        mScannerView.stopCameraPreview(); //stopPreview
        mScannerView.stopCamera();
        //flag_scan=false;
        removeView(mScannerView);
        Globals.BarcodeReslt = "";
        if (settings.get_QR_Type().equals("0")) {
            flag_scan = false;
        } else {
            if (flag_scan == true) {
                mScannerView.startCamera(); // Programmatically initialize the scanner view
                setContentView(mScannerView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void setReloadctivity(ProgressDialog pDialog) {

        Thread timerThread = new Thread() {
            public void run() {

                try {

                    finish();
                    Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
                    startActivity(intent);

                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pDialog.dismiss();

                            Toast.makeText(getApplicationContext(), getString(R.string.Data_sync_succ), Toast.LENGTH_SHORT).show();
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }


        };


        timerThread.start();
    }
    public void load_cart() {
        if (edt_toolbar_retail.getText().toString().equals("\n") || edt_toolbar_retail.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();

                edt_toolbar_retail.requestFocus();

        } else {
            try {
                AddToCart();
            }
            catch(Exception e){

            }
        }
    }
    public void getDemoDatabse() {

        //  database.beginTransaction();

        // tax_master = new Tax_Master(getApplicationContext(), null, Globals.objLPD.getLocation_Code(), "Tax1"  , "P", "2.5", comment, "1", modified_by, date, "N");



        progressDialog = new ProgressDialog(Retail_IndustryActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.Syncdatademo));
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        sleep(2000);


                        if (Globals.objLPR.getIndustry_Type().equals("3")) {

                            try {
                                read_contact();
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            if (Globals.objLPR.getCountry_Id().equals("99")) {
                                try {
                                    read_tax();
                                } catch (Exception e) {
                                }
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            } else if (Globals.objLPR.getCountry_Id().equals("114")) {
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            } else if (Globals.objLPR.getCountry_Id().equals("221")) {

                                try {
                                    read_tax();
                                } catch (Exception e) {
                                }
                                try {
                                    read_item();
                                } catch (Exception e) {

                                }
                                try {
                                    read_itemgroup();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                try {
                                    read_contact();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }


                                //   progressDialog.dismiss();

                            }
                        }

                        ArrayList<String> arrayListGetFileImage = new ArrayList<>();
                        arrayListGetFileImage.add("2001.png");
                        arrayListGetFileImage.add("2002.png");
                        arrayListGetFileImage.add("2003.png");
                        arrayListGetFileImage.add("2004.png");
                        arrayListGetFileImage.add("2005.png");
                        arrayListGetFileImage.add("2006.png");
                        arrayListGetFileImage.add("2007.png");
                        arrayListGetFileImage.add("2008.png");
                        arrayListGetFileImage.add("2009.png");
                        arrayListGetFileImage.add("2010.png");
                        arrayListGetFileImage.add("2011.png");
                        arrayListGetFileImage.add("2012.png");
                        arrayListGetFileImage.add("2013.png");
                        arrayListGetFileImage.add("2014.png");
                        arrayListGetFileImage.add("2015.jpg");
                        arrayListGetFileImage.add("2016.jpg");
                        arrayListGetFileImage.add("2017.jpg");
                        arrayListGetFileImage.add("2018.jpg");
                        arrayListGetFileImage.add("2019.jpg");
                        arrayListGetFileImage.add("2020.jpg");
                        arrayListGetFileImage.add("2021.jpg");
                        arrayListGetFileImage.add("2022.jpg");
                        arrayListGetFileImage.add("2023.jpg");
                        arrayListGetFileImage.add("2024.jpg");
                        arrayListGetFileImage.add("2025.jpg");
                        arrayListGetFileImage.add("2027.jpg");
                        arrayListGetFileImage.add("2028.jpg");
                        arrayListGetFileImage.add("2029.jpg");
                        arrayListGetFileImage.add("2030.jpg");
                        arrayListGetFileImage.add("2031.jpg");
                        for (int i = 0; i < arrayListGetFileImage.size(); i++) {
                            //DownloadFileFromURL(imageURL, arrayListGetFileImage.get(i));
                            downloadImage(Globals.imageURL + arrayListGetFileImage.get(i), arrayListGetFileImage.get(i));
                            // DownloadImageFile(imageURL , arrayListGetFileImage.get(i));
                            Globals.arrayListGetFile_Image = arrayListGetFileImage;
                        }
                        progressDialog.dismiss();


                    } catch(Exception e){

                        System.out.println(e.getMessage());
                    }

                }

                catch (Exception e) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Globals.objLPR.getIndustry_Type().equals("3")) {
                            Toast.makeText(getApplicationContext(),"Demo Database Sync Sucessfully",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(Retail_IndustryActivity.this, PaymentCollection_MainScreen.class);
                            startActivity(intent);
                        }
                       else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                            Toast.makeText(getApplicationContext(),"Demo Database Sync Sucessfully",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
                            startActivity(intent);
                        }

                        else{
                            Toast.makeText(getApplicationContext(),"Demo Database Sync Sucessfully",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(Retail_IndustryActivity.this, MainActivity.class);
                            startActivity(intent);
                        }


                    }
                });
            }

        };
        t.start();


    }
    private void read_tax() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.tax)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.tax_dubai)));

            }
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
           /*     try {
                    long l = Country.delete_country(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }*/

                JSONObject jsonObject_tax = new JSONObject(sb.toString());

                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");

                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);

                    tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "N");
                    long l = tax_master.insertTax_Master(database);
                    //  JSONObject jsonObject_country1 = jsonArray_country.getJSONObject(i);

                    JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                    for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                        JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                        order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                        long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                        if (odrtx > 0) {
                            // succ_manu = "1";
                        } else {
                        }
                    }

                    JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                    for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                        JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                        tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                        long odrtx1 = tax_detail.insertTax_Detail(database);

                        if (odrtx1 > 0) {
                            //  succ_manu = "1";
                        } else {
                        }
                    }

                    JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                    for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                        JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                        Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
                        long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);

                        if (odrtx1 > 0) {
                            //succ_manu = "1";
                        } else {
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void read_item() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_kuwait)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_dubai)));

            }
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                JSONObject jsonObject_item = new JSONObject(sb.toString());

                JSONArray jsonArray_bg = jsonObject_item.getJSONArray("result");
                ArrayList<Item> itlist = new ArrayList<Item>();
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    }

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        //path = getPath(context, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {
                        // itlist.add(new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null));
                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            // succ_bg = "1";
                        } else {
                        }
                        /*try {
                            item.add_item(itlist, database);
                        } catch (Exception e) {

                        }*/
                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.insertItem_Location(database);

                            if (itmlc > 0) {
                                //succ_bg = "1";

                            } else {
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                // succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");

                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);
                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                //succ_bg = "1";
                            } else {
                            }
                        }

                    }

                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
    }



    private void read_itemgroup() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.item_group)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.itemgroup_kuwait)));

            }
            else if(Globals.objLPR.getCountry_Id().equals("221")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.itemgroup_dubai)));

            }
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                JSONObject jsonObject_itemgroup = new JSONObject(sb.toString());

                JSONArray jsonArray_bg = jsonObject_itemgroup.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item_group'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    }

                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "N",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            // succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            //succ_bg = "1";
                        } else {
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private void read_contact() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact)));
            }
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact_kuwait)));

            }
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.contact_dubai)));

            }
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                JSONObject jsonObject_contact = new JSONObject(sb.toString());

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");
                    Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    }


                    if (contact == null) {
                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "N", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            //succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new org.phomellolitepos.database.Address(getApplicationContext(), null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                long itmsp = address.insertAddress(database);

                                if (itmsp > 0) {
                                    // succ_bg = "1";

                                } else {
                                    // succ_bg = "0";
                                }


                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);
                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    // succ_bg = "1";

                                } else {
                                    //  succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");

                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    // succ_bg = "1";
                                } else {
                                    //  succ_bg = "0";
                                }
                            }
                        } else {
                            //succ_bg = "0";
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void downloadImage(String fUrl,String file_name){
        try{
            URL url = new URL(fUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File root = new File(Globals.folder+"ItemImages/");
            if (!root.exists()) {
                root.mkdirs();
            }
            String filename=file_name;
            Log.i("Local filename:",""+filename);
            File file = new File(root,filename);
  /*  if(file.createNewFile())
    {
        file.createNewFile();
    }*/
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 )
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
            }
            fileOutput.close();
            if(downloadedSize==totalSize) fUrl=file.getPath();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            fUrl=null;
            e.printStackTrace();
        }
        Log.i("filepath:"," "+fUrl) ;
    }
    public void removeView(View view) {
        ViewGroup vg = (ViewGroup) (view.getParent());
        vg.removeView(view);

        main_view();

        edt_toolbar_retail.setText(Globals.BarcodeReslt + "");
        edt_toolbar_retail.selectAll();

        load_cart();
    }

    public void openWhatsApp(){
        try {
            String text = "Hello Sir.\n I am Using Trigger POS Application Version "+ versionname+"\n\n Company Name : "+ Globals.objLPR.getCompany_Name()+"\n User name: "+ Globals.objLPR.getContact_Person()+"\n Reg. Code :"+ Globals.objLPR.getRegistration_Code()+"\n License Mode :"+ Globals.objLPR.getproject_id()+"\n Expiry Date :"+ javaEncryption.decrypt(Globals.objLPD.getExpiry_Date(), "12345678") +"\n";
            ;
            String toNumber="";
            //India
            if(Globals.objLPR.getCountry_Id().equals("99")) {

                toNumber = "919530047775"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            }
            // Kuwait
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                toNumber = "96569029773";
            }
            // UAE
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                toNumber = "971558838749";
            }

            else {
                toNumber = "919530047775";
            }


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getScaleBarcodefunc(String strValue,String weight){
        String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
        arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
        contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + Globals.strContact_Code + "'");

        if (arrayList.size() >= 1) {
            Item resultp = arrayList.get(0);
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            ck_project_type = lite_pos_registration.getproject_id();

            if (settings.get_Is_Stock_Manager().equals("false")) {

                String item_code = resultp.get_item_code();

                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                ArrayList<ShoppingCart> myCart = Globals.cart;
                int count = 0;
                boolean bFound = false;

                while (count < myCart.size()) {
                    if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                        bFound = true;
                        myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                        myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                        Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                        Globals.TotalQty = Globals.TotalQty + 1;
                    }
                    count = count + 1;
                }

                if (!bFound) {
                    if (item_location == null) {
                        sale_priceStr = "0";
                        cost_priceStr = "0";
                    } else {

                        sale_priceStr = item_location.get_selling_price();
                        cost_priceStr = item_location.get_cost_price();
                    }
                    // ArrayList<String> item_group_taxArrayList = calculateTax();
                    Double iTax = 0d;
                    Double iTaxTotal = 0d;
                    Double sprice=0d;
                    if(item_group_taxArrayList.size()>0) {
                        if(Globals.objLPR.getCountry_Id().equals("99")) {
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            Globals.Taxdifferent_state="2";

                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    Globals.NoTax="0";
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if (tax_master != null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                    }
                                    Globals.Taxwith_state="1";
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                            }
                        }
                        else if(Globals.objLPR.getCountry_Id().equals("114")){
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;

                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxdifferent_state="2";
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    Globals.NoTax="0";
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                    iTax = 0d;
                                    //String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                    if(tax_master!=null) {
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                    }
                                    Globals.Taxwith_state="1";
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                            }
                        }
                        else if(Globals.objLPR.getCountry_Id().equals("221")){
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxwith_state="1";
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                            Globals.Taxdifferent_state="2";
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    Globals.NoTax="0";
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                    iTax = 0d;
                                    //String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                    if(tax_master!=null) {
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                    }
                                    Globals.Taxwith_state="1";
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;

                            }
                        }
                    }
                    else{
                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                    }
                    // if(scalesetup.getWp_LEN().equals())
                    Dweightvalue=Double.parseDouble(weight)/1000;
                    String qty= Globals.myNumberFormat2QtyDecimal(Dweightvalue,qty_decimal_check);
                    Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");
                  Double beforetaxprice= sprice-iTaxTotal;
                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), qty, cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * sprice) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");

                    Globals.cart.add(0, cartItem);
                    receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                    if (receipemodifierlist.size() > 0) {

                        Globals.SRNO = Globals.SRNO;
                    } else {
                        Globals.SRNO = Globals.SRNO + 1;
                    }
                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                    Globals.TotalItem = Globals.TotalItem + 1;
                    Globals.TotalQty = Globals.TotalQty+Double.parseDouble(qty);
                }
                Globals.cart = myCart;
                retail_list_load();
                String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_retail_2.setText(item_price);
                btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                edt_toolbar_search.requestFocus();
                edt_toolbar_search.setText("");

                edt_toolbar_search.selectAll();

            } else {
                // Item resultp = arrayList.get(0);
                String item_code = resultp.get_item_code();

                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                ArrayList<ShoppingCart> myCart = Globals.cart;
                int count = 0;
                boolean bFound = false;

                while (count < myCart.size()) {
                    if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                        bFound = true;
                        curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                        boolean result = stock_check(item_code, curQty + 1);
                        if (result == true) {
                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                            Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                            Globals.TotalQty = Globals.TotalQty + 1;
//                                                                            curQty = curQty+1;
                        }

                    }
                    count = count + 1;
                }

                if (!bFound) {
                    curQty = 0d;
                    boolean result = stock_check(item_code, Double.parseDouble("1"));
                    if (result == true) {
                        if (item_location == null) {
                            sale_priceStr = "0";
                            cost_priceStr = "0";
                        } else {
                            sale_priceStr = item_location.get_selling_price();
                            cost_priceStr = item_location.get_cost_price();
                        }
                        //  ArrayList<String> item_group_taxArrayList = calculateTax();
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                            iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                            // String tax_id = item_group_taxArrayList.get(i);
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            } else {
                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                            }
                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                            Globals.order_item_tax.add(order_item_tax);
                        }
                        Double sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                        Dweightvalue=Double.parseDouble(weight)/1000;
                        String qty= Globals.myNumberFormat2QtyDecimal(Dweightvalue,qty_decimal_check);
                        //  Globals.TotalQty=Double.parseDouble(qty);
                        Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                        Double beforetaxprice=sprice-iTaxTotal;
                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), qty, cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * sprice) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                                                                            Globals.cart.add(cartItem);
                        Globals.cart.add(0, cartItem);
                        receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                        if (receipemodifierlist.size() > 0) {

                            Globals.SRNO = Globals.SRNO;
                        } else {
                            Globals.SRNO = Globals.SRNO + 1;
                        }
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                        Globals.TotalItem = Globals.TotalItem + 1;
                        Globals.TotalQty = Globals.TotalQty +Double.parseDouble(qty);
//                                                                        curQty = curQty+1;
                    }
                }
                Globals.cart = myCart;
                retail_list_load();
                String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_retail_2.setText(item_price);
                btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                edt_toolbar_search.setText("");
                edt_toolbar_search.requestFocus();
                edt_toolbar_search.selectAll();

            }
            String itemcode = resultp.get_item_code();

            receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
            if (receipemodifierlist.size() > 0) {
                Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                i.putExtra("itemcode", resultp.get_item_code());
                i.putExtra("opr", Globals.Operation);
                i.putExtra("srno", Globals.SRNO);
                i.putExtra("odr_code", Globals.Order_Code);
                startActivity(i);
                // return;
            }
        } else {
            edt_toolbar_search.requestFocus();
            edt_toolbar_search.selectAll();
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void Load_Activity() {
        Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
        startActivity(intent);
    }
    private ArrayList<String> calculateTax() {
        ArrayList<Tax_Detail> taxIdAarry = new ArrayList<Tax_Detail>();
        ArrayList<String> taxIdFinalAarry = new ArrayList<String>();
        ArrayList<Item_Group_Tax> item_group_taxList = new ArrayList<Item_Group_Tax>();
        try {
            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {
                Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                if (taxIdAarry.size() > 0) {
                    for (int i = 0; i < taxIdAarry.size(); i++) {
                        Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                } else {
                    item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                    for (int i = 0; i < item_group_taxList.size(); i++) {
                        Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                }

            } else {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + Globals.strContact_Code + "'");
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                if (contact.get_zone_id().equals(lite_pos_registration.getZone_Id())) {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                } else {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Intrastate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return taxIdFinalAarry;
    }


    DrawerLayout mDrawerLayout;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

            int id = item.getItemId();
            Globals.SRNO = 1;

        //if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
         /*   if(Globals.cart.size()>0) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }//CLOSE Nav Drawer!
        else {
                mDrawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
*/
                //Item Activity
     /*   Globals.cart.clear();
        Globals.order_item_tax.clear();
        Globals.TotalItemPrice = 0;
        Globals.TotalQty = 0;*/


                if (id == R.id.nav_item) {
                    userPermission = new UserPermission();


                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Item", ItemListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ItemListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }

                // Item Category
                else if (id == R.id.nav_item_category) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Item Category", CategoryListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, CategoryListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }
                // Order Screen (Recept Activity)

                else if (id == R.id.nav_recepts) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Order", ReceptActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent recept_intent = new Intent(MainActivity.this, ReceptActivity.class);
//                startActivity(recept_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }
                // Settings Activity

                else if (id == R.id.nav_settings) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Settings", SetttingsActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }

                // Update License
                else if (id == R.id.nav_lic) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Update License", ActivateActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ActivateActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }

                // Report Activity

                else if (id == R.id.nav_Report) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Report", ReportActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ReportActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_printtest) {
                    Intent it = new Intent(getApplicationContext(), PrintTestActivity.class);
                    startActivity(it);


                }
                // Logout

                else if (id == R.id.nav_logout) {
                    Globals.user = " ";
                    Globals.setEmpty();
                    getAlertDialog();
        /*    Intent item_intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(item_intent);
            finish();*/
                }

                // Contact Activity

                else if (id == R.id.nav_contact) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Contact", ContactListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ContactListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_payment) {

                    Intent intent_payment = new Intent(Retail_IndustryActivity.this, PaymentListActivity.class);
                    startActivity(intent_payment);
                    finish();
  /*          userPermission = new UserPermission();

            Boolean result = userPermission.Permission(MainActivity.this, "Contact", PaymentListActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ContactListActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }*/

                } else if (id == R.id.nav_search_order) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Search Order", SearchOrderActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SearchOrderActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }
                // Manager

                else if (id == R.id.nav_man) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Manager", ManagerActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ManagerActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_resv) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Reservation", ReservationListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, ReservationListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_pay_collection) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Payment Collection", PayCollectionListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }
                // Accounts Activity

                else if (id == R.id.nav_acc) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Accounts", AccountsListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_stock_adjest) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Stock Adjustment", StockAdjestmentListActivity.class);
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }

                // Return
                else if (id == R.id.nav_return) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Return", ReturnOptionActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, PayCollectionListActivity.class);
//                startActivity(item_intent);
//                finish();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
//            Intent in = new Intent(MainActivity.this,ReturnOptionActivity.class);
//            startActivity(in);

                } else if (id == R.id.nav_purchese) {
                    userPermission = new UserPermission();
                    Boolean result = null;
                    if (Locale.getDefault().equals("en")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Purchase", PurchaseListActivity.class);
                    } else if (Locale.getDefault().equals("ar")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Purchase", PurchaseListActivity.class);

                    }
                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_class) {
                    userPermission = new UserPermission();
                    Boolean result = null;
                    if (Locale.getDefault().equals("en")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Class/Category", ClassDestinationListActivity.class);
                    } else if (Locale.getDefault().equals("ar")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Class/Category", ClassDestinationListActivity.class);

                    }
                    Globals.TicketCategory = "Class";
                    if (result == null) {
                        Globals.TicketCategory = "";
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_destination) {
                    userPermission = new UserPermission();
                    Boolean result = null;
                    if (Locale.getDefault().equals("en")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Area/Destination", ClassDestinationListActivity.class);
                    } else if (Locale.getDefault().equals("ar")) {
                        result = userPermission.Permission(Retail_IndustryActivity.this, "Area/Destination", ClassDestinationListActivity.class);

                    }
                    Globals.TicketCategory = "Distination";
                    if (result == null) {
                        Globals.TicketCategory = "";
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_setup) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Setup", TicketSetupListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_ticketing) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Ticketing", TicketingActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                } else if (id == R.id.nav_manufacture) {
                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(Retail_IndustryActivity.this, "Manufacture", ManufactureListActivity.class);


                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            return true;


    }


    public void getAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Retail_IndustryActivity.this);

        builder.setTitle(getString(R.string.alertlogtitle));
        builder.setMessage(getString(R.string.areyousure));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                // Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                // String licensecustomerid= lite_pos_device.getLic_customer_license_id();
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                postDeviceInfo(company_email, company_password, Globals.isuse_logout, Globals.master_product_id, Globals.license_id, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, lite_pos_registration.getRegistration_Code());
            }
        });

        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing

                //  alert.dismiss();
                Load_Activity();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    // Logout api calling using Volley mechanism
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String regcode) {

        pDialog = new ProgressDialog(Retail_IndustryActivity.this);
        pDialog.setMessage("Logging Out....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
        /*HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                JSONObject result2 = responseObject.optJSONObject("result");
                                //  for (int i = 0; i < result2.length(); i++) {
                                // JSONObject jobj= result2.getJSONObject(i);
                                String lic_custid = result2.getString("lic_customer_license_id");
                                String oc_custid = result2.getString("oc_customer_id");
                                String lic_product_id = result2.getString("lic_product_id");
                                String lic_code = result2.getString("lic_code");
                                String license_key = result2.getString("license_key");
                                String licenseType = result2.getString("license_type");
                                String deviceCode = result2.getString("device_code");
                                String locationId = result2.getString("location_id");
                                String deviceName = result2.getString("device_name");
                                String deviceSymbol = result2.getString("device_symbol");
                                String registrationDate = result2.getString("registration_date");
                                String expiryDate = result2.getString("expiry_date");
                                String isActive = result2.getString("is_active");
                                String isUse = result2.getString("is_use");
                                String isUpdate = result2.getString("is_update");
                                String modifiedBy = result2.getString("modified_by");
                                String modifiedDate = result2.getString("modified_date");

                               /* boolean recordExists = db.checkIfDeviceRecordExist("DeviceInformation");
                                if (recordExists == true) {

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                                 }*/
                                try {
                                    // database.beginTransaction();
                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                    if (ct > 0) {
                                       /* database.setTransactionSuccessful();
                                        database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                Intent intent_category = new Intent(Retail_IndustryActivity.this, LoginActivity.class);
                                                startActivity(intent_category);
                                                finish();
                                            }
                                        });

                                    } else {
                                        /*database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "cannot logout", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (Exception e) {

                                }

                                //  }

                                pDialog.dismiss();

                            } else if (status.equals("false")) {
                                try {
                                    // database.beginTransaction();
                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                    lite_pos_device.setStatus("Out");
                                    long ct = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
                                    if (ct > 0) {
                                       /* database.setTransactionSuccessful();
                                        database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                Intent intent_category = new Intent(Retail_IndustryActivity.this, LoginActivity.class);
                                                startActivity(intent_category);
                                                finish();
                                            }
                                        });

                                    } else {
                                        /*database.endTransaction();*/
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "cannot logout", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (Exception e) {

                                }

                              /*  Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();*/
                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication issue", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();

                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("is_use", isuse);
                params.put("master_product_id", masterproductid);
                params.put("lic_customer_license_id", liccustomerlicenseid);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("reg_code", regcode);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private String DatabaseBackUp() {
        File backupDB = null;
        String backupDBPath = null;
        try {
            File sd = new File(Environment.getExternalStorageDirectory(), "TriggerPOS/Backup");

            if (!sd.exists()) {
                sd.mkdirs();
            }
            // File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = Database.DATABASE_FILE_PATH + File.separator + Database.DBNAME;
            backupDBPath = "TriggerPOS" + DateUtill.currentDatebackup() + ".db";
            File currentDB = new File(currentDBPath);
            backupDB = new File(sd, backupDBPath);

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

                String[] s = new String[1];

                // Type the path of the files in here
                s[0] = backupDB.getPath();

                // first parameter is d files second parameter is zip file name
                ZipManager zipManager = new ZipManager();

                // calling the zip function


                zipManager.zip(s, Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "Backup" + "/" + "DatabaseTriggerPOS.zip");

                send_email_manager();

                Toast.makeText(getApplicationContext(), R.string.BackupSuccCreated,
                        Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return backupDBPath;
    }


    private void send_email_manager() {
        try {
            String[] recipients = {"developer.pegasus@gmail.com"};
            final Retail_IndustryActivity.SendEmailAsyncTask email = new Retail_IndustryActivity.SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender("developer.pegasus@gmail.com", "Passw0rd@", "smtp.gmail.com", "465");
            email.m.set_from("developer.pegasus@gmail.com");
            email.m.setBody("Phomello-TriggerPOSDB");
            email.m.set_to(recipients);
            email.m.set_subject("Phomello-TriggerPOSDB ( " + date + " )");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "Backup" + "/" + "DatabaseTriggerPOS.zip");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        Retail_IndustryActivity activity;

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
                Log.e(PaymentCollection_MainScreen.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(PaymentCollection_MainScreen.SendEmailAsyncTask.class.getName(), "Email failed");
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
    public  class PrintKOT_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Retail_IndustryActivity activity;

        public PrintKOT_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

               // printKOT(strOrderCode);

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
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pDialog = new ProgressDialog(Retail_IndustryActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (flag_scan == true) {
                    Globals.BarcodeReslt = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mScannerView.stopCameraPreview();
                            mScannerView.stopCamera();
                            ViewGroup vg = (ViewGroup) (mScannerView.getParent());
                            vg.removeView(mScannerView);
                            main_view();
                            flag_scan = false;
                            pDialog.dismiss();
                        }
                    });

                } else {
                    Globals.BarcodeReslt = "";

                    if (pressedTime + 2000 > System.currentTimeMillis()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.dismiss();
                                Retail_IndustryActivity.super.onBackPressed();
                                finish();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

                            }
                        });
                        pressedTime = System.currentTimeMillis();
                    }

                   /*     if (doubleBackToExitPressedOnce) {
                            pDialog.dismiss();
                            Retail_IndustryActivity.super.onBackPressed();
                            return;
                        }

                        doubleBackToExitPressedOnce = true;
                     runOnUiThread(new Runnable() {
                         @Override
                            public void run() {
                             pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

                                doubleBackToExitPressedOnce = false;
                            }
                        });*/

/*
                        try {
                            sleep(1000);
                            Intent intent = new Intent(Retail_IndustryActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }*/

                }
            }
        };
        timerThread.start();
    }
}