package org.phomellolitepos.Reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.DailySalesReportAdapter;
import org.phomellolitepos.Adapter.ItemGroupReportAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.R;
import org.phomellolitepos.ReportActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.DailySalesReport;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.ItemGroupReport;

import java.util.ArrayList;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ItemGroupReport_view extends AppCompatActivity
{
    Database db;
    SQLiteDatabase database;
    TextView txt_compname,txt_add,txt_date,txtsalesperson,txt_ordertype,totalamnt,totalRecord;
    String name, from, to, operation;
    Intent intent;
    ArrayList<ItemGroupReport> itemGroupReports;
    ItemGroupReportAdapter itemGroupReportAdapter;
    String flag = "0";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_group_report_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item Group Report");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);


        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        itemGroupReports =new ArrayList<ItemGroupReport>();
        intent=getIntent();
        String str_type = intent.getStringExtra("type");
        String sqlFooter = intent.getStringExtra("qry_footer");
        from=intent.getStringExtra("from");
        to=intent.getStringExtra("to");
        name = intent.getStringExtra("name");
       // numCols = intent.getIntegerArrayListExtra("numCols");
       // dateCols = intent.getIntegerArrayListExtra("dateCols");
        String sql = intent.getStringExtra("qry");
        operation = intent.getStringExtra("operation");
        operation = "Edit";


        txt_compname=(TextView)findViewById(R.id.txt_companyname);
        txt_add=(TextView)findViewById(R.id.txt_adress);
        txt_date=(TextView)findViewById(R.id.txt_date);
        txtsalesperson=(TextView)findViewById(R.id.txt_salesperson);
        txt_ordertype=(TextView)findViewById(R.id.txt_ordertype);
        totalamnt=(TextView)findViewById(R.id.totalamnt);

        TableRow tr_salesperson,tr_ordertype,tr_totalamt;
        tr_salesperson=findViewById(R.id.ll_status);
        tr_ordertype=findViewById(R.id.ll_statusout);
        tr_totalamt=findViewById(R.id.totllay);
        tr_salesperson.setVisibility(View.GONE);
        tr_ordertype.setVisibility(View.GONE);
        tr_totalamt.setVisibility(View.GONE);

        totalRecord=findViewById(R.id.txt_totalRecord);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ItemGroupReport_view.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                          sleep(1000);
                            Globals.online_report_cursor = null;
                            Globals.pageNO = 0;
                            Intent intent = new Intent(ItemGroupReport_view.this, ReportActivity.class);
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

        getqry1(sql);
        txt_date.setText(from + " To  \n" + to);
        txt_compname.setText(Globals.objLPR.getCompany_Name());
        getReportList();

    }

    public void getqry1(String qry)
    {
        Cursor cursor = database.rawQuery(qry, null);
        Cursor colCursor = cursor;

        if (colCursor != null)
            colCursor.moveToFirst();

        if (colCursor.moveToFirst())
        {
            totalRecord.setText(cursor.getCount()+"");
            do {
                String srno=cursor.getString(0);
                String groupcode= cursor.getString(1);
                String groupname= cursor.getString(2);
                itemGroupReports.add(new ItemGroupReport(srno,groupcode,groupname));
               } while (cursor.moveToNext());

        }

    }

    private void getReportList() {

        RecyclerView list = (RecyclerView) findViewById(R.id.report_recyclerView);
        if (itemGroupReports.size() > 0)
        {
            itemGroupReportAdapter = new ItemGroupReportAdapter(ItemGroupReport_view.this, itemGroupReports);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(itemGroupReportAdapter);

            int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            list.setLayoutManager(manager);


            flag="1";
            itemGroupReportAdapter.notifyDataSetChanged();

        } else {

            list.setVisibility(View.GONE);
        }
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
    @Override
    public void onBackPressed() {
        progressDialog = new ProgressDialog(ItemGroupReport_view.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.online_report_cursor = null;
                    Globals.pageNO = 0;
                    Intent intent = new Intent(ItemGroupReport_view.this, ReportActivity.class);
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
        ItemGroupReport_view activity;

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
                Log.e(DailySales_Report.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(DailySales_Report.SendEmailAsyncTask.class.getName(), "Email failed");
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