package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.phomellolitepos.BussinessGroupActivity;
import org.phomellolitepos.CheckBoxClass.ContactCheck;
import org.phomellolitepos.PaymentCollectionActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pay_Collection;

import java.util.ArrayList;

public class PayCollectionListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Pay_Collection> data;
    Database db;
    SQLiteDatabase database;
    String decimal_check;

    public PayCollectionListAdapter(Context context,
                                    ArrayList<Pay_Collection> list) {
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
        TextView txt_bussiness_gp_name, txt_bussiness_gp_code;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.bussiness_group_list_item,
                viewGroup, false);
        db = new Database(context);
        database = db.getWritableDatabase();
        Pay_Collection resultp = data.get(i);
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        txt_bussiness_gp_name = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_name);
        txt_bussiness_gp_code = (TextView) itemView.findViewById(R.id.txt_bussiness_gp_code);
        Contact contact = Contact.getContact(context, database, db, " WHERE contact_code='" + resultp.get_contact_code() + "'");
        txt_bussiness_gp_name.setText(contact.get_name());
        txt_bussiness_gp_code.setText(Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_amount()), decimal_check));

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Pay_Collection resultp = data.get(i);
                String code = resultp.get_collection_code();
                String operation = "Edit";
                Intent intent = new Intent(context, PaymentCollectionActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("operation", operation);
                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
