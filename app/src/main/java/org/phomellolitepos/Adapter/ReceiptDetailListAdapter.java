package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.ReceptDetailActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Settings;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class ReceiptDetailListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    String decimal_check, qty_decimal_check;
    ArrayList<Order_Detail> data;
    Database db;
    SQLiteDatabase database;

    public ReceiptDetailListAdapter(Context context,
                                    ArrayList<Order_Detail> list) {
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        TextView txt_item_name, txt_item_qty, txt_price, txt_line_total, txt_tax;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.recept_detail_list_item,
                viewGroup, false);
        Order_Detail resultp = data.get(i);
        db = new Database(context);
        database = db.getWritableDatabase();
        Settings settings = Settings.getSettings(context, database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_qty = (TextView) itemView.findViewById(R.id.txt_item_qty);
        txt_line_total = (TextView) itemView.findViewById(R.id.txt_line_total);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        txt_tax = (TextView) itemView.findViewById(R.id.txt_tax);
        String strItemCode = resultp.get_item_code();
        String strItemName = Order_Detail.getItemName(context, " WHERE Order_detail.item_code  = '" + strItemCode + "'  GROUP By Order_detail.item_code");
        txt_item_name.setText(strItemName);
        txt_item_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(resultp.get_quantity()), qty_decimal_check));
        String line_total;
        line_total = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_line_total()), decimal_check);
        txt_line_total.setText(line_total);
        String sale_price;
        sale_price = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_sale_price()), decimal_check);
        txt_price.setText(sale_price);
        String tax;
        tax = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_tax()), decimal_check);
        txt_tax.setText("+ " + tax + " )");

        return itemView;
    }
}
