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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.CouponListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.Settings;

import java.util.ArrayList;

public class CouponListActivity extends AppCompatActivity {
    EditText edt_toolbar_coupon_list;
    TextView coupon_title;
    ArrayList<Pro_Loyalty_Setup> arrayList;
    CouponListAdapter couponListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        settings = Settings.getSettings(getApplicationContext(), database, "");

        edt_toolbar_coupon_list = (EditText) findViewById(R.id.edt_toolbar_coupon_list);
        coupon_title = (TextView) findViewById(R.id.coupon_title);

        edt_toolbar_coupon_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_coupon_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_coupon_list.requestFocus();
                    edt_toolbar_coupon_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_coupon_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_coupon_list.selectAll();
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
                pDialog = new ProgressDialog(CouponListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(CouponListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(CouponListActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }
                    }
                };
                timerThread.start();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = "Add";
                Intent intent = new Intent(CouponListActivity.this, CouponSetupActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        getCouponList("");
    }

    private void getCouponList(String strFilter) {
        arrayList = Pro_Loyalty_Setup.getAllPro_Loyalty_Setup(getApplicationContext(), "WHERE loyalty_type = 'PROMOTIONSYSTEM' " + strFilter + " Order By lower(name) asc", database);
        ListView category_list = (ListView) findViewById(R.id.coupon_list);
        if (arrayList.size() > 0) {
            couponListAdapter = new CouponListAdapter(CouponListActivity.this, arrayList);
            coupon_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(couponListAdapter);
            category_list.setTextFilterEnabled(true);
            couponListAdapter.notifyDataSetChanged();
        } else {
            coupon_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_coupon_list.getText().toString().trim();
            strFilter = " and ( name Like '%" + strFilter + "%' )";
            edt_toolbar_coupon_list.selectAll();
            getCouponList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {
//            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                    CouponListActivity.this);
//            alertDialog.setTitle(R.string.Tax);
//            alertDialog
//                    .setMessage(R.string.dilog_msg_cdopr);
//
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//
//            alertDialog.setNegativeButton(R.string.sync,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,
//                                            int which) {
//                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                                    CouponListActivity.this);
//                            alertDialog.setTitle("");
//                            alertDialog
//                                    .setMessage(R.string.sync_data_from_server);
//
//                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.MATCH_PARENT);
//
//
//                            alertDialog.setNegativeButton(R.string.Cancel,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                        }
//                                    });
//
//                            alertDialog.setPositiveButton(R.string.Ok,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//
//                                            if (isNetworkStatusAvialable(getApplicationContext())) {
//                                                pDialog = new ProgressDialog(CouponListActivity.this);
//                                                pDialog.setCancelable(false);
//                                                pDialog.setMessage(getString(R.string.Downloading_tax));
//                                                pDialog.show();
//                                                new Thread() {
//                                                    @Override
//                                                    public void run() {
//                                                        String result = send_online_tax();
//                                                        String suss = getTax();
//                                                        pDialog.dismiss();
//
//                                                        switch (suss) {
//                                                            case "1":
//                                                                runOnUiThread(new Runnable() {
//                                                                    public void run() {
//                                                                        getTaxList("");
//                                                                        Toast.makeText(getApplicationContext(), R.string.Tax_download, Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//                                                                break;
//                                                            case "2":
//                                                                runOnUiThread(new Runnable() {
//                                                                    public void run() {
//
//                                                                        Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//                                                                break;
//                                                            default:
//                                                                runOnUiThread(new Runnable() {
//                                                                    public void run() {
//
//                                                                        Toast.makeText(getApplicationContext(), R.string.Tax_not_found, Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//                                                                break;
//                                                        }
//                                                    }
//                                                }.start();
//                                            } else {
//                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
//
//                                            }
//                                        }
//                                    });
//
//                            AlertDialog alert = alertDialog.create();
//                            alert.show();
//
//                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
//
//                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        }
//                    });
//
//            AlertDialog alert = alertDialog.create();
//
//            alert.setOnShowListener(new DialogInterface.OnShowListener() {
//
//                @Override
//                public void onShow(DialogInterface dialog) {
//                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
//                    String ck_project_type = lite_pos_registration.getproject_id();
//
//                    if (ck_project_type.equals("standalone")) {
//                        ((AlertDialog) dialog).getButton(
//                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//
//                        ((AlertDialog) dialog).getButton(
//                                AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
//                    }
//                }
//            });
//
//            alert.show();
//            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//            nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
//
//            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//            pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        return super.onOptionsItemSelected(item);
    }


//    private String getTax() {
//        String succ_manu = "0";
//        database.beginTransaction();
//        String serverData = get_tax_from_server();
//        try {
//            final JSONObject jsonObject_tax = new JSONObject(serverData);
//            final String strStatus = jsonObject_tax.getString("status");
//
//            if (strStatus.equals("true")) {
//                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
//                for (int i = 0; i < jsonArray_tax.length(); i++) {
//                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
//                    String taxId = jsonObject_tax1.getString("tax_id");
//                    String taxlocation = jsonObject_tax1.getString("location_id");
//                    tax_master = Tax_Master.getTax_Master(getApplicationContext(), "WHERE tax_id ='" + taxId + "'", database, db);
//                    if (tax_master == null) {
//                        tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
//                        long l = tax_master.insertTax_Master(database);
//                        if (l > 0) {
//                            succ_manu = "1";
//                        } else {
//                        }
//
//                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");
//
//                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
//                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
//                            order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
//                            long odrtx = order_type_tax.insertOrder_Type_Tax(database);
//                            if (odrtx > 0) {
//                                succ_manu = "1";
//                            } else {
//                            }
//                        }
//
//                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
//
//                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
//                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
//                            tax_detail = new Tax_Detail(getApplicationContext(), null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
//                            long odrtx1 = tax_detail.insertTax_Detail(database);
//
//                            if (odrtx1 > 0) {
//                                succ_manu = "1";
//                            } else {
//                            }
//                        }
//
//                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");
//
//                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
//                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
//                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), null, jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
//                            long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);
//
//                            if (odrtx1 > 0) {
//                                succ_manu = "1";
//                            } else {
//                            }
//                        }
//                    } else {
//                        tax_master = new Tax_Master(getApplicationContext(), taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
//                        long l = tax_master.updateTax_Master("tax_id=? And location_id=?", new String[]{taxId, taxlocation}, database);
//                        if (l > 0) {
//                            succ_manu = "1";
//
//                        } else {
//                        }
//
//                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");
//
//                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
//                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
//                            Order_Type_Tax order_type_tax5 = Order_Type_Tax.getOrder_Type_Tax(getApplicationContext(),"WHERE tax_id = '"+taxId+"' and order_type_id='"+jsonObject_od_typ_tax.getString("order_type_id")+"'",database);
//                            if (order_type_tax5==null){
//                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
//                                long odrtx = order_type_tax.insertOrder_Type_Tax(database);
//                                if (odrtx > 0) {
//                                    succ_manu = "1";
//                                } else {
//                                }
//                            }else {
//                                order_type_tax = new Order_Type_Tax(getApplicationContext(), jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
//                                long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
//                                if (odrtx > 0) {
//                                    succ_manu = "1";
//                                } else {
//                                }
//                            }
//
//                        }
//
//                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
//                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
//                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
//                            tax_detail = new Tax_Detail(getApplicationContext(), jsonObject_tax_detail.getString("id"), jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
//                            long odrtx1 = tax_detail.updateTax_Detail("tax_id=? And tax_type_id=?", new String[]{taxId, jsonObject_tax_detail.getString("tax_type_id")}, database);
//                            if (odrtx1 > 0) {
//                                succ_manu = "1";
//                            } else {
//                            }
//                        }
//
//                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");
//
//                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
//                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
//                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(getApplicationContext(), jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
//                            long odrtx1 = sys_tax_group.updateSys_Tax_Group("tax_id=? And tax_master_id=?", new String[]{taxId, jsonObject_tax_group.getString("tax_master_id")}, database);
//                            if (odrtx1 > 0) {
//                                succ_manu = "1";
//                            } else {
//                            }
//                        }
//                    }
//                }
//            } else {
//                succ_manu = "0";
//            }
//
//            if (succ_manu.equals("1")) {
//                database.setTransactionSuccessful();
//                database.endTransaction();
//            } else {
//                database.endTransaction();
//            }
//
//        } catch (JSONException e) {
//            succ_manu = "2";
//            database.endTransaction();
//        }
//        return succ_manu;
//    }

    /*private String get_tax_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "tax");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
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

//    private String send_online_tax() {
//        String result = Tax_Master.sendOnServer(getApplicationContext(), database, db, "Select  * From tax  WHERE is_push = 'N'");
//        return result;
//    }

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
        pDialog = new ProgressDialog(CouponListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(CouponListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(CouponListActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }

}
