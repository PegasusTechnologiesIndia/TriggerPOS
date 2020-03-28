package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.ManufactureSpiinerAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.ReportCategoryCheckListAdapter;
import org.phomellolitepos.Adapter.ReportCustomerAdapter;
import org.phomellolitepos.CheckBoxClass.ReportCategoryCheck;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Offer_Product;
import org.phomellolitepos.database.Offers;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ReportActivity extends AppCompatActivity {
    EditText edt_from, edt_to;
    TableRow category_row, tt_cus_spn;
    Button btn_psumry_header, btn_psumy_by_item, btn_psumy_by_suplr_pay, btn_cus_sate, btn_cus_smry, btn_low_stock, btn_stock, btn_cus_cr_coltion, btn_item_profit_report, btn_salesby_item, btn_gst_item_wise, btn_delivery, btn_order_payment, btn_daily_report, btn_item_summary, btn_item_wise, btn_invoice_header, btn_report_category, btn_user_item, btn_user_system, btn_order_tax_report, btn_item_tax_report, btn_monthly_report, btn_zclose, btn_order_tax, btn_order_item_tax, btn_open_odr_report, btn_pay_collec, btn_item_categry, btn_item, btn_contact_group, btn_contact, btn_tax, btn_item_tax, btn_user, btn_gst_sale_regis, btn_oder_type_tax;
    Calendar myCalendar;
    ArrayList<Item_Group> arrayList;
    String date, decimal_check;
    SQLiteDatabase database;
    Dialog listDialog;
    Database db;
    Item_Group item_group;
    String operation = "";
    Offers offers;
    ProgressDialog progressDialog;
    ReportCategoryCheckListAdapter reportCategorySpinnerAdapter;
    ArrayList<ReportCategoryCheck> list = new ArrayList<ReportCategoryCheck>();
    ArrayList<String> strCategoryCode = new ArrayList<String>();
    ProgressDialog pDialog;
    Settings settings;
    Spinner spn_cus;
    ArrayList<Contact> contact_ArrayList;
    String cusCode;
    LinearLayout ll_purchase, ll_gst, ll_master, ll_detail, ll_header, ll_summary,ll_account;
    String ck_projct_type,tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Report);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        myCalendar = Calendar.getInstance();
        listDialog = new Dialog(this);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ReportActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                if (Globals.rtick.equals("Ticket")){
                                    Globals.rtick="";
                                    Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    try {
                                        Intent intent = new Intent(ReportActivity.this, RetailActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } finally {
                                    }
                                }else {
                                    Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            } finally {
                            }
                        } else {
                            try {
                                if (Globals.rtick.equals("Ticket")){
                                    Globals.rtick="";
                                    Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                }else {
                                    Intent intent = new Intent(ReportActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();

            }
        });

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

        date = format.format(d);

        edt_from = (EditText) findViewById(R.id.edt_from);
        edt_to = (EditText) findViewById(R.id.edt_to);

        btn_report_category = (Button) findViewById(R.id.btn_report_category);
        category_row = (TableRow) findViewById(R.id.category_row);
        tt_cus_spn = (TableRow) findViewById(R.id.tt_cus_spn);
        btn_daily_report = (Button) findViewById(R.id.btn_daily_report);
        btn_item_summary = (Button) findViewById(R.id.btn_item_summary);
        btn_item_wise = (Button) findViewById(R.id.btn_item_wise);
        btn_invoice_header = (Button) findViewById(R.id.btn_invoice_header);
        btn_delivery = (Button) findViewById(R.id.btn_delivery);
        btn_monthly_report = (Button) findViewById(R.id.btn_monthly_report);
        btn_zclose = (Button) findViewById(R.id.btn_zclose);
        btn_open_odr_report = (Button) findViewById(R.id.btn_open_odr_report);
        btn_pay_collec = (Button) findViewById(R.id.btn_pay_collec);
        btn_order_payment = (Button) findViewById(R.id.btn_order_payment);
        btn_item_categry = (Button) findViewById(R.id.btn_item_categry);
        btn_item = (Button) findViewById(R.id.btn_item);
        btn_contact_group = (Button) findViewById(R.id.btn_contact_group);
        btn_contact = (Button) findViewById(R.id.btn_contact);
        btn_tax = (Button) findViewById(R.id.btn_tax);
        btn_item_tax = (Button) findViewById(R.id.btn_item_tax);
        btn_user = (Button) findViewById(R.id.btn_user);
        btn_gst_sale_regis = (Button) findViewById(R.id.btn_gst_sale_regis);
        btn_oder_type_tax = (Button) findViewById(R.id.btn_oder_type_tax);
        btn_gst_item_wise = (Button) findViewById(R.id.btn_gst_item_wise);
        btn_salesby_item = (Button) findViewById(R.id.btn_salesby_item);
        btn_item_profit_report = (Button) findViewById(R.id.btn_item_profit_report);
        btn_cus_cr_coltion = (Button) findViewById(R.id.btn_cus_cr_coltion);
        btn_low_stock = (Button) findViewById(R.id.btn_low_stock);
        btn_stock = (Button) findViewById(R.id.btn_stock);
        btn_cus_smry = (Button) findViewById(R.id.btn_cus_smry);
        btn_cus_sate = (Button) findViewById(R.id.btn_cus_sate);

        btn_psumry_header = (Button) findViewById(R.id.btn_psumry_header);
        btn_psumy_by_item = (Button) findViewById(R.id.btn_psumy_by_item);
        btn_psumy_by_suplr_pay = (Button) findViewById(R.id.btn_psumy_by_suplr_pay);
        ll_purchase = (LinearLayout) findViewById(R.id.ll_purchase);
        ll_gst = (LinearLayout) findViewById(R.id.ll_gst);
        ll_master = (LinearLayout) findViewById(R.id.ll_master);
        ll_detail = (LinearLayout) findViewById(R.id.ll_detail);
        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
        ll_account = (LinearLayout) findViewById(R.id.ll_account);


        spn_cus = (Spinner) findViewById(R.id.spn_cus);

        Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        try {
            ck_projct_type = lite_pos_registration.getproject_id();
        } catch (Exception e) {
            ck_projct_type = "";
        }

        if (Globals.Industry_Type.equals("3")) {
            ll_master.setVisibility(View.GONE);
            ll_purchase.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            ll_gst.setVisibility(View.GONE);
            ll_detail.setVisibility(View.GONE);
            ll_summary.setVisibility(View.GONE);
            btn_zclose.setVisibility(View.GONE);
            btn_cus_cr_coltion.setVisibility(View.GONE);
            category_row.setVisibility(View.GONE);
            tt_cus_spn.setVisibility(View.GONE);

        } else if (Globals.Industry_Type.equals("1")) {
            btn_pay_collec.setVisibility(View.GONE);
        } else if (Globals.Industry_Type.equals("2")) {
            btn_pay_collec.setVisibility(View.GONE);
        } else if (Globals.Industry_Type.equals("5")) {
            btn_pay_collec.setVisibility(View.GONE);
        } else if (Globals.Industry_Type.equals("6")) {
            btn_pay_collec.setVisibility(View.GONE);
            ll_master.setVisibility(View.VISIBLE);
            ll_purchase.setVisibility(View.GONE);
            btn_open_odr_report.setVisibility(View.GONE);
            ll_gst.setVisibility(View.GONE);
            ll_detail.setVisibility(View.GONE);
            btn_item_categry.setVisibility(View.GONE);
            btn_delivery.setVisibility(View.GONE);
            btn_item_profit_report.setVisibility(View.GONE);
            btn_zclose.setVisibility(View.GONE);
            btn_cus_cr_coltion.setVisibility(View.GONE);
            category_row.setVisibility(View.GONE);
            tt_cus_spn.setVisibility(View.GONE);
            ll_account.setVisibility(View.GONE);
            btn_low_stock.setVisibility(View.GONE);
            btn_stock.setVisibility(View.GONE);
            btn_item_tax.setVisibility(View.GONE);
            btn_oder_type_tax.setVisibility(View.GONE);
            btn_item_summary.setVisibility(View.GONE);
        }

        if (ck_projct_type.equals("cloud")) {
            ll_purchase.setVisibility(View.GONE);
            if (Globals.Industry_Type.equals("3")) {

            } else {
                btn_cus_smry.setVisibility(View.VISIBLE);
                btn_cus_sate.setVisibility(View.VISIBLE);
            }
        }

        if (Globals.Industry_Type.equals("3")) {
            ll_master.setVisibility(View.GONE);
            ll_purchase.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            ll_gst.setVisibility(View.GONE);
            ll_detail.setVisibility(View.GONE);
            ll_summary.setVisibility(View.GONE);
            btn_zclose.setVisibility(View.GONE);
            btn_cus_cr_coltion.setVisibility(View.GONE);
            category_row.setVisibility(View.GONE);
            tt_cus_spn.setVisibility(View.GONE);

        }


        final Intent intent = getIntent();
        String from, to;
        try {
            from = intent.getStringExtra("from");
        } catch (Exception ex) {
            from = "";
        }
        try {
            to = intent.getStringExtra("to");
        } catch (Exception ex) {
            to = "";
        }

        tick = intent.getStringExtra("Ticket");

        try {
            operation = intent.getStringExtra("operation");
            if (operation == null) {
                operation = "Add";
            }

        } catch (Exception ex) {
            operation = "";
        }

        if (tick == null) {
            tick = "";
        }

        if (operation.equals("Edit")) {
            edt_from.setText(from);
            edt_to.setText(to);

        } else {
            edt_from.setText(date);
            edt_to.setText(date);
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        fill_spinner_contact();

        spn_cus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact resultp = contact_ArrayList.get(position);
                cusCode = resultp.get_contact_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabe2();
            }

        };

        edt_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_from.getWindowToken(), 0);
                new DatePickerDialog(ReportActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edt_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_to.getWindowToken(), 0);
                new DatePickerDialog(ReportActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        btn_psumry_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "select '' as SNo, purchase.contact_code as 'Supplier Code', contact.name as 'Supplier Name', purchase.date as 'Date', purchase.voucher_no as 'Voucher', purchase.total as 'Amount' from purchase left join contact on contact.contact_code = purchase.contact_code where date(purchase.date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "";
                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(5);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(3);

                Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_psumry_header.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_psumy_by_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = " select '' as SNo ,item_group.item_group_code as 'Item Group Code',item_group.item_group_name as 'Item Group Name', item.item_code as 'Item Code',item.item_name  as 'Item Name',unit.name as 'Unit',item_location.quantity from purchase_detail LEFT JOIN item on item.item_code = purchase_detail.item_code LEFT JOIN unit on unit.unit_id = item.unit_id LEFT JOIN item_location on item_location.item_code = purchase_detail.item_code LEFT JOIN item_group on item_group.item_group_code = item.item_group_code where item.item_code = purchase_detail.item_code Group by item.item_code";

                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_psumy_by_item.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_psumy_by_suplr_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "select '' as SNo ,purchase.voucher_no as 'Voucher Code', purchase.contact_code as 'Supplier Code', contact.name as 'Supplier Name',purchase_payment.pay_amount as 'Amount',payments.payment_name as 'Payment Name' from purchase INNER JOIN payments on payments.payment_id = purchase_payment.payment_id  LEFT JOIN contact on contact.contact_code = purchase.contact_code  LEFT JOIN purchase_payment on purchase.voucher_no = purchase_payment.ref_voucher_no  where payments.is_active='1'";

                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(3);
                ArrayList<Integer> dateCols = new ArrayList<>();

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_psumy_by_suplr_pay.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_cus_sate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    pDialog = new ProgressDialog(ReportActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Getting data...");
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            final Cursor cursor = getCustomerStatment();
                            Globals.online_report_cursor = cursor;
                            pDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String sql_footer = "";
                                    ArrayList<Integer> numCols = new ArrayList<>();
                                    numCols.add(3);
                                    numCols.add(4);
                                    numCols.add(5);
                                    ArrayList<Integer> dateCols = new ArrayList<>();
                                    dateCols.add(1);
                                   Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                                    intent1.putExtra("qry", "");
                                    intent1.putExtra("type", "online");
                                    intent1.putExtra("name", btn_cus_sate.getText().toString());
                                    intent1.putExtra("qry_footer", sql_footer);
                                    intent1.putIntegerArrayListExtra("numCols", numCols);
                                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                                    intent1.putExtra("from", edt_from.getText().toString());
                                    intent1.putExtra("to", edt_to.getText().toString());
                                    intent1.putExtra("operation", operation);
                                    startActivity(intent1);
                                }
                            });
                        }
                    }.start();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cus_smry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    pDialog = new ProgressDialog(ReportActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Getting data...");
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            final Cursor cursor = getCustomerSummery();
                            Globals.online_report_cursor = cursor;
                            pDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String sql_footer = "";
                                    ArrayList<Integer> numCols = new ArrayList<>();

                                    ArrayList<Integer> dateCols = new ArrayList<>();
                                    Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                                    intent1.putExtra("qry", "");
                                    intent1.putExtra("type", "online");
                                    intent1.putExtra("name", btn_cus_smry.getText().toString());
                                    intent1.putExtra("qry_footer", sql_footer);
                                    intent1.putIntegerArrayListExtra("numCols", numCols);
                                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                                    intent1.putExtra("from", edt_from.getText().toString());
                                    intent1.putExtra("to", edt_to.getText().toString());
                                    intent1.putExtra("operation", operation);
                                    startActivity(intent1);
                                }
                            });
                        }
                    }.start();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_low_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sql = "select '' as SNo,item_group.item_group_code as 'Item Group Code',item_group.item_group_name as 'Item Group Name',item.item_code as 'Item Code',item.item_name as 'Item Name',unit.name as 'Unit Name',item_location.quantity as 'Quantity' from item left join item_group on item_group.item_group_code = item.item_group_code left join unit on unit.unit_id = item.unit_id left join item_location on item.item_code = item_location.item_code where item_location.quantity <item_location.reorder_point";

                String sql_footer = "";
                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_low_stock.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sql = "select '' as SNo,item_group.item_group_code as 'Item Group Code',item_group.item_group_name as 'Item Group Name',item.item_code as 'Item Code',item.item_name as 'Item Name',unit.name as 'Unit Name',item_location.quantity as 'Quantity'  from item left join item_group on item_group.item_group_code = item.item_group_code left join unit on unit.unit_id = item.unit_id left join item_location on item.item_code = item_location.item_code";

                String sql_footer = "";
                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_stock.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_cus_cr_coltion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "Select '' as SRNO,Acc_Customer_Credit.contact_code as 'Contact Code',contact.name as 'Contact Name',Acc_Customer_Credit.trans_date as 'Date',Acc_Customer_Credit.cr_amount as 'Credit Amount',Acc_Customer_Credit.paid_amount as 'Paid Amount',Acc_Customer_Credit.balance_amount as 'Balance Amount'from Acc_Customer_Credit LEFT join contact on contact.contact_code = Acc_Customer_Credit.contact_code where Acc_Customer_Credit.z_no ='0' and date(Acc_Customer_Credit.trans_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                String sql_footer = "Select '' as SRNO,'' as 'Contact Code','' as 'Contact Name','' as 'Date',sum(Acc_Customer_Credit.cr_amount) as 'Credit Amount', sum(Acc_Customer_Credit.paid_amount) as 'Paid Amount', sum(Acc_Customer_Credit.balance_amount) as 'Balance Amount' from Acc_Customer_Credit LEFT join contact on contact.contact_code = Acc_Customer_Credit.contact_code where Acc_Customer_Credit.z_no ='0' and date(Acc_Customer_Credit.trans_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(4);
                numCols.add(5);
                numCols.add(6);

                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(3);
               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_cus_cr_coltion.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_item_profit_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "Select '' as 'SRNO',item.item_code as 'Item Code',item.item_name as 'Item Name',SUM(quantity*cost_price) AS 'Cost Price', sum(quantity*sale_price) AS 'Sale Price',  sum(quantity) AS 'Quantity', SUM(((quantity*sale_price) - (quantity*cost_price))) AS 'Net Profit', cast((SUM(((cast(quantity as float)*sale_price) - (cast(quantity as float)*cost_price)))/SUM(quantity*cost_price)) as float)*100 AS 'Net Profit Per' From order_detail inner join item  on item.item_code = order_detail.item_code inner join orders  on orders.order_code = order_detail.order_code WHERE orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  item.item_code ORDER BY item.item_name ASC";

                String sql_footer = "Select '' as 'SRNO','' as 'Item Code','' as 'Item Name',SUM(quantity*cost_price) AS 'Cost Price', sum(quantity*sale_price) AS 'Sale Price',  sum(quantity) AS 'Quantity', SUM(((quantity*sale_price) - (quantity*cost_price))) AS 'Net Profit', cast((SUM(((cast(quantity as float)*sale_price) - (cast(quantity as float)*cost_price)))/SUM(quantity*cost_price)) as float)*100 AS 'Net Profit Per' From order_detail inner join item  on item.item_code = order_detail.item_code inner join orders  on orders.order_code = order_detail.order_code WHERE orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(3);
                numCols.add(4);
                numCols.add(6);

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
                Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_item_profit_report.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_salesby_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql;
                if (Globals.Industry_Type.equals("6")){
                    sql = "select  '' as srno,order_detail.item_code ,((Select  item_name from  item Where item.item_code =  ticket_setup.tck_from) || '-' || (Select  item_name from  item Where item.item_code =  ticket_setup.tck_to)) as Item_name,\n" +
                            "Sum((order_detail.sale_price )) as 'Sales price  (without Tax)', SUM(order_detail.quantity) AS 'Quantity',  \n" +
                            "SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax',  \n" +
                            "SUM( line_total) As 'Net amount'  From order_detail \n" +
                            "inner join ticket_setup on order_detail.item_code = ticket_setup.id\n" +
                            "inner join orders on orders.order_code = order_detail.order_code  \n" +
                            "Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')" +
                            "Group by order_detail.item_code";
                }else {
                    sql = "select  '' as srno,item_group.item_group_name as 'Item Category' ,item.item_code, item.item_name,\n" +
                            "Sum((order_detail.sale_price )) as 'Sales price  (without Tax)', SUM(order_detail.quantity) AS 'Quantity',  \n" +
                            "SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax', \n" +
                            "round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %', \n" +
                            "SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount', \n" +
                            "SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', \n" +
                            "SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount',  \n" +
                            "SUM( line_total) As 'Net amount'  From order_detail \n" +
                            "inner join  item on item.item_code = order_detail.item_code \n" +
                            "inner join item_group on item_group.item_group_code = item.item_group_code  \n" +
                            "inner join orders on orders.order_code = order_detail.order_code  \n" +
                            "Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')" +
                            "Group by item.item_code";
                }


                String sql_footer = "select  '' as srno, '' as 'Item Category' ,'' as 'Item Code', '' as 'Item Name', Sum((order_detail.sale_price )) as 'Sales price  (without Tax)', SUM(order_detail.quantity) AS 'Quantity', SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax', round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %', SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount', SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount'  From order_detail inner join  item on item.item_code = order_detail.item_code inner join item_group on item_group.item_group_code = item.item_group_code  inner join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(6);
                numCols.add(8);
                numCols.add(9);
                numCols.add(4);
                numCols.add(10);
                numCols.add(11);

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_salesby_item.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_item_categry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "Select '' as SNO, item_group_code as 'Item Group Code',item_group_name as 'Item Group Name' From item_Group where is_active = '1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();


               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_item_categry.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "Select '' as SNO,item_Group.item_group_code as 'Item Group Code',item_Group.item_group_name as 'Item Group Name',item.item_code as 'Item Code',item.item_name as 'Item Name',item.item_type  as 'Item Type',item.hsn_sac_code as 'HSN Code',item.barcode as 'Barcode',item.unit_id as 'Unit ID',unit.name as 'Unit Name',item.is_inclusive_tax as 'Is Inclusive Tax',item_location.cost_price as 'Cost Price',item_location.selling_price as 'Sale Price',item_location.new_sell_price  as 'New Sale Price'   From item \n" +
                        "inner join  item_Group  on item_Group.item_group_code = item.item_group_code\n" +
                        "left join  unit on  unit.unit_id =  item.unit_id \n" +
                        "left join  item_location  on  item_location.item_code  =  item.item_code     \n" +
                        " where item.is_active = '1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(11);
                numCols.add(12);
                numCols.add(13);
                ArrayList<Integer> dateCols = new ArrayList<>();


               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_item.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,business_group.business_group_code as 'Contact Group Code',business_group.name as 'Contact Group Name' from business_group where business_group.is_active ='1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();


               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_contact_group.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,Contact.contact_code as 'Contact Code',Contact.title as 'Title',Contact.name as 'Name',Contact.gender as 'Gender',Contact.dob as 'DOB',Contact.company_name as 'Company Name',Contact.contact_1 as 'Contact 1',\n" +
                        "Contact.contact_2 as 'Contact 2',Contact.email_1 as 'Email 1',Contact.email_2 as 'Email 2',Contact.credit_limit as 'Credit Limit' ,Contact.gstin as 'GSTN'  from Contact where Contact.is_active = '1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(5);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_contact.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as 'Tax Rate' from tax where tax.is_active ='1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);

                ArrayList<Integer> dateCols = new ArrayList<>();
                Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_tax.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_item_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,item.item_code as 'Item Code',item.item_name as 'Item Name',item.hsn_sac_code as 'HSN Code',item.barcode as 'Barcode',item.item_type as 'Item Type',item.is_inclusive_tax as 'Is Inclusive Tax',tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as'Tax Rate' from  item_group_tax \n" +
                        "inner join tax on tax.tax_id = item_group_tax.tax_id \n" +
                        "inner join item  on item.item_code  = item_group_tax .item_group_code";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(10);

                ArrayList<Integer> dateCols = new ArrayList<>();

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_item_tax.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,user.user_code as 'User Code',user.name as 'User Name',user.email as 'User Email',user.max_discount as 'Max Discount' from user where  user.is_active ='1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);
                ArrayList<Integer> dateCols = new ArrayList<>();


               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_user.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_oder_type_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SNO,tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as 'Tax Rate',order_type.name as 'Name' from  order_type_tax\n" +
                        "inner join order_type  on order_type.order_type_id = order_type_tax.order_type_id \n" +
                        "inner join tax  on tax.tax_id = order_type_tax.tax_id";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);
                ArrayList<Integer> dateCols = new ArrayList<>();

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_oder_type_tax.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });


        btn_gst_item_wise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sql = "select  '' as srno,item_group.item_group_name as 'Item Category' , order_detail.item_code as 'Item number' ,item.item_name  as 'Item Name/Stock ',item.hsn_sac_code   as 'HSN Code',order_detail.order_code as 'Invoice number',date(orders.order_date) as Date,time(orders.order_date) as Time,orders.modified_by AS 'User ID  (Sales person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code)  as  'Device ID',orders.remarks as ' Remarks ', (order_detail.sale_price ) as 'Sales price (without Tax)', order_detail.quantity AS 'Quantity', unit.code as UOM, ((order_detail.sale_price) * order_detail.quantity) As 'Gross Amount w/o Tax', round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %', round(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity),2) as 'Disount amount',  round(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ),2) as 'Amount after  Discount',(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1') LIMIT 1) as CGST_PER,(Select SUM(order_detail.quantity * order_detail_tax.tax_value) from order_detail_tax where order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1'))  as 'GGST AMOUNT',(select tax.rate  from tax where tax.tax_id IN (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2') LIMIT 1) as SGST_PER,(Select  SUM(order_detail.quantity * order_detail_tax.tax_value) from  order_detail_tax where order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2')) as 'SGST AMOUNT',(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3') LIMIT 1) as IGST_PER,(Select SUM(order_detail.quantity * order_detail_tax.tax_value) from  order_detail_tax where order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3')) as 'IGST AMOUNT',(order_detail.quantity * order_detail.tax) as 'Total Tax',round(line_total,2) As 'Net amount' From order_detail  inner join  item on item.item_code = order_detail.item_code inner join item_group on item_group.item_group_code = item.item_group_code  inner join unit  on unit.unit_id = item.unit_id inner join orders on orders.order_code = order_detail.order_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(11);
                numCols.add(14);
                numCols.add(16);
                numCols.add(17);
                numCols.add(19);
                numCols.add(21);
                numCols.add(23);
                numCols.add(24);
                numCols.add(25);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(6);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_gst_item_wise.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });

        btn_gst_sale_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sql = "Select '' as SNO,orders.order_code ,orders.order_date ,\n" +
                        "(select contact.name from  contact where contact.contact_code  = orders.contact_code) as  'Buyers Name' ,\n" +
                        "(select contact.gstin  from  contact where contact.contact_code  = orders.contact_code) as  'Buyers GST' ,\n" +
                        "((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code)  as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount ) As 'Amount after Discount',\n" +
                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1') LIMIT 1) as CGST_PER,\n" +
                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1') \n" +
                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1')))  as CGST_AMOUNT,\n" +
                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2') LIMIT 1) as SGST_PER,\n" +
                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2') \n" +
                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2'))) as SGST_AMOUNT,\n" +
                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3') LIMIT 1) as IGST_PER,\n" +
                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3') \n" +
                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3'))) as IGST_AMOUNT,\n" +
                        "((select (sum(tax*quantity) + orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  )) as TOTALGST,\n" +
                        "orders.total  as INV_AMOUNT\n" +
                        "  from  orders Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(5);
                numCols.add(7);
                numCols.add(8);
                numCols.add(10);
                numCols.add(12);
                numCols.add(14);
                numCols.add(15);
                numCols.add(16);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(2);

                Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_gst_sale_regis.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);
            }
        });

        btn_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "select '' as SRNO,orders.order_code AS'Invoice number',orders.order_date AS'Invoice Date',orders.remarks As 'Remarks',orders.total As 'Invoice Amount',order_payment.pay_amount As 'Credit Amount' from orders inner join order_payment on order_payment.order_code = orders.order_code Where orders.order_status ='CLOSE' And order_payment.payment_id='5' And date(orders.delivery_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "select '' as SRNO,'' AS'Invoice number','' AS'Invoice Date','' As 'Remarks',sum(orders.total) As 'Invoice Amount',sum(order_payment.pay_amount) As 'Credit Amount' from orders inner join order_payment on order_payment.order_code = orders.order_code Where orders.order_status ='CLOSE' And order_payment.payment_id='5' And date(orders.delivery_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);
                numCols.add(5);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(2);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_delivery.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });

        btn_order_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sql = "SELECT  '' AS SRNO ,orders.order_code AS'Invoice number' ,date(orders.order_date) AS Date,time(orders.order_date) as Time  ,orders.modified_by as 'User ID\\n\" +\n" +
                        "                        \"(Sales person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code)  as 'Device ID',remarks AS 'Remarks',(select payment_name from payments where payment_id = order_payment.payment_id) as 'Payment method',(((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code \t )) - total_discount ) As ' Sales Amount without Tax After Discount ',(select (sum(tax*quantity) + orders.total_tax  ) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t ) as ' Tax amount',total as ' Net Amount ', order_payment.pay_amount as 'Pay Amount' ,location.location_name as 'Location Name',order_type.name AS 'Order Type',contact.name AS contact FROM order_payment INNER JOIN orders ON  order_payment.order_code = orders.order_code LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN contact ON contact.contact_code=orders.contact_code LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";


                String sql_footer = "SELECT  '' AS SRNO ,'' AS'Invoice number' ,'' AS Date,'' as Time  ,'' as 'User ID\n" +
                        "(Sales person)',''  as 'Device ID','' AS 'Remarks',\n" +
                        "'' as 'Payment method',sum((((select sum(order_detail.sale_price * order_detail.quantity) From\n" +
                        "order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As ' Sales Amount without Tax After Discount ',sum((select (sum(tax*quantity) + orders.total_tax  ) From\n" +
                        "order_detail Where order_detail.order_code = orders.order_code  )) as ' Tax amount',\n" +
                        " sum(total) as ' Net Amount ', sum(order_payment.pay_amount) as 'Pay Amount' ,'' as 'Location Name','' AS 'Order Type','' AS contact FROM order_payment INNER JOIN orders ON  order_payment.order_code = orders.order_code LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN contact ON contact.contact_code=orders.contact_code LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(8);
                numCols.add(9);
                numCols.add(10);
                numCols.add(11);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(2);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_order_payment.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });


        btn_report_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog();
            }
        });

        btn_pay_collec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "select '' as SRNO, collection_date as 'Collection Date',Pay_Collection.contact_code as 'Contact Code',contact.name as 'Name',amount as 'Amount',payment_mode as 'Payment Mode',Bank.bank_name as 'Bank Name',ref_no as 'Cheque No' from Pay_Collection\n" +
                        "inner join contact on contact.contact_code =  Pay_Collection.contact_code \n" +
                        "left join Bank on Bank.bank_code =  Pay_Collection.ref_type WHERE DATE(Pay_Collection.collection_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);

                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(1);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_pay_collec.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });

        btn_daily_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sql = "Select '' as SRNO, date(orders.order_date) as Date,orders.modified_by as 'User Id(Sales Person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code) as 'Device Id',SUM(((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t))) As  ' Amount w/o Tax ',((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t))) as ' Discount %', SUM(total_discount) as 'Disount amount ', SUM((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t)) - total_discount )) As 'Amount after Discount', SUM((select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t  )) as 'Tax amount',SUM(total) as 'Net amount',COUNT(*) AS 'Total Invoice' ,location.location_name As'Location Name',order_type.name AS 'Order Type' from orders LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')    Group by date(orders.order_date)";


                String sql_footer = "Select  '' as SRNO, '' as Date,'' as 'User Id(Sales Person)','' as 'Device Id', SUM( ((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) as ' Discount %', SUM(total_discount) as 'Disount amount ', SUM((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As 'Amount after  Discount', SUM((select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  )) as 'Tax amount',SUM(total) as 'Net amount',COUNT(*) AS 'Total Invoice' ,'' As 'Location Name','' AS order_type   from orders     LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(orders.order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();


                numCols.add(4);
