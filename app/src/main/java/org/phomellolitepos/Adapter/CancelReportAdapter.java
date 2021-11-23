package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.CancelReport;

import java.util.ArrayList;

public class CancelReportAdapter extends RecyclerView.Adapter<CancelReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<CancelReport> mValues;

    public CancelReportAdapter(Context mContext, ArrayList<CancelReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_cancel_report_list, parent, false);
        return new CancelReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount()
    {
     return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno,invoicenumber,datetime,salesperson,devicename,amtwithouttax,disPer,disAmt,amtAfterdis,taxAmt,netAmt,orderType;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            srno=itemView.findViewById(R.id.txt_itemNo);
            invoicenumber=itemView.findViewById(R.id.txt_invoiceNo);
            datetime=itemView.findViewById(R.id.txt_datetime);
            salesperson=itemView.findViewById(R.id.txt_saleperson);
            devicename=itemView.findViewById(R.id.txt_devicename);
            amtwithouttax=itemView.findViewById(R.id.txt_grossAmtwithouttax);
            disPer=itemView.findViewById(R.id.txt_disPer);
            disAmt=itemView.findViewById(R.id.txt_invoiceNo);
            taxAmt=itemView.findViewById(R.id.txt_taxAmt);
            amtAfterdis=itemView.findViewById(R.id.txt_amtafterdis);
            netAmt=itemView.findViewById(R.id.txt_netAmt);
            orderType=itemView.findViewById(R.id.txt_ordertype);


        }

        public void setData(CancelReport cancelReport, int position)
        {
          srno.setText(position+1+"");
          invoicenumber.setText(cancelReport.getInvoiceNumber());
          datetime.setText(cancelReport.getDate()+" "+cancelReport.getTime());
          salesperson.setText(cancelReport.getSalesperson());
          devicename.setText(cancelReport.getDeviceName());
          amtwithouttax.setText(cancelReport.getAmtWithouttax());
          disPer.setText(cancelReport.getDiscountPer());
          disAmt.setText(cancelReport.getDiscountAmt());
          amtAfterdis.setText(cancelReport.getAmtAfterDis());
          taxAmt.setText(cancelReport.getTaxAmt());
          netAmt.setText(cancelReport.getNetAmt());
          orderType.setText(cancelReport.getOrderType());
          //contact.setText(cancelReport.getContact());
        }
    }
}
