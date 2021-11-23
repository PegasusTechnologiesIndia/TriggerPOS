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
import org.phomellolitepos.database.SalesByItemReport;

import java.util.ArrayList;

public class SalesByItemAdapter extends RecyclerView.Adapter<SalesByItemAdapter.ViewHolder> {

    ArrayList<SalesByItemReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public SalesByItemAdapter(Context context, ArrayList<SalesByItemReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_salesbyitem_list, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_invnumber, txt_datetime, txt_itemcategory, txt_itemcode,txt_discamnt, txt_itemname,txt_saleprice,txt_quantity,txt_grossamnt,txt_netamnt;
        LinearLayout ll_vehdate, ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        SalesByItemReport tablevalue;
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
            txt_itemcategory = (TextView) v.findViewById(R.id.txtitemcategory);
            txt_itemcode = (TextView) v.findViewById(R.id.txt_taxtype);
            txt_itemname = (TextView) v.findViewById(R.id.txtitemname);
            txt_saleprice = (TextView) v.findViewById(R.id.txtsaleprice);
            txt_quantity = (TextView) v.findViewById(R.id.txtquantity);
            txt_grossamnt = (TextView) v.findViewById(R.id.txtgrossamnt);
            txt_netamnt = (TextView) v.findViewById(R.id.txtnetamnt);
            txt_discamnt = (TextView) v.findViewById(R.id.txtdiscamnt);

        }

        public void setData(SalesByItemReport table, int pos) {
            this.tablevalue = table;
            txt_invnumber.setText(tablevalue.getInvoicenumber());
            txt_datetime.setText(tablevalue.getDate() + "  " + tablevalue.getTime());
            txt_itemcategory.setText(tablevalue.getItemcategory());
            //   txt_taxamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getTaxAmnt()),decimal_check));
            txt_itemcode.setText(tablevalue.getItemcode());
            txt_itemname.setText(tablevalue.getItemname());
            txt_saleprice.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getSaleprice()),decimal_check));
            txt_quantity.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getQuantity()),decimal_check));
            txt_grossamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getGrossamount()),decimal_check));
            txt_netamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getNetamount()),decimal_check));
            txt_discamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(tablevalue.getDiscamount()),decimal_check));


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

    public void updateList(ArrayList<SalesByItemReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}
