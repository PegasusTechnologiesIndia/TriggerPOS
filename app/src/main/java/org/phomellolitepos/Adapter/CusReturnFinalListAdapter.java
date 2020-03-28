package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.CusReturnFinalActivity;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.Globals;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class CusReturnFinalListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    String decimal_check;
    ArrayList<StockAdjectmentDetailList> data;

    public CusReturnFinalListAdapter(Context context,
                                     ArrayList<StockAdjectmentDetailList> list) {
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
        StockAdjectmentDetailList resultp = data.get(position);
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_code = (TextView) itemView.findViewById(R.id.txt_item_code);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_item_name.setText(resultp.getItem_name());
        txt_item_code.setText(resultp.getItem_code());
        txt_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.getLine_total()), decimal_check));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_update= "update";
                StockAdjectmentDetailList resultp = data.get(position);
                ((CusReturnFinalActivity) context).setTextView(position+"",resultp.getItem_code(),resultp.getItem_name(),resultp.getQty(),resultp.getPrice(),str_update);
            }
        });


        return itemView;
    }
}
