package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.database.sqlite.SQLiteDatabase;
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
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Return_Item_Tax;
import org.phomellolitepos.database.Tax_Master;

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
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    SQLiteDatabase database;
    Database db;
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
        try {
            db = new Database(context);
            database = db.getWritableDatabase();
        }
        catch(Exception e){

        }
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

       String item_code= resultp.getItem_code();
        item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + item_code + "'", database, db);
        Double iTax = 0d;
        Double iTaxTotal = 0d;
        //      if (taxIdFinalAarry.size() > 0) {
        for (int i = 0; i < item_group_taxArrayList.size(); i++) {
            iTax = 0d;
            Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
            if(tax_master!=null) {
                Double iPrice = Double.parseDouble(resultp.getPrice());
                if (tax_master.get_tax_type().equals("P")) {
                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                } else {
                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                }

                Return_Item_Tax return_item_tax = new Return_Item_Tax(context, "", "", Globals.SRNO + "", resultp.getItem_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                Globals.return_item_tax.add(return_item_tax);
            }
            else{}
        }
        // }
       /* Double sprice = Double.parseDouble(sale_priceStr) + iTaxTotal;
        Globals.TotalSlesPrice_CustomerDisplay = Double.parseDouble(sprice + "");*/

        //((CusReturnFinalActivity) context).setQuantityPrice(data.get(position).getQty(), data.get(position).getLine_total());
        /*Globals.ReturnTotalPrice =  (Double.parseDouble(resultp.getLine_total())) * Double.parseDouble(resultp.getQty()));
        Globals.ReturnTotalQty =  Double.parseDouble(resultp.getQty()) ;*/


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

                        String str_update= "DELETE";
                        StockAdjectmentDetailList resultp = data.get(position);
                        ((CusReturnFinalActivity) context).setTextView(position+"",resultp.getQty(),resultp.getPrice(),str_update);
                        data.remove(position);
                        notifyDataSetChanged();
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
