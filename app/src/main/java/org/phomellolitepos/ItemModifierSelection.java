package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.phomellolitepos.Adapter.ItemModifierAdapter;
import org.phomellolitepos.Adapter.ModifierCheckListAdapter;
import org.phomellolitepos.Adapter.TableRecyclerViewAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.CheckBoxClass.ModifierItemCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.ModiefierData;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Table;

import java.util.ArrayList;

import sunmi.ds.DSKernel;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.UPacketFactory;

public class ItemModifierSelection extends AppCompatActivity {
    Database db;
    int id;
    SQLiteDatabase database;
    String decimal_check, qty_decimal_check;
    ItemModifierAdapter itemModifierAdapter;
    String itemCode,operation,orderCode,SRNO;
    MenuItem menuItem;
    Item item;
    ModifierListener modifierlistener;
    TextView txtitemname;
    Settings settings;
    ArrayList<ModiefierData> arrayListreceipe = new ArrayList<ModiefierData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_modifier_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item Modifier");
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        Intent i=getIntent();
        itemCode=i.getStringExtra("itemcode");
        operation = i.getStringExtra("opr");
        orderCode = i.getStringExtra("odr_code");
        SRNO= i.getStringExtra("srno");
        txtitemname=(TextView)findViewById(R.id.txtitemname) ;

        item=Item.getItem(getApplicationContext(),"WHERE item_code = '"+ itemCode+"'",database,db);
        if(item!=null){
            txtitemname.setText(item.get_item_name());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    finish();



            }
        });


       settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        fill_modifier_list();
        RecyclerView modifierlist = (RecyclerView) findViewById(R.id.modifier_recyclerView);
     /*   modifierlist.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL
                , true));*/

        //modifierlist.setHasFixedSize(true);
        if(arrayListreceipe.size()>0){


            // arrayListreceipe = ReceipeModifier.getAllReceipeModifier(getApplicationContext(), " ", database);
            itemModifierAdapter = new ItemModifierAdapter(ItemModifierSelection.this,arrayListreceipe,decimal_check,database,db,itemCode);

            modifierlist.setVisibility(View.VISIBLE);
            modifierlist.setAdapter(itemModifierAdapter);
            GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            modifierlist.setLayoutManager(manager);
            itemModifierAdapter.notifyDataSetChanged();
        }

    }

    public void fill_modifier_list() {
        String Query = "SELECT  re.item_code,re.modifier_code, it.item_name,it.item_group_code FROM Receipe_modifier re LEFT JOIN item  it ON it.item_code = re.modifier_code where re.item_code='"+itemCode+"'";
        Cursor cursor = database.rawQuery(Query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String itemcode = cursor.getString(0);
                String modifiercode = cursor.getString(1);
                String itemname = cursor.getString(2);
                String itemgroupcode = cursor.getString(3);




                //zonelabels.add(zoneId);
                arrayListreceipe.add(new ModiefierData(itemcode, modifiercode, itemname,itemgroupcode));
            }while (cursor.moveToNext()) ;

        }

        // closing connection
        cursor.close();


    }
    public void setTextView(String price, String qty) {
//        Globals.TotalItemPrice = 15d;
       Globals.TotalItemPrice= Double.parseDouble(price);
       Globals.TotalQty=Double.parseDouble(qty);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(ItemModifierSelection.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Item Group saved based on this save function

                    save();

                }
                catch(Exception e){
System.out.println(e.getMessage());
                }
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


    public void save(){
//setTextView(String.valueOf(Globals.TotalItemPrice),String.valueOf(Globals.TotalQty));



        if(Globals.objLPR.getIndustry_Type().equals("2")){
            Globals.SRNO=Globals.SRNO+1;
            Intent intent = new Intent(ItemModifierSelection.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("opr", "Add");
            intent.putExtra("order_code", orderCode);
            startActivity(intent);
            finish();
        }
        else {
            if (settings.get_Home_Layout().equals("0")) {
                Globals.SRNO = Globals.SRNO + 1;
                Intent intent = new Intent(ItemModifierSelection.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("opr", "Add");
                intent.putExtra("order_code", orderCode);
                startActivity(intent);
                finish();

            } else if (settings.get_Home_Layout().equals("1")) {
                Globals.SRNO = Globals.SRNO + 1;
                Intent intent = new Intent(ItemModifierSelection.this, Main2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("opr", "Add");
                intent.putExtra("order_code", orderCode);
                startActivity(intent);
                finish();
            } else if (settings.get_Home_Layout().equals("2")) {
                Globals.SRNO = Globals.SRNO + 1;
                Intent intent = new Intent(ItemModifierSelection.this, RetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("opr", "Add");
                intent.putExtra("order_code", orderCode);
                startActivity(intent);
                //finish();
            }
        }
    }


    public interface ModifierListener {
        void onItemModifier(String itemprice,String itemqty);
    }
}