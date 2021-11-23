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
import org.phomellolitepos.database.VatOrderItemReport;

import java.util.ArrayList;

public class VatOrderItemReportAdapter extends RecyclerView.Adapter<VatOrderItemReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<VatOrderItemReport> mValues;
    String decimal_check;

    public VatOrderItemReportAdapter(Context mContext, ArrayList<VatOrderItemReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }


    @NonNull
    @Override
    public VatOrderItemReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(mContext).inflate(R.layout.content_vat_order_item_report_list, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VatOrderItemReportAdapter.ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno, itemcategory, itemcode,itemName, hsncode, invoiceNo, dateTime,deviceName,
                SalesPriceWithouttax, Qty, UOM, GrossAmtwithoutTax, disPer, disAmt, amtAfterdis, totalVat,netAmt;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }
            srno =itemView.findViewById(R.id.txt_itemNo);
            itemcategory =itemView.findViewById(R.id.txt_itemcategory);
            itemcode=itemView.findViewById(R.id.txt_itemCode);
            itemName =itemView.findViewById(R.id.txt_itemName);
            hsncode =itemView.findViewById(R.id.txt_hsncode);
            invoiceNo =itemView.findViewById(R.id.txt_invoiceNo);
            dateTime =itemView.findViewById(R.id.txt_datetime);
            deviceName=itemView.findViewById(R.id.txt_deviceName);
            SalesPriceWithouttax =itemView.findViewById(R.id.txt_salepricewithouTax);
            Qty =itemView.findViewById(R.id.txt_qty);
            UOM =itemView.findViewById(R.id.txt_uom);
            GrossAmtwithoutTax =itemView.findViewById(R.id.txt_grossAmtwithouttax);
            disPer=itemView.findViewById(R.id.txt_disPer);
            disAmt =itemView.findViewById(R.id.txt_disAmt);
            amtAfterdis=itemView.findViewById(R.id.txt_amtafterDis);
            totalVat=itemView.findViewById(R.id.txt_totalVat);
            netAmt=itemView.findViewById(R.id.txt_netAmt);
        }


        public void setData(VatOrderItemReport values, int position)
        {
            srno.setText(position+1+"");
            itemcategory.setText(values.getItemCategory());
            itemcode.setText(values.getItemcode());
            itemName.setText(values.getItemName());
            hsncode.setText(values.getHsnCode());
            invoiceNo.setText(values.getInvoiceNum());
            dateTime.setText(values.getDateTime());
            deviceName.setText(values.getDeviceName());
            SalesPriceWithouttax.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getSalepricewithouttax()),decimal_check));
            Qty.setText(values.getQty());
            UOM.setText(values.getUom());
            GrossAmtwithoutTax.setText(values.getGrossAmountWithoutTax());
            disPer.setText(values.getDisPer()+"%");
            disAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getDisAmt()),decimal_check));
            amtAfterdis.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getAmtAfterdis()),decimal_check));
            totalVat.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getTotalVat()),decimal_check));
            netAmt.setText(Globals.myNumberFormat2Price(Double.parseDouble(values.getNetAmount()),decimal_check));
        }
    }
}
