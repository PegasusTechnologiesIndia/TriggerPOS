package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import org.phomellolitepos.Adapter.InvReturnItemListadapter;
import org.phomellolitepos.Adapter.InvReturnListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Settings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvReturnItemListActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Dialog listDialog;
    Settings settings;
    InvReturnItemListadapter returnListAdapter;
    TextView item_title;
    String relt = "", PayId,cusCode;
    ArrayList itemlist;
    String operation, str_voucher_no, str_date, str_remarks, ordCode;
    String decimal_check, qty_decimal_check;
    ArrayList<Order_Detail> order_detail = new ArrayList<Order_Detail>();
    ArrayList<Object> object;
    ArrayList<Object> itemcodelist;
    ArrayList<Object> barcodelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_return_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        listDialog = new Dialog(this);
        settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {

            decimal_check = "1";
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(InvReturnItemListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
                            intent.putExtra("contact_code", cusCode);
                            intent.putExtra("order_code", ordCode);
                            Bundle args = new Bundle();
                            args.putSerializable("ARRAYLIST",(Serializable)object);
                            intent.putExtra("BUNDLE",args);


                            if (cusCode.equals("")) {
                                intent.putExtra("payment_id", "1");
                            } else {
                                intent.putExtra("payment_id", PayId);
                            }
                            startActivity(intent);
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

        getSupportActionBar().setTitle(R.string.invoice_return);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        item_title = (TextView) findViewById(R.id.item_title);

        try {
            Intent intent = getIntent();
            getSupportActionBar().setTitle("");
            operation = intent.getStringExtra("operation");
            str_voucher_no = intent.getStringExtra("voucher_no");
            str_date = intent.getStringExtra("date");
            str_remarks = intent.getStringExtra("remarks");
            PayId = intent.getStringExtra("payment_id");
            ordCode = intent.getStringExtra("order_code");
            cusCode = intent.getStringExtra("contact_code");
            Bundle args = intent.getBundleExtra("BUNDLE");
           object = (ArrayList<Object>) args.getSerializable("ARRAYLIST");

            itemcodelist = (ArrayList<Object>) args.getSerializable("ItemCodeList");
            barcodelist = (ArrayList<Object>) args.getSerializable("BarCodeList");

            order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), "where order_code='" + ordCode + "'", database);
            ListView category_list = (ListView) findViewById(R.id.item_list);
            returnListAdapter = new InvReturnItemListadapter(InvReturnItemListActivity.this, order_detail,object);
            category_list.setAdapter(returnListAdapter);
            returnListAdapter.notifyDataSetChanged();
        }
        catch(Exception e){}
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(InvReturnItemListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
                    intent.putExtra("contact_code", cusCode);
                    intent.putExtra("order_code", ordCode);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)object);
                    intent.putExtra("BUNDLE",args);


                    if (cusCode.equals("")) {
                        intent.putExtra("payment_id", "1");
                    } else {
                        intent.putExtra("payment_id", PayId);
                    }
                    startActivity(intent);

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
