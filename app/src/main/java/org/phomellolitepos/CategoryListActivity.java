package org.phomellolitepos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.phomellolitepos.Adapter.CategoryAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class CategoryListActivity extends AppCompatActivity {
    EditText edt_toolbar_category_list;
    TextView category_title;
    Item_Group item_group;
    ArrayList<Item_Group> arrayList;
    CategoryAdapter categoryAdapter;
    ProgressDialog pDialog;
    Lite_POS_Registration lite_pos_registration;
    Database db;
    Lite_POS_Device liteposdevice;
    SQLiteDatabase database;
    String succ_import, date;
    int id;
    Settings settings;
    String liccustomerid;
    int PICKFILE_RESULT_CODE = 100;
    String serial_no, android_id, myKey, device_id, imei_no;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        edt_toolbar_category_list = (EditText) findViewById(R.id.edt_toolbar_category_list);
        category_title = (TextView) findViewById(R.id.category_title);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edt_toolbar_category_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_category_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_category_list.requestFocus();
                    edt_toolbar_category_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_category_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_category_list.selectAll();
                    return true;
                }
            }
        });

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
        imei_no = telephonyManager.getImei();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(CategoryListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(CategoryListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(CategoryListActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = "Add";
                Intent intent = new Intent(CategoryListActivity.this, ItemCategoryActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        getCategoryList("");
    }

    private void getCategoryList(String strFilter) {
        arrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(item_group_name) asc limit " + Globals.ListLimit + "", database, db);
        ListView category_list = (ListView) findViewById(R.id.category_list);
        if (arrayList.size() > 0) {
            categoryAdapter = new CategoryAdapter(CategoryListActivity.this, arrayList);
            category_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(categoryAdapter);
            category_list.setTextFilterEnabled(true);
            categoryAdapter.notifyDataSetChanged();
        } else {
            category_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        if(Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
 /*       lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        String ck_project_type = lite_pos_registration.getproject_id();
        if(id==R.id.action_send){
        if(ck_project_type.equals("standalone")){
            item.setVisible(false);
           // item.getItem(R.id.action_send).setVisible(false);
        }
        else{
            item.setVisible(true);
           // menu.getItem(R.id.action_send).setVisible(true);

        }
        }*/
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_category_list.getText().toString().trim();
            strFilter = " and ( item_group_code Like '%" + strFilter + "%'  Or item_group_name Like '%" + strFilter + "%' )";
            edt_toolbar_category_list.selectAll();
            getCategoryList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {
            // here dialog will open for cloud operations
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    CategoryListActivity.this);
            alertDialog.setTitle("Import Function");
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
                                    CategoryListActivity.this);
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
                                                pDialog = new ProgressDialog(CategoryListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.down_item_grp));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        // Get item group from server
                                                        String result = send_online_item_group();
                                                        String suss = getitemGroup();
                                                        pDialog.dismiss();

                                                        if(result=="0"){

                                                            try {
                                                                if(Globals.responsemessage.equals("Device Not Found")){

                                                                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                                    lite_pos_device.setStatus("Out");
                                                                    long ct = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
                                                                    if (ct > 0) {

                                                                        Intent intent_category = new Intent(CategoryListActivity.this, LoginActivity.class);
                                                                        startActivity(intent_category);
                                                                        finish();
                                                                    }


                                                                }

                                                            }
                                                            catch(Exception e){
                                                                System.out.println("Device not found Exception "+ e.getMessage());
                                                            }
                                                        }
                                                        switch (suss) {
                                                            case "1":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getCategoryList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Itm_Grp_downld, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                                break;

                                                            case "2":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getCategoryList("");
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

                                                                                Intent intent_category = new Intent(CategoryListActivity.this, LoginActivity.class);
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
                                                                        getCategoryList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Itm_grp_not_fnd, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
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

//            alertDialog.setNeutralButton(R.string.Import_CSV,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,
//                                            int which) {
//                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                                    CategoryListActivity.this);
//                            alertDialog.setTitle("");
//                            alertDialog
//                                    .setMessage(R.string.Imprt_csv_dilog_msg);
//                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.MATCH_PARENT);
//                            alertDialog.setNegativeButton(R.string.Cancel,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                        }
//                                    });
//                            alertDialog.setPositiveButton(R.string.Ok,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//
////                                           String path= String.valueOf(Environment.getExternalStorageDirectory());
////                                            File file = new File(path);
////                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
////                                            intent.setType("text/*");
////                                            try {
////                                                startActivityForResult(Intent.createChooser(intent, "Open CSV"), 100);
////                                            } catch (ActivityNotFoundException e) {
////
////                                            }
//
////                                            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
////                                            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
////                                            chooseFile.setType("*/*");
////                                            startActivityForResult(chooseFile,0);
////                                            succ_import = "0";
//                                            pDialog = new ProgressDialog(CategoryListActivity.this);
//                                            pDialog.setTitle("");
//                                            pDialog.setMessage(getString(R.string.Impting_itm_ctgry));
//                                            pDialog.setCancelable(false);
//                                            pDialog.show();
//                                            final Thread t = new Thread() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        try {
//                                                            sleep(200);
//                                                            database.beginTransaction();
//                                                            String modified_by = Globals.user;
//                                                            File myFile = new File("/sdcard/itemgroup.csv");
//                                                            FileInputStream fIn = new FileInputStream(myFile);
//                                                            BufferedReader myReader = new BufferedReader(
//                                                                    new InputStreamReader(fIn));
//                                                            String aDataRow = "";
//                                                            String aBuffer = "";
//                                                            int count = 0;
//                                                            while ((aDataRow = myReader.readLine()) != null) {
//
//                                                                if (count==0){
//                                                                    count=1;
//                                                                }else {
//                                                                    List<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));
//                                                                    String item_group_code = myList.get(0);
//                                                                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");
//                                                                    if (item_group == null) {
//                                                                        Item_Group item_group = new Item_Group(getApplicationContext(), null,
//                                                                                Globals.Device_Code, myList.get(0), "0", myList.get(1), "0",
//                                                                                "1", modified_by, date, "N");
//                                                                        long l = item_group.insertItem_Group(database);
//                                                                        if (l > 0) {
//                                                                            succ_import = "1";
//                                                                        } else {
//                                                                        }
//                                                                    } else {
//                                                                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(),
//                                                                                Globals.Device_Code, myList.get(0), "0", myList.get(1), "0",
//                                                                                "1", modified_by, date, "N");
//                                                                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
//                                                                        if (l > 0) {
//                                                                            succ_import = "1";
//                                                                        } else {
//                                                                        }
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            myReader.close();
//                                                            if (succ_import.equals("1")) {
//                                                                database.setTransactionSuccessful();
//                                                                database.endTransaction();
//                                                                runOnUiThread(new Runnable() {
//                                                                    public void run() {
//                                                                        pDialog.dismiss();
//                                                                        getCategoryList("");
//                                                                        Toast.makeText(getBaseContext(), R.string.itm_catgy_imprt_succ,
//                                                                                Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//
//                                                            } else {
//                                                                database.endTransaction();
//                                                            }
//
//                                                        } catch (final Exception e) {
//                                                            database.endTransaction();
//                                                            runOnUiThread(new Runnable() {
//                                                                public void run() {
//                                                                    pDialog.dismiss();
//                                                                    Toast.makeText(getBaseContext(), e.getMessage(),
//                                                                            Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            });
//                                                        }
//                                                    } catch (Exception ex) {
//                                                        database.endTransaction();
//                                                        runOnUiThread(new Runnable() {
//                                                            public void run() {
//                                                                pDialog.dismiss();
//                                                            }
//                                                        });
//                                                        // TODO Auto-generated catch block
//                                                        ex.printStackTrace();
//                                                    }
//                                                }
//                                            };
//                                            t.start();
//                                        }
//                                    });
//                            AlertDialog alert = alertDialog.create();
//                            alert.show();
//                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        }
//                    });
            AlertDialog alert = alertDialog.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = lite_pos_registration.getproject_id();
                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }
                }

            });
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
            Button sbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
            sbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        return super.onOptionsItemSelected(item);
    }

    private String send_online_item_group() {
        Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String l = Item_Group.sendOnServer(getApplicationContext(), database, db, "Select * From item_group  WHERE is_push = 'N'",serial_no,Globals.syscode2,android_id,myKey,liccustomerid);
        return l;
    }

    private String getitemGroup() {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");
        String succ_bg = "0";

        // Call get item group api here
        System.out.println("get sync date"+ sys_sycntime.get_datetime());
        database.beginTransaction();
        if (sys_sycntime == null) {
            serverData = get_item_gp_from_server("");
        } else {
            serverData = get_item_gp_from_server(sys_sycntime.get_datetime());
        }

        try {
                final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmessage= jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    }

                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            } else if(strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage=strmessage;


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

    private String get_item_gp_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item_group");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
            System.out.println("namevalue get group"+ nameValuePairs);
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
            System.out.println("response get group "+serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;


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
        pDialog = new ProgressDialog(CategoryListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(CategoryListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(CategoryListActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 100) {
////                Uri uri1 = data.getData();
////                File myFile = new File("/sdcard/itemgroup.csv");
//                File myFile = new File(data.getData().getPath());
//                FileInputStream fIn = null;
//                try {
//
//                    fIn = new FileInputStream(myFile);
//
//                    BufferedReader myReader = new BufferedReader(
//                            new InputStreamReader(fIn));
//                    String aDataRow = "";
//                    String aBuffer = "";
//                    int count = 0;
//                    while ((aDataRow = myReader.readLine()) != null) {
//
//                        if (count == 0) {
//                            count = 1;
//                        } else {
//                            List<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));
//                            String item_group_code = myList.get(0);
//                            item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");
//                            if (item_group == null) {
//                                Item_Group item_group = new Item_Group(getApplicationContext(), null,
//                                        Globals.Device_Code, myList.get(0), "0", myList.get(1), "0",
//                                        "1", "", date, "N");
//                                long l = item_group.insertItem_Group(database);
//                                if (l > 0) {
//                                    succ_import = "1";
//                                } else {
//                                }
//                            } else {
//                                item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(),
//                                        Globals.Device_Code, myList.get(0), "0", myList.get(1), "0",
//                                        "1", "", date, "N");
//                                long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
//                                if (l > 0) {
//                                    succ_import = "1";
//                                } else {
//                                }
//                            }
//                        }
//
//                    }
//                    myReader.close();
//                    if (succ_import.equals("1")) {
//                        database.setTransactionSuccessful();
//                        database.endTransaction();
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                pDialog.dismiss();
//                                getCategoryList("");
//                                Toast.makeText(getBaseContext(), R.string.itm_catgy_imprt_succ,
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    } else {
//                        database.endTransaction();
//                    }
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            } else {
//                //  CommonMethods.ShowMessageBox(CraneTrackActivity.this, "Invalid file type");
//                Toast.makeText(CategoryListActivity.this, "Wrong File Selected ", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
