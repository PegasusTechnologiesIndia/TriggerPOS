package org.phomellolitepos.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.Adapter.MainItemListAdapter;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Settings;

@SuppressLint("ValidFragment")
public class ItemFragment extends android.support.v4.app.Fragment {
    ArrayList<Item> arrayList;
    View v;
    private ListView category_list;
    MainItemListAdapter mainItemListAdapter;
    Activity activity;
    Context context;
    Button btn_Item_Price, btn_Qty;
    private String itemGrpCode, strFilter, operation;
    Database db;
    SQLiteDatabase database;
    ProgressBar progressBar;
    Settings settings;

    @SuppressLint("ValidFragment")
    public ItemFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_item, container, false);
        setRetainInstance(true);
        Bundle bundle = getArguments();
        itemGrpCode = bundle.getString("itemGrpCode");
        Globals.item_category_code = itemGrpCode;
        strFilter = bundle.getString("filter");
        operation = bundle.getString("operation");

        init_View();
        return v;
    }

    public void init_View() {
        category_list = (ListView) v.findViewById(R.id.main_list);
        btn_Item_Price = (Button) v.findViewById(R.id.btn_Item_Price);
        btn_Qty = (Button) v.findViewById(R.id.btn_Qty);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar2);

        db = new Database(activity);
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getActivity(),database,"");
        if (operation.equals("search")) {
            if (settings.get_Is_Zero_Stock().equals("true")){
                arrayList = Item.getAllItem(activity, "WHERE  is_active = '1'  " + strFilter + "  Order By lower(item_name) asc limit 100", database);
            }else {
                arrayList = Item.getAllItem(activity, " left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1'  " + strFilter + "  and item_location.quantity!='0'  Order By lower(item_name) asc limit 100", database);
            }

            if (arrayList.size() > 0) {
                mainItemListAdapter = new MainItemListAdapter(activity, arrayList, category_list,progressBar);
                category_list.setAdapter(mainItemListAdapter);
                category_list.setTextFilterEnabled(true);
                mainItemListAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.GONE);
            }

        } else {
            if (settings.get_Is_Zero_Stock().equals("true")){
                arrayList = Item.getAllItem(activity, "WHERE  is_active = '1'  and item_group_code='" + itemGrpCode + "' Order By lower(item_name) asc limit 100", database);
            }else {
                arrayList = Item.getAllItem(activity, "left join item_location on item.item_code = item_location.item_code WHERE  item.is_active = '1'  and  item.item_group_code='" + itemGrpCode + "' and item_location.quantity!='0' Order By lower(item_name) asc limit 100", database);
            }

            if (arrayList.size() > 0) {
                mainItemListAdapter = new MainItemListAdapter(activity, arrayList, category_list,progressBar);
                category_list.setAdapter(mainItemListAdapter);
                category_list.setTextFilterEnabled(true);
                mainItemListAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
