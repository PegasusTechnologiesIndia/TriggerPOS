package org.phomellolitepos;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.phomellolitepos.Adapter.ReportCategoryCheckListAdapter;
import org.phomellolitepos.Adapter.ReportCustomerAdapter;
import org.phomellolitepos.Adapter.Spin_VehicleAdapter;
import org.phomellolitepos.Adapter.UserAdapter;
import org.phomellolitepos.CheckBoxClass.ReportCategoryCheck;
import org.phomellolitepos.Reports.CancelReport_view;
import org.phomellolitepos.Reports.ContactReport_view;
import org.phomellolitepos.Reports.DailySales_Report;
import org.phomellolitepos.Reports.GstOrderHeaderReport_view;
import org.phomellolitepos.Reports.GstOrderItemReport_view;
import org.phomellolitepos.Reports.ItemGroupReport_view;
import org.phomellolitepos.Reports.ItemProfitReport_view;
import org.phomellolitepos.Reports.ItemReport_view;
import org.phomellolitepos.Reports.ItemTaxReport_view;
import org.phomellolitepos.Reports.MonthlySalesReport_View;
import org.phomellolitepos.Reports.OrderDetailReport_View;
import org.phomellolitepos.Reports.OrderHeaderReport_View;
import org.phomellolitepos.Reports.OrderPaymentReport_View;
import org.phomellolitepos.Reports.OrderTaxReport_view;
import org.phomellolitepos.Reports.SalesByItemGroupReport_View;
import org.phomellolitepos.Reports.SalesByItemReport_View;
import org.phomellolitepos.Reports.TaxReport_view;
import org.phomellolitepos.Reports.UserReport_view;
import org.phomellolitepos.Reports.VatOrderHeaderReport_view;
import org.phomellolitepos.Reports.VatOrderItemReport_view;
import org.phomellolitepos.Reports.ZeroReportView;
import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Offers;
import org.phomellolitepos.database.OrderDetail_Report;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;

