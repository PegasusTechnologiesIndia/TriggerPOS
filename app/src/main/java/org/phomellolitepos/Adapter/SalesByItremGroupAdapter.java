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
import org.phomellolitepos.database.MonthlySalesReport;
import org.phomellolitepos.database.SalesByItemGroup;

import java.util.ArrayList;

public class SalesByItremGroupAdapter extends RecyclerView.Adapter<SalesByItremGroupAdapter.ViewHolder> {

    ArrayList<SalesByItemGroup> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public SalesByItremGroupAdapter(Context context, ArrayList<SalesByItemGroup> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_list_itemview, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_itemcategory, txt_saleprice, txt_quantity, txt_grossamnt, txt_discamnt,txt_amntafterdisc;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        SalesByItemGroup tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_itemcategory = (TextView) v.findViewById(R.id.ttl_itemcategory);
            txt_saleprice = (TextView) v.findViewById(R.id.txtsaleprice);
            txt_quantity = (TextView) v.findViewById(R.id.txtquantity);
            txt_grossamnt = (TextView) v.findViewById(R.id.txtgrossamnt);
            txt_discamnt = (TextView) v.findViewById(R.id.txtdiscamnt);
            txt_amntafterdisc = (TextView) v.findViewById(R.id.txtamntafterdis);

        }

        public void setData(SalesByItemGroup table, int pos) {
            this.tablevalue = table;
            txt_itemcategory.setText(tablevalue.getItemcategory());
            txt_saleprice.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getSaleprice()),decimal_check));
            txt_quantity.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getQuantity()),decimal_check));
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_grossamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getGrossamount()),decimal_check));
            txt_discamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscamnt()),decimal_check));
            txt_amntafterdisc.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getAmountafterdis()),decimal_check));
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

    public void updateList(ArrayList<SalesByItemGroup> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
