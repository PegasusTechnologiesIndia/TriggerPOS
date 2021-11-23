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
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.CheckBoxClass.ItemGroupCheck;
import org.phomellolitepos.Adapter.ItemGroupCheckListAdpater;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */
public class ItemGroupCheckListActivity extends AppCompatActivity {
    ListView item_gp_ck_list;
    Button btn_next;
    TextView txt_title;
    String tax_id, operation;
    Database db;
    Sys_Tax_Type sys_tax_type;
    Tax_Detail tax_detail;
    ArrayList<Tax_Detail> arrayList_cbg = new ArrayList<Tax_Detail>();
    SQLiteDatabase database;
    ArrayList<Sys_Tax_Type> arrayList = new ArrayList<Sys_Tax_Type>();
    ArrayList<ItemGroupCheck> list = new ArrayList<ItemGroupCheck>();
    ItemGroupCheckListAdpater itemGroupCheckListAdpater;
    boolean flag = false;
    String strMenu = "";
    SharedPreferences prefs;
    ProgressDialog pDialog;
    MenuItem item_menu;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_group_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Item_group);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
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
                pDialog = new ProgressDialog(ItemGroupCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ItemGroupCheckListActivity.this, TaxActivity.class);
                            intent.putExtra("tax_id", tax_id);
                            intent.putExtra("operation", operation);
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
        item_gp_ck_list = (ListView) findViewById(R.id.item_gp_ck_list);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        tax_id = intent.getStringExtra("tax_id");
        operation = intent.getStringExtra("operation");

        if (operation.equals("Add")) {
            fill_item_group_spinner();
        } else if (operation.equals("Edit")) {
            arrayList_cbg = Tax_Detail.getAllTax_Detail(getApplicationContext(), "WHERE tax_id ='" + tax_id + "'", database);
            arrayList = Sys_Tax_Type.getAllSys_Tax_Type(getApplicationContext(), " WHERE is_active ='1'", database);

            if (arrayList_cbg.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    ItemGroupCheck b = new ItemGroupCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {
                        String bg_code = arrayList_cbg.get(h).get_tax_type_id();
                        sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "WHERE id ='" + bg_code + "'");
                        if (sys_tax_type == null) {
                            Toast.makeText(getApplicationContext(), "Invalid item Group", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(ItemGroupCheckListActivity.this, TaxListActivity.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            String name = sys_tax_type.get_type();
                            b.setName(arrayList.get(i).get_type());
                            b.setSelected(false);
                            if (name.equals(arrayList.get(i).get_type())) {
                                found = true;
                                b.setSelected(true);
                                break;
                            }
                        }

                    }
                    list.add(i, b);
                }

                if (list.size() > 0) {
                    itemGroupCheckListAdpater = new ItemGroupCheckListAdpater(this, list);

                    txt_title.setVisibility(View.GONE);
                    item_gp_ck_list.setVisibility(View.VISIBLE);
                    item_gp_ck_list.setAdapter(itemGroupCheckListAdpater);
                    item_gp_ck_list.setTextFilterEnabled(true);
                    itemGroupCheckListAdpater.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    item_gp_ck_list.setVisibility(View.GONE);
                }


            } else {
                fill_item_group_spinner();
            }
        }
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View view) {
                                            pDialog = new ProgressDialog(ItemGroupCheckListActivity.this);
                                            pDialog.setCancelable(false);
                                            pDialog.setMessage(getString(R.string.Wait_msg));
                                            pDialog.show();
                                            Thread timerThread = new Thread() {
                                                public void run() {
                                                    try {
                                                        sleep(1000);

                                                        insert_item_group_tax();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();

                                                    } finally {
                                                    }
                                                }
                                            };
                                            timerThread.start();

                                        }
                                    }
        );
    }

    private void insert_item_group_tax() {
        String strCheck = "0";
        arrayList = Sys_Tax_Type.getAllSys_Tax_Type(getApplicationContext(), " WHERE is_active ='1'", database);

        if (arrayList.size() > 0) {
            long l = Tax_Detail.deleteTax_Detail(getApplicationContext(), "tax_id=?", new String[]{tax_id}, database);

            for (int i = 0; i < list.size(); i++) {
                ItemGroupCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
                    sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "WHERE type ='" + name + "'");
                    String item_group_code = sys_tax_type.get_id();
                    tax_detail = new Tax_Detail(getApplicationContext(),null, tax_id, item_group_code);
                    database.beginTransaction();
                    long a = tax_detail.insertTax_Detail(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent intent_category = new Intent(ItemGroupCheckListActivity.this, OrderTypeCheckListActivity.class);
                                intent_category.putExtra("tax_id", tax_id);
                                intent_category.putExtra("operation", operation);
                                startActivity(intent_category);
                                finish();
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
                        Intent intent_category = new Intent(ItemGroupCheckListActivity.this, OrderTypeCheckListActivity.class);
                        intent_category.putExtra("tax_id", tax_id);
                        intent_category.putExtra("operation", operation);
                        startActivity(intent_category);
                        finish();
                    }
                });


            }
        } else {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent_category = new Intent(ItemGroupCheckListActivity.this, OrderTypeCheckListActivity.class);
                    intent_category.putExtra("tax_id", tax_id);
                    intent_category.putExtra("operation", operation);
                    startActivity(intent_category);
                    finish();
                }
            });


        }
    }

    private void fill_item_group_spinner() {
        arrayList = Sys_Tax_Type.getAllSys_Tax_Type(getApplicationContext(), "WHERE is_active ='1'", database);
        for (int i = 0; i < arrayList.size(); i++) {
            ItemGroupCheck b = new ItemGroupCheck();
            b.setName(arrayList.get(i).get_type());
            b.setSelected(false);
            list.add(b);
        }

        if (list.size() > 0) {
            itemGroupCheckListAdpater = new ItemGroupCheckListAdpater(this, list);

            txt_title.setVisibility(View.GONE);
            item_gp_ck_list.setVisibility(View.VISIBLE);
            item_gp_ck_list.setAdapter(itemGroupCheckListAdpater);
            item_gp_ck_list.setTextFilterEnabled(true);
            itemGroupCheckListAdpater.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            item_gp_ck_list.setVisibility(View.GONE);
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
        strMenu = item.getTitle() + "";

        if (id == R.id.action_settings) {
            list.clear();
            String ab=getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Sys_Tax_Type.getAllSys_Tax_Type(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ItemGroupCheck b = new ItemGroupCheck();
                    b.setName(arrayList.get(i).get_type());
                    b.setSelected(true);
                    list.add(b);
                }
                itemGroupCheckListAdpater = new ItemGroupCheckListAdpater(this, list);
                item_gp_ck_list.setAdapter(itemGroupCheckListAdpater);
            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Sys_Tax_Type.getAllSys_Tax_Type(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ItemGroupCheck b = new ItemGroupCheck();
                    b.setName(arrayList.get(i).get_type());
                    b.setSelected(false);
                    list.add(b);
                }
                itemGroupCheckListAdpater = new ItemGroupCheckListAdpater(this, list);
                item_gp_ck_list.setAdapter(itemGroupCheckListAdpater);
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ItemGroupCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ItemGroupCheckListActivity.this, TaxActivity.class);
                    intent.putExtra("tax_id", tax_id);
                    intent.putExtra("operation", operation);
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
