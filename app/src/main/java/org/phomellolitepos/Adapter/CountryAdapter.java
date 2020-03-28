package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;

import java.util.ArrayList;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class CountryAdapter extends BaseAdapter {
    LayoutInflater inflter;
    Context context;
    ArrayList<org.phomellolitepos.database.Country> data;

    public CountryAdapter(Context context, ArrayList<org.phomellolitepos.database.Country> arrayList) {
        this.context = context;
        data = arrayList;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView country_name, country_code;

        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflter.inflate(R.layout.country_spinner_item,
                viewGroup, false);
        org.phomellolitepos.database.Country resultp = data.get(i);
        country_name = (TextView) itemView.findViewById(R.id.country_name);
        country_code = (TextView) itemView.findViewById(R.id.country_code);
        country_name.setText(resultp.get_name());
        country_code.setText(resultp.get_country_id());
        return itemView;
    }
}
