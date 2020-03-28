package org.phomellolitepos.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.phomellolitepos.CustomerImageActivity;
import org.phomellolitepos.ExpensesActivity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.ReservationActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.Reservation;
import org.phomellolitepos.database.Reservation_Detail;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;

import java.util.ArrayList;

import sunmi.ds.DSKernel;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.UPacketFactory;

/**
 * Created by LENOVO on 9/27/2017.
 */

public class ReservationListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Reservation> data;
    String decimal_check;
    Database db;
    SQLiteDatabase database;
    String item_group_code;

    public ReservationListAdapter(Context context,
                                  ArrayList<Reservation> list) {
        this.context = context;
        data = list;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView txt_expense_name, txt_expense_amount;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.expenses_list_item,
                parent, false);
        db = new Database(context);
        database = db.getWritableDatabase();
        Reservation resultp = data.get(position);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        txt_expense_name = (TextView) itemView.findViewById(R.id.txt_expense_name);
        txt_expense_amount = (TextView) itemView.findViewById(R.id.txt_expense_amount);
        Contact contact = null;
        try {
            if (resultp.get_customer_code().equals("") || resultp.get_customer_code().equals("null")) {
            } else {
                contact = Contact.getContact(context, database, db, "WHERE contact_code='" + resultp.get_customer_code() + "'");
            }

            txt_expense_name.setText(contact.get_name());
        } catch (Exception ex) {

        }
        String dt = resultp.get_date_time();
        String[] dtary = dt.split("#");
        String d = dtary[0];
        String t = dtary[1];
        String dateTime = d + "  " + t;
        txt_expense_amount.setText(dateTime);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        context);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage(R.string.do_u_want_to_genert);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                alertDialog.setNegativeButton(R.string.edit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Reservation resultp = data.get(position);
                                String operation = "Edit";
                                String id = resultp.get_id();
                                Intent intent = new Intent(context, ReservationActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("operation", operation);
                                context.startActivity(intent);
                            }
                        });
                alertDialog.setPositiveButton(R.string.invoice,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Globals.cart.clear();
                                Globals.setEmpty();
                                String sale_priceStr;
                                String cost_priceStr;
                                ArrayList<Item_Group_Tax> item_group_taxArrayList = null;
                                Reservation resultp = data.get(position);
                                Globals.UserResrv = resultp.get_user_code();
                                Globals.CustomerResrv = resultp.get_customer_code();
                                final ArrayList<ShoppingCart> myCart = Globals.cart;
                                ArrayList<Reservation_Detail> reservation_details = Reservation_Detail.getAllReservation_Detail(context, "WHERE ref_id='" + resultp.get_id() + "'", database);
                                for (int count = 0; count < reservation_details.size(); count++) {
                                    Item item = Item.getItem(context, " WHERE item_code='" + reservation_details.get(count).get_item_code() + "'", database, db);
                                    item_group_code = item.get_item_code();
                                    Item_Location item_location = Item_Location.getItem_Location(context, " WHERE item_code='" + reservation_details.get(count).get_item_code() + "'", database);
                                    item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item.get_item_group_code() + "'", database, db);
                                    if (item_location == null) {
                                        sale_priceStr = "0";
                                        cost_priceStr = "0";
                                    } else {
                                        sale_priceStr = item_location.get_selling_price();
                                        cost_priceStr = item_location.get_cost_price();
                                    }
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
                                            Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", reservation_details.get(i).get_item_code(), taxIdFinalAarry.get(i), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                            Globals.order_item_tax.add(order_item_tax);
                                        }
                                    }


                                    Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
                                    ShoppingCart cartItem = new ShoppingCart(context, Globals.SRNO + "", item.get_item_code(), item.get_item_name(), "1", cost_priceStr, sale_priceStr + "", iTaxTotal + "", "0", (1 * Double.parseDouble(sale_priceStr)) + iTaxTotal + "");
                                    Globals.cart.add(cartItem);
                                    Globals.SRNO = Globals.SRNO + 1;
                                    Globals.TotalItemPrice = Globals.TotalItemPrice + (1 * Double.parseDouble(sprice + ""));
                                    Globals.TotalItem = Globals.TotalItem + 1;
                                    Globals.TotalQty = Globals.TotalQty + 1;
                                    Globals.cart = myCart;
                                }
                                String strOperation = "Resv";
                                Globals.ModeResrv = strOperation;
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("opr", strOperation);
                                context.startActivity(intent);
                            }
                        });
                AlertDialog alert = alertDialog.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(context.getResources().

                        getColor(R.color.colorPrimary));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(context.getResources().

                        getColor(R.color.colorPrimary));


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
}
