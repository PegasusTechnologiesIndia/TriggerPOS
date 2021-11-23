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
import org.phomellolitepos.database.GstOrderItemReport;

import java.util.ArrayList;

public class GstOrderItemReportAdapter extends RecyclerView.Adapter<GstOrderItemReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<GstOrderItemReport> mValues;
    String decimal_check;

    public GstOrderItemReportAdapter(Context mContext, ArrayList<GstOrderItemReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public GstOrderItemReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_gst_order_item_report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GstOrderItemReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView itemNo, itemcategory, itemName, hsncode, invoiceNo, dateTime,
                SalesPriceWithouttax, Qty, UOM, GrossAmtwithoutTax, disPer, disAmt, amtAfterdis,
                cgstPer, CGSTamt,sgstPer, SGSTamt,igstPer, IGSTamt,totalTax,netAmt;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }
            itemNo =itemView.findViewById(R.id.txt_itemNo);
            itemcategory =itemView.findViewById(R.id.txt_itemcategory);
            itemName =itemView.findViewById(R.id.txt_itemName);
            hsncode =itemView.findViewById(R.id.txt_hsncode);
            invoiceNo =itemView.findViewById(R.id.txt_invoiceNo);
            dateTime =itemView.findViewById(R.id.txt_datetime);
            SalesPriceWithouttax =itemView.findViewById(R.id.txt_salepricewithouTax);
            Qty =itemView.findViewById(R.id.txt_qty);
            UOM =itemView.findViewById(R.id.txt_uom);
            GrossAmtwithoutTax =itemView.findViewById(R.id.txt_grossAmtwithouttax);
            disPer=itemView.findViewById(R.id.txt_disPer);
            disAmt =itemView.findViewById(R.id.txt_disAmt);
            amtAfterdis=itemView.findViewById(R.id.txt_amtafterDis);
            cgstPer =itemView.findViewById(R.id.txt_cgstPer);
            CGSTamt =itemView.findViewById(R.id.CGSTamt);
            sgstPer =itemView.findViewById(R.id.txt_sgstPer);
            SGSTamt =itemView.findViewById(R.id.SGSTamt);
            igstPer=itemView.findViewById(R.id.txt_igstPer);
            IGSTamt =itemView.findViewById(R.id.IGSTamt);
            totalTax=itemView.findViewById(R.id.txt_totalVat);
            netAmt=itemView.findViewById(R.id.txt_netAmt);

        }

        public void setData(GstOrderItemReport values, int position)
        {
            itemNo.setText(values.getItemno());
            itemcategory.setText(values.getItemcategory());
            itemName.setText(values.getItemname());
            hsncode.setText(values.getHsncode());
            invoiceNo.setText(values.getInvoicenum());
            dateTime.setText(values.getDateTime());
            SalesPriceWithouttax.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getSalePriceWithoutTax()),decimal_check));
            Qty.setText(values.getQty());
            UOM.setText(values.getUom());
            GrossAmtwithoutTax.setText(values.getgAmtWithoutTax());

            if(values.getDisPer()==null)
            {
             disPer.setText("0%");
             disAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            }else{
                  disPer.setText(values.getDisPer()+"%");
                  disAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getDisAmt()),decimal_check));
                 }

            if(values.getCgstPer()==null)
            {
                cgstPer.setText("0%");
                CGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            }else{
                cgstPer.setText(values.getCgstPer()+"%");
                CGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getCgstAmt()),decimal_check));
            }
            if(values.getSgstPer()==null)
            {
                sgstPer.setText("0%");
                SGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            }else{
                  sgstPer.setText(values.getSgstPer()+"%");
                  SGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getSgstAmt()),decimal_check));
                 }

            if(values.getIgstPer()==null)
            {
                igstPer.setText("0%");
                IGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            }else
                {
                    igstPer.setText(values.getIgstPer()+"%");
                    IGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getIgstAmt()),decimal_check));
                }

            amtAfterdis.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getAmtAfterdis()),decimal_check));
         //   cgstPer.setText(values.getCgstPer()+"%");
         //   CGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getCgstAmt()),decimal_check));
         //   sgstPer.setText(values.getSgstPer()+"%");
         //   SGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getSgstAmt()),decimal_check));
         //   igstPer.setText(values.getIgstPer()+"%");
         //   IGSTamt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getIgstAmt()),decimal_check));
            totalTax.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getTotalTax()),decimal_check));
            netAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getNetAmt()),decimal_check));
        }
    }
}
