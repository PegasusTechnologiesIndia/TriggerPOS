package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import org.phomellolitepos.database.Pos_Balance;

public class ExpensesActivity extends AppCompatActivity {
    TextInputLayout edt_layout_expenses, edt_layout_expense_name;
    EditText edt_expenses, edt_expense_name;
    Button btn_save, btn_delete;
    Database db;
    SQLiteDatabase database;
    String date, decimal_check, expense_amount, succ="0", operation, code,id;
    Pos_Balance pos_balance;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Expenses);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        Intent intent = getIntent();
        operation = intent.getStringExtra("operation");
        code = intent.getStringExtra("code");

        if (operation == null) {
            operation = "Add";
            id = null;
        }


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id==0){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ExpensesActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ExpensesActivity.this, ExpensesListActivity.class);
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

        edt_layout_expenses = (TextInputLayout) findViewById(R.id.edt_layout_expenses);
        edt_layout_expense_name = (TextInputLayout) findViewById(R.id.edt_layout_expense_name);
        edt_expense_name = (EditText) findViewById(R.id.edt_expense_name);
        edt_expenses = (EditText) findViewById(R.id.edt_expenses);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        edt_expenses.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (edt_expenses.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_expenses.requestFocus();
                    edt_expenses.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_expenses, InputMethodManager.SHOW_IMPLICIT);
                    edt_expenses.selectAll();
                    return true;
                }
            }
        });

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        if (operation.equals("Edit")) {
            btn_delete.setVisibility(View.VISIBLE);
            pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(), database, " WHERE z_code ='0' And type ='EXP' And pos_balance_code = '" + code + "'");

            expense_amount = Globals.myNumberFormat2Price(Double.parseDouble(pos_balance.get_amount()), decimal_check);
            edt_expenses.setText(expense_amount);
            edt_expense_name.setText(pos_balance.get_remarks());

        }


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(ExpensesActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            database.beginTransaction();
                            pos_balance.set_is_active("0");
                            long it = pos_balance.updatePos_Balance("pos_balance_id=?", new String[]{pos_balance.get_pos_balance_id()}, database);
                            if (it > 0) {
                                database.setTransactionSuccessful();
                                database.endTransaction();
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent_category = new Intent(ExpensesActivity.this, ExpensesListActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }
                                });


                            } else {
                                database.endTransaction();
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } finally {
                        }
                    }
                };
                timerThread.start();

            }


        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String expense_amount, expense_name;
                if (edt_expense_name.getText().toString().trim().equals("")) {
                    edt_expense_name.setError(getString(R.string.Name_is_required));
                    edt_expense_name.requestFocus();
                    return;
                } else {

                    expense_name = edt_expense_name.getText().toString().trim();
                }

                if (edt_expenses.getText().toString().equals("")||edt_expenses.getText().toString().equals(".")) {
                    edt_expenses.setError(getString(R.string.Amount_is_required));
                    edt_expenses.requestFocus();
                    return;
                } else {
                    expense_amount = edt_expenses.getText().toString();
                }

                pDialog = new ProgressDialog(ExpensesActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            fill_expenses(expense_name, expense_amount);
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

    private void fill_expenses(String expense_name, String expense_amount) {

        pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(),database,"  order By pos_balance_id Desc LIMIT 1");
        String strPBCode = "";

        if (pos_balance == null) {
            strPBCode = "PB-" + 1;
        } else {
            strPBCode = "PB-" + (Integer.parseInt(pos_balance.get_pos_balance_id()) + 1);
        }

        String modified_by = Globals.user;

        database.beginTransaction();
        if (operation.equals("Edit")) {
            pos_balance = Pos_Balance.getPos_Balance(getApplicationContext(),database,"WHERE pos_balance_code ='"+code+"'");
            pos_balance.set_remarks(expense_name);
            pos_balance.set_amount(expense_amount);
            long u = pos_balance.updatePos_Balance("pos_balance_code=?", new String[]{code}, database);
            if (u > 0) {
                succ = "1";
            }

        } else {
            pos_balance = new Pos_Balance(getApplicationContext(),null,strPBCode,Globals.objLPD.getDevice_Code(),"EXP", date, expense_amount, expense_name, "0", "1", modified_by, date, "N");
            long l = pos_balance.insertPos_Balance(database);
            if (l > 0) {
                succ = "1";
            }
        }

        if (succ.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Expenses_Added_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ExpensesActivity.this, ExpensesListActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Expenses_Not_Added, Toast.LENGTH_SHORT).show();
                }
            });

           
        }

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ExpensesActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ExpensesActivity.this, ExpensesListActivity.class);
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
