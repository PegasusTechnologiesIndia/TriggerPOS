package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Adapter.CustomAdapter;
import org.phomellolitepos.Adapter.SpinnerAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ItemCategoryActivity extends AppCompatActivity {
    EditText edt_item_ct_name, edt_item_ct_code;
    TextInputLayout edt_layout_item_ct_name, edt_layout_item_ct_code;
    Spinner spn_parent_category;
    Button btn_save, btn_delete;
    Item_Group item_group;
    String operation, code;
    Settings settings;
    String JsonString;
    Database db;
    SQLiteDatabase database;
    String date;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String Item_category_name = "";
    String strIGCode = "";
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    ArrayList<Item_Group> arrayList;
    ArrayList<String> arrayList_custom;
    String spn_parent_code="0";
    String flag = "0";
    private boolean isSpinnerInitial = true;
    MenuItem menuItem;
    Lite_POS_Device liteposdevice;
    String liccustomerid;
    String itemgroup_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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
                pDialog = new ProgressDialog(ItemCategoryActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            Intent intent = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
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
        });
        Intent intent = getIntent();
        getSupportActionBar().setTitle(R.string.Item_Category);
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        liteposdevice = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
        System.out.println("lic data"+ liteposdevice);
        try {
            if (liteposdevice != null) {
                liccustomerid = liteposdevice.getLic_customer_license_id();
                System.out.println("lic data"+ liccustomerid);
            }
        } catch (Exception e) {

        }
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

