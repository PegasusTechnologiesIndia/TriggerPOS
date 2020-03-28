package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.itextpdf.text.pdf.codec.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Adapter.CustomAdapter;
import org.phomellolitepos.Adapter.ItemUnitListAdapter;
import org.phomellolitepos.Adapter.ManufactureSpiinerAdapter;
import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Purchase_Payment;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.utils.ImageCache;

public class ItemActivity extends AppCompatActivity {
    TextInputLayout edt_layout_item_name, edt_layout_item_code, edt_layout_item_cost_price, edt_layout_item_sku,
            edt_layout_item_barcode, edt_layout_item_description, edt_layout_item_sales_price, edt_layout_hsn_code;
    EditText edt_item_name, edt_price, edt_item_sku, edt_item_barcode, edt_item_description, edt_item_code,
            edt_item_cost_price, edt_item_sales_price, edt_hsn_code;
    Spinner spinner_item_category, spinner_manufacture, spinner_item_type, spinner_item_unit;
    Button btn_next, btn_item_delete;
    CheckBox ck_stockable, ck_service, ck_PIT;
    Item item;
    Item_Group item_group;
    Item_Location item_location;
    Manufacture manufacture;
    ArrayList<Item_Group> arrayList;
    ArrayList<Unit> arrayUnitList;
    ArrayList<Manufacture> arrayList_manufacture;
    String spn_code, spn_manufacture_code = "", operation, code, Item_Id, date, decimal_check, spn_unit_code = "";
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String Item_item_name = "", Item_code = "", Item_sales_price = "", Item_sku = "", Item_barcode = "", Item_hsn = "", Item_description = "", description = "", Item_cost_price = "";
    String strITCode = "", item_type;
    String[] itemType = {};
    ItemUnitListAdapter itemUnitListAdapter;
    ImageView img_logo, img_logo_add;
    private int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    Bitmap bitmap;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    String tick;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ItemActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            if (tick.equals("Ticket")) {
                                Intent intent = new Intent(ItemActivity.this, TicketActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } else {
                                Intent intent = new Intent(ItemActivity.this, ItemListActivity.class);
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

        itemType = getResources().getStringArray(R.array.item_type);

        Intent intent = getIntent();
        getSupportActionBar().setTitle(getString(R.string.Item));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("operation");
        tick = intent.getStringExtra("Ticket");
        if (operation == null) {
            operation = "Add";
            Item_Id = null;
        }

        if (tick == null) {
            tick = "";
        }

        edt_layout_item_name = (TextInputLayout) findViewById(R.id.edt_layout_item_name);
        edt_layout_item_code = (TextInputLayout) findViewById(R.id.edt_layout_item_code);
        edt_layout_item_cost_price = (TextInputLayout) findViewById(R.id.edt_layout_item_cost_price);
        edt_layout_item_sku = (TextInputLayout) findViewById(R.id.edt_layout_item_sku);
        edt_layout_item_barcode = (TextInputLayout) findViewById(R.id.edt_layout_item_barcode);
        edt_layout_item_description = (TextInputLayout) findViewById(R.id.edt_layout_item_description);
        edt_layout_item_sales_price = (TextInputLayout) findViewById(R.id.edt_layout_item_sales_price);
        edt_layout_hsn_code = (TextInputLayout) findViewById(R.id.edt_layout_hsn_code);
        edt_item_name = (EditText) findViewById(R.id.edt_item_name);
        edt_item_code = (EditText) findViewById(R.id.edt_item_code);
        edt_item_sales_price = (EditText) findViewById(R.id.edt_item_sales_price);
        edt_item_sku = (EditText) findViewById(R.id.edt_item_sku);
        edt_item_barcode = (EditText) findViewById(R.id.edt_item_barcode);
        edt_item_description = (EditText) findViewById(R.id.edt_item_description);
        edt_item_cost_price = (EditText) findViewById(R.id.edt_item_cost_price);
        edt_hsn_code = (EditText) findViewById(R.id.edt_hsn_code);
        spinner_item_category = (Spinner) findViewById(R.id.spinner_item_category);
        spinner_manufacture = (Spinner) findViewById(R.id.spinner_manufacture);
        spinner_item_type = (Spinner) findViewById(R.id.spinner_item_type);
        spinner_item_unit = (Spinner) findViewById(R.id.spinner_item_unit);
        ck_stockable = (CheckBox) findViewById(R.id.ck_stockable);
        ck_service = (CheckBox) findViewById(R.id.ck_service);
        ck_PIT = (CheckBox) findViewById(R.id.ck_PIT);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_item_delete = (Button) findViewById(R.id.btn_item_delete);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_logo_add = (ImageView) findViewById(R.id.img_logo_add);
        btn_next.setVisibility(View.GONE);
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        img_logo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        if (operation.equals("Edit")) {
            btn_item_delete.setVisibility(View.VISIBLE);
            item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + code + "'", database, db);
            item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code = '" + code + "'", database);
            edt_item_name.setText(item.get_item_name());
            edt_item_code.setText(item.get_item_code());
            edt_item_sku.setText(item.get_sku());
            edt_item_barcode.setText(item.get_barcode());
            edt_hsn_code.setText(item.get_hsn_sac_code());
            edt_item_description.setText(item.get_description());

            String cost_pri = "";
            try {
                cost_pri = item_location.get_cost_price();
                if (cost_pri.equals("null") || cost_pri.equals("")) {
                    cost_pri = "0";
                }
            } catch (Exception e) {
                cost_pri = "0";
            }

            cost_pri = Globals.myNumberFormat2Price(Double.parseDouble(cost_pri), decimal_check);
            edt_item_cost_price.setText(cost_pri);

            String sale_pri;
            try {
                sale_pri = item_location.get_new_sell_price();

                if (sale_pri.equals("null") || sale_pri.equals("")) {
                    sale_pri = item_location.get_selling_price();
                } else {
                    sale_pri = item_location.get_new_sell_price();
                }

            } catch (Exception e) {
                sale_pri = "0";
            }
            String salePri;
            try {
                salePri = Globals.myNumberFormat2Price(Double.parseDouble(sale_pri), decimal_check);
                edt_item_sales_price.setText(salePri);
            }catch (Exception ex){}


            Item_Id = item.get_item_id();

            if (item.get_is_return_stockable().equals("1")) {
                ck_stockable.setChecked(true);
            }

            if (item.get_is_inclusive_tax().equals("1")) {
                ck_PIT.setChecked(true);
            }

            if (item.get_is_service().equals("1")) {
                ck_service.setChecked(true);
            }
            item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + item.get_item_group_code() + "'");
            String compare_item_group_name;
            try {
                compare_item_group_name = item_group.get_item_group_name();
            } catch (Exception ex) {
                compare_item_group_name = "";
            }

            manufacture = Manufacture.getManufacture(getApplicationContext(), database, db, "WHERE manufacture_code = '" + item.get_manufacture_code() + "'");
            String compare_manufacture_name;
            try {
                compare_manufacture_name = manufacture.get_manufacture_name();
            } catch (Exception ex) {
                compare_manufacture_name = "";
            }

            String str = item.get_item_type();
            String chktype;
            if (str.equals("goods")) {
                chktype = "GOODS";
            } else {
                chktype = "SERVICE";
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemType);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_item_type.setAdapter(dataAdapter);
            if (!chktype.equals(null)) {
                int spinnerPosition = dataAdapter.getPosition(chktype);
                spinner_item_type.setSelection(spinnerPosition);
            }

            try {
                fill_spinner(compare_item_group_name);
                fill_spinner_manufacture(compare_manufacture_name);
                fill_spinner_unit(item.get_unit_id());
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
//                Bitmap bitmap = StringToBitMap(item.get_item_image());
                img_logo.setImageBitmap(ImageCache.decodeSampledBitmapFromResource(item.get_item_image(), 100, 100));
//                Globals.logo = bitmap;
                Globals.logo = item.get_item_image();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
            fill_spinner("");
            fill_spinner_manufacture("");
            fill_spinner_itemtype("");
            fill_spinner_unit("");
        }

        spinner_item_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spn_code = (String) spinner_item_category.getItemAtPosition(position).toString();
                Item_Group resultp = arrayList.get(position);
                spn_code = resultp.get_item_group_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_manufacture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spn_manufacture_code = (String) spinner_manufacture.getItemAtPosition(position).toString();
                Manufacture resultp = arrayList_manufacture.get(position);
                spn_manufacture_code = resultp.get_manufacture_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spinner_item_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Unit resultp = arrayUnitList.get(position);
                    spn_unit_code = resultp.get_unit_id();
                } catch (Exception ex) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_item_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    String pr_type_chk = String.valueOf(spinner_item_type.getItemAtPosition(i));

