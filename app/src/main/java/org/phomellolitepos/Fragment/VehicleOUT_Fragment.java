package org.phomellolitepos.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.PaymentListAdapter;
import org.phomellolitepos.Adapter.VehicleOutAdapter;
import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.AppController;
import org.phomellolitepos.PaymentActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.VehicleOutList;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.MinCalculation;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.ScaleSetup;
import org.phomellolitepos.printer.PrintLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VehicleOUT_Fragment extends Fragment {
    EditText edt_veh_mob;
    TextView tv_amountpaid, lbl_spn_item_payment,tv_dueamount, tv_slipno, tv_vehicleno, tv_intym, tv_vehiclestatus, tv_norecord, tv_mobileno;
    EditText edt_colectamnt;
    LinearLayout ll_textlayout;
    Button btn_collect, btn_enter, btn_reset;
    View vout;
    Lite_POS_Device lite_pos_device;
    String part2;
    Database db;
    String PayId = "1";
    SQLiteDatabase database;
    String liccustomerid;
    String orderCode;
    String decimal_check;
    RelativeLayout rel_parking;
    String Time;
    Dialog listDialog;
    String str_total_Amount;
    SimpleDateFormat sdf;
    Contact contact;
    ArrayList<MinCalculation> min_calculation;
    ArrayList<Payment> paymentArrayList;
    Spinner spn_paymentmode;
    String spnpaymentmode;
    String strPayMethod;
    String strTagId=null;
    int count;
    ArrayList<Vehicle_Order> arrayListorder;
    String serial_no, android_id, myKey, device_id, imei_no;
    public VehicleOUT_Fragment() {
        //required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        final TelephonyManager mTelephony = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return ;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getActivity().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getActivity().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
       // device_id = telephonyManager.getDeviceId();
      //  imei_no = telephonyManager.getImei();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vout = inflater.inflate(R.layout.contentfrag_parking_vehicleout, container, false);
        //rel_parking = (FrameLayout) vout.findViewById(R.id.content_parkingindustry);
        lbl_spn_item_payment=vout.findViewById(R.id.lbl_spn_item_payment);
        lbl_spn_item_payment.setPadding(10,0,0,0);
        edt_veh_mob = (EditText) vout.findViewById(R.id.edt_vehiclemobileno);
        edt_veh_mob.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_veh_mob.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if (edt_veh_mob.getText().toString().length() == 0) {
                        edt_veh_mob.setError("Enter Value");
                        return true;
                    } else {
                        //getVehicleDetails();
                        String strFilter = edt_veh_mob.getText().toString().trim();
                        // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                        strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%' Or od.RFID Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                        edt_veh_mob.selectAll();

                        if(Globals.objsettings.getIs_cloudprint().equals("true")){

                            getOrderList(edt_veh_mob.getText().toString());

                        }
                        else {
                            getVehicleDetails(strFilter);
                        }
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                    return true;
                }
                return false;
            }
        });
        edt_colectamnt = (EditText) vout.findViewById(R.id.edt_collectamnt);
        btn_collect = (Button) vout.findViewById(R.id.btn_collect);
        tv_amountpaid = (TextView) vout.findViewById(R.id.tv_amnt);
        tv_dueamount = (TextView) vout.findViewById(R.id.tv_dueamnt);
        tv_slipno = (TextView) vout.findViewById(R.id.tv_slipno);
        tv_vehicleno = (TextView) vout.findViewById(R.id.tv_vehicleno);
        tv_vehiclestatus = (TextView) vout.findViewById(R.id.tv_vstatus);
        tv_intym = (TextView) vout.findViewById(R.id.tv_intym);
        ll_textlayout = (LinearLayout) vout.findViewById(R.id.ll_text);
        tv_norecord = (TextView) vout.findViewById(R.id.txt_norecord);
        tv_mobileno = (TextView) vout.findViewById(R.id.tv_mobileno);
        btn_enter = (Button) vout.findViewById(R.id.btn_enter);
        btn_reset = (Button) vout.findViewById(R.id.btn_reset);
        spn_paymentmode=(Spinner)vout.findViewById(R.id.spinner_item_payment);
        db = new Database(getActivity());
        database = db.getWritableDatabase();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Time = sdf.format(new Date());
        lite_pos_device = Lite_POS_Device.getDevice(getActivity(), "", database);
        try {
            if (lite_pos_device != null) {
                liccustomerid = lite_pos_device.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();

        } catch (Exception ex) {
            decimal_check = "1";
        }

        fill_spinner_pay_method("");
        arrayListorder=new ArrayList<Vehicle_Order>();

        if(Globals.newvehicleorderList.size()>0){

            try{
            ll_textlayout.setVisibility(View.VISIBLE);
                btn_collect.setVisibility(View.VISIBLE);

         /*   TabLayout tabLayout = (TabLayout) vout.findViewById(R.id.tabs);
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();*/
            double dueamnt=0d;
for(int i=0;i<Globals.newvehicleorderList.size();i++) {
    orderCode=Globals.newvehicleorderList.get(i).getOrdercode();
    edt_veh_mob.setText(Globals.newvehicleorderList.get(i).getOrdercode());
    tv_slipno.setText(Globals.newvehicleorderList.get(i).getOrdercode());
    if(Globals.newvehicleorderList.get(i).getVehicleno().equals("")){
        tv_vehicleno.setText("NA");
    }
    else {
        tv_vehicleno.setText(Globals.newvehicleorderList.get(i).getVehicleno());
    }
    tv_vehiclestatus.setText(Globals.newvehicleorderList.get(i).getVehiclestatus());
    tv_intym.setText(Globals.newvehicleorderList.get(i).getInTime());

    if (!Globals.newvehicleorderList.get(i).getMobileno().isEmpty()) {
        tv_mobileno.setText(Globals.newvehicleorderList.get(i).getMobileno());
    }
    else{
        tv_mobileno.setText("NA");
    }
    if (!Globals.newvehicleorderList.get(i).getAdvanceamnt().equals("") && Globals.newvehicleorderList.get(i).getAdvanceamnt().length()>0) {

        dueamnt = Double.parseDouble(Globals.newvehicleorderList.get(i).getAmount()) - Double.parseDouble(Globals.newvehicleorderList.get(i).getAdvanceamnt());
        tv_amountpaid.setText(Globals.myNumberFormat2Price(Double.parseDouble(Globals.newvehicleorderList.get(i).getAmount() + ""), decimal_check));
        tv_dueamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(dueamnt + ""), decimal_check));
    } else {
        tv_dueamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(Globals.newvehicleorderList.get(i).getAmount() + ""), decimal_check));
        tv_amountpaid.setText(Globals.myNumberFormat2Price(Double.parseDouble("0" + ""), decimal_check));

    }

