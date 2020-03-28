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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.ReportCustomerAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InvReturnHeaderActivity extends AppCompatActivity {

    EditText edt_voucher_no, edt_date, edt_remarks, edt_order_code;
    ImageView img_date;
    Button btn_next;
    String operation, voucher_no, strVoucherNo;
    Database db;
    SQLiteDatabase database;
    Returns returns;
    ProgressDialog pDialog;
    Calendar myCalendar;
    Spinner spn_pay_method;
    ArrayList<Payment> paymentArrayList;
    String strPayMethod,PayId,cusCode;;
    Contact contact;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_return_header);
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
                pDialog = new ProgressDialog(InvReturnHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnListActivity.class);
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
//      getSupportActionBar().setTitle("Return");
        getSupportActionBar().setTitle(R.string.Return);
        operation = intent.getStringExtra("operation");
        voucher_no = intent.getStringExtra("voucher_no");

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        myCalendar = Calendar.getInstance();
        edt_voucher_no = (EditText) findViewById(R.id.edt_voucher_no);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_remarks = (EditText) findViewById(R.id.edt_remarks);
        edt_order_code = (EditText) findViewById(R.id.edt_order_code);
        img_date = (ImageView) findViewById(R.id.img_date);
        btn_next = (Button) findViewById(R.id.btn_next);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);

        settings = Settings.getSettings(getApplicationContext(), database, "");
        if (operation.equals("Edit")) {
            try {
                returns = Returns.getReturns(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'", database);
                edt_voucher_no.setText(returns.get_voucher_no());
                edt_date.setText(returns.get_date());
                edt_remarks.setText(returns.get_remarks());
                edt_order_code.setText(returns.get_order_code());
                fill_spn_pay_method(returns.get_payment_id());
            } catch (Exception ex) {
            }
        } else {
            fill_spn_pay_method("");
            String myFormat = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            String date;
            try {
                date = sdf.format(myCalendar.getTime());
            } catch (Exception ex) {
                date = "";
            }
            edt_date.setText(date);
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

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
                new DatePickerDialog(InvReturnHeaderActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                if (edt_order_code.getText().toString().trim().equals("")) {
                    edt_order_code.setError("Invoice no. is required");
                    edt_order_code.requestFocus();
                    return;
                }

                if (edt_date.getText().toString().trim().equals("")) {
                    edt_date.setError("Date is required");
                    return;
                }

                pDialog = new ProgressDialog(InvReturnHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            String relt = ValidInvoiceFromServer(edt_order_code.getText().toString().trim());
//                            String relt="1";
                            pDialog.dismiss();
                            if (relt.equals("1")) {
                                Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnFinalActivity.class);
                                intent.putExtra("operation", operation);
                                intent.putExtra("voucher_no", strVoucherNo);
                                intent.putExtra("date", edt_date.getText().toString().trim());
                                intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                intent.putExtra("contact_code", cusCode);
                                intent.putExtra("order_code", edt_order_code.getText().toString().trim());
                                if (cusCode.equals("")){
                                    intent.putExtra("payment_id","1");
                                }else {
                                    intent.putExtra("payment_id",PayId);
                                }

                                startActivity(intent);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Invoice Not Valid!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {}
                    }
                };
                timerThread.start();
            }
        });
    }

    private String ValidInvoiceFromServer(String InvoiceNo) {
        String succ = "0";
        String serverData = ValidInvoice(InvoiceNo);
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray = jsonObject_bg.optJSONArray("result");
                for (int i=0;i<jsonArray.length();i++){
                   JSONObject jsonObject  = jsonArray.getJSONObject(i);
                    cusCode = jsonObject.getString("contact_code");
                }
                succ = "1";
//
            }
        } catch (JSONException e) {}
        return succ;
    }

    private String ValidInvoice(String invoiceNo) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/invoice_return/check_order");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        //nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("order_code", invoiceNo));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
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

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(InvReturnHeaderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnListActivity.class);
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
}
