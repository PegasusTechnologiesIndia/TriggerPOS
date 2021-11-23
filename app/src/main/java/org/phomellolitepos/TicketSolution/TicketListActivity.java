package org.phomellolitepos.TicketSolution;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.TicketingActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Ticket_Setup;

import java.util.ArrayList;

public class TicketListActivity extends AppCompatActivity {
    TextView contact_title;
    ListView list_places;
    FlightTicketListAdapter ticketListAdapter;
    Database db;
    SQLiteDatabase database;
    ArrayList<Ticket_Setup> arrayList;
    String strDay,strBrand,strCategory,FromItemID = "",ToItemID = "";
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List");
        list_places = (ListView) findViewById(R.id.list_places);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Intent intent = getIntent();

//        FromItemID = intent.getStringExtra("from");
//        ToItemID = intent.getStringExtra("to");
//        strCategory = intent.getStringExtra("category");
//        strBrand = intent.getStringExtra("brand");
//        strDay = intent.getStringExtra("days");
        FromItemID =Globals.From;
        ToItemID = Globals.To;
        strCategory = Globals.TickCat;
        strBrand = Globals.TicBrand;
        strDay = Globals.TicDay;

        contact_title = (TextView) findViewById(R.id.contact_title);
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
                pDialog = new ProgressDialog(TicketListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
            Thread timerThread = new Thread() {
                    public void run() {

                            try {
                                Intent intent = new Intent(TicketListActivity.this, TicketingActivity.class);
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

        getTicketList("");
    }

    private void getTicketList(String s) {
        try {
            arrayList = Ticket_Setup.getAllTicket_Setup(getApplicationContext(), "inner join item on item.item_code = ticket_setup.tck_from \n" +
                    "inner join ticket_setup_category on ticket_setup.id = ticket_setup_category.ref_id\n" +
                    "inner join ticket_setup_days on ticket_setup.id = ticket_setup_days.ref_id where ticket_setup.menufacture_id='"+strBrand+"' and ticket_setup.tck_from='"+FromItemID+"' \n" +
                    "and ticket_setup.tck_to='"+ToItemID+"' \n" +
                    "and ticket_setup_category.category_id='"+strCategory+"' \n" +
                    "and ticket_setup_days.days='"+strDay+"'\n" +
                    "and ticket_setup.is_active='1'", database);
        }catch (Exception ex){

        }
            if (arrayList.size() > 0) {
                ticketListAdapter = new FlightTicketListAdapter(TicketListActivity.this, arrayList);
                contact_title.setVisibility(View.GONE);
                list_places.setVisibility(View.VISIBLE);
                list_places.setAdapter(ticketListAdapter);
                list_places.setTextFilterEnabled(true);
                ticketListAdapter.notifyDataSetChanged();
            }else {
                contact_title.setVisibility(View.VISIBLE);
                list_places.setVisibility(View.GONE);
            }


    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TicketListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {

                try {
                    Intent intent = new Intent(TicketListActivity.this, TicketingActivity.class);
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
