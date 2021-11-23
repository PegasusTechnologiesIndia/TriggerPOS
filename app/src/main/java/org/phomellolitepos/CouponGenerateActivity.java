package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Coupon_Detail;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CouponGenerateActivity extends AppCompatActivity {
    EditText edt_noc, edt_start, edt_last_code;
    Button btn_save, btn_csv;
    Coupon_Detail couponDetail;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    Settings settings;
    String strCodeId, operation, str_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_generate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Coupon Generate");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(CouponGenerateActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(CouponGenerateActivity.this, CouponListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(CouponGenerateActivity.this, CouponListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });

        edt_noc = (EditText) findViewById(R.id.edt_noc);
        edt_start = (EditText) findViewById(R.id.edt_start);
        edt_last_code = (EditText) findViewById(R.id.edt_last_code);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_csv = (Button) findViewById(R.id.btn_csv);

        Intent intent = getIntent();
        strCodeId = intent.getStringExtra("id");
        operation = intent.getStringExtra("operation");

        if (operation == null) {
            operation = "Add";
        }

        if (operation.equals("Edit")) {
            couponDetail = Coupon_Detail.getCoupon_Detail(getApplicationContext(), "WHERE card_id = '" + strCodeId + "'", database);
//            if (couponDetail!=null){
//                edt_start.setText(couponDetail.get());
//                edt_last_code.setText(couponDetail.get_earn_value());
//            }
        }

        btn_csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // here dialog will open for cloud operations

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                        CouponGenerateActivity.this);
                                alertDialog.setTitle("");
                                alertDialog
                                        .setMessage("do you want to import?");
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                alertDialog.setNegativeButton(R.string.Cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        });
                                alertDialog.setPositiveButton(R.string.Ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                pDialog = new ProgressDialog(CouponGenerateActivity.this);
                                                pDialog.setTitle("");
                                                pDialog.setMessage("Importing coupon...");
                                                pDialog.setCancelable(false);
                                                pDialog.show();
                                                final Thread t = new Thread() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            try {
                                                                String succ_import="0";
                                                                sleep(200);
                                                                database.beginTransaction();
                                                                String modified_by = Globals.user;
                                                                File myFile = new File("/sdcard/coupon.csv");
                                                                FileInputStream fIn = new FileInputStream(myFile);
                                                                BufferedReader myReader = new BufferedReader(
                                                                        new InputStreamReader(fIn));
                                                                String aDataRow = "";
                                                                while ((aDataRow = myReader.readLine()) != null) {
                                                                    List<String> myList = new ArrayList<String>(Arrays.asList(aDataRow.split(",")));

                                                                    couponDetail = new Coupon_Detail(getApplicationContext(), null,
                                                                                strCodeId, myList.get(0),"N");
                                                                        long l = couponDetail.insertCoupon_Detail(database);
                                                                        if (l > 0) {
                                                                            succ_import = "1";
                                                                        } else {
                                                                        }
                                                                }
                                                                myReader.close();
                                                                if (succ_import.equals("1")) {
                                                                    database.setTransactionSuccessful();
                                                                    database.endTransaction();
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            pDialog.dismiss();
                                                                            Toast.makeText(getBaseContext(),"Import successfully",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                } else {
                                                                    database.endTransaction();
                                                                }

                                                            } catch (final Exception e) {
                                                                database.endTransaction();
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        pDialog.dismiss();
                                                                        Toast.makeText(getBaseContext(), e.getMessage(),
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        } catch (Exception ex) {
                                                            database.endTransaction();
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    pDialog.dismiss();
                                                                }
                                                            });
                                                            // TODO Auto-generated catch block
                                                            ex.printStackTrace();
                                                        }
                                                    }
                                                };
                                                t.start();
                                            }
                                        });
                                AlertDialog alert = alertDialog.create();
                                alert.show();
                                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_noc.getText().toString().trim().equals("")) {
                    edt_noc.setError("Min parchase is required");
                    edt_noc.requestFocus();
                    return;
                }

                pDialog = new ProgressDialog(CouponGenerateActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Fill_Setup(str_start);
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

    private void Fill_Setup(String str_start) {
        String suc = "0";
        int CardNo;
        try {
            database.beginTransaction();
            Coupon_Detail coupon_detail = Coupon_Detail.getCoupon_Detail(getApplicationContext(), "  order By id Desc LIMIT 1", database);
            if (coupon_detail == null) {
                CardNo = 1;
            } else {
                CardNo = Integer.parseInt(coupon_detail.getid()) + 1;
            }

            for (int i = 1; i <= Integer.parseInt(edt_noc.getText().toString()); i++) {
                couponDetail = new Coupon_Detail(getApplicationContext(), null, strCodeId, CardNo + "", "N");
                long l = couponDetail.insertCoupon_Detail(database);
                if (l > 0) {
                    suc = "1";
                }
                CardNo++;
            }

            if (suc.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Save successful", Toast.LENGTH_SHORT).show();
                    }
                });
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(CouponGenerateActivity.this, CouponListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(CouponGenerateActivity.this, CouponListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Somthing went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception ex) {
            database.endTransaction();
        }
    }
}
