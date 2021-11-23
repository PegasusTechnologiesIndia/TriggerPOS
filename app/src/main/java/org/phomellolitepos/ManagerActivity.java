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
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;

import java.util.ArrayList;
import java.util.List;


public class ManagerActivity extends AppCompatActivity {
    Button btn_ob, btn_expenses, btn_xz;
    ProgressDialog pDialog;
   // Settings settings;
    Database db;
    SQLiteDatabase database;
    List<String> stringarraylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Manager);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

      //  Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
       // settings = Settings.getSettings(getApplicationContext(), database, "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* pDialog = new ProgressDialog(ManagerActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/
                        if(Globals.objLPR.getIndustry_Type().equals("1")) {
                            if (Globals.objsettings.get_Home_Layout().equals("0")) {
                                try {
                                    Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                                try {
                                    Intent intent = new Intent(ManagerActivity.this, RetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(ManagerActivity.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                   // pDialog.dismiss();
                                    finish();
                                } finally {
                                }
                            }
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("2")){
                            Intent intent = new Intent(ManagerActivity.this, Retail_IndustryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else if(Globals.objLPR.getIndustry_Type().equals("3")){
                            Intent intent = new Intent(ManagerActivity.this, PaymentCollection_MainScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                  /*  }
                };
                timerThread.start();*/

            }
        });

        btn_ob = (Button) findViewById(R.id.btn_ob);
        btn_expenses = (Button) findViewById(R.id.btn_expenses);
        btn_xz = (Button) findViewById(R.id.btn_xz);

        btn_ob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 //   stringarraylist.add("str");
                    Intent intent = new Intent(ManagerActivity.this, OpeningBalanceActivity.class);

                    startActivity(intent);
                    finish();

            }
        });

        btn_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerActivity.this, ExpensesListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerActivity.this, X_ZActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
/*        pDialog = new ProgressDialog(ManagerActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {*/
                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (Globals.objsettings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(ManagerActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(ManagerActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                           // pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    }
                }
                else if(Globals.objLPR.getIndustry_Type().equals("2")){

                    Intent intent = new Intent(ManagerActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(Globals.objLPR.getIndustry_Type().equals("3")){

                    Intent intent = new Intent(ManagerActivity.this, PaymentCollection_MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
           /* }
        };
        timerThread.start();*/
    }
}
