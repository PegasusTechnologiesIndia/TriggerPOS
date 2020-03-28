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

import org.phomellolitepos.Adapter.OrderTypeCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.OrderTypeCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;

public class OrderTypeCheckListActivity extends AppCompatActivity {

    ListView order_type_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String tax_id, operation;
    Database db;
    Order_Type order_type;
    Order_Type_Tax order_type_tax;
    ArrayList<Order_Type_Tax> arrayList_cbg = new ArrayList<Order_Type_Tax>();
    SQLiteDatabase database;
    ArrayList<Order_Type> arrayList = new ArrayList<Order_Type>();
    ArrayList<OrderTypeCheck> list = new ArrayList<OrderTypeCheck>();
    OrderTypeCheckListAdapter orderTypeCheckListAdapter;
    String strMenu = "";
    ProgressDialog pDialog;
    MenuItem item_menu;
    boolean found = false;
    Lite_POS_Device lite_pos_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Order_Type);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(),"" ,database);
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
                pDialog = new ProgressDialog(OrderTypeCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                            sleep(1000);

                            Intent intent = new Intent(OrderTypeCheckListActivity.this, TaxActivity.class);
                            intent.putExtra("tax_id", tax_id);
                            intent.putExtra("operation", operation);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        } finally {
                        }
                    }
                };
                timerThread.start();

            }
        });

        order_type_ck_list = (ListView) findViewById(R.id.order_type_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);

        Intent intent = getIntent();
        tax_id = intent.getStringExtra("tax_id");
        operation = intent.getStringExtra("operation");

        if (operation.equals("Add")) {

            fill_order_type_spinner();

        } else if (operation.equals("Edit")) {


            arrayList_cbg = Order_Type_Tax.getAllOrder_Type_Tax(getApplicationContext(), "WHERE tax_id ='" + tax_id + "'", database);

            arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), " WHERE is_active ='1'", database);
            if (arrayList.size() > 0) {

                if (arrayList_cbg.size() > 0) {

                    for (int i = 0; i < arrayList.size(); i++) {
                        OrderTypeCheck b = new OrderTypeCheck();

                        for (int h = 0; h < arrayList_cbg.size(); h++) {

                            String order_type_id = arrayList_cbg.get(h).get_order_type_id();
                            order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id ='" + order_type_id + "'", database, db);
                            String name = order_type.get_name();
                            b.setName(arrayList.get(i).get_name());
                            b.setSelected(false);
                            if (name.equals(arrayList.get(i).get_name())) {
                                found = true;
                                b.setSelected(true);
                                break;
                                //list.add(b);
                            }
                        }
                        list.add(i, b);
                    }

                    if (list.size() > 0) {
                        orderTypeCheckListAdapter = new OrderTypeCheckListAdapter(this, list);
                        txt_title.setVisibility(View.GONE);
                        order_type_ck_list.setVisibility(View.VISIBLE);
                        order_type_ck_list.setAdapter(orderTypeCheckListAdapter);
                        order_type_ck_list.setTextFilterEnabled(true);
                    } else {
                        txt_title.setVisibility(View.VISIBLE);
                        order_type_ck_list.setVisibility(View.GONE);
                    }
                } else {
                    fill_order_type_spinner();
                }

            } else {
                fill_order_type_spinner();
            }


        }

        btn_finish.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {

                                              pDialog = new ProgressDialog(OrderTypeCheckListActivity.this);
                                              pDialog.setCancelable(false);
                                              pDialog.setMessage(getString(R.string.Wait_msg));
                                              pDialog.show();
                                              Thread timerThread = new Thread() {
                                                  public void run() {
                                                      try {
                                                          sleep(1000);
                                                          insert_order_type_tax();

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

    private void insert_order_type_tax() {
        String strCheck = "0";
        arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), " WHERE is_active ='1'", database);

        if (arrayList.size() > 0) {
            long l = Order_Type_Tax.delete_Order_Type_Tax(getApplicationContext(), "tax_id=?", new String[]{tax_id}, database);

            for (int i = 0; i < list.size(); i++) {
                OrderTypeCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
                    order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE name ='" + name + "'", database, db);
                    String order_type_id = order_type.get_order_type_id();
                    order_type_tax = new Order_Type_Tax(getApplicationContext(), lite_pos_device.getLocation_Code(), tax_id, order_type_id);
                    database.beginTransaction();
                    long a = order_type_tax.insertOrder_Type_Tax(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                                Intent intent_category = new Intent(OrderTypeCheckListActivity.this, TaxListActivity.class);
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
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(OrderTypeCheckListActivity.this, TaxListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }
        } else {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(OrderTypeCheckListActivity.this, TaxListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });
        }
    }


    private void fill_order_type_spinner() {
        try {

            arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "WHERE is_active ='1'", database);
            if (arrayList.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    OrderTypeCheck b = new OrderTypeCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(false);
                    list.add(b);
                }
            }

            // attaching data adapter to spinner
            if (list.size() > 0) {
                orderTypeCheckListAdapter = new OrderTypeCheckListAdapter(this, list);
                txt_title.setVisibility(View.GONE);
                order_type_ck_list.setVisibility(View.VISIBLE);
                order_type_ck_list.setAdapter(orderTypeCheckListAdapter);
                order_type_ck_list.setTextFilterEnabled(true);
                orderTypeCheckListAdapter.notifyDataSetChanged();
            } else {
                txt_title.setVisibility(View.VISIBLE);
                order_type_ck_list.setVisibility(View.GONE);
                item_menu.setEnabled(false);
            }

        } catch (Exception ex) {
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

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        item_menu = menu.findItem(R.id.action_settings);
//        item_menu.setEnabled(true);
//        return true;
//    }

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
            String ab = getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    OrderTypeCheck b = new OrderTypeCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(true);
                    list.add(b);
                }
                orderTypeCheckListAdapter = new OrderTypeCheckListAdapter(this, list);
                order_type_ck_list.setAdapter(orderTypeCheckListAdapter);
            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    OrderTypeCheck b = new OrderTypeCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(false);
                    list.add(b);
                }
                orderTypeCheckListAdapter = new OrderTypeCheckListAdapter(this, list);
                order_type_ck_list.setAdapter(orderTypeCheckListAdapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(OrderTypeCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Intent intent = new Intent(OrderTypeCheckListActivity.this, TaxActivity.class);
                    intent.putExtra("tax_id", tax_id);
                    intent.putExtra("operation", operation);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        };
        timerThread.start();
    }
}
