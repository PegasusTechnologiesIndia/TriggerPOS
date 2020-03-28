package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.TicketCategoryActivity;
import org.phomellolitepos.TicketSetupActivity;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/6/2018.
 */

public class ClassCategoryListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item_Group> data;

    public ClassCategoryListAdapter(Context context,
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView txt_item_name1, txt_item_code1;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.item_list_item,
                viewGroup, false);
        Item_Group resultp = data.get(position);
        txt_item_name1 = (TextView) itemView.findViewById(R.id.txt_item_name1);
        txt_item_code1 = (TextView) itemView.findViewById(R.id.txt_item_code1);
        txt_item_name1.setText(resultp.get_item_group_name());
//        txt_item_code1.setVisibility(View.GONE);
//        txt_unit_code.setText(resultp.get_item_group_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Item_Group resultp = data.get(position);
                String code = resultp.get_item_group_code();
                String operation = "Edit";
                Intent intent = new Intent(context, TicketCategoryActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
