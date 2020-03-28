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
import org.phomellolitepos.TaxActivity;
import org.phomellolitepos.database.Tax_Master;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class TaxListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Tax_Master> data;

    public TaxListAdapter(Context context,
                          ArrayList<Tax_Master> list) {
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
        Tax_Master resultp = data.get(i);
        txt_tax_name = (TextView) itemView.findViewById(R.id.txt_tax_name);
        txt_tax_id = (TextView) itemView.findViewById(R.id.txt_tax_id);
        txt_tax_name.setText(resultp.get_tax_name());
        txt_tax_id.setText(resultp.get_tax_id());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Tax_Master resultp = data.get(i);
                String id = resultp.get_tax_id();
                String operation = "Edit";
                Intent intent = new Intent(context, TaxActivity.class);
                intent.putExtra("tax_id", id);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
