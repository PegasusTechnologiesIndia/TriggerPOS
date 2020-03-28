package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Pos_Balance;

public class OpeningBalanceActivity extends AppCompatActivity {
    TextInputLayout edt_layout_ob;
    EditText edt_ob;
    Button btn_save;
    Database db;
    SQLiteDatabase database;
    String date, decimal_check, opening_amount, succ;
    Pos_Balance pos_balance;
    String part2;
    String posId = null;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_balance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Opening_Balance);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

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
                pDialog = new ProgressDialog(OpeningBalanceActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(OpeningBalanceActivity.this, ManagerActivity.class);
                            startActivity(intent);
                            startActivity(intent);
                            pDialog.dismiss();
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

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }


        edt_layout_ob = (TextInputLayout) findViewById(R.id.edt_layout_ob);
        edt_ob = (EditText) findViewById(R.id.edt_ob);
        btn_save = (Button) findViewById(R.id.btn_save);

        edt_ob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_ob.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_ob.requestFocus();
                    edt_ob.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_ob, InputMethodManager.SHOW_IMPLICIT);
                    edt_ob.selectAll();
                    return true;
                }
            }
        });

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        check_pos_balance();

//        edt_ob.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                edt_ob.selectAll();
//                return true;
//            }
//        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String modified_by = Globals.user;
                final String op_balance;

                if (edt_ob.getText().toString().equals("")) {
                    edt_ob.setError(getString(R.string.Costprice_is_required));
                    edt_ob.requestFocus();
                    return;
                } else {
                    op_balance = edt_ob.getText().toString();
                }

                pDialog = new ProgressDialog(OpeningBalanceActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            fill_opening_balance(op_balance, modified_by);

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

    private void fill_opening_balance(String op_balance, String modified_by) {

        pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, "  order By pos_balance_id Desc LIMIT 1");
        String strPBCode = "";

        Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
        if (last_code == null) {
            if (pos_balance == null) {
                strPBCode = "PB-" + 1;
            } else {
                strPBCode = "PB-" + (Integer.parseInt(pos_balance.get_pos_balance_id()) + 1);
            }

        } else {
            if (last_code.getlast_pos_balance_code().equals("0")) {
                if (pos_balance == null) {
                    strPBCode = "PB-" + 1;
                } else {
                    strPBCode = "PB-" + (Integer.parseInt(pos_balance.get_pos_balance_id()) + 1);
                }
            } else {
                if (pos_balance == null) {

                    String code = last_code.getlast_order_code();
                    String[] strCode = code.split("-");
                    part2 = strCode[1];
                    posId = (Integer.parseInt(part2) + 1) + "";
                    strPBCode = "PB-" + (Integer.parseInt(part2) + 1);
                } else {
                    strPBCode = "PB-" + (Integer.parseInt(pos_balance.get_pos_balance_id()) + 1);
                }
            }
        }


        pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, " WHERE z_code ='0' And type ='OB'");
        database.beginTransaction();
        if (pos_balance == null) {
            pos_balance = new Pos_Balance(getApplicationContext(), posId, strPBCode, Globals.objLPD.getDevice_Code(), "OB", date, op_balance, "opening balance", "0", "1", modified_by, date, "N");
            long l = pos_balance.insertPos_Balance(database);
            if (l > 0) {
                succ = "1";
            }
        } else {
            pos_balance.set_amount(op_balance);
            long u = pos_balance.updatePos_Balance("pos_balance_code=?", new String[]{pos_balance.get_pos_balance_code()}, database);
            if (u > 0) {
                succ = "1";
            }
        }

        if (succ.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.OB_Added_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OpeningBalanceActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.OB_Not_Added, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void check_pos_balance() {

        pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, " WHERE z_code ='0' And type ='OB'");

        if (pos_balance == null) {
            String op_balance = "0";
            opening_amount = Globals.myNumberFormat2Price(Double.parseDouble(op_balance), decimal_check);
            edt_ob.setText(opening_amount);
            edt_ob.selectAll();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        } else {
            opening_amount = Globals.myNumberFormat2Price(Double.parseDouble(pos_balance.get_amount()), decimal_check);
            edt_ob.setText(opening_amount);
            edt_ob.selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(OpeningBalanceActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(OpeningBalanceActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    startActivity(intent);
                    pDialog.dismiss();
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
