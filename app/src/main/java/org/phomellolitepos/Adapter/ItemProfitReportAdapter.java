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
import org.phomellolitepos.database.ItemProfitReport;

import java.util.ArrayList;

public class ItemProfitReportAdapter extends RecyclerView.Adapter<ItemProfitReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<ItemProfitReport> mValues;
    String decimal_check;

    public ItemProfitReportAdapter(Context mContext, ArrayList<ItemProfitReport> mValues)
    {
        this.mContext = mContext;
        this.mValues = mValues;
    }


    @NonNull
    @Override
    public ItemProfitReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_profit_report_list, parent, false);
        return new ItemProfitReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemProfitReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno,itemcode,itemname,costprice,salesprice,qty,netproft,netprofitper;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            srno=itemView.findViewById(R.id.txt_itemNo);
            itemcode=itemView.findViewById(R.id.txt_orderno);
            itemname=itemView.findViewById(R.id.txt_taxname);
            costprice=itemView.findViewById(R.id.txt_buyerName);
            salesprice=itemView.findViewById(R.id.txt_saleprice);
            qty=itemView.findViewById(R.id.txt_qty);
            netproft=itemView.findViewById(R.id.txt_netprofit);
            netprofitper=itemView.findViewById(R.id.txt_netprofitper);
        }

        public void setData(ItemProfitReport itemProfitReport, int position)
        {
            srno.setText(position+1+"");
            itemcode.setText(itemProfitReport.getItemCode());
            itemname.setText(itemProfitReport.getItemName());

            costprice.setText(Globals.myNumberFormat2Price(Double.parseDouble(itemProfitReport.getCostprice()),decimal_check));
            salesprice.setText(Globals.myNumberFormat2Price(Double.parseDouble(itemProfitReport.getSalesprice()),decimal_check));
            qty.setText(itemProfitReport.getQty());
            netproft.setText(Globals.myNumberFormat2Price(Double.parseDouble(itemProfitReport.getNetProfit()),decimal_check));
            netprofitper.setText(Globals.myNumberFormat2Price(Double.parseDouble(itemProfitReport.getNetProfitPer()),decimal_check));
        }
    }
}
