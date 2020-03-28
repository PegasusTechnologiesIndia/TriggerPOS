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

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Order_Type;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class DialogOrderTypeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Order_Type> data;
    Dialog dialog;
    MenuItem orertyp;
    BottomNavigationView topNavigation;

    public DialogOrderTypeListAdapter(Context context,
                                      ArrayList<Order_Type> list, Dialog listDialog, MenuItem orertype, BottomNavigationView topNavigationView) {
        this.context = context;
        data = list;
        dialog = listDialog;
        orertyp = orertype;
        topNavigation = topNavigationView;
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
        Menu menu = topNavigation.getMenu();
        orertyp = menu.findItem(R.id.action_order_type);
        Order_Type resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_name());
        txt_order_type_id.setText(resultp.get_order_type_id());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Order_Type resultp = data.get(position);
                String id = resultp.get_order_type_id();
                Globals.strOrder_type_id = id;
                orertyp.setTitle(resultp.get_name());
                switch (id) {
                    case "1":
                        orertyp.setIcon(R.drawable.deliver);
                        break;
                    case "2":
                        orertyp.setIcon(R.drawable.drive_thrue);
                        break;
                    case "3":
                        orertyp.setIcon(R.drawable.pick_up);
                        break;
                    case "4":
                        orertyp.setIcon(R.drawable.take_out);
                        break;
                    case "5":
                        orertyp.setIcon(R.drawable.dine_ine);
                        break;
                    default:
                        orertyp.setIcon(R.drawable.deliver);
                        break;
                }
                dialog.dismiss();
            }
        });
        return itemView;
    }
}
