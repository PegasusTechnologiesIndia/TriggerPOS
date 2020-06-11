package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.itextpdf.text.pdf.codec.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.phomellolitepos.Adapter.ItemAdapter;
import org.phomellolitepos.Adapter.MainItemListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;

import au.com.bytecode.opencsv.CSVWriter;
import in.gauriinfotech.commons.Commons;

public class ItemListActivity extends AppCompatActivity {
    EditText edt_toolbar_item_list;
    TextView item_title;
    Item_Supplier item_supplier;
    Item_Location item_location;
    ArrayList<Item> arrayList;
    ItemAdapter itemAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    String date, succ_import;
    Item item;
    Item_Group_Tax item_group_tax;
    Orders orders;
    Settings settings;
    private RecyclerView recyclerView;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    String PathHolder;
    String unitId, item_Groupcode;

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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        orders = Orders.getOrders(getApplicationContext(), database, " where z_code ='0'");
        settings = Settings.getSettings(getApplicationContext(), database, "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ItemListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else if (settings.get_Home_Layout().equals("2")) {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ItemListActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        item_title = (TextView) findViewById(R.id.item_title);
        edt_toolbar_item_list = (EditText) findViewById(R.id.edt_toolbar_item_list);

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_intent = new Intent(ItemListActivity.this, ItemActivity.class);
                startActivity(item_intent);
                finish();
            }
        });
        // Gettting item list here
        getItemList("");
    }


    private void getItemList(String strFilter) {
        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(item_name) asc limit " + Globals.ListLimit + "", database);
        if (arrayList.size() > 0) {
            itemAdapter = new ItemAdapter(ItemListActivity.this, arrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            item_title.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Litepos Item CSV");
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
                                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phomello Litepos Item Sample CSV");
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

/*            alertDialog.setNegativeButton(R.string.Location_Stock_Sync,
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

                            alertDialog.setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            if (orders == null) {
                                                if (isNetworkStatusAvialable(getApplicationContext())) {
                                                    pDialog = new ProgressDialog(ItemListActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.Downloading_data));
                                                    pDialog.show();
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            // Getting items form server
                                                            String suss = getLocationStock();
                                                            pDialog.dismiss();
                                                            // send items on server
                                                            switch (suss) {
                                                                case "1":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            getItemList("");
                                                                            Toast.makeText(getApplicationContext(), R.string.data_dwld_sufly, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    break;

                                                                case "2":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            getItemList("");
                                                                            Toast.makeText(getApplicationContext(), Globals.ErrorMsg, Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                                    break;
                                                                default:
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {

                                                                            Toast.makeText(getApplicationContext(), R.string.No_Data_Found, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    break;
                                                            }
                                                        }
                                                    }.start();

                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(getApplicationContext(), "Post all orders on server first", Toast.LENGTH_SHORT).show();
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
                    });*/

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

                            alertDialog.setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            if (orders == null) {
                                                if (isNetworkStatusAvialable(getApplicationContext())) {
                                                    pDialog = new ProgressDialog(ItemListActivity.this);
                                                    pDialog.setCancelable(false);
                                                    pDialog.setMessage(getString(R.string.dwnld_item));
                                                    pDialog.show();
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            // Getting items form server
                                                            String result = send_online_item();
                                                            String suss = getitem();
                                                            pDialog.dismiss();
                                                            // send items on server
                                                            switch (suss) {
                                                                case "1":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            getItemList("");
                                                                            Toast.makeText(getApplicationContext(), R.string.item_dwnld, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    break;

                                                                case "2":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            getItemList("");
                                                                            Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    break;
                                                                case "3":
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                            if (Globals.responsemessage.equals("Device Not Found")) {

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

                                                        }
                                                    }.start();

                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(getApplicationContext(), "Post all orders on server first", Toast.LENGTH_SHORT).show();
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

/*            alertDialog.setNeutralButton(R.string.Import_CSV,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    ItemListActivity.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.Imprt_csv_dilog_msg);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);


                            alertDialog.setNegativeButton(R.string.Cancel,

                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {


                                        }
                                    });

                            alertDialog.setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            succ_import = "0";
                                            pDialog = new ProgressDialog(ItemListActivity.this);
                                            pDialog.setTitle("");
                                            pDialog.setMessage(getString(R.string.Importing_item_csv));
                                            pDialog.setCancelable(false);
                                            pDialog.show();
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
                                                            File myFile = new File("/sdcard/item.csv");
                                                            FileReader file = new FileReader(myFile);
                                                            BufferedReader myReader = new BufferedReader(file);
                                                            ContentValues contentValues = new ContentValues();
                                                            String aDataRow = "";
                                                            String aBuffer = "";
//                                                            db.executeDML("delete from item", database);
//                                                            db.executeDML("delete from item_location", database);
//                                                            db.executeDML("delete from item_group_tax", database);
                                                            int count = 0;
                                                            while ((aDataRow = myReader.readLine()) != null) {
                                                                if (count == 0) {
                                                                    count = 1;
                                                                } else {
                                                                    String[] str = aDataRow.split(",", 18);  // defining 3 columns with null or blank field //values acceptance

                                                                    //Id, Company,Name,Price

                                                                    String item_code = str[0].toString();
                                                                    String item_group_code = str[1].toString();
                                                                    String manufacture_code = str[2].toString();
                                                                    String item_name = str[3].toString();
                                                                    String description = str[4].toString();
                                                                    String sku = str[5].toString();
                                                                    String barcode = str[6].toString();
                                                                    String cost_price = str[7].toString();
                                                                    String sell_price = str[8].toString();
                                                                    String unit_id = str[9].toString();
                                                                    String item_type = str[10].toString();
                                                                    String hsn_sac_code = str[11].toString();

                                                                    contentValues.put("device_code", Globals.Device_Code);
                                                                    contentValues.put("item_code", item_code);
                                                                    contentValues.put("parent_code", "0");
                                                                    contentValues.put("item_group_code", item_group_code);
                                                                    contentValues.put("manufacture_code", manufacture_code);
                                                                    contentValues.put("item_name", item_name);
                                                                    contentValues.put("description", description);
                                                                    contentValues.put("sku", sku);
                                                                    contentValues.put("barcode", barcode);
                                                                    contentValues.put("hsn_sac_code", hsn_sac_code);
                                                                    contentValues.put("image", "0");
                                                                    contentValues.put("item_type", item_type);
                                                                    contentValues.put("unit_id", unit_id);
                                                                    contentValues.put("is_return_stockable", "0");
                                                                    contentValues.put("is_service", "0");
                                                                    contentValues.put("is_active", "1");
                                                                    contentValues.put("modified_by", modified_by);
                                                                    contentValues.put("modified_date", date);
                                                                    contentValues.put("is_push", "N");
                                                                    contentValues.put("is_inclusive_tax", "0");
                                                                    Item item = Item.getItem(getApplicationContext(), "where item_code = '" + item_code + "'", database, db);
                                                                    if (item == null) {
                                                                        long l = database.insert("item", null, contentValues);
                                                                        if (l > 0) {
                                                                            Log.e("data", l + "");
                                                                            succ_import = "1";
                                                                        }

                                                                        ContentValues contentValues1 = new ContentValues();
                                                                        contentValues1.put("location_id", loc);
                                                                        contentValues1.put("item_code", item_code);
                                                                        contentValues1.put("cost_price", cost_price);
                                                                        contentValues1.put("markup", "0");
                                                                        contentValues1.put("selling_price", sell_price);
                                                                        contentValues1.put("quantity", "0");
                                                                        contentValues1.put("loyalty_point", "0");
                                                                        contentValues1.put("reorder_point", "0");
                                                                        contentValues1.put("reorder_amount", "0");
                                                                        contentValues1.put("is_inventory_tracking", "0");
                                                                        contentValues1.put("is_active", "1");
                                                                        contentValues1.put("modified_by", modified_by);
                                                                        contentValues1.put("modified_date", date);
                                                                        contentValues1.put("new_sell_price", sell_price);

                                                                        long l1 = database.insert("item_location", null, contentValues1);
                                                                        if (l1 > 0) {
                                                                            succ_import = "1";
                                                                        }

                                                                        for (int i = 12; i < 18; i++) {
                                                                            if (str[i].toString().equals("0")) {
                                                                            } else {
                                                                                ContentValues contentValues2 = new ContentValues();
                                                                                contentValues2.put("location_id", loc);
                                                                                contentValues2.put("tax_id", str[i].toString());
                                                                                contentValues2.put("item_group_code", item_code);
                                                                                long l2 = database.insert("item_group_tax", null, contentValues2);
                                                                                if (l2 > 0) {
                                                                                    succ_import = "1";
                                                                                } else {
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        long l = database.update("item", contentValues, "item_code=?", new String[]{item_code});
                                                                        if (l > 0) {
                                                                            Log.e("data", l + "");
                                                                            succ_import = "1";
                                                                        }

                                                                        ContentValues contentValues1 = new ContentValues();
                                                                        contentValues1.put("location_id", loc);
                                                                        contentValues1.put("item_code", item_code);
                                                                        contentValues1.put("cost_price", cost_price);
                                                                        contentValues1.put("markup", "0");
                                                                        contentValues1.put("selling_price", sell_price);
                                                                        contentValues1.put("quantity", "0");
                                                                        contentValues1.put("loyalty_point", "0");
                                                                        contentValues1.put("reorder_point", "0");
                                                                        contentValues1.put("reorder_amount", "0");
                                                                        contentValues1.put("is_inventory_tracking", "0");
                                                                        contentValues1.put("is_active", "1");
                                                                        contentValues1.put("modified_by", modified_by);
                                                                        contentValues1.put("modified_date", date);
                                                                        contentValues1.put("new_sell_price", sell_price);

                                                                        db.executeDML("delete from item_location where item_code = '" + item_code + "'", database);

                                                                        long l1 = database.insert("item_location", null, contentValues1);
                                                                        if (l1 > 0) {
                                                                            succ_import = "1";
                                                                        }

                                                                        db.executeDML("delete from item_group_tax where item_group_code = '" + item_code + "'", database);
                                                                        for (int i = 12; i < 18; i++) {

                                                                            if (str[i].toString().equals("0")) {
                                                                            } else {
                                                                                ContentValues contentValues2 = new ContentValues();
                                                                                contentValues2.put("location_id", loc);
                                                                                contentValues2.put("tax_id", str[i].toString());
                                                                                contentValues2.put("item_group_code", item_code);
                                                                                long l2 = database.insert("item_group_tax", null, contentValues2);
                                                                                if (l1 > 0) {
                                                                                    succ_import = "1";
                                                                                } else {
                                                                                }
                                                                            }
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
                                        }
                                    });

                            AlertDialog alert = alertDialog.create();
                            alert.show();

                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });*/

            AlertDialog alert = alertDialog.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = lite_pos_registration.getproject_id();

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

    private String getLocationStock() {
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
    }

    private String get_loc_stock_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item/location");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
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
    }

    private String send_online_item() {
        Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String result = Item.sendOnServer(getApplicationContext(), database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax FROM item  WHERE is_push = 'N'", liccustomerid);
        return result;
    }

    private String getitem() {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();
        if (sys_sycntime == null) {
            serverData = get_item_from_server("");
        } else {
            serverData = get_item_from_server(sys_sycntime.get_datetime());
        }

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmsg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    item = Item.getItem(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database, db);
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    }

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        path = getPath(ItemListActivity.this, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {

                        item = new Item(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), path);
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
                        item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), path);
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

    private String get_item_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_data", datetime));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
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
        pDialog = new ProgressDialog(ItemListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ItemListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else if (settings.get_Home_Layout().equals("2")) {
                    try {
                        Intent intent = new Intent(ItemListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(ItemListActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                                    FileReader file = new FileReader(myFile);
                                    BufferedReader myReader = new BufferedReader(file);
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
                                            String[] str = aDataRow.split(",", 18);  // defining 3 columns with null or blank field //values acceptance

                                            //Id, Company,Name,Price
                                            String item_code = str[0].toString().replace("\"", "");
                                            String item_group_name = str[1].toString().replace("\"", "");
                                            // public String manufacture_code;
                                            String item_name = str[2].toString().replace("\"", "");

                                            String description = str[3].toString().replace("\"", "");

                                            String sku = str[4].toString().replace("\"", "");

                                            String barcode = str[5].toString().replace("\"", "");

                                            String unit_name = str[6].toString().replace("\"", "");

                                            String hsn_sac_code = str[7].toString().replace("\"", "");

                                            String is_inclusive_tax = str[8].toString().replace("\"", "");

                                            String cost_price = str[9].toString().replace("\"", "");

                                            //String sales_price_without_tax = str[10].toString().replace("\"","");

                                            String sales_price_with_tax = str[10].toString().replace("\"", "");

                                            String tax1 = str[11].toString().replace("\"", "");

                                            String tax2 = str[12].toString().replace("\"", "");

                                            Double dPerValue = 100.00;
                                            Double dFixValue = 0.0;


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
                                                            "1", modified_by, date, "N");
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
        String selectQuery = "SELECT item.item_code,item_group.item_group_name ,item_name,item.description as item_name_l,sku,barcode,unit.name as 'unit_name',hsn_sac_code,is_inclusive_tax,item_location.cost_price,item_location.new_sell_price as 'sales_price_with_tax', CASE (Select count(*) from item_group_tax where item_group_code = item.item_code) WHEN 1 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id LIMIT 1) WHEN 2 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id LIMIT 1) Else '0' END as 'tax1', CASE (Select count(*) from item_group_tax where item_group_code = item.item_code) WHEN 2 THEN (Select tax_id from item_group_tax where item_group_code = item.item_code order by tax_id desc LIMIT 1) Else '0' END as 'tax2' FROM item Join item_location on item_location.item_code = item.item_code Join item_group on item_group.item_group_code = item.item_group_code join unit on unit.unit_id = item.unit_id";

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
            ListSampleItem listSampleItem = new ListSampleItem("item_code", "item_group_name", "item_name", "description", "sku", "barcode", "unit_name", "hsn_sac_code", "is_inclusive_tax", "cost_price",  "sales_price_with_tax", "tax1", "tax2");
            list_sampleitem.add((listSampleItem));

            listSampleItem = new ListSampleItem("1001", "Juice", "Applie Juice", "description", "sku", "1001", "PCS", "hsncode", "0", "50",  "70", "0", "0");
            list_sampleitem.add((listSampleItem));
            listSampleItem = new ListSampleItem("1002", "Juice", "Mango Juice", "description", "sku", "1002", "PCS", "hsncode", "0", "50",  "70", "0", "0");
            list_sampleitem.add((listSampleItem));
            listSampleItem = new ListSampleItem("1003", "Shake", "Kitkat Shake", "description", "sku", "1003", "PCS", "hsncode", "0", "50", "70", "0", "0");
            list_sampleitem.add((listSampleItem));
            listSampleItem = new ListSampleItem("1004", "Shake", "Choclate Shake", "description", "sku", "1004", "PCS", "hsncode", "0", "50",  "70", "0", "0");
            list_sampleitem.add((listSampleItem));
            listSampleItem = new ListSampleItem("1005", "Shake", "Applie Shake", "description", "sku", "1005", "PCS", "hsncode", "0", "50",  "70", "0", "0");
            list_sampleitem.add((listSampleItem));


            int RowCount = 0;


            //  csvWrite.writeNext(curCSV.getColumnNames());
            while (RowCount < list_sampleitem.size()) {

                ListSampleItem listSampleItem1 = list_sampleitem.get(RowCount);

                ArrayList<String> stringArrayList = new ArrayList<String>();
                int columncount = 13;

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
//    public String tax3;

    public ListSampleItem(String itemcode, String itemgroupname, String itemname, String description, String sku, String barcode, String unitname, String hsncode, String incltax, String costprice, String sales_price_withtax, String t1, String t2) {

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
    }
}

