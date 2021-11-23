package org.phomellolitepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Adapter.UserPermissionCheckListAdapter;
import org.phomellolitepos.CheckBoxClass.UserPermissionCheck;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.User;

import java.util.ArrayList;

public class UserParmissionCheckListActivity extends AppCompatActivity {
    ListView bg_ck_list;
    TextView txt_title;
    Button btn_finish, btn_delete;
    String user_code, operation;
    Database db;
    User user;
    ArrayList<String> arrayList_cbg = new ArrayList<String>();
    SQLiteDatabase database;
    //    ArrayList<User> arrayList = new ArrayList<User>();
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<UserPermissionCheck> list = new ArrayList<UserPermissionCheck>();
    UserPermissionCheckListAdapter userPermissionCheckListAdapter;
    ProgressDialog pDialog;
    String strResult = "";
    boolean found = false;
    MenuItem item_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_parmission_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.user_permission);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
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
                Intent intent = new Intent(UserParmissionCheckListActivity.this, UserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("code", user_code);
                intent.putExtra("operation", operation);
                startActivity(intent);
                finish();
            }
        });
        bg_ck_list = (ListView) findViewById(R.id.bg_ck_list);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_title = (TextView) findViewById(R.id.txt_title);
        Intent intent = getIntent();
        user_code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");

        user = User.getUser(getApplicationContext(), "WHERE is_active = '1' and user_code ='" + user_code + "'", database);
        if (operation.equals("Add")) {

            fill_user_permission();
        } else if (operation.equals("Edit")) {
            fill_user_permission();
            arrayList.clear();
            list.clear();
        if (Globals.Industry_Type.equals("1")) {
             if(Globals.objLPR.getproject_id().equals("cloud")) {
                 arrayList.add("Item Category");
                 arrayList.add("Item");
                 arrayList.add("Contact");
                 arrayList.add("Order");
                 arrayList.add("Manager");
                 //arrayList.add("Reservation");
                 arrayList.add("Settings");
                 arrayList.add("Report");
                 arrayList.add("Tax");
                 arrayList.add("Unit");
                 arrayList.add("Database");
                 arrayList.add("Update License");
                 arrayList.add("Profile");
                 arrayList.add("Return");
                 arrayList.add("Accounts");
                 arrayList.add("User");
             }

             else {
                 arrayList.add("Item Category");
                 arrayList.add("Item");
                 arrayList.add("Contact");
                 arrayList.add("Order");
                 arrayList.add("Manager");
                 arrayList.add("Payment");
                 //arrayList.add("Reservation");
                 arrayList.add("Settings");
                 arrayList.add("Report");
                 arrayList.add("Tax");
                 arrayList.add("Unit");
                 arrayList.add("Database");
                 arrayList.add("Update License");
                 arrayList.add("Profile");
                 arrayList.add("User");
             }
            }

        else if(Globals.objLPR.getIndustry_Type().equals("3")){


            arrayList.add("Settings");
            arrayList.add("Manager");
            arrayList.add("Database");
            arrayList.add("Update License");
            arrayList.add("Profile");
            arrayList.add("Report");
            arrayList.add("User");
            arrayList.add("Contact");
            arrayList.add("Accounts");

        }

        else if(Globals.objLPR.getIndustry_Type().equals("4")){

            arrayList.add("Item");
            arrayList.add("Contact");
            arrayList.add("Order");
            arrayList.add("Settings");
            arrayList.add("Report");
            arrayList.add("Tax");
            arrayList.add("Database");
            arrayList.add("Update License");
            arrayList.add("Profile");
            arrayList.add("User");

        }
            if (user.get_app_user_permission().equals("")) {
            } else {
                String[] str = user.get_app_user_permission().split(",");
                for (int i = 0; i < str.length; i++) {
                    arrayList_cbg.add(str[i]);
                }
            }

            if (arrayList_cbg.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    UserPermissionCheck b = new UserPermissionCheck();

                    for (int h = 0; h < arrayList_cbg.size(); h++) {


                        String name = arrayList_cbg.get(h);
                        b.setName(arrayList.get(i));

                            b.setSelected(true);

                        if (name.equals(arrayList.get(i))) {
                            found = true;
                            b.setSelected(true);
                            break;
                        }
                    }
                    list.add(i, b);
                }

                if (list.size() > 0) {
                    userPermissionCheckListAdapter = new UserPermissionCheckListAdapter(this, list);
                    txt_title.setVisibility(View.GONE);
                    bg_ck_list.setVisibility(View.VISIBLE);
                    bg_ck_list.setAdapter(userPermissionCheckListAdapter);
                    bg_ck_list.setTextFilterEnabled(true);
                    userPermissionCheckListAdapter.notifyDataSetChanged();
                } else {
                    txt_title.setVisibility(View.VISIBLE);
                    bg_ck_list.setVisibility(View.GONE);
                }
            } else {
                try {
                    fill_user_permission();
                }
                catch(Exception e){}
            }


        }


        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(UserParmissionCheckListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            try {
                                insert_user_permision();
                            }
                            catch(Exception e){

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


    }

    private void insert_user_permision() {
        String strCheck = "0";
        //arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, " WHERE is_active ='1'");


        if (arrayList.size() > 0) {
            //long l = Contact_Bussiness_Group.delete_Contact_Bussiness_Group(getApplicationContext(), database, db, "contact_code =?", new String[]{contact_code});

            for (int i = 0; i < list.size(); i++) {
                UserPermissionCheck b = list.get(i);

                if (b.isSelected()) {
                    String name = b.getName();
                    if (strResult.equals("")) {
                        strResult = name;
                    }
                    strResult = strResult + "," + name;

                } else {
                    strCheck = "1";
                }

            }

            user = new User(getApplicationContext(), user.get_user_id(), "1", user_code, user.get_name(), user.get_email(), user.get_password(), user.get_max_discount(), "0", user.get_is_active(), user.get_Modified_By(), user.get_Modified_Date(), "N", strResult);
            database.beginTransaction();
            long a = user.updateUser("user_code=? And user_id =?", database, new String[]{user_code, user.get_user_id()});
            if (a > 0) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Globals.cart.clear();
                        Globals.order_item_tax.clear();
                        Globals.TotalItemPrice = 0;
                        Globals.TotalQty = 0;
                        Intent intent_category = new Intent(UserParmissionCheckListActivity.this, UserListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }

            if (strCheck.equals("1")) {
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Globals.cart.clear();
                        Globals.order_item_tax.clear();
                        Globals.TotalItemPrice = 0;
                        Globals.TotalQty = 0;
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(UserParmissionCheckListActivity.this, UserListActivity.class);
                        startActivity(intent_category);
                        finish();
                    }
                });
            }
        } else {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {

                    Globals.cart.clear();
                    Globals.order_item_tax.clear();
                    Globals.TotalItemPrice = 0;
                    Globals.TotalQty = 0;
                    Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(UserParmissionCheckListActivity.this, UserListActivity.class);
                    startActivity(intent_category);
                    finish();
                }
            });
        }


    }

    private void fill_user_permission() {
        arrayList.clear();

// if (Globals.Industry_Type.equals("1")) {
     if(Globals.objLPR.getproject_id().equals("cloud")) {
         arrayList.add("Item Category");
         arrayList.add("Item");
         arrayList.add("Contact");
         arrayList.add("Order");
         arrayList.add("Manager");
         //arrayList.add("Reservation");
         arrayList.add("Settings");
         arrayList.add("Report");
         arrayList.add("Tax");
         arrayList.add("Unit");
         arrayList.add("Database");
         arrayList.add("Update License");
         arrayList.add("Profile");
         arrayList.add("Return");
         arrayList.add("Accounts");
         arrayList.add("User");
     }
     else{
         arrayList.add("Item Category");
         arrayList.add("Item");
         arrayList.add("Contact");
         arrayList.add("Order");
         arrayList.add("Manager");
         //arrayList.add("Reservation");
         arrayList.add("Settings");
         arrayList.add("Report");
         arrayList.add("Tax");
         arrayList.add("Unit");
         arrayList.add("Database");
         arrayList.add("Update License");
         arrayList.add("Profile");
         arrayList.add("User");
     }
       // }

        for (int i = 0; i < arrayList.size(); i++) {
            UserPermissionCheck b = new UserPermissionCheck();
            b.setName(arrayList.get(i));
            b.setSelected(false);
            list.add(b);
        }

        if (list.size() > 0) {
            userPermissionCheckListAdapter = new UserPermissionCheckListAdapter(this, list);
            txt_title.setVisibility(View.GONE);
            bg_ck_list.setVisibility(View.VISIBLE);
            bg_ck_list.setAdapter(userPermissionCheckListAdapter);
            bg_ck_list.setTextFilterEnabled(true);
            userPermissionCheckListAdapter.notifyDataSetChanged();
        } else {
            txt_title.setVisibility(View.VISIBLE);
            bg_ck_list.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_menu = menu.findItem(R.id.action_settings);
        if (found == true) {
            item_menu.setTitle(R.string.Deselect_All);
        } else {

            item_menu.setTitle(R.string.Select_All);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_all_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String strMenu = item.getTitle() + "";
        if (id == R.id.action_settings) {
            list.clear();
            if (strMenu.equals(getString(R.string.Select_All))) {
                item.setTitle(R.string.Deselect_All);
                //arrayList = Bussiness_Group.getAllBussiness_Group(getApplicationContext(), database, db, "WHERE is_active ='1' Order By name asc");
                for (int i = 0; i < arrayList.size(); i++) {
                    UserPermissionCheck b = new UserPermissionCheck();
                    b.setName(arrayList.get(i));
                    b.setSelected(true);
                    list.add(b);
                }
                userPermissionCheckListAdapter = new UserPermissionCheckListAdapter(this, list);
                bg_ck_list.setAdapter(userPermissionCheckListAdapter);

            } else {
                item.setTitle(R.string.Select_All);
                for (int i = 0; i < arrayList.size(); i++) {
                    UserPermissionCheck b = new UserPermissionCheck();
                    b.setName(arrayList.get(i));
                    b.setSelected(false);
                    list.add(b);
                }
                userPermissionCheckListAdapter = new UserPermissionCheckListAdapter(this, list);
                bg_ck_list.setAdapter(userPermissionCheckListAdapter);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserParmissionCheckListActivity.this, UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("code", user_code);
        intent.putExtra("operation", operation);
        startActivity(intent);
        finish();
    }



}

