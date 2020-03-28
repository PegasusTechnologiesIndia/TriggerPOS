package org.phomellolitepos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.phomellolitepos.Adapter.FromAdapter;
import org.phomellolitepos.Adapter.ItemAdapter;
import org.phomellolitepos.Adapter.ManufactureSpiinerAdapter;
import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Ticket_Setup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TicketSetupActivity extends AppCompatActivity {
    EditText edt_price, edt_deprt_time, edt_arriv_time,edt_bus_no;
    Spinner spn_brand, sp_from, sp_to;
    ImageView img_deprt_time, img_arriv_time;
    Button btn_next, btn_delete;
    CheckBox ck_PIT;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Manufacture manufacture;
    ArrayList<Manufacture> arrayList_manufacture;
    String str_manufacture_code;
    Calendar myCalendar;
    String strFrom, strTo, strPrice, strDepartTime, strArrivTime,strBusNo;
    Ticket_Setup ticket_setup;
    String strPIT, code, operation, Item_Id, decimal_check;
    AlertDialog.Builder alertDialog;
    LinearLayout.LayoutParams lp;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    private ArrayList<Item> FromList = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        myCalendar = Calendar.getInstance();
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(TicketSetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(TicketSetupActivity.this, TicketSetupListActivity.class);
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
        code = intent.getStringExtra("strID");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
            Item_Id = null;
        }else {
            Item_Id = code;
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        sp_from = (Spinner) findViewById(R.id.sp_from);
        sp_to = (Spinner) findViewById(R.id.sp_to);
        edt_price = (EditText) findViewById(R.id.edt_price);
        edt_deprt_time = (EditText) findViewById(R.id.edt_deprt_time);
        edt_arriv_time = (EditText) findViewById(R.id.edt_arriv_time);
        edt_bus_no = (EditText) findViewById(R.id.edt_bus_no);
        spn_brand = (Spinner) findViewById(R.id.spn_brand);
        ck_PIT = (CheckBox) findViewById(R.id.ck_PIT);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        img_deprt_time = (ImageView) findViewById(R.id.img_deprt_time);
        img_arriv_time = (ImageView) findViewById(R.id.img_arriv_time);

//         FromList = Item.getAllItem(getApplicationContext(), "WHERE is_active ='1'", database);

        if (operation.equals("Edit")) {
            btn_delete.setVisibility(View.VISIBLE);
            ticket_setup = Ticket_Setup.getTicket_Setup(getApplicationContext(), database, db, "WHERE id = '" + code + "'");
            edt_deprt_time.setText(ticket_setup.get_departure());
            edt_arriv_time.setText(ticket_setup.get_arrival());
            edt_bus_no.setText(ticket_setup.get_bus_number());
            String sale_pri;
            try {
                sale_pri = ticket_setup.get_new_price();

                if (sale_pri.equals("null") || sale_pri.equals("")) {
                    sale_pri = ticket_setup.get_price();
                } else {
                    sale_pri = ticket_setup.get_new_price();
                }

            } catch (Exception e) {
                sale_pri = "0";
            }
            String salePri;
            salePri = Globals.myNumberFormat2Price(Double.parseDouble(sale_pri), decimal_check);
            edt_price.setText(salePri);

            if (ticket_setup.get_is_inclusive_tax().equals("1")) {
                ck_PIT.setChecked(true);
            }

            manufacture = Manufacture.getManufacture(getApplicationContext(), database, db, "WHERE manufacture_code = '" + ticket_setup.get_menufacture_id() + "'");
            String compare_manufacture_name;
            try {
                compare_manufacture_name = manufacture.get_manufacture_name();
            } catch (Exception ex) {
                compare_manufacture_name = "";
            }

            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + ticket_setup.get_tck_from() + "'", database, db);
            String compare_from;
            try {
                compare_from = item.get_item_name();
            } catch (Exception ex) {
                compare_from = "";
            }

            fill_spinner_from(compare_from);

            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + ticket_setup.get_tck_to() + "'", database, db);
            String compare_to;
            try {
                compare_to = item.get_item_name();
            } catch (Exception ex) {
                compare_to = "";
            }

            fill_spinner_to(compare_to);


            fill_spinner_manufacture(compare_manufacture_name);

        } else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
            fill_spinner_manufacture("");
            fill_spinner_from("");
            fill_spinner_to("");
        }

        sp_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adt, View arg1, int pos, long arg3) {
                strFrom = FromList.get(pos).get_item_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        sp_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adt, View arg1, int pos, long arg3) {
                strTo =  FromList.get(pos).get_item_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        spn_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                str_manufacture_code = (String) spn_brand.getItemAtPosition(position).toString();
                Manufacture resultp = arrayList_manufacture.get(position);
                str_manufacture_code = resultp.get_manufacture_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        img_deprt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_deprt_time.getWindowToken(), 0);
                new TimePickerDialog(TicketSetupActivity.this, depart_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        img_arriv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_arriv_time.getWindowToken(), 0);
                new TimePickerDialog(TicketSetupActivity.this, arriv_time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(
                        TicketSetupActivity.this);
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
                                pDialog = new ProgressDialog(TicketSetupActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);

                                            database.beginTransaction();
                                            ticket_setup = Ticket_Setup.getTicket_Setup(getApplicationContext(), database, db, "where id = '" + code + "'");
                                            ticket_setup.set_is_active("0");
                                            long it = ticket_setup.updateTicket_Setup("id=?", new String[]{code}, database);
                                            if (it > 0) {
                                                database.setTransactionSuccessful();
                                                database.endTransaction();
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                        Intent intent_category = new Intent(TicketSetupActivity.this, TicketSetupListActivity.class);
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


                alert = alertDialog.create();
                alert.show();

                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (edt_from.getText().toString().trim().equals("")) {
//                    edt_from.setError("From is required");
//                    edt_from.requestFocus();
//                    return;
//                } else {
//                    strFrom = edt_from.getText().toString().trim();
//                }

//                if (edt_to.getText().toString().trim().equals("")) {
//                    edt_to.setError("To is required");
//                    edt_to.requestFocus();
//                    return;
//                } else {
//                    strTo = edt_to.getText().toString().trim();
//                }

                if (edt_price.getText().toString().trim().equals("")) {
                    edt_price.setError("To is required");
                    edt_price.requestFocus();
                    return;
                } else {
                    strPrice = edt_price.getText().toString().trim();
                }

                if (edt_bus_no.getText().toString().trim().equals("")) {
                    edt_bus_no.setError("Bus no is required");
                    edt_bus_no.requestFocus();
                    return;
                } else {
                    strBusNo = edt_bus_no.getText().toString().trim();
                }

                if (edt_deprt_time.getText().toString().trim().equals("")) {
                    edt_deprt_time.setError("Departure time is required");
                    edt_deprt_time.requestFocus();
                    return;
                } else {
                    strDepartTime = edt_deprt_time.getText().toString().trim();
                }

                if (edt_arriv_time.getText().toString().trim().equals("")) {
                    edt_arriv_time.setError("Departure time is required");
                    edt_arriv_time.requestFocus();
                    return;
                } else {
                    strArrivTime = edt_arriv_time.getText().toString().trim();
                }

                if (edt_price.getText().toString().trim().equals("")) {
                    edt_price.setError("To is required");
                    edt_price.requestFocus();
                    return;
                } else {
                    strPrice = edt_price.getText().toString().trim();
                }




                if (ck_PIT.isChecked()) {

                    strPIT = "1";
                } else {
                    strPIT = "0";
                }

                pDialog = new ProgressDialog(TicketSetupActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Fill_Setup(str_manufacture_code, strFrom, strTo, strPrice, strDepartTime, strArrivTime, strPIT, Item_Id,strBusNo);
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


    private void Fill_Setup(String str_manufacture_code, String strFrom, String strTo, String strPrice, String strDepartTime, String strArrivTime, final String strPIT, String item_Id, String strBusNo) {
        ticket_setup = new Ticket_Setup(getApplicationContext(), item_Id, str_manufacture_code, strFrom, strTo, strPrice, strDepartTime,
                strArrivTime, strPIT, strPrice,strBusNo, "1");

        if (operation.equals("Edit")) {
            database.beginTransaction();
            final long l = ticket_setup.updateTicket_Setup("id=?", new String[]{code}, database);
            if (l > 0) {

                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {

                        Intent intent_category = new Intent(TicketSetupActivity.this, TicketSetupTaxCheckListActivity.class);
                        intent_category.putExtra("strID",code);
                        intent_category.putExtra("PIT", strPIT);
                        intent_category.putExtra("price", ticket_setup.get_price());
                        intent_category.putExtra("operation", operation);
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
        } else {
            database.beginTransaction();
            final long l = ticket_setup.insertTicket_Setup(database);
            if (l > 0) {

                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent_category = new Intent(TicketSetupActivity.this, TicketSetupTaxCheckListActivity.class);
                        intent_category.putExtra("strID", l+"");
                        intent_category.putExtra("PIT", strPIT);
                        intent_category.putExtra("price", ticket_setup.get_price());
                        intent_category.putExtra("operation", operation);
                        startActivity(intent_category);
                        //finish();

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

    }

    final TimePickerDialog.OnTimeSetListener depart_time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            SetDepartTime();
        }
    };

    final TimePickerDialog.OnTimeSetListener arriv_time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            SetArrivTime();
        }
    };

    private void SetDepartTime() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String time;
        try {
            time = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            time = "";
        }
        edt_deprt_time.setText(time);
    }

    private void SetArrivTime() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String time;
        try {
            time = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            time = "";
        }
        edt_arriv_time.setText(time);
    }

    private void fill_spinner_manufacture(String compare_manufacture_name) {
        arrayList_manufacture = Manufacture.getAllManufactureSpinner_All(getApplicationContext(), " WHERE is_active ='1' Order By manufacture_name asc");
        if (arrayList_manufacture.size()>0) {
            ManufactureSpiinerAdapter manufactureSpiinerAdapter = new ManufactureSpiinerAdapter(getApplicationContext(), arrayList_manufacture);
            spn_brand.setAdapter(manufactureSpiinerAdapter);

            if (!compare_manufacture_name.equals("")) {
                for (int i = 0; i < manufactureSpiinerAdapter.getCount(); i++) {
                    int h = (int) spn_brand.getAdapter().getItemId(i);
                    String iname = arrayList_manufacture.get(h).get_manufacture_name();
                    if (compare_manufacture_name.equals(iname)) {
                        spn_brand.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void fill_spinner_to(String to) {
        FromList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' Order By item_name asc", database);
        if (FromList.size()>0) {
            FromAdapter fromAdapter = new FromAdapter(getApplicationContext(), FromList);
            sp_to.setAdapter(fromAdapter);

            if (!to.equals("")) {
                for (int i = 0; i < fromAdapter.getCount(); i++) {
//                int h = (int) sp_to.getAdapter().getItemId(i);
                    String iname = FromList.get(i).get_item_name();
                    if (to.equals(iname)) {
                        sp_to.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void fill_spinner_from(String from) {
        FromList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' Order By item_name asc", database);
        if (FromList.size()>0) {
            FromAdapter fromAdapter = new FromAdapter(getApplicationContext(), FromList);
            sp_from.setAdapter(fromAdapter);

            if (!from.equals("")) {
                for (int i = 0; i < fromAdapter.getCount(); i++) {
//                int h = (int) sp_from.getAdapter().getItemId(i);
                    String iname = FromList.get(i).get_item_name();
                    if (from.equals(iname)) {
                        sp_from.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
}
