package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.R;
import org.phomellolitepos.TaxActivity;
import org.phomellolitepos.database.Sys_Support;
import org.phomellolitepos.database.Tax_Master;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/12/2018.
 */

public class YouTubeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Sys_Support> data;

    public YouTubeListAdapter(Context context,
                              ArrayList<Sys_Support> list) {
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
        TextView txt_name, txt_url;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.tax_list_item,
                viewGroup, false);
        Sys_Support resultp = data.get(i);
        txt_name = (TextView) itemView.findViewById(R.id.txt_tax_name);
        txt_url = (TextView) itemView.findViewById(R.id.txt_tax_id);
        txt_url.setVisibility(View.GONE);
        txt_name.setText(resultp.get_name());
        txt_url.setText(resultp.get_video_url());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Sys_Support resultp = data.get(i);
                    String video_path = resultp.get_video_url();
                    Uri uri = Uri.parse(video_path);
                    uri = Uri.parse("vnd.youtube:" + uri.getQueryParameter("v"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(context, "No youtube app found for play this video", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return itemView;
    }
}
