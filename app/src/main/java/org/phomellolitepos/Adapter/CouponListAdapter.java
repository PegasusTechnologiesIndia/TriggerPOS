package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.CouponSetupActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.TaxActivity;
import org.phomellolitepos.database.Pro_Loyalty_Setup;
import org.phomellolitepos.database.Tax_Master;

import java.util.ArrayList;

public class CouponListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Pro_Loyalty_Setup> data;

    public CouponListAdapter(Context context,
                          ArrayList<Pro_Loyalty_Setup> list) {
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
        TextView txt_name, txt_date,txt_price;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.coupon_list_item,
                viewGroup, false);
        Pro_Loyalty_Setup resultp = data.get(i);
        txt_name = (TextView) itemView.findViewById(R.id.txt_name);
        txt_date = (TextView) itemView.findViewById(R.id.txt_date);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        txt_name.setText(resultp.get_name());
        txt_date.setText("Valid From: "+resultp.get_valid_from());
        txt_price.setText("Valid To     : "+resultp.get_valid_to());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Pro_Loyalty_Setup resultp = data.get(i);
                String id = resultp.get_id();
                String operation = "Edit";
                Intent intent = new Intent(context, CouponSetupActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
