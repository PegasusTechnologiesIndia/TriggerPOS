package org.phomellolitepos;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import org.phomellolitepos.Adapter.MainItemCategoryListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Settings;

public class MainCategoryListActivity extends AppCompatActivity {
    Item_Group item_group;
    ArrayList<Item_Group> arrayList;
    MainItemCategoryListAdapter mainItemCategoryListAdapter;
    Database db;
    SQLiteDatabase database;
    Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item Category");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.get_Home_Layout().equals("0")) {
                    Intent intent = new Intent(MainCategoryListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainCategoryListActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        fill_main_category_list();
    }

    private void fill_main_category_list() {
        arrayList = Item_Group.getAllItem_Group(getApplicationContext(), " Where is_active ='1'", database, db);
        if (arrayList.size()>0){
            mainItemCategoryListAdapter = new MainItemCategoryListAdapter(MainCategoryListActivity.this, arrayList);
            ListView category_list = (ListView) findViewById(R.id.lv);
            category_list.setAdapter(mainItemCategoryListAdapter);
            category_list.setTextFilterEnabled(true);
            mainItemCategoryListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onBackPressed() {
        if (settings.get_Home_Layout().equals("0")) {
            Intent intent = new Intent(MainCategoryListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(MainCategoryListActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

    }
}
