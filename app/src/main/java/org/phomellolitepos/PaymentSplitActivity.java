package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.PaymentSplitListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.Util.Loyalty_Redeem;
import org.phomellolitepos.Util.Watermark;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Coupon_Detail;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Loyalty_Earn;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.SplitPaymentList;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PaymentSplitActivity extends AppCompatActivity {
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
    String date, strOrderNo = "", modified_by;
    Lite_POS_Device lite_pos_device;
    String part2;
    String orderId = null;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    Orders objOrder;
    ArrayList<String> list1a, list2a, list3a, list4a;
    MenuItem item;
    private ICallback callback1 = null;
    private IWoyouService woyouService;
    String PrinterType, opr = "", strOrderCode = "";
    Contact contact;
    String ck_project_type;
    ArrayList<Order_Payment> order_payment_array;
    private static final String TAG = "PrinterTestDemo";
    Loyalty_Redeem loyalty_redeem;

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };
    private final int MSG_TEST = 1;
    private long printCount = 0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                //testAll();
                long mm = MemInfo.getmem_UNUSED(PaymentSplitActivity.this);
                if (mm < 100) {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 800);
                }
                Log.i(TAG, "testAll: " + printCount + " Memory: " + mm);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_split);
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
                pDialog = new ProgressDialog(PaymentSplitActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Globals.splitPsyMd.clear();
                            Intent intent = new Intent(PaymentSplitActivity.this, PaymentActivity.class);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
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

        callback1 = new ICallback.Stub() {

            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "printlength:" + value + "\n");
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "onRaiseException: " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        };
        Intent intent_1 = new Intent();
        intent_1.setPackage("woyou.aidlservice.jiuiv5");
        intent_1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_1);
        bindService(intent_1, connService, Context.BIND_AUTO_CREATE);

        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Intent intent = getIntent();
        strAmount = intent.getStringExtra("Amount");
        opr = intent.getStringExtra("opr");
        strOrderCode = intent.getStringExtra("order_code");
        array_list = new ArrayList<>();
        txt_amount = (TextView) findViewById(R.id.txt_amount);
        edt_amount = (EditText) findViewById(R.id.edt_amount);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        img_add = (ImageView) findViewById(R.id.img_add);
        list = (ListView) findViewById(R.id.list);
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        settings = Settings.getSettings(getApplicationContext(), database, "");

        ck_project_type = lite_pos_registration.getproject_id();
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {

            decimal_check = "1";
        }
        Globals.CouponTotal = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
        txt_amount.setText("Amount : " + Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
        edt_amount.selectAll();
        fill_spinner_pay_method("");
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
                } else if (edt_amount.getText().toString().trim().equals(".")) {
                    edt_amount.setError("Can't enter decimal only");
                    edt_amount.selectAll();
                    return;
                } else {
                    if (Double.parseDouble(strAmount) > 0) {
                        if (Double.parseDouble(strAmount) >= Double.parseDouble(edt_amount.getText().toString())) {

                            takenAmount = edt_amount.getText().toString().trim();
                            UpdAmount = Double.parseDouble(strAmount) - Double.parseDouble(takenAmount);

                            txt_amount.setText(getString(R.string.Amount2) + Globals.myNumberFormat2Price(UpdAmount, decimal_check));
                            strAmount = UpdAmount + "";
                            edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                            edt_amount.selectAll();
                            //strChange = Globals.myNumberFormat2Price(UpdAmount, decimal_check);
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
                if (strPayCode.equals("5")) {
                    if (Globals.strContact_Code.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please select customer for this mode", Toast.LENGTH_SHORT).show();
                        fill_spinner_pay_method("");
                    }
                }
//                else if (strPayCode.equals("6")) {
//                    coupon_dialog();
//                } else if (strPayCode.equals("3")) {
//                    loyalty_dialog(Globals.strContact_Code);
//                }
                PayMethodName = resultp.get_payment_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loyalty_dialog(String strContact_code) {
        final Dialog listDialog2 = new Dialog(PaymentSplitActivity.this);
        listDialog2.setTitle("Loyalty");
        LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.payment_loyalty_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_earned_point = (EditText) listDialog2.findViewById(R.id.edt_earned_point);
        final EditText edt_point_value = (EditText) listDialog2.findViewById(R.id.edt_point_value);
        final EditText edt_point_rdm = (EditText) listDialog2.findViewById(R.id.edt_point_rdm);
        final EditText edt_amt_loyalty = (EditText) listDialog2.findViewById(R.id.edt_amt_loyalty);
        Button btn_apply = (Button) listDialog2.findViewById(R.id.btn_apply);
        Button btn_save = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) listDialog2.findViewById(R.id.btn_cancel);
        listDialog2.show();
        Window window = listDialog2.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

        String strEarnPoint = "0", strPointValue = "0", strPointRedeem = "0", strAmt = "0";
        String strQry = "select sum(earn_point-redeem_point) from order_loyalty_earn where customer_code = '" + Globals.strContact_Code + "'";
        try {
            Cursor cursor = database.rawQuery(strQry, null);
            if (cursor.moveToFirst()) {
                do {
                    strEarnPoint = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
        }

        strQry = "select sum(earn_point - redeem_point) * sum(earn_value) from order_loyalty_earn where customer_code = '" + Globals.strContact_Code + "' and (earn_point-redeem_point) > 0 order by id Asc";
        try {
            Cursor cursor = database.rawQuery(strQry, null);
            if (cursor.moveToFirst()) {
                do {
                    strPointValue = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(0)), decimal_check);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            strEarnPoint = "0";
        }

        edt_earned_point.setText(Globals.myNumberFormat2Price(Double.parseDouble(strEarnPoint), decimal_check));
        edt_point_value.setText(Globals.myNumberFormat2Price(Double.parseDouble(strPointValue), decimal_check));
        edt_point_rdm.setText(Globals.myNumberFormat2Price(Double.parseDouble(strPointRedeem), decimal_check));
        edt_amt_loyalty.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmt), decimal_check));

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double Amount = 0d;
                Double Point = Double.parseDouble(edt_point_rdm.getText().toString());
                if (Double.parseDouble(edt_point_rdm.getText().toString()) > Double.parseDouble(edt_earned_point.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Can't apply more then earned point", Toast.LENGTH_SHORT).show();
                } else {
                    String strQry1 = "select * from order_loyalty_earn where customer_code = '" + Globals.strContact_Code + "' and (earn_point-redeem_point) > 0 order by id Asc";
                    try {
                        Cursor cursor = database.rawQuery(strQry1, null);
                        if (cursor.moveToFirst()) {
                            do {
                                Double ComVuale = (Double.parseDouble(cursor.getString(3)) - Double.parseDouble(cursor.getString(5)));
                                if (Point > ComVuale) {
                                    Double RedmValue = (Double.parseDouble(cursor.getString(4)) / Double.parseDouble(cursor.getString(3))) * Double.parseDouble(cursor.getString(5));
                                    loyalty_redeem = new Loyalty_Redeem(getApplicationContext(), cursor.getString(0), ComVuale + "", RedmValue + "");
                                    Globals.LoyaltyRedeem.add(loyalty_redeem);
                                    Point = Point - ComVuale;
                                    Amount = Amount + RedmValue;
                                } else {
                                    if (Point > ComVuale) {
                                    } else {
                                        Double RedmValue = (Double.parseDouble(cursor.getString(4)) / Double.parseDouble(cursor.getString(3))) * Double.parseDouble(cursor.getString(5));
                                        loyalty_redeem = new Loyalty_Redeem(getApplicationContext(), cursor.getString(0), Point + "", RedmValue + "");
                                        Globals.LoyaltyRedeem.add(loyalty_redeem);
                                        Amount = Amount + RedmValue;
                                        break;
                                    }
                                }
                            } while (cursor.moveToNext());
                        }
                    } catch (Exception ex) {
                    }
                    edt_amt_loyalty.setText(Globals.myNumberFormat2Price(Amount, decimal_check));
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String takenAmount;
                String strAmount1 = "0";
                if (Double.parseDouble(strAmount) > 0) {
                    if (Double.parseDouble(strAmount) >= Double.parseDouble(edt_amt_loyalty.getText().toString())) {

                        takenAmount = edt_amt_loyalty.getText().toString();
                        UpdAmount = Double.parseDouble(strAmount) - Double.parseDouble(takenAmount);

                        txt_amount.setText("Amount : " + Globals.myNumberFormat2Price(UpdAmount, decimal_check));
                        strAmount = UpdAmount + "";
                        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                        edt_amount.selectAll();
                        strChange = Globals.myNumberFormat2Price(UpdAmount, decimal_check);
                        SplitPaymentList splitPaymentList = new SplitPaymentList(getApplicationContext(), strPayCode, PayMethodName, takenAmount);
                        array_list.add(splitPaymentList);
                        Globals.splitPsyMd.add(splitPaymentList);
                        fill_list(takenAmount);
                        if (Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check)) == Double.parseDouble(Globals.myNumberFormat2Price(0, decimal_check))) {
                            item.setEnabled(true);
                        } else {
                            item.setEnabled(false);
                        }
                        listDialog2.dismiss();
                    } else {
                        Double EdAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(edt_amount.getText().toString()), decimal_check));
                        Double TxAmount = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                        if (EdAmount > TxAmount) {
                            edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(strAmount), decimal_check));
                            edt_amount.selectAll();
                        }
                        listDialog2.dismiss();
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog2.dismiss();
            }
        });
    }

    private void fill_list(String takenAmount) {
        PaymentSplitListAdapter paymentSplitListAdapter = new PaymentSplitListAdapter(PaymentSplitActivity.this, array_list, strAmount, list);
        list.setAdapter(paymentSplitListAdapter);
    }

    private void fill_spinner_pay_method(String s) {
        //Pro_Loyalty_Setup pro_loyalty_setup = Pro_Loyalty_Setup.getPro_Loyalty_Setup(getApplicationContext(), database, db, " where loyalty_type = 'POINTSYSTEM'");
        if (Globals.strContact_Code.equals("")) {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'  and payment_id!=3");
        } else {
            paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1'");
        }
//      paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' Order By payment_name asc");
        if (paymentArrayList.size() > 0) {
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
            pDialog = new ProgressDialog(PaymentSplitActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            new Thread() {
                public void run() {
                    if (settings.get_Is_Cash_Drawer().equals("true")) {
                        open_cash_drawer();
                    }
                    CheckOutOrder();

                }
            }.start();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CheckOutOrder() {
        try {
            Globals.SRNO = 1;
            String strFlag = "0";
            String strOrdeeStatus = "CLOSE";
            Double netAmountStr = 0.0;
            database.beginTransaction();
            if (Globals.Inv_Opr.equals("Add") || Globals.Inv_Opr.equals("Resv")) {
                try {
                    netAmountStr = Globals.Net_Amount;
                } catch (Exception ex) {
                }

                Double taxStr = 0.0;
                try {
                    //taxStr = Double.parseDouble(edt_tax.getText().toString());
                } catch (Exception ex) {
                }

                Double tenderAmountStr = 0.0;
                try {
                    tenderAmountStr = Globals.Net_Amount;
                } catch (Exception ex) {
                }

//                if (tenderAmountStr == 0) {
//                    tenderAmountStr = netAmountStr;
//                }
                if (tenderAmountStr >= netAmountStr) {

                    Double changeStr = 0d;

                    // Order Number Generate
                    Order_Detail objOrderDetail;
                    Orders objOrder;
                    Order_Payment objOrderPayment;

                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                    date = format.format(d);

                    Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");

                    Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);

                    if (last_code == null) {
                        if (objOrder1 == null) {
                            strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
                        } else {
                            strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                        }
                    } else {
                        if (last_code.getlast_order_code().equals("0")) {

                            if (objOrder1 == null) {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
                            } else {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                            }
                        } else {
                            if (objOrder1 == null) {

                                String code = last_code.getlast_order_code();
                                String[] strCode = code.split("-");
                                part2 = strCode[1];
                                orderId = (Integer.parseInt(part2) + 1) + "";
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(part2) + 1);
                            } else {
                                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                            }
                        }
                    }


                    ArrayList<ShoppingCart> myCart = Globals.cart;

                    String locCode = null;
                    try {
                        locCode = Globals.objLPD.getLocation_Code();
                    } catch (Exception ex) {
                        locCode = "";
                    }

                    String strDis = Globals.Inv_Discount + "";

                    if (strDis.equals("")) {
                        strDis = "0";
                    }


                    objOrder = new Orders(getApplicationContext(), orderId, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                            "0", Globals.TotalItem + "", Globals.TotalQty + "",
                            Globals.TotalItemPrice + "", Globals.Inv_Tax + "", strDis, Globals.Net_Amount + "", tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, Globals.Inv_Description, Globals.strTable_Code, Globals.Inv_Delivery_Date);

                    long l = objOrder.insertOrders(database);
                    if (l > 0) {
                        strFlag = "1";
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);
                            objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                            long o = objOrderDetail.insertOrder_Detail(database);
                            if (o > 0) {
                                if (settings.get_Is_Stock_Manager().equals("true")) {
                                    stock_deduct(mCart.get_Item_Code(), mCart.get_Quantity());
                                }
                                strFlag = "1";
                            } else {

                            }
                        }
                        //database.endTransaction();

//                    long e = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderNo}, database);
                        ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                        Order_Tax objOrderTax;
                        for (int count = 0; count < orderTaxArray.size(); count++) {
                            OrderTaxArray Otary = orderTaxArray.get(count);
                            objOrderTax = new Order_Tax(getApplicationContext(), null, strOrderNo, Otary.get_tax_id()
                                    , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

                            long o = objOrderTax.insertOrder_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {

                            }
                        }

//                    long e1 = Order_Item_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderNo}, database);

                        ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                        Order_Detail_Tax objOrderDetailTax;
                        for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                            Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);


                            objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                                    , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());

                            long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {

                            }
                        }

                        int count = 1;
                        for (int i = 0; i < Globals.splitPsyMd.size(); i++) {
                            if (Globals.splitPsyMd.get(i).getPayment_Type().equals("6")) {
                                objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, count + "", Globals.splitPsyMd.get(i).getAmount(),
                                        Globals.splitPsyMd.get(i).getPayment_Type(), "", "", Globals.CardNo, "", "", "");
                            } else {
                                objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, count + "", Globals.splitPsyMd.get(i).getAmount(),
                                        Globals.splitPsyMd.get(i).getPayment_Type(), "", "", "", "", "", "");
                            }

                            long op = objOrderPayment.insertOrder_Payment(database);
                            if (op > 0) {
                                strFlag = "1";
                            } else {
                            }
                            count++;
                        }
