package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Table;

/**
 * Created by Neeraj on 6/15/2017.
 */

public class DialogTableMainListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Table> data;
    Dialog dialog;

    public DialogTableMainListAdapter(Context context,
                                      ArrayList<Table> list, Dialog listDialog) {
        this.context = context;
        data = list;
        dialog = listDialog;
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
        Table resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_table_name());
        txt_order_type_id.setText(resultp.get_table_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Table resultp = data.get(position);
                String id = resultp.get_table_code();
                Globals.strTable_Code = id;
                dialog.dismiss();
            }
        });
        return itemView;
    }
}
