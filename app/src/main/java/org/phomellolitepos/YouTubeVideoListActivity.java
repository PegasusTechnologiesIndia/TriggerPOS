package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import org.phomellolitepos.Adapter.YouTubeListAdapter;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Sys_Support;
import java.util.ArrayList;

public class YouTubeVideoListActivity extends AppCompatActivity {
    ListView list_youtube;
    YouTubeListAdapter youTubeListAdapter;
    ArrayList<Sys_Support> arrayList;
    Database db;
    SQLiteDatabase database;
    ProgressDialog pDialog;
    Settings settings;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_video_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Youtube Library");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        list_youtube = (ListView) findViewById(R.id.list_youtube);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(YouTubeVideoListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {

                        if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(YouTubeVideoListActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            finish();

                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("4")){
                            Intent intent = new Intent(YouTubeVideoListActivity.this, ParkingIndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        }
                        else {
                            if (settings.get_Home_Layout().equals("0")) {
                                try {
                                    Intent intent = new Intent(YouTubeVideoListActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (settings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(YouTubeVideoListActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(YouTubeVideoListActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            }
                        }
                    }
                };
                timerThread.start();
            }
        });
        load_urls();

    }

    private void load_urls() {

        arrayList = Sys_Support.getAllSys_Support(getApplicationContext(), "Order By lower(name) asc", database);

        if (arrayList.size() > 0) {
            youTubeListAdapter = new YouTubeListAdapter(YouTubeVideoListActivity.this, arrayList);
            list_youtube.setVisibility(View.VISIBLE);
            list_youtube.setAdapter(youTubeListAdapter);
            list_youtube.setTextFilterEnabled(true);
            youTubeListAdapter.notifyDataSetChanged();

        } else {
            list_youtube.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(YouTubeVideoListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {

                if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(YouTubeVideoListActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
               else if(Globals.objLPR.getIndustry_Type().equals("4")){
                    Intent intent = new Intent(YouTubeVideoListActivity.this, ParkingIndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
                else {

                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(YouTubeVideoListActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(YouTubeVideoListActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(YouTubeVideoListActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }
            }
        };
        timerThread.start();
    }
}
