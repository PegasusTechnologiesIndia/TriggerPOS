package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.R;
import org.phomellolitepos.CusReturnFinalActivity;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.Util.Globals;

import java.util.ArrayList;

/**
 * Created by LENOVO on 4/6/2018.
 */

public class CusReturnFinalListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    String decimal_check;
    ArrayList<StockAdjectmentDetailList> data;

    public CusReturnFinalListAdapter(Context context,
                                     ArrayList<StockAdjectmentDetailList> list) {
        this.context = context;
        data = list;
    }

    @Override
    public int getCount() {
        return data.size();
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
        final TextView txt_item_name, txt_item_code, txt_price;
        ImageView delete;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.main_list_item,
                parent, false);
        final StockAdjectmentDetailList resultp = data.get(position);
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_code = (TextView) itemView.findViewById(R.id.txt_item_code);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
      //  delete = (ImageView) itemView.findViewById(R.id.delete);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_item_name.setText(resultp.getItem_name());
        txt_item_code.setText(resultp.getItem_code() + " ("+ resultp.getQty() + "*" +resultp.getPrice() + ")");
        txt_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.getLine_total()), decimal_check));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_update= "update";
                StockAdjectmentDetailList resultp = data.get(position);
                ((CusReturnFinalActivity) context).setTextView(position+"",resultp.getItem_code(),resultp.getItem_name(),resultp.getQty(),resultp.getPrice(),str_update);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setTitle(resultp.getItem_name());
                alertDialog.setMessage("Are you sure you want delete this?");
                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        data.remove(position);
                    notifyDataSetChanged();
                        String str_update= "DELETE";
                        ((CusReturnFinalActivity) context).setTextView();

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
/*delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        data.remove(position);
        notifyDataSetChanged();
    }
});*/

        return itemView;
    }

    public void showdialog(){

    }
}
