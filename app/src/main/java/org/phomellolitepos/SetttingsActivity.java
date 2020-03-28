package org.phomellolitepos;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.itextpdf.text.pdf.codec.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Adapter.CustomAdapter;
import org.phomellolitepos.Adapter.DefaultOrderTypeSettingAdapter;
import org.phomellolitepos.Adapter.DialogOrderTypeListAdapter;
import org.phomellolitepos.Adapter.ItemUnitListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.UserPermission;
import org.phomellolitepos.database.Acc_Customer_Debit;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Unit;

public class SetttingsActivity extends AppCompatActivity {
    CheckBox is_costp_show,is_show_brc, is_show_print_dialog, is_show_device_cus, is_show_zdetail_inreprt, is_change_price, is_zero_stck, is_cash_drawer, is_stock_manage, is_accounts, chk_ad, chk_ac, chk_bop, is_file_share, is_online, is_email, is_sms, is_cmd, chk_hsn, is_denomination, is_barcode_print, is_discount, is_print_kot, is_print_invoice;
    Button save, btn_cus_image,btn_test_email,btn_test_sms,btn_reset;
    EditText edt_list_limit,edt_gst_lbl, edt_no_of_prnt, edt_host, edt_port, edt_sequence_no, edt_footer, edt_gst_no, edt_print_order, edt_print_cashier, edt_print_inv_no, edt_print_inv_date, edt_print_device_id;
    Spinner sp_qr_type,sp_print_memo, sp_defualt_odrtype, sp_print, sp_scale, sp_print_lang, sp_item_tax, sp_qty_dicml, sp_cus_dis, sp_home_layout;
    private TableRow wifi_set;
    private EditText ip;
    private String chk_is_cost_show,chk_is_show_brc, chk_is_show_device_cus, chk_is_show_print_dialog, chk_is_zdetail_inreprt, chk_is_change_price, chk_is_zero_stock, chk_is_cash_drawer, chk, wifi_ip = "", chk_email, chk_sms, chk_cmd, chk_hsn_prnt, chk_is_denomination, chk_is_barcode, chk_is_discount, chk_is_print_kot, chk_is_print_invoice, chk_is_file_share;
    private int qr_type,pri_pos, scale, item_tax, qty, cus_dis, home_layout, default_ordertype, print_memo;
    Database db;
    SQLiteDatabase database;
    Lite_POS_Registration lite_pos_registration;
    ImageView img_logo, img_logo_add,img_delete;
    EditText edt_email, edt_pass, edt_mnger_email, edt_uri, edt_key, edt_sender_id;
    TableLayout tl_email, tl_sms;
    private int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    String strQRType,strScale, strItemTax, strCusDis, strHomeLayout, strDefaultOrderType, strPrintMemo;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Bitmap bitmap;
    Intent intent;
    String strMgEmail = "";
    String strMailManager = "";
    String strEmail, strPass, strHost, strPort;
    String strPriLang, strQtyDecimal, strChangeParm;
    ProgressDialog pDialog;
    LinearLayout il_scale, ll_cash_drawer, ll_is_online,ll_CMD,ll_cus_img;
    Last_Code last_code;
    String strIsAccount;
    String strIsStockManage, strIsZeroStock, strIsChangePrice;
    String chk_is_account;
    String chk_is_stock_manage;
    Settings settings;
    ArrayList<Order_Type> order_typeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setttings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Settings);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                pDialog = new ProgressDialog(SetttingsActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                        if (settings.get_Home_Layout().equals("0")) {
                            try {
                                Intent intent = new Intent(SetttingsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();

                            } finally {
                            }
                        }else if (settings.get_Home_Layout().equals("2")){
                            try {
                                Intent intent = new Intent(SetttingsActivity.this, RetailActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {
                            }
                        } else {
                            try {
                                Intent intent = new Intent(SetttingsActivity.this, Main2Activity.class);
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

        is_costp_show = (CheckBox) findViewById(R.id.is_costp_show);
        is_show_brc = (CheckBox) findViewById(R.id.is_show_brc);
        is_show_device_cus = (CheckBox) findViewById(R.id.is_show_device_cus);
        is_show_print_dialog = (CheckBox) findViewById(R.id.is_show_print_dialog);
        is_show_zdetail_inreprt = (CheckBox) findViewById(R.id.is_show_zdetail_inreprt);
        is_cash_drawer = (CheckBox) findViewById(R.id.is_cash_drawer);
        is_online = (CheckBox) findViewById(R.id.is_online);
        sp_print_memo = (Spinner) findViewById(R.id.sp_print_memo);
        sp_defualt_odrtype = (Spinner) findViewById(R.id.sp_defualt_odrtype);
        sp_print = (Spinner) findViewById(R.id.sp_print);
        sp_scale = (Spinner) findViewById(R.id.sp_scale);
        sp_home_layout = (Spinner) findViewById(R.id.sp_home_layout);
        sp_qty_dicml = (Spinner) findViewById(R.id.sp_qty_dicml);
        sp_item_tax = (Spinner) findViewById(R.id.sp_item_tax);
        sp_print_lang = (Spinner) findViewById(R.id.sp_print_lang);
        sp_cus_dis = (Spinner) findViewById(R.id.sp_cus_dis);
        sp_qr_type = (Spinner) findViewById(R.id.sp_qr_type);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        edt_uri = (EditText) findViewById(R.id.edt_uri);
        edt_key = (EditText) findViewById(R.id.edt_key);
        edt_sender_id = (EditText) findViewById(R.id.edt_sender_id);
        edt_no_of_prnt = (EditText) findViewById(R.id.edt_no_of_prnt);
        edt_gst_lbl = (EditText) findViewById(R.id.edt_gst_lbl);
        edt_list_limit = (EditText) findViewById(R.id.edt_list_limit);
        tl_email = (TableLayout) findViewById(R.id.tl_email);
        tl_sms = (TableLayout) findViewById(R.id.tl_sms);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_logo_add = (ImageView) findViewById(R.id.img_logo_add);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        save = (Button) findViewById(R.id.save);
        btn_cus_image = (Button) findViewById(R.id.btn_cus_image);
        btn_test_email = (Button) findViewById(R.id.btn_test_email);
        btn_test_sms = (Button) findViewById(R.id.btn_test_sms);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        wifi_set = (TableRow) findViewById(R.id.wifi_set_row);
        ip = (EditText) findViewById(R.id.ip);
        edt_mnger_email = (EditText) findViewById(R.id.edt_mnger_email);
        is_email = (CheckBox) findViewById(R.id.is_email);
        is_sms = (CheckBox) findViewById(R.id.is_sms);
        is_cmd = (CheckBox) findViewById(R.id.is_cmd);
        chk_hsn = (CheckBox) findViewById(R.id.chk_hsn);
        edt_footer = (EditText) findViewById(R.id.edt_footer);
        is_denomination = (CheckBox) findViewById(R.id.is_denomination);
        is_barcode_print = (CheckBox) findViewById(R.id.is_barcode_print);
        is_discount = (CheckBox) findViewById(R.id.is_discount);
        is_print_kot = (CheckBox) findViewById(R.id.is_print_kot);
        is_print_invoice = (CheckBox) findViewById(R.id.is_print_invoice);
        is_file_share = (CheckBox) findViewById(R.id.is_file_share);
        chk_ad = (CheckBox) findViewById(R.id.chk_ad);
        chk_ac = (CheckBox) findViewById(R.id.chk_ac);
        chk_bop = (CheckBox) findViewById(R.id.chk_bop);
        is_accounts = (CheckBox) findViewById(R.id.is_accounts);
        is_stock_manage = (CheckBox) findViewById(R.id.is_stock_manage);
        is_zero_stck = (CheckBox) findViewById(R.id.is_zero_stck);
        is_change_price = (CheckBox) findViewById(R.id.is_change_price);
        edt_gst_no = (EditText) findViewById(R.id.edt_gst_no);
        edt_print_order = (EditText) findViewById(R.id.edt_print_order);
        edt_print_cashier = (EditText) findViewById(R.id.edt_print_cashier);
        edt_print_inv_no = (EditText) findViewById(R.id.edt_print_inv_no);
        edt_print_inv_date = (EditText) findViewById(R.id.edt_print_inv_date);
        edt_print_device_id = (EditText) findViewById(R.id.edt_print_device_id);
        edt_sequence_no = (EditText) findViewById(R.id.edt_sequence_no);
        edt_host = (EditText) findViewById(R.id.edt_host);
        edt_port = (EditText) findViewById(R.id.edt_port);
        il_scale = (LinearLayout) findViewById(R.id.ll_scale);
        ll_cash_drawer = (LinearLayout) findViewById(R.id.ll_cash_drawer);
        ll_is_online = (LinearLayout) findViewById(R.id.ll_is_online);
        ll_CMD = (LinearLayout) findViewById(R.id.ll_CMD);
        ll_cus_img = (LinearLayout) findViewById(R.id.ll_cus_img);
        is_print_invoice.setVisibility(View.GONE);
        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.set_Logo("");
                long h = settings.updateSettings("_Id = ?",new String[]{settings.get_Id()},database);
                if (h>0){
                    try {
                        Globals.logo1=null;
                        Bitmap bitmap = StringToBitMap("");
                        img_logo.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chk_ad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_ad.isChecked()) {
                    chk_ac.setChecked(false);
                    chk_bop.setChecked(false);
                }
            }
        });

        chk_ac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_ac.isChecked()) {
                    chk_ad.setChecked(false);
                    chk_bop.setChecked(false);
                }
            }
        });

        chk_bop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chk_bop.isChecked()) {
                    chk_ad.setChecked(false);
                    chk_ac.setChecked(false);
                }
            }
        });

        is_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_email.isChecked()) {
                    tl_email.setVisibility(View.VISIBLE);
                } else {
                    tl_email.setVisibility(View.GONE);
                }
            }
        });

        is_sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_sms.isChecked()) {
                    tl_sms.setVisibility(View.VISIBLE);
                } else {
                    tl_sms.setVisibility(View.GONE);
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = Settings.getSettings(getApplicationContext(), database, "");
                settings = new Settings(getApplicationContext(), settings.get_Id(), "false", "0", "", "", "", "", "", "", "", "", "", "", "", "0", "false", "false", "0", "Powered By Phomello", "0", "", "", "false", "false", "false", "GST", "TAX INVOICE", "Salesperson", "Invoice Number", "Invoice Date", "Device ID", "false", "false", "false", "", "", "AC", "false", "false", "0", "false", "true", "false", "1", "GST", "false", "false", "false", "false", "1", "0","false","0");
                long l = settings.updateSettings("_Id=?",new String[]{settings.get_Id()},database);
                if (l>0) {
                    Toast.makeText(getApplicationContext(), R.string.Reset_Succ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetttingsActivity.this,SetttingsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_test_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    test_email(strEmail, strPass, edt_mnger_email.getText().toString());
            }
        });

        btn_test_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_sms.isChecked()) {
                    chk_sms = "true";

                    if (edt_uri.getText().toString().equals("")) {
                        edt_uri.setError(getString(R.string.Url_is_required));
                        edt_uri.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_key.getText().toString().equals("")) {
                        edt_key.setError(getString(R.string.Key_is_required));
                        edt_key.requestFocus();
                        return;
                    } else {}

                    if (edt_sender_id.getText().toString().equals("")) {
                        edt_sender_id.setError(getString(R.string.Sender_id_is_required));
                        edt_sender_id.requestFocus();
                        return;
                    } else {}

                } else {
                    chk_sms = "false";
                }
            }
        });

        lite_pos_registration = Lite_POS_Registration.getRegistration(getApplicationContext(), database, db, "");

        if (lite_pos_registration == null) {

        } else {

            String ck_project_type = lite_pos_registration.getproject_id();

            if (ck_project_type.equals("standalone")) {

                ll_is_online.setVisibility(View.GONE);

            } else {
                ll_is_online.setVisibility(View.VISIBLE);
            }
        }

        settings = Settings.getSettings(SetttingsActivity.this, database, "");
        try {
            if (settings.get_Is_sms().equals("true")) {
                edt_uri.setText(settings.get_URL());
                edt_key.setText(settings.get_Auth_Key());
                edt_sender_id.setText(settings.get_Sender_Id());
            } else {
                edt_uri.setText("https://control.msg91.com/api/sendhttp.php");
                edt_key.setText("102234");
                edt_sender_id.setText("102234");
            }
        } catch (Exception Ex) {
            edt_uri.setText("https://control.msg91.com/api/sendhttp.php");
            edt_key.setText("102234");
            edt_sender_id.setText("102234");
        }

        edt_list_limit.setText(Globals.ListLimit);

        if (settings != null) {
            try {
                last_code = Last_Code.getLast_Code(getApplicationContext(), "", database);
                edt_sequence_no.setText(last_code.getlast_order_code());
            } catch (Exception ex) {
                edt_sequence_no.setText("0");
            }


            String strCheckAD = settings.get_Change_Parameter();
            if (strCheckAD.equals("AD")) {
                chk_ad.setChecked(true);
                chk_ac.setChecked(false);
                chk_bop.setChecked(false);
            }

            String strCheckAC = settings.get_Change_Parameter();
            if (strCheckAC.equals("AC")) {
                chk_ad.setChecked(false);
                chk_ac.setChecked(true);
                chk_bop.setChecked(false);
            }

            String strCheckBOP = settings.get_Change_Parameter();
            if (strCheckBOP.equals("BOP")) {
                chk_ad.setChecked(false);
                chk_ac.setChecked(false);
                chk_bop.setChecked(true);
            }

            String strOnline = settings.get_IsOnline();
            if (strOnline.equals("true")) {
                is_online.setChecked(true);
            }

            if (settings.get_Is_email()==null) {
                tl_email.setVisibility(View.GONE);
            }else {
                String strIsEmail = settings.get_Is_email();
                if (strIsEmail.equals("true")) {
                    is_email.setChecked(true);
                    tl_email.setVisibility(View.VISIBLE);
                } else {
                    tl_email.setVisibility(View.GONE);
                }
            }

            if (settings.get_Is_sms()==null){
                tl_sms.setVisibility(View.GONE);
            }else {
                String strIsSMS = settings.get_Is_sms();
                if (strIsSMS.equals("true")) {
                    is_sms.setChecked(true);
                    tl_sms.setVisibility(View.VISIBLE);
                } else {
                    tl_sms.setVisibility(View.GONE);
                }
            }

            String strIsShowBRC = settings.get_Is_BR_Scanner_Show();
            if (strIsShowBRC.equals("true")) {
                is_show_brc.setChecked(true);
            }

            String strIsShowZDetail = settings.get_Is_ZDetail_InPrint();
            if (strIsShowZDetail.equals("true")) {
                is_show_zdetail_inreprt.setChecked(true);
            }

            String strIsShowDeviceCus = settings.get_Is_Device_Customer_Show();
            if (strIsShowDeviceCus.equals("true")) {
                is_show_device_cus.setChecked(true);
            }

            String strIsShowPrintDialog = settings.get_Is_Print_Dialog_Show();
            if (strIsShowPrintDialog.equals("true")) {
                is_show_print_dialog.setChecked(true);
            }

            if (settings.get_Is_Customer_Display()==null){
            }else {
                String strIsCMD = settings.get_Is_Customer_Display();
                if (strIsCMD.equals("true")) {
                    is_cmd.setChecked(true);
                    btn_cus_image.setVisibility(View.VISIBLE);
                } else {
                    btn_cus_image.setVisibility(View.GONE);
                }
            }

            String strIsCashDrawer = settings.get_Is_Cash_Drawer();
            if (strIsCashDrawer.equals("true")) {
                is_cash_drawer.setChecked(true);
                ll_cash_drawer.setVisibility(View.VISIBLE);
            } else {
                ll_cash_drawer.setVisibility(View.GONE);
            }


            strIsAccount = settings.get_Is_Accounts();
            if (strIsAccount.equals("true")) {
                is_accounts.setChecked(true);
            }

            strIsStockManage = settings.get_Is_Stock_Manager();
            if (strIsStockManage.equals("true")) {
                is_stock_manage.setChecked(true);
            }

            strIsZeroStock = settings.get_Is_Zero_Stock();
            if (strIsZeroStock.equals("true")) {
                is_zero_stck.setChecked(true);
            }

            strIsChangePrice = settings.get_Is_Change_Price();
            if (strIsChangePrice.equals("true")) {
                is_change_price.setChecked(true);
            }

            String strIsHSN = settings.get_HSN_print();
            if (strIsHSN.equals("true")) {
                chk_hsn.setChecked(true);
            }

            String strDenomination = settings.get_Is_Denomination();
            if (strDenomination.equals("true")) {
                is_denomination.setChecked(true);
                il_scale.setVisibility(View.VISIBLE);
            } else {
                il_scale.setVisibility(View.GONE);
            }


            String strBarcodePrint = settings.get_Is_BarcodePrint();
            if (strBarcodePrint.equals("true")) {
                is_barcode_print.setChecked(true);
            }

            String strDiscount = settings.get_Is_Discount();
            if (strDiscount.equals("true")) {
                is_discount.setChecked(true);
            }

            String strIsPrintKOT = settings.get_Is_KOT_Print();
            if (strIsPrintKOT.equals("true")) {
                is_print_kot.setChecked(true);
            }

            String strIsPrintInvoice = settings.get_Is_Print_Invoice();
            if (strIsPrintInvoice.equals("true")) {
                is_print_invoice.setChecked(true);
            }

            String strIsFileShare = settings.get_Is_File_Share();
            if (strIsFileShare.equals("true")) {
                is_file_share.setChecked(true);
            }

            String strIsCostShow = settings.get_Is_Cost_Show();
            if (strIsCostShow.equals("true")) {
                is_costp_show.setChecked(true);
            }


            edt_no_of_prnt.setText(settings.get_No_Of_Print());
            edt_gst_lbl.setText(settings.get_GST_Label());
            edt_gst_no.setText(settings.get_Gst_No());
            edt_print_order.setText(settings.get_Print_Order());
            edt_print_cashier.setText(settings.get_Print_Cashier());
            edt_print_inv_no.setText(settings.get_Print_InvNo());
            edt_print_inv_date.setText(settings.get_Print_InvDate());
            edt_print_device_id.setText(settings.get_Print_DeviceID());

            edt_footer.setText(settings.get_Footer_Text());
            edt_email.setText(settings.get_Email());
            edt_pass.setText(settings.get_Password());
            edt_host.setText(settings.get_Host());
            edt_port.setText(settings.get_Port());
            edt_mnger_email.setText(settings.get_Manager_Email());

            try {
                Bitmap bitmap = StringToBitMap(settings.get_Logo());
                img_logo.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Toast.makeText(SetttingsActivity.this, R.string.noSet, Toast.LENGTH_SHORT).show();
        }

        setPrinterType(settings);
        setPrinterLang(settings);
        setScale(settings);
        setItemTax(settings);
        setQtyDecimal(settings);
        setCustomerImage(settings);
        setHomeLayout(settings);
        setDefaultOrderType(settings);
        setPrintMemo(settings);
        setQRType(settings);

        is_cmd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_cus_image.setVisibility(View.VISIBLE);
                } else {
                    btn_cus_image.setVisibility(View.GONE);
                }
            }
        });

        is_denomination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    il_scale.setVisibility(View.VISIBLE);
                } else {
                    il_scale.setVisibility(View.GONE);
                }
            }
        });


        sp_print.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                if (pos == 2) {
                    wifi_set.setVisibility(View.VISIBLE);
                    if (settings != null) {
                        if (settings.getPrinterIp() != null) {
                            ip.setText(settings.getPrinterIp());
                        }
                    }
                } else {
                    wifi_set.setVisibility(View.GONE);
                }

                if (pos == 6) {
                    ll_cus_img.setVisibility(View.VISIBLE);
                    ll_CMD.setVisibility(View.VISIBLE);
                    ll_cash_drawer.setVisibility(View.VISIBLE);
                } else {
                    ll_cus_img.setVisibility(View.GONE);
                    ll_CMD.setVisibility(View.GONE);
                    ll_cash_drawer.setVisibility(View.GONE);
                }

                if (pos == 0) {
                    is_print_invoice.setChecked(false);
                }else {
                    is_print_invoice.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        btn_cus_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent item_intent = new Intent(SetttingsActivity.this, CustomerImageActivity.class);
                startActivity(item_intent);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.ListLimit = edt_list_limit.getText().toString();
                long e = Settings.delete_settings(SetttingsActivity.this, "Settings", database, "", new String[]{});

                strEmail = edt_email.getText().toString().trim();
                strPass = edt_pass.getText().toString().trim();
                strHost = edt_host.getText().toString().trim();
                strPort = edt_port.getText().toString().trim();

                if (chk_ad.isChecked()) {
                    strChangeParm = "AD";
                }

                if (chk_ac.isChecked()) {
                    strChangeParm = "AC";
                }

                if (chk_bop.isChecked()) {
                    strChangeParm = "BOP";
                }

                if (is_show_brc.isChecked()) {
                    chk_is_show_brc = "true";
                } else {
                    chk_is_show_brc = "false";
                }

                if (is_show_zdetail_inreprt.isChecked()) {
                    chk_is_zdetail_inreprt = "true";
                } else {
                    chk_is_zdetail_inreprt = "false";
                }

                if (is_show_device_cus.isChecked()) {
                    chk_is_show_device_cus = "true";
                } else {
                    chk_is_show_device_cus = "false";
                }

                if (is_show_print_dialog.isChecked()) {
                    chk_is_show_print_dialog = "true";
                } else {
                    chk_is_show_print_dialog = "false";
                }

                if (is_denomination.isChecked()) {
                    chk_is_denomination = "true";
                } else {
                    chk_is_denomination = "false";
                }

                if (is_barcode_print.isChecked()) {
                    chk_is_barcode = "true";
                } else {
                    chk_is_barcode = "false";
                }

                if (is_discount.isChecked()) {
                    chk_is_discount = "true";
                } else {
                    chk_is_discount = "false";
                }

                if (is_online.isChecked()) {
                    chk = "true";
                } else {
                    chk = "false";
                }

                if (chk_hsn.isChecked()) {
                    chk_hsn_prnt = "true";
                } else {
                    chk_hsn_prnt = "false";
                }


                if (is_print_kot.isChecked()) {
                    chk_is_print_kot = "true";
                } else {
                    chk_is_print_kot = "false";
                }


                if (is_print_invoice.isChecked()) {
                    chk_is_print_invoice = "true";
                } else {
                    chk_is_print_invoice = "false";
                }

                if (is_file_share.isChecked()) {
                    chk_is_file_share = "true";
                } else {
                    chk_is_file_share = "false";
                }

                if (is_accounts.isChecked()) {
                    chk_is_account = "true";
                } else {
                    chk_is_account = "false";
                }

                if (is_stock_manage.isChecked()) {
                    chk_is_stock_manage = "true";
                } else {
                    chk_is_stock_manage = "false";
                }

                if (is_cash_drawer.isChecked()) {
                    chk_is_cash_drawer = "true";
                } else {
                    chk_is_cash_drawer = "false";
                }

                if (is_zero_stck.isChecked()) {
                    chk_is_zero_stock = "true";
                } else {
                    chk_is_zero_stock = "false";
                }

                if (is_change_price.isChecked()) {
                    chk_is_change_price = "true";
                } else {
                    chk_is_change_price = "false";
                }

                if (is_costp_show.isChecked()) {
                    chk_is_cost_show = "true";
                } else {
                    chk_is_cost_show = "false";
                }

                if (is_email.isChecked()) {
                    chk_email = "true";

                    if (!isValidEmail(strEmail)) {
                        edt_email.setError(getString(R.string.Invalid_Email));
                        edt_email.requestFocus();
                        return;
                    } else {
                        strEmail = edt_email.getText().toString();
                    }

                    if (strPass.equals("")) {
                        edt_pass.setError(getString(R.string.Password_is_required));
                        edt_pass.requestFocus();
                        return;
                    } else {
                        strPass = edt_pass.getText().toString();
                    }

                    if (strHost.equals("")) {
                        edt_host.setError(getString(R.string.Password_is_required));
                        edt_host.requestFocus();
                        return;
                    } else {
                        strHost = edt_host.getText().toString();
                    }

                    if (strPort.equals("")) {
                        edt_port.setError(getString(R.string.Password_is_required));
                        edt_port.requestFocus();
                        return;
                    } else {
                        strPort = edt_port.getText().toString();
                    }


                    //test_email(strEmail, strPass, edt_mnger_email.getText().toString());

                } else {
                    chk_email = "false";
                }

                if (is_sms.isChecked()) {
                    chk_sms = "true";

                    if (edt_uri.getText().toString().equals("")) {
                        edt_uri.setError(getString(R.string.Url_is_required));
                        edt_uri.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_key.getText().toString().equals("")) {
                        edt_key.setError(getString(R.string.Key_is_required));
                        edt_key.requestFocus();
                        return;
                    } else {
                    }

                    if (edt_sender_id.getText().toString().equals("")) {
                        edt_sender_id.setError(getString(R.string.Sender_id_is_required));
                        edt_sender_id.requestFocus();
                        return;
                    } else {
                    }

                } else {
                    chk_sms = "false";
                }
//
//
                if (is_cmd.isChecked()) {
                    chk_cmd = "true";

                } else {
                    chk_cmd = "false";
                }
//
                strMgEmail = edt_mnger_email.getText().toString().trim();
                if (strMgEmail.equals("")) {
                } else {
                    {
                        strMgEmail = edt_mnger_email.getText().toString().trim();
                    }
                }

                pri_pos = sp_print.getSelectedItemPosition();
                if (pri_pos == 2) {
                    wifi_ip = ip.getText().toString().trim();
                    if (wifi_ip.equals("")) {
                        Toast.makeText(SetttingsActivity.this, R.string.plsinsrtIp, Toast.LENGTH_SHORT).show();
                    }
                }

                qr_type = sp_qr_type.getSelectedItemPosition();
                if (qr_type == 0) {
                    strQRType = "0";
                } else if (qr_type == 1) {
                    strQRType = "1";
                }

                scale = sp_scale.getSelectedItemPosition();
                if (scale == 0) {
                    strScale = "0";
                } else if (scale == 1) {
                    strScale = "1";
                } else if (scale == 2) {
                    strScale = "5";
                }

                qty = sp_qty_dicml.getSelectedItemPosition();
                if (qty == 0) {
                    strQtyDecimal = "0";
                } else if (qty == 1) {
                    strQtyDecimal = "1";
                } else if (qty == 2) {
                    strQtyDecimal = "2";
                } else if (qty == 3) {
                    strQtyDecimal = "3";
                }

                int pri_lang = sp_print_lang.getSelectedItemPosition();
                if (pri_lang == 0) {
                    strPriLang = "0";
                } else if (pri_lang == 1) {
                    strPriLang = "1";
                } else if (pri_lang == 2) {
                    strPriLang = "2";
                }

                item_tax = sp_item_tax.getSelectedItemPosition();
                if (item_tax == 0) {
                    strItemTax = "0";
                } else if (item_tax == 1) {
                    strItemTax = "1";
                } else if (item_tax == 2) {
                    strItemTax = "2";
                } else if (item_tax == 3) {
                    strItemTax = "3";
                }

                home_layout = sp_home_layout.getSelectedItemPosition();
                if (home_layout == 0) {
                    strHomeLayout = "0";
                } else if (home_layout == 1) {
                    strHomeLayout = "1";
                } else if (home_layout == 2) {
                    strHomeLayout = "2";
                }

                cus_dis = sp_cus_dis.getSelectedItemPosition();
                if (cus_dis == 0) {
                    strCusDis = "0";
                } else if (cus_dis == 1) {
                    strCusDis = "1";
                } else if (cus_dis == 2) {
                    strCusDis = "2";
                }

                String logo;
                if (Globals.logo1 == null) {
                    logo = settings.get_Logo();
                } else {
                    logo = BitmapToString(Globals.logo1);
                }

                default_ordertype = sp_defualt_odrtype.getSelectedItemPosition();
                if (default_ordertype == 0) {
                    strDefaultOrderType = "1";
                } else if (default_ordertype == 1) {
                    strDefaultOrderType = "2";
                } else if (default_ordertype == 2) {
                    strDefaultOrderType = "3";
                } else if (default_ordertype == 3) {
                    strDefaultOrderType = "4";
                } else if (default_ordertype == 4) {
                    strDefaultOrderType = "5";
                }

                print_memo = sp_print_memo.getSelectedItemPosition();
                if (print_memo == 0) {
                    strPrintMemo = "0";
                } else if (print_memo == 1) {
                    strPrintMemo = "1";
                }

                pDialog = new ProgressDialog(SetttingsActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();

                final String finalLogo = logo;
                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(2000);
                            database.beginTransaction();
                            settings = new Settings(SetttingsActivity.this, null, chk, String.valueOf(pri_pos), wifi_ip, strScale, strEmail, strPass, finalLogo, strMgEmail, chk_sms, chk_email, edt_uri.getText().toString(), edt_key.getText().toString(), edt_sender_id.getText().toString(), strPriLang, chk_cmd, chk_hsn_prnt, strItemTax, settings.get_Copy_Right(), strQtyDecimal, edt_footer.getText().toString().trim(), strCusDis, chk_is_denomination, chk_is_barcode, chk_is_discount, edt_gst_no.getText().toString().trim(), edt_print_order.getText().toString().trim(), edt_print_cashier.getText().toString().trim(), edt_print_inv_no.getText().toString().trim(), edt_print_inv_date.getText().toString().trim(), edt_print_device_id.getText().toString().trim(), chk_is_print_kot, chk_is_print_invoice, chk_is_file_share, strHost, strPort, strChangeParm, chk_is_account, chk_is_stock_manage, strHomeLayout, chk_is_cash_drawer, chk_is_zero_stock, chk_is_change_price, edt_no_of_prnt.getText().toString().trim(), edt_gst_lbl.getText().toString().trim(), chk_is_zdetail_inreprt, chk_is_show_device_cus, chk_is_show_print_dialog, chk_is_show_brc, strDefaultOrderType, strPrintMemo,chk_is_cost_show,strQRType);
                            long l = settings.insertSettings(database);
                            if (l > 0) {
                                try {
                                    last_code.setlast_order_code(edt_sequence_no.getText().toString().trim());
                                    last_code.updateLast_Code("id=?", new String[]{last_code.getid()}, database);
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    pDialog.dismiss();
                                } catch (Exception e) {
                                    database.setTransactionSuccessful();
                                    database.endTransaction();
                                    pDialog.dismiss();
                                }
                                Globals.strIsBarcodePrint = settings.get_Is_BarcodePrint();
                                Globals.strIsDenominationPrint = settings.get_Is_Denomination();
                                Globals.strIsDiscountPrint = settings.get_Is_Discount();
                                Globals.GSTNo = settings.get_Gst_No();
                                Globals.GSTLbl = settings.get_GST_Label();
                                Globals.PrintOrder = settings.get_Print_Order();
                                Globals.PrintCashier = settings.get_Print_Cashier();
                                Globals.PrintInvNo = settings.get_Print_InvNo();
                                Globals.PrintInvDate = settings.get_Print_InvDate();
                                Globals.PrintDeviceID = settings.get_Print_DeviceID();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SetttingsActivity.this, R.string.savesucc, Toast.LENGTH_SHORT).show();
                                        onBackPressed();
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

        img_logo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void setPrintMemo(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.print_memo);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_print_memo.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_print_memo.setSelection(Integer.parseInt(settings.get_Print_Memo()));
            } catch (Exception ex) {}
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setDefaultOrderType(Settings settings) {
        try {
            order_typeArrayList = Order_Type.getAllOrder_Type(getApplicationContext(), "", database);
            DefaultOrderTypeSettingAdapter defaultOrderTypeSettingAdapter = new DefaultOrderTypeSettingAdapter(getApplicationContext(), order_typeArrayList);
            sp_defualt_odrtype.setAdapter(defaultOrderTypeSettingAdapter);

            if (!settings.get_Default_Ordertype().equals("")) {
                for (int i = 0; i < defaultOrderTypeSettingAdapter.getCount(); i++) {
                    String iname = order_typeArrayList.get(i).get_order_type_id();
                    if (settings.get_Default_Ordertype().equals(iname)) {
                        sp_defualt_odrtype.setSelection(i);
                        break;
                    }
                }
            }

        }catch (Exception ex) {
        }
    }

    private void setHomeLayout(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.home_layout);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_home_layout.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_home_layout.setSelection(Integer.parseInt(settings.get_Home_Layout()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

   private void setCustomerImage(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.customer_display);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_cus_dis.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_cus_dis.setSelection(Integer.parseInt(settings.get_CustomerDisplay()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setQtyDecimal(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.qty_decimal);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_qty_dicml.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_qty_dicml.setSelection(Integer.parseInt(settings.get_Qty_Decimal()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setItemTax(Settings settings) {

        String[] stringArray = getResources().getStringArray(R.array.item_tax);
        ArrayAdapter<String> adapterItemTax = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        sp_item_tax.setAdapter(adapterItemTax);
        if (settings != null) {
            try {
                sp_item_tax.setSelection(Integer.parseInt(settings.get_ItemTax()));
            } catch (Exception ex) {

            }
        }
        adapterItemTax.notifyDataSetChanged();
    }

    private void test_email(final String strEmail, final String strPass, String s) {

//        String[] recipients = {strEmail};
//        final SendEmailAsyncTask email = new SendEmailAsyncTask();
//        email.activity = this;
//        email.m = new GMailSender("npaliwal24@gmail.com", strPass);
//        email.m.set_from("npaliwal24@gmail.com");
//        email.m.setBody("Test Email");
//        email.m.set_to(recipients);
//        email.m.set_subject("litePOS");
//        email.execute();

        String[] recipients = s.split(",");
        final SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new GMailSender(strEmail, strPass, strHost, strPort);
        email.m.set_from(strEmail);
        email.m.setBody("Test Email");
        email.m.set_to(recipients);
        email.m.set_subject("Dear [Customer Name],\n" +
                "\n" +
                "This is a test email sent from LitePOS. If you received this email, it confirms that your LitePOS email notification service has been successfully set-up.\n" +
                "Thank you for choosing LitePOS.\n" +
                "\n" +
                "Kind Regards\n" +
                "LitePOS Team");
        email.execute();

    }

    private void setScale(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.Scale);
        ArrayAdapter<String> adapterPtype1 = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_scale.setAdapter(adapterPtype1);
        if (settings != null) {
            try {
                sp_scale.setSelection(Integer.parseInt(settings.get_Scale()));
            } catch (Exception ex) {

            }
        }
        adapterPtype1.notifyDataSetChanged();
    }

    private void setPrinterType(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.Printer_Type);
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_print.setAdapter(adapterPtype);
        if (settings != null) {
            sp_print.setSelection(Integer.parseInt(settings.getPrinterId()));
        }
        adapterPtype.notifyDataSetChanged();
    }

    private void setPrinterLang(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.Printer_Lang);
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_print_lang.setAdapter(adapterPtype);
        if (settings != null) {
            sp_print_lang.setSelection(Integer.parseInt(settings.get_Print_Lang()));
        }
        adapterPtype.notifyDataSetChanged();
    }

    private void setQRType(Settings settings) {
        String[] stringArray = getResources().getStringArray(R.array.qr_type);
        ArrayAdapter<String> adapterPtype = new ArrayAdapter<String>(this, R.layout.items_spinner, stringArray);
        this.sp_qr_type.setAdapter(adapterPtype);
        if (settings != null) {
            sp_qr_type.setSelection(Integer.parseInt(settings.get_QR_Type()));
        }
        adapterPtype.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                img_logo.setImageBitmap(bitmap);
                Globals.logo1 = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(SetttingsActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();

        Thread timerThread = new Thread() {
            public void run() {
                if (settings.get_Home_Layout().equals("0")) {
                    try {
                        Intent intent = new Intent(SetttingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();

                    } finally {}
                }else if (settings.get_Home_Layout().equals("2")){
                    try {
                        Intent intent = new Intent(SetttingsActivity.this, RetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();
                    } finally {
                    }
                } else {
                    try {
                        Intent intent = new Intent(SetttingsActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                        finish();

                    } finally {}
                }
            }
        };
        timerThread.start();
    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        SetttingsActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
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


    public void sendMySMS(String auth_key, String url, String sender_id) {

        //Your authentication key
        String authkey = auth_key;
//Multiple mobiles numbers separated by comma
        String mobiles = "9530470882";
//Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = sender_id;
//Your message to send, Add URL encoding here.
        String message = "Test message";
//define route
        String route = "default";

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

//encoding message
        String encoded_message = URLEncoder.encode(message);

//Send SMS API
        String mainUrl = url;

//Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + senderId);

//final string
        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
                Log.d("RESPONSE", "" + response);

            //finally close connection
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeBytes(b, Base64.ENCODE);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DECODE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }

        return false;
    }
}
