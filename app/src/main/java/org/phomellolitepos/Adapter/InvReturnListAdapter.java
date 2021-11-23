package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.phomellolitepos.CustomerReturnListActivity;
import org.phomellolitepos.InvReturnHeaderActivity;
import org.phomellolitepos.InvReturnListActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.CusReturnHeaderActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Returns;

import java.util.ArrayList;

public class InvReturnListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Returns> data;
    String decimal_check;

    public InvReturnListAdapter(Context context,
                                     ArrayList<Returns> list) {
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
        TextView txt_voucher_no, txt_remarks, txt_date,txt_total,txt_inv_no;
        ImageView img_whatsappshare;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.inv_retrn_list_item,
                parent, false);
        Returns resultp = data.get(position);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        txt_total = (TextView) itemView.findViewById(R.id.txt_total);
        txt_inv_no = (TextView) itemView.findViewById(R.id.txt_inv_no);

        txt_voucher_no = (TextView) itemView.findViewById(R.id.txt_voucher_no);
       // txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
        img_whatsappshare=(ImageView)itemView.findViewById(R.id.img_whatsapp);
        try {

                txt_date = (TextView) itemView.findViewById(R.id.txt_date);
                txt_voucher_no.setText(resultp.get_voucher_no());
                //txt_remarks.setText(resultp.get_remarks());
                txt_date.setText(resultp.get_date().substring(0, 10));
                txt_inv_no.setText(resultp.get_order_code());
                txt_total.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_total()), decimal_check));
            }catch(Exception e){
            }

            if (resultp.get_is_post().equals("false")) {
                img_whatsappshare.setVisibility(View.GONE);
                itemView.setBackgroundColor(Color.parseColor("#D3D3D3"));
            }
            if (resultp.get_is_cancel().equals("true")) {
                img_whatsappshare.setVisibility(View.GONE);
                itemView.setBackgroundColor(Color.parseColor("#fb8951"));
            }
            if (resultp.get_is_post().equals("true")) {

                if(Globals.objsettings.get_Is_File_Share().equals("true")){
                    img_whatsappshare.setVisibility(View.VISIBLE);
                }
                else{

                    img_whatsappshare.setVisibility(View.GONE);
                }
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));



            }
        img_whatsappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Returns resultp = data.get(position);
                if (resultp.get_is_post().equals("true")){
                    ((InvReturnListActivity) context).startWhatsapp(resultp.get_voucher_no(),resultp.get_contact_code());
                }
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Returns resultp = data.get(position);
                String operation = "Edit";
                String voucher_no = resultp.get_voucher_no();
                Intent intent = new Intent(context, InvReturnHeaderActivity.class);
                intent.putExtra("voucher_no", voucher_no);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Returns resultp = data.get(position);
                if (resultp.get_is_post().equals("true")){
                    ((InvReturnListActivity) context).setView(resultp.get_voucher_no());
                }
                return true;
            }
        });
        return itemView;
    }
}