                    if (pr_type_chk.equals("GOODS")) {

                        item_type = "goods";
                    } else if (pr_type_chk.equals("SERVICE")) {
                        item_type = "services";

                    } else {
                        item_type = "Select";
                    }

                } catch (Exception ecx) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(
                        ItemActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage(R.string.do_you_want_to_delete);

                lp = new LinearLayout.LayoutParams(
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
                                pDialog = new ProgressDialog(ItemActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);

                                            database.beginTransaction();
                                            long l = Item_Supplier.delete_Item_Supplier(getApplicationContext(), "item_code=?", new String[]{code}, database);
                                            if (l > 0) {
                                                item.set_is_active("0");
                                                long it = item.updateItem("item_code=?", new String[]{code}, database);
                                                if (it > 0) {
                                                    database.setTransactionSuccessful();
                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(ItemActivity.this, ItemListActivity.class);
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


                alert = alertDialog.create();
                alert.show();

                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    private void save() {
        if (edt_item_code.getText().toString().equals("")) {
            Item objIT1 = Item.getItem(getApplicationContext(), "  order By item_id Desc LIMIT 1", database, db);

            if (objIT1 == null) {
                strITCode = "IT-" + 1;
            } else {
                strITCode = "IT-" + (Integer.parseInt(objIT1.get_item_id()) + 1);
            }
        } else {
            if (edt_item_code.getText().toString().contains(" ")) {
                edt_item_code.setError(getString(R.string.Cant_Enter_Space));
                edt_item_code.requestFocus();
                return;
            } else {
                strITCode = edt_item_code.getText().toString();
            }

        }

        if (edt_item_name.getText().toString().trim().equals("")) {
            edt_item_name.setError(getString(R.string.Item_name_is_required));
            edt_item_name.requestFocus();
            return;
        } else {
            Item_item_name = edt_item_name.getText().toString().trim();
        }

        Item_sku = edt_item_sku.getText().toString();
        Item_hsn = edt_hsn_code.getText().toString();

        if (edt_item_barcode.getText().toString().equals("")) {
            edt_item_barcode.setError(getString(R.string.Barcode_is_required));
            edt_item_barcode.requestFocus();
            return;
        } else {
            Item_barcode = edt_item_barcode.getText().toString();
        }

        if (edt_item_cost_price.getText().toString().equals("")) {
            edt_item_cost_price.setError(getString(R.string.Cost_price_is_required));
            edt_item_cost_price.requestFocus();
            return;
        } else {
            Item_cost_price = Globals.myNumberFormat2Price(Double.parseDouble(edt_item_cost_price.getText().toString()), decimal_check);
        }

        if (edt_item_sales_price.getText().toString().equals("")) {
            edt_item_sales_price.setError(getString(R.string.Sale_price_is_required));
            edt_item_sales_price.requestFocus();
            return;
        } else {
            Item_sales_price = Globals.myNumberFormat2Price(Double.parseDouble(edt_item_sales_price.getText().toString()), decimal_check);
        }


        final String strStockable;
        if (ck_stockable.isChecked()) {

            strStockable = "1";
        } else {
            strStockable = "0";
        }
        final String strService;
        if (ck_service.isChecked()) {

            strService = "1";
        } else {
            strService = "0";
        }

        final String strPIT;
        if (ck_PIT.isChecked()) {

            strPIT = "1";
        } else {
            strPIT = "0";
        }

        if (spn_manufacture_code.equals("")) {
            spn_manufacture_code = "0";
        }

        String logo = "";
        try {
            if (Globals.logo == null) {
                logo = "0";
            } else {
                logo = Globals.logo;
            }
        } catch (Exception ex) {
            logo = "0";
        }


        if (item_type.equals("Select")) {
            Toast.makeText(getApplicationContext(), R.string.select_item_type, Toast.LENGTH_SHORT).show();
        } else {
            pDialog = new ProgressDialog(ItemActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();

            final String finalLogo = logo;
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        Fill_Item(Item_Id, Item_item_name, strITCode, Item_sales_price, Item_sku, Item_barcode, Item_description,
                                Item_cost_price, spn_code, strStockable, strService, spn_manufacture_code, item_type, spn_unit_code, Item_hsn, strPIT, finalLogo);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {}
                }
            };
            timerThread.start();
        }
    }

    private void fill_spinner_unit(String str) {
        Unit unit;
        arrayUnitList = Unit.getAllUnit(getApplicationContext(), " WHERE is_active ='1' Order By name asc");
        if (str.equals("")) {
            unit = Unit.getUnit(getApplicationContext(), database, db, " WHERE is_active ='1'");
            if (arrayUnitList.size() > 0) {
                itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                spinner_item_unit.setAdapter(itemUnitListAdapter);
            }
        } else {
            unit = Unit.getUnit(getApplicationContext(), database, db, " WHERE is_active ='1' and unit_id = '" + str + "'");
            if (unit == null) {
                if (arrayUnitList.size() > 0) {
                    itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                    spinner_item_unit.setAdapter(itemUnitListAdapter);
                }
            } else {

                if (arrayUnitList.size() > 0) {
                    itemUnitListAdapter = new ItemUnitListAdapter(getApplicationContext(), arrayUnitList);
                    spinner_item_unit.setAdapter(itemUnitListAdapter);

                    if (!unit.get_name().toString().equals("")) {
                        for (int i = 0; i < itemUnitListAdapter.getCount(); i++) {
                            String iname = arrayUnitList.get(i).get_name();
                            if (unit.get_name().equals(iname)) {
                                spinner_item_unit.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void fill_spinner_itemtype(String str) {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemType);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item_type.setAdapter(dataAdapter1);
    }

    private void fill_spinner_manufacture(String compare_manufacture_name) {
        arrayList_manufacture = Manufacture.getAllManufactureSpinner_All(getApplicationContext(), " WHERE is_active ='1' Order By manufacture_name asc");
        ManufactureSpiinerAdapter manufactureSpiinerAdapter = new ManufactureSpiinerAdapter(getApplicationContext(), arrayList_manufacture);
        spinner_manufacture.setAdapter(manufactureSpiinerAdapter);
        if (!compare_manufacture_name.equals("")) {
            for (int i = 0; i < manufactureSpiinerAdapter.getCount(); i++) {
                int h = (int) spinner_manufacture.getAdapter().getItemId(i);
                String iname = arrayList_manufacture.get(h).get_manufacture_name();
                if (compare_manufacture_name.equals(iname)) {
                    spinner_manufacture.setSelection(i);
                    break;
                }
            }
        }
    }

    private void fill_spinner(String compare_item_group_name) {
        arrayList = Item_Group.getAllItem_GroupSpinner_All(getApplicationContext(), " WHERE is_active ='1' Order By item_group_name asc");
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), arrayList);
        spinner_item_category.setAdapter(customAdapter);

        if (!compare_item_group_name.equals("")) {
            for (int i = 0; i < customAdapter.getCount(); i++) {
                int h = (int) spinner_item_category.getAdapter().getItemId(i);
                String iname = arrayList.get(h).get_item_group_name();
                if (compare_item_group_name.equals(iname)) {
                    spinner_item_category.setSelection(i);
                    break;
                }
            }
        }
    }

    private void Fill_Item(String item_Id, String item_item_name, final String item_code, String item_sales_price, String item_sku, String item_barcode, String item_description, String item_cost_price, String spn_code, String strStockable, String strService, String spn_manufacture_code, String item_type, String spn_unit_code, String hsn_code, final String strPIT, String logo) {
        String modified_by = Globals.user;

        item = new Item(getApplicationContext(), item_Id, Globals.Device_Code, item_code, "0", spn_code, spn_manufacture_code,
                item_item_name, item_description, item_sku, item_barcode, hsn_code, logo, item_type, spn_unit_code, strStockable, strService, "1", modified_by, date, "N", strPIT, logo);

        if (operation.equals("Edit")) {
            database.beginTransaction();
            long l = item.updateItem("item_code=? And item_id =?", new String[]{code, item_Id}, database);
            if (l > 0) {
                String loc = "";
                try {
                    loc = Globals.objLPD.getLocation_Code();
                } catch (Exception e) {
                    loc = "1";
                }
                item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code ='" + code + "'", database);
                if (item_location == null) {
                    item_location = new Item_Location(getApplicationContext(), null, loc, code, item_cost_price, "0", item_sales_price, "0",
                            "0", "0", "0", "0", "1", modified_by, date, item_sales_price);

                    try {
                        item_location.insertItem_Location(database);
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (tick.equals("Ticket")) {
                                    Intent intent = new Intent(ItemActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } else {
                                    Intent intent_category = new Intent(ItemActivity.this, TaxCheckListActivity.class);
                                    intent_category.putExtra("item_code", item_code);
                                    intent_category.putExtra("PIT", strPIT);
                                    intent_category.putExtra("sprice", item_location.get_selling_price());
                                    intent_category.putExtra("operation", operation);
                                    startActivity(intent_category);
                                    finish();
                                }
                            }
                        });

                    } catch (Exception ex) {
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    String loc_id = item_location.get_item_location_id();
                    item_location = new Item_Location(getApplicationContext(), loc_id, loc, item_code, item_cost_price, "0", item_sales_price, item_location.get_quantity(),
                            "0", "0", "0", "0", "1", modified_by, date, item_sales_price);

                    try {
                        item_location.updateItem_Location("item_code=? And item_location_id=?", new String[]{item_code, loc_id}, database);
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (tick.equals("Ticket")) {
                                    Intent intent = new Intent(ItemActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } else {
                                    Intent intent_category = new Intent(ItemActivity.this, TaxCheckListActivity.class);
                                    intent_category.putExtra("item_code", item_code);
                                    intent_category.putExtra("PIT", strPIT);
                                    intent_category.putExtra("sprice", item_location.get_selling_price());
                                    intent_category.putExtra("operation", operation);
                                    intent_category.putExtra("Ticket", tick);
                                    startActivity(intent_category);
                                    finish();
                                }
                            }
                        });

                    } catch (Exception ex) {
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }

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
            database.beginTransaction();
            long l = item.insertItem(database);
            if (l > 0) {
                String loc;
                try {
                    loc = Globals.objLPD.getLocation_Code();
                } catch (Exception ex) {
                    loc = "1";
                }
                item_location = new Item_Location(getApplicationContext(), null, loc, item_code, item_cost_price, "0", item_sales_price, "0",
                        "0", "0", "0", "0", "1", modified_by, date, item_sales_price);
                try {
                    long r = item_location.insertItem_Location(database);

                    if (r > 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        pDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (tick.equals("Ticket")) {
                                    Intent intent = new Intent(ItemActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } else {
                                    Intent intent_category = new Intent(ItemActivity.this, TaxCheckListActivity.class);
                                    intent_category.putExtra("item_code", item_code);
                                    intent_category.putExtra("PIT", strPIT);
                                    intent_category.putExtra("sprice", item_location.get_selling_price());
                                    intent_category.putExtra("operation", operation);
                                    intent_category.putExtra("Ticket", tick);
                                    startActivity(intent_category);
                                    finish();
                                }
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

                } catch (Exception ex) {
                    pDialog.dismiss();
                    database.endTransaction();
                }

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

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }

        return false;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                String filePath = getPath(getApplicationContext(), uri);
                try {
                    File file = new File(filePath);
                    long length = file.length();
                    length = length / 1024;
                    if (length <= 10) {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        img_logo.setImageBitmap(bitmap);
                        Globals.logo = filePath;
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.File10KB, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                }


            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }


    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeBytes(b, Base64.ENCODE);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
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
        pDialog = new ProgressDialog(ItemActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    if (tick.equals("Ticket")) {
                        Intent intent = new Intent(ItemActivity.this, TicketActivity.class);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } else {
                        Intent intent = new Intent(ItemActivity.this, ItemListActivity.class);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(ItemActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.split_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
