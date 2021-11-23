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
import org.phomellolitepos.database.VatOrderHeaderReport;

import java.util.ArrayList;

public class VatOrderHeaderReportAdapter extends RecyclerView.Adapter<VatOrderHeaderReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<VatOrderHeaderReport> mValues;
    String decimal_check;

    public VatOrderHeaderReportAdapter(Context mContext, ArrayList<VatOrderHeaderReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }



    @NonNull
    @Override
    public VatOrderHeaderReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_vat_order_header_report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VatOrderHeaderReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView srno,orderCode,orderDate,amtWithoutTax,disAmt,totalVat,invoiceAmt;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }
            srno=itemView.findViewById(R.id.txt_itemNo);
            orderCode=itemView.findViewById(R.id.txt_itemcategory);
            orderDate=itemView.findViewById(R.id.txt_itemCode);
            amtWithoutTax=itemView.findViewById(R.id.txt_hsncode);
            disAmt=itemView.findViewById(R.id.txt_invoiceNo);
            totalVat=itemView.findViewById(R.id.txt_datetime);
            invoiceAmt=itemView.findViewById(R.id.txt_salepricewithouTax);


        }

        public void setData(VatOrderHeaderReport values, int position)
        {
          srno.setText(position+1+"");
          orderCode.setText(values.getOrderCode());
          orderDate.setText(values.getOrderDate());
          amtWithoutTax.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getAmtWithouttax()),decimal_check));
          disAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getDisAmt()),decimal_check));
          totalVat.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getTotalVat()),decimal_check));
          invoiceAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getInvoiceAmt()),decimal_check));
        }
    }
}
