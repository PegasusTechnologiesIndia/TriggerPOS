package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.Settings;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProLoyaltySetupActivity extends AppCompatActivity {
    EditText edt_min_purchase_value, edt_base_value, edt_earn_point, edt_earn_value, edt_min_redeem_value, edt_valid_from, edt_valid_to;
    Button btn_next;
    String date, decimal_check;
    Pro_Loyalty_Setup pro_loyalty_setup;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    Settings settings;
    Calendar myCalendar;
    String str_min_pur_value, str_earn_point, str_earn_value, str_redeem_value, str_valid_from, str_valid_to, str_base_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_loyalty_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loyalty Setup");
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
                pDialog = new ProgressDialog(ProLoyaltySetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ProLoyaltySetupActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ProLoyaltySetupActivity.this, Main2Activity.class);
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
        edt_base_value = (EditText) findViewById(R.id.edt_base_value);
        edt_earn_point = (EditText) findViewById(R.id.edt_earn_point);
        edt_earn_value = (EditText) findViewById(R.id.edt_earn_value);
        edt_min_redeem_value = (EditText) findViewById(R.id.edt_min_redeem_value);

        btn_next = (Button) findViewById(R.id.btn_next);

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = format.format(d);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        pro_loyalty_setup = Pro_Loyalty_Setup.getPro_Loyalty_Setup(getApplicationContext(), database, db, "where pro_loyalty_setup.loyalty_type='POINTSYSTEM'");

        if (pro_loyalty_setup != null) {
            edt_min_purchase_value.setText(pro_loyalty_setup.get_min_purchase_value());
            edt_base_value.setText(pro_loyalty_setup.get_base_value());
            edt_earn_point.setText(pro_loyalty_setup.get_earn_point());
            edt_earn_value.setText(pro_loyalty_setup.get_earn_value());
            edt_min_redeem_value.setText(pro_loyalty_setup.get_mis_redeem_value());
        }


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

                if (edt_base_value.getText().toString().equals("")) {
                    edt_base_value.setError("Base value is required");
                    edt_base_value.requestFocus();
                    return;
                } else {
                    str_base_value = edt_base_value.getText().toString();
                }

                if (edt_earn_point.getText().toString().equals("")) {
                    edt_earn_point.setError("Earn point is required");
                    edt_earn_point.requestFocus();
                    return;
                } else {
                    str_earn_point = edt_earn_point.getText().toString();
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


                pDialog = new ProgressDialog(ProLoyaltySetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Fill_Setup(str_min_pur_value, str_earn_point, str_earn_value, str_redeem_value, str_valid_from, str_valid_to, str_base_value);
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

    private void Fill_Setup(String str_min_pur_value, String str_earn_point, String str_earn_value, String str_redeem_value, String str_valid_from, String str_valid_to, String str_base_value) {

        String suc = "0";
        try {
            database.beginTransaction();
            if (pro_loyalty_setup == null) {
                pro_loyalty_setup = new Pro_Loyalty_Setup(getApplicationContext(), null, str_min_pur_value, str_base_value, str_earn_point, str_earn_value, str_redeem_value, "POINTSYSTEM", "", "", "");
                long l = pro_loyalty_setup.insertPro_Loyalty_Setup(database);
                if (l > 0) {
                    suc = "1";
                }
            } else {

                pro_loyalty_setup = new Pro_Loyalty_Setup(getApplicationContext(), pro_loyalty_setup.get_id(), str_min_pur_value, str_base_value, str_earn_point, str_earn_value, str_redeem_value, "POINTSYSTEM", "", "", "");
                long l = pro_loyalty_setup.updatePro_Loyalty_Setup("id=?", new String[]{pro_loyalty_setup.get_id()}, database);
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
                        Toast.makeText(getApplicationContext(),"Save successful",Toast.LENGTH_SHORT).show();
                    }
                });
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ProLoyaltySetupActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(ProLoyaltySetupActivity.this, Main2Activity.class);
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
                        Toast.makeText(getApplicationContext(),"Somthing went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception ex) {
            database.endTransaction();
        }
    }
}
