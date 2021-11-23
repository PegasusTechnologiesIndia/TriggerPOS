package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
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
import org.phomellolitepos.database.Table;
import org.phomellolitepos.printer.PrintLayout;

import java.util.ArrayList;

public class VehicleOutAdapter extends RecyclerView.Adapter<VehicleOutAdapter.ViewHolder> {

    ArrayList<Vehicle_Order> mValues;
    Context mContext;
    String Zoneid;
    ArrayList<Table> resultorder_list;
    protected VehicleOutAdapter.ItemListener mListener;
 //   protected VehicleOutAdapter.ItemListener mListener;
    String decimal_check;
    public VehicleOutAdapter(Context context, ArrayList<Vehicle_Order> values, VehicleOutAdapter.ItemListener itemListener) {
        mContext = context;
        mValues = values;
        mListener=itemListener;
    }


    @NonNull
    @Override
    public VehicleOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vehicleout_list, parent, false);

        return new VehicleOutAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_vehicleno,txt_mobileno,txt_intym,txt_vehstatus,txt_advamnt,txt_amnt,txt_orderno,txt_rfid;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        Vehicle_Order tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_vehicleno = (TextView) v.findViewById(R.id.txt_vehicleno);
            txt_mobileno = (TextView) v.findViewById(R.id.txt_mobno);
            txt_intym = (TextView) v.findViewById(R.id.txt_intime);
            txt_vehstatus = (TextView) v.findViewById(R.id.txt_vehstatus);

            txt_orderno = (TextView) v.findViewById(R.id.txt_orderno);

            cardview = (CardView) v.findViewById(R.id.cardView);

            ll_rfid = (LinearLayout) v.findViewById(R.id.ll_rfid);
            txt_rfid = (TextView) v.findViewById(R.id.txt_rfid);

            cardview.setBackgroundResource(R.drawable.cardview_border);

            v.setOnClickListener(this);

        }
        public void setData(Vehicle_Order table, int pos) {
            this.tablevalue = table;
            //table=mValues.get(pos);
            txt_orderno.setText(table.getOrdercode());
            if (table.getVehicleno()==null) {
                txt_vehicleno.setText("NA");
            } else {
                txt_vehicleno.setText(table.getVehicleno());
            }

            if (table.getMobileno()==null) {
                txt_mobileno.setText("NA");
            } else {
                txt_mobileno.setText(table.getMobileno());
            }
            txt_intym.setText(table.getInTime());
            txt_vehstatus.setText(table.getVehiclestatus());
            // txt_advamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(table.getAdvanceamnt()),decimal_check));
            // txt_amnt.setText(table.getAmount());
            if (table.getRfid() == null || table.getRfid().equals("")) {
                ll_rfid.setVisibility(View.GONE);
            } else {
                ll_rfid.setVisibility(View.VISIBLE);
                txt_rfid.setText(table.getRfid());
            }
        }

            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(tablevalue);
                }
            }

    }

    @Override
    public void onBindViewHolder(@NonNull VehicleOutAdapter.ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public void updateList(ArrayList<Vehicle_Order> list){
        mValues = list;
        notifyDataSetChanged();
    }

    public interface ItemListener {
        void onItemClick(Vehicle_Order item);
    }
}
