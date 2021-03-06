package org.phomellolitepos;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.*;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Zone;

import sunmi.bean.SecondScreenData;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.Data;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreen extends AppCompatActivity {
    Lite_POS_Registration lite_pos_registration;
    Lite_POS_Device lite_pos_device;
    String deviceFlag = "0";
    User user;
    Country country;
    Address_Category address_category;
    Address_Type address_type;
    Payment payment;
    Bussiness_Group bussiness_group;
    Zone zone;
    Order_Type order_type;
    Unit unit;
    SQLiteDatabase database;
    Database db;
    Last_Code last_code;
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    ProgressBar progressBar;
    private int STORAGE_PERMISSION_CODE = 23;
    Boolean Flag = false;
    String date;
    AESHelper aesHelper;
    JavaEncryption javaEncryption;
    private static final int PERMISSION_REQUEST_CODE = 200;
    String serial_no, android_id, myKey, device_id,imei_no;
    private ISendCallback callback = new ISendCallback() {

        @Override
        public void onSendFail(int arg0, String arg1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onSendProcess(long arg0, long arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSendSuccess(long arg0) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                }
            });
        }
    };

    private IConnectionCallback mConnCallback = new IConnectionCallback() {

        @Override
        public void onDisConnect() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

        }

        @Override
        public void onConnected(final ConnState state) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case AIDL_CONN:
                            break;
                        case VICE_SERVICE_CONN:
                            break;
                        case VICE_APP_CONN:
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    };


    // ???????????????????????????
    private IReceiveCallback mReceiveCallback = new IReceiveCallback() {

        @Override
        public void onReceiveFile(DSFile arg0) {
            // TODO
        }

        @Override
        public void onReceiveFiles(DSFiles dsFiles) {
            // TODO
        }

        @Override
        public void onReceiveData(DSData data) {
            if (dsPacket == null) return;
            long taskId = dsPacket.getData().taskId;
            Gson gson = new Gson();
            Data data4Json = gson.fromJson(data.data, Data.class);
            if (taskId == data.taskId) {
                final SecondScreenData secondScreenData = gson.fromJson(data4Json.data, SecondScreenData.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }

        @Override
        public void onReceiveCMD(DSData arg0) {
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        try {
            if (isReadStorageAllowed()) {
                create_database();
                return;
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestStoragePermission();
                } else {
                    try {
                        create_database();
                    }
                    catch(Exception e){

                    }
                }
            }
        }
        catch(Exception e){
            Toast.makeText(SplashScreen.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            finishAffinity();
            System.exit(0);
        }
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
        device_id = telephonyManager.getDeviceId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei_no=telephonyManager.getImei();
        }
//

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2== PackageManager.PERMISSION_GRANTED ;
    }

    private void create_database() {
        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();




      /*  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);*/

        Thread timerThread = new Thread() {
            public void run() {
                try {
                  //  sleep(100);
                    javaEncryption = new JavaEncryption();
                    Date d = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = format.format(d);
                    Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                    if (settings == null) {
                        InputStream ins = SplashScreen.this.getResources().openRawResource(
                                R.raw.litepos);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
                        try {
                            String line = null;
                            String sql = "";
                            while ((line = reader.readLine()) != null) {
                                sql += line;
                            }
                            ins.close();
                            runInsert(database, sql);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                     //  copyFiletoExternalStorage(R.raw.triggerpos);


   /*                     if (db.version > 79) {
                            InputStream ins = SplashScreen.this.getResources().openRawResource(
                                    R.raw.litepos);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
                            try {
                                String line = null;
                                String sql = "";
                                while ((line = reader.readLine()) != null) {
                                    sql += line;
                                }
                                ins.close();
                                runInsert(database, sql);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            copyResources(R.raw.triggerpos);
                        }*/

                    }


                    country = Country.getCountry(getApplicationContext(), "", database);
                    if (country == null) {
                        try {
                            read_country();
                        }
                        catch(Exception e){

                        }
                        try {
                            read_zone();
                        }
                        catch(Exception e){

                        }
                        try {
                            read_defult();
                        }
                        catch(Exception e){}
                    }

                   /* try {
                        read_defult();
                    }
                    catch(Exception e){}*/

                  //  Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                    if (settings == null) {
                        database.beginTransaction();
                        settings = new Settings(getApplicationContext(), null, "false", "0", "", "", "", "", "", "", "", "", "", "", "", "0", "false", "false", "0", "Powered By Phomello", "0", "", "", "false", "false", "false", "TRN", "TAX INVOICE", "Salesperson", "Invoice Number", "Invoice Date", "Device ID", "false", "false", "false", "", "", "AC", "false", "false", "0", "false", "true", "false", "1", "GST", "false", "false", "false", "false", "1", "0","false","0","false","false","false","false","false","false","false","false","false","false","false","74.208.235.72:85","false","","");
                        long l = settings.insertSettings(database);

                        if (l > 0) {
                            database.setTransactionSuccessful();
                            database.endTransaction();
                            Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                            Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                            Globals.strIsDiscountPrint = settings.get_Is_Discount();
                            Globals.GSTNo = settings.get_Gst_No();
                            Globals.GSTLbl = settings.get_GST_Label();
                            Globals.PrintOrder = settings.get_Print_Order();
                            Globals.PrintCashier = settings.get_Print_Cashier();
                            Globals.PrintInvNo = settings.get_Print_InvNo();
                            Globals.PrintInvDate = settings.get_Print_InvDate();
                            Globals.PrintDeviceID = settings.get_Print_DeviceID();
                        }
                    } else {
                        Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                        Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                        Globals.strIsDiscountPrint = settings.get_Is_Discount();
                        Globals.GSTNo = settings.get_Gst_No();
                        Globals.GSTLbl = settings.get_GST_Label();
                        Globals.PrintOrder = settings.get_Print_Order();
                        Globals.PrintCashier = settings.get_Print_Cashier();
                        Globals.PrintInvNo = settings.get_Print_InvNo();
                        Globals.PrintInvDate = settings.get_Print_InvDate();
                        Globals.PrintDeviceID = settings.get_Print_DeviceID();

                        if (settings.get_Is_Customer_Display()==null){
                        }else {
                            if (settings.get_Is_Customer_Display().equals("true")) {
                                mDSKernel = DSKernel.newInstance();
                                mDSKernel.checkConnection();
                                mDSKernel.init(getApplicationContext(), mConnCallback);
                                mDSKernel.addReceiveCallback(mReceiveCallback);
                                call_CMD();

                                if (settings.get_CustomerDisplay().equals("2")) {
                                    ArrayList<Customer_Image> arrayList = Customer_Image.getAllCustomer_Image(getApplicationContext(), "", database);
                                    if (arrayList.size() > 0) {

                                        for (int count = 0; count < arrayList.size(); count++) {
                                            ArrayList<String> arrayListImages = new ArrayList<>();
                                            arrayListImages.add(arrayList.get(count).get_image_Path());
                                            Globals.CMD_Images = arrayListImages;
                                        }
                                    }
                                }
                            }
                        }

                    }

                  /*  Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(getApplicationContext(), database, db, "");
                    if (sys_tax_type == null) {

                        sys_tax_type = new Sys_Tax_Type(getApplicationContext(), null, "Other", "0");
                        sys_tax_type.insertSys_Tax_Type(database);

                        sys_tax_type = new Sys_Tax_Type(getApplicationContext(), null, "International", "0");
                        sys_tax_type.insertSys_Tax_Type(database);

                        sys_tax_type = new Sys_Tax_Type(getApplicationContext(), null, "Interstate", "1");
                        sys_tax_type.insertSys_Tax_Type(database);

                        sys_tax_type = new Sys_Tax_Type(getApplicationContext(), null, "Intrastate", "1");
                        sys_tax_type.insertSys_Tax_Type(database);
                    }
*/
                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    lite_pos_device = Lite_POS_Device.getDevice(getApplicationContext(), "", database);
                    if (lite_pos_registration == null && lite_pos_device == null) {
                        new Thread() {
                            @Override
                            public void run() {


                                Intent intent = new Intent(SplashScreen.this, ActivateEmailActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }.start();
                    } else if (lite_pos_registration != null && lite_pos_device == null) {
                        deviceFlag = "1";
                        new Thread() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(SplashScreen.this, ActivateEmailActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }.start();
                    } else if (lite_pos_registration != null && lite_pos_device != null) {


//                        if (lite_pos_device.getDevice_Code().equals(Globals.Device_Code)) {
                        Globals.Company_Id = lite_pos_registration.getcompany_id();
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                progressBar.setVisibility(View.GONE);
//                                Toast.makeText(getApplicationContext(), "Security error", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    sleep(3000);
//                                    System.exit(0);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }.start();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();
    } catch (Exception ex) {
        Toast.makeText(getApplicationContext(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
        System.exit(0);
    }
    }

    private void call_CMD() {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("title", "Phomello-Lite");
            dsPacket = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback);
            mDSKernel.sendData(dsPacket);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastCode(String serverData) {

        if (serverData == null) {
            System.exit(0);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Server Not Found", Toast.LENGTH_SHORT).show();
                }

            });
        }

        try {
            JSONObject collection_jsonObject1 = new JSONObject(serverData);
            String strStatus = collection_jsonObject1.getString("status");
            String massage = collection_jsonObject1.getString("message");

            if (strStatus.equals("true")) {
                database.beginTransaction();
                JSONObject jsonObject = collection_jsonObject1.getJSONObject("result");

                String last_order_code = jsonObject.getString("last_order_code");
                String last_pos_balance_code = jsonObject.getString("last_pos_balance_code");
                String last_z_close_code = jsonObject.getString("last_z_close_code");
                String last_order_return_code=jsonObject.getString("last_order_return_code");
                long l = Last_Code.delete_Last_Code(getApplicationContext(), null, null, database);
                if (l > 0) {
                } else {
                }

                last_code = new Last_Code(getApplicationContext(), null, last_order_code, last_pos_balance_code, last_z_close_code,last_order_return_code);

                long d = last_code.insertLast_Code(database);

                if (d > 0) {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                }
            } else {
            }
        } catch (JSONException e) {

        }
    }

/*    private String getLastCodeFromServer(String liccustomerid) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "last_code");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1",serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
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


    public void postLastCodeFromServer(final String registration_code, final String devicecode, final String syscode1, final String syscode2, final String syscode3, final String syscode4,final String licensecustomerid) {

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
                            getLastCode(response);

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
                        //pDialog.dismiss();
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
    private void check_device() {
        String serverData ="";
                //check_on_server();
        if (serverData == null) {
            System.exit(0);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Server Not Found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        try {
            JSONObject collection_jsonObject1 = new JSONObject(serverData);
            String strStatus = collection_jsonObject1.getString("status");
            String massage = collection_jsonObject1.getString("message");
            if (strStatus.equals("false")) {
                if (massage.equals("Device Not Found")) {
                    if (deviceFlag.equals("1")) {
                        new Thread() {
                            @Override
                            public void run() {
                                device_process();
                            }
                        }.start();
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(SplashScreen.this, RegistrationActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } else {
                    database.beginTransaction();
                    JSONObject jsonObject_result = collection_jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    long l = Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                    lite_pos_device = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"),jsonObject_device.getString("lic_customer_license_id"),jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"),jsonObject_device.getString("license_type"),"IN",jsonObject_device.getString("location_name"));

                    long d = lite_pos_device.insertDevice(database);

                    if (d > 0) {

                        JSONObject jsonObject_company = jsonObject_result.optJSONObject("company");
                        String company_id = jsonObject_company.getString("company_id");
                        Globals.Company_Id = company_id;
                        String registration_code = jsonObject_company.getString("registration_code");
                        String company_name = jsonObject_company.getString("company_name");
                        String license_no = jsonObject_company.getString("license_no");
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
                        Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                        settings.set_Logo(logo);
                        settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);

                        Lite_POS_Registration.delete_Registration(getApplicationContext(), "Lite_POS_Registration", null, null, database);

                        lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name, contact_person,
                                mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                        long r = lite_pos_registration.insertRegistration(database);
                        if (r > 0) {
                            long u = 0;
                            User.delete_User(getApplicationContext(), null, null, database);

                            JSONArray jsonArray = jsonObject_result.getJSONArray("company_user");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject_user = jsonArray.getJSONObject(i);

                                user = new User(getApplicationContext(), null,
                                        jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", "");

                                u = user.insertUser(database);
                            }

                            if (u > 0) {
                                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("user_code", user.get_user_code());
                                editor.putString("pass", user.get_password()); // Storing string
                                editor.apply();
                                database.setTransactionSuccessful();
                                database.endTransaction();
                            } else {
                                database.endTransaction();
                            }

                        } else {
                            database.endTransaction();
                        }


                    } else {
                        database.endTransaction();
                    }

                    Last_Code last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                    if (last_code == null) {
                        new Thread() {
                            @Override
                            public void run() {
                                postLastCodeFromServer(Globals.reg_code,Globals.Device_Code,serial_no,Globals.syscode2,android_id,myKey,Globals.license_id);

                            }
                        }.start();

                    }

//                    pDialog.dismiss();

                    runOnUiThread(new Runnable() {

                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    });
                }
            }
            if (strStatus.equals("true")) {
//                pDialog.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(SplashScreen.this, RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }

                });
            }
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {


        }
    }

  /*  private String check_on_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "device/check_license");
        ArrayList nameValuePairs = new ArrayList(6);
        nameValuePairs.add(new BasicNameValuePair("email", ""));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", "4"));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
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

    private void device_process() {
        String serverData = "";
                //device_on_server();
        try {
            final JSONObject collection_jsonObject1 = new JSONObject(serverData);
            final String strStatus = collection_jsonObject1.getString("status");
            String massage = collection_jsonObject1.getString("message");
            if (strStatus.equals("true")) {
                try {
                    database.beginTransaction();
                    //Lite_POS_Device.delete_Device(getApplicationContext(), null, null);
                    long l = Lite_POS_Device.delete_Device(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                    JSONObject jsonObject_result = collection_jsonObject1.getJSONObject("result");
                    JSONObject jsonObject_device = jsonObject_result.optJSONObject("device");
                    lite_pos_device = new Lite_POS_Device(getApplicationContext(), null, jsonObject_device.getString("device_id"),
                            jsonObject_device.getString("app_type"), jsonObject_device.getString("device_code"),
                            jsonObject_device.getString("device_name"), javaEncryption.encrypt(jsonObject_device.getString("expiry_date"), "12345678"),
                            jsonObject_device.getString("device_symbol"), jsonObject_device.getString("location_id"),
                            jsonObject_device.getString("currency_symbol"), jsonObject_device.getString("decimal_place"),
                            jsonObject_device.getString("currency_place"),jsonObject_device.getString("lic_customer_license_id"),jsonObject_device.getString("lic_code"),
                            jsonObject_device.getString("license_key"),jsonObject_device.getString("license_type"),"IN",jsonObject_device.getString("location_name"));

                    long d = lite_pos_device.insertDevice(database);

                    if (d > 0) {

                        JSONObject jsonObject_company = jsonObject_result.optJSONObject("company");
                        String company_id = jsonObject_company.getString("company_id");
                        Globals.Company_Id = company_id;
                        String registration_code = jsonObject_company.getString("registration_code");
                        String company_name = jsonObject_company.getString("company_name");
                        String license_no = jsonObject_company.getString("license_no");
                        String contact_person = jsonObject_company.getString("contact_person");
                        String email = jsonObject_company.getString("email");
                        String country_id = jsonObject_company.getString("country_id");
                        String zone_id = jsonObject_company.getString("zone_id");
                        String mobile_no = jsonObject_company.getString("phone");
                        String address = jsonObject_company.getString("address");
                        String project_id = jsonObject_company.getString("project_id");
                        String srvc_trf = jsonObject_company.getString("service_code_tariff");
                        String indus_type = jsonObject_company.getString("industry_type");
                        String logo = jsonObject_company.getString("logo");
                        String zonename = jsonObject_company.getString("zone_name");
                        String countryname = jsonObject_company.getString("country_name");
                        Settings settings = Settings.getSettings(getApplicationContext(), database, "");
                        settings.set_Logo(logo);
                        settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);
                        // Lite_POS_Registration.delete_Registration(getApplicationContext(), null, null);
                        long l2 = Lite_POS_Registration.delete_Registration(getApplicationContext(), "Lite_POS_Registration", null, null, database);
                        if (l2 > 0) {
                        } else {
                        }
                        lite_pos_registration = new Lite_POS_Registration(getApplicationContext(), null, company_name, contact_person,
                                mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                        long r = lite_pos_registration.insertRegistration(database);
                        if (r > 0) {
                            long u = 0;
                            User.delete_User(getApplicationContext(), null, null, database);

                            JSONArray jsonArray = jsonObject_result.getJSONArray("company_user");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject_user = jsonArray.getJSONObject(i);

                                user = new User(getApplicationContext(), null,
                                        jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", "");

                                u = user.insertUser(database);
                            }

                            if (u > 0) {
                                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("user_code", user.get_user_code());
                                editor.putString("pass", user.get_password()); // Storing string
                                editor.apply();
                                database.setTransactionSuccessful();
                                database.endTransaction();
                            } else {
                                database.endTransaction();
                            }
                        } else {
                            database.endTransaction();
                        }
                    } else {
                        database.endTransaction();
                    }
                } catch (JSONException jex) {
                    database.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if (massage.equals("Please Register Email")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if (massage.equals("Purchase More Device ")) {
                System.exit(0);
            }
        } catch (JSONException e) {
        }
    }

  /*  private String device_on_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "device/register");
        ArrayList nameValuePairs = new ArrayList(7);
        nameValuePairs.add(new BasicNameValuePair("email", lite_pos_registration.getEmail().toString()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", "4"));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
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

    private void read_defult() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.defaullt)));

            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                try {
                    long l = Address_Category.delete_Address_Category(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_default = new JSONObject(sb.toString());
                ArrayList<Address_Category> address_categoryArrayList = new ArrayList<Address_Category>();
                Address_Category address_category = new Address_Category(getApplicationContext(), "", "", "", "", "","","","");
                JSONArray jsonArray_address_category = jsonObject_default.getJSONArray("address_category");

                for (int i = 0; i < jsonArray_address_category.length(); i++) {

                    JSONObject jsonObject_address_category1 = jsonArray_address_category.getJSONObject(i);

                    address_category = new Address_Category(getApplicationContext(), null,
                            jsonObject_address_category1.getString("device_code"),
                            jsonObject_address_category1.getString("address_category_code"),
                            jsonObject_address_category1.getString("name"),
                            jsonObject_address_category1.getString("is_active"),
                            jsonObject_address_category1.getString("modified_by"),
                            date, "N");

                    address_categoryArrayList.add(address_category);

                  //  address_category.insertAddress_Category(database);
                }

                try{
                    address_category.add_addrresscategory(address_categoryArrayList,database);
                }
                catch(Exception e){

                }

                try {
                    long l = Address_Type.delete_Address_Type(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_address_type = jsonObject_default.getJSONArray("address_type");
                ArrayList<Address_Type> address_typelist = new ArrayList<Address_Type>();
                Address_Type address_type = new Address_Type(getApplicationContext(), "", "");
                for (int i = 0; i < jsonArray_address_type.length(); i++) {

                    JSONObject jsonObject_address_type1 = jsonArray_address_type.getJSONObject(i);

                    address_type = new Address_Type(getApplicationContext(), null,
                            jsonObject_address_type1.getString("name"));


                    address_typelist.add(address_type);
                    //address_type.insertAddress_Type(database);
                }
                try{
                    address_type.add_Addressstype(address_typelist,database);
                }
                catch(Exception e){

                }


                try {
                    long l = Bussiness_Group.delete_Bussiness_Group(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_bussiness_group = jsonObject_default.getJSONArray("business_group");
                ArrayList<Bussiness_Group> bussiness_groupArrayList = new ArrayList<Bussiness_Group>();
                Bussiness_Group bussiness_group = new Bussiness_Group(getApplicationContext(), "", "","","","","","","","");
                for (int i = 0; i < jsonArray_bussiness_group.length(); i++) {

                    JSONObject jsonObject_bussiness_group1 = jsonArray_bussiness_group.getJSONObject(i);

                    bussiness_group = new Bussiness_Group(getApplicationContext(), null
                            , jsonObject_bussiness_group1.getString("device_code"),
                            jsonObject_bussiness_group1.getString("business_group_code"), "1",
                            jsonObject_bussiness_group1.getString("name"),
                            jsonObject_bussiness_group1.getString("is_active"),
                            jsonObject_bussiness_group1.getString("modified_by"), date, "N");


                    bussiness_groupArrayList.add(bussiness_group);
                   // bussiness_group.insertBussiness_Group(database);
                }


                try{
                    bussiness_group.add_businessgrp(bussiness_groupArrayList,database);
                }
                catch(Exception e){}
                try {
                    long l = Order_Type.delete_Order_Type(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_order_type = jsonObject_default.getJSONArray("order_type");
                ArrayList<Order_Type> ordertype_arraylist = new ArrayList<Order_Type>();
                Order_Type ordertype = new Order_Type(getApplicationContext(), "", "","","","","");
                for (int i = 0; i < jsonArray_order_type.length(); i++) {

                    JSONObject jsonObject_order_type1 = jsonArray_order_type.getJSONObject(i);

                    order_type = new Order_Type(getApplicationContext(), jsonObject_order_type1.getString("order_type_id")
                            , jsonObject_order_type1.getString("name"),
                            jsonObject_order_type1.getString("is_active"),
                            jsonObject_order_type1.getString("modified_by"),
                            date, "N");

                    ordertype_arraylist.add(order_type);
                 //   order_type.insertOrder_Type(database);
                }


                try{
                    ordertype.add_ordertype(ordertype_arraylist,database);
                }
                catch (Exception e){}

                try {
                    long l = Payment.delete_Payment(getApplicationContext(), "payments", null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_payments = jsonObject_default.getJSONArray("payments");
                ArrayList<Payment> paymentArrayList = new ArrayList<Payment>();
                Payment paymentobj = new Payment(getApplicationContext(), "", "","","","","","");
                for (int i = 0; i < jsonArray_payments.length(); i++) {

                    JSONObject jsonObject_payments1 = jsonArray_payments.getJSONObject(i);

                    payment = new Payment(getApplicationContext(), jsonObject_payments1.getString("payment_id")
                            , jsonObject_payments1.getString("parent_id"),
                            jsonObject_payments1.getString("payment_name"),
                            jsonObject_payments1.getString("is_active"),
                            jsonObject_payments1.getString("modified_by"),
                            date, "N");


                    paymentArrayList.add(payment);
                   // payment.insertPayment(database);
                }

                try{

                    paymentobj.add_payment(paymentArrayList,database);
                }catch(Exception e){}


            //  String sqlQuery="select * from unit ut left join item i ON i.unit_id = ut.unit_id where ut.unit_id = ''and i.is_active ='1'";
                try {
                    long l = Unit.deleteUnit(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_unit = jsonObject_default.getJSONArray("unit");
                ArrayList<Unit> unitArrayList = new ArrayList<Unit>();
                Unit unitobj = new Unit(getApplicationContext(), "", "","","","","","","");
                for (int i = 0; i < jsonArray_unit.length(); i++) {

                    JSONObject jsonObject_unit1 = jsonArray_unit.getJSONObject(i);
                    unit = new Unit(getApplicationContext(),
                            jsonObject_unit1.getString("unit_id"),
                            jsonObject_unit1.getString("name"),
                            jsonObject_unit1.getString("code"),
                            jsonObject_unit1.getString("description"),
                            jsonObject_unit1.getString("is_active"),
                            jsonObject_unit1.getString("modified_by"),
                            date, "N");

                    unitArrayList.add(unit);
                    //unit.insertUnit(database);
                }

              try{
                  unitobj.add_unit(unitArrayList,database);
              }
              catch(Exception e){

              }

            } catch (JSONException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }

        }
    }

    private void read_zone() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.zone)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                try {
                    long l = Zone.delete_Zone(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_zone = new JSONObject(sb.toString());
                ArrayList<Zone> zonelist = new ArrayList<Zone>();
                Zone zone = new Zone(getApplicationContext(), "", "", "", "", "");
                JSONArray jsonArray_zone = jsonObject_zone.getJSONArray("zone");

                for (int i = 0; i < jsonArray_zone.length(); i++) {

                    JSONObject jsonObject_zone1 = jsonArray_zone.getJSONObject(i);

                    zone = new Zone(getApplicationContext(), jsonObject_zone1.getString("zone_id"),
                            jsonObject_zone1.getString("country_id"), jsonObject_zone1.getString("name"), jsonObject_zone1.getString("code"), jsonObject_zone1.getString("status"));

                    zonelist.add(zone);
                  //  zone.insertZone(database);
                }

                try{
                    zone.add_zone(zonelist,database);
                }
                catch(Exception e){

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void read_country() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(R.raw.country)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                try {
                    long l = Country.delete_country(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_country = new JSONObject(sb.toString());
                ArrayList<Country> clist = new ArrayList<Country>();
                Country country = new Country(getApplicationContext(), "", "", "", "", "", "","","","","");
                JSONArray jsonArray_country = jsonObject_country.getJSONArray("country");

                for (int i = 0; i < jsonArray_country.length(); i++) {

                    JSONObject jsonObject_country1 = jsonArray_country.getJSONObject(i);

                    country = new Country(getApplicationContext(), jsonObject_country1.getString("country_id"),
                            jsonObject_country1.getString("name"), jsonObject_country1.getString("isd_code"), jsonObject_country1.getString("iso_code_2"), jsonObject_country1.getString("iso_code_3"), jsonObject_country1.getString("currency_symbol"), jsonObject_country1.getString("currency_place"), jsonObject_country1.getString("decimal_place"), jsonObject_country1.getString("postcode_required"), jsonObject_country1.getString("status"));


                    clist.add(country);
                  //  country.insertCountry(database);
                }

                try {
                    country.add_country(clist, database);
                } catch (Exception e) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void runInsert(SQLiteDatabase db, String sql) {
        String a[] = sql.split(";");
        for (String X : a) {
            db.execSQL(X);
        }
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status

        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_CONTACTS);

        //If permission is granted returning true

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED;

    }

    //Requesting permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, CAMERA,WRITE_EXTERNAL_STORAGE,READ_PHONE_STATE,READ_CONTACTS,WRITE_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted) {
                        create_database();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel(getString(R.string.acessperm),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA,WRITE_EXTERNAL_STORAGE,READ_PHONE_STATE,READ_CONTACTS,WRITE_CONTACTS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void copyFiletoExternalStorage(int resourceId){
        String pathSDCard = Database.DATABASE_FILE_PATH;
        try{
            InputStream in = getResources().openRawResource(resourceId);
            FileOutputStream out = null;
            out = new FileOutputStream(pathSDCard);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void copyResources(int resId){
        Log.i("Test", "Setup::copyResources");
        InputStream in = getApplicationContext().getResources().openRawResource(resId);
        String filename = getApplicationContext().getResources().getResourceEntryName(resId);

        File f = new File(filename);

        if(!f.exists()){
            try {
                db = new Database(getApplicationContext());
                database = db.getWritableDatabase();
                OutputStream out = new FileOutputStream(new File(db.DATABASE_FILE_PATH, filename+".db"));
                byte[] buffer = new byte[1024];
                int len;
                while((len = in.read(buffer, 0, buffer.length)) != -1){
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                Log.i("Test", "Setup::copyResources - "+e.getMessage());
            } catch (IOException e) {
                Log.i("Test", "Setup::copyResources - "+e.getMessage());
            }
        }
    }

}


