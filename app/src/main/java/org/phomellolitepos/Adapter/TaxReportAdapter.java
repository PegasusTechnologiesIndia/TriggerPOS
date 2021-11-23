package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.TaxReport;

import java.util.ArrayList;

public class TaxReportAdapter extends RecyclerView.Adapter<TaxReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<TaxReport> mValues;


    public TaxReportAdapter(Context mContext, ArrayList<TaxReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_tax_report_list, parent, false);
        return new TaxReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno,taxId,taxName,taxType,taxRate;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            srno=itemView.findViewById(R.id.txt_itemNo);
            taxId=itemView.findViewById(R.id.txt_orderno);
            taxName=itemView.findViewById(R.id.txt_taxname);
            taxType=itemView.findViewById(R.id.txt_taxtype);
            taxRate=itemView.findViewById(R.id.txt_taxType);
        }

        public void setData(TaxReport taxReport, int position)
        {
            srno.setText(position+1+"");
            taxId.setText(taxReport.getTaxId());
            taxName.setText(taxReport.getTaxName());
            taxType.setText(taxReport.getTaxType());
            taxRate.setText(taxReport.getTaxRate());
        }
    }
}