public class ReportActivity extends AppCompatActivity {
    TextView edt_from, edt_to;
    TableRow category_row, tt_cus_spn;
    Button btn_psumry_header, btn_psumy_by_item, btn_psumy_by_suplr_pay, btn_cus_sate, btn_cus_smry, btn_low_stock, btn_stock, btn_cus_cr_coltion, btn_item_profit_report, btn_salesby_item, btn_gst_item_wise, btn_delivery, btn_order_payment, btn_daily_report, btn_item_summary, btn_item_wise, btn_invoice_header, btn_report_category, btn_user_item, btn_user_system, btn_order_tax_report, btn_item_tax_report, btn_monthly_report, btn_zclose, btn_order_tax, btn_order_item_tax, btn_open_odr_report, btn_pay_collec, btn_item_categry, btn_item, btn_contact_group, btn_contact, btn_tax, btn_item_tax, btn_user, btn_gst_sale_regis, btn_oder_type_tax,btn_vat_orderheader,btn_vat_orderitemreport;
    Button btn_vehiclein_report,btn_vehsummaryreport;
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
    ArrayList<User> user_ArrayList;
    ArrayList<Item> vehtype_ArrayList;
    ArrayList<Item> vehtype_ArrayListnew;
    String cusCode,userCode,VehCode;
    String serial_no, android_id, myKey, device_id, imei_no;
    LinearLayout ll_purchase, ll_gst, ll_master, ll_detail, ll_header, ll_summary,ll_account,ll_vat;
    String ck_projct_type,tick;
    int srno_count;
    TextView txt_accounts;
    TableRow tb_cususer,tb_cus_vehtype;
    Spinner spn_user,spn_vehtype;
    String username;
    ArrayList<String> itemnamelist;
    ArrayList<String> userlist;
    ArrayList<String> itemcodelist;
    ArrayList<String> qrysizelist = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Report);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        myCalendar = Calendar.getInstance();
        listDialog = new Dialog(this);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                            if (settings.get_Home_Layout().equals("0")) {
                                try {
                                    if (Globals.rtick.equals("Ticket")) {
                                        Globals.rtick = "";
                                        Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else if (settings.get_Home_Layout().equals("2")) {
                                        try {
                                            Intent intent = new Intent(ReportActivity.this, RetailActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            pDialog.dismiss();
                                            finish();
                                        } finally {
                                        }
                                    } else {
                                        Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                } finally {
                                }
                            } else {
                                try {
                                    if (Globals.rtick.equals("Ticket")) {
                                        Globals.rtick = "";
                                        Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else {
                                        Intent intent = new Intent(ReportActivity.this, Main2Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                } finally {
                                }
                            }
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){

                            Intent intent = new Intent(ReportActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);



                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("3")){

                            Intent intent = new Intent(ReportActivity.this, PaymentCollection_MainScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);



                        }

                        else if(Globals.objLPR.getIndustry_Type().equals("4")){
                            Intent intent = new Intent(ReportActivity.this, ParkingIndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                };
                timerThread.start();

            }
        });

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

        date = format.format(d);

        edt_from =findViewById(R.id.edt_from);
        edt_to =findViewById(R.id.edt_to);

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
        btn_vat_orderheader = (Button) findViewById(R.id.btn_vat_sale_regis);
        btn_vat_orderitemreport = (Button) findViewById(R.id.btn_vat_item_wise);
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
        ll_vat= (LinearLayout) findViewById(R.id.ll_vat);
        spn_user=(Spinner)findViewById(R.id.spn_user);
        spn_vehtype=(Spinner)findViewById(R.id.spn_vehtype);

        btn_vehiclein_report=(Button) findViewById(R.id.btn_vehicleinreport);
        txt_accounts=(TextView)findViewById(R.id.textView_acc);
        tb_cususer=(TableRow)findViewById(R.id.tt_cus_user);
        tb_cus_vehtype=(TableRow)findViewById(R.id.tt_cus_vehtype);

        btn_vehsummaryreport=(Button) findViewById(R.id.btn_vehiclesumreport);
        spn_cus = (Spinner) findViewById(R.id.spn_cus);

        try {
            ck_projct_type = Globals.objLPR.getproject_id();
        } catch (Exception e) {
            ck_projct_type = "";
        }




        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
       /* device_id = telephonyManager.getDeviceId();
        imei_no = telephonyManager.getImei();*/
        if (Globals.Industry_Type.equals("1")) {
            btn_pay_collec.setVisibility(View.GONE);

        }
        if (Globals.Industry_Type.equals("2")) {
            btn_pay_collec.setVisibility(View.GONE);

        }
        if(Globals.objLPR.getIndustry_Type().equals("3")){

            ll_summary.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            ll_detail.setVisibility(View.GONE);
            btn_pay_collec.setVisibility(View.GONE);
            btn_cus_cr_coltion.setVisibility(View.VISIBLE);
            btn_cus_smry.setVisibility(View.VISIBLE);
            btn_cus_sate.setVisibility(View.VISIBLE);
            ll_master.setVisibility(View.GONE);
            ll_gst.setVisibility(View.GONE);
            ll_vat.setVisibility(View.GONE);
            btn_vehsummaryreport.setVisibility(View.GONE);
            btn_vehiclein_report.setVisibility(View.GONE);
            tt_cus_spn.setVisibility(View.VISIBLE);
            tb_cususer.setVisibility(View.GONE);
            tb_cus_vehtype.setVisibility(View.GONE);

        }
        else if (Globals.objLPR.getIndustry_Type().equals("4")){
            ll_summary.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            ll_detail.setVisibility(View.GONE);
            btn_pay_collec.setVisibility(View.GONE);
            btn_zclose.setVisibility(View.GONE);
            ll_master.setVisibility(View.GONE);
            ll_gst.setVisibility(View.GONE);
            ll_vat.setVisibility(View.GONE);
            btn_vehsummaryreport.setVisibility(View.VISIBLE);
            btn_vehiclein_report.setVisibility(View.VISIBLE);
            txt_accounts.setVisibility(View.GONE);
            tb_cususer.setVisibility(View.VISIBLE);
            tb_cus_vehtype.setVisibility(View.VISIBLE);
            tt_cus_spn.setVisibility(View.GONE);

        }
        else{
            btn_vehsummaryreport.setVisibility(View.GONE);
            btn_vehiclein_report.setVisibility(View.GONE);
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
            try {
                edt_from.setText(from);
                edt_to.setText(to);
            }
            catch(Exception e){}
        } else {
            try {
                if (from == null && to == null) {
                    edt_from.setText(date);
                    edt_to.setText(date);
                } else {
                    edt_from.setText(from);
                    edt_to.setText(to);
                }
            }
            catch(Exception e){

            }
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        try {
            fill_spinner_contact();
        }
        catch(Exception e)
        {

        }

        if(Globals.objLPR.getIndustry_Type().equals("4")){

            try{
                fill_spinner_User();
                itemnamelist=getAllItems();

            }
            catch(Exception e){

            }
        }
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

        if(Globals.objLPR.getIndustry_Type().equals("4")) {
            spn_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    User resultp = user_ArrayList.get(position);
//                    userCode = resultp.get_user_code();
                    username=userlist.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spn_vehtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    VehCode=itemnamelist.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if(settings.getIs_deliverydate().equals("true")){
            btn_delivery.setVisibility(View.VISIBLE);
        }
        else{
            btn_delivery.setVisibility(View.GONE);
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try {
                    updateLabel();
                }
                catch(Exception e){}
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
                try {
                    updateLabe2();
                }
                catch(Exception e){}
            }

        };

//        edt_from.setOnClickListener(new View.OnClickListener() {
//           /* @Override
//            public void onClick(View view) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(edt_from.getWindowToken(), 0);
//                new DatePickerDialog(ReportActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//
//            }*/
//        });
          edt_from.setOnClickListener(new DoubleClickListener()
           {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_from.getWindowToken(), 0);
                new DatePickerDialog(ReportActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
               @Override
 				public void onDoubleClick()
                {
                    // double-click code that is executed if the user double-taps
 					// within a span of 200ms (default).
 				}
 			});
        /*GestureDetector gd = new GestureDetector(this,new GestureDetector.OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        gd.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                // if the second tap hadn't been released and it's being moved

                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

        });*/

//

        edt_to.setOnClickListener(new DoubleClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_to.getWindowToken(), 0);
                new DatePickerDialog(ReportActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            @Override
            public void onDoubleClick()
            {
                // double-click code that is executed if the user double-taps
                // within a span of 200ms (default).
            }
        });

        btn_psumry_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String sql = "select '' as 'Sr.No.', purchase.contact_code as 'Supplier Code', contact.name as 'Supplier Name', purchase.date as 'Date', purchase.voucher_no as 'Voucher', purchase.total as 'Amount' from purchase left join contact on contact.contact_code = purchase.contact_code where date(purchase.date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                    String sql_footer = "";
                    ArrayList<Integer> numCols = new ArrayList<>();
                    numCols.add(5);
                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(3);

                    Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);
                    Globals.ReportCondition = "";
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
                catch(Exception e){

                }
            }
        });


        btn_vat_orderheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String sql = "SELECT '' AS 'Sr.No.',        orders.order_code as 'Order Code' ,        orders.order_date as 'Order Date' ,        (               SELECT contact.NAME               FROM   contact               WHERE  contact.contact_code = orders.contact_code) AS 'Buyers Name' ,        (               SELECT contact.gstin               FROM   contact               WHERE  contact.contact_code = orders.contact_code) AS 'Buyers GST' ,        (        (               SELECT Sum(order_detail.sale_price * order_detail.quantity)               FROM   order_detail               WHERE  order_detail.order_code = orders.order_code)) AS ' Amount w/o Tax ',        total_discount                                             AS 'Disount amount ',               (        (               SELECT (sum(tax*quantity) + orders.total_tax)               FROM   order_detail               WHERE  order_detail.order_code = orders.order_code )) AS 'Total VAT',        orders.total                                                 AS 'Invoice Amount' FROM   orders   WHERE  orders.order_status ='CLOSE'  And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();

                    numCols.add(5);
                    numCols.add(7);
                    numCols.add(8);
                    numCols.add(9);

                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);

                    Intent intent1 = new Intent(ReportActivity.this, VatOrderHeaderReport_view.class);
                    Globals.ReportCondition = "";
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
                catch(Exception e){

                }
            }
        });

        btn_vat_orderitemreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String sql = "SELECT     ''                         AS 'Sr.No.',            item_group.item_group_name AS 'Item Category' ,            order_detail.item_code     AS 'Item Code' ,            (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name/Stock',            item.hsn_sac_code          AS 'HSN Code',            order_detail.order_code    AS 'Invoice number',            Date(orders.order_date)    AS date,            Time(orders.order_date)    AS time,           (select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',           orders.remarks                                                                                    AS ' Remarks ',           (order_detail.sale_price )                                                                        AS 'Sales price (without Tax)',           order_detail.quantity                                                                             AS 'Quantity',           unit.code                                                                                         AS uom,           ((order_detail.sale_price) * order_detail.quantity)                                               AS 'Gross Amount w/o Tax',           Round((((sale_price - ((Cast(line_total AS FLOAT)/quantity) - tax))*100)/sale_price),2)           AS 'Discount %',           Round(((sale_price - ((Cast(line_total AS FLOAT)/quantity) - tax))* quantity),2)                  AS 'Disount amount',           Round(((sale_price - (sale_price - ((Cast(line_total AS FLOAT)/quantity) - tax)) )* quantity ),2) AS 'Amount after  Discount',           (order_detail.quantity * order_detail.tax)       AS 'Total VAT',            round(line_total,2)                              AS 'Net amount' FROM       order_detail INNER JOIN item ON         item.item_code = order_detail.item_code INNER JOIN item_group ON         item_group.item_group_code = item.item_group_code INNER JOIN unit ON         unit.unit_id = item.unit_id INNER JOIN orders ON         orders.order_code = order_detail.order_code   WHERE      orders.order_status ='CLOSE'  And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();

                    numCols.add(11);
                    numCols.add(14);
                    numCols.add(16);
                    numCols.add(17);
                    numCols.add(18);

                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(6);

                    Intent intent1 = new Intent(ReportActivity.this, VatOrderItemReport_view.class);
                    Globals.ReportCondition = "";
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", sql);
                    intent1.putExtra("name", btn_vat_orderitemreport.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);
                }
                catch(Exception e)
                {}
            }
        });
        btn_psumy_by_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String sql = " select '' as SNo ,item_group.item_group_code as 'Item Group Code',item_group.item_group_name as 'Item Group Name', item.item_code as 'Item Code',item.item_name  as 'Item Name',unit.name as 'Unit',item_location.quantity from purchase_detail LEFT JOIN item on item.item_code = purchase_detail.item_code LEFT JOIN unit on unit.unit_id = item.unit_id LEFT JOIN item_location on item_location.item_code = purchase_detail.item_code LEFT JOIN item_group on item_group.item_group_code = item.item_group_code where item.item_code = purchase_detail.item_code Group by item.item_code";

                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();

                    ArrayList<Integer> dateCols = new ArrayList<>();
                    //dateCols.add(5);
                    Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);
                    Globals.ReportCondition = "";
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
                catch(Exception e){}
            }
        });


        btn_vehiclein_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    String selectQuery;

                    if(spn_vehtype.getSelectedItem().equals("Select"))
                    {
                        if(spn_user.getSelectedItem().equals("Select"))
                            selectQuery = "  SELECT '' AS 'Sr.No.' , od.remarks AS 'Vehicle Type', date(od.order_date) as Date,COUNT(od.remarks) AS 'Total Count' ,sum(od_det.sale_price) AS 'Fare Amnt.',u.name AS 'User Name' From orders od left join order_detail od_det ON od_det.order_code=od.order_code LEFT JOIN user u ON u.user_code=od.modified_by where od.is_active='1' AND date(od.order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')  GROUP BY od.remarks,date(od.order_date)";
                        else
                            selectQuery = "  SELECT '' AS 'Sr.No.' , od.remarks AS 'Vehicle Type', date(od.order_date) as Date,COUNT(od.remarks) AS 'Total Count' ,sum(od_det.sale_price) AS 'Fare Amnt.',u.name AS 'User Name' From orders od left join order_detail od_det ON od_det.order_code=od.order_code LEFT JOIN user u ON u.user_code=od.modified_by where od.is_active='1' AND  u.name like '"+username+"' AND date(od.order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')  GROUP BY od.remarks,date(od.order_date)";

                    }
                    else{
                        if(spn_user.getSelectedItem().equals("Select"))
                            selectQuery = "SELECT '' AS 'Sr.No.' , od.remarks AS 'Vehicle Type', date(od.order_date) as Date,COUNT(od.remarks) AS 'Total Count' ,sum(od_det.sale_price) AS 'Fare Amnt.',u.name From orders od left join order_detail od_det ON od_det.order_code=od.order_code LEFT JOIN user u ON u.user_code=od.modified_by where od.is_active='1' AND date(od.order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') and od.remarks='"+VehCode+"' GROUP BY od.remarks,date(od.order_date)";
                        else
                            selectQuery = "SELECT '' AS 'Sr.No.' , od.remarks AS 'Vehicle Type', date(od.order_date) as Date,COUNT(od.remarks) AS 'Total Count' ,sum(od_det.sale_price) AS 'Fare Amnt.',u.name From orders od left join order_detail od_det ON od_det.order_code=od.order_code LEFT JOIN user u ON u.user_code=od.modified_by where od.is_active='1' AND  u.name like '"+username+"' AND date(od.order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') and od.remarks='"+VehCode+"' GROUP BY od.remarks,date(od.order_date)";
                    }
                    String sql_footer = "" ;
                    ArrayList<Integer> numCols = new ArrayList<>();


                    numCols.add(3);
                    numCols.add(4);
                    // numCols.add(5);


                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);


                    Intent intent1 = new Intent(ReportActivity.this, VehicleInReport.class);
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", selectQuery);
                    intent1.putExtra("name", btn_vehiclein_report.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);
                }
                catch(Exception e){}
            }
        });

        btn_vehsummaryreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String selectQuery;
                    if(spn_vehtype.getSelectedItem().equals("Select")) {
                        selectQuery = "SELECT ''                         AS 'Sr.No.' , od.remarks AS 'Vehicle Type',COUNT(*) AS 'Total Count' , sum(od_det.sale_price) AS 'Fare Amnt.' From orders od left join order_detail od_det ON od_det.order_code=od.order_code where od.is_active='1' GROUP BY od.remarks";
                    }
                    else{
                        selectQuery = "SELECT ''                         AS 'Sr.No.' , od.remarks AS 'Vehicle Type',COUNT(*) AS 'Total Count' , sum(od_det.sale_price) AS 'Fare Amnt.' From orders od left join order_detail od_det ON od_det.order_code=od.order_code where od.is_active='1' and od.remarks='"+VehCode+"' GROUP BY od.remarks";

                    }
                    String sql_footer = "" ;
                    ArrayList<Integer> numCols = new ArrayList<>();

                    numCols.add(2);
                    numCols.add(3);
                    // numCols.add(5);


                    ArrayList<Integer> dateCols = new ArrayList<>();
                    // dateCols.add(7);


                    Intent intent1 = new Intent(ReportActivity.this, VehicleInReport.class);
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", selectQuery);
                    intent1.putExtra("name", btn_vehsummaryreport.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);
                }
                catch(Exception e){}
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
                  /*  pDialog = new ProgressDialog(ReportActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Getting data...");
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {*/

                    get_CustomerStatment_server(serial_no,Globals.syscode2,android_id,myKey);
                           /* Globals.online_report_cursor = cursor;
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
                            });*/
                      /*  }
                    }.start();
*/
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cus_smry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                   /* pDialog = new ProgressDialog(ReportActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Getting data...");
                    pDialog.show();
                    new Thread() {
                        @Override
                        public void run() {

                            pDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {*/
                    get_CustomerSummery_server(serial_no,Globals.syscode2,android_id,myKey);

                           /*     }
                            });
                        }
                    }.start();*/

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_low_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sql = "select '' as 'Sr.No.',item_group.item_group_code as 'Item Group Code',item_group.item_group_name as 'Item Group Name',item.item_code as 'Item Code',item.item_name as 'Item Name',unit.name as 'Unit Name',item_location.quantity as 'Quantity' from item left join item_group on item_group.item_group_code = item.item_group_code left join unit on unit.unit_id = item.unit_id left join item_location on item.item_code = item_location.item_code where item_location.quantity <item_location.reorder_point";

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
                final String sql = "   Select '' as 'Sr.No.',item.item_code as 'Item Code',(CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name',SUM(quantity*cost_price) AS 'Cost Price', sum(quantity*sale_price) AS 'Sale Price',  sum(quantity) AS 'Quantity', SUM(((quantity*sale_price) - (quantity*cost_price))) AS 'Net Profit', cast((SUM(((cast(quantity as float)*sale_price) - (cast(quantity as float)*cost_price)))/SUM(quantity*cost_price)) as float)*100 AS 'Net Profit Per' From order_detail inner join item  on item.item_code = order_detail.item_code inner join orders  on orders.order_code = order_detail.order_code WHERE orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  item.item_code ORDER BY item.item_name ASC";

                String sql_footer = "   Select '' as 'Sr.No.',item.item_code as 'Item Code',(CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name',SUM(quantity*cost_price) AS 'Cost Price', sum(quantity*sale_price) AS 'Sale Price',  sum(quantity) AS 'Quantity', SUM(((quantity*sale_price) - (quantity*cost_price))) AS 'Net Profit', cast((SUM(((cast(quantity as float)*sale_price) - (cast(quantity as float)*cost_price)))/SUM(quantity*cost_price)) as float)*100 AS 'Net Profit Per' From order_detail inner join item  on item.item_code = order_detail.item_code inner join orders  on orders.order_code = order_detail.order_code WHERE orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  item.item_code ORDER BY item.item_name ASC";
                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(3);
                numCols.add(4);
                numCols.add(6);

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
                Intent intent1 = new Intent(ReportActivity.this, ItemProfitReport_view.class);    Globals.ReportCondition = "";
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
   /*             if (Globals.Industry_Type.equals("6")){
                    sql = "select  '' as srno,order_detail.item_code as 'Item Code'; ,((Select  item_name from  item Where item.item_code =  ticket_setup.tck_from)  as ' Item Name' || '-' || (Select  item_name from  item Where item.item_code =  ticket_setup.tck_to)) as Item_name,\n" +
                            "Sum((order_detail.sale_price )) as 'Sales price  (without Tax)', SUM(order_detail.quantity) AS 'Quantity',  \n" +
                            "SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax',  \n" +
                            "SUM( line_total) As 'Net amount'  From order_detail \n" +
                            "inner join ticket_setup on order_detail.item_code = ticket_setup.id\n" +
                            "inner join orders on orders.order_code = order_detail.order_code  \n" +
                            "Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')" +
                            "Group by order_detail.item_code";
                }else {*/
                // sql="select  '' as srno, '' as 'Item Category' ,'' as 'Item Code', '' as 'Item Name', Sum((order_detail.sale_price * order_detail.quantity )) as 'Sales price  (without Tax)', SUM(order_detail.quantity) AS 'Quantity', SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax', SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount', SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount'  From order_detail inner join  item on item.item_code = order_detail.item_code inner join item_group on item_group.item_group_code = item.item_group_code  inner join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                //  sql = "select  '' as 'Sr.No.',item_group.item_group_name as 'Item Category' ,item.item_code as 'Item Code', item.item_name as 'Item Name',Sum((order_detail.sale_price * order_detail.quantity )) as 'Sales price (without Tax)', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount w/o Tax',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount'  From order_detail inner join  item on item.item_code = order_detail.item_code inner join item_group on item_group.item_group_code = item.item_group_code inner join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code";
                //}

                // nEW qUERY
           //   sql= "select  '' as 'Sr.No.',groupmaster.grouptype as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', ot.name AS 'Order Type', (order_detail.sale_price) as 'Sales price ', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount ',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount',  SUM( line_total) As 'Net amount'  From order_detail left join  item on item.item_code = order_detail.item_code and  item.brand_Code = order_detail.brand_Code and item.group_code = order_detail.group_code and item.packing_code = order_detail.packing_code  left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code left join  order_type ot on  ot.order_type_id =   orders.order_type_id left join GroupMaster groupmaster ON groupmaster.Group_Code= order_detail.group_code Where orders.order_status ='CLOSE' And date(order_date)   BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,item.brand_code,item.group_code,item.packing_code,orders.order_type_id ORDER by orders.modified_date desc";
                // 25-10-2021
            //    sql="select  '' as 'Sr.No.','' as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', ot.name AS 'Order Type', (order_detail.sale_price) as 'Sales price ', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount ',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount',  SUM( line_total) As 'Net amount'  From order_detail left join  item on item.item_code = order_detail.item_code left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code left join  order_type ot on  ot.order_type_id =   orders.order_type_id  Where orders.order_status ='CLOSE' And date(order_date)   BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,item.brand_code,item.group_code,item.packing_code,orders.order_type_id ORDER by orders.modified_date desc";

                sql="select  '' as 'Sr.No.',item_group_name as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', ot.name AS 'Order Type', (order_detail.sale_price) as 'Sales price ', SUM(order_detail.quantity) AS 'Quantity' , \n" +
                       "SUM(((order_detail.sale_price)  * order_detail.quantity)) AS 'Gross Amount',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax)) * quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount',  SUM( line_total) As 'Net amount'  From order_detail order_detail left join  item on item.item_code = order_detail.item_code left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code left join  order_type ot on  ot.order_type_id =   orders.order_type_id  Where orders.order_status ='CLOSE' And date(order_date)   BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,orders.order_type_id ORDER by orders.modified_date desc";
                // sql="select  '' as 'Sr.No.',groupmaster.grouptype as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', ot.name AS 'Order Type',Sum((order_detail.sale_price * order_detail.quantity )) as 'Sales price (without Tax)', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount w/o Tax',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount' ,item.brand_code,item.group_code,item.packing_code From order_detail left join  item on item.item_code = order_detail.item_code and  item.brand_Code = order_detail.brand_Code and item.group_code = order_detail.group_code and item.packing_code = order_detail.packing_code  left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code left join  order_type ot on  ot.order_type_id =   orders.order_type_id left join GroupMaster groupmaster ON groupmaster.Group_Code= order_detail.group_code Where orders.order_status ='CLOSE' And date(order_date)   BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,item.brand_code,item.group_code,item.packing_code,orders.order_type_id";
                // oLD qUERY
                // sql="select  '' as 'Sr.No.',item_group.item_group_name as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', item.is_modifier ='1' AS 'TYPE',Sum((order_detail.sale_price * order_detail.quantity )) as 'Sales price (without Tax)', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount w/o Tax',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount' ,item.brand_code,item.group_code,item.packing_code From order_detail left join  item on item.item_code = order_detail.item_code and  item.brand_Code = order_detail.brand_Code and item.group_code = order_detail.group_code and item.packing_code = order_detail.packing_code  left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,item.brand_code,item.group_code,item.packing_code";

                String sql_footer = "select  '' as 'Sr.No.',item_group.item_group_name as 'Item Category' ,item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name', item.is_modifier ='1' AS 'TYPE',Sum(order_detail.sale_price) as 'Sales price (without Tax)', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount w/o Tax',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount'  From order_detail left join  item on item.item_code = order_detail.item_code left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(5);
                numCols.add(6);
                numCols.add(7);
                numCols.add(8);
                numCols.add(9);
                numCols.add(10);
                numCols.add(11);
                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
                Intent intent1 = new Intent(ReportActivity.this, OrderDetailReport_View.class);    Globals.ReportCondition = "";
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
                final String sql = "Select '' as 'Sr.No.', item_group_code as 'Item Group Code',item_group_name as 'Item Group Name' From item_Group where is_active = '1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();


                Intent intent1 = new Intent(ReportActivity.this, ItemGroupReport_view.class);    Globals.ReportCondition = "";
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
               /* final String sql = "Select '' as 'Sr.No.',item_Group.item_group_code as 'Item Group Code',item_Group.item_group_name as 'Item Group Name',item.item_code as 'Item Code',item.item_name as 'Item Name',item.item_type  as 'Item Type',item.hsn_sac_code as 'HSN Code',item.barcode as 'Barcode',item.unit_id as 'Unit ID',unit.name as 'Unit Name',item.is_inclusive_tax as 'Is Inclusive Tax',item_location.cost_price as 'Cost Price',item_location.selling_price as 'Sale Price',item_location.new_sell_price  as 'New Sale Price'   From item \n" +
                        "inner join  item_Group  on item_Group.item_group_code = item.item_group_code\n" +
                        "left join  unit on  unit.unit_id =  item.unit_id \n" +
                        "left join  item_location  on  item_location.item_code  =  item.item_code     \n" +
                        " where item.is_active = '1'";*/

              //  final String sql ="Select '' as 'Sr.No.',groupmaster.Group_Code as 'ItemGroup Code',groupmaster.grouptype as 'ItemGroup Name',item.item_code as 'Item Code',(CASE WHEN item.is_modifier ='1' THEN item.item_name   ELSE item.item_name END) as 'Item Name',packingmaster.Packing_Code as 'Packing Code',packingmaster.Packing_Desc as 'Packing Name',item.brand_Code,cost_price as 'Cost Price',item_location.selling_price as 'Sale Price',item_location.new_sell_price  as 'New Sale Price'   From item left join GroupMaster groupmaster ON groupmaster.Group_Code=item.Group_Code left join  Packingmaster packingmaster ON  packingmaster.Packing_Code =  item.Packing_Code left join  item_location  on  item_location.item_code  =  item.item_code";
                final String sql="Select '' as 'Sr.No.','' as 'ItemGroup Code','' as 'ItemGroup Name',item.item_code as 'Item Code',(CASE WHEN item.is_modifier ='1' THEN item.item_name   ELSE item.item_name END) as 'Item Name',cost_price as 'Cost Price',item_location.selling_price as 'Sale Price',item_location.new_sell_price  as 'New Sale Price'   From item  left join  item_location  on  item_location.item_code  =  item.item_code";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(11);
                numCols.add(12);
                numCols.add(13);
                ArrayList<Integer> dateCols = new ArrayList<>();


                Intent intent1 = new Intent(ReportActivity.this, ItemReport_view.class);    Globals.ReportCondition = "";
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
                final String sql = "select '' as 'Sr.No.',business_group.business_group_code as 'Contact Group Code',business_group.name as 'Contact Group Name' from business_group where business_group.is_active ='1'";
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
                final String sql = "select '' as 'Sr.No.',Contact.contact_code as 'Contact Code',Contact.title as 'Title',Contact.name as 'Name',Contact.gender as 'Gender',Contact.dob as 'DOB',Contact.company_name as 'Company Name',Contact.contact_1 as 'Contact 1',\n" +
                        "Contact.contact_2 as 'Contact 2',Contact.email_1 as 'Email 1',Contact.email_2 as 'Email 2',Contact.credit_limit as 'Credit Limit' ,Contact.gstin as 'GSTN'  from Contact where Contact.is_active = '1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();

                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(5);

                Intent intent1 = new Intent(ReportActivity.this, ContactReport_view.class);    Globals.ReportCondition = "";
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
                final String sql = "select '' as 'Sr.No.',tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as 'Tax Rate' from tax where tax.is_active ='1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);

                ArrayList<Integer> dateCols = new ArrayList<>();
                Intent intent1 = new Intent(ReportActivity.this, TaxReport_view.class);    Globals.ReportCondition = "";
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
                final String sql = " select '' as 'Sr.No.',item.item_code as 'Item Code', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name',item.hsn_sac_code as 'HSN Code',item.barcode as 'Barcode',item.item_type as 'Item Type',item.is_inclusive_tax as 'Is Inclusive Tax',tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as'Tax Rate' from  item_group_tax\n" +
                        "inner join tax on tax.tax_id = item_group_tax.tax_id\n" +
                        "inner join item  on item.item_code  = item_group_tax .item_group_code";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(10);

                ArrayList<Integer> dateCols = new ArrayList<>();

                Intent intent1 = new Intent(ReportActivity.this, ItemTaxReport_view.class);    Globals.ReportCondition = "";
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
                final String sql = "select '' as 'Sr.No.',user.user_code as 'User Code',user.name as 'User Name',user.email as 'User Email',user.max_discount as 'Max Discount' from user where  user.is_active ='1'";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);
                ArrayList<Integer> dateCols = new ArrayList<>();


                Intent intent1 = new Intent(ReportActivity.this, UserReport_view.class);    Globals.ReportCondition = "";
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
                final String sql = "select '' as 'Sr.No.',tax.tax_id as 'Tax ID',tax.tax_name as 'Tax Name',tax.tax_type as 'Tax Type',tax.rate as 'Tax Rate',order_type.name as 'Name' from  order_type_tax\n" +
                        "inner join order_type  on order_type.order_type_id = order_type_tax.order_type_id \n" +
                        "inner join tax  on tax.tax_id = order_type_tax.tax_id";
                String sql_footer = "";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(4);
                ArrayList<Integer> dateCols = new ArrayList<>();

                Intent intent1 = new Intent(ReportActivity.this, OrderTaxReport_view.class);    Globals.ReportCondition = "";
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
                try {
                    final String sql = "SELECT `order_detail`.`item_code` as 'Item Number', `item_group`.`item_group_name` as 'Item Category', (CASE WHEN item.is_modifier ='1' THEN item.item_name || ' ' || '(M)'  ELSE item.item_name END) as 'Item Name or Stock', `item`.`hsn_sac_code` as 'HSN Code', `order_detail`.`order_code` as 'Invoice Number', date(orders.order_date) as Date, time(orders.order_date) as Time, `orders`.`remarks` as  'Remarks', ((order_detail.sale_price)* order_detail.quantity) as 'Sales price without Tax', `order_detail`.`quantity` AS `Quantity`, `unit`.`code` as `UOM`, ((order_detail.sale_price)* order_detail.quantity) As 'Gross Amount without Tax', round((((sale_price - ((line_total /quantity) - tax))*100)/sale_price), 2) as 'Discount Per', round(((sale_price - ((line_total /quantity) - tax))* quantity), 2) as 'Disount Amount', round(((sale_price - (sale_price - ((line_total /quantity) - tax)) )* quantity ), 2) as 'Amount After Discount', (Select t.rate fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 1 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'CGST PER', (Select odt.tax_value fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 1 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'GGST AMOUNT', (Select t.rate fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 2 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'SGST PER', (Select odt.tax_value fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 2 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'SGST AMOUNT', (Select t.rate fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 3 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'IGST PER', (Select odt.tax_value fROm tax t left join order_detail_tax odt ON odt.tax_id = t.tax_id left join order_detail od on od.sr_no = odt.sr_no and od.item_code = odt.item_code and od.order_code = odt.order_code where t.tax_id = (select tax_id from Sys_Tax_Group where tax_master_id = 3 limit 1) AND odt.order_code = order_detail.order_code AND odt.item_code = order_detail.item_code AND odt.sr_no = order_detail.sr_no ) as 'IGST AMOUNT', (order_detail.quantity * order_detail.tax) as 'Total Tax', round(order_detail.line_total, 2) As 'Net Amount' FROM `order_detail` LEFT JOIN `item` ON `item`.`item_code` = `order_detail`.`item_code` LEFT JOIN `item_group` ON `item_group`.`item_group_code` = `item`.`item_group_code` LEFT JOIN `unit` ON `unit`.`unit_id` = `item`.`unit_id` LEFT JOIN `orders` `orders` ON `orders`.`order_code` = `order_detail`.`order_code` WHERE `orders`.`order_status` = 'CLOSE' AND DATE(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') ORDER BY `orders`.`order_date` DESC";
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

                    Intent intent1 = new Intent(ReportActivity.this, GstOrderItemReport_view.class);
                    Globals.ReportCondition = "";
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
                catch(Exception e){

                }
            }
        });

        btn_gst_sale_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                final String sql = "Select '' as SNO,orders.order_code ,orders.order_date ,\n" +
