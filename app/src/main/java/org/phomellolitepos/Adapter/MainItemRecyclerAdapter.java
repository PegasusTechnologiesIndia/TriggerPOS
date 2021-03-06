package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.ItemModifierSelection;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;

import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Master;

import java.util.ArrayList;

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

public class MainItemRecyclerAdapter extends RecyclerView.Adapter<MainItemRecyclerAdapter.MyViewHolder>{
    Context context;
    LayoutInflater inflater;
    ArrayList<Item> data;
    Item_Location item_location;
    String sale_priceStr;
    String cost_priceStr;
    String decimal_check, qty_decimal_check;
    ListView listView;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    Sys_Tax_Group sys_tax_group;
    Item_Group item_group;
    ArrayList<ReceipeModifier> receipemodifierlist;
    SQLiteDatabase database;
    Database db;
    MediaPlayer mp;

    //Customer display variables
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    Settings settings;
    String  SRNO;
    String item_group_code;
    Contact contact;
    int count = 0;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    String path2 = Environment.getExternalStorageDirectory().getPath() + "/big.png";
    String path3 = Environment.getExternalStorageDirectory().getPath()
            + "/qrcode.png";
    Activity activity;
    private final static int FADE_DURATION = 300;
    Double curQty = 0d;
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


    // ???????????????????????????
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
            Globals.AppLogWrite("Connection  dsPacket" + dsPacket);
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


    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Globals.AppLogWrite("Woyoservice disconnection");
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
            Globals.AppLogWrite("Woyoservice connection");
        }
    };
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item_name, txt_item_code, txt_price;

        public MyViewHolder(View view) {
            super(view);
            txt_item_name = (TextView) view.findViewById(R.id.txt_item_name);
            txt_item_code = (TextView) view.findViewById(R.id.txt_item_code);
            txt_price = (TextView) view.findViewById(R.id.txt_price);
            settings = Settings.getSettings(context, database, "");
        }

    }

    public MainItemRecyclerAdapter(Context context,ArrayList<Item> list) {
        this.context = context;
        data = list;

        try {
            db = new Database(context);
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_item, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Item resultp = data.get(position);
        // mp = MediaPlayer.create(context, R.raw.beep1);


        if (settings.get_Is_Customer_Display().equals("true")) {
            mDSKernel = DSKernel.newInstance();
            mDSKernel.checkConnection();
            Globals.AppLogWrite(mDSKernel.toString());
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
            // decimal_check = Globals.objLPD.getDecimal_Place();
            decimal_check = "2";
        } catch (Exception ex) {
            decimal_check = "1";
        }

        try {
            holder.txt_item_name.setText(resultp.get_item_name());
            holder.txt_item_code.setText(resultp.get_item_code());
            String sale_price = "0";
            try {
                item_location = Item_Location.getItem_Location(context, "where item_code='" + resultp.get_item_code() + "'", database);

                //  item_location = Item_Location.getItem_Location(context, " WHERE item_code = '" + resultp.get_item_code() + "'", database);
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
            } catch (Exception ex) {
                sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);
            }
            holder.txt_price.setText(sale_price);
            // progressBar.setVisibility(View.GONE);
        } catch (Exception ex) {
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //  progressBar.setVisibility(View.VISIBLE);

                contact = Contact.getContact(context, database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                if (settings.get_Is_Stock_Manager().equals("false")) {
                    try {

                        //mp.start();
                        //  data = Item.getAllItem(activity, "WHERE  is_active = '1'  and is_modifier ='1'", database);
                        Item resultp = data.get(position);


                        //Double TotalSlesPrice_CustomerDisplay;
                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_group_code + "'", database, db);


                        String item_code = resultp.get_item_code();
                        item_group = Item_Group.getItem_Group(context, database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                        item_location = Item_Location.getItem_Location(context, "Where item_code = '" + item_code + "'", database);
                        final ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        Double spricewdtax=0d;
                        boolean bFound = false;
                        while (count < myCart.size()) {
//                            setAnimation(itemView, i);
                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()))) + "");
                                if(resultp.get_is_inclusive_tax().equals("0")) {
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price())));
                                }
                                else{
                                    spricewdtax= Double.parseDouble(sale_priceStr)- Double.parseDouble(myCart.get(count).get_Tax_Price());
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * spricewdtax) + Double.parseDouble(myCart.get(count).get_Tax_Price());

                                }

                                //Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                Globals.TotalQty = Globals.TotalQty + 1;
                                Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                                String salePrice;
                                salePrice = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Sales_Price()), decimal_check);
                                if (settings.get_Is_Customer_Display().equals("true")) {
                                    Globals.AppLogWrite("Customer display true on ItemClick");
                                    if (settings.get_CustomerDisplay().equals("1")) {
                                        Globals.AppLogWrite("Customer display true on 1");

                                        try {
                                            Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                            Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                            jsonObject = new JSONObject();
                                            jsonObject.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                            jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                            Globals.AppLogWrite("json result" + jsonObject.toString());
                                            mDSKernel.sendData(dsPacket);
                                        } catch (Exception ex) {
                                            Toast.makeText(context, "Stock this Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();

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
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), json.toString(), callback1);
                                            Globals.AppLogWrite(" Else json result" + json.toString());
                                            mDSKernel.sendData(dsPacket);
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
                                            Toast.makeText(context, " Stock Else this Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                            //  calculateTax();
                            //   ArrayList<String> taxIdFinalAarry = calculateTax();
                            Double iTax = 0d;
                            Double iTaxTl = 0d;
                            Double iTaxTotal = 0d;
                            Double sprice=0d;
                            double beforeTaxPrice =  0;
                            //      if (taxIdFinalAarry.size() > 0) {

                            if (Globals.objLPR.getCountry_Id().equals("114")) {
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {

                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                            if (Globals.Taxwith_state.equals("1")  || Globals.Taxwith_state.equals("")) {
                                                iTax = 0d;
                                                Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {
                                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                    } else {
                                                        iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                    }

                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    iTaxTotal += iTax;
                                                    Globals.Taxwith_state = "1";
                                                }
                                            }
                                            else{
                                                Toast.makeText(context,"You cannot add item. your contact have different Tax Group",Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        if (resultp.get_is_inclusive_tax().equals("1")) {

                                            spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                            sprice = spricewdtax + iTaxTotal;

                                        } else {
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }
                                        //sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;



                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr);
                                        Globals.NoTax="0";
                                    }
                                } else if (contact == null) {
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        iTax = 0d;
                                        Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if (tax_master != null) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }

                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));

                                            Globals.Taxwith_state = "1";
                                        }
                                    }
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }

                            }
                            else if (Globals.objLPR.getCountry_Id().equals("221")) {
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        iTax = 0d;
                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                        for (int i = 0; i < item_group_taxArrayList.size(); i++) {




                                            Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if(tax_master!=null) {

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }

                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                                iTaxTotal += iTax;
                                                Globals.Taxwith_state = "1";
                                            }
                                        }

                                        if (resultp.get_is_inclusive_tax().equals("1")) {

                                            spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                            sprice = spricewdtax + iTaxTotal;

                                        } else {
                                            sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                        }



                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr);
                                        Globals.NoTax="0";
                                    }
                                } else if (contact == null) {

                                    iTax = 0d;
                                    Tax_Master tax_master=null;
                                    Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                        tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                        if(tax_master!=null) {

                                            if (tax_master.get_tax_type().equals("P")) {
                                                iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()));
                                            } else {
                                                iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                            }


                                        }
                                    }

                                    if (tax_master.get_tax_type().equals("P")) {
                                        iTaxTl = (iPrice * iTax) / (100 + iTax);
                                    }
                                    else{
                                        iTaxTl=iTax;
                                    }
                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(position).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                    Globals.order_item_tax.add(order_item_tax);
                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                    Globals.Taxwith_state = "1";
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }

                            }
                            else if(Globals.objLPR.getCountry_Id().equals("99")) {
                                if (contact != null) {
                                    if (contact.getIs_taxable().equals("1")) {
                                        if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                            Tax_Master tax_master = null;
                                            iTax = 0d;
                                            double   iPTax = 0;
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {



                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                    tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {


                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iPTax = iPTax + ( Double.parseDouble(tax_master.get_rate())/100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }



                                                    }
                                                }


                                            }
                                            if(iPTax > 0 )
                                            {

                                                beforeTaxPrice = ( (iPrice ) -  (iTax ))/(iPTax+ 1);

                                            }
                                            else
                                            {
                                                beforeTaxPrice = ( (iPrice ) -  (iTax ));
                                            }




                                            // Now you get item price before tax
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                                    tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                        } else {
                                                            iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                        }


                                                    }
                                                }

                                            }

                                            Globals.Taxwith_state = "1";
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                            Tax_Master tax_master=null;
                                            iTax = 0d;
                                            double   iPTax = 0;
                                            Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                    tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iPTax = iPTax + ( Double.parseDouble(tax_master.get_rate())/100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }

                                                    }
                                                }

                                            }

                                            if(iPTax > 0 )
                                            {

                                                beforeTaxPrice = ( (iPrice ) -  (iTax ))/(iPTax+ 1);

                                            }
                                            else
                                            {
                                                beforeTaxPrice = ( (iPrice ) -  (iTax ));
                                            }



                                            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                                sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                    tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                        } else {
                                                            iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                            Globals.order_item_tax.add(order_item_tax);
                                                            iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                        }


                                                    }
                                                }

                                            }

                                            Globals.Taxdifferent_state = "2";
                                            if (resultp.get_is_inclusive_tax().equals("1")) {

                                                spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                                sprice = spricewdtax + iTaxTotal;

                                            } else {
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        }


                                    } else {
                                        Globals.NoTax="0";
                                        sprice = Double.parseDouble(sale_priceStr);
                                    }
                                } else if (contact == null) {
                                    Double iPce = Double.parseDouble(item_location.get_selling_price());
                                    Tax_Master tax_master=null;
                                    iTax = 0d;
                                    double   iPTax = 0;

                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                            tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iPTax = iPTax + ( Double.parseDouble(tax_master.get_rate())/100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }


                                            }
                                        }

                                    }

                                    if(resultp.get_is_inclusive_tax().equals("1")) {
                                        if (iPTax > 0) {

                                            beforeTaxPrice = ((iPce) - (iTax)) / (iPTax + 1);

                                        } else {
                                            beforeTaxPrice = ((iPce) - (iTax));
                                        }
                                    }
                                    else{
                                        beforeTaxPrice=Double.parseDouble(sale_priceStr);
                                    }



                                    // Now you get item price before tax
                                    for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                        sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(context, "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                        if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                            tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    iTaxTotal = iTaxTotal + iTaxTl;

                                                } else {
                                                    iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                    Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                    Globals.order_item_tax.add(order_item_tax);
                                                    iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                }


                                            }
                                        }

                                    }







                                    Globals.Taxwith_state="1";
                                    if (resultp.get_is_inclusive_tax().equals("1")) {

                                        spricewdtax = Double.parseDouble(sale_priceStr) - iTaxTotal;
                                        sprice = spricewdtax + iTaxTotal;

                                    } else {
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                }
                            }
                            sale_priceStr=String.valueOf(sprice);
                            Globals.TotalSlesPrice_CustomerDisplay = Double.parseDouble(sprice + "");
                            ShoppingCart cartItem = new ShoppingCart(context, Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + "","0","0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforeTaxPrice+"");
                            Globals.cart.add(cartItem);
                            receipemodifierlist= ReceipeModifier.getAllReceipeModifier(context,"Where item_code = '"+resultp.get_item_code()+"'",database);

                            if(receipemodifierlist.size()>0) {

                                Globals.SRNO = Globals.SRNO ;
                            }
                            else{
                                Globals.SRNO = Globals.SRNO + 1;
                            }
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));

                            //Globals.TotalSlesPrice_CustomerDispla0y= Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                            Globals.TotalItem = Globals.TotalItem + 1;
                            Globals.TotalQty = Globals.TotalQty + 1;
                            final String salePrice;
                            salePrice = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), decimal_check);
                            Globals.CMDItemPrice = Double.parseDouble(salePrice);
                            Globals.CMDItemName = resultp.get_item_name();
                            if (settings.get_Is_Customer_Display().equals("true")) {
                                Globals.AppLogWrite("Customer display true  bfound flag");
                                if (settings.get_CustomerDisplay().equals("1")) {
                                    Globals.AppLogWrite("Customer display true on 1 bfound flag");
                                    try {
                                        Globals.CMDItemPrice = Double.parseDouble(salePrice);
                                        Globals.CMDItemName = myCart.get(count).get_Item_Name();
                                        jsonObject = new JSONObject();
                                        jsonObject.put("title", resultp.get_item_name() + " " + salePrice);
                                        jsonObject.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                        dsPacket = UPacketFactory.buildShowText(
                                                DSKernel.getDSDPackageName(), jsonObject.toString(), callback1);
                                        Globals.AppLogWrite("json result 1" + jsonObject.toString());
                                        mDSKernel.sendData(dsPacket);
                                    } catch (Exception ex) {
                                        Toast.makeText(context, " bFOUND this Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                    try {
                                        woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                    } catch (Exception ex) {
                                    }
                                } else {
                                    try {
                                        Double doubletotalprice;
                                        JSONObject json = new JSONObject();
                                        //Double TotalItemPrice = (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()));

                                        String totalprice = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Line_Total()), decimal_check);
                                        String totalquantity = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Quantity()), decimal_check);

                                        doubletotalprice = (Double.parseDouble(totalprice) / Double.parseDouble(totalquantity));

                                        String title_value = resultp.get_item_name();
                                        json.put("title", title_value + "   " + Globals.myNumberFormat2Price(doubletotalprice, decimal_check));
                                        json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                        String titleContentJsonStr = json.toString();
                                        dsPacket = UPacketFactory.buildShowText(
                                                DSKernel.getDSDPackageName(), json.toString(), callback1);
                                        Globals.AppLogWrite("else json result 1" + json.toString());
                                        mDSKernel.sendData(dsPacket);
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
                                        Toast.makeText(context, " bFOUND Else this Exception" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                        // SRNO= myCart.get(count).get_SRNO();
                        notifyDataSetChanged();
                        // Collections.reverse(myCart);
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        ((MainActivity) context).setTextView(item_price, Globals.TotalQty + "");

                    } catch (Exception ex) {
                    }
                }

                String itemcode=resultp.get_item_code();

                receipemodifierlist= ReceipeModifier.getAllReceipeModifier(context,"Where item_code = '"+itemcode+"'",database);
                if(receipemodifierlist.size()>0){
                    Intent i= new Intent(context, ItemModifierSelection.class);
                    i.putExtra("itemcode",resultp.get_item_code());
                    i.putExtra("opr", Globals.Operation);
                    i.putExtra("srno",Globals.SRNO);
                    i.putExtra("odr_code", Globals.Order_Code);
                    context.startActivity(i);
                    // return;
                }
            }
        });


        // final Item resultp = data.get(i);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
                        intent.putExtra("arr_position", position + "");
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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void showQRCode(long fileId) {

        try {
            String json = UPacketFactory.createJson(sunmi.ds.data.DataModel.QRCODE, "");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
