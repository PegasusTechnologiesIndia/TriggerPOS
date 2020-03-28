package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.UserActivity;
import org.phomellolitepos.database.User;

/**
 * Created by Neeraj on 5/23/2017.
 */

public class UserListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<User> data;

    public UserListAdapter(Context context,
                           ArrayList<User> list) {
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
        TextView txt_bussiness_gp_name, txt_bussiness_gp_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.bussiness_group_list_item,
                viewGroup, false);
        User resultp = data.get(i);
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_name.setText(resultp.get_name());
        txt_bussiness_gp_code.setText(resultp.get_user_code());

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                User resultp = data.get(i);
                String code = resultp.get_user_code();
                String operation = "Edit";
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
