package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.PurchaseFinalListAdapter;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Purchase;
import org.phomellolitepos.database.Purchase_Detail;
import org.phomellolitepos.database.Purchase_Payment;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PurchaseFinalActivity extends AppCompatActivity {
    EditText edt_toolbar_item_code, edt_name, edt_qty, edt_price;
    Button btn_add;
    ListView list;
    String operation, str_voucher_no, str_ref_voucher_code, str_date, str_remarks;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    BottomNavigationView bottomNavigationView;
    String[] invFlag = {};
    ArrayList<Item> arrayListItem;
    ArrayList<StockAdjectmentDetailList> arraylist = new ArrayList<StockAdjectmentDetailList>();
    ArrayList<Purchase_Detail> purchase_detailArrayList;
    String sale_priceStr, decimal_check, qty_decimal_check, str_inv;
    Item resultp;
    String item_code, date;
    PurchaseFinalListAdapter purchaseFinalListAdapter;
    Purchase purchase;
    Purchase_Detail purchase_detail;
    String strupdate = "", strItemCode, cusCode;
    int Position;
    Purchase_Payment purchase_payment;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_final);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        invFlag = getResources().getStringArray(R.array.Inv_flag);
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
                pDialog = new ProgressDialog(PurchaseFinalActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(PurchaseFinalActivity.this, PurchaseHeaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("ref_voucher_code", cusCode);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
                            intent.putExtra("contact_code", cusCode);
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
        getSupportActionBar().setTitle("Purchase");
        final Intent intent = getIntent();
        operation = intent.getStringExtra("operation");
        str_voucher_no = intent.getStringExtra("voucher_no");
        str_ref_voucher_code = intent.getStringExtra("ref_voucher_code");
        str_date = intent.getStringExtra("date");
        str_remarks = intent.getStringExtra("remarks");
        cusCode = intent.getStringExtra("contact_code");

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.retail_bottom_navigation);
        edt_toolbar_item_code = (EditText) findViewById(R.id.edt_toolbar_item_code);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_qty = (EditText) findViewById(R.id.edt_qty);
        edt_price = (EditText) findViewById(R.id.edt_price);
        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Settings settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        if (purchase == null) {
        } else {
            if (purchase.get_is_post().equals("true") || purchase.get_is_cancel().equals("true")) {
                Menu menu = bottomNavigationView.getMenu();
                MenuItem cancel = menu.findItem(R.id.action_cancel);
                MenuItem delete = menu.findItem(R.id.action_delete);
                MenuItem post = menu.findItem(R.id.action_post);
                MenuItem save = menu.findItem(R.id.action_save);
                cancel.setEnabled(false);
                delete.setEnabled(false);
                post.setEnabled(false);
                save.setEnabled(false);
                btn_add.setEnabled(false);
            }
        }

        if (operation.equals("Edit")) {

            purchase_detailArrayList = Purchase_Detail.getAllPurchase_Detail(getApplicationContext(), " where ref_voucher_no ='" + str_voucher_no + "' ", database);
            if (purchase_detailArrayList.size() > 0) {
                String inv;
                for (int i = 0; i < purchase_detailArrayList.size(); i++) {
                    Item item = Item.getItem(getApplicationContext(), " where item_code = '" + purchase_detailArrayList.get(i).get_item_code() + "'", database, db);

                    StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", purchase_detailArrayList.get(i).get_item_code(), purchase_detailArrayList.get(i).get_qty(), "", item.get_item_name(), purchase_detailArrayList.get(i).get_price(), purchase_detailArrayList.get(i).get_line_total());
                    arraylist.add(stockAdjectmentDetailList);
                }
                list_load(arraylist);
            }
        }

        edt_qty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_qty.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_qty.requestFocus();
                    edt_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_qty, InputMethodManager.SHOW_IMPLICIT);
                    edt_qty.selectAll();
                    return true;
                }
            }
        });

        edt_price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_price.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_price.requestFocus();
                    edt_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_price, InputMethodManager.SHOW_IMPLICIT);
                    edt_price.selectAll();
                    return true;
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_name.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "No item selected", Toast.LENGTH_SHORT).show();
                } else {
                    if (edt_qty.getText().toString().trim().equals("")) {
                        edt_qty.setError("Enter Quantity");
                        return;
                    }
                    if (edt_price.getText().toString().trim().equals("")) {
                        edt_price.setError("Enter Price");
                        return;
                    }

                    closeKeyboard();

                    if (strupdate.equals("update")) {
                        StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", strItemCode, edt_qty.getText().toString().trim(), str_inv, edt_name.getText().toString().trim(), edt_price.getText().toString().trim(), (Double.parseDouble(edt_qty.getText().toString().trim()) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                        arraylist.remove(Position);
                        arraylist.add(Position, stockAdjectmentDetailList);
                        list_load(arraylist);
                    } else {
                        int count = 0;
                        boolean bFound = false;

                        while (count < arraylist.size()) {
                            if (resultp.get_item_code().equals(arraylist.get(count).getItem_code())) {
                                bFound = true;
                                arraylist.get(count).setQty(((Integer.parseInt(arraylist.get(count).getQty())) + Integer.parseInt(edt_qty.getText().toString().trim())) + "");
                                arraylist.get(count).setLine_total(((Double.parseDouble(arraylist.get(count).getQty())) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                            }
                            count = count + 1;
                        }

                        if (!bFound) {
                            StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", item_code, edt_qty.getText().toString().trim(), str_inv, resultp.get_item_name(), edt_price.getText().toString().trim(), (Double.parseDouble(edt_qty.getText().toString().trim()) * Double.parseDouble(edt_price.getText().toString().trim())) + "");
                            arraylist.add(stockAdjectmentDetailList);

                        }
                        list_load(arraylist);
                    }
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_post:
                                alertDialog = new AlertDialog.Builder(
                                        PurchaseFinalActivity.this);
                                alertDialog.setTitle("");
                                alertDialog
                                        .setMessage("Do you want to post?");

                                lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                                alertDialog.setNegativeButton(R.string.Cancel,

                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        });

                                alertDialog.setPositiveButton(R.string.Ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                if (arraylist.size() == 0) {
                                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pDialog = new ProgressDialog(PurchaseFinalActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                                    pDialog.show();
                                                    Thread timerThread1 = new Thread() {
                                                        public void run() {
                                                            purchase_payment = Purchase_Payment.getPurchase_Payment(getApplicationContext(), " where ref_voucher_no = '" + str_voucher_no + "'", database);
                                                            if (purchase_payment == null) {
                                                                pDialog.dismiss();
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), "Cannot post without saving", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                String rsultPost = stock_post();
                                                                pDialog.dismiss();
                                                                if (rsultPost.equals("1")) {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                                            Intent intent1 = new Intent(PurchaseFinalActivity.this, PurchaseListActivity.class);
                                                                            startActivity(intent1);
                                                                            finish();
                                                                        }
                                                                    });

                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Record not post", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                }
                                                            }

                                                        }
                                                    };
                                                    timerThread1.start();
                                                }

                                            }
                                        });


                                alert = alertDialog.create();
                                alert.show();

                                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                                break;

                            case R.id.action_save:
                                alertDialog = new AlertDialog.Builder(
                                        PurchaseFinalActivity.this);
                                alertDialog.setTitle("");
                                alertDialog
                                        .setMessage("Do you want to save?");

                                lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                                alertDialog.setNegativeButton(R.string.Cancel,

                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        });

                                alertDialog.setPositiveButton(R.string.Ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {

                                                if (arraylist.size() == 0) {
                                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pDialog = new ProgressDialog(PurchaseFinalActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                                    pDialog.show();
                                                    Thread timerThread = new Thread() {
                                                        public void run() {
                                                            String result = stock_save();
                                                            pDialog.dismiss();
                                                            if (result.equals("1")) {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(PurchaseFinalActivity.this, PusrchasePaymentActivity.class);
                                                                        intent.putExtra("operation", operation);
                                                                        intent.putExtra("voucher_no", str_voucher_no);
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                            } else {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    };
                                                    timerThread.start();
                                                }
                                            }
                                        });


                                alert = alertDialog.create();
                                alert.show();

                                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                                break;
                            case R.id.action_cancel:
                                alertDialog = new AlertDialog.Builder(
                                        PurchaseFinalActivity.this);
                                alertDialog.setTitle("");
                                alertDialog
                                        .setMessage("Do you want to cancel?");

                                lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                                alertDialog.setNegativeButton(R.string.Cancel,

                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        });

                                alertDialog.setPositiveButton(R.string.Ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                if (arraylist.size() == 0) {
                                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pDialog = new ProgressDialog(PurchaseFinalActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                                    pDialog.show();
                                                    Thread timerThread2 = new Thread() {
                                                        public void run() {
                                                            purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                                            if (purchase == null) {
                                                                String result = stock_save();
                                                                pDialog.dismiss();
                                                                if (result.equals("1")) {
                                                                    String rsultPost = stock_cancel();
                                                                    if (rsultPost.equals("1")) {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
                                                                                Intent intent1 = new Intent(PurchaseFinalActivity.this, PurchaseListActivity.class);
                                                                                startActivity(intent1);
                                                                                finish();
                                                                            }
                                                                        });

                                                                    } else {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                Toast.makeText(getApplicationContext(), "Record not Cancel", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                String rsultPost = stock_cancel();
                                                                pDialog.dismiss();
                                                                if (rsultPost.equals("1")) {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
                                                                            Intent intent1 = new Intent(PurchaseFinalActivity.this, PurchaseListActivity.class);
                                                                            startActivity(intent1);
                                                                            finish();
                                                                        }
                                                                    });

                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Record not Cancel", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        }
                                                    };
                                                    timerThread2.start();
                                                }
                                            }
                                        });

                                alert = alertDialog.create();
                                alert.show();

                                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                break;

                            case R.id.action_delete:
                                alertDialog = new AlertDialog.Builder(
                                        PurchaseFinalActivity.this);
                                alertDialog.setTitle("");
                                alertDialog
                                        .setMessage("Do you want to delete?");

                                lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                                alertDialog.setNegativeButton(R.string.Cancel,

                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        });

                                alertDialog.setPositiveButton(R.string.Ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                if (arraylist.size() == 0) {
                                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pDialog = new ProgressDialog(PurchaseFinalActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                                    pDialog.show();
                                                    Thread timerThread3 = new Thread() {
                                                        public void run() {
                                                            purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                                            if (purchase == null) {
                                                                pDialog.dismiss();
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), "Cannot delete without saving", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                String result = stock_delete();
                                                                ;
                                                                pDialog.dismiss();
                                                                if (result.equals("1")) {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                                                                            Intent intent1 = new Intent(PurchaseFinalActivity.this, PurchaseListActivity.class);
                                                                            startActivity(intent1);
                                                                            finish();
                                                                        }
                                                                    });

                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    };
                                                    timerThread3.start();
                                                }

                                            }
                                        });


                                alert = alertDialog.create();
                                alert.show();

                                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                                break;
                        }
                        return true;
                    }
                });

        edt_toolbar_item_code.setOnKeyListener(new View.OnKeyListener() {
                                                   @Override
                                                   public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                       if (event.getAction() == KeyEvent.ACTION_DOWN
                                                               && keyCode == KeyEvent.KEYCODE_ENTER) {
                                                           String strValue = edt_toolbar_item_code.getText().toString();
                                                           if (edt_toolbar_item_code.getText().toString().equals("\n") || edt_toolbar_item_code.getText().toString().equals("")) {
                                                               Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                               edt_toolbar_item_code.requestFocus();
                                                           } else {
                                                               String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                               arrayListItem = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                               if (arrayListItem.size() >= 1) {
                                                                   strupdate = "";
                                                                   resultp = arrayListItem.get(0);
                                                                   item_code = resultp.get_item_code();
                                                                   Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);

                                                                   if (item_location == null) {
                                                                       sale_priceStr = "0";
                                                                   } else {
                                                                       sale_priceStr = item_location.get_selling_price();
                                                                   }

                                                                   String item_price;
                                                                   item_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                                                                   edt_name.setText(resultp.get_item_name());
                                                                   edt_price.setText(item_price);
                                                                   edt_qty.setText("1");
                                                                   edt_toolbar_item_code.setText("");

                                                               } else {
                                                                   edt_toolbar_item_code.selectAll();
                                                                   Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                           return true;
                                                       }
                                                       return false;
                                                   }
                                               }
        );
    }

    private String get_total() {
        Double total = 0d;
        try {
            if (arraylist.size() > 0) {
                for (int i = 0; i < arraylist.size(); i++) {
                    total = total + Double.parseDouble(arraylist.get(i).getLine_total());
                }
            }
        } catch (Exception e) {
        }
        return total + "";
    }

    private String stock_cancel() {
        String suc = "0";
        purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        purchase.set_is_cancel("true");
        long l = purchase.updatePurchase("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_post() {
        String suc = "0";
        purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        purchase.set_is_post("true");
        long l = purchase.updatePurchase("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_delete() {
        String suc = "0";
        purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        purchase.set_is_active("0");
        long l = purchase.updatePurchase("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_save() {
        String suc = "0";
        try {
            database.beginTransaction();
            String total = get_total();
            purchase = Purchase.getPurchase(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
            if (purchase == null) {
                purchase = new Purchase(getApplicationContext(), null, cusCode, str_voucher_no, str_ref_voucher_code, str_date, str_remarks, total, "false", "false", "1", "N", Globals.user, date);
                long l = purchase.insertPurchase(database);
                if (l > 0) {
                    suc = "1";
                    for (int i = 0; i < arraylist.size(); i++) {
                        purchase_detail = new Purchase_Detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getPrice(), arraylist.get(i).getLine_total());
                        long l1 = purchase_detail.insertPurchase_Detail(database);
                        if (l1 > 0) {
                            suc = "1";
                        }
                    }
                }
            } else {

                purchase = new Purchase(getApplicationContext(), purchase.get_id(), cusCode, str_voucher_no, str_ref_voucher_code, str_date, str_remarks, total, purchase.get_is_post(), purchase.get_is_cancel(), "1", "N", Globals.user, date);
                long l = purchase.updatePurchase("voucher_no=?", new String[]{str_voucher_no}, database);
                if (l > 0) {
                    suc = "1";
                    long e6 = Purchase_Detail.delete_Purchase_Detail(getApplicationContext(), "purchase_detail", " ref_voucher_no =? ", new String[]{str_voucher_no}, database);
                    for (int i = 0; i < arraylist.size(); i++) {
                        purchase_detail = new Purchase_Detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getPrice(), arraylist.get(i).getLine_total());
                        long l1 = purchase_detail.insertPurchase_Detail(database);
                        if (l1 > 0) {
                            suc = "1";
                        }
                    }
                }
            }

            if (suc.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }

        } catch (Exception ex) {
            database.endTransaction();
        }

        return suc;
    }

    private void list_load(ArrayList<StockAdjectmentDetailList> arraylist) {
        ListView list = (ListView) findViewById(R.id.list);
        if (arraylist.size() > 0) {
            purchaseFinalListAdapter = new PurchaseFinalListAdapter(PurchaseFinalActivity.this, arraylist);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(purchaseFinalListAdapter);
            purchaseFinalListAdapter.notifyDataSetChanged();
        } else {
            list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String strValue = edt_toolbar_item_code.getText().toString();
            if (edt_toolbar_item_code.getText().toString().equals("\n") || edt_toolbar_item_code.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                edt_toolbar_item_code.requestFocus();
            } else {
                String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                arrayListItem = Item.getAllItem(getApplicationContext(), strWhere, database);
                if (arrayListItem.size() >= 1) {
                    strupdate = "";
                    resultp = arrayListItem.get(0);
                    item_code = resultp.get_item_code();
                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);

                    if (item_location == null) {
                        sale_priceStr = "0";
                    } else {
                        sale_priceStr = item_location.get_selling_price();
                    }

                    String item_price;
                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                    edt_name.setText(resultp.get_item_name());
                    edt_price.setText(item_price);

                    edt_qty.setText("1");
                    edt_toolbar_item_code.setText("");
                    edt_toolbar_item_code.requestFocus();
                    closeKeyboard();
                } else {
                    edt_toolbar_item_code.selectAll();
                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTextView(String position, String item_code, String item_name, String qty, String price, String strUpdate) {
        try {
            Position = Integer.parseInt(position);
            strupdate = strUpdate;
            strItemCode = item_code;
            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check);
            edt_price.setText(item_price);
            edt_name.setText(item_name);
            edt_qty.setText(qty);
            String ab;

        } catch (Exception ex) {
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(PurchaseFinalActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(PurchaseFinalActivity.this, PurchaseHeaderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
                    intent.putExtra("contact_code", cusCode);
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
