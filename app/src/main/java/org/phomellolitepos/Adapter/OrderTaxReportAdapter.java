package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.OrderTaxReport;

import java.util.ArrayList;

public class OrderTaxReportAdapter extends RecyclerView.Adapter<OrderTaxReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<OrderTaxReport> mValues;

    public OrderTaxReportAdapter(Context mContext, ArrayList<OrderTaxReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }



    @NonNull
    @Override
    public OrderTaxReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_order_tax_report_list, parent, false);
        return new OrderTaxReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderTaxReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView srno,taxid,taxname,taxtype,taxrate,name;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            srno=itemView.findViewById(R.id.txt_itemNo);
            taxid=itemView.findViewById(R.id.txt_orderno);
            taxname= itemView.findViewById(R.id.txt_taxname);
            taxtype=itemView.findViewById(R.id.txt_taxtype);
            taxrate=itemView.findViewById(R.id.txt_taxrate);
            name=itemView.findViewById(R.id.txt_name);

        }

        public void setData(OrderTaxReport orderTaxReport, int position)
        {
            srno.setText(position+1+"");
            taxid.setText(orderTaxReport.getTaxId());
            taxname.setText(orderTaxReport.getTaxName());
            taxtype.setText(orderTaxReport.getTaxType());
            taxrate.setText(orderTaxReport.getTaxRate());
            name.setText(orderTaxReport.getName());
        }
    }
}
