package org.phomellolitepos.Adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Table;

import java.util.ArrayList;

public class TableRecyclerViewAdapter  extends RecyclerView.Adapter<TableRecyclerViewAdapter.ViewHolder> {

    ArrayList<Table_Order> mValues;
    Context mContext;
   String Zoneid;
   ArrayList<Table> resultorder_list;
    protected ItemListener mListener;

    //protected ItemListener mListener;

    public TableRecyclerViewAdapter(Context context, ArrayList<Table_Order> values, ArrayList<Table> orderlist, ItemListener itemListener) {
        mContext = context;
        mValues = values;

      resultorder_list=orderlist;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        public LinearLayout relativeLayout;
        Table_Order tablevalue;

        public ViewHolder(@NonNull View v) {

            super(v);


           textView = v.findViewById(R.id.tv_tablename);


            imageView =  v.findViewById(R.id.img_table);
            relativeLayout =v.findViewById(R.id.relativeLayout);
            v.setOnClickListener(this);
        }
            public void setData (Table_Order table,int pos){
                this.tablevalue = table;
                table = mValues.get(pos);
                textView.setText(table.getTable_name());

                if (table.getNoOfOrder().equals("1")) {
                    imageView.setImageResource(R.drawable.bookedtable);
                    relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
                } else {
                    imageView.setImageResource(R.drawable.unbookedtable);
                    relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                //String table_value= mValues.get(pos).get_table_code();
                // for(  i=0;i<mValues.size();i++) {
          /*  table=mValues.get(i);
            String table_value=table.get_table_code();*/

 /*           if(resultorder_list.size()>0) {
               // for (int j = 0; j < resultorder_list.size(); j++) {

                  // String resultvalue = resultorder_list.get(j).get_table_code();
                    if (resultorder_list.equals(table_value)) {
                        imageView.setImageResource(R.drawable.bookedtable);
                        relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
                    }
                    else{
                        imageView.setImageResource(R.drawable.unbookedtable);
                        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }

                //}


            }
            else{
                imageView.setImageResource(R.drawable.unbookedtable);
                relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }*/
            }


          //  for(int i=resultorder_list.size(); i<mValues.size();i++) {
               /* if(resultorder_list.size()>0) {
                    if ((table.get_table_code().equals(resultorder_list))) {
                        imageView.setImageResource(R.drawable.bookedtable);
                        relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
                    }
                    else{
                        imageView.setImageResource(R.drawable.unbookedtable);
                        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                else{
                    imageView.setImageResource(R.drawable.unbookedtable);
                    relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }*/
                //}
            //}





        /*    if(table.getTable_status().equals("1")){
                imageView.setImageResource(R.drawable.bookedtable);
                relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
            }
            else if(table.getTable_status().equals("0")){
                imageView.setImageResource(R.drawable.unbookedtable);
                relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }*/
  /*          if (resultorder_list.size() > 0) {
                 for(int j=0;j<resultorder_list.size();j++) {

               *//* final Orders resultp = resultorder_list.get(j);
                if (resultp.get_order_status().equals("OPEN")) {*//*
                if(resultorder_list.get(j).get_order_status().equals("OPEN")){
                    imageView.setImageResource(R.drawable.bookedtable);
                    relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
                }
             else if(resultorder_list.get(j).get_order_status().equals("CLOSE")) {
                    imageView.setImageResource(R.drawable.unbookedtable);
                    relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                }
                }
               // imageView.setImageResource(R.drawable.unbookedtable);


            } else {
                imageView.setImageResource(R.drawable.unbookedtable);
                relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }*/


           // relativeLayout.setBackgroundColor(Color.parseColor(item.color));




        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(tablevalue);
            }
        }
    }

    @Override
    public TableRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.table_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(mValues.get(i), i);


        //}


    }
       /* if(resultorder_list.size()>0) {
            if(mValues.get(i).get_table_code().contentEquals(resultorder_list.get(i).get_table_code())){

                viewHolder.imageView.setImageResource(R.drawable.bookedtable);
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
            }
            else{
                viewHolder.imageView.setImageResource(R.drawable.unbookedtable);
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }*/

           /* String table = resultorder_list.get(i).get_table_code();
            if (!table.equals(mValues.get(i).get_table_code())) {
                viewHolder.imageView.setImageResource(R.drawable.unbookedtable);
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            for (int j = 0; j < resultorder_list.size(); j++) {
                if (resultorder_list.get(j).get_table_code().equals(table)) {
                    viewHolder.imageView.setImageResource(R.drawable.bookedtable);
                    viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
                }
            }*/

        /*if(resultorder_list.size()>0) {
            if((resultorder_list.get(i).get_table_code().contains(mValues.get(i).get_table_code()))) {
                viewHolder.imageView.setImageResource(R.drawable.bookedtable);
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
            }
            else{
                viewHolder.imageView.setImageResource(R.drawable.unbookedtable);
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }*/



    @Override
    public int getItemCount() {


        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Table_Order item);
    }
}
