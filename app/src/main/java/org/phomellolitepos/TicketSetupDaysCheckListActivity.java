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

import org.phomellolitepos.Adapter.TicketDaysCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.TicketDaysCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Ticket_Setup_Days;

import java.util.ArrayList;

public class TicketSetupDaysCheckListActivity extends AppCompatActivity {
    ListView contact_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String strID, operation, strPIT, sPrice;
    Database db;
    Item_Group item_group;
    Ticket_Setup_Days ticket_setup_days;
    ArrayList<Ticket_Setup_Days> arrayList_cbg = new ArrayList<Ticket_Setup_Days>();
    SQLiteDatabase database;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<TicketDaysCheck> list = new ArrayList<TicketDaysCheck>();
    TicketDaysCheckListAdapter ticketDaysCheckListAdapter;
    String strMenu = "";
    ProgressDialog pDialog;
    MenuItem item_menu;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_setup_days_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Days");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id1 = pref.getInt("id", 0);
        if (id1 == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(TicketSetupDaysCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                            sleep(1000);

                            Intent intent = new Intent(TicketSetupDaysCheckListActivity.this, TicketSetupCategoryCheckListActivity.class);
                            intent.putExtra("strID", strID);
                            intent.putExtra("operation", operation);
                            intent.putExtra("PIT", strPIT);
                            intent.putExtra("price", sPrice);
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

        contact_ck_list = (ListView) findViewById(R.id.contact_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);

        Intent intent = getIntent();
        strID = intent.getStringExtra("strID");
        operation = intent.getStringExtra("operation");
        strPIT = intent.getStringExtra("PIT");
        sPrice = intent.getStringExtra("price");

        arrayList.add("Sunday");
        arrayList.add("Monday");
        arrayList.add("Tuesday");
        arrayList.add("Wednesday");
        arrayList.add("Thrusday");
        arrayList.add("Friday");
        arrayList.add("Saturday");

        if (operation.equals("Add")) {

            fill_order_type_spinner();

        } else if (operation.equals("Edit")) {


            arrayList_cbg = Ticket_Setup_Days.getAllTicket_Setup_Days(getApplicationContext(), "WHERE ref_id ='" + strID + "'", database);

            //arrayList = Item_Group.getAllItem_Group(getApplicationContext(), " WHERE is_active ='1'", database,db);

            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    TicketDaysCheck b = new TicketDaysCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {

//                            String category_code = arrayList_cbg.get(h).get_category_id();
//                            item_group = Item_Group.getItem_Group(getApplicationContext(),  database, db,"WHERE category_code ='" + category_code + "'");
                        String name = arrayList_cbg.get(h).get_days();
                        b.setName(arrayList.get(i));
                        b.setSelected(false);
                        if (name.equals(arrayList.get(i))) {
                            found = true;
                            b.setSelected(true);
                            break;
                            //list.add(b);
                        }

                    }
                    list.add(i, b);
                }


                if (list.size() > 0) {
                    ticketDaysCheckListAdapter = new TicketDaysCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    contact_ck_list.setVisibility(View.VISIBLE);
                    contact_ck_list.setAdapter(ticketDaysCheckListAdapter);
                    contact_ck_list.setTextFilterEnabled(true);
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    contact_ck_list.setVisibility(View.GONE);
                }

            } else {
                fill_order_type_spinner();
            }


        }

        btn_finish.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {

                                              pDialog = new ProgressDialog(TicketSetupDaysCheckListActivity.this);
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
        //arrayList = Item_Group.getAllItem_Group(getApplicationContext(), " WHERE is_active ='1'", database,db);

        if (arrayList.size() > 0) {
            long l = Ticket_Setup_Days.deleteTicket_Setup_Days(getApplicationContext(), "ref_id=?", new String[]{strID}, database);

            for (int i = 0; i < list.size(); i++) {
                TicketDaysCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
//                    item_group = Item_Group.getItem_Group(getApplicationContext(),database, db, "WHERE name ='" + name + "'");
//                    String category_code = item_group.get_item_group_code();
                    ticket_setup_days = new Ticket_Setup_Days(getApplicationContext(), null, strID, name);
                    database.beginTransaction();
                    long a = ticket_setup_days.insertTicket_Setup_Days(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                                Intent intent_category = new Intent(TicketSetupDaysCheckListActivity.this, TicketSetupListActivity.class);
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
                        Intent intent_category = new Intent(TicketSetupDaysCheckListActivity.this, TicketSetupListActivity.class);
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
                    Intent intent_category = new Intent(TicketSetupDaysCheckListActivity.this, TicketSetupListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });
        }
    }


    private void fill_order_type_spinner() {
        try {

            if (arrayList.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TicketDaysCheck b = new TicketDaysCheck();
                    b.setName(arrayList.get(i));
                    b.setSelected(false);
                    list.add(b);
                }
            }

            // attaching data adapter to spinner
            if (list.size() > 0) {
                ticketDaysCheckListAdapter = new TicketDaysCheckListAdapter(this, list);
                txt_title.setVisibility(View.GONE);
                contact_ck_list.setVisibility(View.VISIBLE);
                contact_ck_list.setAdapter(ticketDaysCheckListAdapter);
                contact_ck_list.setTextFilterEnabled(true);
                ticketDaysCheckListAdapter.notifyDataSetChanged();
            } else {
                txt_title.setVisibility(View.VISIBLE);
                contact_ck_list.setVisibility(View.GONE);
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
                //arrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1'", database,db);
                for (int i = 0; i < arrayList.size(); i++) {
                    TicketDaysCheck b = new TicketDaysCheck();
                    b.setName(arrayList.get(i));
                    b.setSelected(true);
                    list.add(b);
                }
                ticketDaysCheckListAdapter = new TicketDaysCheckListAdapter(this, list);
                contact_ck_list.setAdapter(ticketDaysCheckListAdapter);
            } else {
                item.setTitle(R.string.Select_All);
                //arrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1'", database,db);
                for (int i = 0; i < arrayList.size(); i++) {
                    TicketDaysCheck b = new TicketDaysCheck();
                    b.setName(arrayList.get(i));
                    b.setSelected(false);
                    list.add(b);
                }
                ticketDaysCheckListAdapter = new TicketDaysCheckListAdapter(this, list);
                contact_ck_list.setAdapter(ticketDaysCheckListAdapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TicketSetupDaysCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Intent intent = new Intent(TicketSetupDaysCheckListActivity.this, TicketSetupCategoryCheckListActivity.class);
                    intent.putExtra("strID", strID);
                    intent.putExtra("operation", operation);
                    intent.putExtra("PIT", strPIT);
                    intent.putExtra("price", sPrice);
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
