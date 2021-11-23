package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class BussinessGroupActivity extends AppCompatActivity {
    EditText edt_business_gp_code, edt_business_gp_name;
    TextInputLayout edt_layout_business_gp_code, edt_layout_business_gp_name;
    Button btn_save, btn_delete;
    String operation, code, date;
    SQLiteDatabase database;
    Database db;
    Bussiness_Group bussiness_group;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String strBgCode = "";
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Bussiness_Group);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        edt_layout_business_gp_code = (TextInputLayout) findViewById(R.id.edt_layout_business_gp_code);
        edt_layout_business_gp_name = (TextInputLayout) findViewById(R.id.edt_layout_business_gp_name);
        edt_business_gp_code = (EditText) findViewById(R.id.edt_business_gp_code);
        edt_business_gp_name = (EditText) findViewById(R.id.edt_business_gp_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);

//        edt_business_gp_code.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (edt_business_gp_code.getText().toString().trim().equals("")) {
//                    return false;
//                } else {
//                    edt_business_gp_code.requestFocus();
//                    edt_business_gp_code.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm4.showSoftInput(edt_business_gp_code, InputMethodManager.SHOW_IMPLICIT);
//                    edt_business_gp_code.selectAll();
//                    return true;
//                }
//            }
//        });

//        edt_business_gp_name.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (edt_business_gp_name.getText().toString().trim().equals("")) {
//                    return false;
//                } else {
//                    edt_business_gp_name.requestFocus();
//                    edt_business_gp_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm4.showSoftInput(edt_business_gp_name, InputMethodManager.SHOW_IMPLICIT);
//                    edt_business_gp_name.selectAll();
//                    return true;
//                }
//            }
//        });

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
                pDialog = new ProgressDialog(BussinessGroupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(BussinessGroupActivity.this, BussinessGroupListActivity.class);
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
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
        }
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        if (operation.equals("Edit")) {

            btn_delete.setVisibility(View.VISIBLE);
            bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE business_group_code = '" + code + "'");
            if (bussiness_group!=null){
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                edt_business_gp_code.setText(bussiness_group.get_business_group_code());
                edt_business_gp_name.setText(bussiness_group.get_name());
            }

        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (operation.equals("Edit")) {
                    final String business_gp_name = edt_business_gp_name.getText().toString().trim();
                    final String business_gp_code = edt_business_gp_code.getText().toString().trim();
                    pDialog = new ProgressDialog(BussinessGroupActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                Update_Bussiness_Group(code, business_gp_name, business_gp_code);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                } else if (operation.equals("Add")) {
                    final String business_gp_name;

                    if (edt_business_gp_code.getText().toString().equals("")) {
                        Bussiness_Group objBg1 = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "  order By business_group_id Desc LIMIT 1");
                        if (objBg1 == null) {
                            strBgCode = "BGC-" + 1;
                        } else {
                            strBgCode = "BGC-" + (Integer.parseInt(objBg1.get_business_group_id()) + 1);
                        }
                    } else {
                        if (edt_business_gp_code.getText().toString().contains(" ")) {
                            edt_business_gp_code.setError(getString(R.string.Cant_Enter_Space));
                            edt_business_gp_code.requestFocus();
                            return;
                        } else {
                            strBgCode = edt_business_gp_code.getText().toString();
                        }
                    }
                    if (edt_business_gp_name.getText().toString().equals("")) {
                        edt_business_gp_name.setError(getString(R.string.Name_is_required));
                        edt_business_gp_name.requestFocus();
                        return;
                    } else {
                        business_gp_name = edt_business_gp_name.getText().toString();
                    }
                    pDialog = new ProgressDialog(BussinessGroupActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                Fill_Bussiness_Group(business_gp_name, strBgCode);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(
                        BussinessGroupActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage("Do you want to delete?");

                lp = new LinearLayout.LayoutParams(
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
                                pDialog = new ProgressDialog(BussinessGroupActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);
                                            bussiness_group.set_is_active("0");
                                            long l = bussiness_group.updateBussiness_Group("business_group_code=?", new String[]{code}, database);
                                            if (l > 0) {
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                        Intent intent_category = new Intent(BussinessGroupActivity.this, BussinessGroupListActivity.class);
                                                        startActivity(intent_category);
                                                        finish();
                                                    }
                                                });
                                            } else {
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


                alert = alertDialog.create();
                alert.show();

                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        });
    }

    private void Update_Bussiness_Group(String code, String business_gp_name, String business_gp_code) {
        database.beginTransaction();
        bussiness_group.set_name(business_gp_name);
        bussiness_group.set_business_group_code(business_gp_code);
        bussiness_group.set_is_push("N");
        long l = bussiness_group.updateBussiness_Group("business_group_code=?", new String[]{this.code}, database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Update_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(BussinessGroupActivity.this, BussinessGroupListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });

        } else {
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void Fill_Bussiness_Group(final String business_gp_name, final String business_gp_code) {
        final String modified_by = Globals.user;
        database.beginTransaction();
        bussiness_group = new Bussiness_Group(getApplicationContext(), null, Globals.Device_Code, business_gp_code, "1", business_gp_name, "1", modified_by, date, "N");
        long l = bussiness_group.insertBussiness_Group(database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(BussinessGroupActivity.this, BussinessGroupListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });
        } else {
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void check_online_mode() {
        String ck_projct_type = "";

        if (isNetworkStatusAvialable(getApplicationContext())) {
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            try {
                ck_projct_type = lite_pos_registration.getproject_id();
            } catch (Exception e) {
                ck_projct_type = "";
            }
            if (ck_projct_type.equals("cloud")) {
                Bussiness_Group.sendOnServer(getApplicationContext(), database, db, "Select  * From business_group  WHERE is_push = 'N'");
            }
        } else {
        }
    }

    private boolean isNetworkStatusAvialable(Context applicationContext) {
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
        pDialog = new ProgressDialog(BussinessGroupActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Intent intent = new Intent(BussinessGroupActivity.this, BussinessGroupListActivity.class);
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
