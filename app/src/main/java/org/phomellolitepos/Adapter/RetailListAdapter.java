package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

import org.phomellolitepos.ChangePriceActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class RetailListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    String decimal_check, qty_decimal_check;
    ArrayList<ShoppingCart> data;

    String operation;
    String strOrderCode1;
    Database db;
    SQLiteDatabase database;
    private Comparator<ShoppingCart> comparator;
    String Main_Land;
    public RetailListAdapter(Context context,
                             ArrayList<ShoppingCart> list, String s, String opr,String main_Land) {
        this.context = context;
        data = list;
        operation = s;
        strOrderCode1 = opr;
        Main_Land = main_Land;
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
         RelativeLayout relativeLayout;
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

       // relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rel_retail);


        String mastercode=data.get(i).getMaster_itemcode().toString();
        String itemcode=data.get(i).get_Item_Code().toString();
        if(!data.get(i).getMaster_itemcode().equals("0")) {
            itemView.setBackgroundColor(Color.parseColor("#ffc476")) ;

           // itemView.setBackgroundColor(Color.parseColor("#72dc6c"));
        }
        else if(data.get(i).getMaster_itemcode().equals("0")){
            itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) ;
        }
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
                intent.putExtra("opr", operation);
                intent.putExtra("odr_code", strOrderCode1);
                intent.putExtra("flag", flag);
                intent.putExtra("srno", resultp.get_SRNO());
                intent.putExtra("mastercode", resultp.getMaster_itemcode());
                intent.putExtra("MainLand", Main_Land);
                context.startActivity(intent);
                return false;
            }
        });
        return itemView;
    }
}
