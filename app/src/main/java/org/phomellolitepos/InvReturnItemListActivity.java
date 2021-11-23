package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.phomellolitepos.Adapter.InvReturnItemListadapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Settings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvReturnItemListActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Dialog listDialog;
    Settings settings;
    InvReturnItemListadapter returnListAdapter;
    TextView item_title;
    String relt = "", PayId, cusCode;
    ArrayList itemlist;
    String operation, str_voucher_no, str_date, str_remarks, ordCode;
    String decimal_check, qty_decimal_check;
    ArrayList<Order_Detail> order_detail = new ArrayList<Order_Detail>();
    ArrayList<Object> object;
    ArrayList<Object> itemcodelist;
    ArrayList<Object> barcodelist;
    ArrayList<Object> return_qtylist;
    ArrayList<Object> qtylist;
    ArrayList<Object> pricelist;

    ListView category_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_return_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        listDialog = new Dialog(this);
        settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {

            decimal_check = "1";
        }
        try {
            Intent intent = getIntent();
            getSupportActionBar().setTitle("");
            operation = intent.getStringExtra("operation");
            str_voucher_no = intent.getStringExtra("voucher_no");
            str_date = intent.getStringExtra("date");
            str_remarks = intent.getStringExtra("remarks");
            PayId = intent.getStringExtra("payment_id");
            ordCode = intent.getStringExtra("order_code");
            cusCode = intent.getStringExtra("contact_code");
            Bundle args = intent.getBundleExtra("BUNDLE");
            object = (ArrayList<Object>) args.getSerializable("ARRAYLIST");

            itemcodelist = (ArrayList<Object>) args.getSerializable("ItemCodeList");
            barcodelist = (ArrayList<Object>) args.getSerializable("BarCodeList");
            return_qtylist = (ArrayList<Object>) args.getSerializable("ret_quantitylist");
            qtylist = (ArrayList<Object>) args.getSerializable("quantitylist");
            pricelist = (ArrayList<Object>) args.getSerializable("pricelist");
            order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), "where order_code='" + ordCode + "'", database);
            category_list = (ListView) findViewById(R.id.item_list);

            returnListAdapter = new InvReturnItemListadapter(InvReturnItemListActivity.this, itemcodelist, object,qtylist,return_qtylist,pricelist);
            category_list.setAdapter(returnListAdapter);
            returnListAdapter.notifyDataSetChanged();

            /*category_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    ArrayList<String> arraylist = new ArrayList<String>();
                    arraylist.add(itemcodelist.toString());
                    String TempListViewClickedValue = arraylist.get(position).toString();

                    Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
                    intent.putExtra("contact_code", cusCode);
                    intent.putExtra("order_code", ordCode);
                    intent.putExtra("itemcode", TempListViewClickedValue);
                    startActivity(intent);
                }
            });*/
        } catch (Exception e) {
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(InvReturnItemListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                           // sleep(100);
                            pDialog.dismiss();
                            Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
                            intent.putExtra("contact_code", cusCode);
                            intent.putExtra("order_code", ordCode);
                            Bundle args = new Bundle();
                            args.putSerializable("ARRAYLIST", (Serializable) object);
                            args.putSerializable("ItemCodeList", (Serializable) itemcodelist);
                            args.putSerializable("BarCodeList", (Serializable) barcodelist);
                            args.putSerializable("ret_quantitylist", (Serializable) return_qtylist);
                            args.putSerializable("quantitylist", (Serializable) qtylist);
                            args.putSerializable("pricelist", (Serializable) pricelist);
                            intent.putExtra("BUNDLE", args);


                            if (cusCode.equals("")) {
                                intent.putExtra("payment_id", "1");
                            } else {
                                intent.putExtra("payment_id", PayId);
                            }
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });
        //getSupportActionBar().setTitle(R.string.invoice_return);
        if (order_detail.size() == 0) {
            item_title = (TextView) findViewById(R.id.item_title);
            item_title.setText("NO Result Found");
        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(InvReturnItemListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                  //  sleep(100);
                    pDialog.dismiss();
                    Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
                    intent.putExtra("contact_code", cusCode);
                    intent.putExtra("order_code", ordCode);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) object);
                    args.putSerializable("ItemCodeList", (Serializable) itemcodelist);
                    args.putSerializable("BarCodeList", (Serializable) barcodelist);
                    args.putSerializable("ret_quantitylist", (Serializable) return_qtylist);
                    args.putSerializable("quantitylist", (Serializable) qtylist);
                    args.putSerializable("pricelist", (Serializable) pricelist);
                    intent.putExtra("BUNDLE", args);


                    if (cusCode.equals("")) {
                        intent.putExtra("payment_id", "1");
                    } else {
                        intent.putExtra("payment_id", PayId);
                    }
                    startActivity(intent);

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        timerThread.start();
    }

    public  void callIntent(String itemcodevalue,String itemnamevalue,String itemqtyvalue,String itemrtqtyvalue,String itemprice){

        Intent intent = new Intent(InvReturnItemListActivity.this, InvReturnFinalActivity.class);
      //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("operation", operation);
        intent.putExtra("voucher_no", str_voucher_no);
        intent.putExtra("date", str_date);
        intent.putExtra("remarks", str_remarks);
        intent.putExtra("contact_code", cusCode);
        intent.putExtra("order_code", ordCode);
        intent.putExtra("itemcode", itemcodevalue);
        intent.putExtra("itemname", itemnamevalue);
        intent.putExtra("itemqty", itemqtyvalue);
        intent.putExtra("itemreturnqty", itemrtqtyvalue);
        intent.putExtra("itemprice", itemprice);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable) object);
        args.putSerializable("ItemCodeList", (Serializable) itemcodelist);
        args.putSerializable("BarCodeList", (Serializable) barcodelist);
        args.putSerializable("ret_quantitylist", (Serializable) return_qtylist);
        args.putSerializable("quantitylist", (Serializable) qtylist);
        args.putSerializable("pricelist", (Serializable) pricelist);
        intent.putExtra("BUNDLE", args);
        if (cusCode.equals("")) {
            intent.putExtra("payment_id", "1");
        } else {
            intent.putExtra("payment_id", PayId);
        }
        startActivity(intent);
        finish();
    }

}
