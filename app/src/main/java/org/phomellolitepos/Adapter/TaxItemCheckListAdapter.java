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

import org.phomellolitepos.CheckBoxClass.TaxItemCheck;
import org.phomellolitepos.R;


/**
 * Created by Neeraj on 5/2/2017.
 */

public class TaxItemCheckListAdapter extends ArrayAdapter<TaxItemCheck>{
 private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<TaxItemCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView tax_item;
        private CheckBox ch_tax_item;
    }

    public TaxItemCheckListAdapter(Activity context, ArrayList<TaxItemCheck> list) {
        super(context, R.layout.tax_item_check_list_item, list);
        this.context = context;
        this.list = list;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.tax_item_check_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tax_item = (TextView) convertView.findViewById(R.id.tax_item);
            viewHolder.ch_tax_item = (CheckBox) convertView.findViewById(R.id.ch_tax_item);
            viewHolder.ch_tax_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.order_type_name, viewHolder.tax_item);
            convertView.setTag(R.id.ch_order_type, viewHolder.ch_tax_item);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ch_tax_item.setTag(position); // This line is important.

        viewHolder.tax_item.setText(list.get(position).getName());
        viewHolder.ch_tax_item.setChecked(list.get(position).isSelected());

        return convertView;
    }
}