//                numCols.add(5);
                numCols.add(6);
                numCols.add(7);
                numCols.add(8);
                numCols.add(9);

                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(1);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name",btn_daily_report.getText().toString() );
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });

        btn_zclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
//                    final String sql = "select  '' AS SRNO, Z_Close.device_code,date(Z_Close.date) as Date,  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='OB') As OB, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='EXP') As EXP, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CASH') As CASH, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='BANK CARD') As 'BANK CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='LOYALTY CARD') As 'LOYALTY CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CHEQUE') As 'CHEQUE',  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CUSTOMER ACCOUNT') As 'CUSTOMER ACCOUNT',   (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='PROMO CODE') As 'PROMO CODE',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='KNET') As 'KNET',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CR AMT') As 'CR Amount',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='DR AMT') As 'DR Amount',(SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='RETURN') As 'Total Return', (z_close.total_amount - (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='RETURN')) As 'Total Cash',(SELECT SUM(amount) from Z_Detail Where z_detail.z_code = z_close.z_code and type not in ('OB','EXP','CR AMT','DR AMT','RETURN','LOYALTY CARD','PROMO CODE')) As 'Total Sales',Z_Close.z_code From Z_Close  INNER JOIN z_detail ON z_detail.z_code = z_close.z_code  Where date(date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')    Group by date(Z_Close.date),Z_Close.z_code";
                    //This is for hamseer without return in total
                    final String sql = "select  '' AS SRNO, Z_Close.device_code,date(Z_Close.date) as Date,  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='OB') As OB, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='EXP') As EXP, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CASH') As CASH, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='BANK CARD') As 'BANK CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='LOYALTY CARD') As 'LOYALTY CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CHEQUE') As 'CHEQUE',  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CUSTOMER ACCOUNT') As 'CUSTOMER ACCOUNT',   (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='PROMO CODE') As 'PROMO CODE',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='KNET') As 'KNET',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CR AMT') As 'CR Amount',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='DR AMT') As 'DR Amount',(SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='RETURN') As 'Total Return', (z_close.total_amount) As 'Total Cash',(SELECT SUM(amount) from Z_Detail Where z_detail.z_code = z_close.z_code and type not in ('OB','EXP','CR AMT','DR AMT','RETURN','LOYALTY CARD','PROMO CODE')) As 'Total Sales',Z_Close.z_code From Z_Close  INNER JOIN z_detail ON z_detail.z_code = z_close.z_code  Where date(date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')    Group by date(Z_Close.date),Z_Close.z_code";

                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();

                    numCols.add(3);
                    numCols.add(4);
                    numCols.add(5);
                    numCols.add(6);
                    numCols.add(7);
                    numCols.add(8);
                    numCols.add(9);
                    numCols.add(10);
                    numCols.add(11);
                    numCols.add(12);
                    numCols.add(13);
                    numCols.add(14);
                    numCols.add(15);


                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);
                   Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", sql);
                    intent1.putExtra("name", btn_zclose.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);

                }
            }
        });

        btn_open_odr_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "Select  '' as SRNO, orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,orders.modified_by as 'User Id(Sales Person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code) as 'Device Id',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount ) As 'Amount after Discount',(select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  ) as 'Tax amount',total as 'Net amount',location.location_name,order_type.name AS order_type,contact.name AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id           LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CANCEL' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "Select '' as SRNO, '' As 'Invoice Number' ,'' as Date,'' as Time  ,'' 'User Id(Sales Person)','' as 'Device Id','' as Remarks,Sum(((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code)  as ' Discount %', sum(total_discount) as 'Disount amount ', sum((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As 'Amount after Discount',sum((select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  )) as 'Tax amount',sum(total) as 'Net amount','' As 'Location Name','' AS 'Order Type','' AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id  LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CANCEL' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(7);
                numCols.add(9);
                numCols.add(10);
                numCols.add(11);
                numCols.add(12);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(2);

               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_open_odr_report.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });

        btn_item_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sql = "select  '' as srno,item_group.item_group_name as 'Item Category' ," +
                        " Sum((order_detail.sale_price )) as 'Sales price " +
                        " (without Tax)'," +
                        " SUM(order_detail.quantity) AS 'Quantity'," +
                        " " +
                        " SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax'," +
                        " round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %'," +
                        " SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount'," +
                        " SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after " +
                        " Discount'," +
                        " SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount" +
                        " ', " +
                        " SUM( line_total) As 'Net amount'  From order_detail" +
                        " inner join  item on item.item_code = order_detail.item_code" +
                        " inner join item_group on item_group.item_group_code = item.item_group_code " +
                        " inner join orders on orders.order_code = order_detail.order_code  Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')" +
                        " Group by item_group.item_group_code";

                String sql_footer = "select  '' as srno,'' as 'Item Category' ," +
                        " Sum((order_detail.sale_price )) as 'Sales price " +
                        " (without Tax)'," +
                        " SUM(order_detail.quantity) AS 'Quantity'," +
                        " " +
                        " SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax'," +
                        " round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %'," +
                        " SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount'," +
                        " SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after " +
                        " Discount'," +
                        " SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount" +
                        " ', " +
                        " SUM( line_total) As 'Net amount'  From order_detail" +
                        " inner join  item on item.item_code = order_detail.item_code" +
                        " inner join item_group on item_group.item_group_code = item.item_group_code " +
                        " inner join orders on orders.order_code = order_detail.order_code  Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(6);
                numCols.add(2);
                numCols.add(8);
                numCols.add(9);
                numCols.add(4);
                numCols.add(7);
                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
               Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_item_summary.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);


            }
        });

        btn_item_wise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    final String sql = "select  '' as srno,item_group.item_group_name as 'Item Category' , order_detail.item_code as 'Item number' ,item.item_name  as 'Item Name/Stock ',order_detail.order_code as 'Invoice number',date(orders.order_date) as Date,time(orders.order_date) as Time,orders.modified_by AS 'User ID  (Sales person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code)  as  'Device ID',orders.remarks as ' Remarks ', (order_detail.sale_price ) as 'Sales price (without Tax)', order_detail.quantity AS 'Quantity', unit.code as UOM, ((order_detail.sale_price) * order_detail.quantity) As 'Gross Amount w/o Tax', round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) as 'Discount %', round(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity),2) as 'Disount amount',  round(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ),2) as 'Amount after  Discount', (order_detail.quantity * order_detail.tax) as 'Tax amount',  round(line_total,2) As 'Net amount'  From order_detail inner join  item on item.item_code = order_detail.item_code inner join item_group on item_group.item_group_code = item.item_group_code inner join unit  on unit.unit_id = item.unit_id inner join orders on orders.order_code = order_detail.order_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();


                    numCols.add(10);
                    numCols.add(13);
                    numCols.add(16);
                    numCols.add(17);
                    numCols.add(18);
                    numCols.add(11);
                    numCols.add(14);
                    numCols.add(15);
                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(5);

                   Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", sql);
                    intent1.putExtra("name", btn_item_wise.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);
                }
            }
        });

        btn_invoice_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {

                    final String sql = "Select '' as SRNO, orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,orders.modified_by as 'User Id(Sales Person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code) as 'Device Id',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code \t)) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code and  order_detail.device_code = orders.device_code \t )  as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t)) - total_discount  ) As 'Amount after Discount',(select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t ) as 'Tax amount',total as 'Net amount',location.location_name As 'Location Name',order_type.name AS 'Order Type',contact.name AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                    String sql_footer = "Select '' as SRNO, '' As 'Invoice Number' ,'' as Date,'' as Time  ,'' 'User Id(Sales Person)','' as 'Device Id','' as Remarks,Sum(((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code)  as ' Discount %', sum(total_discount) as 'Disount amount ', sum((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As 'Amount after Discount',sum((select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  )) as 'Tax amount',sum(total) as 'Net amount','' As 'Location Name','' AS 'Order Type','' AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id  LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                    ArrayList<Integer> numCols = new ArrayList<>();
                    numCols.add(7);
                    numCols.add(10);
                    numCols.add(11);
                    numCols.add(12);
                    numCols.add(8);
                    numCols.add(9);
                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);
                   Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", sql);
                    intent1.putExtra("name", btn_invoice_header.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);


                }
            }
        });

        btn_monthly_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sql = "Select  '' as SRNO,   strftime('%m',orders.order_date) AS month_name, strftime('%Y',orders.order_date) AS year,orders.modified_by as 'User Id(Sales Person)',(select device_name from  Lite_POS_Device where  Device_Code =   orders.device_code) as 'Device Id', SUM( ((select sum(order_detail.sale_price *order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code\n" +
                        "AND   order_detail.device_code = orders.device_code   ))) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price *order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code AND   order_detail.device_code = orders.device_code ))) as ' Discount %', SUM(total_discount) as 'Disount amount ', SUM((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code AND   order_detail.device_code = orders.device_code )) - total_discount )) As 'Amount after  Discount',  SUM((select (sum(tax*quantity)+orders.total_tax) From  order_detail Where order_detail.order_code = orders.order_code AND   order_detail.device_code = orders.device_code  )) as 'Tax amount',SUM(total) as 'Net amount',COUNT(*)\n" +
                        " As 'Total Invoice' ,location.location_name As 'Location Name' ,order_type.name AS 'Order Type'   from orders      LEFT JOIN location ON location.location_id=orders.location_id             LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')    Group by strftime('%m', orders.order_date) ,strftime('%Y', orders.order_date)";


                String sql_footer = "Select  '' as SRNO, '' AS month_name, '' AS year,'' as 'User Id(Sales Person)','' as 'Device Id', SUM( ((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) as ' Discount %', SUM(total_discount) as 'Disount amount ', SUM((((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As 'Amount after  Discount',  SUM((select (sum(tax*quantity)+orders.total_tax) From  order_detail Where order_detail.order_code = orders.order_code  )) as 'Tax amount',SUM(total) as 'Net amount',count(*) As 'Total Invoice','' As 'Location Name','' AS 'Order Type'    from orders      LEFT JOIN location ON location.location_id=orders.location_id  LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();


                numCols.add(5);
                numCols.add(8);
                numCols.add(9);
                numCols.add(10);
