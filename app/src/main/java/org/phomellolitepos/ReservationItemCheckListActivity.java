package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.OrderTypeCheckListAdapter;
import org.phomellolitepos.Adapter.ReservationItemCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.OrderTypeCheck;
import org.phomellolitepos.CheckBoxClass.ReservationItemCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Reservation_Detail;

import java.util.ArrayList;

public class ReservationItemCheckListActivity extends AppCompatActivity {
    ListView order_type_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String tax_id, operation;
    Database db;
    Item item;
    Reservation_Detail reservation_detail;
    ArrayList<Reservation_Detail> arrayList_cbg = new ArrayList<Reservation_Detail>();
    SQLiteDatabase database;
    ArrayList<Item> arrayList = new ArrayList<Item>();
    ArrayList<ReservationItemCheck> list = new ArrayList<ReservationItemCheck>();
    ReservationItemCheckListAdapter reservationItemCheckListAdapter;
    String strMenu = "";
    ProgressDialog pDialog;
    boolean found = false;
    MenuItem item_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_item_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Item);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

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
                pDialog = new ProgressDialog(ReservationItemCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                            sleep(1000);

                            Intent intent = new Intent(ReservationItemCheckListActivity.this, ReservationActivity.class);
                            intent.putExtra("id", tax_id);
                            intent.putExtra("operation", operation);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

//                        } catch (InterruptedException e) {
//                            e.printStackTrace();

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
        tax_id = intent.getStringExtra("id");
        operation = intent.getStringExtra("operation");

        if (operation.equals("Add")) {

            fill_order_type_spinner();

        } else if (operation.equals("Edit")) {

            arrayList_cbg = Reservation_Detail.getAllReservation_Detail(getApplicationContext(), "WHERE ref_id ='" + tax_id + "'", database);

            arrayList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1'", database);

            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    ReservationItemCheck b = new ReservationItemCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {

                        String order_type_id = arrayList_cbg.get(h).get_item_code();
                        item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + order_type_id + "'", database, db);
                        String name = item.get_item_name();
                        b.setName(arrayList.get(i).get_item_name());
                        b.setSelected(false);
                        if (name.equals(arrayList.get(i).get_item_name())) {
                            found = true;
                            b.setSelected(true);
                            break;
                            //list.add(b);
                        }

                    }
                    list.add(i, b);
                }

                if (list.size() > 0) {
                    reservationItemCheckListAdapter = new ReservationItemCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    order_type_ck_list.setVisibility(View.VISIBLE);
                    order_type_ck_list.setAdapter(reservationItemCheckListAdapter);
                    order_type_ck_list.setTextFilterEnabled(true);
                    reservationItemCheckListAdapter.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    order_type_ck_list.setVisibility(View.GONE);
                }

            } else {
                fill_order_type_spinner();
            }


        }

        btn_finish.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {

                                              pDialog = new ProgressDialog(ReservationItemCheckListActivity.this);
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

        arrayList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1'", database);

        if (arrayList.size() > 0) {
            long l = Reservation_Detail.deleteReservation_Detail(getApplicationContext(), "ref_id=?", new String[]{tax_id}, database);

            for (int i = 0; i < list.size(); i++) {
                ReservationItemCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();

                    item = Item.getItem(getApplicationContext(), "WHERE item_name ='" + name + "'", database, db);
                    String item_code = item.get_item_code();

                    reservation_detail = new Reservation_Detail(getApplicationContext(), null, tax_id, item_code);

                    database.beginTransaction();
                    long a = reservation_detail.insertReservation_Detail(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                                Intent intent_category = new Intent(ReservationItemCheckListActivity.this, ReservationListActivity.class);
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
                        Intent intent_category = new Intent(ReservationItemCheckListActivity.this, ReservationListActivity.class);
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
                    Intent intent_category = new Intent(ReservationItemCheckListActivity.this, ReservationListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });

        }
    }


    private void fill_order_type_spinner() {

        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active ='1'", database);
        for (int i = 0; i < arrayList.size(); i++) {
            ReservationItemCheck b = new ReservationItemCheck();
            b.setName(arrayList.get(i).get_item_name());
            b.setSelected(false);
            list.add(b);
        }

        // attaching data adapter to spinner

        if (list.size() > 0) {
            reservationItemCheckListAdapter = new ReservationItemCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            order_type_ck_list.setVisibility(View.VISIBLE);
            order_type_ck_list.setAdapter(reservationItemCheckListAdapter);
            order_type_ck_list.setTextFilterEnabled(true);
            reservationItemCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            order_type_ck_list.setVisibility(View.GONE);
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
            String ab = getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ReservationItemCheck b = new ReservationItemCheck();
                    b.setName(arrayList.get(i).get_item_name());
                    b.setSelected(true);
                    list.add(b);
                }

                reservationItemCheckListAdapter = new ReservationItemCheckListAdapter(this, list);
                // attaching data adapter to spinner
                order_type_ck_list.setAdapter(reservationItemCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ReservationItemCheck b = new ReservationItemCheck();
                    b.setName(arrayList.get(i).get_item_name());
                    b.setSelected(false);
                    list.add(b);
                }

                reservationItemCheckListAdapter = new ReservationItemCheckListAdapter(this, list);
                // attaching data adapter to spinner
                order_type_ck_list.setAdapter(reservationItemCheckListAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ReservationItemCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
//                            sleep(1000);

                    Intent intent = new Intent(ReservationItemCheckListActivity.this, ReservationActivity.class);
                    intent.putExtra("id", tax_id);
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
