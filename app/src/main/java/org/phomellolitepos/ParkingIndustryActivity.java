package org.phomellolitepos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.hoin.btsdk.BluetoothService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.ViewPagerAdapterParking;
import org.phomellolitepos.Fragment.VehicleIN_Fragment;
import org.phomellolitepos.Fragment.VehicleOUT_Fragment;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.Util.ZipManager;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ParkingIndustryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPagerAdapterParking viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Database db;
    String date;
    UserPermission userPermission;
    ProgressDialog pDialog;
    DrawerLayout drawer;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    String email, password;
    String serial_no, android_id, myKey, device_id, imei_no;
    String company_email, company_password;
    TextView list_title, my_company_name, my_company_email, txt_user_name, txt_version, txt_industry;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int STATE_CONNECTED = 2;
    public static BluetoothService mService;
    public static BluetoothDevice con_dev;
    private static NfcAdapter nfcAdapter;
    private static PendingIntent mPendingIntent;
    AlertDialog.Builder alertbox;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    private Intent mIntent = null;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_industry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Parking Solution");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
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
        settings = Settings.getSettings(getApplicationContext(), database, "");
        viewPager = findViewById(R.id.viewpager);

        //setting up the adapter

        viewPagerAdapter = new ViewPagerAdapterParking(getSupportFragmentManager());

        // add the fragments
        viewPagerAdapter.add(new VehicleIN_Fragment(), "Vehicle IN");
        viewPagerAdapter.add(new VehicleOUT_Fragment(), "Vehicle OUT");



        //Set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        if(Globals.newvehicleorderList.size()>0) {
            viewPager.setCurrentItem(1);
        }
        else{
            viewPager.setCurrentItem(0);
        }
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
        StrictMode.setThreadPolicy(policy);


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

        try {
            backgroundLocationJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    Thread t = new Thread() {
        public void run() {
            try {
                Intent serviceIntent = new Intent(ParkingIndustryActivity.this, BackgroundApiService.class);
                startService(serviceIntent);
            } catch (Exception e) {
            }
        }
    };t.start();
        if(Globals.objsettings.getIs_singleWindow().equals("true")){
           tabLayout.removeTabAt(1);

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
        Globals.user = user.get_user_code();
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = df.format(d);

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

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);

        if (Globals.objsettings.getPrinterId().equals("3") || Globals.objsettings.getPrinterId().equals("4") || Globals.objsettings.getPrinterId().equals("5")) {
            if (Globals.strIsBlueService.equals("utc")) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                serverIntent.putExtra("flag", "MainActivity");

                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            } else if (con_dev == null) {
                mService = new BluetoothService(getApplicationContext(), mHandler);
                Intent serverIntent = new Intent(getApplicationContext(), BluetoothDeviceListActivity.class);
                serverIntent.putExtra("flag", "MainActivity");
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        }
        Globals.objLPD = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (Globals.objLPD != null) {
                Globals.license_id = Globals.objLPD.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }

        //NavigationView navigationViewRight = (NavigationView) findViewById(R.id.right_nav_view);

//        View hView1 = navigationViewRight.getHeaderView(0);
//        my_company_name = (TextView) hView1.findViewById(R.id.my_company_name);
//        my_company_email = (TextView) hView1.findViewById(R.id.my_company_email);
//        txt_user_name = (TextView) hView1.findViewById(R.id.txt_user_name);


        NavigationView rightNavigationView = (NavigationView) findViewById(R.id.right_nav_view);
        Menu menu1 = rightNavigationView.getMenu();
        View rView = rightNavigationView.getHeaderView(0);
        txt_industry = (TextView) rView.findViewById(R.id.txt_industryname);
        if (Globals.objLPR.getIndustry_Type().equals("4")) {
            txt_industry.setText("Parking Solution");
        }

        if (Globals.objsettings.getIs_NFC().equals("true")) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) {
                Log.d("h_bl", "No support NFC！");
                Toast.makeText(getApplicationContext(), "No support NFC！", Toast.LENGTH_SHORT).show();

                return;
            }
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Please enable the NFC function first in the system settings！", Toast.LENGTH_SHORT).show();
                Log.d("h_bl", "请在系统设置中先启用NFC功能！");
            }


            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

            try {
                ndef.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mFilters = new IntentFilter[]{ndef};

            //设置
            mTechLists = new String[][]{new String[]{IsoDep.class
                    .getName()}, new String[]{MifareClassic.class.getName()}};//MifareClassic

            mIntent = this.getIntent();
            if (getIntent() != null) {
                resolveIntent(getIntent());
            }

        }
        rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle Right navigation view item clicks here.
                int id = item.getItemId();
                Globals.SRNO = 1;
                if (id == R.id.nav_tax) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Tax", TaxListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }


                } else if (id == R.id.nav_user) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "User", UserListActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }


                } else if (id == R.id.nav_profile) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Profile", ProfileActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }

                } else if (id == R.id.nav_lic) {

                    userPermission = new UserPermission();

                    Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Update License", ActivateActivity.class);

                    if (result == null) {
                        Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.END);
                        return false;
                    }


                } else if (id == R.id.nav_database) {

                    Intent item_intent = new Intent(ParkingIndustryActivity.this, DataBaseActivity.class);
                    startActivity(item_intent);
                    finish();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;


                } else if (id == R.id.nav_send_db) {

                    if (isNetworkStatusAvialable(getApplicationContext())) {
                        DatabaseBackUp();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }


                } else if (id == R.id.nav_ab) {

                    Intent item_intent = new Intent(ParkingIndustryActivity.this, AboutActivity.class);
                    startActivity(item_intent);
                    finish();

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;


                } else if (id == R.id.nav_support) {
                    Intent intent_about = new Intent(ParkingIndustryActivity.this, YouTubeVideoListActivity.class);
                    startActivity(intent_about);
                    finish();
                } else if (id == R.id.nav_privacy) {
                    try {
                        String url = "http://www.pegasustech.net/privacy";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));


                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        Toast.makeText(ParkingIndustryActivity.this, "There is no default browser Installed", Toast.LENGTH_LONG).show();
                        //i.setPackage(null);
                        //startActivity(i);
                    }
                    //finish();
                }
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        Globals.SRNO = 1;
        if (id == R.id.nav_item_category) {


            Intent item_intent = new Intent(ParkingIndustryActivity.this, CategoryListActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        } else if (id == R.id.nav_item) {

            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Item", ItemListActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }


        } else if (id == R.id.nav_settings) {

            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Settings", SetttingsActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_contact) {

            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Contact", ContactListActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

        } else if (id == R.id.nav_recepts) {

            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Order", VehicleHistoryScreen.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        } else if (id == R.id.nav_printtest) {


            Intent item_intent = new Intent(ParkingIndustryActivity.this, PrintTestActivity.class);
            startActivity(item_intent);
            finish();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;


        } else if (id == R.id.nav_Report) {
            userPermission = new UserPermission();

            Boolean result = userPermission.Permission(ParkingIndustryActivity.this, "Report", ReportActivity.class);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "This user don't have permission to access this form", Toast.LENGTH_SHORT).show();
//                Intent item_intent = new Intent(MainActivity.this, SetttingsActivity.class);
//                startActivity(item_intent);
//                finish();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        } else if (id == R.id.nav_logout) {


            Globals.user = " ";
            Globals.setEmpty();
            getAlertDialog();


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            final ParkingIndustryActivity.SendEmailAsyncTask email = new ParkingIndustryActivity.SendEmailAsyncTask();
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
        ParkingIndustryActivity activity;

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
                Log.e(ParkingIndustryActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(ParkingIndustryActivity.SendEmailAsyncTask.class.getName(), "Email failed");
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ParkingIndustryActivity.this, ParkingIndustryActivity.class);
        intent.putExtra("opr", "Add");
        intent.putExtra("order_code", "");
        startActivity(intent);
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
        }
    }


    public void getAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ParkingIndustryActivity.this);

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

                //  alert.dismiss();
                Load_Activity();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // Logout api calling using Volley mechanism
    public void postDeviceInfo(final String email, final String password, final String isuse, final String masterproductid, final String liccustomerlicenseid, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4, final String regcode) {

        pDialog = new ProgressDialog(ParkingIndustryActivity.this);
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
                                                Intent intent_category = new Intent(ParkingIndustryActivity.this, LoginActivity.class);
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
                                                Intent intent_category = new Intent(ParkingIndustryActivity.this, LoginActivity.class);
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

    private void Load_Activity() {
        Intent intent = new Intent(ParkingIndustryActivity.this, ParkingIndustryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parking_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_openRight) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        //-----------------非常关键，必要的哦，不能删除----------------
        if (Globals.objsettings.getIs_NFC().equals("true")) {
            if (nfcAdapter != null) {
                nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                        mTechLists);
                // resolveIntent(mIntent);
            } else {
                Toast.makeText(ParkingIndustryActivity.this, "NFC is not enabled", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        if (settings.getIs_NFC().equals("true")) {
            if (getIntent() != null) {
                setIntent(intent);
                resolveIntent(intent);
            }
        }

    }

    private void showAlert(String alertCase) {
        // prepare the alert box
         alertbox = new AlertDialog.Builder(ParkingIndustryActivity.this);

        alertbox.setMessage(alertCase);
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            // Save the data from the UI to the database - already done
            public void onClick(DialogInterface dialogInterface, int arg1) {
                //clearFields();
               dialogInterface.dismiss();
            }
        });
        // display box
        alertbox.show();
    }

    void resolveIntent(Intent intent) {
        Context context = ParkingIndustryActivity.this;


        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] extraID = tagFromIntent.getId();

            StringBuilder sb = new StringBuilder();
            for (byte b : extraID) {
                sb.append(String.format("%02X", b));
            }
            ;

            String tagID = sb.toString();
            Bundle bundle = new Bundle();
            bundle.putString("TagId", tagID);
// set Fragmentclass Arguments
            FragmentManager fragmentManager = getSupportFragmentManager();


            Fragment currentFragment = viewPagerAdapter.getItem(viewPager.getCurrentItem());
            if (viewPager.getCurrentItem()==0) {
                FrameLayout fl = (FrameLayout) findViewById(R.id.content_parkingindustry);
            fl.removeAllViews();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                VehicleIN_Fragment fragobj = new VehicleIN_Fragment();
                fragobj.setArguments(bundle);
                fragmentTransaction.add(R.id.content_parkingindustry, fragobj).addToBackStack(null).commit();
                fragobj.setArguments(bundle);
            } else if (viewPager.getCurrentItem()==1) {
                FrameLayout fl = (FrameLayout) findViewById(R.id.content_parkingindustry_vehicleout);
                fl.removeAllViews();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                VehicleOUT_Fragment fragobj = new VehicleOUT_Fragment();
                fragobj.setArguments(bundle);
                fragmentTransaction.add(R.id.content_parkingindustry_vehicleout, fragobj).addToBackStack(null).commit();
                fragobj.setArguments(bundle);
            }



            Log.e("nfc ID", tagID);


            //tv_nfctag.setText(tagID);

        } else {
            Toast.makeText(getApplicationContext(), "Do not find a card", Toast.LENGTH_SHORT).show();

         //   showAlert("Do not find a card");
        }

    }// End of method

    @Override
    protected void onStart() {
        super.onStart();
        if (Globals.objsettings.getIs_NFC().equals("true")) {
            if (getIntent() != null) {
                resolveIntent(getIntent());
            }
        }
    }

    public void backgroundLocationJson() throws JSONException {
        JSONArray jsonArr = new JSONArray();

        final JSONObject jsonObj1 = new JSONObject();
        try {

            JSONObject jsonObj = new JSONObject();
            // Toast.makeText(getApplicationContext(), "size"+export.size(), Toast.LENGTH_SHORT).show();
            jsonObj.put("latitude", Globals.latitude);
            jsonObj.put("longitude", Globals.longitude);
            jsonObj.put("address", Globals.locationddress);
            jsonObj.put("datetime", date);

            jsonArr.put(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        jsonObj1.put("result", jsonArr);
        Globals.jsonArray_background = jsonObj1;


    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (Globals.objsettings.getIs_NFC().equals("true")) {
/*            if (getIntent() != null) {
                resolveIntent(getIntent());
            }
        }*/
        }
    }
}