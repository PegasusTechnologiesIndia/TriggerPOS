package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.OrderHeaderReport;

import java.util.ArrayList;

public class OrderHeaderReportAdapter extends RecyclerView.Adapter<OrderHeaderReportAdapter.ViewHolder> {

    ArrayList<OrderHeaderReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public OrderHeaderReportAdapter(Context context, ArrayList<OrderHeaderReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderheader_report_list, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_invnumber, txt_datetime, txt_totalitem, txt_totalquantity, txtamount,txt_discntper,txt_discountamount,txt_amountafterdis,txt_grossamnt,txt_netamnt;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        OrderHeaderReport tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_invnumber = (TextView) v.findViewById(R.id.txt_itemNo);
            txt_datetime = (TextView) v.findViewById(R.id.txtdatetime);
            txt_totalitem = (TextView) v.findViewById(R.id.txttotalitem);
            txt_totalquantity = (TextView) v.findViewById(R.id.txtttl_qty);
            txtamount=(TextView)v.findViewById(R.id.txtitemamount);
            txt_discntper = (TextView) v.findViewById(R.id.txtdiscntper);
            txt_discountamount = (TextView) v.findViewById(R.id.txtdiscountamount);
            txt_amountafterdis = (TextView) v.findViewById(R.id.txtamntafterdis);
            txt_netamnt = (TextView) v.findViewById(R.id.txtnetamnt);

        }

        public void setData(OrderHeaderReport table, int pos) {
            this.tablevalue = table;
            txt_invnumber.setText(tablevalue.getInvoicenumber());
            txt_datetime.setText(tablevalue.getDate() + "  " + tablevalue.getTime());
            txt_totalitem.setText(tablevalue.getTotalitem());
            txtamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmount()),decimal_check));
            txt_totalquantity.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTotalquantity()),decimal_check));
            txt_discntper.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscountper()),decimal_check));
            txt_discountamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscountamnt()),decimal_check));
            txt_amountafterdis.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmountafterdis()),decimal_check));
            txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetamnt()),decimal_check));


        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateList(ArrayList<OrderHeaderReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
