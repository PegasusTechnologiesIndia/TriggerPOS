package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.PurchaseHeaderActivity;
import org.phomellolitepos.PurchaseListActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Purchase;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class PurchaseListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Purchase> data;
    String decimal_check;
    Database db;
    SQLiteDatabase database;
    public PurchaseListAdapter(Context context,
                               ArrayList<Purchase> list) {
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
        TextView txt_voucher_no, txt_supl_name, txt_date, txt_price;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.purchase_list_item,
                parent, false);
        Purchase resultp = data.get(position);
        txt_voucher_no = (TextView) itemView.findViewById(R.id.txt_voucher_no);
        txt_supl_name = (TextView) itemView.findViewById(R.id.txt_supl_name);
        txt_date = (TextView) itemView.findViewById(R.id.txt_date);
        txt_supl_name = (TextView) itemView.findViewById(R.id.txt_supl_name);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);

        db = new Database(context);
        database = db.getWritableDatabase();
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        try {

            txt_voucher_no.setText(resultp.get_voucher_no());
            Contact contact = Contact.getContact(context,database,db," where contact_code='"+resultp.get_contact_code()+"'");
            if (contact!=null){
                txt_supl_name.setText(contact.get_name());

            }
            txt_date.setText(resultp.get_date().substring(0, 10));
            txt_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_total()), decimal_check));
        } catch (Exception e) {
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Purchase resultp = data.get(position);
                String operation = "Edit";
                String voucher_no = resultp.get_voucher_no();
                Intent intent = new Intent(context, PurchaseHeaderActivity.class);
                intent.putExtra("voucher_no", voucher_no);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Purchase resultp = data.get(position);
                if (resultp.get_is_post().equals("true")){
                    ((PurchaseListActivity) context).setView(resultp.get_voucher_no());
                }
                return true;
            }
        });
        return itemView;
    }
}
