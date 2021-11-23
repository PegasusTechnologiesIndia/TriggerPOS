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

import org.phomellolitepos.Adapter.UserListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;

public class UserListActivity extends AppCompatActivity {
    EditText edt_toolbar_bussiness_gp_list;
    TextView bussiness_group_title;
    User user;
    ArrayList<User> arrayList;
    UserListAdapter userListAdapter;
   // Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @SuppressLint("RestrictedApi")
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
        edt_toolbar_bussiness_gp_list.setMaxLines(1);
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
          /*      pDialog = new ProgressDialog(UserListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/
                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                            if (settings.get_Home_Layout().equals("0")) {
                                try {
                                    Globals.cart.clear();
                                    Globals.order_item_tax.clear();
                                    Globals.TotalItemPrice = 0;
                                    Globals.TotalQty = 0;
                                    Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (settings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(UserListActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(UserListActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                  //  pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            }
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(UserListActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("3")){
                            Intent intent = new Intent(UserListActivity.this, PaymentCollection_MainScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("4")){
                            Intent intent = new Intent(UserListActivity.this, ParkingIndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                /*    }
                };
                timerThread.start();*/

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(Globals.objLPR.getproject_id().equals("standalone")) {
            fab.setVisibility(View.VISIBLE);
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
        }
        else{
            fab.setVisibility(View.GONE);
        }
        try {
            //Get user list  here
            getUserList("");
        }
        catch(Exception e){}

    }

    private void getUserList(String strFilter) {
        try {
            arrayList = User.getAllUser(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(name) asc limit " + Globals.ListLimit + "", database, db);
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
        catch(Exception e){

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

        // Search user
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_bussiness_gp_list.getText().toString().trim();
            strFilter = " and ( user_code Like '%" + strFilter + "%'  Or name Like '%" + strFilter + "%' )";
            edt_toolbar_bussiness_gp_list.selectAll();
            getUserList(strFilter);
            return true;
        }

        // User Sync and send to server both
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
                                        String suss="";
                                        try {
                                            send_online_user();
                                        }
                                        catch(Exception e){

                                        }
                                        try {
                                            get_user_from_server(pDialog);

                                        }
                                        catch(Exception e){

                                        }
                                      //  pDialog.dismiss();

                                    }
                                }.start();
                            }
                        });


                AlertDialog alert = alertDialog.create();

                alert.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                      //  lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
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

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private String getUser(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here

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

   /* private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "user");
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

    }*/

    public void get_user_from_server(final ProgressDialog pDialog) {

    /*    pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage(getString(R.string.Syncdataserver));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "user";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUser(response);
                            switch (result) {
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

                        } catch (Exception e) {
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
       /* pDialog = new ProgressDialog(UserListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Globals.cart.clear();
                            Globals.order_item_tax.clear();
                            Globals.TotalItemPrice = 0;
                            Globals.TotalQty = 0;
                            Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                           // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(UserListActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                           // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(UserListActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                           // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")){

                    Intent intent = new Intent(UserListActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(Globals.objLPR.getIndustry_Type().equals("3")){

                    Intent intent = new Intent(UserListActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(Globals.objLPR.getIndustry_Type().equals("4")){

                    Intent intent = new Intent(UserListActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            /*}
        };
        timerThread.start();*/
    }
}
