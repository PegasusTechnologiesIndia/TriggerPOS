package org.phomellolitepos.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONObject;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Item;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item> data;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item_name1, txt_item_code1;

        public MyViewHolder(View view) {
            super(view);
            txt_item_name1 = (TextView) itemView.findViewById(R.id.txt_item_name1);
            txt_item_code1 = (TextView) itemView.findViewById(R.id.txt_item_code1);
        }
    }

    public ItemAdapter(Context context,ArrayList<Item> list) {
        this.context = context;
        data = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item resultp = data.get(position);
        holder.txt_item_name1.setText(resultp.get_item_name());
        holder.txt_item_code1.setText(resultp.get_item_code());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
