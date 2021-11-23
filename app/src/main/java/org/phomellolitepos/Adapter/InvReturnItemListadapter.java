package org.phomellolitepos.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.InvReturnFinalActivity;
import org.phomellolitepos.InvReturnHeaderActivity;
import org.phomellolitepos.InvReturnItemListActivity;
import org.phomellolitepos.InvReturnListActivity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Returns;

import java.util.ArrayList;

import in.gauriinfotech.commons.Progress;

public class InvReturnItemListadapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Object> itemcodelist;
    ArrayList<Object> qtylist;
    String decimal_check;
    ArrayList<Object>  item_name;
    ArrayList<Object>  returnqty_list;
    ArrayList<Object>  Price_list;

    public InvReturnItemListadapter(Context context,
                                    ArrayList<Object> itemcode,ArrayList<Object> itemname,ArrayList<Object>quantity,ArrayList<Object> returnqtylist,ArrayList<Object> price_list) {
        this.context = context;
        itemcodelist = itemcode;
        item_name=itemname;
        qtylist=quantity;
        returnqty_list=returnqtylist;
        Price_list=price_list;
    }

    @Override
    public int getCount() {
        return itemcodelist.size();
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
        TextView txt_qty, txt_temcode, txt_itemname, txt_total, txt_inv_no;
        Returns returns;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.inv_return_listitemcode,
                parent, false);
       // Order_Detail resultp = data.get(position);

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_qty = (TextView) itemView.findViewById(R.id.txt_qty);
        txt_temcode = (TextView) itemView.findViewById(R.id.txt_itemcode);

        txt_itemname = (TextView) itemView.findViewById(R.id.txt_itemname);


        try {

            String strQty = Globals.myNumberFormat2Price(Double.parseDouble(qtylist.get(position).toString()), decimal_check);
            String strReturnQty = Globals.myNumberFormat2Price(Double.parseDouble(returnqty_list.get(position).toString()), decimal_check);
            // Double updReturnQty = Double.parseDouble(strQty) - Double.parseDouble(strReturnQty);
       // if(!strQty.equals("0")) {
         txt_itemname.setText(item_name.get(position).toString());
         txt_temcode.setText(itemcodelist.get(position).toString());
         txt_qty.setText(strQty.toString());
          /*}
     else if(strQty.equals("0"))
     {
      item_name.remove(position);
       itemcodelist.remove(position);
       qtylist.remove(position);
    notifyDataSetChanged();
      }*/
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String  itemcode=itemcodelist.get(position).toString();
                String quantity=qtylist.get(position).toString();
                String itemname=item_name.get(position).toString();
                String returnqty=returnqty_list.get(position).toString();
                String price=Price_list.get(position).toString();
                String strtotalQty = Globals.myNumberFormat2Price(Double.parseDouble(quantity), decimal_check);
                String strreturnQty = Globals.myNumberFormat2Price(Double.parseDouble(returnqty), decimal_check);
                String strprice = Globals.myNumberFormat2Price(Double.parseDouble(price), decimal_check);

                boolean bFlag =  false;
               for(int i=0;i<Globals.data.size();i++){
                   
                   if(Globals.data.get(i).getItem_code().equals(itemcode))
                   {
                       bFlag= true;
                   }
               }
               if(!bFlag)
                {
            
                ((InvReturnItemListActivity) context).callIntent( itemcode,itemname,strtotalQty,strreturnQty,strprice);
            
               // bFlag=false;
                }
               else
                {

                    AlertDialog alertDialog1 = new AlertDialog.Builder(
                            context).create();

                    // Setting Dialog Title
                    alertDialog1.setTitle("Invoice return");

                    // Setting Dialog Message
                    alertDialog1.setMessage("Item Already Present in List");

                    // Setting Icon to Dialog
                  //  alertDialog1.setIcon(R.drawable.tick);

                    // Setting OK Button
                    alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            // closed
                           // Toast.makeText(context, "Item Already Present in List", Toast.LENGTH_SHORT).show();
                          //  bFlag=false;
                        }
                    });

                    // Showing Alert Message
                    alertDialog1.show();
                   /* Thread thread = new Thread(){
                        public void run(){
                            Looper.prepare();//Call looper.prepare()

                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    Toast.makeText(context, "Item Already Present in List", Toast.LENGTH_SHORT).show();
                                }
                            };

                            Looper.loop();
                        }
                    };
                    thread.start();*/

                    //Toast.makeText(context, "Item Already Present in List", Toast.LENGTH_SHORT).show();
                }
            }
        });

       /* itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              //  Order_Detail resultp = data.get(position);
                if (resultp.get_is_post().equals("true")) {
                    ((InvReturnListActivity) context).setView(resultp.get_voucher_no());
                }
                return true;
            }
        });*/
        return itemView;
    }
}
