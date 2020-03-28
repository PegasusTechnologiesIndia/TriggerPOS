package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.phomellolitepos.TicketSolution.TicketActivity;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketCategoryActivity extends AppCompatActivity {
    EditText edt_item_ct_name;
    Button btn_save, btn_delete;
    ProgressDialog pDialog;
    String operation,code,date;
    Database db;
    SQLiteDatabase database;
    String strIGCode = "",Item_category_name;
    Item_Group item_group;
    Item item;
    Item_Location item_location;
    Settings settings;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Globals.TicketCategory.equals("Class")) {
            getSupportActionBar().setTitle("Class/Category");
        } else {
            getSupportActionBar().setTitle("Destination");
        }

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(TicketCategoryActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }

                    }
                };
                timerThread.start();
            }
        });

        edt_item_ct_name = (EditText) findViewById(R.id.edt_item_ct_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
        }
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

        if (operation.equals("Edit")) {
            if (Globals.TicketCategory.equals("Class")) {
                btn_delete.setVisibility(View.VISIBLE);
                item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + code + "'");
                if (item_group != null) {
                    edt_item_ct_name.setText(item_group.get_item_group_name());
                }
            } else {
                btn_delete.setVisibility(View.VISIBLE);
                item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + code + "'", database, db);
                if (item != null) {
                    edt_item_ct_name.setText(item.get_item_name());
                }
            }


        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
        }




        if (Globals.TicketCategory.equals("Class")) {
            edt_item_ct_name.setHint("Class/Category");
        } else {
            edt_item_ct_name.setHint("Destination");
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Globals.TicketCategory.equals("Class")) {
                    if (operation.equals("Add")) {
                        Item_Group objIG1 = Item_Group.getItem_Group(getApplicationContext(), database, db, "  order By item_group_id Desc LIMIT 1");

                        if (objIG1 == null) {
                            strIGCode = "IG-" + 1;
                        } else {
                            strIGCode = "IG-" + (Integer.parseInt(objIG1.get_item_group_id()) + 1);
                        }

                    }
                    if (edt_item_ct_name.getText().toString().equals("")) {
                        edt_item_ct_name.setError(getString(R.string.Category_name_is_required));
                        edt_item_ct_name.requestFocus();
                        return;
                    } else {
                        Item_category_name = edt_item_ct_name.getText().toString().trim();
                    }

                    pDialog = new ProgressDialog(TicketCategoryActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();

                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);

                                Fill_category(Item_category_name, strIGCode);

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            } finally {
                            }
                        }
                    };
                    timerThread.start();

                } else {
                    if (operation.equals("Add")) {
                        Item objIT1 = Item.getItem(getApplicationContext(), "  order By item_id Desc LIMIT 1", database, db);

                        if (objIT1 == null) {
                            strIGCode = "IT-" + 1;
                        } else {
                            strIGCode = "IT-" + (Integer.parseInt(objIT1.get_item_id()) + 1);
                        }
                    }

                    if (edt_item_ct_name.getText().toString().trim().equals("")) {
                        edt_item_ct_name.setError(getString(R.string.Item_name_is_required));
                        edt_item_ct_name.requestFocus();
                        return;
                    } else {
                        Item_category_name = edt_item_ct_name.getText().toString().trim();
                    }

                    pDialog = new ProgressDialog(TicketCategoryActivity.this);
                    pDialog.setCancelable(false);
                    pDialog.setMessage(getString(R.string.Wait_msg));
                    pDialog.show();

                    final String finalLogo = "";
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                Fill_Destination(Item_category_name, strIGCode);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    };
                    timerThread.start();
                }
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(
                        TicketCategoryActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage("Do you want to delete?");

                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                alertDialog.setNegativeButton(R.string.Cancel,

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });

                alertDialog.setPositiveButton(R.string.Ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                pDialog = new ProgressDialog(TicketCategoryActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);
                                            if (Globals.TicketCategory.equals("Class")) {
                                                item_group.set_is_active("0");
                                                long l = item_group.updateItem_Group("item_group_code=?", new String[]{code}, database);
                                                if (l > 0) {
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                item.set_is_active("0");
                                                long l = item.updateItem("item_code=?", new String[]{code}, database);
                                                if (l > 0) {
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } finally {
                                        }
                                    }
                                };
                                timerThread.start();

                            }
                        });


                alert = alertDialog.create();
                alert.show();

                nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


                pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        });
    }

    private void Fill_Destination(String item_category_name, String strIGCode) {
        String modified_by = Globals.user;
        if (operation.equals("Edit")) {
            database.beginTransaction();
            item.set_item_name(item_category_name);
            item.set_item_code(code);
            item.set_is_push("N");
            long l = item.updateItem("item_code=?", new String[]{code}, database);
            if (l > 0) {
                pDialog.dismiss();
                database.setTransactionSuccessful();
                database.endTransaction();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();

                            try {
                                Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                    }
                });
            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            item = new Item(getApplicationContext(), null, Globals.Device_Code, strIGCode, "0", "", "",
                    item_category_name, "", "", "", "", "", "", "", "", "", "1", modified_by, date, "N", "", "");

            database.beginTransaction();
            long l = item.insertItem(database);
            if (l > 0) {
                pDialog.dismiss();
                database.setTransactionSuccessful();
                database.endTransaction();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();

                            try {
                                Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }

                    }
                });
            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private void Fill_category(String item_category_name, String item_category_code) {
        String modified_by = Globals.user;
        if (operation.equals("Edit")) {
            database.beginTransaction();
            item_group.set_item_group_name(item_category_name);
            item_group.set_item_group_code(code);
            item_group.set_is_push("N");
            long l = item_group.updateItem_Group("item_group_code=?", new String[]{code}, database);
            if (l > 0) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();

                        try {
                            Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();
                        } finally {
                        }

                    }
                });

            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {
            database.beginTransaction();
            item_group = new Item_Group(getApplicationContext(), null, Globals.Device_Code, item_category_code, "0", item_category_name, "0", "1", modified_by, date, "N");
            long l = item_group.insertItem_Group(database);
            if (l > 0) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();

                            try {
                                Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }

                    }
                });

            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Inserted, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(TicketCategoryActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {

                    try {
                        Intent intent = new Intent(TicketCategoryActivity.this, ClassDestinationListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }

            }
        };
        timerThread.start();
    }
}
