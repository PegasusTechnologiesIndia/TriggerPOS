package org.phomellolitepos.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Manufacture;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ManufactureSpiinerAdapter extends BaseAdapter {
    LayoutInflater inflter;
    Context context;
    ArrayList<Manufacture> data;

    public ManufactureSpiinerAdapter(Context context, ArrayList<Manufacture> arrayList) {
        this.context = context;
        data = arrayList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView txt_manufacture_name, txt_manufacture_code;
        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflter.inflate(R.layout.manufacture_list_item,
                viewGroup, false);
        Manufacture resultp = data.get(position);
        txt_manufacture_name = (TextView) itemView.findViewById(R.id.txt_manufacture_name);
        txt_manufacture_code = (TextView) itemView.findViewById(R.id.txt_manufacture_code);
        txt_manufacture_code.setVisibility(View.INVISIBLE);
        txt_manufacture_name.setText(resultp.get_manufacture_name());
        return itemView;
    }
}
