package org.phomellolitepos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.UserReport;

import java.util.ArrayList;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.ViewHolder>
{
    public UserReportAdapter( Context mContext,ArrayList<UserReport> mValues) {
        this.mValues = mValues;
        this.mContext = mContext;
    }

    ArrayList<UserReport> mValues;
    Context mContext;
    @NonNull
    @Override
    public UserReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_user_report_list, parent, false);
        return new UserReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReportAdapter.ViewHolder holder, int position)
    {
        holder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount()
    {
      return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView srno,userCode,userName,userEmail,maxDis;
//        UserReport tablevalue;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            srno=itemView.findViewById(R.id.txt_itemNo);
            userCode=itemView.findViewById(R.id.txt_invoiceNo);
            userName=itemView.findViewById(R.id.txt_taxname);
            userEmail=itemView.findViewById(R.id.txt_buyerName);
            maxDis=itemView.findViewById(R.id.txt_netprofit);
        }

        public void setData(UserReport userReport, int position)
        {
            srno.setText(position+1+"");
            userCode.setText(userReport.getUserCode());
            userName.setText(userReport.getUsername());
            userEmail.setText(userReport.getUserEmail());
            maxDis.setText(userReport.getMaxDiscount());
        }
    }
}
