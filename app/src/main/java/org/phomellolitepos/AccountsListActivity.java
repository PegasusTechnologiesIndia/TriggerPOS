package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    ZBarScannerView mScannerView;
Intent intent;
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
        edt_toolbar_contact_list = (EditText) findViewById(R.id.edt_toolbar_contact_list);
        contact_title = (TextView) findViewById(R.id.contact_title);
        try {
            if(mScannerView!=null) {
                mScannerView = new ZBarScannerView(AccountsListActivity.this);
                mScannerView.setResultHandler(AccountsListActivity.this);
            }
            else if(mScannerView == null)
            {
                mScannerView = new ZBarScannerView(this);
               // setContentView(mScannerView);
            }
        }
        catch(Exception e){

        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        intent=getIntent();

            String whatsappflag = intent.getStringExtra("whatsappFlag");
            String contact_code = intent.getStringExtra("contact_code");
        if(whatsappflag!=null) {
            if (whatsappflag.equals("true")) {
                share_dialog(contact_code);

            }
        }
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

                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                                try {
                                    Globals.AccountBarcode = "0";
                                    Globals.BarcodeReslt = "";
                                    Intent intent = new Intent(AccountsListActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(AccountsListActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Globals.AccountBarcode = "0";
                                    Globals.BarcodeReslt = "";
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
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(AccountsListActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("3")){
                            Intent intent = new Intent(AccountsListActivity.this, PaymentCollection_MainScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                };
                timerThread.start();
            }
        });

        // Getting contact list here
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
        if (Globals.objsettings.get_Is_Device_Customer_Show().equals("true")) {
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

        //search filter
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_contact_list.getText().toString().trim();
            strFilter = " and ( contact_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_contact_list.selectAll();
            getContactList(strFilter);
            return true;
        }
// Barcode Scanner
        if (id == R.id.action_send) {
            Globals.BarcodeReslt = "";
            if(mScannerView == null)
            {
                mScannerView = new ZBarScannerView(this);
               // setContentView(mScannerView);
            }
            else {
                mScannerView.startCamera(); // Programmatically initialize the scanner view
                setContentView(mScannerView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
            Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
            //Prints the scan format (qrcode, pdf417 etc.)
            Contact contact = Contact.getContact(getApplicationContext(), database, db, "where contact_code='" + rawResult.getContents() + "'");
            if (contact == null) {
                Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_SHORT).show();
                Globals.AccountBarcode = "1";
                mScannerView.stopCameraPreview(); //stopPreview
                mScannerView.stopCamera();
                Intent intent = new Intent(AccountsListActivity.this, AccountsListActivity.class);
                startActivity(intent);
            } else {
                Globals.AccountBarcode = "1";
                Globals.BarcodeReslt = rawResult.getContents();
                mScannerView.stopCameraPreview(); //stopPreview
                mScannerView.stopCamera();
                Intent intent = new Intent(AccountsListActivity.this, AccountsListActivity.class);
                startActivity(intent);
            }
        }
        catch(Exception e){

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
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                        try {
                            Globals.AccountBarcode = "0";
                            Globals.BarcodeReslt = "";
                            Intent intent = new Intent(AccountsListActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
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
                            Globals.AccountBarcode = "0";
                            Globals.BarcodeReslt = "";
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
                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(AccountsListActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
               else if(Globals.objLPR.getIndustry_Type().equals("3")){
                    Intent intent = new Intent(AccountsListActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    public void share_dialog(String Contactcode){
        final Dialog listDialog2 = new Dialog(AccountsListActivity.this);
        listDialog2.setTitle("File Share");
        LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(false);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        edt_remark.setVisibility(View.GONE);
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        btnButton.setText("Share");
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

                runOnUiThread(new Runnable() {
                    public void run() {

                        //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                        startWhatsApp(Contactcode);

                    }
                });

                listDialog2.dismiss();

            }
        });
    }

    private void startWhatsApp(String Contactcode) {
        String strContct = "";
        contact = Contact.getContact(getApplicationContext(), database, db, " where is_active ='1' and contact_code='" + Contactcode + "'");
        if (contact == null) {
        } else {
            if(Globals.objLPR.getCountry_Id().equals("99")) {
                strContct = "91"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("114")) {

                strContct = "965"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("221")) {
                strContct = "971"+contact.get_contact_1();
            }
        }
        File file=null;
        if(Globals.PrinterType.equals("11")){
            file = new File(Globals.folder + Globals.pdffolder
                    + "/" + Contactcode+"80mm" + ".pdf");
        }
        else {
            file = new File(Globals.folder + Globals.pdffolder
                    + "/" + Contactcode + ".pdf");
        }
        // Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_SHORT).show();
        if (contactExists(getApplicationContext(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file,getApplicationContext(),contact.get_contact_1());
                    // print_dialog();
                } catch (Exception e) {
                    Globals.AppLogWrite("Contact Exception  "+e.getMessage());
                    /// Toast.makeText(getApplicationContext(),"Exception"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        else {

            if (SaveContact()) {
                Toast.makeText(getBaseContext(), "Contact Saved!", Toast.LENGTH_SHORT).show();
                finish();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file,getApplicationContext(),contact.get_contact_1());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file,Context context,String contactnumbr) {
        Uri path = FileProvider.getUriForFile(context, "com.org.phomellolitepos.myfileprovider", file);
        //Uri path = Uri.fromFile(file);
        String formattedNumber =  contactnumbr;
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);

        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        if(!number.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null && cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            //  Toast.makeText(getApplicationContext(),app_installed+"App Installed",Toast.LENGTH_SHORT).show();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean SaveContact() {
        //Get text
        String szFirstname = contact.get_name(),
                szPhone = "+91" + " " + contact.get_contact_1();

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //Create a new contact entry!

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFirstname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, szFirstname) // Name of the person
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szPhone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        //INSERT PITURE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        stream.toByteArray())
                .build());

        Uri newContactUri = null;

        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
            }
        } catch (RemoteException e) {
            // error
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            newContactUri = null;
        }
        return newContactUri != null;
    }
}
