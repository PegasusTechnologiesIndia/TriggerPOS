package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.phomellolitepos.InvReturnFinalActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.Globals;

import java.util.ArrayList;

public class InvReturnFinalListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    String decimal_check;

  // ArrayList<StockAdjectmentDetailList> data;

    Gson gson;
    public InvReturnFinalListAdapter(Context context,
                                     ArrayList<StockAdjectmentDetailList> list) {
        this.context = context;
        Globals.data = list;
    }

    @Override
    public int getCount() {
        return Globals.data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {



        ViewHolder viewHolder;
        if (convertView == null) {


           inflater = (LayoutInflater) context
                   .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           // convertView = null;

           convertView = inflater.inflate(R.layout.inv_return_final_listitem,
                   parent, false);

      /*  if (convertView == null) {
            // your code
        }else{
            inflater = (LayoutInflater) convertView.getTag();
        }*/

            viewHolder = new ViewHolder();
           viewHolder.txt_item_name = (TextView) convertView.findViewById(R.id.txt_item_name);
           viewHolder.txt_item_code = (TextView) convertView.findViewById(R.id.txt_item_code);
           viewHolder.txt_price = (TextView) convertView.findViewById(R.id.txt_price);
           viewHolder.txt_qty = (TextView) convertView.findViewById(R.id.txt_qty);
           convertView.setTag(viewHolder);
       }
       else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
            final StockAdjectmentDetailList resultp = Globals.data.get(position);
        try {
            if (resultp.getItem_name().length()>30){
                viewHolder.txt_item_name.setText(resultp.getItem_name().substring(0,30));
            }else {
                viewHolder.txt_item_name.setText(resultp.getItem_name());
            }
        }catch (Exception ex){}

       // for(int i=0;i<Globals.data.size();i++) {
        viewHolder.txt_item_code.setText(resultp.getItem_code());
        viewHolder.txt_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.getLine_total()), decimal_check));
        viewHolder.txt_qty.setText("Qty :" + Globals.myNumberFormat2Price(Double.parseDouble(resultp.getQty()), decimal_check));
            String qty = Globals.myNumberFormat2Price(Double.parseDouble(resultp.getQty()), decimal_check);
            String totalPrice = Globals.myNumberFormat2Price(Double.parseDouble(resultp.getLine_total()), decimal_check);


            //   Globals.data.add(resultp);
       // }
      /*  prefrences = context.getSharedPreferences("MyPrefrences", Context.MODE_PRIVATE);
        edtor = prefrences.edit();
        gson = new Gson();
      ///  String json = gson.toJson();

        edtor.putString(context.getString(R.string.itemnamepref), resultp.getItem_name());
        edtor.putString(context.getString(R.string.itemqtypref), resultp.getQty());
        edtor.commit();*/
       /* Globals.ReturnTotalQty=Globals.ReturnTotalQty + Double.parseDouble(qty);
        Globals.ReturnTotalPrice=Globals.ReturnTotalPrice + Double.parseDouble(totalPrice);
        ((InvReturnFinalActivity) context).setQuantityPrice();*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_update= "update";
                StockAdjectmentDetailList resultp = Globals.data.get(position);
                ((InvReturnFinalActivity) context).setTextView(position+"",resultp.getItem_code(),resultp.getItem_name(),resultp.getQty(),resultp.getPrice(),str_update);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setTitle(resultp.getItem_name());
                alertDialog.setMessage("Are you sure you want delete this?");
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                       Globals.data.remove(position);

                        notifyDataSetChanged();
                        String str_update= "DELETE";
                        float itprice=Float.parseFloat(resultp.getPrice());
                        float itqty=Float.parseFloat(resultp.getQty());
                        float totalprice = itqty * itprice;
                        ((InvReturnFinalActivity) context).setTextView(resultp.getQty(),String.valueOf(totalprice));

                    }
                });


                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
               /* data.remove(position);
                notifyDataSetChanged();*/
                return true;
            }
        });

        return convertView;
    }


    static class ViewHolder {
        private TextView txt_item_name;
        private TextView  txt_item_code;
        private TextView txt_price;
        private TextView txt_qty;
    }
}
