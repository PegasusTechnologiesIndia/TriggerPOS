package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;

public class ManufactureActivity extends AppCompatActivity {

    EditText edt_manufacture_code, edt_manufacture_name;
    TextInputLayout edt_layout_manufacture_code, edt_layout_manufacture_name;
    Button btn_save, btn_manufacture_delete;
    Manufacture manufacture;
    String operation, code;
    Database db;
    SQLiteDatabase database;
    String date;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String strMCCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id==0){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ManufactureActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ManufactureActivity.this, ManufactureListActivity.class);
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
        getSupportActionBar().setTitle(R.string.Manufacture);
        code = intent.getStringExtra("manufacture_code");
        operation = intent.getStringExtra("operation");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        edt_layout_manufacture_code = (TextInputLayout) findViewById(R.id.edt_layout_manufacture_code);
        edt_layout_manufacture_name = (TextInputLayout) findViewById(R.id.edt_layout_manufacture_name);
        edt_manufacture_code = (EditText) findViewById(R.id.edt_manufacture_code);
        edt_manufacture_name = (EditText) findViewById(R.id.edt_manufacture_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_manufacture_delete = (Button) findViewById(R.id.btn_manufacture_delete);

        if (operation.equals("Edit")) {
            btn_manufacture_delete.setVisibility(View.VISIBLE);
            manufacture = manufacture.getManufacture(getApplicationContext(), database, db, "WHERE manufacture_code = '" + code + "'");
            edt_manufacture_code.setText(manufacture.get_manufacture_code());
            edt_manufacture_name.setText(manufacture.get_manufacture_name());
        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

        new Thread() {
            @Override
            public void run() {
                String result = check_online_mode();
            }
        }.start();


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (operation.equals("Edit")) {

                    final String manufacture_name = edt_manufacture_name.getText().toString().trim();
                    final String manufacture_code = edt_manufacture_code.getText().toString().trim();
                    pDialog = new ProgressDialog(ManufactureActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                Update_manufacture(code, manufacture_name, manufacture_code);

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();

                } else if (operation.equals("Add")) {
                    final String manufacture_name;

                    if (edt_manufacture_code.getText().toString().equals("")) {
                        Manufacture objMC1 = Manufacture.getManufacture(getApplicationContext(), database, db, "  order By manufacture_id Desc LIMIT 1");

                        if (objMC1 == null) {
                            strMCCode = "M-" + 1;
                        } else {
                            strMCCode = "M-" + (Integer.parseInt(objMC1.get_manufacture_id()) + 1);
                        }
                    } else {
                        if (edt_manufacture_code.getText().toString().contains(" ")) {
                            edt_manufacture_code.setError(getString(R.string.Cant_Enter_Space));
                            edt_manufacture_code.requestFocus();
                            return;
                        } else {
                            strMCCode = edt_manufacture_code.getText().toString();
                        }

                    }

                    if (edt_manufacture_name.getText().toString().equals("")) {
                        edt_manufacture_name.setError(getString(R.string.Manufacture_name_is_required));
                        edt_manufacture_name.requestFocus();
                        return;
                    } else {
                        manufacture_name = edt_manufacture_name.getText().toString();
                    }

                    pDialog = new ProgressDialog(ManufactureActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);

                                Fill_Manufacture(manufacture_name, strMCCode);

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

        btn_manufacture_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(ManufactureActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            manufacture.set_is_active("0");
                            long l = manufacture.updateManufacture("manufacture_code=?", new String[]{code}, database);
                            if (l > 0) {
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent_category = new Intent(ManufactureActivity.this, ManufactureListActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }
                                });

                            } else {
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
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
    }

    private void Fill_Manufacture(String manufacture_name, String manufacture_code) {

        String modified_by = Globals.user;
        database.beginTransaction();
        manufacture = new Manufacture(getApplicationContext(), null, Globals.Device_Code, manufacture_code, manufacture_name, "0", "1", modified_by, date, "N");
        long l = manufacture.insertManufacture(database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(ManufactureActivity.this, ManufactureListActivity.class);
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

    private void Update_manufacture(String code, String manufacture_name, String manufacture_code) {
        database.beginTransaction();
        manufacture.set_manufacture_name(manufacture_name);
        manufacture.set_manufacture_code(manufacture_code);
        manufacture.set_is_push("N");
        long l = manufacture.updateManufacture("manufacture_code=?", new String[]{code}, database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Update_Successfully, Toast.LENGTH_SHORT).show();
                    //clear();
                    Intent intent_category = new Intent(ManufactureActivity.this, ManufactureListActivity.class);
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

    private String check_online_mode() {
        String result = "";
        String ck_projct_type = "";

        if (isNetworkStatusAvialable(getApplicationContext())) {
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

            try {
                ck_projct_type = lite_pos_registration.getproject_id();
            } catch (Exception e) {
                ck_projct_type = "";
            }

            if (ck_projct_type.equals("cloud")) {
                result = Manufacture.sendOnServer(getApplicationContext(), database, db, "Select  * From manufacture  WHERE is_push = 'N'");
            }

        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
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
        pDialog = new ProgressDialog(ManufactureActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ManufactureActivity.this, ManufactureListActivity.class);
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
