package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import org.phomellolitepos.Adapter.OrderTypeListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Tax_Master;

public class OrderTypeListActivity extends AppCompatActivity {

    EditText edt_toolbar_odrty_list;
    TextView order_type_title;
    Order_Type order_type;
    ArrayList<Order_Type> arrayList;
    OrderTypeListAdapter orderTypeListAdapter;
    Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        edt_toolbar_odrty_list = (EditText) findViewById(R.id.edt_toolbar_odrty_list);

        order_type_title = (TextView) findViewById(R.id.order_type_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(OrderTypeListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(OrderTypeListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        getOrderTypeList("");

    }

    private void getOrderTypeList(String strFilter) {

        arrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "WHERE is_active = '1' " + strFilter, database);
        ListView category_list = (ListView) findViewById(R.id.order_type_list);
        if (arrayList.size() > 0) {

//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.category_list_item ,arrayList);
            orderTypeListAdapter = new OrderTypeListAdapter(OrderTypeListActivity.this, arrayList);
            order_type_title.setVisibility(View.GONE);
            category_list.setVisibility(View.VISIBLE);
            category_list.setAdapter(orderTypeListAdapter);
            category_list.setTextFilterEnabled(true);
            orderTypeListAdapter.notifyDataSetChanged();

        } else {

            order_type_title.setVisibility(View.VISIBLE);
            category_list.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_send);
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");
        String ck_project_type = lite_pos_registration.getproject_id();

        if (ck_project_type.equals("standalone")) {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);

        } else {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_odrty_list.getText().toString().trim();
            strFilter = "and ( name Like '%" + strFilter + "%' )";
            getOrderTypeList(strFilter);
            return true;
        }
//        if (id == R.id.action_send) {
//
//            if (isNetworkStatusAvialable(getApplicationContext())) {
//
//                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                        OrderTypeListActivity.this);
//                alertDialog.setTitle("Order Type");
//                alertDialog
//                        .setMessage("Do you want to do cloud operation!");
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//
//
//                alertDialog.setNegativeButton("Get",
//
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                pDialog = new ProgressDialog(OrderTypeListActivity.this);
//                                pDialog.setCancelable(false);
//                                pDialog.setMessage("Downloading tax....");
//                                pDialog.show();
//                                new Thread() {
//                                    @Override
//                                    public void run() {
//
//                                        String suss = getorder_type();
//                                        pDialog.dismiss();
//
//                                        if (suss.equals("1")) {
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//
//                                                    Toast.makeText(getApplicationContext(), "Tax download!", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                        } else {
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//
//                                                    Toast.makeText(getApplicationContext(), "Tax data not found!", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//
//                                    }
//                                }.start();
//                            }
//                        });
//
//                alertDialog.setPositiveButton("Post",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//
//                                pDialog = new ProgressDialog(OrderTypeListActivity.this);
//                                pDialog.setCancelable(false);
//                                pDialog.setMessage("Sending data on server....");
//                                pDialog.show();
//                                new Thread() {
//                                    @Override
//                                    public void run() {
//
//                                        send_online_order_type();
//
//                                        pDialog.dismiss();
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                Toast.makeText(getApplicationContext(), "Data post successfully", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//
//                                }.start();
//
//                            }
//
//
//                        });
//
//                alertDialog.show();
//
//            } else {
//                Toast.makeText(getApplicationContext(), "internet Not avialable", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }

        return super.onOptionsItemSelected(item);
    }


//    private String getorder_type() {
//
//        String succ_manu = "0";
//
//        String serverData = get_order_type_from_server();
//        try {
//
//            final JSONObject jsonObject_tax = new JSONObject(serverData);
//            final String strStatus = jsonObject_tax.getString("status");
//            if (strStatus.equals("true")) {
//
//                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
//                for (int i = 0; i < jsonArray_tax.length(); i++) {
//                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
//
//                    String taxId = jsonObject_tax1.getString("tax_id");
//                    String taxName = jsonObject_tax1.getString("tax_name");
//
//                    database.beginTransaction();
//                    order_type = Order_Type.getOrder_Type(getApplicationContext(), "WHERE order_type_id ='" + taxId + "'", database, db);
//                    if (tax_master == null) {
//                        tax_master = new Tax_Master(getApplicationContext(), null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "N");
//
//                        long l = tax_master.insertTax_Master(database);
//
//                        if (l > 0) {
//                            succ_manu = "1";
//                            database.setTransactionSuccessful();
//                            database.endTransaction();
//
//                        } else {
//                            database.endTransaction();
//                        }
//
//                    } else {
//                        database.beginTransaction();
//                        tax_master = new Tax_Master(getApplicationContext(), taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "N");
//
//                        long l = tax_master.updateTax_Master("tax_id=? And tax_name=? ", new String[]{taxId, taxName}, database);
//                        if (l > 0) {
//                            succ_manu = "1";
//                            database.setTransactionSuccessful();
//                            database.endTransaction();
//
//                        } else {
//                            database.endTransaction();
//                        }
//                    }
//                }
//            } else {
//
//            }
//        } catch (JSONException e) {
//
//        }
//
//        return succ_manu;
//    }

 /*   private String get_order_type_from_server() {

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

    }
*/

    private void send_online_order_type() {

        Tax_Master.sendOnServer(getApplicationContext(), database, db, "Select  * From tax  WHERE is_push = 'N'");
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
        if (settings.get_Home_Layout().equals("0")) {
            Intent intent = new Intent(OrderTypeListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(OrderTypeListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
