package org.phomellolitepos.TicketSolution;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.codec.Base64;

import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.PaymentActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.ReservationActivity;
import org.phomellolitepos.TicketingActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

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

import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.Util.Watermark;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Ticket_Setup;
import org.phomellolitepos.database.Ticket_Setup_Tax;
import org.phomellolitepos.database.User;

public class TicketPaymentActivity extends AppCompatActivity {
    TextView txt_name, txt_time, txt_price, txt_mobile, txt_totl_amt,txt_tax;
    Spinner spr_payment;
    Button btn_payment, btn_customer, btn_plus_qty, btn_minus_qty;
    String strName, strDate, strPrice,strId,strTax;
    String strSelectedCategory = "";
    ArrayList<Contact> contact_ArrayList;
    ArrayList<Payment> paymentArrayList;
    DialogContactMainListAdapter dialogContactMainListAdapter;
    Dialog listDialog1;
    Database db;
    SQLiteDatabase database;
    Settings settings;
    String strPayMethod, PayId, decimal_check, qty_decimal_check, strOrderNo, part2, modified_by, orderId, date;
    EditText count_qty;
    Lite_POS_Device lite_pos_device;
    ProgressDialog progressDialog;
    ImageView img_add;
    Lite_POS_Registration lite_pos_registration;
    ArrayList<String> list1a, list2a, list3a, list4a, list5a, list6a, list7a, list8a, list9a;
    Ticket_Setup ticket_setup;
    String depart,arriv;
    Double iPrice = 0d;
    Double iTax = 0d;
    ArrayList<Ticket_Setup_Tax> taxArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
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
                Thread timerThread = new Thread() {
                    public void run() {

                        try {
                            Intent intent = new Intent(TicketPaymentActivity.this, TicketListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } finally {
                        }

                    }
                };
                timerThread.start();

            }
        });

        try {
            decimal_check = lite_pos_device.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();
        list5a = new ArrayList<String>();
        list6a = new ArrayList<String>();
        list7a = new ArrayList<String>();
        list8a = new ArrayList<String>();
        list9a = new ArrayList<String>();

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = format.format(d);

        Intent intent = getIntent();
        strId = intent.getStringExtra("strID");

        ticket_setup = Ticket_Setup.getTicket_Setup(getApplicationContext(),database,db,"where id ='"+strId+"'");

        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_time = (TextView) findViewById(R.id.txt_time);
        txt_tax = (TextView) findViewById(R.id.txt_tax);
        txt_price = (TextView) findViewById(R.id.txt_price);
        txt_totl_amt = (TextView) findViewById(R.id.txt_totl_amt);
        txt_mobile = (TextView) findViewById(R.id.txt_mobile);
        spr_payment = (Spinner) findViewById(R.id.spr_payment);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        btn_customer = (Button) findViewById(R.id.btn_customer);
        count_qty = (EditText) findViewById(R.id.count_qty);
        btn_plus_qty = (Button) findViewById(R.id.btn_plus_qty);
        btn_minus_qty = (Button) findViewById(R.id.btn_minus_qty);
        img_add = (ImageView) findViewById(R.id.img_add);

        if (ticket_setup!=null){
            Item item = Item.getItem(getApplicationContext(),"where item_code ='"+ticket_setup.get_tck_from()+"'",database,db);
            try {
                depart = item.get_item_name();
            }catch (Exception ex){}
            item = Item.getItem(getApplicationContext(),"where item_code ='"+ticket_setup.get_tck_to()+"'",database,db);
            try {
                arriv = item.get_item_name();
            }catch (Exception ex){}
            txt_name.setText(depart+"-"+arriv);
            txt_time.setText(ticket_setup.get_departure());

            strPrice = Globals.myNumberFormat2Price(Double.parseDouble(ticket_setup.get_price()), decimal_check);
            Calculate_Tax(strPrice);
        }



        count_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Double qty = Double.parseDouble(count_qty.getText().toString());
                try {
                    if (qty > 0) {
                        Double FinalAmt = qty * Double.parseDouble(txt_price.getText().toString());
                        txt_totl_amt.setText(Globals.myNumberFormat2Price(FinalAmt, decimal_check));
                    }
                } catch (Exception ex) {
                }

            }
        });


        btn_plus_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double qty = Double.parseDouble(count_qty.getText().toString().trim()) + 1;
                count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));

            }
        });

        btn_minus_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double qty = Double.parseDouble(count_qty.getText().toString().trim()) - 1;
                if (qty <= 0) {
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
                } else {
                    count_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
                }
            }
        });

        spr_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Payment resultp = paymentArrayList.get(position);
                strPayMethod = resultp.get_payment_name();
                try {
                    PayId = resultp.get_payment_id();
                } catch (Exception ex) {
                    PayId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_customer_dialog(Globals.strContact_Code);
            }
        });

        btn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = Contact.getContact(getApplicationContext(), database, db, "WHERE is_active ='1'");
                if (contact == null) {
                    Toast.makeText(getApplicationContext(), R.string.No_Contact_Found, Toast.LENGTH_SHORT).show();
                } else {
                    showdialogContact();
                }
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(TicketPaymentActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.Wait_msg));
                progressDialog.show();
                new Thread() {
                    public void run() {
                        if (Globals.strResvContact_Code.equals("")) {
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Select Customer first", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            payment();
                        }

                    }
                }.start();

            }
        });

        fill_spinner_pay_method("");
        count_qty.setText("1");
    }

    private void Calculate_Tax(String strPri) {
        taxArrayList = Ticket_Setup_Tax.getAllTicket_Setup_Tax(getApplicationContext(),"WHERE ref_id = '" + strId + "'",database);

        if (taxArrayList.size() > 0) {
            for (int i = 0; i < taxArrayList.size(); i++) {

                Ticket_Setup_Tax ticket_setup_tax = taxArrayList.get(i);
                String tax_id = ticket_setup_tax.get_tax_id();

                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);

                iPrice = Double.parseDouble(strPri + "");

                Double itempTax = 0d;
                if (tax_master.get_tax_type().equals("P")) {
                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                    itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                } else {
                    iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), decimal_check));
                    itempTax = Double.parseDouble(tax_master.get_rate());
                }

                OrderTaxArray orderTaxArray = new OrderTaxArray(getApplicationContext(), "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                Globals.order_tax_array.add(orderTaxArray);

            }
        }
        strTax = Globals.myNumberFormat2Price(iTax, decimal_check);
        txt_tax.setText(strTax);

        Double NetAmount = (Double.parseDouble(strPri) + Double.parseDouble(txt_tax.getText().toString()));
        String net_amount;
        net_amount = Globals.myNumberFormat2Price(NetAmount, decimal_check);
        txt_price.setText(net_amount);
        txt_totl_amt.setText(net_amount);

    }

    private void payment() {
        String strFlag = "0";
        Order_Payment objOrderPayment;
        Order_Detail order_detail;
        String strOrdeeStatus = "CLOSE";
        modified_by = Globals.user;
        database.beginTransaction();
        Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
        Orders objOrder1 = Orders.getOrders(getApplicationContext(), database, "  order By order_id Desc LIMIT 1");

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

        String locCode;
        try {
            locCode = Globals.objLPD.getLocation_Code();
        } catch (Exception ex) {
            locCode = "";
        }

        Orders objOrder = new Orders(getApplicationContext(), null, Globals.Device_Code, locCode, Globals.strOrder_type_id, strOrderNo, date, Globals.strResvContact_Code,
                "0","1", count_qty.getText().toString(),
                txt_totl_amt.getText().toString(), "", "", txt_totl_amt.getText().toString(), txt_totl_amt.getText().toString(),
                "", "0", "0", "0", "1", modified_by, date, "N", strOrdeeStatus, "", Globals.strTable_Code, "");

        long l = objOrder.insertOrders(database);
        if (l > 0) {
            strFlag = "1";
        }

        order_detail = new Order_Detail(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1",strId,
                "1", ticket_setup.get_price(),ticket_setup.get_price(), "",count_qty.getText().toString(),"","",txt_totl_amt.getText().toString(),"");

        long op = order_detail.insertOrder_Detail(database);
        if (op > 0) {
            strFlag = "1";
        }

        objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", txt_totl_amt.getText().toString(),
                PayId, "", "", "", "", "", "");

        long op1 = objOrderPayment.insertOrder_Payment(database);
        if (op1 > 0) {
            strFlag = "1";
        }

        //long e1 = Order_Tax.delete_Order_Tax(getApplicationContext(), "order_tax", " order_code =? ", new String[]{strOrderNo}, database);

        ArrayList<OrderTaxArray> orderTaxArray = Globals.order_tax_array;
        Order_Tax objOrderTax;
        for (int count = 0; count < orderTaxArray.size(); count++) {
            OrderTaxArray Otary = orderTaxArray.get(count);

            objOrderTax = new Order_Tax(getApplicationContext(), null, strOrderNo, Otary.get_tax_id()
                    , Otary.get_tax_type(), Otary.get_rate(), Otary.get_value());

            long o = objOrderTax.insertOrder_Tax(database);
            if (o > 0) {
                strFlag = "1";
            }
        }



        if (strFlag.equals("1")) {
            database.setTransactionSuccessful();
            database.endTransaction();
            performPDFExport();
            runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    Globals.strResvContact_Code = "";
                    Globals.order_tax_array.clear();
                    Globals.From = "";
                    Globals.To = "";
                    Toast.makeText(getApplicationContext(), "Transaction successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TicketPaymentActivity.this, TicketingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            progressDialog.dismiss();
            database.endTransaction();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Transaction error", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void fill_spinner_pay_method(String s) {
        paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' and payment_id = '1'");
        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getApplicationContext(), paymentArrayList);
        spr_payment.setAdapter(paymentListAdapter);

        if (!s.equals("")) {
            for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                String iname = paymentArrayList.get(i).get_payment_name();
                if (s.equals(iname)) {
                    spr_payment.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showdialogContact() {
        final String strFiltr = "";
        listDialog1 = new Dialog(this);
        listDialog1.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog1.setContentView(v1);
        listDialog1.setCancelable(true);

        final ListView list11 = (ListView) listDialog1.findViewById(R.id.lv_custom_ortype);
        final Spinner spn_filter = (Spinner) listDialog1.findViewById(R.id.spn_filter);
        final TextView contact_title = (TextView) listDialog1.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog1.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog1.findViewById(R.id.srch_image);
        TextView txt_reset = (TextView) listDialog1.findViewById(R.id.txt_reset);
        ImageView img_brs = (ImageView) listDialog1.findViewById(R.id.img_brs);
        listDialog1.show();
        img_brs.setVisibility(View.GONE);
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog1.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        final ArrayList<Bussiness_Group> arrayBusinessGroup = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1' Order By name asc");
        HintAdapter<Bussiness_Group> hintAdapter = new HintAdapter<Bussiness_Group>(
                this,
                R.layout.item_manufacture_spinner_item,
                "Select Category",
                arrayBusinessGroup) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                Bussiness_Group resultp = arrayBusinessGroup.get(position);
                final String name = resultp.get_name();
                final String code = resultp.get_business_group_code();

                // Here you inflate the layout and set the value of your widgets
                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.manufacture_name)).setText(name);
                ((TextView) view.findViewById(R.id.manufacture_code)).setText(code);
                return view;
            }
        };


        HintSpinner<Bussiness_Group> hintSpinner = new HintSpinner<Bussiness_Group>(
                spn_filter,
                hintAdapter,
                new HintSpinner.Callback<Bussiness_Group>() {
                    @Override
                    public void onItemSelected(int position, Bussiness_Group itemAtPosition) {
                        Bussiness_Group resultp = arrayBusinessGroup.get(position);
                        strSelectedCategory = resultp.get_business_group_code();

                        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                    }
                });
        hintSpinner.init();

        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog1.dismiss();
                showdialogContact();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, "");
            }
        });

        srch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strFiltr = edt_srch_contct.getText().toString().trim();
                strFiltr = " and (name Like '%" + strFiltr + "%' OR email_1 Like '%" + strFiltr + "%'  OR contact_1 Like '%" + strFiltr + "%')";
                edt_srch_contct.selectAll();
                fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);

            }
        });

    }

    private void fill_dialog_contact_List(TextView contact_title, ListView list11, String str, final String strFilter) {
        String groupCode;
        if (str.equals("")) {
            groupCode = "";
        } else {
            groupCode = "where business_group_code='" + str + "'";
        }

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogContactMainListAdapter = new DialogContactMainListAdapter(TicketPaymentActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogContactMainListAdapter);
        list11.setTextFilterEnabled(true);
    }

    public void setCustomer(String name, String contact_1) {
        btn_customer.setText(name);
        txt_mobile.setText(contact_1);
    }


    private void create_customer_dialog(String strContact_Code) {

        final String str = "13245";
        final Dialog listDialog2 = new Dialog(TicketPaymentActivity.this);
        listDialog2.setTitle(R.string.Contact);
        LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.payment_customer_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(true);
        final EditText edt_name = (EditText) listDialog2.findViewById(R.id.edt_name);
        final EditText edt_mobile_dialog = (EditText) listDialog2.findViewById(R.id.edt_mobile_dialog);
        final EditText edt_email = (EditText) listDialog2.findViewById(R.id.edt_email);
        Button btn_save = (Button) listDialog2.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) listDialog2.findViewById(R.id.btn_cancel);
        listDialog2.show();
        Window window = listDialog2.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        try {
            Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strResvContact_Code + "'");
            if (contact == null) {
                edt_name.setText("");
                edt_mobile_dialog.setText(txt_mobile.getText().toString().trim());
                edt_email.setText("");
            } else {
                edt_name.setText(contact.get_name());
                edt_mobile_dialog.setText(contact.get_contact_1());
                edt_email.setText(contact.get_email_1());
            }

        } catch (Exception ex) {

        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCTCode = "";
                String chk_mobile = edt_mobile_dialog.getText().toString().trim();
                if (chk_mobile.equals("")) {
                    edt_mobile_dialog.setError(getString(R.string.Mobile_is_required));
                    return;
                }

                if (edt_email.getText().toString().trim().equals("")) {

                } else {
                    if (!isValidEmail(edt_email.getText().toString().trim())) {
                        edt_email.setError(getString(R.string.Enter_valid_Email_ID));
                        edt_email.requestFocus();
                        return;
                    }
                }

                if (Globals.strResvContact_Code.equals("")) {
                    Contact objCT1 = Contact.getContact(getApplicationContext(), database, db, " Where contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%'  order By contact_id Desc LIMIT 1");

                    if (objCT1 == null) {
                        strCTCode = Globals.objLPD.getDevice_Symbol() + "-" + "CT-" + 1;
                    } else {
                        strCTCode = Globals.objLPD.getDevice_Symbol() + "-" + "CT-" + (Integer.parseInt(objCT1.get_contact_code().toString().replace(Globals.objLPD.getDevice_Symbol() + "-CT-", "")) + 1);
                    }

                    progressDialog = new ProgressDialog(TicketPaymentActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.Wait_msg));
                    progressDialog.show();
                    final String finalStrCTCode = strCTCode;
                    Thread timerThread = new Thread() {
                        public void run() {

                            try {
                                sleep(1000);

                                final String result = Fill_Contact(edt_name.getText().toString().trim(), edt_mobile_dialog.getText().toString().trim(), edt_email.getText().toString().trim(), finalStrCTCode, "");

                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + result + "'");
                                        if (contact == null) {
                                            txt_mobile.setText("");
                                            btn_customer.setText("");
                                        } else {
                                            Globals.strResvContact_Code = result;
                                            txt_mobile.setText(contact.get_contact_1());
                                            btn_customer.setText(contact.get_name());

                                        }
                                        listDialog2.dismiss();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                } else {

                    progressDialog = new ProgressDialog(TicketPaymentActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.Wait_msg));
                    progressDialog.show();
                    final String finalStrCTCode = strCTCode;
                    Thread timerThread = new Thread() {
                        public void run() {

                            try {
                                sleep(1000);

                                String flagContact = "1";
                                final String result1 = Fill_Contact(edt_name.getText().toString().trim(), edt_mobile_dialog.getText().toString().trim(), edt_email.getText().toString().trim(), finalStrCTCode, flagContact);
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + result1 + "'");
                                        if (contact == null) {
                                            txt_mobile.setText("");
                                            btn_customer.setText("");
                                        } else {
                                            Globals.strResvContact_Code = result1;
                                            txt_mobile.setText(contact.get_contact_1());
                                            btn_customer.setText(contact.get_name());

                                        }
                                        listDialog2.dismiss();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();

//
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String Fill_Contact(String name, String mobile, String email, String strCTCode, String s) {
        Contact contact = null;
        try {


            if (s.equals("1")) {
                contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + Globals.strResvContact_Code + "'");
                contact.set_name(name);
                contact.set_contact_1(mobile);
                contact.set_email_1(email);

                long l = contact.updateContact("contact_code=?", new String[]{Globals.strResvContact_Code}, database);
                if (l > 0) {

                }

            } else {
                contact = new Contact(getApplicationContext(), null, Globals.Device_Code, strCTCode, "",
                        name, "", "", "", mobile, mobile, "", email, "", "1", modified_by, "N", "", date, "0", "", "", "");
                database.beginTransaction();
                long l1 = contact.insertContact(database);
                if (l1 > 0) {
                    Address address_class = new Address(getApplicationContext(), null, Globals.Device_Code, strCTCode, "AC-1",
                            "0", "", "0", "0", "0", "0", "0", "1", modified_by, date, "N");
                    long a = address_class.insertAddress(database);
                    if (a > 0) {
                        Address_Lookup address_lookup = new Address_Lookup(getApplicationContext(), null, Globals.Device_Code, strCTCode, "1",
                                strCTCode, "N");
                        long b = address_lookup.insertAddress_Lookup(database);

                    }

                    Contact_Bussiness_Group contact_bussiness_group = new Contact_Bussiness_Group(getApplicationContext(), strCTCode, "BGC-1");
                    long ab = contact_bussiness_group.insertContact_Bussiness_Group(database);
                    if (ab > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {
                        database.endTransaction();
                    }
                } else {
                    database.endTransaction();
                    //pDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        } catch (Exception ex) {

        }


        return contact.get_contact_code();

    }


    protected void performPDFExport() {
        Orders objOrder = Orders.getOrders(getApplicationContext(), database, "WHERE order_code = '" + strOrderNo + "'");
        final ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), "WHERE order_code = '" + strOrderNo + "'", database);

//        int count = 0;
//        while (count < order_detail.size()) {

//            String item_code = order_detail.get(count).get_item_code();
//            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
        list1a.add(btn_customer.getText().toString());
        list2a.add("Seat [-]");
        list3a.add(txt_time.getText().toString());
        list4a.add(objOrder.get_order_code());
        list5a.add(Globals.From);
        list6a.add(ticket_setup.get_departure());
        list7a.add(ticket_setup.get_arrival());
        list8a.add(txt_name.getText().toString());
        list9a.add(Globals.To);
//            count++;
//        }


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


            PdfPTable table_company_name = new PdfPTable(1);
            PdfPCell cell_company_name = new PdfPCell(new Paragraph("Passenger Name" + " : " + btn_customer.getText().toString(), B12));
            cell_company_name.setColspan(1);
            cell_company_name.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_company_name.setPadding(5.0f);
            table_company_name.addCell(cell_company_name);
            table_company_name.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_posno = new PdfPTable(1);
            PdfPCell cell_posno = new PdfPCell(new Paragraph(getString(R.string.Mobile_No) + " : " + txt_mobile.getText().toString(), B12));
            cell_posno.setColspan(1);
            cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_posno.setPadding(5.0f);
            table_posno.addCell(cell_posno);
            table_posno.setSpacingBefore(10.0f);
            document.open();

            PdfPTable table_order_no = new PdfPTable(1);
            PdfPCell cell_order_no = new PdfPCell(new Paragraph("Flight No" + " : " + objOrder.get_order_code(), B12));
            cell_order_no.setColspan(1);
            cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_no.setPadding(5.0f);
            table_order_no.addCell(cell_order_no);
            table_order_no.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_order_date = new PdfPTable(1);
            PdfPCell cell_order_date = new PdfPCell(new Paragraph("Booking Date" + " : " + objOrder.get_order_date(), B12));
            cell_order_date.setColspan(1);
            cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_order_date.setPadding(5.0f);
            table_order_date.addCell(cell_order_date);

            User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
            PdfPTable table_cashier = new PdfPTable(1);
            PdfPCell cell_cashier = new PdfPCell(new Paragraph("Payment Method" + " : " + "Cash", B12));
            cell_cashier.setColspan(1);
            cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_cashier.setPadding(5.0f);
            table_cashier.addCell(cell_cashier);


            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph(txt_name.getText().toString(), B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);


            PdfPTable table_header = new PdfPTable(3);
            Phrase pr1 = new Phrase(getString(R.string.PRN), B10);
            PdfPCell c1 = new PdfPCell(pr1);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c1);
            pr1 = new Phrase("" + objOrder.get_order_status(), B10);

            PdfPCell c210 = new PdfPCell(pr1);
            c210.setPadding(5);
            c210.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c210);

            Phrase pr2 = new Phrase(getString(R.string.BookingDate), B10);
            PdfPCell c2 = new PdfPCell(pr2);
            c2.setPadding(5);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c2);
            pr2 = new Phrase("" + objOrder.get_order_date(), B10);

            PdfPCell c211 = new PdfPCell(pr2);
            c211.setPadding(5);
            c211.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c211);

            Phrase pr3 = new Phrase(getString(R.string.PRN), B10);
            PdfPCell c3 = new PdfPCell(pr3);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c3);
            pr3 = new Phrase("" + objOrder.get_order_code(), B10);

            PdfPCell c212 = new PdfPCell(pr3);
            c212.setPadding(5);
            c212.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_header.addCell(c212);

