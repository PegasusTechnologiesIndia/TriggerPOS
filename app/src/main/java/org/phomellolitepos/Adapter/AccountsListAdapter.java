package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.phomellolitepos.AccountsActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.database.Contact;
import java.util.ArrayList;

/**
 * Created by LENOVO on 3/8/2018.
 */

public class AccountsListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;

    public AccountsListAdapter(Context context,
                              ArrayList<Contact> list) {
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
        TextView contact_name, txt_contact_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.contact_list_item,
                parent, false);
        Contact resultp = data.get(position);
        contact_name = (TextView) itemView.findViewById(R.id.contact_name);
        txt_contact_code = (TextView) itemView.findViewById(R.id.txt_contact_code);
        contact_name.setText(resultp.get_name());
        txt_contact_code.setText(resultp.get_contact_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Contact resultp = data.get(position);
                String operation = "Edit";
                String contact_code = resultp.get_contact_code();
                Intent intent = new Intent(context, AccountsActivity.class);
                intent.putExtra("contact_code", contact_code);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
