package org.phomellolitepos.Adapter;

import android.annotation.SuppressLint;
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

@SuppressLint({"InflateParams", "ViewHolder"})
public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> list = new ArrayList<String>();

    public MyAdapter(Context context, ArrayList<String> list) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int pos, View arg1, ViewGroup arg2) {
        View inflateView = inflater.inflate(R.layout.items_printout, null);
        TextView title = (TextView) inflateView.findViewById(R.id._title);
        title.setText(list.get(pos));

        return inflateView;
    }
}
