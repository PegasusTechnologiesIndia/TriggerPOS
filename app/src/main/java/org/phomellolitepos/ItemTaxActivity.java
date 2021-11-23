package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Settings;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class ItemTaxActivity extends AppCompatActivity {
    TextView txt_reset;
    EditText edt_hsn_code;
    Spinner spn_filter;
    Button btn_next;
    SQLiteDatabase database;
    Database db;
    String strSelectedCategory = "";
    ArrayList<String> strCategoryCode = new ArrayList<String>();
    ProgressDialog pDialog;
    Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_tax);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item Tax");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
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

                pDialog = new ProgressDialog(ItemTaxActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                sleep(1000);
                                pDialog.dismiss();
                                Intent intent = new Intent(ItemTaxActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        } else {
                            try {
                                sleep(1000);
                                pDialog.dismiss();
                                Intent intent = new Intent(ItemTaxActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finish();

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();

            }
        });

        txt_reset = (TextView) findViewById(R.id.txt_reset);
        edt_hsn_code = (EditText) findViewById(R.id.edt_hsn_code);
        spn_filter = (Spinner) findViewById(R.id.spn_filter);
        btn_next = (Button) findViewById(R.id.btn_next);
        Intent intent = getIntent();

        getList();


        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
                edt_hsn_code.setText("");
                strSelectedCategory = "";
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strSelectedCategory.equals("") && edt_hsn_code.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Select Atleast One Option", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!edt_hsn_code.getText().toString().trim().equals("")) {
                    Item item = Item.getItem(getApplicationContext(), "where hsn_sac_code ='" + edt_hsn_code.getText().toString().trim() + "'", database, db);
                    if (item == null) {
                        Toast.makeText(getApplicationContext(), "HSN Code not exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String IGCode, HSNCode;
                if (strSelectedCategory.equals("")) {
                    IGCode = "";
                } else {
                    IGCode = "item_group_code ='" + strSelectedCategory + "'";
                }

                if (edt_hsn_code.getText().toString().trim().equals("")) {
                    HSNCode = "";
                } else {
                    if (strSelectedCategory.equals("")){
                        HSNCode = "hsn_sac_code ='" + edt_hsn_code.getText().toString().trim() + "'";
                    }else {
                        HSNCode = "and hsn_sac_code ='" + edt_hsn_code.getText().toString().trim() + "'";
                    }
                }

                String qry = "select item_code from item where " + IGCode + " " + HSNCode + "";
                Cursor mCursor = database.rawQuery(qry, null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    do {
                        strCategoryCode.add(mCursor.getString(0));
                    } while (mCursor.moveToNext());
                }
                Intent intent = new Intent(ItemTaxActivity.this, TaxCheckListActivity.class);
                intent.putExtra("item_tax", "IT");
                intent.putStringArrayListExtra("item_code", strCategoryCode);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getList() {

        final ArrayList<Item_Group> arrayItem_Group = Item_Group.getAllItem_Group(getApplicationContext(), " WHERE is_active ='1' Order By item_group_name asc", database, db);
        HintAdapter<Item_Group> hintAdapter = new HintAdapter<Item_Group>(
                this,
                R.layout.item_manufacture_spinner_item,
                "Select Category",
                arrayItem_Group) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                Item_Group resultp = arrayItem_Group.get(position);
                final String name = resultp.get_item_group_name();
                final String code = resultp.get_item_group_code();

                // Here you inflate the layout and set the value of your widgets
                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.manufacture_name)).setText(name);
                ((TextView) view.findViewById(R.id.manufacture_code)).setText(code);
                return view;
            }
        };

        HintSpinner<Item_Group> hintSpinner = new HintSpinner<Item_Group>(
                spn_filter,
                hintAdapter,
                new HintSpinner.Callback<Item_Group>() {
                    @Override
                    public void onItemSelected(int position, Item_Group itemAtPosition) {
                        Item_Group resultp = arrayItem_Group.get(position);
                        strSelectedCategory = resultp.get_item_group_code();
                    }
                });
        hintSpinner.init();

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ItemTaxActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        sleep(1000);
                        pDialog.dismiss();
                        Intent intent = new Intent(ItemTaxActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                } else {
                    try {
                        sleep(1000);
                        pDialog.dismiss();
                        Intent intent = new Intent(ItemTaxActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }
}
