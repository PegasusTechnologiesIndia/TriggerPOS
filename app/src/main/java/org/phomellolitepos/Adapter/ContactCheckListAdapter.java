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

import org.phomellolitepos.CheckBoxClass.ContactCheck;
import org.phomellolitepos.R;


/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ContactCheckListAdapter extends ArrayAdapter<ContactCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<ContactCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView contact_name;
        private CheckBox ch_contact;
    }

    public ContactCheckListAdapter(Activity context, ArrayList<ContactCheck> list) {
        super(context, R.layout.contact_check_list_item, list);
        this.context = context;
        this.list = list;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.contact_check_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.ch_contact = (CheckBox) convertView.findViewById(R.id.ch_contact);
            viewHolder.ch_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.contact_name, viewHolder.contact_name);
            convertView.setTag(R.id.ch_contact, viewHolder.ch_contact);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ch_contact.setTag(position); // This line is important.
        viewHolder.contact_name.setText(list.get(position).getName());
        viewHolder.ch_contact.setChecked(list.get(position).isSelected());

        return convertView;
    }
}
