package org.phomellolitepos;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.database.Customer_Image;
import org.phomellolitepos.database.Database;

import java.util.ArrayList;

public class CustomerImageActivity extends AppCompatActivity {
    Button btn_browse, btn_add, btn_save;
    EditText edt_cus_img;
    TextView txt_cus_img;
    ListView list_cus_img;
    Database db;
    SQLiteDatabase database;
    ArrayList<String> list;
    ArrayList<String> customer_image;
    ArrayAdapter<String> CusImgAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.customer_image);
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
                pDialog = new ProgressDialog(CustomerImageActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            Intent intent = new Intent(CustomerImageActivity.this, SetttingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        list = new ArrayList<>();
        edt_cus_img = (EditText) findViewById(R.id.edt_cus_img);
        txt_cus_img = (TextView) findViewById(R.id.txt_cus_img);
        btn_browse = (Button) findViewById(R.id.btn_browse);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_save = (Button) findViewById(R.id.btn_save);
        list_cus_img = (ListView) findViewById(R.id.list_cus_img);
        customer_image = Customer_Image.getAllCustomer_ImageString(getApplicationContext(), "", database);
        getImageList();

        btn_browse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_cus_img.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.Browse_Image_First, Toast.LENGTH_SHORT).show();

                } else {
                    customer_image.add(edt_cus_img.getText().toString().trim());
                    edt_cus_img.setText("");
                    getImageList();
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    database.beginTransaction();
                    if (customer_image.size() > 0) {
                        long l = Customer_Image.delete_Customer_Image(getApplicationContext(), "Customer_Image", "", new String[]{}, database);
                        for (int i = 0; i < CusImgAdapter.getCount(); i++) {
                            try {
                                Customer_Image customer_image = new Customer_Image(getApplicationContext(), null, CusImgAdapter.getItem(i) + "");
                                long s = customer_image.insertCustomer_Image(database);
                                if (s > 0) {
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    Toast.makeText(getApplicationContext(), R.string.savesucc,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                database.endTransaction();
                            } finally {
                            }
                        }
                    } else {
                        database.endTransaction();
                        Toast.makeText(getApplicationContext(), R.string.No_data_in_list, Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (Exception ex) {
                    database.endTransaction();
                    Toast.makeText(getApplicationContext(), R.string.No_data_in_list, Toast.LENGTH_SHORT)
                            .show();
                }

            }

        });

        list_cus_img.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String toRemove = CusImgAdapter.getItem(position);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        CustomerImageActivity.this);
                alertDialog.setTitle("");
                alertDialog
                        .setMessage(R.string.do_u_want_to_delete);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
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
                                CusImgAdapter.remove(toRemove);
                                getImageList();
                            }
                        });
                AlertDialog alert = alertDialog.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String realPath;
            Uri uri = data.getData();
            String path = uri.getPath();
            if (Build.VERSION.SDK_INT < 11) {
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(CustomerImageActivity.this, data.getData());
            }
                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19) {
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
            }
                // SDK > 19 (Android 4.4)
            else {
                try {


                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                    edt_cus_img.setText(realPath);
                }
                catch(Exception e){

                }
            }

        }
    }

    private void getImageList() {
        if (customer_image.size() > 0) {
            CusImgAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, customer_image);
            list_cus_img.setAdapter(CusImgAdapter);
            txt_cus_img.setVisibility(View.GONE);
            list_cus_img.setVisibility(View.VISIBLE);
        } else {
            txt_cus_img.setVisibility(View.VISIBLE);
            list_cus_img.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(CustomerImageActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Intent intent = new Intent(CustomerImageActivity.this, SetttingsActivity.class);
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



