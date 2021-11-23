package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.phomellolitepos.CheckBoxClass.ModifierItemCheck;
import org.phomellolitepos.CheckBoxClass.TaxItemCheck;
import org.phomellolitepos.R;

import java.util.ArrayList;

public class ModifierCheckListAdapter extends ArrayAdapter<ModifierItemCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<ModifierItemCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView modifier_item;
        private CheckBox ch_modifier_item;
    }

    public ModifierCheckListAdapter(Activity context, ArrayList<ModifierItemCheck> list) {
        super(context, R.layout.modifier_item_check_list_item, list);
        this.context = context;
        this.list = list;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        ModifierCheckListAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.modifier_item_check_list_item, null);
            viewHolder = new ModifierCheckListAdapter.ViewHolder();
            viewHolder.modifier_item = (TextView) convertView.findViewById(R.id.modifier_item);
            viewHolder.ch_modifier_item = (CheckBox) convertView.findViewById(R.id.ch_modifier_item);
            viewHolder.ch_modifier_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.order_type_name, viewHolder.modifier_item);
            convertView.setTag(R.id.ch_order_type, viewHolder.ch_modifier_item);
        } else {
            viewHolder = (ModifierCheckListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.ch_modifier_item.setTag(position); // This line is important.

        viewHolder.modifier_item.setText(list.get(position).getName());
        viewHolder.ch_modifier_item.setChecked(list.get(position).isSelected());

        return convertView;
    }
}

