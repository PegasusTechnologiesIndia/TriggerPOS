package org.phomellolitepos.Reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.phomellolitepos.Adapter.OrderDetailReport_Adapter;
import org.phomellolitepos.Adapter.OrderPaymentReportAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.R;
import org.phomellolitepos.ReportActivity;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.OrderDetail_Report;
import org.phomellolitepos.database.OrderPayment_Report;

import java.util.ArrayList;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class OrderPaymentReport_View extends AppCompatActivity {
    Database db;
    SQLiteDatabase database;
    TextView txt_compname,txt_add,txt_date,txtsalesperson,txt_ordertype,totalamnt,totalRecord;
    ArrayList<OrderPayment_Report> orderPayment_reportArrayList ;
    OrderPaymentReportAdapter orderPaymentReportAdapter;
    String decimal_check,qty_decimal_check;
    double count_amnt_summry=0;
    String sql, sqlFooter, json, str_type;
    ArrayList<Integer> numCols;
    ArrayList<Integer> dateCols;
    String name, from, to, operation;
    TableRow salespersonrow, ordertyperow;
    Intent intent;
    int id;
    String flag = "0";
    Dialog listDialog;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_report__view);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Payment");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        listDialog = new Dialog(this);
        txt_compname=(TextView)findViewById(R.id.txt_companyname);
        txt_add=(TextView)findViewById(R.id.txt_adress);
        txt_date=(TextView)findViewById(R.id.txt_date);
        txtsalesperson=(TextView)findViewById(R.id.txt_salesperson);
        txt_ordertype=(TextView)findViewById(R.id.txt_ordertype);
        totalamnt=(TextView)findViewById(R.id.totalamnt);
        salespersonrow=(TableRow)findViewById(R.id.ll_status);
        ordertyperow=(TableRow)findViewById(R.id.ll_statusout);
        totalRecord=findViewById(R.id.txt_totalRecord);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = Globals.objsettings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "2";
        }
        orderPayment_reportArrayList =new ArrayList<OrderPayment_Report>();
        intent=getIntent();
        str_type = intent.getStringExtra("type");
        sqlFooter = intent.getStringExtra("qry_footer");
        from=intent.getStringExtra("from");
        to=intent.getStringExtra("to");
        name = intent.getStringExtra("name");
        numCols = intent.getIntegerArrayListExtra("numCols");
        dateCols = intent.getIntegerArrayListExtra("dateCols");
        sql = intent.getStringExtra("qry");

        operation = intent.getStringExtra("operation");
        operation = "Edit";
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(OrderPaymentReport_View.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                          sleep(1000);
                            Globals.online_report_cursor = null;
                            Globals.pageNO = 0;
                            Intent intent = new Intent(OrderPaymentReport_View.this, ReportActivity.class);
                            intent.putExtra("from", from);
                            intent.putExtra("to", to);
                            intent.putExtra("operation", operation);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                };
                timerThread.start();

            }
        });

        salespersonrow.setVisibility(View.GONE);
        ordertyperow.setVisibility(View.GONE);
        getqry1(sql);

        txt_date.setText(from + " To \n" + to);
        txt_compname.setText(Globals.objLPR.getCompany_Name());
        if(Globals.objLPR.getAddress().equals("")){
            txt_add.setVisibility(View.GONE);
        }
        else {
            txt_add.setVisibility(View.VISIBLE);
            txt_add.setText(Globals.objLPR.getAddress());
        }
        getReportList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(OrderPaymentReport_View.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Cursor cursor = database.rawQuery(sql, null);
                            Cursor cursorFooter = null;
                            if (flag.equals("0")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No Report Found", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                final String dtt = name + DateUtill.Reportnamedate();
                                final String namestr=name;
                                Globals.ExportItem(cursor, dtt,namestr, numCols, dateCols);
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Globals.online_report_cursor = null;
                                        Toast.makeText(getApplicationContext(), R.string.excel_export_succ, Toast.LENGTH_LONG).show();
                                        if (Globals.objsettings.get_Is_email().equals("true")) {

                                            listDialog.setTitle("Send Email?");
                                            LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
                                            listDialog.setContentView(v1);
                                            listDialog.setCancelable(true);
                                            final EditText edt_remark = (EditText) listDialog.findViewById(R.id.edt_remark);
                                            edt_remark.setHint(R.string.Enter_Email);
                                            edt_remark.setHintTextColor(Color.parseColor("#cccccc"));
                                            edt_remark.setVisibility(View.GONE);
                                            Button btnPDF = (Button) listDialog.findViewById(R.id.btn_save);
                                            Button btnEXL = (Button) listDialog.findViewById(R.id.btn_clear);
                                            btnEXL.setVisibility(View.VISIBLE);
                                            btnPDF.setText("Email(Yes)");
                                            btnPDF.setTextColor(Color.parseColor("#ffffff"));
                                            btnEXL.setText("Email(No)");
                                            btnEXL.setTextColor(Color.parseColor("#ffffff"));
                                            listDialog.show();

                                            btnPDF.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (isNetworkStatusAvialable(getApplicationContext())) {
                                                        if (Globals.objsettings.get_Is_email().equals("true")) {
                                                            if (Globals.objsettings.get_Manager_Email().equals("")) {
                                                            } else {
                                                                String strEmail = Globals.objsettings.get_Manager_Email();
                                                                send_email_manager(strEmail, dtt, name);
                                                            }
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Email Configuration not set from App Settings", Toast.LENGTH_SHORT).show();

                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                    }
                                                    listDialog.dismiss();
                                                }
                                            });

                                            btnEXL.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    listDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.excel_export_error, Toast.LENGTH_LONG).show();
                                }
                            });
                        } finally {
                            progressDialog.dismiss();
                        }
                    }
                };
                timerThread.start();
            }
        });
    }
    public void getqry1(String qry){

        Cursor cursor = database.rawQuery(qry, null);
        Cursor colCursor = cursor;
        String salesperson="";
        String ordertype="";
        if (colCursor != null)
            colCursor.moveToFirst();

        if (colCursor.moveToFirst())
        {
            totalRecord.setText(cursor.getCount()+"");
            do {

                String invoicenumber= cursor.getString(1);
                String date=cursor.getString(2);
                String time=cursor.getString(3);
                salesperson=cursor.getString(4);
                String devicename= cursor.getString(5);
                //   String taxAmnt=Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(5)),qty_decimal_check);
                String remarks=cursor.getString(6);
                String paymentmethod=cursor.getString(7);
                String salesamountafterdisc=Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(8)),qty_decimal_check);
                String netamount=Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(9)),qty_decimal_check);
                String paymaount=Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(10)),qty_decimal_check);
               ordertype=cursor.getString(11);


                //     String location=cursor.getString(9);



                // String disAmnt=Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(8)),qty_decimal_check);
                //   String ordertype= cursor.getString(10);

                count_amnt_summry += Double.parseDouble(netamount);
                totalamnt.setText("  "+Globals.myNumberFormat2Price(count_amnt_summry,decimal_check)+"");

                orderPayment_reportArrayList.add(new OrderPayment_Report(invoicenumber,date,time,salesperson,devicename,remarks,paymentmethod,salesamountafterdisc,netamount,paymaount,ordertype));

            } while (cursor.moveToNext());
            txt_ordertype.setText(ordertype);
        }

    }
    private void getReportList() {

        RecyclerView order_list = (RecyclerView) findViewById(R.id.report_recyclerView);
        if (orderPayment_reportArrayList.size() > 0) {

            // order_arraylist = Orders.getAllOrders(getApplicationContext(), "WHERE is_active = '1' AND order_status ='OPEN' And z_code='0'", database);



            orderPaymentReportAdapter = new OrderPaymentReportAdapter(OrderPaymentReport_View.this, orderPayment_reportArrayList);
            //table_title.setVisibility(View.INVISIBLE);
            order_list.setVisibility(View.VISIBLE);
            order_list.setAdapter(orderPaymentReportAdapter);

            int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            order_list.setLayoutManager(manager);

flag="1";
            orderPaymentReportAdapter.notifyDataSetChanged();

        } else {

            order_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        progressDialog = new ProgressDialog(OrderPaymentReport_View.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.online_report_cursor = null;
                    Globals.pageNO = 0;
                    Intent intent = new Intent(OrderPaymentReport_View.this, ReportActivity.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", to);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                } finally {
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
    private void send_email_manager(String strEmail, String strFileName, String strReportName) {
        String dtt = Globals.Reportnamedate();
        try {
            String[] recipients = strEmail.split(",");
            final SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender(Globals.objsettings.get_Email(), Globals.objsettings.get_Password(), Globals.objsettings.get_Host(), Globals.objsettings.get_Port());
            email.m.set_from(Globals.objsettings.get_Email());
            email.m.setBody(strReportName);
            email.m.set_to(recipients);
            email.m.set_subject(Globals.objLPD.getDevice_Name() + ":" + strReportName + "");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "Exportxls" + "/" + strFileName + ".xls");


            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        OrderPaymentReport_View activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(MonthlySalesReport_View.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(MonthlySalesReport_View.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }
    }
}