package org.phomellolitepos.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.R;
import org.phomellolitepos.TableMangement;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Table;

/**
 * Created by Neeraj Paliwal on 3/24/2017.
 */

public class DialogOrderTypeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Order_Type> data;
    Dialog dialog;
    MenuItem orertyp;
    Database db;
    SQLiteDatabase database;
    BottomNavigationView topNavigation;

    public DialogOrderTypeListAdapter(Context context,
                                      ArrayList<Order_Type> list, Dialog listDialog, MenuItem orertype, BottomNavigationView topNavigationView) {
        this.context = context;
        data = list;
        dialog = listDialog;
        orertyp = orertype;
        topNavigation = topNavigationView;
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
        // Declare Variables
        TextView txt_order_type_name, txt_order_type_id;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.dialog_order_type_list_item,
                parent, false);
        Menu menu = topNavigation.getMenu();
        orertyp = menu.findItem(R.id.action_order_type);
        Order_Type resultp = data.get(position);
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_name());
        txt_order_type_id.setText(resultp.get_order_type_id());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Order_Type resultp = data.get(position);
                String id_order = resultp.get_order_type_id();
                Globals.strOrder_type_id = id_order;
                orertyp.setTitle(resultp.get_name());
                Globals.strorderType=resultp.get_name();

                switch (id_order) {
                    case "1":
                        orertyp.setIcon(R.drawable.deliver);
                        break;
                    case "2":
                        orertyp.setIcon(R.drawable.drive_thrue);
                        break;
                    case "3":
                        orertyp.setIcon(R.drawable.pick_up);
                        break;
                    case "4":
                        orertyp.setIcon(R.drawable.take_out);
                        break;
                    case "5":
                        orertyp.setIcon(R.drawable.dine_ine);
                        break;
                    default:
                        orertyp.setIcon(R.drawable.deliver);
                        break;
                }
                dialog.dismiss();
                if(Globals.objLPR.getproject_id().equals("cloud")){
                if(id_order.equals("5")) {

                    db = new Database(context);
                    database = db.getReadableDatabase();
                    Globals.strorderType = "Dine-In";
                    Globals.strOrder_type_id = id_order;
                    Table tableobj = Table.getTable(context, database, db, "");
                    if (tableobj == null) {

                        Toast.makeText(context, "No Table Data Found", Toast.LENGTH_LONG).show();
                    } else {
                        Intent table = new Intent(context, TableMangement.class);
                        context.startActivity(table);
                    }
                }
                else{
                   // Toast.makeText(context,"No table Found",Toast.LENGTH_LONG).show();
                }
                  /*  AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Book Table before Placing Order!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
*/
                }
            }
        });
        return itemView;
    }
}