//                        Coupon_Detail coupon_detail = Coupon_Detail.getCoupon_Detail(getApplicationContext(),"where card_no='"+Globals.CardNo+"'",database);
//                        coupon_detail.set_is_used("Y");
//                        long i4 = coupon_detail.updateCoupon_Detail("card_no=?", new String[]{Globals.CardNo}, database);
//                        if (i4 > 0) {}
//                        Globals.CardNo="";
//
//                        int count1 = 1;
//                        Order_Loyalty_Earn order_loyalty_earn;
//                        Double TotalRedeem = 0d;
//                        for (int i = 0; i < Globals.LoyaltyRedeem.size(); i++) {
//                            order_loyalty_earn = Order_Loyalty_Earn.getOrder_Loyalty_Earn(getApplicationContext(), database, db, " where id='" + Globals.LoyaltyRedeem.get(i).getId() + "'");
//                            TotalRedeem = Double.parseDouble(Globals.LoyaltyRedeem.get(i).getRedeem_value()) + Double.parseDouble(order_loyalty_earn.get_redeem_point());
//                            order_loyalty_earn.set_redeem_point(TotalRedeem + "");
//                            long op = order_loyalty_earn.updateOrder_Loyalty_Earn("id=?", new String[]{Globals.LoyaltyRedeem.get(i).getId()}, database);
//                            if (op > 0) {
//                                strFlag = "1";
//                            } else {
//
//                            }
//                            count1++;
//                        }

                        if (!Globals.strContact_Code.equals("")) {
                            Double strOldBalance = 0d;
                            Double strAmount = 0d;
                            String strQry2 = "Select SUM(pay_amount) from order_payment Where order_code ='" + strOrderNo + "' and payment_id='5'";
                            try {
                                Cursor cursor = database.rawQuery(strQry2, null);
                                while (cursor.moveToNext()) {
                                    strAmount = Double.parseDouble(cursor.getString(0));
                                }
                            } catch (Exception ex) {
                            }

                            Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(getApplicationContext(), " where contact_code='" + Globals.strContact_Code + "'", database);
                            if (acc_customer == null) {
                                strAmount = strOldBalance - strAmount;
                                acc_customer = new Acc_Customer(getApplicationContext(), null, Globals.strContact_Code, strAmount + "");
                                acc_customer.insertAcc_Customer(database);
                            } else {
                                strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                strAmount = strOldBalance - strAmount;
                                acc_customer.set_amount(strAmount + "");
                                long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{Globals.strContact_Code}, database);
                            }
                        }


                    } else {
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } else {
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getApplicationContext(), R.string.Payment_Error, Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            } else {

                try {
                    netAmountStr = Globals.Net_Amount;
                } catch (Exception ex) {

                }

                Double taxStr = 0.0;
                try {
                    taxStr = Globals.Inv_Tax;
                } catch (Exception ex) {
                }

                Double tenderAmountStr = 0.0;
                try {
                    tenderAmountStr = Globals.Net_Amount;
                } catch (Exception ex) {
                }

                if (tenderAmountStr == 0) {
                    tenderAmountStr = netAmountStr;
                }
                if (tenderAmountStr >= netAmountStr) {

                    Double changeStr = 0d;

                    // Order Number Generate
                    Order_Detail objOrderDetail;
                    Orders objOrder;
                    Order_Payment objOrderPayment;

                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                    date = format.format(d);


                    strOrderNo = Globals.Inv_Odr_Code;


                    ArrayList<ShoppingCart> myCart = Globals.cart;

                    String locCode = null;
                    try {
                        locCode = Globals.objLPD.getLocation_Code();
                    } catch (Exception ex) {
                        locCode = "";
                    }

                    String strDis = Globals.Inv_Discount + "";

                    if (strDis.equals("")) {
                        strDis = "0";
                    }

                    objOrder = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + strOrderNo + "'");


                    objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, objOrder.get_order_date(), "0",
                            "0", Globals.TotalItem + "", Globals.TotalQty + "",
                            Globals.Sub_Total + "", Globals.Inv_Tax + "", strDis, Globals.Net_Amount + "", tenderAmountStr + "",
                            changeStr + "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, Globals.Inv_Description + "", Globals.strTable_Code, Globals.Inv_Delivery_Date);

                    long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderNo, objOrder.get_order_id()}, database);
                    if (l > 0) {
                        strFlag = "1";
                        long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderNo}, database);
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);
                            objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");

                            long o = objOrderDetail.insertOrder_Detail(database);

                            if (o > 0) {
                                strFlag = "1";
                            } else {

                            }
                        }

                        long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderNo}, database);

                        ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
                        Order_Tax objOrderTax;
                        for (int count = 0; count < orderTaxArray.size(); count++) {
                            OrderTaxArray Otary = orderTaxArray.get(count);


                            objOrderTax = new Order_Tax(getApplicationContext(), null, strOrderNo, Otary.get_tax_id()
                                    , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

                            long o = objOrderTax.insertOrder_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }

                        long e7 = Order_Detail_Tax.delete_Order_Detail_Tax(getApplicationContext(), "order_detail_tax", " order_code =? ", new String[]{strOrderNo}, database);

                        ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                        Order_Detail_Tax objOrderDetailTax;
                        for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                            Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);


                            objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                                    , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());

                            long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }


                        int count = 1;
                        for (int i = 0; i < Globals.splitPsyMd.size(); i++) {


                            long e5 = Order_Payment.delete_order_payment(getApplicationContext(), "order_payment", " order_code =? ", new String[]{strOrderNo}, database);
                            objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, count + "", Globals.splitPsyMd.get(i).getAmount(),
                                    Globals.splitPsyMd.get(i).getPayment_Type(), "", "", "", "", "", "");

                            long op = objOrderPayment.insertOrder_Payment(database);
                            if (op > 0) {
                                strFlag = "1";
                            } else {
                            }
                            count++;
                        }

