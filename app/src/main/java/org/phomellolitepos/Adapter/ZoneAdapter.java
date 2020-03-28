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

public class ZoneAdapter extends BaseAdapter {
    LayoutInflater inflter;
    Context context;
    ArrayList<org.phomellolitepos.database.Zone> data;

    public ZoneAdapter(Context context, ArrayList<org.phomellolitepos.database.Zone> arrayList) {
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
        TextView zone_name, zone_code;
        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflter.inflate(R.layout.zone_spinner_item,
                viewGroup, false);
        org.phomellolitepos.database.Zone resultp = data.get(i);
        zone_name = (TextView) itemView.findViewById(R.id.zone_name);
        zone_code = (TextView) itemView.findViewById(R.id.zone_code);
        zone_name.setText(resultp.get_name());
        zone_code.setText(resultp.get_zone_id());

        return itemView;
    }
}
