package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.AccountsListAdapter;
import org.phomellolitepos.Adapter.ContactListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import java.util.ArrayList;

public class AccountsListActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
    EditText edt_toolbar_contact_list;
    TextView contact_title;
    Contact contact;
    Address address;
    ArrayList<Contact> arrayList;
    AccountsListAdapter accountsListAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;
    ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_contact_list = (EditText) findViewById(R.id.edt_toolbar_contact_list);
        contact_title = (TextView) findViewById(R.id.contact_title);
        mScannerView = new ZBarScannerView(AccountsListActivity.this);
        mScannerView.setResultHandler(AccountsListActivity.this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edt_toolbar_contact_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_toolbar_contact_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_contact_list.requestFocus();
                    edt_toolbar_contact_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_contact_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_contact_list.selectAll();
                    return true;
                }
            }
        });

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
                pDialog = new ProgressDialog(AccountsListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Globals.AccountBarcode="0";
                                Globals.BarcodeReslt="";
                                Intent intent = new Intent(AccountsListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(AccountsListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Globals.AccountBarcode="0";
                                Globals.BarcodeReslt="";
                                Intent intent = new Intent(AccountsListActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });

        // Getting contect list here
        if (Globals.AccountBarcode.equals("1")){
            String strFilter = Globals.BarcodeReslt;
            strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_contact_list.selectAll();
            Globals.BarcodeReslt="";
            getContactList(strFilter);
        }else {
            getContactList("");
        }

    }

    private void getContactList(String strFilter) {
        if (settings.get_Is_Device_Customer_Show().equals("true")) {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE contact_code like  '"+ Globals.objLPD.getDevice_Symbol() +"-CT-%' and is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        } else {
            arrayList = Contact.getAllContact(getApplicationContext(), database, db, "WHERE is_active = '1'  and  contact_code IN (Select contact_code from contact_business_group where business_group_code = 'BGC-1') " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        }
        ListView category_list = (ListView) findViewById(R.id.contact_list);
        if (arrayList.size() > 0) {
            accountsListAdapter = new AccountsListAdapter(AccountsListActivity.this, arrayList);
            contact_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(accountsListAdapter);
            category_list.setTextFilterEnabled(true);
            accountsListAdapter.notifyDataSetChanged();
        } else {
            contact_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_send);
        item.setIcon(getResources().getDrawable(R.drawable.qr_code));
        item.getIcon().setAlpha(130);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_contact_list.getText().toString().trim();
            strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_contact_list.selectAll();
            getContactList(strFilter);
            return true;
        }

        if (id == R.id.action_send) {
            Globals.BarcodeReslt = "";
            mScannerView.startCamera(); // Programmatically initialize the scanner view
            setContentView(mScannerView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        //Prints the scan format (qrcode, pdf417 etc.)
        Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + rawResult.getContents() + "'");
        if (contact == null) {
            Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_SHORT).show();
            Globals.AccountBarcode="1";
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(AccountsListActivity.this, AccountsListActivity.class);
            startActivity(intent);
        } else {
            Globals.AccountBarcode="1";
            Globals.BarcodeReslt = rawResult.getContents();
            mScannerView.stopCameraPreview(); //stopPreview
            mScannerView.stopCamera();
            Intent intent = new Intent(AccountsListActivity.this, AccountsListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(AccountsListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Globals.AccountBarcode="0";
                        Globals.BarcodeReslt="";
                        Intent intent = new Intent(AccountsListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(AccountsListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Globals.AccountBarcode="0";
                        Globals.BarcodeReslt="";
                        Intent intent = new Intent(AccountsListActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }

}