//        check_online_mode();

        edt_layout_item_ct_name = (TextInputLayout) findViewById(R.id.edt_layout_item_ct_name);
        edt_layout_item_ct_code = (TextInputLayout) findViewById(R.id.edt_layout_item_ct_code);
        edt_item_ct_name = (EditText) findViewById(R.id.edt_item_ct_name);
        edt_item_ct_code = (EditText) findViewById(R.id.edt_item_ct_code);
        spn_parent_category = (Spinner) findViewById(R.id.spn_parent_category);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_save.setVisibility(View.GONE);

        spn_parent_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (isSpinnerInitial) {
                    isSpinnerInitial = false;
                } else {
                    if (position == 0) {
                        spn_parent_code = "0";
                    } else {
                        Item_Group item_group_parent1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + arrayList_custom.get(position) + "'");
                        spn_parent_code = item_group_parent1.get_item_group_code();
                        if (spn_parent_code.equals(code)) {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.Canparent, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            fill_parent_code("");
                        } else {
                            Item_Group item_group_parent = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE parent_code = '" + spn_parent_code + "'");
                            if (item_group_parent != null) {
                                if (item_group_parent.get_item_group_code().equals(code)) {
                                    if (item_group_parent.get_parent_code().equals(code)) {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.AlreadyChild, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        fill_parent_code("");
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.Alreadyothercategory, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    fill_parent_code("");
                                }
                            } else {
                                Item_Group item_group_parent2 = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + spn_parent_code + "'");
                                if (item_group_parent2.get_parent_code().equals(code)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.AlreadyChildthiscategory, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    fill_parent_code("");
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (operation.equals("Edit")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            if(!Globals.objLPR.getproject_id().equals("cloud")) {

                btn_delete.setVisibility(View.VISIBLE);
            }
            item_group = item_group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + code + "'");
//            item_group=null;
//            if (item_group == null) {
            if (item_group != null) {
                edt_item_ct_name.setText(item_group.get_item_group_name());
                edt_item_ct_code.setText(item_group.get_item_group_code());
                edt_item_ct_code.setEnabled(false);
                Item_Group item_group_parent = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + item_group.get_parent_code() + "'");
                if (item_group_parent != null) {
                    String compare_parent_name;
                    try {
                        compare_parent_name = item_group_parent.get_item_group_code();
                    } catch (Exception ex) {
                        compare_parent_name = "";
                    }
                    fill_parent_code(compare_parent_name);
                } else {
                    fill_parent_code("");
                }
            }
        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
            fill_parent_code("");
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<Item> item_array = Item.getAllItem(getApplicationContext(),"where item_group_code = '"+code+"'",database);

                if (item_array.size()>0){
                    alertDialog = new AlertDialog.Builder(
                            ItemCategoryActivity.this);
                    alertDialog.setTitle("");
                    alertDialog
                            .setMessage(R.string.categoryDeleteMessage);

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
                                    pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    pDialog = new ProgressDialog(ItemCategoryActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);
                                                item_group.set_is_active("0");
                                                long l = item_group.updateItem_Group("item_group_code=?", new String[]{code}, database);
                                                if (l > 0) {
                                                    for (int i=0;i<item_array.size();i++){
                                                        Item item = Item.getItem(getApplicationContext(),"where item_group_code = '"+item_array.get(i).get_item_group_code()+"'",database,db);
                                                        item.set_item_group_code("");
                                                        item.updateItem("item_code=?",new String[]{item.get_item_code()},database);
                                                    }
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });
                                                    } else {
//                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }



                                            } catch (InterruptedException e) {
                                                pDialog.dismiss();
//                                                database.endTransaction();
                                                e.printStackTrace();
                                            } finally {
                                                pDialog.dismiss();
//                                                database.endTransaction();
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
                }else {
                    alertDialog = new AlertDialog.Builder(
                            ItemCategoryActivity.this);
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
                                    pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    pDialog = new ProgressDialog(ItemCategoryActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);

                                                item_group.set_is_active("0");
                                                long l = item_group.updateItem_Group("item_group_code=?", new String[]{code}, database);
                                                if (l > 0) {
                                                   pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });

                                                } else {
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

                    alert = alertDialog.create();
                    alert.show();

                    nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

            }
        });
    }

    private void fill_parent_code(String compare_parent_name) {
        arrayList = Item_Group.getAllItem_GroupSpinner_All(getApplicationContext(), " WHERE is_active ='1' Order By item_group_name asc");
        arrayList_custom = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == 0) {
                arrayList_custom.add("Select Category");
            }
            arrayList_custom.add(arrayList.get(i).get_item_group_code());
        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getApplicationContext(), arrayList_custom);
        spn_parent_category.setAdapter(spinnerAdapter);

        if (!compare_parent_name.equals("")) {
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
//                int h = (int) spn_parent_category.getAdapter().getItemId(i);
                String iname = arrayList_custom.get(i);
                if (compare_parent_name.equals(iname)) {
                    spn_parent_category.setSelection(i);
                    break;
                }
            }
        }
    }

    private void Update_Item_category(String code, String item_category_name, String item_category_code) {
        database.beginTransaction();
        item_group.set_item_group_name(item_category_name);
        item_group.set_item_group_code(item_category_code);
        item_group.set_parent_code(spn_parent_code);
        item_group.set_is_push("N");
        long l = item_group.updateItem_Group("item_group_code=?", new String[]{code}, database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Update_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
                    startActivity(intent_category);
                    finish();
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
    }

    private void Fill_Item_category(String item_category_name, String item_category_code) {
        String modified_by = Globals.user;
        database.beginTransaction();
        System.out.println("DATE String"+ date);
        System.out.println("license customer id"+ liccustomerid);

        item_group = new Item_Group(getApplicationContext(), null, liccustomerid, item_category_code, spn_parent_code, item_category_name, "0", "1", modified_by, date, "N");
        long l = item_group.insertItem_Group(database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
                    startActivity(intent_category);
                    finish();
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

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ItemCategoryActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Intent intent = new Intent(ItemCategoryActivity.this, CategoryListActivity.class);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(ItemCategoryActivity.this);
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


    private void save() {

        if (operation.equals("Edit")) {
            final String Item_category_name = edt_item_ct_name.getText().toString().trim();
            final String Item_category_code = edt_item_ct_code.getText().toString().trim();

            pDialog = new ProgressDialog(ItemCategoryActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        try {
                            Item_Group objIG1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_name='" + edt_item_ct_name.getText().toString() + "' and  item_group_code != '"+  edt_item_ct_code.getText().toString() +"'");

                            //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                            itemgroup_name = objIG1.get_item_group_name();
                        }
                        catch(Exception e){

                        }

                        if(edt_item_ct_name.getText().toString().equals(itemgroup_name)){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pDialog.dismiss();
                                   edt_item_ct_name.setText(item_group.get_item_group_name());
                                   edt_item_ct_name.selectAll();
                                    Toast.makeText(ItemCategoryActivity.this, "Item Group already present", Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            Update_Item_category(code, Item_category_name, Item_category_code);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                }
            };
            timerThread.start();

        } else if (operation.equals("Add")) {
            if (edt_item_ct_code.getText().toString().equals("")) {
                Item_Group objIG1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "  order By item_group_id Desc LIMIT 1");

                if (objIG1 == null) {
                    strIGCode = Globals.objLPD.getDevice_Symbol() + "IG-" + 1;
                } else {
                    strIGCode = Globals.objLPD.getDevice_Symbol() + "IG-" + (Integer.parseInt(objIG1.get_item_group_id()) + 1);
                }
            } else {

                if (edt_item_ct_code.getText().toString().contains(" ")) {
                    edt_item_ct_code.setError(getString(R.string.Cant_Enter_Space));
                    edt_item_ct_code.requestFocus();
                    return;
                } else {
                    strIGCode = edt_item_ct_code.getText().toString();
                }
            }

            if (edt_item_ct_name.getText().toString().equals("")) {
                edt_item_ct_name.setError(getString(R.string.Category_name_is_required));
                edt_item_ct_name.requestFocus();
                return;
            } else {
                Item_category_name = edt_item_ct_name.getText().toString().trim();
            }

            pDialog = new ProgressDialog(ItemCategoryActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        try {
                            Item_Group objIG1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_name='" + edt_item_ct_name.getText().toString() + "'");

                            //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                            itemgroup_name = objIG1.get_item_group_name();
                        }
                        catch(Exception e){

                        }


                        if(edt_item_ct_name.getText().toString().equals(itemgroup_name)){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pDialog.dismiss();
                                    edt_item_ct_name.setText("");
                                    Toast.makeText(ItemCategoryActivity.this, "Item Group already present", Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            Fill_Item_category(Item_category_name, strIGCode);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            };
            timerThread.start();


        }
    }
}
