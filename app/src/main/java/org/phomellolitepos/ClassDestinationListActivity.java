package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.ClassCategoryListAdapter;
import org.phomellolitepos.Adapter.DestinationListAdapter;
import org.phomellolitepos.Adapter.ManufactureListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassDestinationListActivity extends AppCompatActivity {
    EditText edt_toolbar_manufacture_list;
    TextView manufacture_title;
    ClassCategoryListAdapter classCategoryListAdapter;
    DestinationListAdapter destinationListAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_destination_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_manufacture_list = (EditText) findViewById(R.id.edt_toolbar_manufacture_list);
        manufacture_title = (TextView) findViewById(R.id.manufacture_title);

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
                pDialog = new ProgressDialog(ClassDestinationListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        Globals.TicketCategory="";
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ClassDestinationListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ClassDestinationListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(ClassDestinationListActivity.this, TicketCategoryActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        getClassDestinationList("");
    }

    private void getClassDestinationList(String strFilter) {
        if (Globals.TicketCategory.equals("Class")) {
           ArrayList<Item_Group> arrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By item_group_name asc", database,db);
            ListView manufacture_list = (ListView) findViewById(R.id.manufacture_list);
            if (arrayList.size() > 0) {
                classCategoryListAdapter = new ClassCategoryListAdapter(ClassDestinationListActivity.this, arrayList);
                manufacture_title.setVisibility(View.GONE);
                manufacture_list.setVisibility(View.VISIBLE);
                manufacture_list.setAdapter(classCategoryListAdapter);
                manufacture_list.setTextFilterEnabled(true);
                classCategoryListAdapter.notifyDataSetChanged();
            } else {
                manufacture_title.setVisibility(View.VISIBLE);
                manufacture_list.setVisibility(View.GONE);
            }

        } else {
            ArrayList<Item> arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By item_name asc", database);
            ListView manufacture_list = (ListView) findViewById(R.id.manufacture_list);
            if (arrayList.size() > 0) {
                destinationListAdapter = new DestinationListAdapter(ClassDestinationListActivity.this, arrayList);
                manufacture_title.setVisibility(View.GONE);
                manufacture_list.setVisibility(View.VISIBLE);
                manufacture_list.setAdapter(destinationListAdapter);
                manufacture_list.setTextFilterEnabled(true);
                destinationListAdapter.notifyDataSetChanged();
            } else {
                manufacture_title.setVisibility(View.VISIBLE);
                manufacture_list.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_send);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_manufacture_list.getText().toString().trim();

            if (Globals.TicketCategory.equals("Class")) {
                strFilter = " and ( item_group_code Like '%" + strFilter + "%'  Or item_group_name Like '%" + strFilter + "%' )";
            } else {
                strFilter = " and ( item_code Like '%" + strFilter + "%'  Or item_name Like '%" + strFilter + "%' )";
            }
            edt_toolbar_manufacture_list.selectAll();
            getClassDestinationList(strFilter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ClassDestinationListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                Globals.TicketCategory="";
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ClassDestinationListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(ClassDestinationListActivity.this, Main2Activity.class);
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
}
