package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.User;
import org.phomellolitepos.database.Settings;

public class AboutActivity extends AppCompatActivity {
    TextView txt_version;
    ProgressDialog pDialog;
    ImageView pegasus_image;
    Dialog listDialog2;
    Settings settings;
    Database db;
    SQLiteDatabase database;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.About);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        txt_version = (TextView) findViewById(R.id.txt_version);
        pegasus_image = (ImageView) findViewById(R.id.pegasus_image);
        listDialog2 = new Dialog(this);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(AboutActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(AboutActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(AboutActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        }

                    }
                };
                timerThread.start();
            }
        });

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText("Version : "+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        pegasus_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str = "13245";
//                listDialog2.setTitle("");
                LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.password_dialog, null, false);
                listDialog2.setContentView(v1);
                listDialog2.setCancelable(true);
                final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                listDialog2.show();
                Window window = listDialog2.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                edt_pass.setText(str);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (str.equals(edt_pass.getText().toString().trim())) {
                            listDialog2.dismiss();
                            listDialog2.setTitle("Admin");
                            LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v1 = li1.inflate(R.layout.set_copy_right_dialog, null, false);
                            listDialog2.setContentView(v1);
                            listDialog2.setCancelable(true);
                            final EditText edt_copy_right = (EditText) listDialog2.findViewById(R.id.edt_copy_right);
                            Button btn_save = (Button) listDialog2.findViewById(R.id.btn_save);
                            listDialog2.show();
                            Window window = listDialog2.getWindow();
                            window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                            edt_copy_right.setText(settings.get_Copy_Right());
                            btn_save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (edt_pass.getText().toString().trim().equals("Change Copy Right")) {
                                        Toast.makeText(getApplicationContext(), R.string.field_vaccant, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            settings.set_Copy_Right(edt_copy_right.getText().toString().trim());
                                            long l = settings.updateSettings("_Id=?", new String[]{settings.get_Id()}, database);
                                            if (l > 0) {
                                                Toast.makeText(getApplicationContext(), R.string.savesucc, Toast.LENGTH_SHORT).show();
                                                listDialog2.dismiss();
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception ex){

                                        }

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(AboutActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(AboutActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(AboutActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                }
            }
        };
        timerThread.start();
    }
}
