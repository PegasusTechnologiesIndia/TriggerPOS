package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phomellolitepos.Adapter.CountryAdapter;
import org.phomellolitepos.Adapter.ZoneAdapter;
import org.phomellolitepos.CheckBoxClass.BusinessGroupCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Zone;

import static org.phomellolitepos.R.id.edt_pd_amt;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ContactActivity extends AppCompatActivity {
    TextInputLayout edt_layout_contact_code, edt_layout_contact_name, edt_layout_contact_gender, edt_layout_dob, edt_layout_company_name,
            edt_layout_contact_1, edt_layout_contact_2, edt_layout_email_1, edt_layout_email_2, edt_layout_description, edt_layout_address, edt_layout_gstn;
    EditText edt_contact_code, edt_contact_name, edt_contact_gender, edt_dob, edt_company_name, edt_contact_1, edt_contact_2,
            edt_email_1, edt_email_2, edt_description, edt_address, edt_gstn;
    Spinner spinner_title, spinner_gender, spn_country, spn_zone;
    Button btn_next, btn_contact_delete;
    ArrayList<Bussiness_Group> arrayList;
    ArrayList<BusinessGroupCheck> list = new ArrayList<BusinessGroupCheck>();
    String str_title, str_gender, operation, code, contact_id, date;
    Contact contact;
    Database db;
    SQLiteDatabase database;
    ArrayList<Country> arrayCList;
    ArrayList<Zone> arrayZList;
    Country country;
    Zone zone;
    String strSelectedZoneCode, strSelectedCountryCode;
    Calendar myCalendar;
    Address address_class;
    Address_Lookup address_lookup;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String contact_code = "", contact_name = "", dob = "", gstn = "", company_name = "", contact_1 = "", contact_2 = "", description = "", email_1 = "", email_2 = "", addresss = "";
    String strCTCode = "";
    String[] gender = {};
    String[] title = {};
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    ImageView img_expand;
    LinearLayout ll_othr_info;
    boolean flag=false;
    MenuItem menuItem;
    Contact_Bussiness_Group  contact_bussiness_group;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    String serial_no, android_id, myKey, device_id,imei_no;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        title = getResources().getStringArray(R.array.title);

        gender = getResources().getStringArray(R.array.gender);

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

                pDialog = new ProgressDialog(ContactActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(ContactActivity.this, ContactListActivity.class);
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
        getSupportActionBar().setTitle(R.string.Contact);
        myCalendar = Calendar.getInstance();
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        code = intent.getStringExtra("contact_code");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
            contact_id = null;
        }
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        device_id = telephonyManager.getDeviceId();
        imei_no=telephonyManager.getImei();
        edt_layout_contact_code = (TextInputLayout) findViewById(R.id.edt_layout_contact_code);
        edt_layout_contact_name = (TextInputLayout) findViewById(R.id.edt_layout_contact_name);
        edt_layout_dob = (TextInputLayout) findViewById(R.id.edt_layout_dob);
        edt_layout_company_name = (TextInputLayout) findViewById(R.id.edt_layout_company_name);
        edt_layout_contact_1 = (TextInputLayout) findViewById(R.id.edt_layout_contact_1);
        edt_layout_contact_2 = (TextInputLayout) findViewById(R.id.edt_layout_contact_2);
        edt_layout_email_1 = (TextInputLayout) findViewById(R.id.edt_layout_email_1);
        edt_layout_email_2 = (TextInputLayout) findViewById(R.id.edt_layout_email_2);
        edt_layout_description = (TextInputLayout) findViewById(R.id.edt_layout_description);
        edt_layout_address = (TextInputLayout) findViewById(R.id.edt_layout_address);
        edt_layout_gstn = (TextInputLayout) findViewById(R.id.edt_layout_gstn);

        edt_contact_code = (EditText) findViewById(R.id.edt_contact_code);
        edt_contact_name = (EditText) findViewById(R.id.edt_contact_name);

        edt_dob = (EditText) findViewById(R.id.edt_dob);
        edt_company_name = (EditText) findViewById(R.id.edt_company_name);
        edt_contact_1 = (EditText) findViewById(R.id.edt_contact_1);
        edt_contact_2 = (EditText) findViewById(R.id.edt_contact_2);
        edt_email_1 = (EditText) findViewById(R.id.edt_email_1);
        edt_email_2 = (EditText) findViewById(R.id.edt_email_2);
        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_gstn = (EditText) findViewById(R.id.edt_gstn);

        spinner_title = (Spinner) findViewById(R.id.spinner_title);
        spinner_gender = (Spinner) findViewById(R.id.spinner__gender);
        spn_country = (Spinner) findViewById(R.id.spn_country);
        spn_zone = (Spinner) findViewById(R.id.spn_zone);
        btn_contact_delete = (Button) findViewById(R.id.btn_contact_delete);
        btn_next = (Button) findViewById(R.id.btn_next);
        img_expand = (ImageView) findViewById(R.id.img_expand);
        ll_othr_info = (LinearLayout) findViewById(R.id.ll_othr_info);
        btn_next.setVisibility(View.GONE);
        if (!flag){
            flag=true;
            img_expand.setBackground(getResources().getDrawable(R.drawable.arrow_up));
            ll_othr_info.setVisibility(View.GONE);
        }
        img_expand.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                if (!flag){
                    flag=true;
                    img_expand.setBackground(getResources().getDrawable(R.drawable.arrow_up));
                    ll_othr_info.setVisibility(View.GONE);

                }else {
                    flag=false;
                    img_expand.setBackground(getResources().getDrawable(R.drawable.arrow_down));
                    ll_othr_info.setVisibility(View.VISIBLE);
                }
            }
        });


        final DatePickerDialog.OnDateSetListener date_dob = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        edt_dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    new DatePickerDialog(ContactActivity.this, date_dob, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        spn_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Zone resultp = arrayZList.get(i);
                    strSelectedZoneCode = resultp.get_zone_id();
                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spn_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Country resultp = arrayCList.get(i);
                    strSelectedCountryCode = resultp.get_country_id();

                    get_zone(strSelectedCountryCode);

                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (operation.equals("Edit")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            btn_contact_delete.setVisibility(View.VISIBLE);
            contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code = '" + code + "'");
            String compare_title = contact.get_title();
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, title);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner_title.setAdapter(dataAdapter);
//            if (!compare_title.equals(null)) {
//                int spinnerPosition = dataAdapter.getPosition(compare_title);
//                spinner_title.setSelection(spinnerPosition);
//            }

            String compare_gender = contact.get_gender();
            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);
            dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_gender.setAdapter(dataAdapter1);
            try {
                if (!compare_gender.equals(null)) {
                    int spinnerPosition = dataAdapter1.getPosition(compare_gender);
                    spinner_gender.setSelection(spinnerPosition);
                }
            }catch (Exception ex){}


            String cntryid = contact.get_country_id();
            country = Country.getCountry(getApplicationContext(), "WHERE country_id ='" + cntryid + "'", database);
            if (country == null) {
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                country = Country.getCountry(getApplicationContext(), "WHERE country_id='" + lite_pos_registration.getCountry_Id() + "'", database);
                get_country(country.get_name(), lite_pos_registration.getCountry_Id());
            } else {
                get_country(country.get_name(), cntryid);
            }

            String zoneid = contact.get_zone_id();
            zone = Zone.getZone(getApplicationContext(), "WHERE zone_id='" + zoneid + "'");
            if (zone == null) {
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                zone = Zone.getZone(getApplicationContext(), "WHERE zone_id='" + lite_pos_registration.getZone_Id() + "'");
                get_zone(lite_pos_registration.getCountry_Id());
            } else {
                get_zone(cntryid);
            }


            edt_contact_code.setText(contact.get_contact_code());
            edt_contact_name.setText(contact.get_name());
            edt_dob.setText(contact.get_dob());
            edt_gstn.setText(contact.get_gstin());
            edt_company_name.setText(contact.get_company_name());
            edt_contact_1.setText(contact.get_contact_1());
            String contactCk = contact.get_contact_2();
            if (contactCk.equals("0")) {
                edt_contact_2.setText("");
            } else {
                edt_contact_2.setText(contact.get_contact_2());
            }
            edt_email_1.setText(contact.get_email_1());
            String emailCk = contact.get_email_2();
            if (emailCk.equals("0")) {
                edt_email_2.setText("");
            } else {
                edt_email_2.setText(contact.get_email_2());
            }
            edt_description.setText(contact.get_description());
            edt_address.setText(contact.get_address());
            contact_id = contact.get_contact_id();
        } else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
