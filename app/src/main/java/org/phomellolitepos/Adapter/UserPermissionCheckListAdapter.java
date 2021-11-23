package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.phomellolitepos.CheckBoxClass.ItemGroupCheck;
import org.phomellolitepos.CheckBoxClass.UserPermissionCheck;
import org.phomellolitepos.R;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/2/2018.
 */

public class UserPermissionCheckListAdapter extends ArrayAdapter<UserPermissionCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<UserPermissionCheck> list;

    static class ViewHolder {
        private TextView item_gp_name;
        private CheckBox ch_item_gp;
    }

    public UserPermissionCheckListAdapter(Activity context, ArrayList<UserPermissionCheck> list) {
        super(context, R.layout.item_gp_check_list_item, list);
        this.context = context;
        this.list = list;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        UserPermissionCheckListAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.item_gp_check_list_item, null);
            viewHolder = new UserPermissionCheckListAdapter.ViewHolder();
            viewHolder.item_gp_name = (TextView) convertView.findViewById(R.id.item_gp_name);
            viewHolder.ch_item_gp = (CheckBox) convertView.findViewById(R.id.ch_item_gp);
            viewHolder.ch_item_gp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.bussness_gp_name, viewHolder.item_gp_name);
            convertView.setTag(R.id.ch_bussness_gp, viewHolder.ch_item_gp);
        } else {
            viewHolder = (UserPermissionCheckListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.ch_item_gp.setTag(position); // This line is important.
        viewHolder.item_gp_name.setText(list.get(position).getName());

            viewHolder.ch_item_gp.setChecked(list.get(position).isSelected());

        //viewHolder.ch_item_gp.setChecked(true);
        return convertView;
    }

}