//                    }

                    } else {
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(getApplicationContext(), R.string.Orders_Not_Saved, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } else {
                    pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Payment_Error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            if (settings == null) {
                PrinterType = "";
            } else {
                try {
                    PrinterType = settings.getPrinterId();
                } catch (Exception ex) {

                    PrinterType = "";
                }
            }

            if (strFlag.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();

                performPDFExport();
//                try {
//                    displayTilte = lite_pos_registration.getCompany_Name();
//                    call_customer_disply_title(displayTilte);
//                } catch (Exception ex) {
//                }
                pDialog.dismiss();

                if (Globals.strContact_Code.equals("")) {
                    call_remaining_code();
                } else {
                    if (settings.get_Is_File_Share().equals("true")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Dialog listDialog2 = new Dialog(PaymentSplitActivity.this);
                                listDialog2.setTitle("File Share");
                                LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
                                listDialog2.setContentView(v1);
                                listDialog2.setCancelable(false);
                                final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
                                edt_remark.setVisibility(View.GONE);
                                Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
                                btnButton.setText("Share");
                                Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
                                listDialog2.show();
                                btnClear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        call_remaining_code();
                                    }
                                });
                                btnButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Globals.strContact_Code.equals("")) {
                                            call_remaining_code();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    startWhatsApp();
                                                }
                                            });
                                        }
                                        listDialog2.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        call_remaining_code();
                    }

                }

            } else {
                pDialog.dismiss();
                database.endTransaction();
            }
        } finally {
        }
    }

    private void send_email(String strEmail) {
        try {
            String[] recipients = {strEmail};
            final SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;

            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code = '" + Globals.strContact_Code + "'");
            email.m = new GMailSender(settings.get_Email(), settings.get_Password(), settings.get_Host(), settings.get_Port());
            email.m.set_from(settings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Customer Copy of your Order no : " + strOrderNo + " ");
            email.m.set_to(recipients);
            email.m.set_subject("Confirmation of your Order " + strOrderNo + " Mail");
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "PDF Report" + "/" + strOrderNo + ".pdf");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void performPDFExport() {
        objOrder = Orders.getOrders(getApplicationContext(), database, "WHERE order_code = '" + strOrderNo + "'");
        final ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), "WHERE order_code = '" + strOrderNo + "'", database);

        int count = 0;
        while (count < order_detail.size()) {

            String item_code = order_detail.get(count).get_item_code();
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
            list1a.add(item.get_item_name());
            list2a.add(order_detail.get(count).get_quantity());
            list3a.add(Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_sale_price()), decimal_check));
            list4a.add(Globals.myNumberFormat2Price(Double.parseDouble(order_detail.get(count).get_line_total()), decimal_check));

            count++;
        }

        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + objOrder.get_order_code() + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
