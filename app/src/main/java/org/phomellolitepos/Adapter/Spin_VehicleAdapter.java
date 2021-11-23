package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.User;

import java.util.ArrayList;

public class Spin_VehicleAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item> data;

    public Spin_VehicleAdapter(Context context,
                       ArrayList<Item> list) {
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
        Item resultp = data.get(i);
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_code.setVisibility(View.GONE);

        txt_bussiness_gp_name.setText(resultp.get_item_name());
        txt_bussiness_gp_code.setText(resultp.get_item_code());

        return itemView;
    }
}
