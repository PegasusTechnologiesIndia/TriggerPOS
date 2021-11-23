package org.phomellolitepos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.hoin.btsdk.BluetoothService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.ContactListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.Util.ZipManager;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.User;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class PaymentCollection_MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText edt_toolbar_contact_list;
    TextView contact_title;
    Contact contact;
    Address address;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    Address_Lookup address_lookup;
    Contact_Bussiness_Group contact_bussiness_group;
    ArrayList<Contact> arrayList;
    ContactListAdapter contactListAdapter;
    ProgressDialog pDialog;
    Database db;
    String date;
    DrawerLayout drawer;
    SQLiteDatabase database;
    //Lite_POS_Registration lite_pos_registration;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    private RecyclerView recyclerView;
    String company_email, company_password;
    String email, password;
    Button btn_syncdemo;
    public static BluetoothService mService;
    public static BluetoothDevice con_dev;
    TextView list_title, my_company_name, my_company_email, txt_user_name, txt_version;
    String serial_no, android_id, myKey, device_id,imei_no;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Intent intent;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_collection__main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        settings = Settings.getSettings(getApplicationContext(), database, "");
        
        edt_toolbar_contact_list = (EditText) findViewById(R.id.edt_toolbar_contact_list);
        edt_toolbar_contact_list.setMaxLines(1);
        edt_toolbar_contact_list.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_toolbar_contact_list.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_toolbar_contact_list.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String strFilter = edt_toolbar_contact_list.getText().toString().trim();
                    strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' Or contact_1 Like '%" + strFilter + "%' Or email_1 Like '%" + strFilter + "%' )";
                    edt_toolbar_contact_list.selectAll();
                    getContactList(strFilter);
                    return true;
                }
                return false;
            }
        });

        contact_title = (TextView) findViewById(R.id.contact_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        btn_syncdemo=(Button)findViewById(R.id.btn_syncdemo);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        my_company_name = (TextView) hView.findViewById(R.id.my_company_name);
        my_company_email = (TextView) hView.findViewById(R.id.my_company_email);
        txt_user_name = (TextView) hView.findViewById(R.id.txt_user_name);
        txt_version = (TextView) hView.findViewById(R.id.txt_version);
        Menu menu = navigationView.getMenu();
        MenuItem nav_item;
        navigationView.setNavigationItemSelectedListener(this);

        edt_toolbar_contact_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_toolbar_contact_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_contact_list.requestFocus();
                    edt_toolbar_contact_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_contact_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_contact_list.selectAll();
                    return true;
                }
            }
        });
        serial_no = Build.SERIAL;
        Globals.serialno = serial_no;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Globals.androidid = android_id;
        myKey = serial_no + android_id;
        Globals.mykey = myKey;
        Globals.objLPR = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        company_email = Globals.objLPR.getEmail();
        company_password = Globals.objLPR.getPassword();
        Globals.objLPD = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (Globals.objLPD != null) {
                Globals.license_id = Globals.objLPD.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }


      
        Globals.objsettings = Settings.getSettings(getApplicationContext(), database, "");
        if (Globals.objsettings == null) {
            Globals.PrinterType = "";
        } else {
            try {
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
            } catch (Exception ex) {
                Globals.PrinterType = "";
            }
        }
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
        } catch (Exception ex) {
        }
       Intent intent=getIntent();
      /*  if(intent.getStringExtra("whatsappFlag")==null) {

        }
        else{*/


            String whatsappflag = intent.getStringExtra("whatsappFlag");
           String contactcode = intent.getStringExtra("contact_code");

           if(whatsappflag!=null) {
               if (whatsappflag.equals("true")) {
                   share_dialog(contactcode);
               }
           }
           // }


        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = df.format(d);
        if (Globals.objsettings.getPrinterId().equals("3") || Globals.objsettings.getPrinterId().equals("4") || Globals.objsettings.getPrinterId().equals("5")) {
            if (Globals.strIsBlueService.equals("utc")) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                serverIntent.putExtra("flag", "PaymentCollection_MainScreen");

                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            } else if (con_dev == null) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                serverIntent.putExtra("flag", "PaymentCollection_MainScreen");
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        }
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

      //  device_id = telephonyManager.getDeviceId();
        //imei_no=telephonyManager.getImei();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        /*if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }*/


        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PaymentCollection_MainScreen.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = "Add";
                Intent intent = new Intent(PaymentCollection_MainScreen.this, ContactActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });

        btn_syncdemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                getAlertforDemoDB();
            }
        });

        if(Globals.objLPR.getIndustry_Type().equals("3")){
            contact = Contact.getContact(getApplicationContext(), database, db,"" );

            if(contact==null){
                btn_syncdemo.setVisibility(View.VISIBLE);
            }
            else{
                btn_syncdemo.setVisibility(View.GONE);
            }
        }
        try {
            // Getting contect list here
            getContactList("");
        }
        catch(Exception e){

        }
    }

    private void getContactList(String strFilter) {
        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        } else {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        }

        if (arrayList.size() > 0) {
            contactListAdapter = new ContactListAdapter(PaymentCollection_MainScreen.this, arrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            contact_title.setVisibility(View.GONE);
            btn_syncdemo.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            // recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(contactListAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            btn_syncdemo.setVisibility(View.VISIBLE);
            contact_title.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PaymentCollection_MainScreen.this);
                builder1.setMessage("Do you Want to Switch on");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Accounts",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Contact resultp = arrayList.get(position);
                                String contact_code = resultp.get_contact_code();
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, AccountsActivity.class);
                                intent.putExtra("contact_code", contact_code);
                                startActivity(intent);
                            }
                        });

                builder1.setNegativeButton(
                        "Edit Contact",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Contact resultp = arrayList.get(position);
                                String operation = "Edit";
                                String item_code = resultp.get_contact_code();
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, ContactActivity.class);
                                intent.putExtra("contact_code", item_code);
                                intent.putExtra("operation", operation);
                                startActivity(intent);
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }

            @Override
            public void onLongClick(View view, int position) {

            }


        }));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        if(lite_pos_registration.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        else{
            menu.setGroupVisible(R.id.overFlowItemsToHide, true);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // search filter
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_contact_list.getText().toString().trim();
            strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' Or contact_1 Like '%" + strFilter + "%' Or email_1 Like '%" + strFilter + "%' )";
            edt_toolbar_contact_list.selectAll();
            getContactList(strFilter);
            return true;
        }

        // Sync Code
         if (id == R.id.action_send) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    PaymentCollection_MainScreen.this);
            alertDialog.setTitle(getString(R.string.Contact));
            alertDialog
                    .setMessage(R.string.dilog_msg_cdopr);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);


            alertDialog.setPositiveButton(R.string.sync,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    PaymentCollection_MainScreen.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.sync_data_from_server);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            alertDialog.setNegativeButton(R.string.Cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    });

                            alertDialog.setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                                pDialog = new ProgressDialog(PaymentCollection_MainScreen.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.Downloading_Contact));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {

                                                        String suss="";
                                                        JSONObject result;
                                                        try {
                                                            // send contact to server
                                                            result = send_online_contact();
                                                        }
                                                        catch(Exception e){

                                                        }
         /*                                               if(result.equals("3")){

                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    if(Globals.responsemessage.equals("Device Not Found")){

                                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                                        lite_pos_device.setStatus("Out");
                                                                        long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                                        if (ct > 0) {

                                                                            Intent intent_category = new Intent(PaymentCollection_MainScreen.this, LoginActivity.class);
                                                                            startActivity(intent_category);
                                                                            finish();
                                                                        }


                                                                    }

                                                                }
                                                            });
                                                        }*/
                                                        try {
                                                            //Get contact from server
                                                            Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");


                                                            //Call get contact api here
                                                            if (sys_sycntime==null){
                                                                getcontact_from_server("",serial_no,android_id,myKey,pDialog);
                                                            }else {
                                                                getcontact_from_server(sys_sycntime.get_datetime(),serial_no,android_id,myKey,pDialog);
                                                            }

                                                        }
                                                        catch(Exception e){

                                                        }


                                                    }
                                                }.start();
                                            } else {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                                            }

                                        }


                                    });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
            AlertDialog alert = alertDialog.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type =lite_pos_registration.getproject_id();

                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }/*else if (lite_pos_registration.getIndustry_Type().equals("3")||lite_pos_registration.getIndustry_Type().equals("6")){
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
        return super.onOptionsItemSelected(item);
    }

    private String getAccContact() {
        String succ = "0";
        String serverData = "";
                //get_Acc_Customer_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                try {

                    JSONArray jsonArray = jsonObject_bg.getJSONArray("result");

                    for (int k = 0; k < jsonArray.length(); k++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + jsonObject.get("contact_code") + "'", database);
                        if (acc_customer == null) {
                            acc_customer = new Acc_Customer(getApplicationContext(), null, jsonObject.getString("contact_code"), jsonObject.getString("sum(cr_amount - dr_amount)"));
                            long a = acc_customer.insertAcc_Customer(database);
                            if (a > 0) {
                                succ = "1";
                            }
                        } else {
                            acc_customer.set_amount(jsonObject.getString("sum(cr_amount - dr_amount)"));
                            long a = acc_customer.updateAcc_Customer("contact_code", new String[]{jsonObject.getString("contact_code")}, database);
                            if (a > 0) {
                                succ = "1";
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
        }
        return succ;
    }

  /*  private String get_Acc_Customer_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "customer_summary");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
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

    private JSONObject send_online_contact() {

        JSONObject conList = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id,modified_date,is_taxable from contact where is_push='N'",Globals.license_id,serial_no,android_id,myKey);
        return conList;
    }

    private String getContact(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here

        try {
            final JSONObject jsonObject_contact = new JSONObject(serverData);
            final String strStatus = jsonObject_contact.getString("status");
            final String strmsg = jsonObject_contact.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");

                if (sys_sycntime!=null){
                    sys_sycntime.set_datetime(jsonArray_contact.getJSONObject(0).getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                }
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");



                    if (contact == null) {
                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new Address(getApplicationContext(), null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                long itmsp = address.insertAddress(database);

                                if (itmsp > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }


                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);
                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");

                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    } else {

                        // Edit on 18-Oct-2017
                        contact = new Contact(getApplicationContext(), contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));

                        long l = contact.updateContact("contact_code=? And contact_id=?", new String[]{contact_code, contact.get_contact_id()}, database);

                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = Address.getAddress(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);
                                if (address != null) {
                                    address = new Address(getApplicationContext(), address.get_address_id(), jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                    long itmsp = address.updateAddress("address_code=? And address_id=?", new String[]{contact_code, address.get_address_id()}, database);

                                    if (itmsp > 0) {
                                        succ_bg = "1";

                                    } else {
                                        succ_bg = "0";
                                    }
                                }
                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");
                            long g = address_lookup.delete_Address_Lookup(getApplicationContext(), "address_code=?", new String[]{contact_code}, database);
                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);

                                address_lookup = Address_Lookup.getAddress_Lookup(getApplicationContext(), "WHERE address_code ='" + contact_code + "'", database);


                                address_lookup = new Address_Lookup(getApplicationContext(), null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");
                            long c = contact_bussiness_group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code=?", new String[]{contact_code});
                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    }
                }
            }
            else if (strStatus.equals("false")) {

                succ_bg="3";
                Globals.responsemessage=strmsg;
            }




            else {
                succ_bg = "0";
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }

/*    private String get_contact_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "contact");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",lite_pos_registration.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
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

    public void getcontact_from_server(final String datetime,final String serial_no,final String android_id,final String myKey,final ProgressDialog pDialog) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "contact";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getContact(response);


                            switch (result) {
                                case "1":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            getContactList("");
                                            Toast.makeText(getApplicationContext(), R.string.Contact_download, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    break;

                                case "2":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            getContactList("");
                                            Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;


                                case "3":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (Globals.responsemessage.equals("Device Not Found")) {

                                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                lite_pos_device.setStatus("Out");
                                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                if (ct > 0) {

                                                    Intent intent_category = new Intent(PaymentCollection_MainScreen.this, LoginActivity.class);
                                                    startActivity(intent_category);
                                                    finish();
                                                }


                                            }
                                        }
                                    });
                                    break;

                                default:
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), R.string.Contact_not_found, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());


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
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        Globals.SRNO=1;

        if (id == R.id.nav_settings) {


                Intent item_intent = new Intent(PaymentCollection_MainScreen.this, SetttingsActivity.class);
                startActivity(item_intent);
                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;


        }
       else if (id == R.id.nav_user) {


            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, UserListActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }
        else if (id == R.id.nav_profile) {


            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, ProfileActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }
        else if (id == R.id.nav_database) {


            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, DataBaseActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }
        else if (id == R.id.nav_man) {


            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, ManagerActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }

        else if (id == R.id.nav_Report) {

            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, ReportActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        else if (id == R.id.nav_send_db) {

            if (isNetworkStatusAvialable(getApplicationContext())) {
                DatabaseBackUp();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }
        else if (id == R.id.nav_ab) {

            Intent item_intent = new Intent(PaymentCollection_MainScreen.this, AboutActivity.class);
            startActivity(item_intent);
            finish();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        }
        else if (id == R.id.nav_logout) {


            Globals.user = " ";
            Globals.setEmpty();
            getAlertDialog();


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentCollection_MainScreen.this);

        builder.setTitle(getString(R.string.alertlogtitle));
        builder.setMessage(getString(R.string.areyousure));

        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

                // Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

                // String licensecustomerid= lite_pos_device.getLic_customer_license_id();
                postDeviceInfo(company_email, company_password, Globals.isuse_logout, Globals.master_product_id, Globals.license_id, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.objLPR.getRegistration_Code());
            }
        });

        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // Logout api calling using Volley mechanism
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String regcode) {

        pDialog = new ProgressDialog(PaymentCollection_MainScreen.this);
        pDialog.setMessage("Logging Out....");
        pDialog.show();
        String server_url = Globals.App_Lic_Base_URL + "/index.php?route=api/license_product_1/device_login";
       /* HttpsTrustManager.allowAllSSL();*/
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
                                                Intent intent_category = new Intent(PaymentCollection_MainScreen.this, LoginActivity.class);
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
                                                Intent intent_category = new Intent(PaymentCollection_MainScreen.this, LoginActivity.class);
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
            final PaymentCollection_MainScreen.SendEmailAsyncTask email = new PaymentCollection_MainScreen.SendEmailAsyncTask();
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
        PaymentCollection_MainScreen activity;

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


    public void getAlertforDemoDB () {


        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.areyousure);

        //Setting message manually and performing action on button click
        builder.setMessage(R.string.demomessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDemoDatabse();

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


    public void getDemoDatabse() {

        //  database.beginTransaction();

        // tax_master = new Tax_Master(getApplicationContext(), null, Globals.objLPD.getLocation_Code(), "Tax1"  , "P", "2.5", comment, "1", modified_by, date, "N");



        progressDialog = new ProgressDialog(PaymentCollection_MainScreen.this);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.syncdemodb));
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        sleep(2000);


       /*             if(Globals.objLPR.getCountry_Id().equals("99")) {
                            try {
                                read_tax();
                            } catch (Exception e) {
                            }
                        }
                      if(Globals.objLPR.getCountry_Id().equals("221")) {

                        }
*/

                        if (Globals.objLPR.getIndustry_Type().equals("3")) {

                            try {
                                read_contact();
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }

                    } catch (Exception e) {
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Globals.objLPR.getIndustry_Type().equals("3")) {
                                Toast.makeText(getApplicationContext(), "Demo Database sync Sucessfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(PaymentCollection_MainScreen.this, PaymentCollection_MainScreen.class);
                                startActivity(intent);
                            }


                        }
                    });
                } catch (Exception e) {
                }
            }
        };
        t.start();

        /*DownloadImageUrlTask downloadimage= new DownloadImageUrlTask();
        downloadimage.execute();*/
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
    //Bluetooth service handler
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
        }}
    public void share_dialog(String contactCode){
        final Dialog listDialog2 = new Dialog(PaymentCollection_MainScreen.this);
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
       listDialog2.dismiss();
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    public void run() {

                        //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                        startWhatsApp(contactCode);

                    }
                });

                listDialog2.dismiss();

            }
        });
    }

    private void startWhatsApp(String contactcode) {
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
        File file=null;
        if(Globals.PrinterType.equals("11")){
            file = new File(Globals.folder + Globals.pdffolder
                    + "/" + contactcode+"80mm" + ".pdf");
        }
        else {
            file = new File(Globals.folder + Globals.pdffolder
                    + "/" + contactcode + ".pdf");
        }
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


}
