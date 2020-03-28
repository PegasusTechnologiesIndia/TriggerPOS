package org.phomellolitepos.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.phomellolitepos.MainActivity;
import org.phomellolitepos.PaymentSplitActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.SplitPaymentList;

import java.util.ArrayList;

public class PaymentSplitListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<SplitPaymentList> data;
    String decimal_check, txtAmount;
    ListView listView;

    public PaymentSplitListAdapter(Context context,
                                   ArrayList<SplitPaymentList> list, String s, ListView list1) {
        this.context = context;
        data = list;
        txtAmount = s;
        listView = list1;
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView txt_bussiness_gp_name, txt_bussiness_gp_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.bussiness_group_list_item,
                viewGroup, false);
        SplitPaymentList resultp = data.get(i);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {

            decimal_check = "1";
        }
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        txt_bussiness_gp_code.setTextColor(Color.parseColor("#333333"));
        txt_bussiness_gp_name.setText(resultp.getPayment_Name());
        txt_bussiness_gp_code.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.getAmount()), decimal_check));

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SplitPaymentList resultp = data.get(i);
                Double finalAmount = (Double.parseDouble(txtAmount) + Double.parseDouble(resultp.getAmount()));
                data.remove(i);
                Globals.splitPsyMd.remove(i);
                ((PaymentSplitActivity) context).setTextView(finalAmount + "");
                return true;
            }
        });

        return itemView;
    }
}
