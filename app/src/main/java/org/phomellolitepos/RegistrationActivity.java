package org.phomellolitepos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phomellolitepos.Adapter.CountryAdapter;
import org.phomellolitepos.Adapter.ZoneAdapter;
import org.phomellolitepos.Util.AESHelper;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.JavaEncryption;
import org.phomellolitepos.database.*;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Zone;

public class RegistrationActivity extends AppCompatActivity {
    TextInputLayout edt_password_layout, edt_password_conf_layout, edt_license_no_layout, edt_email_layout, edt_address_layout, edt_company_name_layout, edt_contact_person_layout, edt_mobile_no_layout, edt_srvc_tarf_layout;
    EditText edt_password, edt_password_conf, edt_license_no, edt_email, edt_company_name, edt_contact_person, edt_mobile_no, edt_address, edt_srvc_tarf;
    Spinner spn_country, spn_zone, spn_project_id, spn_indus_type;
    CheckBox chk_terms;
    TextView txt_terms;
    Button btn_save;
    ProgressDialog progressDialog;
    Lite_POS_Registration lpr;
    Lite_POS_Device lpd;
    ArrayList<Country> arrayCList;
    ArrayList<Zone> arrayZList;
    String strSelectedZoneCode, strSelectedCountryCode;
    String project_type[] = {};
    String industry_type[] = {};
    String indus_type, pr_type;
    User user;
    Database db;
    SQLiteDatabase database;
    Dialog listDialog;
    Country country;
    Zone zone;
    AESHelper aesHelper;
    ArrayList<String> List = new ArrayList<String>();
    String strResult = "";
    JavaEncryption javaEncryption;
    String serial_no, android_id, myKey, device_id,imei_no;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Registration);
        aesHelper = new AESHelper();
        arrayCList = new ArrayList<Country>();
        arrayZList = new ArrayList<Zone>();
        edt_password_layout = (TextInputLayout) findViewById(R.id.edt_password_layout);
        edt_password_conf_layout = (TextInputLayout) findViewById(R.id.edt_password_conf_layout);
        edt_license_no_layout = (TextInputLayout) findViewById(R.id.edt_license_no_layout);
        edt_email_layout = (TextInputLayout) findViewById(R.id.edt_email_layout);
        edt_address_layout = (TextInputLayout) findViewById(R.id.edt_address_layout);
        edt_company_name_layout = (TextInputLayout) findViewById(R.id.edt_company_name_layout);
        edt_contact_person_layout = (TextInputLayout) findViewById(R.id.edt_contact_person_layout);
        edt_mobile_no_layout = (TextInputLayout) findViewById(R.id.edt_mobile_no_layout);
        edt_srvc_tarf_layout = (TextInputLayout) findViewById(R.id.edt_srvc_tarf_layout);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_password_conf = (EditText) findViewById(R.id.edt_password_conf);
        edt_license_no = (EditText) findViewById(R.id.edt_license_no);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_company_name = (EditText) findViewById(R.id.edt_company_name);
        edt_contact_person = (EditText) findViewById(R.id.edt_contact_person);
        edt_mobile_no = (EditText) findViewById(R.id.edt_mobile_no);
        edt_srvc_tarf = (EditText) findViewById(R.id.edt_srvc_tarf);
        spn_country = (Spinner) findViewById(R.id.spn_country);
        spn_zone = (Spinner) findViewById(R.id.spn_zone);
        spn_indus_type = (Spinner) findViewById(R.id.spn_indus_type);
        spn_project_id = (Spinner) findViewById(R.id.spn_projuct_id);
        btn_save = (Button) findViewById(R.id.btn_save);
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
        imei_no=telephonyManager.getImei();
        edt_srvc_tarf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_srvc_tarf.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_srvc_tarf.requestFocus();
                    edt_srvc_tarf.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_srvc_tarf, InputMethodManager.SHOW_IMPLICIT);
                    edt_srvc_tarf.selectAll();
                    return true;
                }
            }
        });

        edt_mobile_no.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_mobile_no.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_mobile_no.requestFocus();
                    edt_mobile_no.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_mobile_no, InputMethodManager.SHOW_IMPLICIT);
                    edt_mobile_no.selectAll();
                    return true;
                }
            }
        });

        edt_contact_person.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_contact_person.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_contact_person.requestFocus();
                    edt_contact_person.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_contact_person, InputMethodManager.SHOW_IMPLICIT);
                    edt_contact_person.selectAll();
                    return true;
                }
            }
        });

        edt_company_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_company_name.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_company_name.requestFocus();
                    edt_company_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_company_name, InputMethodManager.SHOW_IMPLICIT);
                    edt_company_name.selectAll();
                    return true;
                }
            }
        });

        edt_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_address.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_address.requestFocus();
                    edt_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_address, InputMethodManager.SHOW_IMPLICIT);
                    edt_address.selectAll();
                    return true;
                }
            }
        });

        edt_email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_email.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_email.requestFocus();
                    edt_email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_email, InputMethodManager.SHOW_IMPLICIT);
                    edt_email.selectAll();
                    return true;
                }
            }
        });

        edt_license_no.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_license_no.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_license_no.requestFocus();
                    edt_license_no.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_license_no, InputMethodManager.SHOW_IMPLICIT);
                    edt_license_no.selectAll();
                    return true;
                }
            }
        });

        chk_terms = (CheckBox) findViewById(R.id.chk_terms);
        txt_terms = (TextView) findViewById(R.id.txt_terms);
        fill_user_permission();
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        javaEncryption   = new JavaEncryption();
        project_type = getResources().getStringArray(R.array.project_type);
        industry_type = getResources().getStringArray(R.array.Industry_type);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);

        spn_indus_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    String indus_type_chk = String.valueOf(spn_indus_type.getItemAtPosition(i));

                    if (indus_type_chk.equals("Hotels and Restaurants")) {
                        indus_type = "1";
                    } else if (indus_type_chk.equals("Salon and SPA")) {
                        indus_type = "2";
                    } else if (indus_type_chk.equals("Payment Collection")) {
                        indus_type = "3";
                    } else if (indus_type_chk.equals("Parking Solution")) {
                        indus_type = "4";
                    } else if (indus_type_chk.equals("Retail Solution")) {
                        indus_type = "5";
                    } else {
                        indus_type = "6";
                    }

                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spn_project_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    String pr_type_chk = String.valueOf(spn_project_id.getItemAtPosition(i));

                    if (pr_type_chk.equals("Standalone")) {

                        pr_type = "standalone";
                    } else if (pr_type_chk.equals("Cloud")) {
                        pr_type = "cloud";

                    } else {
                        pr_type = "Select";
                    }


                } catch (Exception ecx) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spn_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        get_country();

        txt_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showdialogTerms();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (edt_company_name.equals("") || edt_contact_person.equals("") || edt_email.equals("") || edt_license_no.equals("") || edt_mobile_no.equals("") || edt_password.equals("") || edt_password_conf.equals("")) {
//                    Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_SHORT).show();
//                } else {
//                    progressDialog = new ProgressDialog(RegistrationActivity.this);
//                    progressDialog.setTitle("Registering");
//                    progressDialog.setMessage(getString(R.string.Wait_msg));
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            registration_process();
//                        }
//                    }.start();
//                }

                String contact_code = "", contact_name = "", dob = "", company_name = "", contact_1 = "", contact_2 = "", description = "", email_1 = "", email_2 = "", addresss = "";
                if (edt_company_name.getText().toString().trim().equals("")) {
                    edt_company_name.setError(getString(R.string.Company_name_is_required));
                    edt_company_name.requestFocus();
                    return;
                }

                if (edt_contact_person.getText().toString().trim().equals("")) {
                    edt_contact_person.setError(getString(R.string.Contact_person_required));
                    edt_contact_person.requestFocus();
                    return;
                }

                if (edt_email.getText().toString().trim().equals("")) {
                    edt_email.setError(getString(R.string.Enter_Email_Address));
                    edt_email.requestFocus();
                    return;
                } else {
                    final String email1 = edt_email.getText().toString();
                    if (!isValidEmail(email1)) {
                        edt_email.setError(getString(R.string.Invalid_Email));
                        edt_email.requestFocus();
                        return;
                    }

                }


                if (edt_license_no.getText().toString().trim().equals("")) {
                    edt_license_no.setError(getString(R.string.License_is_required));
                    edt_license_no.requestFocus();
                    return;
                }

//                if (edt_srvc_tarf.getText().toString().trim().equals("")) {
//                    edt_srvc_tarf.setError(getString(R.string.License_is_required));
//                    edt_srvc_tarf.requestFocus();
//                    return;
//                }

                if (edt_mobile_no.getText().toString().trim().equals("")) {
                    edt_mobile_no.setError(getString(R.string.Mobile_is_required));
                    edt_mobile_no.requestFocus();
                    return;
                }

                if (edt_password.getText().toString().trim().equals("")) {
                    edt_password.setError(getString(R.string.Password_is_required));
                    edt_password.requestFocus();
                    return;
                }


                if (edt_password_conf.getText().toString().trim().equals("")) {
                    edt_password_conf.setError(getString(R.string.Cnfrm_paswrd_is_required));
                    edt_password_conf.requestFocus();
                    return;

                }

                String passCheck = edt_password.getText().toString().trim();
                String con_passCheck = edt_password_conf.getText().toString().trim();

                if (!passCheck.equals(con_passCheck)) {

                    edt_password_conf.setError(getString(R.string.Cnfrm_paswrd_not_matched));
                    edt_password_conf.requestFocus();
                    return;
                }


                if (edt_address.getText().toString().trim().equals("")) {
                    edt_address.setError(getString(R.string.Address_is_required));
                    edt_address.requestFocus();
                    return;
                }

                if (isNetworkStatusAvialable(getApplicationContext())) {
                    if (pr_type.equals("Select")) {

                        Toast.makeText(getApplicationContext(), R.string.Select_project_type, Toast.LENGTH_SHORT).show();

                    } else {
                        if (chk_terms.isChecked()) {
                            progressDialog = new ProgressDialog(RegistrationActivity.this);
                            progressDialog.setTitle(getString(R.string.Registering));
                            progressDialog.setMessage(getString(R.string.Wait_msg));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            new Thread() {
                                @Override
                                public void run() {
                                    registration_process();
                                }
                            }.start();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.Select_terms_cndtn, Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, project_type);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spn_project_id.setAdapter(dataAdapter);


        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, industry_type);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spn_indus_type.setAdapter(dataAdapter1);

    }

    private void showdialogTerms() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.Terms_Condition);

        WebView wv = new WebView(this);
        wv.loadData("TriggerPOS is the perfect solution for faster billing and customer service.\n" +
                "A Simple POS system for ALL your needs…\n" +
                "TriggerPOS is indispensable in todays retail and hospitality sectors. It is efficient, easy to use and versatile. It is sure to make operating your business easier. Developed by Pegasus, a pioneer company producing POS systems for a wide variety of sectors and applications since past decade. From the small local restaurant to the large hotel, from the local taxi service to an international travel agency , no matter what your business is, with our TriggerPOS software you can simplify your day to day billing activities. Instant tokens, fast billing, staff management and discount creation, all this can be easily accomplished with one system.TriggerPOS is the perfect solution for faster billing and customer service.\n" +
                "\" +\n" +
                "\"A Simple POS system for ALL your needs…\\n\" +\n" +
                "\"TriggerPOS is indispensable in todays retail and hospitality sectors. It is efficient, easy to use and versatile. It is sure to make operating your business easier. Developed by Pegasus, a pioneer company producing POS systems for a wide variety of sectors and applications since past decade. From the small local restaurant to the large hotel, from the local taxi service to an international travel agency , no matter what your business is, with our TriggerPOS software you can simplify your day to day billing activities. Instant tokens, fast billing, staff management and discount creation, all this can be easily accomplished with one system", "text/html; charset=utf-8", "UTF-8");


        alert.setView(wv);
        alert.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

  /*  private String check_on_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "device/check_license");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("email", edt_email.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
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
    }
*/
    private void registration_process() {
        String serverData = "";



                //register_on_server();
        try {
            if (serverData==null){
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");

                if (strStatus.equals("true")) {
                    JSONObject jsonObject_result = collection_jsonObject1.getJSONObject("result");
                    String company_id = jsonObject_result.getString("company_id");
                    Globals.Company_Id = company_id;
                    String registration_code = jsonObject_result.getString("registration_code");
                    String company_name = jsonObject_result.getString("company_name");
                    String license_no = jsonObject_result.getString("license_no");
                    String contact_person = jsonObject_result.getString("contact_person");
                    String email = jsonObject_result.getString("email");
                    String country_id = jsonObject_result.getString("country_id");
                    String zone_id = jsonObject_result.getString("zone_id");
                    String mobile_no = jsonObject_result.getString("mobile_no");
                    String address = jsonObject_result.getString("address");
                    String project_id = jsonObject_result.getString("project_id");
                    String indus_type = jsonObject_result.getString("industry_type");
                    String srvc_trf = jsonObject_result.getString("service_code_tariff");
                    String zonename = jsonObject_result.getString("zone_name");
                    String countryname = jsonObject_result.getString("country_name");
                    Globals.Industry_Type = indus_type;
                    lpr = new Lite_POS_Registration(getApplicationContext(), null, company_name, contact_person,
                            mobile_no, country_id, zone_id, registration_code, license_no, email, address, company_id, project_id, registration_code, srvc_trf, indus_type,"",countryname,zonename);
                    long l = lpr.insertRegistration(database);
                    if (l > 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                String rslt = device_process();
                                progressDialog.dismiss();
                                if (rslt.equals("1")) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.Registration_Successfully, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.Not_Registered, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }.start();
                    }
                } else {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.Not_Registered, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        } catch (JSONException e) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

   /* private String register_on_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "register");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_name", edt_company_name.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("contact_person", edt_contact_person.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("mobile_no", edt_mobile_no.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("country_id", strSelectedCountryCode));
        nameValuePairs.add(new BasicNameValuePair("zone_id", strSelectedZoneCode));
        nameValuePairs.add(new BasicNameValuePair("password", edt_password.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("password_conf", edt_password.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("license_no", edt_license_no.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("email", edt_email.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("address", edt_address.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("project_id", pr_type));
        nameValuePairs.add(new BasicNameValuePair("industry_type", indus_type));
        nameValuePairs.add(new BasicNameValuePair("service_code_tariff", edt_srvc_tarf.getText().toString().trim()));
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
//            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private String device_process() {
        String succ = "0";
        String serverData = "";
                //device_on_server();
        try {
            final JSONObject collection_jsonObject1 = new JSONObject(serverData);
            final String strStatus = collection_jsonObject1.getString("status");
            if (strStatus.equals("true")) {
                try {
                    JSONObject myResult = collection_jsonObject1.getJSONObject("result");

                    JSONObject json_device = myResult.getJSONObject("device");

                    lpd = new Lite_POS_Device(getApplicationContext(), null, json_device.getString("device_id"), json_device.getString("app_type"), json_device.getString("device_code"), json_device.getString("device_name"),javaEncryption.encrypt(json_device.getString("expiry_date"),"1235678"), json_device.getString("device_symbol"), json_device.getString("location_id"), json_device.getString("currency_symbol"), json_device.getString("decimal_place"), json_device.getString("currency_place"),json_device.getString("lic_customer_license_id"),json_device.getString("lic_code"),
                            json_device.getString("license_key"),json_device.getString("license_type"),"IN",json_device.getString("location_name"));
                    lpd.insertDevice(database);

                    JSONArray jsonArray = myResult.getJSONArray("company_user");

                    for (int i = 0; i < List.size(); i++) {

                        String name = List.get(i);
                        if (strResult.equals("")) {
                            strResult = name;
                        }
                        strResult = strResult + "," + name;

                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject_user = jsonArray.getJSONObject(i);

                        user = new User(getApplicationContext(), null,
                                jsonObject_user.getString("user_group_id"), jsonObject_user.getString("user_code"), jsonObject_user.getString("name"), jsonObject_user.getString("email"), jsonObject_user.getString("password"), jsonObject_user.getString("max_discount"), jsonObject_user.getString("image"), jsonObject_user.getString("is_active"), "0", "0", "N", strResult);

                        user.insertUser(database);

                    }
                    succ = "1";
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("pass", user.get_password());
                    editor.putString("user_code", user.get_user_code());// Storing string
                    editor.apply();
                } catch (JSONException jex) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                progressDialog.dismiss();
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "connection error", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        return succ;
    }

   /* private String device_on_server() {
        String serverData = null;//

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "device/register");
        ArrayList nameValuePairs = new ArrayList(6);
        nameValuePairs.add(new BasicNameValuePair("email", edt_email.getText().toString()));
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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private void get_country() {
        arrayCList = country.getAllCountry(getApplicationContext(), "");
        CountryAdapter countryAdapter = new CountryAdapter(getApplicationContext(), arrayCList);
        spn_country.setAdapter(countryAdapter);

    }

    private void get_zone(String strCountryCode) {

        arrayZList = zone.getAllZone(getApplicationContext(), "WHERE country_id = '" + strCountryCode + "'");

        ZoneAdapter countryAdapter = new ZoneAdapter(getApplicationContext(), arrayZList);
        spn_zone.setAdapter(countryAdapter);

    }

   /* private String get_zone_from_server(String strCountryCode) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "zone");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("country_id", strCountryCode));
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
    }
*/
    /*private String get_country_from_server() {
        String serverData = null;// String object to store fetched data from
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "country");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_skip) {
            Intent intent = new Intent(RegistrationActivity.this, ActivateEmailActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

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

    private void fill_user_permission() {
        List.clear();
        List.add("Item Category");
        List.add("Item");
        List.add("Contact");
        List.add("Order");
        List.add("Manager");
        List.add("Reservation");
        List.add("Settings");
        List.add("Report");
        List.add("Tax");
        List.add("Unit");
        List.add("Database");
        List.add("Update License");
        List.add("Profile");
        List.add("Returns");
        List.add("Accounts");
        List.add("User");
    }


    @Override
    public void onBackPressed() {

    }
}