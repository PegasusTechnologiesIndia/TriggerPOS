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

import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;

public class ReturnOptionActivity extends AppCompatActivity {
    Button btn_cus_return, btn_inv_return;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.return_option);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   pDialog = new ProgressDialog(ReturnOptionActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {*/
                 if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(ReturnOptionActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();
                }
                 else {
                     if (settings.get_Home_Layout().equals("0")) {
                         try {
                             Intent intent = new Intent(ReturnOptionActivity.this, MainActivity.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             startActivity(intent);
                             // pDialog.dismiss();
                             finish();
                         } finally {
                         }
                     } else if (settings.get_Home_Layout().equals("2")) {
                         try {
                             Intent intent = new Intent(ReturnOptionActivity.this, RetailActivity.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             startActivity(intent);
                             //pDialog.dismiss();
                             finish();
                         } finally {
                         }
                     } else {
                         try {
                             Intent intent = new Intent(ReturnOptionActivity.this, Main2Activity.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             startActivity(intent);
                             //pDialog.dismiss();
                             finish();
                         } finally {
                         }
                     }
                 }

                   /* }
                };
                timerThread.start();*/
            }
        });

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");

        btn_cus_return = (Button) findViewById(R.id.btn_cus_return);
        btn_inv_return = (Button) findViewById(R.id.btn_inv_return);

        btn_cus_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnOptionActivity.this, CustomerReturnListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_inv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnOptionActivity.this, InvReturnListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(ReturnOptionActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {

                if(Globals.objLPR.getIndustry_Type().equals("2")){
                    Intent intent = new Intent(ReturnOptionActivity.this, Retail_IndustryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                }
                else {
                    if (settings.get_Home_Layout().equals("0")) {
                        try {
                            Intent intent = new Intent(ReturnOptionActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else if (settings.get_Home_Layout().equals("2")) {
                        try {
                            Intent intent = new Intent(ReturnOptionActivity.this, RetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }
                    } else {
                        try {
                            Intent intent = new Intent(ReturnOptionActivity.this, Main2Activity.class);
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
}
