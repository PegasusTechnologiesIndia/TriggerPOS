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

import org.phomellolitepos.Adapter.ContactCheckListAdapter;
import org.phomellolitepos.Adapter.TaxItemCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.ContactCheck;
import org.phomellolitepos.CheckBoxClass.TaxItemCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Tax_Master;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

public class TaxCheckListActivity extends AppCompatActivity {

    ListView contact_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String item_code, operation, strPIT, sPrice, strItemTax, strSelectedCategory;
    Database db;
    Tax_Master tax_master;
    Item_Group_Tax item_group_tax;
    Item item;
    ArrayList<Item_Group_Tax> arrayList_cbg = new ArrayList<Item_Group_Tax>();
    SQLiteDatabase database;
    ArrayList<Tax_Master> arrayList = new ArrayList<Tax_Master>();
    ArrayList<TaxItemCheck> list = new ArrayList<TaxItemCheck>();
    TaxItemCheckListAdapter taxItemCheckListAdapter;
    ProgressDialog pDialog;
    ArrayList<String> category_code = new ArrayList<String>();
    MenuItem item_menu;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.Item_Tax);
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

                pDialog = new ProgressDialog(TaxCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                if (strItemTax.equals("IT")) {
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(TaxCheckListActivity.this, ItemTaxActivity.class);
//                                intent.putStringArrayListExtra("item_code", category_code);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
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
                } else {
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);

                                Intent intent = new Intent(TaxCheckListActivity.this, ItemActivity.class);
                                intent.putExtra("item_code", item_code);
                                intent.putExtra("operation", operation);
                                intent.putExtra("PIT", strPIT);
                                intent.putExtra("sprice", sPrice);
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
        });

        contact_ck_list = (ListView) findViewById(R.id.contact_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        item_code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("operation");
        strItemTax = intent.getStringExtra("item_tax");
        strPIT = intent.getStringExtra("PIT");
        sPrice = intent.getStringExtra("sprice");
        if (operation == null) {
            operation = "";
        }

        if (strItemTax == null) {
            strItemTax = "";
        }

        if (strPIT == null) {
            strPIT = "";
        }
        if (operation.equals("Add")) {
            fill_bussiness_spinner();
        } else if (strItemTax.equals("IT")) {
            try {
                category_code = intent.getStringArrayListExtra("item_code");

                if (category_code.size() > 0) {
                    strSelectedCategory = "";
                    for (int i = 0; i < category_code.size(); i++) {
                        strSelectedCategory = strSelectedCategory + "'" + category_code.get(i).toString() + "',";
                    }
                    strSelectedCategory = strSelectedCategory.substring(0, strSelectedCategory.length() - 1).toString();
                } else {
                }
            } catch (Exception ex) {
                strSelectedCategory = "";
            }
            fill_bussiness_spinner();
        } else if (operation.equals("Edit")) {
            arrayList_cbg = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "WHERE item_group_code ='" + item_code + "'", database, db);
            arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    TaxItemCheck b = new TaxItemCheck();

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
                    taxItemCheckListAdapter = new TaxItemCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    contact_ck_list.setVisibility(View.VISIBLE);
                    contact_ck_list.setAdapter(taxItemCheckListAdapter);
                    contact_ck_list.setTextFilterEnabled(true);
                    taxItemCheckListAdapter.notifyDataSetChanged();
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
                pDialog = new ProgressDialog(TaxCheckListActivity.this);
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
        String loc;
        try {
            loc = Globals.objLPD.getLocation_Code();
        } catch (Exception ex) {
            loc = "1";
        }
        String strCheck = "0";
        database.beginTransaction();
        arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
        if (arrayList.size() > 0) {
            if (strItemTax.equals("IT")) {
                for (int i = 0; i < category_code.size(); i++) {
                    long l = Item_Group_Tax.delete_Item_Group_Tax(getApplicationContext(), "item_group_code =?", new String[]{category_code.get(i)}, database);
                    for (int j = 0; j < list.size(); j++) {
                        TaxItemCheck b = list.get(j);
                        if (b.isSelected()) {
                            String name = b.getName();
                            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_name ='" + name + "'", database, db);
                            String contact_code = tax_master.get_tax_id();
                            item_group_tax = new Item_Group_Tax(getApplicationContext(), loc, contact_code, category_code.get(i));
                            //Database transaction begins here

                            long a = item_group_tax.insertItem_Group_Tax(database);
                            if (a > 0) {

                            }
                        } else {
                            strCheck = "1";
                        }
                    }
                }
            } else {
                // Update on 1 scrum
                long l = Item_Group_Tax.delete_Item_Group_Tax(getApplicationContext(), "item_group_code =?", new String[]{item_code}, database);
                // Update on 1 scrum
                for (int i = 0; i < list.size(); i++) {
                    TaxItemCheck b = list.get(i);
                    if (b.isSelected()) {
                        String name = b.getName();
                        tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_name ='" + name + "'", database, db);
                        String contact_code = tax_master.get_tax_id();
                        item_group_tax = new Item_Group_Tax(getApplicationContext(), loc, contact_code, item_code);
                        //Database transaction begins here

                        long a = item_group_tax.insertItem_Group_Tax(database);
                        if (a > 0) {

                        }
                    } else {
                        strCheck = "1";
                    }
                }
            }

            if (strPIT.equals("1")) {
                Double iTax = 0d;
                Double iPTax = 0d;

                Double iPrice = Double.parseDouble(sPrice);

                ArrayList<Item_Group_Tax> item_group_tax = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "WHERE item_group_code='" + item_code + "'", database, db);
                for (int a = 0; a < item_group_tax.size(); a++) {
                    Item_Group_Tax item_group_tax1 = item_group_tax.get(a);
                    String tax_id = item_group_tax1.get_tax_id();
                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                    if (tax_master.get_tax_type().equals("P")) {
                        iTax = iTax + Double.parseDouble(tax_master.get_rate());

                    } else {

                        iPTax = iPTax + (Double.parseDouble(tax_master.get_rate()) * 100);
                    }
                }
                Double dPrice = ((iPrice * 100) - iPTax) / (100 + iTax);

                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code='" + item_code + "'", database);
                item_location.set_selling_price(dPrice.toString());
                item_location.set_new_sell_price(sPrice);
                long h = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                if (h > 0) {

                }
            }

            if (strCheck.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                if (strItemTax.equals("IT")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(TaxCheckListActivity.this, ItemTaxActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(TaxCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }


            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                if (strItemTax.equals("IT")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(TaxCheckListActivity.this, ItemTaxActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(TaxCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }
            }
        } else {
            database.endTransaction();
            pDialog.dismiss();
            if (strItemTax.equals("IT")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(TaxCheckListActivity.this, ItemTaxActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(TaxCheckListActivity.this, ItemListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }

        }
    }

    private void fill_bussiness_spinner() {
        arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), "WHERE is_active ='1'", database);
        for (int i = 0; i < arrayList.size(); i++) {
            TaxItemCheck b = new TaxItemCheck();
            b.setName(arrayList.get(i).get_tax_name());
            b.setSelected(false);
            list.add(b);
        }


        if (list.size() > 0) {
            taxItemCheckListAdapter = new TaxItemCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            contact_ck_list.setVisibility(View.VISIBLE);
            contact_ck_list.setAdapter(taxItemCheckListAdapter);
            contact_ck_list.setTextFilterEnabled(true);
            taxItemCheckListAdapter.notifyDataSetChanged();
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
                    TaxItemCheck b = new TaxItemCheck();
                    b.setName(arrayList.get(i).get_tax_name());
                    b.setSelected(true);
                    list.add(b);
                }

                taxItemCheckListAdapter = new TaxItemCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(taxItemCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), " WHERE is_active ='1'", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    TaxItemCheck b = new TaxItemCheck();
                    b.setName(arrayList.get(i).get_tax_name());
                    b.setSelected(false);
                    list.add(b);
                }

                taxItemCheckListAdapter = new TaxItemCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(taxItemCheckListAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TaxCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        if (strItemTax.equals("IT")) {
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);

                        Intent intent = new Intent(TaxCheckListActivity.this, ItemTaxActivity.class);
//                        intent.putStringArrayListExtra("item_code", category_code);
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
        } else {
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);

                        Intent intent = new Intent(TaxCheckListActivity.this, ItemActivity.class);
                        intent.putExtra("item_code", item_code);
                        intent.putExtra("operation", operation);
                        intent.putExtra("PIT", strPIT);
                        intent.putExtra("sprice", sPrice);
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

}


