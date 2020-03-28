package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.ReportCustomerAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CusReturnHeaderActivity extends AppCompatActivity {
    EditText edt_voucher_no, edt_date, edt_remarks;
    Spinner spn_pay_method;
    ImageView img_date;
    Button btn_next;
    String operation, voucher_no, strVoucherNo;
    Database db;
    SQLiteDatabase database;
    Returns returns;
    ProgressDialog pDialog;
    Calendar myCalendar;
    Spinner spn_cus;
    ArrayList<Contact> contact_ArrayList;
    ArrayList<Payment> paymentArrayList;
    String cusCode;
    Contact contact;
    Settings settings;
    String strPayMethod,PayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_header);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                pDialog = new ProgressDialog(CusReturnHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(CusReturnHeaderActivity.this, CustomerReturnListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
                            startActivity(intent);

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
        getSupportActionBar().setTitle(R.string.Return);
        operation = intent.getStringExtra("operation");
        voucher_no = intent.getStringExtra("voucher_no");

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        myCalendar = Calendar.getInstance();
        edt_voucher_no = (EditText) findViewById(R.id.edt_voucher_no);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_remarks = (EditText) findViewById(R.id.edt_remarks);
        spn_cus = (Spinner) findViewById(R.id.spn_cus);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        img_date = (ImageView) findViewById(R.id.img_date);
        btn_next = (Button) findViewById(R.id.btn_next);
        settings = Settings.getSettings(getApplicationContext(), database, "");

        spn_pay_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Payment resultp = paymentArrayList.get(position);
                strPayMethod = resultp.get_payment_name();
                try {
                    PayId = resultp.get_payment_id();
                } catch (Exception ex) {
                    PayId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_cus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact resultp = contact_ArrayList.get(position);
                cusCode = resultp.get_contact_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (operation.equals("Edit")) {
            try {
                returns = Returns.getReturns(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'", database);
                edt_voucher_no.setText(returns.get_voucher_no());
                edt_date.setText(returns.get_date());
                edt_remarks.setText(returns.get_remarks());
                fill_spinner_contact(returns.get_contact_code());
                fill_spn_pay_method(returns.get_payment_id());
            } catch (Exception ex){
            }
        } else {
            fill_spn_pay_method("");
            fill_spinner_contact("");
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
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

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(img_date.getWindowToken(), 0);
                new DatePickerDialog(CusReturnHeaderActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cusCode.equals("")) {
                        Toast.makeText(getApplicationContext(),"Customer is required",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Customer is required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edt_date.getText().toString().trim().equals("")) {
                    edt_date.setError("Date is required");
                    return;
                }

                if (edt_voucher_no.getText().toString().trim().equals("")) {
                    Returns objVoucher = Returns.getReturns(getApplicationContext(), "order By id Desc LIMIT 1", database);

                    if (objVoucher == null) {
                        strVoucherNo = "RVC-" + 1;
                    } else {
                        strVoucherNo = "RVC-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                    }
                } else {

                    strVoucherNo = edt_voucher_no.getText().toString().trim();
                }



                pDialog = new ProgressDialog(CusReturnHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(CusReturnHeaderActivity.this, CusReturnFinalActivity.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("voucher_no", strVoucherNo);
                                    intent.putExtra("date", edt_date.getText().toString().trim());
                                    intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                    intent.putExtra("contact_code", cusCode);
                                    intent.putExtra("payment_id",PayId);
                                    startActivity(intent);
                                    finish();
                                }
                            });

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

    private void fill_spn_pay_method(String s) {
        if (Globals.strContact_Code.equals("")) {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'  and payment_id!=3");
        } else {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'");
        }

        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList);
        spn_pay_method.setAdapter(paymentListAdapter);

        if (!s.equals("")) {
            for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                String iname = paymentArrayList.get(i).get_payment_name();
                if (s.equals(iname)) {
                    spn_pay_method.setSelection(i);
                    break;
                }
            }
        }
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

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(CusReturnHeaderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(CusReturnHeaderActivity.this, CustomerReturnListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
                    startActivity(intent);

                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();
    }

    private void fill_spinner_contact(String str) {
        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') Order By lower(name) asc");
        }

        ReportCustomerAdapter reportCustomerAdapter = new ReportCustomerAdapter(CusReturnHeaderActivity.this, contact_ArrayList);
        spn_cus.setAdapter(reportCustomerAdapter);

        if (str.equals("")) {
            contact = Contact.getContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
            if (contact_ArrayList.size() > 0) {
                reportCustomerAdapter = new ReportCustomerAdapter(getApplicationContext(), contact_ArrayList);
                spn_cus.setAdapter(reportCustomerAdapter);
            }
        } else {
            contact = Contact.getContact(getApplicationContext(), database, db, " WHERE is_active ='1' and contact_code = '" + str + "'");
            if (contact == null) {
                if (contact_ArrayList.size() > 0) {
                    reportCustomerAdapter = new ReportCustomerAdapter(getApplicationContext(), contact_ArrayList);
                    spn_cus.setAdapter(reportCustomerAdapter);
                }
            } else {

                if (contact_ArrayList.size() > 0) {
                    reportCustomerAdapter = new ReportCustomerAdapter(getApplicationContext(), contact_ArrayList);
                    spn_cus.setAdapter(reportCustomerAdapter);

                    if (!contact.get_name().toString().equals("")) {
                        for (int i = 0; i < reportCustomerAdapter.getCount(); i++) {
//                    int h = (int) spinner_item_unit.getAdapter().getItemId(i);
                            String iname = contact_ArrayList.get(i).get_name();
                            if (contact.get_name().equals(iname)) {
                                spn_cus.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

}
