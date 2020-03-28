package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.SearchOrder.SearchOrders;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.printer.PrintLayout;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/15/2018.
 */

public class SearchOrderServerListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<SearchOrders> data;
    String decimal_check;
    Settings settings;
    SQLiteDatabase database;
    Database db;
    Orders orders;
//    PrintDirect printDirect = new PrintDirect();

    public SearchOrderServerListAdapter(Context context,
                                        ArrayList<SearchOrders> list) {
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView txt_item_name, txt_item_code, txt_price;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.main_list_item,
                viewGroup, false);
        SearchOrders resultp = data.get(position);
        txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
        txt_item_code = (TextView) itemView.findViewById(R.id.txt_item_code);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        db = new Database(context);
        database = db.getWritableDatabase();
        settings = Settings.getSettings(context, database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_item_name.setText(resultp.getOrder_Code());
        txt_item_code.setText(resultp.getOrder_Date());
        txt_price.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.getTotal_Amount()), decimal_check));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SearchOrders resultp = data.get(position);
                final String order_code = resultp.getOrder_Code();
                final Dialog listDialog2 = new Dialog(context);
                listDialog2.setTitle("Select Option");
                LayoutInflater li1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
                listDialog2.setContentView(v1);
                listDialog2.setCancelable(true);
                final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
                edt_remark.setVisibility(View.GONE);
                Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
                btnButton.setText("Edit");
                Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
                btnClear.setText("Print");
                orders = Orders.getOrders(context,database,"where order_code='"+order_code+"'");
                listDialog2.show();
                if (orders==null){
                    listDialog2.dismiss();
                    Toast.makeText(context, "This order is not present in device", Toast.LENGTH_SHORT).show();
                }else if (orders.get_z_code().equals("0")&& orders.get_order_status().equals("OPEN")){
                    btnButton.setVisibility(View.VISIBLE);
                    btnClear.setVisibility(View.GONE);
                }else if (orders.get_z_code().equals("0")&& orders.get_order_status().equals("CLOSE")){
                    btnButton.setVisibility(View.GONE);
                    btnClear.setVisibility(View.VISIBLE);
                }else if (!orders.get_z_code().equals("0")){
                    btnButton.setVisibility(View.GONE);
                    btnClear.setVisibility(View.VISIBLE);
                }

                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (settings.get_Is_Print_Dialog_Show().equals("false")) {
//                            printDirect.PrintWithoutDialog(context,order_code,"","","");
//                        } else {
                            Intent launchIntent = new Intent(context, PrintLayout.class);
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchIntent.putExtra("strOrderNo", order_code);
                            context.startActivity(launchIntent);
//                        }
                    }
                });
                btnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String operation = "Edit";
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("order_code", order_code);
                        intent.putExtra("opr", operation);
                        context.startActivity(intent);
                        listDialog2.dismiss();
                    }
                });

//                SearchOrders resultp = data.get(position);
//                String operation = "Edit";
//                String order_code = resultp.getOrder_Code();
//                Orders orders = Orders.getOrders(context, database, " where order_code='" + order_code + "' and order_status='CLOSE'");
//                if (orders == null) {
//                    Toast.makeText(context, "This order is not present in device", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.putExtra("order_code", order_code);
//                    intent.putExtra("opr", operation);
//                    context.startActivity(intent);
//                }
            }
        });
        return itemView;
    }
}
