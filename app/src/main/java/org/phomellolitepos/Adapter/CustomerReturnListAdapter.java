package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.CusReturnHeaderActivity;
import org.phomellolitepos.CustomerReturnListActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Returns;
import java.util.ArrayList;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class CustomerReturnListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Returns> data;
    String decimal_check;
    public CustomerReturnListAdapter(Context context,
                                     ArrayList<Returns> list) {
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
        TextView txt_item_name, txt_item_code, txt_price;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.main_list_item,
                parent, false);
        Returns resultp = data.get(position);
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_code = (TextView) itemView.findViewById(R.id.txt_item_code);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }


            try {
                txt_price = (TextView) itemView.findViewById(R.id.txt_price);
                txt_item_name.setText(resultp.get_voucher_no());
                txt_item_code.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_total()), decimal_check));
                txt_price.setText(resultp.get_date().substring(0, 10));
            } catch (Exception e) {
            }

            if (resultp.get_is_post().equals("false")) {
                itemView.setBackgroundColor(Color.parseColor("#D3D3D3"));
            }
            if (resultp.get_is_cancel().equals("true")) {
                itemView.setBackgroundColor(Color.parseColor("#fb8951"));
            }
            if (resultp.get_is_post().equals("true")) {
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Returns resultp = data.get(position);
                String operation = "Edit";
                String voucher_no = resultp.get_voucher_no();
                Intent intent = new Intent(context, CusReturnHeaderActivity.class);
                intent.putExtra("voucher_no", voucher_no);
                intent.putExtra("operation", operation);


                context.startActivity(intent);

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Returns resultp = data.get(position);
                if (resultp.get_is_post().equals("true")){
                    ((CustomerReturnListActivity) context).setView(resultp.get_voucher_no());
                }
                return true;
            }
        });
        return itemView;
    }
    
}