//            image = createimage();

            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.Order), B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);

            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph(getString(R.string.CompanyName) + " : " + Globals.objLPR.getCompany_Name(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph(getString(R.string.POS_No) + " : " + Globals.objLPD.getDevice_Code(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_no = new PdfPTable(1);
            PdfPCell cell_order_no = new PdfPCell(new Paragraph(getString(R.string.Order_No) + " : " + objOrder.get_order_code(), B12));
            cell_order_no.setColspan(1);
            cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_no.setPadding(5.0f);
            table_order_no.addCell(cell_order_no);
            table_order_no.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph(getString(R.string.Order_Date) + objOrder.get_order_date(), B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph(getString(R.string.Cashier) + " : " + user.get_name(), B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);


            PdfPTable table = new PdfPTable(4);

            Phrase pr = new Phrase(getString(R.string.ItemName), B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setPadding(5);
            table.addCell(c1);
            pr = new Phrase(getString(R.string.Quantity), B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            pr = new Phrase(getString(R.string.Price), B10);
            PdfPCell c2 = new PdfPCell(pr);
            c2.setPadding(5);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c2);
            pr = new Phrase(getString(R.string.Total), B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            for (int i = 0; i < list1a.size(); i++) {
                Phrase pr1 = new Phrase(list1a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c7);

                pr1 = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr1 = new Phrase(list3a.get(i), N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                String total_amount;
                total_amount = Globals.myNumberFormat2Price(Double.parseDouble(list4a.get(i)), decimal_check);
                pr1 = new Phrase(total_amount, N9);
                c7 = new PdfPCell(pr1);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);
            }
            table.setSpacingBefore(10.0f);
            table.setHeaderRows(1);
            document.open();


            PdfPTable table_subtotal = new PdfPTable(2);

            Phrase pr23 = new Phrase(getString(R.string.SubTotal), B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c16);
            pr23 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c17);
            table_subtotal.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_total_tax = new PdfPTable(2);

            Phrase pr234 = new Phrase(getString(R.string.Total_Tax), B10);
            PdfPCell c161 = new PdfPCell(pr234);
            c161.setPadding(5);
            c161.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total_tax.addCell(c161);
            pr234 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check), B10);

            PdfPCell c173 = new PdfPCell(pr234);
            c173.setPadding(5);
            c173.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_total_tax.addCell(c173);
//            table_total_tax.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_discount = new PdfPTable(2);

            Phrase pr24 = new Phrase(getString(R.string.Discount), B10);
            PdfPCell c11 = new PdfPCell(pr24);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_discount.addCell(c11);
            pr24 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check), B10);

            PdfPCell c19 = new PdfPCell(pr24);
            c19.setPadding(5);
            c19.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_discount.addCell(c19);
            table_discount.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_net_amount = new PdfPTable(2);

            Phrase pr248 = new Phrase(getString(R.string.NetAmount), B10);
            PdfPCell c171 = new PdfPCell(pr248);
            c171.setPadding(5);
            c171.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_net_amount.addCell(c171);
            pr248 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check), B10);

            PdfPCell c169 = new PdfPCell(pr248);
            c169.setPadding(5);
            c169.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_net_amount.addCell(c169);
//            table_net_amount.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_tender = new PdfPTable(2);

            Phrase pr249 = new Phrase(getString(R.string.Tender), B10);
            PdfPCell c178 = new PdfPCell(pr249);
            c178.setPadding(5);
            c178.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_tender.addCell(c178);
            pr249 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check), B10);

            PdfPCell c167 = new PdfPCell(pr249);
            c167.setPadding(5);
            c167.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_tender.addCell(c167);
//            table_tender.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_change = new PdfPTable(2);

            Phrase pr242 = new Phrase(getString(R.string.Change), B10);
            PdfPCell c158 = new PdfPCell(pr242);
            c158.setPadding(5);
            c158.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_change.addCell(c158);
            pr242 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check), B10);

            PdfPCell c187 = new PdfPCell(pr242);
            c187.setPadding(5);
            c187.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_change.addCell(c187);
//            table_change.setSpacingBefore(10.0f);
            document.open();


            Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

            document.open();


            PdfPTable table_address = new PdfPTable(2);

            Phrase pr287 = new Phrase(getString(R.string.Address), B10);
            PdfPCell c188 = new PdfPCell(pr287);
            c188.setPadding(5);
            c188.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_address.addCell(c188);
            pr287 = new Phrase("" + lite_pos_registration.getAddress(), B10);

            PdfPCell c197 = new PdfPCell(pr287);
            c197.setPadding(5);
            c197.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_address.addCell(c197);
            table_address.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_mobile = new PdfPTable(2);
            Phrase pr217 = new Phrase(getString(R.string.Mobile), B10);
            PdfPCell c218 = new PdfPCell(pr217);
            c218.setPadding(5);
            c218.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_mobile.addCell(c218);
            pr217 = new Phrase("" + lite_pos_registration.getMobile_No(), B10);

            PdfPCell c210 = new PdfPCell(pr217);
            c210.setPadding(5);
            c210.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_mobile.addCell(c210);
            document.open();

            document.add(tableh);
            document.add(table_company_name);
            document.add(table_posno);
            document.add(table_order_no);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table);
            document.add(table_subtotal);
            document.add(table_total_tax);
            document.add(table_discount);
            document.add(table_net_amount);
            document.add(table_tender);
            document.add(table_change);
            document.add(table_address);
            document.add(table_mobile);

            PdfPTable table5 = new PdfPTable(1);
            table5.setSpacingBefore(10.0f); // Space Before table starts, like
            table5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPTable table4 = new PdfPTable(2);
            table4.setSpacingBefore(20.0f);
            float[] columnWidths1 = new float[]{20f, 5f};
            table4.setWidths(columnWidths1);
            table4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            Phrase p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Cashier, N12);
            PdfPCell cell5 = new PdfPCell(p5);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell5.setBorder(Rectangle.NO_BORDER);
            p5 = new Phrase(R.string.Signature + "\n" + "\n" + "\n" + "\n" + "\n" + R.string.Manager, N12);
            PdfPCell cell6 = new PdfPCell(p5);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            table4.addCell(cell5);
            table4.addCell(cell6);
            document.add(table4);
            document.newPage();
            document.close();
            file.close();
