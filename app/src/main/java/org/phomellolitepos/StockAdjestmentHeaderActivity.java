package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Stock_Adjustment_Header;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StockAdjestmentHeaderActivity extends AppCompatActivity {
    EditText edt_voucher_no,edt_date,edt_remarks;
    ImageView img_date;
    Button btn_next;
    String operation,voucher_no,strVoucherNo,Id;
    Database db;
    SQLiteDatabase database;
    Stock_Adjustment_Header stock_adjustment_header;
    ProgressDialog pDialog;
    Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjestment_header);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                pDialog = new ProgressDialog(StockAdjestmentHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(StockAdjestmentHeaderActivity.this, StockAdjestmentListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
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
        Intent intent = getIntent();
        getSupportActionBar().setTitle("Stock Adjustment");
        operation = intent.getStringExtra("operation");
        voucher_no = intent.getStringExtra("voucher_no");

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        myCalendar = Calendar.getInstance();
        edt_voucher_no = (EditText)findViewById(R.id.edt_voucher_no);
        edt_date = (EditText)findViewById(R.id.edt_date);
        edt_remarks = (EditText)findViewById(R.id.edt_remarks);
        img_date = (ImageView)findViewById(R.id.img_date);
        btn_next = (Button)findViewById(R.id.btn_next);

        if (operation.equals("Edit")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            try {
                stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'",database);
                edt_voucher_no.setText(stock_adjustment_header.get_voucher_no());
                edt_date.setText(stock_adjustment_header.get_date());
                edt_remarks.setText(stock_adjustment_header.get_remarks());
            }catch (Exception ex){}

        }else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetDate();
            }

        };

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(img_date.getWindowToken(), 0);
                new DatePickerDialog(StockAdjestmentHeaderActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_voucher_no.getText().toString().trim().equals("")){
                    Stock_Adjustment_Header objVoucher = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), "order By id Desc LIMIT 1",database);

                    if (objVoucher == null) {
                        strVoucherNo = "SVC-" + 1;
                    } else {
                        strVoucherNo = "SVC-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                    }
                }else{

                    strVoucherNo = edt_voucher_no.getText().toString().trim();
                }

                if (edt_date.getText().toString().trim().equals("")){
                    edt_date.setError("Date is required");
                    return;
                }

                pDialog = new ProgressDialog(StockAdjestmentHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(StockAdjestmentHeaderActivity.this, StockAdjestmentFinalActivity.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("voucher_no",strVoucherNo);
                                    intent.putExtra("date", edt_date.getText().toString().trim());
                                    intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });

    }

    private void SetDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String date;
        try {
            date = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            date = "";
        }
        edt_date.setText(date);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(StockAdjestmentHeaderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(StockAdjestmentHeaderActivity.this, StockAdjestmentListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
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
