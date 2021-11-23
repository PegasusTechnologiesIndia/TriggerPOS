package org.phomellolitepos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.phomellolitepos.Adapter.DialogConctPurcsAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Purchase;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class PurchaseHeaderActivity extends AppCompatActivity {
    EditText edt_voucher_no, edt_date, edt_remarks, edt_ref_voucher_code, edt_cus;
    ImageView img_date, img_add;
    Button btn_next;
    String operation, voucher_no, strVoucherNo;
    Database db;
    SQLiteDatabase database;
    Purchase purchase;
    ProgressDialog pDialog;
    Calendar myCalendar;
    Spinner spn_cus;
    ArrayList<Contact> contact_ArrayList;
    String cusCode;
    Contact contact;
    String strSelectedCategory = "";
    Dialog listDialog1;
    DialogConctPurcsAdapter dialogConctPurcsAdapter;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_header);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                pDialog = new ProgressDialog(PurchaseHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            pDialog.dismiss();
                            Intent intent = new Intent(PurchaseHeaderActivity.this, PurchaseListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("operation", operation);
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
        getSupportActionBar().setTitle("Purchase");
        operation = intent.getStringExtra("operation");
        voucher_no = intent.getStringExtra("voucher_no");

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        myCalendar = Calendar.getInstance();
        edt_voucher_no = (EditText) findViewById(R.id.edt_voucher_no);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_remarks = (EditText) findViewById(R.id.edt_remarks);
        edt_ref_voucher_code = (EditText) findViewById(R.id.edt_ref_voucher_code);
        edt_cus = (EditText) findViewById(R.id.edt_cus);
        img_date = (ImageView) findViewById(R.id.img_date);
        img_add = (ImageView) findViewById(R.id.img_add);
        btn_next = (Button) findViewById(R.id.btn_next);
        settings = Settings.getSettings(getApplicationContext(), database, "");

//        edt_voucher_no.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (edt_voucher_no.getText().toString().trim().equals("")) {
//                    return false;
//                } else {
//                    edt_voucher_no.requestFocus();
//                    edt_voucher_no.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm4.showSoftInput(edt_voucher_no, InputMethodManager.SHOW_IMPLICIT);
//                    edt_voucher_no.selectAll();
//                    return true;
//                }
//            }
//        });
//
//        edt_remarks.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (edt_remarks.getText().toString().trim().equals("")) {
//                    return false;
//                } else {
//                    edt_remarks.requestFocus();
//                    edt_remarks.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm4.showSoftInput(edt_remarks, InputMethodManager.SHOW_IMPLICIT);
//                    edt_remarks.selectAll();
//                    return true;
//                }
//            }
//        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialogContact();
            }
        });

        if (operation.equals("Edit")) {
            try {
                purchase = Purchase.getPurchase(getApplicationContext(), "WHERE voucher_no = '" + voucher_no + "'", database);
                if (purchase != null) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    edt_voucher_no.setText(purchase.get_voucher_no());
                    edt_date.setText(purchase.get_date());
                    edt_remarks.setText(purchase.get_remarks());
                    edt_ref_voucher_code.setText(purchase.get_ref_voucher_code());
                    Contact contact = Contact.getContact(getApplicationContext(), database, db, " where contact_code='" + purchase.get_contact_code() + "'");
                    if (contact != null) {
                        edt_cus.setText(contact.get_name());
                    }
                    cusCode = purchase.get_contact_code();
                }
            }catch (Exception ex) {}
        } else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetDate();
            }
        };

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(img_date.getWindowToken(), 0);
                new DatePickerDialog(PurchaseHeaderActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_voucher_no.getText().toString().trim().equals("")) {
                    Purchase objVoucher = Purchase.getPurchase(getApplicationContext(), "order By id Desc LIMIT 1", database);

                    if (objVoucher == null) {
                        strVoucherNo = "PVC-" + 1;
                    } else {
                        strVoucherNo = "PVC-" + (Integer.parseInt(objVoucher.get_id()) + 1);
                    }
                } else {

                    strVoucherNo = edt_voucher_no.getText().toString().trim();
                }

                if (edt_date.getText().toString().trim().equals("")) {
                    edt_date.setError("Date is required");
                    return;
                }

                pDialog = new ProgressDialog(PurchaseHeaderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(PurchaseHeaderActivity.this, PurchaseFinalActivity.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("voucher_no", strVoucherNo);
                                    intent.putExtra("ref_voucher_code", edt_ref_voucher_code.getText().toString().trim());
                                    intent.putExtra("date", edt_date.getText().toString().trim());
                                    intent.putExtra("remarks", edt_remarks.getText().toString().trim());
                                    intent.putExtra("contact_code", cusCode);
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
        });
    }

    private void SetDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        String date;
        try {
            date = sdf.format(myCalendar.getTime());
        } catch (Exception ex) {
            date = "";
        }
        edt_date.setText(date);
    }

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
        final ArrayList<Bussiness_Group> arrayBusinessGroup = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1' and business_group_code = 'BGC-2' Order By name asc");
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
            groupCode = "and business_group_code='" + str + "'";
        }

        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where contact.contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-2'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        } else {
            contact_ArrayList = Contact.getAllContact(getApplicationContext(), database, db, " LEFT join contact_business_group on contact.contact_code = contact_business_group.contact_code where is_active ='1'  AND  contact_business_group.business_group_code = 'BGC-2'   " + groupCode + " " + strFilter + " Group by contact.contact_code Order By lower(name) asc");
        }

        dialogConctPurcsAdapter = new DialogConctPurcsAdapter(PurchaseHeaderActivity.this, contact_ArrayList, listDialog1);
        list11.setVisibility(View.VISIBLE);
        contact_title.setVisibility(View.GONE);
        list11.setAdapter(dialogConctPurcsAdapter);
        list11.setTextFilterEnabled(true);
    }

    public void setCustomer(String name, String contact_code) {
        edt_cus.setText(name);
        cusCode = contact_code;
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(PurchaseHeaderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    pDialog.dismiss();
                    Intent intent = new Intent(PurchaseHeaderActivity.this, PurchaseListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("operation", operation);
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
