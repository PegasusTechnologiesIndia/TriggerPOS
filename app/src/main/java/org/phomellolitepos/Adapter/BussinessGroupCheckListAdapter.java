package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.CheckBoxClass.BusinessGroupCheck;
import org.phomellolitepos.R;


/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class BussinessGroupCheckListAdapter extends ArrayAdapter<BusinessGroupCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<BusinessGroupCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView bussness_gp_name;
        private CheckBox ch_bussness_gp;
    }

    public BussinessGroupCheckListAdapter(Activity context, ArrayList<BusinessGroupCheck> list) {
        super(context, R.layout.bussiness_gp_check_list_item, list);
        this.context = context;
        this.list = list;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.bussiness_gp_check_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.bussness_gp_name = (TextView) convertView.findViewById(R.id.bussness_gp_name);
            viewHolder.ch_bussness_gp = (CheckBox) convertView.findViewById(R.id.ch_bussness_gp);
            viewHolder.ch_bussness_gp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.bussness_gp_name, viewHolder.bussness_gp_name);
            convertView.setTag(R.id.ch_bussness_gp, viewHolder.ch_bussness_gp);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ch_bussness_gp.setTag(position); // This line is important.

        viewHolder.bussness_gp_name.setText(list.get(position).getName());
        viewHolder.ch_bussness_gp.setChecked(list.get(position).isSelected());
        return convertView;
    }
}
