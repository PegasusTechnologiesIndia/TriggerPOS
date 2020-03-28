package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Payment;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/2/2018.
 */

public class ReportCustomerAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;

    public ReportCustomerAdapter(Context context,
                              ArrayList<Contact> list) {
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
        TextView txt_bussiness_gp_name, txt_bussiness_gp_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.bussiness_group_list_item,
                viewGroup, false);
        Contact resultp = data.get(i);
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_code.setVisibility(View.GONE);
        txt_bussiness_gp_name.setText(resultp.get_name());
        txt_bussiness_gp_code.setText(resultp.get_contact_code());

        return itemView;
    }
}
