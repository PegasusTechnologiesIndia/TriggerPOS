package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Adapter.ItemUnitListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Customer_PriceBook;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.Void;
import org.phomellolitepos.database.VoidShoppingCart;


public class ChangePriceActivity extends AppCompatActivity {
    TableRow row_cus_sales_price,tbr_cost;
    TextView txt_cost_price, txt_cus_sale_price,txt_name;
    EditText count_price, count_qty;
    Button btn_plus_qty, btn_minus_qty, btn_minus_price, btn_plus_price;
    Button btn_save, btn_delete;
    int position;
    ShoppingCart shoppingCart;
    ArrayList<String> item_group_taxArrayList;
    ArrayList<Unit> arrayUnitList;
    ArrayList<Item_Group_Tax> item_group_taxArray;
    Database db;
    SQLiteDatabase database;
    String decimal_check, qty_decimal_check, item_code, item_group_code, line_total_bf, operation = "Edit";
    Item item;
    String orderCode, flag,main_land,mastercode;
    String sale_priceStr;
    String cost_priceStr;
    ProgressDialog pDialog;
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type;
    Item_Location item_location;
    Customer_PriceBook customer_priceBook;
    Settings settings;
    ArrayList<ShoppingCart> myCart = Globals.cart;
    String srno;
     Orders orders;
     String date;
    ItemUnitListAdapter itemUnitListAdapter;
  Order_Detail order_detail;
  Spinner spn_unit;
  String spn_unit_code="";
    Void Voidinsert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_price);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        Globals.load_form_cart = "1";
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        date = format.format(d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ChangePriceActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                if (settings.get_Home_Layout().equals("0")) {
                                    if (flag.equals("Main")) {
                                        Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else if (main_land.equals("MainLand")) {
                                        Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else {
                                        Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                        intent.putExtra("opr1", operation);
                                        intent.putExtra("order_code1", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    }
                                } else {
                                    if (flag.equals("Main")) {
                                        Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else if (main_land.equals("MainLand")) {
                                        Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    } else {
                                        Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                        intent.putExtra("opr1", operation);
                                        intent.putExtra("order_code1", orderCode);
                                        startActivity(intent);
                                        pDialog.dismiss();
                                        finish();
                                    }
                                }

                            }
                            else if(Globals.objLPR.getIndustry_Type().equals("2")) {
                                Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                intent.putExtra("opr", operation);
                                intent.putExtra("order_code", orderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            }
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
        if (Globals.cart.size() > 0) {
            position = Integer.parseInt(intent.getStringExtra("arr_position"));
        } else {
            position = 0;
        }

        settings = Settings.getSettings(getApplicationContext(),database,"");

        item_code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("opr");
        orderCode = intent.getStringExtra("odr_code");
        flag = intent.getStringExtra("flag");
        srno = intent.getStringExtra("srno");
        main_land = intent.getStringExtra("MainLand");
        mastercode = intent.getStringExtra("mastercode");
        if (orderCode == null) {
            orderCode = "";
        }
        if(orderCode!=null) {
            orders = Orders.getOrders(ChangePriceActivity.this, database, "WHERE order_code = '" + orderCode + "'");
           order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " WHERE order_code='" + orderCode + "'", database);




        }


        row_cus_sales_price = (TableRow) findViewById(R.id.row_cus_sales_price);
        tbr_cost = (TableRow) findViewById(R.id.tbr_cost);
        txt_cost_price = (TextView) findViewById(R.id.txt_cost_price);
        txt_cus_sale_price = (TextView) findViewById(R.id.txt_cus_sale_price);
        txt_name = (TextView) findViewById(R.id.txt_name);
        count_price = (EditText) findViewById(R.id.count_price);
        count_qty = (EditText) findViewById(R.id.count_qty);
        btn_plus_qty = (Button) findViewById(R.id.btn_plus_qty);
        btn_minus_qty = (Button) findViewById(R.id.btn_minus_qty);
        btn_minus_price = (Button) findViewById(R.id.btn_minus_price);
        btn_plus_price = (Button) findViewById(R.id.btn_plus_price);
        btn_save = (Button) findViewById(R.id.btn_save);
        spn_unit=(Spinner)findViewById(R.id.spn_unit);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        item = Item.getItem(getApplicationContext(),"where item_code ='"+item_code+"'",database,db);
        if(item!=null){
            String unitid= item.get_unit_id();
            fill_spinner_unit(unitid);

        }
        spn_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Unit resultp = arrayUnitList.get(position);
                    spn_unit_code = resultp.get_unit_id();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (settings.get_Is_Change_Price().equals("false")){
            count_price.setEnabled(false);
            btn_minus_price.setEnabled(false);
            btn_plus_price.setEnabled(false);
        }

        count_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    count_qty.requestFocus();
                    count_qty.selectAll();
                }
            }
        });

        count_price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count_price.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    count_price.requestFocus();
                    count_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(count_price, InputMethodManager.SHOW_IMPLICIT);
                    count_price.selectAll();
                    return true;
                }
            }
        });

        count_qty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count_qty.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    count_qty.requestFocus();
                    count_qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(count_qty, InputMethodManager.SHOW_IMPLICIT);
                    count_qty.selectAll();
                    return true;
                }
            }
        });

        count_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (settings.get_Is_Stock_Manager().equals("true")) {
                        if (count_qty.getText().toString().trim().equals("") || count_qty.getText().toString().trim().equals("0")) {
                            count_qty.setText("1");
                            count_qty.selectAll();
                        }
                        boolean result = stock_check(item_code, Double.parseDouble(count_qty.getText().toString().trim()));
                        if (result == true) {
                            btn_save.setEnabled(true);
                        } else {
                            btn_save.setEnabled(false);
                            count_qty.selectAll();
                        }
                    }else {
                        btn_save.setEnabled(true);
                    }

                } catch (Exception ex) {
                }

            }
        });

        final Settings settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
        if (settings.get_Is_Cost_Show().equals("true")){
            try {

                String cost_price;
                cost_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_cost_price()), decimal_check);
                txt_cost_price.setText(cost_price);
            } catch (Exception ex) {}
        }else {
            tbr_cost.setVisibility(View.GONE);
        }

        if (flag.equals("Main")) {
            btn_delete.setVisibility(View.GONE);
        }
        try {
            item = Item.getItem(getApplicationContext(), " Where item_code = '" + item_code + "'", database, db);
            item_group_code = item.get_item_code();
            item_group_taxArray = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
            txt_name.setText(item.get_item_name());
        } catch (Exception ex) {}

        if (flag.equals("Main")) {
            ArrayList<ShoppingCart> shoppingCart = Globals.cart;
            int count = 0;
            boolean bFound = false;
            if (shoppingCart.size() > 0) {

                while (count < shoppingCart.size()) {
                    if (item.get_item_code().equals(shoppingCart.get(count).get_Item_Code())) {
                        bFound = true;
                        if (!Globals.strContact_Code.equals("")) {
                            customer_priceBook = Customer_PriceBook.getCustomer_PriceBook(getApplicationContext(), " where item_code='" + shoppingCart.get(count).get_Item_Code() + "' and contact_code='" + Globals.strContact_Code + "'", database);
                            if (customer_priceBook == null) {
                                row_cus_sales_price.setVisibility(View.GONE);
                                String sale_price;
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get(count).get_Sales_Price()), decimal_check);
                                count_price.setText(sale_price);
                                count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get(count).get_Quantity()), qty_decimal_check));
                            } else {
                                String sale_price;
                                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get(count).get_Sales_Price()), decimal_check);
                                count_price.setText(sale_price);
                                count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get(count).get_Quantity()), qty_decimal_check));
                                txt_cus_sale_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(customer_priceBook.get_sale_price()), decimal_check));

                            }
                        } else {
                            row_cus_sales_price.setVisibility(View.GONE);
                            String sale_price;
                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get(count).get_Sales_Price()), decimal_check);
                            count_price.setText(sale_price);
                            count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get(count).get_Quantity()), qty_decimal_check));
                        }
                    }
                    count = count + 1;
                }

                if (!bFound) {
                    if (!Globals.strContact_Code.equals("")) {
                        customer_priceBook = Customer_PriceBook.getCustomer_PriceBook(getApplicationContext(), " where item_code='" + shoppingCart.get(count).get_Item_Code() + "' and contact_code='" + Globals.strContact_Code + "'", database);
                        if (customer_priceBook == null) {
                            row_cus_sales_price.setVisibility(View.GONE);
                            item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                            String sale_price;
                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                            count_price.setText(sale_price);
                            count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                        } else {

                            item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                            String sale_price;
                            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                            count_price.setText(sale_price);
                            count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                            txt_cus_sale_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(customer_priceBook.get_sale_price()), decimal_check));

                        }
                    } else {
                        row_cus_sales_price.setVisibility(View.GONE);
                        item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                        String sale_price;
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                        count_price.setText(sale_price);
                        count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                    }
                }
            } else {
                if (!Globals.strContact_Code.equals("")) {
                    customer_priceBook = Customer_PriceBook.getCustomer_PriceBook(getApplicationContext(), " where item_code='" + item_code + "' and contact_code='" + Globals.strContact_Code + "'", database);
                    if (customer_priceBook == null) {
                        row_cus_sales_price.setVisibility(View.GONE);
                        item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                        String sale_price;
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                        count_price.setText(sale_price);
                        count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                    } else {

                        item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                        String sale_price;
                        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                        count_price.setText(sale_price);
                        count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                        txt_cus_sale_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(customer_priceBook.get_sale_price()), decimal_check));

                    }
                } else {
                    row_cus_sales_price.setVisibility(View.GONE);
                    item_location = Item_Location.getItem_Location(getApplicationContext(), " WHERE item_code='" + item_code + "'", database);
                    String sale_price;
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
                    count_price.setText(sale_price);
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                }
            }

        } else {
            shoppingCart = Globals.cart.get(position);
            if (!Globals.strContact_Code.equals("")) {
                customer_priceBook = Customer_PriceBook.getCustomer_PriceBook(getApplicationContext(), " where item_code='" + shoppingCart.get_Item_Code() + "' and contact_code='" + Globals.strContact_Code + "'", database);
                if (customer_priceBook == null) {
                    row_cus_sales_price.setVisibility(View.GONE);

                    // add code by hitesh
                    String sale_price;
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get_Sales_Price()), decimal_check);
                    count_price.setText(sale_price);
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get_Quantity()), qty_decimal_check));

                } else {
                    String sale_price;
                    sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get_Sales_Price()), decimal_check);
                    count_price.setText(sale_price);
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get_Quantity()), qty_decimal_check));
                    txt_cus_sale_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(customer_priceBook.get_sale_price()), decimal_check));
                }
            } else {
                row_cus_sales_price.setVisibility(View.GONE);
                String sale_price;
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(shoppingCart.get_Sales_Price()), decimal_check);
                count_price.setText(sale_price);
                count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(shoppingCart.get_Quantity()), qty_decimal_check));
            }
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_qty.getText().toString().trim().contains(".") && settings.get_Qty_Decimal().equals("0")) {
                    count_qty.setError("Can't apply decimal quantity,Fist change quantity decimal from settings then apply");
                    count_qty.requestFocus();
                    return;
                }
                try {
                    if (Double.parseDouble(count_price.getText().toString().trim()) >= Double.parseDouble(item_location.get_cost_price())) {
                        AddToCart();
                    } else {
                        Toast.makeText(getApplicationContext(), "Sale price should not less than cost price..", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){}

            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pDialog = new ProgressDialog(ChangePriceActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            if (operation.equals("Edit")) {
                                long vd=0;
                                if (Globals.cart.size() > 1) {

                                    if (count_price.getText().toString().trim().equals("")) {
                                        count_price.setText("0");
                                    }
                                    if (count_qty.getText().toString().trim().equals("")) {
                                        count_qty.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"), qty_decimal_check));
                                    }


                                    if(!mastercode.equals("0")) {
                                        Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(shoppingCart.get_Line_Total());
                                        Globals.TotalItemCost = Globals.TotalItemCost - (Double.parseDouble(shoppingCart.get_Cost_Price()) * Double.parseDouble(shoppingCart.get_Quantity()));
                                        Globals.TotalItem = Globals.TotalItem - 1;
                                        Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity());
                                        if (Globals.objsettings.getIs_KitchenPrint().equals("true")) {
                                            Voidinsert = new Void(getApplicationContext(), null, orderCode, orders.get_device_code(), myCart.get(position).get_Item_Code(), myCart.get(position).getIs_modifier(), myCart.get(position).get_Quantity(), date, "false", "0", Globals.userId);
                                            vd = Voidinsert.insertVoid(database);
                                            if (vd > 0) {
                                                VoidShoppingCart voidcartItem = new VoidShoppingCart(getApplicationContext(), myCart.get(position).get_SRNO() + "", myCart.get(position).get_Item_Code(), myCart.get(position).get_Item_Name(), myCart.get(position).get_Quantity(), myCart.get(position).get_Cost_Price(), myCart.get(position).get_Sales_Price() + "", myCart.get(position).get_Tax_Price() + "", myCart.get(position).get_Discount(), myCart.get(position).get_Line_Total(), myCart.get(position).getIs_modifier(), myCart.get(position).getMaster_itemcode(), myCart.get(position).getCategoryIp(), "false");
                                                Globals.voidcart.add(voidcartItem);

                                            }
                                        }

                                        Globals.cart.remove(position);

                                    }
                                    else{
                                        for (int i = 0; i < Globals.cart.size(); i++) {
                                            if(!mastercode.equals("0")) {
                                                Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(shoppingCart.get_Line_Total());
                                                Globals.TotalItemCost = Globals.TotalItemCost - (Double.parseDouble(shoppingCart.get_Cost_Price()) * Double.parseDouble(shoppingCart.get_Quantity()));
                                                Globals.TotalItem = Globals.TotalItem - 1;
                                                Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity());
                                                if (Globals.objsettings.getIs_KitchenPrint().equals("true")) {
                                                    Voidinsert = new Void(getApplicationContext(), null, orderCode, orders.get_device_code(), myCart.get(position).get_Item_Code(), myCart.get(position).getIs_modifier(), myCart.get(position).get_Quantity(), date, "false", "0", Globals.userId);
                                                    vd = Voidinsert.insertVoid(database);
                                                    if (vd > 0) {
                                                        VoidShoppingCart voidcartItem = new VoidShoppingCart(getApplicationContext(), myCart.get(position).get_SRNO() + "", myCart.get(position).get_Item_Code(), myCart.get(position).get_Item_Name(), myCart.get(position).get_Quantity(), myCart.get(position).get_Cost_Price(), myCart.get(position).get_Sales_Price() + "", myCart.get(position).get_Tax_Price() + "", myCart.get(position).get_Discount(), myCart.get(position).get_Line_Total(), myCart.get(position).getIs_modifier(), myCart.get(position).getMaster_itemcode(), myCart.get(position).getCategoryIp(), "false");
                                                        Globals.voidcart.add(voidcartItem);

                                                    }
                                                }

                                                Globals.cart.remove(position);

                                            }
                                            else {
                                                try {
                                                    String Sr_no = Globals.cart.get(i).get_SRNO();
                                                    if (srno.equals(Sr_no)) {
                                                        shoppingCart = myCart.get(i);
                                                        Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(shoppingCart.get_Line_Total());
                                                        Globals.TotalItemCost = Globals.TotalItemCost - (Double.parseDouble(shoppingCart.get_Cost_Price()) * Double.parseDouble(shoppingCart.get_Quantity()));
                                                        Globals.TotalItem = Globals.TotalItem - 1;
                                                        Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity());

                                                        if (Globals.objsettings.getIs_KitchenPrint().equals("true")) {
                                                            Voidinsert = new Void(getApplicationContext(), null, orderCode, orders.get_device_code(), myCart.get(position).get_Item_Code(), myCart.get(position).getIs_modifier(), myCart.get(position).get_Quantity(), date, "false", "0", Globals.userId);
                                                            vd = Voidinsert.insertVoid(database);
                                                            if (vd > 0) {
                                                                VoidShoppingCart voidcartItem = new VoidShoppingCart(getApplicationContext(), myCart.get(position).get_SRNO() + "", myCart.get(position).get_Item_Code(), myCart.get(position).get_Item_Name(), myCart.get(position).get_Quantity(), myCart.get(position).get_Cost_Price(), myCart.get(position).get_Sales_Price() + "", myCart.get(position).get_Tax_Price() + "", myCart.get(position).get_Discount(), myCart.get(position).get_Line_Total(), myCart.get(position).getIs_modifier(), myCart.get(position).getMaster_itemcode(), myCart.get(position).getCategoryIp(), "false");
                                                                Globals.voidcart.add(voidcartItem);

                                                            }
                                                        }

                                                        myCart.remove(i);
                                                        i--;
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println(e.getMessage());
                                                }
                                            }
                                        }
                                    }
                                    Globals.cart = myCart;
                                    ArrayList<ShoppingCart> myCart = Globals.cart;
                                    while (position < myCart.size()) {
                                        myCart.get(position).set_SRNO(((Integer.parseInt(myCart.get(position).get_SRNO())) - 1) + "");
                                        position = position + 1;
                                    }
                                   // Globals.cart = myCart;
                                    pDialog.dismiss();

                                    for (int i = 0; i < Globals.order_item_tax.size(); i++) {
                                        if (item_code.equals(Globals.order_item_tax.get(i).get_item_code())) {
                                            Globals.order_item_tax.remove(i);


                                            i--;
                                        }
                                    }
                                    if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                        Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                        intent.putExtra("opr1", operation);
                                        intent.putExtra("order_code1", orderCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                        Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), R.string.Change_price_alrt, Toast.LENGTH_SHORT).show();

                                            if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                                Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                                intent.putExtra("opr1", operation);
                                                intent.putExtra("order_code1", orderCode);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                                Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                                intent.putExtra("opr", operation);
                                                intent.putExtra("order_code", orderCode);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            } else {

                                if (count_price.getText().toString().trim().equals("")) {
                                    count_price.setText("0");
                                }

                                if (count_qty.getText().toString().trim().equals("")) {
                                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("0"), qty_decimal_check));
                                }

                                if (!mastercode.equals("0")) {
                                    Globals.cart.remove(position);

                                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(shoppingCart.get_Line_Total());
                                    Globals.TotalItemCost = Globals.TotalItemCost - (Double.parseDouble(shoppingCart.get_Cost_Price()) * Double.parseDouble(shoppingCart.get_Quantity()));
                                    Globals.TotalItem = Globals.TotalItem - 1;
                                    Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity());

                                } else {

                                    int temp;
                                    for (int i = 0; i < myCart.size(); i++) {
                                        if (Globals.cart.get(i).get_SRNO().equals(srno)) {
                                            shoppingCart = myCart.get(i);
                                            Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(shoppingCart.get_Line_Total());
                                            Globals.TotalItemCost = Globals.TotalItemCost - (Double.parseDouble(shoppingCart.get_Cost_Price()) * Double.parseDouble(shoppingCart.get_Quantity()));
                                            Globals.TotalItem = Globals.TotalItem - 1;
                                            Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity());

                                            myCart.remove(i);

                                            temp = i;
                                            i--;


                                        }

                                    }


                                }

                                Globals.cart = myCart;

                                while (position < myCart.size()) {
                                    myCart.get(position).set_SRNO(((Integer.parseInt(myCart.get(position).get_SRNO())) - 1) + "");
                                    position = position + 1;
                                }
                                for (int i = 0; i < Globals.order_item_tax.size(); i++) {
                                    if (item_code.equals(Globals.order_item_tax.get(i).get_item_code())) {
                                        Globals.order_item_tax.remove(i);
                                        i--;
                                    }
                                }


                                pDialog.dismiss();
                                if (Globals.objLPR.getIndustry_Type().equals("1")) {
                                    if (main_land.equals("MainLand")) {
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                        intent.putExtra("opr1", operation);
                                        intent.putExtra("order_code1", orderCode);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                                else if(Globals.objLPR.getIndustry_Type().equals("2")) {
                                    Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                    intent.putExtra("opr", operation);
                                    intent.putExtra("order_code", orderCode);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                };
                timerThread.start();
            }
        });

        btn_plus_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (count_qty.getText().toString().trim().equals(".")) {
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                } else {
                Double qty = Double.parseDouble(count_qty.getText().toString().trim()) + 1;
                if (settings.get_Is_Stock_Manager().equals("true")){
                    boolean result = stock_check(item_code, qty);
                    if (result == true) {
                        btn_save.setEnabled(true);
                        count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
                    } else {
                        count_qty.selectAll();
                    }
                }else {
                    btn_save.setEnabled(true);
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
                }

            }
            }
        });

        btn_minus_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (count_qty.getText().toString().trim().equals(".")) {
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                } else {
                Double qty = Double.parseDouble(count_qty.getText().toString().trim()) - 1;

                if (qty <= 0) {
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                } else {

                        if(settings.getIs_KitchenPrint().equals("true")) {
                            Voidinsert = Void.getVoid(getApplicationContext(), "where order_no='" + orderCode + "' and item_code='" + item_code + "'", database);
                            if (Voidinsert != null) {

                                Voidinsert.setQty(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
                                long l = Voidinsert.updateVoid("vId=?", database, new String[]{Voidinsert.getvId()});
                                if (l > 0) {
                                    VoidShoppingCart voidcartItem = new VoidShoppingCart(getApplicationContext(), myCart.get(position).get_SRNO() + "", myCart.get(position).get_Item_Code(), myCart.get(position).get_Item_Name(), Voidinsert.getQty(), myCart.get(position).get_Cost_Price(), myCart.get(position).get_Sales_Price() + "", myCart.get(position).get_Tax_Price() + "", myCart.get(position).get_Discount(), myCart.get(position).get_Line_Total(), myCart.get(position).getIs_modifier(), myCart.get(position).getMaster_itemcode(), myCart.get(position).getCategoryIp(), "false");

                                    Globals.voidcart.add(voidcartItem);
                                }
                            } else {
                                long vd = 0;
                                Voidinsert = new Void(getApplicationContext(), null, orderCode, orders.get_device_code(), myCart.get(position).get_Item_Code(), myCart.get(position).getIs_modifier(), myCart.get(position).get_Quantity(), date, "false", "0", Globals.userId);
                                vd = Voidinsert.insertVoid(database);
                                if (vd > 0) {
                                    VoidShoppingCart voidcartItem = new VoidShoppingCart(getApplicationContext(), myCart.get(position).get_SRNO() + "", myCart.get(position).get_Item_Code(), myCart.get(position).get_Item_Name(), myCart.get(position).get_Quantity(), myCart.get(position).get_Cost_Price(), myCart.get(position).get_Sales_Price() + "", myCart.get(position).get_Tax_Price() + "", myCart.get(position).get_Discount(), myCart.get(position).get_Line_Total(), myCart.get(position).getIs_modifier(), myCart.get(position).getMaster_itemcode(), myCart.get(position).getCategoryIp(), "false");
                                    Globals.voidcart.add(voidcartItem);
                                }
                            }
                        }
                        else {
                            count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
                        }
                }
            }
            }
        });
        btn_minus_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double price = Double.parseDouble(count_price.getText().toString().trim()) - 0.10;
                String strPrice;
                strPrice = Globals.myNumberFormat2Price(price, decimal_check);

                if (price < 0) {
                    Double strDouble = 0.0;
                    String str = Globals.myNumberFormat2Price(strDouble, decimal_check);
                    count_price.setText(str);
                } else {
                    count_price.setText(strPrice + "");
                }
            }
        });

        btn_plus_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double price = Double.parseDouble(count_price.getText().toString().trim()) + 0.10;
                String strPrice;
                strPrice = Globals.myNumberFormat2Price(price, decimal_check);
                count_price.setText(strPrice);
            }
        });
    }

    private Boolean stock_check(String item_code, Double curQty) {
        boolean flag = false;
        try {
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            ck_project_type = lite_pos_registration.getproject_id();
//            if (ck_project_type.equals("standalone")) {
//                flag = true;
//            } else {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
            if (avl_stock >= curQty) {
                flag = true;
            } else {
                Toast.makeText(getApplicationContext(), "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
//            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Item not found in this location", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    public void AddToCart() {
        item_group_taxArrayList = calculateTax();
        if (count_price.getText().toString().trim().equals("") || count_qty.getText().toString().trim().equals("0")) {
            count_price.setText("0");
        }
        if (count_qty.getText().toString().trim().equals("") || count_qty.getText().toString().trim().equals("0")) {
            count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
        }
        final ProgressDialog pDialog = new ProgressDialog(ChangePriceActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (flag.equals("Main")) {
                                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                               Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item.get_item_group_code() + "'");

                                ArrayList<ShoppingCart> myCart = Globals.cart;
                                int count = 0;
                                boolean bFound = false;

                                while (count < myCart.size()) {

                                    if (item.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                        for (int i = 0; i < Globals.order_item_tax.size(); i++) {
                                            if (item_code.equals(Globals.order_item_tax.get(i).get_item_code())) {
                                                Globals.order_item_tax.remove(i);
                                                i--;
                                            }
                                        }
                                        bFound = true;
                                        Double iTax = 0d;
                                        Double iTaxTotal = 0d;
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            iTax = 0d;
                                            String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                            Double iPrice = Double.parseDouble(count_price.getText().toString().trim());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", item.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                        }
                                        Double strTotalTax = Double.parseDouble(count_qty.getText().toString().trim()) * iTaxTotal;
                                        Globals.TotalQty = Globals.TotalQty - Double.parseDouble(myCart.get(count).get_Quantity()) + Double.parseDouble(count_qty.getText().toString().trim());
                                        myCart.get(count).set_Sales_Price(count_price.getText().toString().trim());
                                        myCart.get(count).set_Quantity(((Double.parseDouble(count_qty.getText().toString().trim()) + "")));
                                        myCart.get(count).set_Tax_Price(iTaxTotal + "");
                                        line_total_bf = myCart.get(count).get_Line_Total() + "";
                                        myCart.get(count).set_Line_Total((Double.parseDouble(count_qty.getText().toString().trim()) * Double.parseDouble(count_price.getText().toString().trim())) + (strTotalTax) + "");
                                        Globals.cart = myCart;
                                        if (flag.equals("Main")) {
                                            pDialog.dismiss();
                                            if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                                if (settings.get_Home_Layout().equals("0")) {
                                                    Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                                    intent.putExtra("line_total_af", myCart.get(count).get_Line_Total());
                                                    intent.putExtra("line_total_bf", line_total_bf);
                                                    intent.putExtra("opr", operation);
                                                    intent.putExtra("order_code", orderCode);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                                    intent.putExtra("line_total_af", myCart.get(count).get_Line_Total());
                                                    intent.putExtra("line_total_bf", line_total_bf);
                                                    intent.putExtra("opr", operation);
                                                    intent.putExtra("order_code", orderCode);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                            else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                                Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                                intent.putExtra("line_total_af", myCart.get(count).get_Line_Total());
                                                intent.putExtra("line_total_bf", line_total_bf);
                                                intent.putExtra("opr", operation);
                                                intent.putExtra("order_code", orderCode);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                    count = count + 1;
                                }

                                if (!bFound) {
                                    sale_priceStr = count_price.getText().toString().trim();
                                    cost_priceStr = item_location.get_cost_price();
                                    Double iTax = 0d;
                                    Double iTaxTotal = 0d;
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        iTax = 0d;
                                        String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                        Double iPrice = Double.parseDouble(count_price.getText().toString().trim());
                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", item.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                    }
                                    Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    Double sprcewdtax=Double.parseDouble(sale_priceStr)-iTaxTotal;
                                    String spnunit= spn_unit_code;

                                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", item.get_item_code(), item.get_item_name(), count_qty.getText().toString().trim(), cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (Double.parseDouble(count_qty.getText().toString().trim()) * (Double.parseDouble(sale_priceStr) + iTaxTotal)) + "","0","0",item_group.getCategoryIp(),order_detail.getIs_KitchenPrintFlag(),spnunit,sprcewdtax+"");
                                    Globals.cart.add(cartItem);
                                    ArrayList<ShoppingCart> myCart1 = Globals.cart;
                                    Globals.SRNO = Globals.SRNO + 1;
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (Double.parseDouble(count_qty.getText().toString().trim()) * Double.parseDouble(sprice + ""));
                                    Globals.TotalItem = Globals.TotalItem + 1;
                                    Globals.TotalQty = Globals.TotalQty + Double.parseDouble(count_qty.getText().toString().trim());
                                    Globals.cart = myCart1;
                                    pDialog.dismiss();
                                    if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                            // we change here becuase of crash
                                            intent.putExtra("line_total_af", myCart1.get(myCart1.size() - 1).get_Line_Total());
                                            intent.putExtra("line_total_bf", line_total_bf);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                            // we change here becuase of crash
                                            intent.putExtra("line_total_af", myCart1.get(myCart1.size() - 1).get_Line_Total());
                                            intent.putExtra("line_total_bf", line_total_bf);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                    else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                        Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                        intent.putExtra("line_total_af", myCart1.get(myCart1.size() - 1).get_Line_Total());
                                        intent.putExtra("line_total_bf", line_total_bf);
                                        intent.putExtra("opr", operation);
                                        intent.putExtra("order_code", orderCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } else {
                                Double iTax = 0d;
                                Double iTaxTotal = 0d;

                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                    iTax = 0d;
                                    String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                    Double iPrice = Double.parseDouble(count_price.getText().toString().trim());

                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                    } else {
                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                    }
                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                }
                                // add this line 2017-12-16
                                Double strTotalTax = Double.parseDouble(count_qty.getText().toString().trim()) * iTaxTotal;

                                for (int i = 0; i < Globals.order_item_tax.size(); i++) {
                                    if (item_code.equals(Globals.order_item_tax.get(i).get_item_code())) {
                                        Globals.order_item_tax.remove(i);
                                        i--;
                                    }

                                }

                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                    iTax = 0d;
                                    String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                    Double iPrice = Double.parseDouble(count_price.getText().toString().trim());

                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                    } else {
                                        iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                                    }
                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", item_group_taxArray.get(i).get_item_group_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                    Globals.order_item_tax.add(order_item_tax);
                                }
                                ArrayList<ShoppingCart> myCart = Globals.cart;
                                Globals.TotalQty = Globals.TotalQty - Double.parseDouble(shoppingCart.get_Quantity()) + Double.parseDouble(count_qty.getText().toString().trim());
                                myCart.get(position).set_Sales_Price(count_price.getText().toString().trim());
                                myCart.get(position).set_Quantity(((Double.parseDouble(count_qty.getText().toString().trim()) + "")));
                                myCart.get(position).set_Tax_Price(iTaxTotal + "");
                                line_total_bf = myCart.get(position).get_Line_Total() + "";
                                myCart.get(position).setUnitId(spn_unit_code);
                                myCart.get(position).set_Line_Total((Double.parseDouble(count_qty.getText().toString().trim()) * Double.parseDouble(count_price.getText().toString().trim())) + (strTotalTax) + "");
                                Globals.cart = myCart;
                                pDialog.dismiss();
                                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                    if (main_land.equals("MainLand")) {
                                        if (settings.get_Home_Layout().equals("0")) {
                                            Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                                            intent.putExtra("line_total_af", myCart.get(position).get_Line_Total());
                                            intent.putExtra("line_total_bf", line_total_bf);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                                            intent.putExtra("line_total_af", myCart.get(position).get_Line_Total());
                                            intent.putExtra("line_total_bf", line_total_bf);
                                            intent.putExtra("opr", operation);
                                            intent.putExtra("order_code", orderCode);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                                        Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(myCart.get(position).get_Line_Total());
                                        Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                                        intent.putExtra("line_total_af", "");
                                        intent.putExtra("line_total_bf", "");
                                        intent.putExtra("opr1", operation);
                                        intent.putExtra("order_code1", orderCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(myCart.get(position).get_Line_Total());
                                    Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                                    intent.putExtra("line_total_af", "");
                                    intent.putExtra("line_total_bf", "");
                                    intent.putExtra("opr", operation);
                                    intent.putExtra("order_code", orderCode);
                                    startActivity(intent);
                                    finish();
                                }
                            }
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

    private void fill_spinner_unit(String str) {
        Unit unit;
        arrayUnitList = Unit.getAllUnit(getApplicationContext(), " WHERE is_active ='1' Order By name asc");
        if (str.equals("")) {
            unit = Unit.getUnit(getApplicationContext(), database, db, " WHERE is_active ='1'");
            if (arrayUnitList.size() > 0) {
                itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                spn_unit.setAdapter(itemUnitListAdapter);
            }
        } else {
            unit = Unit.getUnit(getApplicationContext(), database, db, " WHERE is_active ='1' and unit_id = '" + str + "'");
            if (unit == null) {
                if (arrayUnitList.size() > 0) {
                    itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                    spn_unit.setAdapter(itemUnitListAdapter);
                }
            } else {

                if (arrayUnitList.size() > 0) {
                    itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                    spn_unit.setAdapter(itemUnitListAdapter);

                    if (!unit.get_name().toString().equals("")) {
                        for (int i = 0; i < itemUnitListAdapter.getCount(); i++) {
                            String iname = arrayUnitList.get(i).get_name();
                            if (unit.get_name().equals(iname)) {
                                spn_unit.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    private ArrayList<String> calculateTax() {
        ArrayList<Tax_Detail> taxIdAarry = new ArrayList<Tax_Detail>();
        ArrayList<String> taxIdFinalAarry = new ArrayList<String>();
        ArrayList<Item_Group_Tax> item_group_taxList = new ArrayList<Item_Group_Tax>();
        try {
            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {
                Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                if (taxIdAarry.size() > 0) {
                    for (int i = 0; i < taxIdAarry.size(); i++) {
                        Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                } else {
                    item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                    for (int i = 0; i < item_group_taxList.size(); i++) {
                        Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                }

            } else {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE contact_code='" + Globals.strContact_Code + "'");
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                if (contact.get_zone_id().equals(lite_pos_registration.getZone_Id())) {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Interstate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                } else {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "where type='Intrastate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(getApplicationContext(), " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(getApplicationContext(), " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return taxIdFinalAarry;
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ChangePriceActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    if(Globals.objLPR.getIndustry_Type().equals("1"))
                    {
                    if (settings.get_Home_Layout().equals("0")) {
                        if (flag.equals("Main")) {
                            Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                            intent.putExtra("opr", operation);
                            intent.putExtra("order_code", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } else if (main_land.equals("MainLand")){
                            Intent intent = new Intent(ChangePriceActivity.this, MainActivity.class);
                            intent.putExtra("opr", operation);
                            intent.putExtra("order_code", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        }else{
                            Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                            intent.putExtra("opr1", operation);
                            intent.putExtra("order_code1", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        }
                    } else {
                        if (flag.equals("Main")) {
                            Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                            intent.putExtra("opr", operation);
                            intent.putExtra("order_code", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } else if (main_land.equals("MainLand")) {
                            Intent intent = new Intent(ChangePriceActivity.this, Main2Activity.class);
                            intent.putExtra("opr", operation);
                            intent.putExtra("order_code", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } else {
                            Intent intent = new Intent(ChangePriceActivity.this, RetailActivity.class);
                            intent.putExtra("opr1", operation);
                            intent.putExtra("order_code1", orderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        }
                    }
                    }
                    else if(Globals.objLPR.getIndustry_Type().equals("2")){
                        Intent intent = new Intent(ChangePriceActivity.this, Retail_IndustryActivity.class);
                        intent.putExtra("opr", operation);
                        intent.putExtra("order_code", orderCode);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();

    }
}
