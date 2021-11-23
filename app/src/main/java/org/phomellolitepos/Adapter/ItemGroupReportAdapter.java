package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.ItemGroupReport;

import java.util.ArrayList;

public class ItemGroupReportAdapter extends RecyclerView.Adapter<ItemGroupReportAdapter.ViewHolder>
{


    ArrayList<ItemGroupReport> Values;
    Context Context;

    public ItemGroupReportAdapter( Context mContext,ArrayList<ItemGroupReport> mValues)
    {
        this.Values = mValues;
        this.Context = mContext;
    }


    @NonNull
    @Override
    public ItemGroupReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Context).inflate(R.layout.content_itemgroupreport_list, parent, false);
        return new ItemGroupReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemGroupReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(Values.get(position), position);
    }

    @Override
    public int getItemCount() {
        return Values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txt_srno, txt_groupcode, txt_groupname;
        ItemGroupReport tablevalue;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            txt_srno=itemView.findViewById(R.id.txt_itemNo);
            txt_groupcode=itemView.findViewById(R.id.txt_invoiceNo);
            txt_groupname=itemView.findViewById(R.id.txt_devicename);
        }
        public void setData(ItemGroupReport table, int pos)
        {
            this.tablevalue = table;
            txt_srno.setText(pos+1+"");
            txt_groupcode.setText(tablevalue.getGroupCode());
            txt_groupname.setText(tablevalue.getGroupName());

        }
    }
}
