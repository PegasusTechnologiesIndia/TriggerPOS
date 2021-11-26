package org.phomellolitepos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.phomellolitepos.Adapter.ItemAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;

import au.com.bytecode.opencsv.CSVWriter;
import in.gauriinfotech.commons.Commons;

public class ItemListActivity extends AppCompatActivity {
    AutoCompleteTextView edt_toolbar_item_list;
    TextView item_title;
    Item_Supplier item_supplier;
    Item_Location item_location;
    ArrayList<Item> arrayList;
    ItemAdapter itemAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Tax_Detail tax_detail;
    Tax_Master tax_master;
    Order_Type_Tax order_type_tax;
    String serial_no, android_id, myKey, device_id, imei_no;
    String date, succ_import;
    Item item;
    Item_Group_Tax item_group_tax;
    ReceipeModifier receipemodifier;
    Orders orders;
    Unit unit;
    Item_Group item_group;
   // Settings settings;
    private RecyclerView recyclerView;
String itemgroup_value;
    String PathHolder;
    ArrayList<String> arrayList1;
    String unitId, item_Groupcode;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        getSupportActionBar().setTitle("");
        FirebaseCrashlytics.getInstance().recordException(new Exception());
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        serial_no = Build.SERIAL;


        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;
        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(Globals.objLPR.getproject_id().equals("4")){
            orders = Orders.getOrders(getApplicationContext(), database, "");
        }
        else {
            orders = Orders.getOrders(getApplicationContext(), database, " where z_code ='0'");
        }
        Globals.objsettings = Settings.getSettings(getApplicationContext(), database, "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FirebaseCrashlytics.getInstance().recordException(new Exception());
          /*      pDialog = new ProgressDialog(ItemListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {*/
                    if (Globals.objLPR.getIndustry_Type().equals("4")) {
                        Intent intent = new Intent(ItemListActivity.this, ParkingIndustryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        // pDialog.dismiss();
                        finish();
                    }
                    else if (Globals.objLPR.getIndustry_Type().equals("2")) {
                        Intent intent = new Intent(ItemListActivity.this, Retail_IndustryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        // pDialog.dismiss();
                        finish();
                    }
                    else {
                        if (Globals.objsettings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                // pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                //  pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                //   pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }
                    }
                    }catch(Exception e){

                    }

                  /*  }
                };
                timerThread.start();*/
            }
        });