//                        "(select contact.name from  contact where contact.contact_code  = orders.contact_code) as  'Buyers Name' ,\n" +
//                        "(select contact.gstin  from  contact where contact.contact_code  = orders.contact_code) as  'Buyers GST' ,\n" +
//                        "((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code)  as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount ) As 'Amount after Discount',\n" +
//                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1') LIMIT 1) as CGST_PER,\n" +
//                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
//                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
//                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1') \n" +
//                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='1')))  as CGST_AMOUNT,\n" +
//                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2') LIMIT 1) as SGST_PER,\n" +
//                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
//                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
//                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2') \n" +
//                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='2'))) as SGST_AMOUNT,\n" +
//                        "(select tax.rate  from tax where tax.tax_id IN (  select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3') LIMIT 1) as IGST_PER,\n" +
//                        "((Select  SUM(order_detail.quantity * order_detail_tax.tax_value)     from  order_detail_tax \n" +
//                        "inner join  order_detail on order_detail.item_code =  order_detail_tax.item_code and order_detail.order_code = order_detail_tax.order_code \n" +
//                        "where order_detail_tax.order_code =orders.order_code  and order_detail_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3') \n" +
//                        " ) + (select   order_tax.tax_value  from   order_tax  where order_tax.order_code = orders.order_code and order_tax.tax_id in (select tax_id from  Sys_Tax_Group WHERE tax_master_id ='3'))) as IGST_AMOUNT,\n" +
//                        "((select (sum(tax*quantity) + orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  )) as TOTALGST,\n" +
//                        "orders.total  as INV_AMOUNT\n" +
//                        "  from  orders Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
//

                try {
                    final String sql = "SELECT '' AS 'Sr.No.',        orders.order_code as 'Order No' ,       \n" +
                            " orders.order_date as 'Order Date' ,       \n" +
                            " (               SELECT contact.NAME               FROM   contact               WHERE  contact.contact_code = orders.contact_code) AS 'Buyers Name' ,        (               SELECT contact.gstin               FROM   contact               WHERE  contact.contact_code = orders.contact_code) AS 'Buyers GST' ,        (        (               SELECT Sum(order_detail.sale_price * order_detail.quantity)               FROM   order_detail               WHERE  order_detail.order_code = orders.order_code)) AS ' Amount w/o Tax ',        (               SELECT (Round((((sale_price - ((Cast(line_total AS FLOAT)/quantity) - tax))*100)/sale_price),2) )              FROM   order_detail               WHERE  order_detail.order_code = orders.order_code) AS ' Discount %',        total_discount                                             AS 'Disount amount ',        ((        (               SELECT Sum(order_detail.sale_price * order_detail.quantity)               FROM   order_detail               WHERE  order_detail.order_code = orders.order_code)) - total_discount ) AS 'Amount after Discount',       (               SELECT tax.rate               FROM   tax               WHERE  tax.tax_id IN                      (                             SELECT tax_id                             FROM   sys_tax_group                             WHERE  tax_master_id ='1') limit 1) AS 'CGST Per',        (        (                 Select Case WHEN (     SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code =orders.order_code and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='1')) >= 0 THEN  \t\t\t\t\t\t\t( SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code =orders.order_code and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='1'))\t\t\t\t\tELSE \t\t0 End orderdetail\t) +        (               SELECT                      CASE                             WHEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE order_tax.order_code = orders.order_code                                           AND     order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='1')) >=0 THEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE  order_tax.order_code = orders.order_code                                           AND    order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='1') )                             ELSE '0'                      END ordertax )) AS 'CGST Amount',        (               SELECT tax.rate               FROM   tax               WHERE  tax.tax_id IN                      (                             SELECT tax_id                             FROM   sys_tax_group                             WHERE  tax_master_id ='2') limit 1) AS 'SGST Per',        (        (                   Select Case WHEN (     SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code =orders.order_code and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='2')) >= 0 THEN  \t\t\t\t\t\t\t( SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code =orders.order_code and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='2'))\t\t\t\t\tELSE \t\t0 End orderdetail\t ) +        (               SELECT                      CASE                             WHEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE order_tax.order_code = orders.order_code                                           AND     order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='2')) >=0 THEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE  order_tax.order_code = orders.order_code                                           AND    order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='2' AND  order_tax.order_code = orders.order_code) )                             ELSE '0'                      END ordertax )) AS 'SGST Amount',        (               SELECT tax.rate               FROM   tax               WHERE  tax.tax_id IN                      (                             SELECT tax_id                             FROM   sys_tax_group                             WHERE  tax_master_id ='3') limit 1) AS 'IGST Per',        (        (                  Select Case WHEN (     SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code ='A-11' and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='3')) >= 0 THEN  \t\t\t\t\t\t\t( SELECT     sum(order_detail.quantity * order_detail_tax.tax_value)                   FROM       order_detail_tax                   INNER JOIN order_detail                   ON         order_detail.item_code = order_detail_tax.item_code                   AND        order_detail.order_code = order_detail_tax.order_code                   WHERE      order_detail.order_code ='A-11' and                          order_detail_tax.tax_id IN                              (                                     SELECT tax_id                                     FROM   sys_tax_group                                     WHERE  tax_master_id ='3'))\t\t\t\t\tELSE \t\t0 End orderdetail\t ) +        (               SELECT                      CASE                             WHEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE order_tax.order_code = orders.order_code                                           AND     order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='3')) >=0 THEN                                    (                                           SELECT order_tax.tax_value                                           FROM   order_tax                                           WHERE  order_tax.order_code = orders.order_code                                           AND    order_tax.tax_id IN                                                  (                                                         SELECT tax_id                                                         FROM   sys_tax_group                                                         WHERE  tax_master_id ='3') )                             ELSE '0'                      END ordertax )) AS 'IGST Amount',        (        (               SELECT (sum(tax*quantity) + orders.total_tax)               FROM   order_detail               WHERE  order_detail.order_code = orders.order_code )) AS 'Total Gst',        orders.total                                                 AS 'Invoice Amount' FROM   orders   WHERE  orders.order_status ='CLOSE'    And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
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

                    Intent intent1 = new Intent(ReportActivity.this, GstOrderHeaderReport_view.class);
                    Globals.ReportCondition = "";
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
                catch(Exception e){}
            }
        });

        btn_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sql = "select '' as SRNO,orders.order_code AS'Invoice number',orders.order_date AS'Invoice Date',orders.remarks As 'Remarks',orders.total As 'Invoice Amount',order_payment.pay_amount As 'Credit Amount' from orders inner join order_payment on order_payment.order_code = orders.order_code Where orders.order_status ='CLOSE' And date(orders.delivery_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "select '' as SRNO,'' AS'Invoice number','' AS'Invoice Date','' As 'Remarks',sum(orders.total) As 'Invoice Amount',sum(order_payment.pay_amount) As 'Credit Amount' from orders inner join order_payment on order_payment.order_code = orders.order_code Where orders.order_status ='CLOSE' And date(orders.delivery_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

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

                final String sql = "SELECT  '' AS 'Sr.No.' ,orders.order_code AS'Invoice number' ,date(orders.order_date) AS Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code)  as 'Device Name',remarks AS 'Remarks',(select payment_name from payments where payment_id = order_payment.payment_id) as 'Payment method',(((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code  )) - total_discount ) As ' Sales Amount After Discount ',total as ' Net Amount ', order_payment.pay_amount as 'Pay Amount' ,order_type.name AS 'Order Type',contact.name AS 'Contact' FROM order_payment INNER JOIN orders ON  order_payment.order_code = orders.order_code LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN contact ON contact.contact_code=orders.contact_code LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') ORDER by orders.modified_date desc";


                String sql_footer = "SELECT  '' AS 'Sr.No.' ,'' AS'Invoice number' ,'' AS Date,'' as Time  ,'' as 'Sales person',''  as 'Device Name,'' AS 'Remarks',\n" +
                        "'' as 'Payment method',sum((((select sum(order_detail.sale_price * order_detail.quantity) From\n" +
                        "order_detail Where order_detail.order_code = orders.order_code)) - total_discount )) As ' Sales Amount without Tax After Discount ',sum((select (sum(tax*quantity) + orders.total_tax  ) From\n" +
                        "order_detail Where order_detail.order_code = orders.order_code  )) as ' Tax amount',\n" +
                        " sum(total) as ' Net Amount ', sum(order_payment.pay_amount) as 'Pay Amount' ,'' AS 'Order Type','' AS contact FROM order_payment INNER JOIN orders ON  order_payment.order_code = orders.order_code LEFT JOIN location ON location.location_id=orders.location_id LEFT JOIN contact ON contact.contact_code=orders.contact_code LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')  ORDER by orders.modified_date desc";

                ArrayList<Integer> numCols = new ArrayList<>();

                numCols.add(8);
                numCols.add(9);
                numCols.add(10);

                ArrayList<Integer> dateCols = new ArrayList<>();


                Intent intent1 = new Intent(ReportActivity.this, OrderPaymentReport_View.class);    Globals.ReportCondition = "";
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

                try {

                    // New Query
                //    final String sql = " SELECT   ''  AS 'Sr.No.' , `u`.`name` AS `Sales Person`, (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name', sum(( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Amount Without Tax',  sum((( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail  WHERE  order_detail.order_code = sc.order_code))- total_discount)) AS 'Amount After Discount',sum(( ( SELECT (sum(tax*quantity)+sc.total_tax) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Tax Amount', sum(sc.total)                                           AS 'Net Amount', count(sc.order_id)                                      AS 'Total Invoice', sum(sc.total_discount)                                  AS 'Discount Amount', `sc`.`location_id` AS 'Location Id', `ot`.`name` AS 'Name' FROM     `orders` `sc` LEFT JOIN  USER u  on u.user_code = sc.modified_by inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE    `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND      `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND   date('" + edt_to.getText().toString()+ "') GROUP BY date(sc.order_date),  `ot`.`name`  ORDER BY `sc`.`order_date` DESC";

                    // Old Query

                    //  final String sql = "SELECT   ''  AS 'Sr.No.' , `u`.`name` AS `Sales Person`, (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name', sum(( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Amount Without Tax',  sum((( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail  WHERE  order_detail.order_code = sc.order_code))- total_discount)) AS 'Amount After Discount',sum(( ( SELECT (sum(tax*quantity)+sc.total_tax) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Tax Amount', sum(sc.total)                                           AS 'Net Amount', count(sc.order_id)                                      AS 'Total Invoice', sum(sc.total_discount)                                  AS 'Discount Amount', `sc`.`location_id` AS 'Location Id', `ot`.`name` AS 'Name' FROM     `orders` `sc` LEFT JOIN  USER u  on u.user_code = sc.modified_by inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE    `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND      `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND   date('" + edt_to.getText().toString()+ "') GROUP BY date(sc.order_date),  `ot`.`name`  ORDER BY `sc`.`order_date` DESC";

                    final String sql = "SELECT   ''  AS 'Sr.No.' , `u`.`name` AS `Sales Person`, (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name', sum(( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Amount Without Tax',  sum((( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail  WHERE  order_detail.order_code = sc.order_code))- total_discount)) AS 'Amount After Discount',sum(( ( SELECT (sum(tax*quantity)+sc.total_tax) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Tax Amount', sum(sc.total)                                           AS 'Net Amount', count(sc.order_id)                                      AS 'Total Invoice', sum(sc.total_discount)                                  AS 'Discount Amount', `sc`.`location_id` AS 'Location Id', `ot`.`name` AS 'Name' FROM     `orders` `sc` LEFT JOIN  USER u  on u.user_code = sc.modified_by inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE    `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND      `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND   date('" + edt_to.getText().toString()+ "') GROUP BY date(sc.order_date),  `ot`.`name`  ORDER BY `sc`.`order_date` DESC";

                    String sql_footer = "SELECT   ''  AS 'Sr.No.' , `u`.`name` AS `Sales Person`, (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name', sum(( ( SELECT sum(order_detail.sale_price*order_detail.quantity) FROM   order_detail WHERE  order_detail.order_code = sc.order_code))) AS 'Amount',   sum(sc.total)                                           AS 'Net Amount', count(sc.order_id)                                      AS 'Total Invoice', `lpd`.`Location_name` AS 'Location', `ot`.`name` AS 'Order Type' FROM     `orders` `sc` LEFT JOIN  USER u  on u.user_code = sc.modified_by left join  order_type ot on  ot.order_type_id =   sc.order_type_id  left join Lite_POS_Device lpd on lpd.Location_Code=sc.location_id WHERE    `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND      `sc`.`order_status` = 'CLOSE' AND      date(order_date)  BETWEEN date('\" + edt_from.getText().toString() + \"') AND   date('\" + edt_to.getText().toString()+ \"') GROUP BY date(sc.order_date),  `ot`.`name`,sc.order_type_id  ORDER BY sc.order_date DESC";

                    ArrayList<Integer> numCols = new ArrayList<>();



                    numCols.add(3);
                    numCols.add(4);
                    numCols.add(5);
                    numCols.add(6);
                    numCols.add(7);
                    //numCols.add(9);

                    ArrayList<Integer> dateCols = new ArrayList<>();
                    //dateCols.add(1);

                    Intent intent1 = new Intent(ReportActivity.this, DailySales_Report.class);
                    Globals.ReportCondition = "";
                    intent1.putExtra("type", "");
                    intent1.putExtra("qry", sql);
                    intent1.putExtra("name", btn_daily_report.getText().toString());
                    intent1.putExtra("qry_footer", sql_footer);
                    intent1.putIntegerArrayListExtra("numCols", numCols);
                    intent1.putIntegerArrayListExtra("dateCols", dateCols);
                    intent1.putExtra("from", edt_from.getText().toString());
                    intent1.putExtra("to", edt_to.getText().toString());
                    intent1.putExtra("operation", operation);
                    startActivity(intent1);
                }
                catch(Exception e){}
            }
        });

        btn_zclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
