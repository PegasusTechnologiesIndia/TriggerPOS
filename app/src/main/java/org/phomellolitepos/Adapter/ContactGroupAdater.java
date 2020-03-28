package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Manufacture;

import java.util.ArrayList;

/**
 * Created by LENOVO on 9/5/2017.
 */

public class ContactGroupAdater extends BaseAdapter {
LayoutInflater inflter;
    Context context;
    ArrayList<Bussiness_Group> data;

    public ContactGroupAdater(Context context, ArrayList<Bussiness_Group> arrayList) {
        this.context = context;
        data = arrayList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView manufacture_name, manufacture_code;
        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflter.inflate(R.layout.item_manufacture_spinner_item,
                viewGroup, false);
        Bussiness_Group resultp = data.get(position);
        manufacture_name = (TextView) itemView.findViewById(R.id.manufacture_name);
        manufacture_code = (TextView) itemView.findViewById(R.id.manufacture_code);
        manufacture_name.setText(resultp.get_name());
        manufacture_code.setText(resultp.get_business_group_code());
        return itemView;
    }
    
}
