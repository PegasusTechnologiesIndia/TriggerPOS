package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.CheckBoxClass.ReportCategoryCheck;
import org.phomellolitepos.R;


/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ReportCategoryCheckListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<ReportCategoryCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView category_name;
        private CheckBox ch_item_gp;
    }

    public ReportCategoryCheckListAdapter(Context context, ArrayList<ReportCategoryCheck> list) {
        this.context = context;
        this.list = list;
        this.itemChecked = new boolean[list.size()];
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View v, ViewGroup parent) {
        // Declare Variables
        ReportCategoryCheckListAdapter.ViewHolder holder;
        holder = new ReportCategoryCheckListAdapter.ViewHolder();
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (v == null) {
            v = inflater.inflate(R.layout.report_category_check_list_item, null);
            v.setTag(holder);
        } else {
            holder = (ReportCategoryCheckListAdapter.ViewHolder) v.getTag();
        }
        holder.category_name = (TextView) v.findViewById(R.id.category_name);
        holder.ch_item_gp = (CheckBox) v.findViewById(R.id.ch_item_gp);
        final ReportCategoryCheck a = list.get(position);
        holder.category_name.setText(a.getName());
        holder.ch_item_gp.setChecked(a.isSelected());
        itemChecked[position] = a.isSelected();
        final ReportCategoryCheckListAdapter.ViewHolder finalHolder = holder;
        holder.ch_item_gp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    a.setName(a.getName());
                    a.setSelected(true);
                } else {
                    a.setName(a.getName());
                    a.setSelected(false);
                }
            }
        });
        final ReportCategoryCheckListAdapter.ViewHolder finalHolder1 = holder;
        holder.category_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder1.ch_item_gp.isChecked()) {
                    itemChecked[position] = true;

                } else {
                    itemChecked[position] = false;
                }
            }
        });
        holder.ch_item_gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (finalHolder.ch_item_gp.isChecked()) {
                    itemChecked[position] = true;

                } else {
                    itemChecked[position] = false;
                }
            }
        });
        return v;
    }
}
