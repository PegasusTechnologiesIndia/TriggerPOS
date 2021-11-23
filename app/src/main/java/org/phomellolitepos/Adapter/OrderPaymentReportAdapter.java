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
import org.phomellolitepos.database.OrderDetail_Report;
import org.phomellolitepos.database.OrderPayment_Report;
import org.phomellolitepos.database.Order_Payment;

import java.util.ArrayList;

public class OrderPaymentReportAdapter extends RecyclerView.Adapter<OrderPaymentReportAdapter.ViewHolder> {

    ArrayList<OrderPayment_Report> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public OrderPaymentReportAdapter(Context context, ArrayList<OrderPayment_Report> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_payment_reportlist, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_invno, txtdatetime, txt_payment, txt_saleamountaftdis, txt_netamount,txt_payamount,txt_grosamount,txt_discamnt,txtamountafterdisc, txt_netamnt;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        OrderPayment_Report tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_invno = (TextView) v.findViewById(R.id.ttl_invno);
            txtdatetime = (TextView) v.findViewById(R.id.txtdatetime);
            txt_payment = (TextView) v.findViewById(R.id.txtpaymethod);
            txt_saleamountaftdis = (TextView) v.findViewById(R.id.txtttl_saleamntafterdisc);
            txt_netamount = (TextView) v.findViewById(R.id.txtnetamnt);
            txt_payamount = (TextView) v.findViewById(R.id.txtpayamount);


        }

        public void setData(OrderPayment_Report table, int pos) {
            this.tablevalue = table;
            txt_invno.setText(tablevalue.getInvoicenumber());
            txtdatetime.setText(tablevalue.getDate() +  "  " + tablevalue.getTime());
            txt_payment.setText(tablevalue.getPaymentmethod());
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_saleamountaftdis.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getSalesamountafterdisc()),decimal_check));
            txt_netamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetamount()),decimal_check));
            txt_payamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getPaymaount()),decimal_check));


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

    public void updateList(ArrayList<OrderPayment_Report> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
