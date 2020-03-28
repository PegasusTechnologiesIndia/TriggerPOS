package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.Adapter.ExpensesListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Pos_Balance;

public class ExpensesListActivity extends AppCompatActivity {
    EditText edt_toolbar_expense_list;
    TextView expense_title;
    Database db;
    SQLiteDatabase database;
    ArrayList<Pos_Balance> arrayList;
    Pos_Balance pos_balance;
    ExpensesListAdapter expensesListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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
                pDialog = new ProgressDialog(ExpensesListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ExpensesListActivity.this, ManagerActivity.class);
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        expense_title = (TextView) findViewById(R.id.expense_title);
        edt_toolbar_expense_list = (EditText) findViewById(R.id.edt_toolbar_expense_list);

        edt_toolbar_expense_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_expense_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_expense_list.requestFocus();
                    edt_toolbar_expense_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_expense_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_expense_list.selectAll();
                    return true;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_intent = new Intent(ExpensesListActivity.this, ExpensesActivity.class);
                startActivity(item_intent);
                finish();
            }
        });

        getExpenseList("");
    }

    private void getExpenseList(String strFilter) {

        arrayList = Pos_Balance.getAllPos_Balance(getApplicationContext(), "WHERE  z_code ='0' And type ='EXP' And is_active ='1'" + strFilter + " Order By remarks asc limit "+ Globals.ListLimit+"", database);
        ListView expense_list = (ListView) findViewById(R.id.expense_list);
        if (arrayList.size() > 0) {
            expensesListAdapter = new ExpensesListAdapter(ExpensesListActivity.this, arrayList);
            expense_list.setVisibility(View.VISIBLE);
            expense_title.setVisibility(View.GONE);
            expense_list.setAdapter(expensesListAdapter);
            expense_list.setTextFilterEnabled(true);
            expensesListAdapter.notifyDataSetChanged();
        } else {
            expense_list.setVisibility(View.GONE);
            expense_title.setVisibility(View.VISIBLE);
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
            String strFilter = edt_toolbar_expense_list.getText().toString().trim();
            strFilter = " and ( remarks Like '%" + strFilter + "%' )";
            edt_toolbar_expense_list.selectAll();
            getExpenseList(strFilter);
            return true;
        }

        return super.onOptionsItemSelected(item_menu);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ExpensesListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ExpensesListActivity.this, ManagerActivity.class);
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
