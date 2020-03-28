package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.TaxActivity;
import org.phomellolitepos.UnitActivity;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/1/2018.
 */

public class UnitListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Unit> data;

    public UnitListAdapter(Context context,
                           ArrayList<Unit> list) {
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
        TextView txt_tax_name, txt_tax_id;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.tax_list_item,
                viewGroup, false);
        Unit resultp = data.get(i);
        txt_tax_name = (TextView) itemView.findViewById(R.id.txt_tax_name);
        txt_tax_id = (TextView) itemView.findViewById(R.id.txt_tax_id);
        txt_tax_name.setText(resultp.get_name());
        txt_tax_id.setText(resultp.get_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Unit resultp = data.get(i);
                String id = resultp.get_unit_id();
                String operation = "Edit";
                Intent intent = new Intent(context, UnitActivity.class);
                intent.putExtra("unit_id", id);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
