package org.phomellolitepos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hoin.btsdk.BluetoothService;

import org.phomellolitepos.Util.Globals;

import java.util.Set;



public class BluetoothDeviceListActivity extends AppCompatActivity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
BluetoothService mService = null;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth_device_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Device_List);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id==0){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothDeviceListActivity.this.finish();
            }
        });

//        setResult(Activity.RESULT_CANCELED);
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    doDiscovery();
                    v.setVisibility(View.GONE);
                }
                catch(Exception e){

                }
            }
        });

        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        try {
            Intent i=getIntent();
            String flag= i.getStringExtra("flag");
            Set<BluetoothDevice> pairedDevices;
            if(flag.equals("settings")) {

                    mService = SetttingsActivity.mService;


              //  pairedDevices = SetttingsActivity.mService.getPairedDev();

            }
            else if(flag.equals("printtest")){
                mService = PrintTestActivity.mService;

            }
            else{
                if(Globals.objLPR.getIndustry_Type().equals("4")){
                    mService = ParkingIndustryActivity.mService;

                }
                else if(Globals.objLPR.getIndustry_Type().equals("3")){
                    mService = PaymentCollection_MainScreen.mService;
                }
                else {
                    mService = MainActivity.mService;
                }


            }
            pairedDevices = mService.getPairedDev();
            if (pairedDevices.size() > 0) {
                findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
                for (BluetoothDevice device : pairedDevices) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.bluetooth_none_paired).toString();
                mPairedDevicesArrayAdapter.add(noDevices);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.cancelDiscovery();
        }
        mService = null;
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {

        try {
            setProgressBarIndeterminateVisibility(true);
            setTitle(R.string.bluetooth_scanning);

            findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

            if (mService.isDiscovering()) {
                mService.cancelDiscovery();
            }

            mService.startDiscovery();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {   //����б�������豸
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mService.cancelDiscovery();

            try {
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length() - 17);
                if (info.equals("No devices have been paired")) {

                } else {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                    Log.d("���ӵ�ַ", address);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            } catch (Exception ex) {

            }

        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.bluetooth_select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.bluetooth_none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
}
