package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Return_Item_Tax;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Tax_Master;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    ArrayList<Payment> paymentArrayList1;
    ArrayList<Object> itemnamelist;
    ArrayList<Object> itemcodelist;
    ArrayList<Object> barcodelist;
    ArrayList<Object> returnquantitylist;
    ArrayList<Object> quantitylist;
    ArrayList<Object> pricelist;
    ArrayList<Returns> arraylistreturns;
    String strPayMethod, PayId, cusCode;
    String decimal_check;
    Contact contact;
    String db_contact, db_itemname;
   // Settings settings;
   // Lite_POS_Registration lite_pos_registration;
    String projectid;
    String itemname, barcode, itemcode, ordercode, returnquantity,quantity,linetotal;
    String part2;
    String orderId = null;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
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
                            sleep(100);
                            pDialog.dismiss();
                            Globals.strContact_Code="";
                            Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
       // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        projectid = Globals.objLPR.getproject_id();
        itemnamelist = new ArrayList<>();
        itemcodelist = new ArrayList<>();
        barcodelist = new ArrayList<>();
        returnquantitylist = new ArrayList<>();
        quantitylist= new ArrayList<>();
        pricelist=new ArrayList<>();
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        //Globals.objsettings = Settings.getSettings(getApplicationContext(), database, "");
        if (operation.equals("Edit")) {

            try {
                returns = Returns.getReturns(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'", database);
                edt_voucher_no.setText(returns.get_voucher_no());
               // edt_date.setText(returns.get_date());
                edt_remarks.setText(returns.get_remarks());
                edt_order_code.setText(returns.get_order_code());
                fill_spn_pay_method(returns.get_payment_id());
                Globals.str_date=returns.get_date();
            } catch (Exception ex) {
            }
        } else {
            try {
                fill_spn_pay_method("");
                String myFormat = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String date;
                try {
                    date = sdf.format(myCalendar.getTime());
                } catch (Exception ex) {
                    date = "";
                }
                Globals.str_date=date;
               // edt_date.setText(date);
                btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
            } catch (Exception e) {

            }
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
                    // Returns objVoucher = Returns.getReturns(getApplicationContext(), "order By id Desc LIMIT 1", database);
                    Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);

                    if (last_code == null) {
                        if (objVoucher == null) {
                            strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + 1;
                        } else {
                            strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                        }
                    } else {
                        if (last_code.getLast_order_return_code().equals("0")) {

                            if (objVoucher == null) {
                                strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + 1;
                            } else {
                                strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                            }
                        } else {
                            if (objVoucher == null) {
                                String code = last_code.getLast_order_return_code();
                                String[] strCode = code.split("-");
                                part2 = strCode[2];
                                orderId = (Integer.parseInt(part2) + 1) + "";
                                strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(part2) + 1);
                            } else {
                                strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objVoucher.get_voucher_no().replace("R-" + Globals.objLPD.getDevice_Symbol() + "-", "")) + 1);
                            }
                        }
                    }
                  /*  if (objVoucher == null) {
                        strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol()+ "-"+1;
                    } else {
                        strVoucherNo = "R-" + Globals.objLPD.getDevice_Symbol()+ "-" +(Integer.parseInt(objVoucher.get_id()) + 1);
                    }*/
                } else {
                    strVoucherNo = edt_voucher_no.getText().toString().trim();
                }

                if (edt_order_code.getText().toString().trim().equals("")) {
                    edt_order_code.setError("Invoice no. is required");
                    edt_order_code.requestFocus();
                    return;
                }

                // Commented this below code becz once need to check with sir if we deleted item and again trying to create order but it not allowing
