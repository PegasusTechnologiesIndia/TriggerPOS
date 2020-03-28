package org.phomellolitepos.Adapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.phomellolitepos.CheckBoxClass.BusinessGroupCheck;
import org.phomellolitepos.CheckBoxClass.PaymentInvoiceCheck;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pay_Collection_Setup;
import org.phomellolitepos.database.Settings;
import java.util.ArrayList;

/**
 * Created by LENOVO on 10/12/2017.
 */

public class PayColInvoiceListAdapter extends ArrayAdapter<PaymentInvoiceCheck> {
    private final Activity context;
    LayoutInflater inflater;
    String result1;
    ArrayList<PaymentInvoiceCheck> list;
    boolean[] itemChecked;

    static class ViewHolder {
        private TextView txt_invoice_no;
        private TextView txt_invoice_date;
        private TextView txt_amount;
        private CheckBox ch_pay_invoice;
    }

    public PayColInvoiceListAdapter(Activity context, ArrayList<PaymentInvoiceCheck> list) {
        super(context, R.layout.pay_collection_invoice_list_item, list);
        this.context = context;
        this.list = list;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        PayColInvoiceListAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.pay_collection_invoice_list_item, null);
            viewHolder = new PayColInvoiceListAdapter.ViewHolder();
            viewHolder.txt_invoice_no = (TextView) convertView.findViewById(R.id.txt_invoice_no);
            viewHolder.txt_invoice_date = (TextView) convertView.findViewById(R.id.txt_invoice_date);
            viewHolder.txt_amount = (TextView) convertView.findViewById(R.id.txt_amount);
            viewHolder.ch_pay_invoice = (CheckBox) convertView.findViewById(R.id.ch_pay_invoice);
            viewHolder.ch_pay_invoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.txt_invoice_no, viewHolder.txt_invoice_no);
            convertView.setTag(R.id.txt_invoice_date, viewHolder.txt_invoice_date);
            convertView.setTag(R.id.txt_amount, viewHolder.txt_amount);
            convertView.setTag(R.id.ch_pay_invoice, viewHolder.ch_pay_invoice);
        } else {
            viewHolder = (PayColInvoiceListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.ch_pay_invoice.setTag(position); // This line is important.

        viewHolder.txt_invoice_no.setText(list.get(position).getInvoice_no());
        viewHolder.txt_invoice_date.setText(list.get(position).getInvoice_date());
        viewHolder.txt_amount.setText(list.get(position).getAmount());
        viewHolder.ch_pay_invoice.setChecked(list.get(position).isSelected());

        return convertView;
    }
}
