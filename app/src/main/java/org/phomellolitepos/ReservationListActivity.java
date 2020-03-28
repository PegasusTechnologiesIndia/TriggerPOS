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
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.phomellolitepos.Adapter.ExpensesListAdapter;
import org.phomellolitepos.Adapter.MainItemCategoryListAdapter;
import org.phomellolitepos.Adapter.ReservationListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.Reservation;
import org.phomellolitepos.database.Settings;

import java.util.ArrayList;

public class ReservationListActivity extends AppCompatActivity {
    EditText edt_toolbar_resv_list;
    TextView reserv_title;
    Database db;
    SQLiteDatabase database;
    ArrayList<Reservation> arrayList;
    Reservation reservation;
    ReservationListAdapter reservationListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setTitle(R.string.Report);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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
                pDialog = new ProgressDialog(ReservationListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(ReservationListActivity.this, MainActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(ReservationListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(ReservationListActivity.this, Main2Activity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });


        reserv_title = (TextView) findViewById(R.id.reserv_title);
        edt_toolbar_resv_list = (EditText) findViewById(R.id.edt_toolbar_resv_list);

        edt_toolbar_resv_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_resv_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_resv_list.requestFocus();
                    edt_toolbar_resv_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_resv_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_resv_list.selectAll();
                    return true;
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_intent = new Intent(ReservationListActivity.this, ReservationActivity.class);
                startActivity(item_intent);
                finish();
            }
        });

        getReservList("");
    }

    private void getReservList(String strFilter) {
        arrayList = Reservation.getAllReservation(getApplicationContext(), "WHERE substr(Reservation.date_time,1,10) >= date('now', '-3 Hour') " + strFilter + " ORDER BY Reservation.date_time", database);
        ListView reserv_list = (ListView) findViewById(R.id.reserv_list);
        if (arrayList.size() > 0) {
            reservationListAdapter = new ReservationListAdapter(ReservationListActivity.this, arrayList);
            reserv_list.setVisibility(View.VISIBLE);
            reserv_title.setVisibility(View.GONE);
            reserv_list.setAdapter(reservationListAdapter);
            reserv_list.setTextFilterEnabled(true);
            reservationListAdapter.notifyDataSetChanged();
        } else {
            reserv_list.setVisibility(View.GONE);
            reserv_title.setVisibility(View.VISIBLE);
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
            String strFilter = edt_toolbar_resv_list.getText().toString().trim();
            strFilter = "and (date_time Like '%" + strFilter + "%')";
            edt_toolbar_resv_list.selectAll();
            getReservList(strFilter);
            return true;
        }
        return super.onOptionsItemSelected(item_menu);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ReservationListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(ReservationListActivity.this, MainActivity.class);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(ReservationListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(ReservationListActivity.this, Main2Activity.class);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }
}
