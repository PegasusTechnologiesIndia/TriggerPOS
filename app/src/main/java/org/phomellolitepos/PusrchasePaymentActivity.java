package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.PurchasePaymentSplitListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Purchase;
import org.phomellolitepos.database.Purchase_Payment;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.SplitPaymentList;

import java.util.ArrayList;

public class PusrchasePaymentActivity extends AppCompatActivity {
    TextView txt_amount;
    EditText edt_amount;
    Spinner spn_pay_method;
    ImageView img_add;
    ListView list;
    String strAmount, decimal_check, strPayCode, PayMethodName = "", strChange;
    Database db;
    SQLiteDatabase database;
    ArrayList<Payment> paymentArrayList;
    ArrayList<SplitPaymentList> array_list;
    Double UpdAmount;
    ProgressDialog pDialog;
    boolean flag = false;
    String date, modified_by;
    Lite_POS_Device lite_pos_device;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    ArrayList<String> list1a, list2a, list3a, list4a;
    MenuItem item;
    String operation = "", strVoucherNo = "";
    Contact contact;
    Purchase purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pusrchase_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        modified_by = Globals.user;
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
                pDialog = new ProgressDialog(PusrchasePaymentActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Globals.splitPsyMd.clear();
                            Intent intent = new Intent(PusrchasePaymentActivity.this, PurchaseFinalActivity.class);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", strVoucherNo);
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

        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Intent intent = getIntent();
        operation = intent.getStringExtra("operation");
        strVoucherNo = intent.getStringExtra("voucher_no");
        array_list = new ArrayList<>();
        txt_amount = (TextView) findViewById(R.id.txt_amount);
        edt_amount = (EditText) findViewById(R.id.edt_amount);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        img_add = (ImageView) findViewById(R.id.img_add);
        list = (ListView) findViewById(R.id.list);
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        settings = Settings.getSettings(getApplicationContext(), database, "");
        fill_spinner_pay_method("");

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {

            decimal_check = "1";
        }

        try {
            purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + strVoucherNo + "' ", database);
            if (purchase == null) {
            } else {
                strAmount = purchase.get_total();
            }
        } catch (Exception ex) {
        }

        txt_amount.setText("Amount : " + Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
        edt_amount.selectAll();
        edt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Double EdAmount = 0d;
                if (edt_amount.getText().toString().trim().equals("")) {

                } else {
                    try {
                        EdAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(edt_amount.getText().toString()), decimal_check));
                    } catch (Exception ex) {
                    }

                    Double TxAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                    if (EdAmount > TxAmount) {
                        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                        edt_amount.selectAll();
                    }
                }

                if (Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check)) == Double.parseDouble(Globals.myNumberFormat2Price(0, decimal_check))) {
                    item.setEnabled(true);
                } else {
                    item.setEnabled(false);
                }

            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takenAmount;
                if (edt_amount.getText().toString().trim().equals("")) {
                    edt_amount.setError(getString(R.string.Amount_is_required));
                    edt_amount.requestFocus();
                    return;
                } else {
                    if (Double.parseDouble(strAmount) > 0) {
                        if (Double.parseDouble(strAmount) >= Double.parseDouble(edt_amount.getText().toString())) {

                            takenAmount = edt_amount.getText().toString().trim();
                            UpdAmount = Double.parseDouble(strAmount) - Double.parseDouble(takenAmount);

                            txt_amount.setText("Amount : " + Globals.myNumberFormat2Price(UpdAmount, decimal_check));
                            strAmount = UpdAmount + "";
                            edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                            edt_amount.selectAll();
                            SplitPaymentList splitPaymentList = new SplitPaymentList(getApplicationContext(), strPayCode, PayMethodName, takenAmount);
                            array_list.add(splitPaymentList);
                            Globals.splitPsyMd.add(splitPaymentList);
                            fill_list(takenAmount);

                            if (Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check)) == Double.parseDouble(Globals.myNumberFormat2Price(0, decimal_check))) {
                                item.setEnabled(true);
                            } else {
                                item.setEnabled(false);
                            }
                        } else {
                            Double EdAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(edt_amount.getText().toString()), decimal_check));
                            Double TxAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                            if (EdAmount > TxAmount) {
                                edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                                edt_amount.selectAll();
                            }
                        }
                    }

                }
            }
        });

        spn_pay_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Payment resultp = paymentArrayList.get(position);
                strPayCode = resultp.get_payment_id();
                PayMethodName = resultp.get_payment_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fill_list(String takenAmount) {
        PurchasePaymentSplitListAdapter purchasePaymentSplitListAdapter = new PurchasePaymentSplitListAdapter(PusrchasePaymentActivity.this, array_list, strAmount, list);
        list.setAdapter(purchasePaymentSplitListAdapter);
    }

    private void fill_spinner_pay_method(String s) {
        paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' Order By payment_name asc");
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.action_settings);

        if (Double.parseDouble(strAmount) > 0) {
            item.setEnabled(false);
        } else {
        }
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


        if (id == R.id.action_settings) {
            pDialog = new ProgressDialog(PusrchasePaymentActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            new Thread() {
                public void run() {
                    String result = save_payment();
                    pDialog.dismiss();
                    if (result.equals("1")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Payment Saved successful", Toast.LENGTH_SHORT).show();
                                Globals.splitPsyMd.clear();
                                Intent intent = new Intent(PusrchasePaymentActivity.this, PurchaseListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Payment not saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String save_payment() {
        String strFlag = "0";
        Purchase_Payment purchase_payment = Purchase_Payment.getPurchase_Payment(getApplicationContext()," where ref_voucher_no = '" + strVoucherNo + "'",database);
        if (purchase_payment==null){
            int count = 1;
            for (int i = 0; i < Globals.splitPsyMd.size(); i++) {
                Purchase_Payment objPurchasePayment = new Purchase_Payment(getApplicationContext(), null, Globals.Device_Code, strVoucherNo, count + "", Globals.splitPsyMd.get(i).getAmount(),
                        Globals.splitPsyMd.get(i).getPayment_Type(), "", "", "", "", "", "");
                long op = objPurchasePayment.insertPurchase_Payment(database);
                if (op > 0) {
                    strFlag = "1";
                }
                count++;
            }
        }else {
            long e6 = Purchase_Payment.delete_Purchase_Payment(getApplicationContext(), "purchase_payment", " ref_voucher_no =? ", new String[]{strVoucherNo}, database);
            int count = 1;
            for (int i = 0; i < Globals.splitPsyMd.size(); i++) {
                Purchase_Payment objPurchasePayment = new Purchase_Payment(getApplicationContext(), null, Globals.Device_Code, strVoucherNo, count + "", Globals.splitPsyMd.get(i).getAmount(),
                        Globals.splitPsyMd.get(i).getPayment_Type(), "", "", "", "", "", "");
                long op = objPurchasePayment.insertPurchase_Payment(database);
                if (op > 0) {
                    strFlag = "1";
                }
                count++;
            }
        }

        return strFlag;
    }

    public void setTextView(String price) {
        txt_amount.setText("Amount : " +Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check));
        strAmount=price;
        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check));
        edt_amount.selectAll();
        UpdAmount=0d;
        fill_list("");
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(PusrchasePaymentActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.splitPsyMd.clear();
                    Intent intent = new Intent(PusrchasePaymentActivity.this, PurchaseFinalActivity.class);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", strVoucherNo);
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
