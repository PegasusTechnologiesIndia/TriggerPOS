package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.AutoText;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.phomellolitepos.Adapter.TableRecyclerViewAdapter;
import org.phomellolitepos.Adapter.Table_Order;
import org.phomellolitepos.Adapter.VehicleHistoryAdapter;
import org.phomellolitepos.Adapter.VehicleOutAdapter;
import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.Fragment.VehicleOUT_Fragment;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;

import java.util.ArrayList;

public class VehicleOutList extends AppCompatActivity implements VehicleOutAdapter.ItemListener {
    Database db;
    SQLiteDatabase database;
    String orderCode;
    ArrayList<Vehicle_Order> arrayListorder;
    VehicleOutAdapter vehicloutAdapter;

    private ShowData showFormulaListener;

    public interface ShowData {
        public void ShowData(Vehicle_Order item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_out_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.vehicleorder));
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        try{
           // Globals.newvehicleorderList.clear();
            getOrderList();
        }
        catch(Exception e){

        }



        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               finish();
            }
        });


    }
    private void getOrderList() {

        RecyclerView order_list = (RecyclerView) findViewById(R.id.vehicleout_recyclerView);
        if (Globals.vehicleorderList.size() > 0) {

            // order_arraylist = Orders.getAllOrders(getApplicationContext(), "WHERE is_active = '1' AND order_status ='OPEN' And z_code='0'", database);


            vehicloutAdapter = new VehicleOutAdapter(VehicleOutList.this, Globals.vehicleorderList,this);
            //table_title.setVisibility(View.INVISIBLE);
            order_list.setVisibility(View.VISIBLE);
            order_list.setAdapter(vehicloutAdapter);

            int mNoOfColumns = Globals.calculateNoOfColumns(getApplicationContext(),250);
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            order_list.setLayoutManager(manager);


            vehicloutAdapter.notifyDataSetChanged();

        } else {

            order_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(Vehicle_Order item) {

        if(Globals.vehicleorderList.size()>0)
        {
            Globals.newvehicleorderList.clear();
            Globals.newvehicleorderList.add(new Vehicle_Order(item.getOrdercode(),item.getVehicleno(),item.getMobileno(),item.getInTime(),item.getVehiclestatus(),"",item.getAmount(),item.getRfid(),item.getUnitId()));
            Intent i=new Intent(getApplicationContext(),ParkingIndustryActivity.class);
            i.putExtra("One", "1");
            startActivity(i);

          // Globals.vehicleorderList.clear();
       /*     Intent intent = new Intent(getApplicationContext(), ParkingIndustryActivity.class);
            startActivityForResult(intent, 10001);
            setResult(Activity.RESULT_OK);*/
    /*        FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            VehicleOUT_Fragment fragobj = new VehicleOUT_Fragment();
            fragmentTransaction.add(R.id.content_parkingindustry_vehicleout, fragobj).addToBackStack(null).commit();*/


        }
    }


}
