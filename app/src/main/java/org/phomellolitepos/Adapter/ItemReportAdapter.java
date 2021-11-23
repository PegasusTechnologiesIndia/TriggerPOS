package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.ItemReport;

import java.util.ArrayList;

public class ItemReportAdapter extends RecyclerView.Adapter<ItemReportAdapter.ViewHolder>{



    ArrayList<ItemReport> mValues;
    Context mContext;
    String decimal_check;

    public ItemReportAdapter( Context mContext,ArrayList<ItemReport> mValues) {
        this.mValues = mValues;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ItemReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_report_list, parent, false);
        return new ItemReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno,itemcode,itemname,costprice,salesprice,newsaleprice;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            try {
                 decimal_check = Globals.objLPD.getDecimal_Place();
                }
            catch (Exception ex) {
                decimal_check = "1";
            }

            srno=itemView.findViewById(R.id.txt_itemNo);
            itemcode=itemView.findViewById(R.id.txt_orderno);
            itemname=itemView.findViewById(R.id.txt_taxname);
            costprice=itemView.findViewById(R.id.txt_buyerName);
            salesprice=itemView.findViewById(R.id.txt_saleprice);
            newsaleprice=itemView.findViewById(R.id.txt_newsalesprice);
        }

        public void setData(ItemReport itemReport, int position)
        {
            srno.setText(position+1+"");
            itemcode.setText(itemReport.getItemcode());
            itemname.setText(itemReport.getItemname());
            costprice.setText(itemReport.getCostprice());
            salesprice.setText(itemReport.getSaleprice());
            newsaleprice.setText(itemReport.getNewsaleprice());
        }
    }
}
