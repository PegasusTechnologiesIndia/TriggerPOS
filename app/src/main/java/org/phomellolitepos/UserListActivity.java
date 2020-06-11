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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.phomellolitepos.Adapter.UserListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;

public class UserListActivity extends AppCompatActivity {
    EditText edt_toolbar_bussiness_gp_list;
    TextView bussiness_group_title;
    User user;
    ArrayList<User> arrayList;
    UserListAdapter userListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_llist);
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
                pDialog = new ProgressDialog(UserListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(UserListActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(UserListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(UserListActivity.this, UserActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        //Get bussiness group list here
        getUserList("");
    }

    private void getUserList(String strFilter) {
        arrayList = User.getAllUser(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"", database, db);
        ListView category_list = (ListView) findViewById(R.id.bussiness_group_list);
        if (arrayList.size() > 0) {
            userListAdapter = new UserListAdapter(UserListActivity.this, arrayList);
            bussiness_group_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(userListAdapter);
            category_list.setTextFilterEnabled(true);
            userListAdapter.notifyDataSetChanged();
        } else {
            bussiness_group_title.setVisibility(View.VISIBLE);
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
            String strFilter = edt_toolbar_bussiness_gp_list.getText().toString().trim();
            strFilter = " and ( user_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_bussiness_gp_list.selectAll();
            getUserList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {
            // here dialog will open for cloud operations
            if (isNetworkStatusAvialable(getApplicationContext())) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        UserListActivity.this);
                alertDialog.setTitle(R.string.User);
                alertDialog
                        .setMessage(R.string.dilog_msg_cdopr);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                alertDialog.setNegativeButton(R.string.sync,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                pDialog = new ProgressDialog(UserListActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage("Downloading User....");
                                pDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        send_online_user();
                                        String suss = getUser();
                                        pDialog.dismiss();
                                        switch (suss) {
                                            case "1":
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        getUserList("");
                                                        Toast.makeText(getApplicationContext(), "User download!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                break;
                                            case "2":

                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        getUserList("");
                                                        Toast.makeText(getApplicationContext(), "User Updated!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                break;

                                            case "3":

                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        getUserList("");
                                                        Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                break;
                                            default:
                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(getApplicationContext(), "User data not found!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                break;
                                        }

                                    }
                                }.start();
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

            } else {
                Toast.makeText(getApplicationContext(), "internet Not avialable", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private String getUser() {
        String succ_manu = "0";
        // Call get bussiness group api here
        String serverData = get_user_from_server();
        database.beginTransaction();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("user_code");
                    String bg_id = jsonObject_bg1.getString("user_id");
                    user = User.getUser(getApplicationContext(), "WHERE  is_active = '1' and user_code ='" + bg_code + "'", database);

                    if (user == null) {
                        user = new User(getApplicationContext(), null, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.insertUser(database);

                        if (l > 0) {
                            succ_manu = "1";


                        } else {

                        }

                    } else {

                        user = new User(getApplicationContext(), bg_id, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.updateUser("user_code=? And user_id=?", database, new String[]{bg_code, bg_id});
                        if (l > 0) {
                            succ_manu = "2";


                        } else {

                        }
                    }
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

    private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/user");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
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

    private void send_online_user() {
        User.sendOnServer(getApplicationContext(), database, db, "Select  * From  user  WHERE is_push = 'N'");
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
        pDialog = new ProgressDialog(UserListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(UserListActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(UserListActivity.this, Main2Activity.class);
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
