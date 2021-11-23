package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Table;

import java.util.ArrayList;

/**
 * Created by LENOVO on 9/27/2017.
 */

public class ReservationTableAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Table> data;
    Dialog dialog;

    public ReservationTableAdapter(Context context,
                                   ArrayList<Table> list) {
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
        TextView txt_tax_name, txt_tax_id;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        View itemView = inflater.inflate(R.layout.dialog_order_type_list_item,
//                parent, false);
        View itemView  = inflater.inflate(R.layout.tax_list_item,parent,false);
        Table resultp = data.get(position);
        txt_tax_name = (TextView) itemView.findViewById(R.id.txt_tax_name);
        txt_tax_id = (TextView) itemView.findViewById(R.id.txt_tax_id);
        txt_tax_id.setVisibility(View.GONE);
        txt_tax_name.setText(resultp.get_table_name());
        txt_tax_id.setText(resultp.get_table_code());

        return itemView;
    }
}
