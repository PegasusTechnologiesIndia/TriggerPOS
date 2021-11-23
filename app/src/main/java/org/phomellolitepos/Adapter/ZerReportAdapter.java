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
import org.phomellolitepos.database.ZeroReport;

import java.util.ArrayList;

public class ZerReportAdapter extends RecyclerView.Adapter<ZerReportAdapter.ViewHolder> {

    ArrayList<ZeroReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public ZerReportAdapter(Context context, ArrayList<ZeroReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_zeroreport_list, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_date, txtob, txt_expense, txt_cashsales, txt_acccash,txt_ttlreturn,txt_totlacash,txt_zcode;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        ZeroReport tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_date = (TextView) v.findViewById(R.id.txt_invoiceNo);
            txtob = (TextView) v.findViewById(R.id.txt_devicename);
            txt_expense = (TextView) v.findViewById(R.id.txtexpense);
            txt_cashsales = (TextView) v.findViewById(R.id.txtttl_cashsales);
            txt_acccash = (TextView) v.findViewById(R.id.txtacccash);
            txt_ttlreturn = (TextView) v.findViewById(R.id.txtttlreturn);
            txt_totlacash = (TextView) v.findViewById(R.id.txttotalcash);
            txt_zcode = (TextView) v.findViewById(R.id.txt_itemNo);


        }

        public void setData(ZeroReport table, int pos) {
            this.tablevalue = table;
            txt_date.setText(tablevalue.getDate());
            txtob.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getOpeningbal()),decimal_check));
            txt_expense.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getExpense()),decimal_check));
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_cashsales.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getCashales()),decimal_check));
            txt_acccash.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAccountcash()),decimal_check));
            txt_ttlreturn.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTotalreturn()),decimal_check));
            txt_totlacash.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTotalcash()),decimal_check));
            txt_zcode.setText(tablevalue.getZcode());


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

    public void updateList(ArrayList<ZeroReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