//                    final String sql = "select  '' AS SRNO, Z_Close.device_code,date(Z_Close.date) as Date,  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='OB') As OB, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='EXP') As EXP, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CASH') As CASH, (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='BANK CARD') As 'BANK CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='LOYALTY CARD') As 'LOYALTY CARD', (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CHEQUE') As 'CHEQUE',  (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CUSTOMER ACCOUNT') As 'CUSTOMER ACCOUNT',   (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='PROMO CODE') As 'PROMO CODE',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='KNET') As 'KNET',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='CR AMT') As 'CR Amount',    (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='DR AMT') As 'DR Amount',(SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='RETURN') As 'Total Return', (z_close.total_amount - (SELECT amount from Z_Detail Where z_detail.z_code = z_close.z_code and z_detail.type ='RETURN')) As 'Total Cash',(SELECT SUM(amount) from Z_Detail Where z_detail.z_code = z_close.z_code and type not in ('OB','EXP','CR AMT','DR AMT','RETURN','LOYALTY CARD','PROMO CODE')) As 'Total Sales',Z_Close.z_code From Z_Close  INNER JOIN z_detail ON z_detail.z_code = z_close.z_code  Where date(date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')    Group by date(Z_Close.date),Z_Close.z_code";
                    //This is for hamseer without return in total
                    final String sql = "SELECT ''                                                                  AS        'Sr.No.',      (select device_name from  Lite_POS_Device where  Device_Id =   z_close.device_code) as 'Device Name'  ,        Date(z_close.date)                                                  AS        Date,        (SELECT amount         FROM   z_detail         WHERE  z_detail.z_code = z_close.z_code                AND z_detail.type = 'OB')                                   AS OB        ,        (SELECT amount         FROM   z_detail         WHERE  z_detail.z_code = z_close.z_code                AND z_detail.type = 'EXP')                                  AS        EXP,        (SELECT amount         FROM   z_detail         WHERE  z_detail.z_code = z_close.z_code                AND z_detail.type = 'CASH')                                 AS        'CASH SALES',                               (SELECT amount         FROM   z_detail         WHERE  z_detail.z_code = z_close.z_code                AND z_detail.type = 'CR AMT')                               AS        'Accounts Cash',               (SELECT amount         FROM   z_detail         WHERE  z_detail.z_code = z_close.z_code                AND z_detail.type = 'RETURN')                               AS        'Total Return',        ( z_close.total_amount - (SELECT amount from z_detail where z_detail.z_code=z_close.z_code and z_detail.type='RETURN'  ) )                                            AS        'Total Cash',              z_close.z_code   AS  'Z Code'   FROM   z_close        INNER JOIN z_detail                ON z_detail.z_code = z_close.z_code    Where date(date) BETWEEN date('" + edt_from.getText().toString() + "') AND   date('" + edt_to.getText().toString()+ "')    Group by date(Z_Close.date),Z_Close.z_code";

                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();

                    numCols.add(3);
                    numCols.add(4);
                    numCols.add(5);
                    numCols.add(6);
                    numCols.add(7);
                    numCols.add(8);


                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);
                    Intent intent1 = new Intent(ReportActivity.this, ZeroReportView.class);    Globals.ReportCondition = "";
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
                final String sql = "Select  '' as 'Sr.No', orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales Person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount ) As 'Amount after Discount',(select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  ) as 'Tax amount',total as 'Net amount',order_type.name as 'Order Type',contact.name AS 'Contact'   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id           LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CANCEL' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";
                String sql_footer = "Select  '' as 'Sr.No', orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales Person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) As  ' Amount w/o Tax ', ((total_discount*100)/((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code))) as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code)) - total_discount ) As 'Amount after Discount',(select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code  ) as 'Tax amount',total as 'Net amount',order_type.name as 'Order Type',contact.name AS 'Contact'   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id           LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CANCEL' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                ArrayList<Integer> numCols = new ArrayList<>();
                numCols.add(7);
                numCols.add(9);
                numCols.add(10);
                numCols.add(11);
                numCols.add(12);
                ArrayList<Integer> dateCols = new ArrayList<>();
                dateCols.add(2);

                Intent intent1 = new Intent(ReportActivity.this, CancelReport_view.class);    Globals.ReportCondition = "";
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

                final String
                        /*sql = "select  '' as 'Sr.No.',groupmaster.grouptype as 'Item Category' ," +
                        "SUM(((order_detail.sale_price) * order_detail.quantity)) as 'Sales price'," +
                        "SUM(order_detail.quantity) AS 'Quantity'," +
                        "SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount'," +
                        " SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after " +
                        " Discount', SUM( line_total) As 'Net amount'  From order_detail" +
                        " left join  item on item.item_code = order_detail.item_code and item.Group_Code=order_detail.group_code and item.Packing_Code=order_detail.packing_code and item.brand_Code=order_detail.brand_code" +
                        " left join item_group on item_group.item_group_code = item.item_group_code " +
                        "left join orders on orders.order_code = order_detail.order_code left join GroupMaster groupmaster ON groupmaster.Group_Code= order_detail.group_code  Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')  Group by groupmaster.Group_Code";
*/
                    sql="select  '' as 'Sr.No.','' as 'Item Category' ,\n" +
                        "SUM(((order_detail.sale_price) * order_detail.quantity)) as 'Sales price',\n" +
                        "SUM(order_detail.quantity) AS 'Quantity',\n" +
                        "SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',\n" +
                        " SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after \n" +
                        " Discount', SUM( line_total) As 'Net amount'  From order_detail\n" +
                        " left join  item on item.item_code = order_detail.item_code\n" +
                        "left join orders on orders.order_code = order_detail.order_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                String sql_footer = "select  '' as srno,'' as 'Item Category' ," +
                        " Sum(order_detail.sale_price) as 'Sales price " +
                        " (without Tax)'," +
                        " SUM(order_detail.quantity) AS 'Quantity'," +
                        " " +
                        " SUM(((order_detail.sale_price) * order_detail.quantity)) As 'Gross Amount w/o Tax',"  +
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


                numCols.add(2);
                numCols.add(3);
                numCols.add(4);
                numCols.add(5);
                numCols.add(6);
                numCols.add(7);

                ArrayList<Integer> dateCols = new ArrayList<>();
                //dateCols.add(5);
                Intent intent1 = new Intent(ReportActivity.this, SalesByItemGroupReport_View.class);    Globals.ReportCondition = "";
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
                    // New Query

                    //final String sql =  " select  '' as 'Sr.No.', orders.order_code As 'Invoice Number',date(orders.order_date) as Date,time(orders.order_date) as Time ,groupmaster.grouptype as 'Item Category' ,item.item_code as 'Item Code', item.item_name as 'Item Name', ot.name AS 'Order Type',(order_detail.sale_price) as 'Sales price', (order_detail.quantity) AS 'Quantity',(order_detail.sale_price * order_detail.quantity) As 'Gross Amount', SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount', ( line_total) As 'Net amount'  From order_detail left join  item on item.item_code = order_detail.item_code  and item.Group_Code=order_detail.group_code and item.Packing_Code=order_detail.packing_code and item.brand_Code=order_detail.brand_code left join item_group on item_group.item_group_code = item.item_group_code left join  order_type ot on  ot.order_type_id =   orders.order_type_id left join orders on orders.order_code = order_detail.order_code left join GroupMaster groupmaster ON groupmaster.Group_Code= order_detail.group_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,item.brand_Code,item.Packing_Code,item.Group_Code,orders.order_type_id,orders.order_code ORDER by orders.modified_date desc";
                 final String   sql="select  '' as 'Sr.No.', orders.order_code As 'Invoice Number',date(orders.order_date) as Date,time(orders.order_date) as Time ,'' as 'Item Category' ,item.item_code as 'Item Code', item.item_name as 'Item Name', ot.name AS 'Order Type',(order_detail.sale_price) as 'Sales price', (order_detail.quantity) AS 'Quantity',((order_detail.sale_price)  * order_detail.quantity) As 'Gross Amount', SUM(((sale_price - ((cast(line_total as float)/quantity) - tax)) *quantity)) as 'Disount amount', ( line_total) As 'Net amount'  From order_detail left join  item ON item.item_code = order_detail.item_code  left join item_group on item_group.item_group_code = item.item_group_code left join  order_type ot ON  ot.order_type_id =   orders.order_type_id left join orders on orders.order_code = order_detail.order_code  Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code,orders.order_type_id,orders.order_code ORDER by orders.modified_date desc";
                    // Old Query
                    // final String sql = "select  '' as 'Sr.No.',groupmaster.grouptype as 'Item Category' ,item.item_code as 'Item Code', item.item_name as 'Item Name', item.is_modifier ='0' AS 'TYPE',Sum((order_detail.sale_price * order_detail.quantity )) as 'Sales price (without Tax)', SUM(order_detail.quantity) AS 'Quantity',SUM((order_detail.sale_price * order_detail.quantity)) As 'Gross Amount w/o Tax',SUM(((sale_price - ((cast(line_total as float)/quantity) - tax))* quantity)) as 'Disount amount',SUM(((sale_price - (sale_price - ((cast(line_total as float)/quantity) - tax)) )* quantity ))as 'Amount after  Discount', SUM((order_detail.quantity * order_detail.tax)) as 'Tax amount', SUM( line_total) As 'Net amount'  From order_detail left join  item on item.item_code = order_detail.item_code  and item.Group_Code=order_detail.group_code and item.Packing_Code=order_detail.packing_code and item.brand_Code=order_detail.brand_code left join item_group on item_group.item_group_code = item.item_group_code left join orders on orders.order_code = order_detail.order_code left join GroupMaster groupmaster ON groupmaster.Group_Code= order_detail.group_code Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') Group by item.item_code ";

                    String sql_footer = "";

                    ArrayList<Integer> numCols = new ArrayList<>();



                    numCols.add(8);
                    numCols.add(9);
                    numCols.add(10);
                    numCols.add(11);


                    ArrayList<Integer> dateCols = new ArrayList<>();


                    Intent intent1 = new Intent(ReportActivity.this, SalesByItemReport_View.class);
                    Globals.ReportCondition = "";
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


                    // New Query
                    final String sql =  "Select '' as 'Sr.No.', orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales Person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',orders.total_item As 'Total Item', orders.total_quantity AS 'Total Quantity',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code)) As  ' Amount  ',(Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code and  order_detail.device_code = orders.device_code  )  as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code )) - total_discount  ) As 'Amount after Discount', total as 'Net amount',order_type.name AS 'Order Type',contact.name AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CLOSE' And date(order_date)  BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') ORDER by orders.modified_date desc";

                    // Old query

                    // final String sql = "Select '' as 'Sr.No.', orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales Person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code \t)) As  ' Amount w/o Tax ',  (Select (round((((sale_price - ((cast(line_total as float)/quantity) - tax))*100)/sale_price),2) ) from order_detail where order_detail.order_code =  orders.order_code and  order_detail.device_code = orders.device_code \t )  as ' Discount %', total_discount as 'Disount amount ', (((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t)) - total_discount  ) As 'Amount after Discount',(select (sum(tax*quantity)+orders.total_tax) From order_detail Where order_detail.order_code = orders.order_code and  order_detail.device_code = orders.device_code \t ) as 'Tax amount',total as 'Net amount',order_type.name AS 'Order Type',contact.name AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                    String sql_footer = "Select '' as 'Sr.No.', orders.order_code As 'Invoice Number' ,date(orders.order_date) as Date,time(orders.order_date) as Time  ,(select name from user where user_id = orders.modified_by) as 'Sales Person',(select device_name from  Lite_POS_Device where  Device_Id =   orders.device_code) as 'Device Name',orders.total_item As 'Total Item', orders.total_quantity AS 'Total Quantity',remarks as Remarks,((select sum(order_detail.sale_price * order_detail.quantity) From order_detail Where order_detail.order_code = orders.order_code  and  order_detail.device_code = orders.device_code)) As  ' Amount  ', total as 'Net amount',order_type.name AS 'Order Type',contact.name AS contact   from orders     LEFT JOIN location ON location.location_id=orders.location_id            LEFT JOIN order_type ON order_type.order_type_id=orders.order_type_id LEFT JOIN contact ON contact.contact_code=orders.contact_code   Where orders.order_status ='CLOSE' And date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "')";

                    ArrayList<Integer> numCols = new ArrayList<>();
                    numCols.add(6);
                    numCols.add(7);
                    numCols.add(9);
                    numCols.add(10);
                    numCols.add(11);
                    numCols.add(12);
                    numCols.add(13);
                    ArrayList<Integer> dateCols = new ArrayList<>();
                    dateCols.add(2);
                    Intent intent1 = new Intent(ReportActivity.this, OrderHeaderReport_View.class);    Globals.ReportCondition = "";
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
                // New query
                final String sql =  "SELECT `u`.`name`   AS 'User Id', (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name',  count(sc.order_id) AS 'Total Invoice',  sum(sc.total) AS 'Net Amount', sum(((select sum(order_detail.sale_price *order_detail.quantity)  from order_detail where order_detail.order_code = sc.order_code))) as 'Amount Tax', ( strftime('%m', sc.order_date))   AS 'Month',  strftime('%Y', sc.order_date)  AS 'Year',  lpd.Location_name AS 'Location Name',  `ot`.`name` AS 'Name' , sum(sc.total_discount)   AS 'Discount Amount'  FROM `orders` `sc`left JOIN  USER u  on u.user_code = sc.modified_by LEFT JOIN Lite_POS_Device lpd ON lpd.Location_Code=sc.location_id inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  strftime('%Y', sc.order_date), strftime('%m', sc.order_date) , `ot`.`name` ,sc.order_type_id ORDER BY `sc`.`order_date` DESC";
                // Old query
                //  final String sql =  "SELECT `u`.`name`   AS 'User Id', (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name',  count(sc.order_id) AS 'Total Invoice', sum(sc.total_discount) AS 'Discount Amount',  sum(sc.total) AS 'Net Amount', sum(((select sum(order_detail.sale_price *order_detail.quantity)  from order_detail where order_detail.order_code = sc.order_code))) as 'Amount Without Tax',  sum((((select sum(order_detail.sale_price *order_detail.quantity) from order_detail where order_detail.order_code = sc.order_code))- total_discount)) as 'Amount After Discount', sum((select (sum(tax*quantity)+sc.total_tax) from order_detail where order_detail.order_code = sc.order_code)) as 'Tax Amount',( strftime('%m', sc.order_date))   AS 'Month',  strftime('%Y', sc.order_date)  AS 'Year',  lpd.Location_name AS 'Location Name',  `ot`.`name` AS 'Name' FROM `orders` `sc`INNER JOIN  USER u  on u.user_code = sc.modified_by LEFT JOIN Lite_POS_Device lpd ON lpd.Location_Code=sc.location_id inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  strftime('%Y', sc.order_date), strftime('%m', sc.order_date) , `ot`.`name`ORDER BY `sc`.`order_date` DESC";
                // final String sql =  "SELECT `u`.`name`   AS 'User Id', (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name',  count(sc.order_id) AS 'Total Invoice', sum(sc.total_discount) AS 'Discount Amount',  sum(sc.total) AS 'Net Amount', sum(((select sum(order_detail.sale_price *order_detail.quantity)  from order_detail where order_detail.order_code = sc.order_code))) as 'Amount Without Tax',  sum((((select sum(order_detail.sale_price *order_detail.quantity) from order_detail where order_detail.order_code = sc.order_code))- total_discount)) as 'Amount After Discount', sum((select (sum(tax*quantity)+sc.total_tax) from order_detail where order_detail.order_code = sc.order_code)) as 'Tax Amount',( strftime('%m', sc.order_date))   AS 'Month',  strftime('%Y', sc.order_date)  AS 'Year',  `location`.`location_name` AS 'Location Name',  `ot`.`name` AS 'Name' FROM `orders` `sc`INNER JOIN  USER u  on u.user_code = sc.modified_by LEFT JOIN location ON location.location_id=sc.location_id inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  strftime('%Y', sc.order_date), strftime('%m', sc.order_date) , `ot`.`name`ORDER BY `sc`.`order_date` DESC";
                // final String sql = "SELECT `u`.`name`   AS `user_id`, (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name',  count(sc.order_id) AS total_invoice, sum(sc.total_discount) AS discount_amount,  sum(sc.total) AS net_amount, sum(((select sum(order_detail.sale_price *order_detail.quantity)  from order_detail where order_detail.order_code = sc.order_code))) as amount_without_tax,  sum((((select sum(order_detail.sale_price *order_detail.quantity) from order_detail where order_detail.order_code = sc.order_code))- total_discount)) as amount_after_discount, sum((select (sum(tax*quantity)+sc.total_tax) from order_detail where order_detail.order_code = sc.order_code)) as tax_amount,( strftime('%m', sc.order_date))   AS month,  strftime('%Y', sc.order_date)  AS year,  `location`.`location_name`,  `ot`.`name`FROM `orders` `sc`INNER JOIN  USER u  on u.user_code = sc.modified_by LEFT JOIN location ON location.location_id=sc.location_id inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  strftime('%Y', sc.order_date), strftime('%m', sc.order_date) , `ot`.`name`ORDER BY `sc`.`order_date` DESC";


                String sql_footer = "SELECT `u`.`name`   AS 'User Id', (select device_name from  Lite_POS_Device where  Device_Id =   sc.device_code) as 'Device Name',  count(sc.order_id) AS 'Total Invoice',  sum(sc.total) AS 'Net Amount', sum(((select sum(order_detail.sale_price *order_detail.quantity)  from order_detail where order_detail.order_code = sc.order_code))) as 'Amount Tax', ( strftime('%m', sc.order_date))   AS 'Month',  strftime('%Y', sc.order_date)  AS 'Year',  lpd.Location_name AS 'Location Name',  `ot`.`name` AS 'Name' FROM `orders` `sc`left JOIN  USER u  on u.user_code = sc.modified_by LEFT JOIN Lite_POS_Device lpd ON lpd.Location_Code=sc.location_id inner join  order_type ot on  ot.order_type_id =   sc.order_type_id WHERE `sc`.`is_active` = 1 AND sc.Z_code <> '0' AND `sc`.`order_status` = 'CLOSE' AND      date(order_date) BETWEEN date('" + edt_from.getText().toString() + "') AND date('" + edt_to.getText().toString() + "') GROUP BY  strftime('%Y', sc.order_date), strftime('%m', sc.order_date) , `ot`.`name` ,sc.order_type_id ORDER BY `sc`.`order_date` DESC";

                ArrayList<Integer> numCols = new ArrayList<>();


                numCols.add(2);
                numCols.add(3);
                numCols.add(4);
                numCols.add(5);
                numCols.add(6);
                //numCols.add(10);
