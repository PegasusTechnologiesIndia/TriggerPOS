package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.ContactReport;

import java.util.ArrayList;

public class ContactReportAdapter extends RecyclerView.Adapter<ContactReportAdapter.ViewHolder>
{
    Context mContext;
    ArrayList<ContactReport> mValues;

    public ContactReportAdapter(Context mContext, ArrayList<ContactReport> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }



    @NonNull
    @Override
    public ContactReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_contactreport_list, parent, false);

        return new ContactReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactReportAdapter.ViewHolder holder, int position) {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txt_srno, txt_contactcode,txt_title,txt_name,
                txt_gender,txt_dob,txt_contact1,txt_email,txt_creditlimit,txt_gstn;
        ContactReport tablevalue;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            txt_srno=itemView.findViewById(R.id.txt_itemNo);
            txt_contactcode=itemView.findViewById(R.id.txt_invoiceNo);
            txt_title=itemView.findViewById(R.id.txt_title);
            txt_name=itemView.findViewById(R.id.txt_taxname);
            txt_gender=itemView.findViewById(R.id.txt_gernder);
            txt_dob=itemView.findViewById(R.id.txt_devicename);
            txt_contact1=itemView.findViewById(R.id.txt_contact1);
            txt_email=itemView.findViewById(R.id.txt_buyerName);
            txt_creditlimit=itemView.findViewById(R.id.txt_creditLimit);
            txt_gstn=itemView.findViewById(R.id.txt_netprofit);
        }
        public void setData(ContactReport table, int pos)
        {
            this.tablevalue = table;
            txt_srno.setText(pos+1+"");
            txt_contactcode.setText(tablevalue.getContactCode());
            txt_title.setText(tablevalue.getTitle());
            txt_name.setText(tablevalue.getName());
            txt_gender.setText(tablevalue.getGender());
            txt_dob.setText(tablevalue.getDob());
            txt_contact1.setText(tablevalue.getContact1());
            txt_email.setText(tablevalue.getEmail());
            txt_creditlimit.setText(tablevalue.getCreditLimit());
            txt_gstn.setText(tablevalue.getGstn());

        }
    }
}
