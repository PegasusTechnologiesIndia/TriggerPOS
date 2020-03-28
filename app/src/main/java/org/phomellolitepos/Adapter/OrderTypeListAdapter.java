package org.phomellolitepos.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Order_Type;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class OrderTypeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Order_Type> data;

    public OrderTypeListAdapter(Context context,
                                ArrayList<Order_Type> list) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView txt_order_type_name;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.order_type_list_item,
                viewGroup, false);
        Order_Type resultp = data.get(i);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_name.setText(resultp.get_name());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Order_Type resultp = data.get(i);
                String order_type_id = resultp.get_order_type_id();
                String operation = "Edit";
                Toast.makeText(context, order_type_id, Toast.LENGTH_SHORT).show();
            }
        });
        return itemView;
    }
}
