package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.phomellolitepos.Adapter.ClassAdapter;
import org.phomellolitepos.Adapter.FromAdapter;
import org.phomellolitepos.Adapter.ManufactureSpiinerAdapter;
import org.phomellolitepos.TicketSolution.TicketListActivity;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketingActivity extends AppCompatActivity {
    Spinner sp_from, sp_to, sp_class, sp_brand;
    EditText edt_date;
    Button get, btn_logout, btn_report;
    ImageView img_date;
    private ArrayList<Item> FromList = new ArrayList<Item>();
    private ArrayList<Item_Group> ClassList = new ArrayList<Item_Group>();
    Manufacture manufacture;
    ArrayList<Manufacture> arrayList_manufacture;
    String strFrom, strTo, str_brand_code, strcateoryCode, strDay;
    Database db;
    SQLiteDatabase database;
    Calendar myCalendar;
    ProgressDialog pDialog;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticteting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ticketing");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        settings = Settings.getSettings(getApplicationContext(), database, "");
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
                pDialog = new ProgressDialog(TicketingActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Globals.From = "";
                Globals.To = "";
                Globals.TickCat = "";
                Globals.TicBrand = "";
                Globals.TicDay = "";
                Globals.TicDate = "";
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(TicketingActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(TicketingActivity.this, Main2Activity.class);
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
        sp_from = (Spinner) findViewById(R.id.sp_from);
        sp_to = (Spinner) findViewById(R.id.sp_to);
        get = (Button) findViewById(R.id.get);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_report = (Button) findViewById(R.id.btn_report);
        sp_class = (Spinner) findViewById(R.id.sp_class);
        sp_brand = (Spinner) findViewById(R.id.sp_brand);
        img_date = (ImageView) findViewById(R.id.img_edt_date);
        edt_date = (EditText) findViewById(R.id.edt_date);

        edt_date.setText(Globals.TicDate);
        fill_spinner_manufacture(Globals.TicBrand);
        fill_spinner_from(Globals.From);
        fill_spinner_to(Globals.To);
        fill_spinner_category(Globals.TickCat);


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
                strTo = FromList.get(pos).get_item_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                str_brand_code = (String) sp_brand.getItemAtPosition(position).toString();
                Manufacture resultp = arrayList_manufacture.get(position);
                str_brand_code = resultp.get_manufacture_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sp_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adt, View arg1, int pos, long arg3) {
                strcateoryCode = ClassList.get(pos).get_item_group_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketingActivity.this, ReportActivity.class);
                Globals.rtick = "Ticket";
                startActivity(intent);
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String strDay = getDayFromDateString(edt_date.getText().toString(), "yyyy-MM-dd");
                    Globals.From = strFrom;
                    Globals.To = strTo;
                    Globals.TickCat = strcateoryCode;
                    Globals.TicBrand = str_brand_code;
                    Globals.TicDay = strDay;
                    Globals.TicDate = edt_date.getText().toString();
                    Intent intent = new Intent(TicketingActivity.this, TicketListActivity.class);
//                    intent.putExtra("from", strFrom);
//                    intent.putExtra("to", strTo);
//                    intent.putExtra("category", strcateoryCode);
//                    intent.putExtra("brand", str_brand_code);
//                    intent.putExtra("days", strDay);
                    startActivity(intent);
                } catch (Exception ex) {
                }
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(img_date.getWindowToken(), 0);
                new DatePickerDialog(TicketingActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        myCalendar = Calendar.getInstance();

    }

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

    private void fill_spinner_to(String code) {
        String to = "";
        try {
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code='" + code + "'", database, db);
            to = item.get_item_name();
        } catch (Exception ex) {
        }
        FromList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' Order By item_name asc", database);
        FromAdapter fromAdapter = new FromAdapter(getApplicationContext(), FromList);
        sp_to.setAdapter(fromAdapter);
        if (!to.equals("")) {
            for (int i = 0; i < fromAdapter.getCount(); i++) {
                String iname = FromList.get(i).get_item_name();
                if (to.equals(iname)) {
                    sp_to.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner_from(String code) {
        String from = "";
        try {
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code='" + code + "'", database, db);
            from = item.get_item_name();
        } catch (Exception ex) {
        }
        FromList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' Order By item_name asc", database);
        FromAdapter fromAdapter = new FromAdapter(getApplicationContext(), FromList);
        sp_from.setAdapter(fromAdapter);
        if (!from.equals("")) {
            for (int i = 0; i < fromAdapter.getCount(); i++) {
                String iname = FromList.get(i).get_item_name();
                if (from.equals(iname)) {
                    sp_from.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner_category(String code) {
        String cate = "";

        try {
            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
            cate = item_group.get_item_group_name();
        } catch (Exception ex) {
        }


        ClassList = Item_Group.getAllItem_Group(getApplicationContext(), " WHERE is_active ='1' Order By item_group_name asc", database, db);
        ClassAdapter classAdapter = new ClassAdapter(getApplicationContext(), ClassList);
        sp_class.setAdapter(classAdapter);

        if (!cate.equals("")) {
            for (int i = 0; i < classAdapter.getCount(); i++) {
//                int h = (int) sp_from.getAdapter().getItemId(i);
                String iname = ClassList.get(i).get_item_group_name();
                if (cate.equals(iname)) {
                    sp_class.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner_manufacture(String code) {
        String compare_manufacture_name = "";
        try {
            manufacture = Manufacture.getManufacture(getApplicationContext(), database, db, "WHERE manufacture_code='" + code + "'");
            compare_manufacture_name = manufacture.get_manufacture_name();
        } catch (Exception ex) {
        }
        arrayList_manufacture = Manufacture.getAllManufactureSpinner_All(getApplicationContext(), " WHERE is_active ='1' Order By manufacture_name asc");
        ManufactureSpiinerAdapter manufactureSpiinerAdapter = new ManufactureSpiinerAdapter(getApplicationContext(), arrayList_manufacture);
        sp_brand.setAdapter(manufactureSpiinerAdapter);

        if (!compare_manufacture_name.equals("")) {
            for (int i = 0; i < manufactureSpiinerAdapter.getCount(); i++) {
                int h = (int) sp_brand.getAdapter().getItemId(i);
                String iname = arrayList_manufacture.get(h).get_manufacture_name();
                if (compare_manufacture_name.equals(iname)) {
                    sp_brand.setSelection(i);
                    break;
                }
            }
        }
    }

    public static String getDayFromDateString(String stringDate, String dateTimeFormat) {
        String[] daysArray = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String day = "";

        int dayOfWeek = 0;
        //dateTimeFormat = yyyy-MM-dd HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        Date date;
        try {
            date = formatter.parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (dayOfWeek < 0) {
                dayOfWeek += 7;
            }
            day = daysArray[dayOfWeek];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TicketingActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Globals.From = "";
        Globals.To = "";
        Globals.TickCat = "";
        Globals.TicBrand = "";
        Globals.TicDay = "";
        Globals.TicDate = "";
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(TicketingActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(TicketingActivity.this, Main2Activity.class);
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
