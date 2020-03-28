package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.ReservationTableAdapter;
import org.phomellolitepos.Adapter.ReservationUserListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Reservation;
import org.phomellolitepos.database.Reservation_Detail;
import org.phomellolitepos.database.Table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class ReservationActivity extends AppCompatActivity {
    EditText edt_time, edt_date;
    Button btn_next, btn_item_delete, spn_customer;
    ImageView img_time, img_date;
    Spinner spn_table, spn_user;
    Calendar myCalendar;
    ProgressDialog pDialog;
    String date, operation, id, Id, strDate, strTime, strCustomerCode, strUserCode, strTableCode, strSelectedCategory = "";
    ;
    Reservation reservation;
    Database db;
    SQLiteDatabase database;
    ArrayList<User> arrayList_user;
    ArrayList<Table> arrayList_table;
    ArrayList<Contact> contact_ArrayList;
    DialogContactMainListAdapter dialogContactMainListAdapter;
    Dialog listDialog1;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reservation");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
            Id = null;
        }

        settings = Settings.getSettings(getApplicationContext(), database, "");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int idP = pref.getInt("id", 0);
        if (idP == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ReservationActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code="";
                            Intent intent = new Intent(ReservationActivity.this, ReservationListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                };
                timerThread.start();

            }
        });

        edt_time = (EditText) findViewById(R.id.edt_time);
        edt_date = (EditText) findViewById(R.id.edt_date);
        img_time = (ImageView) findViewById(R.id.img_time);
        img_date = (ImageView) findViewById(R.id.img_date);
        spn_table = (Spinner) findViewById(R.id.spn_table);
        btn_next = (Button) findViewById(R.id.btn_next);
        spn_customer = (Button) findViewById(R.id.spn_customer);
        btn_item_delete = (Button) findViewById(R.id.btn_item_delete);
        spn_user = (Spinner) findViewById(R.id.spn_user);

        myCalendar = Calendar.getInstance();

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

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                SetTime();
            }
        };

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(img_date.getWindowToken(), 0);
                new DatePickerDialog(ReservationActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        img_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_time.getWindowToken(), 0);
                new TimePickerDialog(ReservationActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        spn_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Table resultp = arrayList_table.get(position);
                strTableCode = resultp.get_table_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User resultp = arrayList_user.get(position);
                strUserCode = resultp.get_user_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE is_active ='1'");
                if (contact == null) {
                    Toast.makeText(getApplicationContext(), R.string.No_Contact_Found, Toast.LENGTH_SHORT).show();
                } else {
                    showdialogContact();
                }
            }
        });

        if (operation.equals("Edit")) {
            reservation = Reservation.getReservation(getApplicationContext(), database, db, " WHERE _id = '" + id + "'");
            btn_item_delete.setVisibility(View.VISIBLE);
            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + reservation.get_customer_code() + "'");
            if (contact != null) {
                spn_customer.setText(contact.get_name());
            }


            if (reservation.get_date_time().equals("")) {

            } else {
                String[] ab = reservation.get_date_time().split("#");
                edt_time.setText(ab[1]);
                edt_date.setText(ab[0]);
            }

            strCustomerCode = reservation.get_customer_code();
            User user = User.getUser(getApplicationContext(), "WHERE user_code='" + reservation.get_user_code() + "'", database);
            if (user == null) {
                fill_spinner_user("");
            } else {
                fill_spinner_user(user.get_name());
            }

            Table table = Table.getTable(getApplicationContext(), database, db, " WHERE table_code='" + reservation.get_table_code() + "'");
            if (table == null) {
                fill_spinner_table("");
            } else {
                fill_spinner_table(table.get_table_name());
            }


        } else {

            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
            fill_spinner_user("");
            fill_spinner_table("");
        }

        btn_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ReservationActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            database.beginTransaction();
                            long l = Reservation.deleteReservation(getApplicationContext(), "_id=?", new String[]{id}, database);
                            if (l > 0) {
                                long it = Reservation_Detail.deleteReservation_Detail(getApplicationContext(), "_id=?", new String[]{id}, database);
                                if (it > 0) {
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    pDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                            Intent intent_category = new Intent(ReservationActivity.this, ReservationListActivity.class);
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

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strTime = edt_time.getText().toString().trim();
                if (edt_time.getText().toString().equals("")) {
                    edt_time.setError("Time is required");
                    edt_time.requestFocus();
                    return;
                }

                strDate = edt_date.getText().toString().trim();
                if (edt_date.getText().toString().equals("")) {
                    edt_date.setError("Date is required");
                    edt_date.requestFocus();
                    return;
                }

                if (spn_customer.getText().equals("Select Customer")) {
                    Toast.makeText(getApplicationContext(), "Please select customer", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog = new ProgressDialog(ReservationActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();

                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);

                                Fill_Reservation(id, strDate, strTime, strCustomerCode, strUserCode, strTableCode);

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

    }


    private void Fill_Reservation(String Id, String strDate, String strTime, String strCustomerCode, String strUserCode, String strTableCode) {

        String dateTime = strDate + "#" + strTime;
        if (Globals.strResvContact_Code.equals("")) {
            Globals.strResvContact_Code = strCustomerCode;
        } else {
            strCustomerCode = Globals.strResvContact_Code;
        }
        reservation = new Reservation(getApplicationContext(), Id, dateTime, strCustomerCode, strUserCode, strTableCode);
        if (operation.equals("Edit")) {

            database.beginTransaction();
            long l = reservation.updateReservation("_id =?", new String[]{Id}, database);
            if (l > 0) {
                try {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                    pDialog.dismiss();
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code="";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Intent intent_category = new Intent(ReservationActivity.this, ReservationItemCheckListActivity.class);
                            intent_category.putExtra("id", reservation.get_id());
                            intent_category.putExtra("operation", operation);
                            startActivity(intent_category);
                            finish();
                        }
                    });

                } catch (Exception ex) {
                    database.endTransaction();
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
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
            final long l = reservation.insertReservation(database);
            if (l > 0) {

                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                Globals.strContact_Code = "";
                Globals.strResvContact_Code="";
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent_category = new Intent(ReservationActivity.this, ReservationItemCheckListActivity.class);
                        intent_category.putExtra("id", l + "");
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
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }

    }

    private void SetTime() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String time;
        try {
            time = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            time = "";
        }
        edt_time.setText(time);

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

    private void fill_spinner_user(String compare_manufacture_name) {
        arrayList_user = User.getAllUser(getApplicationContext(), " WHERE is_active ='1' Order By name asc", database, db);
        ReservationUserListAdapter userListAdapter = new ReservationUserListAdapter(getApplicationContext(), arrayList_user);
        spn_user.setAdapter(userListAdapter);

        if (!compare_manufacture_name.equals("")) {
            for (int i = 0; i < userListAdapter.getCount(); i++) {
                int h = (int) spn_user.getAdapter().getItemId(i);
//                String iname = arrayList_user.get(h).get_name();
                String iname = arrayList_user.get(i).get_name();
                if (compare_manufacture_name.equals(iname)) {
                    spn_user.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner_table(String compare_item_group_name) {
//        Dialog dialog = null;
        arrayList_table = Table.getAllTable(getApplicationContext(), "", database);
        ReservationTableAdapter dialogTableMainListAdapter = new ReservationTableAdapter(getApplicationContext(), arrayList_table);
        spn_table.setAdapter(dialogTableMainListAdapter);

        if (!compare_item_group_name.equals("")) {
            for (int i = 0; i < dialogTableMainListAdapter.getCount(); i++) {
//                int h = (int) spn_table.getAdapter().getItemId(i);
                String iname = arrayList_table.get(i).get_table_name();

                if (compare_item_group_name.equals(iname)) {
                    spn_table.setSelection(i);
                    break;
                }
            }
        }
    }


    private void showdialogContact() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog1 = new Dialog(this);
        listDialog1.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);

        final ListView list11 = (ListView) listDialog1.findViewById(R.id.lv_custom_ortype);
        final Spinner spn_filter = (Spinner) listDialog1.findViewById(R.id.spn_filter);
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        TextView txt_reset = (TextView) listDialog1.findViewById(R.id.txt_reset);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        img_brs.setVisibility(View.GONE);
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        final ArrayList<Bussiness_Group> arrayBusinessGroup = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1' Order By name asc");
        HintAdapter<Bussiness_Group> hintAdapter = new HintAdapter<Bussiness_Group>(
                this,
                R.layout.item_manufacture_spinner_item,
                "Select Category",
                arrayBusinessGroup) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                Bussiness_Group resultp = arrayBusinessGroup.get(position);
                final String name = resultp.get_name();
                final String code = resultp.get_business_group_code();

                // Here you inflate the layout and set the value of your widgets
                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.manufacture_name)).setText(name);
                ((TextView) view.findViewById(R.id.manufacture_code)).setText(code);
                return view;
            }
        };


        HintSpinner<Bussiness_Group> hintSpinner = new HintSpinner<Bussiness_Group>(
                spn_filter,
                hintAdapter,
                new HintSpinner.Callback<Bussiness_Group>() {
                    @Override
                    public void onItemSelected(int position, Bussiness_Group itemAtPosition) {
                        Bussiness_Group resultp = arrayBusinessGroup.get(position);
                        strSelectedCategory = resultp.get_business_group_code();

                        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                    }
                });
        hintSpinner.init();

        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog1.dismiss();
                showdialogContact();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, "");
            }
        });

        srch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);

            }
        });

    }

    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "where business_group_code='" + str + "'";
        }

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogContactMainListAdapter = new DialogContactMainListAdapter(ReservationActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);
    }

    public void setCustomer(String name) {
        spn_customer.setText(name);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ReservationActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code="";
                    Intent intent = new Intent(ReservationActivity.this, ReservationListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        };
        timerThread.start();
    }
}