//                numCols.add(6);
                numCols.add(7);
//                numCols.add(11);

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
                Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";    Globals.ReportCondition = "";
                intent1.putExtra("type", "");
                intent1.putExtra("qry", sql);
                intent1.putExtra("name", btn_monthly_report.getText().toString());
                intent1.putExtra("qry_footer", sql_footer);
                intent1.putIntegerArrayListExtra("numCols", numCols);
                intent1.putIntegerArrayListExtra("dateCols", dateCols);
                intent1.putExtra("from", edt_from.getText().toString());
                intent1.putExtra("to", edt_to.getText().toString());
                intent1.putExtra("operation", operation);
                startActivity(intent1);

            }
        });
    }

    private Cursor getCustomerStatment() {
        MatrixCursor mc = null;
        String serverData = get_CustomerStatment_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                try {
                    mc = new MatrixCursor(new String[]{"SNo", "Date", "Narration", "Dr Amount", "Cr Amount", "Current Balance"});
                    JSONArray jsonArray = jsonObject_bg.getJSONArray("result");

                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        mc.addRow(new Object[]{"", jsonObject.get("date"), jsonObject.get("narration"), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("dr_amount")), decimal_check), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("cr_amount")), decimal_check), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("current_balance")), decimal_check)});
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {

        }
        return mc;

    }

    private String get_CustomerStatment_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/customer_statements");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("contact_code", cusCode));
        nameValuePairs.add(new BasicNameValuePair("from_date", edt_from.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("to_date", edt_to.getText().toString().trim()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;

    }

    private void fill_spinner_contact() {
        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') Order By lower(name) asc");
        }else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') Order By lower(name) asc");
        }
        ReportCustomerAdapter reportCustomerAdapter = new ReportCustomerAdapter(ReportActivity.this, contact_ArrayList);
        spn_cus.setAdapter(reportCustomerAdapter);
    }

    private Cursor getCustomerSummery() {
        MatrixCursor mc = null;
        String serverData = get_CustomerSummery_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                try {
                    mc = new MatrixCursor(new String[]{"SNo", "Contact Code", "Name", "Device Code", "Amount"});
                    JSONArray jsonArray = jsonObject_bg.getJSONArray("result");

                    for (int k = 0; k < jsonArray.length(); k++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        mc.addRow(new Object[]{"", jsonObject.get("contact_code"), jsonObject.get("name"), jsonObject.get("device_code"), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("sum(cr_amount - dr_amount)")), decimal_check)});
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
        }
        return mc;
    }

    private String get_CustomerSummery_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/customer_summary");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String from;
        try {
            from = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            from = "";
        }
        edt_from.setText(from);
    }

    private void updateLabe2() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String too;
        try {
            too = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            too = "";
        }
        edt_to.setText(too);
    }


    private void showdialog() {
        listDialog = new Dialog(this);
        listDialog.setTitle("Select Category");
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.report_category_list, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        listDialog.show();
        ListView list1 = (ListView) listDialog.findViewById(R.id.lv_custom_report_category);


        arrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1'", database, db);
        list.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            ReportCategoryCheck b = new ReportCategoryCheck();
            b.setName(arrayList.get(i).get_item_group_name());
            b.setSelected(false);
            list.add(b);
        }

        reportCategorySpinnerAdapter = new ReportCategoryCheckListAdapter(getApplicationContext(), list);
        // attaching data adapter to spinner
        list1.setAdapter(reportCategorySpinnerAdapter);
        list1.setTextFilterEnabled(true);
    }


    @Override
    public void onBackPressed() {
        progressDialog = new ProgressDialog(ReportActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        if (Globals.rtick.equals("Ticket")){
                            Globals.rtick="";
                            Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        }else {
                            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        if (Globals.rtick.equals("Ticket")) {
                            Globals.rtick="";
                            Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        } else {
                        }
                        Intent intent = new Intent(ReportActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();

                    } finally {
                    }
                } else {
                    try {
                        if (Globals.rtick.equals("Ticket")) {
                            Globals.rtick="";
                            Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        } else {
                        }
                        Intent intent = new Intent(ReportActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();

                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())

                    return true;

        }

        return false;
    }


}

