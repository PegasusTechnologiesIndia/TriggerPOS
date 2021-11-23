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
import org.phomellolitepos.database.DailySalesReport;
import org.phomellolitepos.database.VehicleReport;

import java.util.ArrayList;

public class DailySalesReportAdapter extends RecyclerView.Adapter<DailySalesReportAdapter.ViewHolder> {

    ArrayList<DailySalesReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public DailySalesReportAdapter(Context context, ArrayList<DailySalesReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public DailySalesReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.report_list_item, parent, false);

        return new DailySalesReportAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_netamnt, txt_amntafterdiscount, txt_amntwdtax, txt_totalinv, txt_taxamnt,tx_disamnt,txtordertype;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        DailySalesReport tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_netamnt = (TextView) v.findViewById(R.id.tv_netamnt);
            txt_amntwdtax = (TextView) v.findViewById(R.id.tv_amntwdtax);
            txt_amntafterdiscount = (TextView) v.findViewById(R.id.tv_amntaftertax);
            txt_taxamnt = (TextView) v.findViewById(R.id.taxamnt);
            txt_totalinv = (TextView) v.findViewById(R.id.tx_totalinv);
            tx_disamnt = (TextView) v.findViewById(R.id.tx_disamnt);
            txtordertype=(TextView)v.findViewById(R.id.txtOrder_type);

        }

        public void setData(DailySalesReport table, int pos) {
            this.tablevalue = table;
            txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetAmt()),decimal_check));
            txt_amntwdtax.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmtWithoutTax()),decimal_check));
            txt_amntafterdiscount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmtAfterDiscount()),decimal_check));
            txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmt()),decimal_check));
            txt_totalinv.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTotalInvoice()),decimal_check));
            tx_disamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDisAmt()),decimal_check));
            txtordertype.setText(tablevalue.getOrderType());

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull DailySalesReportAdapter.ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateList(ArrayList<DailySalesReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}