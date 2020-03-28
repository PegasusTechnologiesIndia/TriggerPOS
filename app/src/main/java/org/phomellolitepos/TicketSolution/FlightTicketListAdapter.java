package org.phomellolitepos.TicketSolution;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Ticket_Setup;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/30/2018.
 */

public class FlightTicketListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Ticket_Setup> data;
    Database db;
    SQLiteDatabase database;
    String decimal_check,Pri;
    String depart,arriv;

    public FlightTicketListAdapter(Context context, ArrayList<Ticket_Setup> list) {
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView txt_name, txt_date, txt_price;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.ticket_raw, viewGroup, false);
        final Ticket_Setup resultp = data.get(i);
        txt_name = (TextView) itemView.findViewById(R.id.txt_name);
        txt_date = (TextView) itemView.findViewById(R.id.txt_date);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);

        db = new Database(context);
        database = db.getWritableDatabase();
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        Item item = Item.getItem(context,"where item_code ='"+resultp.get_tck_from()+"'",database,db);
        try {
            depart = item.get_item_name();
        }catch (Exception ex){}
        item = Item.getItem(context,"where item_code ='"+resultp.get_tck_to()+"'",database,db);
        try {
            arriv = item.get_item_name();
        }catch (Exception ex){}
        txt_name.setText(depart+"-"+arriv);

        txt_date.setText("Departure:"+resultp.get_departure());

        Pri = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_price()), decimal_check);
        txt_price.setText(Pri);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                try {
                    Globals.Depart = resultp.get_departure();
                    Globals.Arriv = resultp.get_arrival();
                    Intent intent = new Intent(context, TicketPaymentActivity.class);
//                    intent.putExtra("name",resultp.get_name());
                    intent.putExtra("strID",resultp.get_id());
//                    intent.putExtra("departure",resultp.get_departure());
//                    intent.putExtra("price",resultp.get_price());
                    context.startActivity(intent);
                } catch (Exception ex) {
                }
                return false;
            }
        });

        return itemView;
    }
}