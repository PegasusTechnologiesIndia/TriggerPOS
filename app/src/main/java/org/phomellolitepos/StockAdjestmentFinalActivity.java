package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.phomellolitepos.Adapter.StockAdjestmentFinalListAdapter;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Stock_Adjustment_Detail;
import org.phomellolitepos.database.Stock_Adjustment_Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static org.phomellolitepos.R.id.btn_retail_1;
import static org.phomellolitepos.R.id.btn_retail_2;
import static org.phomellolitepos.R.id.edt_toolbar_retail;
import static org.phomellolitepos.R.id.save;

public class StockAdjestmentFinalActivity extends AppCompatActivity {
    EditText edt_toolbar_item_code, edt_name, edt_qty, edt_price;
    Button btn_add;
    Spinner spn_inv;
    ListView list;
    String operation, str_voucher_no, str_date, str_remarks;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    BottomNavigationView bottomNavigationView;
    String[] invFlag = {};
    ArrayList<Item> arrayListItem;
    ArrayList<StockAdjectmentDetailList> arraylist = new ArrayList<StockAdjectmentDetailList>();
    ArrayList<Stock_Adjustment_Detail> arrayListStockDetail;
    String sale_priceStr, decimal_check, qty_decimal_check, str_inv;
    Item resultp;
    String item_code, date;
    StockAdjestmentFinalListAdapter stockAdjestmentFinalListAdapter;
    Stock_Adjustment_Header stock_adjustment_header;
    Stock_Adjustment_Detail stock_adjustment_detail;
    String strupdate = "", strItemCode;
    int Position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjestment_final);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invFlag = getResources().getStringArray(R.array.Inv_flag);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentHeaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
                            intent.putExtra("voucher_no", str_voucher_no);
                            intent.putExtra("date", str_date);
                            intent.putExtra("remarks", str_remarks);
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
        getSupportActionBar().setTitle("");
        operation = intent.getStringExtra("operation");
        str_voucher_no = intent.getStringExtra("voucher_no");
        str_date = intent.getStringExtra("date");
        str_remarks = intent.getStringExtra("remarks");
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
        spn_inv = (Spinner) findViewById(R.id.spn_inv);
        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        final Settings settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        if (stock_adjustment_header == null) {
        } else {
            if (stock_adjustment_header.get_is_post().equals("true") || stock_adjustment_header.get_is_cancel().equals("true")) {
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

            arrayListStockDetail = Stock_Adjustment_Detail.getAllstock_adjustment_detail(getApplicationContext(), " where ref_voucher_no ='" + str_voucher_no + "' ", database);
            if (arrayListStockDetail.size() > 0) {
                String inv;
                for (int i = 0; i < arrayListStockDetail.size(); i++) {
                    Item item = Item.getItem(getApplicationContext(), " where item_code = '" + arrayListStockDetail.get(i).get_item_code() + "'", database, db);

                    StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", arrayListStockDetail.get(i).get_item_code(), arrayListStockDetail.get(i).get_qty(), arrayListStockDetail.get(i).get_in_out_flag(), item.get_item_name(), "", "");
                    arraylist.add(stockAdjectmentDetailList);
                }
                list_load(arraylist);
            }
        }
        fill_inv_spn();

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
                    closeKeyboard();
                    if (strupdate.equals("update")) {
                        StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", strItemCode, edt_qty.getText().toString().trim(), str_inv, edt_name.getText().toString().trim(), "", "");
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

                            }
                            count = count + 1;
                        }

                        if (!bFound) {
                            StockAdjectmentDetailList stockAdjectmentDetailList = new StockAdjectmentDetailList(getApplicationContext(), "", "", "", item_code, edt_qty.getText().toString().trim(), str_inv, resultp.get_item_name(), "", "");
                            arraylist.add(stockAdjectmentDetailList);
                        }
                        list_load(arraylist);
                    }
                }
            }
        });

        spn_inv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String str = String.valueOf(spn_inv.getItemAtPosition(i));
                    if (str.equals("IN")) {
                        str_inv = "I";
                    } else {
                        str_inv = "O";
                    }
                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected( MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_post:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread1 = new Thread() {
                                        public void run() {
//                                            stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
//                                            if (stock_adjustment_header == null) {
                                            String result = stock_save();
                                            pDialog.dismiss();
                                            if (result.equals("1")) {
                                                String rsultPost = stock_post();
                                                if (rsultPost.equals("1")) {
                                                    if (settings.get_Is_Stock_Manager().equals("true")){
                                                        String rsultUpdate = stock_update();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Post successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentListActivity.class);
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

                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Record not saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }


                                        }
                                    };
                                    timerThread1.start();
                                }
                                break;

                            case R.id.action_save:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
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
                                                        Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentListActivity.class);
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
                                    };
                                    timerThread.start();
                                }

                                break;
                            case R.id.action_cancel:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread2 = new Thread() {
                                        public void run() {
                                            stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                            if (stock_adjustment_header == null) {
                                                String result = stock_save();
                                                pDialog.dismiss();
                                                if (result.equals("1")) {
                                                    String rsultPost = stock_cancel();
                                                    if (rsultPost.equals("1")) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
                                                                Intent intent1 = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentListActivity.class);
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
                                                            Intent intent1 = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentListActivity.class);
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
                                break;

                            case R.id.action_delete:
                                if (arraylist.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "No item added", Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread3 = new Thread() {
                                        public void run() {
                                            stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
                                            if (stock_adjustment_header == null) {
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Cannot delete without saving", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                String result = stock_delete();
                                                pDialog.dismiss();
                                                if (result.equals("1")) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentListActivity.class);
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
//                                                                   ArrayList<StockAdjectmentDetailList> stockAdjtDetailList = Globals.stockAdjtDetailList;
//                                                                   int count = 0;
//                                                                   boolean bFound = false;
//
//                                                                   while (count < stockAdjtDetailList.size()) {
//                                                                       if (resultp.get_item_code().equals(stockAdjtDetailList.get(count).getItem_code())) {
//                                                                           bFound = true;
//
//                                                                           stockAdjtDetailList.get(count).setQty(((Integer.parseInt(stockAdjtDetailList.get(count).getQty())) + 1) + "");
//                                                                           Globals.TotalQty = Globals.TotalQty + 1;
//                                                                       }
//                                                                       count = count + 1;
//                                                                   }

//                                                                   if (!bFound) {
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
//                                                                   }

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

    private String stock_update() {
        String suc = "0";
        try {
            stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
            if (stock_adjustment_header == null) {
            } else {
                if (stock_adjustment_header.get_is_post().equals("true")) {
                    ArrayList<Stock_Adjustment_Detail> stock_adjustment_detailArrayList = Stock_Adjustment_Detail.getAllstock_adjustment_detail(getApplicationContext(), " where ref_voucher_no='" + str_voucher_no + "'", database);
                    if (stock_adjustment_detailArrayList.size() > 0) {
                        for (int i = 0; i < stock_adjustment_detailArrayList.size(); i++) {
                            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "where item_code='" + stock_adjustment_detailArrayList.get(i).get_item_code() + "'", database);
                            Double updatedQty = 0d;
                            if (item_location != null) {
                                Double avlQty = Double.parseDouble(item_location.get_quantity());
                                Double effectiveQty = Double.parseDouble(stock_adjustment_detailArrayList.get(i).get_qty());
                                if (stock_adjustment_detailArrayList.get(i).get_in_out_flag().equals("I")) {
                                    updatedQty = avlQty + effectiveQty;
                                } else {
                                    updatedQty = avlQty - effectiveQty;
                                }
                                item_location.set_quantity(updatedQty + "");
                                long l = item_location.updateItem_Location("item_code=?", new String[]{stock_adjustment_detailArrayList.get(i).get_item_code()}, database);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return suc;
    }

    private String stock_cancel() {
        String suc = "0";
        stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        stock_adjustment_header.set_is_cancel("true");
        long l = stock_adjustment_header.updatestock_adjustment_header("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_post() {
        String suc = "0";
        stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        stock_adjustment_header.set_is_post("true");
        long l = stock_adjustment_header.updatestock_adjustment_header("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_delete() {
        String suc = "0";
        stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
        stock_adjustment_header.set_is_active("0");
        long l = stock_adjustment_header.updatestock_adjustment_header("voucher_no=?", new String[]{str_voucher_no}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }

    private String stock_save() {
        String suc = "0";
        try {
            database.beginTransaction();
            stock_adjustment_header = Stock_Adjustment_Header.getstock_adjustment_header(getApplicationContext(), " where voucher_no ='" + str_voucher_no + "' ", database);
            if (stock_adjustment_header == null) {
//                String ab = "insert into stock_adjustment_header (voucher_no,date,remarks,is_post,is_cancel,is_active,modified_by,modified_date) values ('" + str_voucher_no + "','" + str_date + "','" + str_remarks + "','false','false','1','" + Globals.user + "','" + date + "')";
                stock_adjustment_header = new Stock_Adjustment_Header(getApplicationContext(), null, str_voucher_no, str_date, str_remarks, "false", "false", "1", Globals.user, date);
                long l = stock_adjustment_header.insertstock_adjustment_header(database);
//                long l = db.executeDML(ab, database);
                if (l > 0) {
                    suc = "1";
                    for (int i = 0; i < arraylist.size(); i++) {
//                        String ab1 = "insert into stock_adjustment_detail (ref_voucher_no,s_no,item_code,qty,in_out_flag) values ('" + str_voucher_no + "','" + i + 1 + "" + "','" + arraylist.get(i).getItem_code() + "','" + arraylist.get(i).getQty() + "','" + arraylist.get(i).getIn_out_flag() + "')";
                        stock_adjustment_detail = new Stock_Adjustment_Detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getIn_out_flag());
                        long l1 = stock_adjustment_detail.insertstock_adjustment_detail(database);
//                        long l1 = db.executeDML(ab1, database);
                        if (l1 > 0) {
                            suc = "1";
                        }
                    }
                }
            } else {

                stock_adjustment_header = new Stock_Adjustment_Header(getApplicationContext(), stock_adjustment_header.get_id(), str_voucher_no, str_date, str_remarks, stock_adjustment_header.get_is_post(), stock_adjustment_header.get_is_cancel(), "1", Globals.user, date);
                long l = stock_adjustment_header.updatestock_adjustment_header("voucher_no=?", new String[]{str_voucher_no}, database);
                if (l > 0) {
                    suc = "1";
                    long e6 = Stock_Adjustment_Detail.delete_stock_adjustment_detail(getApplicationContext(), "stock_adjustment_detail", " ref_voucher_no =? ", new String[]{str_voucher_no}, database);
                    for (int i = 0; i < arraylist.size(); i++) {
                        stock_adjustment_detail = new Stock_Adjustment_Detail(getApplicationContext(), null, str_voucher_no, i + 1 + "", arraylist.get(i).getItem_code(), arraylist.get(i).getQty(), arraylist.get(i).getIn_out_flag());
                        long l1 = stock_adjustment_detail.insertstock_adjustment_detail(database);
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
            stockAdjestmentFinalListAdapter = new StockAdjestmentFinalListAdapter(StockAdjestmentFinalActivity.this, arraylist);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(stockAdjestmentFinalListAdapter);
            stockAdjestmentFinalListAdapter.notifyDataSetChanged();
        } else {
            list.setVisibility(View.GONE);
        }
    }

    private void fill_inv_spn() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, invFlag);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_inv.setAdapter(dataAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retail, menu);
        menu.setGroupVisible(R.id.grp_retail, false);
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

    public void setTextView(String position, String item_code, String item_name, String qty, String strinv_flag, String strUpdate) {
        try {
            Position = Integer.parseInt(position);
            strupdate = strUpdate;
            strItemCode = item_code;
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
            if (item_location == null) {
                sale_priceStr = "0";
            } else {
                sale_priceStr = item_location.get_selling_price();
            }
            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
            edt_price.setText(item_price);
            edt_name.setText(item_name);
            edt_qty.setText(qty);
            String ab;
            if (strinv_flag.equals("I")) {
                ab = "IN";
            } else {
                ab = "OUT";
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, invFlag);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_inv.setAdapter(dataAdapter);
            if (!ab.equals(null)) {
                int spinnerPosition = dataAdapter.getPosition(ab);
                spn_inv.setSelection(spinnerPosition);
            }
        } catch (Exception ex) {
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(StockAdjestmentFinalActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(StockAdjestmentFinalActivity.this, StockAdjestmentHeaderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
                    intent.putExtra("voucher_no", str_voucher_no);
                    intent.putExtra("date", str_date);
                    intent.putExtra("remarks", str_remarks);
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
