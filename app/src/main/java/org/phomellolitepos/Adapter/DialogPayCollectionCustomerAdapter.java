package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.PaymentCollectionActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;

import java.util.ArrayList;

/**
 * Created by LENOVO on 10/10/2017.
 */

public class DialogPayCollectionCustomerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;
    Dialog dialog;

    public DialogPayCollectionCustomerAdapter(Context context,
                                              ArrayList<Contact> list, Dialog listDialog) {
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
        Contact resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_name());
        txt_order_type_id.setText(resultp.get_contact_code());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Contact resultp = data.get(position);
                String id = resultp.get_contact_code();
                Globals.strResvContact_Code = id;
                ((PaymentCollectionActivity) context).setCustomer(resultp.get_name());
                Globals.strContact_Name = resultp.get_name();
                dialog.dismiss();
            }
        });
        return itemView;
    }
}
