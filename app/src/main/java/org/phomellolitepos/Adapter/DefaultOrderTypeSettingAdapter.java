package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Order_Type;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/16/2018.
 */

public class DefaultOrderTypeSettingAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Order_Type> data;

    public DefaultOrderTypeSettingAdapter(Context context,
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txt_order_type_name, txt_order_type_id;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.dialog_order_type_list_item,
                parent, false);

        Order_Type resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_name());
        txt_order_type_id.setText(resultp.get_order_type_id());
        
        return itemView;
    }
}
