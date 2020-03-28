package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.ExpensesActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Pos_Balance;

/**
 * Created by Neeraj on 4/25/2017.
 */

public class ExpensesListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Pos_Balance> data;
    String decimal_check;

    public ExpensesListAdapter(Context context,
                               ArrayList<Pos_Balance> list) {
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
        TextView txt_expense_name, txt_expense_amount;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.expenses_list_item,
                parent, false);
        Pos_Balance resultp = data.get(position);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_expense_name = (TextView) itemView.findViewById(R.id.txt_expense_name);
        txt_expense_amount = (TextView) itemView.findViewById(R.id.txt_expense_amount);
        txt_expense_name.setText(resultp.get_remarks());
        String expense_amount = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_amount()), decimal_check);
        txt_expense_amount.setText(expense_amount);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Pos_Balance resultp = data.get(position);
                String operation = "Edit";
                String code = resultp.get_pos_balance_code();
                Intent intent = new Intent(context, ExpensesActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);
            }
        });
        return itemView;
    }
}
