package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.MainActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;

import sunmi.bean.SecondScreenData;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.ISendFilesCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.Data;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;
import woyou.aidlservice.jiuiv5.IWoyouService;

import static org.phomellolitepos.R.id.imageView;

public class MainItemListAdapter extends BaseAdapter implements Animation.AnimationListener {
    Context context;
    LayoutInflater inflater;
    ArrayList<Item> data;
    Item_Location item_location;
    String sale_priceStr;
    String cost_priceStr;
    String decimal_check, qty_decimal_check;
    ListView listView;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    SQLiteDatabase database;
    Database db;
    //Customer display variables
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    Settings settings;
    String item_group_code;
    int count = 0;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    String path2 = Environment.getExternalStorageDirectory().getPath() + "/big.png";
    String path3 = Environment.getExternalStorageDirectory().getPath()
            + "/qrcode.png";
    Activity activity;
    private final static int FADE_DURATION = 300;
    Double curQty = 0d;
    ProgressBar progressBar;
    IWoyouService woyouService;
    private ISendFilesCallback callback = new ISendFilesCallback() {
        @Override
        public void onAllSendSuccess(long fileid) {
            showQRCode(fileid);
        }

        @Override
        public void onSendSuccess(String path, long taskId) {
        }

        @Override
        public void onSendFaile(int errorId, String errorInfo) {
        }

        @Override
        public void onSendFileFaile(String path, int errorId, String errorInfo) {
        }

        @Override
        public void onSendProcess(String path, long totle, long sended) {
        }
    };

