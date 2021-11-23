package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.Adapter.BussinessGroupCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.BusinessGroupCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */
public class ItemCheckListActivity extends AppCompatActivity {
    ListView item_ck_list;
    TextView txt_title;
    Button btn_finish;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Supplier_Group);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id==0){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ItemCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ItemCheckListActivity.this, ItemActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
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
        contact_code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("operation");

        if (operation == null) {
            operation = "Add";
            fill_bussiness_spinner();
        } else {
            arrayList_cbg = Contact_Bussiness_Group.getAllContact_Bussiness_Group(getApplicationContext(), database, db, "WHERE contact_code ='" + contact_code + "'");


            if (arrayList_cbg.size() > 0) {
                arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE Is_Active ='true'");
                for (int i = 0; i < arrayList.size(); i++) {
                    BusinessGroupCheck b = new BusinessGroupCheck();
                    b.setName(arrayList.get(i).get_name());
                    for (int h = 0; h < arrayList_cbg.size(); h++) {
                        String bg_code = arrayList_cbg.get(h).get_business_group_code();
                        bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE business_group_code ='" + bg_code + "'");
                        String name = bussiness_group.get_name();
                        if (name.equals(arrayList.get(i).get_name())) {
                            b.setSelected(true);
                            list.add(b);
                        } else {
                            b.setSelected(false);
                            list.add(b);
                        }
                    }
                    b.setSelected(false);
                    list.add(b);
                }

                if (list.size() > 0) {
                    bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);

                    txt_title.setVisibility(View.GONE);
                    item_ck_list.setVisibility(View.VISIBLE);
                    item_ck_list.setAdapter(bussinessGroupCheckListAdapter);
                    item_ck_list.setTextFilterEnabled(true);
                    bussinessGroupCheckListAdapter.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    item_ck_list.setVisibility(View.GONE);
                }

            }
        }

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        item_ck_list = (ListView) findViewById(R.id.item_ck_list);
        txt_title = (TextView) findViewById(R.id.txt_title);
        btn_finish = (Button) findViewById(R.id.btn_finish);


        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_contact_bussiness_group();
            }

        });

    }

    private void insert_contact_bussiness_group() {

        for (int i = 0; i < list.size(); i++) {
            BusinessGroupCheck b = list.get(i);
            if (b.isSelected()) {
                String name = b.getName();
                long l = Contact_Bussiness_Group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code =?", new String[]{contact_code});
                bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE name ='" + name + "'");
                String bussiness_group_code = bussiness_group.get_business_group_code();
                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), contact_code, bussiness_group_code);
                database.beginTransaction();
                long a = contact_bussiness_group.insertContact_Bussiness_Group(database);
                if (a > 0) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                    Intent intent_category = new Intent(ItemCheckListActivity.this, ItemListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            }
        }
    }

    private void fill_bussiness_spinner() {
        arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='true'");
        for (int i = 0; i < arrayList.size(); i++) {
            BusinessGroupCheck b = new BusinessGroupCheck();
            b.setName(arrayList.get(i).get_name());
            b.setSelected(true);
            list.add(b);
        }

        if (list.size() > 0) {
            bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);

            txt_title.setVisibility(View.GONE);
            item_ck_list.setVisibility(View.VISIBLE);
            item_ck_list.setAdapter(bussinessGroupCheckListAdapter);
            item_ck_list.setTextFilterEnabled(true);
            bussinessGroupCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            item_ck_list.setVisibility(View.GONE);
        }

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
            if (strMenu.equals(R.string.Select_All)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='true'");
                for (int i = 0; i < arrayList.size(); i++) {
                    BusinessGroupCheck b = new BusinessGroupCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(true);
                    list.add(b);
                }


                bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
                item_ck_list.setAdapter(bussinessGroupCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='true'");
                for (int i = 0; i < arrayList.size(); i++) {
                    BusinessGroupCheck b = new BusinessGroupCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(false);
                    list.add(b);
                }


                bussinessGroupCheckListAdapter = new BussinessGroupCheckListAdapter(this, list);
                item_ck_list.setAdapter(bussinessGroupCheckListAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ItemCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ItemCheckListActivity.this, ItemActivity.class);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();
    }
}


