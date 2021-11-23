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

import java.util.ArrayList;


// Sales By Item Report adapter
public class OrderDetailReport_Adapter extends RecyclerView.Adapter<OrderDetailReport_Adapter.ViewHolder> {

    ArrayList<OrderDetail_Report> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public OrderDetailReport_Adapter(Context context, ArrayList<OrderDetail_Report> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderdeail_report_list, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_itemcategory, txtitemcode, txt_itemname, txt_ordertype, txt_quantity,txt_itemamount,txt_grosamount,txt_discamnt,txtamountafterdisc, txt_netamnt;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        OrderDetail_Report tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_itemcategory = (TextView) v.findViewById(R.id.ttl_itemcategory);
            txtitemcode = (TextView) v.findViewById(R.id.txt_taxtype);
            txt_itemname = (TextView) v.findViewById(R.id.txtitemname);
            txt_ordertype = (TextView) v.findViewById(R.id.txtttl_ordertype);
            txt_quantity = (TextView) v.findViewById(R.id.txtquantity);
            txt_itemamount = (TextView) v.findViewById(R.id.txtitemamount);
            txt_grosamount = (TextView) v.findViewById(R.id.txtgrossamnt);
            txt_discamnt = (TextView) v.findViewById(R.id.txtdiscountamount);
            txtamountafterdisc = (TextView) v.findViewById(R.id.txtamntafterdis);
            txt_netamnt = (TextView) v.findViewById(R.id.txtnetamnt);

        }

        public void setData(OrderDetail_Report tablevalue, int pos) {
          //  this.tablevalue = table;
            txt_itemcategory.setText(tablevalue.getItemCategory());
            txtitemcode.setText(tablevalue.getItemcode());
            txt_itemname.setText(tablevalue.getItemname());
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_ordertype.setText(tablevalue.getOrdertype());

            txt_quantity.setText(tablevalue.getQuantity());
            if(tablevalue.getSaleprice()==null)
                txt_itemamount.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            else
            txt_itemamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getSaleprice()),decimal_check));

            if(tablevalue.getGrossamount()==null)
                txt_grosamount.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            else
            txt_grosamount.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getGrossamount()),decimal_check));

            if(tablevalue.getDiscountamount()==null)
                txt_discamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            else
            txt_discamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscountamount()),decimal_check));

            if(tablevalue.getAmountafterdisc()==null)
               txtamountafterdisc.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            else
               txtamountafterdisc.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmountafterdisc()),decimal_check));

            if(tablevalue.getNetamount()==null)
                txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble("0"),decimal_check));
            else
            txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetamount()),decimal_check));


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

    public void updateList(ArrayList<OrderDetail_Report> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
