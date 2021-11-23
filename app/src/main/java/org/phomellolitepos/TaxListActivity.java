package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.phomellolitepos.Adapter.TaxListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;

public class TaxListActivity extends AppCompatActivity {
    EditText edt_toolbar_tax_list;
    TextView tax_title;
    Tax_Master tax_master;
    Item_Group_Tax item_group_tax;
    Order_Type_Tax order_type_tax;
    Tax_Detail tax_detail;
    Sys_Tax_Group systax_group;
    ArrayList<Tax_Master> arrayList;
    TaxListAdapter taxListAdapter;
   // Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        try {
            db = new Database(getApplicationContext());
            database = db.getWritableDatabase();
        }
        catch(Exception e){}
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       // settings = Settings.getSettings(getApplicationContext(), database, "");
        tax_title = (TextView) findViewById(R.id.tax_title);
        edt_toolbar_tax_list = (EditText) findViewById(R.id.edt_toolbar_tax_list);
        edt_toolbar_tax_list.setMaxLines(1);
        edt_toolbar_tax_list.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_toolbar_tax_list.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edt_toolbar_tax_list.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String strFilter = edt_toolbar_tax_list.getText().toString().trim();
                    strFilter = " and ( tax_name Like '%" + strFilter + "%' )";
                    edt_toolbar_tax_list.selectAll();
                    getTaxList(strFilter);
                    return true;
                }
                return false;
            }
        });

        edt_toolbar_tax_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_tax_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_tax_list.requestFocus();
                    edt_toolbar_tax_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_tax_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_tax_list.selectAll();
                    return true;
                }
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* pDialog = new ProgressDialog(TaxListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/

                     if(Globals.objLPR.getIndustry_Type().equals("4")){
                         Intent intent = new Intent(TaxListActivity.this, ParkingIndustryActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(intent);
                         //pDialog.dismiss();
                         finish();
                     }
                  else if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(TaxListActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //pDialog.dismiss();
                    finish();
                }
                     else {
                         if (Globals.objsettings.get_Home_Layout().equals("0")) {
                             try {
                                 Intent intent = new Intent(TaxListActivity.this, MainActivity.class);
                                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 startActivity(intent);
                                 //pDialog.dismiss();
                                 finish();
                             } finally {
                             }
                         } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                             try {
                                 Intent intent = new Intent(TaxListActivity.this, RetailActivity.class);
                                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 startActivity(intent);
                                 //pDialog.dismiss();
                                 finish();
                             } finally {
                             }
                         } else {
                             try {
                                 Intent intent = new Intent(TaxListActivity.this, Main2Activity.class);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = "Add";
                Intent intent = new Intent(TaxListActivity.this, TaxActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });

        getTaxList("");
    }


    private void getTaxList(String strFilter) {
        arrayList = Tax_Master.getAllTax_Master(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(tax_name) asc limit "+Globals.ListLimit+"", database);
        ListView category_list = (ListView) findViewById(R.id.tax_list);
        if (arrayList.size() > 0) {
            taxListAdapter = new TaxListAdapter(TaxListActivity.this, arrayList);
            tax_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(taxListAdapter);
            category_list.setTextFilterEnabled(true);
            taxListAdapter.notifyDataSetChanged();
        } else {
            tax_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        if(Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_tax_list.getText().toString().trim();
            strFilter = " and ( tax_name Like '%" + strFilter + "%' )";
            edt_toolbar_tax_list.selectAll();
            getTaxList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    TaxListActivity.this);
            alertDialog.setTitle(R.string.Tax);
            alertDialog
                    .setMessage(R.string.dilog_msg_cdopr);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            alertDialog.setNegativeButton(R.string.sync,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    TaxListActivity.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.sync_data_from_server);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);


                            alertDialog.setNegativeButton(R.string.Cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    });

                            alertDialog.setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                                pDialog = new ProgressDialog(TaxListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.Downloading_tax));
                                                pDialog.show();

                                                Thread t1=new Thread() {
                                                    @Override
                                                    public void run() {

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    String result="";
                                                                    String suss="";
                                                                    // Getting items form server
                                                                    try {
                                                                        result = send_online_tax();
                                                                    }
                                                                    catch(Exception e){

                                                                    }
                                                                    if(result.equals("0")){
                                                                        pDialog.dismiss();
                                                                    }
                                                                }
                                                                catch(Exception e){

                                                                }
                                                            }
                                                        });



                                                        // send items on server


                                                    }
                                                };t1.start();

                                                try {
                                                    t1.join();
                                                    // pDialog.dismiss();
                                                } catch (InterruptedException ie) {
                                                    ie.printStackTrace();
                                                }
                                                Thread t2=  new Thread() {
                                                    @Override
                                                    public void run() {
                                                        String result="";
                                                        String suss="";
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    //Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='item'");
                                                                    try {
                                                                        get_tax_from_server();
                                                                    }
                                                                    catch(Exception e){

                                                                    }




                                                                    //suss = getitem();
                                                                }
                                                                catch(Exception e){

                                                                }

                                                            }
                                                        });



                                                        // send items on server


                                                    }
                                                };t2.start();
                                                try {
                                                    t2.join();
                                                } catch (InterruptedException ie) {
                                                    ie.printStackTrace();
                                                }



                                            } else {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                            AlertDialog alert = alertDialog.create();
                            alert.show();

                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });

            AlertDialog alert = alertDialog.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                   // lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = Globals.objLPR.getproject_id();

                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                        ((AlertDialog) dialog).getButton(
                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    }
                }
            });

            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));

            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        return super.onOptionsItemSelected(item);
    }


    private String getTax(String serverData) {
        String succ_manu = "0";
        database.beginTransaction();
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(getApplicationContext(), database, db, "WHERE table_name='tax'");

        try {
            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");

            if (strStatus.equals("true")) {
                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
                if (sys_sycntime != null) {
                    sys_sycntime.set_datetime(jsonArray_tax.getJSONObject(0).getString("modified_date"));
                    long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"tax"}, database);
                }
                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id ='" + taxId + "'", database, db);

                    if (tax_master == null) {
                        tax_master = new Tax_Master(getApplicationContext(), jsonObject_tax1.getString("tax_id"), jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.insertTax_Master(database);
                        if (l > 0) {
                            succ_manu = "1";
                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                            long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                            if (odrtx > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.insertTax_Detail(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                          systax_group = new Sys_Tax_Group(getApplicationContext(), jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_master_id"), jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = systax_group.insertSys_Tax_Group(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    } else {
                        //systax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + taxId + "'");

                        tax_master = new Tax_Master(getApplicationContext(), taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");

                        long l = tax_master.updateTax_Master("tax_id=? And location_id=?", new String[]{taxId, taxlocation}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            Order_Type_Tax order_type_tax5 = Order_Type_Tax.getOrder_Type_Tax(getApplicationContext(),"WHERE tax_id = '"+taxId+"' and order_type_id='"+jsonObject_od_typ_tax.getString("order_type_id")+"'",database);
                            if (order_type_tax5==null){
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }else {
                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }

                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(getApplicationContext(), jsonObject_tax_detail.getString("id"), jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.updateTax_Detail("tax_id=? And tax_type_id=?", new String[]{taxId, jsonObject_tax_detail.getString("tax_type_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                          JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);

                          /*  systax_group = Sys_Tax_Group.getSys_Tax_Group(getApplicationContext(), "WHERE tax_id = '" + taxId + "'");

                          systax_group.set_tax_id(jsonObject_tax_group.getString("tax_id"));
                            systax_group.set_tax_master_id(jsonObject_tax_group.getString("tax_master_id"));*/
                             systax_group = new Sys_Tax_Group(getApplicationContext(), jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_master_id"), jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = systax_group.updateSys_Tax_Group("tax_id=? And tax_master_id=?", new String[]{taxId}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                                succ_manu = "0";
                            }
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

/*    private String get_tax_from_server() {
        String serverData = null;//
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(
                     Globals.App_IP_URL + "tax");
            ArrayList nameValuePairs = new ArrayList(5);
            nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
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
        }
        catch(Exception e){}
        return serverData;
    }*/

    public void get_tax_from_server() {

        pDialog = new ProgressDialog(TaxListActivity.this);
        pDialog.setMessage(getString(R.string.Syncingh));
        pDialog.show();
        String server_url = Globals.App_IP_URL + "tax";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getTax(response);
                            switch (result) {
                                case "1":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            getTaxList("");
                                            Toast.makeText(getApplicationContext(), R.string.Tax_download, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case "2":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                default:
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), R.string.Tax_not_found, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;

                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                     pDialog.dismiss();

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
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String send_online_tax() {
        String result = Tax_Master.sendOnServer(getApplicationContext(), database, db, "Select  * From tax  WHERE is_push = 'N'");
        return result;
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


    @Override
    public void onBackPressed() {
      /*  pDialog = new ProgressDialog(TaxListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/

        if(Globals.objLPR.getIndustry_Type().equals("4")){
            Intent intent = new Intent(TaxListActivity.this, ParkingIndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //pDialog.dismiss();
            finish();
        }
        else if(Globals.objLPR.getIndustry_Type().equals("2")){
            Intent intent = new Intent(TaxListActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //pDialog.dismiss();
            finish();
        }
        else {
            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                try {
                    Intent intent = new Intent(TaxListActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                try {
                    Intent intent = new Intent(TaxListActivity.this, RetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // pDialog.dismiss();
                    finish();
                } finally {
                }
            } else {
                try {
                    Intent intent = new Intent(TaxListActivity.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        }
           /* }
        };
        timerThread.start();*/
    }
}
