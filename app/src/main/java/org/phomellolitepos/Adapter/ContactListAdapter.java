package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.ContactActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Item;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>{
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contact_name, txt_contact_code;

        public MyViewHolder(View view) {
            super(view);
            contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            txt_contact_code = (TextView) itemView.findViewById(R.id.txt_contact_code);
        }
    }

    public ContactListAdapter(Context context,ArrayList<Contact> list) {
        this.context = context;
        data = list;
    }


    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.MyViewHolder holder, int position) {
        Contact resultp = data.get(position);
        holder.contact_name.setText(resultp.get_name());
        holder.txt_contact_code.setText(resultp.get_contact_code());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
