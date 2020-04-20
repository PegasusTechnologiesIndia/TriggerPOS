package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.Toast;

import org.phomellolitepos.Adapter.DialogContactAdapter;
import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.ReportCustomerAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CusReturnHeaderActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
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
    EditText spn_cus;
    ArrayList<Contact> contact_ArrayList;
    ArrayList<Payment> paymentArrayList;
    ArrayList<Payment> paymentArrayList1;
    String cusCode;
    Contact contact;
    Settings settings;
    String strPayMethod,PayId;
    Lite_POS_Registration lite_pos_registration;
    String projectid;
    Dialog listDialog;
    Dialog listDialog1;
    Dialog listDialog2;
    Dialog listDialog_table;
    String strSelectedCategory = "";
    ImageView searchimage;
    String part2;
    String orderId = null;
    DialogContactAdapter dialogContactMainListAdapter;
    ZBarScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_header);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        mScannerView = new ZBarScannerView(CusReturnHeaderActivity.this);
        mScannerView.setResultHandler(CusReturnHeaderActivity.this);
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
        spn_cus = (EditText) findViewById(R.id.spn_cus);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        img_date = (ImageView) findViewById(R.id.img_date);
        btn_next = (Button) findViewById(R.id.btn_next);
        searchimage=(ImageView)findViewById(R.id.srch_image);
        settings = Settings.getSettings(getApplicationContext(), database, "");
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
      projectid=lite_pos_registration.getproject_id();
        String date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                edt_date.setText(date1);
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

        searchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showdialogContact();
            }
        });



/*        spn_cus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact resultp = contact_ArrayList.get(position);
                cusCode = resultp.get_contact_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/



        if (operation.equals("Edit")) {
            try {
                returns = Returns.getReturns(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'", database);
                edt_voucher_no.setText(returns.get_voucher_no());
                edt_date.setText(returns.get_date());
                edt_remarks.setText(returns.get_remarks());
                contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + returns.get_contact_code() + "'");
               if(returns.get_contact_code().length()>0) {
                   spn_cus.setText(contact.get_name());
               }
               else{
                   spn_cus.setText("");
               }
               cusCode =returns.get_contact_code();
              // fill_spinner_contact(returns.get_contact_code());
                fill_spn_pay_method(returns.get_payment_id());
            } catch (Exception ex){
            }
        } else {
            fill_spn_pay_method("");
            spn_cus.setText( "");

          //  fill_spinner_contact("");
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
          /*      try {
                    if (cusCode.equals("")) {
                        Toast.makeText(getApplicationContext(),"Customer is required",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Customer is required",Toast.LENGTH_SHORT).show();
                    return;
                }*/
              /*  Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE is_active ='1'");
                      cusCode=contact.get_contact_code();*/
                if (edt_date.getText().toString().trim().equals("")) {
                    edt_date.setError("Date is required");
                    return;
                }

                if (edt_voucher_no.getText().toString().trim().equals("")) {
                    Returns objVoucher = Returns.getReturns(getApplicationContext(), "order By id Desc LIMIT 1", database);
                    Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);

                    if (last_code == null) {
                        if (objVoucher == null) {
                            strVoucherNo = "R-" +Globals.objLPD.getDevice_Symbol() + "-" + 1;
                        } else {
                            strVoucherNo ="R-"+ Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                        }
                    } else {
                        if (last_code.getLast_order_return_code().equals("0")) {

                            if (objVoucher == null) {
                                strVoucherNo = "R-" +Globals.objLPD.getDevice_Symbol() + "-" + 1;
                            } else {
                                strVoucherNo = "R-"+ Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                            }
                        } else {
                            if (objVoucher == null) {
                                String code = last_code.getLast_order_return_code();
                                String[] strCode = code.split("-");
                                part2 = strCode[2];
                                orderId = (Integer.parseInt(part2) + 1) + "";
                                strVoucherNo ="R-"+ Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(part2) + 1);
                            } else {
                                strVoucherNo = "R-"+ Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_voucher_no().replace("R-"+Globals.objLPD.getDevice_Symbol() + "-" ,"")) + 1);
                            }
                        }
                    }
                  /*  if (objVoucher == null) {
                        strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol()+ "-"+ 1;
                    } else {
                        strVoucherNo = "R-"+ Globals.objLPD.getDevice_Symbol()+ "-"+ (Integer.parseInt(objVoucher.get_voucher_no().replace(Globals.objLPD.getDevice_Symbol() + "-" ,"")) + 1);
                    }*/
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
        PaymentListAdapter paymentListAdapter=null;
        contact = Contact.getContact(getApplicationContext(), database, db, " WHERE is_active ='1'");

        if(contact==null) {
    paymentArrayList1 = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' and  payment_id=1");
    for(int i=0;i<1;i++) {
  paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList1);
        spn_pay_method.setAdapter(paymentListAdapter);
    }

    if (s.equals("")) {
        for (int i = 0; i < 1; i++) {
            String iname = paymentArrayList1.get(0).get_payment_name();
            if (s.equals(iname)) {
                spn_pay_method.setSelection(i);
                break;
            }
        }
    }

}
else {
   paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList);
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

/*    private void fill_spinner_contact(String str) {
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
    }*/
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
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

        srch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();

                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);

            }
        });

        img_brs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listDialog1.dismiss();
                    Globals.BarcodeReslt = "";
                    mScannerView.startCamera();
                    // Programmatically initialize the scanner view
                    setContentView(mScannerView);
                } catch (Exception ex) {
                }

            }
        });

    }

    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "and business_group_code='" + str + "'";
        }

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogContactMainListAdapter = new DialogContactAdapter(CusReturnHeaderActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);

    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        // Prints the scan format (qrcode, pdf417 etc.)

        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + rawResult.getContents() + "'");
        if (contact == null) {
            Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_SHORT).show();
            Globals.strContact_Code = "";
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(CusReturnHeaderActivity.this, MainActivity.class);
            intent.putExtra("opr", "Add");
            intent.putExtra("order_code", "");
            startActivity(intent);
        } else {
            Globals.strContact_Code = rawResult.getContents();
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(CusReturnHeaderActivity.this, MainActivity.class);
            intent.putExtra("opr", "Add");
            intent.putExtra("order_code", "");
            startActivity(intent);
        }
    }
    public void setCustomer(String name,String contactcode) {

        spn_cus.setText(name);
try {
    cusCode = contactcode;
    returns.set_contact_code(cusCode);
}
catch(Exception e ){


}
    }
}
