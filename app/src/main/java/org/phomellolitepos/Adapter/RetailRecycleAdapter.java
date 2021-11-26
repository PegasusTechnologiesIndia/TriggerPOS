package org.phomellolitepos.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;

import java.util.ArrayList;
import java.util.Comparator;

public class RetailRecycleAdapter extends RecyclerView.Adapter<RetailRecycleAdapter.MyViewHolder>
{

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
    public RetailRecycleAdapter(Context context,ArrayList<ShoppingCart> list, String s, String opr,String main_Land)
    {
        this.context = context;
        data = list;
        operation = s;
        strOrderCode1 = opr;
        Main_Land = main_Land;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
    //    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.retail_list_item, viewGroup, false);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.retail_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        RelativeLayout relativeLayout;
        final ShoppingCart resultp = data.get(position);
        db = new Database(context);
        database = db.getWritableDatabase();
        try {
            Settings settings = Settings.getSettings(context, database, "");
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }

        // relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rel_retail);


        String mastercode=data.get(position).getMaster_itemcode().toString();
        String itemcode=data.get(position).get_Item_Code().toString();
        if(!data.get(position).getMaster_itemcode().equals("0")) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffc476")) ;

            // itemView.setBackgroundColor(Color.parseColor("#72dc6c"));
        }
        else if(data.get(position).getMaster_itemcode().equals("0")){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) ;
        }
        holder.txt_item_name.setText(resultp.get_Item_Name());
        holder.txt_item_qty.setText(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(resultp.get_Quantity()), qty_decimal_check));
        String sales_price;
        sales_price = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_Sales_Price()), decimal_check);
        holder.txt_item_price.setText(sales_price);
        String line_total;
        line_total = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_Line_Total()), decimal_check);
        holder.txt_total_price.setText(line_total);





        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String flag = "Retail";
                String item_code = resultp.get_Item_Code();
                Intent intent = new Intent(context, ChangePriceActivity.class);
                intent.putExtra("arr_position", position + "");
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
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_item_name, txt_item_qty, txt_item_price, txt_total_price;
        public MyViewHolder(View itemView)
        {
            super(itemView);
                 txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
                 txt_item_qty = (TextView) itemView.findViewById(R.id.txt_item_qty);
                 txt_item_price = (TextView) itemView.findViewById(R.id.txt_item_price);
                 txt_total_price = (TextView) itemView.findViewById(R.id.txt_total_price);

        }
    }
}
