package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MediaController;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.User;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.ThreadPoolManager;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sunmi.sunmiui.edit.Edit;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;


public class TestActivity extends AppCompatActivity {
    EditText edt_from, edt_to, edt_nof_inv, edt_nof_item;
    Button btn_generate, btn_orderList;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Orders orders;
    Order_Detail order_detail;
    Order_Payment order_payment;
    Item item;
    String strFrom, strTo, strNofIvn, strNofItem, myFormat;
    Calendar myCalendar;
    SimpleDateFormat sdf;
    Lite_POS_Device lite_pos_device;
    String strOrderNo = "";
    String part2;
    String orderId = "";
    ArrayList<ShoppingCart> myCart;
    String strFlag;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        edt_from = (EditText) findViewById(R.id.edt_from);
        edt_to = (EditText) findViewById(R.id.edt_to);
        edt_nof_inv = (EditText) findViewById(R.id.edt_nof_inv);
        edt_nof_item = (EditText) findViewById(R.id.edt_nof_item);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_orderList = (Button) findViewById(R.id.btn_orderList);
        myCalendar = Calendar.getInstance();
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        final DatePickerDialog.OnDateSetListener date = new android.app.DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabe2();
            }

        };

        edt_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_from.getWindowToken(), 0);
                new DatePickerDialog(TestActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edt_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_to.getWindowToken(), 0);
                new DatePickerDialog(TestActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, ReceptActivity.class);
                startActivity(intent);
            }
        });

        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_from.getText().toString().trim().equals("")) {
                    edt_from.setError("Date required");
                    edt_from.requestFocus();
                    return;
                } else {
                    strFrom = edt_from.getText().toString().trim();
                }

                if (edt_to.getText().toString().trim().equals("")) {
                    edt_to.setError("Date required");
                    edt_to.requestFocus();
                    return;
                } else {
                    strTo = edt_to.getText().toString().trim();
                }

                if (edt_nof_inv.getText().toString().trim().equals("")) {
                    edt_nof_inv.setError("No Of Invoice Required");
                    edt_nof_inv.requestFocus();
                    return;
                } else {
                    strNofIvn = edt_nof_inv.getText().toString().trim();
                }

                if (edt_nof_item.getText().toString().trim().equals("")) {
                    edt_nof_item.setError("No Of Item Required");
                    edt_nof_item.requestFocus();
                    return;
                } else {
                    strNofItem = edt_nof_item.getText().toString().trim();
                }

                ArrayList<Item> itemArrayList = Item.getAllItem(getApplicationContext(), "", database);
                if (itemArrayList.size() > 0) {
                    if (Integer.parseInt(strNofItem) <= itemArrayList.size()) {
                        myFormat = "yyyy-MM-dd"; //In which you need put here
                        sdf = new SimpleDateFormat(myFormat, Locale.US);
                        final int startDate = getStartDay();
                        final int endDate = getEndDay();
                        int NoFDays = endDate - startDate;
                        if (NoFDays==0){
                            NoFDays=1;
                        }

                        progressDialog = new ProgressDialog(TestActivity.this);
                        progressDialog.setTitle("");
                        progressDialog.setMessage(getString(R.string.Wait_msg));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        final int finalNoFDays = NoFDays;
                        new Thread() {
                            @Override
                            public void run() {
                                GenerateInvoice(strNofIvn, strNofItem, startDate, endDate, finalNoFDays);
                                progressDialog.dismiss();
//                                                        runOnUiThread(new Runnable() {
//                                                            public void run() {
//                                                                Toast.makeText(getApplicationContext(), R.string.Database_Backup_Successfully, Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
                            }
                        }.start();

                    } else {
                        Toast.makeText(getApplicationContext(), "In System Only" + itemArrayList.size() + " Quantity available", Toast.LENGTH_SHORT).show();
                        edt_nof_item.setError("Enter again");
                        edt_nof_item.requestFocus();
                        return;
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Item found!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private int getStartDay() {
        Date DateForm = null;
        try {
            DateForm = sdf.parse(edt_from.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myCalendar.setTime(DateForm);
        return myCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getEndDay() {
        Date DateTo = null;
        try {
            DateTo = sdf.parse(edt_to.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myCalendar.setTime(DateTo);
        return myCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private String[] getDMY() {
        String[] dmy = new String[3];
        Date DateForm = null;
        try {
            DateForm = sdf.parse(edt_from.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myCalendar.setTime(DateForm);
        dmy[0] = myCalendar.get(Calendar.DAY_OF_MONTH) + "";
        String monthNumber = (String) DateFormat.format("MM", DateForm);
        dmy[1] = monthNumber;
        dmy[2] = myCalendar.get(Calendar.YEAR) + "";
        return dmy;
    }

    private void GenerateInvoice(String strNofIvn, String strNofItem, int startDate, int endDate, int noFDays) {

        int InvCount = Integer.parseInt(strNofIvn);
        if (InvCount > 0) {
            database.beginTransaction();
            for (int i = startDate; i <= endDate; i++) {

                for (int j = 1; j <= InvCount; j++) {
                    String[] strDmy = getDMY();
                    String day = strDmy[0];
                    String month = strDmy[1];
                    String year = strDmy[2];
                    String strOrderDate = year + "-" + month + "-" + i;

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

                    if (j==50){
                       String ab;

                    }

                    String netAmount = getNetAmount(strNofItem);

                    orders = new Orders(getApplicationContext(), null, Globals.Device_Code,
                            lite_pos_device.getLocation_Code(), Globals.strOrder_type_id, strOrderNo, strOrderDate, Globals.strContact_Code,
                            "0", strNofItem, strNofItem,
                            netAmount, "0",
                            "0", netAmount, netAmount,
                            "0", "0", "0",
                            "0", "1", "", strOrderDate, "N",
                            "CLOSE", "", Globals.strTable_Code, "");

                    long l = orders.insertOrders(database);
                    if (l > 0) {
                        strFlag = "1";
                        myCart = Globals.cart;
                        for (int count = 0; count < myCart.size(); count++) {
                            ShoppingCart mCart = myCart.get(count);
                            order_detail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                                    "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                                    mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                            long o = order_detail.insertOrder_Detail(database);
                            if (o > 0) {
                                strFlag = "1";
                            } else {
                            }
                        }
                    }

                    order_payment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", netAmount,
                            "1", "", "", "", "", "", "");
                    long op = order_payment.insertOrder_Payment(database);
                    if (op > 0) {
                        strFlag = "1";
                    } else {
                    }

                }
            }
            if (strFlag.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        resetAll();
                        Toast.makeText(getApplicationContext(), "Invoice generated sucessfully", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                database.endTransaction();
                progressDialog.dismiss();
            }
        } else {
            database.endTransaction();
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    resetAll();
                    Toast.makeText(getApplicationContext(), "Aleast one invoice needed!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void resetAll() {
        edt_nof_item.setText("");
        edt_nof_inv.setText("");
        edt_to.setText("");
        edt_from.setText("");
        edt_from.requestFocus();
    }

    private String getNetAmount(String strNofItem) {
        String sale_priceStr;
        String cost_priceStr;
        Double TotalAmt = 0d;
        String Amt;
        ArrayList<Item> itemArrayList = Item.getAllItem(getApplicationContext(), "", database);
        for (int i = 0; i < Integer.parseInt(strNofItem); i++) {
            Item resultp = itemArrayList.get(i);
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "where item_code='" + resultp.get_item_code() + "'", database);
            try {
                TotalAmt = TotalAmt + Double.parseDouble(item_location.get_selling_price());
            } catch (Exception ex) {
            }
            if (item_location == null) {
                sale_priceStr = "0";
                cost_priceStr = "0";
            } else {
                sale_priceStr = item_location.get_selling_price();
                cost_priceStr = item_location.get_cost_price();
            }
            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), i + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", "0", "0", sale_priceStr);
            Globals.cart.add(cartItem);
        }
        try {
            String decimalPlace = lite_pos_device.getDecimal_Place();
            Amt = Globals.myNumberFormat2Price(TotalAmt, decimalPlace);
        } catch (Exception ex) {
            String decimalPlace = "0";
            Amt = Globals.myNumberFormat2Price(TotalAmt, decimalPlace);
        }


        return Amt;
    }

    private void updateLabel() {
        myFormat = "yyyy-MM-dd"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        String from;
        try {
            from = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            from = "";
        }
        edt_from.setText(from);
    }

    private void updateLabe2() {
        myFormat = "yyyy-MM-dd"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        String too;
        try {
            too = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            too = "";
        }
        edt_to.setText(too);
    }
}

