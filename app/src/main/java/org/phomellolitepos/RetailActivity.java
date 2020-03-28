package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Adapter.RetailListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import sunmi.ds.DSKernel;

public class RetailActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    RetailListAdapter retailListAdapter;
    AutoCompleteTextView edt_toolbar_retail;
    EditText edt_toolbar_search;
    Button btn_retail_1, btn_retail_2;
    ArrayList<String> arrayList1;
    ArrayList<Item> arrayList;
    String decimal_check, qty_decimal_check;
    Database db;
    String sale_priceStr;
    String cost_priceStr;
    SQLiteDatabase database;
    ArrayList<String> item_group_taxArrayList;
    String line_total_af,line_total_bf,operation,opr,strOrderCode;
    TextView txt_title;
    String strRemarks = "",date,modified_by;
    Button btn_Item_Price, btn_Qty;
    Dialog listDialog2;
    MenuItem orertype;
    ProgressDialog pDialog;
    String item_group_code;
    Double curQty = 0d;
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type;
    Settings settings;
    ZBarScannerView mScannerView;
    boolean flag_scan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_view();
    }

    private void autocomplete(String strFilter) {
        strFilter = " and ( item.item_code Like '" + strFilter + "%'  Or item.item_name Like '" + strFilter + "%' )";
        if (settings.get_Is_Zero_Stock().equals("true")) {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), " WHERE is_active = '1' " + strFilter + " limit 10");
        } else {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1'  " + strFilter + "  and item_location.quantity!='0'  Order By lower(item_name) asc limit 10");
        }

        if (arrayList1.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, R.layout.items_spinner, arrayList1);
            edt_toolbar_retail.setThreshold(0);
            edt_toolbar_retail.setAdapter(adapter);
        }
    }

    private void retail_list_load() {
        ArrayList<ShoppingCart> myCart = Globals.cart;
//        Collections.reverse(myCart);
        ListView retail_list = (ListView) findViewById(R.id.retail_list);
        if (myCart.size() > 0) {
            retailListAdapter = new RetailListAdapter(RetailActivity.this, myCart, opr, strOrderCode, "");
            retail_list.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
            retail_list.setAdapter(retailListAdapter);
            retailListAdapter.notifyDataSetChanged();
        } else {
            retail_list.setVisibility(View.GONE);
            txt_title.setVisibility(View.VISIBLE);
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
            if (edt_toolbar_retail.getText().toString().equals("\n") || edt_toolbar_retail.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                edt_toolbar_retail.requestFocus();
            } else {
                AddToCart();
            }
            return true;
        }

        if (id == R.id.action_qr) {
            Globals.BarcodeReslt = "";
            flag_scan = true;
            mScannerView.startCamera(); // Programmatically initialize the scanner view
            setContentView(mScannerView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddToCart() {
        String strValue = edt_toolbar_retail.getText().toString().trim();
        String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
        arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
        if (arrayList.size() >= 1) {
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            ck_project_type = lite_pos_registration.getproject_id();

            if (settings.get_Is_Stock_Manager().equals("false")) {
                Item resultp = arrayList.get(0);
                String item_code = resultp.get_item_code();
                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                ArrayList<ShoppingCart> myCart = Globals.cart;
                int count = 0;
                boolean bFound = false;
                while (count < myCart.size()) {
                    if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                        bFound = true;
                        myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                        myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                        //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                        Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                        Globals.TotalQty = Globals.TotalQty + 1;

                    }
                    count = count + 1;
                }

                if (!bFound) {

                    if (item_location == null) {
                        sale_priceStr = "0";
                        cost_priceStr = "0";
                    } else {
                        sale_priceStr = item_location.get_selling_price();
                        cost_priceStr = item_location.get_cost_price();
                    }
                    ArrayList<String> item_group_taxArrayList = calculateTax();
                    Double iTax = 0d;
                    Double iTaxTotal = 0d;
                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                        iTax = 0d;
                        String tax_id = item_group_taxArrayList.get(i);
                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                        if (tax_master.get_tax_type().equals("P")) {
                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                        } else {
                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                        }
                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                        Globals.order_item_tax.add(order_item_tax);
                    }
                    Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
//                    Globals.cart.add(cartItem);
                    Globals.cart.add(0, cartItem);
                    Globals.SRNO = Globals.SRNO + 1;
                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                    Globals.TotalItem = Globals.TotalItem + 1;
                    Globals.TotalQty = Globals.TotalQty + 1;
                }
                Globals.cart = myCart;
                retail_list_load();
                String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_retail_2.setText(item_price);
                btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                edt_toolbar_retail.setText("");
            } else {
                Item resultp = arrayList.get(0);
                String item_code = resultp.get_item_code();
                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                ArrayList<ShoppingCart> myCart = Globals.cart;
                int count = 0;
                boolean bFound = false;
                while (count < myCart.size()) {
                    if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                        bFound = true;
                        curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                        boolean result = stock_check(item_code, curQty + 1);
                        if (result == true) {
                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                            Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                            Globals.TotalQty = Globals.TotalQty + 1;
                        }
                    }
                    count = count + 1;
                }

                if (!bFound) {
                    curQty = 0d;
                    boolean result = stock_check(item_code, Double.parseDouble("1"));
                    if (result == true) {
                        if (item_location == null) {
                            sale_priceStr = "0";
                            cost_priceStr = "0";
                        } else {
                            sale_priceStr = item_location.get_selling_price();
                            cost_priceStr = item_location.get_cost_price();
                        }
                        ArrayList<String> item_group_taxArrayList = calculateTax();
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                            iTax = 0d;
                            String tax_id = item_group_taxArrayList.get(i);
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            } else {
                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                            }
                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                            Globals.order_item_tax.add(order_item_tax);
                        }
                        Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
//                        Globals.cart.add(cartItem);
                        Globals.cart.add(0, cartItem);
                        Globals.SRNO = Globals.SRNO + 1;
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                        Globals.TotalItem = Globals.TotalItem + 1;
                        Globals.TotalQty = Globals.TotalQty + 1;
//                    curQty = curQty+1;
                    }
                }
                Globals.cart = myCart;
                retail_list_load();
                String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_retail_2.setText(item_price);
                btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                edt_toolbar_retail.setText("");
            }

        } else {
            edt_toolbar_retail.requestFocus();
            edt_toolbar_retail.selectAll();
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void save_order(String strRemarks) {
        String strFlag1 = "0";
        String strOrdeeStatus = "OPEN";
        String strOrderNo = "";
        Order_Detail objOrderDetail;
        Orders objOrder;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        database.beginTransaction();

        if (opr.equals("Edit")) {
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            objOrder = Orders.getOrders(getApplicationContext(), database, "  WHERE order_code = '" + strOrderCode + "'");
            objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderCode, objOrder.get_order_date(), Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, objOrder.get_remarks(), objOrder.get_table_code(), "");
            long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderCode, objOrder.get_order_id()}, database);
            if (l > 0) {
                strFlag1 = "1";
                long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderCode}, database);

                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderCode,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
                long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderCode}, database);
                long p = Order_Payment.delete_order_payment(getApplicationContext(), "order_payment", " order_code =? ", new String[]{strOrderCode}, database);
                ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                Order_Detail_Tax objOrderDetailTax;
                long l1 = Order_Detail_Tax.delete_Order_Detail_Tax(getApplicationContext(), "order_detail_tax", " order_code =? ", new String[]{strOrderCode}, database);

                for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                    Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                    objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderCode, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                            , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());
                    long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {

                    }
                }
            }
        } else {
            Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");

            if (objOrder1 == null) {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + 1;
            } else {
                strOrderNo = Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
            }
            ArrayList<ShoppingCart> myCart = Globals.cart;
            String locCode = null;
            try {
                locCode = Globals.objLPD.getLocation_Code();
            } catch (Exception ex) {
                locCode = "";
            }
            objOrder = new Orders(getApplicationContext(), null, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, strRemarks, Globals.strTable_Code, "");
            long l = objOrder.insertOrders(database);
            if (l > 0) {
                strFlag1 = "1";
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", "0", mCart.get_Line_Total(), "0");
                    long o = objOrderDetail.insertOrder_Detail(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
                ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                Order_Detail_Tax objOrderDetailTax;
                for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                    Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);
                    objOrderDetailTax = new Order_Detail_Tax(getApplicationContext(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                            , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());
                    long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                    if (o > 0) {
                        strFlag1 = "1";
                    } else {
                    }
                }
            }
        }

        if (strFlag1.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            Globals.setEmpty();
            opr = "";
            strOrderCode = "";
            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(itemPrice);
            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();

            if (settings.get_Home_Layout().equals("0")) {
                Intent intent = new Intent(RetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(RetailActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    private void showdialogremarksdialog() {
        listDialog2.setTitle(R.string.Remarks);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
        listDialog2.show();
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog2.dismiss();
            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_remark.getText().toString().equals("")) {
                } else {
                    strRemarks = edt_remark.getText().toString();
                }
                save_order(strRemarks);
                listDialog2.dismiss();
            }
        });
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

    private Boolean stock_check(String item_code, Double curQty) {
        boolean flag = false;
        try {
            Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), " where item_code='" + item_code + "'", database);
//        Order_Detail order_detail = Order_Detail.getOrder_Detail(getApplicationContext(), " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
//        Double total_qty = Double.parseDouble(order_detail.get_quantity()) + curQty;
            if (avl_stock >= curQty) {
                flag = true;
            } else {
                Toast.makeText(getApplicationContext(), "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Item not found in this location", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    public boolean Order_Total_Validation() {
        boolean resOrder = false;
        Double GTotal = 0d;
        Double DetailTotal = 0d;

        GTotal = Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check));

        for (int count = 0; count < Globals.cart.size(); count++) {
            ShoppingCart mCart = Globals.cart.get(count);
            DetailTotal = DetailTotal + Double.parseDouble(mCart.get_Line_Total());
        }
        if (Globals.TotalItemPrice - DetailTotal == 0) {
            if (GTotal - DetailTotal == 0) {
                resOrder = true;
            } else {
                Globals.TotalItemPrice = DetailTotal;
            }
        }
        return resOrder;

    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        //Prints the scan format (qrcode, pdf417 etc.)
        Globals.BarcodeReslt = rawResult.getContents();
        mScannerView.stopCameraPreview(); //stopPreview
        mScannerView.stopCamera();
        removeView(mScannerView);
        Globals.BarcodeReslt = "";
        if (settings.get_QR_Type().equals("0")) {
            flag_scan = false;
        } else {
            if (flag_scan == true) {
                mScannerView.startCamera(); // Programmatically initialize the scanner view
                setContentView(mScannerView);
            }
        }
    }

    public void load_cart() {
        if (edt_toolbar_retail.getText().toString().equals("\n") || edt_toolbar_retail.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
            edt_toolbar_retail.requestFocus();
        } else {
            AddToCart();
        }
    }

    public void removeView(View view) {
        ViewGroup vg = (ViewGroup) (view.getParent());
        vg.removeView(view);

        main_view();

        edt_toolbar_retail.setText(Globals.BarcodeReslt + "");
        edt_toolbar_retail.selectAll();
        load_cart();
    }

    public void main_view() {
        setContentView(R.layout.activity_retail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        txt_title = (TextView) findViewById(R.id.txt_title);
        edt_toolbar_retail = (AutoCompleteTextView) findViewById(R.id.edt_toolbar_retail);
        edt_toolbar_search = (EditText) findViewById(R.id.edt_toolbar_search);
        btn_retail_1 = (Button) findViewById(R.id.btn_retail_1);
        btn_retail_2 = (Button) findViewById(R.id.btn_retail_2);

        mScannerView = new ZBarScannerView(RetailActivity.this);
        mScannerView.setResultHandler(RetailActivity.this);
        edt_toolbar_search.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.retail_bottom_navigation);

        Intent intent = getIntent();
        // operation = intent.getStringExtra("opn");
        Globals.load_form_cart = "1";
        opr = intent.getStringExtra("opr1");
        strOrderCode = intent.getStringExtra("order_code1");
        modified_by = Globals.user;
        if (opr == null) {
            opr = "Add";
        }
        if (strOrderCode == null) {
            strOrderCode = "";

        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        listDialog2 = new Dialog(this);

        if (opr.equals("Edit")) {
            try {
                if (flag_scan == true) {
                } else {
//                    line_total_af = intent.getStringExtra("line_total_af");
//                    line_total_bf = intent.getStringExtra("line_total_bf");
//
//                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
//                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                }
            } catch (Exception ex) {

            }

            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String item_price;
            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(item_price);

            Menu menu = bottomNavigationView.getMenu();
            orertype = menu.findItem(R.id.action_clear);
            orertype.setEnabled(false);

        } else {
            try {
                if (flag_scan == true) {
                } else {
//                    line_total_af = intent.getStringExtra("line_total_af");
//                    line_total_bf = intent.getStringExtra("line_total_bf");

//                    Globals.TotalItemPrice = Globals.TotalItemPrice - Double.parseDouble(line_total_bf);
//                    Globals.TotalItemPrice = Globals.TotalItemPrice + Double.parseDouble(line_total_af);
                }
            } catch (Exception ex) {}

            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String item_price;

            item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(item_price);
        }

        arrayList1 = new ArrayList<String>();
        settings = Settings.getSettings(getApplicationContext(), database, "");
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

                pDialog = new ProgressDialog(RetailActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(RetailActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        } else {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(RetailActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    }
                };
                timerThread.start();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_check_out:
//                                boolean OrdValdCheckOut = Order_Total_Validation();
//                                if (!OrdValdCheckOut) {
//                                    Toast.makeText(getApplicationContext(), "Order and Item total are not equal so updating total!", Toast.LENGTH_LONG).show();
//                                }
                                if (Globals.cart.size() == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog = new ProgressDialog(RetailActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);

                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Intent intent = new Intent(RetailActivity.this, PaymentActivity.class);
                                                        intent.putExtra("opr", opr);
                                                        intent.putExtra("order_code", strOrderCode);
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
                                break;

                            case R.id.action_save:

                                if (Globals.cart.size() == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    Globals.CheckContact = "0";
                                    pDialog = new ProgressDialog(RetailActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        if (opr.equals("Edit")) {
                                                            save_order(strRemarks);
                                                        } else {
                                                            showdialogremarksdialog();
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

                                break;
                            case R.id.action_clear:
                                Globals.strContact_Code = "";
                                Globals.strResvContact_Code = "";
                                Globals.CheckContact = "0";
                                edt_toolbar_search.setText("");
                                edt_toolbar_search.requestFocus();
//                                String cart_check2 = Globals.TotalItemPrice + "";
                                if (Globals.cart.size() == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.Cart_is_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    Globals.setEmpty();
                                    retail_list_load();
                                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                    String item_price;
                                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                    btn_retail_2.setText(item_price);
                                    break;
                                }
                        }
                        return true;
                    }
                });

        retail_list_load();

        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.activity_retail, null);
        getSupportActionBar().setCustomView(v);

        edt_toolbar_retail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_retail.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_retail.requestFocus();
                    edt_toolbar_retail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_retail, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_retail.selectAll();
                    return true;
                }
            }
        });

        edt_toolbar_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_search.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_search.requestFocus();
                    edt_toolbar_search.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_search, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_search.selectAll();
                    return true;
                }
            }
        });

        edt_toolbar_retail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strFilter = edt_toolbar_retail.getText().toString();
                autocomplete(strFilter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        edt_toolbar_search.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    if (event.getAction() == KeyEvent.ACTION_DOWN
                                                            && keyCode == KeyEvent.KEYCODE_ENTER) {
                                                        String strValue = edt_toolbar_search.getText().toString();

                                                        if (edt_toolbar_search.getText().toString().equals("\n") || edt_toolbar_search.getText().toString().equals("")) {
                                                            Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                            edt_toolbar_search.requestFocus();
                                                        } else {
                                                            String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                            arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                            if (arrayList.size() >= 1) {
                                                                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                                ck_project_type = lite_pos_registration.getproject_id();

                                                                if (settings.get_Is_Stock_Manager().equals("false")) {
                                                                    Item resultp = arrayList.get(0);
                                                                    String item_code = resultp.get_item_code();

                                                                    item_group_code = resultp.get_item_code();
                                                                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                    ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                    int count = 0;
                                                                    boolean bFound = false;

                                                                    while (count < myCart.size()) {
                                                                        if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                            bFound = true;
                                                                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                                                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                            Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                            Globals.TotalQty = Globals.TotalQty + 1;
                                                                        }
                                                                        count = count + 1;
                                                                    }

                                                                    if (!bFound) {
                                                                        if (item_location == null) {
                                                                            sale_priceStr = "0";
                                                                            cost_priceStr = "0";
                                                                        } else {
                                                                            sale_priceStr = item_location.get_selling_price();
                                                                            cost_priceStr = item_location.get_cost_price();
                                                                        }
                                                                        ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                        Double iTax = 0d;
                                                                        Double iTaxTotal = 0d;
                                                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                            iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                            String tax_id = item_group_taxArrayList.get(i);
                                                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                            } else {
                                                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                            }
                                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                            Globals.order_item_tax.add(order_item_tax);
                                                                        }
                                                                        Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
//                                                                        Globals.cart.add(cartItem);
                                                                        Globals.cart.add(0, cartItem);
                                                                        Globals.SRNO = Globals.SRNO + 1;
                                                                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                        Globals.TotalItem = Globals.TotalItem + 1;
                                                                        Globals.TotalQty = Globals.TotalQty + 1;
                                                                    }
                                                                    Globals.cart = myCart;
                                                                    retail_list_load();
                                                                    String item_price;
                                                                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                    btn_retail_2.setText(item_price);
                                                                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                    edt_toolbar_search.setText("");
                                                                } else {
                                                                    Item resultp = arrayList.get(0);
                                                                    String item_code = resultp.get_item_code();

                                                                    item_group_code = resultp.get_item_code();
                                                                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                    ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                    int count = 0;
                                                                    boolean bFound = false;

                                                                    while (count < myCart.size()) {
                                                                        if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                            bFound = true;
                                                                            curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                                                            boolean result = stock_check(item_code, curQty + 1);
                                                                            if (result == true) {
                                                                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                                                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                                                                Globals.TotalQty = Globals.TotalQty + 1;
//                                                                            curQty = curQty+1;
                                                                            }

                                                                        }
                                                                        count = count + 1;
                                                                    }

                                                                    if (!bFound) {
                                                                        curQty = 0d;
                                                                        boolean result = stock_check(item_code, Double.parseDouble("1"));
                                                                        if (result == true) {
                                                                            if (item_location == null) {
                                                                                sale_priceStr = "0";
                                                                                cost_priceStr = "0";
                                                                            } else {
                                                                                sale_priceStr = item_location.get_selling_price();
                                                                                cost_priceStr = item_location.get_cost_price();
                                                                            }
                                                                            ArrayList<String> item_group_taxArrayList = calculateTax();
                                                                            Double iTax = 0d;
                                                                            Double iTaxTotal = 0d;
                                                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                                                iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                                                                                String tax_id = item_group_taxArrayList.get(i);
                                                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                                                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                                                if (tax_master.get_tax_type().equals("P")) {
                                                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                                                } else {
                                                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                                                }
                                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                                                Globals.order_item_tax.add(order_item_tax);
                                                                            }
                                                                            Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
//                                                                            Globals.cart.add(cartItem);
                                                                            Globals.cart.add(0, cartItem);
                                                                            Globals.SRNO = Globals.SRNO + 1;
                                                                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                                                            Globals.TotalItem = Globals.TotalItem + 1;
                                                                            Globals.TotalQty = Globals.TotalQty + 1;
//                                                                        curQty = curQty+1;
                                                                        }
                                                                    }
                                                                    Globals.cart = myCart;
                                                                    retail_list_load();
                                                                    String item_price;
                                                                    item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                    btn_retail_2.setText(item_price);
                                                                    btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                    edt_toolbar_search.setText("");
                                                                }

                                                            } else {
                                                                edt_toolbar_search.selectAll();
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


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pDialog = new ProgressDialog(RetailActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (flag_scan == true) {
                    Globals.BarcodeReslt = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mScannerView.stopCameraPreview();
                            mScannerView.stopCamera();
                            ViewGroup vg = (ViewGroup) (mScannerView.getParent());
                            vg.removeView(mScannerView);
                            main_view();
                            flag_scan = false;
                            pDialog.dismiss();
                        }
                    });

                } else {
                    Globals.BarcodeReslt = "";
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(RetailActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    } else {
                        try {
                            sleep(1000);
                            Intent intent = new Intent(RetailActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("opr", opr);
                            intent.putExtra("order_code", strOrderCode);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                }
            }
        };
        timerThread.start();
    }
}
