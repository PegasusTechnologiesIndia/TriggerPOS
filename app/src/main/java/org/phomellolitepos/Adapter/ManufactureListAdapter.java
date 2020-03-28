package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.ManufactureActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Manufacture;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ManufactureListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Manufacture> data;

    public ManufactureListAdapter(Context context,
                                  ArrayList<Manufacture> list) {
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView txt_manufacture_name, txt_manufacture_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.manufacture_list_item,
                viewGroup, false);
        Manufacture resultp = data.get(position);
        txt_manufacture_name = (TextView) itemView.findViewById(R.id.txt_manufacture_name);
        txt_manufacture_code = (TextView) itemView.findViewById(R.id.txt_manufacture_code);
        txt_manufacture_name.setText(resultp.get_manufacture_name());
        txt_manufacture_code.setText(resultp.get_manufacture_code());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Manufacture resultp = data.get(position);
                String operation = "Edit";
                String manufacture_code = resultp.get_manufacture_code();
                Intent intent = new Intent(context, ManufactureActivity.class);
                intent.putExtra("manufacture_code", manufacture_code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
