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

import org.phomellolitepos.Adapter.TaxItemCheckListAdapter;
import org.phomellolitepos.Adapter.TicketTaxCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.TaxItemCheck;
import org.phomellolitepos.CheckBoxClass.TicketTaxCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Ticket_Setup;
import org.phomellolitepos.database.Ticket_Setup_Tax;

import java.util.ArrayList;

public class TicketSetupTaxCheckListActivity extends AppCompatActivity {
    ListView contact_ck_list;
    TextView txt_title;
    Button btn_finish;
    String strID, operation, strPIT, sPrice;
    Database db;
    Tax_Master tax_master;
    Ticket_Setup_Tax ticket_setup_tax;
    Item item;
    ArrayList<Ticket_Setup_Tax> arrayList_cbg = new ArrayList<Ticket_Setup_Tax>();
    SQLiteDatabase database;
    ArrayList<Tax_Master> arrayList = new ArrayList<Tax_Master>();
    ArrayList<TicketTaxCheck> list = new ArrayList<TicketTaxCheck>();
    TicketTaxCheckListAdapter ticketTaxCheckListAdapter;
    ProgressDialog pDialog;
    ArrayList<String> category_code = new ArrayList<String>();
    MenuItem item_menu;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_setup_tax_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tax");
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

                pDialog = new ProgressDialog(TicketSetupTaxCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(TicketSetupTaxCheckListActivity.this, TicketSetupActivity.class);
                            intent.putExtra("strID",strID);
                            intent.putExtra("operation", operation);
                            intent.putExtra("PIT", strPIT);
                            intent.putExtra("price", sPrice);
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

        contact_ck_list = (ListView) findViewById(R.id.contact_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setText("Next");
//        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);

        Intent intent = getIntent();
        strID = intent.getStringExtra("strID");
        operation = intent.getStringExtra("operation");
        strPIT = intent.getStringExtra("PIT");
        sPrice = intent.getStringExtra("price");
        if (operation == null) {
            operation = "";
        }

        if (strPIT == null) {
            strPIT = "";
        }
        if (operation.equals("Add")) {
            fill_bussiness_spinner();
        } else if (operation.equals("Edit")) {
            arrayList_cbg = Ticket_Setup_Tax.getAllTicket_Setup_Tax(getApplicationContext(), "WHERE ref_id ='" + strID + "'", database);
            arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    TicketTaxCheck b = new TicketTaxCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {
                        String conct_code = arrayList_cbg.get(h).get_tax_id();
                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE is_active= '1' and tax_id ='" + conct_code + "'", database, db);
                        if (tax_master == null) {
                            b.setName("");
                            b.setSelected(false);
                        } else {
                            String name = tax_master.get_tax_name();
                            b.setName(arrayList.get(i).get_tax_name());
                            b.setSelected(false);
                            if (name.equals(arrayList.get(i).get_tax_name())) {
                                found = true;
                                b.setSelected(true);
                                break;
                            }
                        }
                    }
                    list.add(i, b);
                }
                if (list.size() > 0) {
                    ticketTaxCheckListAdapter = new TicketTaxCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    contact_ck_list.setVisibility(View.VISIBLE);
                    contact_ck_list.setAdapter(ticketTaxCheckListAdapter);
                    contact_ck_list.setTextFilterEnabled(true);
                    ticketTaxCheckListAdapter.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    contact_ck_list.setVisibility(View.GONE);
                }
            } else {
                fill_bussiness_spinner();
            }
        }

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(TicketSetupTaxCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            insert_item_tax();

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });
    }

    private void insert_item_tax() {
        String strCheck = "0";
        database.beginTransaction();
        arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
        if (arrayList.size() > 0) {
            // Update on 1 scrum
            long l = Ticket_Setup_Tax.deleteTicket_Setup_Tax(getApplicationContext(), "ref_id =?", new String[]{strID}, database);
            // Update on 1 scrum
            for (int i = 0; i < list.size(); i++) {
                TicketTaxCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_name ='" + name + "'", database, db);
                    String contact_code = tax_master.get_tax_id();
                    ticket_setup_tax = new Ticket_Setup_Tax(getApplicationContext(), null, strID, contact_code);
                    //Database transaction begins here

                    long a = ticket_setup_tax.insertTicket_Setup_Tax(database);
                    if (a > 0) {

                    }
                } else {
                    strCheck = "1";
                }
            }

            if (strPIT.equals("1")) {
                Double iTax = 0d;
                Double iPTax = 0d;

                Double iPrice = Double.parseDouble(sPrice);

                ArrayList<Ticket_Setup_Tax> ticket_setup_tax = Ticket_Setup_Tax.getAllTicket_Setup_Tax(getApplicationContext(), "WHERE ref_id='" + strID + "'", database);
                for (int a = 0; a < ticket_setup_tax.size(); a++) {
                    Ticket_Setup_Tax ticket_setup_tax1 = ticket_setup_tax.get(a);
                    String tax_id = ticket_setup_tax1.get_tax_id();
                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                    if (tax_master.get_tax_type().equals("P")) {
                        iTax = iTax + Double.parseDouble(tax_master.get_rate());

                    } else {

                        iPTax = iPTax + (Double.parseDouble(tax_master.get_rate()) * 100);
                    }
                }
                Double dPrice = ((iPrice * 100) - iPTax) / (100 + iTax);

                Ticket_Setup ticket_setup = Ticket_Setup.getTicket_Setup(getApplicationContext(), database, db, "WHERE id='" + strID + "'");
                ticket_setup.set_price(dPrice.toString());
                ticket_setup.set_new_price(sPrice);
                long h = ticket_setup.updateTicket_Setup("id=?", new String[]{strID}, database);
                if (h > 0) {

                }
            }

            if (strCheck.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent_category = new Intent(TicketSetupTaxCheckListActivity.this, TicketSetupCategoryCheckListActivity.class);
                        intent_category.putExtra("strID",strID);
                        intent_category.putExtra("operation", operation);
                        intent_category.putExtra("PIT", strPIT);
                        intent_category.putExtra("price", sPrice);
                        startActivity(intent_category);
                        finish();
                    }
                });


            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {
                       // Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(TicketSetupTaxCheckListActivity.this, TicketSetupCategoryCheckListActivity.class);
                        intent_category.putExtra("strID",strID);
                        intent_category.putExtra("operation", operation);
                        startActivity(intent_category);
                        finish();
                    }
                });

            }
        } else {
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(TicketSetupTaxCheckListActivity.this, TicketSetupCategoryCheckListActivity.class);
                    intent_category.putExtra("strID",strID);
                    intent_category.putExtra("operation", operation);
                    startActivity(intent_category);
                    finish();
                }
            });
        }
    }

    private void fill_bussiness_spinner() {
        arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), "WHERE is_active ='1'", database);
        for (int i = 0; i < arrayList.size(); i++) {
            TicketTaxCheck b = new TicketTaxCheck();
            b.setName(arrayList.get(i).get_tax_name());
            b.setSelected(false);
            list.add(b);
        }


        if (list.size() > 0) {
            ticketTaxCheckListAdapter = new TicketTaxCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            contact_ck_list.setVisibility(View.VISIBLE);
            contact_ck_list.setAdapter(ticketTaxCheckListAdapter);
            contact_ck_list.setTextFilterEnabled(true);
            ticketTaxCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            contact_ck_list.setVisibility(View.GONE);

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
            String ab = getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    TicketTaxCheck b = new TicketTaxCheck();
                    b.setName(arrayList.get(i).get_tax_name());
                    b.setSelected(true);
                    list.add(b);
                }

                ticketTaxCheckListAdapter = new TicketTaxCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(ticketTaxCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    TicketTaxCheck b = new TicketTaxCheck();
                    b.setName(arrayList.get(i).get_tax_name());
                    b.setSelected(false);
                    list.add(b);
                }

                ticketTaxCheckListAdapter = new TicketTaxCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(ticketTaxCheckListAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TicketSetupTaxCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);

                        Intent intent = new Intent(TicketSetupTaxCheckListActivity.this, TicketSetupActivity.class);
                        intent.putExtra("strID", strID);
                        intent.putExtra("operation", operation);
                        intent.putExtra("PIT", strPIT);
                        intent.putExtra("price", sPrice);
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
