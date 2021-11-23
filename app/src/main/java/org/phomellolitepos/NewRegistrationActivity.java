package org.phomellolitepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.CountryZone;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRegistrationActivity extends AppCompatActivity {
EditText edt_companyname,edt_firstname,edt_lastname,edt_email,edt_mobile,edt_address,edt_city,edt_password;
    SearchableSpinner searchablespinner_country,searchablespinner_zone,searchspinner_product;
    ProgressDialog pDialog;
    String Country_Id,ProductId,ZoneID;
    MenuItem menuItem;

    String project_id;
    ArrayList<String> countryId_list;

    Database db;
    String strResult = "";
    SQLiteDatabase database;
    JavaEncryption javaEncryption;
    Last_Code last_code;
    String licensecustomer;
    String registration_code,locationname;
    Lite_POS_Registration lite_pos_registration;
    Lite_POS_Device lpd;
    String result = "";
    AESHelper aesHelper;
    ArrayList<String> List = new ArrayList<String>();
    List<String> countryname_list;
    ArrayList<String> ZoneId_list;
    List<String> Zonename_list;
    String EmailId;

    String serial_no, android_id, myKey, device_id,imei_no;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration);
        searchablespinner_country = (SearchableSpinner) findViewById(R.id.spinnercountry);
        searchablespinner_zone = (SearchableSpinner) findViewById(R.id.spinnerZone);
        searchspinner_product = (SearchableSpinner) findViewById(R.id.spinnerproduct);
        edt_companyname=(EditText) findViewById(R.id.edt_company_name);
        edt_firstname=(EditText) findViewById(R.id.edt_first_name);
        edt_lastname=(EditText) findViewById(R.id.edt_last_name);
        edt_email=(EditText) findViewById(R.id.edt_email);
        edt_mobile=(EditText) findViewById(R.id.edt_mobile);
        edt_address=(EditText) findViewById(R.id.edt_address);
        edt_city=(EditText) findViewById(R.id.edt_city);
        edt_password=(EditText) findViewById(R.id.edt_password);
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

        try{
            getCountryZone();
        }
        catch(Exception e){

        }


        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRegistrationActivity.this, ActivateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        // TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
       // imei_no=mTelephony.getImei();
        Globals.serialno=serial_no;
        Globals.androidid=android_id;
        Globals.mykey=myKey;
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
//        int id = pref.getInt("id", 0);
//        if (id == 0) {
//            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
//        } else {
//            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
//        }
        aesHelper = new AESHelper();
        javaEncryption = new JavaEncryption();

    }



    public void getCountryZone(){
        pDialog = new ProgressDialog(NewRegistrationActivity.this);
        pDialog.setMessage(getString(R.string.getcountry));
        pDialog.show();
        String server_url=Globals.App_Lic_Base_URL+"/index.php?route=api/company_detail/get_country_zone";
        //String server_url = "http://192.168.2.72/opencart/pegasus/upload/index.php?route=api/company_detail/get_country_zone";
        /* HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {





                            countryId_list=new ArrayList<>();
                            countryname_list=new ArrayList<>();
                            ZoneId_list=new ArrayList<>();
                            Zonename_list=new ArrayList<>();


                            ArrayList<String> countryzid=new ArrayList<>();
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                pDialog.dismiss();
                                JSONArray jsonArray_country = responseObject.optJSONArray("result");
                                if (jsonArray_country.length() > 0) {
                                    CountryZone countryZone = null;
                                    ArrayList<CountryZone> country_zonelist;
                                    country_zonelist=new ArrayList<CountryZone>();

                                    for (int i = 0; i < jsonArray_country.length(); i++) {
                                        JSONObject jsonobject_country=jsonArray_country.getJSONObject(i);
                                        String country_id = jsonobject_country.getString("country_id");
                                        String country_name = jsonobject_country.getString("country_name");
                                        String isdcode = jsonobject_country.getString("isd_code");
                                        countryId_list.add(country_id);
                                        countryname_list.add(country_name);

                                       // country_zonelist.add(countryZone);
                                        JSONArray jsonArray_zone = jsonobject_country.getJSONArray("zone");
                                        for (int j = 0; j < jsonArray_zone.length(); j++) {
                                            JSONObject jsonObject_zone = jsonArray_zone.getJSONObject(j);
                                            String zoneid = jsonObject_zone.getString("zone_id");
                                            String countryid = jsonObject_zone.getString("country_id");
                                            String zonename = jsonObject_zone.getString("zone_name");
                                           countryzid.add(countryid);

                                               ZoneId_list.add(zoneid);
                                               Zonename_list.add(zonename);
/*

                                            countryZone.setZoneid(zoneid);
                                            countryZone.setZoneName(zonename);
                                            countryZone.setCountryZid(countryid);*/
                                     countryZone= new CountryZone(getApplicationContext(),country_id,country_name,zoneid,zonename,countryid);
                                            country_zonelist.add(countryZone);
                                        }

                                       /* countryZone.setCountryid(country_id);
                                        countryZone.setCountryname(country_name);
*/

                                    }



                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, countryname_list);
                                    searchablespinner_country.setAdapter(adapter);
                                    searchablespinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                                            // On selecting a spinner item
                                            ((TextView) adapter.getChildAt(0)).setTextColor(Color.BLACK);

                                            searchablespinner_country.setTitle("Select Country");
                                            searchablespinner_country.setPositiveButton("CLOSE");
                                           Country_Id = countryId_list.get(position);
                                           String countryid=countryId_list.get(position);
                                           String country_zid=countryzid.get(position);
                                           Zonename_list.clear();
                                           for(int i=1;i<country_zonelist.size();i++){

if(countryid.equals(country_zonelist.get(i).getCountryid())) {
    Zonename_list.add(country_zonelist.get(i).getZoneName());
    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(NewRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, Zonename_list);
    searchablespinner_zone.setAdapter(adapter1);

}
                                             //  adapter1.notifyDataSetChanged();

                                           }
                                            getProductList(Country_Id);
       // searchablespinner_zone.setSelection(position);




                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> arg0) {
                                            // TODO Auto-generated method stub
                                        }
                                    });

                                }

                                searchablespinner_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                                        // On selecting a spinner item
                                        ((TextView) adapter.getChildAt(0)).setTextColor(Color.BLACK);
                                        searchablespinner_zone.setTitle("Select Zone");
                                        searchablespinner_zone.setPositiveButton("CLOSE");
                                        ZoneID = ZoneId_list.get(position);



                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                });
                                //  pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();


                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {



        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getProductList(final String countryid){
        pDialog = new ProgressDialog(NewRegistrationActivity.this);
        pDialog.setMessage(getString(R.string.getproductlist));
        pDialog.show();
        String server_url=Globals.App_Lic_Base_URL+"/index.php?route=api/company_detail/get_license_product";
       // String server_url = "http://192.168.2.72/opencart/pegasus/upload/index.php?route=api/company_detail/get_license_product";
        /* HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            List<String> Productname_list;
                            List<String> ProductID_list;

                            Productname_list=new ArrayList<>();
                            ProductID_list=new ArrayList<>();
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                pDialog.dismiss();
                                JSONArray jsonArray_result = responseObject.optJSONArray("result");
                                if (jsonArray_result.length() > 0) {
                                    for (int i = 0; i < jsonArray_result.length(); i++) {
                                        JSONObject jsonobject_product=jsonArray_result.getJSONObject(i);
                                        String product_id = jsonobject_product.getString("lic_product_id");
                                        String product_name = jsonobject_product.getString("product_name");

                                        ProductID_list.add(product_id);
                                        Productname_list.add(product_name);

                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, Productname_list);
                                    searchspinner_product.setAdapter(adapter);
                                    searchspinner_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                                            // On selecting a spinner item
                                            searchspinner_product.setTitle("Select Product");
                                            searchspinner_product.setPositiveButton("CLOSE");
                                            try {
                                                ((TextView) adapter.getChildAt(0)).setTextColor(Color.BLACK);

                                            }
                                            catch(Exception e){

                                            }
                                            ProductId = ProductID_list.get(position);



                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> arg0) {
                                            // TODO Auto-generated method stub
                                        }
                                    });

                                }
                                //  pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                              //  Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                Productname_list.clear();
                                Productname_list.add(message);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewRegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, Productname_list);
                                searchspinner_product.setAdapter(adapter);
                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("master_product_id", Globals.master_product_id);
                params.put("country_id", countryid);
                System.out.println("params" + params);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(NewRegistrationActivity.this);
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

                }
            }
        });
        return true;
    }






    public void save(){
        String companyname=edt_companyname.getText().toString();
        String firstname= edt_firstname.getText().toString().trim();
        String lastname=edt_lastname.getText().toString().trim();
        String email=edt_email.getText().toString().trim();
        String mobile=edt_mobile.getText().toString().trim();
        String city=edt_city.getText().toString().trim();
        String address=edt_address.getText().toString().trim();
        String edpassword=edt_password.getText().toString().trim();
        String country=searchablespinner_country.getSelectedItem().toString();
        String zone=searchablespinner_zone.getSelectedItem().toString();
       // String product=searchspinner_product.getSelectedItem().toString();

        if(edt_companyname.getText().toString().length()==0){
            edt_companyname.setError("Enter Company Name");
            edt_companyname.requestFocus();
            return;
        }
        else if(edt_firstname.getText().toString().length()==0){
            edt_firstname.setError("Enter First Name");
            edt_firstname.requestFocus();
            return;
        }
        else if(edt_lastname.getText().toString().length()==0){
            edt_lastname.setError("Enter Last Name");
            edt_lastname.requestFocus();
            return;
        }
        else if(edt_email.getText().toString().length()==0){
            edt_email.setError("Enter Email");
            edt_email.requestFocus();
            return;
        }
        else if (!isValidEmail(edt_email.getText().toString())) {
            edt_email.setError(getString(R.string.Invalid_Email));
            edt_email.requestFocus();
            return;
        }
        else if(edt_mobile.getText().toString().length()==0){
            edt_mobile.setError("Enter mobile Number");
            edt_mobile.requestFocus();
            return;
        }
        else if(edt_address.getText().toString().length()==0){
            edt_address.setError("Enter Address");
            edt_address.requestFocus();
            return;
        }
        else if(edt_city.getText().toString().length()==0){
            edt_city.setError("Enter City");
            edt_city.requestFocus();
            return;
        }


        else if(edt_password.getText().toString().length()==0){
            edt_password.setError("Enter Password");
            edt_password.requestFocus();
            return;
        }
        else {
            if (isNetworkStatusAvialable(getApplicationContext())) {


                try {
                    postRegisterInfo(companyname, firstname, lastname, email, Country_Id, ZoneID, mobile, address, city, edpassword, ProductId);
                }
                catch(Exception e){

                }



        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }

        }
    }


    public void postRegisterInfo(final String companyname,final String firstname,final String lastname,final String email,final String countryid,
                                 final String zoneid,final String mobile,final String address,final String city,final String password,final String productid){
       ProgressDialog pDialog = new ProgressDialog(NewRegistrationActivity.this);
        pDialog.setMessage(getString(R.string.registering));
        pDialog.setCancelable(false);
        pDialog.show();
        String server_url=Globals.App_Lic_Base_URL+"/index.php?route=api/company_detail/registration";
       // String server_url = "http://192.168.2.72/opencart/pegasus/upload/index.php?route=api/company_detail/registration";
        /* HttpsTrustManager.allowAllSSL();*/
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {
                                JSONObject jsonobject_result = responseObject.getJSONObject("result");
                                  String reg_code=jsonobject_result.getString("reg_code");
                                Globals.reg_code=reg_code;
                                String email=jsonobject_result.getString("email");
                                EmailId= email;
                                String product_type=jsonobject_result.getString("product_type");
                                String password1=jsonobject_result.getString("password");
                                String nameadd=jsonobject_result.getString("name");
                                String datBase=jsonobject_result.getString("database");
                                String hostname=jsonobject_result.getString("host_name");
                                String userName=jsonobject_result.getString("user_name");
                                String dbPassword=jsonobject_result.getString("dbpassword");
                                String licenseKey=jsonobject_result.getString("license_key");

                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();

                                if (isNetworkStatusAvialable(getApplicationContext())) {

                                  if(product_type.equals("standalone")) {
                                      try {
                                          pDialog.dismiss();
                                          postDeviceRegister(email, reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey);

                                      } catch (Exception e) {
                                          System.out.println(e.getMessage());
                                      }
                                  }
                                  else{

                                      try {
                                          pDialog.dismiss();
                                          postcreateDatabaseServer(reg_code,hostname,userName,dbPassword,datBase,nameadd,password1,email);

                                      } catch (Exception e) {
                                          System.out.println(e.getMessage());
                                      }

                                  }
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                }



                                //  pDialog.dismiss();

                            } else if (status.equals("false")) {
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company", companyname);
                params.put("first_name", firstname);
                params.put("last_name", lastname);
                params.put("email", email);
                params.put("country_id", countryid);
                params.put("zone_id", zoneid);
                params.put("mobile", mobile);
                params.put("address", address);
                params.put("city", city);
                params.put("password", password);
                params.put("product_id", productid);
                System.out.println("params" + params);
                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    public void postDeviceRegister(final String email,final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4) {

        pDialog = new ProgressDialog(NewRegistrationActivity.this);
        pDialog.setMessage(getString(R.string.Activating));
        pDialog.setCancelable(false);
        pDialog.show();
        String server_url = Globals.App_IP_URL + "device/register";

        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            result = device_process(response,pDialog);
                            Globals.AppLogWrite(result);
                            if(result.equals("1")){
                                try {
                                    if(Globals.projectid.equals("cloud")) {
                                        Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                                        if (last_code == null) {
                                            postLastCodeFromServer(pDialog, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);
                                            // getLastCode(Globals.license_id, Globals.reg_code);

                                        }
                                    }
                                    else{
                                        pDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(NewRegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                } catch (Exception e) {

                                }

                            }
                            else if(result.equals("4")){
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.locationserver, Toast.LENGTH_SHORT).show();

                            }
                            else if(result.equals("5")){
                                if (Globals.responsemessage.toString().equals("Purchase More Device")) {

                                    try {
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(NewRegistrationActivity.this);

                                        builder.setTitle(getString(R.string.alerttitle));
                                        builder.setMessage(getString(R.string.alert_loginmsg));

                                        builder.setPositiveButton(getString(R.string.alert_posbtn), new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing but close the dialog
                                                pDialog.dismiss();
                                              //  getAlertclearTransactionDialog();

                                            }
                                        });

                                        builder.setNegativeButton(getString(R.string.alert_nobtn), new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                // Do nothing
                                                dialog.dismiss();
                                            }
                                        });

                                        androidx.appcompat.app.AlertDialog alert = builder.create();
                                        alert.show();

                                    } catch (Exception e) {

                                    }
                                }
                                else{

                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),Globals.responsemessage.toString(), Toast.LENGTH_SHORT).show();

                                }

                            }
                            else{
                                pDialog.dismiss();
                                Toast.makeText(getApplicationContext(),Globals.responsemessage.toString(), Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                System.out.println("params" + params);
                Globals.AppLogWrite("params"+params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
        Globals.AppLogWrite("response"+result);
        //return result;
    }

    public void postLastCodeFromServer(final ProgressDialog pDialog,final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String licensecustomerid) {

      /*  pDialog = new ProgressDialog(ActivateEmailActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "last_code";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            String result_lastcode= getLastCode(response);
                            if(result_lastcode.equals("1")) {
                                getInvoiceParameter_fromserver(pDialog, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey, Globals.license_id);
                            }
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                params.put("lic_customer_license_id", licensecustomerid);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getInvoiceParameter_fromserver(final ProgressDialog pDialog,String registration_code,String devicecode,String serial_no,String syscode2,String android_id,String myKey,String licensecustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "invoice_parameter";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getinvoiceparameter(response);

                            if(result.equals("1")) {


                                pDialog.dismiss();

                                AlertDialog.Builder builder = new AlertDialog.Builder(NewRegistrationActivity.this);

                                builder.setMessage("Thank You for registering with TriggerPOS. Check Registered Email for more detail/n Your Registration Code is : "+  Globals.reg_code)
                                        .setCancelable(true)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things

                                                Toast.makeText(getApplicationContext(), R.string.Activate_Successfully, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewRegistrationActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();



                            }
                            else{
                                pDialog.dismiss();
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("device_code", devicecode);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2",syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", licensecustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    public void postcreateDatabaseServer(final String registration_code, final String localhost, final String userName, final String dbpassword, final String data_base, final String namedb,final String passwrd,String emailid) {

      /*  pDialog = new ProgressDialog(ActivateEmailActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show();*/
       // http://74.208.235.72:85/trigger-pos-ar/index.php/api/register/create_database
        String server_url = Globals.App_IP_URL + "register/create_database";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject responseObject = new JSONObject(response);


                            String status = responseObject.getString("status");
                            final String message = responseObject.getString("message");
                            if (status.equals("true")) {


                                try {
                                    postDeviceRegister(EmailId, Globals.reg_code, Globals.Device_Code, serial_no, Globals.syscode2, android_id, myKey);

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", registration_code);
                params.put("host_name", localhost);
                params.put("user_name", userName);
                params.put("dbpassword", dbpassword);
                params.put("database", data_base);
                params.put("name", namedb);
                params.put("password", passwrd);
                params.put("email", emailid);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String device_process(String serverData,final ProgressDialog progressDialog) {

        String succ = "0";
        long u = 0;
        // Call device register api here

        // String serverData = device_on_server();

        database.beginTransaction();
        try {
            final JSONObject collection_jsonObject1 = new JSONObject(serverData);
            final String strStatus = collection_jsonObject1.getString("status");
            final String strMassage = collection_jsonObject1.getString("message");

            if (strStatus.equals("true")) {
                try {
                    JSONObject myResult = collection_jsonObject1.getJSONObject("result");

                    JSONObject jsonObject_device = myResult.optJSONObject("device");
                    JSONObject jsonObject_company = myResult.optJSONObject("company");

                    if (jsonObject_company.getString("project_id").equals("cloud")){

                        if (jsonObject_device.getString("location_name") == null || jsonObject_device.getString("location_name").equals("null") || jsonObject_device.getString("location_name").length() == 0) {
                            succ = "4";
                        }


                    }
                    if(!succ.equals("4"))  {
                        lpd = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                                jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                                jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                                jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                                jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                                jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                                jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN",jsonObject_device.getString("location_name"));


                        String Scale= jsonObject_device.getString("scale");
                        licensecustomer = jsonObject_device.getString("lic_customer_license_id");
                        long d = lpd.insertDevice(database);

                        if (d > 0) {
                            succ = "1";
                            String company_id = jsonObject_company.getString("company_id");
                            Globals.Company_Id = company_id;
                            registration_code = jsonObject_company.getString("registration_code");
                            Globals.reg_code = registration_code;
                            locationname=jsonObject_device.getString("location_name");
                            Globals.locname=locationname;
                            String company_name = jsonObject_company.getString("company_name");
                            String license_no;
                            try {
                                license_no = jsonObject_company.getString("license_no");
                            } catch (Exception ex) {
                                license_no = "";
                            }

                            String logo = jsonObject_company.getString("logo");
//                            String splash_screen = jsonObject_company.getString("splash_screen");
                            String contact_person = jsonObject_company.getString("contact_person");
                            String email = jsonObject_company.getString("email");
                            String country_id = jsonObject_company.getString("country_id");
                            String zone_id = jsonObject_company.getString("zone_id");
                            String mobile_no = jsonObject_company.getString("phone");
                            String address = jsonObject_company.getString("address");
                            project_id = jsonObject_company.getString("project_id");
                            String indus_type = jsonObject_company.getString("industry_type");
                            String srvc_trf = jsonObject_company.getString("service_code_tariff");
                            String password = jsonObject_company.getString("password");
                            String zonename = jsonObject_company.getString("zone_name");
                            String countryname = jsonObject_company.getString("country_name");


                            Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                            settings.set_Logo(logo);
                            settings.set_Scale(Scale);
                            settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);

                            lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name,
                                    contact_person, mobile_no, country_id, zone_id, password, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                            long r = lite_pos_registration.insertRegistration(database);
                            if (r > 0) {
                                for (int i = 0; i < List.size(); i++) {

                                    String name = List.get(i);
                                    if (strResult.equals("")) {
                                        strResult = null;
                                    }
                                    strResult = strResult + "," + name;

                                }
                                succ = "1";
                                JSONArray jsonArray = myResult.getJSONArray("company_user");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject_user = jsonArray.getJSONObject(i);
                                    user = new User(getApplicationContext(), null,
                                            jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", jsonObject_user.getString("app_user_permission"));
                                    u = user.insertUser(database);
                                }
                                if (u > 0) {
                                    succ = "1";
                                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("pass", user.get_password()); // Storing string
                                    editor.apply();
                                }
                            } else {
                            }
                        } else {
                        }
                    }
                } catch(JSONException jex){
                    progressDialog.dismiss();
                } catch(Exception e){
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

                if (succ.equals("1")) {
                    Globals.license_id = licensecustomer;
                    Globals.reg_code = registration_code;
                    Globals.projectid=project_id;
                       /* database.setTransactionSuccessful();
                        database.endTransaction();*/
                } else {
                       /* database.setTransactionSuccessful();
                        database.endTransaction();*/
                }
            } else if (strStatus.equals("false")) {
                succ = "5";
                Globals.responsemessage = strMassage;
            }


        } catch(JSONException e){
            Globals.AppLogWrite("Server Json exception"+ e.getMessage());

            progressDialog.dismiss();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        return succ;
    }

    private String getLastCode(String serverData) {

        String succ="0";


        try {
            database.beginTransaction();

            JSONObject collection_jsonObject1 = new JSONObject(serverData);
            String strStatus = collection_jsonObject1.getString("status");
            String massage = collection_jsonObject1.getString("message");

            if (strStatus.equals("true")) {

                JSONObject jsonObject = collection_jsonObject1.getJSONObject("result");

                String last_order_code = jsonObject.getString("last_order_code");
                String last_pos_balance_code = jsonObject.getString("last_pos_balance_code");
                String last_z_close_code = jsonObject.getString("last_z_close_code");
                String last_order_return_code = jsonObject.getString("last_order_return_code");

                long l = Last_Code.delete_Last_Code(getApplicationContext(), null, null, database);
                if (l > 0) {
                } else {
                }

                last_code = new Last_Code(getApplicationContext(), null, last_order_code, last_pos_balance_code, last_z_close_code, last_order_return_code);

                long d = last_code.insertLast_Code(database);

                if (d > 0) {

                    succ="1";
                    database.setTransactionSuccessful();
                    database.endTransaction();
                }
            } else {
                succ="2";
                database.endTransaction();
            }
        }
        catch (JSONException e){}
        return succ;
    }

    private String getinvoiceparameter(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        Lite_POS_Registration  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        Lite_POS_Device  lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(),"", database);

        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONObject jsonobj_bg = jsonObject_bp.getJSONObject("result");
                String gst_number=jsonobj_bg.getString("gst_number");
                String inv_address=jsonobj_bg.getString("invoice_address");
                String shortcompanyname=jsonobj_bg.getString("company_name");
                String industry_type=jsonobj_bg.getString("industry_type");
                String locationid=jsonobj_bg.getString("location_id");
                String locationname=jsonobj_bg.getString("location_name");

                lite_pos_device.setLocation_Code(locationid);
                lite_pos_device.setLocation_name(locationname);
                long dt = lite_pos_device.updateDevice("Id=?", new String[]{"1"}, database);
                if (dt > 0) {

                }
                lite_pos_registration.setAddress(inv_address);
                lite_pos_registration.setService_code_tariff(gst_number);
                lite_pos_registration.setShort_companyname(shortcompanyname);
                lite_pos_registration.setIndustry_Type(industry_type);
                long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                if(ct>0){
                    succ_manu="1";
                }


            } else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
            else{
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }
    private boolean isNetworkStatusAvialable(Context applicationContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewRegistrationActivity.this, ActivateEmailActivity.class);
        startActivity(intent);
        finish();

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}