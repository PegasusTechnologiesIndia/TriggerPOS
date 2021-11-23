package org.phomellolitepos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddPaymentActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    String operation, code;
    Database db;
    SQLiteDatabase database;
    String date;
    TextInputLayout edt_layout_pt_name,edt_layout_pt_code;
    EditText edt_pt_name,edt_pt_code;
    Button btn_save,btn_delete;
    String paymentname,str_payment;
    Payment payment;
    MenuItem menuItem;
    String paymentid;
    AlertDialog.Builder alertDialog;
    Button nbutton;
    Button pbutton;
    LinearLayout.LayoutParams lp;
    AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
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
                pDialog = new ProgressDialog(AddPaymentActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            //sleep(100);

                            Intent intent = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            pDialog.dismiss();
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                        }
                    }
                };
                timerThread.start();

            }
        });
        Intent intent = getIntent();
        getSupportActionBar().setTitle(R.string.title_activity_item_payment);
        code = intent.getStringExtra("code");
        operation = intent.getStringExtra("operation");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = format.format(d);

//        check_online_mode();

        edt_layout_pt_name = (TextInputLayout) findViewById(R.id.edt_layout_pt_name);
        edt_layout_pt_code = (TextInputLayout) findViewById(R.id.edt_layout_pt_code);
        edt_pt_name = (EditText) findViewById(R.id.edt_pt_name);
        edt_pt_code = (EditText) findViewById(R.id.edt_pt_code);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_save.setVisibility(View.GONE);
        if (operation.equals("Edit")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            if(!Globals.objLPR.getproject_id().equals("cloud")) {

                btn_delete.setVisibility(View.VISIBLE);
            }
            payment = Payment.getPayment(getApplicationContext(), "WHERE payment_id = '" + code + "'");

         //   payment = Payment.getPayment(getApplicationContext(), "WHERE is_active ='1' Order By payment_name asc");
//            item_group=null;
//            if (item_group == null) {
            if (payment != null) {
                edt_pt_name.setText(payment.get_payment_name());
                edt_pt_code.setText(payment.get_payment_id());
                edt_pt_code.setEnabled(false);
              /*  Item_Group item_group_parent = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code = '" + item_group.get_parent_code() + "'");
                if (item_group_parent != null) {
                    String compare_parent_name;
                    try {
                        compare_parent_name = item_group_parent.get_item_group_code();
                    } catch (Exception ex) {
                        compare_parent_name = "";
                    }
                    //fill_parent_code(compare_parent_name);
                } else {
                    fill_parent_code("");
                }*/
            }
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ArrayList<Payment> payment_array = Payment.getAllPayment(getApplicationContext(), "WHERE payment_id = '" + code + "'");

                if (payment_array.size()>0){
                    alertDialog = new AlertDialog.Builder(
                            AddPaymentActivity.this);
                    alertDialog.setTitle("");
                    alertDialog
                            .setMessage(R.string.paymentDeleteMessage);

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
                                    pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    pDialog = new ProgressDialog(AddPaymentActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);
                                                payment.set_is_active("0");
                                                long l = payment.updatePayment("payment_id=?", new String[]{code}, database);
                                                if (l > 0) {
                                                    for (int i=0;i<payment_array.size();i++){
                                                        Payment objpayment = Payment.getPayment(getApplicationContext(),"where payment_id = '"+payment_array.get(i).get_payment_id()+"'");
                                                        objpayment.set_payment_id("");
                                                        objpayment.updatePayment("payment_id=?",new String[]{objpayment.get_payment_id()},database);
                                                    }
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
                                                            startActivity(intent_category);
                                                            finish();
                                                        }
                                                    });
                                                } else {
//                                                    database.endTransaction();
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Record_Not_Deleted, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }



                                            } catch (InterruptedException e) {
                                                pDialog.dismiss();
//                                                database.endTransaction();
                                                e.printStackTrace();
                                            } finally {
                                                pDialog.dismiss();
//                                                database.endTransaction();
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
                }else {
                    alertDialog = new AlertDialog.Builder(
                            AddPaymentActivity.this);
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
                                    pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    pDialog = new ProgressDialog(AddPaymentActivity.this);
                                    pDialog.setCancelable(false);
                                    pDialog.setMessage(getString(R.string.Wait_msg));
                                    pDialog.show();
                                    Thread timerThread = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(1000);

                                                payment.set_is_active("0");
                                                long l = payment.updatePayment("payment_id=?", new String[]{code}, database);
                                                if (l > 0) {
                                                    pDialog.dismiss();
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), R.string.Delete_Successfully, Toast.LENGTH_SHORT).show();
                                                            Intent intent_category = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
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
                }

            }
        });

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.action_settings);
        ImageView imageView = new ImageView(AddPaymentActivity.this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.save_button));
        menuItem.setActionView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    save();

                }
                catch(Exception e){

                }
            }
        });
        return true;
    }


    // First time adding Item group
    private void Fill_Payment(String payment_name, String payment_code) {
        String modified_by = Globals.user;
        database.beginTransaction();
        System.out.println("DATE String"+ date);
        System.out.println("license customer id"+ Globals.license_id);
        try {
            payment = new Payment(getApplicationContext(), payment_code, Globals.license_id, payment_name, "1", modified_by, date, "N");
            long l = payment.insertPayment(database);
            if (l > 0) {
                database.setTransactionSuccessful();
                database.endTransaction();
                pDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.Save_Successfully, Toast.LENGTH_SHORT).show();
                        Intent intent_category = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
                        startActivity(intent_category);
                        finish();
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
        catch(Exception e){

        }


    }


    private void Update_Payment(String code, String paymentname) {
        database.beginTransaction();
        payment.set_payment_name(paymentname);


        long l = payment.updatePayment("payment_id=?", new String[]{code}, database);
        if (l > 0) {
            database.setTransactionSuccessful();
            database.endTransaction();
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.Update_Successfully, Toast.LENGTH_SHORT).show();
                    Intent intent_category = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
                    startActivity(intent_category);
                    finish();
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
    }

    private void save() {

        if (operation.equals("Edit")) {

            try {
                final String Payment_name = edt_pt_name.getText().toString().trim();
                final String Payment_code = edt_pt_code.getText().toString().trim();

                pDialog = new ProgressDialog(AddPaymentActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            try {
                                Payment objPayment = Payment.getPayment(getApplicationContext(),  "WHERE payment_name='" + edt_pt_name.getText().toString() + "' COLLATE NOCASE");

                                //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                                str_payment = objPayment.get_payment_name();
                            } catch (Exception e) {

                            }

                            if (edt_pt_name.getText().toString().equalsIgnoreCase(str_payment)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        edt_pt_name.setText(payment.get_payment_name());
                                        edt_pt_name.selectAll();
                                        Toast.makeText(AddPaymentActivity.this, getString(R.string.paymentpresent), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                try {
                                    pDialog.dismiss();
                                    Update_Payment(Payment_code,Payment_name);
                                   // Update_Item_category(code, Item_category_name, Item_category_code);
                                }
                                catch(Exception e){

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
            catch(Exception e){

            }
        } else if (operation.equals("Add")) {
            try {


                if (edt_pt_name.getText().toString().equals("")) {
                    edt_pt_name.setError(getString(R.string.paymentname_required));
                    edt_pt_name.requestFocus();
                    return;
                } else {
                    paymentname = edt_pt_name.getText().toString().trim();
                }

                pDialog = new ProgressDialog(AddPaymentActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                            try {
                                String strQry;

                                Cursor cursor;
                                strQry = "Select (max(payment_id)+1) AS payment_id FROM payments";
                                cursor = database.rawQuery(strQry, null);
                                while(cursor.moveToNext()){
                                     paymentid= cursor.getString(0);
                                }
                                Payment objPayment = Payment.getPayment(getApplicationContext(),  "WHERE payment_name='" + edt_pt_name.getText().toString() + "' COLLATE NOCASE" );

                                //Item_Group item_group = Item_Group.getItem_Group(getApplicationContext(), database, db, "WHERE item_group_code='" + code + "'");
                                str_payment = objPayment.get_payment_name();
                            } catch (Exception e) {

                            }


                            if (edt_pt_name.getText().toString().equals(str_payment)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                        edt_pt_name.setText("");
                                        Toast.makeText(AddPaymentActivity.this, getString(R.string.paymentpresent), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getApplicationContext(), "Transaction Clear Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {

                                try {


                                    Fill_Payment(paymentname, paymentid);
                                }
                                catch(Exception e){
                                        System.out.println(e.getMessage());
                                }
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        }
                    }
                };
                timerThread.start();
            }
            catch(Exception e){

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            //sleep(100);

            Intent intent = new Intent(AddPaymentActivity.this, PaymentListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            pDialog.dismiss();
            finish();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
