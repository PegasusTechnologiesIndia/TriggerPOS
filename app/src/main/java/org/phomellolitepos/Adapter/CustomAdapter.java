package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Item_Group;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflter;
    Context context;
    ArrayList<Item_Group> data;

    public CustomAdapter(Context context, ArrayList<Item_Group> arrayList) {
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
        TextView category_name, category_code;
        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflter.inflate(R.layout.item_spinner_item,
                viewGroup, false);
        Item_Group resultp = data.get(position);
        category_name = (TextView) itemView.findViewById(R.id.category_name);
        category_code = (TextView) itemView.findViewById(R.id.category_code);
        category_name.setText(resultp.get_item_group_name());
        category_code.setText(resultp.get_item_group_code());

        return itemView;
    }
}
