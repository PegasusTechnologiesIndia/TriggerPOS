package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import org.phomellolitepos.BussinessGroupActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Bussiness_Group;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class BussinessGroupListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Bussiness_Group> data;

    public BussinessGroupListAdapter(Context context,
                                     ArrayList<Bussiness_Group> list) {
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
        Bussiness_Group resultp = data.get(i);
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_name.setText(resultp.get_name());
        txt_bussiness_gp_code.setText(resultp.get_business_group_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bussiness_Group resultp = data.get(i);
                String code = resultp.get_business_group_code();
                String operation = "Edit";
                Intent intent = new Intent(context, BussinessGroupActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
