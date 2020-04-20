package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.Adapter.BussinessGroupCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.BusinessGroupCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Settings;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class BussinessGroupCheckListActivity extends AppCompatActivity {
    ListView bg_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String contact_code, operation;
    Database db;
    Bussiness_Group bussiness_group;
    Contact_Bussiness_Group contact_bussiness_group;
    ArrayList<Contact_Bussiness_Group> arrayList_cbg = new ArrayList<Contact_Bussiness_Group>();
    SQLiteDatabase database;
    ArrayList<Bussiness_Group> arrayList = new ArrayList<Bussiness_Group>();
    ArrayList<BusinessGroupCheck> list = new ArrayList<BusinessGroupCheck>();
    BussinessGroupCheckListAdapter bussinessGroupCheckListAdapter;
    ProgressDialog pDialog;
    MenuItem item_menu;
    boolean found = false;
    Lite_POS_Device liteposdevice;
    Settings settings;
    String liccustomerid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_group_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Bussiness_Group);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BussinessGroupCheckListActivity.this, ContactActivity.class);
                intent.putExtra("contact_code", contact_code);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        bg_ck_list = (ListView) findViewById(R.id.bg_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        contact_code = intent.getStringExtra("contact_code");
        operation = intent.getStringExtra("operation");

        if (operation.equals("Add")) {
            fill_bussiness_spinner();
        } else if (operation.equals("Edit")) {
            try {

                arrayList_cbg = Contact_Bussiness_Group.getAllContact_Bussiness_Group(getApplicationContext(), database, db, " WHERE contact_code ='" + contact_code + "'");
                arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1'  Order By name asc");

                if (arrayList_cbg.size() > 0) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        BusinessGroupCheck b = new BusinessGroupCheck();

                        for (int h = 0; h < arrayList_cbg.size(); h++) {
                            String bg_code = arrayList_cbg.get(h).get_business_group_code();
                            bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, " WHERE business_group_code ='" + bg_code + "'");
                            String name = bussiness_group.get_name();
                            b.setName(arrayList.get(i).get_name());
                            b.setSelected(false);
                            if (name.equals(arrayList.get(i).get_name())) {
                                found = true;
                                b.setSelected(true);
                                break;
                            }
                        }
                        list.add(i, b);
                    }

                    if (list.size() > 0) {
                        bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
                        txt_title.setVisibility(View.GONE);
                        bg_ck_list.setVisibility(View.VISIBLE);
                        bg_ck_list.setAdapter(bussinessGroupCheckListAdapter);
                        bg_ck_list.setTextFilterEnabled(true);
                        bussinessGroupCheckListAdapter.notifyDataSetChanged();
                    } else {
                        txt_title.setVisibility(View.VISIBLE);
                        bg_ck_list.setVisibility(View.GONE);
                    }
                } else {
                    fill_bussiness_spinner();
                }
            } catch (Exception e) {}
        }

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(BussinessGroupCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            insert_contact_bussiness_group();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {}
                    }
                };
                timerThread.start();
            }
        });
    }

    private void insert_contact_bussiness_group() {
        String strCheck = "0";
        arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1'");

        if (arrayList.size() > 0) {
            long l = Contact_Bussiness_Group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code =?", new String[]{contact_code});

            for (int i = 0; i < list.size(); i++) {
                BusinessGroupCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
                    bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE name ='" + name + "'");
                    String bussiness_group_code = bussiness_group.get_business_group_code();
                    contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), contact_code, bussiness_group_code);
                    database.beginTransaction();
                    long a = contact_bussiness_group.insertContact_Bussiness_Group(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        if (Globals.objLPR.getproject_id().equals("cloud")) {
                            try {
                                String result = send_online_contact();
                            } catch (Exception ex) {}
                        }
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (settings.get_Home_Layout().equals("0")) {
                                    try {
                                        Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, MainActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    } finally {
                                    }
                                } else {
                                    try {
                                        Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, Main2Activity.class);
                                        startActivity(intent_category);
                                        finish();
                                    } finally {
                                    }
                                }

                            }
                        });
                    }
                } else {
                    strCheck = "1";
                }
            }

            if (strCheck.equals("1")) {
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, MainActivity.class);
                                startActivity(intent_category);
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, Main2Activity.class);
                                startActivity(intent_category);
                                finish();
                            } finally {
                            }
                        }
                    }
                });
            }
        } else {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, MainActivity.class);
                            startActivity(intent_category);
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent_category = new Intent(BussinessGroupCheckListActivity.this, Main2Activity.class);
                            startActivity(intent_category);
                            finish();
                        } finally {
                        }
                    }
                }
            });
        }
    }

    private void fill_bussiness_spinner() {
        arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='1' Order By name asc");
        for (int i = 0; i < arrayList.size(); i++) {
            BusinessGroupCheck b = new BusinessGroupCheck();
            b.setName(arrayList.get(i).get_name());
            b.setSelected(false);
            list.add(b);
        }

        if (list.size() > 0) {
            bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            bg_ck_list.setVisibility(View.VISIBLE);
            bg_ck_list.setAdapter(bussinessGroupCheckListAdapter);
            bg_ck_list.setTextFilterEnabled(true);
            bussinessGroupCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            bg_ck_list.setVisibility(View.GONE);
//            item_menu.setEnabled(false);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_menu = menu.findItem(R.id.action_settings);
        if (found == true) {
            item_menu.setTitle(R.string.Deselect_All);
        } else {
            item_menu.setTitle(R.string.Select_All);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_all_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String strMenu = item.getTitle() + "";
        if (id == R.id.action_settings) {
            list.clear();
            if (strMenu.equals(getString(R.string.Select_All))) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='1' Order By name asc");
                for (int i = 0; i < arrayList.size(); i++) {
                    BusinessGroupCheck b = new BusinessGroupCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(true);
                    list.add(b);
                }
                bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
                bg_ck_list.setAdapter(bussinessGroupCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                for (int i = 0; i < arrayList.size(); i++) {
                    BusinessGroupCheck b = new BusinessGroupCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(false);
                    list.add(b);
                }
                bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
                bg_ck_list.setAdapter(bussinessGroupCheckListAdapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String send_online_contact() {

        String conList = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'",liccustomerid,"","","");
        return conList;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BussinessGroupCheckListActivity.this, ContactActivity.class);
        intent.putExtra("contact_code", contact_code);
        intent.putExtra("operation", operation);
        startActivity(intent);
        finish();
    }
}
