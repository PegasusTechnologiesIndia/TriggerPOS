package org.phomellolitepos.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.pdf.codec.Base64;

import org.json.JSONObject;
import org.phomellolitepos.Adapter.MainItemListAdapter2;
import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.ItemModifierSelection;
import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
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
/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ItemFragment2 extends Fragment {
    ArrayList<Item> arrayList;
    View v;
    RecyclerView category_list;
    MainItemListAdapter2 mainItemListAdapter2;
    //    ListView category_list;
    Activity activity;
    Button btn_Item_Price, btn_Qty;
    private String itemGrpCode, strFilter, operation;
    ProgressBar progressBar;
    Database db;
    SQLiteDatabase database;
    Settings settings;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item> data;
    Item_Location item_location;
    String sale_priceStr;
    String cost_priceStr;
    String decimal_check, qty_decimal_check;
    RecyclerView recyclerView;
    ListView listView;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    //Customer display variables
    DSKernel mDSKernel;
    DataPacket dsPacket;
    JSONObject jsonObject;
    String item_group_code;
    MediaPlayer mp;
    int count = 0;
    Sys_Tax_Group sys_tax_group;
    Contact contact;
    ArrayList<ReceipeModifier> receipemodifierlist;
    String path1 = Environment.getExternalStorageDirectory().getPath() + "/small.png";
    String path2 = Environment.getExternalStorageDirectory().getPath() + "/big.png";
    String path3 = Environment.getExternalStorageDirectory().getPath()
            + "/qrcode.png";
    private final static int FADE_DURATION = 300;
    Double curQty = 0d;
    IWoyouService woyouService;
    Item_Group item_group;
    String strKitchenFlag="";
    private ISendFilesCallback callback = new ISendFilesCallback() {


        @Override
        public void onAllSendSuccess(long fileid) {
            //showQRCode(fileid);
        }

        @Override
        public void onSendSuccess(String path, long taskId) {
            //showQRCode(taskId);
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
    public ItemFragment2() {


    }

    public ItemFragment2(Activity activity) {
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.item_fragment2, container, false);
        setRetainInstance(true);
        Bundle bundle = getArguments();
        itemGrpCode = bundle.getString("itemGrpCode");
        Globals.item_category_code = itemGrpCode;
        strFilter = bundle.getString("filter");
        operation = bundle.getString("operation");
        category_list = (RecyclerView) v.findViewById(R.id.main_list2);
         mp = MediaPlayer.create(getActivity(), R.raw.beep1);
try {
    init_View();
}
catch(Exception e){

}
        return v;
    }
    public void init_View() {
        btn_Item_Price = (Button) v.findViewById(R.id.btn_Item_Price);
        btn_Qty = (Button) v.findViewById(R.id.btn_Qty);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        try {
            db = new Database(activity);
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }
        settings = Settings.getSettings(getActivity(),database,"");

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        if (settings.get_Is_Customer_Display().equals("true")) {
            mDSKernel = DSKernel.newInstance();
            mDSKernel.checkConnection();
            mDSKernel.init(activity, mConnCallback);
            mDSKernel.addReceiveCallback(mReceiveCallback);
        }

        Intent intent1 = new Intent();
        intent1.setPackage("woyou.aidlservice.jiuiv5");
        intent1.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        activity.startService(intent1);
        activity.bindService(intent1, connService, activity.BIND_AUTO_CREATE);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        if (operation.equals("search")) {
            if (settings.get_Is_Zero_Stock().equals("true")){
                arrayList = Item.getAllItem(activity, "WHERE  is_active = '1' and is_modifier!='1' "  + strFilter + "  Order By lower(item_name) asc limit 100", database);
            }else {
                arrayList = Item.getAllItem(activity, " left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1'  " + strFilter + "  and item_location.quantity!='0'  Order By lower(item_name) asc limit 100", database);
            }



            if (arrayList.size() > 0) {
                mainItemListAdapter2 = new MainItemListAdapter2(activity, arrayList);
                category_list.setHasFixedSize(true);
                category_list.setVisibility(View.VISIBLE);
//                category_list.setItemViewCacheSize(20);
//                category_list.setDrawingCacheEnabled(true);
//                category_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//              RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                category_list.setLayoutManager(new GridLayoutManager(activity, 3));
//                category_list.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
//                category_list.setItemAnimator(new DefaultItemAnimator());
                category_list.setAdapter(mainItemListAdapter2);
                mainItemListAdapter2.notifyDataSetChanged();
            }else {
                progressBar.setVisibility(View.GONE);
            }

        } else {
            if (settings.get_Is_Zero_Stock().equals("true")){
                arrayList = Item.getAllItem(activity, "WHERE  is_active = '1' and is_modifier!='1'  and item_group_code='" + itemGrpCode + "' Order By lower(item_name) asc limit 100", database);
            }else {
                arrayList = Item.getAllItem(activity, "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1'  and  item.item_group_code='" + itemGrpCode + "' and item_location.quantity!='0' Order By lower(item_name) asc limit 100", database);
            }
            if (arrayList.size() > 0) {
                mainItemListAdapter2 = new MainItemListAdapter2(activity, arrayList);
                category_list.setHasFixedSize(true);
                category_list.setVisibility(View.VISIBLE);
//                category_list.setItemViewCacheSize(20);
//                category_list.setDrawingCacheEnabled(true);
//                category_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                category_list.setLayoutManager(new GridLayoutManager(activity, 3));
//                category_list.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
//                category_list.setItemAnimator(new DefaultItemAnimator());
                category_list.setAdapter(mainItemListAdapter2);
                mainItemListAdapter2.notifyDataSetChanged();
            }else {
                progressBar.setVisibility(View.GONE);
            }
        }


        category_list.addOnItemTouchListener(new RecyclerTouchListener(activity, category_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view,int i) {
                mp.start();
                Item resultp = arrayList.get(i);
                contact = Contact.getContact(getActivity(), database, db, " where is_active ='1' and contact_code='" + Globals.strContact_Code + "'");

                if (settings.get_Is_Stock_Manager().equals("false")) {
                    try {

                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(activity, "Where item_group_code = '" + item_group_code + "'", database, db);
                        item_group = Item_Group.getItem_Group(activity, database, db, "WHERE item_group_code ='" + resultp.get_item_group_code() + "'");

                        String item_code = resultp.get_item_code();
                        item_location = Item_Location.getItem_Location(activity, "Where item_code = '" + item_code + "'", database);
                        final ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {

                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
//                          myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
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
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), json.toString(), callback1);
                                            Globals.AppLogWrite(" Else json result"+ json.toString());
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
                                            progressBar.setVisibility(View.GONE);

                                        }

                                        try {
                                            woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                        } catch (Exception ex) {
//                                        progressBar.setVisibility(View.GONE);
                                        }
                                        ((Main2Activity) activity).callHandler();
                                    }
                                }

                            }
                            count = count + 1;
                        }
                        if (!bFound) {

//                            setAnimation(itemView, i);
                            if (item_location == null) {
                                sale_priceStr = "0";
                                cost_priceStr = "0";
                            } else {
                                sale_priceStr = item_location.get_selling_price();
                                cost_priceStr = item_location.get_cost_price();
                            }
                            Double spricewdtax=0d;
                            //calculateTax();
                           // ArrayList<String> taxIdFinalAarry = item_group_taxArrayList;
                            Double iTax = 0d;
                            Double iTaxTotal = 0d;
                            Double sprice=0d;
                            Double iTaxTl = 0d;
                            double beforeTaxPrice =  0;
                            if (item_group_taxArrayList.size() > 0) {
                                if (Globals.objLPR.getCountry_Id().equals("99")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                                Tax_Master tax_master = null;
                                                iTax = 0d;
                                                double   iPTax = 0;
                                                for (int j = 0; j < item_group_taxArrayList.size(); j++) {



                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(j).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                                        tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(j).get_tax_id() + "'", database, db);
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
                                                for (int k = 0; k < item_group_taxArrayList.size(); k++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(k).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                                        tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(k).get_tax_id() + "'", database, db);
                                                        if (tax_master != null) {

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(k).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                                Globals.order_item_tax.add(order_item_tax);
                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                            } else {
                                                                iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(k).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
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
                                                for (int l = 0; l < item_group_taxArrayList.size(); l++) {


                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(l).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                        tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(l).get_tax_id() + "'", database, db);
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



                                                for (int m = 0; m< item_group_taxArrayList.size(); m++) {
                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(m).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("3")) {

                                                        tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(m).get_tax_id() + "'", database, db);
                                                        if (tax_master != null) {

                                                            if (tax_master.get_tax_type().equals("P")) {
                                                                iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(m).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                                Globals.order_item_tax.add(order_item_tax);
                                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                            } else {
                                                                iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(m).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
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

                                        for (int it = 0; it < item_group_taxArrayList.size(); it++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(it).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                                tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(it).get_tax_id() + "'", database, db);
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

                                            beforeTaxPrice = ( (iPce ) -  (iTax ))/(iPTax+ 1);

                                        }
                                        else
                                        {
                                            beforeTaxPrice = ( (iPce ) -  (iTax ));
                                        }




                                        // Now you get item price before tax
                                        for (int c = 0; c < item_group_taxArrayList.size(); c++) {
                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(c).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {


                                                tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(c).get_tax_id() + "'", database, db);
                                                if (tax_master != null) {

                                                    if (tax_master.get_tax_type().equals("P")) {
                                                        iTaxTl = beforeTaxPrice *  ( Double.parseDouble(tax_master.get_rate())/100);

                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(c).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTaxTl, decimal_check));

                                                    } else {
                                                        iTaxTl =  Double.parseDouble(tax_master.get_rate());

                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(c).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTaxTl, decimal_check) + "");
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
                                else if (Globals.objLPR.getCountry_Id().equals("114")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    iTax = 0d;
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);

                                                    }
                                                    Globals.Taxwith_state = "1";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                                    iTax = 0d;
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }

                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);

                                                    }
                                                    Globals.Taxdifferent_state = "2";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            Globals.NoTax="0";
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                            iTax = 0d;
                                                Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                            }
                                            Globals.Taxdifferent_state = "1";
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;

                                    }
                                }
                                else if (Globals.objLPR.getCountry_Id().equals("221")) {
                                    if (contact != null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                                    iTax = 0d;
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                    }
                                                    Globals.Taxwith_state = "1";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                                    iTax = 0d;
                                                    Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                    if (tax_master != null) {
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }

                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                    }
                                                    Globals.Taxdifferent_state = "2";
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        } else {
                                            Globals.NoTax = "0";
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                    } else if (contact == null) {
                                        for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                            iTax = 0d;
                                            Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                            if (tax_master != null) {
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                            }
                                            Globals.Taxwith_state = "1";
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;

                                    }
                                }

                            }
                            else{
                                sprice = Double.parseDouble(sale_priceStr);
                            }

                        /*    Order_Detail order_detail = Order_Detail.getOrder_Detail(getActivity(), " WHERE order_code='" + strO + "'", database);
                            if(order_detail!=null){
                                strKitchenFlag= order_detail.getIs_KitchenPrintFlag();
                            }*/
                            ShoppingCart cartItem = new ShoppingCart(activity, Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "","0","0",item_group.getCategoryIp(),"false",resultp.get_unit_id(),beforeTaxPrice+"");
                            Globals.cart.add(cartItem);
                            receipemodifierlist= ReceipeModifier.getAllReceipeModifier(getActivity(),"Where item_code = '"+resultp.get_item_code()+"'",database);

                            if(receipemodifierlist.size()>0) {

                                Globals.SRNO = Globals.SRNO ;
                            }
                            else{
                                Globals.SRNO = Globals.SRNO + 1;
                            }
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
//                                        progressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    try {
                                        JSONObject json = new JSONObject();
                                        json.put("title", resultp.get_item_name() + " " + salePrice);
                                        json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                        String titleContentJsonStr = json.toString();
                                        dsPacket = UPacketFactory.buildShowText(
                                                DSKernel.getDSDPackageName(), json.toString(), callback1);
                                        Globals.AppLogWrite("else json result 1"+ json.toString());
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
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    try {
                                        woyouService.sendLCDDoubleString("Total", Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check), null);
                                    } catch (Exception ex) {
                                    }
                                    ((Main2Activity) activity).callHandler();
                                }

                            }
                        }
                        Globals.cart = myCart;
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);

                        ((Main2Activity) activity).setTextView(item_price, Globals.TotalQty + "");
                    } catch (Exception ex) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
                //else

                    /*{
                    try {
                       // Item resultp = data.get(i);
                        item_group_code = resultp.get_item_code();
                        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(activity, "Where item_group_code = '" + item_group_code + "'", database, db);
                        String item_code = resultp.get_item_code();
                        item_location = Item_Location.getItem_Location(activity, "Where item_code = '" + item_code + "'", database);
                        final ArrayList<ShoppingCart> myCart = Globals.cart;
                        int count = 0;
                        boolean bFound = false;
                        while (count < myCart.size()) {
//                                animSideDown = AnimationUtils.loadAnimation(activity,
//                                        R.anim.slide_down);
//                                txt_item_name.setAnimation(animSideDown);
//                                txt_item_code.setAnimation(animSideDown);
//                                txt_price.setAnimation(animSideDown);
//                            setAnimation(itemView, i);

                            if (resultp.get_item_code().equals(myCart.get(count).get_Item_Code())) {
                                bFound = true;
                                curQty = Double.parseDouble(myCart.get(count).get_Quantity());
                                boolean result = stock_check(item_code, curQty + 1);
                                if (result == true) {
                                    myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                                    myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
//                          myCart.get(count).set_Tax_Price(((Double.parseDouble(myCart.get(count).get_Quantity())) * (Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                                    Globals.TotalQty = Globals.TotalQty + 1;
//                            curQty = curQty+1;
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
//                                        progressBar.setVisibility(View.GONE);
                                            }
                                        } else {
                                            try {
                                                JSONObject json = new JSONObject();
                                                json.put("title", myCart.get(count).get_Item_Name() + " " + salePrice);
                                                json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                                String titleContentJsonStr = json.toString();
                                                dsPacket = UPacketFactory.buildShowText(
                                                        DSKernel.getDSDPackageName(), json.toString(), callback1);

                                                mDSKernel.sendData(dsPacket);
                                                Globals.AppLogWrite("Item onclick Json"+ json.toString());
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
                                            ((Main2Activity) activity).callHandler();
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

//                                setAnimation(itemView, i);
                                if (item_location == null) {
                                    sale_priceStr = "0";
                                    cost_priceStr = "0";
                                } else {
                                    sale_priceStr = item_location.get_selling_price();
                                    cost_priceStr = item_location.get_cost_price();
                                }
                              //  calculateTax();
                                //ArrayList<String> taxIdFinalAarry = calculateTax();
                                Double iTax = 0d;
                                Double iTaxTotal = 0d;
                                Double sprice=0d;
                                if (item_group_taxArrayList.size() > 0) {
                                    if(contact!=null) {
                                        if (contact.getIs_taxable().equals("1")) {
                                            if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {

                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                        iTax = 0d;
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }
                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                    }
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;


                                            }
                                            else if(!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())){
                                                for (i = 0; i < item_group_taxArrayList.size(); i++) {


                                                    sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                                    if(sys_tax_group.get_tax_master_id().equals("3")) {
                                                        iTax = 0d;
                                                        Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                        Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                                        if (tax_master.get_tax_type().equals("P")) {
                                                            iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                        } else {
                                                            iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                        }

                                                        iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                        Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                        Globals.order_item_tax.add(order_item_tax);
                                                    }
                                                }
                                                sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                            }
                                        }
                                        else{
                                            sprice = Double.parseDouble(sale_priceStr);
                                        }
                                        }
                                    else if(contact==null){
                                        for (i = 0; i < item_group_taxArrayList.size(); i++) {


                                            sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                            if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                                iTax = 0d;
                                                Tax_Master tax_master = Tax_Master.getTax_Master(activity, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                                Double iPrice = Double.parseDouble(item_location.get_selling_price());

                                                if (tax_master.get_tax_type().equals("P")) {
                                                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                                } else {
                                                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                                }
                                                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                                Order_Item_Tax order_item_tax = new Order_Item_Tax(activity, "", "", Globals.SRNO + "", resultp.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                                Globals.order_item_tax.add(order_item_tax);
                                            }
                                        }
                                        sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    }
                                    }

                         *//*       Order_Detail order_detail = Order_Detail.getOrder_Detail(getActivity(), " WHERE order_code='" + strOrderCode + "'", database);
                                if(order_detail!=null){
                                    strKitchenFlag= order_detail.getIs_KitchenPrintFlag();
                                }*//*
                                ShoppingCart cartItem = new ShoppingCart(activity, Globals.SRNO + "", resultp.get_item_code(), resultp.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "","0","0",item_group.getCategoryIp(),"false",resultp.get_unit_id());
                                Globals.cart.add(cartItem);
                                Globals.SRNO = Globals.SRNO + 1;
                                Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                Globals.TotalItem = Globals.TotalItem + 1;
                                Globals.TotalQty = Globals.TotalQty + 1;
//                            curQty = curQty+1;
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
//                                        progressBar.setVisibility(View.GONE);
                                        }
                                    } else {
                                        try {
                                            JSONObject json = new JSONObject();
                                            json.put("title", resultp.get_item_name() + " " + salePrice);
                                            json.put("content", "Total : " + Globals.myNumberFormat2Price(Globals.TotalItemPrice, decimal_check));
                                            String titleContentJsonStr = json.toString();
                                            dsPacket = UPacketFactory.buildShowText(
                                                    DSKernel.getDSDPackageName(), json.toString(), callback1);

                                            mDSKernel.sendData(dsPacket);
                                            Globals.AppLogWrite("Item onclick Json"+ json.toString());
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
                                        ((Main2Activity) activity).callHandler();
                                    }

                                }

                            }
                        }
                        Globals.cart = myCart;
                        String item_price;
                        item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                        ((Main2Activity) activity).setTextView(item_price, Globals.TotalQty + "");
                    } catch (Exception ex) {
                        progressBar.setVisibility(View.GONE);
                        String ab = ex.getMessage();
                        ab = ab;
                    }

                }*/
                String itemcode=resultp.get_item_code();
                receipemodifierlist= ReceipeModifier.getAllReceipeModifier(getActivity(),"Where item_code = '"+itemcode+"'",database);
                if(receipemodifierlist.size()>0){
                    Intent t= new Intent(getActivity(), ItemModifierSelection.class);
                    t.putExtra("itemcode",resultp.get_item_code());
                    t.putExtra("opr", Globals.Operation);
                    t.putExtra("srno",Globals.SRNO);
                    t.putExtra("odr_code", Globals.Order_Code);
                    getActivity().startActivity(t);
                    // return;
                }
            }

            @Override
            public void onLongClick(View view, final int position) {
                String flag = "Main";
                String item_code = arrayList.get(position).get_item_code();
                Item_Location item_location = Item_Location.getItem_Location(activity, " where item_code='" + item_code + "'", database);
                if (item_location == null) {
                    Toast.makeText(activity, "Item not found in this location", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(activity, ChangePriceActivity.class);
                    intent.putExtra("arr_position", position + "");
                    intent.putExtra("item_code", item_code);
                    intent.putExtra("opr", Globals.Operation);
                    intent.putExtra("odr_code", Globals.Order_Code);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                }

//                    new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                sleep(3*1000);
//                                String flag = "Main";
//                                String item_code = arrayList.get(position).get_item_code();
//                                Item_Location item_location = Item_Location.getItem_Location(activity, " where item_code='" + item_code + "'", database);
//                                if (item_location == null) {
//                                    Toast.makeText(activity, "Item not found in this location", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Intent intent = new Intent(activity, ChangePriceActivity.class);
//                                    intent.putExtra("arr_position", position + "");
//                                    intent.putExtra("item_code", item_code);
//                                    intent.putExtra("opr", Globals.Operation);
//                                    intent.putExtra("odr_code", Globals.Order_Code);
//                                    intent.putExtra("flag", flag);
//                                    startActivity(intent);
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();

            }
        }));
    }

    private ArrayList<String> calculateTax() {
        ArrayList<Tax_Detail> taxIdAarry = new ArrayList<Tax_Detail>();
        ArrayList<String> taxIdFinalAarry = new ArrayList<String>();
        ArrayList<Item_Group_Tax> item_group_taxList = new ArrayList<Item_Group_Tax>();
        try {
            if (Globals.strContact_Code.equals("") || Globals.strContact_Code.equals("0")) {
                Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(activity, database, db, "where type='Interstate'");
                taxIdAarry = Tax_Detail.getAllTax_Detail(activity, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                if (taxIdAarry.size() > 0) {
                    for (int i = 0; i < taxIdAarry.size(); i++) {
                        Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(activity, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                } else {
                    item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(activity, "Where item_group_code = '" + item_group_code + "'", database, db);
                    for (int i = 0; i < item_group_taxList.size(); i++) {
                        Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                        taxIdFinalAarry.add(item_group_tax.get_tax_id());
                    }
                }

            } else {
                Contact contact = Contact.getContact(activity, database, db, "WHERE contact_code='" + Globals.strContact_Code + "'");
                Lite_POS_Registration lite_pos_registration = Lite_POS_Registration.getRegistration(activity, database, db, "");
                if (contact.get_zone_id().equals(lite_pos_registration.getZone_Id())) {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(activity, database, db, "where type='Interstate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(activity, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(activity, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(activity, "Where item_group_code = '" + item_group_code + "'", database, db);
                        for (int i = 0; i < item_group_taxList.size(); i++) {
                            Item_Group_Tax item_group_tax = item_group_taxList.get(i);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    }
                } else {
                    Sys_Tax_Type sys_tax_type = Sys_Tax_Type.getSys_Tax_Type(activity, database, db, "where type='Intrastate'");
                    taxIdAarry = Tax_Detail.getAllTax_Detail(activity, " where tax_type_id='" + sys_tax_type.get_id() + "'", database);
                    if (taxIdAarry.size() > 0) {
                        for (int i = 0; i < taxIdAarry.size(); i++) {
                            Item_Group_Tax item_group_tax = Item_Group_Tax.getItem_Group_Tax(activity, " WHERE tax_id = '" + taxIdAarry.get(i).get_tax_id() + "' and item_group_code = '" + item_group_code + "'", database, db);
                            taxIdFinalAarry.add(item_group_tax.get_tax_id());
                        }
                    } else {
                        item_group_taxList = Item_Group_Tax.getAllItem_Group_Tax(activity, "Where item_group_code = '" + item_group_code + "'", database, db);
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

    private Boolean stock_check(String item_code, Double Qty) {
        boolean flag = false;
        try {
            Item_Location item_location = Item_Location.getItem_Location(activity, " where item_code='" + item_code + "'", database);
            Double avl_stock = Double.parseDouble(item_location.get_quantity());
            if (avl_stock >= Qty) {
                flag = true;
            } else {
                Toast.makeText(activity, "Available Stock : " + avl_stock + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(activity, "Item not found in this location", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    private void showQRCode(long fileId) {

        try {
            String json = UPacketFactory.createJson(sunmi.ds.data.DataModel.QRCODE, "");
            mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
//        showing an image by sending this command
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DECODE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
