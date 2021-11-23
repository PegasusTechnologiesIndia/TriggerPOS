package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.User;


public class UserActivity extends AppCompatActivity {
    TextInputLayout edt_layout_user_code, edt_layout_user_name, edt_layout_email
            , edt_layout_password,
            edt_layout_max_discount;
    EditText edt_user_code, edt_user_name, edt_email, edt_password, edt_max_discount;
    Button btn_save, btn_item_delete;
    User user;
    String operation, code, User_Id, date, decimal_check;
    Database db;
    SQLiteDatabase database;
    // Lite_POS_Registration lite_pos_registration;
    ProgressDialog pDialog;
    String user_name = "", user_email, user_password = "", user_max_discount = "";
    String strITCode = "";
    String strResult = "";
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    AlertDialog alert;
    LinearLayout.LayoutParams lp;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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
                pDialog = new ProgressDialog(UserActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(100);

                            Intent intent = new Intent(UserActivity.this, UserListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } finally {
                        }
                    }
                };
                timerThread.start();

            }
        });
        Intent intent = getIntent();
        getSupportActionBar().setTitle(R.string.User);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        if (operation == null) {
            operation = "Add";
            User_Id = null;
        }


        edt_layout_user_code = (TextInputLayout) findViewById(R.id.edt_layout_user_code);
        edt_layout_user_name = (TextInputLayout) findViewById(R.id.edt_layout_user_name);
        edt_layout_email = (TextInputLayout) findViewById(R.id.edt_layout_email);
        edt_layout_password = (TextInputLayout) findViewById(R.id.edt_layout_password);
        edt_layout_max_discount = (TextInputLayout) findViewById(R.id.edt_layout_max_discount);


        edt_user_code = (EditText) findViewById(R.id.edt_user_code);
        edt_user_name = (EditText) findViewById(R.id.edt_user_name);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_max_discount = (EditText) findViewById(R.id.edt_max_discount);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setText(getString(R.string.Next));
        btn_save.setVisibility(View.GONE);
        btn_item_delete = (Button) findViewById(R.id.btn_item_delete);
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);
       if(Globals.objLPR.getIndustry_Type().equals("3")){
    edt_layout_max_discount.setVisibility(View.GONE);

       }

        edt_max_discount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edt_max_discount.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_max_discount.requestFocus();
                    edt_max_discount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_max_discount, InputMethodManager.SHOW_IMPLICIT);
                    edt_max_discount.selectAll();
                    return true;
                }
            }
        });

        if (operation.equals("Edit")) {
            if (!Globals.objLPR.getproject_id().equals("cloud")) {

                btn_item_delete.setVisibility(View.VISIBLE);
            }
            //btn_item_delete.setVisibility(View.VISIBLE);
            user = User.getUser(getApplicationContext(), "WHERE user_code = '" + code + "'", database);
            edt_user_code.setText(user.get_user_code());
            edt_user_code.setEnabled(false);
            edt_user_name.setText(user.get_name());
            edt_email.setText(user.get_email());
            edt_password.setText(user.get_password());
            User_Id = user.get_user_id();

            try {
                decimal_check = Globals.objLPD.getDecimal_Place();
            } catch (Exception ex) {
                decimal_check = "1";
            }

            String max_discount;
            max_discount = user.get_max_discount();
            edt_max_discount.setText(max_discount);
            if (user.get_app_user_permission().equals("")) {
            } else {
                String[] str = user.get_app_user_permission().split(",");
                for (int i = 0; i < str.length; i++) {
                    strResult = strResult + "," + str[i];
                }
            }

        } else {
            btn_save.setBackgroundColor(getResources().getColor(R.color.button_color));
        }

        btn_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = new AlertDialog.Builder(
                        UserActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage(R.string.do_you_want_to_delete);

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
                                pDialog = new ProgressDialog(UserActivity.this);
                                pDialog.setCancelable(false);
                                pDialog.setMessage(getString(R.string.Wait_msg));
                                pDialog.show();
                                Thread timerThread = new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);

                                            user = User.getUser(getApplicationContext(), "WHERE user_code = '" + code + "'", database);
                                            if (user.get_user_code().equals(Globals.user)) {
                                                pDialog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "You can't delete this user", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                database.beginTransaction();
                                                user.set_is_active("0");
                                                long it = user.updateUser("user_code=?", database, new String[]{code});
                                                if (it > 0) {
                                                    database.setTransactionSuccessful();
                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(UserActivity.this, UserListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });


                                                } else {
                                                    database.endTransaction();
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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void Fill_Item(String user_Id, final String strITCode, String user_name, String user_email, String user_password, String user_max_discount) {
        String modified_by = Globals.user;
        user = new User(getApplicationContext(), user_Id, "1", strITCode, user_name, user_email, user_password, user_max_discount, "0", "1", modified_by, date, "N", strResult);

        if (operation.equals("Edit")) {
            database.beginTransaction();
            long l = user.updateUser("user_code=? And user_id =?", database, new String[]{code, User_Id});
            if (l > 0) {

                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Globals.cart.clear();
                        Globals.order_item_tax.clear();
                        Globals.TotalItemPrice = 0;
                        Globals.TotalQty = 0;
                        //Toast.makeText(getApplicationContext(), R.string.Update_Successfully, Toast.LENGTH_SHORT).show();
//                        Intent intent_category = new Intent(UserActivity.this, UserListActivity.class);

                        if(Globals.objLPR.getEmail().equals(user.get_email())){
                            Intent intent_category = new Intent(UserActivity.this, UserListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                        else {
                            Intent intent_category = new Intent(UserActivity.this, UserParmissionCheckListActivity.class);
                            intent_category.putExtra("code", user.get_user_code());
                            intent_category.putExtra("operation", operation);
                            startActivity(intent_category);
                            finish();
                        }
                    }
                });


            } else {
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getApplicationContext(), R.string.Record_Not_Updated, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {
            database.beginTransaction();
            long l = user.insertUser(database);
            if (l > 0) {

                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Globals.cart.clear();
                        Globals.order_item_tax.clear();
                        Globals.TotalItemPrice = 0;
                        Globals.TotalQty = 0;
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();

                        if(Globals.objLPR.getEmail().equals(user.get_email())){
                            Intent intent_category = new Intent(UserActivity.this, UserListActivity.class);
                            startActivity(intent_category);
                            finish();
                        }
                        else {
                            Intent intent_category = new Intent(UserActivity.this, UserParmissionCheckListActivity.class);
                            intent_category.putExtra("code", strITCode);
                            intent_category.putExtra("operation", operation);
                            startActivity(intent_category);
                            startActivity(intent_category);
                            finish();
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(UserActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        return true;
    }

    private void save() {

        Globals.cart.clear();
        Globals.order_item_tax.clear();
        Globals.TotalItemPrice = 0;
        Globals.TotalQty = 0;
        Globals.setEmpty();
        if (edt_user_code.getText().toString().equals("")) {
            User objIT1 = User.getUser(getApplicationContext(), "  order By user_id Desc LIMIT 1", database);

            if (objIT1 == null) {
                strITCode = "US-" + 1;
            } else {
                strITCode = "US-" + (Integer.parseInt(objIT1.get_user_id()) + 1);
            }
        } else {

            if (edt_user_code.getText().toString().contains(" ")) {
                edt_user_code.setError(getString(R.string.Cant_Enter_Space));
                edt_user_code.requestFocus();
                return;
            } else {
                strITCode = edt_user_code.getText().toString();
            }

        }

        if (edt_user_name.getText().toString().equals("")) {
            edt_user_name.setError(getString(R.string.User_Name_Is_Required));
            edt_user_name.requestFocus();
            return;
        } else {
            user_name = edt_user_name.getText().toString();
        }


        if (edt_email.getText().toString().equals("")) {
        } else {
            final String email1 = edt_email.getText().toString().trim();
            if (!isValidEmail(email1)) {
                edt_email.setError(getString(R.string.Invalid_Email));
                edt_email.requestFocus();
                return;
            } else {
                user_email = edt_email.getText().toString().trim();
            }
        }

        if (edt_password.getText().toString().equals("")) {
            edt_password.setError(getString(R.string.Password_is_required));
            edt_password.requestFocus();
            return;
        } else {
            if (edt_password.getText().toString().contains(" ")) {
                edt_password.setError(getString(R.string.Cant_Enter_Space));
                edt_password.requestFocus();
                return;
            } else {
                user_password = edt_password.getText().toString();
            }

        }

        if (edt_max_discount.getText().toString().equals("")) {
            user_max_discount = "0";
        } else {
            Double maxDescount = Double.parseDouble(edt_max_discount.getText().toString().trim());
            if (maxDescount > 100) {
                edt_max_discount.setError(getString(R.string.Max_Discount_Validation));
                edt_max_discount.requestFocus();
                return;
            } else {
                user_max_discount = edt_max_discount.getText().toString().trim();
            }
        }

        pDialog = new ProgressDialog(UserActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    try {
                        Fill_Item(User_Id, strITCode, user_name, user_email, user_password, user_max_discount);
                    } catch (Exception e) {

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.split_payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(UserActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(100);

                    Intent intent = new Intent(UserActivity.this, UserListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                }
            }
        };
        timerThread.start();
    }
}