getMinCalculation(Globals.newvehicleorderList.get(i).getUnitId(),Globals.newvehicleorderList.get(i).getInTime(),Globals.newvehicleorderList.get(i).getAmount());
   // edt_colectamnt.setText(Globals.newvehicleorderList.get(i).getAmount());
}

}catch (Exception e){

                System.out.println(e.getMessage());
            }

    }
        if(Globals.objsettings.getIs_NFC().equals("true")){

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                strTagId = getArguments().getString("TagId");
                edt_veh_mob.setText(strTagId);
            }

        }
        edt_veh_mob.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                   // getVehicleDetails();

                    String strFilter = edt_veh_mob.getText().toString().trim();
                    // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                    strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%' Or od.RFID Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                    edt_veh_mob.selectAll();

                    if(Globals.objsettings.getIs_cloudprint().equals("true")){

                        getOrderList(edt_veh_mob.getText().toString());

                    }
                    else {
                        getVehicleDetails(strFilter);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        spn_paymentmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                try {


                    // Object item = adapterView.getItemAtPosition(pos);
                    spnpaymentmode= (String) spn_paymentmode.getSelectedItem();
                    Payment resultp = paymentArrayList.get(pos);
                    strPayMethod = resultp.get_payment_name();
                    PayId = resultp.get_payment_id();

                    try {

                        fill_spinner_pay_method(strPayMethod);

                    } catch (Exception ex) {
                        PayId = "1";
                    }
                    //  fill_spinner_pay_method("");
                  /*  if (PayId.equals("5")) {
                        if (Globals.strContact_Code.equals("")) {

                        }
                    }*/
                } catch (Exception ecx) {
                    System.out.println(ecx.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (edt_veh_mob.getText().toString().length() == 0) {
                    edt_veh_mob.setError("Enter Value");
                    return;
                } else {
                    //getVehicleDetails();
                    String strFilter = edt_veh_mob.getText().toString().trim();
                    // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                    strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%' Or od.RFID Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                    edt_veh_mob.selectAll();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view = getActivity().getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    if(Globals.objsettings.getIs_cloudprint().equals("true"))
                    {
                        getOrderList(edt_veh_mob.getText().toString());

                    }
                    else {
                        getVehicleDetails(strFilter);
                    }
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }

            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                edt_veh_mob.setText("");
                edt_veh_mob.clearFocus();
                edt_colectamnt.setText("");
                Globals.newvehicleorderList.clear();
                Globals.vehicleorderList.clear();
                tv_norecord.setVisibility(View.GONE);
                ll_textlayout.setVisibility(View.GONE);
                btn_collect.setVisibility(View.GONE);
            }
        });
        btn_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_veh_mob.getText().toString().length() == 0) {
                    edt_veh_mob.setError("Please Enter Value");
                    return;
                } else
                    {
                     Orders orders = Orders.getOrders(getActivity(), database, " WHERE order_code = '" + orderCode + "'");

                    if(orders==null)
                    {
//                        Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                "No Record Found", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getActivity(),"no data found ",Toast.LENGTH_LONG).show();
                    }else
                    {
                        orders.set_order_status("CLOSE");
                        orders.set_delivery_date(Time);
                        long l = orders.updateOrders("order_code=?", new String[]{orderCode}, database);

                        Order_Payment objOrderPayment = null;
                        objOrderPayment = new Order_Payment(getActivity(), null, liccustomerid, orderCode, "1", edt_colectamnt.getText().toString(),
                                PayId, "", "", "", "", "", "");
                        // }
                        long op = objOrderPayment.insertOrder_Payment(database);
                        if (op > 0) {
                            // strFlag = "1";
                            if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                                VehicleOUT_Fragment.Sendorder_BackgroundAsyncTask order = new VehicleOUT_Fragment.Sendorder_BackgroundAsyncTask();
                                order.execute();
                            }

                            if(Globals.objsettings.get_Is_File_Share().equals("true")) {
                                showdialog();
                            }
                            else{
                                call_remainingcode();
                            }
                        } else {
                        }

                    }

                }
            }
        });
        // getVehicleDetails();
        return vout;
    }

    public void call_remainingcode()
    {

        Toast.makeText(getActivity(), "Vehicle OUT Successfully!", Toast.LENGTH_SHORT).show();
        if(!Globals.objsettings.getPrinterId().equals("0"))
        {
            Intent launchIntent = new Intent(getActivity(), PrintLayout.class);
            launchIntent.putExtra("strflag", "VehicleIn");
            launchIntent.putExtra("strOrderNo", orderCode);
            getActivity().startActivity(launchIntent);
        }
                  /*  getFragmentManager()
                            .beginTransaction()
                            .detach(VehicleOUT_Fragment.this)
                            .attach(VehicleOUT_Fragment.this)
                            .commit();*/
        ll_textlayout.setVisibility(View.GONE);
        btn_collect.setVisibility(View.GONE);
        edt_veh_mob.setText("");
        edt_colectamnt.setText("");

        Globals.vehicleorderList.clear();
        Globals.newvehicleorderList.clear();
        edt_veh_mob.requestFocus();
    }

    public void getOrderList(String searchvalue) {

        ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.gettingOrderdetail));
        pDialog.show();


        String server_url= Globals.App_IP_URL +"order/get_order_by_value";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {



                            pDialog.dismiss();

                           // long e = Order_Detail.delete_order_detail(getActivity(), "order_detail", " " ,database);






                            Globals.vehicleorderList.clear();
                            Globals.newvehicleorderList.clear();
                             JSONObject jsonObject_order = new JSONObject(response);
                            String strStatus = jsonObject_order.getString("status");
                           String orderno="";
                            String vehno="",unitid="";
                            if (strStatus.equals("true")) {
                                JSONArray jsonArray_order = jsonObject_order.getJSONArray("result");
                                Order_Detail objOrderDetail;
                                Orders objOrder;
                                for (int i = 0; i < jsonArray_order.length(); i++) {
                                    JSONObject jsonObject_order1 = jsonArray_order.getJSONObject(i);

                                    orderno= jsonObject_order1.getString("order_code");
                                    String orderdate= jsonObject_order1.getString("order_date");
                                    String contactcode= jsonObject_order1.getString("contact_code");
                                    String orderstatus= jsonObject_order1.getString("order_status");
                                    String remarks= jsonObject_order1.getString("remarks");
                                    String empCode= jsonObject_order1.getString("emp_code");
                                    vehno= jsonObject_order1.getString("table_code");
                                    String mobileno= jsonObject_order1.getString("mobile");
                                    String toalamnt= jsonObject_order1.getString("total");
                                    String modifiedby= jsonObject_order1.getString("modified_by");
                                    String modified_date= jsonObject_order1.getString("modified_date");


                                    long e1 = Orders.delete_orders(getActivity(), "orders", "order_code =?", new String[]{orderno}, database);
                                    long e = Order_Detail.delete_order_detail(getActivity(), "order_detail", "order_code =?", new String[]{orderno}, database);

                                    objOrder = new Orders(getActivity(), null, liccustomerid, "", "", orderno, orderdate, contactcode,
                                            empCode, Globals.TotalItem + "", Globals.TotalQty + "",
                                            Globals.TotalItemPrice + "", "" + "", "0", toalamnt, "0" + "",
                                            "0" + "", "", "0", "1", "1", modifiedby, modified_date, "N", orderstatus, remarks, vehno, orderdate,empCode);

                                    long l = objOrder.insertOrders(database);
                                    JSONArray jsonArray_orderdetail = jsonObject_order1.getJSONArray("order_detail");
                                    String discount="";
                                    for(int j=0;j<jsonArray_orderdetail.length();j++) {

                                        JSONObject jsonObject_orderdetail = jsonArray_orderdetail.getJSONObject(j);

                                      orderno= jsonObject_orderdetail.getString("order_code");
                                       String srno= jsonObject_orderdetail.getString("sr_no");
                                        String itemcode= jsonObject_orderdetail.getString("item_code");
                                        String costprice= jsonObject_orderdetail.getString("cost_price");
                                        String saleprice= jsonObject_orderdetail.getString("sale_price");
                                        String tax= jsonObject_orderdetail.getString("tax");
                                      discount= jsonObject_orderdetail.getString("discount");
                                        unitid= jsonObject_orderdetail.getString("unit_id");
                                                objOrderDetail = new Order_Detail(getActivity(), null, liccustomerid, orderno,
                                                "",itemcode, srno, costprice, saleprice, tax,
                                                "1", "0", discount, saleprice, "0", "false",unitid,"");
                                        long od = objOrderDetail.insertOrder_Detail(database);

                                        arrayListorder.add(new Vehicle_Order(orderno, vehno, mobileno, orderdate,orderstatus, discount,toalamnt,empCode,unitid));


                                    }

                                }
                                Globals.vehicleorderList= arrayListorder;
                                if(Globals.vehicleorderList.size()>1) {
                                    Intent i = new Intent(getActivity(), VehicleOutList.class);
                                    startActivity(i);
                                }
                                else{

                                    String strFilter = vehno;
                                    // od.emp_code='"+edt_veh_mob.getText().toString()+"' OR ct.contact_1='"+edt_veh_mob.getText().toString()+"' OR od.order_code='"+edt_veh_mob.getText().toString()+"'
                                    strFilter = " and ( od.table_code Like '%" + strFilter + "%' Or od.order_code Like '%" + strFilter + "%' Or od.RFID Like '%" + strFilter + "%'  Or ct.contact_1 Like '%" + strFilter + "%' )";
                                    edt_veh_mob.selectAll();
                                    getVehicleDetails(strFilter);


                                }
                            }
                        } catch (Exception e) {

                            pDialog.dismiss();
                            System.out.println(e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getActivity(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getActivity(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getActivity(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.Device_Code);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());
                params.put("search_value", searchvalue);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    class Sendorder_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        VehicleOUT_Fragment activity;

        public Sendorder_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Orders order=new Orders(getActivity());

                order.sendOn_Server(getActivity(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }

    }


    public void getVehicleDetails(String strfilter) {

        String selectQuery = "SELECT  od.order_code,od_det.discount ,od_det.sale_price ,od.order_status,od.table_code,od.remarks,od.modified_date,ct.contact_1,it.unit_id,od.RFID FROM orders od LEFT JOIN  contact ct ON ct.contact_code=od.contact_code left join order_detail od_det ON od_det.order_code=od.order_code left join item it ON it.item_code=od_det.item_code WHERE od.is_active='1'" +strfilter+ " And od.order_status='OPEN' ORDER BY od.modified_date DESC";
        database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

       count= cursor.getCount();
       String orderstatus="",amount = "",totalamount="",vehicleno="",vehicletype="",intime="",mobileno="",unitid="",rfidvalue="";

        arrayListorder.clear();
           if (cursor.moveToFirst()) {
               do {

                   orderCode = cursor.getString(0);
                   amount = cursor.getString(1);
                   totalamount = cursor.getString(2);
                   orderstatus = cursor.getString(3);
                   vehicleno = cursor.getString(4);
                   vehicletype = cursor.getString(5);
                   intime = cursor.getString(6);

//                   if(cursor.getString(7)==null)
//                       mobileno="NA";
//                   else
                       mobileno = cursor.getString(7);
                   unitid = cursor.getString(8);
                   rfidvalue = cursor.getString(9);
                   str_total_Amount = totalamount.toString();
                   arrayListorder.add(new Vehicle_Order( orderCode, vehicleno, mobileno, intime,orderstatus, amount,totalamount,rfidvalue,unitid));


               } while (cursor.moveToNext());
           }
           if(count==1) {
               if (orderstatus.equals("OPEN")) {
                   ll_textlayout.setVisibility(View.VISIBLE);
                   btn_collect.setVisibility(View.VISIBLE);
                   String intimesub = intime.substring(11, 19).toString();
                   if (amount.length() > 0) {
                       double dueamnt = Double.parseDouble(totalamount) - Double.parseDouble(amount);
                       tv_slipno.setText(orderCode);
                       if (vehicleno.equals("")) {
                           tv_vehicleno.setText("NA");
                       } else {
                           tv_vehicleno.setText(vehicleno);
                       }

                       tv_vehiclestatus.setText(orderstatus);
                       tv_intym.setText(intimesub);
                       if (mobileno.equals("")) {
                           tv_mobileno.setText("NA");
                       } else {
                           tv_mobileno.setText(mobileno);
                       }
                       tv_dueamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(dueamnt + ""), decimal_check));
                       tv_amountpaid.setText(Globals.myNumberFormat2Price(Double.parseDouble(amount + ""), decimal_check));

                       getMinCalculation(unitid, intime, totalamount);
                       // edt_colectamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(totalamount + ""), decimal_check));


                   } else {
                       tv_slipno.setText(orderCode);
                       tv_vehicleno.setText(vehicleno);
                       tv_vehiclestatus.setText(orderstatus);
                       tv_intym.setText(intimesub);
                       tv_mobileno.setText(mobileno);


                       edt_colectamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(totalamount + ""), decimal_check));
                       tv_dueamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(totalamount + ""), decimal_check));
                       tv_amountpaid.setText(Globals.myNumberFormat2Price(Double.parseDouble("0" + ""), decimal_check));
                   }
                   tv_norecord.setVisibility(View.GONE);
                   ll_textlayout.setVisibility(View.VISIBLE);
                   btn_collect.setVisibility(View.VISIBLE);
                   btn_collect.setFocusable(true);
                   btn_collect.setEnabled(true);


               } else if (orderstatus.equals("CLOSE")) {
                   ll_textlayout.setVisibility(View.GONE);
                   btn_collect.setVisibility(View.GONE);
                   tv_norecord.setVisibility(View.VISIBLE);
                   tv_norecord.setText("This Vehicle already Out");
                   btn_collect.setEnabled(false);
                   btn_collect.setFocusable(false);
                   Snackbar.make(getActivity().findViewById(android.R.id.content),
                           "This Vehicle already Out", Snackbar.LENGTH_LONG).show();
               } else if (orderCode == null) {
                   ll_textlayout.setVisibility(View.GONE);
                   btn_collect.setVisibility(View.GONE);
                   tv_norecord.setVisibility(View.VISIBLE);
                   tv_norecord.setText("No Record Found");
                   btn_collect.setEnabled(false);
                   btn_collect.setFocusable(false);
               } else if (orderCode.length() == 0) {
                   ll_textlayout.setVisibility(View.GONE);
                   btn_collect.setVisibility(View.GONE);
                   tv_norecord.setVisibility(View.VISIBLE);
                   tv_norecord.setText("No Record Found");
                   btn_collect.setEnabled(false);
                   btn_collect.setFocusable(false);
               }


           }


           else if (cursor.getCount() == 0)
           {
               Snackbar.make(getActivity().findViewById(android.R.id.content),
                       "No Record Found", Snackbar.LENGTH_LONG).show();
           }
       else{

Globals.vehicleorderList=arrayListorder;

               if(Globals.vehicleorderList.size()>=1) {
                   Intent i = new Intent(getActivity(), VehicleOutList.class);
                   startActivity(i);
               }



       }



           // closing connection
           cursor.close();


        // db.close();


    }


    public void getMinCalculation(String unitid, String intime, String saleprice) {
        String outTime = Time.substring(11, 19);
        Date oldDate = null;
        try {
            oldDate = sdf.parse(intime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(oldDate);

        Date currentDate = new Date();

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Time = sdf.format(currentDate);
        try {
            currentDate = sdf.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (unitid.equals("1")) {
            long mills;
            mills = currentDate.getTime() - oldDate.getTime();
            int hours = (int) mills / (1000 * 60 * 60);
            int mins = (int) ((mills / (1000 * 60)) % 60);
            String diff = hours + ":" + mins;
            int converhr_min = hours * 60;
            int totalmin = converhr_min + mins;
            // Actual min calculation (Actual min % 60)
            int actualmin_value = totalmin % 60;

            // Inside  1 hour calculation

            min_calculation = MinCalculation.getAllMin_Calculation(getActivity(), "", database);
            if (min_calculation == null) {

                if (hours == 0) {
                    // Actual Value- Mod Value+Parameter Value
                    int min_value = totalmin - actualmin_value + 60;

                    int sale_price = Integer.parseInt(saleprice);
                    // Amount calculation based on Mins
                    int finalcollectvalue = sale_price * min_value / 60;

                    edt_colectamnt.setText(String.valueOf(finalcollectvalue));
                } else {
                    int sale_price = Integer.parseInt(saleprice);
                    // Amount calculation based on Mins
                    int finalcollectvalue = sale_price * totalmin / 60;
                    edt_colectamnt.setText(String.valueOf(finalcollectvalue));
                }


            } else {
                if (hours > 0) {
                    // Actual Value- Mod Value+Parameter Value
                    for (int i = 0; i < min_calculation.size(); i++) {
                        int range1 = Integer.parseInt(min_calculation.get(i).getRange1());
                        int range2 = Integer.parseInt(min_calculation.get(i).getRange2());
                        if (actualmin_value >= range1 && actualmin_value <= range2) {
                            String strcalc_value = min_calculation.get(i).getActualvalue();
                            int calculation_value = totalmin - actualmin_value + Integer.parseInt(strcalc_value);
                            int sale_price = Integer.parseInt(saleprice);
                            // Amount calculation based on Mins
                            int finalcollectvalue = sale_price * calculation_value / 60;

                            edt_colectamnt.setText(String.valueOf(finalcollectvalue));

                        }
                    }
                }
                else{

                    double salesprice= Double.parseDouble(saleprice);
                    int sale_price = (int)salesprice;
                   // int sale_price = Double.in(saleprice);
                    // Amount calculation based on Mins
                    int finalcollectvalue = sale_price ;
                   // int finalcollectvalue = sale_price * totalmin / 60;
                    edt_colectamnt.setText(String.valueOf(finalcollectvalue));
                }
            }
        } else if (unitid.equals("2")) {
            if (currentDate.getDate() == oldDate.getDate()) {
                edt_colectamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(saleprice + ""), decimal_check));

            }

            else if (currentDate.getDate() > (oldDate.getDate())) {
                long timeDiff = currentDate.getTime() - oldDate.getTime();

                int daysDifference = (int) (timeDiff / (1000 * 60 * 60 * 24));
                double salesprice= Double.parseDouble(saleprice);
                int sale_price = (int)salesprice;
                // Amount calculation based on Mins
                int finalcollectvalue = sale_price * daysDifference;
                edt_colectamnt.setText(String.valueOf(finalcollectvalue));
            }

        }

    }

    public void fill_spinner_pay_method(String s) {
        if (Globals.strContact_Code.equals("")) {
            paymentArrayList = Payment.getAllPayment(getActivity(), " WHERE is_active ='1'  and payment_id NOT IN (3,5)");
        } else {
            paymentArrayList = Payment.getAllPayment(getActivity(), " WHERE is_active ='1'");
        }

        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getActivity(), paymentArrayList);
        spn_paymentmode.setAdapter(paymentListAdapter);

        //  spn_paymentmode.setSelection(i);

        if (!s.equals("")) {
            for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                String iname = paymentArrayList.get(i).get_payment_name();
                if (s.equals(iname)) {
                    spn_paymentmode.setSelection(i);

                    break;
                }
            }
        }

    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK))
            // recreate your fragment here
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            VehicleOUT_Fragment fragobj = new VehicleOUT_Fragment();
            fragmentTransaction.add(R.id.content_parkingindustry_vehicleout, fragobj).addToBackStack(null).commit();
        }

    }*/

    private void showdialog() {
        listDialog = new Dialog(getActivity());
        listDialog.setTitle("Choose Option");
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customdialog, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(false);
        final Button btnprint = (Button) listDialog.findViewById(R.id.btn_print);
        final Button btnwhatsappshare = (Button) listDialog.findViewById(R.id.btn_whatsappShare);
        final Button btnclose = (Button) listDialog.findViewById(R.id.btn_close);
        btnwhatsappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runthread1();
                //  listDialog.dismiss();
            }
        });
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_remainingcode();
                listDialog.dismiss();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog.dismiss();
                ll_textlayout.setVisibility(View.GONE);
                btn_collect.setVisibility(View.GONE);
                edt_veh_mob.setText("");
                edt_colectamnt.setText("");

                Globals.setEmpty();
            }
        });

        listDialog.show();
    }
    private void runthread1(){

        final Dialog listDialog2 = new Dialog(getActivity());
        listDialog2.setTitle("File Share");
        LayoutInflater li1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
        listDialog2.setContentView(v1);
        listDialog2.setCancelable(false);
        final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
        edt_remark.setVisibility(View.GONE);
        Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
        btnButton.setText("Share");
        Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
        listDialog2.show();
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_remainingcode();
                listDialog2.dismiss();
                listDialog.dismiss();

            }
        });
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_mobileno.getText().toString().equals("")|| tv_mobileno.length()==0) {
                    call_remainingcode();
                    // Toast.makeText(getActivity(),"call rem code",Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                            startWhatsApp(tv_slipno.getText().toString());

                        }
                    });
                }
                listDialog2.dismiss();
            }
        });




    }

    private void startWhatsApp(String ordercode) {
        String strContct = "";
        String mobiileno=tv_mobileno.getText().toString();
        contact = Contact.getContact(getActivity(), database, db, " where is_active ='1' and contact_1='" +mobiileno+ "'");
        if (contact == null) {
        } else {

            if(Globals.objLPR.getCountry_Id().equals("99")) {

                strContct = "91"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("114")) {

                strContct = "965"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("221")) {
                strContct = "971"+contact.get_contact_1();
            }
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" +ordercode +"80mm" + ".pdf");
        // Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_SHORT).show();
        if (contactExists(getActivity(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file,getActivity(),strContct);


                } catch (Exception e) {
                    Globals.AppLogWrite("Contact Exception  "+e.getMessage());
                    /// Toast.makeText(getApplicationContext(),"Exception"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
                //call_remaining_code();
            }

        }
        else {

            if (SaveContact()) {
                Toast.makeText(getActivity(), "Contact Saved in Ur PhoneContacts!", Toast.LENGTH_SHORT).show();
                //  finish();
                // call_remaining_code();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file,getActivity(),strContct);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file,Context context,String contactnumbr) {
        Uri path = FileProvider.getUriForFile(context, "com.org.phomellolitepos.myfileprovider", file);
        String toNumber = contactnumbr;
        // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.putExtra("jid", toNumber + "@s.whatsapp.net");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
        // context.startActivity(sendIntent);
        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        if(!number.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null && cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            //  Toast.makeText(getApplicationContext(),app_installed+"App Installed",Toast.LENGTH_SHORT).show();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean SaveContact() {
        //Get text
        String szFirstname = contact.get_name(),
                szPhone = "+91" + " " + contact.get_contact_1();

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //Create a new contact entry!

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFirstname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, szFirstname) // Name of the person
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szPhone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        //INSERT PITURE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        stream.toByteArray())
                .build());

        Uri newContactUri = null;

        try {
            ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
            }
        } catch (RemoteException e) {
            // error
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            newContactUri = null;
        }
        return newContactUri != null;
    }
}
