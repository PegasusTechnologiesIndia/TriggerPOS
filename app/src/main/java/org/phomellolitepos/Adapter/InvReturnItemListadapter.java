package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.InvReturnHeaderActivity;
import org.phomellolitepos.InvReturnListActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Returns;

import java.util.ArrayList;

public class InvReturnItemListadapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Order_Detail> data;
    String decimal_check;
    ArrayList<Object>  item_name;

    public InvReturnItemListadapter(Context context,
                                    ArrayList<Order_Detail> list,ArrayList<Object> itemname) {
        this.context = context;
        data = list;
        item_name=itemname;
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
        TextView txt_qty, txt_temcode, txt_itemname, txt_total, txt_inv_no;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.inv_return_listitemcode,
                parent, false);
        Order_Detail resultp = data.get(position);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_qty = (TextView) itemView.findViewById(R.id.txt_qty);
        txt_temcode = (TextView) itemView.findViewById(R.id.txt_itemcode);

        txt_itemname = (TextView) itemView.findViewById(R.id.txt_itemname);


        try {

            txt_itemname.setText(item_name.get(position).toString());

            txt_temcode.setText(resultp.get_item_code());
            txt_qty.setText(resultp.get_quantity());
        } catch (Exception e) {
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Order_Detail resultp = data.get(position);
            /*    String operation = "Edit";
                String voucher_no = resultp.get_voucher_no();
                Intent intent = new Intent(context, InvReturnHeaderActivity.class);
                intent.putExtra("voucher_no", voucher_no);
                intent.putExtra("operation", operation);
                context.startActivity(intent);*/
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Order_Detail resultp = data.get(position);
             /*   if (resultp.get_is_post().equals("true")) {
                    ((InvReturnListActivity) context).setView(resultp.get_voucher_no());
                }*/
                return true;
            }
        });
        return itemView;
    }
}
