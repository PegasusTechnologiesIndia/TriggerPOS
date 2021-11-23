package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.SearchOrderListAdapter;
import org.phomellolitepos.Adapter.SearchOrderServerListAdapter;
import org.phomellolitepos.SearchOrder.SearchOrders;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.zbar.Result;
import org.phomellolitepos.zbar.ZBarScannerView;

import java.util.ArrayList;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class SearchOrderActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    EditText edt_barcode;
    Button btn_scan, btn_get_order, btn_print_kot;
    ImageView img_search;
    ListView list_search;
    ZBarScannerView mScannerView;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    SearchOrderListAdapter searchOrderListAdaoter;
    ArrayList<Orders> arrayList;
    ArrayList<SearchOrders> arrayListServer;
    Settings settings;
    private ICallback callback = null;
    private IWoyouService woyouService;
    Orders orders;
    String PrinterType;
    SearchOrders searchOrders = null;

    private static final String TAG = "PrinterTestDemo";

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };
    private final int MSG_TEST = 1;
    private long printCount = 0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TEST) {
                //testAll();
                long mm = MemInfo.getmem_UNUSED(SearchOrderActivity.this);
                if (mm < 100) {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_TEST, 800);
                }
                Log.i(TAG, "testAll: " + printCount + " Memory: " + mm);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        getSupportActionBar().setTitle("Search Order");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        settings = Settings.getSettings(getApplicationContext(), database, "");

        callback = new ICallback.Stub() {

            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "printlength:" + value + "\n");
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "onRaiseException: " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        };
        Intent intent_1 = new Intent();
        intent_1.setPackage("woyou.aidlservice.jiuiv5");
        intent_1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_1);
        bindService(intent_1, connService, Context.BIND_AUTO_CREATE);

        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(SearchOrderActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(SearchOrderActivity.this, MainActivity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                mScannerView.stopCamera(); // Programmatically initialize the scanner view
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        } else {
                            try {
                                sleep(1000);
                                Intent intent = new Intent(SearchOrderActivity.this, Main2Activity.class);
                                startActivity(intent);
                                pDialog.dismiss();
                                mScannerView.stopCamera(); // Programmatically initialize the scanner view
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });
        edt_barcode = (EditText) findViewById(R.id.edt_barcode);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_get_order = (Button) findViewById(R.id.btn_get_order);
        btn_print_kot = (Button) findViewById(R.id.btn_print_kot);
        img_search = (ImageView) findViewById(R.id.img_search);
        list_search = (ListView) findViewById(R.id.list_search);
       /* if (Globals.Industry_Type.equals("4")){
            btn_print_kot.setVisibility(View.GONE);
        }*/

        edt_barcode.setText(Globals.BarcodeReslt);
        mScannerView = new ZBarScannerView(SearchOrderActivity.this);

        mScannerView.setResultHandler(SearchOrderActivity.this);
        if (settings == null) {
            PrinterType = "";
        } else {
            try {
                PrinterType = settings.getPrinterId();
            } catch (Exception ex) {

                PrinterType = "";
            }
        }
        btn_print_kot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (edt_barcode.getText().toString().trim().equals("")) {
                        edt_barcode.setError("Field vaccant");
                        return;
                    } else {
                        orders = Orders.getOrders(getApplicationContext(), database, " where order_code = '" + edt_barcode.getText().toString().trim() + "'");
                        if (orders == null) {
                            Toast.makeText(getApplicationContext(), R.string.Order_Not_Found, Toast.LENGTH_SHORT).show();
                        } else {
                            if (settings.get_Is_KOT_Print().equals("true")) {
                                if (PrinterType.equals("1")) {
                                    try {
                                        if (woyouService==null){
                                        }else {
                                            print_kot_mobilepos(orders);
                                        }
                                    } catch (Exception ex) {
                                    }

                                }else if (PrinterType.equals("4")){
                                    print_kot_phapos(orders);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SearchOrderActivity.this, R.string.chkpriset, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Globals.BarcodeReslt = "";
                    mScannerView.startCamera(); // Programmatically initialize the scanner view
                    setContentView(mScannerView);
                } catch (Exception ex) {}
            }
        });

        btn_get_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_barcode.getText().toString().trim().equals("")) {
                    edt_barcode.setError("Field vaccant");
                    return;
                } else {
                    try {
                        if (settings.get_IsOnline().equals("true")) {

                            if (isNetworkStatusAvialable(getApplicationContext())) {
                                pDialog = new ProgressDialog(SearchOrderActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.down_item_grp));
                                pDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {

                                        final ArrayList<SearchOrders> listServer = getOrder();
                                        pDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                load_server_list(listServer);
                                            }
                                        });


                                    }
                                }.start();

                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            String strFilter = edt_barcode.getText().toString().trim();
                            strFilter = " and (contact.contact_1 like '%" + strFilter + "%' or orders.order_code like '%" + strFilter + "%' or orders.remarks like '%" + strFilter + "%')";
                            edt_barcode.selectAll();
                            getOrderList(strFilter);
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_barcode.getText().toString().trim().equals("")) {
                    edt_barcode.setError("Field vaccant");
                    return;
                } else {
                    try {
                        String strFilter = edt_barcode.getText().toString().trim();
                        strFilter = " and (contact.contact_1 like '%" + strFilter + "%' or orders.order_code like '%" + strFilter + "%' or orders.remarks like '%" + strFilter + "%')";
                        edt_barcode.selectAll();
                        getOrderList(strFilter);
                    } catch (Exception ex) {
                    }
                }

            }
        });
    }



    private void load_server_list(ArrayList<SearchOrders> arrayListServer) {
        if (arrayListServer.size() > 0) {
            SearchOrderServerListAdapter searchOrderServerListAdapter = new SearchOrderServerListAdapter(SearchOrderActivity.this, arrayListServer);
            list_search.setVisibility(View.VISIBLE);
            list_search.setAdapter(searchOrderServerListAdapter);
            list_search.setTextFilterEnabled(true);
            searchOrderServerListAdapter.notifyDataSetChanged();
        } else {
            list_search.setVisibility(View.GONE);
        }
    }

    private void getOrderList(String strFilter) {
        arrayList = Orders.getAllOrdersSearch(getApplicationContext(), " left join contact on contact.contact_code = orders.contact_code where orders.is_active = '1' " + strFilter + " Order By lower(orders.order_code) asc", database);
        if (arrayList.size() > 0) {
            searchOrderListAdaoter = new SearchOrderListAdapter(SearchOrderActivity.this, arrayList);
            list_search.setVisibility(View.VISIBLE);
            list_search.setAdapter(searchOrderListAdaoter);
            list_search.setTextFilterEnabled(true);
            searchOrderListAdaoter.notifyDataSetChanged();
        } else {
            list_search.setVisibility(View.GONE);
        }
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.v("BarcodeResult", rawResult.getContents()); // Prints scan results
        Log.v("BarcodeResult", rawResult.getBarcodeFormat().getName());
        // Prints the scan format (qrcode, pdf417 etc.)
        Globals.BarcodeReslt = rawResult.getContents();
        mScannerView.stopCamera();
        Intent intent = new Intent(SearchOrderActivity.this, SearchOrderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(SearchOrderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(SearchOrderActivity.this, MainActivity.class);
                        startActivity(intent);
                        pDialog.dismiss();
                        mScannerView.stopCamera(); // Programmatically initialize the scanner view
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                } else {
                    try {
                        sleep(1000);
                        Intent intent = new Intent(SearchOrderActivity.this, Main2Activity.class);
                        startActivity(intent);
                        pDialog.dismiss();
                        mScannerView.stopCamera(); // Programmatically initialize the scanner view
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();

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


    private ArrayList<SearchOrders> getOrder() {
        String succ_bg = "0";
        String serverData = "";
                //get_order_from_server();
        final ArrayList<SearchOrders> ordersArrayList = new ArrayList<>();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            JSONObject jsonObjResult = jsonObject_bg.getJSONObject("result");
            JSONArray jsonArray = jsonObjResult.getJSONArray("order");
            if (strStatus.equals("true")) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);

                try {
                        searchOrders = new SearchOrders(jsonObject.getString("order_code"), jsonObject.getString("order_date"), jsonObject.getString("total"));
                        ordersArrayList.add(searchOrders);
                    } catch (JSONException e) {
                        String ab = e.getMessage();
                        ab = ab;
                    }

                }
            } else {
            }

            if (succ_bg.equals("1")) {

            } else {

            }
        } catch (JSONException e) {


        }
        return ordersArrayList;
    }

  /*  private String get_order_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "Check_order");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        nameValuePairs.add(new BasicNameValuePair("order_code", edt_barcode.getText().toString().trim()));
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
    private void print_kot_mobilepos(final Orders strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {

                    String Print_type = "0";
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback);
                    woyouService.setFontSize(30, callback);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);
                    }

                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{8, 1, 20}, new int[]{0, 0, 0}, callback);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo.get_order_code() + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback);
                        woyouService.printTextWithFont("--------------------------------\n", "", 24, callback);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X" + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback);
                    woyouService.printTextWithFont("\n", "", 24, callback);
                    woyouService.printTextWithFont("\n", "", 24, callback);


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void print_kot_phapos(final Orders strOrderNo) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {

                    String Print_type = "0";
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("Order : " + orders.get_order_code() + "\n", "", 40, callback);
                    woyouService.setFontSize(30, callback);
                    if (orders.get_table_code().equals("")) {
                    } else {
                        woyouService.printColumnsText(new String[]{"Table", ":", orders.get_table_code()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);
                    }
                    woyouService.setFontSize(35, callback);
                    User user = User.getUser(getApplicationContext(), " Where user_code='" + Globals.user + "'", database);
                    woyouService.printColumnsText(new String[]{"Waiter", ":", user.get_name()}, new int[]{14, 1, 20}, new int[]{0, 0, 0}, callback);

                    ArrayList<Order_Detail> order_detail = Order_Detail.getAllOrder_Detail(getApplicationContext(), " WHERE order_code='" + strOrderNo.get_order_code() + "'", database);
                    if (order_detail.size() > 0) {
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);
                        woyouService.printColumnsText(new String[]{"Product Name", "Quantity"}, new int[]{16, 13}, new int[]{0, 0}, callback);
                        woyouService.printTextWithFont("------------------------------------------------\n", "", 24, callback);

                        for (int i = 0; i < order_detail.size(); i++) {
                            Item item = Item.getItem(getApplicationContext(), " WHERE item_code='" + order_detail.get(i).get_item_code() + "'", database, db);
                            woyouService.setFontSize(40, callback);
                            woyouService.printColumnsText(new String[]{item.get_item_name(), "X " + order_detail.get(i).get_quantity()}, new int[]{14, 13}, new int[]{0, 0}, callback);
                        }
                    }

                    woyouService.printTextWithFont("\n", "", 24, callback);
                    woyouService.printTextWithFont("\n", "", 24, callback);
                    woyouService.printTextWithFont("\n", "", 24, callback);

                    woyouService.cutPaper(callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