        item_title = (TextView) findViewById(R.id.item_title);
        edt_toolbar_item_list = (AutoCompleteTextView) findViewById(R.id.edt_toolbar_item_list);
        edt_toolbar_item_list.setMaxLines(1);
        edt_toolbar_item_list.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edt_toolbar_item_list.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    getCurrentFocus().clearFocus();
                    String strFilter = edt_toolbar_item_list.getText().toString().trim();
                    strFilter = " and ( item_code Like '%" + strFilter + "%'  Or item_name Like '%" + strFilter + "%' Or barcode Like '%" + strFilter + "%' Or sku Like '%" + strFilter + "%')";
                    edt_toolbar_item_list.selectAll();
                    getItemList(strFilter);
                    return true;
                }
                return false;
            }
        });
        edt_toolbar_item_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_toolbar_item_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_item_list.requestFocus();
                    edt_toolbar_item_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_item_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_item_list.selectAll();
                    return true;
                }
            }
        });


        edt_toolbar_item_list.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strFilter = edt_toolbar_item_list.getText().toString().trim();
                autocomplete(strFilter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // add button click for Creating Items
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (Globals.objLPR.getIndustry_Type().equals("4"))
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_intent = new Intent(ItemListActivity.this, ItemActivity.class);
                startActivity(item_intent);
                finish();
            }
        });
        try {
            // Gettting item list here
            getItemList("");
        }
        catch(Exception e){

        }
    }

    private void autocomplete(String strFilter) {

        strFilter = edt_toolbar_item_list.getText().toString().trim();
        strFilter = " and ( item_code Like '%" + strFilter + "%'  Or item_name Like '%" + strFilter + "%' Or barcode Like '%" + strFilter + "%' Or sku Like '%" + strFilter + "%')";
        if (Globals.objsettings.get_Is_Zero_Stock().equals("true")) {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), " WHERE is_active = '1' and  is_modifier != '1' " + strFilter + " limit 10");
        } else {
            arrayList1 = Item.getAllItemforautocomplete(getApplicationContext(), "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1' " + strFilter + "  and item_location.quantity!='0' and item.is_modifier != '1'  Order By lower(item_name) asc limit 10");
        }

        if (arrayList1.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, R.layout.items_spinner, arrayList1);
            edt_toolbar_item_list.setThreshold(0);
            edt_toolbar_item_list.setAdapter(adapter);
        }
    }
    private void getItemList(String strFilter) {
        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(item_name) asc limit " + Globals.ListLimit + "", database);
        if (arrayList.size() > 0) {
            itemAdapter = new ItemAdapter(ItemListActivity.this, arrayList);
           // recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            item_title.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
           // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
           // recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(itemAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            item_title.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item resultp = arrayList.get(position);
                String operation = "Edit";
                String item_code = resultp.get_item_code();
                Intent intent = new Intent(ItemListActivity.this, ItemActivity.class);
                intent.putExtra("item_code", item_code);
                intent.putExtra("operation", operation);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_listmenu, menu);
        if (Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.grp_file, true);
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        } else {
            menu.setGroupVisible(R.id.grp_file, false);

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item_menu) {
        int id = item_menu.getItemId();

        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_item_list.getText().toString().trim();
            strFilter = " and ( item_code Like '%" + strFilter + "%'  Or item_name Like '%" + strFilter + "%' Or barcode Like '%" + strFilter + "%' Or sku Like '%" + strFilter + "%')";
            edt_toolbar_item_list.selectAll();
            getItemList(strFilter);
            View view = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }

        if (id == R.id.action_file) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);

            builder.setTitle(getString(R.string.alerttitle));
            builder.setMessage(getString(R.string.alert_impexpmsg));

            builder.setPositiveButton(getString(R.string.Import_CSV), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    try {

                        String[] mimeTypes = {"text/*"};
                        // File file= new File(Environment.getExternalStorageDirectory()+"");
                        Intent intent;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                            // intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                            if (mimeTypes.length > 0) {
                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                //   intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, mimeTypes);
                            }
                        } else {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);

                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            String mimeTypesStr = "";
                            for (String mimeType : mimeTypes) {
                                mimeTypesStr += mimeType + "|";
                            }
                            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                        }
                        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 7);

                        //  postDeviceInfo(lite_pos_registration.getEmail(), lite_pos_registration.getPassword(), Globals.isuse, Globals.master_product_id, "", Globals.Device_Code, serial_no,  Globals.syscode2, android_id, myKey,lite_pos_registration.getRegistration_Code(),"1");
                    } catch (Exception e) {

                    }
                    dialog.dismiss();
                }
            });


            builder.setNegativeButton(getString(R.string.Export_csv), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    pDialog = new ProgressDialog(ItemListActivity.this);
                    pDialog.setTitle("");
                    pDialog.setMessage("Exporting data.....");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    // sleep(200);

                                    // Export CSV Code
                                    String succ = export();

                                    if (succ.equals("success")) {

                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                try {
                                                    File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "item_export" + ".csv");

                                                    //  Toast.makeText(getApplicationContext(), "CSV Exported Successfully " + fileWithinMyDir, Toast.LENGTH_LONG).show();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);

                                                    builder.setTitle(getString(R.string.alerttitle));
                                                    builder.setMessage(getString(R.string.alert_sharemsg));

                                                    builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Do nothing but close the dialog

                                                            try {
                                                                //  Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                                                File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "item_export.csv");

                                                                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                                                                File f = new File(fileWithinMyDir.getAbsolutePath());

                                                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                                                shareIntent.setType("message/rfc822");
                                                                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.org.phomellolitepos.myfileprovider", new File(fileWithinMyDir.getAbsolutePath()));
                                                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Trigger POS Item CSV");
                                                                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, Please find attached  Item.csv");
                                                                startActivity(Intent.createChooser(shareIntent, f.getName()));

                                                            } catch (Exception e) {
                                                                System.out.println(e.getMessage());
                                                            }
                                                        }
                                                    });

                                                    builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            // Do nothing

                                                            dialog.dismiss();

                                                        }
                                                    });

                                                    AlertDialog alert = builder.create();
                                                    alert.show();

                                                    pDialog.dismiss();
                                                } catch (Exception e) {

                                                }


                                            }
                                        });

                                    }
                                } catch (final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception ex) {


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                    }
                                });
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
            });
            builder.setNeutralButton(getString(R.string.downloadfiles), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog

                    pDialog = new ProgressDialog(ItemListActivity.this);
                    pDialog.setTitle("");
                    pDialog.setMessage("Exporting data.....");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    // sleep(200);

// export sample files
                                    String succ = export_sample();

                                    if (succ.equals("success")) {

                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                try {
                                              File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "item_export_sample" + ".csv");

                                                    //  Toast.makeText(getApplicationContext(), "CSV Exported Successfully " + fileWithinMyDir, Toast.LENGTH_LONG).show();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);

                                                    builder.setTitle(getString(R.string.alerttitle));
                                                    builder.setMessage(getString(R.string.alert_sharemsg));

                                                    builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Do nothing but close the dialog

                                                            try {
                                                                //  Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                                                File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "item_export_sample.csv");

                                                                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                                                                File f = new File(fileWithinMyDir.getAbsolutePath());

                                                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                                                shareIntent.setType("message/rfc822");
                                                                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.org.phomellolitepos.myfileprovider", new File(fileWithinMyDir.getAbsolutePath()));
                                                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Trigger POS Item Sample CSV");
                                                                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, Please find attached  item_export_sample.csv");
                                                                startActivity(Intent.createChooser(shareIntent, f.getName()));

                                                            } catch (Exception e) {
                                                                System.out.println(e.getMessage());
                                                            }
                                                        }
                                                    });

                                                    builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            // Do nothing

                                                            dialog.dismiss();

                                                        }
                                                    });

                                                    AlertDialog alert = builder.create();
                                                    alert.show();

                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "" + "item_export_sample" + ".csv");

                                                            Toast.makeText(getApplicationContext(), fileWithinMyDir.toString(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                } catch (Exception e) {

                                                }


                                            }
                                        });


                                    }else
                                        {
                                            pDialog.dismiss();
                                        }
                                } catch (final Exception e) {
                                    pDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception ex) {


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                    }
                                });
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (id == R.id.action_send) {
            // here dialog will open for cloud operations
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    ItemListActivity.this);
            alertDialog.setTitle(R.string.Import_Function);
            alertDialog
                    .setMessage(R.string.dilog_msg_cdopr);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);



            alertDialog.setPositiveButton(R.string.sync,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    ItemListActivity.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.sync_data_from_server);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);

                            alertDialog.setNegativeButton(R.string.Cancel,

                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    });

                            alertDialog.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener()
                            {
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                            if (orders == null) {
                                                if (isNetworkStatusAvialable(getApplicationContext()))
                                                {
                                                    pDialog = new ProgressDialog(ItemListActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.dwnld_item));
                                                    pDialog.show();
                                                    Thread t1=new Thread() {
                                                        @Override
                                                        public void run() {
                                                            String result="";
                                                            String suss="";
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        // Getting items form server
                                                                       String result= send_online_item(pDialog);
                                                                       if(Globals.objLPR.getIndustry_Type().equals("4"))
                                                                       {
                                                                           String r=send_online_item_group(pDialog);
                                                                       }

                                                                       if(Globals.objLPR.getIndustry_Type().equals("2")||Globals.objLPR.getIndustry_Type().equals("1"))
                                                                       {
                                                                           if(result.equals("0"))
                                                                           {
                                                                            pDialog.dismiss();
                                                                               runOnUiThread(new Runnable()
                                                                               {
                                                                                   @Override
                                                                                   public void run()
                                                                                   {
                                                                                      Toast.makeText(ItemListActivity.this,"No Data  Found ",Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               });
                                                                           }else
                                                                           {
                                                                               pDialog.dismiss();
                                                                               runOnUiThread(new Runnable() {
                                                                                   @Override
                                                                                   public void run()
                                                                                   {
                                                                                       Toast.makeText(ItemListActivity.this," Sync Successfully ",Toast.LENGTH_SHORT).show();
                                                                                   }
                                                                               });
                                                                           }
                                                                       }
                                                                    }
                                                                    catch(Exception e){

                                                                    }
                                                                }
                                                            });



                                                            // send items on server


                                                        }
                                                    };t1.start();

                                                    try {
                                                        t1.join();
                                                       // pDialog.dismiss();
                                                    } catch (InterruptedException ie) {
                                                        ie.printStackTrace();
                                                    }
                                                  Thread t2=  new Thread() {
                                                        @Override
                                                        public void run() {
                                                            String result="";
                                                            String suss="";
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        //Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
                                                                        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");
                                                                        if (sys_sycntime == null) {
                                                                            getitemgrp_from_server("", serial_no, android_id, myKey);
                                                                        } else {
                                                                            getitemgrp_from_server(sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                                                        }
                                                                        //suss = getitem();
                                                                    }
                                                                    catch(Exception e){
                                                                    }
                                                                }
                                                            });
                                                            // send items on server
                                                        }
                                                    };t2.start();
                                                    try {
                                                        t2.join();
                                                    } catch (InterruptedException ie) {
                                                        ie.printStackTrace();
                                                    }


                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(getApplicationContext(), getString(R.string.postorderserverfrst), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }


                                        }


                                    });


                            AlertDialog alert = alertDialog.create();
                            alert.show();

                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                        }
                    });


            AlertDialog alert = alertDialog.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = Globals.objLPR.getproject_id();

                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }
                }
            });

            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

            Button sbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
            sbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        return super.
                onOptionsItemSelected(item_menu);
    }

   /* private String getLocationStock() {
        String succ_bg = "";
        database.beginTransaction();
        String serverData = get_loc_stock_from_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_code = jsonObject_bg1.getString("item_code");

                    item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
                    if (item_location == null) {
                    } else {
                        item_location.set_quantity(jsonObject_bg1.getString("quantity"));
                        long itmlc = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                        if (itmlc > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e
        ) {
            Globals.ErrorMsg = e.getMessage();
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }*/

   /* private String get_loc_stock_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "item/location");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            //TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private String send_online_item(ProgressDialog pDialog) {
       // Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String result = Item.sendOnServer(pDialog,getApplicationContext(), database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax,modified_date,is_modifier FROM item  WHERE is_push = 'N'", Globals.objLPD.getLic_customer_license_id());
        return result;
    }
    private String send_online_item_group(final ProgressDialog pdialog) {
        //   Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String l = Item_Group.sendOnServer(pdialog,getApplicationContext(), database, db, "Select * From item_group  ",serial_no,Globals.syscode2,android_id,myKey,Globals.license_id);
        return l;
    }
    private String getitem( String serverData) {

        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmsg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");

                if (sys_sycntime != null) {
                    //  String modified_date= jsonObject_bg1.getString("modified_date").toString().substring(0,i-1);


                    sys_sycntime.set_datetime(jsonArray_bg.getJSONObject(0).getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                }

                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        path = getPath(ItemListActivity.this, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {

                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code").toString().trim(), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name").toString().trim(), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku").toString().trim(), jsonObject_bg1.getString("barcode").toString().trim(), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.insertItem_Location(database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                           } else {
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");

                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);
                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                    } else {
                        item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.updateItem("item_code=?", new String[]{item_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            String loc_id = jsonObject_item_location.getString("location_id");
                            String itm_lc_id = jsonObject_item_location.getString("item_location_id");
                            item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
                            if (item_location == null) {
                                item_location = new Item_Location(getApplicationContext(), null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.insertItem_Location(database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            } else {
                                item_location = new Item_Location(getApplicationContext(), item_location.get_item_location_id(), jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.updateItem_Location("item_code=? And item_location_id=? ", new String[]{item_code, item_location.get_item_location_id()}, database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");
                        // item_supplier = Item_Supplier.getItem_Supplier(getApplicationContext(), "WHERE item_code ='" + item_code + "' and  contact_code = '"+ jsonObject_item_supplier.getString("contact_code").toString() +"'", database);
                        long l3 = Item_Supplier.delete_Item_Supplier(getApplicationContext(), "item_code =?", new String[]{item_code}, database);

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            // Here wee will process loop according o json data For example : 2
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            //getting item supplier infor from item code two records
                            item_supplier = new Item_Supplier(getApplicationContext(), null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        long ll = Item_Group_Tax.delete_Item_Group_Tax(getApplicationContext(), "item_group_code =?", new String[]{item_code}, database);
                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");
                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);

                            item_group_tax = new Item_Group_Tax(getApplicationContext(), jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        long lm = ReceipeModifier.deleteReceipemodifier(getApplicationContext(),"Receipe_Modifier", "item_code =?", new String[]{item_code}, database);
                        JSONArray json_item_modifier = jsonObject_bg1.getJSONArray("item_modifier");
                        for (int k = 0; k < json_item_modifier.length(); k++) {
                            JSONObject jsonObject_item_modifier = json_item_modifier.getJSONObject(k);

                            receipemodifier = new ReceipeModifier(getApplicationContext(), null,jsonObject_item_modifier.getString("item_code"), jsonObject_item_modifier.getString("modifier_code"));
                            long itmsp = receipemodifier.insertReceipemodifier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }
                    }
                }
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmsg;


            }
            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e
        ) {
           succ_bg = "2";
            database.endTransaction();
        }

        return succ_bg;
    }

   /* private String get_item_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "item");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.license_id));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    public void get_item_from_server(final String datetime,final String serial_no,final String android_id,final String myKey) {

        pDialog.dismiss();
        pDialog = new ProgressDialog(ItemListActivity.this);
        pDialog.setMessage(getString(R.string.GettingData));
        pDialog.show();
        String server_url = Globals.App_IP_URL + "item";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getitem(response);

                            switch (result) {
                                case "1":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            getItemList("");
                                            Toast.makeText(getApplicationContext(), R.string.item_dwnld, Toast.LENGTH_SHORT).show();
                                          //getItemGroupCode(pDialog);

                                        }
                                    });

                                    break;

                                case "2":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            getItemList("");
                                            Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case "3":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (Globals.responsemessage.equals("Device Not Found")) {
pDialog.dismiss();
                                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                                                lite_pos_device.setStatus("Out");
                                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                if (ct > 0) {
                                                    database.endTransaction();
                                                    Intent intent_category = new Intent(ItemListActivity.this, LoginActivity.class);
                                                    startActivity(intent_category);
                                                    finish();
                                                }


                                            }
                                        }
                                    });
                                    break;


                                default:
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), R.string.item_not_fnd, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                        } catch (Exception e) {
                        }

                           pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        //pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("location_id", Globals.objLPD.getLocation_Code());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPR.getLicense_No());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DECODE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    public String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
  /*      pDialog = new ProgressDialog(ItemListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/
        if(Globals.objLPR.getIndustry_Type().equals("4")){

            Intent intent = new Intent(ItemListActivity.this, ParkingIndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //  pDialog.dismiss();
            finish();
        }
        else if(Globals.objLPR.getIndustry_Type().equals("2")){

            Intent intent = new Intent(ItemListActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //  pDialog.dismiss();
            finish();
        }
        else {
            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                try {
                    Intent intent = new Intent(ItemListActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //  pDialog.dismiss();
                    finish();
                } finally {
                }
            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                try {
                    Intent intent = new Intent(ItemListActivity.this, RetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else {
                try {
                    Intent intent = new Intent(ItemListActivity.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        }
            /*}
        };
        timerThread.start();*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {

            case 7:

                if (resultCode == RESULT_OK) {
                    //   String displayName = null;
                    PathHolder = data.getData().getPath();
                    String fullPath = "";
                    try {
                        fullPath = Commons.getPath(data.getData(), getApplicationContext());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }


                    succ_import = "0";
                    pDialog = new ProgressDialog(ItemListActivity.this);
                    pDialog.setTitle("");
                    pDialog.setMessage(getString(R.string.Importing_item_csv));
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final String finalFullPath = fullPath;
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    String loc;
                                    try {
                                        loc = Globals.objLPD.getLocation_Code();
                                    } catch (Exception ex) {
                                        loc = "1";
                                    }
                                    database.beginTransaction();
                                    String modified_by = Globals.user;
                                    sleep(200);
                                    File myFile = new File(finalFullPath);
                                    Reader reader  = new InputStreamReader(new FileInputStream(myFile),"UTF-8");
                                    BufferedReader myReader = new BufferedReader(reader);
                                    ContentValues contentValues = new ContentValues();
                                    String aDataRow = "";
                                    String aBuffer = "";
//                                                            db.executeDML("delete from item", database);
//                                                            db.executeDML("delete from item_location", database);
//                                                            db.executeDML("delete from item_group_tax", database);
                                    int count = 0;
                                    succ_import = "1";
                                    while ((aDataRow = myReader.readLine()) != null && succ_import.equals("1")) {
                                        if (count == 0) {


                                            count = 1;
                                        } else {
                                            String[] str = aDataRow.split(",", 14);  // defining 3 columns with null or blank field //values acceptance

                                            //Id, Company,Name,Price
                                            String item_code = str[0].toString();
                                            String item_group_name = str[1].toString();
                                            // public String manufacture_code;
                                            String item_name = str[2].toString();

                                            String description = str[3].toString();

                                            String sku = str[4].toString();

                                            String barcode = str[5].toString();

                                            String unit_name = str[6].toString();

                                            String hsn_sac_code = str[7].toString();

                                            String is_inclusive_tax = str[8].toString();

                                            String cost_price = str[9].toString();

                                            //String sales_price_without_tax = str[10].toString().replace("\"","");

                                            String sales_price_with_tax = str[10].toString();

                                            String tax1 = str[11].toString();

                                            String tax2 = str[12].toString();
                                            String categoryIp = str[13].toString();
                                           // String is_modifier = str[14].toString();
                                           // .replace("\"", "")
                                            Double dPerValue = 100.00;
                                            Double dFixValue = 0.0;


                                         //   if((!Globals.objLPR.getCountry_Id().equals("114")) || (!Globals.objLPR.getCountry_Id().equals("221"))) {
                                                if (!tax1.equals("0")) {
                                                    //find tax exists or not

                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax1 + "'", database, db);

                                                    if (tax_master == null) {

                                                        succ_import = "0";
                                                    } else {
                                                        // Add Code if fixed or %
                                                        // if % then add in dPerValue = dPerValue + value
                                                        // if fixed add in dFixValue =
                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            dPerValue = dPerValue + Double.parseDouble(tax_master.get_rate());
                                                        } else {
                                                            dFixValue = dFixValue + (Double.parseDouble(tax_master.get_rate()) * 100);
                                                        }

                                                        succ_import = "1";
                                                    }

                                                }
                                                if (succ_import.equals("1")) {


                                                    if (!tax2.equals("0")) {
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax2 + "'", database, db);
                                                        if (tax_master == null) {
                                                            succ_import = "0";
                                                        } else {
                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                dPerValue = dPerValue + Double.parseDouble(tax_master.get_rate());
                                                            } else {
                                                                dFixValue = dFixValue + (Double.parseDouble(tax_master.get_rate()) * 100);
                                                            }
                                                            succ_import = "1";
                                                        }
                                                    }
                                                }
                                            //}
                                            String sales_price_without_tax = str[10].toString().replace("\"", "");
                                            if (is_inclusive_tax.equals("1")) {
                                                Double dWithoutTax =  ((Double.parseDouble(sales_price_without_tax)*100) - dFixValue)/dPerValue;
                                                sales_price_without_tax = dWithoutTax +"";

                                            }
                                            //str[10].toString().replace("\"", "");

                                            if (succ_import.equals("1")) {

                                                Unit unit = Unit.getUnit(getApplicationContext(), database, db, "WHERE name = '" + unit_name + "'");
                                                if (unit == null) {
                                                    unit = new Unit(getApplicationContext(), null, unit_name, unit_name, unit_name, "1", modified_by, date, "N");
                                                    long l = unit.insertUnit(database);
                                                    if (l > 0) {
                                                        unit = Unit.getUnit(getApplicationContext(), database, db, "WHERE name = '" + unit_name + "'");
                                                        unitId = unit.get_unit_id();
                                                        succ_import = "1";
                                                    } else {
                                                        succ_import = "0";
                                                    }


                                                } else {
                                                    unitId = unit.get_unit_id();
                                                    succ_import = "1";
                                                }
                                            }
                                            if (succ_import.equals("1")) {

                                                Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_name ='" + item_group_name + "'");
                                                if (item_group == null) {

                                                    Item_Group objIG1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "  order By item_group_id Desc LIMIT 1");
                                                    String strIGCode = "";
                                                    if (objIG1 == null) {
                                                        strIGCode = Globals.objLPD.getDevice_Symbol() + "IG-" + 1;
                                                    } else {
                                                        strIGCode = Globals.objLPD.getDevice_Symbol() + "IG-" + (Integer.parseInt(objIG1.get_item_group_id()) + 1);
                                                    }


                                                    item_group = new Item_Group(getApplicationContext(), null,
                                                            Globals.Device_Code, strIGCode, "0", item_group_name, "0",
                                                            "1", modified_by, date, "N",categoryIp);
                                                    long l = item_group.insertItem_Group(database);

                                                    if (l > 0) {
                                                        item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_name ='" + item_group_name + "'");

                                                        item_Groupcode = item_group.get_item_group_code();
                                                        succ_import = "1";
                                                    } else {
                                                        succ_import = "0";
                                                    }
                                                } else {
                                                    item_Groupcode = item_group.get_item_group_code();
                                                    succ_import = "1";
                                                }
                                            }
                                            //Unit Id  , Item Group Code
                                            // Get Code and set  if not exists then insert records nd get


                                            //  contentValues.put("device_code", Globals.Device_Code);

                                            if (succ_import.equals("1")) {
                                                contentValues.put("item_code", item_code);
                                                contentValues.put("parent_code", "0");
                                                contentValues.put("item_group_code", item_Groupcode);
                                                contentValues.put("manufacture_code", "");
                                                contentValues.put("item_name", item_name);
                                                contentValues.put("description", description);
                                                contentValues.put("sku", sku);
                                                contentValues.put("barcode", barcode);
                                                contentValues.put("hsn_sac_code", hsn_sac_code);
                                                contentValues.put("image", "0");
                                                contentValues.put("item_type", "Services");
                                                contentValues.put("unit_id", unitId);
                                                contentValues.put("is_return_stockable", "0");
                                                contentValues.put("is_service", "0");
                                                contentValues.put("is_modifier", "0");
                                                contentValues.put("is_active", "1");
                                                contentValues.put("modified_by", modified_by);
                                                contentValues.put("modified_date", date);
                                                contentValues.put("is_push", "N");
                                                contentValues.put("is_inclusive_tax", is_inclusive_tax);
                                                Item item = Item.getItem(getApplicationContext(), "where item_code = '" + item_code + "'", database, db);

                                                if (item == null) {


                                                    long l = 0;
                                                    // Check item name  and Code and barcode unique or not
                                                    Item objIT1=null;
                                                    try {
                                                        objIT1 = Item.getItem(getApplicationContext(), "where item_name='" + item_name.toString() + "'", database, db);



                                                    } catch (Exception e) {

                                                    }


                                                    Item objIT2=null;

                                                    if(item_code.length() > 0) {
                                                        try {
                                                            objIT2 = Item.getItem(getApplicationContext(), "where   ( item_code='" + item_code.toString() + "' or barcode  = '"+ item_code.toString()   +"')", database, db);


                                                        } catch (Exception e) {

                                                        }
                                                    }


                                                    Item objIT3=null;

                                                    if(barcode.length() > 0 && !(barcode.equals("")&& !(barcode.equals("0")) ) ) {
                                                        try {
                                                            objIT3 = Item.getItem(getApplicationContext(), "where  (barcode='" + barcode.toString() + "' or  item_code  = '"+ barcode.toString()    +"')", database, db);


                                                        } catch (Exception e) {

                                                        }
                                                    }

                                                    if (objIT1!=null) {


                                                    }else if(objIT2 != null)
                                                    {

                                                    }
                                                    else if(objIT3 != null)
                                                    {

                                                    } else {
                                                       // database.execSQL("DROP TABLE IF EXISTS "+"item");
                                                        l = database.insert("item", null, contentValues);
                                                    }


                                                    if (l > 0) {
                                                        Log.e("data", l + "");
                                                        succ_import = "1";
                                                    } else {
                                                        succ_import = "0";
                                                    }

                                                    if (succ_import.equals("1")) {
                                                        ContentValues contentValues1 = new ContentValues();
                                                        contentValues1.put("location_id", loc);
                                                        contentValues1.put("item_code", item_code);
                                                        contentValues1.put("cost_price", cost_price);
                                                        contentValues1.put("markup", "0");
                                                        contentValues1.put("selling_price", sales_price_without_tax);
                                                        contentValues1.put("quantity", "0");
                                                        contentValues1.put("loyalty_point", "0");
                                                        contentValues1.put("reorder_point", "0");
                                                        contentValues1.put("reorder_amount", "0");
                                                        contentValues1.put("is_inventory_tracking", "0");
                                                        contentValues1.put("is_active", "1");
                                                        contentValues1.put("modified_by", modified_by);
                                                        contentValues1.put("modified_date", date);
                                                        contentValues1.put("new_sell_price", sales_price_with_tax);

                                                        // You can check if find delete then insert

                                                        long l1 = database.insert("item_location", null, contentValues1);
                                                        if (l1 > 0) {
                                                            succ_import = "1";
                                                        } else {
                                                            succ_import = "0";
                                                        }
                                                    }

                                                    if (succ_import.equals("1")) {
                                                        if (!tax1.equals("0")) {


                                                            ContentValues contentValues2 = new ContentValues();
                                                            contentValues2.put("location_id", loc);
                                                            contentValues2.put("tax_id", tax1);
                                                            contentValues2.put("item_group_code", item_code);
                                                            long l2 = database.insert("item_group_tax", null, contentValues2);
                                                            if (l2 > 0) {
                                                                succ_import = "1";
                                                            } else {
                                                                succ_import = "0";
                                                            }

                                                        }
                                                    }
                                                    if (succ_import.equals("1")) {
                                                        if (!tax2.equals("0")) {


                                                            ContentValues contentValues2 = new ContentValues();
                                                            contentValues2.put("location_id", loc);
                                                            contentValues2.put("tax_id", tax2);
                                                            contentValues2.put("item_group_code", item_code);
                                                            long l2 = database.insert("item_group_tax", null, contentValues2);
                                                            if (l2 > 0) {
                                                                succ_import = "1";
                                                            } else {
                                                                succ_import = "0";
                                                            }

                                                        }
                                                    }
//                                                for (int i = 12; i < 18; i++) {
//                                                    if (str[i].toString().equals("0")) {
//                                                    } else {
//                                                        ContentValues contentValues2 = new ContentValues();
//                                                        contentValues2.put("location_id", loc);
//                                                        contentValues2.put("tax_id", str[i].toString());
//                                                        contentValues2.put("item_group_code", item_code);
//                                                        long l2 = database.insert("item_group_tax", null, contentValues2);
//                                                        if (l2 > 0) {
//                                                            succ_import = "1";
//                                                        } else {
//                                                        }
//                                                    }
//                                                }
                                                } else {

                                                    long l = 0;
                                                    // Check item name  and Code and barcode unique or not
                                                    Item objIT1=null;
                                                    try {
                                                        objIT1 = Item.getItem(getApplicationContext(), "where item_id != '"+ item.get_item_id().toString()  +"' and  item_name='" + item_name.toString() + "'", database, db);



                                                    } catch (Exception e) {

                                                    }


                                                    Item objIT2=null;

                                                    if(item_code.length() > 0) {
                                                        try {
                                                            objIT2 = Item.getItem(getApplicationContext(), "where item_id != '"+ item.get_item_id().toString()  +"' and  ( item_code='" + item_code.toString() + "' or barcode  = '"+ item_code.toString()   +"')", database, db);


                                                        } catch (Exception e) {

                                                        }
                                                    }


                                                    Item objIT3=null;

                                                    if(barcode.length() > 0 && !(barcode.equals("")&& !(barcode.equals("0")) ) ) {
                                                        try {
                                                            objIT3 = Item.getItem(getApplicationContext(), "where  item_id != '"+ item.get_item_id().toString()  +"' and (barcode='" + barcode.toString() + "' or  item_code  = '"+ barcode.toString()    +"')", database, db);


                                                        } catch (Exception e) {

                                                        }
                                                    }

                                                    if (objIT1!=null) {


                                                    }else if(objIT2 != null)
                                                    {

                                                    }
                                                    else if(objIT3 != null)
                                                    {

                                                    } else {
                                                        l =  database.update("item", contentValues, "item_code=?", new String[]{item_code});
                                                    }




                                                    if (l > 0) {
                                                        Log.e("data", l + "");
                                                        succ_import = "1";
                                                    } else {
                                                        succ_import = "0";
                                                    }
                                                    if (succ_import.equals("1")) {
                                                        ContentValues contentValues1 = new ContentValues();
                                                        contentValues1.put("location_id", loc);
                                                        contentValues1.put("item_code", item_code);
                                                        contentValues1.put("cost_price", cost_price);
                                                        contentValues1.put("markup", "0");
                                                        contentValues1.put("selling_price", sales_price_without_tax);
                                                        contentValues1.put("quantity", "0");
                                                        contentValues1.put("loyalty_point", "0");
                                                        contentValues1.put("reorder_point", "0");
                                                        contentValues1.put("reorder_amount", "0");
                                                        contentValues1.put("is_inventory_tracking", "0");
                                                        contentValues1.put("is_active", "1");
                                                        contentValues1.put("modified_by", modified_by);
                                                        contentValues1.put("modified_date", date);
                                                        contentValues1.put("new_sell_price", sales_price_with_tax);

                                                        db.executeDML("delete from item_location where item_code = '" + item_code + "'", database);

                                                        long l1 = database.insert("item_location", null, contentValues1);
                                                        if (l1 > 0) {
                                                            succ_import = "1";
                                                        } else {
                                                            succ_import = "0";
                                                        }
                                                    }

                                                    if (succ_import.equals("1")) {

                                                        db.executeDML("delete from item_group_tax where item_group_code = '" + item_code + "'", database);
                                                    }
                                                    if (succ_import.equals("1")) {
                                                        if (!tax1.equals("0")) {


                                                            ContentValues contentValues2 = new ContentValues();
                                                            contentValues2.put("location_id", loc);
                                                            contentValues2.put("tax_id", tax1);
                                                            contentValues2.put("item_group_code", item_code);
                                                            long l2 = database.insert("item_group_tax", null, contentValues2);
                                                            if (l2 > 0) {
                                                                succ_import = "1";
                                                            } else {
                                                                succ_import = "0";
                                                            }

                                                        }

                                                    }

                                                    if (succ_import.equals("1")) {
                                                        if (!tax2.equals("0")) {


                                                            ContentValues contentValues2 = new ContentValues();
                                                            contentValues2.put("location_id", loc);
                                                            contentValues2.put("tax_id", tax2);
                                                            contentValues2.put("item_group_code", item_code);
                                                            long l2 = database.insert("item_group_tax", null, contentValues2);
                                                            if (l2 > 0) {
                                                                succ_import = "1";
                                                            } else {
                                                                succ_import = "0";
                                                            }

                                                        }
                                                    }
//                                                for (int i = 12; i < 18; i++) {
//
//                                                    if (str[i].toString().equals("0")) {
//                                                    } else {
//                                                        ContentValues contentValues2 = new ContentValues();
//                                                        contentValues2.put("location_id", loc);
//                                                        contentValues2.put("tax_id", str[i].toString());
//                                                        contentValues2.put("item_group_code", item_code);
//                                                        long l2 = database.insert("item_group_tax", null, contentValues2);
//                                                        if (l1 > 0) {
//                                                            succ_import = "1";
//                                                        } else {
//                                                        }
//                                                    }
//                                                }
                                                }
                                            }
                                        }

                                    }
                                    myReader.close();

                                    if (succ_import.equals("1")) {
                                        database.setTransactionSuccessful();
                                        database.endTransaction();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                getItemList("");
                                                Toast.makeText(getBaseContext(), R.string.Item_Import_Successfully,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        database.endTransaction();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pDialog.dismiss();
                                                getItemList("");
                                                Toast.makeText(getBaseContext(), R.string.Item_notimport_successfully,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getBaseContext(), e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                database.endTransaction();
                                ex.printStackTrace();
                            }
                        }
                    };
                    t.start();
                   /*     }catch(Exception e){}
                    }*/
                }

                // Toast.makeText(getActivity(), PathHolder, Toast.LENGTH_LONG).show();

                // }
                break;

        }
    }

    private String export() {

        String strResult = "";


        SQLiteDatabase db1 = db.getWritableDatabase();
        String selectQuery = "SELECT item.item_code,item_group.item_group_name ,item_name,item.description as description,sku,barcode,unit.name as 'unit_name',hsn_sac_code,is_inclusive_tax,item_location.cost_price,item_location.new_sell_price as 'sales_price_with_tax', CASE (Select count(*) from item_group_tax where item_group_code = item.item_code) WHEN 1 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id LIMIT 1) WHEN 2 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id LIMIT 1) Else '0' END as 'tax1', CASE (Select count(*) from item_group_tax where item_group_code = item.item_code) WHEN 2 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id desc LIMIT 1) Else '0' END as 'tax2' ,item_group.categoryIp FROM item Join item_location on item_location.item_code = item.item_code Join item_group on item_group.item_group_code = item.item_group_code join unit on unit.unit_id = item.unit_id";

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "" + "item_export" + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            //SQLiteDatabase sqlite = db.getReadableDatabase();

            Cursor curCSV = db1.rawQuery(selectQuery, null);

            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {

                ArrayList<String> stringArrayList = new ArrayList<String>();
                int columncount = curCSV.getColumnCount();

                for (int i = 0; i < columncount; i++) {

                    stringArrayList.add(curCSV.getString(i));

                }
                //Which column you want to exprort
                String[] stringArray = stringArrayList.toArray(new String[stringArrayList.size()]);

                csvWrite.writeNext(stringArray);
            }
            csvWrite.close();
            curCSV.close();
            //csvWrite.close();
            curCSV.close();
            strResult = "success";

            //Toast.makeText(getApplicationContext(), getString(R.string.exportedcsv), Toast.LENGTH_SHORT).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

        }

        return strResult;

    }


    private String export_sample() {

        String strResult = "";


        //  item_code,item_group_code,manufacture_code,item_name,item_name_l,sku,barcode,unit_id,hsn_sac_code,is_inclusive_tax ";


        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "" + "item_export_sample" + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            //SQLiteDatabase sqlite = db.getReadableDatabase();

            List<ListSampleItem> list_sampleitem = new ArrayList<>();
            ListSampleItem listSampleItem = new ListSampleItem("item_code", "item_group_name", "item_name", "description", "sku", "barcode", "unit_name", "hsn_sac_code", "is_inclusive_tax", "cost_price",  "sales_price_with_tax", "tax1", "tax2","categoryIp");
            list_sampleitem.add((listSampleItem));

//            listSampleItem = new ListSampleItem("1001", "Juice", "Applie Juice", "عصير تفاح", "sku", "1001", "PCS", "hsncode", "0", "50",  "70", "0", "0","192.168.2.200:9100");
//            list_sampleitem.add((listSampleItem));
//            listSampleItem = new ListSampleItem("1002", "Juice", "Mango Juice", "description", "sku", "1002", "PCS", "hsncode", "0", "50",  "70", "0", "0","192.168.2.200:9100");
//            list_sampleitem.add((listSampleItem));
//            listSampleItem = new ListSampleItem("1003", "Shake", "Kitkat Shake", "عصير تفاح", "sku", "1003", "PCS", "hsncode", "0", "50", "70", "0", "0","192.168.2.200:9100");
//            list_sampleitem.add((listSampleItem));
//            listSampleItem = new ListSampleItem("1004", "Shake", "Choclate Shake", "description", "sku", "1004", "PCS", "hsncode", "0", "50",  "70", "0", "0","192.168.2.200:9100");
//            list_sampleitem.add((listSampleItem));
//            listSampleItem = new ListSampleItem("1005", "Shake", "Applie Shake", "description", "sku", "1005", "PCS", "hsncode", "0", "50",  "70", "0", "0","192.168.2.200:9100");
//            list_sampleitem.add((listSampleItem));

            Database db = new Database(ItemListActivity.this);
            SQLiteDatabase database = db.getReadableDatabase();
            String q="Select it.item_code,itgroup.item_group_name,it.item_name,it.description,it.sku,barcode,it.hsn_sac_code,it.is_inclusive_tax FROM item it  LEFT JOIN  item_group itgroup ON itgroup.item_group_code=it.item_group_code";
            Cursor cursor=database.rawQuery(q,null);
            String item_code,item_group_name, item_name,description, sku,barcode, unit_name, hsn_sac_code, is_inclusive_tax,cost_price,sales_price_with_tax,tax1,tax2,categoryIp;

            if((cursor != null) && (cursor.getCount() > 0))
                while (cursor.moveToNext())
                {
                    item_code=cursor.getString(0);
                    item_group_name=cursor.getString(1);
                    item_name=cursor.getString(2);
                    description=cursor.getString(3);
                    sku=cursor.getString(4);
                    barcode=cursor.getString(5);
                    unit_name=" ";
                    hsn_sac_code=cursor.getString(6);

                    if(cursor.getInt(7)>0)
                        is_inclusive_tax="true";
                    else
                        is_inclusive_tax="false";
                    cost_price=" ";
                    sales_price_with_tax=" ";
                    tax1=" ";
                    tax2=" ";
                    categoryIp=" ";

                    listSampleItem = new ListSampleItem(item_code,item_group_name, item_name,description, sku,barcode, unit_name, hsn_sac_code, is_inclusive_tax,cost_price,sales_price_with_tax,tax1,tax2,categoryIp);
                    list_sampleitem.add((listSampleItem));

                }


            int RowCount = 0;


            //  csvWrite.writeNext(curCSV.getColumnNames());
            while (RowCount < list_sampleitem.size()) {

                ListSampleItem listSampleItem1 = list_sampleitem.get(RowCount);

                ArrayList<String> stringArrayList = new ArrayList<String>();
                int columncount = 14;

                for (int i = 0; i < columncount; i++) {

                    switch (i) {
                        case 0:
                            stringArrayList.add(listSampleItem1.item_code);
                            break;
                        case 1:
                            stringArrayList.add(listSampleItem1.item_group_name);
                            break;
                        case 2:
                            stringArrayList.add(listSampleItem1.item_name);
                            break;
                        case 3:
                            stringArrayList.add(listSampleItem1.description);
                            break;
                        case 4:
                            stringArrayList.add(listSampleItem1.sku);
                            break;
                        case 5:
                            stringArrayList.add(listSampleItem1.barcode);
                            break;
                        case 6:
                            stringArrayList.add(listSampleItem1.unit_name);
                            break;
                        case 7:
                            stringArrayList.add(listSampleItem1.hsn_sac_code);
                            break;
                        case 8:
                            stringArrayList.add(listSampleItem1.is_inclusive_tax);
                            break;
                        case 9:
                            stringArrayList.add(listSampleItem1.cost_price);
                            break;

                        case 10:
                            stringArrayList.add(listSampleItem1.sales_price_with_tax);
                            break;
                        case 11:
                            stringArrayList.add(listSampleItem1.tax1);
                            break;
                        case 12:
                            stringArrayList.add(listSampleItem1.tax2);
                            break;
                        case 13:
                            stringArrayList.add(listSampleItem1.categoryIp);
                            break;
                    }


                }
                //Which column you want to exprort
                String[] stringArray = stringArrayList.toArray(new String[stringArrayList.size()]);

                csvWrite.writeNext(stringArray);
                RowCount++;
            }
            csvWrite.close();

            //csvWrite.close();

            strResult = "success";

            //Toast.makeText(getApplicationContext(), getString(R.string.exportedcsv), Toast.LENGTH_SHORT).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

        }

        return strResult;

    }

    private void getItemGroupCode(ProgressDialog pDialog) {
        SQLiteDatabase db1 = db.getWritableDatabase();

        String sql = "Select item_group_code,unit_id from item where item_group_code NOT In (Select item_group_code from item_group WHERE is_active='1' ) OR unit_id not IN  (Select unit_id from unit WHERE is_active='1')";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String itemgroup= cursor.getString(0);
                String unitid= cursor.getString(1);
                if(itemgroup!=null){
                    itemgroup_value=itemgroup;
                  pDialog.dismiss();
                    getItemList("");
                    try {
                        // result = send_online_item_group();
                        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");

                        if (sys_sycntime == null) {
                            getitemgrp_from_server("", serial_no, android_id, myKey);
                        } else {
                            getitemgrp_from_server(sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                        }
                        if(unitid!=null){
                            get_unit_from_server();
                        }

                    } catch (Exception ex) {
                    }
                }
                else{
                    pDialog.dismiss();
                    getItemList("");
                    Toast.makeText(getApplicationContext(), R.string.item_dwnld, Toast.LENGTH_SHORT).show();
                }
            } while (cursor.moveToNext());

        }
    }





    public  void getitemgrp_from_server(final String datetime,final String serial_no,final String android_id,final String myKey) {
        pDialog.dismiss();
        pDialog = new ProgressDialog(ItemListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Syncingh));
        pDialog.show();
        String server_url = Globals.App_IP_URL + "item_group";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getitemGroup(response);
                            if(result.equals("1"))
                            {
                                try {
                                    //   result = send_online_unit();
                                    get_unit_from_server();
                                } catch (Exception e) {

                                }

                                try {
                                    //  result = send_online_tax();
                                    get_tax_from_server();
                                } catch (Exception ex) {
                                }
                               Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
                                if (sys_sycntime == null) {
                                    get_item_from_server("",serial_no,android_id,myKey);
                                } else {
                                    get_item_from_server(sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                }

                            }else
                            {
                                pDialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ItemListActivity.this,""+response,Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }


                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // pDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", Globals.objLPR.getLicense_No());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getitemGroup(String serverData) {

        String succ_bg = "0";

        // Call get item group api here
        // System.out.println("get sync date"+ sys_sycntime.get_datetime());
        database.beginTransaction();

        // Api calling Function For sending key values


        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmessage= jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
            /*    String modify_date=jsonArray_bg.getJSONObject(0).getString("modified_date");
                String modifiedmax_date = null;
                if(!modify_date.isEmpty()){
                    modifiedmax_date=modify_date;

                }
                else{}*/
                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name ='item_group'");
                String maxmodifydate=jsonArray_bg.getJSONObject(0).getString("modified_date");

                if (sys_sycntime != null) {
                    sys_sycntime.set_datetime(maxmodifydate);
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                }
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code ='" + item_group_code + "'");


                    if (item_group == null) {
                        item_group = new Item_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(getApplicationContext(), item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            } else if(strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage=strmessage;


            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";

            database.endTransaction();
        }
        return succ_bg;
    }
    public void get_unit_from_server() {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "unit";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUnit(response);

                        } catch (Exception e) {
                        }

                        //pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        pDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_data","");
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getUnit(String serverData) {

        String succ_manu = "0";
        database.beginTransaction();

        try {

            final JSONObject jsonObject_unit = new JSONObject(serverData);
            final String strStatus = jsonObject_unit.getString("status");
            final String strResult = jsonObject_unit.getString("result");
            final JSONObject jsonObject = new JSONObject(strResult);
            if (strStatus.equals("true")) {

                JSONArray jsonArray_unit = jsonObject.getJSONArray("unit");
                for (int i = 0; i < jsonArray_unit.length(); i++) {
                    JSONObject jsonObjct_unit = jsonArray_unit.getJSONObject(i);
                    String unitId = jsonObjct_unit.getString("unit_id");

                    unit = Unit.getUnit(getApplicationContext(),database,db,"where unit_id='"+unitId+"'");
                    if (unit == null) {
                        unit = new Unit(getApplicationContext(), null,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.insertUnit(database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                    } else {
                        unit = new Unit(getApplicationContext(), unitId,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.updateUnit("unit_id=?", new String[]{unitId}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

    public void get_tax_from_server() {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "tax";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getTax(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getTax(String serverData) {
        String succ_manu = "0";
        database.beginTransaction();

        try {
            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");

            if (strStatus.equals("true")) {
                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id ='" + taxId + "'", database, db);
                    if (tax_master == null) {
                        tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.insertTax_Master(database);
                        if (l > 0) {
                            succ_manu = "1";
                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                            long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                            if (odrtx > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.insertTax_Detail(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
                            long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    } else {
                        tax_master = new Tax_Master(getApplicationContext(), taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.updateTax_Master("tax_id=? And location_id=?", new String[]{taxId, taxlocation}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            Order_Type_Tax order_type_tax5 = Order_Type_Tax.getOrder_Type_Tax(getApplicationContext(),"WHERE tax_id = '"+taxId+"' and order_type_id='"+jsonObject_od_typ_tax.getString("order_type_id")+"'",database);
                            if (order_type_tax5==null){
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }else {
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }

                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), jsonObject_tax_detail.getString("id"), jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.updateTax_Detail("tax_id=? And tax_type_id=?", new String[]{taxId, jsonObject_tax_detail.getString("tax_type_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
                            long odrtx1 = sys_tax_group.updateSys_Tax_Group("tax_id=? And tax_master_id=?", new String[]{taxId, jsonObject_tax_group.getString("tax_master_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }
}

class ListSampleItem {
    public String item_code;
    public String item_group_name;
    // public String manufacture_code;
    public String item_name;
    public String description;
    public String sku;
    public String barcode;
    public String unit_name;
    public String hsn_sac_code;
    public String is_inclusive_tax;
    public String cost_price;

    public String sales_price_with_tax;
    public String tax1;
    public String tax2;
    public String categoryIp;
//    public String tax3;

    public ListSampleItem(String itemcode, String itemgroupname, String itemname, String description, String sku, String barcode, String unitname, String hsncode, String incltax, String costprice, String sales_price_withtax, String t1, String t2,String category_Ip) {

        this.item_code = itemcode;
        this.item_group_name = itemgroupname;
        this.item_name = itemname;
        this.description = description;
        this.sku = sku;
        this.barcode = barcode;
        this.unit_name = unitname;
        this.hsn_sac_code = hsncode;
        this.is_inclusive_tax = incltax;
        this.cost_price = costprice;

        this.sales_price_with_tax = sales_price_withtax;
        this.tax1 = t1;
        this.tax2 = t2;
        this.categoryIp = category_Ip;
    }


}

