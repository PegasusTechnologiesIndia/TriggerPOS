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

import org.phomellolitepos.Adapter.VehicleHistoryAdapter;
import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.VehicleReport;
import org.phomellolitepos.printer.PrintLayout;

import java.util.ArrayList;

public class VehicleIn_ReportAdapter extends RecyclerView.Adapter<VehicleIn_ReportAdapter.ViewHolder> {

    ArrayList<VehicleReport> mValues;
    Context mContext;
    String Zoneid;

    String decimal_check;

    public VehicleIn_ReportAdapter(Context context, ArrayList<VehicleReport> values) {
        mContext = context;
        mValues = values;

    }


    @NonNull
    @Override
    public VehicleIn_ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_report_vehicleinlist, parent, false);

        return new VehicleIn_ReportAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_vehtype, txt_vehdate, txt_totalcount, txt_fareamnt, txt_username;
        LinearLayout ll_vehdate,ll_vehusername;
        Button btnPrint;
        ImageView img_status;
        CardView cardview;
        VehicleReport tablevalue;
        LinearLayout ll_rfid;

        public ViewHolder(View v) {

            super(v);
            try {
                decimal_check = Globals.objLPD.getDecimal_Place();

            } catch (Exception ex) {
                decimal_check = "1";
            }

            txt_vehtype = (TextView) v.findViewById(R.id.txt_vehicletype);
            txt_vehdate = (TextView) v.findViewById(R.id.txt_date);
            txt_totalcount = (TextView) v.findViewById(R.id.txt_totalcount);
            txt_fareamnt = (TextView) v.findViewById(R.id.txt_fareamnt);
            txt_username = (TextView) v.findViewById(R.id.txt_username);
            ll_vehdate=(LinearLayout)v.findViewById(R.id.ll_vehdate);
            ll_vehusername=(LinearLayout)v.findViewById(R.id.ll_username);





        }

        public void setData(VehicleReport table, int pos) {
            this.tablevalue = table;
            txt_vehtype.setText(table.getVtype());
            if(!table.getVdate().equals("")) {

              ll_vehdate.setVisibility(View.VISIBLE);
                txt_vehdate.setText(table.getVdate());
            }
            else{
                ll_vehdate.setVisibility(View.GONE);
            }
            txt_totalcount.setText(table.getVcount());
            txt_fareamnt.setText(table.getVprice());
            if(!table.getVusername().equals("")) {
                ll_vehusername.setVisibility(View.VISIBLE);
                txt_username.setText(table.getVusername());
            }
            else{
                ll_vehusername.setVisibility(View.GONE);
            }
            //table=mValues.get(pos);
/*            txt_orderno.setText(table.getOrdercode());
            if (table.getVehicleno().equals("")) {
                txt_vehicleno.setText("NA");
            } else {
                txt_vehicleno.setText(table.getVehicleno());
            }

            if (table.getMobileno().equals("")) {
                txt_mobileno.setText("NA");
            } else {
                txt_mobileno.setText(table.getMobileno());
            }
            txt_intym.setText(table.getInTime());
            txt_vehstatus.setText(table.getVehiclestatus());
            txt_advamnt.setText(Globals.myNumberFormat2Price(Double.parseDouble(table.getAdvanceamnt()), decimal_check));
            txt_amnt.setText(table.getAmount());
            if (table.getRfid() == null) {
                ll_rfid.setVisibility(View.GONE);
            } else {
                ll_rfid.setVisibility(View.VISIBLE);
                txt_rfid.setText(table.getRfid());
            }
            if (table.getVehiclestatus().equals("OPEN")) {
                img_status.setBackgroundResource(R.drawable.vehicle_in);
            } else {
                img_status.setBackgroundResource(R.drawable.vehicle_out);
            }*/


        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleIn_ReportAdapter.ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateList(ArrayList<VehicleReport> list) {
        mValues = list;
        notifyDataSetChanged();
    }
}