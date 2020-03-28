package org.phomellolitepos.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.R;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter{

    LayoutInflater inflter;
    Context context;
    ArrayList<String> data;
    Database db;
    SQLiteDatabase database;
    Item_Group item_group;

    public SpinnerAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        data = arrayList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        TextView category_name, category_code;
        inflter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflter.inflate(R.layout.item_spinner_item,
                viewGroup, false);
        String resultp = data.get(position);
        db = new Database(context);
        database = db.getWritableDatabase();
        category_name = (TextView) itemView.findViewById(R.id.category_name);
        category_code = (TextView) itemView.findViewById(R.id.category_code);
        try {
            item_group = Item_Group.getItem_Group(context,database,db,"WHERE item_group_code = '"+resultp+"'");
            category_name.setText(item_group.get_item_group_name());
            category_code.setText(item_group.get_item_group_code());
        }catch (Exception ex){
            category_name.setText(resultp);
            category_code.setText("");
        }
        return itemView;
    }
}
