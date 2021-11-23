package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;

import java.util.ArrayList;
import java.util.Random;

import org.phomellolitepos.Adapter.ReceiptDetailListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class ReceptDetailActivity extends AppCompatActivity {
    TextView txt_order_no, txt_date, txt_total, txt_tender, txt_change, txt_tax, txt_subtotal, txt_discount, txt_ttl_aftr_dis, txt_item_tax,txt_customername;
    ReceiptDetailListAdapter receiptDetailListAdapter;
    ArrayList<Order_Detail> arrayList;
    String order_code;
    Orders objOrder;
    String decimal_check;
    private static final String TAG = "PrinterTestDemo";
    public static final int DO_PRINT = 0x10001;
    private IWoyouService woyouService;
    private byte[] inputCommand;
    private final int RUNNABLE_LENGHT = 11;
    private Random random = new Random();
    private ICallback callback = null;
    Database db;
    SQLiteDatabase database;
    private String PrinterType = "";
    BluetoothService mService = null;
    private Settings settings;
    Contact contact;
    ProgressDialog progressDialog;
    String customername;
    TableLayout tl;
    LinearLayout layout_customer;
    //    PrintDirect printDirect = new PrintDirect();
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
                long mm = MemInfo.getmem_UNUSED(ReceptDetailActivity.this);
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
        setContentView(R.layout.activity_recept_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        arrayList = new ArrayList<Order_Detail>();
        Intent intent = getIntent();
        order_code = intent.getStringExtra("order_code");
        getSupportActionBar().setTitle(R.string.Receipt_Detail);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        txt_order_no = (TextView) findViewById(R.id.txt_order_no);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_total = (TextView) findViewById(R.id.txt_total);
        txt_tender = (TextView) findViewById(R.id.txt_tender);
        txt_change = (TextView) findViewById(R.id.txt_change);
        txt_tax = (TextView) findViewById(R.id.txt_tax);
        txt_subtotal = (TextView) findViewById(R.id.txt_subtotal);
        txt_discount = (TextView) findViewById(R.id.txt_discount);
        txt_ttl_aftr_dis = (TextView) findViewById(R.id.txt_ttl_aftr_dis);
        txt_item_tax = (TextView) findViewById(R.id.txt_item_tax);
        txt_customername = (TextView) findViewById(R.id.txt_customername);
        layout_customer = (LinearLayout) findViewById(R.id.Layout3);
        tl = (TableLayout) findViewById(R.id.tl);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

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
                progressDialog = new ProgressDialog(ReceptDetailActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ReceptDetailActivity.this, ReceptActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
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

        settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            PrinterType = settings.getPrinterId();
        } catch (Exception ex) {

            PrinterType = "0";
        }
        mService = MainActivity.mService;

        callback = new ICallback.Stub() {

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
        objOrder = Orders.getOrders(getApplicationContext(), database, " WHERE order_code = '" + order_code + "'");
        final ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(),
                "WHERE order_code = '" + order_code + "'", database);
        int count = 0;
        Double itemFinalTax = 0d;
        while (count < order_detail.size()) {
            Tax_Master tax_master = null;
            ArrayList<Order_Detail_Tax> order_detail_tax = Order_Detail_Tax.getAllOrder_Detail_Tax(getApplicationContext(), "WHERE item_code='" + order_detail.get(count).get_item_code() + "' And order_code = '" + order_detail.get(count).get_order_code() + "'", database);
            for (int i = 0; i < order_detail_tax.size(); i++) {

                double valueFinal = Double.parseDouble(order_detail_tax.get(i).get_tax_value()) * (Double.parseDouble(order_detail.get(count).get_quantity()));
                itemFinalTax += valueFinal;

            }

            count++;
        }
        Double itemTax = 0d;
        String strTableQry1 = "select order_detail_tax.tax_id,SUM(order_detail_tax.tax_value * order_detail.quantity) As Amt from order_detail_tax \n" +
                "inner join order_detail on order_detail.order_code = order_detail_tax.order_code and  order_detail.item_code = order_detail_tax.item_code\n" +
                "where order_detail.order_code ='" + order_code + "' group by  order_detail_tax.tax_id";
        Cursor cursor = database.rawQuery(strTableQry1, null);

        while (cursor.moveToNext()) {

            double valueItemTax = Double.parseDouble(cursor.getString(1));
            itemTax += valueItemTax;
        }

        txt_item_tax.setText(Globals.myNumberFormat2Price(itemTax, decimal_check));

        String subtotal = "";
        String strTableQry = "Select SUM(order_detail.sale_price*order_detail.quantity) From order_detail where order_detail.order_code ='" + order_code + "' ";
        Cursor cursor1 = database.rawQuery(strTableQry, null);
        Tax_Master tax_master;
        while (cursor1.moveToNext()) {
            subtotal = cursor1.getString(0);

        }
try {
    subtotal = Globals.myNumberFormat2Price((Double.parseDouble(subtotal)), decimal_check);
    txt_subtotal.setText(subtotal);
}
catch(Exception e){}

//        String strSubttl = null;
//        if (objOrder == null) {
//            txt_subtotal.setText("");
//        } else {
//            strSubttl = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_sub_total()), decimal_check);
//            txt_subtotal.setText(strSubttl);
//        }


        txt_order_no.setText(objOrder.get_order_code());
        txt_date.setText(objOrder.get_order_date());
        try {
            contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + objOrder.get_contact_code() + "'");
           if(contact==null){
               txt_customername.setVisibility(View.GONE);
               layout_customer.setVisibility(View.GONE);
           }
           else {
               customername = contact.get_name();
               layout_customer.setVisibility(View.VISIBLE);
               txt_customername.setText(customername);
           }
        }
        catch(Exception e){

        }
        String strNetAmount = String.valueOf(objOrder.get_total());
        if (strNetAmount.equals("null")) {
            txt_total.setText("");
        } else {
            String net_amount;

            net_amount = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total()), decimal_check);
            String strCurrency;
            if (Globals.objLPD.getCurreny_Symbol().equals("")) {
                strCurrency = "";
            } else {
                strCurrency = "(" + Globals.objLPD.getCurreny_Symbol() + ")";
            }
            txt_total.setText(net_amount + "" + strCurrency);
        }

        String strtender = String.valueOf(objOrder.get_tender());
        if (strtender.equals("null")) {
            txt_tender.setText("");
        } else {
            String tender;
            tender = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_tender()), decimal_check);

            txt_tender.setText(tender);
        }

        if (strtender.equals("null")) {
            txt_tax.setText("");
        } else {
            String tax;
            tax = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_tax()), decimal_check);

            txt_tax.setText(tax);
        }

        String strDiscount;

        strDiscount = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_total_discount()), decimal_check);
        txt_discount.setText(strDiscount);


        String strChange = String.valueOf(objOrder.get_change_amount());
        if (strChange.equals("null")) {
            txt_change.setText("");
        } else {
            String change_amount;
            change_amount = Globals.myNumberFormat2Price(Double.parseDouble(objOrder.get_change_amount()), decimal_check);
            txt_change.setText(change_amount);
        }

