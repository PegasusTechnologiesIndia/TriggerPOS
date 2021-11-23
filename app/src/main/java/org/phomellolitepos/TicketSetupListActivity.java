package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.TicketSetupListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Ticket_Setup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TicketSetupListActivity extends AppCompatActivity {
    EditText edt_toolbar_item_list;
    ArrayList<Ticket_Setup> arrayList;
    TextView item_title;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;
    String date;
    TicketSetupListAdapter ticketSetupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_setup_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        settings = Settings.getSettings(getApplicationContext(), database, "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(TicketSetupListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(TicketSetupListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(TicketSetupListActivity.this, Main2Activity.class);
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
        item_title = (TextView) findViewById(R.id.item_title);
        edt_toolbar_item_list = (EditText) findViewById(R.id.edt_toolbar_item_list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_intent = new Intent(TicketSetupListActivity.this, TicketSetupActivity.class);
                startActivity(item_intent);
                finish();
            }
        });

        getSetupList("");
    }

    private void getSetupList(String strFilter) {
        try {
            arrayList = Ticket_Setup.getAllTicket_Setup(getApplicationContext(), " inner join item on item.item_code = ticket_setup.tck_from WHERE ticket_setup.is_active='1' " + strFilter + " Order By id asc limit "+Globals.ListLimit+"", database);
        } catch (Exception ex) {
        }
        ListView category_list = (ListView) findViewById(R.id.item_list);
        if (arrayList.size() > 0) {
            ticketSetupListAdapter = new TicketSetupListAdapter(TicketSetupListActivity.this, arrayList);
            category_list.setVisibility(View.VISIBLE);
            item_title.setVisibility(View.GONE);
            category_list.setAdapter(ticketSetupListAdapter);
            category_list.setTextFilterEnabled(true);
            ticketSetupListAdapter.notifyDataSetChanged();
        } else {
            category_list.setVisibility(View.GONE);
            item_title.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(final MenuItem item_menu) {
        int id = item_menu.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_item_list.getText().toString().trim();
            strFilter = " and (item.item_name Like '%" + strFilter + "%')";
            edt_toolbar_item_list.selectAll();
            getSetupList(strFilter);
            return true;
        }
        return super.onOptionsItemSelected(item_menu);
    }
}
