package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import org.phomellolitepos.ItemCategoryActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Item_Group;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class CategoryAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item_Group> data;

    public CategoryAdapter(Context context,
                           ArrayList<Item_Group> list) {
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txt_category_name1, txt_category_code1;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.category_list_item,
                parent, false);
        Item_Group resultp = data.get(position);
        txt_category_name1 = (TextView) itemView.findViewById(R.id.txt_category_name1);
        txt_category_code1 = (TextView) itemView.findViewById(R.id.txt_category_code1);
        txt_category_name1.setText(resultp.get_item_group_name());
        txt_category_code1.setText(resultp.get_item_group_code());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Item_Group resultp = data.get(position);
                String code = resultp.get_item_group_code();
                String operation = "Edit";
                Intent intent = new Intent(context, ItemCategoryActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
