package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/13/2018.
 */

public class MainListLandScapAdapter extends BaseAdapter{

    Context context;
    LayoutInflater layoutInflater;
    String decimal_check, qty_decimal_check;
    ArrayList<ShoppingCart> data;
    String operation;
    String strOrderCode1;
    Database db;
    SQLiteDatabase database;
    String opr;

    public MainListLandScapAdapter(Context context,
                             ArrayList<ShoppingCart> list,String operation) {
        this.context = context;
        data = list;
        opr = operation;
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

        TextView txt_item_name, txt_item_qty, txt_item_price, txt_total_price;

        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = layoutInflater.inflate(R.layout.retail_list_item,
                viewGroup, false);
        final ShoppingCart resultp = data.get(i);
        db = new Database(context);
        database = db.getWritableDatabase();

        try {
            Settings settings = Settings.getSettings(context, database, "");
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_qty = (TextView) itemView.findViewById(R.id.txt_item_qty);
        txt_item_price = (TextView) itemView.findViewById(R.id.txt_item_price);
        txt_total_price = (TextView) itemView.findViewById(R.id.txt_total_price);
        txt_item_name.setText(resultp.get_Item_Name());
        txt_item_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(resultp.get_Quantity()), qty_decimal_check));
        String sales_price;
        sales_price = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_Sales_Price()), decimal_check);
        txt_item_price.setText(sales_price);
        String line_total;
        line_total = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_Line_Total()), decimal_check);
        txt_total_price.setText(line_total);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String flag = "Retail";
                String item_code = resultp.get_Item_Code();
                Intent intent = new Intent(context, ChangePriceActivity.class);
                intent.putExtra("arr_position", i + "");
                intent.putExtra("item_code", item_code);
                intent.putExtra("opr", opr);
                intent.putExtra("odr_code", strOrderCode1);
                intent.putExtra("flag", flag);
                context.startActivity(intent);
                return false;
            }
        });
        return itemView;
    }
}
