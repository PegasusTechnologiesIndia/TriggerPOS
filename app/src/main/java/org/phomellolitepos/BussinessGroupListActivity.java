package org.phomellolitepos;

import android.annotation.SuppressLint;
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
import android.view.WindowManager;
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

import java.util.ArrayList;

import org.phomellolitepos.Adapter.BussinessGroupListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class BussinessGroupListActivity extends AppCompatActivity {
    EditText edt_toolbar_bussiness_gp_list;
    TextView bussiness_group_title;
    Bussiness_Group bussiness_group;
    ArrayList<Bussiness_Group> arrayList;
    BussinessGroupListAdapter bussinessGroupListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_group_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_bussiness_gp_list = (EditText) findViewById(R.id.edt_toolbar_bussiness_gp_list);
        bussiness_group_title = (TextView) findViewById(R.id.bussiness_group_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                pDialog = new ProgressDialog(BussinessGroupListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(BussinessGroupListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(BussinessGroupListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(BussinessGroupListActivity.this, BussinessGroupActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        getBussinessGroupList("");
    }

    private void getBussinessGroupList(String strFilter) {
        arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active = '1' " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"");
        ListView category_list = (ListView) findViewById(R.id.bussiness_group_list);
        if (arrayList.size() > 0) {
            bussinessGroupListAdapter = new BussinessGroupListAdapter(BussinessGroupListActivity.this, arrayList);
            bussiness_group_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(bussinessGroupListAdapter);
            category_list.setTextFilterEnabled(true);
            bussinessGroupListAdapter.notifyDataSetChanged();
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
            strFilter = " and ( business_group_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_bussiness_gp_list.selectAll();
            getBussinessGroupList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {
            // here dialog will open for cloud operations
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    BussinessGroupListActivity.this);
            alertDialog.setTitle(R.string.Bussiness_Group);
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
                                    BussinessGroupListActivity.this);
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
                                                pDialog = new ProgressDialog(BussinessGroupListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.Downloading_Bussiness_Group));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        String result = send_online_bussiness();
                                                        String suss = getBussinessGroup();
                                                        pDialog.dismiss();
                                                        switch (suss) {
                                                            case "1":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getBussinessGroupList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Bussiness_Group_download, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
                                                            case "2":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getBussinessGroupList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Bussiness_Group_Updated, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
                                                            case "3":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getBussinessGroupList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;
                                                            default:
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), R.string.Bussiness_Group_not_fnd, Toast.LENGTH_SHORT).show();
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


            AlertDialog alert = alertDialog.create();
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


    private String getBussinessGroup() {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();
        String serverData ="";
                //get_bussiness_gp_from_server();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("business_group_code");
                    String bg_id = jsonObject_bg1.getString("business_group_id");
                    bussiness_group = Bussiness_Group.getBussiness_Group(getApplicationContext(), database, db, "WHERE  business_group_code ='" + bg_code + "'");
                    if (bussiness_group == null) {
                        bussiness_group = new Bussiness_Group(getApplicationContext(), null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.insertBussiness_Group(database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    } else {
                        bussiness_group = new Bussiness_Group(getApplicationContext(), bg_id, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.updateBussiness_Group("business_group_code=? And business_group_id=? ", new String[]{bg_code, bg_id}, database);
                        if (l > 0) {
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
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }
   /* private String get_bussiness_gp_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "business_group");
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

    private String send_online_bussiness() {
        String l = Bussiness_Group.sendOnServer(getApplicationContext(), database, db, "Select  * From business_group  WHERE is_push = 'N'");
        return l;
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
        pDialog = new ProgressDialog(BussinessGroupListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(BussinessGroupListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(BussinessGroupListActivity.this, Main2Activity.class);
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