    private ISendCallback callback1 = new ISendCallback() {
        @Override
        public void onSendFail(int arg0, String arg1) {
        }

        @Override
        public void onSendProcess(long arg0, long arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSendSuccess(long arg0) {
        }
    };

    private IConnectionCallback mConnCallback = new IConnectionCallback() {
        @Override
        public void onDisConnect() {
        }

        @Override
        public void onConnected(final ConnState state) {
        }
    };


    // 接收副屏数据的回调
    private IReceiveCallback mReceiveCallback = new IReceiveCallback() {
        @Override
        public void onReceiveFile(DSFile arg0) {
            // TODO
        }

        @Override
        public void onReceiveFiles(DSFiles dsFiles) {
            // TODO
        }

        @Override
        public void onReceiveData(DSData data) {
            if (dsPacket == null) return;
            long taskId = dsPacket.getData().taskId;
            Gson gson = new Gson();
            Data data4Json = gson.fromJson(data.data, Data.class);
            if (taskId == data.taskId) {
                final SecondScreenData secondScreenData = gson.fromJson(data4Json.data, SecondScreenData.class);
            }
        }

        @Override
        public void onReceiveCMD(DSData arg0) {
            // TODO
        }
    };

    public MainItemListAdapter(Context context, ArrayList<Item> list, ListView category_list, ProgressBar progressBar1) {
        this.context = context;
        data = list;
        this.listView = category_list;
        this.progressBar = progressBar1;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


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

    @Override
    public View getView(final int i, final View view, ViewGroup viewGroup) {
        TextView txt_item_name, txt_item_code, txt_price;
        db = new Database(context);
        database = db.getWritableDatabase();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.main_list_item, viewGroup, false);
        final Item resultp = data.get(i);
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_code = (TextView) itemView.findViewById(R.id.txt_item_code);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        settings = Settings.getSettings(context, database, "");
        if (settings.get_Is_Customer_Display().equals("true")) {
            mDSKernel = DSKernel.newInstance();
            mDSKernel.checkConnection();
            mDSKernel.init(context, mConnCallback);
            mDSKernel.addReceiveCallback(mReceiveCallback);
        }
        Intent intent1 = new Intent();
        intent1.setPackage("woyou.aidlservice.jiuiv5");
        intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        context.startService(intent1);
        context.bindService(intent1, connService, Context.BIND_AUTO_CREATE);
        //setAnimation(itemView, i);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        try {
            txt_item_name.setText(resultp.get_item_name());
            txt_item_code.setText(resultp.get_item_code());
            String sale_price = "0";
            try {
                item_location = Item_Location.getItem_Location(context, " WHERE item_code = '" + resultp.get_item_code() + "'", database);
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
            } catch (Exception ex) {
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_price), decimal_check);
            }
            txt_price.setText(sale_price);
            progressBar.setVisibility(View.GONE);
        } catch (Exception ex) {
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                progressBar.setVisibility(View.VISIBLE);
                if (settings.get_Is_Stock_Manager().equals("false")) {
                    try {
                        Item resultp = data.get(i);
                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);
                        String item_code = resultp.get_item_code();
                        item_location = Item_Location.getItem_Location(context, "Where item_code = '" + item_code + "'", database);
                        final ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {
//                            setAnimation(itemView, i);
                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                Globals.TotalQty = Globals.TotalQty + 1;
                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                String salePrice;
                                salePrice = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Sales_Price()), decimal_check);
                                if (settings.get_Is_Customer_Display().equals("true")) {
                                    if (settings.get_CustomerDisplay().equals("1")) {
                                        try {
                                            Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                            Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                            jsonObject = new JSONObject();
                                            jsonObject.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                            jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                            mDSKernel.sendData(dsPacket);
                                        } catch (Exception ex) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        try {
                                            woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                        } catch (Exception ex) {
                                        }
                                    } else {
                                        try {
                                            JSONObject json = new JSONObject();
                                            json.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                            json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            String titleContentJsonStr = json.toString();
                                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, path1, new ISendCallback() {
                                                @Override
                                                public void onSendSuccess(long fileId) {
                                                    showQRCode(fileId);//sending the qr-code image
                                                }

                                                public void onSendFail(int i, String s) {
                                                    //failure
                                                }

                                                public void onSendProcess(long l, long l1) {
                                                    //sending status
                                                }
                                            });
                                        } catch (Exception ex) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        try {
                                            woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                        } catch (Exception ex) {
                                        }
                                        ((MainActivity) context).callHandler();
                                    }
                                }

                            }
                            count = count + 1;
                        }
                        if (!bFound) {

                            //setAnimation(itemView, i);
                            if (item_location == null) {
                                sale_priceStr = "0";
                                cost_priceStr = "0";
                            } else {
                                sale_priceStr = item_location.get_selling_price();
                                cost_priceStr = item_location.get_cost_price();
                            }
                            calculateTax();
                            ArrayList<String> taxIdFinalAarry = calculateTax();
                            Double iTax = 0d;
                            Double iTaxTotal = 0d;
                            if (taxIdFinalAarry.size() > 0) {
                                for (int i = 0; i < taxIdFinalAarry.size(); i++) {
                                    iTax = 0d;
                                    Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + taxIdFinalAarry.get(i) + "'", database, db);
                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                    } else {
                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                    }
                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), taxIdFinalAarry.get(i), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                    Globals.order_item_tax.add(order_item_tax);
                                }
                            }
                            Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                            ShoppingCart cartItem = new ShoppingCart(context, Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
                            Globals.cart.add(cartItem);
                            Globals.SRNO = Globals.SRNO + 1;
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                            Globals.TotalItem = Globals.TotalItem + 1;
                            Globals.TotalQty = Globals.TotalQty + 1;
                            final String salePrice;
                            salePrice = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                            Globals.CMDItemPrice = Double.parseDouble(salePrice);
                            Globals.CMDItemName = resultp.get_item_name();
                            if (settings.get_Is_Customer_Display().equals("true")) {
                                if (settings.get_CustomerDisplay().equals("1")) {
                                    try {
                                        Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                        Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                        jsonObject = new JSONObject();
                                        jsonObject.put("title", resultp.get_item_name() + " " + salePrice);
                                        jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                        dsPacket = UPacketFactory.buildShowText(
                                                DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                        mDSKernel.sendData(dsPacket);
                                    } catch (Exception ex) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    try {
                                        woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                    } catch (Exception ex) {
                                    }
                                } else {
                                    try {
                                        JSONObject json = new JSONObject();
                                        json.put("title", resultp.get_item_name() + " " + salePrice);
                                        json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                        String titleContentJsonStr = json.toString();
                                        mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, path1, new ISendCallback() {
                                            @Override
                                            public void onSendSuccess(long fileId) {
                                                showQRCode(fileId);//sending the qr-code image
                                            }

                                            public void onSendFail(int i, String s) {
                                                //failure
                                            }

                                            public void onSendProcess(long l, long l1) {
                                                //sending status
                                            }
                                        });
                                    } catch (Exception ex) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    try {
                                        woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                    } catch (Exception ex) {
                                    }
                                    ((MainActivity) context).callHandler();
                                }
                            }
                        }
                        Globals.cart = myCart;
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        ((MainActivity) context).setTextView(item_price, Globals.TotalQty + "");
                    } catch (Exception ex) {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    try {
                        Item resultp = data.get(i);
                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);
                        String item_code = resultp.get_item_code();
                        item_location = Item_Location.getItem_Location(context, "Where item_code = '" + item_code + "'", database);
                        final ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {
                            //setAnimation(itemView, i);
                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                boolean result = stock_check(item_code, curQty + 1);
                                if (result == true) {
                                    myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                    Globals.TotalQty = Globals.TotalQty + 1;
                                    Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                    String salePrice;
                                    salePrice = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Sales_Price()), decimal_check);
                                    if (settings.get_Is_Customer_Display().equals("true")) {
                                        if (settings.get_CustomerDisplay().equals("1")) {
                                            try {
                                                Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                                Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                                jsonObject = new JSONObject();
                                                jsonObject.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                                jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                                dsPacket = UPacketFactory.buildShowText(
                                                        DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                                mDSKernel.sendData(dsPacket);
                                            } catch (Exception ex) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            try {
                                                woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                            } catch (Exception ex) {
                                            }
                                        } else {
                                            try {
                                                JSONObject json = new JSONObject();
                                                json.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                                json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                                String titleContentJsonStr = json.toString();
                                                mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, path1, new ISendCallback() {
                                                    @Override
                                                    public void onSendSuccess(long fileId) {
                                                        showQRCode(fileId);//sending the qr-code image
                                                    }

                                                    public void onSendFail(int i, String s) {
                                                        //failure
                                                    }

                                                    public void onSendProcess(long l, long l1) {
                                                        //sending status
                                                    }
                                                });

                                            } catch (Exception ex) {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            try {
                                                woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                            } catch (Exception ex) {
                                            }
                                            ((MainActivity) context).callHandler();
                                        }
                                    }
                                }
                            }
                            count = count + 1;
                        }
                        if (!bFound) {
                            curQty = 0d;
                            boolean result = stock_check(item_code, Double.parseDouble("1"));
                            if (result == true) {
                                //setAnimation(itemView, i);
                                if (item_location == null) {
                                    sale_priceStr = "0";
                                    cost_priceStr = "0";
                                } else {
                                    sale_priceStr = item_location.get_selling_price();
                                    cost_priceStr = item_location.get_cost_price();
                                }
                                calculateTax();
                                ArrayList<String> taxIdFinalAarry = calculateTax();
                                Double iTax = 0d;
                                Double iTaxTotal = 0d;
                                if (taxIdFinalAarry.size() > 0) {
                                    for (int i = 0; i < taxIdFinalAarry.size(); i++) {
                                        iTax = 0d;
                                        Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + taxIdFinalAarry.get(i) + "'", database, db);
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                        if (tax_master.get_tax_type().equals("P")) {
                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                        } else {
                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                        }
                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), taxIdFinalAarry.get(i), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                        Globals.order_item_tax.add(order_item_tax);
                                    }
                                }
                                Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                ShoppingCart cartItem = new ShoppingCart(context, Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
                                Globals.cart.add(cartItem);
                                Globals.SRNO = Globals.SRNO + 1;
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                Globals.TotalItem = Globals.TotalItem + 1;
                                Globals.TotalQty = Globals.TotalQty + 1;
                                final String salePrice;
                                salePrice = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                                Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                Globals.CMDItemName = resultp.get_item_name();
                                if (settings.get_Is_Customer_Display().equals("true")) {
                                    if (settings.get_CustomerDisplay().equals("1")) {
                                        try {
                                            Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                            Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                            jsonObject = new JSONObject();
                                            jsonObject.put("title", resultp.get_item_name() + " " + salePrice);
                                            jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                            mDSKernel.sendData(dsPacket);
                                        } catch (Exception ex) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        try {
                                            woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                        } catch (Exception ex) {
                                        }
                                    } else {
                                        try {
                                            JSONObject json = new JSONObject();
                                            json.put("title", resultp.get_item_name() + " " + salePrice);
                                            json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            String titleContentJsonStr = json.toString();
                                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), titleContentJsonStr, path1, new ISendCallback() {
                                                @Override
                                                public void onSendSuccess(long fileId) {
                                                    showQRCode(fileId);//sending the qr-code image
                                                }

                                                public void onSendFail(int i, String s) {
                                                    //failure
                                                }

                                                public void onSendProcess(long l, long l1) {
                                                    //sending status
                                                }
                                            });
                                        } catch (Exception ex) {
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        try {
                                            woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                        } catch (Exception ex) {
//                                        progressBar.setVisibility(View.GONE);
                                        }
                                        ((MainActivity) context).callHandler();
                                    }

                                }

                            }
                        }
                        Globals.cart = myCart;
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        ((MainActivity) context).setTextView(item_price, Globals.TotalQty + "");

                    } catch (Exception ex) {
                        progressBar.setVisibility(View.GONE);
                        String ab = ex.getMessage();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                try {
                    String flag = "Main";
                    String item_code = resultp.get_item_code();
                    Item_Location item_location = Item_Location.getItem_Location(context, " where item_code='" + item_code + "'", database);
                    if (item_location == null) {
                        Toast.makeText(context, "Item not found in this location", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, ChangePriceActivity.class);
                        intent.putExtra("arr_position", i + "");
                        intent.putExtra("item_code", item_code);
                        intent.putExtra("opr", Globals.Operation);
                        intent.putExtra("odr_code", Globals.Order_Code);
                        intent.putExtra("flag", flag);
                        context.startActivity(intent);
                    }
                } catch (Exception ex) {
                }
                return false;
            }
        });

        return itemView;
    }

    private ArrayList<String> calculateTax() {
        ArrayList<Tax_Detail> taxIdAarry = new ArrayList<Tax_Detail>();
        ArrayList<String> taxIdFinalAarry = new ArrayList<String>();
        ArrayList<Item_Group_Tax> item_group_taxList = new ArrayList<Item_Group_Tax>();
        try {
            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {
                Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(context, database, db, "where type='Interstate'");
                taxIdAarry = Tax_Detail.getAllTax_Detail(context, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                if (taxIdAarry.size() > 0) {
                    for (int i = 0; i < taxIdAarry.size(); i++) {
                        Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(context, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                } else {
                    item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);
                    for (int i = 0; i < item_group_taxList.size(); i++) {
                        Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                }
            } else {
                Contact contact = Contact.getContact(context, database, db, "WHERE contact_code='" + Globals.strContact_Code + "'");
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(context, database, db, "");
                if (contact.get_zone_id().equals(lite_pos_registration.getZone_Id())) {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(context, database, db, "where type='Interstate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(context, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(context, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                } else {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(context, database, db, "where type='Intrastate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(context, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(context, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return taxIdFinalAarry;
    }

    private void showQRCode(long fileId) {

        try {
            String json = UPacketFactory.createJson(sunmi.ds.data.DataModel.QRCODE, "");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void setAnimation(View viewToAnimate, int position) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        viewToAnimate.startAnimation(anim);
    }

    private Boolean stock_check(String item_code, Double Qty) {
        boolean flag = false;
        try {
            Item_Location item_location = Item_Location.getItem_Location(context, " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
            if (avl_stock >= Qty) {
                flag = true;
            } else {
                Toast.makeText(context, "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(context, "Item not found in this location", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }
}