/*
                if (!operation.equals("Edit")) {
                    arraylistreturns = Returns.getAllReturns(getApplicationContext(), " where order_code ='" + edt_order_code.getText().toString() + "' and is_post='false' and is_cancel='false'", database);
                    if (arraylistreturns.size() > 0) {
                        Toast.makeText(getApplicationContext(), "Order No " + edt_order_code.getText().toString() + " already created return voucher!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }*/
                if (isNetworkStatusAvialable(getApplicationContext())) {

                    pDialog = new ProgressDialog(InvReturnHeaderActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();

                    Thread timerThread = new Thread() {
                        public void run() {

                            if (projectid.equals("cloud")) {
                                try {
                                    sleep(1000);
                                    ValidInvoice(pDialog,edt_order_code.getText().toString().trim());

//                            String relt="1";



                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            } else {
                                try {
                                    contact = Contact.getContact(getApplicationContext(), database, db, " WHERE is_active ='1'");
                                    cusCode = contact.get_contact_code();


                                    //   String selectquery ="SELECT am.*,e.item_name FROM  order_detail am left join item e on e.item_code = am.item_code where order_code='A-2'";
                                    Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnFinalActivity.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("voucher_no", strVoucherNo);
                                    intent.putExtra("date", edt_date.getText().toString().trim());
                                    intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                    intent.putExtra("contact_code", cusCode);

                                    Bundle args = new Bundle();
                                    args.putSerializable("ARRAYLIST", (Serializable) itemnamelist);
                                    args.putSerializable("ItemCodeList", (Serializable) "");
                                    args.putSerializable("BarCodeList", (Serializable) "");
                                    intent.putExtra("BUNDLE", args);

                                    intent.putExtra("order_code", edt_order_code.getText().toString().trim());
                                    if (cusCode.equals("")) {
                                        intent.putExtra("payment_id", "1");
                                    } else {
                                        intent.putExtra("payment_id", PayId);
                                    }
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {

                                }
                            }

                        }

                    };
                    timerThread.start();
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private String ValidInvoiceFromServer(String serverData) {
        String succ = "0";

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {

                JSONObject myResult = jsonObject_bg.getJSONObject("result");
                JSONArray jsonArray1 = myResult.getJSONArray("order");
                JSONArray jsonArray2 = myResult.getJSONArray("order_detail");
                JSONArray jsonArray3 = myResult.getJSONArray("order_detail_tax");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    cusCode = jsonObject.getString("contact_code");
                    Globals.strContact_Code = cusCode;
                }
                double iPrice=0d;
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);

                    itemcode = jsonObject.getString("item_code");
                    itemname = jsonObject.getString("item_name");
                    ordercode = jsonObject.getString("order_code");
                    barcode = jsonObject.getString("barcode");
                    quantity=jsonObject.getString("quantity");
                    returnquantity = jsonObject.getString("return_quantity");
                    linetotal = jsonObject.getString("line_total");
                    Double quantityDB= Double.parseDouble(quantity);
                    Double returnquantityDB=Double.parseDouble(returnquantity);
                    if(quantityDB-returnquantityDB>0) {
                        itemnamelist.add(itemname);
                        itemcodelist.add(itemcode);
                        barcodelist.add(barcode);
                        returnquantitylist.add(returnquantity);
                        quantitylist.add(quantity);
                        pricelist.add(linetotal);
                        iPrice = Double.parseDouble(linetotal);
                    }
                    String Query = "Update  order_detail Set return_quantity = '" + returnquantity + "' where item_code='" + jsonObject.getString("item_code") + "'";
                    long result = db.executeDML(Query, database);

                    if (result > 0) {

                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + jsonObject.getString("item_code") + "'", database, db);
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        for (int j = 0; j < item_group_taxArrayList.size(); j++) {
                            iTax = 0d;
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(j).get_tax_id() + "'", database, db);
if(iPrice>0) {
    if (tax_master.get_tax_type().equals("P")) {
        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
    } else {
        iTax = iTax + Double.parseDouble(tax_master.get_rate());
    }

    Return_Item_Tax return_item_tax = new Return_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", itemcode, item_group_taxArrayList.get(j).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
    Globals.return_item_tax.add(return_item_tax);
}
                        }
                    } else {

                    }


                }
                for (int k = 0; k < jsonArray3.length(); k++) {
                    JSONObject jsonObject = jsonArray3.getJSONObject(k);

                    String detail_taxid = jsonObject.getString("order_detail_tax_id");
                    String retordercode = jsonObject.getString("order_code");
                    String srno = jsonObject.getString("sr_no");
                    String item_code = jsonObject.getString("item_code");
                    String taxid = jsonObject.getString("tax_id");
                    String taxtype=jsonObject.getString("tax_type");
                    String rate = jsonObject.getString("rate");
                    String value = jsonObject.getString("value");
                    String itemCode= item_code;
                }

                    //      if (taxIdFinalAarry.size() > 0) {





                succ = "1";
//
            }
        } catch (JSONException e) {
        }
        return succ;
    }

   /* private String ValidInvoice(String invoiceNo) {
        String serverData = null;//

        try {
            // String registration_code = lite_pos_registration.getRegistration_Code();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(


                    Globals.App_IP_URL + "invoice_return/check_order");
            ArrayList nameValuePairs = new ArrayList(2);
            nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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
        }
        catch(Exception e){

        }
        return serverData;
    }*/

    public  void ValidInvoice(final ProgressDialog pDialog, final String invoiceNo){
      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url =Globals.App_IP_URL + "invoice_return/check_order";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String relt = ValidInvoiceFromServer(response);
                            if (relt.equals("1")) {


                                try {
                                    Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnFinalActivity.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("voucher_no", strVoucherNo);
                                    intent.putExtra("date", Globals.str_date.toString().trim());
                                    intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                    intent.putExtra("contact_code", cusCode);
                                    intent.putExtra("order_code", edt_order_code.getText().toString().trim());
                                    intent.putExtra("item_code", itemcode);
                                    intent.putExtra("barcode", barcode);
                                    if (Globals.strContact_Code.equals("")) {
                                        intent.putExtra("payment_id", "1");
                                    } else {
                                        intent.putExtra("payment_id", PayId);
                                    }
                                    Bundle args = new Bundle();
                                    args.putSerializable("ARRAYLIST", (Serializable) itemnamelist);
                                    args.putSerializable("ItemCodeList", (Serializable) itemcodelist);
                                    args.putSerializable("BarCodeList", (Serializable) barcodelist);
                                    args.putSerializable("ret_quantitylist", (Serializable) returnquantitylist);
                                    args.putSerializable("quantitylist", (Serializable) quantitylist);
                                    args.putSerializable("pricelist", (Serializable) pricelist);
                                    intent.putExtra("BUNDLE", args);


                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {

                                }
                                // }}


                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Invoice Not Valid!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                         pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
               params.put("reg_code", Globals.objLPR.getRegistration_Code());
                //nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
               params.put("order_code", invoiceNo);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
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
        PaymentListAdapter paymentListAdapter = null;

        contact = Contact.getContact(getApplicationContext(), database, db, " WHERE is_active ='1'");

        if (contact == null) {
            paymentArrayList1 = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' and  payment_id=1");
            for (int i = 0; i < 1; i++) {
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

        } else {
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

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(InvReturnHeaderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(100);
                    pDialog.dismiss();
                    Globals.strContact_Code="";
                    Intent intent = new Intent(InvReturnHeaderActivity.this, InvReturnListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        private boolean isNetworkStatusAvialable(Context applicationContext) {
            // TODO Auto-generated method stub
            ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
                if (netInfos != null)
                    if (netInfos.isConnected())

                        return true;

            }

            return false;
        }
}