//            Toast.makeText(getApplicationContext(), "Pdf Created Successfully",
//                    Toast.LENGTH_SHORT).show();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    //startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            R.string.PDF_validation,
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            f.delete();
        }
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        PaymentSplitActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(PaymentSplitActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(PaymentSplitActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }
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

    private void print_kot(final String strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                    String Print_type = "0";
                    woyouService.setAlignment(1, callback1);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback1);
                    woyouService.setFontSize(30, callback1);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback1);
                    }

                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback1);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback1);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback1);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback1);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback1);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X" + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback1);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void print_kot_phapos(final String strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Orders orders = Orders.getOrders(getApplicationContext(), database, " where order_code='" + strOrderNo + "'");
                    String Print_type = "0";
                    woyouService.setAlignment(1, callback1);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback1);
                    woyouService.setFontSize(30, callback1);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback1);
                    }
                    woyouService.setFontSize(35, callback1);
                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback1);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + orders.get_order_code() + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback1);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback1);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback1);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback1);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X " + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback1);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);
                    woyouService.printTextWithFont("\n", "", 24, callback1);

                    woyouService.cutPaper(callback1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setTextView(String price) {
        txt_amount.setText("Amount : " + Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check));
        strAmount = price;
        edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check));
        edt_amount.selectAll();
        UpdAmount = 0d;
        fill_list("");
    }

    @Override
    public void onBackPressed() {

        pDialog = new ProgressDialog(PaymentSplitActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Globals.splitPsyMd.clear();
                    Intent intent = new Intent(PaymentSplitActivity.this, PaymentActivity.class);
                    intent.putExtra("opr", opr);
                    intent.putExtra("order_code", strOrderCode);
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

    private void call_remaining_code() {
        opr = "";
        strOrderCode = "";
        Globals.CheckContact = "0";
        Globals.setEmpty();
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
            }
        });
        String contactCode = Globals.strContact_Code;
        Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + contactCode + "'");
        if (contact == null) {
        } else {
            if (contact.get_email_1().equals("")) {
            } else {
                if (settings.get_Is_email().equals("true")) {
                    String strEmail = contact.get_email_1();
                    send_email(strEmail);
                }
            }
        }
        try {
            String printer_id = settings.getPrinterId();
            if (isNetworkStatusAvialable(getApplicationContext())) {
                String ck_projct_type = "";
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                try {
                    ck_projct_type = lite_pos_registration.getproject_id();
                } catch (Exception e) {
                    ck_projct_type = "";
                }
                if (ck_projct_type.equals("cloud") && settings.get_IsOnline().equals("true")) {
                    String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'");
                    if (result_order.equals("1")) {
                        if (settings.get_Is_KOT_Print().equals("true")) {
                            if (PrinterType.equals("1")) {
                                try {
                                    if (woyouService == null) {
                                    } else {
                                        print_kot(strOrderNo);
                                    }
                                } catch (Exception ex) {
                                }

                            } else if (PrinterType.equals("6")) {
                                print_kot_phapos(strOrderNo);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        String strTableQry = "";
                                        Cursor cursor1;
                                        if (settings.get_Is_Accounts().equals("true")) {
                                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                    //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                                } else {
                                                    Double showAmount = 0d;
                                                    //                                  String curAmount = "";
                                                    try {
                                                        String strCreditAmt = "", strDeditAmount = "";
                                                        Double creditAmount = 0d,
                                                                debitAmount = 0d;
                                                        Cursor cursor = null;

                                                        String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                        cursor = database.rawQuery(strQry1, null);
                                                        while (cursor.moveToNext()) {
                                                            strCreditAmt = cursor.getString(0);

                                                        }
                                                        creditAmount = Double.parseDouble(strCreditAmt);

                                                        String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                        cursor = database.rawQuery(strQry2, null);
                                                        while (cursor.moveToNext()) {
                                                            strDeditAmount = cursor.getString(0);

                                                        }
                                                        debitAmount = Double.parseDouble(strDeditAmount);
                                                        showAmount = debitAmount + creditAmount;
                                                    } catch (Exception ex) {
//                                        		curAmount = "0";
                                                    }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    } else {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = cursor1.getString(0);

                                                        }
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    Double strBalance = abs1 - Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();


                                                if (Globals.strContact_Code.equals("")) {
                                                    //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                                } else {
                                                    String curAmount = "";
                                                    try {
                                                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);
                                                        if (cursor1.moveToFirst()) {
                                                            do {
                                                                curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                            } while (cursor1.moveToNext());
                                                        }
                                                    } catch (Exception ex) {
                                                        curAmount = "0";
                                                    }
                                                    double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    } else {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        }
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }

                                                    Double strBalance = abs1 + Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                                }
                                            }
                                        }
                                        Globals.strOldCrAmt = "0";
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";
                                        Toast.makeText(PaymentSplitActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if (settings.get_Home_Layout().equals("2")){
                                            Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                });

                            }
                        }
                        if (settings.get_Is_Print_Invoice().equals("true")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
//                                    if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                        printDirect.PrintWithoutDialog(PaymentSplitActivity.this,strOrderNo,"","","");
//                                    } else {
                                    Intent launchIntent = new Intent(PaymentSplitActivity.this, PrintLayout.class);
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.putExtra("strOrderNo", strOrderNo);
                                    PaymentSplitActivity.this.startActivity(launchIntent);
//                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
                                                try {
                                                    String strCreditAmt = "", strDeditAmount = "";
                                                    Double creditAmount = 0d,
                                                            debitAmount = 0d;
                                                    Cursor cursor = null;

                                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                    cursor = database.rawQuery(strQry1, null);
                                                    while (cursor.moveToNext()) {
                                                        strCreditAmt = cursor.getString(0);

                                                    }
                                                    creditAmount = Double.parseDouble(strCreditAmt);

                                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                    cursor = database.rawQuery(strQry2, null);
                                                    while (cursor.moveToNext()) {
                                                        strDeditAmount = cursor.getString(0);

                                                    }
                                                    debitAmount = Double.parseDouble(strDeditAmount);
                                                    showAmount = debitAmount + creditAmount;
                                                } catch (Exception ex) {
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = cursor1.getString(0);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                String curAmount = "";
                                                try {
                                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);
                                                    if (cursor1.moveToFirst()) {
                                                        do {
                                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        } while (cursor1.moveToNext());
                                                    }
                                                } catch (Exception ex) {
                                                    curAmount = "0";
                                                }
                                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                double abs1 = Math.abs(ab);
                                                if (ab > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }

                                                Double strBalance = abs1 + Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";

                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    } else {
                        if (settings.get_Is_KOT_Print().equals("true")) {
                            if (PrinterType.equals("1")) {
                                try {
                                    if (woyouService == null) {
                                    } else {
                                        print_kot(strOrderNo);
                                    }
                                } catch (Exception ex) {
                                }
                            } else if (PrinterType.equals("6")) {
                                print_kot_phapos(strOrderNo);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String strTableQry = "";
                                        Cursor cursor1;
                                        if (settings.get_Is_Accounts().equals("true")) {
                                            order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                            if (ck_project_type.equals("standalone")) {
                                                JSONObject jsonObject = new JSONObject();
                                                if (Globals.strContact_Code.equals("")) {
                                                } else {
                                                    Double showAmount = 0d;
                                                    //                                  String curAmount = "";
                                                    try {
                                                        String strCreditAmt = "", strDeditAmount = "";
                                                        Double creditAmount = 0d,
                                                                debitAmount = 0d;
                                                        Cursor cursor = null;

                                                        String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                        cursor = database.rawQuery(strQry1, null);
                                                        while (cursor.moveToNext()) {
                                                            strCreditAmt = cursor.getString(0);
                                                        }
                                                        creditAmount = Double.parseDouble(strCreditAmt);

                                                        String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                        cursor = database.rawQuery(strQry2, null);
                                                        while (cursor.moveToNext()) {
                                                            strDeditAmount = cursor.getString(0);

                                                        }
                                                        debitAmount = Double.parseDouble(strDeditAmount);
                                                        showAmount = debitAmount + creditAmount;
                                                    } catch (Exception ex) {
                                                    }
                                                    double abs1 = Math.abs(showAmount);
                                                    if (showAmount > 0) {
                                                    } else {
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = cursor1.getString(0);

                                                        }

                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);

                                                    }

                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    Double strBalance = abs1 - Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                                }
                                            } else {

                                                JSONObject jsonObject = new JSONObject();


                                                if (Globals.strContact_Code.equals("")) {
                                                    //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                                } else {
                                                    String curAmount = "";
                                                    try {
                                                        strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);
                                                        if (cursor1.moveToFirst()) {
                                                            do {
                                                                curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                            } while (cursor1.moveToNext());
                                                        }
                                                    } catch (Exception ex) {
                                                        curAmount = "0";
                                                    }
                                                    double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                    double abs1 = Math.abs(ab);
                                                    if (ab > 0) {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    } else {
                                                        //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                    }
                                                    try {
                                                        jsonObject.put("Old Amt", abs1 + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strCur = "";

                                                    try {
                                                        strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                        cursor1 = database.rawQuery(strTableQry, null);

                                                        while (cursor1.moveToNext()) {
                                                            strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        }
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    } catch (Exception ex) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    if (strCur.equals("")) {
                                                        strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    }
                                                    try {
                                                        jsonObject.put("Current Amt", strCur + "");
                                                    } catch (Exception ex) {

                                                    }

                                                    Double strBalance = abs1 + Double.parseDouble(strCur);
                                                    try {
                                                        jsonObject.put("Balance Amt", strBalance + "");
                                                    } catch (Exception ex) {

                                                    }
                                                    String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                    db.executeDML(strUpdatePayment, database);

                                                    //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                    //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                                }
                                            }
                                        }
                                        Globals.strOldCrAmt = "0";
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";
                                        Toast.makeText(PaymentSplitActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if (settings.get_Home_Layout().equals("2")){
                                            Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                            }
                        }
                        if (settings.get_Is_Print_Invoice().equals("true")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                    if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                        printDirect.PrintWithoutDialog(PaymentSplitActivity.this,strOrderNo,"","","");
//                                    } else {
                                    Intent launchIntent = new Intent(PaymentSplitActivity.this, PrintLayout.class);
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.putExtra("strOrderNo", strOrderNo);
                                    PaymentSplitActivity.this.startActivity(launchIntent);
//                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {


                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
                                                try {
                                                    String strCreditAmt = "", strDeditAmount = "";
                                                    Double creditAmount = 0d,
                                                            debitAmount = 0d;
                                                    Cursor cursor = null;

                                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                    cursor = database.rawQuery(strQry1, null);
                                                    while (cursor.moveToNext()) {
                                                        strCreditAmt = cursor.getString(0);

                                                    }
                                                    creditAmount = Double.parseDouble(strCreditAmt);

                                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                    cursor = database.rawQuery(strQry2, null);
                                                    while (cursor.moveToNext()) {
                                                        strDeditAmount = cursor.getString(0);

                                                    }
                                                    debitAmount = Double.parseDouble(strDeditAmount);
                                                    showAmount = debitAmount + creditAmount;
                                                } catch (Exception ex) {
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = cursor1.getString(0);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                String curAmount = "";
                                                try {
                                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);
                                                    if (cursor1.moveToFirst()) {
                                                        do {
                                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        } while (cursor1.moveToNext());
                                                    }
                                                } catch (Exception ex) {
                                                    curAmount = "0";
                                                }
                                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                double abs1 = Math.abs(ab);
                                                if (ab > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }

                                                Double strBalance = abs1 + Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    }
                } else {
                    pDialog.dismiss();
                    if (settings.get_Is_KOT_Print().equals("true")) {
                        if (PrinterType.equals("1")) {
                            try {
                                if (woyouService == null) {
                                } else {
                                    print_kot(strOrderNo);
                                }
                            } catch (Exception ex) {
                            }
                        } else if (PrinterType.equals("6")) {
                            print_kot_phapos(strOrderNo);
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    String strTableQry = "";
                                    Cursor cursor1;
                                    if (settings.get_Is_Accounts().equals("true")) {
                                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                        if (ck_project_type.equals("standalone")) {
                                            JSONObject jsonObject = new JSONObject();
                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                Double showAmount = 0d;
                                                //                                  String curAmount = "";
                                                try {
                                                    String strCreditAmt = "", strDeditAmount = "";
                                                    Double creditAmount = 0d,
                                                            debitAmount = 0d;
                                                    Cursor cursor = null;

                                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                    cursor = database.rawQuery(strQry1, null);
                                                    while (cursor.moveToNext()) {
                                                        strCreditAmt = cursor.getString(0);

                                                    }
                                                    creditAmount = Double.parseDouble(strCreditAmt);

                                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                    cursor = database.rawQuery(strQry2, null);
                                                    while (cursor.moveToNext()) {
                                                        strDeditAmount = cursor.getString(0);

                                                    }
                                                    debitAmount = Double.parseDouble(strDeditAmount);
                                                    showAmount = debitAmount + creditAmount;
                                                } catch (Exception ex) {
//                                        		curAmount = "0";
                                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                                double abs1 = Math.abs(showAmount);
                                                if (showAmount > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = cursor1.getString(0);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }
                                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                            }
                                        } else {

                                            JSONObject jsonObject = new JSONObject();


                                            if (Globals.strContact_Code.equals("")) {
                                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                            } else {
                                                String curAmount = "";
                                                try {
                                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);
                                                    if (cursor1.moveToFirst()) {
                                                        do {
                                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                        } while (cursor1.moveToNext());
                                                    }
                                                } catch (Exception ex) {
                                                    curAmount = "0";
                                                }
                                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                                double abs1 = Math.abs(ab);
                                                if (ab > 0) {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                } else {
                                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Old Amt", abs1 + "");
                                                } catch (Exception ex) {

                                                }
                                                String strCur = "";

                                                try {
                                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                    cursor1 = database.rawQuery(strTableQry, null);

                                                    while (cursor1.moveToNext()) {
                                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    }
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                } catch (Exception ex) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                if (strCur.equals("")) {
                                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                }
                                                try {
                                                    jsonObject.put("Current Amt", strCur + "");
                                                } catch (Exception ex) {

                                                }

                                                Double strBalance = abs1 + Double.parseDouble(strCur);
                                                try {
                                                    jsonObject.put("Balance Amt", strBalance + "");
                                                } catch (Exception ex) {

                                                }
                                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                                db.executeDML(strUpdatePayment, database);

                                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                            }
                                        }
                                    }
                                    Globals.strOldCrAmt = "0";
                                    Globals.strContact_Code = "";
                                    Globals.strResvContact_Code = "";
                                    Toast.makeText(PaymentSplitActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                    if (settings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (settings.get_Home_Layout().equals("2")){
                                        Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        }
                    }
                    if (settings.get_Is_Print_Invoice().equals("true")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent launchIntent = new Intent(PaymentSplitActivity.this, PrintLayout.class);
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchIntent.putExtra("strOrderNo", strOrderNo);
                                PaymentSplitActivity.this.startActivity(launchIntent);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = cursor1.getString(0);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }
                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = abs1 + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }

                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                if (settings.get_Home_Layout().equals("0")) {

                                    Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            } else {

                if (settings.get_Is_KOT_Print().equals("true")) {
                    if (PrinterType.equals("1")) {
                        try {
                            if (woyouService == null) {
                            } else {
                                print_kot(strOrderNo);
                            }
                        } catch (Exception ex) {
                        }
                    } else if (PrinterType.equals("6")) {
                        print_kot_phapos(strOrderNo);
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = cursor1.getString(0);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }
                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = abs1 + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Toast.makeText(PaymentSplitActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    }
                }
                if (settings.get_Is_Print_Invoice().equals("true")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                                printDirect.PrintWithoutDialog(PaymentSplitActivity.this,strOrderNo,"","","");
//                            } else {
                            Intent launchIntent = new Intent(PaymentSplitActivity.this, PrintLayout.class);
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchIntent.putExtra("strOrderNo", strOrderNo);
                            PaymentSplitActivity.this.startActivity(launchIntent);
//                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

//
                            String strTableQry = "";
                            Cursor cursor1;
                            if (settings.get_Is_Accounts().equals("true")) {
                                order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                if (ck_project_type.equals("standalone")) {
                                    JSONObject jsonObject = new JSONObject();
                                    if (Globals.strContact_Code.equals("")) {
                                        //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                    } else {
                                        Double showAmount = 0d;
                                        //                                  String curAmount = "";
                                        try {
                                            String strCreditAmt = "", strDeditAmount = "";
                                            Double creditAmount = 0d,
                                                    debitAmount = 0d;
                                            Cursor cursor = null;

                                            String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                            cursor = database.rawQuery(strQry1, null);
                                            while (cursor.moveToNext()) {
                                                strCreditAmt = cursor.getString(0);

                                            }
                                            creditAmount = Double.parseDouble(strCreditAmt);

                                            String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                            cursor = database.rawQuery(strQry2, null);
                                            while (cursor.moveToNext()) {
                                                strDeditAmount = cursor.getString(0);

                                            }
                                            debitAmount = Double.parseDouble(strDeditAmount);
                                            showAmount = debitAmount + creditAmount;
                                        } catch (Exception ex) {
//                                        		curAmount = "0";
                                        }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                        double abs1 = Math.abs(showAmount);
                                        if (showAmount > 0) {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        } else {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        }
                                        try {
                                            jsonObject.put("Old Amt", abs1 + "");
                                        } catch (Exception ex) {

                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = cursor1.getString(0);

                                            }
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {

                                        }
                                        Double strBalance = abs1 - Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {

                                        }
                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                    }
                                } else {

                                    JSONObject jsonObject = new JSONObject();


                                    if (Globals.strContact_Code.equals("")) {
                                        //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                    } else {
                                        String curAmount = "";
                                        try {
                                            strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);
                                            if (cursor1.moveToFirst()) {
                                                do {
                                                    curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                } while (cursor1.moveToNext());
                                            }
                                        } catch (Exception ex) {
                                            curAmount = "0";
                                        }
                                        double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                        double abs1 = Math.abs(ab);
                                        if (ab > 0) {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        } else {
                                            //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                        }
                                        try {
                                            jsonObject.put("Old Amt", abs1 + "");
                                        } catch (Exception ex) {

                                        }
                                        String strCur = "";

                                        try {
                                            strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                            cursor1 = database.rawQuery(strTableQry, null);

                                            while (cursor1.moveToNext()) {
                                                strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                            }
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        } catch (Exception ex) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                        if (strCur.equals("")) {
                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                            //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        }
                                        try {
                                            jsonObject.put("Current Amt", strCur + "");
                                        } catch (Exception ex) {

                                        }

                                        Double strBalance = abs1 + Double.parseDouble(strCur);
                                        try {
                                            jsonObject.put("Balance Amt", strBalance + "");
                                        } catch (Exception ex) {

                                        }
                                        String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                        db.executeDML(strUpdatePayment, database);

                                        //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                        //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                    }
                                }
                            }
                            Globals.strOldCrAmt = "0";
                            Globals.strContact_Code = "";
                            Globals.strResvContact_Code = "";
                            if (settings.get_Home_Layout().equals("0")) {
                                Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (settings.get_Home_Layout().equals("2")){
                                Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }


        } catch (Exception e) {

            if (isNetworkStatusAvialable(getApplicationContext())) {
                String ck_projct_type = "";
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                try {
                    ck_projct_type = lite_pos_registration.getproject_id();
                } catch (Exception ex) {
                    ck_projct_type = "";
                }
                if (ck_projct_type.equals("cloud") && settings.get_IsOnline().equals("true")) {
                    String result_order = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'");

                    if (result_order.equals("1")) {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = cursor1.getString(0);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }
                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = abs1 + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Toast.makeText(PaymentSplitActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {


                                String strTableQry = "";
                                Cursor cursor1;
                                if (settings.get_Is_Accounts().equals("true")) {
                                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                                    if (ck_project_type.equals("standalone")) {
                                        JSONObject jsonObject = new JSONObject();
                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            Double showAmount = 0d;
                                            //                                  String curAmount = "";
                                            try {
                                                String strCreditAmt = "", strDeditAmount = "";
                                                Double creditAmount = 0d,
                                                        debitAmount = 0d;
                                                Cursor cursor = null;

                                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                                cursor = database.rawQuery(strQry1, null);
                                                while (cursor.moveToNext()) {
                                                    strCreditAmt = cursor.getString(0);

                                                }
                                                creditAmount = Double.parseDouble(strCreditAmt);

                                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                                cursor = database.rawQuery(strQry2, null);
                                                while (cursor.moveToNext()) {
                                                    strDeditAmount = cursor.getString(0);

                                                }
                                                debitAmount = Double.parseDouble(strDeditAmount);
                                                showAmount = debitAmount + creditAmount;
                                            } catch (Exception ex) {
//                                        		curAmount = "0";
                                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                            double abs1 = Math.abs(showAmount);
                                            if (showAmount > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = cursor1.getString(0);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }
                                            Double strBalance = abs1 - Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                                        }
                                    } else {

                                        JSONObject jsonObject = new JSONObject();


                                        if (Globals.strContact_Code.equals("")) {
                                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                                        } else {
                                            String curAmount = "";
                                            try {
                                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);
                                                if (cursor1.moveToFirst()) {
                                                    do {
                                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                    } while (cursor1.moveToNext());
                                                }
                                            } catch (Exception ex) {
                                                curAmount = "0";
                                            }
                                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                            double abs1 = Math.abs(ab);
                                            if (ab > 0) {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            } else {
                                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Old Amt", abs1 + "");
                                            } catch (Exception ex) {

                                            }
                                            String strCur = "";

                                            try {
                                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                                cursor1 = database.rawQuery(strTableQry, null);

                                                while (cursor1.moveToNext()) {
                                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                                }
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            } catch (Exception ex) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            if (strCur.equals("")) {
                                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            }
                                            try {
                                                jsonObject.put("Current Amt", strCur + "");
                                            } catch (Exception ex) {

                                            }

                                            Double strBalance = abs1 + Double.parseDouble(strCur);
                                            try {
                                                jsonObject.put("Balance Amt", strBalance + "");
                                            } catch (Exception ex) {

                                            }
                                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                            db.executeDML(strUpdatePayment, database);

                                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                            //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                                        }
                                    }
                                }
                                Globals.strOldCrAmt = "0";
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                if (settings.get_Home_Layout().equals("0")) {
                                    Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (settings.get_Home_Layout().equals("2")){
                                    Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                } else {


                    String strTableQry = "";
                    Cursor cursor1;
                    if (settings.get_Is_Accounts().equals("true")) {
                        order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                        if (ck_project_type.equals("standalone")) {
                            JSONObject jsonObject = new JSONObject();
                            if (Globals.strContact_Code.equals("")) {
                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                            } else {
                                Double showAmount = 0d;
                                //                                  String curAmount = "";
                                try {
                                    String strCreditAmt = "", strDeditAmount = "";
                                    Double creditAmount = 0d,
                                            debitAmount = 0d;
                                    Cursor cursor = null;

                                    String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                    cursor = database.rawQuery(strQry1, null);
                                    while (cursor.moveToNext()) {
                                        strCreditAmt = cursor.getString(0);

                                    }
                                    creditAmount = Double.parseDouble(strCreditAmt);

                                    String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                    cursor = database.rawQuery(strQry2, null);
                                    while (cursor.moveToNext()) {
                                        strDeditAmount = cursor.getString(0);

                                    }
                                    debitAmount = Double.parseDouble(strDeditAmount);
                                    showAmount = debitAmount + creditAmount;
                                } catch (Exception ex) {
//                                        		curAmount = "0";
                                }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                                double abs1 = Math.abs(showAmount);
                                if (showAmount > 0) {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                } else {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                }
                                try {
                                    jsonObject.put("Old Amt", abs1 + "");
                                } catch (Exception ex) {

                                }
                                String strCur = "";

                                try {
                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        strCur = cursor1.getString(0);

                                    }
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                try {
                                    jsonObject.put("Current Amt", strCur + "");
                                } catch (Exception ex) {

                                }
                                Double strBalance = abs1 - Double.parseDouble(strCur);
                                try {
                                    jsonObject.put("Balance Amt", strBalance + "");
                                } catch (Exception ex) {

                                }
                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                db.executeDML(strUpdatePayment, database);

                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                            }
                        } else {

                            JSONObject jsonObject = new JSONObject();


                            if (Globals.strContact_Code.equals("")) {
                                //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                            } else {
                                String curAmount = "";
                                try {
                                    strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);
                                    if (cursor1.moveToFirst()) {
                                        do {
                                            curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                        } while (cursor1.moveToNext());
                                    }
                                } catch (Exception ex) {
                                    curAmount = "0";
                                }
                                double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                                double abs1 = Math.abs(ab);
                                if (ab > 0) {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                } else {
                                    //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                                }
                                try {
                                    jsonObject.put("Old Amt", abs1 + "");
                                } catch (Exception ex) {

                                }
                                String strCur = "";

                                try {
                                    strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                    cursor1 = database.rawQuery(strTableQry, null);

                                    while (cursor1.moveToNext()) {
                                        strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                    }
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                } catch (Exception ex) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
                                if (strCur.equals("")) {
                                    strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                    //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                }
                                try {
                                    jsonObject.put("Current Amt", strCur + "");
                                } catch (Exception ex) {

                                }

                                Double strBalance = abs1 + Double.parseDouble(strCur);
                                try {
                                    jsonObject.put("Balance Amt", strBalance + "");
                                } catch (Exception ex) {

                                }
                                String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                                db.executeDML(strUpdatePayment, database);

                                //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                                //woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                            }
                        }
                    }
                    Globals.strOldCrAmt = "0";
                    Globals.strContact_Code = "";
                    Globals.strResvContact_Code = "";
                    if (settings.get_Home_Layout().equals("0")) {
                        Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (settings.get_Home_Layout().equals("2")){
                        Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {


                String strTableQry = "";
                Cursor cursor1;
                if (settings.get_Is_Accounts().equals("true")) {
                    order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + strOrderNo + "'");
                    if (ck_project_type.equals("standalone")) {
                        JSONObject jsonObject = new JSONObject();
                        if (Globals.strContact_Code.equals("")) {
                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                        } else {
                            Double showAmount = 0d;
                            //                                  String curAmount = "";
                            try {
                                String strCreditAmt = "", strDeditAmount = "";
                                Double creditAmount = 0d,
                                        debitAmount = 0d;
                                Cursor cursor = null;

                                String strQry1 = "Select SUM(paid_amount - cr_amount) FROM Acc_Customer_Credit where contact_code ='" + Globals.strContact_Code + "'";
                                cursor = database.rawQuery(strQry1, null);
                                while (cursor.moveToNext()) {
                                    strCreditAmt = cursor.getString(0);

                                }
                                creditAmount = Double.parseDouble(strCreditAmt);

                                String strQry2 = "Select SUM(amount) from acc_customer_dedit Where order_code IN (Select Order_code from orders where contact_code ='" + Globals.strContact_Code + "')";
                                cursor = database.rawQuery(strQry2, null);
                                while (cursor.moveToNext()) {
                                    strDeditAmount = cursor.getString(0);

                                }
                                debitAmount = Double.parseDouble(strDeditAmount);
                                showAmount = debitAmount + creditAmount;
                            } catch (Exception ex) {
//                                        		curAmount = "0";
                            }
//                                    			double abs1 = Math.abs(Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount));
                            double abs1 = Math.abs(showAmount);
                            if (showAmount > 0) {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            } else {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            }
                            try {
                                jsonObject.put("Old Amt", abs1 + "");
                            } catch (Exception ex) {

                            }
                            String strCur = "";

                            try {
                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                cursor1 = database.rawQuery(strTableQry, null);

                                while (cursor1.moveToNext()) {
                                    strCur = cursor1.getString(0);

                                }
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            } catch (Exception ex) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", Globals.myNumberFormat2Price(Double.parseDouble(strCur), decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
//                                        if (strCur.equals("")) {
//                                            strCur = Globals.myNumberFormat2Price(0, decimal_check);
//
//                                        }
//                                        //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            try {
                                jsonObject.put("Current Amt", strCur + "");
                            } catch (Exception ex) {

                            }
                            Double strBalance = abs1 - Double.parseDouble(strCur);
                            try {
                                jsonObject.put("Balance Amt", strBalance + "");
                            } catch (Exception ex) {

                            }
                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                            db.executeDML(strUpdatePayment, database);

                            //woyouService.printColumnsText(new String[]{"Balance Amt", ":", Globals.myNumberFormat2Price(strBalance, decimal_check)}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            ///woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                        }
                    } else {

                        JSONObject jsonObject = new JSONObject();


                        if (Globals.strContact_Code.equals("")) {
                            //woyouService.printColumnsText(new String[]{"**"}, new int[]{10}, new int[]{0}, callback);
                        } else {
                            String curAmount = "";
                            try {
                                strTableQry = "Select sum(pay_amount) from order_payment left join orders on orders.order_code = order_payment.order_code where orders.order_code In(select orders.order_code from orders where orders.contact_code = '" + Globals.strContact_Code + "' and orders.z_code='0' and orders.order_code !='" + strOrderNo + "') and order_payment.payment_id='5'";
                                cursor1 = database.rawQuery(strTableQry, null);
                                if (cursor1.moveToFirst()) {
                                    do {
                                        curAmount = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                    } while (cursor1.moveToNext());
                                }
                            } catch (Exception ex) {
                                curAmount = "0";
                            }
                            double ab = Double.parseDouble(Globals.strOldCrAmt) + Double.parseDouble(curAmount);
                            double abs1 = Math.abs(ab);
                            if (ab > 0) {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "CR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            } else {
                                //woyouService.printColumnsText(new String[]{"Old Amt", ":", Globals.myNumberFormat2Price(abs1, decimal_check), "DR"}, new int[]{7, 1, 12, 4}, new int[]{0, 0, 0, 0}, callback);
                            }
                            try {
                                jsonObject.put("Old Amt", abs1 + "");
                            } catch (Exception ex) {

                            }
                            String strCur = "";

                            try {
                                strTableQry = "Select pay_amount from order_payment where order_code = '" + strOrderNo + "' and payment_id='5'";
                                cursor1 = database.rawQuery(strTableQry, null);

                                while (cursor1.moveToNext()) {
                                    strCur = Globals.myNumberFormat2Price(Double.parseDouble(cursor1.getString(0)), decimal_check);

                                }
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            } catch (Exception ex) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            if (strCur.equals("")) {
                                strCur = Globals.myNumberFormat2Price(0, decimal_check);
                                //woyouService.printColumnsText(new String[]{"Current Amt", ":", strCur}, new int[]{11, 1, 20}, new int[]{0, 0, 0}, callback);
                            }
                            try {
                                jsonObject.put("Current Amt", strCur + "");
                            } catch (Exception ex) {

                            }

                            Double strBalance = abs1 + Double.parseDouble(strCur);
                            try {
                                jsonObject.put("Balance Amt", strBalance + "");
                            } catch (Exception ex) {

                            }
                            String strUpdatePayment = " Update order_payment set field2 = '" + jsonObject.toString() + "' where order_payment_id = '" + order_payment_array.get(0).get_order_payment_id() + "'";
                            db.executeDML(strUpdatePayment, database);
                        }
                    }
                }
                Globals.strOldCrAmt = "0";
                Globals.strContact_Code = "";
                Globals.strResvContact_Code = "";
                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(PaymentSplitActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (settings.get_Home_Layout().equals("2")){
                    Intent intent = new Intent(PaymentSplitActivity.this, RetailActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(PaymentSplitActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }

        }

    }


    private void startWhatsApp() {
        String strContct = "";
        contact = Contact.getContact(getApplicationContext(), database, db, " where contact_code='" + Globals.strContact_Code + "'");
        if (contact == null) {
        } else {
            strContct = contact.get_contact_1();
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" + objOrder.get_order_code() + ".pdf");
        if (contactExists(getApplicationContext(), strContct)) {

            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {

            if (SaveContact()) {
                Toast.makeText(getBaseContext(), "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file) {
        Uri path = Uri.fromFile(file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null && cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean SaveContact() {
        //Get text
        String szFirstname = contact.get_name(),
                szPhone = "+91" + " " + contact.get_contact_1();

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //Create a new contact entry!

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFirstname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, szFirstname) // Name of the person
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szPhone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        //INSERT PITURE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        stream.toByteArray())
                .build());

        Uri newContactUri = null;

        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
            }
        } catch (RemoteException e) {
            // error
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            newContactUri = null;
        }
        return newContactUri != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        call_remaining_code();

    }

    private void stock_deduct(String item_code, String curQty) {
        try {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
            if (item_location == null) {
            } else {
                Double reming_qty = Double.parseDouble(item_location.get_quantity()) - Double.parseDouble(curQty);
                item_location.set_quantity(reming_qty + "");
                long itmlc = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
            }
        } catch (Exception ex) {

        }
    }

    private void open_cash_drawer() {
        try {
            woyouService.openDrawer(callback1);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            byte[] aa = new byte[4];

            aa[0] = 0x10;
            aa[1] = 0x14;
            aa[2] = 0x00;
            aa[3] = 0x00;

            try {
                woyouService.sendRAWData(aa, callback1);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
