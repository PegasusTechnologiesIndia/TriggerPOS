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

import org.phomellolitepos.database.MonthlySalesReport;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;

import java.util.ArrayList;

public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.ViewHolder> {

    ArrayList<MonthlySalesReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public MonthlyReportAdapter(Context context, ArrayList<MonthlySalesReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_monthly_report_list, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_ttlinv, txt_netamnt, txt_amnt, txt_month, txt_year,txtdisamnt;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        MonthlySalesReport tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_ttlinv = (TextView) v.findViewById(R.id.ttl_inv);
            txt_netamnt = (TextView) v.findViewById(R.id.txtnetamnt);
            txt_amnt = (TextView) v.findViewById(R.id.txtamount);
            txt_month = (TextView) v.findViewById(R.id.txtmonth);
            txtdisamnt = (TextView) v.findViewById(R.id.discamount);


        }

        public void setData(MonthlySalesReport table, int pos) {
            this.tablevalue = table;
            txt_ttlinv.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTotalInvoice()),decimal_check));
            txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetAmount()),decimal_check));
            txt_amnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmount()),decimal_check));
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_month.setText(tablevalue.getMonth() + "/" + tablevalue.getYear());
txtdisamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscountamount()),decimal_check));

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

    public void updateList(ArrayList<MonthlySalesReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }


}