//  Passenger Information
            PdfPTable table = new PdfPTable(2);

            Phrase pr = new Phrase(getString(R.string.passengerInf), B10);
            PdfPCell c11 = new PdfPCell(pr);
            c11.setPadding(5);
            table.addCell(c11);
            pr = new Phrase(getString(R.string.Flight), B10);
            PdfPCell c33 = new PdfPCell(pr);
            c33.setPadding(5);
            c33.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c33);

            for (int i = 0; i < list1a.size(); i++) {
                Phrase pr11 = new Phrase(list1a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr11);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c7);

                pr11 = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr11);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);


                c7 = new PdfPCell(pr11);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);
            }
            table.setSpacingBefore(10.0f);
            table.setHeaderRows(1);
            document.open();

// Flight Details


            PdfPTable table_detail = new PdfPTable(7);

            Phrase pr11 = new Phrase(getString(R.string.travelDate), B10);
            PdfPCell c111 = new PdfPCell(pr11);
            c111.setPadding(5);
            table_detail.addCell(c111);

            pr11 = new Phrase(getString(R.string.flight_no), B10);
            PdfPCell c333 = new PdfPCell(pr11);
            c333.setPadding(5);
            c333.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c333);

            pr11 = new Phrase(getString(R.string.from_terminal), B10);
            PdfPCell c44 = new PdfPCell(pr11);
            c44.setPadding(5);
            c44.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c44);

            pr11 = new Phrase(getString(R.string.to_terminal), B10);
            PdfPCell c55 = new PdfPCell(pr11);
            c55.setPadding(5);
            c55.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c55);

            pr11 = new Phrase(getString(R.string.dep_time), B10);
            PdfPCell c66 = new PdfPCell(pr11);
            c66.setPadding(5);
            c66.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c66);

            pr11 = new Phrase(getString(R.string.arr_time), B10);
            PdfPCell c77 = new PdfPCell(pr11);
            c77.setPadding(5);
            c77.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c77);

            pr11 = new Phrase(getString(R.string.airline), B10);
            PdfPCell c88 = new PdfPCell(pr11);
            c88.setPadding(5);
            c88.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_detail.addCell(c88);

            for (int i = 0; i < list2a.size(); i++) {
                Phrase pr123 = new Phrase(list3a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list4a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list5a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list9a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list6a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list7a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

                pr123 = new Phrase(list8a.get(i), N9);
                c7 = new PdfPCell(pr123);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_detail.addCell(c7);

            }
            table_detail.setSpacingBefore(10.0f);
            table_detail.setHeaderRows(1);
            document.open();

            PdfPTable table_subtotal = new PdfPTable(2);

            Phrase pr23 = new Phrase(getString(R.string.Total_Amount), B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c16);
            pr23 = new Phrase("" + objOrder.get_total(), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_subtotal.addCell(c17);
            table_subtotal.setSpacingBefore(10.0f);
            document.open();


            document.add(tableh);
            document.add(table_company_name);
            // document.add(table_posno);
            document.add(table_order_no);
            document.add(table_order_date);
            document.add(table_cashier);
            document.add(table);
            document.add(table_detail);
            document.add(table_subtotal);


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
            Globals.Depart = "";
            Globals.Arriv = "";
            Globals.From = "";
            Globals.To = "";
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {

                } catch (ActivityNotFoundException e) {
                }
            }

        } catch (Exception e) {
            f.delete();
        }
    }
}