//            fill_title_spinner();

            fill_gender_spinner();
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            country = Country.getCountry(getApplicationContext(), "WHERE country_id='" + lite_pos_registration.getCountry_Id() + "'", database);
            get_country(country.get_name(), lite_pos_registration.getCountry_Id());
            zone = Zone.getZone(getApplicationContext(), "WHERE zone_id='" + lite_pos_registration.getZone_Id() + "'");
            get_zone(lite_pos_registration.getCountry_Id());
        }


        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        spinner_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    str_title = String.valueOf(spinner_title.getItemAtPosition(i));
                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    str_gender = String.valueOf(spinner_gender.getItemAtPosition(i));
                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_contact_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(
                        ContactActivity.this);
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
                                pDialog = new ProgressDialog(ContactActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);

                                            database.beginTransaction();
                                            long l = Contact_Bussiness_Group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code=?", new String[]{code});
                                            if (l > 0) {
                                                contact.set_is_active("0");
                                                long ct = contact.updateContact("contact_code=?", new String[]{code}, database);
                                                if (ct > 0) {
                                                    database.setTransactionSuccessful();
                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(ContactActivity.this, ContactListActivity.class);
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
                                // Database transaction begins here

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
            public void onClick(View view) {


            }
        });
    }

    private void fill_gender_spinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(dataAdapter);
    }

    private void fill_title_spinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, title);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_title.setAdapter(dataAdapter);
    }

    private void Fill_Item(String contact_id, final String contact_code, String contact_name, String str_gender, String dob, String company_name, String contact_1, String contact_2, String email_1, String email_2, String description, String address, String date, String gstn, String strSelectedCountryCode, String strSelectedZoneCode) {
        String modified_by = Globals.user;

        if (operation.equals("Edit")) {
            contact = new Contact(getApplicationContext(), contact_id, liccustomerid, contact_code, str_title,
                    contact_name, str_gender, dob, company_name, description, contact_1, contact_2, email_1, email_2, "1", modified_by, "N", address, date, "0", gstn, strSelectedCountryCode, strSelectedZoneCode);
            database.beginTransaction();
            long l = contact.updateContact("contact_code=?", new String[]{code}, database);
            if (l > 0) {

                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), contact_code, "BGC-1");
                long a1 = contact_bussiness_group.insertContact_Bussiness_Group(database);
                if (a1 > 0) {
                }

                String add_id, add_code;
                address_lookup = Address_Lookup.getAddress_Lookup(getApplicationContext(), "WHERE refrence_code = '" + code + "'", database);

                if (address_lookup == null) {
                    add_code = "";
                } else {
                    add_code = address_lookup.get_address_code();
                }
                address_class = Address.getAddress(getApplicationContext(), "WHERE address_code = '" + add_code + "'", database);

                if (address_class == null) {
                    add_id = "null";
                } else {
                    add_id = address_class.get_address_id();
                }
                long c=0;
                if (!add_code.equals("")) {
                    address_class = new Address(getApplicationContext(), add_id, liccustomerid, contact_code, "AC-1",
                            "0", address, "0", "0", "0", "0", "0", "1", modified_by, date, "N");

                    c = address_class.updateAddress("address_code=?", new String[]{add_code}, database);
                }
                else
                {
                    c=1;
                }
                if (c > 0) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Intent intent_category = new Intent(ContactActivity.this, ContactListActivity.class);
                            intent_category.putExtra("contact_code", contact_code);
                            intent_category.putExtra("operation", operation);
                            startActivity(intent_category);
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
                pDialog.dismiss();
                database.endTransaction();

            }
        } else {
            contact = new Contact(getApplicationContext(), contact_id, liccustomerid, contact_code, str_title,
                    contact_name, str_gender, dob, company_name, description, contact_1, contact_2, email_1, email_2, "1", modified_by, "N", address, date, "0", gstn, strSelectedCountryCode, strSelectedZoneCode);
            database.beginTransaction();
            long l = contact.insertContact(database);
            if (l > 0) {

                contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), contact_code, "BGC-1");
                long a1 = contact_bussiness_group.insertContact_Bussiness_Group(database);
                if (a1 > 0) {
                }
                address_class = new Address(getApplicationContext(), null, liccustomerid, contact_code, "AC-1",
                        "0", address, "0", "0", "0", "0", "0", "1", modified_by, date, "N");
                long a = address_class.insertAddress(database);
                if (a > 0) {
                    address_lookup = new Address_Lookup(getApplicationContext(), null, liccustomerid, contact_code, "1",
                            contact_code, "N");
                    long b = address_lookup.insertAddress_Lookup(database);

                    if (b > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent intent_category = new Intent(ContactActivity.this, ContactListActivity.class);
                                intent_category.putExtra("contact_code", contact_code);
                                intent_category.putExtra("operation", operation);
                                startActivity(intent_category);
                                finish();
                            }
                        });

                    } else {
                        pDialog.dismiss();
                        database.endTransaction();
                    }
                }


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

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String str_dob;
        try {
            str_dob = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            str_dob = "";
        }
        edt_dob.setText(str_dob);
    }

    @Override
    public void onBackPressed() {

        pDialog = new ProgressDialog(ContactActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);

                    Intent intent = new Intent(ContactActivity.this, ContactListActivity.class);
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private String check_online_mode() {
        String con = "";
        if (isNetworkStatusAvialable(getApplicationContext())) {
            String ck_projct_type = "";


            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

            try {
                ck_projct_type = lite_pos_registration.getproject_id();
            } catch (Exception e) {
                ck_projct_type = "";
            }

            if (ck_projct_type.equals("cloud")) {
                con = Contact.sendOnServer(getApplicationContext(), database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by from contact where is_push='N'",liccustomerid,serial_no,android_id,myKey);
            }

        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        return con;
    }

    private void get_country(String name, String id) {
        arrayCList = country.getAllCountry(getApplicationContext(), "");
        CountryAdapter countryAdapter = new CountryAdapter(getApplicationContext(), arrayCList);
        spn_country.setAdapter(countryAdapter);
        if (!name.equals("")) {
            for (int i = 0; i < countryAdapter.getCount(); i++) {
                String iname = arrayCList.get(i).get_name();
                if (name.equals(iname)) {
                    spn_country.setSelection(i);
                    break;
                }
            }
        }
    }

    private void get_zone(String strCountryCode) {
        arrayZList = zone.getAllZone(getApplicationContext(), "WHERE country_id = '" + strCountryCode + "'");
        ZoneAdapter countryAdapter1 = new ZoneAdapter(getApplicationContext(), arrayZList);
        spn_zone.setAdapter(countryAdapter1);
        if (!zone.get_name().equals("")) {
            for (int i = 0; i < countryAdapter1.getCount(); i++) {
                String iname = arrayZList.get(i).get_name();
                if (zone.get_name().equals(iname)) {
                    spn_zone.setSelection(i);
                    break;
                }
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(ContactActivity.this);
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

        if (edt_contact_code.getText().toString().trim().equals("")) {
            // Auto-generate code here for contact code
            Contact objCT1 = Contact.getContact(getApplicationContext(), database, db, " Where contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%'  order By contact_id Desc LIMIT 1");
            if (objCT1 == null) {
                strCTCode = Globals.objLPD.getDevice_Symbol() + "-"+"CT-" + 1;
            } else {
                strCTCode = Globals.objLPD.getDevice_Symbol() + "-"+"CT-" + (Integer.parseInt(objCT1.get_contact_code().toString().replace(Globals.objLPD.getDevice_Symbol() + "-CT-","")) + 1);
            }
        } else {
//            if (edt_contact_code.getText().toString().contains("")) {
//                edt_contact_code.setError(getResources().getString(R.string.Cant_Enter_Space));
//                edt_contact_code.requestFocus();
//                return;
//            } else {
                strCTCode = edt_contact_code.getText().toString().trim();
//            }
        }


        if (edt_contact_name.getText().toString().equals("")) {
            edt_contact_name.setError(getString(R.string.Contact_name_is_required));
            edt_contact_name.requestFocus();
            return;
        } else {
            contact_name = edt_contact_name.getText().toString();
        }

        if (edt_contact_1.getText().toString().equals("")) {
            edt_contact_1.setError(getString(R.string.Contact_is_required));
            edt_contact_1.requestFocus();
            return;
        } else {
            contact_1 = edt_contact_1.getText().toString().trim();
        }

        dob = edt_dob.getText().toString().trim();
        company_name = edt_company_name.getText().toString().trim();
        gstn = edt_gstn.getText().toString().trim();


        if (edt_contact_1.getText().toString().equals("")) {
            contact_1 = "";
        } else {
            contact_1 = edt_contact_1.getText().toString();
        }

        if (edt_contact_2.getText().toString().equals("")) {
            contact_2 = "";
        } else {
            contact_2 = edt_contact_2.getText().toString();
        }
        email_1 = edt_email_1.getText().toString().trim();
        if (edt_email_1.getText().toString().trim().equals("")){
        }else {
            final String email1 = edt_email_1.getText().toString();
            if (!isValidEmail(email1)) {
                edt_email_1.setError(getString(R.string.Invalid_Email));
                edt_email_1.requestFocus();
                return;
            }
        }
        description = edt_description.getText().toString();
        addresss = edt_address.getText().toString();

        if (str_gender==null){
            str_gender = "Male";
        }

        if (strSelectedCountryCode==null){
            strSelectedCountryCode = lite_pos_registration.getCountry_Id();
        }

        if (strSelectedZoneCode==null){
            strSelectedZoneCode = lite_pos_registration.getZone_Id();
        }

        pDialog = new ProgressDialog(ContactActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Fill_Item(contact_id, strCTCode, contact_name, str_gender, dob, company_name, contact_1, contact_2,
                            email_1, email_2, description, addresss, date, gstn, strSelectedCountryCode, strSelectedZoneCode);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        timerThread.start();
    }


}
