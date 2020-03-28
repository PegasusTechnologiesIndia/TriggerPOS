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

import org.phomellolitepos.Adapter.ContactCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.ContactCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Supplier;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ContactCheckListActivity extends AppCompatActivity {
    ListView contact_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String item_code, operation;
    Database db;
    Contact contact;
    Item_Supplier item_supplier;
    Item item;
    ArrayList<Item_Supplier> arrayList_cbg = new ArrayList<Item_Supplier>();
    SQLiteDatabase database;
    ArrayList<Contact> arrayList = new ArrayList<Contact>();
    ArrayList<ContactCheck> list = new ArrayList<ContactCheck>();
    ContactCheckListAdapter contactCheckListAdapter;
    ProgressDialog pDialog;
    MenuItem item_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Item_Supplier);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
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

                pDialog = new ProgressDialog(ContactCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ContactCheckListActivity.this, ItemActivity.class);
                            intent.putExtra("item_code", item_code);
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
        contact_ck_list = (ListView) findViewById(R.id.contact_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        item_code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("operation");

        if (operation.equals("Add")) {
            fill_bussiness_spinner();
        } else if (operation.equals("Edit")) {
            arrayList_cbg = Item_Supplier.getAllItem_Supplier(getApplicationContext(), "WHERE item_code ='" + item_code + "'");
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    ContactCheck b = new ContactCheck();
                    boolean found = false;
                    for (int h = 0; h < arrayList_cbg.size(); h++) {
                        String conct_code = arrayList_cbg.get(h).get_contact_code();
                        String name = "";
                        try {
                        contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code ='" + conct_code + "'");
                        name = contact.get_name();
                        }catch (Exception ex){
}
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
                    contactCheckListAdapter = new ContactCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    contact_ck_list.setVisibility(View.VISIBLE);
                    contact_ck_list.setAdapter(contactCheckListAdapter);
                    contact_ck_list.setTextFilterEnabled(true);
                    contactCheckListAdapter.notifyDataSetChanged();
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
                pDialog = new ProgressDialog(ContactCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            insert_item_supplier();

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

    private void insert_item_supplier() {
        String strCheck = "0";
        arrayList = Contact.getAllContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
        if (arrayList.size() > 0) {
            long l = Item_Supplier.delete_Item_Supplier(getApplicationContext(), "item_code =?", new String[]{item_code}, database);

            for (int i = 0; i < list.size(); i++) {
                ContactCheck b = list.get(i);
                if (b.isSelected()) {
                    String name = b.getName();
                    contact = Contact.getContact(getApplicationContext(), database, db, "WHERE name ='" + name + "'");
                    String contact_code = contact.get_contact_code();
                    item_supplier = new Item_Supplier(getApplicationContext(), null, item_code, contact_code);
                    //Database transaction begins here
                    database.beginTransaction();
                    long a = item_supplier.insertItem_Supplier(database);
                    if (a > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                                Intent intent_category = new Intent(ContactCheckListActivity.this, ItemListActivity.class);
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
                        Intent intent_category = new Intent(ContactCheckListActivity.this, ItemListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });

            }
        } else {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent_category = new Intent(ContactCheckListActivity.this, ItemListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });
          
        }
    }

    private void fill_bussiness_spinner() {
        arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active ='1'");
        for (int i = 0; i < arrayList.size(); i++) {
            ContactCheck b = new ContactCheck();
            b.setName(arrayList.get(i).get_name());
            b.setSelected(false);
            list.add(b);
        }



        if (list.size() > 0) {
            contactCheckListAdapter = new ContactCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            contact_ck_list.setVisibility(View.VISIBLE);
            contact_ck_list.setAdapter(contactCheckListAdapter);
            contact_ck_list.setTextFilterEnabled(true);
            contactCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            contact_ck_list.setVisibility(View.GONE);
//            item_menu.setEnabled(false);
        }
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
       String strMenu = item.getTitle() + "";
        if (id == R.id.action_settings) {
            list.clear();
            String ab=getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Contact.getAllContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
                for (int i = 0; i < arrayList.size(); i++) {
                    ContactCheck b = new ContactCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(true);
                    list.add(b);
                }

                contactCheckListAdapter = new ContactCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(contactCheckListAdapter);

            }else {
                item.setTitle(R.string.Select_All);
                arrayList = Contact.getAllContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
                for (int i = 0; i < arrayList.size(); i++) {
                    ContactCheck b = new ContactCheck();
                    b.setName(arrayList.get(i).get_name());
                    b.setSelected(false);
                    list.add(b);
                }

                contactCheckListAdapter = new ContactCheckListAdapter(this, list);
                // attaching data adapter to spinner
                contact_ck_list.setAdapter(contactCheckListAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ContactCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ContactCheckListActivity.this, ItemActivity.class);
                    intent.putExtra("item_code", item_code);
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
