package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.TableRecyclerViewAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Table;

import java.util.ArrayList;

public class TableMangement extends AppCompatActivity implements TableRecyclerViewAdapter.ItemListener {
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
  TextView table_title;
    ArrayList<String> zonelabels;
    ArrayList<String> zonelabel_id;
    ArrayList<Table> arrayList;
    ArrayList<Table_Order> arrayListTable;
    ArrayList<Orders> arrayListOrder;
    ArrayList<Table> arrayListTableCode;
    ArrayList<String>arrayListString;
    Spinner spn_zone;
    ArrayList<Table> order_arraylist;
    TableRecyclerViewAdapter tableMangementAdapter;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_mangement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
          spn_zone=(Spinner)findViewById(R.id.spin_zone) ;
        table_title = (TextView) findViewById(R.id.table_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    /*            pDialog = new ProgressDialog(CategoryListActivity.this);
                        pDialog.setCancelable(false);
                        pDialog.setMessage(getString(R.string.Wait_msg));
                        pDialog.show();
                        Thread timerThread = new Thread() {
                            public void run() {*/


                finish();
/*                if (Globals.objsettings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(TableMangement.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        // pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (Globals.objsettings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(TableMangement.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //  pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(TableMangement.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        // pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }*/

                   /* }
                };
                timerThread.start();*/
            }
        });
        //order_arraylist = new ArrayList<Orders>();
        Table tableobj = Table.getTable(getApplicationContext(), database, db, "");
        if(tableobj==null){
                      table_title.setVisibility(View.VISIBLE);
                  table_title.setText("No Table Found");
            Toast.makeText(getApplicationContext(),"No Table Data Found",Toast.LENGTH_LONG).show();
        }
        else{
            table_title.setVisibility(View.INVISIBLE);
        }
        try {
            zonelabels = getAllZones();


            if(zonelabels!=null) {
                ArrayAdapter dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, zonelabels);
                spn_zone.setAdapter(dataAdapter);

                // appCompatSpinner.setPrompt("Select Insurance Company");
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                table_title.setVisibility(View.INVISIBLE);
            }
            else{
                table_title.setVisibility(View.VISIBLE);
            }
        }
        catch(Exception e){
        }




        spn_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemIdAtPosition(position);

                Globals.ZoneID = String.valueOf(parent.getItemIdAtPosition(position));
                    try {

                            getTables(zonelabel_id.get(position));
                            order_arraylist = getAllOrders(zonelabel_id.get(position));

                            getTableList();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public ArrayList<String> getAllZones(){

        zonelabels = new ArrayList<String>();
        zonelabel_id=new ArrayList<String>();

        String selectQuery = "SELECT DISTINCT(Zone_name),Zone_id FROM  tables" ;

     database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String zoneId = cursor.getString(cursor.getColumnIndex("Zone_id"));
                String zoneName = cursor.getString(cursor.getColumnIndex("Zone_name"));


                zonelabel_id.add(zoneId);
                zonelabels.add(zoneName);
            } while (cursor.moveToNext());

        }

        // closing connection
        cursor.close();
       // db.close();

        // returning lables
        return zonelabels;
    }

    private void getTableList() {

        RecyclerView table_list = (RecyclerView) findViewById(R.id.table_recyclerView);
        if (arrayListTable.size() > 0) {

               // order_arraylist = Orders.getAllOrders(getApplicationContext(), "WHERE is_active = '1' AND order_status ='OPEN' And z_code='0'", database);


                tableMangementAdapter = new TableRecyclerViewAdapter(TableMangement.this, arrayListTable   , order_arraylist, this);
                table_title.setVisibility(View.INVISIBLE);
                table_list.setVisibility(View.VISIBLE);
                table_list.setAdapter(tableMangementAdapter);
                int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
                GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
                table_list.setLayoutManager(manager);

                tableMangementAdapter.notifyDataSetChanged();

        } else {
            table_title.setVisibility(View.VISIBLE);
            table_list.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(Table_Order table) {

        if(table.getNoOfOrder().equals("1")){

            Toast.makeText(getApplicationContext(), "This Table is Already Booked With Other Order", Toast.LENGTH_SHORT).show();
        }

        else {
            //Toast.makeText(getApplicationContext(), table.get_table_code() + " is clicked", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to place order with " + table.getTable_name() + " Of Zone " + table.getZone_name())
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            Globals.strTable_Code = table.getTable_code();
                            Globals.table_code = table.getTable_code();
                            Globals.table_name = table.getTable_name();
                            Globals.strZoneName = table.getZone_name();

                            // item = new Item(getApplicationContext(), item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null);

                       /* table.setTable_status("1");
                        long l = Table.updateTable("table_status=?", new String[]{"0"}, database);
                        if (l > 0) {
                            //succ_bg = "1";
                        } else {
                        }*/
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void getTables(String zoneid){
      // String selectQuery = "SELECT table_code,table_name,Zone_id,Zone_name FROM  tables where Zone_id='"+zoneid+"'" ;
       // String selectQuery =  "SELECT t.*, o.order_id FROM `tables` t left join orders o on t.table_code = o.table_code and o.`order_status` = 'OPEN' AND o.z_code = '0' AND o.is_post='0' WHERE t.`Zone_id` = '1' AND t.`is_active` = '1' group by t.table_code";

        String selectQuery = "Select *, ( Select count(*) From orders where  Z_code =0  and order_status = 'OPEN' and table_code = tables.table_code) as NoOfOrder From tables where Zone_id='"+zoneid+"'";
        try {
    arrayListTable=new ArrayList<Table_Order>();
    arrayListString=new ArrayList<String>();
    database = db.getReadableDatabase();
    Cursor cursor = database.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
        do {
            String tablecode = cursor.getString(1);
            String tablename = cursor.getString(2);
            String zone_id = cursor.getString(3);
            String zone_name = cursor.getString(4);
            String NoOfOrder = cursor.getString(8);





             // arrayListOrder.add(new Orders(getApplicationContext(),orderid));
            //zonelabels.add(zoneId);
            arrayListTable.add(new Table_Order( tablecode, tablename, zone_id, zone_name, NoOfOrder));

            ///arrayListTable.add(new Table(this, null, tablecode, tablename, zone_id, zone_name, "", "", "",""));
        } while (cursor.moveToNext());

    }

    // closing connection
    cursor.close();


}
catch(Exception e){
  System.out.println(e.getMessage());
}
    }


    public ArrayList<Table> getAllOrders(String zoneid){
       String selectQuery = " Select * From tables where table_code  in ( Select table_code From orders where  z_code ='0'  and order_status = 'OPEN') and Zone_id = '"+zoneid+"'" ;
        arrayListTableCode=new ArrayList<Table>();
        database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String tablecode = cursor.getString(cursor.getColumnIndex("table_code"));
                String tablename = cursor.getString(cursor.getColumnIndex("table_name"));
                String zone_id = cursor.getString(cursor.getColumnIndex("Zone_id"));
                String zone_name = cursor.getString(cursor.getColumnIndex("Zone_name"));


                //zonelabels.add(zoneId);
                arrayListTableCode.add(new Table(this, null, tablecode, "", "", "", "", "", ""));
            }while (cursor.moveToNext()) ;

            }

            // closing connection
            cursor.close();
        return arrayListTableCode;
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
