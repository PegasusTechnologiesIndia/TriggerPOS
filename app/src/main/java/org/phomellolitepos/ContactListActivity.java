package org.phomellolitepos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.phomellolitepos.Adapter.ContactListAdapter;
import org.phomellolitepos.Adapter.ItemAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;

public class ContactListActivity extends AppCompatActivity {
    EditText edt_toolbar_contact_list;
    TextView contact_title;
    Contact contact;
    Address address;
    Address_Lookup address_lookup;
    Contact_Bussiness_Group contact_bussiness_group;
    ArrayList<Contact> arrayList;
    ContactListAdapter contactListAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    private RecyclerView recyclerView;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    String serial_no, android_id, myKey, device_id,imei_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_contact_list = (EditText) findViewById(R.id.edt_toolbar_contact_list);
        contact_title = (TextView) findViewById(R.id.contact_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);

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
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
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
                pDialog = new ProgressDialog(ContactListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(ContactListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ContactListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        // Getting contect list here
        getContactList("");
    }

    private void getContactList(String strFilter) {
        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        } else {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        }

        if (arrayList.size() > 0) {
            contactListAdapter = new ContactListAdapter(ContactListActivity.this, arrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            contact_title.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(contactListAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            contact_title.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact resultp = arrayList.get(position);
                String operation = "Edit";
                String item_code = resultp.get_contact_code();
                Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                intent.putExtra("contact_code", item_code);
                intent.putExtra("operation", operation);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        if(Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_contact_list.getText().toString().trim();
            strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' Or contact_1 Like '%" + strFilter + "%' Or email_1 Like '%" + strFilter + "%' )";
            edt_toolbar_contact_list.selectAll();
            getContactList(strFilter);
            return true;
        }

        if (id == R.id.action_send) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    ContactListActivity.this);
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
                                    ContactListActivity.this);
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
                                                pDialog = new ProgressDialog(ContactListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.Downloading_Contact));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        //Get contact from server
                                                        String result = send_online_contact();

                                                        if(result.equals("3")){

                                                            runOnUiThread(new Runnable() {
                                                                public void run() {

                                                                    if(Globals.responsemessage.equals("Device Not Found")){

                                                                        Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                                        lite_pos_device.setStatus("Out");
                                                                        long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                                        if (ct > 0) {

                                                                            Intent intent_category = new Intent(ContactListActivity.this, LoginActivity.class);
                                                                            startActivity(intent_category);
                                                                            finish();
                                                                        }


                                                                    }

                                                                }
                                                            });
                                                        }
                                                        String suss = getContact();
                                                        pDialog.dismiss();

                                                        switch (suss) {
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

                                                                                Intent intent_category = new Intent(ContactListActivity.this, LoginActivity.class);
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
                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = lite_pos_registration.getproject_id();

                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }else if (lite_pos_registration.getIndustry_Type().equals("3")||lite_pos_registration.getIndustry_Type().equals("6")){
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
        }
        return super.onOptionsItemSelected(item);
    }

    private String getAccContact() {
        String succ = "0";
        String serverData = get_Acc_Customer_server();
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

    private String get_Acc_Customer_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/customer_summary");
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
    }

    private String send_online_contact() {

        String conList = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'",liccustomerid,serial_no,android_id,myKey);
        return conList;
    }

    private String getContact() {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here
        if (sys_sycntime==null){
            serverData = get_contact_from_server("");
        }else {
            serverData = get_contact_from_server(sys_sycntime.get_datetime());
        }

        try {
            final JSONObject jsonObject_contact = new JSONObject(serverData);
            final String strStatus = jsonObject_contact.getString("status");
            final String strmsg = jsonObject_contact.getString("message");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");
                    if (sys_sycntime!=null){
                        sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    }


                    if (contact == null) {
                        contact = new Contact(getApplicationContext(), null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));
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
                        contact = new Contact(getApplicationContext(), contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));

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

    private String get_contact_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/contact");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
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
        pDialog = new ProgressDialog(ContactListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(ContactListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(ContactListActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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
}
