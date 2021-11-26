package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.ModifierCheckListAdapter;
import org.phomellolitepos.Adapter.TaxItemCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.ModifierItemCheck;
import org.phomellolitepos.CheckBoxClass.TaxItemCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Tax_Master;

import java.util.ArrayList;

public class ModiferCheckListActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    ListView modifier_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    ArrayList<Item> arrayList = new ArrayList<Item>();
    ArrayList<ModifierItemCheck> list = new ArrayList<ModifierItemCheck>();
    String item_code, operation, strPIT, sPrice, strItemTax, strSelectedCategory;
    ArrayList<String> modifier_code = new ArrayList<String>();
    MenuItem item_menu;
    ModifierCheckListAdapter modifierCheckListAdapter;
    Item item_master;
    ReceipeModifier item_modifier;
   Item_Group item_group;
String strIsModifier;
    ArrayList<ReceipeModifier>arrayList_cbg=new ArrayList<ReceipeModifier>();

    boolean found = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifer_check_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.Item_Modifier);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
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

                pDialog = new ProgressDialog(ModiferCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(100);

                                Intent intent = new Intent(ModiferCheckListActivity.this, ItemActivity.class);
                                intent.putExtra("item_code", item_code);
                                intent.putExtra("operation", operation);
                                intent.putExtra("PIT", strPIT);
                                intent.putExtra("sprice", sPrice);
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



        modifier_ck_list = (ListView) findViewById(R.id.modifier_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        item_code = intent.getStringExtra("item_code");
        operation = intent.getStringExtra("operation");
        strItemTax = intent.getStringExtra("item_tax");
        strPIT = intent.getStringExtra("PIT");
        strIsModifier=intent.getStringExtra("modifier");
        sPrice = intent.getStringExtra("sprice");
        if (operation == null) {
            operation = "";
        }
        if (strItemTax == null) {
            strItemTax = "";
        }

        if (strPIT == null) {
            strPIT = "";
        }
        if (operation.equals("Add")) {
            fill_modifier_list();
        }
        else if (operation.equals("Edit")) {
            arrayList_cbg = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), "WHERE item_code ='" + item_code + "'", database);
            arrayList = Item.getAllItem(getApplicationContext(), " WHERE is_active ='1' and is_modifier='1'", database);
            if (arrayList_cbg.size() > 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    ModifierItemCheck b = new ModifierItemCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {
                        String itemcode = arrayList_cbg.get(h).getModifier_code();
                        item_master = Item.getItem(getApplicationContext(), "WHERE is_active= '1' and item_code='" + itemcode + "'", database, db);
                        if (item_master == null) {
                            b.setName(arrayList.get(i).get_item_name());
                            b.setSelected(false);
                        } else {
                            String name = item_master.get_item_name();
                            b.setName(arrayList.get(i).get_item_name());
                            b.setSelected(false);
                            if (name.equals(arrayList.get(i).get_item_name())) {
                                found = true;
                                b.setSelected(true);
                                break;
                            }
                        }

                    }
                    list.add(i, b);
                }


                if (list.size() > 0) {
                    modifierCheckListAdapter = new ModifierCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    modifier_ck_list.setVisibility(View.VISIBLE);
                    modifier_ck_list.setAdapter(modifierCheckListAdapter);
                    modifier_ck_list.setTextFilterEnabled(true);
                    modifierCheckListAdapter.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    modifier_ck_list.setVisibility(View.GONE);
                }
            } else {
                fill_modifier_list();
            }
        }

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(ModiferCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);

                            insert_item_modifier();

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

   /* private void insert_item_modifier() {
        String loc;
        try {
            loc = Globals.objLPD.getLocation_Code();
        } catch (Exception ex) {
            loc = "1";
        }
        String strCheck = "0";
        database.beginTransaction();
        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' and is_modifier = '1' ", database);
        if (arrayList.size() > 0) {

                // Update on 1 scrum
                // Update on 1 scrum
                for (int i = 0; i < list.size(); i++) {
                    ModifierItemCheck b = list.get(i);
                    if (b.isSelected()) {
                        String name = b.getName();
                        item_master = Item.getItem(getApplicationContext(), "WHERE item_name ='" + name + "'", database, db);
                        String contact_code = item_master.get_item_code();
                        //Database transaction begins here

                    } else {
                        strCheck = "1";
                    }
                }
            //}

            if (strPIT.equals("1")) {
                Double iTax = 0d;
                Double iPTax = 0d;

                Double iPrice = Double.parseDouble(sPrice);

                ArrayList<Item_Group_Tax> item_group_tax = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "WHERE item_group_code='" + item_code + "'", database, db);
                for (int a = 0; a < item_group_tax.size(); a++) {
                    Item_Group_Tax item_group_tax1 = item_group_tax.get(a);
                    String tax_id = item_group_tax1.get_tax_id();
                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                    if (tax_master.get_tax_type().equals("P")) {
                        iTax = iTax + Double.parseDouble(tax_master.get_rate());

                    } else {

                        iPTax = iPTax + (Double.parseDouble(tax_master.get_rate()) * 100);
                    }
                }
                Double dPrice = ((iPrice * 100) - iPTax) / (100 + iTax);

                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code='" + item_code + "'", database);
                item_location.set_selling_price(dPrice.toString());
                item_location.set_new_sell_price(sPrice);
                long h = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                if (h > 0) {

                }
            }

            if (strCheck.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                if (strItemTax.equals("IT")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemTaxActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }


            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                if (strItemTax.equals("IT")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemTaxActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }
            }
        } else {
            database.endTransaction();
            pDialog.dismiss();
            if (strItemTax.equals("IT")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemTaxActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }

        }
    }*/



    private void insert_item_modifier() {

        String strCheck = "0";
        database.beginTransaction();
        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' and is_modifier = '1' ", database);
        if (arrayList.size() > 0) {

                // Update on 1 scrum
                long l = ReceipeModifier.delete_Item_ReceipeModifier(getApplicationContext(), "item_code =?", new String[]{item_code}, database);
                // Update on 1 scrum
                for (int i = 0; i < list.size(); i++) {
                    ModifierItemCheck b = list.get(i);
                    if (b.isSelected()) {
                        String name = b.getName();

                        item_master = Item.getItem(getApplicationContext(), "WHERE item_name ='" + name + "'", database, db);

                        String itemcode = item_master.get_item_code();
                        item_modifier = ReceipeModifier.getReceipemOdifier(getApplicationContext(), database, db,"WHERE item_code ='" + item_code + "'");

                        item_modifier = new ReceipeModifier(getApplicationContext(), null, item_code, itemcode);
                        //Database transaction begins here

                        long a = item_modifier.insertReceipemodifier(database);
                        if (a > 0) {

                        }
                    } else {
                        strCheck = "1";
                    }
                }


            /*if (strPIT.equals("1")) {
                Double iTax = 0d;
                Double iPTax = 0d;

                Double iPrice = Double.parseDouble(sPrice);

                ArrayList<Item_Group_Tax> item_group_tax = Item_Group_Tax.getAllItem_Group_Tax(getApplicationContext(), "WHERE item_group_code='" + item_code + "'", database, db);
                for (int a = 0; a < item_group_tax.size(); a++) {
                    Item_Group_Tax item_group_tax1 = item_group_tax.get(a);
                    String tax_id = item_group_tax1.get_tax_id();
                    Tax_Master tax_master = Tax_Master.getTax_Master(getApplicationContext(), "Where tax_id = '" + tax_id + "'", database, db);
                    if (tax_master.get_tax_type().equals("P")) {
                        iTax = iTax + Double.parseDouble(tax_master.get_rate());

                    } else {

                        iPTax = iPTax + (Double.parseDouble(tax_master.get_rate()) * 100);
                    }
                }
                Double dPrice = ((iPrice * 100) - iPTax) / (100 + iTax);

                Item_Location item_location = Item_Location.getItem_Location(getApplicationContext(), "WHERE item_code='" + item_code + "'", database);
                item_location.set_selling_price(dPrice.toString());
                item_location.set_new_sell_price(sPrice);
                long h = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                if (h > 0) {

                }
            }
*/
            if (strCheck.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
/*
                if(strIsModifier.equals("0")){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ModiferCheckListActivity.class);
                            intent_category.putExtra("item_code", item_code);
                            intent_category.putExtra("PIT", strPIT);
                            intent_category.putExtra("sprice", sPrice);
                            intent_category.putExtra("operation", operation);
                            intent_category.putExtra("modifier", strIsModifier);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }
                else {*/
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
              //  }


            } else {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                if (strItemTax.equals("IT")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemTaxActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }

                else if(strIsModifier.equals("0")){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                            intent_category.putExtra("item_code", item_code);
                            intent_category.putExtra("PIT", strPIT);
                            intent_category.putExtra("sprice", sPrice);
                            intent_category.putExtra("operation", operation);
                            intent_category.putExtra("modifier", strIsModifier);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                            Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                    });
                }
            }
        } else {
            database.endTransaction();
            pDialog.dismiss();
            if (strItemTax.equals("IT")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemTaxActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }
            else if(strIsModifier.equals("0")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                        intent_category.putExtra("item_code", item_code);
                        intent_category.putExtra("PIT", strPIT);
                        intent_category.putExtra("sprice", sPrice);
                        intent_category.putExtra("operation", operation);
                        intent_category.putExtra("modifier", strIsModifier);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }

            else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(ModiferCheckListActivity.this, ItemListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }

        }
    }

    private void fill_modifier_list() {
        arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' and is_modifier = '1' ", database);
        for (int i = 0; i < arrayList.size(); i++) {
            ModifierItemCheck b = new ModifierItemCheck();
            b.setName(arrayList.get(i).get_item_name());
            b.setSelected(false);
            list.add(b);
        }


        if (list.size() > 0) {
            modifierCheckListAdapter = new ModifierCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            modifier_ck_list.setVisibility(View.VISIBLE);
            modifier_ck_list.setAdapter(modifierCheckListAdapter);
            modifier_ck_list.setTextFilterEnabled(true);
            modifierCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            modifier_ck_list.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_menu = menu.findItem(R.id.action_settings);
        if (found == true) {
            item_menu.setTitle(R.string.Deselect_All);
        } else {

            item_menu.setTitle(R.string.Select_All);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_all_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String strMenu = item.getTitle() + "";
        if (id == R.id.action_settings) {
            list.clear();
            String ab = getString(R.string.Select_All);
            if (strMenu.equals(ab)) {
                item.setTitle(R.string.Deselect_All);
                arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' and is_modifier = '1' ", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ModifierItemCheck b = new ModifierItemCheck();
                    b.setName(arrayList.get(i).get_item_name());
                    b.setSelected(false);
                    list.add(b);
                }

                modifierCheckListAdapter = new ModifierCheckListAdapter(this, list);
                // attaching data adapter to spinner
                modifier_ck_list.setAdapter(modifierCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                arrayList = Item.getAllItem(getApplicationContext(), "WHERE is_active = '1' and is_modifier = '1' ", database);
                for (int i = 0; i < arrayList.size(); i++) {
                    ModifierItemCheck b = new ModifierItemCheck();
                    b.setName(arrayList.get(i).get_item_name());
                    b.setSelected(false);
                    list.add(b);
                }

                modifierCheckListAdapter = new ModifierCheckListAdapter(this, list);
                // attaching data adapter to spinner
                modifier_ck_list.setAdapter(modifierCheckListAdapter);

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ModiferCheckListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(100);

                        Intent intent = new Intent(ModiferCheckListActivity.this, ItemActivity.class);
                        intent.putExtra("item_code", item_code);
                        intent.putExtra("operation", operation);
                        intent.putExtra("PIT", strPIT);
                        intent.putExtra("sprice", sPrice);
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
