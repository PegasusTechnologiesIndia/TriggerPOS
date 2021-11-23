package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.ItemTaxReport;

import java.util.ArrayList;

public class ItemTaxReportAdapter extends RecyclerView.Adapter<ItemTaxReportAdapter.ViewHolder>
{


    Context mContext;
    ArrayList<ItemTaxReport> mValues;

    public ItemTaxReportAdapter(Context mContext, ArrayList<ItemTaxReport> mValues)
    {
        this.mContext = mContext;
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public ItemTaxReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_tax_report_list, parent, false);
        return new ItemTaxReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTaxReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView srno,itemcode,itemname,hsnCode,barcode,itemType,isInclusiveTax,Taxid,Taxname,TaxType,Taxrate;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            srno=itemView.findViewById(R.id.txt_itemNo);
            itemcode=itemView.findViewById(R.id.txt_itemcode);
            itemname=itemView.findViewById(R.id.txt_itemName);
            hsnCode=itemView.findViewById(R.id.txt_hsncode);
            barcode=itemView.findViewById(R.id.txt_barcode);
            itemType=itemView.findViewById(R.id.txt_itemtype);
            isInclusiveTax=itemView.findViewById(R.id.txt_isInclusiveTax);
            Taxid=itemView.findViewById(R.id.txt_taxid);
            Taxname=itemView.findViewById(R.id.txt_taxname);
            TaxType=itemView.findViewById(R.id.txt_taxType);
            Taxrate=itemView.findViewById(R.id.txt_taxrate);
        }

        public void setData(ItemTaxReport itemTaxReport, int position)
        {
            srno.setText(position+1+"");
            itemcode.setText(itemTaxReport.getItemcode());
            itemname.setText(itemTaxReport.getItemname());
            hsnCode.setText(itemTaxReport.getHsnCode());
            barcode.setText(itemTaxReport.getBarcode());
            itemType.setText(itemTaxReport.getItemType());
            isInclusiveTax.setText(itemTaxReport.getIsInclusiveTax());
            Taxid.setText(itemTaxReport.getTaxId());
            Taxname.setText(itemTaxReport.getTaxName());
            TaxType.setText(itemTaxReport.getTaxType());
            Taxrate.setText(itemTaxReport.getTaxRate()+"%");

        }
    }
}
