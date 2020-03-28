package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Order_Type;
import java.util.ArrayList;

/**
 * Created by LENOVO on 8/1/2018.
 */

public class ParentCategoryListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item_Group> data;
    Dialog dialog;

    public ParentCategoryListAdapter(Context context,
                                     ArrayList<Item_Group> list, Dialog listDialog) {
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

        final Item_Group resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_item_group_name());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    ((MainActivity) context).call_parent_dialog(resultp.get_item_group_code());
                }catch (Exception ex){
                    ((Main2Activity) context).call_parent_dialog(resultp.get_item_group_code());
                }
            }
        });
        return itemView;
    }
}
