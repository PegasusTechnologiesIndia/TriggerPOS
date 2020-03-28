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
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.CheckBoxClass.OrderTypeCheck;
import org.phomellolitepos.R;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class OrderTypeCheckListAdapter extends ArrayAdapter<OrderTypeCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<OrderTypeCheck> list;

    static class ViewHolder {
        private TextView order_type_name;
        private CheckBox ch_order_type;
    }


    public OrderTypeCheckListAdapter(Activity context, ArrayList<OrderTypeCheck> list) {
        super(context, R.layout.order_type_check_list_item, list);
        this.context = context;
        this.list = list;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.order_type_check_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.order_type_name = (TextView) convertView.findViewById(R.id.order_type_name);
            viewHolder.ch_order_type = (CheckBox) convertView.findViewById(R.id.ch_order_type);
            viewHolder.ch_order_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.order_type_name, viewHolder.order_type_name);
            convertView.setTag(R.id.ch_order_type, viewHolder.ch_order_type);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ch_order_type.setTag(position); // This line is important.
        viewHolder.order_type_name.setText(list.get(position).getName());
        viewHolder.ch_order_type.setChecked(list.get(position).isSelected());

        return convertView;
    }
}
