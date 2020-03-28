package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.BankListAdapter;
import org.phomellolitepos.Adapter.DialogContactMainListAdapter;
import org.phomellolitepos.Adapter.DialogPayCollectionCustomerAdapter;
import org.phomellolitepos.Adapter.PayColInvoiceListAdapter;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.CheckBoxClass.PaymentInvoiceCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bank;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pay_Collection;
import org.phomellolitepos.database.Pay_Collection_Detail;
import org.phomellolitepos.database.Pay_Collection_Setup;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.printer.PrintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class PaymentCollectionActivity extends AppCompatActivity {
    EditText edt_cus_name, edt_amount, edt_cheque, edt_remarks, edt_code, edt_on_account;
    Spinner spn_pay_method, spn_bank;
    Button btn_save, btn_delete;
    ImageView img_cus;
    LinearLayout tb_row_bank;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    ArrayList<Payment> paymentArrayList;
    ArrayList<Bank> bankArrayList;
    ArrayList<Contact> contactArrayList;
    String strBankCode, strPayMethod, strCustomerCode = "", strSelectedCategory, strAutoGtCode = "";
    Pay_Collection pay_collection;
    DialogPayCollectionCustomerAdapter dialogPayCollectionCustomerAdapter;
    Dialog listDialog;
    String operation, code, date, decimal_check;
    ArrayList<PaymentInvoiceCheck> list = new ArrayList<PaymentInvoiceCheck>();
    String FlagPaymentdetail = "0";
    Settings settings;
//    PrintDirect printDirect = new PrintDirect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_collection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment Collection");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int idP = pref.getInt("id", 0);
        if (idP == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PaymentCollectionActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Globals.strResvContact_Code = "";
                            Intent intent = new Intent(PaymentCollectionActivity.this, PayCollectionListActivity.class);
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
        });

        edt_cus_name = (EditText) findViewById(R.id.edt_cus_name);
        edt_code = (EditText) findViewById(R.id.edt_code);
        edt_amount = (EditText) findViewById(R.id.edt_amount);
        edt_cheque = (EditText) findViewById(R.id.edt_cheque);
        edt_remarks = (EditText) findViewById(R.id.edt_remarks);
        edt_on_account = (EditText) findViewById(R.id.edt_on_account);
        spn_pay_method = (Spinner) findViewById(R.id.spn_pay_method);
        spn_bank = (Spinner) findViewById(R.id.spn_bank);
        img_cus = (ImageView) findViewById(R.id.img_cus);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_item_delete);
        tb_row_bank = (LinearLayout) findViewById(R.id.tb_row_bank);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        settings = Settings.getSettings(getApplicationContext(),database,"");
        if (operation.equals("Edit")) {
            btn_delete.setVisibility(View.VISIBLE);
            pay_collection = Pay_Collection.getPay_Collection(getApplicationContext(), "WHERE collection_code = '" + code + "'", database);
            strCustomerCode = pay_collection.get_contact_code();
            Globals.strResvContact_Code = strCustomerCode;
            Contact contact = Contact.getContact(getApplicationContext(), database, db, " WHERE contact_code='" + pay_collection.get_contact_code() + "'");
            edt_cus_name.setText(contact.get_name());
            edt_code.setText(pay_collection.get_collection_code());

            edt_amount.setText(Globals.myNumberFormat2Price(Double.parseDouble(pay_collection.get_amount()), decimal_check));
            edt_on_account.setText(pay_collection.get_on_account());
            edt_remarks.setText(pay_collection.get_remarks());
            String payment_mode = pay_collection.get_payment_mode();
            if (payment_mode.equals("CHEQUE")) {
                tb_row_bank.setVisibility(View.VISIBLE);
                fill_spinner_bank(pay_collection.get_ref_type());
                edt_cheque.setText(pay_collection.get_ref_no());
            } else {
                tb_row_bank.setVisibility(View.GONE);
            }

            fill_spinner_pay_method(payment_mode);


        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
            fill_spinner_bank("");
            fill_spinner_pay_method("");
        }

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        spn_pay_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Payment resultp = paymentArrayList.get(position);
                strPayMethod = resultp.get_payment_name();

                if (strPayMethod.equals("CHEQUE")) {
                    tb_row_bank.setVisibility(View.VISIBLE);

                } else {
                    tb_row_bank.setVisibility(View.GONE);

                }
                fill_spinner_bank("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bank resultp = bankArrayList.get(position);
                strBankCode = resultp.get_bank_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        img_cus.setOnClickListener(new View.OnClickListener() {
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


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(PaymentCollectionActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            database.beginTransaction();
                            pay_collection.set_is_active("0");
                            long it = pay_collection.updatePay_Collection("collection_code=?", new String[]{code}, database);
                            if (it > 0) {
                                database.setTransactionSuccessful();
                                database.endTransaction();
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent_category = new Intent(PaymentCollectionActivity.this, PayCollectionListActivity.class);
                                        startActivity(intent_category);
                                        finish();
                                    }
                                });
                            } else {
                                database.endTransaction();
                                pDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                    }
                                });
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


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_code.getText().toString().equals("")) {
                    Pay_Collection objPC = Pay_Collection.getPay_Collection(getApplicationContext(), "  order By id Desc LIMIT 1", database);

                    if (objPC == null) {
                        strAutoGtCode = Globals.objLPD.getDevice_Symbol() + "-" + 1;
                    } else {
                        strAutoGtCode = Globals.objLPD.getDevice_Symbol() + "-" + (Integer.parseInt(objPC.get_id()) + 1);
                    }
                } else {
                    strAutoGtCode = edt_code.getText().toString();
                }

                if (edt_cus_name.getText().toString().equals("")) {
                    edt_cus_name.setError("Customer is required");
                    edt_cus_name.requestFocus();
                    return;
                }

                if (edt_amount.getText().toString().equals("")) {
                    edt_amount.setError("Amount is required");
                    edt_amount.requestFocus();
                    return;
                }

                if (tb_row_bank.getVisibility() == View.VISIBLE) {
                    if (edt_cheque.getText().toString().equals("")) {
                        edt_cheque.setError("Cheque is required");
                        edt_cheque.requestFocus();
                        return;
                    }

                } else {
                    strBankCode = "";
                }

                pDialog = new ProgressDialog(PaymentCollectionActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Fill_Pay_Collection(strPayMethod, strBankCode, edt_amount.getText().toString(), strCustomerCode, edt_cheque.getText().toString().trim(), edt_remarks.getText().toString().trim(), strAutoGtCode, edt_on_account.getText().toString().trim());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                };
                timerThread.start();
                }
        });


    }

    private void Fill_Pay_Collection(String strPayMethod, String strBankCode, String amount, String customer_code, String cheque_no, String remarks, String strAutoGtCode, String onAccount) {
        final String opr = "PayCollection";
        String modified_by = Globals.user;
        if (Globals.strResvContact_Code.equals("")) {
            Globals.strResvContact_Code = strCustomerCode;
        } else {
            strCustomerCode = Globals.strResvContact_Code;
        }


        if (operation.equals("Edit")) {
            pay_collection = new Pay_Collection(getApplicationContext(), pay_collection.get_id(), strCustomerCode, strAutoGtCode, date, amount, strPayMethod, cheque_no, strBankCode, onAccount, remarks, "1", modified_by, date, "N");
            database.beginTransaction();
            final long l1 = pay_collection.updatePay_Collection("collection_code=?", new String[]{code}, database);
            if (l1 > 0) {
                if (FlagPaymentdetail.equals("1")) {
                    long e5 = Pay_Collection_Detail.delete_Pay_Collection_Detail(getApplicationContext(), "Pay_Collection_Detail", " collection_code =? ", new String[]{code}, database);
                    for (int i = 0; i < list.size(); i++) {
                        PaymentInvoiceCheck b = list.get(i);
                        if (b.isSelected()) {

                            Pay_Collection_Detail pay_collection_detail = new Pay_Collection_Detail(getApplicationContext(), null, strAutoGtCode, b.getInvoice_no(), b.getAmount());

                            long a = pay_collection_detail.insertPay_Collection_Detail(database);
                            if (a > 0) {
                                }
                        } else {
                        }
                    }
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {

                            Intent intent_category = new Intent(PaymentCollectionActivity.this, PrintLayout.class);
                            intent_category.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent_category.putExtra("id", pay_collection.get_id() + "");
                            intent_category.putExtra("opr", opr);
                            startActivity(intent_category);

                    }
                });


            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            pay_collection = new Pay_Collection(getApplicationContext(), null, strCustomerCode, strAutoGtCode, date, amount, strPayMethod, cheque_no, strBankCode, onAccount, remarks, "1", modified_by, date, "N");
            database.beginTransaction();
            final long l = pay_collection.insertPay_Collection(database);
            if (l > 0) {
                if (FlagPaymentdetail.equals("1")) {
                    for (int i = 0; i < list.size(); i++) {
                        PaymentInvoiceCheck b = list.get(i);
                        if (b.isSelected()) {

                            Pay_Collection_Detail pay_collection_detail = new Pay_Collection_Detail(getApplicationContext(), null, strAutoGtCode, b.getInvoice_no(), b.getAmount());

                            long a = pay_collection_detail.insertPay_Collection_Detail(database);
                            if (a > 0) {

                            }
                        } else {

                        }
                    }
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                Globals.strResvContact_Code = "";
                runOnUiThread(new Runnable() {
                    public void run() {

//                        if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                            printDirect.PrintWithoutDialog(PaymentCollectionActivity.this,"","",l + "",opr);
//                        } else {
                            Intent intent_category = new Intent(PaymentCollectionActivity.this, PrintLayout.class);
                            intent_category.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent_category.putExtra("id", l + "");
                            intent_category.putExtra("opr", opr);
                            startActivity(intent_category);
//                        }

                    }
                });

            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }

    private void fill_spinner_pay_method(String s) {
        paymentArrayList = Payment.getAllPayment(getApplicationContext(), " WHERE is_active ='1' Order By payment_name asc");
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

    private void fill_spinner_bank(String s) {
        bankArrayList = Bank.getAllBank(getApplicationContext(), " WHERE is_active ='1' Order By bank_name asc");
        BankListAdapter bankListAdapter = new BankListAdapter(getApplicationContext(), bankArrayList);
        spn_bank.setAdapter(bankListAdapter);

        if (!s.equals("")) {
            for (int i = 0; i < bankListAdapter.getCount(); i++) {
                String iname = bankArrayList.get(i).get_bank_name();
                if (s.equals(iname)) {
                    spn_bank.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showdialogContact() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog = new Dialog(this);
        listDialog.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pos_contact_list_item, null, false);
        listDialog.setContentView(v1);
        listDialog.setCancelable(true);

        final ListView list11 = (ListView) listDialog.findViewById(R.id.lv_custom_ortype);
        final TextView contact_title = (TextView) listDialog.findViewById(R.id.contact_title);
        final EditText edt_srch_contct = (EditText) listDialog.findViewById(R.id.edt_srch_contct);
        ImageView srch_image = (ImageView) listDialog.findViewById(R.id.srch_image);
        TextView txt_reset = (TextView) listDialog.findViewById(R.id.txt_reset);
        ImageView img_brs = (ImageView) listDialog.findViewById(R.id.img_brs);
        img_brs.setVisibility(View.GONE);
        listDialog.show();
        fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
        Window window = listDialog.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

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
            groupCode = "and business_group_code='" + str + "'";
        }

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contactArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contactArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-1'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogPayCollectionCustomerAdapter = new DialogPayCollectionCustomerAdapter(PaymentCollectionActivity.this, contactArrayList, listDialog);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogPayCollectionCustomerAdapter);
        list11.setTextFilterEnabled(true);

    }

    public void setCustomer(String name) {
        edt_cus_name.setText(name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pay_collection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Pay_Collection_Setup pay_collection_setup;
        int id = item.getItemId();
        if (id == R.id.action_invoice) {
            pay_collection_setup = Pay_Collection_Setup.getPay_Collection_Setup(getApplicationContext(), "", database);

            if (!Globals.strResvContact_Code.equals("")) {
                pay_collection_setup = Pay_Collection_Setup.getPay_Collection_Setup(getApplicationContext(), " WHERE contact_code = '" + Globals.strResvContact_Code + "'", database);
                if (pay_collection_setup == null) {
                    Toast.makeText(getApplicationContext(), "No Invoice Found", Toast.LENGTH_SHORT).show();
                } else {
                    showdialogInvoiceList();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Select customer first", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showdialogInvoiceList() {
        strSelectedCategory = "";
        final String strFiltr = "";
        listDialog = new Dialog(this);
//        listDialog.setTitle(R.string.Select_Contact);
        LayoutInflater li1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.pay_col_dialog_invoice, null, false);
        listDialog.setContentView(v1);
        listDialog.setCancelable(true);

        final ListView list1 = (ListView) listDialog.findViewById(R.id.list);
        Button btn_clear = (Button) listDialog.findViewById(R.id.btn_clear);
        listDialog.show();
        fill_dialog_invoice_List(list1);
        Window window = listDialog.getWindow();
        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
//        final ArrayList<Bussiness_Group> arrayBusinessGroup = Bussiness_Group.getAllBussiness_Group(getApplicationContext(),database,db," WHERE is_active ='1' Order By name asc");

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double totalAmount = 0d;
                for (int i = 0; i < list.size(); i++) {
                    PaymentInvoiceCheck b = list.get(i);
                    if (b.isSelected()) {
                        totalAmount = totalAmount + (Double.parseDouble(b.getAmount()));
                    } else {
                    }
                }
                if (totalAmount != 0) {
                    FlagPaymentdetail = "1";
                    edt_amount.setText(Globals.myNumberFormat2Price(totalAmount, decimal_check));
                }
                listDialog.dismiss();
            }
        });

    }

    private void fill_dialog_invoice_List(ListView list11) {
        list.clear();
        ArrayList<Pay_Collection_Setup> pay_collection_setupArrayList = Pay_Collection_Setup.getAllPay_Collection_Setup(getApplicationContext(), " WHERE contact_code='" + Globals.strResvContact_Code + "'");
        for (int i = 0; i < pay_collection_setupArrayList.size(); i++) {
            PaymentInvoiceCheck b = new PaymentInvoiceCheck();
            b.setInvoice_no(pay_collection_setupArrayList.get(i).get_invoice_no());
            b.setInvoice_date(pay_collection_setupArrayList.get(i).get_invoice_date());
            b.setAmount(pay_collection_setupArrayList.get(i).get_amount());
            b.setSelected(false);
            list.add(b);
        }
        if (list.size() > 0) {
            PayColInvoiceListAdapter payColInvoiceListAdapter = new PayColInvoiceListAdapter(PaymentCollectionActivity.this, list);
            list11.setVisibility(View.VISIBLE);
//        contact_title.setVisibility(View.GONE);
            list11.setAdapter(payColInvoiceListAdapter);
            list11.setTextFilterEnabled(true);
        }


    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(PaymentCollectionActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Globals.strResvContact_Code = "";
                    Intent intent = new Intent(PaymentCollectionActivity.this, PayCollectionListActivity.class);
                    startActivity(intent);
                    pDialog.dismiss();
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
