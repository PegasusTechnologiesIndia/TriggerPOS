package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Contact;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>{
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;
    Contact resultp;
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

/*       itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String operation = "Edit";
                String contact_code = resultp.get_contact_code();
                Intent intent = new Intent(context, AccountsActivity.class);
                intent.putExtra("contact_code", contact_code);
                context.startActivity(intent);

                return false;
            }
        });*/

        return new ContactListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.MyViewHolder holder, int position) {
         resultp = data.get(position);
        holder.contact_name.setText(resultp.get_name());
        holder.txt_contact_code.setText(resultp.get_contact_code());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
