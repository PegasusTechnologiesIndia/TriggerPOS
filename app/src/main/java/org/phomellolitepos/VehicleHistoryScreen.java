package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.TableRecyclerViewAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.Adapter.VehicleHistoryAdapter;
import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;

import java.util.ArrayList;

public class VehicleHistoryScreen extends AppCompatActivity {
    Database db;
    SQLiteDatabase database;
    String orderCode,status;
    ArrayList<Vehicle_Order> arrayListorder;
    ArrayList<String> spn_list;
    Spinner spn_status;
    VehicleHistoryAdapter vehicleHistoryAdapter;
    EditText edt_toolbar_order_list;
    LinearLayout LL_spn;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_history_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.vehiclehistory));
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        edt_toolbar_order_list = (EditText) findViewById(R.id.edt_toolbar_order_list);
        LL_spn=findViewById(R.id.LL_spn);

        edt_toolbar_order_list.setMaxLines(1);
        edt_toolbar_order_list.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_toolbar_order_list.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edt_toolbar_order_list.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    //close soft keyboard
                    String strFilter = edt_toolbar_order_list.getText().toString().trim();
                    // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                    strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                    arrayListorder.clear();
                    spn_status.setSelection(0);
                    getVehicleDetailsFilter(strFilter);
                    getOrderList();
                    edt_toolbar_order_list.clearFocus();
                    return true;
                }
                return false;
            }
        });
        edt_toolbar_order_list.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                String strFilter = edt_toolbar_order_list.getText().toString().trim();
                // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                arrayListorder.clear();
                spn_status.setSelection(0);
                getVehicleDetailsFilter(strFilter);
                getOrderList();
            }
        });


        arrayListorder=new ArrayList<Vehicle_Order>();
        try {

            String strFilter = edt_toolbar_order_list.getText().toString().trim();
            // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
            strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
            arrayListorder.clear();
            getVehicleDetailsFilter(strFilter);
            getOrderList();

            // getVehicleDetails();
        }
        catch (Exception e){

        }
        try {
            if (arrayListorder.size() > 0) {
                getOrderList();
            }
        }catch(Exception e){

        }

        spn_list=new ArrayList<>();
        spn_list.add("--Select--");
        spn_list.add("IN");
        spn_list.add("OUT");
        spn_status=findViewById(R.id.spn_status);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spn_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_status.setAdapter(dataAdapter);
        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                arrayListorder.clear();
                status=spn_status.getItemAtPosition(position).toString();
                String strFilter=edt_toolbar_order_list.getText().toString();

                if(status.equals("IN"))
                {
                    status="OPEN";
                    strFilter = "and  od.order_status='"+status+"' and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                }else if(status.equals("OUT"))
                         {
                             status="CLOSE";
                             strFilter = "and  od.order_status='"+status+"' and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                         }else
                           strFilter = "and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";

                getVehicleDetailsFilter(strFilter);
                getOrderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edt_toolbar_order_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_order_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_order_list.requestFocus();
                    edt_toolbar_order_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_order_list, InputMethodManager.SHOW_IMPLICIT);

                    return true;
                }
            }
        });

        edt_toolbar_order_list.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                   try {
                       vehicleHistoryAdapter.updateList(arrayListorder);
                       }catch (Exception e)
                       {

                       }

                    return true;
                } else {
                    return false;
                }
            }
        });
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

                if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(VehicleHistoryScreen.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                }
                else {
                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(VehicleHistoryScreen.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(VehicleHistoryScreen.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //  pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(VehicleHistoryScreen.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }

                   /* }
                };
                timerThread.start();*/
            }
        });

    }

    public void getVehicleDetails(){

        String selectQuery = "SELECT  od.order_code,od_det.discount ,od_det.sale_price ,od.order_status,od.table_code,od.remarks,od.modified_date,ct.contact_1,od.RFID FROM orders od LEFT JOIN  contact ct ON ct.contact_code=od.contact_code left join order_detail od_det ON od_det.order_code=od.order_code WHERE od.is_active='1' Order by od.modified_date desc" ;

        database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                orderCode=cursor.getString(0);
                String advamount = cursor.getString(1);
                String totalamount = cursor.getString(2);
                String orderstatus = cursor.getString(3);
                String vehicleno = cursor.getString(4);
                String vehicletype = cursor.getString(5);
                String intime = cursor.getString(6);
                String contactno = cursor.getString(7);
                String rfidtag = cursor.getString(8);
                String intimesub= DateUtill.PaternDate1(intime).substring(12,20).toString();

                arrayListorder.add(new Vehicle_Order( orderCode, vehicleno, contactno, intime,orderstatus, advamount,totalamount,rfidtag,""));
            } while (cursor.moveToNext());

        }

        // closing connection
        cursor.close();
        // db.close();


    }
    public void getVehicleDetailsFilter(String strfilter)
    {

        String selectQuery = "SELECT  od.order_code,od_det.discount ,od_det.sale_price ,od.order_status,od.table_code,od.remarks,od.modified_date,ct.contact_1,od.RFID FROM orders od LEFT JOIN  contact ct ON ct.contact_code=od.contact_code left join order_detail od_det ON od_det.order_code=od.order_code  WHERE od.is_active='1'" +strfilter+ "  ORDER BY od.modified_date DESC";

        database=db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                orderCode=cursor.getString(0);
                String advamount = cursor.getString(1);
                String totalamount = cursor.getString(2);
                String orderstatus = cursor.getString(3);
                String vehicleno = cursor.getString(4);
                String vehicletype = cursor.getString(5);
                String intime = cursor.getString(6);
                String contactno = cursor.getString(7);
                String rfidtag = cursor.getString(8);
                String intimesub= DateUtill.PaternDate1(intime).substring(12,20).toString();

                arrayListorder.add(new Vehicle_Order( orderCode, vehicleno, contactno, intime,orderstatus, advamount,totalamount,rfidtag,""));
            } while (cursor.moveToNext());

        }
        if(cursor.getCount()==0)
            Toast.makeText(VehicleHistoryScreen.this,"no data found",Toast.LENGTH_LONG).show();
        else
        vehicleHistoryAdapter.updateList(arrayListorder);
        // closing connection
        cursor.close();


        // db.close();


    }
    private void getOrderList() {

        RecyclerView order_list = (RecyclerView) findViewById(R.id.order_recyclerView);
        TextView table_title=findViewById(R.id.table_title);
        if (arrayListorder.size() > 0) {

            // order_arraylist = Orders.getAllOrders(getApplicationContext(), "WHERE is_active = '1' AND order_status ='OPEN' And z_code='0'", database);


            vehicleHistoryAdapter = new VehicleHistoryAdapter(VehicleHistoryScreen.this, arrayListorder);
            table_title.setVisibility(View.GONE);
            LL_spn.setVisibility(View.VISIBLE);
            order_list.setVisibility(View.VISIBLE);
            order_list.setAdapter(vehicleHistoryAdapter);

            int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            order_list.setLayoutManager(manager);


            vehicleHistoryAdapter.notifyDataSetChanged();


        } else {

            order_list.setVisibility(View.GONE);
            LL_spn.setVisibility(View.GONE);
            table_title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        if(Globals.objLPR.getIndustry_Type().equals("4")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_order_list.getText().toString().trim();
           // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
            strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
            arrayListorder.clear();
            spn_status.setSelection(0);
            getVehicleDetailsFilter(strFilter);
            getOrderList();

            View view = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {


        if(Globals.objLPR.getIndustry_Type().equals("4")){
            Intent intent = new Intent(VehicleHistoryScreen.this, ParkingIndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // pDialog.dismiss();
            finish();
        }
        else {
            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                try {
                    Intent intent = new Intent(VehicleHistoryScreen.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                try {
                    Intent intent = new Intent(VehicleHistoryScreen.this, RetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else {
                try {
                    Intent intent = new Intent(VehicleHistoryScreen.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        }

    }

}