//        String ttl_aftr_dis = (Double.parseDouble(strSubttl) - Double.parseDouble(strDiscount)) + "";
        String ttl_aftr_dis = (Double.parseDouble(subtotal) + itemFinalTax - Double.parseDouble(strDiscount)) + "";
        String tatalAftrDis = Globals.myNumberFormat2Price(Double.parseDouble(ttl_aftr_dis), decimal_check);
        txt_ttl_aftr_dis.setText(tatalAftrDis);

        get_receipt_detail_list(order_code);

        String strTotal;
        ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(getApplicationContext(), " where order_code='" + order_code + "'");
        if (order_payment_array.size() > 0) {
            for (int i = 0; i < order_payment_array.size(); i++) {
                Payment payment = Payment.getPayment(getApplicationContext(), " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");
                String name = "";
                if (payment != null) {
                    name = payment.get_payment_name();
                    strTotal = Globals.myNumberFormat2Price(Double.parseDouble(order_payment_array.get(i).get_pay_amount()), decimal_check);
                    TableRow tr = new TableRow(this);
                    TextView tv1 = new TextView(this);
                    TextView tv2 = new TextView(this);
                    TextView tv3 = new TextView(this);
                    tr.setGravity(1);

                    tv1.setTextColor(Color.parseColor("#333333"));
                    tv1.setTextSize(16);
                    tv1.setText(name);
                    tr.addView(tv1);

                    tv2.setTextColor(Color.parseColor("#333333"));
                    tv2.setTextSize(16);
                    tv2.setText(" : ");
                    tr.addView(tv2);

                    tv3.setTextColor(Color.parseColor("#333333"));
                    tv3.setTextSize(16);
                    tv3.setText(strTotal);
                    tr.addView(tv3);
                    tl.addView(tr);
                }
            }
        }
    }

    private void get_receipt_detail_list(String order_code) {
        arrayList = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + order_code + "'", database);
        ListView category_list = (ListView) findViewById(R.id.recept_detail_list);
        receiptDetailListAdapter = new ReceiptDetailListAdapter(ReceptDetailActivity.this, arrayList);
        category_list.setAdapter(receiptDetailListAdapter);
        category_list.setTextFilterEnabled(true);
        receiptDetailListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.receipt_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Print) {
            if (PrinterType.equals("") || PrinterType.equals("0")) {
                Toast.makeText(getApplicationContext(), R.string.chkpriset, Toast.LENGTH_SHORT).show();
            } else {
                String flag = "RDA";
                Intent launchIntent = new Intent(ReceptDetailActivity.this, PrintLayout.class);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.putExtra("strOrderNo", order_code);
                launchIntent.putExtra("flag", flag);
                launchIntent.putExtra("posflag","1");
                ReceptDetailActivity.this.startActivity(launchIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        progressDialog = new ProgressDialog(ReceptDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.Wait_msg));
        progressDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Intent intent = new Intent(ReceptDetailActivity.this, ReceptActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {}
            }
        };
        timerThread.start();
    }
}
