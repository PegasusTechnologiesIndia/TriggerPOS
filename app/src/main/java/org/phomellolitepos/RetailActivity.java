package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.RetailListAdapter;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.ScaleSetup;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.VoidShoppingCart;
import org.phomellolitepos.printer.WifiPrintDriver;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import static org.phomellolitepos.Util.Globals.StringSplit;

public class RetailActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    RetailListAdapter retailListAdapter;
    AutoCompleteTextView edt_toolbar_retail;
    EditText edt_toolbar_search;
    Button btn_retail_1, btn_retail_2;
    JavaEncryption javaEncryption;
    ArrayList<String> arrayList1;
    ArrayList<Item> arrayList;
    String versionname;
    ArrayList<ReceipeModifier> receipemodifierlist;
    String decimal_check, qty_decimal_check;
    Database db;   String strSelectedCategory = "";
    Dialog listDialog1;
    String sale_priceStr;
    String cost_priceStr;
    Contact contact;
    SQLiteDatabase database;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    String line_total_af,line_total_bf,operation,opr,strOrderCode;
    TextView txt_title;
    String strRemarks = "",date,modified_by;
    DialogContactMainListAdapter dialogContactMainListAdapter;
    Button btn_Item_Price, btn_Qty;
    Dialog listDialog2;
    MenuItem orertype;
    ProgressDialog pDialog;
    ProgressDialog  progressDialog;
    String item_group_code;
    ArrayList<Contact> contact_ArrayList;
    Double curQty = 0d;
    Sys_Tax_Group sys_tax_group;
    Lite_POS_Registration lite_pos_registration;
    String ck_project_type;
    Settings settings;
    ScaleSetup scalesetup;
    ZBarScannerView mScannerView;
    boolean flag_scan = false;
    String barcodelength="0",plvaluelen;
    String weightlength;
    double Dweightvalue=0;
    String strKitchenFlag="";
    private ArrayList<String> mylist = new ArrayList<String>();
    ArrayList<String> ipAdd = new ArrayList<String>();
    ArrayList<Item_Group> itemgroup_catArrayList;
    private ArrayList<String> catId = new ArrayList<String>();
    String ip = null;
    Order_Type order_type;
    BottomNavigationView bottomNavigationView;
    //AppLocationService appLocationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_view();
    }

    private void autocomplete(String strFilter) {
        strFilter = " and ( item.item_code Like '" + strFilter + "%'  Or item.item_name Like '" + strFilter + "%' Or item.barcode Like '" + strFilter + "%' )";
        if (settings.get_Is_Zero_Stock().equals("true")) {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), " WHERE is_active = '1' and  is_modifier != '1' " + strFilter + " limit 10");
        } else {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1' " + strFilter + "  and item_location.quantity!='0' and item.is_modifier != '1'  Order By lower(item_name) asc limit 10");
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
            if(mScannerView!=null) {
                mScannerView = new ZBarScannerView(RetailActivity.this);
                mScannerView.setResultHandler(RetailActivity.this);
                mScannerView.startCamera(); // Programmatically initialize the scanner view
                setContentView(mScannerView);
            }
            return true;
        }

       /* if (id == R.id.action_contact) {
            showdialogContact();


            return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    public void change_customer_icon() {
        bottomNavigationView  = (BottomNavigationView) findViewById(R.id.retail_bottom_navigation);
        Menu menu2 = bottomNavigationView.getMenu();
        MenuItem cusIcon = menu2.findItem(R.id.action_contact);
        if (Globals.strContact_Code.equals("") && Globals.strResvContact_Code.equals("")) {
            //if (Globals.strContact_Code.equals("") ) {
            bottomNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.contact1);
        } else {
            bottomNavigationView.setItemIconTintList(null);
            cusIcon.setIcon(R.drawable.green);
        }
    }
    // Contact Display Dialog
    private void showdialogContact() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog1 = new Dialog(this);
        listDialog1.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);
        final ListView list11 = (ListView) listDialog1.findViewById(R.id.lv_custom_ortype);
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        srch_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
            }
        });

        img_brs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listDialog1.dismiss();
                    Globals.BarcodeReslt = "";
                    mScannerView.startCamera();
                    // Programmatically initialize the scanner view
                    setContentView(mScannerView);
                } catch (Exception ex) {
                }

            }
        });

    }
    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "and business_group_code='" + str + "'";
        }

        if (Globals.objsettings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogContactMainListAdapter = new DialogContactMainListAdapter(RetailActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);

    }


    public void AddToCart() {
        String strValue = edt_toolbar_retail.getText().toString().trim();
        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

/*    int barcodelen = Integer.parseInt(barcodelength);

        if(strValue.length()==barcodelen){

            String pllen=strValue.substring(0,Integer.parseInt(scalesetup.getPlu_len()));
            String plengthfinal=strValue.substring(pllen.length());
            if(pllen.equals(scalesetup.getPlu_value())){
                // String itemlen= strValue.substring(3,7);

                String itemlen= plengthfinal.substring(0,Integer.parseInt(scalesetup.getITEM_CODE_LEN()));
                String itemcodefinal= plengthfinal.substring(itemlen.length());

                weightlength= itemcodefinal.substring(0,Integer.parseInt(scalesetup.getWp_LEN()));
                String wightlenfinal= weightlength.substring(weightlength.length());
                // String weight=strValue.substring(itemlen.length(),itemlen.length()-1);

                *//************* Scale Integration Functon *********************//*

                try {
                    getScaleBarcodefunc(strValue, weightlength);
                }
                catch(Exception e){

                }
            }
        }
        else {*/
            String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
            arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
            if (arrayList.size() >= 1) {
                lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                ck_project_type = lite_pos_registration.getproject_id();
                Item resultp = arrayList.get(0);
                if (settings.get_Is_Stock_Manager().equals("false")) {
                    // Item resultp = arrayList.get(0);
                    String item_code = resultp.get_item_code();
                    item_group_code = resultp.get_item_code();
                    item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                    Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                    ArrayList<ShoppingCart> myCart = Globals.cart;
                    int count = 0;
                    boolean bFound = false;
                    Double spricewdtax=0d;
                    while (count < myCart.size()) {
                        if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                            bFound = true;
                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            //myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");

                            if(resultp.get_is_inclusive_tax().equals("0")) {
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                            }
                            else{
                                spricewdtax= Double.parseDouble(sale_priceStr)- Double.parseDouble(myCart.get(count).get_Tax_Price());
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * spricewdtax) + Double.parseDouble(myCart.get(count).get_Tax_Price());

                            }
                           // Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
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
                        //ArrayList<String> item_group_taxArrayList = calculateTax();
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        Double sprice=0d;
                        if(item_group_taxArrayList.size()>0) {
                            if(Globals.objLPR.getCountry_Id().equals("99")) {
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                    iTax = 0d;
                                                    //String tax_id = item_group_taxArrayList.get(i);
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                    }
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        }
                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                    }
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                            else if(Globals.objLPR.getCountry_Id().equals("114")){
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        }
                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if (tax_master != null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                    }
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                            else if(Globals.objLPR.getCountry_Id().equals("221")){
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        }
                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if (tax_master != null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                    }
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                        }
                        else{
                            sprice = Double.parseDouble(sale_priceStr);
                        }
                        Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                        sale_priceStr=String.valueOf(sprice);
                        Double beforetaxprice=sprice-iTaxTotal;
                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                    Globals.cart.add(cartItem);
                        Globals.cart.add(0, cartItem);
                        receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                        if (receipemodifierlist.size() > 0) {

                            Globals.SRNO = Globals.SRNO;
                        } else {
                            Globals.SRNO = Globals.SRNO + 1;
                        }
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
                            //  ArrayList<String> item_group_taxArrayList = calculateTax();
                            Double iTax = 0d;
                            Double iTaxTotal = 0d;
                            Double sprice=0d;
                            if(contact!=null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                    else if(!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                                else{
                                    sprice = Double.parseDouble(sale_priceStr);
                                }
                            }

                            else if(contact==null){
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                    }
                                }
                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                            }

                            Double beforetaxprice=sprice-iTaxTotal;
                            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");

                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "", "",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
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
                String itemcode = resultp.get_item_code();

                receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
                if (receipemodifierlist.size() > 0) {
                    Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                    i.putExtra("itemcode", resultp.get_item_code());
                    i.putExtra("opr", Globals.Operation);
                    i.putExtra("srno", Globals.SRNO);
                    i.putExtra("odr_code", Globals.Order_Code);
                    startActivity(i);
                    // return;
                }
            } else {
                edt_toolbar_retail.requestFocus();
                edt_toolbar_retail.selectAll();
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            }
        //}
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
            String strDis = Globals.Inv_Discount + "";

            if (strDis.equals("")) {
                strDis = "0";
            }
            objOrder = Orders.getOrders(getApplicationContext(), database, "  WHERE order_code = '" + strOrderCode + "'");
            objOrder = new Orders(getApplicationContext(), objOrder.get_order_id(), Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderCode, objOrder.get_order_date(), Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, objOrder.get_remarks(), objOrder.get_table_code(), "",null);
            long l = objOrder.updateOrders("order_code=? And order_id=?", new String[]{strOrderCode, objOrder.get_order_id()}, database);
            if (l > 0) {
                strFlag1 = "1";
                long e = Order_Detail.delete_order_detail(getApplicationContext(), "order_detail", "order_code =?", new String[]{strOrderCode}, database);

                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    Double cartsales_price= Double.parseDouble(mCart.get_Sales_Price());
                    Double discountedvalue= Globals.DiscountPer;
                    double finalDis =  cartsales_price * (discountedvalue / 100.0);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderCode,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0", String.valueOf(finalDis), mCart.get_Line_Total(), "0", mCart.getKitchenprintflag(),mCart.getUnitId(),mCart.getBeforeTaxPrice());
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
            String strDis = Globals.Inv_Discount + "";

            if (strDis.equals("")) {
                strDis = "0";
            }
            objOrder = new Orders(getApplicationContext(), null, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strContact_Code,
                    "0", Globals.TotalItem + "", Globals.TotalQty + "",
                    Globals.TotalItemPrice + "", "0", "0", Globals.TotalItemPrice + "", "",
                    "0", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, strRemarks, Globals.strTable_Code, "",null);
            long l = objOrder.insertOrders(database);
            if (l > 0) {
                strFlag1 = "1";
                for (int count = 0; count < myCart.size(); count++) {
                    ShoppingCart mCart = myCart.get(count);
                    Double cartsales_price= Double.parseDouble(mCart.get_Sales_Price());
                    Double discountedvalue= Globals.DiscountPer;
                    double finalDis =  cartsales_price * (discountedvalue / 100.0);
                    objOrderDetail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo,
                            "", mCart.get_Item_Code(), mCart.get_SRNO(), mCart.get_Cost_Price(), mCart.get_Sales_Price(), mCart.get_Tax_Price(),
                            mCart.get_Quantity(), "0",  String.valueOf(finalDis), mCart.get_Line_Total(), "0","false",mCart.getUnitId(),mCart.getBeforeTaxPrice());
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
            if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                /*Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                order.execute();*/
            }


            if(!opr.equals("Edit")) {
                strOrderCode = strOrderNo;
            }
            opr = "Add";
            Globals.Operation = opr;
            btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
            String itemPrice;
            itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
            btn_retail_2.setText(itemPrice);


            change_customer_icon();
            Toast.makeText(getApplicationContext(), R.string.Orders_Saved_Successfully, Toast.LENGTH_SHORT).show();
            if(!Globals.objsettings.getIs_KitchenPrint().equals("true")){
                Globals.setEmpty();
                try {
                    if (settings.get_Home_Layout().equals("0")) {
                        Intent intent = new Intent(RetailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(RetailActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                               /* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*/
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }


/*            if(opr.equals("Edit")){
                            if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
               // pDialog.dismiss();
               // listDialog2.dismiss();

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage(getString(R.string.waiting));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        printKOT(strOrderCode,progressDialog);

                    }
                };t.start();
             *//*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*//*



            }
            }*/
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
                if(Globals.objsettings.getIs_KitchenPrint().equals("true")){
                    pDialog.dismiss();
                    listDialog2.dismiss();

                    progressDialog = new ProgressDialog(RetailActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            printKOT(strOrderCode,progressDialog);
                            progressDialog.dismiss();
                            Globals.setEmpty();
                            try {
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                               /* btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                String itemPrice;
                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                btn_Item_Price.setText(itemPrice);*/
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }

                        }
                    };t.start();
             /*  PrintKOT_BackgroundAsyncTask order = new PrintKOT_BackgroundAsyncTask();
                order.execute();*/



                }
                else{
                    listDialog2.dismiss();
                }
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
      /*  try {
            appLocationService = new AppLocationService(
                    RetailActivity.this);
        } catch (Exception e) {
        }*/
        settings = Settings.getSettings(getApplicationContext(), database, "");
        txt_title = (TextView) findViewById(R.id.txt_title);
        edt_toolbar_retail = (AutoCompleteTextView) findViewById(R.id.edt_toolbar_retail);
        edt_toolbar_search = (EditText) findViewById(R.id.edt_toolbar_search);
        btn_retail_1 = (Button) findViewById(R.id.btn_retail_1);
        btn_retail_2 = (Button) findViewById(R.id.btn_retail_2);
        try {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.format(d);


           /* if (appLocationService.canGetLocation()) {


                double longitude = appLocationService.getLongitude();
                double latitude = appLocationService.getLatitude();
                Globals.latitude = String.valueOf(latitude);
                Globals.longitude = String.valueOf(longitude);
                getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

            } else {
                if (Globals.gpsFlag == true) {
                    appLocationService.showSettingsAlert();
                }
            }
            try {
                backgroundLocationJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
        catch(Exception e){}


        change_customer_icon();
       /* try {
            Intent serviceIntent = new Intent(this, BackgroundApiService.class);
            startService(serviceIntent);
        } catch (Exception e) {
        }*/

        try {
            if(mScannerView!=null) {
                mScannerView = new ZBarScannerView(RetailActivity.this);
                mScannerView.setResultHandler(RetailActivity.this);
            }
            else if(mScannerView == null)
            {
                mScannerView = new ZBarScannerView(this);
                // setContentView(mScannerView);
            }
        }
        catch(Exception e){

        }try {
            scalesetup = ScaleSetup.getScaleSetup(getApplicationContext(), database, db, "");
            if (scalesetup == null) {
                barcodelength = "0";
                plvaluelen = "0";
            } else {
                barcodelength = scalesetup.getPLU_BARCODE_LEN();
                plvaluelen = scalesetup.getPlu_len();
            }
        }catch(Exception e){

        }
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
        javaEncryption = new JavaEncryption();

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
        Globals.objsettings=settings;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode

        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        if(settings!=null){
            Globals.PrinterType = Globals.objsettings.getPrinterId();
            Globals.strIsBarcodePrint = Globals.objsettings.get_Is_BarcodePrint();
            Globals.strIsDenominationPrint = Globals.objsettings.get_Is_Denomination();
            Globals.strIsDiscountPrint = Globals.objsettings.get_Is_Discount();
            Globals.GSTNo = Globals.objsettings.get_Gst_No();
            Globals.GSTLbl = Globals.objsettings.get_GST_Label();
            Globals.PrintOrder = Globals.objsettings.get_Print_Order();
            Globals.PrintCashier = Globals.objsettings.get_Print_Cashier();
            Globals.PrintInvNo = Globals.objsettings.get_Print_InvNo();
            Globals.PrintInvDate = Globals.objsettings.get_Print_InvDate();
            Globals.PrintDeviceID = Globals.objsettings.get_Print_DeviceID();
        }

        final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE order_code = '" + strOrderCode + "'",database);
        if(order_detail1!=null){
            strKitchenFlag= order_detail1.getIs_KitchenPrintFlag();
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
                              //  sleep(1000);
                                Intent intent = new Intent(RetailActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }
                        } else {
                            try {
                              //  sleep(1000);
                                Intent intent = new Intent(RetailActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("opr", opr);
                                intent.putExtra("order_code", strOrderCode);
                                startActivity(intent);
                                //pDialog.dismiss();
                                //finish();
                            } catch (Exception e) {
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
                                                sleep(100);

                                                pDialog.dismiss();
                                                Globals.Order_Code = "";
                                                Globals.Operation = "";
                                                Globals.CMDItemPrice = 0;
                                                Globals.CMDItemName = "";
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
                                                            progressDialog = new ProgressDialog(RetailActivity.this);
                                                            progressDialog.setTitle("");
                                                            progressDialog.setMessage(getString(R.string.waiting));
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.show();
                                                            Thread t = new Thread(){
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        printKOT(strOrderCode, progressDialog);
                                                                        progressDialog.dismiss();
                                                                        Globals.setEmpty();
                                                                        try {
                                                                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                                                            startActivity(i);
                                                                              /*  btn_Qty.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                                                                                String itemPrice;
                                                                                itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                                                                                btn_Item_Price.setText(itemPrice);*/
                                                                        }
                                                                        catch(Exception e){
                                                                            System.out.println(e.getMessage());
                                                                        }

                                                                    }
                                                                    catch(Exception e){
                                                                        System.out.println(e.getMessage());
                                                                    }

                                                                }
                                                            };t.start();
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
                            case R.id.action_contact:
                                showdialogContact();
                                break;
                            case R.id.action_whatsapphelp:
                                openWhatsApp();
                                break;
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
                String strFilter = edt_toolbar_retail.getText().toString().trim();
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
                                                        edt_toolbar_search.requestFocus();
                                                        String strValue = edt_toolbar_search.getText().toString().trim();
                                                        int barcodelen=Integer.parseInt(barcodelength);
                                                        if(strValue.length()==barcodelen){

                                                            String pllen=strValue.substring(0,Integer.parseInt(scalesetup.getPlu_len()));
                                                          String plengthfinal=strValue.substring(pllen.length());
                                                            if(pllen.equals(scalesetup.getPlu_value())){

                                                                String itemlen= plengthfinal.substring(0,Integer.parseInt(scalesetup.getITEM_CODE_LEN()));
                                                                String itemcodefinal= plengthfinal.substring(itemlen.length());

                                                                 weightlength= itemcodefinal.substring(0,Integer.parseInt(scalesetup.getWp_LEN()));
                                                                String wightlenfinal= weightlength.substring(weightlength.length());

                                                                /************* Scale Integration Functon *********************/
                                                               try {
                                                                   getScaleBarcodefunc(strValue, weightlength);
                                                               }
                                                               catch(Exception e){}
                                                            }
                                                        }
                                                        else {
                                                            if (edt_toolbar_search.getText().toString().equals("\n") || edt_toolbar_search.getText().toString().equals("")) {
                                                                Toast.makeText(getApplicationContext(), "field vaccant", Toast.LENGTH_SHORT).show();
                                                                edt_toolbar_search.requestFocus();
                                                            } else {
                                                                String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
                                                                arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
                                                                if (arrayList.size() >= 1) {
                                                                    Item resultp = arrayList.get(0);
                                                                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                                                                    ck_project_type = lite_pos_registration.getproject_id();

                                                                    if (settings.get_Is_Stock_Manager().equals("false")) {

                                                                        String item_code = resultp.get_item_code();

                                                                        item_group_code = resultp.get_item_code();
                                                                        Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                                                                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

                                                                        ArrayList<ShoppingCart> myCart = Globals.cart;
                                                                        int count = 0;
                                                                        boolean bFound = false;
                                                                        Double spricewdtax=0d;
                                                                        Double sprice=0d;
                                                                        while (count < myCart.size()) {
                                                                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                                                                bFound = true;
                                                                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                                                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");

                                                                                if(resultp.get_is_inclusive_tax().equals("0")) {
                                                                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                                                                }
                                                                                else{
                                                                                    spricewdtax= Double.parseDouble(sale_priceStr)- Double.parseDouble(myCart.get(count).get_Tax_Price());
                                                                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * spricewdtax) + Double.parseDouble(myCart.get(count).get_Tax_Price());

                                                                                }
                                                                              // Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
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
                                                                            double beforeTaxPrice =  0;
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
                                                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                                                sprice = spricewdtax + iTaxTotal;

                                                                            } else {
                                                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                            }
                                                                            sale_priceStr=String.valueOf(sprice);
                                                                          //  sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                                                            Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                                                                            ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr))+ "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforeTaxPrice+"");
//                                                                        Globals.cart.add(cartItem);
                                                                            Globals.cart.add(0, cartItem);
                                                                            receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                                                                            if (receipemodifierlist.size() > 0) {

                                                                                Globals.SRNO = Globals.SRNO;
                                                                            } else {
                                                                                Globals.SRNO = Globals.SRNO + 1;
                                                                            }
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
                                                                        edt_toolbar_search.requestFocus();
                                                                        edt_toolbar_search.setText("");
                                                                    }
                                                                    //else

                                                                        /*{
                                                                        // Item resultp = arrayList.get(0);
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
                                                                                Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");

                                                                                ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id());
//                                                                            Globals.cart.add(cartItem);
                                                                                Globals.cart.add(0, cartItem);
                                                                                receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                                                                                if (receipemodifierlist.size() > 0) {

                                                                                    Globals.SRNO = Globals.SRNO;
                                                                                } else {
                                                                                    Globals.SRNO = Globals.SRNO + 1;
                                                                                }
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
                                                                        edt_toolbar_search.requestFocus();
                                                                        edt_toolbar_search.setText("");
                                                                    }*/
                                                                    String itemcode = resultp.get_item_code();

                                                                    receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
                                                                    if (receipemodifierlist.size() > 0) {
                                                                        Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                                                                        i.putExtra("itemcode", resultp.get_item_code());
                                                                        i.putExtra("opr", Globals.Operation);
                                                                        i.putExtra("srno", Globals.SRNO);
                                                                        i.putExtra("odr_code", Globals.Order_Code);
                                                                        startActivity(i);
                                                                        // return;
                                                                    }
                                                                } else {
                                                                    edt_toolbar_search.selectAll();
                                                                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }


                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (edt_toolbar_search != null) {
                                                                    edt_toolbar_search.requestFocus();
                                                                }
                                                            }
                                                        }, 10);
                                                        return true;
                                                    }
                                                    return false;
                                                }
                                            }
        );
    }
    private void printKOT(String strOrderNo,ProgressDialog pdialog) {
        boolean flag = false;
        getIP();
        try {


            for (int i = 0; i < ipAdd.size(); i++) {
                ip = ipAdd.get(i).toString();

                try {
                    final Orders orders = Orders.getOrders(RetailActivity.this, database, "WHERE order_code = '" +strOrderNo + "'");
                    final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(RetailActivity.this, "WHERE order_code = '" + strOrderNo + "'",database);

                    performOperationEn(ipAdd.get(i), 0, "Order",orders,order_detail1,pdialog);
                    performOperationEn(ipAdd.get(i), 0, "Void",orders,order_detail1,pdialog);

                } catch (Exception ex) {
                    //   GlobleVar.isSendOnline = true;
                    ex.getStackTrace();
                    //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
                //String last = ipAdd.get(ipAdd.size() - 1);


            }


/******************************** Master Print ***************************/

  /*          try {
                String result = getMasterPrint_from_server();
                JSONObject jsonObject = new JSONObject(result);

                String message = jsonObject.getString("Message");
                String printername = jsonObject.getString("PrinterName");


                if (message.equals("Success")) {
                    // Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    performOperationEn(printername, 1, "Order");
                    performOperationEn(printername, 1, "Void");
                } else {
                    Toast.makeText(getApplicationContext(), "Master Print json" + result, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }*/
            // pDialog.dismiss();


        } catch (Exception e) {
            //  GlobleVar.isSendOnline = true;
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean CheckprinterConnection(String ipp) {


        if (!ipp.equals("")) {
            String ipAddress = "";
            String tmpPort = "";
            int port = 9100;
            String[] strings = StringSplit(ipp, ":");
            ipAddress = strings[0];
            tmpPort = strings[1];
            port = Integer.parseInt(tmpPort);

            if (!WifiPrintDriver.WIFISocket(ipAddress, port)) {
                WifiPrintDriver.Close();
                return false;
            } else {
                if (WifiPrintDriver.IsNoConnection()) {
                    return false;
                }
                return true;
            }

        } else {
            return false;
        }
    }

    private void getIP() {
        try {
            ipAdd.clear();

            String sqlQuery=  "Select Distinct(categoryIp ) from item_group where item_group_code IN (Select item_group_code from item where item_code IN (Select item_code from order_detail Where order_code='"+strOrderCode+"'))";

            Cursor cursor = database.rawQuery(sqlQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    String categoryIps = cursor.getString(0);
                    ipAdd.add(categoryIps);

                    ///arrayListTable.add(new Table(this, null, tablecode, tablename, zone_id, zone_name, "", "", "",""));
                } while (cursor.moveToNext());

            }

            cursor.close();

            // Old Code

     /*       itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            if (itemgroup_catArrayList.size() > 0) {
                for (int i = 0; i < itemgroup_catArrayList.size(); i++) {

                   // if(itemgroup_catArrayList.get(i).getCategoryIp().equals(ip)) {
                        ipAdd.add(itemgroup_catArrayList.get(i).getCategoryIp());
                    //}
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void performOperationEn(String ip, int iOperation, String strMode,Orders orders,Order_Detail order_detail,ProgressDialog pDialog) {
        try {
            mylist.clear();

            try {
                getItemCategoryForPrint(ip, iOperation, strMode);
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            if (catId.size() > 0) {
                try {
                    mylist = getlistTest(iOperation, strMode, ip,orders);
                } catch (Exception e) {

                    System.out.println(e.getMessage());
                }
                Globals.AppLogWrite(mylist);
                if (mylist.size() > 0) {
                    try {


                        ArrayList<String> lPrintLog1 = new ArrayList<>();
                        lPrintLog1.add("Operation :" + iOperation);
                        lPrintLog1.add("Mode :" + strMode);
                        lPrintLog1.add("Current Printer :" + ip);
                        lPrintLog1.add("Total Item   :" + Globals.cart.size() + "");
                       /* for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                            lPrintLog1.add("Count   :" + (lCount + 1) + "");
                            ShoppingCart cartitem = Globals.cart.get(lCount);
                            try {
                                lPrintLog1.add("Item IP :  " + cartitem.getCategoryIp().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item IP :  " + ex.getMessage().toString());
                            }

                 *//*           try {
                                lPrintLog1.add("Item Print Flag :  " + cartitem.getPrintFlag().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Print Falg :  " + ex.getMessage().toString());
                            }
                            try {
                                lPrintLog1.add("Item New :  " + cartitem.isNew() + "");
                            } catch (Exception ex) {
                                lPrintLog1.add("Item New Falg :  " + ex.getMessage().toString());
                            }*//*
                            try {
                                lPrintLog1.add("Item  :  " + cartitem.get_Item_Name().toString());
                            } catch (Exception ex) {
                                lPrintLog1.add("Item Name :  " + ex.getMessage().toString());
                            }
                        }
*/
         /*               if (iOperation == 0 && strMode.equals("Order")) {
                            for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                ShoppingCart cartItem = Globals.cart.get(lCount);
                                try {
                                    if (cartItem.getCategoryIp().equals(ip)) {
                                        if (order_detail.getIs_KitchenPrintFlag().equals("false")) {
                                           // lPrintLog.add(cartItem.get_Item_Name());
                                            order_detail.setIs_KitchenPrintFlag("true");
                                        }
                                    }
                                } catch (Exception e) {

                                }


                            }
                        }*/
                        Globals.AppLogWrite(lPrintLog1);


                        if (CheckprinterConnection(ip.trim())) {
                            try {
                                String bill = "";
                                for (String data : mylist) {
                                    bill = bill + data;
                                }
                                for (int k = 1; k <= Integer.parseInt(Globals.objsettings.get_No_Of_Print()); k++) {
                                    WifiPrintDriver.Begin();
                                    WifiPrintDriver.ImportData(bill);
                                    WifiPrintDriver.ImportData("\r");
                                    WifiPrintDriver.excute();
                                    WifiPrintDriver.ClearData();
                                    String str = "\r\n\r\n\r\n\r\n";
                                    byte[] feed = str.getBytes();
                                    WifiPrintDriver.SPPWrite(feed);
                                    byte[] paramString1 = new byte[]{27, 109, 2};
                                    WifiPrintDriver.SPPWrite(paramString1);
                                    WifiPrintDriver.excute();
                                    WifiPrintDriver.ClearData();

                                    ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add(ip);
                                    lPrintLog.add(" Printing time ip ------------------");

                                    if (iOperation == 0 && strMode.equals("Order")) {
                                        for (int lCount = 0; lCount < Globals.cart.size(); lCount++) {
                                            ShoppingCart cartItem = Globals.cart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getKitchenprintflag().equals("false")) {
                                                        lPrintLog.add(cartItem.get_Item_Name());
                                                        String sql= "UPDATE order_detail SET is_KitchenPrintFlag = 'true' where item_code= '"+cartItem.get_Item_Code()+"' and order_code='"+strOrderCode+"'";
                                                        db.executeDML(sql,database);
                                                        //  order_detail.setIs_KitchenPrintFlag("true");
                                                    }
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                        // pDialog.dismiss();
                                      /*  Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    else  if (iOperation == 0 && strMode.equals("Void")) {
                                        for (int lCount = 0; lCount < Globals.voidcart.size(); lCount++) {
                                            VoidShoppingCart cartItem = Globals.voidcart.get(lCount);
                                            try {
                                                if (cartItem.getCategoryIp().equals(ip)) {
                                                    if (cartItem.getVoidkitchenflag().equals("false")) {
                                                        String sql = "UPDATE Void SET print_flag = 'true' where item_code= '" + cartItem.get_Item_Code() + "' and order_no='" + strOrderCode + "'";
                                                        db.executeDML(sql, database);
                                                    }
                                                    //  order_detail.setIs_KitchenPrintFlag("true");
                                                }
                                            } catch (Exception e) {

                                            }


                                        }

                                        // pDialog.dismiss();
                                   /*     Globals.setEmpty1();
                                        Globals.strContact_Code = "";
                                        Globals.strResvContact_Code = "";*/
                                    }
                                    Globals.AppLogWrite(lPrintLog);


                                }


                                // Globals.isSendOnline = true;
                            } catch (Exception e) {
                                //GlobleVar.isSendOnline = true;

                            }

                        }


                    } catch (Exception ex) {
                        //  GlobleVar.isSendOnline = true;
                        Toast.makeText(getApplicationContext(), "Please Check Printer Connection", Toast.LENGTH_SHORT).show();
                    }

                    // pDialog.dismiss();
                    //  Globals.setEmpty();

                } else {
                    // GlobleVar.isSendOnline = true;
                }


            } else {

                //GlobleVar.isSendOnline = true;
            }
        } catch (Exception ex) {
            // GlobleVar.isSendOnline = true;
            ex.getStackTrace();
        }

    }

    private void getItemCategoryForPrint(String ip, int iOperation, String strMoString) {
        catId.clear();
        final ArrayList<ShoppingCart> myCart = Globals.cart;
        if (strMoString.equals("Order")) {
            if (iOperation == 0) {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0' and categoryIp!='' Group BY [categoryIp]", database, db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), "WHERE is_active ='1' and parent_code = '0'", database, db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < myCart.size(); i++) {
                    ShoppingCart cartitem = myCart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");


                    if (cartitem.getIs_modifier().equals("1")) {
                        if (dtl_modifier != null) {
                            Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'",database);

                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                        if (!catId.contains(item.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(item.get_item_group_code());
                                        }
                                    } /*else {
                                        if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                            if (!catId.contains(categoryitem.get_item_group_code())) {
                                                catId.add(categoryitem.get_item_group_code());
                                            }
                                        }
                                    }*/

                                }

                            }
                        }
                    } else {
                        Order_Detail categoryitem = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + myCart.get(i).get_Item_Code() + "'",database);
                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + categoryitem.get_item_code()+ "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                if (!categoryitem.getIs_KitchenPrintFlag().equals("true")) {
                                    if (!catId.contains(item.get_item_group_code())&& cartitem.getCategoryIp().equals(ipAdd.get(a))){
                                        catId.add(item.get_item_group_code());
                                    }
                                }
                            } /*else {
                                if (!categoryitem.getIsmaster_PrintFlag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code())) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                            }*/
                        }

                    }
                }
            }


        } else {
            if (iOperation == 0) {


                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where categoryIp = '" + ip.trim() + "' and (is_active ='1' and parent_code = '0')", database,db);
            } else {
                itemgroup_catArrayList = Item_Group.getAllItem_Group(getApplicationContext(), " where (Active='1' or Active='1')", database,db);
            }

            for (int a = 0; a < ipAdd.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database,db," where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getItem_code() + "'",database);

                            Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);
                            if (categoryitem != null) {
                                if (iOperation == 0) {
                                    if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }
                                } else {
                                    if (!cartitem.getVoidkitchenflag().equals("true")) {
                                        if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                            catId.add(categoryitem.get_item_group_code());
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        //  Order_Detail categoryitemorder = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database,db);

                        if (categoryitem != null) {
                            if (iOperation == 0) {
                                // if (!item.getPrintFlag().equals("true")) {
                                if (!cartitem.getVoidkitchenflag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                                // }
                            } else {
                                if (!cartitem.getVoidkitchenflag().equals("true")) {
                                    if (!catId.contains(categoryitem.get_item_group_code()) && cartitem.getCategoryIp().equals(ipAdd.get(a))) {
                                        catId.add(categoryitem.get_item_group_code());
                                    }
                                }
                            }

                        }

                    }
                }
            }



        }

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

    private ArrayList<String> getlistTest(int iOperation, String strMode, String Ip,Orders orders) {
        order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id='" + orders.get_order_type_id() + "'", database, db);

        if (strMode.equals("Order")) {


            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name() );

            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.cart.size(); i++) {


                    ShoppingCart cartitem = Globals.cart.get(i);
                    ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");

                    if (cartitem.getIs_modifier().equals("1")) {


                        if (dtl_modifier != null) {
                            //Item categoryitem = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + itemgroup_catArrayList.get(a).get_item_group_code()+ "'", database,db);
                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);

                            //     Item aetSet_pos_menu_item = Item.getItem(getApplicationContext(), " where item_code = '" + dtl_modifier.getItem_code() + "' and item_group_code = '" + catId.get(a) + "'", database,db);
                            if (aetSet_pos_menu_item != null) {
                                if (iOperation == 0) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                        //item.setPrintFlag("true");

                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name + "(M)";
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name + " (M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'", database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + aetSet_pos_menu_item.get_item_code() + "'", database, db);
                        if (aetSet_pos_menu_item != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                    } else {
                                   /* ArrayList<String> lPrintLog1 = new ArrayList<>();
                                    lPrintLog1.add(ip);
                                    lPrintLog1.add("-------- get item operation 0");
                                    GlobleVar.AppLogWrite(lPrintLog1);*/
                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    //cartitem.setCategoryIp(Ip);

                               /*     ArrayList<String> lPrintLog = new ArrayList<>();
                                    lPrintLog.add("-------- get item 2");
                                    lPrintLog.add(Ip);
                                    GlobleVar.AppLogWrite(lPrintLog);*/
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            } else {
                               /* ArrayList<String> lPrintLog = new ArrayList<>();
                                lPrintLog.add("-------- get item operation 1");
                                lPrintLog.add(Ip);
                                GlobleVar.AppLogWrite(lPrintLog);*/

                                // working code
//                                if (cartitem.getIsmaster_PrintFlag().equals("true")) {
//                                } else {
//                                    qty = cartitem.get_Quantity() + "";
//                                    //item.setCatIp(Ip);
//                                    name = cartitem.get_Item_Name();
//                                    if (name == null) {
//                                    } else {
//                                        bFinalCheck = false;
//                                        bCheck = false;
//                                        raw = qty + "   " + name;
//                                        mylist.add("\n" + raw);
//                                    }
//
//                                }
                            }

                        }
                    }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("Order"+ mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;
        }
        else
        {
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("------------------------------------------------\n");
            int ln = "Void KOT".length();
            int rem = 48 - ln;
            int part = rem / 2;
            String tt1 = "";
            for (int k = 0; k < part; k++) {
                tt1 = tt1 + " ";
            }
            tt1 = tt1 + "Void KOT";
            for (int j = 0; j < part; j++) {
                tt1 = tt1 + " ";
            }
            mylist.add(tt1);
            mylist.add("\n------------------------------------------------");
            mylist.add("\nDate&Time :" + new DateUtill().currentDate());
            mylist.add("\nPOS       :" + orders.get_device_code());
            mylist.add("\nOrder No. :" + orders.get_order_code());
            mylist.add("\nOrder Type     :" + order_type.get_name());


            mylist.add("\n------------------------------------------------\n");
            mylist.add("Qty  * Item                             " + "\n");
            mylist.add("------------------------------------------------");
            String qty = "", name = "", raw;
            boolean bCheck = true;
            boolean bFinalCheck = true;
            for (int a = 0; a < catId.size(); a++) {
                for (int i = 0; i < Globals.voidcart.size(); i++) {
                    VoidShoppingCart cartitem = Globals.voidcart.get(i);


                    if (cartitem.getIs_modifier().equals("1")) {
                        ReceipeModifier dtl_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db, " where modifier_code='" + cartitem.get_Item_Code() + "'");
                        if (dtl_modifier != null) {

                            Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + dtl_modifier.getModifier_code() + "'", database);
                            Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code()+ "'", database, db);
                            if (cartitem != null) {
                                if (iOperation == 0) {
                                    if (strMode.equals("Order")) {
                                        if (aetSet_pos_menu_item.getIs_KitchenPrintFlag().equals("true")) {
                                        } else {
//                                        item.setPrintFlag("true");
                                            qty = cartitem.get_Quantity() + "";

                                            name = cartitem.get_Item_Name();
                                            if (name == null) {
                                            } else {
                                                if (ip.equals(cartitem.getCategoryIp())) {
                                                    if (catId.get(a).equals(item.get_item_group_code())) {
                                                        bFinalCheck = false;
                                                        bCheck = false;
                                                        raw = qty + "   " + name + " (M)";
                                                        mylist.add("\n" + raw);
                                                    }}
                                            }
                                        }
                                    } else {
                                        qty = cartitem.get_Quantity() + "";

                                        name = item.get_item_name();
                                        if (name == null) {
                                        } else {
                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name + " (M)";
                                                    mylist.add("\n" + raw);
                                                }}
                                        }
                                    }
                                } else {
                                    // item.setPrintFlag("true");
                                    qty = cartitem.get_Quantity() + "";

                                    name = cartitem.get_Item_Name();
                                    ;
                                    if (name == null) {
                                    } else {
                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name + "(M)";
                                                mylist.add("\n" + raw);
                                            }
                                        }}
                                }

                            }
                        }
                    } else {
                        //  Order_Detail aetSet_pos_menu_item = Order_Detail.getOrder_Detail(getApplicationContext(), "WHERE item_code = '" + cartitem.get_Item_Code() + "'",database);

                        Item item = Item.getItem(getApplicationContext(), " where item_code = '" + cartitem.get_Item_Code() + "'", database, db);
                        if (cartitem != null) {
                            if (iOperation == 0) {
                                if (strMode.equals("Order")) {
                                    if (cartitem.getVoidkitchenflag().equals("true")) {
                                    } else {
                                        //  item.setPrintFlag("true");
                                        qty = cartitem.get_Quantity() + "";
                                        // cartitem.setCategoryIp(Ip);
                                        name = cartitem.get_Item_Name();
                                        if (name == null) {
                                        } else {

                                            if (ip.equals(cartitem.getCategoryIp())) {
                                                if (catId.get(a).equals(item.get_item_group_code())) {
                                                    bFinalCheck = false;
                                                    bCheck = false;
                                                    raw = qty + "   " + name;
                                                    mylist.add("\n" + raw);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    qty = cartitem.get_Quantity() + "";
                                    //cartitem.setCategoryIp(Ip);
                                    name = cartitem.get_Item_Name();
                                    if (name == null) {
                                    } else {

                                        if (ip.equals(cartitem.getCategoryIp())) {
                                            if (catId.get(a).equals(item.get_item_group_code())) {
                                                bFinalCheck = false;
                                                bCheck = false;
                                                raw = qty + "   " + name;
                                                mylist.add("\n" + raw);
                                            }
                                        }
                                    }
                                }

                            } else {

                                //   item.setPrintFlag("true");
                                qty = cartitem.get_Quantity() + "";
                                //cartitem.setCategoryIp(Ip);
                                name = cartitem.get_Item_Name();
                                if (name == null) {
                                } else {

                                    if (ip.equals(cartitem.getCategoryIp())) {
                                        if (catId.get(a).equals(item.get_item_group_code())) {
                                            bFinalCheck = false;
                                            bCheck = false;
                                            raw = qty + "   " + name;
                                            mylist.add("\n" + raw);
                                        }
                                    }
                                }

                            }

                        }
                    }

                }
            }
            mylist.add("\n");
            Globals.AppLogWrite("VOID"+mylist);
            if (bFinalCheck) {
                mylist.clear();
            }

            return mylist;

        }
//return  mylist;
    }
    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    //Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        Globals.locationddress = result;
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    Globals.locationddress = locationAddress;
                    break;
                default:
                    locationAddress = null;
            }

            // Toast.makeText(getApplicationContext(), locationAddress.toString(), Toast.LENGTH_LONG).show();
            //  tvAddress.setText(locationAddress);
        }
    }

    public void backgroundLocationJson() throws JSONException {
        JSONArray jsonArr = new JSONArray();

        final JSONObject jsonObj1 = new JSONObject();
        try {

            JSONObject jsonObj = new JSONObject();
            // Toast.makeText(getApplicationContext(), "size"+export.size(), Toast.LENGTH_SHORT).show();
            jsonObj.put("latitude", Globals.latitude);
            jsonObj.put("longitude", Globals.longitude);
            jsonObj.put("address", Globals.locationddress);
            jsonObj.put("datetime", date);

            jsonArr.put(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        jsonObj1.put("result", jsonArr);
        Globals.jsonArray_background = jsonObj1;


    }
    public void openWhatsApp(){
        try {
            String text = "Hello Sir.\n I am Using Trigger POS Application Version "+ versionname+"\n\n Company Name : "+ Globals.objLPR.getCompany_Name()+"\n User name: "+ Globals.objLPR.getContact_Person()+"\n Reg. Code :"+ Globals.objLPR.getRegistration_Code()+"\n License Mode :"+ Globals.objLPR.getproject_id()+"\n Expiry Date :"+ javaEncryption.decrypt(Globals.objLPD.getExpiry_Date(), "12345678") +"\n";
            ;
            String toNumber="";
            //India
            if(Globals.objLPR.getCountry_Id().equals("99")) {

                toNumber = "919530047775"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            }
            // Kuwait
            else if(Globals.objLPR.getCountry_Id().equals("114")){
                toNumber = "96569029773";
            }
            // UAE
            else if(Globals.objLPR.getCountry_Id().equals("221")){
                toNumber = "971558838749";
            }

            else {
                toNumber = "919530047775";
            }


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getScaleBarcodefunc(String strValue,String weight){
        String strWhere = "Where item_code = '" + strValue + "' or item_name ='" + strValue + "' or barcode= '" + strValue + "' or sku = '" + strValue + "'";
        arrayList = Item.getAllItem(getApplicationContext(), strWhere, database);
        if (arrayList.size() >= 1) {
            Item resultp = arrayList.get(0);
            lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
            ck_project_type = lite_pos_registration.getproject_id();

            if (settings.get_Is_Stock_Manager().equals("false")) {

                String item_code = resultp.get_item_code();

                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

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
                   // ArrayList<String> item_group_taxArrayList = calculateTax();
                    Double iTax = 0d;
                    Double iTaxTotal = 0d;
                    Double sprice=0d;
                    if(item_group_taxArrayList.size()>0) {
                        if(Globals.objLPR.getCountry_Id().equals("99")) {
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                iTax = 0d;
                                                //String tax_id = item_group_taxArrayList.get(i);
                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                                }
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                        iTax = 0d;
                                        //String tax_id = item_group_taxArrayList.get(i);
                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if (tax_master != null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                        }
                                    }
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                            }
                        }
                        else if(Globals.objLPR.getCountry_Id().equals("114")){
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;

                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                    iTax = 0d;
                                    //String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                    if(tax_master!=null) {
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                    }
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                            }
                        }
                        else if(Globals.objLPR.getCountry_Id().equals("221")){
                            if (contact != null) {
                                if (contact.getIs_taxable().equals("1")) {
                                    if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            iTax = 0d;
                                            //String tax_id = item_group_taxArrayList.get(i);
                                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                            }
                                        }
                                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                                    }
                                } else {
                                    sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                                }
                            } else if (contact == null) {
                                for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                    iTax = 0d;
                                    //String tax_id = item_group_taxArrayList.get(i);
                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                    if(tax_master!=null) {
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                        Globals.AppLogWrite("retail tax" + Globals.order_item_tax);
                                    }
                                }
                                sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;

                            }
                        }
                    }
                    else{
                        sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC());
                    }

                    Double beforetaxprice= sprice-iTaxTotal;
                    Dweightvalue=Double.parseDouble(weight)/1000;
                    String qty= Globals.myNumberFormat2QtyDecimal(Dweightvalue,qty_decimal_check);
                    Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");

                    ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), qty, cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * sprice) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");

                    Globals.cart.add(0, cartItem);
                    receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                    if (receipemodifierlist.size() > 0) {

                        Globals.SRNO = Globals.SRNO;
                    } else {
                        Globals.SRNO = Globals.SRNO + 1;
                    }
                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                    Globals.TotalItem = Globals.TotalItem + 1;
                    Globals.TotalQty = Globals.TotalQty+Double.parseDouble(qty);
                }
                Globals.cart = myCart;
                retail_list_load();
                String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                btn_retail_2.setText(item_price);
                btn_retail_1.setText(Globals.myNumberFormat2QtyDecimal(Globals.TotalQty, qty_decimal_check));
                edt_toolbar_search.setText("");
            } else {
                // Item resultp = arrayList.get(0);
                String item_code = resultp.get_item_code();

                item_group_code = resultp.get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "Where item_code = '" + item_code + "'", database);
                item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "Where item_group_code = '" + item_group_code + "'", database, db);

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
                      //  ArrayList<String> item_group_taxArrayList = calculateTax();
                        Double iTax = 0d;
                        Double iTaxTotal = 0d;
                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                            iTax = 0d;
