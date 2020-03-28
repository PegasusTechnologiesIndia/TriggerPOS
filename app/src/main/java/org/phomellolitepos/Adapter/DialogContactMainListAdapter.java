package org.phomellolitepos.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.PaymentCollectionActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.ReservationActivity;
import org.phomellolitepos.TicketSolution.TicketPaymentActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Settings;


/**
 * Created by Neeraj on 5/22/2017.
 */

public class DialogContactMainListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Contact> data;
    Dialog dialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    public DialogContactMainListAdapter(Context context,
                                        ArrayList<Contact> list, Dialog listDialog) {
        this.context = context;
        data = list;
        dialog = listDialog;
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
        Contact resultp = data.get(position);
        db = new Database(context);
        database = db.getWritableDatabase();
        settings = Settings.getSettings(context,database,"");
        txt_order_type_name = (TextView) itemView.findViewById(R.id.txt_order_type_name);
        txt_order_type_id = (TextView) itemView.findViewById(R.id.txt_order_type_id);
        txt_order_type_name.setText(resultp.get_name());
        txt_order_type_id.setText(resultp.get_contact_code());
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Contact resultp = data.get(position);
                String id = resultp.get_contact_code();
                Globals.strContact_Code = id;
                Globals.strResvContact_Code = id;
                if (settings.get_Home_Layout().equals("0")) {

                    try {
                        ((MainActivity) context).change_customer_icon();
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        ((Main2Activity) context).change_customer_icon();
                    } catch (Exception ex) {
                    }
                }

                try {
                    ((ReservationActivity) context).setCustomer(resultp.get_name());

                } catch (Exception ex) {
                }

                try {
                    ((TicketPaymentActivity) context).setCustomer(resultp.get_name(),resultp.get_contact_1());
                } catch (Exception ex) {
                }

                Globals.strContact_Name = resultp.get_name();
                dialog.dismiss();
            }
        });
        return itemView;
    }
}
