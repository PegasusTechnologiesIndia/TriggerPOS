package org.phomellolitepos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Master;

public class TaxActivity extends AppCompatActivity {
    TextInputLayout edt_layout_tax_name, edt_layout_value, edt_layout_comment;
    EditText edt_tax_name, edt_value, edt_comment;
    Spinner spinner_type,spinner_tax_group;
    Button btn_next, btn_tax_delete;
    String operation, tax_id;
    SQLiteDatabase database;
    Database db;
    String date, strType;
    Tax_Master tax_master;
String taxId;
    String taxtype[] = {};
    String taxGroup[] = {};
    ProgressDialog pDialog;
    String tax_name = "", value = "", comment = "";
    String strTaxGroup;
    Sys_Tax_Group sys_tax_group;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    MenuItem menuItem;
String taxname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Tax);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        taxtype = getResources().getStringArray(R.array.taxtype);
        taxGroup = getResources().getStringArray(R.array.tax_group);
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
                pDialog = new ProgressDialog(TaxActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(100);
                            Intent intent = new Intent(TaxActivity.this, TaxListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }
    //    lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(),"" ,database);
        edt_layout_tax_name = (TextInputLayout) findViewById(R.id.edt_layout_tax_name);
        edt_layout_value = (TextInputLayout) findViewById(R.id.edt_layout_value);
        edt_layout_comment = (TextInputLayout) findViewById(R.id.edt_layout_comment);
        edt_tax_name = (EditText) findViewById(R.id.edt_tax_name);
        edt_value = (EditText) findViewById(R.id.edt_value);
        edt_comment = (EditText) findViewById(R.id.edt_comment);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_tax_delete = (Button) findViewById(R.id.btn_tax_delete);
        spinner_type = (Spinner) findViewById(R.id.spinner__type);
        spinner_tax_group = (Spinner) findViewById(R.id.spinner_tax_group);
        btn_next.setVisibility(View.GONE);



        edt_value.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_value.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_value.requestFocus();
                    edt_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_value, InputMethodManager.SHOW_IMPLICIT);
                    edt_value.selectAll();
                    return true;
                }
            }
        });


        Intent intent = getIntent();
        tax_id = intent.getStringExtra("tax_id");
        operation = intent.getStringExtra("operation");

        if (operation == null) {
            operation = "Add";
        }

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);


        if (operation.equals("Edit")) {
            if(!Globals.objLPR.getproject_id().equals("cloud")) {

                btn_tax_delete.setVisibility(View.VISIBLE);
            }

            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id = '" + tax_id + "'", database, db);
            edt_tax_name.setText(tax_master.get_tax_name());
            edt_value.setText(tax_master.get_rate());
            edt_comment.setText(tax_master.get_comment());
            String compareValue;
            String chk_spn_value = tax_master.get_tax_type();
            if (chk_spn_value.equals("F")) {
                compareValue = getString(R.string.Fix_Rate);
            } else {
                compareValue = getString(R.string.Percentage);
            }


            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taxtype);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_type.setAdapter(dataAdapter);
            if (!compareValue.equals(null)) {
                int spinnerPosition = dataAdapter.getPosition(compareValue);
                spinner_type.setSelection(spinnerPosition);
            }

            try {
                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(),"WHERE tax_id = '"+tax_master.get_tax_id()+"'");
                String compareValue_tax_grp = sys_tax_group.get_tax_master_id();

                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taxGroup);
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_tax_group.setAdapter(dataAdapter1);
                if (!compareValue_tax_grp.equals(null)) {
                    int spinnerPosition = Integer.parseInt(compareValue_tax_grp);
                    spinner_tax_group.setSelection(spinnerPosition);
                }
            }catch (Exception e){
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taxGroup);
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_tax_group.setAdapter(dataAdapter1);
            }

        } else {
            btn_next.setBackgroundColor(getResources().getColor(R.color.button_color));
            try {
                fill_tax_type_spinner();
            }
            catch(Exception e){

            }
            try {
                fill_tax_group_spinner();
            }
            catch(Exception e){

            }
        }

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String strCheckType = String.valueOf(spinner_type.getItemAtPosition(i));
                    String ab = getString(R.string.Fix_Rate);
                    if (strCheckType.equals(ab)) {
                        strType = "F";
                    } else {
                        strType = "P";
                    }
                } catch (Exception ecx) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner_tax_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    strTaxGroup = i+"";
                    strTaxGroup=strTaxGroup;
                } catch (Exception ecx) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_tax_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = new AlertDialog.Builder(
                        TaxActivity.this);
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
                                pDialog = new ProgressDialog(TaxActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);
                                           // database.beginTransaction();


                                            String sqlQuery="select tx.tax_id from tax tx left join item_group_tax it_tax ON it_tax.tax_id = tx.tax_id left join Sys_Tax_Group sys_tax ON sys_tax.tax_id = tx.tax_id left join order_type_tax od_tax ON od_tax.tax_id = tx.tax_id where it_tax.tax_id = '"+ tax_id+"' and tx.is_active ='1'";
                                          //  database = db.getReadableDatabase();
                                            Cursor cursor = database.rawQuery(sqlQuery, null);
                                            if (cursor.moveToFirst()) {
                                                do {
                                                    taxId=cursor.getString(0);
                                                   // Unitid = cursor.getString(1);
                                                } while (cursor.moveToNext());

                                            }

                                            // closing connection
                                            cursor.close();

                                            if(taxId!=null){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        pDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), getString(R.string.deletemsg), Toast.LENGTH_LONG).show();


                                                    }
                                                });
                                            }
                                            else {
                                                database.beginTransaction();
                                                try {
                                                    long l = Order_Type_Tax.delete_Order_Type_Tax(getApplicationContext(), "tax_id=?", new String[]{tax_id}, database);
                                                } catch (Exception ex) {
                                                }

                                                try {
                                                    long lg = Item_Group_Tax.delete_Item_Group_Tax(getApplicationContext(), "tax_id=?", new String[]{tax_id}, database);
                                                } catch (Exception ex) {
                                                }

                                                try {
                                                    long lg1 = Sys_Tax_Group.delete_Sys_Tax_Group(getApplicationContext(), "tax_id=?", new String[]{tax_id}, database);
                                                } catch (Exception ex) {
                                                }

                                                Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id = '" + tax_id + "'", database, db);
                                                tax_master.set_is_active("false");
                                                long h = tax_master.updateTax_Master("tax_id=?", new String[]{tax_id}, database);
                                                if (h > 0) {
                                                    database.setTransactionSuccessful();
                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(TaxActivity.this, TaxListActivity.class);
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

    private void fill_tax_group_spinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taxGroup);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tax_group.setAdapter(dataAdapter);
    }

    private void Insert_tax(String tax_name, String value, String comment, String strType, String strTaxGroup) {
        String modified_by = Globals.user;
        database.beginTransaction();
        tax_master = new Tax_Master(getApplicationContext(), null, Globals.objLPD.getLocation_Code(), tax_name, strType, value, comment, "1", modified_by, date, "N");
        long l = tax_master.insertTax_Master(database);
        if (l > 0) {
            if (Integer.parseInt(strTaxGroup)>0){
                sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, strTaxGroup, l+"");
                long l1 = sys_tax_group.insertSys_Tax_Group(database);
                if (l1 > 0) {

                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            tax_master = Tax_Master.getTax_Master(getApplicationContext(), "", database, db);
            final String strTaxId = tax_master.get_tax_id();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if(Globals.objLPR.getIndustry_Type().equals("4")){
                        Intent intent_category = new Intent(TaxActivity.this, TaxListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                    else {
                        Intent intent_category = new Intent(TaxActivity.this, OrderTypeCheckListActivity.class);
                        intent_category.putExtra("tax_id", strTaxId);
                        intent_category.putExtra("operation", operation);
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

    }

    private void Update_tax(final String tax_id, String tax_name, String tax_value, String tax_comment, String strType, String strTaxGroup) {

        database.beginTransaction();
        tax_master.set_tax_name(tax_name);
        tax_master.set_rate(tax_value);
        tax_master.set_comment(tax_comment);
        tax_master.set_tax_type(strType);
        tax_master.set_is_push("N");
        long l = tax_master.updateTax_Master("tax_id=?", new String[]{tax_id}, database);
        if (l > 0) {

            if (Integer.parseInt(strTaxGroup)>0) {
                try {
                    sys_tax_group.set_tax_master_id(strTaxGroup);
                    long l1 = sys_tax_group.updateSys_Tax_Group("tax_id=?", new String[]{tax_id}, database);
                    if (l1 > 0) {
                    }
                }catch (Exception ex){
                    sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, strTaxGroup, tax_id);
                    long l1 = sys_tax_group.insertSys_Tax_Group(database);
                    if (l1 > 0) {

                    }
                }

            }
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if(Globals.objLPR.getIndustry_Type().equals("4")){
                        Intent intent_category = new Intent(TaxActivity.this, TaxListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                    else {
                        Intent intent_category = new Intent(TaxActivity.this, OrderTypeCheckListActivity.class);
                        intent_category.putExtra("tax_id", tax_id);
                        intent_category.putExtra("operation", operation);
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
                    Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    private void fill_tax_type_spinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taxtype);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(dataAdapter);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(TaxActivity.this);
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
            final String tax_name = edt_tax_name.getText().toString().trim();
            final String tax_value = edt_value.getText().toString().trim();
            final String tax_comment = edt_comment.getText().toString().trim();

            pDialog = new ProgressDialog(TaxActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        try {
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_name = '" + edt_tax_name.getText().toString() + "'and tax_id!='"+tax_id+"'", database, db);

                           // Tax_Master tax_obj = Tax_Master.getTax_Master(getApplicationContext(), database, db, "WHERE tax_name='" + edt_item_ct_name.getText().toString() + "' and  item_group_code != '"+  edt_item_ct_code.getText().toString() +"'");

                            //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                            taxname = tax_master.get_tax_name();
                        }
                        catch(Exception e){

                        }

                        if(edt_tax_name.getText().toString().equalsIgnoreCase(taxname)){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pDialog.dismiss();
                                    edt_tax_name.setText(tax_master.get_tax_name());
                                    edt_tax_name.selectAll();
                                    Toast.makeText(TaxActivity.this, getString(R.string.taxpresent), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            try {
                                Update_tax(tax_id, tax_name, tax_value, tax_comment, strType, strTaxGroup);
                            }
                            catch(Exception e){

                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                }
            };
            timerThread.start();


        } else if (operation.equals("Add")) {


            if (edt_tax_name.getText().toString().trim().equals("")) {
                edt_tax_name.setError(getString(R.string.Tax_name_is_required));
                edt_tax_name.requestFocus();
                return;
            } else {
                tax_name = edt_tax_name.getText().toString().trim();
            }

            if (edt_value.getText().toString().trim().equals("")) {
                edt_value.setError(getString(R.string.Tax_value_is_required));
                edt_value.requestFocus();
                return;
            } else {
                value = edt_value.getText().toString().trim();
            }

            comment = edt_comment.getText().toString().trim();
            pDialog = new ProgressDialog(TaxActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.Wait_msg));
            pDialog.show();
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        try {
                            Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_name = '" + edt_tax_name.getText().toString() + "'", database, db);

                            // Tax_Master tax_obj = Tax_Master.getTax_Master(getApplicationContext(), database, db, "WHERE tax_name='" + edt_item_ct_name.getText().toString() + "' and  item_group_code != '"+  edt_item_ct_code.getText().toString() +"'");

                            //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                            taxname = tax_master.get_tax_name();
                        }
                        catch(Exception e){

                        }

                        if(edt_tax_name.getText().toString().equals(taxname)){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pDialog.dismiss();
                                    edt_tax_name.setText("");

                                    Toast.makeText(TaxActivity.this, getString(R.string.taxpresent), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            try {
                                Insert_tax(tax_name, value, comment, strType, strTaxGroup);
                            }
                            catch(Exception e){

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

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TaxActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(100);

                    Intent intent = new Intent(TaxActivity.this, TaxListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