//                                                                        Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                           // String tax_id = item_group_taxArrayList.get(i);
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                            Double iPrice = Double.parseDouble(item_location.get_selling_price());

                            if (tax_master.get_tax_type().equals("P")) {
                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                            } else {
                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                            }
                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                            Order_Item_Tax order_item_tax = new Order_Item_Tax(getApplicationContext(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                            Globals.order_item_tax.add(order_item_tax);
                        }
                        Double sprice = (Double.parseDouble(sale_priceStr))*(Double.parseDouble(weight)*1)/Double.parseDouble(scalesetup.getWp_CALC()) + iTaxTotal;
                       Double beforetaxprice= sprice-iTaxTotal;
                        Dweightvalue=Double.parseDouble(weight)/1000;
                        String qty= Globals.myNumberFormat2QtyDecimal(Dweightvalue,qty_decimal_check);
                      //  Globals.TotalQty=Double.parseDouble(qty);
                        Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + resultp.get_item_code() + "'");

                        ShoppingCart cartItem = new ShoppingCart(getApplicationContext(), Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), qty, cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * sprice) + iTaxTotal + "", "0", "0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforetaxprice+"");
//                                                                            Globals.cart.add(cartItem);
                        Globals.cart.add(0, cartItem);
                        receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + resultp.get_item_code() + "'", database);

                        if (receipemodifierlist.size() > 0) {

                            Globals.SRNO = Globals.SRNO;
                        } else {
                            Globals.SRNO = Globals.SRNO + 1;
                        }
                        Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                        Globals.TotalItem = Globals.TotalItem + 1;
                        Globals.TotalQty = Globals.TotalQty +Double.parseDouble(qty);
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
            String itemcode = resultp.get_item_code();

            receipemodifierlist = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "Where item_code = '" + itemcode + "'", database);
            if (receipemodifierlist.size() > 0) {
                Intent i = new Intent(getApplicationContext(), ItemModifierSelection.class);
                i.putExtra("itemcode", resultp.get_item_code());
                i.putExtra("opr", Globals.Operation);
                i.putExtra("srno", Globals.SRNO);
                i.putExtra("odr_code", Globals.Order_Code);
                startActivity(i);
                // return;
            }
        } else {
            edt_toolbar_search.selectAll();
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }


}
