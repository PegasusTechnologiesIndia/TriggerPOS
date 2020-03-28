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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.BussinessGroupListAdapter;
import org.phomellolitepos.Adapter.PayCollectionListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Pay_Collection;
import org.phomellolitepos.database.Pay_Collection_Setup;
import org.phomellolitepos.database.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PayCollectionListActivity extends AppCompatActivity {
    EditText edt_toolbar_bussiness_gp_list;
    TextView bussiness_group_title;
    Bussiness_Group bussiness_group;
    ArrayList<Pay_Collection> arrayList;
    PayCollectionListAdapter payCollectionListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_collection_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_bussiness_gp_list = (EditText) findViewById(R.id.edt_toolbar_bussiness_gp_list);
        bussiness_group_title = (TextView) findViewById(R.id.bussiness_group_title);

        edt_toolbar_bussiness_gp_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_bussiness_gp_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_bussiness_gp_list.requestFocus();
                    edt_toolbar_bussiness_gp_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_bussiness_gp_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_bussiness_gp_list.selectAll();
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

                pDialog = new ProgressDialog(PayCollectionListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(PayCollectionListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(PayCollectionListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(PayCollectionListActivity.this, PaymentCollectionActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        //Get bussiness group list here
        getPayCollectonList("");
    }

    private void getPayCollectonList(String strFilter) {
        arrayList = Pay_Collection.getAllPay_Collection(getApplicationContext(), " WHERE is_active = '1' limit "+Globals.ListLimit+"" + strFilter);
        ListView category_list = (ListView) findViewById(R.id.bussiness_group_list);
        if (arrayList.size() > 0) {
            payCollectionListAdapter = new PayCollectionListAdapter(PayCollectionListActivity.this, arrayList);
            bussiness_group_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(payCollectionListAdapter);
            category_list.setTextFilterEnabled(true);
            payCollectionListAdapter.notifyDataSetChanged();
        } else {
            bussiness_group_title.setVisibility(View.VISIBLE);
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
            String strFilter = edt_toolbar_bussiness_gp_list.getText().toString().trim();
            strFilter = " and ( collection_code Like '%" + strFilter + "%'  Or contact_code Like '%" + strFilter + "%' )";
            edt_toolbar_bussiness_gp_list.selectAll();
            getPayCollectonList(strFilter);
            return true;
        }

        if (id == R.id.action_send) {

            //dialog for download

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    PayCollectionListActivity.this);
            alertDialog.setTitle(R.string.payment_collection);
            alertDialog
                    .setMessage(R.string.dilog_msg_cdopr);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            alertDialog.setNegativeButton(R.string.Get,

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    PayCollectionListActivity.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.dilog_msg_srvropr);

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
                                                pDialog = new ProgressDialog(PayCollectionListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.downloading_paycollection));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {

                                                        String suss = getPaycollection();
                                                        pDialog.dismiss();

                                                        switch (suss) {
                                                            case "1":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getPayCollectonList("");
                                                                        Toast.makeText(getApplicationContext(),"Data downlosd successful", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                                break;

                                                            case "2":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getPayCollectonList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;

                                                            default:
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getPayCollectonList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Data_not_found, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
                                                        }

                                                    }
                                                }.start();
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

            alertDialog.setPositiveButton(R.string.Post,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    PayCollectionListActivity.this);
                            alertDialog.setTitle("");
                            alertDialog
                                    .setMessage(R.string.dilog_pst_data_on_srvr);

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
                                                pDialog = new ProgressDialog(PayCollectionListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.snding_data_on_srvr));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        //downloading thread
                                                        String result = send_online_manufacture();
                                                        pDialog.dismiss();
                                                        if (result.equals("1")) {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), R.string.Data_pst_succ, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        } else {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), R.string.No_data_fnd, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                    }
                                                }.start();
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

            //here checking if mode is online then enable option menu button
            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = lite_pos_registration.getproject_id();

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

    private String send_online_manufacture() {

        String mStr = Pay_Collection.sendOnServer(getApplicationContext(), database, db, "Select * From Pay_Collection  WHERE is_push = 'N'");
        return mStr;
    }

    private String getPaycollection() {
        String succ_manu = "0";
        database.beginTransaction();
        String serverData = get_Paycollection_from_server();
        try {

            final JSONObject jsonObject_manufacture = new JSONObject(serverData);
            final String strStatus = jsonObject_manufacture.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray = jsonObject_manufacture.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String inv_no = jsonObject.getString("inv_no");
                    String contact_code = jsonObject.getString("contact_code");
                    String inv_date = jsonObject.getString("inv_date");
                    String is_active = jsonObject.getString("is_active");
                    String inv_amount = jsonObject.getString("inv_amount");
//                    String inv_no = jsonObject.getString("inv_no");
                    Pay_Collection_Setup pay_collection_setup = Pay_Collection_Setup.getPay_Collection_Setup(getApplicationContext(), " WHERE invoice_no = '" + inv_no + "'", database);
                    if (pay_collection_setup == null) {
                        pay_collection_setup = new Pay_Collection_Setup(getApplicationContext(), null, contact_code, inv_no, inv_date, inv_amount);
                        long l = pay_collection_setup.insertPay_Collection_Setup(database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    } else {
                        pay_collection_setup = new Pay_Collection_Setup(getApplicationContext(), pay_collection_setup.get_id(), contact_code, inv_no, inv_date, inv_amount);
                        long a = pay_collection_setup.updatePay_Collection_Setup("invoice_no=?", new String[]{pay_collection_setup.get_invoice_no()}, database);
                        if (a > 0) {
                            succ_manu = "1";
                        }
                    }
                }
            } else {
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

    private String get_Paycollection_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/payment_setup");
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

    }


    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(PayCollectionListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(PayCollectionListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(PayCollectionListActivity.this, Main2Activity.class);
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
