package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.AddPaymentActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.database.Payment;

import java.util.ArrayList;

public class PaymentListAdapterNew extends BaseAdapter {
    android.content.Context Context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Payment> data;

    public PaymentListAdapterNew(Context context,
                                 ArrayList<Payment> list) {
        Context = context;
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
        TextView txt_bussiness_gp_name, txt_bussiness_gp_code;
        inflater = (LayoutInflater) Context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.bussiness_group_list_item,
                viewGroup, false);
        Payment resultp = data.get(i);
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_code.setVisibility(View.GONE);
        txt_bussiness_gp_name.setText(resultp.get_payment_name());
        txt_bussiness_gp_code.setText(resultp.get_payment_id());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Payment resultp = data.get(i);
                String code = resultp.get_payment_id();
                String operation = "Edit";
                Intent intent = new Intent(Context, AddPaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                Context.startActivity(intent);
            }
        });
        return itemView;
    }
}