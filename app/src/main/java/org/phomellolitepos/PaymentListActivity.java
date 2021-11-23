package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.PaymentListAdapterNew;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentListActivity extends AppCompatActivity {
    Database db;
    String succ_import, date;
    EditText edt_toolbar_payment_list;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    int id;
    TextView payment_title;
    ArrayList<Payment> arrayList;
    PaymentListAdapterNew paymentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        // settings = Settings.getSettings(getApplicationContext(), database, "");
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        edt_toolbar_payment_list = (EditText) findViewById(R.id.edt_toolbar_payment_list);
        payment_title = (TextView) findViewById(R.id.payment_title);
        edt_toolbar_payment_list.setMaxLines(1);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edt_toolbar_payment_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_payment_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_payment_list.requestFocus();
                    edt_toolbar_payment_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_payment_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_payment_list.selectAll();
                    return true;
                }
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        try{
            getPaymentList("");
        }
        catch(Exception e){

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PaymentListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                                try {
                                    Intent intent = new Intent(PaymentListActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(PaymentListActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(PaymentListActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            }
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(PaymentListActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
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
                Intent intent = new Intent(PaymentListActivity.this, AddPaymentActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });

    }

    private void getPaymentList(String strfilter) {
        arrayList = Payment.getAllPayment(getApplicationContext(), "WHERE is_active ='1' Order By payment_name asc");
        ListView paymentlist = (ListView) findViewById(R.id.payment_list);
        if (arrayList.size() > 0) {
            paymentAdapter = new PaymentListAdapterNew(PaymentListActivity.this, arrayList);
            paymentlist.setVisibility(View.GONE);
            paymentlist.setVisibility(View.VISIBLE);
            paymentlist.setAdapter(paymentAdapter);
            paymentlist.setTextFilterEnabled(true);
            paymentAdapter.notifyDataSetChanged();
        } else {
            payment_title.setVisibility(View.VISIBLE);
            paymentlist.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        if(Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);

        }
       /* menu.getItem(R.id.action_send).setVisible(false);
        menu.getItem(R.id.action_send).setEnabled(false);*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_payment_list.getText().toString().trim();
            strFilter = " and ( payment_name Like '%" + strFilter + "%' )";
            edt_toolbar_payment_list.selectAll();
            getPaymentList(strFilter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pDialog = new ProgressDialog(PaymentListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(PaymentListActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(PaymentListActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(PaymentListActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(PaymentListActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }

            }
        };
        timerThread.start();
    }
}
