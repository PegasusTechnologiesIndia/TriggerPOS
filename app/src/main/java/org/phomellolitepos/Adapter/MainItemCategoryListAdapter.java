package org.phomellolitepos.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import org.phomellolitepos.MainActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Item_Group;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class MainItemCategoryListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Item_Group> data;

    public MainItemCategoryListAdapter(Context context,
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

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        TextView txt_main_category_name, txt_main_category_code;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = layoutInflater.inflate(R.layout.main_category_list_item,
                parent, false);
        Item_Group resultp = data.get(position);
        txt_main_category_name = (TextView) itemView.findViewById(R.id.txt_main_category_name);
        txt_main_category_code = (TextView) itemView.findViewById(R.id.txt_main_category_code);
        txt_main_category_name.setText(resultp.get_item_group_name());
        txt_main_category_code.setText(resultp.get_item_group_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String operation = "Filter";
                Item_Group resultp = data.get(position);
                String code = resultp.get_item_group_code();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
