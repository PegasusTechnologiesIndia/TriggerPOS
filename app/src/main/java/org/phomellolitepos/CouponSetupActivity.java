package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CouponSetupActivity extends AppCompatActivity {
    EditText edt_min_purchase_value, edt_earn_value, edt_min_redeem_value, edt_name, edt_valid_from, edt_valid_to;
    ImageView img_valid_from, img_valid_to;
    Button btn_next;
    String date, decimal_check;
    Pro_Loyalty_Setup pro_loyalty_setup;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    Settings settings;
    Calendar myCalendar;
    String str_min_pur_value, str_earn_value, str_redeem_value, str_valid_from, str_valid_to, str_name;
    String strid, operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Coupon Setup");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        myCalendar = Calendar.getInstance();
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
                pDialog = new ProgressDialog(CouponSetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(CouponSetupActivity.this, CouponListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(CouponSetupActivity.this, CouponListActivity.class);
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

        edt_min_purchase_value = (EditText) findViewById(R.id.edt_min_purchase_value);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_earn_value = (EditText) findViewById(R.id.edt_earn_value);
        edt_min_redeem_value = (EditText) findViewById(R.id.edt_min_redeem_value);
        edt_valid_to = (EditText) findViewById(R.id.edt_valid_to);
        edt_valid_from = (EditText) findViewById(R.id.edt_valid_from);
        img_valid_from = (ImageView) findViewById(R.id.img_valid_from);
        img_valid_to = (ImageView) findViewById(R.id.img_valid_to);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setText("Next");
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        Intent intent = getIntent();
        strid = intent.getStringExtra("id");
        operation = intent.getStringExtra("operation");

        if (operation == null) {
            operation = "Add";
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }


        if (operation.equals("Edit")) {
            pro_loyalty_setup = Pro_Loyalty_Setup.getPro_Loyalty_Setup(getApplicationContext(), database, db, "WHERE id = '" + strid + "'");
            edt_min_purchase_value.setText(pro_loyalty_setup.get_min_purchase_value());
            edt_name.setText(pro_loyalty_setup.get_name());
            edt_earn_value.setText(pro_loyalty_setup.get_earn_value());
            edt_min_redeem_value.setText(pro_loyalty_setup.get_mis_redeem_value());
            edt_valid_from.setText(pro_loyalty_setup.get_valid_from());
            edt_valid_to.setText(pro_loyalty_setup.get_valid_to());
        }

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

        img_valid_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_valid_from.getWindowToken(), 0);
                new DatePickerDialog(CouponSetupActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        img_valid_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_valid_to.getWindowToken(), 0);
                new DatePickerDialog(CouponSetupActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_min_purchase_value.getText().toString().trim().equals("")) {
                    edt_min_purchase_value.setError("Min parchase is required");
                    edt_min_purchase_value.requestFocus();
                    return;
                } else {
                    str_min_pur_value = edt_min_purchase_value.getText().toString().trim();
                }

                if (edt_name.getText().toString().equals("")) {
                    edt_name.setError("Name is required");
                    edt_name.requestFocus();
                    return;
                } else {
                    str_name = edt_name.getText().toString();
                }

                if (edt_earn_value.getText().toString().equals("")) {
                    edt_earn_value.setError("Earn value is required");
                    edt_earn_value.requestFocus();
                    return;
                } else {
                    str_earn_value = edt_earn_value.getText().toString();
                }

                if (edt_min_redeem_value.getText().toString().equals("")) {
                    edt_min_redeem_value.setError("Min redeem is required");
                    edt_min_redeem_value.requestFocus();
                    return;
                } else {
                    str_redeem_value = edt_min_redeem_value.getText().toString();
                }

                if (edt_valid_from.getText().toString().equals("")) {
                    edt_valid_from.setError("Valid from is required");
                    edt_valid_from.requestFocus();
                    return;
                } else {
                    str_valid_from = edt_valid_from.getText().toString();
                }

                if (edt_valid_to.getText().toString().equals("")) {
                    edt_valid_to.setError("Valid from is required");
                    edt_valid_to.requestFocus();
                    return;
                } else {
                    str_valid_to = edt_valid_to.getText().toString();
                }

                pDialog = new ProgressDialog(CouponSetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Fill_Setup(str_min_pur_value, str_earn_value, str_redeem_value, str_valid_from, str_valid_to, str_name);
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

    private void Fill_Setup(String str_min_pur_value, String str_earn_value, String str_redeem_value, String str_valid_from, String str_valid_to, String str_name) {

        String suc = "0";
        long l;
        try {
            database.beginTransaction();
            if (pro_loyalty_setup == null) {
                pro_loyalty_setup = new Pro_Loyalty_Setup(getApplicationContext(), null, str_min_pur_value, "0", "0", str_earn_value, str_redeem_value, "PROMOTIONSYSTEM", str_name, str_valid_from, str_valid_to);
                l = pro_loyalty_setup.insertPro_Loyalty_Setup(database);
                if (l > 0) {
                    suc = "1";
                }
            } else {
                pro_loyalty_setup = new Pro_Loyalty_Setup(getApplicationContext(), pro_loyalty_setup.get_id(), str_min_pur_value, "0", "0", str_earn_value, str_redeem_value, "PROMOTIONSYSTEM", str_name, str_valid_from, str_valid_to);
                l = pro_loyalty_setup.updatePro_Loyalty_Setup("id=?", new String[]{pro_loyalty_setup.get_id()}, database);
                if (l > 0) {
                    suc = "1";
                }
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

                Intent intent = new Intent(CouponSetupActivity.this, CouponGenerateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("id", l + "");
                intent.putExtra("operation", operation);
                startActivity(intent);
                pDialog.dismiss();
                finish();
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

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String from;
        try {
            from = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            from = "";
        }
        edt_valid_from.setText(from);
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
        edt_valid_to.setText(too);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(CouponSetupActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(CouponSetupActivity.this, CouponListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(CouponSetupActivity.this, CouponListActivity.class);
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
}