//                numCols.add(6);

//                numCols.add(11);

                ArrayList<Integer> dateCols = new ArrayList<>();

                Intent intent1 = new Intent(ReportActivity.this, MonthlySalesReport_View.class);    Globals.ReportCondition = "";    Globals.ReportCondition = "";
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

    private Cursor getCustomerStatment(String serverData) {
        MatrixCursor mc = null;

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                try {
                    mc = new MatrixCursor(new String[]{"SNo", "Date", "Narration", "Dr Amount", "Cr Amount", "Current Balance"});
                    JSONArray jsonArray = jsonObject_bg.getJSONArray("result");

                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        mc.addRow(new Object[]{k+1, jsonObject.get("date"), jsonObject.get("narration"), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("dr_amount")), decimal_check), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("cr_amount")), decimal_check), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("current_balance")), decimal_check)});
                        srno_count=k+1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {

        }
        return mc;

    }

    /*private String get_CustomerStatment_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "customer_statements");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("contact_code", cusCode));
        nameValuePairs.add(new BasicNameValuePair("from_date", edt_from.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("to_date", edt_to.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", edt_to.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", edt_to.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", edt_to.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", edt_to.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", edt_to.getText().toString().trim()));

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

    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hidekeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hidekeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
    public void get_CustomerStatment_server(final String syscode1,final String syscode2,final String syscode3,final String syscode4) {

        pDialog = new ProgressDialog(ReportActivity.this);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        String server_url= Globals.App_IP_URL + "customer_statements";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final Cursor cursor = getCustomerStatment(response);
                            Globals.online_report_cursor = cursor;
                            pDialog.dismiss();
                            String sql_footer = "";
                            ArrayList<Integer> numCols = new ArrayList<>();
                            numCols.add(3);
                            numCols.add(4);
                            numCols.add(5);
                            ArrayList<Integer> dateCols = new ArrayList<>();
                            dateCols.add(1);
                            Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);    Globals.ReportCondition = "";
                            intent1.putExtra("qry", String.valueOf(srno_count));
                            intent1.putExtra("type", "online");
                            intent1.putExtra("name", btn_cus_sate.getText().toString());
                            intent1.putExtra("qry_footer", sql_footer);
                            intent1.putIntegerArrayListExtra("numCols", numCols);
                            intent1.putIntegerArrayListExtra("dateCols", dateCols);
                            intent1.putExtra("from", edt_from.getText().toString());
                            intent1.putExtra("to", edt_to.getText().toString());
                            intent1.putExtra("operation", operation);
                            startActivity(intent1);
                        } catch (Exception e) {

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("contact_code", cusCode);
                params.put("from_date", edt_from.getText().toString().trim());
                params.put("to_date", edt_to.getText().toString().trim());
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void get_CustomerSummery_server(final String syscode1,final String syscode2,final String syscode3,final String syscode4) {

        pDialog = new ProgressDialog(ReportActivity.this);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        String server_url= Globals.App_IP_URL + "customer_summary";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            final JSONObject jsonObject_manufacture = new JSONObject(response);
                            final Cursor cursor = getCustomerSummery(response);
                            // JSONArray jsonresult=jsonObject_manufacture.getJSONArray("result");

                            Globals.online_report_cursor = cursor;
                            pDialog.dismiss();
                            String sql_footer = "";
                            ArrayList<Integer> numCols = new ArrayList<>();

                            ArrayList<Integer> dateCols = new ArrayList<>();
                            Intent intent1 = new Intent(ReportActivity.this, ReportViewerActivity.class);
                            Globals.ReportCondition = "";
                            intent1.putExtra("qry",String.valueOf(srno_count));
                            intent1.putExtra("type", "online");
                            intent1.putExtra("name", btn_cus_smry.getText().toString());
                            intent1.putExtra("qry_footer", sql_footer);
                            intent1.putIntegerArrayListExtra("numCols", numCols);
                            intent1.putIntegerArrayListExtra("dateCols", dateCols);
                            intent1.putExtra("from", edt_from.getText().toString());
                            intent1.putExtra("to", edt_to.getText().toString());
                            intent1.putExtra("operation", operation);
                            startActivity(intent1);
                        } catch (Exception e) {

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.objLPD.getDevice_Code());
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
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

    private void fill_spinner_User() {

//      user_ArrayList = User.getAllUser(getApplicationContext(),"WHERE is_active ='1' Order By name asc", database, db);
//        UserAdapter userAdapter = new UserAdapter(ReportActivity.this, user_ArrayList);
//        spn_user.setAdapter(userAdapter);
        ArrayList<String> userList= getuser_list();;
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, userList);
        spn_user.setAdapter(dataAdapter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
    private void fill_VehicleType() {
        try {
            if(itemnamelist!=null) {
                ArrayAdapter dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, itemnamelist);
                spn_vehtype.setAdapter(dataAdapter);

                // appCompatSpinner.setPrompt("Select Insurance Company");
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            }

            // vehtype_ArrayListnew=new ArrayList<Item>();

            //vehtype_ArrayList.add(resultp);
            // vehtype_ArrayList.get(0).set_item_code("0");
            /* vehtype_ArrayList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' Order By item_name asc", database);
             *//*    Item resultp= vehtype_ArrayList.get(0);
    resultp.set_item_name("Select");

    vehtype_ArrayList.add(resultp);*//*
 for (int i=0;i<vehtype_ArrayList.size()+1;i++){
     Item resultp= vehtype_ArrayList.get(i);
     resultp.set_item_name("Select");
     vehtype_ArrayList.add(vehtype_ArrayList.size()+1,resultp);
 }
    Spin_VehicleAdapter userAdapter = new Spin_VehicleAdapter(ReportActivity.this, vehtype_ArrayList);

    spn_vehtype.setAdapter(userAdapter);*/
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public ArrayList<String> getAllItems(){

        itemnamelist = new ArrayList<String>();
        itemcodelist=new ArrayList<String>();

        String selectQuery = "SELECT item_name,item_code FROM  item where is_active ='1' Order By item_name asc" ;
        itemnamelist.add(0,"Select");
        itemcodelist.add(0,"0");
        database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String itemname = cursor.getString(cursor.getColumnIndex("item_name"));
                String itemcode = cursor.getString(cursor.getColumnIndex("item_code"));


                itemnamelist.add(itemname);
                itemcodelist.add(itemcode);
            } while (cursor.moveToNext());

        }

        // closing connection
        cursor.close();
        // db.close();
        fill_VehicleType();
        // returning lables
        return itemnamelist;
    }

    public ArrayList<String> getuser_list()
    {
        userlist = new ArrayList<String>();
        String selectQuery = "SELECT name from user WHERE is_active ='1' Order By name asc ";
        userlist.add(0,"Select");
        database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex("name"));
                userlist.add(username);
            } while (cursor.moveToNext());

        }
        // closing connection
        cursor.close();
        return userlist;
    }

    private Cursor getCustomerSummery(String serverData) {
        MatrixCursor mc = null;

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                try {
                    mc = new MatrixCursor(new String[]{"SNo", "Contact Code", "Name", "Device Code", "Amount"});
                    JSONArray jsonArray = jsonObject_bg.getJSONArray("result");

                    for (int k = 0; k < jsonArray.length(); k++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        mc.addRow(new Object[]{k+1, jsonObject.get("contact_code"), jsonObject.get("name"), jsonObject.get("device_code"), Globals.myNumberFormat2Price(Double.parseDouble(jsonObject.getString("sum(cr_amount - dr_amount)")), decimal_check)});
                        srno_count=k+1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
        }
        return mc;
    }

/*    private String get_CustomerSummery_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
               Globals.App_IP_URL + "customer_summary");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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
    }*/

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
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            if (Globals.rtick.equals("Ticket")) {
                                Globals.rtick = "";
                                Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } else {
                                Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            if (Globals.rtick.equals("Ticket")) {
                                Globals.rtick = "";
                                Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();

                            } else {
                            }
                            Intent intent = new Intent(ReportActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();

                        } finally {
                        }
                    } else {
                        try {
                            if (Globals.rtick.equals("Ticket")) {
                                Globals.rtick = "";
                                Intent intent = new Intent(ReportActivity.this, TicketActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();

                            } else {
                            }
                            Intent intent = new Intent(ReportActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();

                        } finally {
                        }
                    }

                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")){

                    Intent intent = new Intent(ReportActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }

                else if(Globals.objLPR.getIndustry_Type().equals("3")){

                    Intent intent = new Intent(ReportActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }

                else if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(ReportActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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

