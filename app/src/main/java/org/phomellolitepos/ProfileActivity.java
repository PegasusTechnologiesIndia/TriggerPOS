package org.phomellolitepos;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Zone;

public class ProfileActivity extends AppCompatActivity {
    EditText txt_email, txt_res_code, txt_exp, txt_device_code, txt_loc_code;
    EditText txt_contact_person, txt_license_no, txt_mobile, txt_address, txt_device_no, txt_company_name;
    Button btn_upd_exp, btn_upd_profile, btn_get_profile;
    SQLiteDatabase database;
    Database db;

    ProgressDialog progressDialog;
    String str;
    String flag = "";
    TextView spn_country, spn_zone;
    Country country;
    Zone zone;
    ArrayList<Country> arrayCList;
    ArrayList<Zone> arrayZList;
    String strSelectedZoneCode, strSelectedCountryCode;
    //    AESHelper aesHelper;
    Settings settings;
    PackageInfo pInfo = null;
    JavaEncryption javaEncryption;
    Lite_POS_Registration lite_pos_registration;
    Lite_POS_Device lite_pos_device;
    ProgressDialog pDialog;
    String serial_no, android_id, myKey, device_id,imei_no;
    Button btn_change_ip;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        aesHelper = new AESHelper();
        javaEncryption = new JavaEncryption();
        getSupportActionBar().setTitle(R.string.Profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        final Intent intent = getIntent();
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
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

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
      /*  device_id = telephonyManager.getDeviceId();
        imei_no=telephonyManager.getImei();*/
        try {
            flag = intent.getStringExtra("flag");
            if (flag.equals("null")) {
                flag = "profile";
            }
        } catch (Exception ex) {
            flag = "profile";
        }
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        if (flag.equals("login")) {


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.Wait_msg));
                    progressDialog.show();
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {

                              //  sleep(100);
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                }
            });
        } else {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(lite_pos_registration.getIndustry_Type().equals("2")){
                        Intent intent = new Intent(ProfileActivity.this, Retail_IndustryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();

                    }
                   else if(lite_pos_registration.getIndustry_Type().equals("3")){
                        Intent intent = new Intent(ProfileActivity.this, PaymentCollection_MainScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();

                    }
                    else if(lite_pos_registration.getIndustry_Type().equals("4")){
                        Intent intent = new Intent(ProfileActivity.this, ParkingIndustryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();

                    }
                    else {
                        if (settings.get_Home_Layout().equals("0")) {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            finish();
                        } else if (settings.get_Home_Layout().equals("2")) {
                            try {
                                Intent intent = new Intent(ProfileActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivity(intent);
                                finish();
                            } finally {
                            }
                        } else {
                            Intent intent = new Intent(ProfileActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }

        txt_company_name = (EditText) findViewById(R.id.txt_company_name);
        txt_contact_person = (EditText) findViewById(R.id.txt_contact_person);
        txt_license_no = (EditText) findViewById(R.id.txt_license_no);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_mobile = (EditText) findViewById(R.id.txt_mobile);
        txt_address = (EditText) findViewById(R.id.txt_address);
        txt_res_code = (EditText) findViewById(R.id.txt_res_code);
        txt_device_code = (EditText) findViewById(R.id.txt_device_code);
        txt_device_no = (EditText) findViewById(R.id.txt_device_no);
        txt_loc_code = (EditText) findViewById(R.id.txt_loc_code);
        txt_exp = (EditText) findViewById(R.id.txt_exp);
        btn_upd_exp = (Button) findViewById(R.id.btn_upd_exp);
        btn_upd_profile = (Button) findViewById(R.id.btn_upd_profile);
        btn_get_profile = (Button) findViewById(R.id.btn_get_profile);
        spn_country = (EditText) findViewById(R.id.spn_country);
        spn_zone = (EditText) findViewById(R.id.spn_zone);
      //  btn_upd_profile.setVisibility(View.GONE);
        txt_company_name.setText(lite_pos_registration.getCompany_Name());
        txt_contact_person.setText(lite_pos_registration.getContact_Person());
        txt_license_no.setText(lite_pos_registration.getService_code_tariff());
        txt_email.setText(lite_pos_registration.getEmail());
        txt_mobile.setText(lite_pos_registration.getMobile_No());
        txt_address.setText(lite_pos_registration.getAddress());
        txt_res_code.setText(lite_pos_registration.getRegistration_Code());
        txt_address.setText(lite_pos_registration.getAddress());
        txt_res_code.setText(lite_pos_registration.getRegistration_Code());
        btn_change_ip=(Button)findViewById(R.id.btn_get_changeip);
        try {
            if (flag.equals("login")) {

                btn_upd_profile.setVisibility(View.GONE);
                btn_get_profile.setVisibility(View.GONE);
                btn_upd_exp.setVisibility(View.VISIBLE);
                btn_change_ip.setVisibility(View.VISIBLE);
            } else if (flag.equals("profile")) {
                btn_upd_profile.setVisibility(View.GONE);
                btn_get_profile.setVisibility(View.GONE);
                btn_upd_exp.setVisibility(View.GONE);
                btn_change_ip.setVisibility(View.GONE);

            }
        }
        catch(Exception e){

        }
        try {
            lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);

            txt_device_no.setText(lite_pos_device.getDevice_Name());
            txt_device_code.setText(lite_pos_device.getDevice_Code());
            txt_loc_code.setText(lite_pos_device.getLocation_name());
        } catch (Exception e) {

        }
        try {
            String ab = javaEncryption.decrypt(lite_pos_device.getExpiry_Date(), "12345678");
            ab = ab;
            txt_exp.setText(javaEncryption.decrypt(lite_pos_device.getExpiry_Date(), "12345678"));
        } catch (Exception e) {
            e.printStackTrace();
        }


       // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        String strCheck = lite_pos_registration.getproject_id();

        String cntryname = "";
        try {
            cntryname = lite_pos_registration.getCountry_name();
            spn_country.setText(cntryname);
          //  country = Country.getCountry(getApplicationContext(), "WHERE country_id ='" + cntryid + "'", database);
           /* if (country == null) {
              //  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                country = Country.getCountry(getApplicationContext(), "WHERE country_id='" + lite_pos_registration.getCountry_Id() + "'", database);
                get_country(country.get_name(), lite_pos_registration.getCountry_Id());
            } else {
                get_country(country.get_name(), cntryid);
            }*/
        } catch (Exception ex) {
            ex.getStackTrace();
        }


        btn_change_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog listDialog1 = new Dialog(ProfileActivity.this);
                listDialog1.setTitle(R.string.Select_Contact);
                LayoutInflater li1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.custom_layout_ipchange, null, false);
                listDialog1.setContentView(v1);
                listDialog1.setCancelable(true);


                final EditText edt_ip = (EditText) listDialog1.findViewById(R.id.edt_ipchange);
                final EditText edt_password = (EditText) listDialog1.findViewById(R.id.edt_ippassword);
                Button btn_save = (Button) listDialog1.findViewById(R.id.btn_save);

                listDialog1.show();
                // fill_dialog_contact_List(contact_title, list11, strSelectedCategory, strFiltr);
                Window window = listDialog1.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Settings settings = Settings.getSettings(getApplicationContext(), database, "");

                edt_ip.setText(settings.getApi_Ip());
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        edt_password.setVisibility(View.VISIBLE);


                        if(edt_ip.getText().toString().length()==0){
                            edt_ip.setError("Enter Ip Address");
                            return;
                        }
                        else if(edt_password.getText().toString().length()==0){
                            edt_password.setError("Enter Password");
                            return;
                        }
                        else if(!edt_password.getText().toString().equals(pInfo.versionName)){
                            Toast.makeText(getApplicationContext(),"Please Enter Password to proceed",Toast.LENGTH_LONG).show();
                            edt_password.setError("Wrong Password");
                            return;
                        }
                        else if(edt_password.getText().toString().equals(pInfo.versionName)){
                            Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                            settings.setApi_Ip(String.valueOf(edt_ip.getText().toString()));
                            settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);
                            listDialog1.dismiss();
                        }


                    }
                });
            }
        });
        try {
            String zonename = lite_pos_registration.getZone_name();
            spn_zone.setText(zonename);
           /* zone = Zone.getZone(getApplicationContext(), "WHERE zone_id='" + zoneid + "'");
            if (zone == null) {
                //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                zone = Zone.getZone(getApplicationContext(), "WHERE zone_id='" + lite_pos_registration.getZone_Id() + "'");
                get_zone(lite_pos_registration.getCountry_Id());
            } else {
                get_zone(cntryid);
            }*/
        } catch (Exception ex) {
            ex.getStackTrace();
        }


        /*spn_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Zone resultp = arrayZList.get(i);
                    strSelectedZoneCode = resultp.get_zone_id();
                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spn_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Country resultp = arrayCList.get(i);
                    strSelectedCountryCode = resultp.get_country_id();

                    get_zone(strSelectedCountryCode);

                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
*/
        btn_get_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {

                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage("Getting Profile....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            check_device();
                        }
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_upd_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage("Updating Profile....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        database.beginTransaction();
                       // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                       // lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                        lite_pos_registration.setCompany_Name(txt_company_name.getText().toString());
                        lite_pos_registration.setContact_Person(txt_contact_person.getText().toString());
                        lite_pos_registration.setService_code_tariff(txt_license_no.getText().toString());
                        lite_pos_registration.setMobile_No(txt_mobile.getText().toString());
                        lite_pos_registration.setAddress(txt_address.getText().toString());
                        lite_pos_registration.setCountry_Id(strSelectedCountryCode);
                        lite_pos_registration.setZone_Id(strSelectedZoneCode);
                        lite_pos_device.setDevice_Name(txt_device_no.getText().toString());
                        long l =  lite_pos_registration.updateRegistration("Id=?", new String[]{ lite_pos_registration.getId()}, database);
                        if (l > 0) {
                            long l1 =  lite_pos_device.updateDevice("Id=?", new String[]{ lite_pos_device.getId()}, database);
                            if (l > 0) {
                                database.setTransactionSuccessful();
                                database.endTransaction();
                                if (isNetworkStatusAvialable(getApplicationContext())) {
                                    update_profile_process();
                                } else {
                                }
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.Profile_Update, Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(ProfileActivity.this, SplashScreen.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            database.endTransaction();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.Profile_Not_Update, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }.start();


            }
        });

        btn_upd_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage(getString(R.string.Updating_License));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {

                            String existing_expiry = "";
                            try {
                                existing_expiry = javaEncryption.encrypt( lite_pos_device.getExpiry_Date(), "12345678");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                postDeviceCheckLicense(progressDialog,lite_pos_registration.getRegistration_Code(),Globals.Device_Code,lite_pos_registration.getEmail(),serial_no,Globals.syscode2,android_id,myKey,existing_expiry);
                            }
                            catch(Exception e){
                 System.out.println(e.getMessage());
                            }
                            // progressDialog.dismiss();
                        }
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void update_profile_process() {

        String serverData ="";
                //= profile_on_server();
        try {
            JSONObject collection_jsonObject1 = new JSONObject(serverData);
            final String strStatus = collection_jsonObject1.getString("status");

            if (strStatus.equals("true")) {
                JSONObject jsonObject_result = collection_jsonObject1.getJSONObject("result");
                JSONObject jsonObject = jsonObject_result.getJSONObject("device");

                lite_pos_device.setCurreny_Symbol(jsonObject.getString("currency_symbol"));
                lite_pos_device.setDecimal_Place(jsonObject.getString("decimal_place"));

                lite_pos_device.updateDevice("id=?", new String[]{lite_pos_device.getId()}, database);
            } else {

            }
        } catch (JSONException e) {
        }
    }

   /* private String profile_on_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "register/update");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("device_code", lite_pos_device.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("license_no", lite_pos_registration.getLicense_No()));
        nameValuePairs.add(new BasicNameValuePair("contact_person", lite_pos_registration.getContact_Person()));
        nameValuePairs.add(new BasicNameValuePair("mobile_no", lite_pos_registration.getMobile_No()));
        nameValuePairs.add(new BasicNameValuePair("country_id", lite_pos_registration.getCountry_Id()));
        nameValuePairs.add(new BasicNameValuePair("zone_id", lite_pos_registration.getZone_Id()));
        nameValuePairs.add(new BasicNameValuePair("address",lite_pos_registration.getAddress()));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/


    private void update_license(String serverData ,String existing_expiry, final ProgressDialog progressDialog) {
        // Call check license api here

        try {
            final JSONObject jsonObject1 = new JSONObject(serverData);
            final String strStatus = jsonObject1.getString("status");
            final String msg = jsonObject1.getString("message");
            if (strStatus.equals("true")) {
                try {
                    JSONObject myResult = jsonObject1.getJSONObject("result");
                    JSONObject myResult1 = myResult.getJSONObject("device");
                    String device_code = myResult1.getString("device_code");
                    String expiry_date = myResult1.getString("expiry_date");
                    String duration = myResult1.getString("duration");
                    if (existing_expiry.equals(expiry_date)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.Not_Update_Found, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        lite_pos_device.setExpiry_Date(javaEncryption.encrypt(expiry_date, "12345678"));
                        long l = lite_pos_device.updateDevice("device_code=?", new String[]{device_code}, database);
                        if (l > 0) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.License_Not_Updated, Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                } catch (JSONException jex) {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (strStatus.equals("false")) {
                progressDialog.dismiss();
                try {
//                    JSONObject myResult = jsonObject1.getJSONObject("result");
//                    JSONObject myResult1 = myResult.getJSONObject("device");
//                    String device_code = myResult1.getString("device_code");
//                    String expiry_date = myResult1.getString("expiry_date");
//                    String duration = myResult1.getString("duration");
//                    if (existing_expiry.equals(expiry_date)) {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(), R.string.Not_Update_Found, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                       lite_pos_device.setExpiry_Date(javaEncryption.encrypt("2020-07-10 09:58:21", "12345678"));
//                        long l = lite_pos_device.updateDevice("device_code=?", new String[]{device_code}, database);
//                        if (l > 0) {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
////                                    Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getApplicationContext(), R.string.Updated_Successfully, Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), R.string.License_Not_Updated, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
                    Toast.makeText(getApplicationContext(),jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (JSONException jex)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), jex+"", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

/*
    private String Update_licence_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "device/check_license");
        ArrayList nameValuePairs = new ArrayList(7);
        try {
            try {
                str = lite_pos_registration.getEmail();
            } catch (Exception ex) {
                str = "null";
            }
            if (str.equals("null")) {
            } else {
                nameValuePairs.add(new BasicNameValuePair("email", lite_pos_registration.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
                nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
                nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
                nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
                nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
                nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }
*/

    public void postDeviceCheckLicense(final ProgressDialog pDialog,final String registration_code,final String devicecode,final String email,final String syscode1,final String syscode2,final String syscode3,final String syscode4,String existing_expiry) {

      /*  pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage(getString(R.string.loggingin));
        pDialog.show()*/;
        String server_url= Globals.App_IP_URL + "device/check_license";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            update_license(response,existing_expiry,pDialog);
                            pDialog.dismiss();
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
                params.put("email", email);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", syscode2);
                params.put("sys_code_3", syscode3);
                params.put("sys_code_4", syscode4);
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (flag.equals("login")) {

            progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.Wait_msg));
            progressDialog.show();

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);

                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                    }
                }
            };
            timerThread.start();

        } else {

            if(lite_pos_registration.getIndustry_Type().equals("2")){
                Intent intent = new Intent(ProfileActivity.this, Retail_IndustryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                finish();

            }
            else if(lite_pos_registration.getIndustry_Type().equals("3")){
                Intent intent = new Intent(ProfileActivity.this, PaymentCollection_MainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                finish();

            }
            else if(lite_pos_registration.getIndustry_Type().equals("4")){
                Intent intent = new Intent(ProfileActivity.this, ParkingIndustryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                finish();

            }
            else {

                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (settings.get_Home_Layout().equals("2")) {
                    try {
                        Intent intent = new Intent(ProfileActivity.this, RetailActivity.class);
                        startActivity(intent);
                        finish();
                    } finally {
                    }
                } else {
                    Intent intent = new Intent(ProfileActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }


    }

  /*  private void get_country(String name, String id) {
        arrayCList = country.getAllCountry(getApplicationContext(), "");
        CountryAdapter countryAdapter = new CountryAdapter(getApplicationContext(), arrayCList);
        spn_country.setAdapter(countryAdapter);
        if (!name.equals("")) {
            for (int i = 0; i < countryAdapter.getCount(); i++) {
//                int h = (int) spn_country.getAdapter().getItem(i);
                String iname = arrayCList.get(i).get_name();
                if (name.equals(iname)) {
                    spn_country.setSelection(i);
                    break;
                }
            }
        }
    }

    private void get_zone(String strCountryCode) {

        arrayZList = zone.getAllZone(getApplicationContext(), "WHERE country_id = '" + strCountryCode + "'");

        ZoneAdapter countryAdapter1 = new ZoneAdapter(getApplicationContext(), arrayZList);
        spn_zone.setAdapter(countryAdapter1);
        if (!zone.get_name().equals("")) {
            for (int i = 0; i < countryAdapter1.getCount(); i++) {
//                int h = (int) spn_zone.getAdapter().getItemId(i);
                String iname = arrayZList.get(i).get_name();
                if (zone.get_name().equals(iname)) {
                    spn_zone.setSelection(i);
                    break;
                }
            }
        }
    }
*/
    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }

        return false;
    }

    private void check_device() {
        // Call check license api here
        String succ = "0";
        String serverData ="";
                //getting_profile_server();
        try {
            final JSONObject jsonObject1 = new JSONObject(serverData);
            final String strStatus = jsonObject1.getString("status");
            final String msg = jsonObject1.getString("message");
            if (strStatus.equals("true")) {
                try {

                    database.beginTransaction();
                    JSONObject jsonObject_result = jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    JSONObject jsonObject_company = jsonObject_result.optJSONObject("company");

                    String company_id = jsonObject_company.getString("company_id");
                    Globals.Company_Id = company_id;
                    String registration_code = jsonObject_company.getString("registration_code");
                    String company_name = jsonObject_company.getString("company_name");
                    String license_no;
                    try {
                        license_no = jsonObject_company.getString("license_no");
                    } catch (Exception ex) {
                        license_no = "";
                    }

                    String contact_person = jsonObject_company.getString("contact_person");
                    String email = jsonObject_company.getString("email");
                    String country_id = jsonObject_company.getString("country_id");
                    String zone_id = jsonObject_company.getString("zone_id");
                    String mobile_no = jsonObject_company.getString("mobile_no");
                    String address = jsonObject_company.getString("address");
                    String project_id = jsonObject_company.getString("project_id");
                    String srvc_trf = jsonObject_company.getString("service_code_tariff");
                    String indus_type = jsonObject_company.getString("industry_type");
                    String logo = jsonObject_company.getString("logo");
                    String zonename = jsonObject_company.getString("zone_name");
                    String countryname = jsonObject_company.getString("country_name");

                    Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);

                    lite_pos_device
                            = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"), jsonObject_device.getString("lic_customer_license_id"), jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"), jsonObject_device.getString("license_type"), "IN",jsonObject_device.getString("location_name"));

                    long d = lite_pos_device.insertDevice(database);
                    if (d > 0) {
                        succ = "1";

                    }

                    long l2 = Lite_POS_Registration.delete_Registration(getApplicationContext(), "Lite_POS_Registration", null, null, database);

                   lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name, contact_person,
                            mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                    long r = lite_pos_registration.insertRegistration(database);
                    if (r > 0) {
                        succ = "1";
                    }

                    if (succ.equals("1")) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Profile get successful", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(ProfileActivity.this, SplashScreen.class);
                                startActivity(intent1);
                                finish();

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        database.endTransaction();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException jex) {
                    progressDialog.dismiss();
                    database.endTransaction();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (strStatus.equals("false")) {


                if (msg.equals("Device Not Found")) {

                    Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                    lite_pos_device.setStatus("Out");
                    long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                    if (ct > 0) {

                        Intent intent_category = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent_category);
                        finish();
                    }


                }
            }

  /*                  database.beginTransaction();
                    JSONObject jsonObject_result = jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    JSONObject jsonObject_company = jsonObject_result.optJSONObject("company");

                    String company_id = jsonObject_company.getString("company_id");
                    Globals.Company_Id = company_id;
                    String registration_code = jsonObject_company.getString("registration_code");
                    String company_name = jsonObject_company.getString("company_name");
                    String license_no;
                    try {
                        license_no = jsonObject_company.getString("license_no");
                    } catch (Exception ex) {
                        license_no = "";
                    }
                    String contact_person = jsonObject_company.getString("contact_person");
                    String email = jsonObject_company.getString("email");
                    String country_id = jsonObject_company.getString("country_id");
                    String zone_id = jsonObject_company.getString("zone_id");
                    String mobile_no = jsonObject_company.getString("mobile_no");
                    String address = jsonObject_company.getString("address");
                    String project_id = jsonObject_company.getString("project_id");
                    String srvc_trf = jsonObject_company.getString("service_code_tariff");
                    String indus_type = jsonObject_company.getString("industry_type");
                    String logo = jsonObject_company.getString("logo");

                    Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);

                    lite_pos_device = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"),jsonObject_device.getString("lic_customer_license_id"),jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"),jsonObject_device.getString("license_type"),"IN");

                    long d = lite_pos_device.insertDevice(database);
                    if (d > 0) {
                        succ = "1";

                    }

                    long l2 = Lite_POS_Registration.delete_Registration(getApplicationContext(), "Lite_POS_Registration", null, null, database);

                    lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name, contact_person,
                            mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type);
                    long r = lite_pos_registration.insertRegistration(database);
                    if (r > 0) {
                        succ = "1";
                    }

                    if (succ.equals("1")) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Profile get successful", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(ProfileActivity.this, SplashScreen.class);
                                startActivity(intent1);
                                finish();

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        database.endTransaction();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException jex) {
                    progressDialog.dismiss();
                    database.endTransaction();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }*/

            } catch (JSONException e) {
        progressDialog.dismiss();
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
        }

   /* private String getting_profile_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "device/check_license");
        ArrayList nameValuePairs = new ArrayList(7);
        try {
            nameValuePairs.add(new BasicNameValuePair("email", lite_pos_registration.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
            nameValuePairs.add(new BasicNameValuePair("reg_code", lite_pos_registration.getRegistration_Code()));
            nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
            nameValuePairs.add(new BasicNameValuePair("sys_code_2", "4"));
            nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
            nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
//            nameValuePairs.add(new BasicNameValuePair("lic_code","61700"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/
}
