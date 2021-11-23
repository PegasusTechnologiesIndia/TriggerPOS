package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.PagingAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.Paging;
import org.phomellolitepos.database.Database;

import java.util.ArrayList;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ReportViewerActivity extends AppCompatActivity
{
    Button btn_clr_fltr;
    TextView txt_total;
    Spinner spn_paging;
    String decimal_check;
    Database db;
    SQLiteDatabase database;
    LinearLayout ll;

    ScrollView sv;
    HorizontalScrollView hsv;
    int r;
    TableRow tableRow;
    ProgressDialog progressDialog;
    Dialog listDialog;

    String sql, sqlFooter, json, str_type;
    ArrayList<Integer> numCols;
    ArrayList<Integer> dateCols;
    String name, from, to, operation;
    String flag = "0";
    int id;
    Dialog listDialog1;
    String[] fieldArray;
    String strField = "";
    String strOperation = "";
    String strValue = "";
    int record_count = 0;
    int limit = 200;
    int nop = 1;
    String strAddLimit = "",FirstCoulm="";
    int SnoCount = 1;
    ArrayList<Paging> pagingArrayList = new ArrayList<>();
    TableLayout tableLayout;
    int check = 0;
    Intent intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.report_viewer);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        listDialog = new Dialog(this);
        intent3 = getIntent();
        json = intent3.getStringExtra("json");
        str_type = intent3.getStringExtra("type");
        sqlFooter = intent3.getStringExtra("qry_footer");
        name = intent3.getStringExtra("name");
        numCols = intent3.getIntegerArrayListExtra("numCols");
        dateCols = intent3.getIntegerArrayListExtra("dateCols");
        from = intent3.getStringExtra("from");
        to = intent3.getStringExtra("to");
        operation = intent3.getStringExtra("operation");
        operation = "Edit";
     //   settings = Settings.getSettings(getApplicationContext(), database, "");


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ReportViewerActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
//                          sleep(1000);
                            Globals.online_report_cursor = null;
                            Globals.pageNO = 0;
                            Intent intent = new Intent(ReportViewerActivity.this, ReportActivity.class);
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

        spn_paging = (Spinner) findViewById(R.id.spn_paging);
        btn_clr_fltr = (Button) findViewById(R.id.btn_clr_fltr);
        txt_total = (TextView) findViewById(R.id.txt_total);
        spn_paging.setSelection(0, false);
        spn_paging.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++check > 1) {
                    Globals.pageNO = position;
                    Paging resultp = pagingArrayList.get(position);
//                    SnoCount = resultp.getStart_number();
//                    strAddLimit = "and order_id BETWEEN " + resultp.getStart_number() + " and " + resultp.getEnd_number() + "";
                    int offset = (position+1 -1) * 200;
                    SnoCount = offset+1;
                    strAddLimit = "LIMIT 200 offset "+offset;
                    ll.removeAllViews();
                    progressDialog = new ProgressDialog(ReportViewerActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Wait...");
                    progressDialog.show();
                    new
                        Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(2 * 1000);
                                        load_data();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Globals.pageNO == 0) {
            try {
                if((name.equals("Customer Summary Report"))|| (name.equals("Customer Statement Report"))){
                    sql = intent3.getStringExtra("qry");
                    txt_total.setText(getResources().getString(R.string.total2)+" "+sql+"");
                }
                else {
                    record_count = getTotalRecords(sql);
                    txt_total.setText(getResources().getString(R.string.total2)+" "+record_count+"");


                }
            }catch (Exception ex){
                record_count = 0;
                txt_total.setText(getResources().getString(R.string.total2)+" "+record_count+"");

            }


            nop = Math.round(Float.parseFloat(record_count+"") / Float.parseFloat(limit+""));
            Float compare = (Float.parseFloat(record_count+"") / Float.parseFloat(limit+""));

            if (compare>nop){
                nop = nop+1;
            }
            if (nop<=0){
                nop= 1;
            }
            fill_pages(nop);
            Paging resultp = pagingArrayList.get(Globals.pageNO);
            strAddLimit = "LIMIT 200 offset 0";
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(ReportViewerActivity.this);
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

        btn_clr_fltr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.online_report_cursor = null;
                Globals.ReportCondition="";
                Globals.pageNO = 0;
                ll.removeAllViews();
                progressDialog = new ProgressDialog(ReportViewerActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Wait...");
                progressDialog.show();
                new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(2 * 1000);
                                    load_data();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
            }
        });

        progressDialog = new ProgressDialog(ReportViewerActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait...");
        progressDialog.show();
        new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(2 * 1000);
                            load_data();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.

                start();
    }

    private void fill_pages(int nop) {
        Paging paging;

        for (int i = 1; i <= nop; i++) {

            if (i == nop) {
                paging = new Paging();
                paging.setPage_number(i);
                int strt = (limit * i) - 50 + 1;
                paging.setStart_number(strt);

                int end = (limit * i);
                int fin = record_count - end;
                if (fin == 0) {
                    paging.setEnd_number(end);
                    pagingArrayList.add(paging);
                } else {
                    int finlend = end + fin;
                    paging.setEnd_number(finlend);
                    pagingArrayList.add(paging);
                }

            } else {
                paging = new Paging();
                paging.setPage_number(i);
                int strt = (limit * i) - 50 + 1;
                paging.setStart_number(strt);
                int end = (limit * i);
                paging.setEnd_number(end);
                pagingArrayList.add(paging);
            }
        }

        if (pagingArrayList.size() > 0) {
            PagingAdapter pagingAdapter = new PagingAdapter(getApplicationContext(), pagingArrayList);
            spn_paging.setAdapter(pagingAdapter);
        }
    }

    private int getTotalRecords(String sql) {
        sql = intent3.getStringExtra("qry");
        int r_count = 0;
        Cursor cursor = database.rawQuery(sql, null);
        Cursor colCursor = cursor;

        if (colCursor != null)
            colCursor.moveToFirst();

        if (colCursor.moveToFirst()) {

            do {
                r_count = colCursor.getCount();

            } while (false);
        }
        return r_count;
    }

    private void load_data() {
        sql = intent3.getStringExtra("qry");
        runOnUiThread(new Runnable() {
            public void run() {
                if (str_type.equals("online")) {
                    try {

                        sv = new ScrollView(ReportViewerActivity.this);
                        hsv = new HorizontalScrollView(ReportViewerActivity.this);
                        ll = (LinearLayout) findViewById(R.id.ll);
                        //cardView=(CardView)findViewById(R.id.cardView);

                        final TextView textView1 = new TextView(ReportViewerActivity.this);
                        textView1.setText(name);
                        textView1.setGravity(Gravity.CENTER);
                        ll.setGravity(Gravity.CENTER);
                        textView1.setPadding(0, 10, 0, 10);
                        textView1.setTextColor(Color.parseColor("#333333"));
                        textView1.setTextSize(20);

                        ll.addView(textView1);

                        try {
                            decimal_check = Globals.objLPD.getDecimal_Place();

                        } catch (Exception ex) {
                            decimal_check = "2";
                        }


                        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.FILL_PARENT);
                        tableLayout = new TableLayout(ReportViewerActivity.this);
                        tableLayout.setBackgroundColor(Color.BLACK);


                        // 2) create tableRow params
                        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.FILL_PARENT, 1f);
                        tableRowParams.setMargins(1, 1, 1, 1);
                        tableRowParams.weight = 1;

                        int i = 0;
                        Cursor mc = Globals.online_report_cursor;
                        Cursor colCursor = mc;
                        if (colCursor != null) {
                            if (colCursor != null)
                                colCursor.moveToFirst();

                            if (colCursor.moveToFirst()) {
                                do {
                                    {
                                        flag = "1";
                                        // 3) create tableRow
                                        tableRow = new TableRow(ReportViewerActivity.this);
                                        tableRow.setId(i);
                                        tableRow.setBackgroundColor(Color.BLACK);
                                        tableRow.setMinimumHeight(80);


                                        for (int j = 0; j < colCursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(ReportViewerActivity.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
                                            textView.setBackgroundColor(Color.parseColor("#fb7c3e"));
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setTextColor(Color.parseColor("#ffffff"));
                                            textView.setWidth(100);
                                            textView.setTextSize(16f);
                                            textView.setPadding(5, 5, 5, 5);
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(colCursor.getColumnName(j).toString());

                                            } else {

                                                if (numCols.contains(j)) {
                                                    textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                } else if (dateCols.contains(j)) {
                                                    textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                } else {
                                                    textView.setText(colCursor.getString(j).toString());
                                                }
                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }

                                } while (false);
                            }

                            i = 1;

                            if (mc != null)
                                mc.moveToFirst();
                            int rawIndex = 1;

                            if (mc.moveToFirst()) {
                                do {
                                    {
                                        // 3) create tableRow
                                        tableRow = new TableRow(ReportViewerActivity.this);

                                        tableRow.setBackgroundColor(Color.BLACK);

                                        for (int j = 0; j < mc.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(ReportViewerActivity.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
//                                            textView.setBackgroundColor(Color.WHITE);
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setPadding(0, 5, 0, 5);

                                            if(i%2==0) {
//                                                textView.setBackgroundColor(Color.WHITE);
                                                textView.setBackgroundColor(Color.WHITE);
                                            }
                                            //alternate background
                                            else {
                                                textView.setBackgroundColor(Color.parseColor("#FFFF66"));
                                            }
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(mc.getColumnName(j).toString());

                                            } else {

                                                if (j == 0) {
                                                    try {
                                                        tableRow.setId(Integer.parseInt(mc.getString(j).toString()));

                                                    } catch (Exception ex) {
                                                        tableRow.setId(Integer.parseInt("10000"));
                                                    }


                                                }
                                                try {

                                                    if (numCols.contains(j)) {
                                                        textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                    } else if (dateCols.contains(j)) {
                                                        textView.setPadding(10, 8, 8, 10);
                                                        textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                    } else {
                                                        if (j == 0) {
                                                            textView.setText(i + "");
                                                        } else {
                                                            textView.setText(colCursor.getString(j).toString());
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    textView.setText("");
                                                }


                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);

                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }


                                } while (mc.moveToNext());
                            }
                        }

                        hsv.addView(tableLayout);
                        sv.addView(hsv);
                        ll.addView(sv);
                    } catch (Exception ex) {

                    }
                } else {
                    try {
                        sv = new ScrollView(ReportViewerActivity.this);
                        hsv = new HorizontalScrollView(ReportViewerActivity.this);
                        ll = (LinearLayout) findViewById(R.id.ll);

                      ll.setGravity(Gravity.CENTER);
                        TextView textView1 = new TextView(ReportViewerActivity.this);
                        textView1.setText(name);
                        textView1.setGravity(Gravity.CENTER);
                        textView1.setPadding(0, 10, 0, 10);
                        textView1.setTextColor(Color.parseColor("#333333"));
                        textView1.setTextSize(20);
                        ll.addView(textView1);
                        String decimal_check;
                        try {
                            decimal_check = Globals.objLPD.getDecimal_Place();

                        } catch (Exception ex) {
                            decimal_check = "2";
                        }

                        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.FILL_PARENT);
                        tableLayout = new TableLayout(ReportViewerActivity.this);
                        tableLayout.setBackgroundColor(Color.BLACK);

                        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT, 1f);
                        tableRowParams.setMargins(1, 1, 1, 1);
                        tableRowParams.weight = 1;

                        if (sql.equals("")) {

                        } else {

                            int i = 0;
                            if (sql.contains("where") || sql.contains("Where") || sql.contains("WHERE")) {
//                                sql = sql + Globals.ReportCondition + "";
                                sql = sql + Globals.ReportCondition +" "+strAddLimit;
                            } else {
                                sql = sql + Globals.ReportCondition.replace("AND", " Where ") +" "+strAddLimit;
                            }
                            Cursor cursor = database.rawQuery(sql, null);
                            Cursor colCursor = cursor;

                            if (colCursor != null)
                                colCursor.moveToFirst();

                            if (colCursor.moveToFirst()) {

                                do {
                                    {
                                        flag = "1";
                                        // 3) create tableRow
                                        tableRow = new TableRow(ReportViewerActivity.this);
                                        tableRow.setId(i);
                                        tableRow.setBackgroundColor(Color.BLACK);
                                        tableRow.setMinimumHeight(80);


                                        for (int j = 0; j < colCursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(ReportViewerActivity.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
                                            textView.setBackgroundColor(Color.parseColor("#fb7c3e"));
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setTextColor(Color.parseColor("#ffffff"));
                                            textView.setTextSize(16f);
                                            textView.setPadding(5, 5, 5, 5);
                                            textView.setWidth(100);
                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(colCursor.getColumnName(j).toString());

                                            } else {

                                                if (numCols.contains(j)) {
                                                    textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                } else if (dateCols.contains(j)) {
                                                    textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                } else {
                                                    textView.setText(colCursor.getString(j).toString());
                                                }
                                            }

                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }

                                } while (false);
                            }

                            i = SnoCount;

                            if (cursor != null)
                                cursor.moveToFirst();
                            int rawIndex = 1;

                            if (cursor.moveToFirst()) {
                                do {
                                    {
                                        // 3) create tableRow
                                        tableRow = new TableRow(ReportViewerActivity.this);

                                        tableRow.setBackgroundColor(Color.BLACK);
                                        for (int j = 0; j < cursor.getColumnCount(); j++) {
                                            // 4) create textView
                                            TextView textView = new TextView(ReportViewerActivity.this);
                                            //  textView.setText(String.valueOf(j));dynamic table layout
//                                            textView.setBackgroundColor(Color.WHITE);
                                            textView.setGravity(Gravity.CENTER);
                                            textView.setPadding(0, 5, 0, 5);
                                            if(i%2==0) {
//                                                textView.setBackgroundColor(Color.WHITE);
                                                textView.setBackgroundColor(Color.parseColor("#FFFFE0"));
                                            }
                                            //alternate background
                                            else {
                                                textView.setBackgroundColor(Color.parseColor("#FFFF66"));
                                            }

                                            String s1 = Integer.toString(i);
                                            String s2 = Integer.toString(j);
                                            String s3 = s1 + s2;
                                            id = Integer.parseInt(s3);
                                            Log.d("TAG", "-___>" + id);
                                            if (i == 0) {
                                                Log.d("TAAG", "set Column Headers");
                                                textView.setText(cursor.getColumnName(j).toString());

                                            } else {

                                                if (j == 0) {
                                                    try {
                                                        tableRow.setId(Integer.parseInt(cursor.getString(j).toString()));

                                                    } catch (Exception ex) {
                                                        tableRow.setId(Integer.parseInt("10000"));
                                                    }


                                                }
                                                try {

                                                    if (numCols.contains(j)) {
                                                        textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(colCursor.getString(j).toString()), decimal_check));
                                                    } else if (dateCols.contains(j)) {
                                                        textView.setPadding(10, 8, 8, 10);
                                                        textView.setText(DateUtill.PaternDate2(colCursor.getString(j).toString()));
                                                    } else {
                                                        if (j == 0) {
                                                            textView.setText(i + "");
                                                        } else {
                                                            textView.setText(colCursor.getString(j).toString());
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    textView.setText("");
                                                }
                                            }
                                            // 5) add textView to tableRow
                                            tableRow.addView(textView, tableRowParams);
                                        }

                                        // 6) add tableRow to tableLayout
                                        tableLayout.addView(tableRow, tableLayoutParams);
                                        i++;
                                    }
                                } while (cursor.moveToNext());


                                if (sqlFooter.equals("")) {

                                } else {
//                                    Cursor cursor1 = database.rawQuery(sqlFooter, null);
//                                    int i1 = i;
//
//                                    if (cursor1 != null)
//                                        cursor1.moveToFirst();
//
//                                    if (cursor1.moveToFirst()) {
//                                        do {
//                                            {
//                                                // 3) create tableRow
//                                                tableRow = new TableRow(ReportViewerActivity.this);
//
//                                                tableRow.setBackgroundColor(Color.BLACK);
//
//                                                for (int j = 0; j < cursor.getColumnCount(); j++) {
//                                                    // 4) create textView
//                                                    TextView textView = new TextView(ReportViewerActivity.this);
//                                                    //  textView.setText(String.valueOf(j));dynamic table layout
//                                                    textView.setBackgroundColor(Color.WHITE);
//                                                    textView.setGravity(Gravity.CENTER);
//
//                                                    String s1 = Integer.toString(i1);
//                                                    String s2 = Integer.toString(j);
//                                                    String s3 = s1 + s2;
//                                                    id = Integer.parseInt(s3);
//                                                    Log.d("TAG", "-___>" + id);
//                                                    if (i1 == i) {
//                                                        if (j == 0) {
//                                                            try {
//                                                                tableRow.setId(Integer.parseInt(cursor1.getString(j).toString()));
//
//                                                            } catch (Exception ex) {
//                                                                tableRow.setId(Integer.parseInt("10000"));
//                                                            }
//
//                                                        }
//                                                        try {
//                                                            if (numCols.contains(j)) {
//                                                                textView.setText(Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(j).toString()), decimal_check));
//                                                            } else if (dateCols.contains(j)) {
//                                                                textView.setPadding(10, 8, 8, 10);
//                                                                textView.setText(DateUtill.PaternDate2(cursor1.getString(j).toString()));
//                                                            } else {
//                                                                if (j == 0) {
//                                                                    //textView.setText(i + "");
//                                                                } else {
//                                                                    textView.setText(cursor1.getString(j).toString());
//                                                                }
//                                                            }
//
//                                                        } catch (Exception ex) {
//                                                            textView.setText("");
//                                                        }
//
//                                                    } else {
//                                                    }
//
//                                                    // 5) add textView to tableRow
//                                                    tableRow.addView(textView, tableRowParams);
//
//                                                }
//
//                                                // 6) add tableRow to tableLayout
//                                                tableLayout.addView(tableRow, tableLayoutParams);
//                                                i++;
//                                            }
//
//
//                                        } while (cursor.moveToNext());
//                                    }
                                }
                            }


                            hsv.addView(tableLayout);
                            sv.addView(hsv);
                            ll.addView(sv);
                        }
                    } catch (Exception ex) {

                    }
                }
                progressDialog.dismiss();
            }
        });

    }

    private void send_email_manager(String strEmail, String strFileName, String strReportName) {
        String dtt = Globals.Reportnamedate();
        try {
            String[] recipients = strEmail.split(",");
            final ReportViewerActivity.SendEmailAsyncTask email = new ReportViewerActivity.SendEmailAsyncTask();
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
        ReportViewerActivity activity;

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
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Email failed");
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_qr);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retail, menu);
        menu.setGroupVisible(R.id.grp_retail, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (flag.equals("0")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "No Report Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                showdialogFilter();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showdialogFilter() {
        String[] operArray = {};
        final String strFiltr = "";

        listDialog1 = new Dialog(this);
        listDialog1.setTitle("Search Filter");
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.report_filter, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);
        final Spinner spn_fields = (Spinner) listDialog1.findViewById(R.id.spn_fields);
        final Spinner spn_opr = (Spinner) listDialog1.findViewById(R.id.spn_opr);
        final EditText edt_value = (EditText) listDialog1.findViewById(R.id.edt_value);
        Button btn_search = (Button) listDialog1.findViewById(R.id.btn_search);
        listDialog1.show();
        Window window = listDialog1.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        operArray = getResources().getStringArray(R.array.operation);
        fill_operation();
        try {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fieldArray);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_fields.setAdapter(dataAdapter);
        } catch (Exception ex) {
        }
        try {
            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operArray);
            dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_opr.setAdapter(dataAdapter1);
        } catch (Exception ex) {
        }
        spn_fields.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    strField = String.valueOf(spn_fields.getItemAtPosition(i));
                } catch (Exception ecx) {
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spn_opr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    strOperation = String.valueOf(spn_opr.getItemAtPosition(i));
                } catch (Exception ecx) {
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_value.getText().toString().trim().equals("")) {
                    edt_value.setError("Field vaccant");
                    return;
                }
                strValue = edt_value.getText().toString().trim();
                if (str_type.equals("online")) {
                    Globals.online_report_cursor = null;
                    if (name.equals("Customer Statement Report")) {
                        if (isNetworkStatusAvialable(getApplicationContext())) {
                            progressDialog = new ProgressDialog(ReportViewerActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Getting data...");
                            progressDialog.show();
                            new Thread() {
                                @Override
                                public void run() {
                                    final Cursor cursor = getCustomerStatment();
                                    Globals.online_report_cursor = cursor;
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            String sql_footer = "";
                                            ArrayList<Integer> numCols = new ArrayList<>();
                                            numCols.add(2);
                                            numCols.add(3);
                                            numCols.add(4);
                                            ArrayList<Integer> dateCols = new ArrayList<>();
                                            dateCols.add(0);
                                            Intent intent = new Intent(ReportViewerActivity.this, ReportViewerActivity.class);
                                            Globals.ReportCondition = "";
                                            intent.putExtra("type", str_type);
                                            intent.putExtra("qry", "");
                                            intent.putExtra("name", name);
                                            intent.putExtra("qry_footer", sqlFooter);
                                            intent.putIntegerArrayListExtra("numCols", numCols);
                                            intent.putIntegerArrayListExtra("dateCols", dateCols);
                                            intent.putExtra("from", from);
                                            intent.putExtra("to", to);
                                            intent.putExtra("operation", operation);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }.start();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (isNetworkStatusAvialable(getApplicationContext())) {
                            progressDialog = new ProgressDialog(ReportViewerActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Getting data...");
                            progressDialog.show();
                            new Thread() {
                                @Override
                                public void run() {
                                    final Cursor cursor = getCustomerSummery();
                                    Globals.online_report_cursor = cursor;
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            String sql_footer = "";
                                            ArrayList<Integer> numCols = new ArrayList<>();
                                            ArrayList<Integer> dateCols = new ArrayList<>();
                                            Intent intent = new Intent(ReportViewerActivity.this, ReportViewerActivity.class);
                                            intent.putExtra("type", str_type);
                                            intent.putExtra("qry", "");
                                            intent.putExtra("name", name);
                                            intent.putExtra("qry_footer", sqlFooter);
                                            intent.putIntegerArrayListExtra("numCols", numCols);
                                            intent.putIntegerArrayListExtra("dateCols", dateCols);
                                            intent.putExtra("from", from);
                                            intent.putExtra("to", to);
                                            intent.putExtra("operation", operation);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }.start();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Globals.ReportCondition = "";
                    strValue = edt_value.getText().toString().trim();
                    if (strOperation.equals("Contains")) {
                        Globals.ReportCondition = " AND [" + strField + "] LIKE  '%" + strValue + "%'";
                    }

                    listDialog1.dismiss();

//                    Intent intent = new Intent(ReportViewerActivity.this, ReportViewerActivity.class);
//                    intent.putExtra("type", "");
//                    intent.putExtra("qry", sql);
//                    intent.putExtra("name", name);
//                    intent.putExtra("qry_footer", sqlFooter);
//                    intent.putIntegerArrayListExtra("numCols", numCols);
//                    intent.putIntegerArrayListExtra("dateCols", dateCols);
//                    intent.putExtra("from", from);
//                    intent.putExtra("to", to);
//                    intent.putExtra("operation", operation);
//                    startActivity(intent);
//                    finish();

                    ll.removeAllViews();
                    progressDialog = new ProgressDialog(ReportViewerActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Wait...");
                    progressDialog.show();
                    new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(2 * 1000);
                                        load_data();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                }
            }
        });
    }

    private void fill_operation() {
        try {
            if (str_type.equals("online")) {
                if (name.equals("Customer Statement Report")) {
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add("Date");
                    arrayList1.add("Narration");
                    arrayList1.add("Dr Amount");
                    arrayList1.add("Cr Amount");
                    arrayList1.add("Current Balance");
                    fieldArray = new String[arrayList1.size()];
                    for (int i = 0; i < arrayList1.size(); i++) {
                        fieldArray[i] = arrayList1.get(i);
                    }
                } else {
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add("Contact Code");
                    arrayList1.add("Name");
                    arrayList1.add("Device Code");
                    fieldArray = new String[arrayList1.size()];
                    for (int i = 0; i < arrayList1.size(); i++) {
                        fieldArray[i] = arrayList1.get(i);
                    }
                }
            } else {
                Cursor cursor = database.rawQuery(sql, null);
                Cursor colCursor = cursor;
                if (colCursor != null)
                    colCursor.moveToFirst();
                if (colCursor.moveToFirst()) {
                    do {
                        fieldArray = new String[colCursor.getColumnCount()];
                        for (int j = 0; j < colCursor.getColumnCount(); j++) {
                            try {
                                fieldArray[j] = colCursor.getColumnName(j).toString();
                            } catch (Exception ex) {
                            }
                        }
                    } while (colCursor.moveToNext());
                    }
            }
            Cursor cursor = database.rawQuery(sql, null);
            Cursor colCursor = cursor;
            if (colCursor != null)
                colCursor.moveToFirst();
            if (colCursor.moveToFirst()) {
                do {
                    fieldArray = new String[colCursor.getColumnCount()];
                    for (int j = 0; j < colCursor.getColumnCount(); j++) {
                        try {
                            fieldArray[j] = colCursor.getColumnName(j).toString();
                        } catch (Exception ex) {
                        }
                    }
                } while (colCursor.moveToNext());
            }
        } catch (Exception ex) {
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
        progressDialog = new ProgressDialog(ReportViewerActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.online_report_cursor = null;
                    Globals.pageNO = 0;
                    Intent intent = new Intent(ReportViewerActivity.this, ReportActivity.class);
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

    private Cursor getCustomerSummery() {
        MatrixCursor mc = null;
        String serverData = "";
              //  get_CustomerSummery_server();
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

    /*private String get_CustomerSummery_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "customer_summary");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("field", strField));
        nameValuePairs.add(new BasicNameValuePair("operation", strOperation));
        nameValuePairs.add(new BasicNameValuePair("value", strValue));

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


    private Cursor getCustomerStatment() {
        MatrixCursor mc = null;
        String serverData = "";
                //get_CustomerStatment_server();
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

    /*private String get_CustomerStatment_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
             Globals.App_IP_URL + "customer_statements");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("contact_code", ""));
        nameValuePairs.add(new BasicNameValuePair("from_date", from));
        nameValuePairs.add(new BasicNameValuePair("to_date", to));
        nameValuePairs.add(new BasicNameValuePair("field", strField));
        nameValuePairs.add(new BasicNameValuePair("operation", strOperation));
        nameValuePairs.add(new BasicNameValuePair("value", strValue));
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
}


