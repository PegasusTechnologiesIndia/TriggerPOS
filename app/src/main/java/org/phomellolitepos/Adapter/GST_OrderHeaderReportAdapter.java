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
import org.phomellolitepos.database.GST_OrderHeaderReport;

import java.util.ArrayList;

public class GST_OrderHeaderReportAdapter extends RecyclerView.Adapter<GST_OrderHeaderReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<GST_OrderHeaderReport> mValues;
    String decimal_check;

    public GST_OrderHeaderReportAdapter(Context mContext, ArrayList<GST_OrderHeaderReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public GST_OrderHeaderReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_gst_order_header_report_list, parent, false);
        return new GST_OrderHeaderReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GST_OrderHeaderReportAdapter.ViewHolder holder, int position)
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
        public TextView srno,orderNo,orderDate,amtwithoutTax,disPer,disAmt,
               amtAfterdis,cgstPer,cgstAmt,sgstPer,sgstAmt,igstPer,igstAmt,totalGst,invoiceAmt;

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
            orderNo=itemView.findViewById(R.id.txt_orderno);
            orderDate=itemView.findViewById(R.id.txt_itemCode);
            amtwithoutTax=itemView.findViewById(R.id.txt_grossAmtwithouttax);
            disPer=itemView.findViewById(R.id.txt_disPer);
            disAmt=itemView.findViewById(R.id.txt_invoiceNo);
            amtAfterdis=itemView.findViewById(R.id.txt_amtafterDis);
            cgstPer=itemView.findViewById(R.id.txt_cgstPer);
            cgstAmt=itemView.findViewById(R.id.txt_CGSTamt);
            sgstPer=itemView.findViewById(R.id.txt_sgstPer);
            sgstPer=itemView.findViewById(R.id.txt_sgstPer);
            sgstAmt=itemView.findViewById(R.id.txt_SGSTamt);
            igstPer=itemView.findViewById(R.id.txt_igstPer);
            igstAmt=itemView.findViewById(R.id.txt_IGSTamt);
            totalGst=itemView.findViewById(R.id.txt_totalVat);
            invoiceAmt=itemView.findViewById(R.id.txt_netAmt);

        }

        public void setData(GST_OrderHeaderReport values, int position)
        {
            srno.setText(position+1+"");
            orderNo.setText(values.getOrderno());
            orderDate.setText(values.getOrderDateTime());
            amtwithoutTax.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getAmtwithouTax()),decimal_check));
            disPer.setText(values.getDisPer()+"%");
            disAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getDisAmt()),decimal_check));
            amtAfterdis.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getAmtAfterdis()),decimal_check));
            cgstPer.setText(values.getCgstPer()+"%");
            cgstAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getCgstAmt()),decimal_check));
            sgstPer.setText(values.getSgstPer()+"%");
            sgstAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getSgstAmt()),decimal_check));
            igstPer.setText(values.getIgstPer()+"%");
            igstAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getIgstAmt()),decimal_check));
            totalGst.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getTotalGst()),decimal_check));
            invoiceAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getInvoiceAmt()),decimal_check));
        }
   }
}
