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
import org.phomellolitepos.Adapter.UnitListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UnitListActivity extends AppCompatActivity {
    EditText edt_toolbar_unit_list;
    TextView unit_title;
    Unit unit;
    ArrayList<Unit> arrayList;
    UnitListAdapter unitListAdapter;
  //  Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    //Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
      //  settings = Settings.getSettings(getApplicationContext(), database, "");
        unit_title = (TextView) findViewById(R.id.unit_title);
        edt_toolbar_unit_list = (EditText) findViewById(R.id.edt_toolbar_unit_list);
        edt_toolbar_unit_list.setMaxLines(1);

        edt_toolbar_unit_list.setInputType(InputType.TYPE_CLASS_TEXT);
        edt_toolbar_unit_list.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edt_toolbar_unit_list.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String strFilter = edt_toolbar_unit_list.getText().toString().trim();
                    strFilter = " and ( name Like '%" + strFilter + "%' )";
                    edt_toolbar_unit_list.selectAll();
                    getUnitList(strFilter);
                    return true;

                }
                return false;
            }
        });

        edt_toolbar_unit_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_toolbar_unit_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_unit_list.requestFocus();
                    edt_toolbar_unit_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_unit_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_unit_list.selectAll();
                    return true;
                }
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id==0){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         /*       pDialog = new ProgressDialog(UnitListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/
                         if(Globals.objLPR.getIndustry_Type().equals("2")){

            Intent intent = new Intent(UnitListActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
           else {
                             if (Globals.objsettings.get_Home_Layout().equals("0")) {
                                 try {
                                     Intent intent = new Intent(UnitListActivity.this, MainActivity.class);
                                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                     startActivity(intent);
                                     //pDialog.dismiss();
                                     finish();
                                 } finally {
                                 }
                             } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                                 try {
                                     Intent intent = new Intent(UnitListActivity.this, RetailActivity.class);
                                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                     startActivity(intent);
                                     ////pDialog.dismiss();
                                     finish();
                                 } finally {
                                 }
                             } else {
                                 try {
                                     Intent intent = new Intent(UnitListActivity.this, Main2Activity.class);
                                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                     startActivity(intent);
                                     //pDialog.dismiss();
                                     finish();
                                 } finally {
                                 }
                             }
                         }
                  /*  }
                };
                timerThread.start();*/

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String operation = "Add";
                Intent intent = new Intent(UnitListActivity.this, UnitActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });

        try {
            getUnitList("");
        }
        catch(Exception e){

        }
    }


    private void getUnitList(String strFilter) {

        arrayList = Unit.getAllUnit(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By lower(name) asc limit "+Globals.ListLimit+"", database);
        ListView category_list = (ListView) findViewById(R.id.unit_list);
        if (arrayList.size() > 0) {


            unitListAdapter = new UnitListAdapter(UnitListActivity.this, arrayList);
            unit_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(unitListAdapter);
            category_list.setTextFilterEnabled(true);
            unitListAdapter.notifyDataSetChanged();

        } else {

            unit_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        if(Globals.objLPR.getproject_id().equals("standalone")) {
            menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Search filter by name
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_unit_list.getText().toString().trim();
            strFilter = " and ( name Like '%" + strFilter + "%' )";
            edt_toolbar_unit_list.selectAll();
            getUnitList(strFilter);
            return true;
        }

        // Sync code
        if (id == R.id.action_send) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    UnitListActivity.this);
            alertDialog.setTitle(R.string.Unit);
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
                                    UnitListActivity.this);
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
                                                pDialog = new ProgressDialog(UnitListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.downloading_unit));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        String suss="";
                                                        String result="";

                                                        try {
                                                             result = send_online_unit();
                                                        }
                                                        catch(Exception e){

                                                        }
                                                        try {
                                                        get_unit_from_server(pDialog);
                                                        }
                                                        catch(Exception e){

                                                        }
                                                      




                                                    }
                                                }.start();
                                            }else {
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

                    //lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
                    String ck_project_type = Globals.objLPR.getproject_id();

                    if (ck_project_type.equals("standalone")) {
                        ((AlertDialog)dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                        ((AlertDialog)dialog).getButton(
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


    private String getUnit(String serverData) {

        String succ_manu = "0";
        database.beginTransaction();

        try {

            final JSONObject jsonObject_unit = new JSONObject(serverData);
            final String strStatus = jsonObject_unit.getString("status");
            final String strResult = jsonObject_unit.getString("result");
            final JSONObject jsonObject = new JSONObject(strResult);
            if (strStatus.equals("true")) {

                JSONArray jsonArray_unit = jsonObject.getJSONArray("unit");
                for (int i = 0; i < jsonArray_unit.length(); i++) {
                    JSONObject jsonObjct_unit = jsonArray_unit.getJSONObject(i);
                    String unitId = jsonObjct_unit.getString("unit_id");

                    unit = Unit.getUnit(getApplicationContext(),database,db,"where unit_id='"+unitId+"'");
                    if (unit == null) {
                        unit = new Unit(getApplicationContext(), null,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.insertUnit(database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                    } else {
                        unit = new Unit(getApplicationContext(), unitId,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.updateUnit("unit_id=?", new String[]{unitId}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
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

  /*  private String get_unit_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "unit");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_data",""));
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

    public void get_unit_from_server(final ProgressDialog pDialog) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "unit";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUnit(response);
                            switch (result) {
                                case "1":
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            getUnitList("");
                                            Toast.makeText(getApplicationContext(), R.string.Unit_Download, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    break;
                                case "2":
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                default:
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), R.string.Unit_not_found, Toast.LENGTH_SHORT).show();
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
                params.put("modified_data","");
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String send_online_unit() {

        String result = Unit.sendOnServer(getApplicationContext(), database, db, "Select  * From unit  WHERE is_push = 'N'");
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

        /*pDialog = new ProgressDialog(UnitListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/

           if(Globals.objLPR.getIndustry_Type().equals("2")){

            Intent intent = new Intent(UnitListActivity.this, Retail_IndustryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
           else {
               if (Globals.objsettings.get_Home_Layout().equals("0")) {
                   try {
                       Intent intent = new Intent(UnitListActivity.this, MainActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       //  pDialog.dismiss();
                       finish();
                   } finally {
                   }
               } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                   try {
                       Intent intent = new Intent(UnitListActivity.this, RetailActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       // pDialog.dismiss();
                       finish();
                   } finally {
                   }
               } else {
                   try {
                       Intent intent = new Intent(UnitListActivity.this, Main2Activity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       // pDialog.dismiss();
                       finish();
                   } finally {
                   }
               }
           }
          /*  }
        };
        timerThread.start();*/
    }

}
