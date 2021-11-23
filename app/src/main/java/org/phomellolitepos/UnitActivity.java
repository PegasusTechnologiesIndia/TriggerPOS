package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Unit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UnitActivity extends AppCompatActivity {
    EditText edt_unit_name, edt_description, edt_code;
    Button btn_save, btn_delete;
    ProgressDialog pDialog;
    String operation, unit_id, date, str_unit_name, str_unit_code, str_unit_des;
    SQLiteDatabase database;
    Database db;
    Unit unit;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    MenuItem menuItem;
    String Unitid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Unit);
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
                pDialog = new ProgressDialog(UnitActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(UnitActivity.this, UnitListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        edt_unit_name = (EditText) findViewById(R.id.edt_unit_name);
        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_code = (EditText) findViewById(R.id.edt_code);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_save.setVisibility(View.GONE);

        Intent intent = getIntent();
        unit_id = intent.getStringExtra("unit_id");
        operation = intent.getStringExtra("operation");

        if (operation == null) {
            operation = "Add";
        }

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);


        if (operation.equals("Edit")) {
            try {
                if(!Globals.objLPR.getproject_id().equals("cloud")) {

                    btn_delete.setVisibility(View.VISIBLE);
                }

                unit = Unit.getUnit(getApplicationContext(), database, db, " WHERE unit_id = '" + unit_id + "'");
                edt_unit_name.setText(unit.get_name());
                edt_code.setText(unit.get_code());
                edt_code.setEnabled(false);
                edt_description.setText(unit.get_description());
            }catch (Exception ex){}
        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
        }


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(
                        UnitActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage(R.string.do_you_want_to_delete);

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
                                pDialog = new ProgressDialog(UnitActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);
                                          //  database.beginTransaction();

                                              String sqlQuery="select ut.unit_id from unit ut left join item i ON i.unit_id = ut.unit_id where ut.unit_id = '" + unit_id + "' and i.is_active ='1'";
                                            //database = db.getReadableDatabase();
                                            Cursor cursor = database.rawQuery(sqlQuery, null);
                                            if (cursor.moveToFirst()) {
                                                do {
                                                  Unitid = cursor.getString(0);
                                                } while (cursor.moveToNext());

                                            }

                                            // closing connection
                                            cursor.close();
                                            
                                            if(Unitid!=null){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        pDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), getString(R.string.deletemsg), Toast.LENGTH_LONG).show();

                                                    }
                                                });
                                                }
                                            else {
                                                Unit unit = Unit.getUnit(getApplicationContext(), database, db, "WHERE unit_id = '" + unit_id + "'");
                                                unit.set_is_active("false");
                                                long h = unit.updateUnit("unit_id=?", new String[]{unit_id}, database);
                                                if (h > 0) {
                                                   /* database.setTransactionSuccessful();
                                                    database.endTransaction();*/
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(UnitActivity.this, UnitListActivity.class);
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

    private void Update_Unit(String unit_name, String unit_code, String unit_des) {

        database.beginTransaction();
        unit.set_name(unit_name);
        unit.set_code(unit_code);
        unit.set_description(unit_des);
        unit.set_is_push("N");
        long l = unit.updateUnit("unit_id=?", new String[]{unit.get_unit_id()}, database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.UpdateSuccessfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(UnitActivity.this, UnitListActivity.class);
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

    private void Insert_Unit(String str_unit_name, String str_unit_code, String str_unit_des) {
        String modified_by = Globals.user;
        database.beginTransaction();
        unit = new Unit(getApplicationContext(), null, str_unit_name, str_unit_code, str_unit_des, "1", modified_by, date,"N");
        long l = unit.insertUnit(database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();


            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.SaveSuccessfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(UnitActivity.this, UnitListActivity.class);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(UnitActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.split_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void save() {

        if (operation.equals("Edit")) {
            final String unit_name = edt_unit_name.getText().toString().trim();
            final String unit_code = edt_code.getText().toString().trim();
            final String unit_des = edt_description.getText().toString().trim();
            pDialog = new ProgressDialog(UnitActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        Update_Unit(unit_name, unit_code, unit_des);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            };
            timerThread.start();
        } else if (operation.equals("Add")) {
            if (edt_unit_name.getText().toString().trim().equals("")) {
                edt_unit_name.setError(getString(R.string.Unit_is_required));
                edt_unit_name.requestFocus();
                return;
            } else {
                str_unit_name = edt_unit_name.getText().toString().trim();
            }

            if (edt_code.getText().toString().trim().equals("")) {
                edt_code.setError(getString(R.string.unitcodereq));
                edt_code.requestFocus();
                return;
            } else {
                str_unit_code = edt_code.getText().toString().trim();
            }

            str_unit_des = edt_description.getText().toString().trim();
            pDialog = new ProgressDialog(UnitActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);

                        Insert_Unit(str_unit_name, str_unit_code, str_unit_des);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                }
            };
            timerThread.start();
        }
    }


}
