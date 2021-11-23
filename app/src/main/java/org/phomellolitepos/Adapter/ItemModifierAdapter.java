package org.phomellolitepos.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
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
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.ModiefierData;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.TempShoppingCart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.ds.DSKernel;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.UPacketFactory;

public class ItemModifierAdapter  extends RecyclerView.Adapter<ItemModifierAdapter.MyViewHolder>{
    private ArrayList<ModiefierData> modiefierDataArrayList ;
    String  qty_decimal_check;
    Context context;
    Item itemarray;
    JSONObject jsonObject;
    Settings settings;
    String item_group_code;
    Item_Location item_location;
    Item item;
   Item_Group item_group;
    String sale_priceStr;
    SQLiteDatabase database;
    Database db;
    String cost_priceStr;
    String item_Code;
    String sale_price;
    // RecyclerView recyclerView;
    public ItemModifierAdapter(Context ctx, ArrayList<ModiefierData> listdata, String decimalcheck, SQLiteDatabase sqldatabase, Database database, String itemcode) {
        this.context=ctx;
        this.modiefierDataArrayList = listdata;
        this.qty_decimal_check=decimalcheck;
        this.item_Code=itemcode;


    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            db = new Database(context);
            database = db.getReadableDatabase();
        }
        catch(Exception e){

        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem= layoutInflater.inflate(R.layout.modifier_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ModiefierData myListData = modiefierDataArrayList.get(position);
        holder.text_modifiername.setText(myListData.getItem_name());


        try {
            item_location = Item_Location.getItem_Location(context, " WHERE item_code = '" + myListData.getModifiercode() + "'", database);
            String sellingprice=item_location.get_selling_price().toString();
            Double saleprice=Double.parseDouble(sellingprice);
            sale_price = Globals.myNumberFormat2Price(saleprice, qty_decimal_check);

            holder.textmodifiercode.setText(sale_price);
        } catch (Exception ex) {
          /*  sale_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_price), decimal_check);
            holder.textmodifiercode.setText(sale_price);*/
        }

     /*   holder.cardView.setCardBackgroundColor( ContextCompat.getColorStateList(
                context,R.drawable.selector_card_view_colors
        ));*/


    holder.btn_plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Double qty = Double.parseDouble(holder.edt_quantity.getText().toString().trim()) + 1;

               // btn_save.setEnabled(true);
                holder.edt_quantity.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
            }


    });

    holder.btn_minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Double qty = Double.parseDouble(holder.edt_quantity.getText().toString().trim()) - 1;

            if (qty <= 0) {
                holder.edt_quantity.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble("1"), qty_decimal_check));
            } else {
                holder.edt_quantity.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(qty + ""), qty_decimal_check));
            }
        }
    });

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        ModiefierData resultp = modiefierDataArrayList.get(position);

       String item_code = resultp.getModifiercode();

        holder.cardView.setCardBackgroundColor( ContextCompat.getColorStateList(
                context,R.drawable.selector_card_view_colors));
                //Double TotalSlesPrice_CustomerDisplay;

               item_location = Item_Location.getItem_Location(context, "Where item_code = '" + item_code + "'", database);
        item_group = Item_Group.getItem_Group(context, database, db, "WHERE item_group_code ='" + resultp.getItem_groupcode() + "'");

        item=Item.getItem(context, " WHERE item_code='" + item_code + "'", database, db);
               ArrayList<ShoppingCart> myCart = Globals.cart;
        ShoppingCart tempShoppingCart = null;
        ArrayList<ShoppingCart> tempshoppingArraylist;
                int count = 0;
                boolean bFound = false;
                while (count < myCart.size()) {
//                            setAnimation(itemView, i);
                    if (resultp.getModifiercode().equals(myCart.get(count).get_Item_Code())) {

                        if (resultp.getItemcode().equals(myCart.get(count).getMaster_itemcode())) {
                            bFound = true;
                            myCart.get(count).set_Quantity(((Double.parseDouble(myCart.get(count).get_Quantity())) + 1) + "");
                            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
                            Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * (Double.parseDouble(myCart.get(count).get_Sales_Price()) + Double.parseDouble(myCart.get(count).get_Tax_Price())));
                            Globals.TotalQty = Globals.TotalQty + 1;
                            Globals.TotalItemCost = Globals.TotalItemCost + (1 * Double.parseDouble(myCart.get(count).get_Cost_Price()));
                            String salePrice;
                            salePrice = Globals.myNumberFormat2Price(Double.parseDouble(myCart.get(count).get_Sales_Price()), qty_decimal_check);
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
                            iTaxTotal = iTaxTotal + iTax;
                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", resultp.getItemcode(), taxIdFinalAarry.get(i), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, qty_decimal_check) + "");
                            Globals.order_item_tax.add(order_item_tax);
                        }
                    }
                    Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                    Double beforetaxprice= sprice-iTaxTotal;
                    Globals.TotalSlesPrice_CustomerDisplay = Double.parseDouble(sprice + "");


                        ShoppingCart cartItem = new ShoppingCart(context, Globals.SRNO + "", resultp.getModifiercode(), resultp.getItem_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "", "1", item_Code,item_group.getCategoryIp(),"false",item.get_unit_id(),beforetaxprice+"");
                   tempshoppingArraylist=new ArrayList<>();
                        if(cartItem.getMaster_itemcode().equals("0")) {
                            Globals.cart.add(cartItem);
                        }
                        else{

                            for(int j=0;j<myCart.size();j++){

                                tempShoppingCart=new ShoppingCart(context,myCart.get(j).get_SRNO(),myCart.get(j).get_Item_Code(),myCart.get(j).get_Item_Name(),
                                        myCart.get(j).get_Quantity(),myCart.get(j).get_Cost_Price(),myCart.get(j).get_Sales_Price(),myCart.get(j).get_Tax_Price(),myCart.get(j).get_Discount()
                                ,myCart.get(j).get_Line_Total(),myCart.get(j).getIs_modifier(),myCart.get(j).getMaster_itemcode(),myCart.get(j).getCategoryIp(),myCart.get(j).getKitchenprintflag(),myCart.get(j).getUnitId(),myCart.get(j).getBeforeTaxPrice());
                               tempshoppingArraylist.add(tempShoppingCart);
                                if(myCart.get(j).get_Item_Code().equals(cartItem.getMaster_itemcode())){
                                    tempShoppingCart=new ShoppingCart(context,myCart.get(j).get_SRNO(),cartItem.get_Item_Code(),cartItem.get_Item_Name(),
                                            cartItem.get_Quantity(),cartItem.get_Cost_Price(),cartItem.get_Sales_Price(),cartItem.get_Tax_Price(),cartItem.get_Discount()
                                            ,cartItem.get_Line_Total(),cartItem.getIs_modifier(),cartItem.getMaster_itemcode(),cartItem.getCategoryIp(),cartItem.getKitchenprintflag(),cartItem.getUnitId(),cartItem.getBeforeTaxPrice());

                                    tempshoppingArraylist.add(tempShoppingCart);
                                }




                            }


                         //  tempshoppingArraylist.add(tempShoppingCart);

                        }


                    Globals.cart.clear();
                    myCart=tempshoppingArraylist;

                  //  Globals.cart=myCart;


                 //   Globals.SRNO = Globals.SRNO+1;
                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));

                    //Globals.TotalSlesPrice_CustomerDispla0y= Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                    Globals.TotalItem = Globals.TotalItem + 1;
                    Globals.TotalQty = Globals.TotalQty + 1;
                    final String salePrice;
                    salePrice = Globals.myNumberFormat2Price(Double.parseDouble(sale_priceStr), qty_decimal_check);
                    Globals.CMDItemPrice = Double.parseDouble(salePrice);
                    Globals.CMDItemName = resultp.getItem_name();

                }
                Globals.cart = myCart;

                // Collections.reverse(myCart);
             /*   String item_price;
                item_price = Globals.myNumberFormat2Price(Double.parseDouble(Globals.TotalItemPrice + ""), decimal_check);
                Globals.TotalItemPrice=Double.valueOf(item_price);*/
        notifyDataSetChanged();
              try {
                // ((ItemModifierSelection) (context)).setTextView(item_price, Globals.TotalQty + "");

              }
              catch(Exception e){
                  System.out.println(e.getMessage());
              }
    }
});



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

    @Override
    public int getItemCount() {
        return modiefierDataArrayList.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public Button btn_plus,btn_minus;
        public TextView text_modifiername,textmodifiercode;
        public EditText edt_quantity;
CardView cardView;
        public RelativeLayout relativeLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.btn_plus = (Button) itemView.findViewById(R.id.btn_plus_price);
            this.btn_minus = (Button) itemView.findViewById(R.id.btn_minus_price);
            this.text_modifiername = (TextView) itemView.findViewById(R.id.modifiername);
            this.edt_quantity = (EditText) itemView.findViewById(R.id.count_price);
            this.textmodifiercode = (TextView) itemView.findViewById(R.id.modifiercode);

            this.cardView = (CardView) itemView.findViewById(R.id.cardView);

        }
    }


}
