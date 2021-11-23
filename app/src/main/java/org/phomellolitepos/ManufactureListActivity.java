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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.phomellolitepos.Adapter.ManufactureListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Manufacture;
import org.phomellolitepos.database.Settings;

public class ManufactureListActivity extends AppCompatActivity {
    EditText edt_toolbar_manufacture_list;
    TextView manufacture_title;
    Manufacture manufacture;
    ArrayList<Manufacture> arrayList;
    ManufactureListAdapter manufactureListAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacture_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_manufacture_list = (EditText) findViewById(R.id.edt_toolbar_manufacture_list);
        manufacture_title = (TextView) findViewById(R.id.manufacture_title);

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
                pDialog = new ProgressDialog(ManufactureListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(ManufactureListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(ManufactureListActivity.this, Main2Activity.class);
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
                Intent intent = new Intent(ManufactureListActivity.this, ManufactureActivity.class);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        getManufactureList("");
    }

    private void getManufactureList(String strFilter) {

        arrayList = Manufacture.getAllManufacture(getApplicationContext(), "WHERE is_active = '1' " + strFilter + " Order By manufacture_name asc limit "+Globals.ListLimit+"", database);
        ListView manufacture_list = (ListView) findViewById(R.id.manufacture_list);
        if (arrayList.size() > 0) {
            manufactureListAdapter = new ManufactureListAdapter(ManufactureListActivity.this, arrayList);
            manufacture_title.setVisibility(View.GONE);
            manufacture_list.setVisibility(View.VISIBLE);
            manufacture_list.setAdapter(manufactureListAdapter);
            manufacture_list.setTextFilterEnabled(true);
            manufactureListAdapter.notifyDataSetChanged();
        } else {
            manufacture_title.setVisibility(View.VISIBLE);
            manufacture_list.setVisibility(View.GONE);
        }

    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        MenuItem item = menu.findItem(R.id.action_send);
//        lite_pos_registration = lite_pos_registration.getRegistration(getApplicationContext(), database, db, "");
//        String ck_project_type = lite_pos_registration.getproject_id();
//
//        if (ck_project_type.equals("standalone")) {
//            item.setEnabled(false);
//            item.getIcon().setAlpha(130);
//
//        } else {
//            item.setEnabled(true);
//            item.getIcon().setAlpha(255);
//        }
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_manufacture_list.getText().toString().trim();
            strFilter = " and ( manufacture_code Like '%" + strFilter + "%' Or manufacture_name Like '%" + strFilter + "%' )";
            edt_toolbar_manufacture_list.selectAll();
            getManufactureList(strFilter);
            return true;
        }
        if (id == R.id.action_send) {


            //dialog for download

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    ManufactureListActivity.this);
            alertDialog.setTitle(R.string.Manufacture);
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
                                    ManufactureListActivity.this);
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
                                                pDialog = new ProgressDialog(ManufactureListActivity.this);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage(getString(R.string.Downloading_manufacture));
                                                pDialog.show();
                                                new Thread() {
                                                    @Override
                                                    public void run() {

                                                        String suss = getManufacture();
                                                        pDialog.dismiss();

                                                        switch (suss) {
                                                            case "1":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getManufactureList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Manufacture_download, Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                                break;

                                                            case "2":
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getManufactureList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.srvr_error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                break;

                                                            default:
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        getManufactureList("");
                                                                        Toast.makeText(getApplicationContext(), R.string.Manufacture_data_not_fnd, Toast.LENGTH_SHORT).show();
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
                                    ManufactureListActivity.this);
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
                                                pDialog = new ProgressDialog(ManufactureListActivity.this);
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

    private String send_online_manufacture() {

        String mStr = Manufacture.sendOnServer(getApplicationContext(), database, db, "Select  * From manufacture  WHERE is_push = 'N'");

        return mStr;
    }

    private String getManufacture() {
        String succ_manu = "0";
        database.beginTransaction();
        String serverData = "";
               // get_manufacture_from_server();
        try {

            final JSONObject jsonObject_manufacture = new JSONObject(serverData);
            final String strStatus = jsonObject_manufacture.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_manufacture = jsonObject_manufacture.getJSONArray("result");
                for (int i = 0; i < jsonArray_manufacture.length(); i++) {
                    JSONObject jsonObject_manufactrue1 = jsonArray_manufacture.getJSONObject(i);

                    String mnc_code = jsonObject_manufactrue1.getString("manufacture_code");
                    String mnc_id = jsonObject_manufactrue1.getString("manufacture_id");


                    manufacture = manufacture.getManufacture(getApplicationContext(), database, db, "WHERE manufacture_code ='" + mnc_code + "'");

                    if (manufacture == null) {
                        manufacture = new Manufacture(getApplicationContext(), null, jsonObject_manufactrue1.getString("device_code"), jsonObject_manufactrue1.getString("manufacture_code"), jsonObject_manufactrue1.getString("manufacture_name"), jsonObject_manufactrue1.getString("image"), jsonObject_manufactrue1.getString("is_active"), jsonObject_manufactrue1.getString("modified_by"), jsonObject_manufactrue1.getString("modified_date"), "N");

                        long l = manufacture.insertManufacture(database);

                        if (l > 0) {
                            succ_manu = "1";
//                            database.setTransactionSuccessful();
//                            database.endTransaction();

                        } else {
                            //database.endTransaction();
                        }

                    } else {
                        //database.beginTransaction();
                        manufacture = new Manufacture(getApplicationContext(), mnc_id, jsonObject_manufactrue1.getString("device_code"), jsonObject_manufactrue1.getString("manufacture_code"), jsonObject_manufactrue1.getString("manufacture_name"), jsonObject_manufactrue1.getString("image"), jsonObject_manufactrue1.getString("is_active"), jsonObject_manufactrue1.getString("modified_by"), jsonObject_manufactrue1.getString("modified_date"), "N");

                        long l = manufacture.updateManufacture("manufacture_code=? And manufacture_id=? ", new String[]{mnc_code, mnc_id}, database);
                        if (l > 0) {
                            succ_manu = "1";
//                            database.setTransactionSuccessful();
//                            database.endTransaction();

                        } else {
                            // database.endTransaction();
                        }
                    }
                }

            } else {
                //database.endTransaction();
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

  /*  private String get_manufacture_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "manufacture");
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
        pDialog = new ProgressDialog(ManufactureListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(ManufactureListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(ManufactureListActivity.this, Main2Activity.class);
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
